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

package net.sf.okapi.steps.tokenization.ui.tokens;

import java.util.List;

import net.sf.okapi.common.ListUtil;
import net.sf.okapi.common.ui.abstracteditor.InputQueryDialog;

import org.eclipse.swt.widgets.Shell;

public class TokenSelector {

	/**
	 * For creation of new tokens and storing them to the globally accessible net.sf.okapi.steps.tokenization.tokens/tokens.tprm
	 */
	public static void main(String[] args) {
		
		select();
	}

	public static String[] select() {
		
		return select(null, TokenSelectorTsPage.class, null); 
	}

	/**
	 * 
	 * @param parent
	 * @param classRef
	 * @param initialData a list of comma-separated token names
	 * @return
	 */
	public static String[] select(Shell parent, Class<? extends TokenSelectorPage> classRef, String initialData) {
				
		InputQueryDialog dlg = new InputQueryDialog();
		List<String> list = ListUtil.stringAsList(initialData);
				
		dlg.run(parent, classRef, "Tokens", "", list, null);
				
		return list.toArray(new String[] {});
	}
}
