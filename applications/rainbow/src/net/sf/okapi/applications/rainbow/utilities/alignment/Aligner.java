/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.applications.rainbow.utilities.alignment;

import net.sf.okapi.common.resource.InvalidContentException;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.ui.ClosePanel;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.common.ui.filters.GenericContent;
import net.sf.okapi.lib.ui.segmentation.SRXEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Aligner {
	
	private Shell            shell;
	private int              result = 0;
	private ClosePanel       pnlActions;
	private List             srcList;
	private List             trgList;
	private Text             edDocument;
	private Text             edName;
	private Text             edCause;
	private Button           btMoveUp; 
	private Button           btMoveDown; 
	private Button           btMerge; 
	private Button           btSplit;
	private Button           btAccept; 
	private Button           btSkip;
	private Button           btEditRules;
	private Button           btEditSeg;
	private Text             edSource;
	private Text             edTarget;
	private Text             edSrcSeg;
	private Text             edTrgSeg;
	private Font             textFont;
	private Text             edCounter;
	private Button           chkShowInlineCodes;
	private Button           chkSyncScrolling;
	private Button           chkCheckSingleSegUnit;
	private TextContainer    source;
	private TextContainer    target;
	private boolean          splitMode = false;
	private boolean          editMode = false;
	private int              indexActiveSegment;
	private GenericContent   genericCont;
	private String           targetSrxPath;
	private Color            colorWhite;
	private Color            colorGreen;
	private Color            colorAmber;
	private Color            colorRed;
	private boolean          canAcceptUnit;


	@Override
	protected void finalize () {
		dispose();
	}
	
	public void dispose () {
		if ( textFont != null ) {
			textFont.dispose();
			textFont = null;
		}
		if ( shell != null ) {
			shell.close();
			shell = null;
		}
		if ( colorGreen != null ) {
			colorGreen.dispose();
			colorGreen = null;
		}
		if ( colorAmber != null ) {
			colorAmber.dispose();
			colorAmber = null;
		}
		if ( colorRed != null ) {
			colorRed.dispose();
			colorRed = null;
		}
		if ( colorWhite != null ) {
			colorWhite.dispose();
			colorWhite = null;
		}
	}

	public Aligner (Shell parent) {
		colorGreen = new Color(null, 0, 128, 0);
		colorAmber = new Color(null, 255, 153, 0);
		colorRed = new Color(null, 220, 20, 60);
		colorWhite = new Color(null, 255, 255, 255);
		
		genericCont = new GenericContent();
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | 
			SWT.MAX | SWT.MIN | SWT.APPLICATION_MODAL);
		shell.setText("Alignment Verification");
		shell.setImage(parent.getImage());
		shell.setLayout(new GridLayout(4, true));
		
		// On close: Hide instead of closing
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				event.doit = false;
				result = 0;
				hide();
			}
		});

		edDocument = new Text(shell, SWT.BORDER);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		edDocument.setLayoutData(gdTmp);
		edDocument.setEditable(false);
		
		edCounter = new Text(shell, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);;
		edCounter.setLayoutData(gdTmp);
		edCounter.setEditable(false);
		
		edName = new Text(shell, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 3;
		edName.setLayoutData(gdTmp);
		edName.setEditable(false);
		
		edCause = new Text(shell, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		edCause.setLayoutData(gdTmp);
		edCause.setEditable(false);
		edCause.setForeground(colorWhite);

		Font font = edCause.getFont();
		FontData[] fontData = font.getFontData();
		fontData[0].setHeight(10);
		textFont = new Font(font.getDevice(), fontData[0]);
		
		srcList = new List(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		srcList.setLayoutData(gdTmp);
		srcList.setFont(textFont);
		srcList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( chkSyncScrolling.getSelection() ) synchronizeFromSource();
				else updateSourceSegmentDisplay();
			}
		});
		
		trgList = new List(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		trgList.setLayoutData(gdTmp);
		trgList.setFont(textFont);
		trgList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( chkSyncScrolling.getSelection() ) synchronizeFromTarget();
				else updateTargetSegmentDisplay();
			}
		});

		//-- Options
		
		Composite cmpOptions = new Composite(shell, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		cmpOptions.setLayout(layout);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		cmpOptions.setLayoutData(gdTmp);
		
		chkSyncScrolling = new Button(cmpOptions, SWT.CHECK);
		chkSyncScrolling.setText("Synchronize scrolling");
		chkSyncScrolling.setSelection(true);
		chkSyncScrolling.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( chkSyncScrolling.getSelection() ) synchronizeFromTarget();
			}
		});
		
		chkShowInlineCodes = new Button(cmpOptions, SWT.CHECK);
		chkShowInlineCodes.setText("Display in-line codes with generic markers");
		chkShowInlineCodes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fillSourceList(srcList.getSelectionIndex());
				updateSourceDisplay();
				fillTargetList(trgList.getSelectionIndex());
				updateTargetDisplay();
			}
		});
		
		chkCheckSingleSegUnit = new Button(cmpOptions, SWT.CHECK);
		chkCheckSingleSegUnit.setText("Verify in-line codes for text-unit with a single segment");

		//--- Main buttons
		
		int buttonWidth = 120;
		
		Composite cmpButtons = new Composite(shell, SWT.NONE);
		layout = new GridLayout(4, false);
		layout.marginWidth = 0;
		cmpButtons.setLayout(layout);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END);
		gdTmp.horizontalSpan = 2;
		cmpButtons.setLayoutData(gdTmp);

		btEditRules = UIUtil.createGridButton(cmpButtons, SWT.PUSH, "Edit Rules...", buttonWidth);
		btEditRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editRules();
			}
		});
		
		btMoveUp = UIUtil.createGridButton(cmpButtons, SWT.PUSH, "Move Up", buttonWidth);
		btMoveUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveUp();
			}
		});
	
		btMerge = UIUtil.createGridButton(cmpButtons, SWT.PUSH, "Merge With Next", buttonWidth);
		btMerge.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				mergeWithNext();
			}
		});
		
		btAccept = UIUtil.createGridButton(cmpButtons, SWT.PUSH, "Accept", buttonWidth);
		btAccept.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( splitMode ) endSplitMode(true);
				else if ( editMode ) endEditMode(true);
				else { // Accept this text unit
					result = 1;
					if ( !saveData() ) return;
					hide();
				}
			}
		});
		
		btEditSeg = UIUtil.createGridButton(cmpButtons, SWT.PUSH, "Edit Segment...", buttonWidth);
		btEditSeg.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				startEditMode();
			}
		});
		
		btMoveDown = UIUtil.createGridButton(cmpButtons, SWT.PUSH, "Move Down", buttonWidth);
		btMoveDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveDown();
			}
		});
		
		btSplit = UIUtil.createGridButton(cmpButtons, SWT.PUSH, "Split...", buttonWidth);
		btSplit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				startSplitMode();
			}
		});

		btSkip = UIUtil.createGridButton(cmpButtons, SWT.PUSH, "Skip", buttonWidth);
		btSkip.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( splitMode ) {
					endSplitMode(false);
					return;
				}
				else if ( editMode ) {
					endEditMode(false);
					return;
				}
				// Else: Skip this text unit
				result = 2;
				hide();
			}
		});
		
		//--- Edit boxes
		
		edSrcSeg = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		edSrcSeg.setEditable(false);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		gdTmp.heightHint = 16;
		edSrcSeg.setLayoutData(gdTmp);
		edSrcSeg.setFont(textFont);
		
		edTrgSeg = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		edTrgSeg.setEditable(false);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		gdTmp.heightHint = 16;
		edTrgSeg.setLayoutData(gdTmp);
		edTrgSeg.setFont(textFont);
		edTrgSeg.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if ( e.keyCode == SWT.CR ) {
					e.doit = true;
				}
			}
		});

		Label label = new Label(shell, SWT.NONE);
		label.setText("Full text unit:");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		label.setLayoutData(gdTmp);
		
		edSource = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		edSource.setEditable(false);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		gdTmp.heightHint = 32;
		edSource.setLayoutData(gdTmp);
		edSource.setFont(textFont);
		
		edTarget = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		edTarget.setEditable(false);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 4;
		gdTmp.heightHint = 32;
		edTarget.setLayoutData(gdTmp);
		edTarget.setFont(textFont);
		
		//--- Dialog-level buttons

		SelectionAdapter CloseActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( e.widget.getData().equals("h") ) {
					//TODO: UIUtil.start(help);
					return;
				}
				if ( e.widget.getData().equals("c") ) {
					result = 0;
					hide();
				}
			};
		};
		pnlActions = new ClosePanel(shell, SWT.NONE, CloseActions, true);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		pnlActions.setLayoutData(gdTmp);
		pnlActions.btClose.setText("Cancel");
		shell.setDefaultButton(btAccept);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 700 ) startSize.x = 700; 
		if ( startSize.y < 700 ) startSize.y = 700; 
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
	}
	
	/**
	 * Sets information data for the visual verification.
	 * @param targetSrxPath The full path of the SRX document to use for the target.
	 * @param checkSingleSegUnit True if unit with a single segment should be checked, false to accept
	 * them as aligned without checking.
	 */
	public void setInfo (String targetSrxPath,
		boolean checkSingleSegUnit)
	{
		this.targetSrxPath = targetSrxPath;
		chkCheckSingleSegUnit.setSelection(checkSingleSegUnit);
	}
	
	private void synchronizeFromSource () {
		updateSourceSegmentDisplay();
		int n = srcList.getSelectionIndex();
		if ( n >= trgList.getItemCount() ) {
			edTrgSeg.setText("");
			return; // Cannot synchronize
		}
		trgList.setSelection(n);
		updateTargetSegmentDisplay();
	}

	private void synchronizeFromTarget () {
		updateTargetSegmentDisplay();
		int n = trgList.getSelectionIndex();
		if ( n >= srcList.getItemCount() ) {
			edSrcSeg.setText("");
			return; // Cannot synchronize
		}
		srcList.setSelection(n);
		updateSourceSegmentDisplay();
	}

	private void updateSourceDisplay () {
		edSource.setText(genericCont.printSegmentedContent(source, true,
			!chkShowInlineCodes.getSelection()));
	}
	
	private void updateTargetDisplay () {
		edTarget.setText(genericCont.printSegmentedContent(target, true,
			!chkShowInlineCodes.getSelection()));
	}
	
	private void updateSourceSegmentDisplay () {
		int n = srcList.getSelectionIndex();
		if ( n < 0 ) edSrcSeg.setText("");
		else edSrcSeg.setText(srcList.getItem(n));
	}
	
	private void updateTargetSegmentDisplay () {
		int n = trgList.getSelectionIndex();
		if ( n < 0 ) edTrgSeg.setText("");
		else edTrgSeg.setText(trgList.getItem(n));

		n = trgList.getSelectionIndex();
		int count = trgList.getItemCount();
		btMoveUp.setEnabled(n>0);
		btMoveDown.setEnabled(( n < count-1 ) && ( n > -1 ));
		btMerge.setEnabled(( n < count-1 ) && ( n > -1 ));
		btSplit.setEnabled(( count > 0 ) && ( n > -1 ));
	}
	
	private void moveUp () {
		//TODO: move up
		Dialogs.showError(shell, "Not implemented yet", null);
		updateTargetSegmentDisplay();
	}
	
	private void moveDown () {
		//TODO: move down
		Dialogs.showError(shell, "Not implemented yet", null);
		updateTargetSegmentDisplay();
	}
	
	private void mergeWithNext () {
		try {
			int n = trgList.getSelectionIndex();
			if ( n < 0  ) return;
			target.joinSegmentWithNext(n);
			updateTargetDisplay();
			fillTargetList(n);
			trgList.setFocus();
			// Re-check for issues
			hasIssue(true);
		}
		catch ( Throwable e) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void splitSegment (int segIndex,
		int start,
		int end)
	{
		// No split if location is not a range and is not inside text
		if (( start == end ) && ( start == 0 )) return;
		// Get the length of the segment to re-split
		int len = target.getSegments().get(segIndex).getCodedText().length();
		//TODO: case for if (( start == 0 ) && ( end == len ))

		// Merge the segment to re-split
		int pos = target.mergeSegment(segIndex);
		// Now pos value is the position 0 of the segment character indices
		if ( pos == -1 ) return; // Segment index not found
		
		// Create the new segment(s)
		if ( start == 0 ) {
			// Only one new segment: From the end of selection
			// up to the end of the original segment.
			target.createSegment(pos+end, pos+len);
		}
		else if ( end == len ) {
			// Only one new segment: From the start of the original segment
			// up to the start of the selection 
			target.createSegment(pos, pos+start);
		}
		else { 
			// First new segment goes from start of original to start selection
			target.createSegment(pos, pos+start);
			// Second new segment goes from end of previous segment marker
			// plus the length of the selection to end of original segment minus length of first segment
			target.createSegment(pos+2+(end-start), pos+(len-(start-2)));
		}
	}
	
	public void close () {
		shell.close();
	}
	
	/**
	 * Calls the form to align a text unit visually.
	 * @return 1=the segments are deemed aligned, 2=skip this entry,
	 * 0=stop the process.
	 */
	private int showDialog ()
	{
		TextUnit tu = source.getParent();
		edName.setText("");
		if ( tu != null ) {
			if ( tu.getName().length() > 0 ) {
				edName.setText(tu.getName());
			}
		}
		setData();
		
		shell.setVisible(true);
		trgList.setFocus();
		Display Disp = shell.getDisplay();
		while ( shell.isVisible() ) {
			if ( !Disp.readAndDispatch() )
				Disp.sleep();
		}
		return result;
	}
	
	public void setDocumentName (String name) {
		edDocument.setText(name);
	}

	/**
	 * Verifies the alignment of the segments of a given TextUnit object.
	 * @param tu The text unit containing the segments to verify.
	 * @param currentSource The current source unit being verified.
	 * @param totalTarget The total number of target units available.
	 * @return 1=the segments are deemed aligned, 2=skip this entry,
	 * 0=stop the process.
	 */
	public int align (TextUnit tu,
		int currentSource,
		int totalTarget)
	{
		// Make sure we do have a target to align
		if ( !tu.hasTarget() ) return 2;
		// Set the new values
		source = tu.getSourceContent();
		target = tu.getTargetContent();
		// Check if both are segmented
		if ( !source.isSegmented() || !target.isSegmented() ) return 2;
		// Check for issues
		if ( hasIssue(false) ) {
			// Correct manually
			edCounter.setText(String.format("This source: #%d / Total targets: %d", currentSource, totalTarget));
			return showDialog();
		}
		// Else: assumes correct alignment
		return 1;
	}

	private void hide () {
		shell.setVisible(false);
		if ( shell.getMinimized() ) shell.setMinimized(false);
	}

	private void fillTargetList (int selection) {
		trgList.removeAll();
		boolean useGeneric = chkShowInlineCodes.getSelection();
		for ( TextFragment tf : target.getSegments() ) {
			if ( useGeneric ) trgList.add(genericCont.setContent(tf).toString());
			else trgList.add(tf.toString());
		}
		if (( trgList.getItemCount() > 0 ) && ( selection < trgList.getItemCount() )) 
			trgList.select(selection);
		
		if ( !edTrgSeg.getEditable() ) { // Not while the field can be edited
			updateTargetSegmentDisplay();
		}
	}
	
	private void fillSourceList (int selection) {
		srcList.removeAll();
		boolean useGeneric = chkShowInlineCodes.getSelection();
		for ( TextFragment tf : source.getSegments() ) {
			if ( useGeneric ) srcList.add(genericCont.setContent(tf).toString());
			else srcList.add(tf.toString());
		}
		if (( srcList.getItemCount() > 0 ) && ( selection < srcList.getItemCount() )) 
			srcList.select(selection);
		updateSourceSegmentDisplay();
	}
	
	private void setData () {
		updateSourceDisplay();
		fillSourceList(0);
		updateTargetDisplay();
		fillTargetList(0);
	}
	
	private boolean saveData () {
		if ( splitMode ) return false;
		return true;
	}
	
	private void startSplitMode () {
		indexActiveSegment = trgList.getSelectionIndex();
		if ( indexActiveSegment == -1 ) return;
		splitMode = true;
		toggleFields(true);
	}
	
	private void endSplitMode (boolean accept) {
		try {
			// Compute the new segmentation
			if ( accept ) {
				// genericCont is already set with the proper text
				Point sel = genericCont.getCodedTextPosition(edTrgSeg.getSelection());
				splitSegment(indexActiveSegment, sel.x, sel.y);
			}
			// Re-check for issues
			hasIssue(true);
			
			// Reset the controls
			splitMode = false;
			toggleFields(false);
			
			// Update the display
			if ( accept ) {
				updateTargetDisplay();
				fillTargetList(indexActiveSegment);
			}
			else updateTargetSegmentDisplay();
			trgList.setFocus();
		}
		catch ( Throwable e) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void editRules () {
		try {
			SRXEditor editor = new SRXEditor(shell);
			editor.showDialog(targetSrxPath);
		}
		catch ( Throwable e) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}

	private void startEditMode () {
		indexActiveSegment = trgList.getSelectionIndex();
		if ( indexActiveSegment == -1 ) return;
		editMode = true;
		toggleFields(true);
	}
	
	private void endEditMode (boolean accept) {
		try {
			// Update the content
			if ( accept ) {
				try {
					// genericCont is already set with the proper text
					genericCont.updateFragment(edTrgSeg.getText(),
						target.getSegments().get(indexActiveSegment), true);
				}
				catch ( InvalidContentException e ) {
					Dialogs.showError(shell, e.getMessage(), null);
					//TODO: recover by resetting the original, or prevent end of
					//edit mode
				}
			}
			// Re-check for issues
			hasIssue(true);
			
			// Reset the controls
			editMode = false;
			toggleFields(false);
			
			// Update the display
			if ( accept ) {
				updateTargetDisplay();
				fillTargetList(indexActiveSegment);
			}
			else updateTargetSegmentDisplay();
			trgList.setFocus();
		}
		catch ( Throwable e) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}

	private void toggleFields (boolean specialMode) {
		if ( specialMode ) {
			genericCont.setContent(target.getSegments().get(indexActiveSegment));
			edTrgSeg.setText(genericCont.toString());
			edTrgSeg.setFocus();
			btAccept.setEnabled(true);
		}
		else {
			btAccept.setEnabled(canAcceptUnit);
		}
		edTrgSeg.setEditable(specialMode);
		srcList.setEnabled(!specialMode);
		trgList.setEnabled(!specialMode);
		btEditRules.setVisible(!specialMode);
		btEditSeg.setVisible(!specialMode);
		btMoveUp.setVisible(!specialMode);
		btMoveDown.setVisible(!specialMode);
		btMerge.setVisible(!specialMode);
		btSplit.setVisible(!specialMode);
		chkSyncScrolling.setVisible(!specialMode);
		
		if ( specialMode ) {
			if ( splitMode ) btAccept.setText("Accept Split");
			else btAccept.setText("Accept Changes");
			btSkip.setText("Discard");
		}
		else {
			btAccept.setText("Accept");
			btSkip.setText("Skip");
		}
	}

	/**
	 * Tries to find some issue with the current alignment.
	 * @param forceIssueDisplay True if we need to set the issue display.
	 * Such display is not needed when calling the function when the dialog is hidden
	 * and no issues are found. 
	 * @return True if an issue has been found. False if no issue has been found.
	 */
	private boolean hasIssue (boolean forceIssueDisplay) {
		try {
			// Check the number of segments
			if ( source.getSegments().size() != target.getSegments().size() ) {
				// Optional visual alignment to fix the problems
				setIssue(1);
				return true;
			}
			// Assumes the list have same number of segments now
			
			// Check if we do further verification for single-segment unit
			if (( source.getSegments().size() == 1 ) && !chkCheckSingleSegUnit.getSelection() ) {
				// We assume it's ok to align
				if ( forceIssueDisplay ) setIssue(0);
				return false;
			}
			
			// Sanity check using common anchors
			java.util.List<TextFragment> srcList = source.getSegments();
			java.util.List<TextFragment> trgList = target.getSegments();
			for ( int i=0; i<srcList.size(); i++ ) {
				if ( srcList.get(i).getCodes().size() != trgList.get(i).getCodes().size() ) {
					setIssue(2);
					return true;
				}
			}
			if ( forceIssueDisplay ) setIssue(0);
			return false;
		}
		catch ( Throwable e) {
			Dialogs.showError(shell, e.getMessage(), null);
			return true;
		}
	}
	
	private void setIssue (int type) {
		canAcceptUnit = true;
		switch ( type ) {
		case 0:
			edCause.setText("No issue automatically detected.");
			edCause.setBackground(colorGreen);
			break;
		case 1:
			edCause.setText("Different number of segments in target.");
			edCause.setBackground(colorRed);
			canAcceptUnit = false;
			break;
		case 2:
			edCause.setText("At least one target segment has a different number on in-line codes.");
			edCause.setBackground(colorAmber);
			break;
		}
		btAccept.setEnabled(canAcceptUnit);
	}
}
