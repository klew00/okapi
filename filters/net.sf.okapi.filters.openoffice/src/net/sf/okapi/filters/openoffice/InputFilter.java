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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
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
	private String commonPart = null;
	

	public InputFilter () {
		parser = new Parser();
	}
	
	public void close () {
		if ( parser != null ) {
			parser.close();
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
		this.input = input;
		parser.resource.setName(name);
		parser.resource.setFilterSettings(filterSettings);
		parser.resource.setSourceEncoding(encoding);
		parser.resource.setSourceLanguage(sourceLanguage);
		parser.resource.setTargetLanguage(targetLanguage);
		//TODO: Get the real target/output encoding from parameters
		parser.resource.setTargetEncoding(encoding);
		// Set the location of the original file so the output filter can use it.
		parser.resource.setProperty("originalPath", inputPath);

		// Extract or no the comments
		final String annotationTag = "office:annotation";
		if ( parser.resource.params.extractComments ) {
			parser.inlineRef.remove(annotationTag);
		}
		else {
			if ( !parser.inlineRef.contains("parser.inlineRef") ) {
				parser.inlineRef.add(annotationTag);
			}
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
			close();
			
			// Unzip the meta file and get the sub-documents names
			ArrayList<DocumentEntry> subDocs = unzipContent(parser.resource.getName());
			// Set the location of the temporary unzipped folder, so the output filter can use it.
			parser.resource.setProperty("tmpUnzippedFolder", commonPart);
			
			// Send start doc event
			output.startResource(parser.resource);

			// process the sub-document
			for ( DocumentEntry subDoc : subDocs ) {
				processSubDocument(subDoc);
			}
			
			// Send end doc event
			output.endResource(parser.resource);
		}
		finally {
			close();
		}
	}
	
	private void processSubDocument (DocumentEntry subDoc) {
		try {
			if ( input != null ) {
				input.close();
				input = null;
			}
			input = new FileInputStream(subDoc.path); 
			parser.open(input);
			
			Group subDocResource = new Group();
			subDocResource.setName(subDoc.path);
			subDocResource.setType(subDoc.docType);
			
			// Get started
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
				}
			}
			while ( tok != ParserTokenType.ENDINPUT );

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
	
	
	private ArrayList<DocumentEntry> unzipContent (String path) {
		BufferedOutputStream dest = null;
		BufferedInputStream is = null;
		ArrayList<DocumentEntry> list = new ArrayList<DocumentEntry>();
		try {
			//TODO: generate more unique temp
			commonPart = Util.getTempDirectory() + File.separator + Util.getFilename(path, true);
			ZipEntry entry;
			ZipFile zipfile = new ZipFile(path);
			Enumeration<? extends ZipEntry> entries = zipfile.entries();
			
			while( entries.hasMoreElements() ) {
				entry = entries.nextElement();
				DocumentEntry docEntry = new DocumentEntry();
				if ( entry.getName().equals("content.xml") ) {
					docEntry.path = commonPart + "." + entry.getName();
					docEntry.docType = entry.getName();
					list.add(docEntry);
				}
				else if ( entry.getName().equals("meta.xml") ) {
					docEntry.path = commonPart + "." + entry.getName();
					docEntry.docType = entry.getName();
					list.add(docEntry);
				}
				else if ( entry.getName().equals("styles.xml") ) {
					docEntry.path = commonPart + "." + entry.getName();
					docEntry.docType = entry.getName();
					list.add(docEntry);
				}
				else continue;
				
				Util.createDirectories(docEntry.path);
				
				// If it's a file, unzip it
				is = new BufferedInputStream(zipfile.getInputStream(entry));
				FileOutputStream fos = new FileOutputStream(docEntry.path);
				int count;
				byte data[] = new byte[BUFFER_SIZE];
				dest = new BufferedOutputStream(fos, BUFFER_SIZE);
				while ( (count = is.read(data, 0, BUFFER_SIZE)) != -1 ) {
					dest.write(data, 0, count);
				}
				dest.flush();
			}
			return list;
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( dest != null ) dest.close();
				if ( is != null ) is.close();
			}
			catch ( IOException e ) {
				throw new RuntimeException(e);
			}
		}
	}

}
