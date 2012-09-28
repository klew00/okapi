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

package net.sf.okapi.steps.termextraction;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;

@UsingParameters(Parameters.class)
public class TermExtractionStep extends BasePipelineStep {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private Parameters params;
	private SimpleTermExtractor extractor;
	private LocaleId sourceLocale;
	private String rootDir;

	public TermExtractionStep () {
		params = new Parameters();
		extractor = new SimpleTermExtractor();
	}
	
	@Override
	public String getName () {
		return "Term Extraction";
	}

	@Override
	public String getDescription () {
		return "Extract a list of possible terms found in the source content. "
			+ "Expects: filter events. Sends back: filter events.";
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	@StepParameterMapping(parameterType = StepParameterType.ROOT_DIRECTORY)
	public void setRootDirectory (String rootDir) {
		this.rootDir = rootDir;
	}

	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourcetLocale (LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}
	
	@Override
	protected Event handleStartBatch (Event event) {
		extractor.initialize(params, sourceLocale, rootDir);
		return event;
	}
	
	@Override
	protected Event handleTextUnit (Event event) {
		extractor.processTextUnit(event.getTextUnit());
		return event;
	}
	
	@Override
	protected Event handleEndBatch (Event event) {
		extractor.completeExtraction();

		String finalPath = Util.fillRootDirectoryVariable(params.getOutputPath(), rootDir);
		LOGGER.info("Output: " + finalPath);
		LOGGER.info(String.format("Candidate terms found = %d", extractor.getTerms().size()));

		if ( params.getAutoOpen() ) {
			Util.openURL((new File(finalPath)).getAbsolutePath());
		}
		return event;
	}

}
