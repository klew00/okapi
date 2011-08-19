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

	private static final Pattern CODES = Pattern.compile("\\<[/be]?(\\d+?)/?\\>");

	private StyledText edit;
	private boolean modified;
	
	
	public SegmentEditor (Composite parent,
		int flags)
	{
		if ( flags < 0 ) { // Use the default styles if requested
			flags = SWT.WRAP | SWT.V_SCROLL | SWT.BORDER;
		}
		edit = new StyledText(parent, flags);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		edit.setLayoutData(gdTmp);
		
		edit.addExtendedModifyListener(new ExtendedModifyListener() {
			public void modifyText(ExtendedModifyEvent event) {
				String text = edit.getText();
		    	  java.util.List<StyleRange> ranges = new java.util.ArrayList<StyleRange>();
		    	  Matcher m = CODES.matcher(text);
		    	  while ( m.find() ) {
		    		  ranges.add(new StyleRange(m.start(), m.end()-m.start(),
		    			  edit.getDisplay().getSystemColor(SWT.COLOR_GRAY),
		    			  null, 0));
		    	  }		    	  
		    	  if ( !ranges.isEmpty() ) {
		    		  edit.replaceStyleRanges(0, text.length(),
		    			  (StyleRange[])ranges.toArray(new StyleRange[0]));
		    	  }
		    	  modified = true;
			}
		});
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

}
