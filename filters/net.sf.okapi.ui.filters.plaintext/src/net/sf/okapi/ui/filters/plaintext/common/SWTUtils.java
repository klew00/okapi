/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.ui.filters.plaintext.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.okapi.common.IHelp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * 
 * 
 * @version 0.1, 13.06.2009
 * @author Sergei Vasilyev
 */

public class SWTUtils {

	public static final String GET_CAPTION = "SWT_GET_CAPTION";

// Group enabling/disabling
	
	/**
	 * Sets the enabled state for all of a Composite's child Controls,
	 * including those of nested Composites.
	 *
	 * @param container
	 *            The Composite whose children are to have their
	 *            enabled state set.
	 * @param enabled
	 *            True if the Controls are to be enabled, false if
	 *            disabled.
	 * @param excludedControls
	 *            An optional array of Controls that should be excluded from
	 *            having their enabled state set. This is useful, for example,
	 *            to have a single widget enable/disable all of its siblings
	 *            other than itself.
	 */
	public static void setAllEnabled(Composite container, boolean enabled,
	        Control... excludedControls) {

		List<Control> excludes = null;
		
		if (excludedControls != null)
			excludes = Arrays.asList(excludedControls);
		else {
			excludes = new ArrayList<Control>();
			excludes.add(container);
		}
									
		container.setEnabled(enabled);
		
	    Control[] children = container.getChildren();
	    for (Control aChild : children) {
	        if (!excludes.contains(aChild)) {
	            aChild.setEnabled(enabled);
	            if (aChild instanceof Composite) {
	                setAllEnabled((Composite) aChild, enabled);
	            }
	        }
	    }
	}
	
	public static void setAllEnabled(Composite container, boolean enabled) {

		setAllEnabled(container, enabled, (Control[])null);
	}
	
// Input query
	private static Object result = null;
	
	public static boolean inputQuery(Shell parent, String caption, String prompt, int initialValue, IHelp help) {

		result = null;
		
		InputQueryDialog dlg = new InputQueryDialog();
		boolean res = dlg.run(parent, InputQueryPageInt.class, caption, prompt, initialValue, help);
		
		if (res) result = dlg.getResult();		
		return res;
	}
	
	public static boolean inputQuery(Shell parent, String caption, String prompt, String initialValue, IHelp help) {
		
		result = null;
		
		InputQueryDialog dlg = new InputQueryDialog();
		boolean res = dlg.run(parent, InputQueryPageString.class, caption, prompt, initialValue, help);
		
		if (res) result = dlg.getResult();		
		return res;
	}
	
	public static boolean inputQuery(Class<?> pageClass, Shell parent, String caption, Object initialData, IHelp help) {

		result = null;
		
		InputQueryDialog dlg = new InputQueryDialog();
		boolean res = dlg.run(parent, pageClass, caption, null, initialData, help);
		
		if (res) result = dlg.getResult();		
		return res;
	}

	public static Object getResult() {
		
		return result;
	}

// Radio group
	public static Button getRadioGroupSelection(Composite container) {
		
		if (container == null) return null;
		
	    for (Control aChild : container.getChildren())
	    	if (aChild instanceof Composite) {
                return getRadioGroupSelection((Composite) aChild);
            }
	    	else
	    	if (aChild instanceof Button &&
	    			Util2.checkFlag(((Button) aChild).getStyle(), SWT.RADIO) &&
	    			((Button) aChild).getSelection())
	    		return (Button) aChild;
	    
		return null;
	}

	public static void setRadioGroupSelection(Composite container, String selCaption) {
		
		if (container == null) return;
		
	    for (Control aChild : container.getChildren())
	    	if (aChild instanceof Composite) {
                setRadioGroupSelection((Composite) aChild, selCaption);
            }
	    	else
	    	if (aChild instanceof Button &&	Util2.checkFlag(((Button) aChild).getStyle(), SWT.RADIO))
	    		((Button) aChild).setSelection(((Button) aChild).getText().equalsIgnoreCase(selCaption));
	}

// Table	
	public static String [] getText(TableItem item) {
		
		if (item == null) return null;
		
		Table table = item.getParent();
		if (table == null) return null;
		
		int numCol = table.getColumnCount();
		
		String res [] = new String [numCol];
		
		for (int i = 0; i < numCol; i++)
			res[i] = item.getText(i);
		
		return res;
	}
	
	public static boolean checkRowIndex(Table table, int rowIndex) {
		
		if (table == null) return false;
		
		return rowIndex >= 0 && rowIndex < table.getItemCount();
	}
	
	public static boolean checkColumnIndex(Table table, int colIndex) {
		
		if (table == null) return false;
		
		return colIndex >= 0 && colIndex < table.getColumnCount();
	}
	
	public static int getColumnMaxValue(Table table, int colIndex) {
				
		if (table == null) return 0;
		if (!checkColumnIndex(table, colIndex)) return 0;
		
		int res = 0;
		
		for (TableItem item : table.getItems()) {
			
			String st = item.getText(colIndex);
			int val = Util2.strToInt(st, 0); 
			if (val > res) res = val; 
		}
		
		return res;
	}
		
}
