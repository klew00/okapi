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

import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.MemMappedCharSequence;
import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;

/**
 * File resource which holds a URI, InputStream or MemMappedCharSequence
 * referencing a file to be processed in the pipeline. File's can be decomposed
 * into events in the pipeline.
 * 
 */
public class FileResource implements IResource {
	private Annotations annotations;
	private String id;
	private String encoding;
	private String locale;
	private BOMNewlineEncodingDetector.NewlineType originalNewlineType;
	private InputStream inputStream;
	private URI inputURI;
	private MemMappedCharSequence inputMemMappedCharSequence;
	private String mimeType;
	private Reader reader;

	public FileResource(URI inputURI, String encoding, String mimeType, String locale) {
		this.annotations = new Annotations();
		reset(inputURI, encoding, mimeType, locale);
	}

	public FileResource(InputStream inputStream, String encoding, String mimeType, String locale) {
		this.annotations = new Annotations();
		reset(inputStream, encoding, mimeType, locale);
	}

	public FileResource(MemMappedCharSequence inputMemMappedCharSequence, String mimeType, String locale) {
		this.annotations = new Annotations();
		reset(inputMemMappedCharSequence, mimeType, locale);
	}
	
	public void reset(MemMappedCharSequence inputMemMappedCharSequence, String mimeType, String locale) {
		setInputMemMappedCharSequence(inputMemMappedCharSequence);
		setEncoding("UTF-16BE");
		setMimeType(mimeType);
		setLocale(locale);
		setOriginalNewlineType(BOMNewlineEncodingDetector.getNewLineType(getInputMemMappedCharSequence()));		
	}
	
	public void reset(URI inputURI, String encoding, String mimeType, String locale) {
		setInputURI(inputURI);
		setMimeType(mimeType);
		setLocale(locale);
		try {
			InputStream inputStream = inputURI.toURL().openStream();
			setInputStream(inputStream);
			setOriginalNewlineType(new BOMNewlineEncodingDetector(inputStream).getNewLineType());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}				
	}
	
	public void reset(InputStream inputStream, String encoding, String mimeType, String locale) {
		setEncoding(encoding);		
		setMimeType(mimeType);
		setLocale(locale);
		setInputStream(inputStream);
		try {
			setOriginalNewlineType(new BOMNewlineEncodingDetector(inputStream).getNewLineType());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @param mimeType the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @param originalNewlineType the originalNewlineType to set
	 */
	public void setOriginalNewlineType(BOMNewlineEncodingDetector.NewlineType originalNewlineType) {
		this.originalNewlineType = originalNewlineType;
	}

	/**
	 * @return the originalNewlineType
	 */
	public BOMNewlineEncodingDetector.NewlineType getOriginalNewlineType() {
		return originalNewlineType;
	}

	public Reader getReader() {
		if (reader != null) {
			return reader;
		}
		
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
	
	public void close() {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		if (getInputStream() != null)
			try {
				getInputStream().close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		if (getInputMemMappedCharSequence() != null) {
			getInputMemMappedCharSequence().close();
		}					
	}
}
