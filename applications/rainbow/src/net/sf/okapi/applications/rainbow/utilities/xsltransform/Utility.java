/*===========================================================================*/
/* Copyright (C) 2008 By the Okapi Framework contributors                    */
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

package net.sf.okapi.applications.rainbow.utilities.xsltransform;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.event.EventListenerList;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.utilities.CancelListener;
import net.sf.okapi.applications.rainbow.utilities.ISimpleUtility;
import net.sf.okapi.common.ConfigurationString;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;

public class Utility implements ISimpleUtility {

	private String srcLang;
	private String trgLang;
	private Source xsltInput;
	private ArrayList<String> inputPaths;
	private ArrayList<String> outputEncodings;
	private ArrayList<String> outputPaths;
	private Hashtable<String, String> paramList;
	private Transformer trans;
	private Parameters params;
	private String commonFolder;
	private EventListenerList listenerList = new EventListenerList();
	
	public Utility () {
		params = new Parameters();
	}
	
	public void resetLists () {
		inputPaths = new ArrayList<String>();
		outputEncodings = new ArrayList<String>();
		outputPaths = new ArrayList<String>();
	}
	
	public String getID () {
		return "oku_xsltransform"; //$NON-NLS-1$
	}
	
	public void processInput () {
		try {
			trans.reset();
			fillParameters();
			// Create the source for the XML input
			Source xmlInput = new javax.xml.transform.stream.StreamSource(
				new File(inputPaths.get(0)));
			// Create the output
			Result result = new javax.xml.transform.stream.StreamResult(
				new File(outputPaths.get(0)));
			trans.transform(xmlInput, result);
		}
		catch ( TransformerException e ) {
			throw new RuntimeException(Res.getString("utility.errorTransforming"), e); //$NON-NLS-1$
		}
	}

	public void doEpilog () {
		// Not used in this utility.
	}

	public void doProlog (String sourceLanguage,
		String targetLanguage)
	{
		try {
			commonFolder = null; // Reset
			// Store the languages
			srcLang = sourceLanguage;
			trgLang = targetLanguage;
			
			ConfigurationString cfgString = new ConfigurationString(
				params.paramList);
			paramList = cfgString.toMap();
			
			// Create the source for the XSLT
			xsltInput = new javax.xml.transform.stream.StreamSource(
				new File(params.xsltPath));
			
			// Create an instance of TransformerFactory
			javax.xml.transform.TransformerFactory fact =
				javax.xml.transform.TransformerFactory.newInstance();
			trans = fact.newTransformer(xsltInput);
		}
		catch ( TransformerConfigurationException e ) {
			throw new RuntimeException(Res.getString("utility.errorInXSLT"), e); //$NON-NLS-1$
		}
	}

	public String getInputRoot () {
		// Not used in this utility.
		return null;
	}

	public String getOutputRoot () {
		// Not used in this utility.
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

	public void addInputData (String path,
		String encoding,
		String filterSettings)
	{
		// Add the info to the list
		inputPaths.add(path);
	}

	public void addOutputData (String path,
		String encoding)
	{
		// Compute the longest common folder
		if ( path != null ) {
			commonFolder = Util.longestCommonDir(commonFolder,
				Util.getDirectoryName(path), !Util.isOSCaseSensitive());
		}
		// Add the info to the list
		outputPaths.add(path);
		outputEncodings.add(encoding);
	}

	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}

	public void setRoots (String inputRoot,
		String outputRoot)
	{
		// Not used in this utility.
	}

	private void fillParameters () {
		trans.clearParameters();
		String value;
		for ( String key : paramList.keySet() ) {
			value = paramList.get(key).replace("<$SrcLang>", srcLang); //$NON-NLS-1$
			value = value.replace("<$TrgLang>", trgLang); //$NON-NLS-1$
			value = value.replace("<$Input1>", Util.makeURIFromPath(inputPaths.get(0))); //$NON-NLS-1$
			value = value.replace("<$Output1>", Util.makeURIFromPath(outputPaths.get(0))); //$NON-NLS-1$
			if ( inputPaths.get(1) != null ) {
				value = value.replace("<$Input2>", Util.makeURIFromPath(inputPaths.get(1))); //$NON-NLS-1$
				value = value.replace("<$Output2>", Util.makeURIFromPath(outputPaths.get(1))); //$NON-NLS-1$
			}
			if ( inputPaths.get(2) != null ) {
				value = value.replace("<$Input3>", Util.makeURIFromPath(inputPaths.get(2))); //$NON-NLS-1$
				value = value.replace("<$Output3>", Util.makeURIFromPath(outputPaths.get(2))); //$NON-NLS-1$
			}
			trans.setParameter(key, value);
		}
	}

	public int getInputCount () {
		return 3; // between 1 and 3, depending on the template
	}

	public String getFolderAfterProcess () {
		return commonFolder;
	}

	public void setFilterAccess (FilterAccess filterAccess,
		String paramsFolder)
	{
		// Not used
	}

	public void setContextUI (Object contextUI) {
		// Not used
	}

	public void addCancelListener (CancelListener listener) {
		listenerList.add(CancelListener.class, listener);
	}

	public void removeCancelListener (CancelListener listener) {
		listenerList.remove(CancelListener.class, listener);
	}

}
