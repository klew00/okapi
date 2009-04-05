/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package example01;

import java.io.File;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.pipeline.FilterPipelineStepAdaptor;
import net.sf.okapi.common.pipeline.FilterWriterPipelineStepAdaptor;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.resource.FileResource;
import net.sf.okapi.filters.xml.XMLFilter;
import net.sf.okapi.filters.html.HtmlFilter;
import net.sf.okapi.filters.openoffice.OpenOfficeFilter;
import net.sf.okapi.filters.properties.PropertiesFilter;

public class Main {
	
	private static String srcLang = "en";
	private static String trgLang = "fr";
	private static String inputEncoding = "UTF-8";
	private static String outputEncoding = "UTF-8";
	private static String inputPath = null;
	private static String outputPath = null;
	private static IFilter filter = null;
	private static String steps = "";
	
	public static void main (String[] args) {
		try {
			System.out.println("------------------------------------------------------------");
			if ( args.length == 0 ) {
				printUsage();
				return;
			}
			// Get the parameters
			for ( int i=0; i<args.length; i++ ) {
				if ( args[i].equals("-sl") ) srcLang = args[++i];
				else if ( args[i].equals("-tl") ) trgLang = args[++i];
				else if ( args[i].equals("-ie") ) inputEncoding = args[++i];
				else if ( args[i].equals("-oe") ) outputEncoding = args[++i];
				else if ( args[i].equals("-s") ) {
					if ( args[++i].equals("pseudo") ) steps += "p"; 
					else if ( args[i].equals("upper") ) steps += "u";
					else throw new RuntimeException("Unknown step.");
				}
				else if ( args[i].equals("-?") ) {
					printUsage();
					return;
				}
				else if ( inputPath == null ) inputPath = args[i];
				else outputPath = args[i];
			}
			
			if ( inputPath == null ) {
				throw new RuntimeException("No input file defined.");
			}
			if ( outputPath == null ) {
				outputPath = Util.getFilename(inputPath, false) + ".out" + Util.getExtension(inputPath); 
			}

			// Detect the file to use
			String ext = Util.getExtension(inputPath);
			if ( ext == null ) throw new RuntimeException("No filter detected for the file extension.");
			if ( ext.equals(".xml") ) filter = new XMLFilter();
			else if ( ext.equals(".html") ) filter = new HtmlFilter();
			else if ( ext.equals(".htm") ) filter = new HtmlFilter();
			else if ( ext.equals(".odt") ) filter = new OpenOfficeFilter();
			else if ( ext.equals(".ods") ) filter = new OpenOfficeFilter();
			else if ( ext.equals(".odp") ) filter = new OpenOfficeFilter();
			else if ( ext.equals(".odg") ) filter = new OpenOfficeFilter();
			else if ( ext.equals(".properties") ) filter = new PropertiesFilter();
			else throw new RuntimeException("No filter detected for the file extension.");
			
			// Display the parsed options
			System.out.println("            input file: " + inputPath);
			System.out.println("default input encoding: " + inputEncoding);
			System.out.println("           output file: " + outputPath);
			System.out.println("       output encoding: " + inputEncoding);
			System.out.println("       source language: " + srcLang);
			System.out.println("       target language: " + trgLang);
			System.out.println("       filter detected: " + filter.getName());
			
			// Process
			pipeline1();
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
	}
	
	private static void printUsage () {
		System.out.println("Usage: [option(s)] inputFile[ outputFile]");
		System.out.println("where the options are:");
		System.out.println(" -sl <sourceLang>");
		System.out.println(" -tl <targetLang>");
		System.out.println(" -ie <inputEncoding>");
		System.out.println(" -oe <outputEncoding>");
		System.out.println(" -s pseudo|upper");
	}
	
	private static void pipeline1 () {
		// Create the pipeline
		IPipeline pipeline = new Pipeline();
		
		// Create the filter step
		IPipelineStep inputStep = new FilterPipelineStepAdaptor(filter);
		// Add this step to the pipeline
		pipeline.addStep(inputStep);

		for ( int i=0; i<steps.length(); i++ ) {
			switch ( steps.charAt(i) ) {
			case 'p':
				pipeline.addStep(new PseudoTranslateStep(trgLang, i==steps.length()-1));
				break;
			case 'u':
				pipeline.addStep(new UppercaseStep(trgLang, i==steps.length()-1));
				break;
			}
		}

		// Create the writer we will use
		IFilterWriter writer = filter.createFilterWriter();
		// Create the writer step (using the writer provider by our filter)
		IPipelineStep outputStep = new FilterWriterPipelineStepAdaptor(writer);
		// Add this step to the pipeline
		pipeline.addStep(outputStep);

		// Sets the writer options and output
		writer.setOptions(trgLang, outputEncoding);
		writer.setOutput(outputPath);
		
		// Launch the execution
		FileResource fr = new FileResource((new File(inputPath)).toURI(), inputEncoding, srcLang);
		pipeline.process(fr);
		pipeline.destroy();
	}
}
