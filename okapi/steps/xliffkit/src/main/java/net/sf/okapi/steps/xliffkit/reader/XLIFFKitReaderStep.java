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

package net.sf.okapi.steps.xliffkit.reader;

import java.io.File;
import java.net.URI;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.steps.xliffkit.opc.OPCPackageReader;

@UsingParameters()
public class XLIFFKitReaderStep extends BasePipelineStep {

	private IFilter reader = new OPCPackageReader();
	private boolean isDone = true;
	private String outputPath;
	private boolean writeTargets = false;
	private LocaleId targetLocale; 
	private IFilterWriter filterWriter;
	private String outputEncoding;
	
	public String getDescription () {
		return "Reads XLIFF translation kit. Expects: Raw document for T-kit. Sends back: filter events.";
	}

	public String getName () {
		return "XLIFF Kit Reader";
	}

	@StepParameterMapping(parameterType = StepParameterType.OUTPUT_URI)
	public void setOutputURI (URI outputURI) {
		this.outputPath = outputURI.getPath();
		writeTargets = !Util.isEmpty(outputPath);
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.OUTPUT_ENCODING)
	public void setOutputEncoding (String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}
	
	@Override
	public Event handleEvent(Event event) {
		switch (event.getEventType()) {
		case START_BATCH:
			isDone = true;
			break;

		case START_BATCH_ITEM:
			isDone = false;
			return event;

		case RAW_DOCUMENT:
			isDone = false;
			reader.open((RawDocument)event.getResource());
			return reader.next();
		}

		if (isDone) {
			return event;
		} else {
			
			if (writeTargets) {
				switch (event.getEventType()) {
				case START_DOCUMENT:
					processStartDocument(event);
					break;

				case END_DOCUMENT:
					processEndDocument(event);
					break;
					
				case START_SUBDOCUMENT:
				case START_GROUP:
				case END_SUBDOCUMENT:
				case END_GROUP:
				case DOCUMENT_PART:
				case TEXT_UNIT:
					filterWriter.handleEvent(event);
				}
			}			
			Event e = reader.next();			
			isDone = !reader.hasNext();
			return e;
		}
	}
	
	@Override
	public boolean isDone() {
		return isDone;
	}

	@Override
	public void destroy() {
		reader.close();
	}

	@Override
	public void cancel() {
		reader.cancel();
	}
	
	private void processStartDocument (Event event) {
		StartDocument startDoc = (StartDocument)event.getResource();
		if ( outputEncoding == null ) outputEncoding = startDoc.getEncoding();
		
		filterWriter = startDoc.getFilterWriter();
		filterWriter.setOptions(targetLocale, outputEncoding);
		
		String srcName = startDoc.getName();
		String outFileName = outputPath + srcName;
		
		File outputFile = new File(outFileName);
		Util.createDirectories(outputFile.getAbsolutePath());
		
		filterWriter.setOutput(outputFile.getAbsolutePath());
		filterWriter.handleEvent(event);
	}
	
	private void processEndDocument (Event event) {
		filterWriter.handleEvent(event);
		filterWriter.close();
	}
}
