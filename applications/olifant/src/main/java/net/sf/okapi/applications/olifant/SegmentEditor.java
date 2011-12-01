/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.applications.olifant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

class SegmentEditor {

	private static final Pattern CLEANCODES = Pattern.compile("\\<[/be]?(\\d+?)/?\\>");
	private static final Pattern REALCODES = Pattern.compile("\\<[/be]?(\\d+?)/?\\>"); //TODO

	private StyledText edit;
	private boolean modified;
	private boolean fullCodesMode = false;
	private Pattern currentCodes;
	
	public SegmentEditor (Composite parent,
		int flags)
	{
		if ( flags < 0 ) { // Use the default styles if requested
			flags = SWT.WRAP | SWT.V_SCROLL | SWT.BORDER;
		}
		edit = new StyledText(parent, flags);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		edit.setLayoutData(gdTmp);
		
		currentCodes = CLEANCODES;
		
		edit.addExtendedModifyListener(new ExtendedModifyListener() {
			@Override
			public void modifyText(ExtendedModifyEvent event) {
				String text = edit.getText();
		    	  java.util.List<StyleRange> ranges = new java.util.ArrayList<StyleRange>();
		    	  Matcher m = currentCodes.matcher(text);
		    	  while ( m.find() ) {
		    		  ranges.add(new StyleRange(m.start(), m.end()-m.start(),
		    			  edit.getDisplay().getSystemColor(SWT.COLOR_GRAY),
		    			  null));
		    	  }		    	  
		    	  if ( !ranges.isEmpty() ) {
		    		  edit.replaceStyleRanges(0, text.length(),
		    			  (StyleRange[])ranges.toArray(new StyleRange[0]));
		    	  }
		    	  modified = true;
			}
		});
		
//		edit.addVerifyKeyListener(new VerifyKeyListener() {
//			@Override
//			public void verifyKey(VerifyEvent e) {
//				if ( e.stateMask == SWT.ALT ) {
//					switch ( e.keyCode ) {
//					case SWT.ARROW_RIGHT:
//						selectNextCode(edit.getCaretOffset(), true);
//						e.doit = false;
//						break;
//					case SWT.ARROW_LEFT:
//						selectPreviousCode(edit.getCaretOffset(), true);
//						e.doit = false;
//						break;
////					case SWT.ARROW_DOWN: // Target-mode command
////						setNextSourceCode();
////						e.doit = false;
////						break;
////					case SWT.ARROW_UP: // Target-mode command
////						setPreviousSourceCode();
////						e.doit = false;
////						break;
//					}
//				}
//				else if ( e.stateMask == SWT.CTRL ) {
//					switch ( e.keyCode ) {
////					case 'd':
////						cycleDisplayMode();
////						e.doit = false;
////						break;
////					case 'c':
////						copyToClipboard(edit.getSelection());
////						e.doit = false;
////						break;
////					case 'v':
////						pasteFromClipboard();
////						e.doit = false;
////						break;
//					case ' ':
//						placeText("\u00a0");
//						e.doit = false;
//						break;
//					}
//				}
////				else if ( e.stateMask == SWT.SHIFT ) {
////					switch ( e.keyCode ) {
////					case SWT.DEL:
////						cutToClipboard(edit.getSelection());
////						e.doit = false;
////						break;
////					case SWT.INSERT:
////						pasteFromClipboard();
////						e.doit = false;
////						break;
////					}
////				}
//				else if ( e.keyCode == SWT.DEL ){
//					int i = 0;
//				}
//			}
//		});
		
	}

	public boolean setFocus () {
		return edit.setFocus();
	}
	
	public void setEnabled (boolean enabled) {
		edit.setEnabled(enabled);
	}
	
	public void setEditable (boolean editable) {
		edit.setEditable(editable);
	}

	public void clear () {
		edit.setText("");
		modified = false;
	}
	
	public boolean isModified () {
		return modified;
	}

	public void setText (String text) {
		edit.setEnabled(text != null);
		if ( text == null ) {
			edit.setText("");
		}
		else {
			edit.setText(text);
		}
		modified = false;
	}

	public String getText () {
		return edit.getText();
	}

//	private void placeText (String text) {
//		Point pt = edit.getSelection();
//		edit.replaceTextRange(pt.x, pt.y-pt.x, text);
//		edit.setCaretOffset(pt.x+text.length());
//	}
//	
//	private void selectNextCode (int position,
//		boolean cycle)
//	{
//		StyleRange[] ranges = edit.getStyleRanges();
//		if ( ranges.length == 0 ) return;
//		while ( true ) {
//			for ( StyleRange range : ranges ) {
//				if ( position <= range.start ) {
//					edit.setSelection(range.start, range.start+range.length);
//					return;
//				}
//			}
//			// Not found yet: Stop here if we don't cycle to the first
//			if ( !cycle ) return;
//			position = 0; // Otherwise: re-start from front
//		}
//	}
	
//	private void selectPreviousCode (int position,
//		boolean cycle)
//	{
//		StyleRange[] ranges = edit.getStyleRanges();
//		if ( ranges.length == 0 ) return;
//		StyleRange sr;
//		while ( true ) {
//			for ( int i=ranges.length-1; i>=0; i-- ) {
//				sr = ranges[i];
//				if ( position >= sr.start+sr.length ) {
//					Point pt = edit.getSelection();
//					if (( pt.x == sr.start ) && ( pt.x != pt.y )) continue;
//					edit.setSelection(sr.start, sr.start+sr.length);
//					return;
//				}
//			}
//			// Not found yet: Stop here if we don't cycle to the first
//			if ( !cycle ) return;
//			position = edit.getCharCount()-1; // Otherwise: re-start from the end
//		}
//	}

}
