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

package net.sf.okapi.steps.qualitycheck;

import java.io.File;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.lib.verification.Parameters;
import net.sf.okapi.lib.verification.QualityCheckSession;

@UsingParameters(Parameters.class)
public class QualityCheckStep extends BasePipelineStep {

	private static final Logger LOGGER = Logger.getLogger(QualityCheckStep.class.getName());

	private QualityCheckSession session;
	private LocaleId targetLocale;
	private String rootDir;

	public QualityCheckStep () {
		session = new QualityCheckSession();
	}
	
	@Override
	public String getName () {
		return "Quality Check";
	}

	@Override
	public String getDescription () {
		return "Compare source and target for quality. "
			+ "Expects: filter events. Sends back: filter events.";
	}

	@Override
	public IParameters getParameters () {
		return session.getParameters();
	}

	@Override
	public void setParameters (IParameters params) {
		session.setParameters((Parameters)params);
	}

	@StepParameterMapping(parameterType = StepParameterType.ROOT_DIRECTORY)
	public void setRootDirectory (String rootDir) {
		this.rootDir = rootDir;
	}

	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}
	
	@Override
	protected Event handleStartBatch (Event event) {
		session.startProcess(targetLocale, rootDir);
		return event;
	}
	
	@Override
	protected Event handleStartDocument (Event event) {
		session.processStartDocument((StartDocument)event.getResource());
		return event;
	}
	
	@Override
	protected Event handleTextUnit (Event event) {
		session.processTextUnit(event.getTextUnit());
		return event;
	}
	
	@Override
	protected Event handleEndBatch (Event event) {
		session.completeProcess();
		
		String finalPath = Util.fillRootDirectoryVariable(session.getParameters().getOutputPath(), rootDir);
		LOGGER.info("\nOutput: " + finalPath);
		int count = session.getIssues().size();
		if ( count == 0 ) {
			LOGGER.info("No issue found.");
		}
		else {
			LOGGER.warning(String.format("Number of issues found = %d", count));
		}

		if ( session.getParameters().getAutoOpen() ) {
			Util.openURL((new File(finalPath)).getAbsolutePath());
		}
		return event;
	}

}
