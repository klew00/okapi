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

import net.sf.okapi.applications.rainbow.lib.TMXWriter;
import net.sf.okapi.applications.rainbow.utilities.ISimpleUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
//import net.sf.okapi.common.filters.IParser;
import net.sf.okapi.lib.segmentation.SRXDocument;

public class Utility implements ISimpleUtility {

	private TMXWriter   writer;
	private Parameters  params;
	private String      lastFolder;
	private String      sourcePath;
//	private String      sourceEncoding;
//	private String      sourceFilterSettings;
	private String      targetPath;
//	private String      targetEncoding;
//	private String      targetFilterSettings;
//	private IParser     srcParser;
	
	
	public void processInput () {
		
		// Open the second input if required
		if ( !params.singleInput ) {
			if ( targetPath == null ) {
				throw new RuntimeException("Unspecified target file. You must specify a target input file for each source input file, or use the single input option.");
			}
		}

/*		if ( item.hasTarget() ) {
			if ( !item.getTarget().isEmpty() ) {
				/*TODO: if ( params.segment ) {
					srcSegmenter.segment(item.getSource());
					trgSegmenter.segment(item.getTarget());
				}
				writer.writeItem(item);
			}
		}
		*/
	}
	
	public void addInputData (String path,
		String encoding,
		String filterSettings)
	{
		if ( sourcePath == null ) {
			sourcePath = path;
//			sourceEncoding = encoding;
//			sourceFilterSettings = filterSettings;
		}
		else {
			targetPath = path;
//			targetEncoding = encoding;
//			targetFilterSettings = filterSettings;
		}
	}

	public void addOutputData (String path,
		String encoding)
	{
		// Not used for this utility
	}

	public void doEpilog () {
		
		writer.writeEndDocument();
		writer.close();
	}

	public void doProlog (String sourceLanguage,
		String targetLanguage)
	{
		lastFolder = null;
		writer.create(params.tmxPath);
		writer.writeStartDocument(sourceLanguage, targetLanguage);
			
		if ( params.segment ) {
			SRXDocument doc = new SRXDocument();
			doc.loadRules(params.srxPath);
			//TODO: srcSegmenter = doc.applyLanguageRules(sourceLanguage, null);
			//TODO: trgSegmenter = doc.applyLanguageRules(targetLanguage, null);
		}
		lastFolder = Util.getDirectoryName(params.tmxPath);
	}

	public String getFolderAfterProcess () {
		return lastFolder;
	}

	public String getID () {
		return "oku_alignment";
	}
	
	public int getInputCount () {
		return 2;
	}
	
	public String getInputRoot () {
		return null;
	}

	public String getOutputRoot () {
		return null;
	}

	public IParameters getParameters () {
		return params;
	}
	
	public boolean hasParameters () {
		return true;
	}
	
	public boolean isFilterDriven () {
		return false;
	}

	public boolean needsRoots () {
		return false;
	}

	public void resetLists () {
		// Not used for this utility
	}
	
	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;		
	}
	
	public void setRoots (String inputRoot,
		String outputRoot)
	{
		// Not used for this utility
	}

}
