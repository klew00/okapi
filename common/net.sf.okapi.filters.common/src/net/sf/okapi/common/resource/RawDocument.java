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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
import net.sf.okapi.common.exceptions.OkapiNotImplementedException;
import net.sf.okapi.common.exceptions.OkapiUnsupportedEncodingException;

/**
 * Resource that carries all the information needed for a filter to open a given
 * document, and also the resource associated with the event RAW_DOCUMENT.
 * Documents are passed through the pipeline either as RawDocument, or a filter
 * events. Specialized steps allows to convert one to the other and conversely.
 * The RawDocument object has one (and only one) of three input objects: a
 * CharSequence, a URI, or an InputStream.
 */
public class RawDocument implements IResource {
	private static final Logger LOGGER = Logger.getLogger(RawDocument.class.getName());

	public static final String UNKOWN_ENCODING = "null";

	private Annotations annotations;
	private String filterConfigId;
	private String id;
	private String encoding = UNKOWN_ENCODING;
	private String srcLang;
	private String trgLang;
	private InputStream inputStream;
	private URI inputURI;
	private CharSequence inputCharSequence;
	private InputStream aStream;
	private boolean hasGetReaderBeenCalled;

	/**
	 * Creates a new RawDocument object with a given CharSequence and a source
	 * language.
	 * 
	 * @param inputCharSequence
	 *            the CharSequence for this RawDocument.
	 * @param sourceLanguage
	 *            the source language for this RawDocument.
	 */
	public RawDocument(CharSequence inputCharSequence, String sourceLanguage) {
		create(inputCharSequence, sourceLanguage, null);
	}

	/**
	 * Creates a new RawDocument object with a given CharSequence, a source
	 * language and a target language.
	 * 
	 * @param inputCharSequence
	 *            the CharSequence for this RawDocument.
	 * @param sourceLanguage
	 *            the source language for this RawDocument.
	 * @param targetLanguage
	 *            the target language for this RawDocument.
	 */
	public RawDocument(CharSequence inputCharSequence, String sourceLanguage, String targetLanguage) {
		create(inputCharSequence, sourceLanguage, targetLanguage);
	}

	/**
	 * Creates a new RawDocument object with a given URI, a default encoding and
	 * a source language.
	 * 
	 * @param inputURI
	 *            the URI for this RawDocument.
	 * @param defaultEncoding
	 *            the default encoding for this RawDocument.
	 * @param sourceLanguage
	 *            the source language for this RawDocument.
	 */
	public RawDocument(URI inputURI, String defaultEncoding, String sourceLanguage) {
		create(inputURI, defaultEncoding, sourceLanguage, null);
	}

	/**
	 * Creates a new RawDocument object with a given URI, a default encoding, a
	 * source language and a target language.
	 * 
	 * @param inputURI
	 *            the URI for this RawDocument.
	 * @param defaultEncoding
	 *            the default encoding for this RawDocument.
	 * @param sourceLanguage
	 *            the source language for this RawDocument.
	 * @param targetLanguage
	 *            the target language for this RawDocument.
	 */
	public RawDocument(URI inputURI, String defaultEncoding, String sourceLanguage, String targetLanguage) {
		create(inputURI, defaultEncoding, sourceLanguage, targetLanguage);
	}

	/**
	 * Creates a new RawDocument object with a given InputStream, a default
	 * encoding and a source language.
	 * 
	 * @param inputStream
	 *            the InputStream for this RawDocument.
	 * @param defaultEncoding
	 *            the default encoding for this RawDocument.
	 * @param sourceLanguage
	 *            the source language for this RawDocument.
	 */
	public RawDocument(InputStream inputStream, String defaultEncoding, String sourceLanguage) {
		create(inputStream, defaultEncoding, sourceLanguage, null);
	}

	/**
	 * Creates a new RawDocument object with a given InputStream, a default
	 * encoding and a source language.
	 * 
	 * @param inputStream
	 *            the InputStream for this RawDocument.
	 * @param defaultEncoding
	 *            the default encoding for this RawDocument.
	 * @param sourceLanguage
	 *            the source language for this RawDocument.
	 * @param targetLanguage
	 *            the target language for this RawDocument.
	 */
	public RawDocument(InputStream inputStream, String defaultEncoding, String sourceLanguage, String targetLanguage) {
		create(inputStream, defaultEncoding, sourceLanguage, targetLanguage);
	}

	private void create(CharSequence inputCharSequence, String srcLang, String trgLang) {
		this.inputCharSequence = inputCharSequence;
		this.encoding = "UTF-16";
		this.srcLang = srcLang;
		this.trgLang = trgLang;
		hasGetReaderBeenCalled = false;
	}

	private void create(URI inputURI, String defaultEncoding, String srcLang, String trgLang) {
		this.inputURI = inputURI;
		this.encoding = defaultEncoding;
		this.srcLang = srcLang;
		this.trgLang = trgLang;
		hasGetReaderBeenCalled = false;
	}

	private void create(InputStream inputStream, String defaultEncoding, String srcLang, String trgLang) {
		this.inputStream = inputStream;
		this.encoding = defaultEncoding;
		this.srcLang = srcLang;
		this.trgLang = trgLang;
		hasGetReaderBeenCalled = false;
	}

