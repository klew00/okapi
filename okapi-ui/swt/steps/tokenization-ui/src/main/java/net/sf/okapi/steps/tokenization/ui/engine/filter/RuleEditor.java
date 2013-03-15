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

package net.sf.okapi.steps.tokenization.ui.engine.filter;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.steps.tokenization.ui.engine.AbstractRuleEditor;

public class RuleEditor extends AbstractRuleEditor {

	@Override
	protected Class<? extends Composite> getRuleClass() {
		
		return RuleTab.class;
	}

	@Override
	public IParameters createParameters() {
		
		return null;
	}

	@Override
	protected String getCaption() {
		
		return "Token recognizer rule";
	}

	@Override
	protected void interop(Widget speaker) {
		
		
	}

}
