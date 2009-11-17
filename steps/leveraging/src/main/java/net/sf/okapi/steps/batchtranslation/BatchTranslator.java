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

package net.sf.okapi.steps.batchtranslation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.translation.QueryUtil;
import net.sf.okapi.tm.pensieve.common.PensieveUtil;
import net.sf.okapi.tm.pensieve.writer.ITmWriter;
import net.sf.okapi.tm.pensieve.writer.TmWriterFactory;

public class BatchTranslator {

	private IFilterConfigurationMapper fcMapper;
	private IFilter filter;
	private RawDocument rawDoc;
	private QueryUtil qutil;
	private File htmlSourceFile;
	private File htmlTargetFile;
	private Parameters params;
	private ITmWriter tmWriter;
	private LocaleId srcLoc;
	private LocaleId trgLoc;
	
	public BatchTranslator (IFilterConfigurationMapper fcMapper,
		Parameters params)
	{
		this.fcMapper = fcMapper;
		this.params = params;
		
		if ( this.params == null ) {
			this.params = new Parameters();
		}
		qutil = new QueryUtil();
	}
	
	public void processDocument (RawDocument rd,
		String filterConfigId)
	{
		rawDoc = rd;
		srcLoc = rawDoc.getSourceLocale();
		trgLoc = rawDoc.getTargetLocale();
		
		filter = fcMapper.createFilter(filterConfigId, filter);
		if ( filter == null ) {
			throw new RuntimeException("Unsupported filter type.");
		}
		createBatchInput();
		runBatchTranslation();
		retrieveTranslation();
	}

