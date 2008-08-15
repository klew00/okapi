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

package net.sf.okapi.filters.regex;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;

public class OutputFilter implements IOutputFilter {
	
	private OutputStream          output;
	private OutputStreamWriter    writer;
//	private CharsetEncoder        outputEncoder;
	//private final Logger          logger = LoggerFactory.getLogger("net.sf.okapi.logging");


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
		String targetLanguage)
	{
		this.output = output;
	}

	public void endContainer (Group resourceContainer) {
	}

	private void buildContent (TextUnit item) {
		try {
			if ( item.hasTarget() ) {
				writer.write(item.getTarget().toString());
			}
			else {
				writer.write(item.getSource().toString());
			}
		}
		catch ( IOException e) {
			throw new RuntimeException(e);
		}
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
			// Create the output writer from the provided stream
			writer = new OutputStreamWriter(
				new BufferedOutputStream(output), resource.getTargetEncoding());
			//TODO: maybe the outputEncoder won't be needed?
			//outputEncoder = Charset.forName(resource.getTargetEncoding()).newEncoder(); 
			Util.writeBOMIfNeeded(writer, true, resource.getTargetEncoding());
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

    public void skeletonContainer (SkeletonUnit resource) {
    	try {
    		//TODO: Handle line-break type, we need to output the original
    		writer.write(resource.toString());
    	}
    	catch ( IOException e ) {
    		throw new RuntimeException(e);
    	}
    }
    
}
