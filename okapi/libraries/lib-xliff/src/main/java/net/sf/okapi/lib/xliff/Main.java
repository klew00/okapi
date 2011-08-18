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

package net.sf.okapi.lib.xliff;

import java.io.File;
import java.util.ArrayList;

/**
 * Tool to test XLIFF input and output.
 */
public class Main {

	private int style = Fragment.STYLE_DATAINSIDE;
	private boolean verbose = false;

	public static void main (String[] args) {
		
		Main main = new Main();
		XLIFFReader reader;
		XLIFFWriter writer;

		System.out.println("============================================================");
		System.out.println("XLIFF 2.0 Read/Write Test Tool");
		
		boolean showUsage = false;
		int outputStyle = Fragment.STYLE_NODATA;
		
		ArrayList<File> list = new ArrayList<File>();
		
		for ( String arg : args ) {
			String opt = arg.toLowerCase();
			if ( opt.equals("-verbose") ) {
				main.setVerbose(true);
			}
			else if ( opt.equals("-inside") ) {
				outputStyle = Fragment.STYLE_DATAINSIDE;
			}
			else if ( opt.equals("-outside") ) {
				outputStyle = Fragment.STYLE_DATAOUTSIDE;
			}
			else if ( arg.equals("-?") || opt.equals("-h") ) {
				showUsage = true;
			}
			else if ( arg.startsWith("-") ) {
				System.out.println("Invalid option: "+arg);
				showUsage = true;
			}
			else {
				list.add(new File(arg));
			}
		}
		
		if ( showUsage || list.isEmpty() ) {
			System.out.println("Parameters: [options] file1 file2 ...");
			System.out.println("Where options are:");
			System.out.println("-trace:   show the details of the documents being read");
			System.out.println("-outside: in the output, store original data of codes outside the content");
			System.out.println("-inside:  in the output, store original data of codes inside the content");
			System.out.println("-? or -h = shows this help");
			return; // Stop here
		}
		
		// Else: process the input files
		reader = new XLIFFReader();
			
		for ( File input : list ) {
			// Compute the output path
			String path = input.getAbsolutePath();
			String ext = "";
			int n = path.lastIndexOf('.');
			if ( n > -1 ) {
				ext = path.substring(n);
				path = path.substring(0, n);
			}
			File output = new File(path+".out"+ext);
			// Show the input and output paths
			System.out.println("------------------------------------------------------------");
			System.out.println(" Input: "+input.getAbsolutePath());
			System.out.println("Output: "+output.getAbsolutePath());
			
			// Open the input and create the output
			reader.open(input.toURI());
			writer = new XLIFFWriter();
			writer.setInlineStyle(outputStyle);
			writer.create(output, null);
			
			while ( reader.hasNext() ) {
				// Get the event
				XLIFFEvent event = reader.next();
				
				// Display the trace
				switch ( event.getType() ) {
				case START_DOCUMENT:
					main.printLine("Start document");
					break;
				case START_SECTION:
					SectionData sd = event.getSectionData();
					main.printLine(String.format("Start section (file id=%s):", sd.getId()));
					main.printLine(String.format("original=%s", sd.getOriginal()));
					main.printLine(String.format("source language=%s", sd.getSourceLanguage()));
					main.printLine(String.format("target language=%s", sd.getTargetLanguage()));
					break;
				case START_GROUP:
					GroupData gd = event.getGroupData();
					main.printLine(String.format("Start group (id=%s):", gd.getId()));
					main.printLine(String.format("type=%s", gd.getType()));
					break;
				case EXTRACTION_UNIT:
					Unit unit = event.getUnit();
					main.printLine(String.format("Unit (id=%s):", unit.getId()));
					for ( Part part : unit ) {
						main.printPart(part);
					}
					break;
				case END_GROUP:
					main.printLine("End group");
					break;
				case END_SECTION:
					main.printLine("End section");
					break;
				case END_DOCUMENT:
					main.printLine("End document");
					break;
				}
				
				// Re-write the event
				writer.writeEvent(event);
			}

			writer.close();
			reader.close();
		}

		System.out.println("============================================================\n");
	}

	void printLine (String text) {
		if ( verbose ) {
			System.out.println(text);
		}
	}
	
	void printPart (Part part) {
		if ( !verbose ) return;
		if ( part instanceof Segment ) {
			Segment seg = (Segment)part;
			System.out.println(String.format(" segment (id=%s):", seg.getId()));
			System.out.print("  source=");
			System.out.println("["+seg.getSource().toXLIFF(style)+"]");
			System.out.print("  target=");
			if ( seg.hasTarget() ) {
				System.out.println("["+seg.getTarget(false).toXLIFF(style)+"]");
			}
			else {
				System.out.println("<no target defined>");
			}
		}
		else {
			System.out.println(" ignorable:");
			System.out.print("  source=");
			System.out.println("["+part.getSource().toXLIFF(style)+"]");
			System.out.print("  target=");
			if ( part.hasTarget() ) {
				System.out.println("["+part.getTarget(false).toXLIFF(style)+"]");
			}
			else {
				System.out.println("<no target defined>");
			}
		}
		
	}

	void setVerbose (boolean verbose) {
		this.verbose = verbose;
	}
	
}