	private void createBatchInput () {
		XMLWriter htmlWriter = null;
		try {
			// Open the document
			filter.open(rawDoc);

			htmlSourceFile = File.createTempFile("hft_", ".html");
			htmlWriter = new XMLWriter(htmlSourceFile.getPath());

			// Set the output name and make sure it's deleted
			String path = htmlSourceFile.getAbsolutePath();
			path = Util.getDirectoryName(path) + File.separator + Util.getFilename(path, false) + ".trg.html";
			htmlTargetFile = new File(path);
			
			if ( htmlTargetFile.exists() ) {
				htmlTargetFile.delete();
			}
			
			// Start building the source file
			htmlWriter.writeStartElement("html");
			htmlWriter.writeStartElement("meta");
			htmlWriter.writeAttributeString("http-equiv", "Content-Type");
			htmlWriter.writeAttributeString("content", "text/html; charset=UTF-8");
			htmlWriter.writeEndElement();

			// Process
			Event event;
			int subDocId = 0;
			int currentSubDocId = 0;
			
			while ( filter.hasNext() ) {
				event = filter.next();
				switch ( event.getEventType() ) {
				case START_SUBDOCUMENT:
					currentSubDocId = ++subDocId;
					break;
					
				case END_SUBDOCUMENT:
					currentSubDocId = 0; // Top-level
					break;
					
				case TEXT_UNIT:
					TextUnit tu = (TextUnit)event.getResource();
					if ( !tu.isTranslatable() ) continue;
					TextContainer tc = tu.getSource();
					if ( tc.isSegmented() ) {
						List<Segment> segments = tc.getSegments();
						for ( Segment seg : segments ) {
							htmlWriter.writeStartElement("p");
							htmlWriter.writeAttributeString("id", String.format("%d:%s:%s", currentSubDocId, tu.getId(), seg.id));
							htmlWriter.writeRawXML(qutil.toCodedHTML(seg.text));
							htmlWriter.writeEndElement(); // p
						}
					}
					else { // Not segmented
						htmlWriter.writeStartElement("p");
						htmlWriter.writeAttributeString("id", String.format("%d:%s:", currentSubDocId, tu.getId()));
						htmlWriter.writeRawXML(qutil.toCodedHTML(tc.getContent()));
						htmlWriter.writeEndElement(); // p
					}
				}
			}
			
			if ( htmlWriter != null ) {
				htmlWriter.writeEndElement(); // html
				htmlWriter.writeEndDocument();
				htmlWriter.close();
			}
			if ( htmlSourceFile != null ) {
				htmlSourceFile.deleteOnExit();
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error when creating the input of the batch process.", e);
		}
		finally {
			if ( htmlWriter != null ) htmlWriter.close();
			if ( filter != null ) filter.close();
		}
	}

	private void runBatchTranslation () {
		try {
			String cmd = params.getCommand();

			cmd = cmd.replace("${input}", htmlSourceFile.getPath());
			cmd = cmd.replace("${output}", htmlTargetFile.getPath());
			
			Locale loc = rawDoc.getSourceLocale().toJavaLocale();
			cmd = cmd.replace("${srcLangName}", loc.getDisplayLanguage(Locale.ENGLISH));
			
			loc = rawDoc.getTargetLocale().toJavaLocale();
			cmd = cmd.replace("${trgLangName}", loc.getDisplayLanguage(Locale.ENGLISH));
			
			Process p = Runtime.getRuntime().exec(cmd);
	    	p.waitFor();
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error during the batch translation.", e);
		}
		catch ( InterruptedException e ) {
			throw new RuntimeException("Program interrupted.", e);
		}
	}
	
	private void retrieveTranslation () {
		Source html = null;
		tmWriter = null;
		try {
			// Open the TM if needed
			if ( params.getMakeTM() ) {
				String tmDir = params.getTmDirectory();
				Util.createDirectories(tmDir+File.separator);
				//TODO: Move this check at the pensieve package level
				File file = new File(tmDir+File.separator+"segments.gen");
				// Create a new index only if one does not exists yet
				// If one exists we pass false to append to it
				tmWriter = TmWriterFactory.createFileBasedTmWriter(tmDir, !file.exists());
			}
			
			// Open the original document
			filter.open(rawDoc);

			// Open the translated file
			html = new Source(htmlTargetFile.toURI().toURL());
			html.fullSequentialParse();
			// Get the elements
			List<Element> paragraphs = html.getAllElements(HTMLElementName.P);

			// Process
			// The element should be in the same order as the event of the original file
			int subDocId = 0;
			int currentSubDocId = 0;
			TextUnit tu = null;
			
			for ( Element elem : paragraphs ) {
				String id = elem.getAttributeValue("id");
				if ( id == null ) continue; // No id means we can't match
				// Decompose the html id in its sub-doc, tu and seg parts
				String parts[] = id.split(":", -1);
				int htmlSubDocId = Integer.valueOf(parts[0]);
				String htmlTuId = parts[1];
				String htmlSegId = parts[2];
				
				// Look for the source
				Event event;
				boolean found = false;
				while ( filter.hasNext() ) {
					event = filter.next();
					switch ( event.getEventType() ) {
					case START_SUBDOCUMENT:
						currentSubDocId = ++subDocId;
						continue; // Keep looking for next text unit
					case END_SUBDOCUMENT:
						currentSubDocId = 0; // Top-level
						continue; // Keep looking for next text unit
					case TEXT_UNIT:
						tu = (TextUnit)event.getResource();
						if ( !tu.isTranslatable() ) continue;
						
						if (( htmlSubDocId == currentSubDocId ) && htmlTuId.equals(tu.getId()) ) {
							found = true;
						}
						// In all case we break here, as the first TU should be the good one
						// If it is not, we are not synchronized anymore.
						break;
					default:
						continue; // Skip other events
					}
					
					// Things are not synchronized any more
					if ( found ) {
						TextFragment frag = tu.getSourceContent();
						String ctext = qutil.fromCodedHTML(elem.getContent().toString(), frag);
						tu.setTargetContent(trgLoc, new TextFragment(ctext, frag.getCodes()));
						
						if ( tmWriter != null ) {
							tmWriter.indexTranslationUnit(PensieveUtil.convertToTranslationUnit(
								srcLoc, trgLoc, tu));							
						}
//						
//						System.out.println("");
//						System.out.println("SRC="+tu.getSource().toString());
//						System.out.println("TRG="+tu.getTarget(trgLoc).toString());
					}
					else {
						// Error
					}
					
					// In all cases we leave this loop
					break; 
				}
				
				if ( !found ) {
					break; // No need to continue
				}
			}
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error reading the translations.");
		}
		finally {
			if ( tmWriter != null ) {
				try {
					tmWriter.endIndex();
				}
				catch ( IOException e ) {
					// Ignore this error
				}
			}
			if ( filter != null ) filter.close();
			if ( html != null ) html.clearCache();
			htmlTargetFile.deleteOnExit();
		}
	}

}
