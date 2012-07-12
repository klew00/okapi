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

package net.sf.okapi.steps.msbatchtranslation;

import java.util.ArrayList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.connectors.microsoft.MicrosoftMTConnector;

@UsingParameters(SubmissionParameters.class)
public class MSBatchSubmissionStep extends BasePipelineStep {

	private final static int MAXCOUNT = 80;
	
	private SubmissionParameters params;
	private ArrayList<TextFragment> sources;
	private ArrayList<TextFragment> targets;
	private ArrayList<Integer> ratings;
	private int maxCount = MAXCOUNT;
	private MicrosoftMTConnector conn;
	private LocaleId sourceLocale;
	private LocaleId targetLocale;

	public MSBatchSubmissionStep () {
		params = new SubmissionParameters();
	}
	
	private void clean () {
		if ( sources != null ) {
			sources.clear();
			sources = null;
		}
		if ( targets != null ) {
			targets.clear();
			targets = null;
		}
		if ( ratings != null ) {
			ratings.clear();
			ratings = null;
		}
	}
	
	@Override
	public String getDescription () {
		return "Submits translations to Microosft Translator."
			+ " Expects: filter events. Sends back: filter events.";
	}

	@Override
	public String getName () {
		return "Microsoft Batch Submission";
	}

	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourceLocale (LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}
	
	@Override
	public IParameters getParameters () {
		return params;
	}
	
	@Override
	public void setParameters (IParameters params) {
		this.params = (SubmissionParameters)params;
	}
	
	@Override
	protected Event handleStartBatch (Event event) {
		sources = new ArrayList<TextFragment>();
		targets = new ArrayList<TextFragment>();
		ratings = new ArrayList<Integer>();
		if (( maxCount < 1 ) || ( maxCount > 100 )) maxCount = MAXCOUNT;
		
		// Initialize the engine
		conn = new MicrosoftMTConnector();
		net.sf.okapi.connectors.microsoft.Parameters prm = (net.sf.okapi.connectors.microsoft.Parameters)conn.getParameters();
		prm.setClientId(params.getClientId());
		prm.setSecret(params.getSecret());
		conn.setLanguages(sourceLocale, targetLocale);

		return event;
	}

	@Override
	protected Event handleTextUnit (Event event) {
		ITextUnit tu = event.getTextUnit();
		// Filter out the case where we don't send a translation
		if ( !tu.isTranslatable() ) {
			return event;
		}
		if ( !tu.hasTarget(targetLocale) ) {
			return event;
		}

		// The text unit should have valid translation at this point
		ISegments trgSegs = tu.getTargetSegments(targetLocale);
		for ( Segment srcSeg : tu.getSourceSegments() ) {
			if ( !srcSeg.text.hasText() ) continue;
			Segment trgSeg = trgSegs.get(srcSeg.getId());
			if ( trgSeg == null ) continue;
			if ( !trgSeg.text.hasText() ) continue;

			// Add the entry to the batch
			// Note: it may be safer to create copies? As the events will be processed
			// by following steps before these list get submitted.
			sources.add(srcSeg.text);
			targets.add(trgSeg.text);
			ratings.add(params.getRating());
		
			// Submit the batch if we reached the trigger point
			if ( sources.size() >= maxCount ) {
				submitTranslations();
			}
		}
		
		return event;
	}

	@Override
	protected Event handleEndBatch (Event event) {
		// Submit any remaining entries
		if ( !Util.isEmpty(sources) ) {
			submitTranslations();
		}
		clean();
		return event;
	}
	
	private void submitTranslations () {
		// Send the translations
		conn.addTranslationList(sources, targets, ratings);
		// Reset for next block
		sources.clear();
		targets.clear();
		ratings.clear();
	}

}
