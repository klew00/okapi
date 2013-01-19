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

package net.sf.okapi.steps.tokenization.ui.locale;

import net.sf.okapi.common.ui.abstracteditor.AbstractListAddRemoveTab;
import net.sf.okapi.steps.tokenization.common.LanguageAndTokenParameters;
import net.sf.okapi.steps.tokenization.locale.LanguageList;

import org.eclipse.swt.widgets.Composite;

public class LanguagesPage extends AbstractListAddRemoveTab {

	public LanguagesPage(Composite parent, int style) {
		
		super(parent, style);
	}

	@Override
	protected boolean getDisplayListDescr() {

		return false;
	}

	@Override
	protected void actionAdd(int afterIndex) {
		
		String[] res = LanguageSelector.select(getShell(), LanguageSelectorPePage.class, "EN-US,RU-RU");
		
		for (int i = 0; i < res.length; i++) {
			
			if (list.indexOf(res[i]) == -1)
				list.add(res[i]);
		}
	}

	public boolean canClose(boolean isOK) {

		return true;
	}

	public boolean load(Object data) {
		
		if (!(data instanceof LanguageAndTokenParameters)) return false;
		
//		LanguageAndTokenParameters params = (LanguageAndTokenParameters) data;
//		
//		if (params.getLanguageMode() == LanguageAndTokenParameters.LANGUAGES_ALL) return false;
//		
//		if (params.getLanguageMode() == LanguageAndTokenParameters.LANGUAGES_ONLY_WHITE_LIST)
//			list.setItems(ListUtil.listAsArray(params.getLanguageWhiteList()));
//		
//		if (params.getLanguageMode() == LanguageAndTokenParameters.LANGUAGES_ALL_EXCEPT_BLACK_LIST)
//			list.setItems(ListUtil.listAsArray(params.getLanguageBlackList()));
//			
//		selectListItem(0);			
		
		return true;
	}

	public boolean save(Object data) {
		
		if (!(data instanceof LanguageAndTokenParameters)) return false;
		
//		LanguageAndTokenParameters params = (LanguageAndTokenParameters) data;
//		if (params.getLanguageMode() == LanguageAndTokenParameters.LANGUAGES_ALL) return false;
//		
//		if (params.getLanguageMode() == LanguageAndTokenParameters.LANGUAGES_ONLY_WHITE_LIST)			
//			params.setLanguageWhiteList(ListUtil.arrayAsList(list.getItems()));
//		
//		if (params.getLanguageMode() == LanguageAndTokenParameters.LANGUAGES_ALL_EXCEPT_BLACK_LIST)
//			params.setLanguageBlackList(ListUtil.arrayAsList(list.getItems()));
		
		return true;
	}

	@Override
	protected String getItemDescription(int index) {
		return LanguageList.getDisplayName(list.getItem(index));
	}
	
}
