/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.wordcount.categorized.okapi;

import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.steps.wordcount.categorized.CategoryGroup;
import net.sf.okapi.steps.wordcount.categorized.CategoryHandler;
import net.sf.okapi.steps.wordcount.common.AltAnnotationBasedCountStep;

public class ExactDocumentContextMatchWordCountStep extends AltAnnotationBasedCountStep implements CategoryHandler {
	
	public static final String METRIC = MatchType.EXACT_DOCUMENT_CONTEXT.name(); 

	@Override
	public String getMetric() {
		return METRIC;
	}

	@Override
	public String getDescription() {
		return "Matches EXACT and comes from a repeated segment in the same document or a different version of the same document. " +
				"See also EXACT_PREVIOUS_VERSION"
		+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName() {
		return "Exact Document Context Match Word Count";
	}

	@Override
	protected boolean accept(MatchType type) {
		return type == MatchType.EXACT_DOCUMENT_CONTEXT;
	}

	@Override
	public CategoryGroup getCategoryGroup() {
		return CategoryGroup.OKAPI_WORD_COUNTS;
	}
}
