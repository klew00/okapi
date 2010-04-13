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

package net.sf.okapi.steps.simplekit.writer;

import java.io.File;
import java.net.URI;
import java.util.UUID;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.StartDocument;

@UsingParameters(Parameters.class)
public class SimpleKitWriterStep extends BasePipelineStep {

	private SimpleKitWriter writer;
	private Parameters params;
	private LocaleId srcLoc;
	private LocaleId trgLoc;
	private URI inputURI;
	private URI outputURI;
	private String outputEncoding;
	private String filterConfigId;
	private String rootDir;
	private String inputRoot;
	private String outputRoot;
	private int docId;

	public SimpleKitWriterStep () {
		super();
		params = new Parameters();
	}

	@Override
	public String getDescription () {
		return "Generate a Generic XLIFF translation package. Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName () {
		return "Generic XLIFF Package Creation";
	}

	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourceLocale (LocaleId sourceLocale) {
		this.srcLoc = sourceLocale;
	}

	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.trgLoc = targetLocale;
	}

	@StepParameterMapping(parameterType = StepParameterType.INPUT_URI)
	public void setInputURI (URI inputURI) {
		this.inputURI = inputURI;
	}

	@StepParameterMapping(parameterType = StepParameterType.OUTPUT_URI)
	public void setOutputURI (URI outputURI) {
		this.outputURI = outputURI;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.OUTPUT_ENCODING)
	public void setOutputEncoding (String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.FILTER_CONFIGURATION_ID)
	public void setFilterConfigurationId (String filterConfigId) {
		this.filterConfigId = filterConfigId;
	}

	@StepParameterMapping(parameterType = StepParameterType.ROOT_DIRECTORY)
	public void setRootDirectory (String rootDir) {
		this.rootDir = rootDir;
	}

	@Override
	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_BATCH:
			handleStartBatch(event);
			break;
		case END_BATCH:
			writer.writeEndPackage(false);
			break;
		case START_DOCUMENT:
			handleStartDocument(event);
			break;
		}
		// All events then go to the actual writer
		return writer.handleEvent(event);
	}

	
	@Override
	protected Event handleStartBatch (Event event) {
		writer = new SimpleKitWriter();
		writer.setParameters(params);
		docId = 0;
//TODO: input/output roots
inputRoot = rootDir;
outputRoot = rootDir;

		String resolvedOutputDir = params.getPackageDirectory() + File.separator + params.getPackageName();
		resolvedOutputDir = Util.fillRootDirectoryVariable(resolvedOutputDir, rootDir);
		Util.deleteDirectory(resolvedOutputDir, false);
		
		String pkgId = UUID.randomUUID().toString();
		// Use the hash code of the input root for project ID, just to have one
		writer.setInformation(srcLoc, trgLoc, Util.makeId(inputRoot),
			resolvedOutputDir, pkgId, inputRoot, getClass().getName());
		writer.writeStartPackage();
		return event;
	}
	
	@Override
	protected Event handleStartDocument (Event event) {
		StartDocument sd = (StartDocument)event.getResource();
		String tmpIn = Util.makePathFromURI(inputURI);
		String relativeInput = tmpIn.substring(inputRoot.length()+1);
		String tmpOut = Util.makePathFromURI(outputURI);
		String relativeOutput = tmpOut.substring(outputRoot.length()+1);
		String res[] = FilterConfigurationMapper.splitFilterFromConfiguration(filterConfigId);
		
		writer.createOutput(++docId, relativeInput, relativeOutput,
			sd.getEncoding(), outputEncoding, res[0], sd.getFilterParameters(),
			sd.getFilterWriter().getEncoderManager());
		
		return event;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}
	
	@Override
	public IParameters getParameters() {
		return params;
	}

}
