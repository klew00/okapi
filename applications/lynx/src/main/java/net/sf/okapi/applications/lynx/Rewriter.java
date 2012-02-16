/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.applications.lynx;

import java.io.File;
import java.util.List;

import net.sf.okapi.lib.xliff.DocumentData;
import net.sf.okapi.lib.xliff.Fragment;
import net.sf.okapi.lib.xliff.GroupData;
import net.sf.okapi.lib.xliff.Part;
import net.sf.okapi.lib.xliff.SectionData;
import net.sf.okapi.lib.xliff.Segment;
import net.sf.okapi.lib.xliff.Unit;
import net.sf.okapi.lib.xliff.XLIFFEvent;
import net.sf.okapi.lib.xliff.XLIFFReader;
import net.sf.okapi.lib.xliff.XLIFFWriter;

public class Rewriter {
	
	private boolean verbose = false;
	private int outputStyle = Fragment.STYLE_DATAINSIDE;
	private boolean rewrite = false;
	
	public Rewriter (boolean verbose,
		boolean rewrite,
		int outputStyle)
	{
		this.verbose = verbose;
		this.rewrite = rewrite;
		this.outputStyle = outputStyle;
	}
	
	public void process (List<File> list) {
		// Else: process the input files
		XLIFFReader reader = new XLIFFReader();
		XLIFFWriter writer = null;

		try {
			File output = null;
			for ( File input : list ) {
				// Show the input path
				System.out.println(" Input: "+input.getAbsolutePath());

				if ( rewrite ) {
					// Compute the output path
					String path = input.getAbsolutePath();
					String ext = "";
					int n = path.lastIndexOf('.');
					if ( n > -1 ) {
						ext = path.substring(n);
						path = path.substring(0, n);
					}
					output = new File(path+".out"+ext);
					// Display the output path
					System.out.println("Output: "+output.getAbsolutePath());
				}
				
				// Open the input and create the output
				reader.open(input.toURI());
				
				if ( rewrite ) {
					writer = new XLIFFWriter();
					writer.setInlineStyle(outputStyle);
					writer.create(output, null);
				}
				
				while ( reader.hasNext() ) {
					// Get the event
					XLIFFEvent event = reader.next();
					
					// Display the trace
					switch ( event.getType() ) {
					case START_DOCUMENT:
						DocumentData dd = event.getDocumentData();
						printLine("Start document");
						printLine(String.format("source language=%s", dd.getSourceLanguage()));
						printLine(String.format("target language=%s", dd.getTargetLanguage()==null ? "<none declated>" : dd.getTargetLanguage()));
						break;
					case START_SECTION:
						SectionData sd = event.getSectionData();
						printLine(String.format("Start section (file id=%s):", sd.getId()));
						printLine(String.format("original=%s", sd.getOriginal()));
						break;
					case START_GROUP:
						GroupData gd = event.getGroupData();
						printLine(String.format("Start group (id=%s):", gd.getId()));
						printLine(String.format("type=%s", gd.getType()));
						break;
					case TEXT_UNIT:
						Unit unit = event.getUnit();
						printLine(String.format("Unit (id=%s):", unit.getId()));
						for ( Part part : unit ) {
							printPart(part);
						}
						break;
					case END_GROUP:
						printLine("End group");
						break;
					case END_SECTION:
						printLine("End section");
						break;
					case END_DOCUMENT:
						printLine("End document");
						System.out.println("--------------------");
						break;
					}
					
					// Re-write the event
					if ( writer != null ) {
						writer.writeEvent(event);
					}
				}
			}
		}
		finally {
			if ( reader != null ) {
				reader.close();
			}
			if ( writer != null ) {
				writer.close();
			}
		}
	}

	private void printLine (String text) {
		if ( verbose ) {
			System.out.println(text);
		}
	}
	
	private void printPart (Part part) {
		if ( !verbose ) return;
		if ( part instanceof Segment ) {
			Segment seg = (Segment)part;
			System.out.println(String.format(" segment (id=%s):", seg.getId()));
			System.out.print("  source=");
			System.out.println("["+seg.getSource().toXLIFF(outputStyle)+"]");
			System.out.print("  target=");
			if ( seg.hasTarget() ) {
				System.out.println("["+seg.getTarget(false).toXLIFF(outputStyle)+"]");
			}
			else {
				System.out.println("<no target defined>");
			}
		}
		else {
			System.out.println(" ignorable:");
			System.out.print("  source=");
			System.out.println("["+part.getSource().toXLIFF(outputStyle)+"]");
			System.out.print("  target=");
			if ( part.hasTarget() ) {
				System.out.println("["+part.getTarget(false).toXLIFF(outputStyle)+"]");
			}
			else {
				System.out.println("<no target defined>");
			}
		}
		
	}

}
