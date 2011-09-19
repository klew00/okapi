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

package net.sf.okapi.lib.extra.pipelinebuilder;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.pipelinedriver.DocumentData;
import net.sf.okapi.common.resource.RawDocument;

public class XDocument  {

	private DocumentData documentData;
	private FilterConfigurationMapper fcMapper;
		
	{
		fcMapper = new FilterConfigurationMapper();
	
		// Used in XLIFFKitReaderTest
		// TODO Registration of filter configs in the FilterConfigurationMapper, not here
		fcMapper.addConfigurations("net.sf.okapi.filters.xml.XMLFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.html.HtmlFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.openoffice.OpenOfficeFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.openxml.OpenXMLFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.openxml.OpenXMLContentFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.properties.PropertiesFilter");						
		fcMapper.addConfigurations("net.sf.okapi.filters.plaintext.PlainTextFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.table.TableFilter");
	}
	
	public XDocument(URI inputURI, String defaultEncoding, String filterConfigId,
			URI outputURI, String outputEncoding, LocaleId sourceLocale, LocaleId targetLocale) {
		this(new RawDocument(inputURI, defaultEncoding, sourceLocale,	targetLocale), outputURI, outputEncoding);
		getRawDocument().setFilterConfigId(filterConfigId);
	}
	
	public XDocument(RawDocument rawDocument) {
		setRawDocument(rawDocument);
	}
	
	public XDocument(DocumentData documentData) {
		setDocumentData(documentData);
	}
	
	public XDocument(RawDocument rawDoc,
			URI outputURI,
			String outputEncoding) {
		setRawDocument(rawDoc);
		documentData.outputURI = outputURI;
		documentData.outputEncoding = outputEncoding;
	}
	
	public XDocument(CharSequence inputCharSequence, LocaleId sourceLocale) {
		setRawDocument(new RawDocument(inputCharSequence, sourceLocale));
	}
	
	public XDocument(URI inputURI, String defaultEncoding, LocaleId sourceLocale) {
		setRawDocument(new RawDocument(inputURI, defaultEncoding, sourceLocale));
	}

	public XDocument(URI inputURI, String defaultEncoding, URI outputURI, String outputEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		this(inputURI, defaultEncoding, null, outputURI, outputEncoding, sourceLocale, targetLocale);
	}
	
	public XDocument(URI inputURI, String defaultEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		setRawDocument(new RawDocument(inputURI, defaultEncoding, sourceLocale,	targetLocale));
	}
	
	public XDocument(URL inputURL, String defaultEncoding, LocaleId sourceLocale) {
		try {
			setRawDocument(new RawDocument(inputURL.toURI(), defaultEncoding, sourceLocale));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public XDocument(URL inputURL, String defaultEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		setRawDocument(new RawDocument(Util.toURI(inputURL.getPath()), defaultEncoding, sourceLocale,	targetLocale));
	}
	
	public XDocument(URL inputURL, String defaultEncoding, URL outputURL, String outputEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		try {
			setRawDocument(new RawDocument(Util.toURI(inputURL.getPath()), defaultEncoding, sourceLocale,	targetLocale));
			documentData.outputURI = (outputURL == null) ? null : outputURL.toURI();
			documentData.outputEncoding = outputEncoding;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block Util.toURI(inputURL.getPath()).getPath()
			e.printStackTrace();
		}
	}
	
	public XDocument(URL inputURL, String defaultEncoding, String outputPath, String outputEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
			setRawDocument(new RawDocument(Util.toURI(inputURL.getPath()), defaultEncoding, sourceLocale,	targetLocale));
			documentData.outputURI = Util.toURI(outputPath);
			documentData.outputEncoding = outputEncoding;			
	}
	
	public XDocument(InputStream inputStream, String defaultEncoding, LocaleId sourceLocale) {
		setRawDocument(new RawDocument(inputStream, defaultEncoding, sourceLocale));
	}
	
	public XDocument(InputStream inputStream, String defaultEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		setRawDocument(new RawDocument(inputStream, defaultEncoding, sourceLocale, targetLocale));
	}
	
	protected XDocument() {
		super();
	}

	public DocumentData getDocumentData() {
		return documentData;
	}

	public void setDocumentData(DocumentData documentData) {
		this.documentData = documentData;
		validateFilterConfigId();
	}

	public RawDocument getRawDocument() {
		return (documentData != null) ? documentData.rawDocument : null;
	}

	public void setRawDocument(RawDocument rawDocument) {
		if (documentData == null)
			documentData = new DocumentData();
		
		documentData.rawDocument = rawDocument;
		validateFilterConfigId();
	}
	
	private void validateFilterConfigId() {
		RawDocument rd = getRawDocument();
		if (rd == null) return;
		if (!Util.isEmpty(rd.getFilterConfigId())) return; // Already set
		if (rd.getInputURI() == null) return; 
		
		String ext = Util.getExtension(rd.getInputURI().toString());
		if (Util.isEmpty(ext)) return;
		
		ext = ext.substring(1); // Exclude leading dot
		String mimeType = MimeTypeMapper.getMimeType(ext);
		
		FilterConfiguration cfg = fcMapper.getDefaultConfiguration(mimeType);
		if (cfg == null) return;
		
		rd.setFilterConfigId(cfg.configId);
	}
	
}
