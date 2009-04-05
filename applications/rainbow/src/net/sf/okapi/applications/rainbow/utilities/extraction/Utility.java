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

package net.sf.okapi.applications.rainbow.utilities.extraction;

import java.io.File;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.packages.IWriter;
import net.sf.okapi.applications.rainbow.utilities.BaseFilterDrivenUtility;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.ScoresAnnotation;
import net.sf.okapi.common.resource.InputResource;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.lib.segmentation.SRXDocument;
import net.sf.okapi.lib.segmentation.Segmenter;
import net.sf.okapi.lib.translation.QueryManager;
import net.sf.okapi.tm.simpletm.SimpleTMConnector;

public class Utility extends BaseFilterDrivenUtility {

	private Parameters params;
	private IWriter writer;
	private int id;
	private Segmenter sourceSeg;
	private Segmenter targetSeg;
	private QueryManager qm;
	private String resolvedOutputDir;
	private int segDocCount;
	private int segTotalCount;
	private int exactDocCount;
	private int fuzzyDocCount;
	private int exactTotalCount;
	private int fuzzyTotalCount;
	
	public Utility () {
		params = new Parameters();
		needsSelfOutput = false;
	}
	
	public String getName () {
		return "oku_extraction";
	}
	
	public void preprocess () {
		if ( params.pkgType.equals("xliff") ) {
			writer = new net.sf.okapi.applications.rainbow.packages.xliff.Writer();
			writer.setParameters(params.xliffOptions);
		}
		else if ( params.pkgType.equals("omegat") )
			writer = new net.sf.okapi.applications.rainbow.packages.omegat.Writer();
		else if ( params.pkgType.equals("ttx") )
			writer = new net.sf.okapi.applications.rainbow.packages.ttx.Writer();
		else if ( params.pkgType.equals("rtf") )
			writer = new net.sf.okapi.applications.rainbow.packages.rtf.Writer(
				new GenericSkeletonWriter());
		else
			throw new RuntimeException("Unknown package type: " + params.pkgType);
	
		// Load SRX file(s) and create segmenters if required
		if ( params.preSegment ) {
			String src = params.sourceSRX.replace(VAR_PROJDIR, projectDir);
			String trg = params.targetSRX.replace(VAR_PROJDIR, projectDir);
			SRXDocument doc = new SRXDocument();
			doc.loadRules(src);
			if ( doc.hasWarning() ) logger.warning(doc.getWarning());
			sourceSeg = doc.applyLanguageRules(srcLang, null);
			//TODO: This is not working cross-platform!
			if ( !src.equalsIgnoreCase(trg) ) {
				doc.loadRules(trg);
				if ( doc.hasWarning() ) logger.warning(doc.getWarning());
			}
			targetSeg = doc.applyLanguageRules(trgLang, null);
		}

		if ( params.preTranslate ) {
			qm = new QueryManager();
			qm.setLanguages(srcLang, trgLang);
			String path = params.tmPath.replace(VAR_PROJDIR, projectDir);
			qm.addAndInitializeResource(new SimpleTMConnector(), path, path);
		}
		
		resolvedOutputDir = params.outputFolder + File.separator + params.pkgName;
		resolvedOutputDir = resolvedOutputDir.replace(VAR_PROJDIR, projectDir);
		Util.deleteDirectory(resolvedOutputDir, false);
		
		id = 0;
		writer.setInformation(srcLang, trgLang,
			"TODO:projectID", resolvedOutputDir, params.makePackageID(), inputRoot);
		writer.writeStartPackage();
		
		segTotalCount = exactTotalCount = fuzzyTotalCount = 0;
	}

	public void postprocess () {
		if ( writer != null ) {
			writer.writeEndPackage(params.createZip);
			writer = null;
		}
		if ( qm != null ) {
			qm.close();
			qm = null;
		}
		
		// Statistics
		logger.info(String.format("\nTotal exact matches = %d (%3d%%)", exactTotalCount,
			Util.getPercentage(exactTotalCount, segTotalCount)));
		logger.info(String.format("Total Fuzzy matches = %d (%3d%%)", fuzzyTotalCount,
			Util.getPercentage(fuzzyTotalCount, segTotalCount)));
		int n = segTotalCount-(exactTotalCount+fuzzyTotalCount);
		logger.info(String.format("Total No matches = %d (%3d%%)", n,
			Util.getPercentage(n, segTotalCount)));
		logger.info(String.format("Total segments = %d", segTotalCount));
		
	}
	
