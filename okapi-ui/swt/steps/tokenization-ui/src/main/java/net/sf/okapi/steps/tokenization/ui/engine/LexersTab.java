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

package net.sf.okapi.steps.tokenization.ui.engine;

import org.eclipse.swt.widgets.Composite;

import net.sf.okapi.common.ui.abstracteditor.SWTUtil;
import net.sf.okapi.steps.tokenization.ui.common.CompoundStepItemsTab;

public class LexersTab extends CompoundStepItemsTab {

	public LexersTab(Composite parent, int style) {
		
		super(parent, style);
		
		SWTUtil.setText(listDescr, "Listed below are configured lexers in the order of invocation.");
	}

	public boolean canClose(boolean isOK) {
		
		return true;
	}

	public boolean load(Object data) {

		return true;
	}

	public boolean save(Object data) {

		return true;
	}

	@Override
	protected void actionAdd(int afterIndex) {
		// TODO Auto-generated method stub
		
		//System.out.println("add");
	}

	@Override
	protected void actionDown(int itemIndex) {
		// TODO Auto-generated method stub
		
		//System.out.println("down");
	}

	@Override
	protected void actionModify(int itemIndex) {
		// TODO Auto-generated method stub
		
		//System.out.println("modify");
	}

	@Override
	protected void actionUp(int itemIndex) {
		// TODO Auto-generated method stub
	
		//System.out.println("up");
	}

	@Override
	protected String getItemDescription(int index) {
		// TODO Auto-generated method stub
		return null;
	}

}
