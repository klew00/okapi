/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow.utilities.alignment;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.okapi.applications.rainbow.lib.TMXWriter;
import net.sf.okapi.applications.rainbow.utilities.BaseFilterDrivenUtility;
import net.sf.okapi.common.ConfigurationString;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.segmentation.SRXDocument;
import net.sf.okapi.lib.segmentation.Segmenter;
import net.sf.okapi.tm.simpletm.Database;

public class Utility extends BaseFilterDrivenUtility {

	private Parameters params;
	private String fileName;
	private DbStoreBuilder dbStoreBuilder;
	private DbStore dbStore;
	private TMXWriter tmxWriter = null;
	private Database simpleTm = null;
	private IFilter trgFilter;
	private Segmenter srcSeg;
	private Segmenter trgSeg;
	private int aligned;
	private int alignedTotal;
	private int noText;
	private int noTextTotal;
	private int count;
	private int countTotal;
	private int manual;
	private int manualTotal;
	private Aligner aligner;
	private boolean stopProcess;
	private int targetCount;
	private Map<String, String> originalAttributes;
	private Map<String, String> assignedAttributes;

	public Utility () {
		params = new Parameters();
		needsSelfOutput = false;
	}
	
	public String getName () {
		return "oku_alignment";
	}
	
	public void preprocess () {
		// Load the segmentation rules
		if ( params.segment ) {
			SRXDocument doc = new SRXDocument();
			doc.loadRules(params.sourceSrxPath);
			if ( doc.hasWarning() ) logger.warn(doc.getWarning());
			srcSeg = doc.applyLanguageRules(srcLang, null);
			if ( !params.sourceSrxPath.equals(params.targetSrxPath) ) {
				doc.loadRules(params.targetSrxPath);
				if ( doc.hasWarning() ) logger.warn(doc.getWarning());
			}
			trgSeg = doc.applyLanguageRules(trgLang, null);
		}
		
		// Prepare the TMX output if requested
		if ( params.createTMX ) {
			if ( tmxWriter != null ) {
				tmxWriter.close();
				tmxWriter = null;
			}
			tmxWriter = new TMXWriter();
			tmxWriter.create(params.tmxPath);
			tmxWriter.setTradosWorkarounds(params.useTradosWorkarounds);
			tmxWriter.writeStartDocument(srcLang, trgLang,
				getName(), null, (params.segment ? "sentence" : "paragraph"),
				null, null);
		}
		
		// Prepare the simpletm database
		if ( params.createTM ) {
			simpleTm = new Database();
			simpleTm.create(params.tmPath, true, trgLang);
		}
		
		// Prepare the attributes if needed
		if ( params.createAttributes ) {
			ConfigurationString cfgString = new ConfigurationString(
				params.attributes);
			originalAttributes = cfgString.toMap();
			assignedAttributes = new LinkedHashMap<String, String>();
		}
		
		// Prepare exclusion pattern if needed
		if ( params.useExclusion ) {
			tmxWriter.setExclusionOption(params.exclusion);
		}
		else {
			tmxWriter.setExclusionOption(null);
		}
		
		// Prepare the db store
		dbStoreBuilder = new DbStoreBuilder();
		// We use the source part only, and it contains the target language of the alignment task
		dbStoreBuilder.setSegmenters(trgSeg, null);
		
		if ( aligner == null ) {
			aligner = new Aligner(shell, help, updateCommand);
			aligner.setInfo(params.targetSrxPath, params.checkSingleSegUnit,
				params.useAutoCorrection, trgLang);
		}
		
		alignedTotal = 0;
		noTextTotal = 0;
		countTotal = 0;
		manualTotal = 0;
	}
	
	public void postprocess () {
		logger.info(String.format("Total translatable text units = %d", countTotal));
		logger.info(String.format("Total without text = %d", noTextTotal));
		logger.info(String.format("Total aligned = %d (manually modified = %d)",
			alignedTotal, manualTotal));
    	
		if ( aligner != null ) {
			aligner.closeWithoutWarning();
			aligner = null;
		}
		if ( tmxWriter != null ) {
			tmxWriter.writeEndDocument();
			tmxWriter.close();
			tmxWriter = null;
		}
		if ( simpleTm != null ) {
			simpleTm.close();
			simpleTm = null;
		}
		if ( dbStore != null ) {
			dbStore.close();
			dbStore = null;
		}
		srcSeg = null;
		trgSeg = null;
	}
	
	public IParameters getParameters () {
		return params;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean needsRoots () {
		return false;
	}
	
	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}

	public boolean isFilterDriven () {
		return true;
	}

