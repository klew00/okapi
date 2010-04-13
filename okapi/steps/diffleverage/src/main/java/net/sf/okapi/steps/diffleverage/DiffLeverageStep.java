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

package net.sf.okapi.steps.diffleverage;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.extra.diff.incava.DiffLists;

/**
 * Contextually match source segments between two documents using a standard diff algorithm
 * (http://en.wikipedia.org/wiki/Diff). The result is a new document with the translations 
 * from the first document merged into it. This allows translations between different document 
 * versions to be preserved while still maintaining the newer source document modifications.      
 * 
 * @author HARGRAVEJE
 * 
 */
public class DiffLeverageStep extends BasePipelineStep {
	private static final Logger LOGGER = Logger.getLogger(DiffLeverageStep.class.getName());
	
	private IFilter filter;
	private IFilterConfigurationMapper fcMapper;
	private LocaleId targetLocale;
	private LocaleId sourceLocale;
	private RawDocument targetInput;
	private DiffLists<TextUnit> textUnitDiffer;
	private List<TextUnit> oldTextUnits;
	private List<TextUnit> newTextUnits;
	private List<Event> newDocumentEvents;
	
	/**
	 * 
	 * @param fcMapper
	 */
	@StepParameterMapping(parameterType = StepParameterType.FILTER_CONFIGURATION_MAPPER)
	public void setFilterConfigurationMapper(IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}

	/**
	 * 
	 * @param sourceLocale
	 */
	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourceLocale(LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}

	/**
	 * 
	 * @param targetLocale
	 */
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale(LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}

	/**
	 * This is the old document (previously translated)
	 * @param secondInput
	 */
	@StepParameterMapping(parameterType = StepParameterType.SECOND_INPUT_RAWDOC)
	public void setSecondInput(RawDocument secondInput) {
		this.targetInput = secondInput;
	}

	@Override
	public String getDescription() {
		return "Diff (i.e. compare) two bi-lingual documents."
				+ "Copy the old target segments into the new document's "
				+ "TextUnits based on contextual matching of the source segments";
	}

	@Override
	public String getName() {
		return "Diff Leverage Step";
	}
	
	/**
	 * Handle {@link Event}s of the new document (not translated).
	 */
	@Override
	public Event handleEvent(Event event) {
		switch (event.getEventType()) {		
		case RAW_DOCUMENT:
			LOGGER.log(Level.SEVERE, "Encountered a RAW_DOCUMENT event. Expected a filtered event stream.");
			newDocumentEvents.add(event);
			break;
		case CUSTOM:
			LOGGER.log(Level.WARNING, "Encountered a CUSTOM event. We don't know what to do with it so we pass it on.");
			newDocumentEvents.add(event);
			break;
		case MULTI_EVENT:
			LOGGER.log(Level.WARNING, "Encountered a MULTI_EVENT event. We don't know what to do with it so we pass it on.");
			newDocumentEvents.add(event);
			break;
		case TEXT_UNIT:
			newTextUnits.add(event.getTextUnit());
			newDocumentEvents.add(event);
			break;
		default:				
			newDocumentEvents.add(event);
			break;
		}
		
		return Event.NOOP_EVENT;
	}
	
	private void initializeFilter() {
		// Initialize the filter to read the translation to compare
		filter = fcMapper.createFilter(targetInput.getFilterConfigId(), null);
		// Open the second input for this batch item
		filter.open(targetInput);
	}
}
