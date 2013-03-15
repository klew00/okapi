/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.wordcount.categorized.gmx;

import net.sf.okapi.common.Event;
import net.sf.okapi.steps.wordcount.categorized.CategoryGroup;
import net.sf.okapi.steps.wordcount.categorized.CategoryHandler;
import net.sf.okapi.steps.wordcount.common.GMX;
import net.sf.okapi.steps.wordcount.common.TokenCountStep;

public class GMXAlphanumericOnlyTextUnitWordCountStep extends TokenCountStep implements CategoryHandler {

	public static final String METRIC = GMX.AlphanumericOnlyTextUnitWordCount;

	@Override
	protected String[] getTokenNames() {
		return new String[] {"ABBREVIATION", "E-MAIL", "INTERNET", "COMPANY", "EMOTICON", "MARKUP"};
	}

	@Override
	public String getName() {
		return "GMX Alphanumeric Only Word Count";
	}

	@Override
	public String getDescription() {
		return "An accumulation of the word count for text units that have been identified as containing only alphanumeric words."
		+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getMetric() {
		return METRIC;
	}

	@Override
	protected boolean countOnlyTranslatable() {
		return true;
	}
	
	@Override
	protected Event handleTextUnit(Event event) {
		// TODO Auto-generated method stub
		return super.handleTextUnit(event);
	}

	@Override
	public CategoryGroup getCategoryGroup() {
		return CategoryGroup.GMX_WORD_COUNTS;
	}

}
