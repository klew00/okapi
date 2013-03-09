/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.fileanalysis;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.pipeline.BasePipelineStep;

@UsingParameters(Parameters.class)
public class FileAnalysisStep extends BasePipelineStep {

	private Parameters params;

	public FileAnalysisStep () {
		params = new Parameters();
	}

	public String getDescription () {
		return "Analyzes a file properties such as BOM, line-break, encoding, etc."
			+ " Expects: raw document. Sends back: raw document.";
	}

	public String getName () {
		return "File Analysis";
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	@Override
	protected Event handleStartBatch (Event event) {
		//TODO: any task before the first file of the batch
		return event;
	}
	
	@Override
	protected Event handleEndBatch (Event event) {
		//TODO: any tasks at the end of the batch
		return event;
	}
	
	@Override
	protected Event handleRawDocument (Event event) {
//		RawDocument rawDoc = event.getRawDocument();
		//TODO: analyze the file
		return event;
	}

}
