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

package net.sf.okapi.steps.wordcount;

import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.steps.wordcount.common.GMX;
import net.sf.okapi.steps.wordcount.common.Parameters;
import net.sf.okapi.steps.wordcount.common.TokenCountStep;

/**
 * Word Counter pipeline step. The counter counts a number of words in translatable text units. 
 * The count results are placed in a MetricsAnnotation structure (with the GMX TotalWordCount 
 * metric set), attached to the respective event's resource (TEXT_UNIT, END_DOCUMENT, END_BATCH, 
 * END_BATCH_ITEM, END_SUBDOCUMENT, END_GROUP).  
 * 
 * @version 0.1 06.07.2009
 */
@UsingParameters(Parameters.class)
public class WordCountStep extends TokenCountStep {
	
	public static final String METRIC = GMX.TotalWordCount; 
	
	@Override
	public String getMetric() {
		return METRIC;
	}

	@Override
	protected String[] getTokenNames() {
		return new String[] {WordCounter.getTokenName()};
	}

	@Override
	protected boolean countOnlyTranslatable() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Count the number of words in the text units of a set of documents or/and in its parts."
		+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName() {
		return "Word Count";
	}
	
}
