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

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipelinedriver.DocumentData;
import net.sf.okapi.common.resource.RawDocument;

public class BatchItem  {

	private DocumentData documentData;
	
	public BatchItem(URI inputURI, String defaultEncoding, String filterConfigId,
			URI outputURI, String outputEncoding, LocaleId sourceLocale, LocaleId targetLocale) {
		this(new RawDocument(inputURI, defaultEncoding, sourceLocale,	targetLocale), outputURI, outputEncoding);
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
	}

	public RawDocument getRawDocument() {
		return (documentData != null) ? documentData.rawDocument : null;
	}

	public void setRawDocument(RawDocument rawDocument) {
		if (documentData == null)
			documentData = new DocumentData();
		
		documentData.rawDocument = rawDocument;
	}
	
}
