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
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.TMXWriter;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.translation.QueryUtil;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;
import net.sf.okapi.tm.pensieve.writer.ITmWriter;
import net.sf.okapi.tm.pensieve.writer.TmWriterFactory;

public class BatchTranslator {

	private static final Logger LOGGER = Logger.getLogger(BatchTranslator.class.getName());

	private IFilterConfigurationMapper fcMapper;
	private IFilter filter;
	private RawDocument rawDoc;
	private QueryUtil qutil;
	private File htmlSourceFile;
	private File htmlTargetFile;
	private Parameters params;
	private ITmWriter tmWriter;
	private TMXWriter tmxWriter;
	private LocaleId srcLoc;
	private LocaleId trgLoc;
	private int subDocId;
	private int currentSubDocId;
	private int htmlSubDocId;
	private String htmlTuId;
	private String htmlSegId;
	private TextUnit oriTu;
	private boolean initDone;
	private Map<String, String> attributes;

	public BatchTranslator (IFilterConfigurationMapper fcMapper,
		Parameters params)
	{
		this.fcMapper = fcMapper;
		this.params = params;
		
		if ( this.params == null ) {
			this.params = new Parameters();
		}
		qutil = new QueryUtil();
		initDone = false;
		attributes = new Hashtable<String, String>();
		attributes.put("creationid", Util.ORIGIN_MT);
	}
	
	protected void finalize () {
		endBatch();
	}
	
	public void endBatch () {
		if ( tmxWriter != null ) {
			tmxWriter.writeEndDocument();
			tmxWriter.close();
			tmxWriter = null;
		}
		initDone = false;
	}

	// Call this method at the first document
	private void initialize () {
		if ( params.getMakeTMX() ) {
			tmxWriter = new TMXWriter(params.getTmxPath());
			tmxWriter.writeStartDocument(srcLoc, trgLoc, getClass().getCanonicalName(), "1", "sentence", "MT-based", "unknown");
		}
		if ( !Util.isEmpty(params.getOrigin()) ) {
			attributes.put("Txt::Origin", params.getOrigin());
		}
		initDone = true;
	}
	
	public void processDocument (RawDocument rd) {
		rawDoc = rd;
		srcLoc = rawDoc.getSourceLocale();
		trgLoc = rawDoc.getTargetLocale();
		
		if ( !initDone ) {
			initialize();
		}
		
		filter = fcMapper.createFilter(rd.getFilterConfigId(), filter);
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
			htmlWriter.writeEndElementLineBreak();

			// Process
			Event event;
			subDocId = 0;
			currentSubDocId = 0;
			
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
							htmlWriter.writeEndElementLineBreak(); // p
						}
					}
					else { // Not segmented
						htmlWriter.writeStartElement("p");
						htmlWriter.writeAttributeString("id", String.format("%d:%s:", currentSubDocId, tu.getId()));
						htmlWriter.writeRawXML(qutil.toCodedHTML(tc.getContent()));
						htmlWriter.writeEndElementLineBreak(); // p
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
			subDocId = 0;
			currentSubDocId = 0;
			
			for ( Element elem : paragraphs ) {
				String id = elem.getAttributeValue("id");
				if ( id == null ) continue; // No id means we can't match
				// Decompose the html id in its sub-doc, tu and seg parts
				String parts[] = id.split(":", -1);
				htmlSubDocId = Integer.valueOf(parts[0]);
				htmlTuId = parts[1];
				htmlSegId = parts[2];
				
				if ( !getNextFromOriginal() ) {
					// Not found, out of synchronization
					break; // No need to continue
				}
				TextFragment srcFrag;
				TextFragment trgFrag;
				if ( Util.isEmpty(htmlSegId) ) {
					srcFrag = oriTu.getSourceContent();
				}
				else { // Segmented text unit
					srcFrag = oriTu.getSource().getSegments().get(Integer.valueOf(htmlSegId)).text;
				}
				try {
					String ctext = qutil.fromCodedHTML(elem.getContent().toString(), srcFrag);
					trgFrag = new TextFragment(ctext, srcFrag.getCodes());
				}
				catch ( Throwable e ) {
					// Catch issues with inline codes
					LOGGER.warning(String.format("Skipping entry '%d:%s:%s'.\n", htmlSubDocId, htmlTuId, htmlSegId)
						+ e.getMessage());
					continue; // Skip this entry
				}

				if ( tmWriter != null ) {
					TranslationUnit unit = new TranslationUnit(
						new TranslationUnitVariant(srcLoc, srcFrag),
						new TranslationUnitVariant(trgLoc, trgFrag));
					tmWriter.indexTranslationUnit(unit);							
				}
				
				if ( tmxWriter != null ) {
					tmxWriter.writeTU(srcFrag, trgFrag, null, attributes);
				}
//				System.out.println("");
//				System.out.println("SRC="+srcFrag.toString());
//				System.out.println("TRG="+trgFrag.toString());
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

	private boolean getNextFromOriginal () {
		boolean found = false;
		// If we are not on the proper text unit, we look for it
		if (( oriTu == null ) || ( htmlSubDocId != currentSubDocId ) || !htmlTuId.equals(oriTu.getId()) ) {
			Event event;
			boolean stop = false;
			while ( filter.hasNext() && !found && !stop ) {
				event = filter.next();
				switch ( event.getEventType() ) {
				case START_SUBDOCUMENT:
					currentSubDocId = ++subDocId;
					continue; // Keep looking for next text unit
				case END_SUBDOCUMENT:
					currentSubDocId = 0; // Top-level
					continue; // Keep looking for next text unit
				case TEXT_UNIT:
					oriTu = (TextUnit)event.getResource();
					if ( !oriTu.isTranslatable() ) continue;
					if (( htmlSubDocId == currentSubDocId ) && htmlTuId.equals(oriTu.getId()) ) {
						found = true;
						stop = true;
					}
					// In all case we break here, as the first TU should be the good one
					// If it is not, we are not synchronized anymore.
					break;
				default:
					continue; // Skip other events
				}
			}
			
			if ( !found ) { // No corresponding text unit
				return false;
			}
		}
		
		// We are on the correct TU
		// Now get the segment, if we are dealing with segments
		if ( !Util.isEmpty(htmlSegId) ) {
			found = false;
			for ( Segment seg : oriTu.getSource().getSegments() ) {
				if ( htmlSegId.equals(seg.id) ) {
					found = true;
					break;
				}
			}
		}
		
		return found;
	}

}
