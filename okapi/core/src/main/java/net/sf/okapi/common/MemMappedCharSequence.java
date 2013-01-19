// Jericho HTML Parser - Java based library for analyzing and manipulating HTML
// Version 3.0-beta1
// Copyright (C) 2007 Martin Jericho
// http://jerichohtml.sourceforge.net/
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of either one of the following licences:
//
// 1. The Eclipse Public License (EPL) version 1.0,
// included in this distribution in the file licence-epl-1.0.html
// or available at http://www.eclipse.org/legal/epl-v10.html
//
// 2. The GNU Lesser General Public License (LGPL) version 2.1 or later,
// included in this distribution in the file licence-lgpl-2.1.txt
// or available at http://www.gnu.org/licenses/lgpl.txt
//
// This library is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the individual licence texts for more details.

// Modified by Jim Hargrave for the Okapi project licensed under the LGPL

package net.sf.okapi.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.UnsupportedCharsetException;

import net.sf.okapi.common.exceptions.OkapiFileNotFoundException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.exceptions.OkapiUnsupportedEncodingException;

/**
 * Represents the text from the source document that is to be parsed.
 * <p>
 * This class is normally only of interest to users who wish to create <a
 * href="TagType.html#Custom">custom tag types</a>.
 * <p>
 * The parse text is defined as the entire text of the source document in lower
 * case, with all <code>Segment.ignoreWhenParsing()</code> ignored} segments
 * replaced by space characters.
 * <p>
 * The text is stored in lower case to make case insensitive parsing as
 * efficient as possible.
 * <p>
 * This class provides many methods which are also provided by the
 * <code>java.lang.String</code> class, but adds an extra parameter called
 * <code>breakAtIndex</code> to the various <code>indexOf</code> methods. This
 * parameter allows a search on only a specified segment of the text, which is
 * not possible using the normal <code>String</code> class.
 * <p>
 * <code>ParseText</code> instances are obtained using the
 * <code>Source.getParseText()</code> method.
 */
public final class MemMappedCharSequence implements CharSequence {
	
	private CharBuffer text;
	private MappedByteBuffer byteBuffer;
	private File tempUTF16BEfile;

	/**
	 * A value to use as the <code>breakAtIndex</code> argument in certain
	 * methods to indicate that the search should continue to the start or end
	 * of the parse text.
	 */
	public static final int NO_BREAK = -1;

	/**
	 * Constructs a new <code>ParseText</code> object based on the specified
	 * string. Convert the char[] to CharBuffer to be compatible with buffered
	 * case.
	 * 
	 * @param string
	 *            the string upon which the parse text is based.
	 */
	public MemMappedCharSequence(final String string, boolean lowercase) {		
		text = CharBuffer.wrap(string, 0, string.length());		
		if (lowercase) {
			toLowercase();
		}
	}

	public MemMappedCharSequence(final String string) {
		this(string, false);
	}

	/**
	 * Constructs a new <code>ParseText</code> object based on the specified
	 * file name. The file is assumed to be encoded as UTF-16BE.
	 * @param inputSource
	 *      the reader to use for the input.
	 */
	public MemMappedCharSequence(final Reader inputSource) {
		this(inputSource, false);
	}

	/**
	 * Constructs a new <code>ParseText</code> object based on the specified
	 * file name, and optionally converts it into lowercase characters.
	 * The file is assumed to be encoded as UTF-16BE.
	 * @param inputSource the reader to use for the input.
	 * @param lowercase true if the content should be converted to lowercases.
	 */
	public MemMappedCharSequence(final Reader inputSource, boolean lowercase) {		
		try {
			createMemMappedCharBuffer(Channels.newChannel(new ReaderInputStream(inputSource, "UTF-16BE")), "UTF-16BE",
					lowercase);
		} catch (UnsupportedEncodingException e) {
			// UTF-16BE encoding should be supported on all Java VM's
			throw new OkapiUnsupportedEncodingException("UTF-16BE encoding not supported", e);
		}
	}

	/**
	 * Constructs a new <code>ParseText</code> object based on the specified
	 * file name, for a given encoding.
	 * @param inputSource the reader to use for the input.
	 * @param encoding the encoding to use.
	 */
	public MemMappedCharSequence(final InputStream inputSource, String encoding) {
		this(inputSource, encoding, false);
	}

