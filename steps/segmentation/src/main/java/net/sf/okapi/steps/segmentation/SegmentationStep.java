/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.steps.segmentation;

import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.segmentation.ISegmenter;
import net.sf.okapi.lib.segmentation.SRXDocument;

@UsingParameters(Parameters.class)
public class SegmentationStep extends BasePipelineStep {

	private final Logger logger = Logger.getLogger(getClass().getName());

	private Parameters params;
	private ISegmenter srcSeg;
	private ISegmenter trgSeg;
	private LocaleId sourceLocale;
	private LocaleId targetLocale;
	private boolean initDone;

	public SegmentationStep () {
		params = new Parameters();
		srcSeg = null;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setsourceLocale (LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}
	
	public String getName () {
		return "Segmentation";
	}

	public String getDescription () {
		return "Apply SRX segmentation to the text units content of a document. "
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

	@Override
	protected Event handleStartBatch (Event event) {
		initDone = false;
		return event;
	}
	
	@Override
	protected Event handleStartBatchItem (Event event) {
		if ( initDone ) return event; // Initialize once per batch
		//TODO: implement projDir
		SRXDocument srxDoc = new SRXDocument();
		String src = null;
		if ( params.segmentSource ) {
			src = params.sourceSrxPath; //.replace(VAR_PROJDIR, projectDir);
			srxDoc.loadRules(src);
			if ( srxDoc.hasWarning() ) logger.warning(srxDoc.getWarning());
			srcSeg = srxDoc.compileLanguageRules(sourceLocale, null);
		}
		if ( params.segmentTarget ) {
			String trg = params.targetSrxPath; //.replace(VAR_PROJDIR, projectDir);
			if ( !src.equals(trg) ) {
				srxDoc.loadRules(trg);
				if ( srxDoc.hasWarning() ) logger.warning(srxDoc.getWarning());
			}
		}
		trgSeg = srxDoc.compileLanguageRules(targetLocale, null);
		
		return event;
	}
	
	@Override
	protected Event handleTextUnit (Event event) {
		TextUnit tu = (TextUnit)event.getResource();
		// Skip non-translatable
		if ( !tu.isTranslatable() ) return event;

		TextContainer cont;
		if ( tu.hasTarget(targetLocale) ) {
			if ( params.segmentTarget ) {
				cont = tu.getTarget(targetLocale);
				if ( !cont.isSegmented() ) {
					trgSeg.computeSegments(cont);
					cont.createSegments(trgSeg.getRanges());
				}
			}
		}
		else if ( params.segmentSource ) {
			cont = tu.getSource();
			if ( !cont.isSegmented() ) {
				srcSeg.computeSegments(cont);
				cont.createSegments(srcSeg.getRanges());
			}
		}
		
		// Make sure we have target content
		tu.createTarget(targetLocale, false, IResource.COPY_ALL);
		
		return event;
	}

}
