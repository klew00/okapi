/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.lib.ui.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.ui.Dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class FragmentEditorPanel {

	private String codedText;
	private List<Code> codes;
	private TextFragment frag;
	private StyledText edit;
	private TextStyle codeStyle;
	private int mode = 0;
	private Menu contextMenu;
	private TextOptions textOptions;
	private ArrayList<StyleRange> ranges;
	private boolean updateCodeRanges = false;
	private int prevPos = 0;
	private boolean shiftDown = false;
	private boolean mouseDown = false;
	private boolean targetMode = false;
	private FragmentEditorPanel source;
	private PairEditorPanel parentPanel;
	private int nextCodeForCopy = -1;
	
	public FragmentEditorPanel (Composite parent,
		int flag,
		boolean paramTargetMode)
	{
		targetMode = paramTargetMode;
		if ( flag < 0 ) { // Use the default styles if requested
			flag = SWT.WRAP | SWT.V_SCROLL | SWT.BORDER;
		}
		edit = new StyledText(parent, flag);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		edit.setLayoutData(gdTmp);
		
		codeStyle = new TextStyle();
		codeStyle.foreground = parent.getDisplay().getSystemColor(SWT.COLOR_RED);
		//codeStyle.background = parent.getDisplay().getSystemColor(SWT.COLOR_CYAN);
		//codeStyle.foreground = parent.getDisplay().getSystemColor(SWT.COLOR_GREEN);
		
		createContextMenu();
		edit.setMenu(contextMenu);
		
		edit.addCaretListener(new CaretListener() {
			@Override
			public void caretMoved(CaretEvent e) {
				for ( StyleRange range : ranges ) {
					if (( e.caretOffset > range.start ) && ( e.caretOffset < range.start+range.length )) {
						boolean backward = false;
						if ( prevPos < e.caretOffset ) prevPos = range.start+range.length;
						else {
							prevPos = range.start;
							backward = true;
						}
						if ( shiftDown || mouseDown ) {
							Point pt = edit.getSelection();
							if ( backward ) {
								pt.x = pt.y; pt.y = prevPos;
								edit.setSelection(pt);
							}
							else {
								pt.y = prevPos;
								edit.setSelection(pt);
							}
						}
						else {
							edit.setCaretOffset(prevPos);
						}
						return;
					}
				}
				// Else: No in a code range: just remember the position
				prevPos = e.caretOffset;
			}
		});
		
		edit.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
				mouseDown = false;
			}
			@Override
			public void mouseDown(MouseEvent e) {
				mouseDown = true;
			}
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		
		edit.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				if ( e.keyCode == SWT.SHIFT ) {
					shiftDown = false;
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				if ( e.keyCode == SWT.SHIFT ) {
					shiftDown = true;
				}
			}
		});
		
		edit.addVerifyKeyListener(new VerifyKeyListener() {
			@Override
			public void verifyKey(VerifyEvent e) {
				if ( e.stateMask == SWT.ALT ) {
					switch ( e.keyCode ) {
					case SWT.ARROW_RIGHT:
						selectNextCode(edit.getCaretOffset(), true);
						break;
					case SWT.ARROW_LEFT:
						selectPreviousCode(edit.getCaretOffset(), true);
						break;
					case SWT.ARROW_DOWN: // Target-mode command
						setNextSourceCode();
						break;
					case SWT.ARROW_UP: // Target-mode command
						setPreviousSourceCode();
						break;
					case SWT.HOME:
						selectFirstCode();
						break;
					}
				}
				else if (( e.stateMask == SWT.CTRL ) && ( e.keyCode == 'd' )) {
					cycleDisplayMode();
				}
			}
		});

		edit.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				if ( !updateCodeRanges || ( e.start == edit.getCharCount() )) {
					return; // OK in all cases, and no need to update code ranges
				}
				int len = e.end-e.start;
				if ( len == 0 ) {
					if ( e.start > 0 ) {
						int n = isOnCodeRange(e.start);
						if (( n != -1 ) && ( n != e.start )) {
							e.doit = false;
							return;
						}
					} // Position zero always OK, and ranges still need to be updated
				}
				else {
					if ( breakRange(e.start, e.end) ) {
						e.doit = false;
						return;
					}
				}
				// Modification is allowed: Update the code ranges
				updateRanges(e.start, e.end, e.text.length());
			}
		});

		edit.setMargins(2, 2, 2, 2);
		edit.setKeyBinding(SWT.CTRL|'a', ST.SELECT_ALL);
		
		// Create a copy of the default text field options for the source
		textOptions = new TextOptions(parent.getDisplay(), edit);
		Font tmp = textOptions.font;
		// Make the font a bit larger by default
		FontData[] fontData = tmp.getFontData();
		fontData[0].setHeight(fontData[0].getHeight()+4);
		textOptions.font = new Font(parent.getDisplay(), fontData[0]);

		applyTextOptions(textOptions);
	}
	
	@Override
	protected void finalize () {
		dispose();
	}

	public void dispose () {
		if ( textOptions != null ) {
			textOptions.dispose();
			textOptions = null;
		}
	}

	public boolean setFocus () {
		return edit.setFocus();
	}
	
	/**
	 * Sets the fragment editor for the corresponding source. This must be set when this control is in target mode.
	 * @param source the fragment editor for the source.
	 */
	public void setTargetRelations (FragmentEditorPanel source,
		PairEditorPanel parentPanel)
	{
		this.source = source;
		this.parentPanel = parentPanel;
	}
	
	public void setEnabled (boolean enabled) {
		edit.setEnabled(enabled);
	}
	
	public void setEditable (boolean editable) {
		edit.setEditable(editable);
	}

	public void applyTextOptions (TextOptions textOptions) {
		edit.setBackground(textOptions.background);
		edit.setForeground(textOptions.foreground);
		edit.setFont(textOptions.font);
		edit.setOrientation(textOptions.isBidirectional ? SWT.RIGHT_TO_LEFT : SWT.LEFT_TO_RIGHT);
	}
	
	private void createContextMenu () {
		contextMenu = new Menu(edit.getShell(), SWT.POP_UP);

		MenuItem item = new MenuItem(contextMenu, SWT.PUSH);
		item.setText("Change Code Display Mode");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				cycleDisplayMode();
            }
		});
		
		if ( targetMode ) {
			new MenuItem(contextMenu, SWT.SEPARATOR);
		
			item = new MenuItem(contextMenu, SWT.PUSH);
			item.setText("Remove All Codes");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					clearCodes();
	            }
			});

			item = new MenuItem(contextMenu, SWT.PUSH);
			item.setText("Copy Source Into Target");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					pasteSource();
	            }
			});

			item = new MenuItem(contextMenu, SWT.PUSH);
			item.setText("Copy All Source Codes Into Target");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					pasteAllSourceCodes();
	            }
			});
			
			new MenuItem(contextMenu, SWT.SEPARATOR);
			
			item = new MenuItem(contextMenu, SWT.PUSH);
			item.setText("Switch Panel Orientation");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					parentPanel.setOrientation(parentPanel.getOrientation() == SWT.VERTICAL ? SWT.HORIZONTAL : SWT.VERTICAL);
	            }
			});
		}
	
	}
	
	private void cycleDisplayMode () {
		Point pt = cacheContent(edit.getSelection());
		if ( pt == null ) return;
		mode = (mode==0 ? 1 : (mode==2 ? 0 : 2));
		updateText(pt);
	}
	
	public void setText (TextFragment oriFrag) {
		frag = oriFrag;
		codedText = frag.getCodedText();
		// Make a copy of the list, as getCodes() gives an un-modifiable list
		//TODO: do we need a deep-copy in case we modify the actual codes data?
		codes = new ArrayList<Code>(frag.getCodes());
		updateText(null);
	}

	/**
	 * Gets the text in the edit control to a coded-text string with the proper code
	 * markers. This also makes basic validation.
	 * @param sel optional selection to re-compute. Use null to not use.
	 * @return a Point corresponding to the converted selection passed as parameter. Will be 0 and 0
	 * if the given selection was null. Return null if an error occurred
	 * and the text could not be cached.
	 */
	private Point cacheContent (Point sel) {
		try {
			Point pt = new Point(0, 0);
			if ( sel != null ) {
				pt.x = sel.x; pt.y = sel.y;
			}
			Code code = null;
			StringBuilder tmp = new StringBuilder(edit.getText());
			int diff = 0;
			for ( StyleRange range : ranges ) {
				code = (Code)range.data;
				int index = getCodeObjectIndex(code);
				switch ( code.getTagType() ) {
				case OPENING:
					tmp.replace(diff+range.start, diff+(range.start+range.length),
						String.format("%c%c", (char)TextFragment.MARKER_OPENING, TextFragment.toChar(index)));
					break;
				case CLOSING:
					tmp.replace(diff+range.start, diff+(range.start+range.length),
						String.format("%c%c", (char)TextFragment.MARKER_CLOSING, TextFragment.toChar(index)));
					break;
				case PLACEHOLDER:
					tmp.replace(diff+range.start, diff+(range.start+range.length),
						String.format("%c%c", (char)TextFragment.MARKER_ISOLATED, TextFragment.toChar(index)));
					break;
				}
				diff += (2-range.length);

				// Compute new selection if needed
				if ( sel != null ) {
					if ( sel.x >= range.start+range.length ) pt.x += (2-range.length);
					if ( sel.y >= range.start+range.length ) pt.y += (2-range.length);
				}
			}
			codedText = tmp.toString();
			return pt;
		}
		catch ( Throwable e ) {
			Dialogs.showError(edit.getShell(), "Error when retrieving edited text.\n"+e.getLocalizedMessage(), null);
			return null;
		}
	}

	/**
	 * Gets the index in the codes array of the given code object.
	 * @param codeToSearch the code to search for.
	 * @return the index of the given code in the codes array, or -1 if not found.
	 */
	private int getCodeObjectIndex (Code codeToSearch) {
		Code code;
		for ( int i=0; i<codes.size(); i++ ) {
			if ( codeToSearch == codes.get(i) ) {
				return i;
			}
		}
		return -1; // Not found
	}
	
	private void updateRanges (int start,
		int end,
		int length)
	{
		// Check for flag
		// This is needed because resetting the content means we have ranges set before
		// the text is set in the control.
		if ( !updateCodeRanges ) return;
		
		// Compute the length difference between the selection part and the replacement
		length = length-(end-start);

		// Update ranges after the end
		Iterator<StyleRange> iter = ranges.iterator();
		StyleRange range;
		while ( iter.hasNext() ) {
			range = iter.next();
			// Is the range is after or at the end of the modified text
			if ( end <= range.start ) {
				range.start += length;
			}
			// Otherwise, if the range is included in the selection it's deletion or replacement
			// So that range needs to be removed, along with its code
			else if (( start <= range.start ) && ( end >= range.start+range.length )) {
				// Remove the code and the range
				codes.remove((Code)range.data);
				iter.remove();
			}
		}
	}
	
	/**
	 * Moves the select in this editor to the next code and returns it.
	 * @return the next code in this editor, or null if no code was found.
	 */
	public FragmentData getNextCode () {
		if ( codes.isEmpty() ) return null;
		nextCodeForCopy++;
		if ( nextCodeForCopy >= codes.size() ) {
			nextCodeForCopy = 0;
		}
		return getCode(nextCodeForCopy);
	}

	public FragmentData getPreviousCode () {
		if ( codes.isEmpty() ) return null;
		nextCodeForCopy--;
		if ( nextCodeForCopy < 0 ) {
			nextCodeForCopy = codes.size()-1;
		}
		return getCode(nextCodeForCopy);
	}

	public FragmentData getAllContent () {
		FragmentData data = new FragmentData();
		cacheContent(null);
		data.codedText = codedText;
		data.codes = new ArrayList<Code>(codes);
		return data;
	}
	
	public FragmentData getAllCodes () {
		// Get the code
		FragmentData data = new FragmentData();
		data.codes = new ArrayList<Code>();
		StringBuilder tmp = new StringBuilder();
		for ( Code code : codes ) {
			data.codes.add(code.clone());
			// Construct the coded text
			switch ( code.getTagType() ) {
			case OPENING:
				tmp.append(String.format("%c%c", (char)TextFragment.MARKER_OPENING,
					TextFragment.toChar(data.codes.size()-1)));
				break;
			case CLOSING:
				tmp.append(String.format("%c%c", (char)TextFragment.MARKER_CLOSING,
					TextFragment.toChar(data.codes.size()-1)));
				break;
			case PLACEHOLDER:
				tmp.append(String.format("%c%c", (char)TextFragment.MARKER_ISOLATED,
					TextFragment.toChar(data.codes.size()-1)));
				break;
			}
		}
		data.codedText = tmp.toString();
		return data;
	}
	
	private FragmentData getCode (int index) {
		// Get the code
		FragmentData data = new FragmentData();
		data.codes = new ArrayList<Code>();
		Code code = codes.get(index);
		data.codes.add(code.clone());
		
		// Construct the coded text
		switch ( code.getTagType() ) {
		case OPENING:
			data.codedText = String.format("%c%c", (char)TextFragment.MARKER_OPENING, TextFragment.toChar(0));
			break;
		case CLOSING:
			data.codedText = String.format("%c%c", (char)TextFragment.MARKER_CLOSING, TextFragment.toChar(0));
			break;
		case PLACEHOLDER:
			data.codedText = String.format("%c%c", (char)TextFragment.MARKER_ISOLATED, TextFragment.toChar(0));
			break;
		}
		
		// Select the corresponding location in the editor
		for ( StyleRange range : ranges ) {
			if ( code == (Code)range.data ) {
				edit.setSelection(range.start, range.start+range.length);
				break;
			}
		}
		
		return data;
	}
	
	/**
	 * Indicates if a given position is on a code range.
	 * @param position the given position.
	 * @return the start of the first code range found for the position.
	 */
	private int isOnCodeRange (int position) {
		for ( StyleRange range : ranges ) {
			if (( position >= range.start ) && ( position < range.start+range.length )) {
				return range.start;
			}
		}
		return -1;
	}

