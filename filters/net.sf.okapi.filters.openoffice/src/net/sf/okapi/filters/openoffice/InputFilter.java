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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.filters.IParser.ParserTokenType;
import net.sf.okapi.common.pipeline.IResourceBuilder;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;

public class InputFilter implements IInputFilter {

	static final int         BUFFER_SIZE = 2048;
	
	private InputStream           input;
	private IResourceBuilder      output;
	private Parser                parser;
	

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
			
			// Unzip the content.xml and make it the input
			/*NOT ready yet String[] contentPaths = unzipContent(parser.resource.getName());
			
			//TODO: stream need to be open in filter, not before
			input.close();
			input = new FileInputStream(contentPaths[0]); 
			*/
			
			parser.open(input);
			
			// Get started
			output.startResource(parser.resource);
			
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

			output.endResource(parser.resource);
		}
//		catch ( IOException e ) {
//			throw new RuntimeException(e);
//		}
		finally {
			close();
		}
	}

	public void setOutput (IResourceBuilder builder) {
		output = builder;
	}

	public void cancel () {
		if ( parser != null ) parser.cancel();
	}
	
	
	private String[] unzipContent (String path) {
		BufferedOutputStream dest = null;
		BufferedInputStream is = null;
		String tmpDir = null;
		try {
			tmpDir = Util.getTempDirectory() + File.separator + Util.getFilename(path, true);
			String[] outPaths = new String[2];
			int out = 0;
			ZipEntry entry;
			ZipFile zipfile = new ZipFile(path);
			Enumeration e = zipfile.entries();
			
			while( e.hasMoreElements() ) {
				entry = (ZipEntry)e.nextElement();
				if ( entry.getName().equals("content.xml") ) {
					out = 0;
				}
				else if ( entry.getName().equals("meta.xml") ) {
					out = 1;
				}
				else continue; // Skip the others
				outPaths[out] = tmpDir + File.separator + entry.getName();
				Util.createDirectories(outPaths[out]);
				is = new BufferedInputStream(zipfile.getInputStream(entry));
				FileOutputStream fos = new FileOutputStream(outPaths[0]);
				int count;
				byte data[] = new byte[BUFFER_SIZE];
				dest = new BufferedOutputStream(fos, BUFFER_SIZE);
				while ( (count = is.read(data, 0, BUFFER_SIZE)) != -1 ) {
					dest.write(data, 0, count);
				}
				dest.flush();
			}
			return outPaths;
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
