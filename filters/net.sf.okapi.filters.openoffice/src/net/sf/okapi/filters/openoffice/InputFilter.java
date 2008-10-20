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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.filters.IParser.ParserTokenType;
import net.sf.okapi.common.pipeline.IResourceBuilder;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;

public class InputFilter implements IInputFilter {

	static final int         BUFFER_SIZE = 2048;
	
	private InputStream input;
	private IResourceBuilder output;
	private Parser parser;
	private ZipFile zipFile = null;
	

	public InputFilter () {
		parser = new Parser();
	}
	
	public void close () {
		try {
			if ( zipFile != null ) {
				zipFile.close();
				zipFile = null;
			}
			if ( parser != null ) {
				parser.close();
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public IParameters getParameters () {
		return parser.resource.getParameters();
	}

	public void initialize (InputStream input,
		String inputPath,
		String name,
		String filterSettings,
		String encoding,
		String sourceLanguage,
		String targetLanguage)
	{
		close();
		this.input = input; // Not used directly (should be changed in future)
		parser.resource.setName(name);
		parser.resource.setFilterSettings(filterSettings);
		parser.resource.setSourceEncoding("UTF-8"); // Always
		parser.resource.setSourceLanguage(sourceLanguage);
		parser.resource.setTargetLanguage(targetLanguage);
		//TODO: Get the real target/output encoding from parameters
		parser.resource.setTargetEncoding("UTF-8"); // Always
		// Set the location of the original file so the output filter can use it.
		parser.resource.setProperty("originalPath", inputPath);

		// Set flags to extract or no the comments
		final String annotationTag = "office:annotation";
		if ( parser.resource.params.extractComments ) {
			parser.toProtect.remove(annotationTag);
		}
		else if ( !parser.toProtect.contains(annotationTag) ) {
			parser.toProtect.add(annotationTag);
		}
	}

	public boolean supports (int feature) {
		switch ( feature ) {
		case FEATURE_TEXTBASED:
			return true;
		default:
			return false;
		}
	}

	public void process () {
		try {
			// Send start doc event
			output.startResource(parser.resource);

			// Open the zip file
			zipFile = new ZipFile(parser.resource.getName());
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			
			// Find the relevant zip entries and process them
			ZipEntry entry;
			while( entries.hasMoreElements() ) {
				entry = entries.nextElement();
				if ( entry.getName().equals("content.xml") ) {
					processSubDocument(entry);
				}
				else if ( entry.getName().equals("meta.xml") ) {
					processSubDocument(entry);
				}
				else if ( entry.getName().equals("styles.xml") ) {
					processSubDocument(entry);
				}
				else continue;
			}

			// Send end document event
			output.endResource(parser.resource);
			
			close();
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		finally {
			close(); // Make sure we free resources
		}
	}

	private void processSubDocument (ZipEntry zipEntry)
	{
		try {
			if ( input != null ) {
				input.close();
				input = null;
			}
			
			// Get the input stream
			input = new BufferedInputStream(zipFile.getInputStream(zipEntry));
			parser.open(input);
			
			// Send the start sub-document event
			//TODO: Use real sub-doc even when available
			Group subDocResource = new Group();
			subDocResource.setName(zipEntry.getName());
			subDocResource.setType(zipEntry.getName());
			output.startContainer(subDocResource);
			
			// Process
			ParserTokenType tok;
			do {
				switch ( (tok = parser.parseNext()) ) {
				case TRANSUNIT:
					output.startExtractionItem((TextUnit)parser.getResource());
					output.endExtractionItem((TextUnit)parser.getResource());
					break;
				case SKELETON:
					output.skeletonContainer((SkeletonUnit)parser.getResource());
					break;
				case STARTGROUP:
					output.startContainer((Group)parser.getResource());
					break;
				case ENDGROUP:
					output.endContainer((Group)parser.getResource());
					break;
				}
			}
			while ( tok != ParserTokenType.ENDINPUT );

			// Send the end sub-document even
			output.endContainer(subDocResource);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		finally {
			if ( input != null ) {
				try {
					input.close();
					input = null;
				}
				catch ( IOException e ) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public void setOutput (IResourceBuilder builder) {
		output = builder;
	}

	public void cancel () {
		if ( parser != null ) parser.cancel();
	}

}
