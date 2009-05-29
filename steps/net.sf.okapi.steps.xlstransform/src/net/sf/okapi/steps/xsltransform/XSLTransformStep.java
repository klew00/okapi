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

package net.sf.okapi.steps.xsltransform;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import net.sf.okapi.common.ConfigurationString;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.RawDocument;

public class XSLTransformStep extends BasePipelineStep {

	private final Logger logger = Logger.getLogger(getClass().getName());

	private Parameters params;
	private Source xsltInput;
	private Map<String, String> paramList;
	private Transformer trans;
	private boolean isDone;
	
	public XSLTransformStep () {
		params = new Parameters();
		trans = null;
	}

	@Override
	public void destroy () {
		// Make available to GC
		trans = null;
		xsltInput = null;
	}

	public String getDescription () {
		return "Apply an XSLT template to a raw document.";
	}

	public String getName () {
		return "XSLT Transformation";
	}

	@Override
	public IParameters getParameters () {
		return params;
	}
	
	@Override
	public boolean isDone () {
		return isDone;
	}

	@Override
	public void setParameters (IParameters params) {
		params = (Parameters)params;
	}
 
	@Override
	public boolean needsOutput (int inputIndex) {
		return pipeline.isLastStep(this);
	}
	
	@Override
	protected void handleStartBatch (Event event) {
		try {
			// Create the parameters map
			ConfigurationString cfgString = new ConfigurationString(params.paramList);
			paramList = cfgString.toMap();
			
			// Create the source for the XSLT
			xsltInput = new javax.xml.transform.stream.StreamSource(
				new File(params.xsltPath)); //TODO: .replace(VAR_PROJDIR, projectDir)));
			
			// Create an instance of TransformerFactory
			javax.xml.transform.TransformerFactory fact =
				javax.xml.transform.TransformerFactory.newInstance();
			trans = fact.newTransformer(xsltInput);
			isDone = true;
		}
		catch ( TransformerConfigurationException e ) {
			throw new OkapiIOException("Error in XSLT input.", e);
		}
	}
	
	@Override
	protected void handleStartBatchItem (Event event) {
		isDone = false;
	}

	@Override
	protected void handleRawDocument (Event event) {
		try {
			RawDocument rawDoc = (RawDocument)event.getResource();
			trans.reset();
			fillParameters();
			
			Properties props = trans.getOutputProperties();
			for ( Object obj: props.keySet() ) {
				String key = (String)obj;
				String value = props.getProperty(key);
				value = value+"";
			}
			
			// Create the input source
			Source xmlInput = new javax.xml.transform.stream.StreamSource(rawDoc.getReader());
			
			// Create the output
			File outFile;
			if ( pipeline.isLastStep(this) ) {
				outFile = new File(getContext().getOutputURI(0));
				Util.createDirectories(outFile.getAbsolutePath());
			}
			else {
				try {
					outFile = File.createTempFile("okp-xslt_", ".tmp");
				}
				catch ( Throwable e ) {
					throw new OkapiIOException("Cannot create temporary output.", e);
				}
				outFile.deleteOnExit();
			}
			
			Result result = new javax.xml.transform.stream.StreamResult(outFile);
			
			// Apply the template
			trans.transform(xmlInput, result);
			
			// Create the new raw-document resource
			// Other info stays the same
			rawDoc.setInputURI(outFile.toURI());
			rawDoc.setEncoding("UTF-8"); // Just so we have a default
		}
		catch ( TransformerException e ) {
			throw new OkapiIOException("Transformation error.", e);
		}
		finally {
			isDone = true;
		}
	}

	private void fillParameters () {
		trans.clearParameters();
		String value = null;
		try {
			for ( String key : paramList.keySet() ) {
				value = paramList.get(key).replace("${SrcLang}", getContext().getSourceLanguage(0)); //$NON-NLS-1$
				if ( value.indexOf("${Input1}") > -1 ) {
					value = value.replace("${Input1}", getContext().getRawDocument(0).getInputURI().toString()); //$NON-NLS-1$
				}
				if ( value.indexOf("${TrgLang}") > -1 ) {
					value = value.replace("${TrgLang}", getContext().getTargetLanguage(0)); //$NON-NLS-1$
				}
				if ( value.indexOf("${Output1}") > -1 ) {
					value = value.replace("${Output1}", getContext().getOutputURI(0).toString()); //$NON-NLS-1$
				}
				if ( value.indexOf("${Input2}") > -1 ) {
					value = value.replace("${Input2}", getContext().getRawDocument(1).getInputURI().toString()); //$NON-NLS-1$
				}
				if ( value.indexOf("${Output2}") > -1 ) {
					value = value.replace("${Output2}", getContext().getOutputURI(1).toString()); //$NON-NLS-1$
				}
				if ( value.indexOf("${Input3}") > -1 ) {
					value = value.replace("${Input3}", getContext().getRawDocument(2).getInputURI().toString()); //$NON-NLS-1$
				}
				if ( value.indexOf("${Output3}") > -1 ) {
					value = value.replace("${Output3}", getContext().getOutputURI(2).toString()); //$NON-NLS-1$
				}
				value = paramList.get(key);
				trans.setParameter(key, value);
			}
		}
		catch ( Throwable e ) {
			logger.severe(String.format("Error when trying to substitute variables in the parameter value '%s'", value));
		}
	}

}
