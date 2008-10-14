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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.resource.Document;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;

public class OutputFilter implements IOutputFilter {

	static final int BUFFER_SIZE = 2048;

	private String encoding;
	private OutputStreamWriter writer;
	private String outputPath;
	private ArrayList<String> subDocs;
	private String originalPath;
	private String tmpUnzippedFolder;
	private byte[] buffer;
	private Escaper escaper;
	
	public OutputFilter () {
		escaper = new Escaper();
	}

	public void initialize (OutputStream output,
		String outputPath,
		String encoding,
		String targetLanguage)
	{
		try {
			// Make sure we don't have an output open
			if ( output != null ) {
				output.close();
				output = null;
			}
			
			this.outputPath = outputPath;
			this.encoding = "UTF-8";
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
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
							writer.write(escaper.escape(tu.getTarget().getContent(), false));
						}
						else {
							writer.write(escaper.escape(tu.getSource().getContent(), false));
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
					writer.write(escaper.escape(item.getTarget().getContent(), false));
				}
				else {
					writer.write(escaper.escape(item.getSource().getContent(), false));
				}
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void endResource (Document resource) {
		try {
			// Make sure all is closed
			if ( writer != null ) {
				writer.close();
				writer = null;
			}
			// Re-zip the files from the temporary unzipped folder
			// and create the output zipped file
			rezipContent();
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void startContainer (Group resource) {
		try {
			//TODO: make distinction between groups and sub-documents at a high level
			if ( writer != null ) {
				writer.close();
				writer = null;
			}

			// Temporary output file
			String tmpOutputPath = tmpUnzippedFolder + ".out-" + resource.getType();
			subDocs.add(resource.getType());
			
			// Create the output writer from the provided stream
			OutputStream out = new FileOutputStream(tmpOutputPath);
			writer = new OutputStreamWriter(
				new BufferedOutputStream(out), encoding);
			Util.writeBOMIfNeeded(writer, true, encoding);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void startExtractionItem (TextUnit item) {
	}

	public void startResource (Document resource) {
		subDocs = new ArrayList<String>();
		tmpUnzippedFolder = resource.getProperty("tmpUnzippedFolder");
		originalPath = resource.getProperty("originalPath");
	}

    public void skeletonContainer (SkeletonUnit resource) {
    	try {
    		writer.write(resource.toString());
    	}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
    }

	/**
	 * Re-create the zipped file based on the original.
	 * @throws IOException 
	 * @throws ZipException 
	 */
	private void rezipContent () throws ZipException, IOException {
		ZipOutputStream zipout = null;
		InputStream in = null;
		try {
			buffer = new byte[BUFFER_SIZE];
			File inFile = new File(originalPath);
			ZipFile zipfile = new ZipFile(inFile);
			File outFile = new File(outputPath);
			zipout = new ZipOutputStream(new FileOutputStream(outFile));
			Enumeration<? extends ZipEntry> entries = zipfile.entries();
			
			while ( entries.hasMoreElements() ) {
				ZipEntry entry = entries.nextElement();
				if ( subDocs.contains(entry.getName()) ) {
					String transPath = tmpUnzippedFolder + ".out-" + entry.getName();
					in = new FileInputStream(transPath);
				}
				else {
					in = zipfile.getInputStream(entry);
				}
				// Copy the data into the new zip file
				ZipEntry outentry = new ZipEntry(entry.getName());
				zipout.putNextEntry(outentry);
				copy(in, zipout);
				in.close();
				in = null;
				zipout.closeEntry();
			}
		}
		finally {
			if ( in != null ) in.close();
			if ( zipout != null ) zipout.close();
		}
	}
	
	private void copy (InputStream fromStream,
		OutputStream toStream)
		throws IOException
	{
		int count; 
		while( (count = fromStream.read(buffer)) > 0 ) { 
			toStream.write(buffer, 0, count); 
		}
	}

}
