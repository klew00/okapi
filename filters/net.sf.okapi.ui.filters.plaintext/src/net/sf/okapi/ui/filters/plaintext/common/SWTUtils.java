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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * 
 * @version 0.1, 13.06.2009
 * @author Sergei Vasilyev
 */

public class SWTUtils {

//	public static final String SET_FOCUS = "SWT_SET_FOCUS";
	
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
	
}

