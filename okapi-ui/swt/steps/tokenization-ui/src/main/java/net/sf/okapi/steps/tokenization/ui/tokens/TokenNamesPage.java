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

package net.sf.okapi.steps.tokenization.ui.tokens;

import net.sf.okapi.common.ui.abstracteditor.AbstractListAddRemoveTab;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

import org.eclipse.swt.widgets.Composite;

public class TokenNamesPage extends AbstractListAddRemoveTab {

	public TokenNamesPage(Composite parent, int style) {
		
		super(parent, style);
	}

	@Override
	protected void actionAdd(int afterIndex) {
		
		String[] res = TokenSelector.select(getShell(), TokenSelectorPePage.class, "WORD,NUMBER");
		
		for (int i = 0; i < res.length; i++) {
			
			if (list.indexOf(res[i]) == -1)
				list.add(res[i]);
		}
	}

	@Override
	protected String getItemDescription(int index) {

		return Tokens.getTokenDescription(list.getItem(index));
	}

	@Override
	protected boolean getDisplayListDescr() {

		return false;
	}

	public boolean canClose(boolean isOK) {

		return true;
	}

	public boolean load(Object data) {

//		if (!(data instanceof LanguageAndTokenParameters)) return false;
//		
//		LanguageAndTokenParameters params = (LanguageAndTokenParameters) data;		
//		if (params.getTokenMode() == LanguageAndTokenParameters.TOKENS_ALL) return false;		
//		
//		list.setItems(ListUtil.listAsArray(params.getTokenNames()));		
//		selectListItem(0);			
		
		return true;
	}

	public boolean save(Object data) {
		
//		if (!(data instanceof LanguageAndTokenParameters)) return false;
//		
//		LanguageAndTokenParameters params = (LanguageAndTokenParameters) data;
//		if (params.getTokenMode() == LanguageAndTokenParameters.TOKENS_ALL) return false;
//		
//		params.setTokenNames(ListUtil.arrayAsList(list.getItems()));
		
		return true;
	}

}
