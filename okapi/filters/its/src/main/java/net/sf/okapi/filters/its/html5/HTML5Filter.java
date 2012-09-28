/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.filters.its.html5;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiBadFilterParametersException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.its.ITSFilter;
import net.sf.okapi.filters.its.Parameters;
import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.its.IProcessor;
import org.w3c.its.ITSEngine;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@UsingParameters(Parameters.class)
public class HTML5Filter extends ITSFilter {

	public HTML5Filter () {
		super(true, MimeTypeMapper.HTML_MIME_TYPE);
		URL url = getClass().getResource("default.fprm");
		try {
			params.load(url.toURI(), false);
		}
		catch ( URISyntaxException e ) {
			throw new OkapiBadFilterParametersException("Cannot load default parameters.");
		}
	}

	@Override
	public String getName () {
		return "okf_itshtml5";
	}
	
	@Override
	public String getDisplayName () {
		return "HTML5+ITS Filter";
	}

	@Override
	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MimeTypeMapper.HTML_MIME_TYPE,
			getClass().getName(),
			"Standard HTML5",
			"Configuration for standard HTML5 documents.",
			"default.fprm",
			".html;.htm;"));
		return list;
	}
	
	@Override
	public EncoderManager getEncoderManager () {
		if ( encoderManager == null ) {
			encoderManager = new EncoderManager();
			encoderManager.setMapping(getMimeType(), "net.sf.okapi.common.encoder.HtmlEncoder");
		}
		return encoderManager;
	}
	
	@Override
	protected void initializeDocument () {
		input.setEncoding("UTF-8"); // Default for HTML5, other should be auto-detected
		BOMNewlineEncodingDetector detector = new BOMNewlineEncodingDetector(input.getStream(), input.getEncoding());
		detector.detectBom();
		
		if ( detector.isAutodetected() ) {
			encoding = detector.getEncoding();
			//--Start workaround issue with XML Parser
			// "UTF-16xx" are not handled as expected, using "UTF-16" alone 
			// seems to resolve the issue.
			if (( encoding.equals("UTF-16LE") ) || ( encoding.equals("UTF-16BE") )) {
				encoding = "UTF-16";
			}
			//--End workaround
			input.setEncoding(encoding);
		}
		
		HtmlDocumentBuilder docBuilder = new HtmlDocumentBuilder();
		try {
			InputSource is = new InputSource(input.getStream());
			//is.setEncoding(input.getEncoding());
			doc = docBuilder.parse(is);
		}
		catch ( SAXException e ) {
			throw new OkapiIOException("Error when parsing the document.\n"+e.getMessage(), e);
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error when reading the document.\n"+e.getMessage(), e);
		}

		encoding = doc.getXmlEncoding();
		if ( encoding == null ) {
			encoding = detector.getEncoding();
		}
		srcLang = input.getSourceLocale();
		if ( srcLang == null ) throw new NullPointerException("Source language not set.");
		hasUTF8BOM = detector.hasUtf8Bom();
		lineBreak = detector.getNewlineType().toString();
		if ( input.getInputURI() != null ) {
			docName = input.getInputURI().getPath();
		}

	}

	@Override
	protected void applyRules (ITSEngine itsEng) {
		// Check for links in the HTML5 document
		loadLinkedRules(doc, input.getInputURI(), itsEng);
		// Apply the rules (external and internal) to the document
		// (Applies only the ones used by the filter
		itsEng.applyRules(IProcessor.DC_TRANSLATE | IProcessor.DC_IDVALUE
			| IProcessor.DC_LOCNOTE | IProcessor.DC_WITHINTEXT | IProcessor.DC_TERMINOLOGY
			| IProcessor.DC_DOMAIN | IProcessor.DC_TARGETPOINTER | IProcessor.DC_EXTERNALRES
			| IProcessor.DC_LOCFILTER | IProcessor.DC_LOCQUALITYISSUE
			| IProcessor.DC_STORAGESIZE | IProcessor.DC_ALLOWEDCHARS);
	}

	@Override
	protected void createStartDocumentSkeleton (StartDocument startDoc) {
		// Add the XML declaration
		skel = new GenericSkeleton();
		skel.add("<!DOCTYPE html>"+lineBreak);
	}
	
	/**
	 * Loads the linked rules of an HTML document.
	 * @param doc the document to process.
	 * @param docURI the document URI.
	 * @param itsEng the engine to use.
	 */
	public static void loadLinkedRules (Document doc,
		URI docURI,
		ITSEngine itsEng)
	{
		String href = null;
		try {
			XPathExpression expr= itsEng.getXPath().compile("//"+ITSEngine.HTML_NS_PREFIX+":link[@rel='its-rules']");
			NodeList list = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			for ( int i=0; i<list.getLength(); i++ ) {
				Element elem = (Element)list.item(i);
				// get the HREF value (could be surrounded by spaces, so we trim)
				href = elem.getAttribute("href").trim();
				if (( href.indexOf('/') == -1 ) && ( href.indexOf('\\') == -1 )) {
					String base = itsEng.getPartBeforeFile(docURI);
					href = base + href;
				}
				itsEng.addExternalRules(new URI(href));
			}
		}
		catch ( Throwable e ) {
			throw new OkapiIOException(String.format(
				"Error trying to load external rules (%s).\n"+e.getMessage(), href));
		}
	}

}