//	private StyleRange getCodeRange (int position) {
//		for ( StyleRange range : ranges ) {
//			if (( position >= range.start ) && ( position < range.start+range.length )) {
//				return range;
//			}
//		}
//		return null;
//	}

	/**
	 * Indicates if the given selection does break one of the code ranges.
	 * @param start the start of the selection to check.
	 * @param end the end of the selection to check.
	 * @return true if the selection falls within one of the code ranges.
	 */
	private boolean breakRange (int start,
		int end)
	{
		//end--; // Look at position just before the end of the selection
		for ( StyleRange range : ranges ) {
			if (( start > range.start ) && ( start < range.start+range.length )) {
				return true;
			}
			if (( end > range.start ) && ( end < range.start+range.length )) {
				return true;
			}
		}
		return false;
	}
	
	private void updateText (Point sel) {
		try {
			updateCodeRanges = false;
			StringBuilder tmp = new StringBuilder();
			ranges = new ArrayList<StyleRange>();
			int pos = 0;
			StyleRange sr;
			String disp = null;
			Point pt = new Point(0, 0);
			if ( sel != null ) {
				pt.x = sel.x; pt.y = sel.y;
			}
			Code code;
			for ( int i=0; i<codedText.length(); i++ ) {
				if ( TextFragment.isMarker(codedText.charAt(i)) ) {
					code = codes.get(TextFragment.toIndex(codedText.charAt(++i)));
					switch ( code.getTagType() ) {
					case OPENING:
						if ( mode == 1 ) disp = code.getData();
						else if ( mode == 2 ) disp = "["+code.getData()+"]";
						else disp = String.format("<%d>", code.getId());
						break;
					case CLOSING:
						if ( mode == 1 ) disp = code.getData();
						else if ( mode == 2 ) disp = "["+code.getData()+"]";
						else disp = String.format("</%d>", code.getId());
						break;
					case PLACEHOLDER:
						if ( mode == 1 ) disp = code.getData();
						else if ( mode == 2 ) disp = "["+code.getData()+"]";
						else disp = String.format("<%d/>", code.getId());
						break;
					}
					tmp.append(disp);
					sr = new StyleRange(codeStyle);
					sr.start = pos;
					sr.length = disp.length();
					sr.data = code;
					ranges.add(sr);
					pos += disp.length();

					// Update the selection if needed
					if ( sel != null ) {
						if ( sel.x >= i ) pt.x += (disp.length()-2);
						if ( sel.y >= i ) pt.y += (disp.length()-2);
					}
				}
				else {
					tmp.append(codedText.charAt(i));
					pos++;
				}
			}
			
			nextCodeForCopy = -1;
			edit.setText(tmp.toString());
			for ( StyleRange range : ranges ) {
				edit.setStyleRange(range);
			}
			if ( sel != null ) {
				edit.setSelection(pt);
			}
		}
		catch ( Throwable e ) {
			Dialogs.showError(edit.getShell(), "Error when updating text.\n"+e.getLocalizedMessage(), null);
		}
		finally {
			updateCodeRanges = true;
		}
	}
	
