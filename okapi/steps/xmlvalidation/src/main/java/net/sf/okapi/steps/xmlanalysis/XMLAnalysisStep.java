/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xmlanalysis;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.pipeline.BasePipelineStep;

@UsingParameters(Parameters.class)
public class XMLAnalysisStep extends BasePipelineStep {

	private XMLAnalyzer xmlan;

	public XMLAnalysisStep () {
		xmlan = new XMLAnalyzer();
	}
	
	@Override
	public String getDescription () {
		return "Generate an analysis report of a set of XML documents."
			+ " Expects: raw document. Sends back: raw document.";
	}

	@Override
	public String getName () {
		return "XML Analysis";
	}

	@Override
	public IParameters getParameters () {
		return xmlan.getParameters();
	}

	@Override
	public void setParameters (IParameters params) {
		xmlan.setParameters((Parameters)params);
	}
 
	@Override
	protected Event handleStartBatch (Event event) {
		xmlan.reset();
		return event;
	}
	
	@Override
	protected Event handleEndBatch (Event event) {
		// Generate the report
		xmlan.generateOutput();
		return event;
	}

	@Override
	protected Event handleRawDocument (Event event) {
		xmlan.analyzeDocument(event.getRawDocument());
		return event;
	}

}
