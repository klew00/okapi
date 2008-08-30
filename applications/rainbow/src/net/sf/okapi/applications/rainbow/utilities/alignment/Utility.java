/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.lib.TMXWriter;
import net.sf.okapi.applications.rainbow.utilities.IFilterDrivenUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.segmentation.SRXDocument;
import net.sf.okapi.lib.segmentation.Segmenter;

public class Utility extends ThrougputPipeBase implements IFilterDrivenUtility  {

	//private final Logger                    logger = LoggerFactory.getLogger("net.sf.okapi.logging");
	private Parameters       params;
	private String           trgPath;
	private String           trgEncoding;
	private String           trgFilterSettings;
	private DbStoreBuilder   dbStoreBuilder;
	private DbStore          dbStore;
	private TMXWriter        tmxWriter = null;
	private FilterAccess     fa;
	private String           paramsFolder;
	private IInputFilter     trgFilter;
	private Segmenter        srcSeg;
	private Segmenter        trgSeg;

	
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
			doc.loadRules(params.srxPath);
			srcSeg = doc.applyLanguageRules(sourceLanguage, null);
			trgSeg = doc.applyLanguageRules(targetLanguage, null);
		}
		
		// Prepare the TMX output
		if ( tmxWriter != null ) {
			tmxWriter.close();
			tmxWriter = null;
		}
		tmxWriter = new TMXWriter();
		tmxWriter.create(params.getParameter("tmxPath"));
		tmxWriter.writeStartDocument(sourceLanguage, targetLanguage);
		
		// Prepare the Db store
		dbStoreBuilder = new DbStoreBuilder();
		dbStoreBuilder.setSegmenters(srcSeg, trgSeg);
	}
	
	public void doEpilog () {
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
		}
		catch ( FileNotFoundException e ) {
			throw new RuntimeException(e);
		}
    }

	@Override
    public void endExtractionItem(TextUnit item) {
		try {
			processTU(item);
			if ( item.hasChild() ) {
				for ( TextUnit tu : item.childTextUnitIterator() ) {
					processTU(tu);
				}
			}
		}
		finally {
			super.endExtractionItem(item);
		}
    }
	
	private void processTU (TextUnit tu) {
		TextContainer trgTC = dbStore.findEntry(tu.getName());
		if ( trgTC != null ) {
			tu.setTargetContent(trgTC);
			tmxWriter.writeItem(tu);
		}
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
		return Util.getDirectoryName(params.getParameter("tmxPath"));
	}

	public void setFilterAccess (FilterAccess filterAccess,
		String paramsFolder)
	{
		fa = filterAccess;
		this.paramsFolder = paramsFolder;
	}
}
