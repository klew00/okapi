/*===========================================================================
  Copyright (C) 2013 by the Okapi Framework contributors
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

package net.sf.okapi.common;

/**
 * A class to encapsulate information about execution details such as
 * the name of the application, the current UI parent, etc.
 */
public class ExecutionContext extends BaseContext {

	private static String IS_NO_PROMPT = "isNoPrompt";
	private static String UI_PARENT = "uiParent";
	private static String APPLICATION_NAME = "applicationName";

	public boolean getIsGui() {
		return getUiParent() != null;
	}

	public void setIsNoPrompt(boolean bool) {
		setBoolean(IS_NO_PROMPT, bool);
	}

	public boolean getIsNoPrompt() {
		return getBoolean(IS_NO_PROMPT);
	}

	public void setUiParent(Object uiParent) {
		setObject(UI_PARENT, uiParent);
	}

	public Object getUiParent() {
		return getObject(UI_PARENT);
	}

	public void setApplicationName(String name) {
		setString(APPLICATION_NAME, name);
	}

	public String getApplicationName() {
		return getString(APPLICATION_NAME);
	}
}