	/**
	 * Constructs a new <code>ParseText</code> object based on the specified
	 * file name, for a given encoding, and optionally converts it into lowercase characters.
	 * @param inputSource the reader to use for the input.
	 * @param encoding the encoding to use.
	 * @param lowercase true if the content should be converted to lowercases.
	 */
	public MemMappedCharSequence(final InputStream inputSource, String encoding, boolean lowercase) {		
		createMemMappedCharBuffer(Channels.newChannel(inputSource), encoding, lowercase);
	}

	/**
	 * Constructs a new <code>ParseText</code> object based on the specified
	 * file name, in a given encoding.
	 * @param inputSource the URL of the input upon which the parse text is based.
	 * @param encoding the encoding to use.
	 */
	public MemMappedCharSequence(final URL inputSource, String encoding) {
		this(inputSource, encoding, false);
	}

	/**
	 * Constructs a new <code>ParseText</code> object based on the specified
	 * file name, in a given encoding, and optionally converts it into lowercase characters.
	 * @param inputSource the URL of the input upon which the parse text is based.
	 * @param encoding the encoding to use.
	 * @param lowercase true if the content should be converted to lowercases.
	 */
	public MemMappedCharSequence(final URL inputSource, String encoding, boolean lowercase) {
		try {
			createMemMappedCharBuffer(Channels.newChannel(inputSource.openStream()), encoding, lowercase);
		} catch (IOException e) {
			throw new OkapiIOException("Cannot open URL stream: " + inputSource.toString(), e);
		}
	}

