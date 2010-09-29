package net.sf.okapi.steps.idaligner;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.TMXWriter;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;

/**
 * Align two {@link TextUnit}s based on matching ids. The ids are taken from the name (TextUnit.getName()) of each
 * {@link TextUnit}. Any {@link IFilter} that produces a name for its {@link TextUnit}s will work with this aligner.
 * Expects filtered {@link Event}s as input and returns a new (aligned) bi-lingual {@link TextUnit} {@link Event}. Optionally
 * produce a TMX file in the specified output path.
 * 
 * @author Greg Perkins
 * @author HargraveJE
 * 
 */
@UsingParameters(Parameters.class)
public class IdBasedAlignerStep extends BasePipelineStep {

	private final Logger logger = Logger.getLogger(getClass().getName());
	private Parameters params;
	private IFilter filter = null;
	private IFilterConfigurationMapper fcMapper;
	private LocaleId sourceLocale;
	private LocaleId targetLocale;
	private RawDocument targetInput = null;
	private TMXWriter tmx;
	private Map<String, TextUnit> targetTextUnitMap;

	public IdBasedAlignerStep() {
		params = new Parameters();
	}

	@StepParameterMapping(parameterType = StepParameterType.FILTER_CONFIGURATION_MAPPER)
	public void setFilterConfigurationMapper(IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}

	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourceLocale(LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}

	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale(LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}

	@StepParameterMapping(parameterType = StepParameterType.SECOND_INPUT_RAWDOC)
	public void setSecondInput(RawDocument secondInput) {
		this.targetInput = secondInput;
	}

	@Override
	public String getDescription() {
		return "Align text units in two id-based files (e.g. Java properties)."
			+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName() {
		return "Id-Based Aligner";
	}

	@Override
	public IParameters getParameters() {
		return params;
	}

	@Override
	public void setParameters(final IParameters params) {
		this.params = (Parameters) params;
	}

	@Override
	protected Event handleStartBatch(final Event event) {
		// Start TMX writer (one for all input documents)
		if (tmx == null && params.getGenerateTMX()) {
			String mimeType = event.getStartDocument().getMimeType();
			tmx = new TMXWriter(params.getTmxOutputPath());
			tmx.writeStartDocument(sourceLocale, targetLocale, getClass().getName(), null,
					"sentence", mimeType, null);
		}

		return event;
	}

	@Override
	protected Event handleEndBatch(final Event event) {
		if (tmx != null) {
			tmx.writeEndDocument();
			tmx.close();
			tmx = null;
		}
		return event;
	}

	@Override
	protected Event handleStartDocument(Event event) {
		if (targetInput != null) {
			targetTextUnitMap = new HashMap<String, TextUnit>();
			getTargetTextUnits();
		}

		return event;
	}

	@Override
	protected Event handleEndDocument(Event event) {
		if (filter != null) {
			filter.close();
		}

		return event;
	}

	@Override
	protected Event handleTextUnit(Event sourceEvent) {
		TextUnit sourceTu = sourceEvent.getTextUnit();
		TextUnit targetTu = null;

		// Skip non-translatable and empty
		if (!sourceTu.isTranslatable() || sourceTu.isEmpty()) {
			return sourceEvent;
		}

		// Populate the target TU
		TextUnit alignedTextUnit = sourceTu.clone();
		if (targetInput != null) {
			// Use the target text, if it exists
			if (targetTextUnitMap.containsKey(sourceTu.getName())) {
				targetTu = targetTextUnitMap.get(sourceTu.getName());
				alignedTextUnit.setTarget(targetLocale, targetTu.getSource());
			} else if (params.getReplaceWithSource()) {
				// Use the source text if there is no target
				logger.warning("String is missing from the target: " + sourceTu.getName());
				alignedTextUnit.setTarget(targetLocale, sourceTu.getSource());
			} else {
				logger.warning("String is missing from the target: " + sourceTu.getName());
			}
		}

		// Send the aligned TU to the TMX file or pass it on
		if (params.getGenerateTMX()) {
			tmx.writeTUFull(alignedTextUnit);
		} else { // Otherwise send each aligned TextUnit downstream
			return new Event(EventType.TEXT_UNIT, alignedTextUnit);
		}

		return sourceEvent;
	}

	private void getTargetTextUnits() {
		try {
			// Initialize the filter to read the translation to compare
			filter = fcMapper.createFilter(targetInput.getFilterConfigId(), null);
			// Open the second input for this batch item
			filter.open(targetInput);

			while (filter.hasNext()) {
				final Event event = filter.next();
				if (event.getEventType() == EventType.TEXT_UNIT) {
					// TODO: Error checking: Does the TU have a Name?
					// TODO: Error checking: Is there already an element with this Name?
					targetTextUnitMap.put(event.getTextUnit().getName(), event.getTextUnit());
				}
			}
		} finally {
			if (filter != null) {
				filter.close();
			}
		}
	}
}
