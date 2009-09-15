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

package net.sf.okapi.steps.formatconversion;

import java.io.File;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipelinedriver.PipelineContext;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.filters.po.POFilterWriter;

public class FormatConversionStep extends BasePipelineStep {

	private static final int PO_OUTPUT = 0;
	
	private Parameters params;
	private IFilterWriter writer;
	private boolean firstOutputCreated;
	private int outputType;

	public FormatConversionStep () {
		params = new Parameters();
	}

	public String getDescription () {
		return "Converts the output of a filter into a specified file format.";
	}

	public String getName () {
		return "Format Conversion";
	}

	@Override
	public PipelineContext getContext() {		
		return (PipelineContext)super.getContext();
	}

	@Override
	public boolean needsOutput (int inputIndex) {
		return isLastStep();
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters) params;
	}

	@Override
	public Parameters getParameters () {
		return params;
	}
	
	public Event handleEvent (Event event) {
		switch (event.getEventType()) {
		case START_BATCH:
			firstOutputCreated = false;
			if ( params.getOutputFormat().equals(Parameters.FORMAT_PO) ) {
				outputType = PO_OUTPUT;
			}
			break;
		case END_BATCH:
			if ( params.isSingleOutput() ) {
				Ending ending = new Ending("end");
				writer.handleEvent(new Event(EventType.END_DOCUMENT, ending));
			}
			break;
		case START_DOCUMENT:
			if ( !firstOutputCreated || !params.isSingleOutput() ) {
				switch ( outputType ) {
				case PO_OUTPUT:
					startPOOutput();
				}
			}
			writer.handleEvent(event);
			break;
		case END_DOCUMENT:
			if ( !params.isSingleOutput() ) {
				writer.handleEvent(event);
			}
			// Else: Do nothing
			break;
		case START_BATCH_ITEM:
		case END_BATCH_ITEM:
		case START_SUBDOCUMENT:
		case END_SUBDOCUMENT:
		case START_GROUP:
		case END_GROUP:
		case TEXT_UNIT:
			if ( writer != null ) {
				writer.handleEvent(event);
			}
			break;
		case RAW_DOCUMENT:
		case DOCUMENT_PART:
		case CUSTOM:
			break;
		}
		return event;
	}

	private void startPOOutput () {
		writer = new POFilterWriter();
		net.sf.okapi.filters.po.Parameters outParams = (net.sf.okapi.filters.po.Parameters)writer.getParameters();
		outParams.outputGeneric = true;
		File outFile;
		if ( isLastStep() ) {
			if ( params.isSingleOutput() ) {
				outFile = new File(params.getOutputPath());
			}
			else {
				outFile = new File(getContext().getOutputURI(0));
			}
			// Not needed, writer does this: Util.createDirectories(outFile.getAbsolutePath());
			writer.setOutput(outFile.getPath());
			writer.setOptions(getContext().getTargetLanguage(0), "UTF-8");
		}
		else {
			try {
				outFile = File.createTempFile("okp-fc_", ".tmp");
			}
			catch ( Throwable e ) {
				throw new OkapiIOException("Cannot create temporary output.", e);
			}
			outFile.deleteOnExit();
		}
		firstOutputCreated = true;
	}
}
