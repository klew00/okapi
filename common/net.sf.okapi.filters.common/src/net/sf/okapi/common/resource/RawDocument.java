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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.exceptions.OkapiUnsupportedEncodingException;

/**
 * Resource that carries all the information needed for a filter to open a given
 * document, and also the resource associated with the event INPUT_RESOURCE.
 * Documents are passed through the pipeline either as RawDocument, or a
 * filter events. Specialized steps allows to convert one to the other and
 * conversely. The RawDocument object has one (and only one) of three input
 * objects: a CharSequence, a URI, or an InputStream.
 */
public class RawDocument implements IResource {

	private static final Logger LOGGER = Logger.getLogger(RawDocument.class.getName());

	private Annotations annotations;
	private String id;
	private String encoding;
	private String srcLang;
	private String trgLang;
	private InputStream inputStream;
	private URI inputURI;
	private CharSequence inputCharSequence;
	private Reader inputReader;

	/**
	 * Creates a new RawDocument object with a given CharSequence and a source
	 * language.
	 * 
	 * @param inputCharSequence
	 *            The CharSequence for this RawDocument.
	 * @param sourceLanguage
	 *            The source language for this RawDocument.
	 */
	public RawDocument(CharSequence inputCharSequence, String sourceLanguage) {
		create(inputCharSequence, sourceLanguage, null);
	}

	/**
	 * Creates a new RawDocument object with a given CharSequence, a source
	 * language and a target language.
	 * 
	 * @param inputCharSequence
	 *            The CharSequence for this RawDocument.
	 * @param sourceLanguage
	 *            The source language for this RawDocument.
	 * @param targetLanguage
	 *            The target language for this RawDocument.
	 */
	public RawDocument(CharSequence inputCharSequence, String sourceLanguage, String targetLanguage) {
		create(inputCharSequence, sourceLanguage, targetLanguage);
	}

	/**
	 * Creates a new RawDocument object with a given URI, a default encoding
	 * and a source language.
	 * 
	 * @param inputURI
	 *            The URI for this RawDocument.
	 * @param defaultEncoding
	 *            The default encoding for this RawDocument.
	 * @param sourceLanguage
	 *            The source language for this RawDocument.
	 */
	public RawDocument(URI inputURI, String defaultEncoding, String sourceLanguage) {
		create(inputURI, defaultEncoding, sourceLanguage, null);
	}

	/**
	 * Creates a new RawDocument object with a given URI, a default encoding,
	 * a source language and a target language.
	 * 
	 * @param inputURI
	 *            The URI for this RawDocument.
	 * @param defaultEncoding
	 *            The default encoding for this RawDocument.
	 * @param sourceLanguage
	 *            The source language for this RawDocument.
	 * @param targetLanguage
	 *            The target language for this RawDocument.
	 */
	public RawDocument(URI inputURI, String defaultEncoding, String sourceLanguage, String targetLanguage) {
		create(inputURI, defaultEncoding, sourceLanguage, targetLanguage);
	}

	/**
	 * Creates a new RawDocument object with a given InputStream, a default
	 * encoding and a source language.
	 * 
	 * @param inputStream
	 *            The InputStream for this RawDocument.
	 * @param defaultEncoding
	 *            The default encoding for this RawDocument.
	 * @param sourceLanguage
	 *            The source language for this RawDocument.
	 */
	public RawDocument(InputStream inputStream, String defaultEncoding, String sourceLanguage) {
		create(inputStream, defaultEncoding, sourceLanguage, null);
	}

	/**
	 * Creates a new RawDocument object with a given InputStream, a default
	 * encoding and a source language.
	 * 
	 * @param inputStream
	 *            The InputStream for this RawDocument.
	 * @param defaultEncoding
	 *            The default encoding for this RawDocument.
	 * @param sourceLanguage
	 *            The source language for this RawDocument.
	 * @param targetLanguage
	 *            The target language for this RawDocument.
	 */
	public RawDocument(InputStream inputStream, String defaultEncoding, String sourceLanguage, String targetLanguage) {
		create(inputStream, defaultEncoding, sourceLanguage, targetLanguage);
	}

