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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class TextUnitEditorPanel {

	private StyledText edit;
	private TextStyle codeStyle;
	private TextStyle markStyle;
	private TextOptions srcOptions;
	private TextOptions trgOptions;
	
	public TextUnitEditorPanel (Composite parent,
		int flag)
	{
		if ( flag < 0 ) { // Use the default styles if requested
			flag = SWT.WRAP | SWT.V_SCROLL | SWT.BORDER;
		}
		edit = new StyledText(parent, flag);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		edit.setLayoutData(gdTmp);
		
		codeStyle = new TextStyle();
		codeStyle.foreground = parent.getDisplay().getSystemColor(SWT.COLOR_RED);
		
		markStyle = new TextStyle();
		markStyle.foreground = parent.getDisplay().getSystemColor(SWT.COLOR_GRAY);

		edit.setMargins(2, 2, 2, 2);
		edit.setKeyBinding(SWT.CTRL|'a', ST.SELECT_ALL);

		// Disable Cut/Copy/Paste commands to override them
		edit.setKeyBinding(SWT.CTRL|'c', SWT.NULL);
		edit.setKeyBinding(SWT.CTRL|'v', SWT.NULL);
		edit.setKeyBinding(SWT.SHIFT|SWT.DEL, SWT.NULL);
		edit.setKeyBinding(SWT.SHIFT|SWT.INSERT, SWT.NULL);

		// Create a copy of the default text field options for the source
		srcOptions = new TextOptions(parent.getDisplay(), edit);
		Font tmp = srcOptions.font;
		// Make the font a bit larger by default
		FontData[] fontData = tmp.getFontData();
		fontData[0].setHeight(fontData[0].getHeight()+3);
		srcOptions.font = new Font(parent.getDisplay(), fontData[0]);

		// Create a copy of the default text field options for the target
		trgOptions = new TextOptions(parent.getDisplay(), edit);
		tmp = srcOptions.font;
		// Make the font a bit larger by default
		fontData = tmp.getFontData();
		fontData[0].setHeight(fontData[0].getHeight()+3);
		trgOptions.font = new Font(parent.getDisplay(), fontData[0]);

		
	}

	@Override
	protected void finalize () {
		dispose();
	}

	public void dispose () {
		if ( srcOptions != null ) {
			srcOptions.dispose();
			srcOptions = null;
		}
		if ( trgOptions != null ) {
			trgOptions.dispose();
			trgOptions = null;
		}
	}

	public boolean setFocus () {
		return edit.setFocus();
	}

	public void setEnabled (boolean enabled) {
		edit.setEnabled(enabled);
	}

}
