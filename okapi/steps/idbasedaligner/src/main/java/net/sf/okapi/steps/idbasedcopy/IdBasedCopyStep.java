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

package net.sf.okapi.steps.idbasedcopy;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.ITextUnit;

/**
 * This step copies into a destination file (first input file) the text of a
 * reference file (second input file) for text units that have the same id. 
 * The ids are taken from the name (TextUnit.getName()) of each text unit.
 */
@UsingParameters(Parameters.class)
public class IdBasedCopyStep extends BasePipelineStep {

	private final Logger logger = Logger.getLogger(getClass().getName());

	private Parameters params;
	private IFilter filter = null;
	private IFilterConfigurationMapper fcMapper;
	private LocaleId targetLocale;
	private RawDocument toCopyInput = null;
	private Map<String, ITextUnit> toCopy;
	private boolean useTargetText;

	public IdBasedCopyStep () {
		params = new Parameters();
	}

	@StepParameterMapping(parameterType = StepParameterType.FILTER_CONFIGURATION_MAPPER)
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}

	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}

	@StepParameterMapping(parameterType = StepParameterType.SECOND_INPUT_RAWDOC)
	public void setSecondInput (RawDocument secondInput) {
		this.toCopyInput = secondInput;
	}

	@Override
	public String getDescription () {
		return "Copies the source text of the second input into the target of the first input based on matching id."
			+ "\nExpects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName () {
		return "Id-Based Copy";
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
	protected Event handleStartDocument (Event event) {
		// Use target text if reference file is bilingual, otherwise use the source
		useTargetText = event.getStartDocument().isMultilingual();
		// Create the table if possible
		if ( toCopyInput == null ) {
			logger.warning("Second input file is not specified.");
			toCopy = null;
		}
		else {
			// Else: read the file
			readEntriesToCopy();
		}
		return event;
	}
	
	@Override
	protected Event handleEndDocument (Event event) {
		// Warn if we have any entries that were in the delat file, but not the input
		if ( toCopy != null ) {
			if ( toCopy.size() > 0 ) {
				for ( String id : toCopy.keySet() ) {
					logger.warning(String.format("Id '%s' is in the second file, but not in the main input.", id));
				}
			}
		}
		return event;
	}

	@Override
	protected Event handleTextUnit (Event event) {
		// No file to copy from
		if ( toCopy == null ) {
			return event;
		}
		
		ITextUnit tu = event.getTextUnit();
		// Skip non-translatable and empty
		if ( !tu.isTranslatable() ) {
			return event; // No change
		}

		// Find the matching id in the entries to copy
		ITextUnit toCopyTu = toCopy.get(tu.getName());
		TextContainer tc;
		if ( toCopyTu != null ) {
			if ( useTargetText ) tc = toCopyTu.getTarget(targetLocale);
			else tc = toCopyTu.getSource();
			if ( tc != null ) {
				tu.setTarget(targetLocale, tc);
				toCopy.remove(tu.getName());
				if ( params.getMarkAsTranslateNo() ) {
					tu.setIsTranslatable(false);
				}
				if ( params.getMarkAsApproved() ) {
					tu.setTargetProperty(targetLocale, new Property(Property.APPROVED, "yes"));
				}
			}
		}

		return event;
	}

	private void readEntriesToCopy () {
		toCopy = new HashMap<String, ITextUnit>();
		try {
			// Initialize the filter to read the translation to compare
			filter = fcMapper.createFilter(toCopyInput.getFilterConfigId(), null);
			// Open the second input for this batch item
			filter.open(toCopyInput);

			while ( filter.hasNext() ) {
				final Event event = filter.next();
				if ( event.getEventType() == EventType.TEXT_UNIT ) {
					ITextUnit tu = event.getTextUnit();
					String id = tu.getName();
					if ( Util.isEmpty(id) ) {
						logger.warning("Entry without id detected in second file.");
						continue;
					}
					// Else: put in the hash table
					if ( toCopy.get(id) != null ) {
						logger.warning("Duplicate id detected: "+id);
						continue;
					}
					toCopy.put(id, tu);
				}
			}
		}
		finally {
			if ( filter != null ) {
				filter.close();
			}
		}
	}
}
