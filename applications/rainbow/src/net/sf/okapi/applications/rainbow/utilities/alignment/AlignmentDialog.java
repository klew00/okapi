/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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

import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

import sf.okapi.lib.ui.segmentation.GenericContent;

public class AlignmentDialog {
	
	private Shell            shell;
	private int              result = 0;
	private OKCancelPanel    pnlActions;
	private List             srcList;
	private List             trgList;
	private Text             edDocument;
	private Text             edName;
	private Text             edCause;
	private Button           btMoveUp; 
	private Button           btMoveDown; 
	private Button           btMerge; 
	private Button           btSplit;
	private Text             edSource;
	private Text             edTarget;
	private Text             edSrcSeg;
	private Text             edTrgSeg;
	private Font             textFont;
	private Button           chkShowInlineCodes;
	private Button           chkSyncScrolling;
	private TextContainer    source;
	private TextContainer    target;
	private boolean          splitMode = false;
	private int              indexSegToResplit;
	private GenericContent   genericCont;


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
	}

	public AlignmentDialog (Shell parent)
	{
		genericCont = new GenericContent();
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | 
			SWT.MAX | SWT.MIN | SWT.APPLICATION_MODAL);
		shell.setText("Alignment Verification");
		shell.setImage(parent.getImage());
		shell.setLayout(new GridLayout(2, true));
		
		// On close: Hide instead of closing
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				event.doit = false;
				hide();
			}
		});

		edDocument = new Text(shell, SWT.BORDER);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edDocument.setLayoutData(gdTmp);
		edDocument.setEditable(false);
		
		edName = new Text(shell, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edName.setLayoutData(gdTmp);
		edName.setEditable(false);
		
		edCause = new Text(shell, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		edCause.setLayoutData(gdTmp);
		edCause.setEditable(false);
		
		srcList = new List(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		srcList.setLayoutData(new GridData(GridData.FILL_BOTH));
		srcList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( chkSyncScrolling.getSelection() ) synchronizeFromSource();
				else updateSourceSegmentDisplay();
			}
		});
		
		trgList = new List(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		trgList.setLayoutData(new GridData(GridData.FILL_BOTH));
		trgList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( chkSyncScrolling.getSelection() ) synchronizeFromTarget();
				else updateTargetSegmentDisplay();
			}
		});

		//-- Options
		
		Composite cmpOptions = new Composite(shell, SWT.NONE);
		cmpOptions.setLayout(new GridLayout());
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
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
		chkShowInlineCodes.setText("Display generic inline codes");
		chkShowInlineCodes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fillSourceList(srcList.getSelectionIndex());
				fillTargetList(trgList.getSelectionIndex());
			}
		});

		//--- Target buttons
		
		Composite cmpButtons = new Composite(shell, SWT.NONE);
		cmpButtons.setLayout(new GridLayout(2, false));
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		cmpButtons.setLayoutData(gdTmp);
		
		int buttonWidth = 120;
		btMoveUp = UIUtil.createGridButton(cmpButtons, SWT.PUSH, "Move Up", buttonWidth);
		btMoveUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveUp();
			}
		});
	
		btMerge = UIUtil.createGridButton(cmpButtons, SWT.PUSH, "Merge with Above", buttonWidth);
		btMerge.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				mergeWithAbove();
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
				if ( splitMode ) endSplitMode(true);
				else startSplitMode();
			}
		});

		//--- Edit boxes
		
		edSrcSeg = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		edSrcSeg.setEditable(false);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		gdTmp.heightHint = 16;
		edSrcSeg.setLayoutData(gdTmp);
		Font font = edSrcSeg.getFont();
		FontData[] fontData = font.getFontData();
		fontData[0].setHeight(10);
		textFont = new Font(font.getDevice(), fontData[0]);
		edSrcSeg.setFont(textFont);
		
		edTrgSeg = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		edTrgSeg.setEditable(false);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		gdTmp.heightHint = 16;
		edTrgSeg.setLayoutData(gdTmp);
		edTrgSeg.setFont(textFont);

		Label label = new Label(shell, SWT.NONE);
		label.setText("Full text unit:");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		label.setLayoutData(gdTmp);
		
		edSource = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		edSource.setEditable(false);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		gdTmp.heightHint = 32;
		edSource.setLayoutData(gdTmp);
		
		edTarget = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		edTarget.setEditable(false);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		gdTmp.heightHint = 32;
		edTarget.setLayoutData(gdTmp);
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = 0;
				if ( e.widget.getData().equals("h") ) {
					//TODO: UIUtil.start(help);
					return;
				}
				if ( e.widget.getData().equals("o") ) {
					result = 1;
					if ( !saveData() ) return;
				}
				hide();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 700 ) startSize.x = 700; 
		if ( startSize.y < 600 ) startSize.y = 600; 
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
	}
	
	private void synchronizeFromSource () {
		updateSourceSegmentDisplay();
		int n = srcList.getSelectionIndex();
		if ( n >= trgList.getItemCount() ) return; // Cannot synchronize
		trgList.select(n);
		updateTargetSegmentDisplay();
	}

	private void synchronizeFromTarget () {
		updateTargetSegmentDisplay();
		int n = trgList.getSelectionIndex();
		if ( n >= srcList.getItemCount() ) return; // Cannot synchronize
		srcList.select(n);
		updateSourceSegmentDisplay();
	}

	private void updateSourceDisplay () {
		edSource.setText(genericCont.printSegmentedContent(source, true));
	}
	
	private void updateTargetDisplay () {
		edTarget.setText(genericCont.printSegmentedContent(target, true));
	}
	
	private void updateSourceSegmentDisplay () {
		int n = srcList.getSelectionIndex();
		if ( n < 0 ) edSrcSeg.setText("");
		edSrcSeg.setText(srcList.getItem(n));
	}
	
	private void updateTargetSegmentDisplay () {
		int n = trgList.getSelectionIndex();
		if ( n < 0 ) edTrgSeg.setText("");
		edTrgSeg.setText(trgList.getItem(n));

		n = trgList.getSelectionIndex();
		int count = trgList.getItemCount();
		btMoveUp.setEnabled(n>0);
		btMoveDown.setEnabled(( n < count-1 ) && ( n > -1 ));
		btMerge.setEnabled(n>0);
		btSplit.setEnabled(( count > 1 ) && ( n > -1 ));
	}
	
	private void moveUp () {
		updateTargetSegmentDisplay();
	}
	
	private void moveDown () {
		updateTargetSegmentDisplay();
	}
	
	private void mergeWithAbove () {
		try {
			if ( splitMode ) {
				endSplitMode(false);
				return;
			}
			int n = trgList.getSelectionIndex();
			if ( n < 1  ) return;
			target.joinSegmentWithNext(n-1);
			updateTargetDisplay();
			fillTargetList(n-1);
		}
		catch ( Throwable e) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void startSplitMode () {
		indexSegToResplit = trgList.getSelectionIndex();
		if ( indexSegToResplit == -1 ) return;
		splitMode = true;
		
		genericCont.setContent(target.getSegments().get(indexSegToResplit));
		edTrgSeg.setText(genericCont.toString());
		
		edTrgSeg.setEditable(true);
		edTrgSeg.setFocus();
		edSrcSeg.setText("To split: Place the cursor between the characters where you want to split the target segment, and press \"Accept\"\n"
			+ "To cancel: Press \"Discard\"");
		btSplit.setText("Accept");
		shell.setDefaultButton(btSplit);
		btMerge.setText("Discard");
		btMerge.setEnabled(true); // Make sure it's enabled
		srcList.setEnabled(false);
		trgList.setEnabled(false);
		btMoveUp.setVisible(false);
		btMoveDown.setVisible(false);
		chkShowInlineCodes.setVisible(false);
		chkSyncScrolling.setVisible(false);
	}
	
	private void endSplitMode (boolean accept) {
		try {
			// Compute the new segmentation
			if ( accept ) {
				// genericCont is already set with the proper text
				Point sel = genericCont.getCodedTextPosition(edTrgSeg.getSelection());
				splitSegment(indexSegToResplit, sel.x, sel.y);
			}
			// Reset the controls
			edTrgSeg.setEditable(false);
			btSplit.setText("Split...");
			btMerge.setText("Merge with Above");
			srcList.setEnabled(true);
			trgList.setEnabled(true);
			btMoveUp.setVisible(true);
			btMoveDown.setVisible(true);
			chkShowInlineCodes.setVisible(true);
			chkSyncScrolling.setVisible(true);
			splitMode = false;
			// Update the display
			if ( accept ) {
				updateTargetDisplay();
				fillTargetList(indexSegToResplit);
			}
			else updateTargetSegmentDisplay();
			
			shell.setDefaultButton(pnlActions.btOK);
			trgList.setFocus();
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
		// Merge the segment to re-split
		int pos = target.mergeSegment(segIndex);
		// Now pos value is the position 0 of the segment character indices
		if ( pos == -1 ) return; // Segment index not found
		// First new segment goes from start of original to start selection
		target.createSegment(pos, pos+start);
		// Second new segment goes from end selection to end of original segment
		target.createSegment(pos+end, -1);
	}
	
	public void close () {
		shell.close();
	}
	
	public int showDialog (TextContainer sourceContainer,
		TextContainer targetContainer,
		String document,
		String cause)
	{
		this.source = sourceContainer;
		this.target = targetContainer;
		edDocument.setText(document);
		TextUnit tu = source.getParent();
		edName.setText("");
		if ( tu != null ) {
			if ( tu.getName().length() > 0 ) {
				edName.setText(tu.getName());
			}
		}
		edCause.setText(cause);
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
	
	public void hide () {
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
		updateTargetSegmentDisplay();
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
}
