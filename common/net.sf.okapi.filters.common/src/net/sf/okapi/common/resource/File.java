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

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;

import net.sf.okapi.common.MemMappedCharSequence;
import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.filters.ISkeleton;

/**
 * File resource which holds a URI, InputStream or MemMappedCharSequence
 * referencing a file to be processed in the pipeline. File's can be decomposed
 * into events in the pipeline.
 * 
 */
public class File implements IResource {

	private Annotations annotations;
	private String id;
	private String encoding;
	private InputStream inputStream;
	private URI inputURI;
	private MemMappedCharSequence inputMemMappedCharSequence;

	public File(URI inputURI, String encoding) {
		this.annotations = new Annotations();
		setInputURI(inputURI);
		try {
			setInputStream(inputURI.toURL().openStream());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
	}

	public File(InputStream inputStream, String encoding) {
		this.annotations = new Annotations();
		setEncoding(encoding);		
		setInputStream(inputStream);
	}

	public File(MemMappedCharSequence inputMemMappedCharSequence) {
		this.annotations = new Annotations();		 
		setInputMemMappedCharSequence(inputMemMappedCharSequence);
		setEncoding("UTF-16BE");
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
	public ISkeleton getSkeleton() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.okapi.common.resource.IResource#setAnnotation(net.sf.okapi.common
	 * .annotation.IAnnotation)
	 */
	public void setAnnotation(IAnnotation annotation) {
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
	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.okapi.common.resource.IResource#setSkeleton(net.sf.okapi.common
	 * .filters.ISkeleton)
	 */
	public void setSkeleton(ISkeleton skeleton) {
		// Not Implemented
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public URI getInputURI() {
		return inputURI;
	}

	public void setInputURI(URI inputURI) {
		this.inputURI = inputURI;
	}

	/**
	 * @param inputMemMappedCharSequence
	 *            the inputMemMappedCharSequence to set
	 */
	public void setInputMemMappedCharSequence(MemMappedCharSequence inputMemMappedCharSequence) {
		this.inputMemMappedCharSequence = inputMemMappedCharSequence;
	}

	/**
	 * @return the inputMemMappedCharSequence
	 */
	public MemMappedCharSequence getInputMemMappedCharSequence() {
		return inputMemMappedCharSequence;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public Reader getReader() {
		if (getInputStream() != null)
			try {
				return new InputStreamReader(getInputStream(), getEncoding());
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		if (getInputMemMappedCharSequence() != null)
			return new CharArrayReader(getInputMemMappedCharSequence().array());
		
		return null;
	}
}
