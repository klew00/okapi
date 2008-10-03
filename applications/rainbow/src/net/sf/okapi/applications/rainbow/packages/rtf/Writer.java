/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.applications.rainbow.packages.rtf;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import net.sf.okapi.applications.rainbow.packages.BaseWriter;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

public class Writer extends BaseWriter {
	
	private static final String   EXTENSION = ".rtf";

	private PrintWriter      writer;     
	private CharsetEncoder   outputEncoder;


	public String getPackageType () {
		return "rtf";
	}
	
	public String getReaderClass () {
		//TODO: Use dynamic name
		return "net.sf.okapi.applications.rainbow.packages.rtf.Reader";
	}
	
	@Override
	public void writeStartPackage () {
		manifest.setSourceLocation("work");
		manifest.setTargetLocation("work");
		manifest.setOriginalLocation("original");
		manifest.setDoneLocation("done");
		super.writeStartPackage();
	}

	@Override
	public void createDocument (int docID,
		String relativeSourcePath,
		String relativeTargetPath,
		String sourceEncoding,
		String targetEncoding,
		String filtersettings,
		IParameters filterParams)
	{
		relativeWorkPath = relativeSourcePath;
		relativeWorkPath += EXTENSION;
		outputEncoder = Charset.forName(targetEncoding).newEncoder();

		super.createDocument(docID, relativeSourcePath, relativeTargetPath,
			sourceEncoding, targetEncoding, filtersettings, filterParams);

		try {
			if ( writer != null ) {
				writer.close();
			}
			String path = manifest.getRoot() + File.separator
				+ ((manifest.getSourceLocation().length() == 0 ) ? "" : (manifest.getSourceLocation() + File.separator)) 
				+ relativeWorkPath;
			Util.createDirectories(path);
			writer = new PrintWriter(path, targetEncoding);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void writeEndDocument (Document resource) {
		writer.write(Util.RTF_ENDCODE+"}\n");
		writer.close();
		manifest.addDocument(docID, relativeWorkPath, relativeSourcePath,
			relativeTargetPath, sourceEncoding, targetEncoding, filterID);
	}

	public void writeTextUnit (TextUnit item,
		int status)
	{
		// Write the items in the TM if needed
		if ( item.hasTarget() ) {
			tmxWriter.writeItem(item);
			if ( item.hasChild() ) {
				for ( TextUnit tu : item.childTextUnitIterator() ) {
					if ( tu.hasTarget() ) {
						tmxWriter.writeItem(tu);
					}
				}
			}
		}

		// Output the text unit
		
/*		if ( item.getSkeletonBefore() != null ) {
			writer.write(Util.escapeToRTF(item.getSkeletonBefore().toString(),
				true, 1, outputEncoder));
		}
		StringBuilder tmp = new StringBuilder();
		tmp.append(Util.RTF_ENDCODE);
		if ( item.hasTarget() ) {
			//TODO: handle pre-populated target
		}
		else { // No target
			processContent(item.getSourceContent(), tmp,
				item.getSourceContent().isSegmented());
		}
		tmp.append(Util.RTF_STARTCODE);
		writer.write(tmp.toString());
		
		if ( item.getSkeletonAfter() != null ) {
			writer.write(Util.escapeToRTF(item.getSkeletonAfter().toString(),
				true, 1, outputEncoder));
		}
*/

		StringBuilder tmp = new StringBuilder();
		tmp.append(Util.RTF_ENDCODE);
		TextUnit tu;
		if ( item.hasChild() ) {
			for ( IContainable part : item.childUnitIterator() ) {
				if ( part instanceof TextUnit ) {
					tu = (TextUnit)part;
					if ( tu.hasTarget() ) {
						//TODO
					}
					else {
						processContent(tu.getSourceContent(), tmp,
							tu.getSourceContent().isSegmented());
					}
				}
				else if ( part instanceof SkeletonUnit ) {
					if ( SkeletonUnit.MAINTEXT.equals(part.getID()) ) {
						if ( item.hasTarget() ) {
							//TODO
						}
						else {
							processContent(item.getSourceContent(), tmp,
								item.getSourceContent().isSegmented());
						}
					}
					else {
						writer.write(Util.escapeToRTF(part.toString(),
							true, 1, outputEncoder));
					}
				}
			}
		}
		else {
			if ( item.hasTarget() ) {
				//TODO
			}
			else {
				processContent(item.getSourceContent(), tmp,
					item.getSourceContent().isSegmented());
			}
			
		}
		tmp.append(Util.RTF_STARTCODE);
		writer.write(tmp.toString());
	}

	private String processContent (TextFragment content,
		StringBuilder buffer,
		boolean isSegmented)
	{
		try {
			String text = content.getCodedText();
			CharBuffer tmpBuf = CharBuffer.allocate(1);
			ByteBuffer encBuf;
			Code code;
			// Cast to a TextContainer if needed
			TextContainer tc = null;
			if ( isSegmented ) {
				tc = (TextContainer)content;
			}
			
			for ( int i=0; i<text.length(); i++ ) {
				switch ( text.charAt(i) ) {
				case TextFragment.MARKER_OPENING:
				case TextFragment.MARKER_CLOSING:
					buffer.append(Util.RTF_STARTINLINE);
					code = content.getCode(text.charAt(++i));
					//TODO: handle sub-flows!!!
					buffer.append(Util.escapeToRTF(code.getData(), true, 2, outputEncoder));
					buffer.append(Util.RTF_ENDINLINE);
					break;
				case TextFragment.MARKER_ISOLATED:
				case TextFragment.MARKER_SEGMENT:
					//TODO: handle sub-flows!!!
					code = content.getCode(text.charAt(++i));
					if ( isSegmented ) {
						if ( code.getType().equals(TextContainer.CODETYPE_SEGMENT) ) {
							int index = Integer.valueOf(code.getData());
							buffer.append(Util.RTF_STARTMARKER);
							processContent(tc.getSegments().get(index), buffer, false);
							//TODO: case when a target is available (what kind of match?)
							buffer.append(Util.RTF_MIDMARKER1+"0"+Util.RTF_MIDMARKER2);
							//debug: buffer.append("{\\highlight7 ");
							//processContent(tc.getSegments().get(index), buffer, false);
							//debug: buffer.append("}");
							buffer.append(Util.RTF_ENDMARKER);
							break;
						}
						// Else: fall back to normal isolated in-line 
					}
					buffer.append(Util.RTF_STARTINLINE);
					buffer.append(Util.escapeToRTF(code.getData(), true, 2, outputEncoder));
					buffer.append(Util.RTF_ENDINLINE);
					break;
				case '{':
				case '}':
				case '\\':
					buffer.append("\\"+text.charAt(i));
					break;
				case '\r': // to skip
					break;
				case '\n':
					buffer.append("\r\n\\par ");
					break;
				case '\u00a0': // Non-breaking space
					buffer.append("\\~"); // No extra space (it's a control word)
					break;
				case '\t':
					buffer.append("\\tab ");
					break;
				case '\u2022':
					buffer.append("\\bullet ");
					break;
				case '\u2018':
					buffer.append("\\lquote ");
					break;
				case '\u2019':
					buffer.append("\\rquote ");
					break;
				case '\u201c':
					buffer.append("\\ldblquote ");
					break;
				case '\u201d':
					buffer.append("\\rdblquote ");
					break;
				case '\u2013':
					buffer.append("\\endash ");
					break;
				case '\u2014':
					buffer.append("\\emdash ");
					break;
				case '\u200d':
					buffer.append("\\zwj ");
					break;
				case '\u200c':
					buffer.append("\\zwnj ");
					break;
				case '\u200e':
					buffer.append("\\ltrmark ");
					break;
				case '\u200f':
					buffer.append("\\rtlmark ");
					break;
				default:
					if ( text.charAt(i) > 127 ) {
						if ( outputEncoder.canEncode(text.charAt(i)) ) {
							tmpBuf.put(0, text.charAt(i));
							tmpBuf.position(0);
							encBuf = outputEncoder.encode(tmpBuf);
							if ( encBuf.limit() > 1 ) {
								buffer.append(String.format("{\\uc%d",
									encBuf.limit()));
								buffer.append(String.format("\\u%d",
									(int)text.charAt(i)));
								for ( int j=0; j<encBuf.limit(); j++ ) {
									buffer.append(String.format("\\'%x",
										(encBuf.get(j)<0 ? (0xFF^~encBuf.get(j)) : encBuf.get(j)) ));
								}
								buffer.append("}");
							}
							else {
								buffer.append(String.format("\\u%d",
									(int)text.charAt(i)));
								buffer.append(String.format("\\'%x",
									(encBuf.get(0)<0 ? (0xFF^~encBuf.get(0)) : encBuf.get(0))));
							}
						}
						else { // Cannot encode in the RTF encoding, so use just Unicode
							buffer.append(String.format("\\u%d ?",
								(int)text.charAt(i)));
						}
					}
					else buffer.append(text.charAt(i));
					break;
				}
			}
			return buffer.toString();
		}
		catch ( CharacterCodingException e ) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void writeSkeletonUnit (SkeletonUnit resource) {
		writer.write(Util.escapeToRTF(resource.toString(), true, 1, outputEncoder));
	}
	
	public void writeStartDocument (Document resource) {
		//TODO: change codepage
		writer.write("{\\rtf1\\ansi\\ansicpg" + "1252" + "\\uc1\\deff1 \n"+
			"{\\fonttbl \n"+
			"{\\f1 \\fmodern\\fcharset0\\fprq1 Courier New;}\n"+
			"{\\f2 \\fswiss\\fcharset0\\fprq2 Arial;}\n"+
			"{\\f3 \\froman\\fcharset0\\fprq2 Times New Roman;}}\n"+
			"{\\colortbl \\red0\\green0\\blue0;\\red0\\green0\\blue0;\\red0\\green0\\blue255;"+
			"\\red0\\green255\\blue255;\\red0\\green255\\blue0;\\red255\\green0\\blue255;"+
			"\\red255\\green0\\blue0;\\red255\\green255\\blue0;\\red255\\green255\\blue255;"+
			"\\red0\\green0\\blue128;\\red0\\green128\\blue128;\\red0\\green128\\blue0;"+
			"\\red128\\green0\\blue128;\\red128\\green0\\blue0;\\red128\\green128\\blue0;"+
			"\\red128\\green128\\blue128;\\red192\\green192\\blue192;}\n"+
			"{\\stylesheet \n"+
			"{\\s0 \\sb80\\slmult1\\widctlpar\\fs20\\f1 \\snext0 Normal;}\n"+
			"{\\cs1 \\additive \\v\\cf12\\sub\\f1 tw4winMark;}\n"+
			"{\\cs2 \\additive \\cf4\\fs40\\f1 tw4winError;}\n"+
			"{\\cs3 \\additive \\f1\\cf11 tw4winPopup;}\n"+
			"{\\cs4 \\additive \\f1\\cf10 tw4winJump;}\n"+
			"{\\cs5 \\additive \\cf15\\f1\\lang1024\\noproof tw4winExternal;}\n"+
			"{\\cs6 \\additive \\cf6\\f1\\lang1024\\noproof tw4winInternal;}\n"+
			"{\\cs7 \\additive \\cf2 tw4winTerm;}\n"+
			"{\\cs8 \\additive \\cf13\\f1\\lang1024\\noproof DO_NOT_TRANSLATE;}\n"+
			"{\\cs9 \\additive Default Paragraph Font;}"+
			"{\\cs15 \\additive \\v\\f1\\cf12\\sub tw4winMark;}"+
			"}\n"+
			"\\paperw11907\\paperh16840\\viewkind4\\viewscale100\\pard\\plain\\s0\\sb80\\slmult1\\widctlpar\\fs20\\f1 \n"+
			Util.RTF_STARTCODE);
	}
}
