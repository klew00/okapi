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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.RawDocument;

public class XsltTransformStep extends BasePipelineStep {
	private InputStream xstlInputstream;
	private boolean done;
	
	public XsltTransformStep(InputStream xstlInputstream) {
		done = false;
		this.xstlInputstream = xstlInputstream;
	}

	public String getName() {
		return "XSLT Processing Step";
	}
	 
	public String getDescription () {
		return "Applies an XSLT template to the document.";
	}
	
	/**
	 * handler takes a 
	 */
	@Override
	protected Event handleRawDocument(Event event) {
		
		ByteArrayOutputStream tempStream = new ByteArrayOutputStream();

		// get the input xml and xslt streams
		Reader XmlInput = ((RawDocument)event.getResource()).getReader(); 
				
		Source xmlSource = new StreamSource(XmlInput);
		Source xsltSource = new StreamSource(xstlInputstream);

		Result result = new StreamResult(tempStream);

		TransformerFactory transFact = TransformerFactory.newInstance();
		Transformer trans;
		try {
			trans = transFact.newTransformer(xsltSource);
			// do the transformation
			trans.transform(xmlSource, result);
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}

		ByteArrayInputStream transformedInput = new ByteArrayInputStream(tempStream.toByteArray());
			
		// overwrite our event to the new transformed content	
		event.setResource(new RawDocument(transformedInput, "UTF-8", new LocaleId("en")));
		
		// This step is done generating events
		done = true;
		
		return event;
	}

	@Override
	public boolean isDone () {
		return done;
	}

}
