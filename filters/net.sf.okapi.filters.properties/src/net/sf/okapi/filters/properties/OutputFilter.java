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

package net.sf.okapi.filters.properties;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;

public class OutputFilter implements IOutputFilter {

	private Resource              res;
	private OutputStream          output;
	private String                encoding;
	private OutputStreamWriter    writer;
	private CharsetEncoder        outputEncoder;
	
	public void initialize (OutputStream output,
		String encoding,
		String targetLanguage) {
		this.output = output;
		this.encoding = encoding;
		// Not used: targetLanguage
	}

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

	public void endContainer (Group resource) {
	}

	public void endExtractionItem (TextUnit item) {
		try {
			// Then write the item content
			if ( item.hasTarget() ) {
				writer.write(escape(item.getTarget().toString()));
			}
			else {
				writer.write(escape(item.getSource().toString()));
			}
			if ( res.endingLB ) writer.write(res.lineBreak);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void endResource (Document resource) {
		try {
			writer.close();
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void startContainer (Group resource) {
	}

	public void startExtractionItem (TextUnit item) {
	}

	public void startResource (Document resource) {
		try {
			// Save the resource for later use
			res = (Resource)resource;
			
			// Create the output writer from the provided stream
			writer = new OutputStreamWriter(
				new BufferedOutputStream(output), encoding);
			outputEncoder = Charset.forName(encoding).newEncoder();
			Util.writeBOMIfNeeded(writer, true, encoding);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

    public void skeletonContainer (SkeletonUnit resource) {
    	try {
    		writer.write(resource.toString());
    	}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
    }
    
	private String escape (String text) {
		StringBuilder escaped = new StringBuilder();
		for ( int i=0; i<text.length(); i++ ) {
			if ( text.codePointAt(i) > 127 ) {
				if ( res.params.escapeExtendedChars ) {
					escaped.append(String.format("\\u%04x", text.codePointAt(i))); 
				}
				else {
					if ( outputEncoder.canEncode(text.charAt(i)) )
						escaped.append(text.charAt(i));
					else
						escaped.append(String.format("\\u%04x", text.codePointAt(i)));
				}
			}
			else {
				switch ( text.charAt(i) ) {
				case '\n':
					escaped.append("\\n");
					break;
				case '\t':
					escaped.append("\\t");
					break;
				default:
					escaped.append(text.charAt(i));
					break;
				}
			}
		}
		return escaped.toString();
	}

}
