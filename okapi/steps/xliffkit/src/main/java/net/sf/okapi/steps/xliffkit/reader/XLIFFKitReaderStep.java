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

package net.sf.okapi.steps.xliffkit.reader;

import java.net.URI;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.lib.persistence.PersistenceSession;
import net.sf.okapi.steps.xliffkit.opc.OPCPackageReader;

@UsingParameters()
public class XLIFFKitReaderStep extends BasePipelineStep {

	private OPCPackageReader reader;
	private boolean isDone = true;
	private String outputPath;
	//private boolean writeTargets = false;
	//private LocaleId targetLocale; 
	//private IFilterWriter filterWriter;
	private String outputEncoding;
	private Parameters params;
	private TextUnitMerger merger;

	public XLIFFKitReaderStep() {
		super();
		params = new Parameters();
		merger = new TextUnitMerger();
		reader = new OPCPackageReader(merger);
	}
	
	public String getDescription () {
		return "Reads XLIFF translation kit. Expects: Raw document for T-kit. Sends back: filter events.";
	}

	public String getName () {
		return "XLIFF Kit Reader";
	}

	@StepParameterMapping(parameterType = StepParameterType.OUTPUT_URI)
	public void setOutputURI (URI outputURI) {
		this.outputPath = outputURI.getPath();
		//writeTargets = !Util.isEmpty(outputPath);
	}
	
//	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
//	public void setTargetLocale (LocaleId targetLocale) {
//		this.targetLocale = targetLocale;
//	}
	
	@StepParameterMapping(parameterType = StepParameterType.OUTPUT_ENCODING)
	public void setOutputEncoding (String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}
	
	@Override
	public Event handleEvent(Event event) {
		event = super.handleEvent(event); // to allow access to handleStartDocument() etc. 
		
		switch (event.getEventType()) {
		case START_BATCH:
			isDone = true;
			break;

		case START_BATCH_ITEM:
			isDone = false;
			return event;
			
		case RAW_DOCUMENT:
			isDone = false;
			RawDocument rd = (RawDocument)event.getResource();
			//targetLocale = rd.getTargetLocale();
			//merger.setTrgLoc(targetLocale);
			merger.setUseApprovedOnly(params.isUseApprovedOnly());
			merger.setUpdateApprovedFlag(params.isUpdateApprovedFlag());
			if (params.isGenerateTargets())
				reader.setGeneratorOptions(outputEncoding, outputPath, params.isGroupByPackagePath());
			
			//reader.setGenerateTargets(params.isGenerateTargets());
			reader.open(rd); // Annotations are deserialized here
			
			Event e = reader.next(); 
			if (e.isStartDocument()) {
				StartDocument sd = e.getStartDocument();
				for (IAnnotation annotation : getSession().getAnnotations()) {
					sd.setAnnotation(annotation);
				}
			}
			return e;
		}

		if (isDone) {
			return event;
		} else {
//			if (event.getEventType() == EventType.START_DOCUMENT) {
//				RefsAnnotation ra = new RefsAnnotation(
//						getSession().getAnnotation());
//			}
			
			
//			if (writeTargets) {
//				switch (event.getEventType()) {				
//				case START_DOCUMENT:
//					processStartDocument(event);
//					break;
//
//				case END_DOCUMENT:
//					processEndDocument(event);
//					break;
//				
//				case TEXT_UNIT:								
//				case START_SUBDOCUMENT:
//				case START_GROUP:
//				case END_SUBDOCUMENT:
//				case END_GROUP:
//				case DOCUMENT_PART:
//					if (params.isGenerateTargets())
//						filterWriter.handleEvent(event);
//				}
//			}
			
			Event e = reader.next();
			isDone = !reader.hasNext();			
//			if (isDone && e.getEventType() == EventType.END_DOCUMENT)
//				processEndDocument(e);
			
			return e;
		}			
	}
	
	@Override
	public boolean isDone() {
		return isDone;
	}

	@Override
	public void destroy() {
		reader.close();
	}

	@Override
	public void cancel() {
		reader.cancel();
	}
	
	@Override
	public IParameters getParameters() {
		return params;
	}
	
	protected PersistenceSession getSession() {
		return reader.getSession();
	}
	
//	@Override
//	protected Event handleRawDocument(Event event) {
//		event = super.handleRawDocument(event); 
//		
//		RawDocument rd = event.getRawDocument();
//		for (IAnnotation annotation : getSession().getAnnotations()) {
//			rd.setAnnotation(annotation);
//		}
//		return event;
//	}
	
//	@Override
//	protected Event handleStartBatchItem(Event event) {
//		StartBatchItem sbi = event.getStartBatchItem();
//		for (IAnnotation annotation : getSession().getAnnotations()) {
//			sbi.setAnnotation(annotation);
//		}
//		return super.handleStartBatchItem(event);
//	}
	
//	private void processStartDocument (Event event) {
//		StartDocument startDoc = (StartDocument)event.getResource();
//		if ( outputEncoding == null ) outputEncoding = startDoc.getEncoding();
//		
////		if (params.isGenerateTargets()) {
////			filterWriter = startDoc.getFilterWriter();
////			//filterWriter.setOptions(targetLocale, outputEncoding);
////		}		
//		
//		String srcName = startDoc.getName();
//		String outFileName = outputPath + srcName;
//		
//		File outputFile = new File(outFileName);
//		Util.createDirectories(outputFile.getAbsolutePath());
//		
//		if (params.isGenerateTargets()) {
//			filterWriter.setOutput(outputFile.getAbsolutePath());
//			filterWriter.handleEvent(event);
//		}
//	}
//	
//	private void processEndDocument (Event event) {
//		if (params.isGenerateTargets()) {
//			filterWriter.handleEvent(event);
//			filterWriter.close();
//		}
//	}
}