	/**
	 * Create a {@link Reader} from either inputStream or inputCharSequence. If
	 * an inputURL is set then an inputStream is set automatically.
	 * 
	 * @return the Reader
	 * 
	 * @throws OkapiIOException
	 */
	public Reader getReader() {
		URL url = null;

		if (inputReader != null) {
			return inputReader;
		}

		if (getInputStream() != null)
			readerFromInputStream(getInputStream());
		else if (getInputCharSequence() != null)
			inputReader = new StringReader(getInputCharSequence().toString());
		else if (getInputURI() != null) {
			try {
				url = getInputURI().toURL();
				readerFromInputStream(getInputURI().toURL().openStream());
			} catch (IllegalArgumentException e) {
				OkapiIOException re = new OkapiIOException(e);
				LOGGER.log(Level.SEVERE, "Could not open the URI. The URI must be absolute: "
						+ ((url == null) ? "URL is null" : url.toString()), re);
				throw re;

			} catch (MalformedURLException e) {
				OkapiIOException re = new OkapiIOException(e);
				LOGGER.log(Level.SEVERE, "Could not open the URI. The URI may be malformed: "
						+ ((url == null) ? "URL is null" : url.toString()), re);
				throw re;

			} catch (IOException e) {
				OkapiIOException re = new OkapiIOException(e);
				LOGGER.log(Level.SEVERE,
						"Could not open the URL. The URL is OK but the input stream could not be opened", re);
				throw re;
			}
		}

		return inputReader;
	}

	private void readerFromInputStream(InputStream inStream) {
		try {
			inputReader = new InputStreamReader(inStream, getEncoding());
		} catch (UnsupportedEncodingException e) {
			OkapiUnsupportedEncodingException re = new OkapiUnsupportedEncodingException(e);
			LOGGER.log(Level.SEVERE, "The encoding " + getEncoding()
					+ "is not a standard Java encoding. Please check the spelling.", re);
			throw re;
		}
	}

	private void create(CharSequence inputCharSequence, String sourceLanguage, String targetLanguage) {
		setInputCharSequence(inputCharSequence);
		setEncoding("UTF-16BE");
		setSourceLanguage(sourceLanguage);
		setTargetLanguage(targetLanguage);
	}

	private void create(URI inputURI, String defaultEncoding, String sourceLanguage, String targetLanguage) {
		setInputURI(inputURI);
		setEncoding(defaultEncoding);
		setSourceLanguage(sourceLanguage);
		setTargetLanguage(targetLanguage);
	}

	private void create(InputStream inputStream, String defaultEncoding, String sourceLanguage, String targetLanguage) {
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
		// This resource has no skeleton
	}

	/**
	 * Gets the InputStream object associated with this resource. It may be null
	 * if either {@link #getInputCharSequence()} or {@link #getInputURI()} are
	 * not null.
	 * 
	 * @return The InputStream object for this resource (may be null).
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * Sets the InputStream object associated with this resource.
	 * 
	 * @param inputStream
	 *            The InputStream object for this resource.
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		// Only one of the three inputs can be set at the same time
		inputURI = null;
		inputCharSequence = null;
	}

	/**
	 * Gets the URI object associated with this resource. It may be null if
	 * either {@link #getInputCharSequence()} or {@link #getInputStream()} are
	 * not null.
	 * 
	 * @return The URI object for this resource (may be null).
	 */
	public URI getInputURI() {
		return inputURI;
	}

	/**
	 * Sets the URI object associated with this resource.
	 * 
	 * @param inputURI
	 *            The URI object for this resource.
	 */
	public void setInputURI(URI inputURI) {
		this.inputURI = inputURI;
		// Only one of the three inputs can be set at the same time
		inputStream = null;
		inputCharSequence = null;
	}

	/**
	 * Gets the CharSequence object associated with this resource. It may be
	 * null if either {@link #getInputURI()} or {@link #getInputStream()} are
	 * not null.
	 * 
	 * @return The CharSequence object for this resource (may be null).
	 */
	public CharSequence getInputCharSequence() {
		return inputCharSequence;
	}

	/**
	 * Sets the CharSequence object associated with this resource.
	 * 
	 * @param inputCharSequence
	 *            The CharSequence object for this resource.
	 */
	public void setInputCharSequence(CharSequence inputCharSequence) {
		this.inputCharSequence = inputCharSequence;
		// Only one of the three inputs can be set at the same time
		inputURI = null;
		inputStream = null;
	}

	/**
	 * Gets the default encoding associated to this resource.
	 * 
	 * @return The default encoding associated to this resource.
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Sets the default encoding associated with this resource.
	 * 
	 * @param encoding
	 *            The default encoding associated with this resource.
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Gets the source language associated to this resource.
	 * 
	 * @return The source language associated to this resource.
	 */
	public String getSourceLanguage() {
		return srcLang;
	}

	/**
	 * Sets the source language associated with this resource.
	 * 
	 * @param language
	 *            The source language associated with this resource.
	 */
	public void setSourceLanguage(String language) {
		srcLang = language;
	}

	/**
	 * Gets the target language associated to this resource.
	 * 
	 * @return The target language associated to this resource.
	 */
	public String getTargetLanguage() {
		return trgLang;
	}

	/**
	 * Sets the target language associated with this resource.
	 * 
	 * @param language
	 *            The target language associated with this resource.
	 */
	public void setTargetLanguage(String language) {
		trgLang = language;
	}

}
