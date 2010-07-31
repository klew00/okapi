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
	private boolean shiftMode = false;
	
	public FragmentEditorPanel (Composite parent,
		int flag)
	{
		if ( flag < 0 ) { // Use the default styles if requested
			flag = SWT.WRAP | SWT.V_SCROLL | SWT.BORDER;
		}
		edit = new StyledText(parent, flag);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		edit.setLayoutData(gdTmp);
		
		codeStyle = new TextStyle();
		//codeStyle.foreground = parent.getDisplay().getSystemColor(SWT.COLOR_RED);
		codeStyle.background = parent.getDisplay().getSystemColor(SWT.COLOR_CYAN);

		createContextMenu();
		edit.setMenu(contextMenu);
		
//		edit.addVerifyKeyListener(new VerifyKeyListener() {
//			@Override
//			public void verifyKey(VerifyEvent e) {
//				Point pt = edit.getSelectionRange();
//				if ( pt.x < 0 ) return;
//				if ( pt.y == 0 ) {
//					if ( e.character == '\u0008' ) {
//						if ( --pt.x < 0) {
//							return;
//						}
//						if ( edit.getStyleRangeAtOffset(pt.x) != null ) {
//							e.doit = false;
//						}
//					}
//					else if ( e.character == '\u007f' ) {
//						if ( pt.x >= edit.getCharCount() ) return;
//						if ( edit.getStyleRangeAtOffset(pt.x) != null ) {
//							e.doit = false;
//						}
//					}
//				}
//				// Else: length > 0
//			}
//		});
		
		edit.addCaretListener(new CaretListener() {
			@Override
			public void caretMoved(CaretEvent e) {
				for ( StyleRange range : ranges ) {
					if (( e.caretOffset > range.start ) && ( e.caretOffset < range.start+range.length )) {
						if ( prevPos < e.caretOffset ) prevPos = range.start+range.length;
						else prevPos = range.start;
						if ( shiftMode ) {
							Point pt = edit.getSelection(); pt.y = prevPos;
							edit.setSelection(pt);
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
		
		edit.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				if ( e.keyCode == SWT.SHIFT ) {
					shiftMode = false;
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				if ( e.keyCode == SWT.SHIFT ) {
					shiftMode = true;
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
					case SWT.HOME:
						selectFirstCode();
						break;
					}
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

		edit.setMargins(4, 4, 4, 4);
		edit.setKeyBinding(SWT.CTRL|'a', ST.SELECT_ALL);
		
		// Create a copy of the default text field options for the source
		textOptions = new TextOptions(parent.getDisplay(), edit);
		Font tmp = textOptions.font;
		// Make the font a bit larger by default
		FontData[] fontData = tmp.getFontData();
		fontData[0].setHeight(fontData[0].getHeight()+2);
		textOptions.font = new Font(parent.getDisplay(), fontData[0]);

		applyTextOptions(textOptions);
	}
	
	@Override
	protected void finalize () {
		dispose();
	}

	private void dispose () {
		if ( textOptions != null ) {
			textOptions.dispose();
			textOptions = null;
		}
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
		item.setText("Show/Hide Codes Content");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if ( !cacheContent() ) return;
				mode = (mode==0 ? 1 : 0);
				updateText();
            }
		});
	}
	
	public void setText (TextFragment oriFrag) {
		frag = oriFrag; // TODO: clone or not???
		codedText = frag.getCodedText();
		codes = frag.getCodes();
		updateText();
	}

	/**
	 * Gets the text in the edit control to a coded-text string with the proper code
	 * markers. This also makes basic validation.
	 * @return true if the text was cached without error. False if an error occurred
	 * and the text could not be cached.
	 */
	private boolean cacheContent () {
		try {
			Code code = null;
			StringBuilder tmp = new StringBuilder(edit.getText());
			//TODO: deal with empty string
			int diff = 0;
			for ( StyleRange range : ranges ) {
				code = (Code)range.data;
				int index = getCodeIndex(code);
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
			}
			codedText = tmp.toString();
			return true;
		}
		catch ( Throwable e ) {
			Dialogs.showError(edit.getShell(), "Error when retrieving edited text.\n"+e.getLocalizedMessage(), null);
			return false;
		}
	}

	private int getCodeIndex (Code codeToSearch) {
		Code code;
		for ( int i=0; i<codes.size(); i++ ) {
			code = codes.get(i);
			if ( code.getId() == codeToSearch.getId() ) {
				if ( code.getTagType() == codeToSearch.getTagType() ) {
					return i;
				}
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
		
		// For now, assumes a code range is not modifiable
		
		// If length is zero, it's a deletion
		if ( length == 0 ) {
			length = -1*(end-start);
		}
		// Update ranges after the end
		for ( StyleRange range : ranges ) {
			// Is the range after or at the end of the new text?
			if ( end <= range.start ) {
				range.start += length;
			}
		}
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
		end--; // Look at position just before the end of the selection
		for ( StyleRange range : ranges ) {
			if (( start >= range.start ) && ( start < range.start+range.length )) {
				return true;
			}
			if (( end >= range.start ) && ( end < range.start+range.length )) {
				return true;
			}
		}
		return false;
	}
	
	private void updateText () {
		try {
			updateCodeRanges = false;
			StringBuilder tmp = new StringBuilder();
			ranges = new ArrayList<StyleRange>();
			int pos = 0;
			StyleRange sr;
			String disp = null;
			Code code;
			for ( int i=0; i<codedText.length(); i++ ) {
				if ( TextFragment.isMarker(codedText.charAt(i)) ) {
					code = codes.get(TextFragment.toIndex(codedText.charAt(++i)));
					switch ( code.getTagType() ) {
					case OPENING:
						if ( mode == 1 ) disp = code.getData();
						else disp = String.format("<%d>", code.getId());
						break;
					case CLOSING:
						if ( mode == 1 ) disp = code.getData();
						else disp = String.format("</%d>", code.getId());
						break;
					case PLACEHOLDER:
						if ( mode == 1 ) disp = code.getData();
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
				}
				else {
					tmp.append(codedText.charAt(i));
					pos++;
				}
			}
			
			edit.setText(tmp.toString());
			for ( StyleRange range : ranges ) {
				edit.setStyleRange(range);
			}
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
		finally {
			updateCodeRanges = true;
		}
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