	/**
	 * Returns a Reader based on the current Stream returned from getStream().
	 * <h3>WARNING:</h3> For CharSequence and URI inputs the Reader returned
	 * will be recreated (<b>and more importantly reset</b>) for each call. For
	 * InputStream input the same Reader is returned for each call and it is the
	 * responsibility of the caller to reset it if needed.
	 * <p>
	 * 
	 * @return a Reader
	 */
	public Reader getReader() {
		if (getEncoding() == UNKOWN_ENCODING) {
			throw new OkapiUnsupportedEncodingException("Encoding has not been set");
		}

		Reader reader = null;
		try {
			reader = new InputStreamReader(getStream(), getEncoding());
			hasGetReaderBeenCalled = true;
		} catch (UnsupportedEncodingException e) {
			throw new OkapiUnsupportedEncodingException(String.format("The encoding '%s' is not supported.",
					getEncoding()), e);
		}

		hasGetReaderBeenCalled = true;
		return reader;
	}

	/**
	 * Returns an InputStream based on the current input. <h2>WARNING:</h2> For
	 * CharSequence and URI inputs the stream returned will be recreated (<b>and
	 * more importantly reset</b>) for each call. For InputStream input the same
	 * stream is returned for each call and it is the responsibility of the
	 * caller to reset it if needed.
	 * <p>
	 * 
	 * @return the InputStream
	 * 
	 * @throws OkapiIOException
	 */
	public InputStream getStream() {
		if (inputCharSequence != null) {
			try {
				aStream = new ByteArrayInputStream(inputCharSequence.toString().getBytes(getEncoding()));
			} catch (UnsupportedEncodingException e) {
				throw new OkapiUnsupportedEncodingException(String.format("The encoding '%s' is not supported.",
						getEncoding()), e);
			}
		} else if (getInputURI() != null) {
			URL url = null;
			try {
				url = getInputURI().toURL();
				aStream = getInputURI().toURL().openStream();
			} catch (IllegalArgumentException e) {
				throw new OkapiIOException("Could not open the URI. The URI must be absolute: "
					+ ((url == null) ? "URL is null" : url.toString()), e);
			} catch (MalformedURLException e) {
				throw new OkapiIOException("Could not open the URI. The URI may be malformed: "
					+ ((url == null) ? "URL is null" : url.toString()), e);
			} catch (IOException e) {
				throw new OkapiIOException(
					"Could not open the URL. The URL is OK but the input stream could not be opened", e);
			}
		} else if (inputStream != null) {
			if (aStream != null) {
				return aStream;
			}
			aStream = inputStream;
		} else {
			throw new OkapiIOException("RawDocument has no input defined.");
		}

		return aStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.okapi.common.resource.IResource#getAnnotation(java.lang.Class)
	 */
	public <A extends IAnnotation> A getAnnotation (Class<A> annotationType) {
		if ( annotations == null ) return null;
		return annotationType.cast(annotations.get(annotationType) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.resource.IResource#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns always null as there is never a skeleton associated to a
	 * RawDocument.
	 * 
	 * @return always null.
	 */
	public ISkeleton getSkeleton() {
		throw new OkapiNotImplementedException("The RawDocument resource does not have skeketon");
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

	/**
	 * This method has no effect as there is never a skeleton for a RawDocument.
	 */
	public void setSkeleton(ISkeleton skeleton) {
		throw new OkapiNotImplementedException("RawDcoument has no skeleton");
	}

	/**
	 * Gets the URI object associated with this resource. It may be null if
	 * either CharSequence InputStream inputs are not null.
	 * 
	 * @return the URI object for this resource (may be null).
	 */
	public URI getInputURI() {
		return inputURI;
	}

	/**
	 * Gets the CharSequence associated with this resource. It may be null if
	 * either URI or InputStream inputs are not null.
	 * 
	 * @return the CHarSequence
	 */
	public CharSequence getInputCharSequence() {
		return inputCharSequence;
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
	 * Gets the source language associated to this resource.
	 * 
	 * @return the source language associated to this resource.
	 */
	public String getSourceLanguage() {
		return srcLang;
	}

	/**
	 * Gets the target language associated to this resource.
	 * 
	 * @return the target language associated to this resource.
	 */
	public String getTargetLanguage() {
		return trgLang;
	}

	/**
	 * Set the input encoding. <h4>WARNING:</h4> Any Readers gotten via
	 * getReader() are now invalid. In some cases it may not be possible to
	 * create a new Reader. It is best to set the encoding <b>before</b> a any
	 * calls to getReader
	 * <p>
	 * 
	 * @param encoding
	 */
	public void setEncoding(String encoding) {
		// Cannot reset an encoding on a CharSequence document
		if (inputCharSequence != null) {
			LOGGER.log(Level.FINE, "Cannot reset an encoding on a CharSequence input in RawDocument");
			return;
		}

		if (hasGetReaderBeenCalled) {
			throw new OkapiNotImplementedException("Cannot call setEncoding() after a getReader() has been called");
		}

		this.encoding = encoding;
	}

	/**
	 * Sets the identifier of the filter configuration to use with this
	 * document.
	 * 
	 * @param filterConfigId
	 *            the filter configuration identifier to set.
	 */
	public void setFilterConfigId(String filterConfigId) {
		this.filterConfigId = filterConfigId;
	}

	/**
	 * Gets the identifier of the filter configuration to use with this
	 * document.
	 * 
	 * @return the the filter configuration identifier for this document, or
	 *         null if none is set.
	 */
	public String getFilterConfigId() {
		return filterConfigId;
	}
}