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

package net.sf.okapi.steps.xliffkit.sandbox.pipelinebuilder;

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

public class BatchItem  {

	private DocumentData documentData;
	private FilterConfigurationMapper fcMapper;
		
	{
		fcMapper = new FilterConfigurationMapper();
	
		// TODO Registration of filter configs in the FilterConfigurationMapper, not here
		fcMapper.addConfigurations("net.sf.okapi.filters.xml.XMLFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.html.HtmlFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.openoffice.OpenOfficeFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.openxml.OpenXMLFilter");
		fcMapper.addConfigurations("net.sf.okapi.filters.properties.PropertiesFilter");
	}
	
	public BatchItem(URI inputURI, String defaultEncoding, String filterConfigId,
			URI outputURI, String outputEncoding, LocaleId sourceLocale, LocaleId targetLocale) {
		this(new RawDocument(inputURI, defaultEncoding, sourceLocale,	targetLocale), outputURI, outputEncoding);
		getRawDocument().setFilterConfigId(filterConfigId);
	}
	
	public BatchItem(RawDocument rawDocument) {
		setRawDocument(rawDocument);
	}
	
	public BatchItem(DocumentData documentData) {
		setDocumentData(documentData);
	}
	
	public BatchItem(RawDocument rawDoc,
			URI outputURI,
			String outputEncoding) {
		setRawDocument(rawDoc);
		documentData.outputURI = outputURI;
		documentData.outputEncoding = outputEncoding;
	}
	
	public BatchItem(CharSequence inputCharSequence, LocaleId sourceLocale) {
		setRawDocument(new RawDocument(inputCharSequence, sourceLocale));
	}
	
	public BatchItem(URI inputURI, String defaultEncoding, LocaleId sourceLocale) {
		setRawDocument(new RawDocument(inputURI, defaultEncoding, sourceLocale));
	}

	public BatchItem(URI inputURI, String defaultEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		setRawDocument(new RawDocument(inputURI, defaultEncoding, sourceLocale,	targetLocale));
	}
	
	public BatchItem(URL inputURL, String defaultEncoding, LocaleId sourceLocale) {
		try {
			setRawDocument(new RawDocument(inputURL.toURI(), defaultEncoding, sourceLocale));
		} catch (URISyntaxException e) {
			// TODO Handle exception
		}
	}

	public BatchItem(URL inputURL, String defaultEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		try {
			setRawDocument(new RawDocument(inputURL.toURI(), defaultEncoding, sourceLocale,	targetLocale));
		} catch (URISyntaxException e) {
			// TODO Handle exception
		}
	}
	
	public BatchItem(InputStream inputStream, String defaultEncoding, LocaleId sourceLocale) {
		setRawDocument(new RawDocument(inputStream, defaultEncoding, sourceLocale));
	}
	
	public BatchItem(InputStream inputStream, String defaultEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		setRawDocument(new RawDocument(inputStream, defaultEncoding, sourceLocale, targetLocale));
	}
	
	protected BatchItem() {
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
		
		String ext = Util.getExtension(rd.getInputURI().toString());
		if (Util.isEmpty(ext)) return;
		
		ext = ext.substring(1); // Exclude leading dot
		String mimeType = MimeTypeMapper.getMimeType(ext);
		
		FilterConfiguration cfg = fcMapper.getDefaultConfiguration(mimeType);
		if (cfg == null) return;
		
		rd.setFilterConfigId(cfg.configId);
	}
	
}
