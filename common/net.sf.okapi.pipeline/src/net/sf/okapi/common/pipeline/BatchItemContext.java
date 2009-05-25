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

package net.sf.okapi.common.pipeline;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.BaseContext;
import net.sf.okapi.common.resource.RawDocument;

/**
 * Default implementation of the {@link IBatchItemContext} interface.
 */
public class BatchItemContext extends BaseContext implements IBatchItemContext {

	private List<DocumentData> list;

	/**
	 * Creates a new empty BatchItemContext object.
	 */
	public BatchItemContext () {
		super();
		list = new ArrayList<DocumentData>();
	}
	
	/**
	 * Creates a new BatchItemContext object and initializes it with a given
	 * {@link RawDocument} and additional arguments.
	 * @param rawDoc the {@link RawDocument} to use as the main input document.
	 * @param filterConfigId the filter configuration ID for the input document
	 * (can be null if not used).
	 * @param outputURI the output URI of the input document (can be null if not used).
	 * @param outputEncoding the output encoding (can be null if not used).
	 */
	public BatchItemContext (RawDocument rawDoc,
		String filterConfigId,
		URI outputURI,
		String outputEncoding)
	{
		super();
		list = new ArrayList<DocumentData>();
		DocumentData ddi = new DocumentData();
		ddi.rawDocument = rawDoc;
		ddi.filterConfigId = filterConfigId;
		ddi.outputURI = outputURI;
		ddi.outputEncoding = outputEncoding;
		list.add(ddi);
	}
	
	/**
	 * Creates a new BatchItemContext object and initializes it based on a given
	 * input URI and additional arguments.
	 * @param inputURI the URI of the main input document
	 * @param defaultEncoding the default encoding of the input document.
	 * @param filterConfigId the filter configuration ID (can be null if not used)
	 * @param outputURI the output URI (can be null if not used).
	 * @param outputEncoding the output encoding (can be null if not used)
	 * @param sourceLanguage the source language.
	 * @param targetLanguage the target language.
	 */
	public BatchItemContext (URI inputURI,
		String defaultEncoding,
		String filterConfigId,
		URI outputURI,
		String outputEncoding,
		String sourceLanguage,
		String targetLanguage)
	{
		super();
		list = new ArrayList<DocumentData>();
		DocumentData ddi = new DocumentData();
		ddi.rawDocument = new RawDocument(inputURI, defaultEncoding, sourceLanguage, targetLanguage);
		ddi.filterConfigId = filterConfigId;
		ddi.outputURI = outputURI;
		ddi.outputEncoding = outputEncoding;
		list.add(ddi);
	}
	
	/**
	 * Adds a document to the list of inputs for this batch item.
	 * @param data the data of the document.
	 */
	public void add (DocumentData data) {
		list.add(data);
	}
	
	public String getFilterConfigurationId (int index) {
		return list.get(index).filterConfigId;
	}
	
	public String getOutputEncoding (int index) {
		return list.get(index).outputEncoding;
	}
	
	public URI getOutputURI (int index) {
		return list.get(index).outputURI;
	}
	
	public RawDocument getRawDocument (int index) {
		return list.get(index).rawDocument;
	}
	
	public String getSourceLanguage (int index) {
		return list.get(index).rawDocument.getSourceLanguage();
	}

	public String getTargetLanguage (int index) {
		return list.get(index).rawDocument.getTargetLanguage();
	}

}
