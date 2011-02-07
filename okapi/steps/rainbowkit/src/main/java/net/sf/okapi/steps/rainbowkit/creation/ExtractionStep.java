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

package net.sf.okapi.steps.rainbowkit.creation;

import java.io.File;
import java.net.URI;
import java.util.UUID;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.steps.rainbowkit.common.IPackageWriter;

@UsingParameters(Parameters.class)
public class ExtractionStep extends BasePipelineStep {

	private IPackageWriter writer;
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

	public ExtractionStep () {
		super();
		params = new Parameters();
	}

	@Override
	public String getDescription () {
		return "Generates a Rainbow translation kit for a batch of input documents."
			+" Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName () {
		return "Translation Kit Creation (EXPERIMENTAL)";
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
			return handleStartBatch(event);
		case END_BATCH:
			return handleEndBatch(event);
		case START_DOCUMENT:
			return handleStartDocument(event);
		default:
			return writer.handleEvent(event);
		}
	}

	@Override
	protected Event handleStartBatch (Event event) {
		try {
			// Get the format (class name)
			String writerClass = params.getWriterClass();
			writer = (IPackageWriter)Class.forName(writerClass).newInstance();
			writer.setParameters(params);

//TODO: input/output roots
inputRoot = rootDir;
outputRoot = rootDir;

			String resolvedOutputDir = params.getPackageDirectory() + File.separator + params.getPackageName();
			resolvedOutputDir = Util.fillRootDirectoryVariable(resolvedOutputDir, rootDir);
			resolvedOutputDir = LocaleId.replaceVariables(resolvedOutputDir, srcLoc, trgLoc);
			Util.deleteDirectory(resolvedOutputDir, false);
			
			String packageId = UUID.randomUUID().toString();
			String projectId = Util.makeId(params.getPackageName()+srcLoc.toString()+trgLoc.toString());

			writer.setBatchInformation(resolvedOutputDir, srcLoc, trgLoc, inputRoot, packageId, projectId);
		}
		catch ( InstantiationException e ) {
			throw new RuntimeException("Error creating writer class.", e);
		}
		catch ( IllegalAccessException e ) {
			throw new RuntimeException("Error creating writer class.", e);
		}
		catch ( ClassNotFoundException e ) {
			throw new RuntimeException("Error creating writer class.", e);
		}
		
		return writer.handleEvent(event);
	}

	@Override
	protected Event handleEndBatch (Event event) {
		event = writer.handleEvent(event);
		writer.close();
		writer = null;
		return event;
	}
	
	@Override
	protected Event handleStartDocument (Event event) {
		StartDocument sd = event.getStartDocument();
		String tmpIn = inputURI.getPath();
		String relativeInput = tmpIn.substring(inputRoot.length()+1);
		String tmpOut = outputURI.getPath();
		String relativeOutput = tmpOut.substring(outputRoot.length()+1);
		
		IParameters prm = sd.getFilterParameters();
		String paramsData = null;
		if ( prm != null ) {
			paramsData = prm.toString();
		}

		writer.setDocumentInformation(relativeInput, filterConfigId, paramsData, sd.getEncoding(),
			relativeOutput, outputEncoding, sd.getFilterWriter().getSkeletonWriter());
		return writer.handleEvent(event);
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}
	
	@Override
	public IParameters getParameters () {
		return params;
	}

}
