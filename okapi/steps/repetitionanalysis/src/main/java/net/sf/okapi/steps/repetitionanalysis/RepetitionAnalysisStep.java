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

package net.sf.okapi.steps.repetitionanalysis;

import java.util.List;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.tm.pensieve.common.MetadataType;
import net.sf.okapi.tm.pensieve.common.TmHit;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;
import net.sf.okapi.tm.pensieve.seeker.ITmSeeker;
import net.sf.okapi.tm.pensieve.seeker.TmSeekerFactory;
import net.sf.okapi.tm.pensieve.writer.ITmWriter;
import net.sf.okapi.tm.pensieve.writer.TmWriterFactory;

/**
 * The step analyzes repetitions in single documents. Either exact or configurable fuzzy search is performed.
 * <p>2 types of annotations are created for found repetitive segments -- RepetitiveSegmentAnnotation and AltTranslationsAnnotation.  
 * RepetitiveSegmentAnnotation's are attached to all repetitive source segments. 
 * AltTranslationsAnnotation's are attached to target segments, corresponding to repetitive source segments.
 * AltTranslationsAnnotation is not attached for the first repetitive segment not to be counted by counting steps
 * twice as repetitive with itself.
 */
public class RepetitionAnalysisStep extends BasePipelineStep {

	private Parameters params;
	private boolean searchExact;
	private int counter;
	private String tmDir;
	private ITmWriter tmWriter;
	private ITmSeeker currentTm;
	private LocaleId sourceLocale;
	private LocaleId targetLocale;

	public RepetitionAnalysisStep() {
		super();
		params = new Parameters();
		//tmDir = Util.ensureSeparator(ClassUtil.getTargetPath(this.getClass()), true) + "tm/";
		tmDir = Util.ensureSeparator(ClassUtil.getTargetPath(this.getClass()), true);
		//System.out.println((new File(tmDir)).getAbsolutePath());
	}
	
	@Override
	public String getName() {
		return "Repetition Analysis";
	}

	@Override
	public String getDescription() {
		return "Analyzes repetitions in input documents. Adds AltTranslationsAnnotation's and RepetitiveSegmentAnnotation's to " +
				"repetitive segments."
		+ " Expects: raw document. Sends back: raw document.";
	}
	
	@Override
	public IParameters getParameters () {
		return params;
	}
	
	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	@StepParameterMapping(parameterType = StepParameterType.SOURCE_LOCALE)
	public void setSourceLocale (LocaleId sourceLocale) {
		this.sourceLocale = sourceLocale;
	}
	
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.targetLocale = targetLocale;
	}
	
	private void close() {
		if (tmWriter != null) {
			tmWriter.close();
			tmWriter = null;
		}
		if (currentTm != null) {
			currentTm.close();
			currentTm = null;
		}
	}
	
	@Override
	protected Event handleStartDocument(Event event) {
		close();
//		Util.deleteDirectory(tmDir, true);
//		Util.createDirectories(tmDir);
		searchExact = params.getFuzzyThreshold() >= 100;
		counter = 0;		
		tmWriter = TmWriterFactory.createFileBasedTmWriter(tmDir, true);
		currentTm = TmSeekerFactory.createFileBasedTmSeeker(tmDir);
		return super.handleStartDocument(event);
	}
	
	@Override
	protected Event handleEndDocument(Event event) {
		close();
		return super.handleEndDocument(event);
	}
	
	@Override
	protected Event handleTextUnit(Event event) {
		ITextUnit tu = event.getTextUnit();
		if (tu.isTranslatable()) {
			ISegments ssegments = tu.getSource().getSegments();
			ISegments tsegments = null;
			
			if (targetLocale != null) {
				TextContainer ttc = tu.getTarget(targetLocale);				
				if (ttc != null) tsegments = ttc.getSegments();
			}
						
			for (Segment seg : ssegments) {
				counter++;
				TextFragment tf = seg.getContent();
				//TextFragment tf = new TextFragment("Elephants cannot fly.");
				if (tf.isEmpty()) continue;
				
				String tuid = Integer.toString(counter);
				List<TmHit> hits = null;
				if (searchExact) {
					hits = currentTm.searchExact(tf, null);
				}
				else {
					hits = currentTm.searchFuzzy(tf, params.getFuzzyThreshold(), 1, null);
				}
								
				if (hits.size() > 0) {
					TmHit hit = hits.get(0);
					TranslationUnit hitTu = hit.getTu();
					RepetitiveSegmentAnnotation ann = 
						new RepetitiveSegmentAnnotation(
								tuid,
								hitTu.getMetadataValue(MetadataType.ID), 
								hit.getScore());
					seg.setAnnotation(ann);
					//System.out.println("= " + tf);
					
					Segment tseg = tsegments.get(seg.getId()); // Always exists, created empty in case of no target
					TextFragment stf = hitTu.getSource().getContent();
					TextFragment ttf = hitTu.getTarget().getContent();
					AltTranslationsAnnotation ata = new AltTranslationsAnnotation();
					ata.add(new AltTranslation(sourceLocale, targetLocale == null ? sourceLocale : targetLocale, 
							tf, stf, ttf, MatchType.EXACT_DOCUMENT_CONTEXT, 
							Math.round(hit.getScore() * 100), ""));
					tseg.setAnnotation(ata);
				}
				else {
					TranslationUnit ntu = new TranslationUnit(
							new TranslationUnitVariant(sourceLocale, tf),
							new TranslationUnitVariant(targetLocale == null ? sourceLocale : targetLocale, new TextFragment("")));
					ntu.setMetadataValue(MetadataType.ID, tuid);
					RepetitiveSegmentAnnotation ann = 
						new RepetitiveSegmentAnnotation(
								tuid,
								tuid, // Is the head translation unit for itself 
								1.0f);
					seg.setAnnotation(ann);
					tmWriter.indexTranslationUnit(ntu);
					//System.out.println("+ " + tf);
					
					// Should be called here after every segment addition to the TM for the situations 
					// of repetitive segments within a tu
					tmWriter.commit();
					
					// TODO Remove when fixed
//					currentTm.close();
//					currentTm = TmSeekerFactory.createFileBasedTmSeeker(tmDir);
				}
			}
		}
		return super.handleTextUnit(event);
	}
}