//	private FragmentData copySelection () {
//		Point pt = edit.getSelection();
//		if ( pt.x == pt.y ) return null; // Nothing to copy
//		
//		String text = edit.getText(pt.x, pt.y);
//		
////		for ( StyleRange range : ranges ) {
////			if (( pt.x >= range.start ) && ( position < range.start+range.length )) {
////				
////			}
////		}
//		
//		return null;
//	}
	
	private void pasteSource () {
		if ( !targetMode ) return;
		setFragmentData(source.getAllContent(), 1);
	}
	
	private void pasteAllSourceCodes () {
		if ( !targetMode ) return;
		setFragmentData(source.getAllCodes(), 1);
	}
	
	private void setNextSourceCode () {
		if ( !targetMode ) return;
		setFragmentData(source.getNextCode(), 0);
	}

	private void setPreviousSourceCode () {
		if ( !targetMode ) return;
		setFragmentData(source.getPreviousCode(), 0);
	}

	/**
	 * Sets a FragmentData into this editor. The fragment replaces the current selection.
	 * @param data the fragment data to set.
	 * @param positionAfter Indicates how to place the caret after:
	 * <ul><li>1=place the caret just before
	 * <li>2=place the caret just after
	 * <li>0 or other=select the part placed
	 */
	private void setFragmentData (FragmentData data,
		int positionAfter)
	{
		try {
			if ( data == null ) return; // Nothing to do
	
			// Remove the current selection
			// This removes any underlying ranges and codes
			Point sel = edit.getSelection();
			remove(sel.x, sel.y);
	
			// Find if there is a code just after or at the insertion point
			// Get the index of the first range after the insertion position
			int index = 0;
			for ( StyleRange range : ranges ) {
				if ( range.start >= sel.x ) break;
				index++;
			}
	
			// Insert the new codes and ranges and build the display text
			StringBuilder tmp = new StringBuilder();
			String disp = null;
			Code code;
			int pos = sel.x;
			StyleRange sr;
			ArrayList<StyleRange> newRanges = new ArrayList<StyleRange>();
			int insPos = index;
			for ( int i=0; i<data.codedText.length(); i++ ) {
				if ( TextFragment.isMarker(data.codedText.charAt(i)) ) {
					code = data.codes.get(TextFragment.toIndex(data.codedText.charAt(++i))).clone();
					switch ( code.getTagType() ) {
					case OPENING:
						if ( mode == 1 ) disp = code.getData();
						else if ( mode == 2 ) disp = "["+code.getData()+"]";
						else disp = String.format("<%d>", code.getId());
						break;
					case CLOSING:
						if ( mode == 1 ) disp = code.getData();
						else if ( mode == 2 ) disp = "["+code.getData()+"]";
						else disp = String.format("</%d>", code.getId());
						break;
					case PLACEHOLDER:
						if ( mode == 1 ) disp = code.getData();
						else if ( mode == 2 ) disp = "["+code.getData()+"]";
						else disp = String.format("<%d/>", code.getId());
						break;
					}
					tmp.append(disp);
					sr = new StyleRange(codeStyle);
					sr.start = pos;
					sr.length = disp.length();
					sr.data = code;
					pos += disp.length();
					
					// Do not set the range immediately, so the text update can be done properly
					newRanges.add(sr);
					codes.add(insPos++, code);
				}
				else {
					tmp.append(data.codedText.charAt(i));
					pos++;
				}
			}
			
			// Insert the display text. This will update 
			edit.replaceTextRange(sel.x, 0, tmp.toString());
			// Set the ranges, and now add them to the list
			for ( StyleRange newRange : newRanges ) {
				edit.setStyleRange(newRange);
				ranges.add(index++, newRange);
			}
	
	//debug
	//cacheContent(null);
	//TextFragment tf1 = new TextFragment();
	//tf1.setCodedText(codedText, codes);
	//System.out.println(tf1.toString());
	
			// Place the caret
			switch ( positionAfter ) {
			case 1:
				edit.setCaretOffset(sel.x);
				break;
			case 2:
				edit.setCaretOffset(sel.x+tmp.length());
				break;
			default:
				edit.setSelection(sel.x, sel.x+tmp.length());
			}
		}
		catch ( Throwable e ) {
			Dialogs.showError(edit.getShell(), "Error when placing fragment data.\n"+e.getLocalizedMessage(), null);
		}
	}
	
	private void remove (int start,
		int end)
	{
		if ( start == end ) return; // Nothing to remove
		// Delete the text from the control
		edit.replaceTextRange(start, end-start, "");
	}
	
	private void selectFirstCode () {
		if ( ranges.size() == 0 ) return;
		StyleRange sr = ranges.get(0);
		edit.setSelection(sr.start, sr.start+sr.length);
	}
	
	private void selectNextCode (int position,
		boolean cycle)
	{
		if ( ranges.size() == 0 ) return;
		while ( true ) {
			for ( StyleRange range : ranges ) {
				if ( position <= range.start ) {
					edit.setSelection(range.start, range.start+range.length);
					return;
				}
			}
			// Not found yet: Stop here if we don't cycle to the first
			if ( !cycle ) return;
			position = 0; // Otherwise: re-start from front
		}
	}
	
	private void clearCodes () {
		if ( !edit.getEditable() ) return;

		cacheContent(null);
		StringBuilder tmp = new StringBuilder();
		for ( int i=0; i<codedText.length(); i++ ) {
			if ( TextFragment.isMarker(codedText.charAt(i)) ) {
				i++; // Skip
			}
			else {
				tmp.append(codedText.charAt(i));
			}
		}
		codes.clear();
		codedText = tmp.toString();
		// Ranges will get cleared in updateText()
		updateText(null);
		edit.setCaretOffset(0);
	}
	
	private void selectPreviousCode (int position,
		boolean cycle)
	{
		if ( ranges.size() == 0 ) return;
		StyleRange sr;
		while ( true ) {
			for ( int i=ranges.size()-1; i>=0; i-- ) {
				sr = ranges.get(i);
				if ( position >= sr.start+sr.length ) {
					Point pt = edit.getSelection();
					if (( pt.x == sr.start ) && ( pt.x != pt.y )) continue;
					edit.setSelection(sr.start, sr.start+sr.length);
					return;
				}
			}
			// Not found yet: Stop here if we don't cycle to the first
			if ( !cycle ) return;
			position = edit.getCharCount()-1; // Otherwise: re-start from the end
		}
	}
		
	
}
