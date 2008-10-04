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

package net.sf.okapi.filters.openoffice;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
//import java.nio.charset.Charset;
//import java.nio.charset.CharsetEncoder;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;

public class OutputFilter implements IOutputFilter {

	private OutputStream          output;
	private String                encoding;
	private OutputStreamWriter    writer;
//	private CharsetEncoder        outputEncoder;
	

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
		//TODO: need escape for the text parts
		try {
			if ( item.hasChild() ) {
				TextUnit tu;
				for ( IContainable part : item.childUnitIterator() ) {
					if ( part instanceof TextUnit ) {
						tu = (TextUnit)part;
						if ( tu.hasTarget() ) {
							writer.write(tu.getTarget().toString());
						}
						else {
							writer.write(tu.getSource().toString());
						}
					}
					else if ( part instanceof SkeletonUnit ) {
						if ( SkeletonUnit.MAINTEXT.equals(part.getID()) ) {
							if ( item.hasTarget() ) {
								writer.write(item.getTarget().toString());
							}
							else {
								writer.write(item.getSource().toString());
							}
						}
						else writer.write(part.toString());
					}
				}
			}
			else {
				if ( item.hasTarget() ) {
					writer.write(item.getTarget().toString());
				}
				else {
					writer.write(item.getSource().toString());
				}
			}
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
			// Create the output writer from the provided stream
			writer = new OutputStreamWriter(
				new BufferedOutputStream(output), encoding);
//			outputEncoder = Charset.forName(encoding).newEncoder();
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
    
}
