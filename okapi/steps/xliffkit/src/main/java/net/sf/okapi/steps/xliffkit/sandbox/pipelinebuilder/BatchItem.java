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
import java.net.URL;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipelinedriver.BatchItemContext;
import net.sf.okapi.common.pipelinedriver.DocumentData;
import net.sf.okapi.common.pipelinedriver.IBatchItemContext;
import net.sf.okapi.common.resource.RawDocument;

public class BatchItem  {

	private BatchItemContext bic = new BatchItemContext();
//	private List<Document> documents = new ArrayList<Document>();

	public BatchItem(Document... documents) {
		for (Document document : documents)
			addDocument(document);
	}

	private void addDocument(Document document) {
		if (document == null) return;		
		bic.add(document.getDocumentData());
	}
	
	public BatchItem(URI inputURI, String defaultEncoding, String filterConfigId,
			URI outputURI, String outputEncoding, LocaleId sourceLocale, LocaleId targetLocale) {
		addDocument(new Document(inputURI, defaultEncoding, filterConfigId,
			outputURI, outputEncoding, sourceLocale, targetLocale));		
	}
	
	public BatchItem(RawDocument rawDocument) {
		addDocument(new Document(rawDocument));
	}
	
	public BatchItem(DocumentData documentData) {
		addDocument(new Document(documentData));
	}
	
	public BatchItem(RawDocument rawDoc,
			URI outputURI,
			String outputEncoding) {
		addDocument(new Document(rawDoc, outputURI, outputEncoding));
	}
	
	public BatchItem(CharSequence inputCharSequence, LocaleId sourceLocale) {
		addDocument(new Document(inputCharSequence, sourceLocale));
	}
	
	public BatchItem(URI inputURI, String defaultEncoding, LocaleId sourceLocale) {
		addDocument(new Document(inputURI, defaultEncoding, sourceLocale));
	}

	public BatchItem(URI inputURI, String defaultEncoding, URI outputURI, String outputEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		addDocument(new Document(inputURI, defaultEncoding, outputURI, outputEncoding, sourceLocale,
				targetLocale));
	}
	
	public BatchItem(URI inputURI, String defaultEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		addDocument(new Document(inputURI, defaultEncoding, sourceLocale, targetLocale));
	}
	
	public BatchItem(URL inputURL, String defaultEncoding, LocaleId sourceLocale) {
		addDocument(new Document(inputURL, defaultEncoding, sourceLocale));
	}

	public BatchItem(URL inputURL, String defaultEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		addDocument(new Document(inputURL, defaultEncoding, sourceLocale, targetLocale));
	}
	
	public BatchItem(URL inputURL, String defaultEncoding, URL outputURL, String outputEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		addDocument(new Document(inputURL, defaultEncoding, outputURL, outputEncoding, sourceLocale,
				targetLocale));
	}
	
	public BatchItem(URL inputURL, String defaultEncoding, String outputPath, String outputEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		addDocument(new Document(inputURL, defaultEncoding, outputPath, outputEncoding, sourceLocale,
				targetLocale));
	}
	
	public BatchItem(InputStream inputStream, String defaultEncoding, LocaleId sourceLocale) {
		addDocument(new Document(inputStream, defaultEncoding, sourceLocale));
	}
	
	public BatchItem(InputStream inputStream, String defaultEncoding, LocaleId sourceLocale,
			LocaleId targetLocale) {
		addDocument(new Document(inputStream, defaultEncoding, sourceLocale,
				targetLocale));
	}
	
	public IBatchItemContext getContext() {
		return bic;
	}
}
