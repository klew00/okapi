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
import java.io.IOException;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import net.sf.okapi.common.ConfigurationString;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.resource.RawDocument;

public class XSLTransformStep implements IPipelineStep {

	private Parameters params;
	private Source xsltInput;
	private Map<String, String> paramList;
	private Transformer trans;
	
	public XSLTransformStep () {
		params = new Parameters();
		trans = null;
	}
	
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

	public IParameters getParameters () {
		return params;
	}

	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_DOCUMENT:
			if ( trans == null ) startBatch();
			break;
		case CANCELED:
		case FINISHED:
			trans = null; // Reset for next batch
			break;
		case RAW_DOCUMENT:
			processRawDocument(event);
			break;
		}
		return event;
	}

	public boolean hasNext () {
		return false;
	}

	public void postprocess () {
		// Nothing to do
	}

	public void preprocess () {
		// Nothing to do
	}

	public void setParameters (IParameters params) {
		params = (Parameters)params;
	}
 
	private void startBatch () {
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
		}
		catch ( TransformerConfigurationException e ) {
			throw new RuntimeException("Error in XSLT input", e);
		}
	}

	public void processRawDocument (Event event) {
		try {
			RawDocument rawDoc = (RawDocument)event.getResource();
			trans.reset();
			fillParameters();
			
			// Create input source
			Source xmlInput = new javax.xml.transform.stream.StreamSource(
				rawDoc.getReader());
			
			// Create the output
			//TODO: Need to use output provided by the pipeline if this is last step!
			File tmpOut;
			try {
				tmpOut = File.createTempFile("okptmp_", ".xml");
			}
			catch ( IOException e ) {
				throw new OkapiIOException("Cannot create temporary output.", e);
			}
			Result result = new javax.xml.transform.stream.StreamResult(tmpOut);
			
			// Apply the template
			trans.transform(xmlInput, result);
			
			// Create the new raw-document resource
			event.setResource(new RawDocument(tmpOut.toURI(), "UTF-8",
				rawDoc.getSourceLanguage(), rawDoc.getTargetLanguage()));
		}
		catch ( TransformerException e ) {
			throw new RuntimeException("Transformation error.", e);
		}
	}

	private void fillParameters () {
		trans.clearParameters();
		String value;
		for ( String key : paramList.keySet() ) {
/*TODO			value = paramList.get(key).replace("${SrcLang}", srcLang); //$NON-NLS-1$
			value = value.replace("${TrgLang}", trgLang); //$NON-NLS-1$
			value = value.replace("${Input1}", Util.makeURIFromPath(getInputPath(0))); //$NON-NLS-1$
			value = value.replace("${Output1}", Util.makeURIFromPath(getOutputPath(0))); //$NON-NLS-1$
			if ( getInputPath(1) != null ) {
				value = value.replace("${Input2}", Util.makeURIFromPath(getInputPath(1))); //$NON-NLS-1$
				value = value.replace("${Output2}", Util.makeURIFromPath(getOutputPath(1))); //$NON-NLS-1$
			}
			if ( getInputPath(2) != null ) {
				value = value.replace("${Input3}", Util.makeURIFromPath(getInputPath(2))); //$NON-NLS-1$
				value = value.replace("${Output3}", Util.makeURIFromPath(getOutputPath(2))); //$NON-NLS-1$
			}*/
			value = paramList.get(key);
			trans.setParameter(key, value);
		}
	}

}
