/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.applications.rainbow.utilities.alignment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import net.sf.okapi.applications.rainbow.lib.TMXWriter;
import net.sf.okapi.applications.rainbow.utilities.BaseUtility;
import net.sf.okapi.applications.rainbow.utilities.CancelEvent;
import net.sf.okapi.applications.rainbow.utilities.IFilterDrivenUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.segmentation.SRXDocument;
import net.sf.okapi.lib.segmentation.Segmenter;

public class Utility extends BaseUtility implements IFilterDrivenUtility  {

	private Parameters       params;
	private String           trgPath;
	private String           trgEncoding;
	private String           trgFilterSettings;
	private DbStoreBuilder   dbStoreBuilder;
	private DbStore          dbStore;
	private TMXWriter        tmxWriter = null;
	private IInputFilter     trgFilter;
	private Segmenter        srcSeg;
	private Segmenter        trgSeg;
	private int              aligned;
	private int              alignedTotal;
	private int              noText;
	private int              noTextTotal;
	private int              count;
	private int              countTotal;
	private Aligner          aligner;
	private boolean          stopProcess;
	

	public Utility () {
		params = new Parameters();
	}
	
	public void resetLists () {
		// Not used for this utility
	}
	
	public String getID () {
		return "oku_alignment";
	}
	
	public void doProlog (String sourceLanguage,
		String targetLanguage)
	{
		// Load the segmentation rules
		if ( params.segment ) {
			SRXDocument doc = new SRXDocument();
			doc.loadRules(params.sourceSrxPath);
			if ( doc.hasWarning() ) logger.warn(doc.getWarning());
			srcSeg = doc.applyLanguageRules(sourceLanguage, null);
			if ( !params.sourceSrxPath.equals(params.targetSrxPath) ) {
				doc.loadRules(params.targetSrxPath);
				if ( doc.hasWarning() ) logger.warn(doc.getWarning());
			}
			trgSeg = doc.applyLanguageRules(targetLanguage, null);
		}
		
		// Prepare the TMX output
		if ( tmxWriter != null ) {
			tmxWriter.close();
			tmxWriter = null;
		}
		tmxWriter = new TMXWriter();
		tmxWriter.create(params.tmxPath);
		tmxWriter.setTradosWorkarounds(params.useTradosWorkarounds);
		tmxWriter.writeStartDocument(sourceLanguage, targetLanguage);
		
		// Prepare the db store
		dbStoreBuilder = new DbStoreBuilder();
		// We use the source part only, and it contains the target language of the alignment task
		dbStoreBuilder.setSegmenters(trgSeg, null);
		
		if ( aligner == null ) {
			//TODO: make info part of constructor
			//aligner = new SegmentsAligner(shell, params.targetSrxPath);
			aligner = new Aligner(shell);
			aligner.setInfo(params.targetSrxPath, params.checkSingleSegUnit);
		}
		
		alignedTotal = 0;
		noTextTotal = 0;
		countTotal = 0;
	}
	
	public void doEpilog () {
		logger.info(String.format("Total translatable text units = %d", countTotal));
		logger.info(String.format("Total without text = %d", noTextTotal));
		logger.info(String.format("Total aligned = %d", alignedTotal));
    	
		if ( aligner != null ) {
			aligner.close();
			aligner = null;
		}
		if ( tmxWriter != null ) {
			tmxWriter.writeEndDocument();
			tmxWriter.close();
			tmxWriter = null;
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

	public String getInputRoot () {
		return null;
	}
	
	public String getOutputRoot () {
		return null;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean needsRoots () {
		return false;
	}

	public boolean needsOutputFilter () {
		return false;
	}

	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}

	public void setRoots (String inputRoot,
		String outputRoot)
	{
	}

	@Override
    public void startResource (Document resource) {
		try {
			// Initialize the filter for the target
			Object[] filters = fa.loadFilterFromFilterSettingsType1(paramsFolder,
				trgFilterSettings, trgFilter, null);
			trgFilter = (IInputFilter)filters[0];
			trgFilter.setOutput(dbStoreBuilder);
			InputStream input = new FileInputStream(trgPath);
			trgFilter.initialize(input, trgPath, trgFilterSettings, trgEncoding,
				// Note we use the target language as the source, because we are
				// processing the 'target' from the utility viewpoint
				resource.getTargetLanguage(), resource.getTargetLanguage());
			
			// Fill the database with the target file
			trgFilter.process();
			trgFilter.close();
			dbStore = dbStoreBuilder.getDbStore();
			aligned = 0;
			noText = 0;
			count = 0;
			
			aligner.setDocumentName(resource.getName());
		}
		catch ( FileNotFoundException e ) {
			throw new RuntimeException(e);
		}
    }
	
	@Override
    public void endResource (Document resource) {
    	alignedTotal += aligned;
    	noTextTotal += noText;
    	countTotal += count;
    	logger.info(String.format("Translatable text units = %d", count));
    	logger.info(String.format("Without text = %d", noText));
    	logger.info(String.format("Aligned = %d", aligned));
    }

	@Override
    public void endExtractionItem (TextUnit item) {
		processTU(item);
		if ( item.hasChild() ) {
			for ( TextUnit tu : item.childTextUnitIterator() ) {
				processTU(tu);
			}
		}
    }
	
	private void processTU (TextUnit tu) {
		//TODO: Find a way to stop the filter
		if ( stopProcess ) return;
		
		// Skip non-translatable
		if ( !tu.isTranslatable() ) return;
		count++;
		// Segment the source if needed
		if ( params.segment ) {
			srcSeg.computeSegments(tu.getSourceContent());
			tu.getSourceContent().createSegments(srcSeg.getSegmentRanges());
			if ( !tu.getSourceContent().isSegmented() ) {
				noText++;
				return;
			}
		}
		// Retrieve the corresponding target(s)
		TextContainer trgTC = dbStore.findEntry(tu.getName(), true);
		if ( trgTC != null ) {
			// Check alignment and fix it if needed
			tu.setTargetContent(trgTC);
			switch ( aligner.align(tu) ) {
			case 1:
				aligned++;
				tmxWriter.writeItem(tu);
				return;
			case 2:
				// Do nothing (skip entry)
				break;
			case 0:
				stopProcess = true;
				fireCancelEvent(new CancelEvent(this));
				break;
			}
		}
		// Else: track the item not aligned
		logger.info("Not aligned: "+tu.getName());
	}
	
	public boolean isFilterDriven () {
		return true;
	}

	public void addInputData (String path,
		String encoding,
		String filterSettings)
	{
		// Target set the second time this is called
		trgPath = path;
		trgEncoding = encoding;
		trgFilterSettings = filterSettings;
	}

	public void addOutputData (String path,
		String encoding)
	{
		// Not used for this utility
	}

	public int getInputCount () {
		// Source and possibly target
		return 2;
	}

	public String getFolderAfterProcess () {
		return Util.getDirectoryName(params.tmxPath);
	}


}
