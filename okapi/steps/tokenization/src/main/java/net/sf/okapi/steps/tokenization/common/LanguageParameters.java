/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.steps.tokenization.common;

import net.sf.okapi.common.LocaleFilter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.ParametersString;
import net.sf.okapi.lib.extra.AbstractParameters;

public class LanguageParameters extends AbstractParameters {

	private LocaleFilter localeFilter;
		
	@Override
	protected void parameters_init() {
		
		localeFilter = new LocaleFilter();
	}
	
	@Override
	protected void parameters_load(ParametersString buffer) {
		
		if (localeFilter == null) return;		
		localeFilter.fromString(buffer.getString("languages"));
	}

	@Override
	protected void parameters_reset() {
		
		if (localeFilter == null) return;
		localeFilter.reset();
	}

	@Override
	protected void parameters_save(ParametersString buffer) {
		
		if (localeFilter == null) return;
		buffer.setString("languages", localeFilter.toString());
	}

	public boolean supportsLanguage(LocaleId language) {
		
		if (localeFilter == null) return false;		
		return localeFilter.matches(language);
	}

	public LocaleFilter getLocaleFilter() {
		
		return localeFilter;
	}
	
	public String getLanguages() {
		
		if (localeFilter == null) return "";
		return localeFilter.toString();
	}

	public void setLocaleFilter(LocaleFilter localeFilter) {
		
		this.localeFilter = localeFilter;
	}		
	
	public void setLocaleFilter(String string) {
		
		if (localeFilter == null) return;
		this.localeFilter.fromString(string);
	}
}
