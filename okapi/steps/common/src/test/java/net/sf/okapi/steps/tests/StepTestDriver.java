/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.tests;

import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipelinedriver.PipelineDriver;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;

/**
 * Generic driver class to simplify executing steps based on filter events.
 */
public class StepTestDriver {

	private PipelineDriver driver;
	private RawDocumentToFilterEventsStep filterStep;
	private CaptureStep captureStep;
	private FilterConfigurationMapper fcMapper;
	private RawDocument rawDoc;
	
	public StepTestDriver () {
		fcMapper = new FilterConfigurationMapper();
		fcMapper.addConfigurations("net.sf.okapi.common.filters.DummyFilter");
		driver = new PipelineDriver();
		driver.setFilterConfigurationMapper(fcMapper);
		captureStep = new CaptureStep();
		filterStep = new RawDocumentToFilterEventsStep();
	}

	/**
	 * Prepares the data to process.
	 * @param srcText the source text to process
	 * @param trgText the optional target text to process (can be null)
	 * @param srcLoc the source locale.
	 * @param trgLoc the target locale.
	 */
	public void prepareFilterEventsStep (String srcText,
		String trgText,
		LocaleId srcLoc,
		LocaleId trgLoc)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(srcText);
		if ( trgText != null ) {
			sb.append("\n");
			sb.append(trgText);
		}
		rawDoc = new RawDocument(sb.toString(), srcLoc, trgLoc);
		rawDoc.setFilterConfigId("okf_dummy");
	}
	
	/**
	 * Gets the last text unit after the process is done.
	 * @return the last text unit processed.
	 */
	public ITextUnit getResult () {
		return captureStep.getLastTextUnit();
	}

	/**
	 * Executes a simple pipeline to test a filter-events based step.
	 * @param step the step to test.
	 */
	public void testFilterEventsStep (IPipelineStep step) {
		driver.clearItems();
		driver.getPipeline().getSteps().clear();
		driver.addStep(filterStep);
		driver.addStep(step);
		captureStep.reset(); // Reset result
		driver.addStep(captureStep);
		driver.addBatchItem(rawDoc, null, null);
		driver.processBatch();
	}

}
