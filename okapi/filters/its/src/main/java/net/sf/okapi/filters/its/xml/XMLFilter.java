/*===========================================================================
  Copyright (C) 2008-2012 by the Okapi Framework contributors
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

package net.sf.okapi.filters.its.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.DefaultEntityResolver;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.its.ITSFilter;
import net.sf.okapi.filters.its.Parameters;

import org.w3c.dom.DocumentType;
import org.w3c.its.IProcessor;
import org.w3c.its.ITSEngine;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@UsingParameters(Parameters.class)
public class XMLFilter extends ITSFilter {

	public XMLFilter () {
		super(false, MimeTypeMapper.XML_MIME_TYPE);
	}

	@Override
	public String getName () {
		return "okf_itsxml";
	}
	
	@Override
	public String getDisplayName () {
		return "XML+ITS Filter";
	}

	@Override
	public String getMimeType () {
		return MimeTypeMapper.XML_MIME_TYPE;
	}

	@Override
	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			getMimeType(),
			getClass().getName(),
			"Generic XML",
			"Configuration for generic XML documents (default ITS rules).",
			null,
			".xml;"));
		list.add(new FilterConfiguration(getName()+"-resx",
			getMimeType(),
			getClass().getName(),
			"RESX",
			"Configuration for Microsoft RESX documents (without binary data).",
			"resx.fprm",
			".resx;"));
		list.add(new FilterConfiguration(getName()+"-MozillaRDF",
			getMimeType(),
			getClass().getName(),
			"Mozilla RDF",
			"Configuration for Mozilla RDF documents.",
			"MozillaRDF.fprm",
			".rdf;"));
		list.add(new FilterConfiguration(getName()+"-JavaProperties",
			getMimeType(),
			getClass().getName(),
			"Java Properties XML",
			"Configuration for Java Properties files in XML.",
			"JavaProperties.fprm"));
		list.add(new FilterConfiguration(getName()+"-AndroidStrings",
			getMimeType(),
			getClass().getName(),
			"Android Strings",
			"Configuration for Android Strings XML documents.",
			"AndroidStrings.fprm"));
		list.add(new FilterConfiguration(getName()+"-WixLocalization",
			getMimeType(),
			getClass().getName(),
			"WiX Localization",
			"Configuration for WiX (Windows Installer XML) Localization files.",
			"WixLocalization.fprm",
			".wxl;"));
		return list;
	}
	
	@Override
	public EncoderManager getEncoderManager () {
		if ( encoderManager == null ) {
			encoderManager = new EncoderManager();
			encoderManager.setMapping(getMimeType(), "net.sf.okapi.common.encoder.XMLEncoder");
		}
		return encoderManager;
	}

	@Override
	protected void initializeDocument () {
		// Create the document builder factory
		DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
		fact.setNamespaceAware(true);
		fact.setValidating(false);
		// Expand entity references only if we do not protect them
		// "Expand entity" means don't have ENTITY_REFERENCE
		fact.setExpandEntityReferences(!params.protectEntityRef);
		
		// Create the document builder
		DocumentBuilder docBuilder;
		try {
			docBuilder = fact.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw new OkapiIOException(e);
		}
		//TODO: Do this only as an option
		// Avoid DTD declaration
		docBuilder.setEntityResolver(new DefaultEntityResolver());

		input.setEncoding("UTF-8"); // Default for XML, other should be auto-detected
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

	protected void applyRules (ITSEngine itsEng) {
		// (Applies only the ones used by the filter
		itsEng.applyRules(IProcessor.DC_TRANSLATE | IProcessor.DC_IDVALUE
			| IProcessor.DC_LOCNOTE | IProcessor.DC_WITHINTEXT | IProcessor.DC_TERMINOLOGY
			| IProcessor.DC_DOMAIN | IProcessor.DC_TARGETPOINTER | IProcessor.DC_EXTERNALRES
			| IProcessor.DC_LOCFILTER | IProcessor.DC_PRESERVESPACE | IProcessor.DC_LOCQUALITYISSUE
			| IProcessor.DC_STORAGESIZE);
	}
	
	@Override
	protected void createStartDocumentSkeleton (StartDocument startDoc) {
		// Add the XML declaration
		skel = new GenericSkeleton();
		if ( !params.omitXMLDeclaration ) {
			skel.add("<?xml version=\"" + doc.getXmlVersion() + "\"");
			skel.add(" encoding=\"");
			skel.addValuePlaceholder(startDoc, Property.ENCODING, LocaleId.EMPTY);
			skel.add("\"");
			startDoc.setProperty(new Property(Property.ENCODING, encoding, false));
			if ( doc.getXmlStandalone() ) skel.add(" standalone=\"yes\"");
			skel.add("?>"+lineBreak);
		}
		// Add the DTD if needed
		DocumentType dt = doc.getDoctype();
		if ( dt != null ) {
			rebuildDocTypeSection(dt);
		}
	}

	private void rebuildDocTypeSection (DocumentType dt) {
		StringBuilder tmp = new StringBuilder();
		// Set the start syntax
		if ( dt.getPublicId() != null ) {
			tmp.append(String.format("<!DOCTYPE %s PUBLIC \"%s\" \"%s\"",
				dt.getName(),
				dt.getPublicId(),
				dt.getSystemId()));
		}
		else if ( dt.getSystemId() != null ) {
			tmp.append(String.format("<!DOCTYPE %s SYSTEM \"%s\"",
				dt.getName(),
				dt.getSystemId()));
		}
		else if ( dt.getInternalSubset() != null ) {
			tmp.append(String.format("<!DOCTYPE %s",
				dt.getName()));
		}
		
		// Add the internal sub-set if there is any
		if ( dt.getInternalSubset() != null ) {
			tmp.append(" [");
			tmp.append(dt.getInternalSubset().replace("\n", lineBreak));
			tmp.append("]");
		}
		
		if ( tmp.length() > 0 ) {
			tmp.append(">"+lineBreak);
			skel.add(tmp.toString());
		}
	}
	
}
