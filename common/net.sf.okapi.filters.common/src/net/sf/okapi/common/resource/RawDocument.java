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

import java.io.StringReader;
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

import net.sf.okapi.common.BOMNewlineEncodingDetector;
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

	public static final String ISO_8859_1 = "ISO-8859-1";

	private Annotations annotations;
	private String id;
	private String encoding;
	private String srcLang;
	private String trgLang;
	private InputStream inputStream;
	private URI inputURI;
	private CharSequence inputCharSequence;
	private InputStream aStream;
	private boolean hasUtf8Bom;
	private boolean hasUtf8Encoding;
	private String newLineType;
	private boolean isBinary;
	private boolean hasBom;
	private boolean autodetected;
	private int bomSize;

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
		create(inputURI, defaultEncoding, sourceLanguage, null, false);
	}

	/**
	 * Creates a new RawDocument object with a given URI, a default encoding and
	 * a source language.
	 * 
	 * @param inputURI
	 *            the URI for this RawDocument.	
	 * @param sourceLanguage
	 *            the source language for this RawDocument.
	 * @param isBinary
	 *            is the input non textual?
	 */
	public RawDocument(URI inputURI, String sourceLanguage, boolean isBinary) {
		create(inputURI, ISO_8859_1, sourceLanguage, null, isBinary);
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
		create(inputURI, defaultEncoding, sourceLanguage, targetLanguage, false);
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
		create(inputStream, defaultEncoding, sourceLanguage, null, false);
	}

	/**
	 * Creates a new RawDocument object with a given URI, a default encoding, a
	 * source language and a target language.
	 * 
	 * @param inputURI
	 *            the URI for this RawDocument.
	 * @param sourceLanguage
	 *            the source language for this RawDocument.
	 * @param targetLanguage
	 *            the target language for this RawDocument.
	 * @param isBinary
	 *            is the input non textual?
	 */
	public RawDocument(URI inputURI, String sourceLanguage, String targetLanguage, boolean isBinary) {
		create(inputURI, ISO_8859_1, sourceLanguage, targetLanguage, isBinary);
	}

	/**
	 * Creates a new RawDocument object with a given InputStream, a default
	 * encoding and a source language.
	 * 
	 * @param inputStream
	 *            the InputStream for this RawDocument.
	 * @param sourceLanguage
	 *            the source language for this RawDocument.
	 * @param isBinary
	 *            is the input non textual?
	 */
	public RawDocument(InputStream inputStream, String sourceLanguage, boolean isBinary) {
		create(inputStream, ISO_8859_1, sourceLanguage, null, isBinary);
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
		create(inputStream, defaultEncoding, sourceLanguage, targetLanguage, false);
	}

	/**
	 * Creates a Reader based on the current input types.
	 * 
	 * @param removeBom
	 *            - true if we want to remove the Byte Order Mark, false
	 *            otherwise
	 * @return - a Reader
	 */
	public Reader getReader(boolean removeBom) {
		if (hasBom()) {
			if (removeBom) {
				getStream(removeBom);
			}
		}
		return getReader();
	}

	/**
	 * Creates or returns an existing {@link InputStream} from the input for
	 * this RawDocument. The stream is created from inputURI, inputStream or
	 * inputCharSequence.
	 * 
	 * @param removeBom
	 *            - true if we want to remove the Byte Order Mark, false
	 *            otherwise
	 * @return the InputStream
	 * 
	 * @throws OkapiIOException
	 */
	public InputStream getStream(boolean removeBom) {
		InputStream s = getStream();
		if (hasBom()) {
			if (removeBom) {
				byte[] bom = new byte[this.getBomSize()];
				try {
					s.read(bom);
				} catch (IOException e) {
					throw new OkapiIOException("Error removing BOM from stream", e);
				}
			}
		}
		return s;
	}

	/**
	 * Creates a Reader based on the current Stream.
	 * 
	 * @return a Reader
	 */
	public Reader getReader() {
		if (isBinary()) {
			throw new OkapiNotImplementedException("Cannot create a Reader on a binary document");
		}

		Reader reader = null;
		try {
			if (inputCharSequence != null) {
				reader = new StringReader(inputCharSequence.toString());
			} else {
				reader = new InputStreamReader(getStream(), getEncoding());
			}
		} catch (UnsupportedEncodingException e) {
			OkapiUnsupportedEncodingException re = new OkapiUnsupportedEncodingException(e);
			LOGGER.log(Level.SEVERE, String.format("The encoding '%s' is not supported.", getEncoding()), re);
			throw re;
		}
		return reader;
	}

	/**
	 * Creates or returns an existing {@link InputStream} from the input for
	 * this RawDocument. The stream is created from inputURI, inputStream or
	 * inputCharSequence.
	 * 
	 * @return the InputStream
	 * 
	 * @throws OkapiIOException
	 */
	public InputStream getStream() {
		if (inputCharSequence != null) {
			try {
				aStream = new ByteArrayInputStream(inputCharSequence.toString().getBytes(
						getEncoding()));
			} catch (UnsupportedEncodingException e) {
				OkapiUnsupportedEncodingException re = new OkapiUnsupportedEncodingException(e);
				LOGGER.log(Level.SEVERE, String.format("The encoding '%s' is not supported.", getEncoding()), re);
				throw re;
			}
		} else if (getInputURI() != null) {
			URL url = null;
			try {
				url = getInputURI().toURL();
				aStream = getInputURI().toURL().openStream();
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

	private void getInputInfo() {
		try {
			BOMNewlineEncodingDetector bomDetector = new BOMNewlineEncodingDetector(getStream(), encoding);
			if (bomDetector.isDifinitive()) {
				if (!bomDetector.getEncoding().equals(getEncoding())) {
					LOGGER.log(Level.FINE, String.format("Byte Order Mark detected. Changing encoding to %s'",
							bomDetector.getEncoding()));
				}
				this.encoding = bomDetector.getEncoding();
			}
			this.hasBom = bomDetector.hasBom();
			this.hasUtf8Bom = bomDetector.hasUtf8Bom();
			this.hasUtf8Encoding = bomDetector.getEncoding().equals(BOMNewlineEncodingDetector.UTF_8) ? true : false;
			this.newLineType = bomDetector.getNewlineType().toString();
			this.autodetected = bomDetector.isAutodetected();
			this.bomSize = bomDetector.getBomSize();
		} catch (IOException e) {
			throw new OkapiIOException(e);
		}
	}

	private void create(CharSequence inputCharSequence, String srcLang, String trgLang) {
		this.inputCharSequence = inputCharSequence;
		this.encoding = "UTF-16";
		this.srcLang = srcLang;
		this.trgLang = trgLang;
		this.autodetected = false;
		this.bomSize = 0;
		getInputInfo();
	}

	private void create(URI inputURI, String defaultEncoding, String srcLang, String trgLang, boolean isBinary) {
		this.inputURI = inputURI;
		this.encoding = defaultEncoding;
		this.srcLang = srcLang;
		this.trgLang = trgLang;
		this.isBinary = isBinary;
		this.autodetected = false;
		this.bomSize = 0;
		if (!isBinary) {
			getInputInfo();
		}
	}

	private void create(InputStream inputStream, String defaultEncoding, String srcLang, String trgLang,
			boolean isBinary) {
		this.inputStream = inputStream;
		this.encoding = defaultEncoding;
		this.srcLang = srcLang;
		this.trgLang = trgLang;
		this.isBinary = isBinary;
		this.autodetected = false;
		this.bomSize = 0;
		if (!isBinary) {
			getInputInfo();
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
	 * @return the hasUtf8Bom
	 */
	public boolean hasUtf8Bom() {
		return hasUtf8Bom;
	}

	/**
	 * @return the hasUtf8Encoding
	 */
	public boolean hasUtf8Encoding() {
		return hasUtf8Encoding;
	}

	/**
	 * Get the newline type in the input, if none are found return the platform
	 * default newline
	 * 
	 * @return the newLineType
	 */
	public String getNewLineType() {
		return newLineType;
	}

	/**
	 * Is the input a non-textual?
	 * 
	 * @return the isBinary
	 */
	public boolean isBinary() {
		return isBinary;
	}

	/**
	 * Does this document have a byte order mark?
	 * 
	 * @return true if there is a BOM, false otherwise.
	 */
	public boolean hasBom() {
		return hasBom;
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
		if (isBinary()) {
			throw new OkapiNotImplementedException("Cannot set an encoding on a binary document");
		}

		this.encoding = encoding;
	}

	/**
	 * Indicates if the guessed encoding was auto-detected. If not it is the
	 * default encoding that was provided.
	 * 
	 * @return True if the guessed encoding was auto-detected, false if not.
	 */
	public boolean isAutodetected() {
		return autodetected;
	}

	/**
	 * Gets the number of bytes used by the Byte-Order-mark in this document.
	 * 
	 * @return The byte size of the BOM in this document.
	 */
	public int getBomSize() {
		return bomSize;
	}
}