	private void createMemMappedCharBuffer(final ReadableByteChannel inputSource, String encoding, boolean lowercase) {
		text = null;
		FileChannel fc = null;
		tempUTF16BEfile = null;

		try {
			// create temp mem map file and convert it to UTF-16(either BE or LE
			// depending on the native order of the platform)
			tempUTF16BEfile = File.createTempFile("memmap", ".tmp");
			BufferedWriter tempOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempUTF16BEfile),
					"UTF-16BE"));
			decodeChannel(inputSource, tempOut, Charset.forName(encoding));
			tempOut.close();

			// set up modes based on what we need to do with the buffer
			String mode = "r";
			FileChannel.MapMode mapMode = FileChannel.MapMode.READ_ONLY;
			if (lowercase) {
				mode = "rw";
				mapMode = FileChannel.MapMode.PRIVATE;
			}

			// memory map the UTF-16BE temp file and create a CharBuffer view
			fc = new RandomAccessFile(tempUTF16BEfile, mode).getChannel();
			byteBuffer = fc.map(mapMode, 0, fc.size());
			byteBuffer.order(ByteOrder.BIG_ENDIAN);
			text = byteBuffer.asCharBuffer();

			// TODO: Would it be faster to do this per method call?
			// lowercase the buffer
			if (lowercase) {
				toLowercase();
			}

		} catch (FileNotFoundException e) {
			throw new OkapiFileNotFoundException("Cannot create memory mapped file", e);
		} catch (IOException e) {
			throw new OkapiIOException("Cannot create memory mapped file", e);
		} finally {
			if (tempUTF16BEfile != null)
				tempUTF16BEfile.deleteOnExit();
			if (fc != null) {
				try {
					fc.close();
				} catch (IOException ignore) {
					// ignore
				}
			}
		}
	}

	/**
	 * Indicates whether this parse text contains the specified string at the
	 * specified position.
	 * <p>
	 * This method is analogous to the
	 * <code>java.lang.String.startsWith(String prefix, int toffset)</code>
	 * method.
	 * 
	 * @param str
	 *            a string.
	 * @param pos
	 *            the position (index) in this parse text at which to check for
	 *            the specified string.
	 * @return <code>true</code> if this parse text contains the specified
	 *         string at the specified position, otherwise <code>false</code>.
	 */
	public boolean containsAt(final String str, final int pos) {
		for (int i = 0; i < str.length(); i++)
			if (str.charAt(i) != text.get(pos + i))
				return false;
		return true;
	}

	/**
	 * Returns the character at the specified index.
	 * 
	 * @param index
	 *            the index of the character.
	 * @return the character at the specified index, which is always in lower
	 *         case.
	 */
	public char charAt(final int index) {
		return text.get(index);
	}

	/**
	 * Returns the index within this parse text of the first occurrence of the
	 * specified character, starting the search at the position specified by
	 * <code>fromIndex</code>.
	 * <p>
	 * If the specified character is not found then -1 is returned.
	 * 
	 * @param searchChar
	 *            a character.
	 * @param fromIndex
	 *            the index to start the search from.
	 * @return the index within this parse text of the first occurrence of the
	 *         specified character within the specified range, or -1 if the
	 *         character is not found.
	 */
	public int indexOf(final char searchChar, final int fromIndex) {
		return indexOf(searchChar, fromIndex, NO_BREAK);
	}

	/**
	 * Returns the index within this parse text of the first occurrence of the
	 * specified character, starting the search at the position specified by
	 * <code>fromIndex</code>, and breaking the search at the index specified by
	 * <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in
	 * the search.
	 * <p>
	 * If the search is to continue to the end of the text, the value
	 * {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the
	 * <code>breakAtIndex</code>.
	 * <p>
	 * If the specified character is not found then -1 is returned.
	 * 
	 * @param searchChar
	 *            a character.
	 * @param fromIndex
	 *            the index to start the search from.
	 * @param breakAtIndex
	 *            the index at which to break off the search, or
	 *            {@link #NO_BREAK} if the search is to continue to the end of
	 *            the text.
	 * @return the index within this parse text of the first occurrence of the
	 *         specified character within the specified range, or -1 if the
	 *         character is not found.
	 */
	public int indexOf(final char searchChar, final int fromIndex, final int breakAtIndex) {
		final int actualBreakAtIndex = (breakAtIndex == NO_BREAK || breakAtIndex > text.length() ? text.length()
				: breakAtIndex);
		for (int i = (fromIndex < 0 ? 0 : fromIndex); i < actualBreakAtIndex; i++)
			if (text.get(i) == searchChar)
				return i;
		return -1;
	}

	/**
	 * Returns the index within this parse text of the last occurrence of the
	 * specified character, searching backwards starting at the position
	 * specified by <code>fromIndex</code>.
	 * <p>
	 * If the specified character is not found then -1 is returned.
	 * 
	 * @param searchChar
	 *            a character.
	 * @param fromIndex
	 *            the index to start the search from.
	 * @return the index within this parse text of the last occurrence of the
	 *         specified character within the specified range, or -1 if the
	 *         character is not found.
	 */
	public int lastIndexOf(final char searchChar, final int fromIndex) {
		return lastIndexOf(searchChar, fromIndex, NO_BREAK);
	}

	/**
	 * Returns the index within this parse text of the last occurrence of the
	 * specified character, searching backwards starting at the position
	 * specified by <code>fromIndex</code>, and breaking the search at the index
	 * specified by <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in
	 * the search.
	 * <p>
	 * If the search is to continue to the start of the text, the value
	 * {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the
	 * <code>breakAtIndex</code>.
	 * <p>
	 * If the specified character is not found then -1 is returned.
	 * 
	 * @param searchChar
	 *            a character.
	 * @param fromIndex
	 *            the index to start the search from.
	 * @param breakAtIndex
	 *            the index at which to break off the search, or
	 *            {@link #NO_BREAK} if the search is to continue to the start of
	 *            the text.
	 * @return the index within this parse text of the last occurrence of the
	 *         specified character within the specified range, or -1 if the
	 *         character is not found.
	 */
	public int lastIndexOf(final char searchChar, final int fromIndex, final int breakAtIndex) {
		for (int i = (fromIndex > text.length() ? text.length() : fromIndex); i > breakAtIndex; i--)
			if (text.get(i) == searchChar)
				return i;
		return -1;
	}

	/**
	 * Returns the index within this parse text of the first occurrence of the
	 * specified string, starting the search at the position specified by
	 * <code>fromIndex</code>.
	 * <p>
	 * If the specified string is not found then -1 is returned.
	 * 
	 * @param searchString
	 *            a string.
	 * @param fromIndex
	 *            the index to start the search from.
	 * @return the index within this parse text of the first occurrence of the
	 *         specified string within the specified range, or -1 if the string
	 *         is not found.
	 */
	public int indexOf(final String searchString, final int fromIndex) {
		return (searchString.length() == 1) ? indexOf(searchString.charAt(0), fromIndex, NO_BREAK) : indexOf(
				searchString.toCharArray(), fromIndex, NO_BREAK);
	}

	/**
	 * Returns the index within this parse text of the first occurrence of the
	 * specified character array, starting the search at the position specified
	 * by <code>fromIndex</code>.
	 * <p>
	 * If the specified character array is not found then -1 is returned.
	 * 
	 * @param searchCharArray
	 *            a character array.
	 * @param fromIndex
	 *            the index to start the search from.
	 * @return the index within this parse text of the first occurrence of the
	 *         specified character array within the specified range, or -1 if
	 *         the character array is not found.
	 */
	public int indexOf(final char[] searchCharArray, final int fromIndex) {
		return indexOf(searchCharArray, fromIndex, NO_BREAK);
	}

	/**
	 * Returns the index within this parse text of the first occurrence of the
	 * specified string, starting the search at the position specified by
	 * <code>fromIndex</code>, and breaking the search at the index specified by
	 * <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in
	 * the search.
	 * <p>
	 * If the search is to continue to the end of the text, the value
	 * {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the
	 * <code>breakAtIndex</code>.
	 * <p>
	 * If the specified string is not found then -1 is returned.
	 * 
	 * @param searchString
	 *            a string.
	 * @param fromIndex
	 *            the index to start the search from.
	 * @param breakAtIndex
	 *            the index at which to break off the search, or
	 *            {@link #NO_BREAK} if the search is to continue to the end of
	 *            the text.
	 * @return the index within this parse text of the first occurrence of the
	 *         specified string within the specified range, or -1 if the string
	 *         is not found.
	 */
	public int indexOf(final String searchString, final int fromIndex, final int breakAtIndex) {
		return (searchString.length() == 1) ? indexOf(searchString.charAt(0), fromIndex, breakAtIndex) : indexOf(
				searchString.toCharArray(), fromIndex, breakAtIndex);
	}

	/**
	 * Returns the index within this parse text of the first occurrence of the
	 * specified character array, starting the search at the position specified
	 * by <code>fromIndex</code>, and breaking the search at the index specified
	 * by <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in
	 * the search.
	 * <p>
	 * If the search is to continue to the end of the text, the value
	 * {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the
	 * <code>breakAtIndex</code>.
	 * <p>
	 * If the specified character array is not found then -1 is returned.
	 * 
	 * @param searchCharArray
	 *            a character array.
	 * @param fromIndex
	 *            the index to start the search from.
	 * @param breakAtIndex
	 *            the index at which to break off the search, or
	 *            {@link #NO_BREAK} if the search is to continue to the end of
	 *            the text.
	 * @return the index within this parse text of the first occurrence of the
	 *         specified character array within the specified range, or -1 if
	 *         the character array is not found.
	 */
	public int indexOf(final char[] searchCharArray, final int fromIndex, final int breakAtIndex) {
		if (searchCharArray.length == 0)
			return fromIndex;
		final char firstChar = searchCharArray[0];
		final int lastPossibleBreakAtIndex = text.length() - searchCharArray.length + 1;
		final int actualBreakAtIndex = (breakAtIndex == NO_BREAK || breakAtIndex > lastPossibleBreakAtIndex) ? lastPossibleBreakAtIndex
				: breakAtIndex;
		outerLoop: for (int i = (fromIndex < 0 ? 0 : fromIndex); i < actualBreakAtIndex; i++) {
			if (text.get(i) == firstChar) {
				for (int j = 1; j < searchCharArray.length; j++)
					if (searchCharArray[j] != text.get(j + i))
						continue outerLoop;
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index within this parse text of the last occurrence of the
	 * specified string, searching backwards starting at the position specified
	 * by <code>fromIndex</code>.
	 * <p>
	 * If the specified string is not found then -1 is returned.
	 * 
	 * @param searchString
	 *            a string.
	 * @param fromIndex
	 *            the index to start the search from.
	 * @return the index within this parse text of the last occurrence of the
	 *         specified string within the specified range, or -1 if the string
	 *         is not found.
	 */
	public int lastIndexOf(final String searchString, final int fromIndex) {
		return (searchString.length() == 1) ? lastIndexOf(searchString.charAt(0), fromIndex, NO_BREAK) : lastIndexOf(
				searchString.toCharArray(), fromIndex, NO_BREAK);
	}

	/**
	 * Returns the index within this parse text of the last occurrence of the
	 * specified character array, searching backwards starting at the position
	 * specified by <code>fromIndex</code>.
	 * <p>
	 * If the specified character array is not found then -1 is returned.
	 * 
	 * @param searchCharArray
	 *            a character array.
	 * @param fromIndex
	 *            the index to start the search from.
	 * @return the index within this parse text of the last occurrence of the
	 *         specified character array within the specified range, or -1 if
	 *         the character array is not found.
	 */
	public int lastIndexOf(final char[] searchCharArray, final int fromIndex) {
		return lastIndexOf(searchCharArray, fromIndex, NO_BREAK);
	}

	/**
	 * Returns the index within this parse text of the last occurrence of the
	 * specified string, searching backwards starting at the position specified
	 * by <code>fromIndex</code>, and breaking the search at the index specified
	 * by <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in
	 * the search.
	 * <p>
	 * If the search is to continue to the start of the text, the value
	 * {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the
	 * <code>breakAtIndex</code>.
	 * <p>
	 * If the specified string is not found then -1 is returned.
	 * 
	 * @param searchString
	 *            a string.
	 * @param fromIndex
	 *            the index to start the search from.
	 * @param breakAtIndex
	 *            the index at which to break off the search, or
	 *            {@link #NO_BREAK} if the search is to continue to the start of
	 *            the text.
	 * @return the index within this parse text of the last occurrence of the
	 *         specified string within the specified range, or -1 if the string
	 *         is not found.
	 */
	public int lastIndexOf(final String searchString, final int fromIndex, final int breakAtIndex) {
		return (searchString.length() == 1) ? lastIndexOf(searchString.charAt(0), fromIndex, breakAtIndex)
				: lastIndexOf(searchString.toCharArray(), fromIndex, breakAtIndex);
	}

	/**
	 * Returns the index within this parse text of the last occurrence of the
	 * specified character array, searching backwards starting at the position
	 * specified by <code>fromIndex</code>, and breaking the search at the index
	 * specified by <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in
	 * the search.
	 * <p>
	 * If the search is to continue to the start of the text, the value
	 * {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the
	 * <code>breakAtIndex</code>.
	 * <p>
	 * If the specified character array is not found then -1 is returned.
	 * 
	 * @param searchCharArray
	 *            a character array.
	 * @param fromIndex
	 *            the index to start the search from.
	 * @param breakAtIndex
	 *            the index at which to break off the search, or
	 *            {@link #NO_BREAK} if the search is to continue to the start of
	 *            the text.
	 * @return the index within this parse text of the last occurrence of the
	 *         specified character array within the specified range, or -1 if
	 *         the character array is not found.
	 */
	public int lastIndexOf(final char[] searchCharArray, int fromIndex, final int breakAtIndex) {
		if (searchCharArray.length == 0)
			return fromIndex;
		final int rightIndex = text.length() - searchCharArray.length;
		if (breakAtIndex > rightIndex)
			return -1;
		if (fromIndex > rightIndex)
			fromIndex = rightIndex;
		final int lastCharIndex = searchCharArray.length - 1;
		final char lastChar = searchCharArray[lastCharIndex];
		final int actualBreakAtPos = breakAtIndex + lastCharIndex;
		outerLoop: for (int i = fromIndex + lastCharIndex; i > actualBreakAtPos; i--) {
			if (text.get(i) == lastChar) {
				final int startIndex = i - lastCharIndex;
				for (int j = lastCharIndex - 1; j >= 0; j--)
					if (searchCharArray[j] != text.get(j + startIndex))
						continue outerLoop;
				return startIndex;
			}
		}
		return -1;
	}

	/**
	 * Returns the length of the parse text.
	 * 
	 * @return the length of the parse text.
	 */
	public int length() {
		return text.length();
	}

	/**
	 * Returns a new string that is a substring of this parse text.
	 * <p>
	 * The substring begins at the specified <code>beginIndex</code> and extends
	 * to the character at index <code>endIndex</code> - 1. Thus the length of
	 * the substring is <code>endIndex-beginIndex</code>.
	 * 
	 * @param beginIndex
	 *            the begin index, inclusive.
	 * @param endIndex
	 *            the end index, exclusive.
	 * @return a new string that is a substring of this parse text.
	 */
	public String substring(final int beginIndex, final int endIndex) {
		return text.subSequence(beginIndex, endIndex).toString();
	}

	/**
	 * Returns a new character sequence that is a subsequence of this sequence.
	 * <p>
	 * This is equivalent to {@link #substring(int,int)
	 * substring(beginIndex,endIndex)}.
	 * 
	 * @param beginIndex
	 *            the begin index, inclusive.
	 * @param endIndex
	 *            the end index, exclusive.
	 * @return a new character sequence that is a subsequence of this sequence.
	 */
	public CharSequence subSequence(final int beginIndex, final int endIndex) {
		return substring(beginIndex, endIndex);
	}

	/**
	 * Returns the content of the parse text as a <code>String</code>.
	 * 
	 * @return the content of the parse text as a <code>String</code>.
	 */
	public String toString() {
		text.rewind();
		return text.toString();
	}
	
	public void toLowercase() {
		text.rewind();
		for (int i = 0; i < text.length(); i++)
			text.put(i, Character.toLowerCase(text.get(i)));
	}

	public char[] array() {
		return text.array();
	}
	
	/**
	 * General purpose static method which reads bytes from a Channel, decodes
	 * them according to the given charset
	 * 
	 * @param source
	 *            A ReadableByteChannel object which will be read to EOF as a
	 *            source of encoded bytes.
	 * @param writer
	 *            A Writer object to which decoded chars will be written.
	 * @param charset
	 *            A Charset object, whose CharsetDecoder will be used to do the
	 *            character set decoding.
	 */
	public static void decodeChannel(ReadableByteChannel source, Writer writer, Charset charset)
			throws UnsupportedCharsetException, IOException {
		// get a decoder instance from the Charset
		CharsetDecoder decoder = charset.newDecoder();

		// tell decoder to replace bad chars with default marker
		decoder.onMalformedInput(CodingErrorAction.REPORT);
		decoder.onUnmappableCharacter(CodingErrorAction.REPORT);

		// allocate radically different input and output buffer sizes
		// for testing purposes
		ByteBuffer bb = ByteBuffer.allocateDirect(16 * 1024);
		CharBuffer cb = CharBuffer.allocate(57);
		// buffer starts empty, indicate input is needed
		CoderResult result = CoderResult.UNDERFLOW;
		boolean eof = false;

		while (!eof) {
			// input buffer underflow, decoder wants more input
			if (result == CoderResult.UNDERFLOW) {
				// decoder consumed all input, prepare to refill
				bb.clear();

				// fill the input buffer, watch for EOF
				eof = (source.read(bb) == -1);

				// prepare the buffer for reading by decoder
				bb.flip();
			}

			// decode input bytes to output chars, pass EOF flag
			result = decoder.decode(bb, cb, eof);
			if (result.isError()) {
				throw new RuntimeException("Cannot map byte to char using charset: " + charset.displayName());
			}

			// if output buffer is full, drain output
			if (result == CoderResult.OVERFLOW) {
				drainCharBuf(cb, writer);
			}
		}

		// flush any remaining state from the decoder, being careful
		// to detect output buffer overflow(s).
		while (decoder.flush(cb) == CoderResult.OVERFLOW) {
			drainCharBuf(cb, writer);
		}

		// drain any chars remaining in the output buffer
		drainCharBuf(cb, writer);

		// close the channel, push out any buffered data to stdout
		// source.close();
		writer.flush();
	}

	/**
	 * Helper method to drain the char buffer and write its content to the given
	 * Writer object. Upon return, the buffer is empty and ready to be refilled.
	 * 
	 * @param cb
	 *            A CharBuffer containing chars to be written.
	 * @param writer
	 *            A Writer object to consume the chars in cb.
	 */
	static private void drainCharBuf(CharBuffer cb, Writer writer) throws IOException {
		cb.flip(); // prepare buffer for draining

		// This writes the chars contained in the CharBuffer but
		// doesn't actually modify the state of the buffer.
		// If the char buffer was being drained by calls to get(),
		// a loop might be needed here.
		if (cb.hasRemaining()) {
			writer.write(cb.toString());
		}

		cb.clear(); // prepare buffer to be filled again
	}

	public void close() {
		if (byteBuffer == null)
			return;
		tempUTF16BEfile.delete();
		/*
		 This code works around a bug that prevents temp files backed by a mem mapped buffer to be deleted. 
		 We will try to find an implementation that is Java VM independent. Comment out for now to prevent compiler errors.
		 
		AccessController.doPrivileged(new java.security.PrivilegedAction() {
			public Object run() {
				try {
					java.lang.reflect.Method getCleanerMethod = byteBuffer.getClass()
							.getMethod("cleaner", new Class[0]);
					getCleanerMethod.setAccessible(true);
					sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(byteBuffer, new Object[0]);
					cleaner.clean();					
					tempUTF16BEfile.delete();
					byteBuffer = null;
					System.gc();
				} catch (Exception e) {
					throw new OkapiIOException("Cannot close memory mapped file", e);
				}
				return null;
			}
		});*/
	}
}
