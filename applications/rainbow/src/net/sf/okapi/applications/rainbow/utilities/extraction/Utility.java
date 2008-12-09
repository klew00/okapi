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

package net.sf.okapi.applications.rainbow.utilities.extraction;

import java.io.File;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.packages.IWriter;
import net.sf.okapi.applications.rainbow.utilities.BaseUtility;
import net.sf.okapi.applications.rainbow.utilities.IFilterDrivenUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.segmentation.SRXDocument;
import net.sf.okapi.lib.segmentation.Segmenter;
import net.sf.okapi.lib.translation.QueryManager;
import net.sf.okapi.tm.simpletm.SimpleTMConnector;

public class Utility extends BaseUtility implements IFilterDrivenUtility {

	private String inputRoot;
	private String inputPath;
	private String outputRoot;
	private String outputPath;
	private Parameters params;
	private IWriter writer;
	private int id;
	private Segmenter sourceSeg;
	private Segmenter targetSeg;
	private QueryManager qm;
	
	public Utility () {
		params = new Parameters();
	}
	
	public void resetLists () {
		// Not used for this utility
	}
	
	public String getID () {
		return "oku_extraction";
	}
	
	public void doEpilog () {
		if ( qm != null ) {
			qm.close();
			qm = null;
		}
		if ( writer != null ) {
			writer.writeEndPackage(params.createZip);
			writer = null;
		}
	}

	public void doProlog (String sourceLanguage,
		String targetLanguage)
	{
		if ( params.pkgType.equals("xliff") )
			writer = new net.sf.okapi.applications.rainbow.packages.xliff.Writer();
		else if ( params.pkgType.equals("omegat") )
			writer = new net.sf.okapi.applications.rainbow.packages.omegat.Writer();
		else if ( params.pkgType.equals("ttx") )
			writer = new net.sf.okapi.applications.rainbow.packages.ttx.Writer();
		else if ( params.pkgType.equals("rtf") )
			writer = new net.sf.okapi.applications.rainbow.packages.rtf.Writer();
		else
			throw new RuntimeException("Unknown package type: " + params.pkgType);
	
		// Load SRX file(s) and create segmenters if required
		if ( params.preSegment ) {
			SRXDocument doc = new SRXDocument();
			doc.loadRules(params.sourceSRX);
			if ( doc.hasWarning() ) logger.warn(doc.getWarning());
			sourceSeg = doc.applyLanguageRules(sourceLanguage, null);
			if ( !params.sourceSRX.equalsIgnoreCase(params.targetSRX) ) {
				doc.loadRules(params.targetSRX);
				if ( doc.hasWarning() ) logger.warn(doc.getWarning());
			}
			targetSeg = doc.applyLanguageRules(targetLanguage, null);
		}

		if ( params.preTranslate ) {
			qm = new QueryManager();
			qm.setLanguages(sourceLanguage, targetLanguage);
			qm.addAndInitializeResource(new SimpleTMConnector(),
				params.tmPath, params.tmPath);
		}
		
		String outputDir = params.outputFolder + File.separator + params.pkgName;
		/*if ( (new File(outputDir)).exists() ) {
			// Ask confirmation for cleaning existing folder
			MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
			dlg.setMessage("\nDo you want to save the project?");
			dlg.setText("Rainbow");
			switch  ( dlg.open() ) {
			case SWT.NO:
				return true;
			case SWT.CANCEL:
				return false;
			}
		}*/
		Util.deleteDirectory(outputDir, false);
		
		id = 0;
		writer.setParameters(sourceLanguage, targetLanguage,
			"TODO:projectID", outputDir, params.makePackageID(), inputRoot);
		writer.writeStartPackage();
	}

	public IParameters getParameters () {
		return params;
	}

	public String getInputRoot () {
		return inputRoot;
	}

	public String getOutputRoot () {
		return outputRoot;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean needsRoots () {
		return true;
	}

	public boolean needsOutputFilter () {
		// This utility does not re-write the input
		return false;
	}
	
	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}

	public void setRoots (String inputRoot,
		String outputRoot)
	{
		if ( inputRoot == null ) throw new NullPointerException();
		if ( outputRoot == null ) throw new NullPointerException();
		this.inputRoot = inputRoot;
		this.outputRoot = outputRoot;
	}

	@Override
    public void startResource (Document resource) {
		if ( qm != null ) {
			qm.setAttribute("FileName", Util.getFilename(resource.getName(), true));
		}
		
		String relativeInput = inputPath.substring(inputRoot.length()+1);
		String relativeOutput = outputPath.substring(outputRoot.length()+1);
		String[] res = FilterAccess.splitFilterSettingsType1("", resource.getFilterSettings());
		writer.createDocument(++id, relativeInput, relativeOutput,
			resource.getSourceEncoding(), resource.getTargetEncoding(),
			res[1], resource.getParameters());
		writer.writeStartDocument(resource);
    }
	
	@Override
    public void endResource (Document resource) {
		writer.writeEndDocument(resource);
	}
	
	@Override
    public void startExtractionItem (TextUnit item) {
	}
	
	@Override
    public void endExtractionItem (TextUnit item ) {
		//TODO: int status = IFilterItem.TSTATUS_TOTRANS;
		//if ( !sourceItem.isTranslatable() ) status = IFilterItem.TSTATUS_NOTRANS;
		//else if ( sourceItem.hasTarget() ) status = IFilterItem.TSTATUS_TOEDIT;

		if ( params.includeTargets ) {
			//TODO: Find a solution to not output item with
			// existing target
		}
		
		if (( params.preSegment ) && !"no".equals(item.getProperty("canSegment")) ) {
			try {
				TextContainer cont;
				cont = item.getSourceContent();
				sourceSeg.computeSegments(cont);
				cont.createSegments(sourceSeg.getSegmentRanges());
				if ( item.hasTarget() ) {
					cont = item.getTargetContent();
					targetSeg.computeSegments(cont);
					cont.createSegments(targetSeg.getSegmentRanges());
				}
			}
			catch ( Throwable e ) {
				logger.error(String.format("Error segmenting text unit id=%s: "
					+e.getMessage(), item.getID()));
			}
		}
		
		// Leverage if requested
		if ( qm != null ) {
			qm.setAttribute("GroupName", item.getName());
			qm.leverage(item);
		}
		
		//TODO: Status
		writer.writeTextUnit(item, 0);
	}
    
	@Override
    public void startContainer (Group resource) {
	}

	@Override
	public void endContainer (Group resource) {
	}

	@Override
	public void skeletonContainer (SkeletonUnit resource) {
		writer.writeSkeletonUnit(resource);
	}
	
	public boolean isFilterDriven () {
		return true;
	}

	public void addInputData (String path,
		String encoding,
		String filterSettings)
	{
		if ( path == null ) throw new NullPointerException();
		inputPath = path;
	}

	public void addOutputData (String path,
		String encoding)
	{
		if ( path == null ) throw new NullPointerException();
		// Not used: if ( encoding == null ) throw new NullPointerException();
		outputPath = path;
		// Not used: outputEncoding = encoding;
	}

	public int getInputCount () {
		return 1;
	}

	public String getFolderAfterProcess () {
		return params.outputFolder;
	}

}