	public IParameters getParameters () {
		return params;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean needsRoots () {
		return true;
	}

	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}

	public boolean isFilterDriven () {
		return true;
	}

	public int requestInputCount () {
		return 1;
	}

	@Override
	public String getFolderAfterProcess () {
		return resolvedOutputDir;
	}

	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			processStartDocument((StartDocument)event.getResource());
			break;
		case END_DOCUMENT:
			segTotalCount += segDocCount;
			exactTotalCount += exactDocCount;
			fuzzyTotalCount += fuzzyDocCount;
			logger.info(String.format("Exact matches = %d (%3d%%)", exactDocCount,
				Util.getPercentage(exactDocCount, segDocCount)));
			logger.info(String.format("Fuzzy matches = %d (%3d%%)", fuzzyDocCount,
				Util.getPercentage(fuzzyDocCount, segDocCount)));
			int n = segDocCount-(exactDocCount+fuzzyDocCount);
			logger.info(String.format("No matches = %d (%3d%%)", n,
				Util.getPercentage(n, segDocCount)));					
			logger.info(String.format("Segments = %d", segDocCount));
			break;
		case TEXT_UNIT:
			processTextUnit((TextUnit)event.getResource());
			break;
		case FILE_RESOURCE:
			processFileResource((InputResource)event.getResource());
			break;
		}
		// All events then go to the actual writer
		return writer.handleEvent(event);
	}

	/**
	 * Handles files without any associated filter settings (.png, etc.)
	 * @param fr The file resource to process.
	 */
	private void processFileResource (InputResource fr) {
		String relativeInput = getInputPath(0).substring(inputRoot.length()+1);
		writer.createCopies(++id, relativeInput);
	}
	
    private void processStartDocument (StartDocument resource) {
    	segDocCount = exactDocCount = fuzzyDocCount = 0;
		if (( qm != null ) && params.useFileName ) {
			qm.setAttribute("FileName", Util.getFilename(getInputPath(0), true));
		}
		String relativeInput = getInputPath(0).substring(inputRoot.length()+1);
		String relativeOutput = getOutputPath(0).substring(outputRoot.length()+1);
		String[] res = FilterAccess.splitFilterSettingsType1("", getInputFilterSettings(0));
		writer.createOutput(++id, relativeInput, relativeOutput,
			getInputEncoding(0), getOutputEncoding(0),
			res[1], resource.getFilterParameters());
    }
	
    private void processTextUnit (TextUnit tu) {
    	// Do not process non-translatable text units
    	//TODO: Do we need to still make sure we have a target copy?
    	if ( !tu.isTranslatable() ) return;

    	TextContainer cont = null;
		// Segment if requested
		if (( params.preSegment ) && !"no".equals(tu.getProperty("canSegment")) ) {
			try {
				cont = tu.getSource();
				sourceSeg.computeSegments(cont);
				cont.createSegments(sourceSeg.getSegmentRanges());
				if ( tu.hasTarget(trgLang) ) {
					cont = tu.getTarget(trgLang);
					targetSeg.computeSegments(cont);
					cont.createSegments(targetSeg.getSegmentRanges());
				}
			}
			catch ( Throwable e ) {
				logger.severe(String.format("Error segmenting text unit id=%s: "
					+e.getMessage(), tu.getId()));
			}
		}
		
		// Compute the statistics
		segDocCount += tu.getSource().getSegmentCount();

		// Leverage if requested
		if ( qm != null ) {
			if ( params.useGroupName ) {
				qm.setAttribute("GroupName", tu.getName());
			}
			qm.leverage(tu);
			cont = tu.getTarget(trgLang);

			// Compute statistics
			if ( cont != null ) {
				ScoresAnnotation scores = cont.getAnnotation(ScoresAnnotation.class);
				if ( scores != null ) {
					for ( int score : scores.getList() ) {
						if ( score > 99 ) exactDocCount++;
						else if ( score != 0 ) fuzzyDocCount++;
					}
				}
			}
		}
	}

}
