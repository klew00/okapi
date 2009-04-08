/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.resource;

import java.io.InputStream;
import java.net.URI;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;

/**
 * File resource which holds a URI, InputStream or MemMappedCharSequence
 * referencing a file to be processed in the pipeline. File's can be decomposed
 * into events in the pipeline.
 */
public class InputResource implements IResource {
	private Annotations annotations;
	private String id;
	private String encoding;
	private String srcLang;
	private String trgLang;
	private InputStream inputStream;
	private URI inputURI;
	private CharSequence inputCharSequence;
	
	public InputResource (CharSequence inputCharSequence,
		String sourceLanguage)
	{
		create(inputCharSequence, sourceLanguage, null);
	}
			
	public InputResource (CharSequence inputCharSequence,
		String sourceLanguage,
		String targetLanguage)
	{
		create(inputCharSequence, sourceLanguage, targetLanguage);
	}

	public InputResource (URI inputURI,
		String defaultEncoding,
		String sourceLanguage)
	{
		create(inputURI, defaultEncoding, sourceLanguage, null);
	}

	public InputResource (URI inputURI,
		String defaultEncoding,
		String sourceLanguage,
		String targetLanguage)
	{
		create(inputURI, defaultEncoding, sourceLanguage, targetLanguage);
	}

	public InputResource (InputStream inputStream,
		String defaultEncoding,
		String sourceLanguage)
	{
		create(inputStream, defaultEncoding, sourceLanguage, null);
	}
		
	public InputResource (InputStream inputStream,
		String defaultEncoding,
		String sourceLanguage,
		String targetLanguage)
	{
		create(inputStream, defaultEncoding, sourceLanguage, targetLanguage);
	}
		
	private void create (CharSequence inputCharSequence,
		String sourceLanguage,
		String targetLanguage)
	{
		setInputCharSequence(inputCharSequence);
		setEncoding("UTF-16BE");
		setSourceLanguage(sourceLanguage);
		setTargetLanguage(targetLanguage);
	}
				
	private void create (URI inputURI,
		String defaultEncoding,
		String sourceLanguage,
		String targetLanguage)
	{
		setInputURI(inputURI);
		setEncoding(defaultEncoding);
		setSourceLanguage(sourceLanguage);
		setTargetLanguage(targetLanguage);
	}

	private void create (InputStream inputStream,
		String defaultEncoding,
		String sourceLanguage,
		String targetLanguage)
	{
		setInputStream(inputStream);
		setEncoding(defaultEncoding);
		setSourceLanguage(sourceLanguage);
		setTargetLanguage(targetLanguage);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.okapi.common.resource.IResource#getAnnotation(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <A> A getAnnotation(Class<? extends IAnnotation> type) {
		if (annotations == null)
			return null;
		else
			return (A) annotations.get(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.resource.IResource#getId()
	 */
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.resource.IResource#getSkeleton()
	 */
	public ISkeleton getSkeleton () {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.okapi.common.resource.IResource#setAnnotation(net.sf.okapi.common
	 * .annotation.IAnnotation)
	 */
	public void setAnnotation (IAnnotation annotation) {
		if (annotations == null) {
			annotations = new Annotations();
		}
		annotations.set(annotation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.resource.IResource#setId(java.lang.String)
	 */
	public void setId (String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.okapi.common.resource.IResource#setSkeleton(net.sf.okapi.common
	 * .filters.ISkeleton)
	 */
	public void setSkeleton (ISkeleton skeleton) {
		// Not Implemented
	}

	/**
	 * Gets the InputStream object associated with this resource. It may be null if 
	 * either {@link #getInputCharSequence()} or {@link #getInputURI()} are not null.
	 * @return The InputStream object for this resource (may be null).
	 */
	public InputStream getInputStream () {
		return inputStream;
	}

	public void setInputStream (InputStream inputStream) {
		this.inputStream = inputStream;
		// Only one of the three inputs can be set at the same time
		inputURI = null;
		inputCharSequence = null;
	}

	public URI getInputURI () {
		return inputURI;
	}

	public void setInputURI(URI inputURI) {
		this.inputURI = inputURI;
		// Only one of the three inputs can be set at the same time
		inputStream = null;
		inputCharSequence = null;
	}

	public void setInputCharSequence(CharSequence inputCharSequence) {
		this.inputCharSequence = inputCharSequence;
		// Only one of the three inputs can be set at the same time
		inputURI = null;
		inputStream = null;
	}

	public CharSequence getInputCharSequence () {
		return inputCharSequence;
	}

	public String getEncoding () {
		return encoding;
	}

	public void setEncoding (String encoding) {
		this.encoding = encoding;
	}
	
	public String getSourceLanguage () {
		return srcLang;
	}

	public void setSourceLanguage (String language) {
		srcLang = language;
	}

	public String getTargetLanguage () {
		return trgLang;
	}

	public void setTargetLanguage (String language) {
		trgLang = language;
	}

}