	public int requestInputCount () {
		// Source and possibly target
		return 2;
	}

	@Override
	public String getFolderAfterProcess () {
		return Util.getDirectoryName(params.tmxPath);
	}

	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			processStartDocument((StartDocument)event.getResource());
			break;
		case TEXT_UNIT:
			processTextUnit((TextUnit)event.getResource());
			break;
		case END_DOCUMENT:
			processEndDocument();
			break;
		}
		return event;
	}

    private void processStartDocument (StartDocument resource) {
		// fileName is the value we set as attribute in the TM.
    	// Get it from the document name
		fileName = Util.getFilename(resource.getName(), true);
		
		readTargetToDb();
		
		dbStore = dbStoreBuilder.getDbStore();
		targetCount = dbStore.getTextUnitCount(true);
		aligned = 0;
		noText = 0;
		count = 0;
		manual = 0;
		
		aligner.setDocumentName(resource.getName());
    }
	
    private void readTargetToDb () {
    	try {
			// Initialize the filter for the target
			trgFilter = fa.loadFilterFromFilterSettingsType1(paramsFolder,
				getInputFilterSettings(1), trgFilter);
			
			// Note we use the target language as the source, because we are
			// processing the 'target' from the utility viewpoint
			trgFilter.setOptions(srcLang, trgLang, getInputEncoding(1), false);

			// Open the file
			File f = new File(getInputPath(1));
			trgFilter.open(f.toURI());
			
			// Send the START event
			dbStoreBuilder.handleEvent(new Event(EventType.START));
			// Fill the database with the target file
			while ( trgFilter.hasNext() ) {
				dbStoreBuilder.handleEvent(trgFilter.next());
			}
			// Send the FINISHED event
			dbStoreBuilder.handleEvent(new Event(EventType.FINISHED));
    	}
    	finally {
    		if ( trgFilter != null ) trgFilter.close();
    	}
		
    }
    
    private void processEndDocument () {
    	alignedTotal += aligned;
    	noTextTotal += noText;
    	countTotal += count;
    	manualTotal += manual;
    	logger.info(String.format("Translatable text units = %d", count));
    	logger.info(String.format("Without text = %d", noText));
    	logger.info(String.format("Aligned = %d (manually modified = %d)",
    		aligned, manual));
    }

	private void processTextUnit (TextUnit tu) {
		//TODO: Find a way to stop the filter
		if ( stopProcess ) return;
		
		// Skip non-translatable
		if ( !tu.isTranslatable() ) return;
		count++;
		// Segment the source if needed
		if ( params.segment ) {
			srcSeg.computeSegments(tu.getSource());
			tu.getSource().createSegments(srcSeg.getSegmentRanges());
			if ( !tu.getSource().isSegmented() ) {
				noText++;
				return;
			}
		}
		// Retrieve the corresponding target(s)
		TextContainer trgTC = dbStore.findEntry(tu.getName(), true);
		if ( trgTC != null ) {
			// Check alignment and fix it if needed
			tu.setTarget(trgLang, trgTC);
			switch ( aligner.align(tu, count, targetCount) ) {
			case 1:
				aligned++;
				if ( aligner.wasModifiedManually() ) manual++;
				// Prepare the attributes if needed
				if ( params.createAttributes ) {
					String value;
					for ( String key : originalAttributes.keySet() ) {
						value = originalAttributes.get(key);
						if ( "${filename}".equals(value) ) {
							assignedAttributes.put(key, fileName);
						}
						else if ( "${resname}".equals(value) ) {
							assignedAttributes.put(key, tu.getName());
						}
						else {
							assignedAttributes.put(key, value);
						}
					}
				}
				// Check for 'to-review' mark
				if ( tu.hasTargetProperty(trgLang, Aligner.ALIGNSTATUS_KEY) ) { 
					assignedAttributes.put(Aligner.ALIGNSTATUS_KEY,
						tu.getTargetProperty(trgLang, Aligner.ALIGNSTATUS_KEY).getValue());
				}
				else {
					assignedAttributes.remove(Aligner.ALIGNSTATUS_KEY);
				}
				
				// Output to TMX
				if ( params.createTMX ) {
					tmxWriter.writeItem(tu, assignedAttributes);
				}
				// Output to SimpleTM
				if ( params.createTM ) {
					simpleTm.addEntry(tu, tu.getName(), fileName);
				}
				return;
			case 2:
				// Do nothing (skip entry)
				break;
			case 0:
				stopProcess = true;
				cancel();
				break;
			}
		}
		// Else: track the item not aligned
		logger.info("Not aligned: "+tu.getName());
	}
	
}
