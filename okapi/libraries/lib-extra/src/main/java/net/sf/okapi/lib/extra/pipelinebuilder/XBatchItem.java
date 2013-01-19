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
import java.net.URL;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipelinedriver.BatchItemContext;
import net.sf.okapi.common.pipelinedriver.DocumentData;
import net.sf.okapi.common.pipelinedriver.IBatchItemContext;
import net.sf.okapi.common.resource.RawDocument;

public class XBatchItem  {

	private BatchItemContext bic = new BatchItemContext();
//	private List<XDocument> documents = new ArrayList<XDocument>();

	public XBatchItem(XDocument... documents) {
		for (XDocument document : documents)
			addDocument(document);
	}

	private void addDocument(XDocument document) {
		if (document == null) return;		
		bic.add(document.getDocumentData());
	}
	
	public XBatchItem(URI inputURI, String defaultEncoding, String filterConfigId,
			URI outputURI, String outputEncoding, LocaleId sourceLocale, LocaleId targetLocale) {
		addDocument(new XDocument(inputURI, defaultEncoding, filterConfigId,
			outputURI, outputEncoding, sourceLocale, targetLocale));		
	}
	
	public XBatchItem(RawDocument rawDocument) {
		addDocument(new XDocument(rawDocument));
	}
	
	public XBatchItem(DocumentData documentData) {
		addDocument(new XDocument(documentData));
	}
	
	public XBatchItem(RawDocument rawDoc,
			URI outputURI,
			String outputEncoding) {
		addDocument(new XDocument(rawDoc, outputURI, outputEncoding));
	}
	
	public XBatchItem(CharSequence inputCharSequence, LocaleId sourceLocale) {
		addDocument(new XDocument(inputCharSequence, sourceLocale));
	}
	
	public XBatchItem(URI inputURI, String defaultEncoding, LocaleId sourceLocale) {
		addDocument(new XDocument(inputURI, defaultEncoding, sourceLocale));
	}

	public XBatchItem(URI inputURI, String defaultEncoding, URI outputURI, String outputEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		addDocument(new XDocument(inputURI, defaultEncoding, outputURI, outputEncoding, sourceLocale,
				targetLocale));
	}
	
	public XBatchItem(URI inputURI, String defaultEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		addDocument(new XDocument(inputURI, defaultEncoding, sourceLocale, targetLocale));
	}
	
	public XBatchItem(URL inputURL, String defaultEncoding, LocaleId sourceLocale) {
		addDocument(new XDocument(inputURL, defaultEncoding, sourceLocale));
	}

	public XBatchItem(URL inputURL, String defaultEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		addDocument(new XDocument(inputURL, defaultEncoding, sourceLocale, targetLocale));
	}
	
	public XBatchItem(URL inputURL, String defaultEncoding, URL outputURL, String outputEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		addDocument(new XDocument(inputURL, defaultEncoding, outputURL, outputEncoding, sourceLocale,
				targetLocale));
	}
	
	public XBatchItem(URL inputURL, String defaultEncoding, String outputPath, String outputEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		addDocument(new XDocument(inputURL, defaultEncoding, outputPath, outputEncoding, sourceLocale,
				targetLocale));
	}
	
	public XBatchItem(InputStream inputStream, String defaultEncoding, LocaleId sourceLocale) {
		addDocument(new XDocument(inputStream, defaultEncoding, sourceLocale));
	}
	
	public XBatchItem(InputStream inputStream, String defaultEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		addDocument(new XDocument(inputStream, defaultEncoding, sourceLocale,
				targetLocale));
	}
	
	public IBatchItemContext getContext() {
		return bic;
	}
}
