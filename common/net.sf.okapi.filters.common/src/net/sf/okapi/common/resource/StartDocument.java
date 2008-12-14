/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.common.resource;

import net.sf.okapi.common.IParameters;

public class StartDocument extends BaseNameable {

	protected String language;
	protected String encoding;
	protected boolean isMultilingual;
	protected IParameters params;
	
	public String getLanguage () {
		return language;
	}
	
	public void setLanguage (String language) {
		this.language = language;
	}

	public String getEncoding () {
		return encoding;
	}
	
	public void setEncoding (String encoding) {
		this.encoding = encoding;
	}
	
	public boolean isMultilingual () {
		return isMultilingual;
	}
	
	public void setIsMultilingual (boolean value) {
		isMultilingual = value;
	}

	public IParameters getFilterParameters () {
		return params;
	}
	
	public void setFilterParameters (IParameters params) {
		this.params = params;
	}

}
