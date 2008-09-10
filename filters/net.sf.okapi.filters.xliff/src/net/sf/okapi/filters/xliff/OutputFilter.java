/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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

package net.sf.okapi.filters.xliff;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.InvalidContentException;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;

public class OutputFilter implements IOutputFilter {
	
	private OutputStream          output;
	private OutputStreamWriter    writer;
	private XLIFFContent          xliffCont;
	private Resource              res;
	private CharsetEncoder        outputEncoder;
	private final Logger          logger = LoggerFactory.getLogger("net.sf.okapi.logging");


	public void close () {
		try {
			if ( writer != null ) {
				writer.close();
				writer = null;
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void initialize (OutputStream output,
		String encoding,
		String targetLanguage) {
		this.output = output;
		xliffCont = new XLIFFContent();
	}

	public void endContainer (Group resourceContainer) {
	}

	private void buildContent (TextUnit item) {
		try {
			if ( res.needTargetElement ) {
				writer.write(String.format("<target xml:lang=\"%s\">", res.getTargetLanguage()));
				// We did not have a target, so we get the inlines from the source, as the
				// new target is likely to be obtain from the source
				//TODO: This does not resolve all case, some target may be generated from another file, need to handle that
				res.trgCodes = res.srcCodes;
			}

			// We reset the in-line code here to use the full-outer XML, rather than
			// the codes with just the inner portion
			try {
				TextContainer content;
				if ( item.hasTarget() ) {
					content = item.getTargetContent();
					content.setCodedText(content.getCodedText(), res.trgCodes, false);
				}
				else {
					content = item.getSourceContent();
					content.setCodedText(content.getCodedText(), res.srcCodes, false);
				}
				String tmp = xliffCont.setContent(content).toString(0, false, true);
				writer.write(escapeChars(tmp));
			}
			catch ( InvalidContentException e ) {
				logger.error(String.format("Inline code problem in item id=\"%s\" (resname=\"%s\"):",
					item.getID(), item.getName()), e);
				logger.info("Content: ["+item.toString()+"]");
			}
				
			if ( res.needTargetElement ) {
				writer.write("</target>\n");
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	private String escapeChars (String text) {
		StringBuilder escaped = new StringBuilder(text.length());
		for ( int i=0; i<text.length(); i++ ) {
			if ( outputEncoder.canEncode(text.charAt(i)) )
				escaped.append(text.charAt(i));
			else
				escaped.append(String.format("&#x%04x;", text.codePointAt(i)));
		}
		return escaped.toString();
	}
	
	public void endExtractionItem (TextUnit item) {
		if ( item.isTranslatable() ) {
			buildContent(item);
		}
	}

	public void endResource (Document resource) {
		close();
	}

	public void startContainer (Group resource) {
	}

	public void startExtractionItem (TextUnit item) {
	}

	public void startResource (Document resource) {
		try {
			res = (Resource)resource;
			// Create the output writer from the provided stream
			writer = new OutputStreamWriter(
				new BufferedOutputStream(output), res.getTargetEncoding());
			Util.writeBOMIfNeeded(writer, true, res.getTargetEncoding());
			writer.write("<?xml version=\"1.0\" encoding=\""
				+ res.getTargetEncoding() + "\"?>");
			outputEncoder = Charset.forName(res.getTargetEncoding()).newEncoder(); 
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

    public void skeletonContainer (SkeletonUnit resource) {
    	try {
    		writer.write(escapeChars(resource.toString()));
    	}
    	catch ( IOException e ) {
    		throw new RuntimeException(e);
    	}
    }
    
}
