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

package net.sf.okapi.filters.openxml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.common.skeleton.ZipSkeleton;
import net.sf.okapi.common.filterwriter.ZipFilterWriter;
import net.sf.okapi.filters.markupfilter.Parameters;

public class OpenXMLZipFilter implements IFilter {

	private enum NextAction {
		OPENZIP, NEXTINZIP, NEXTINSUBDOC, DONE
	}

	public final static int MSWORD=1;
	public final static int MSEXCEL=2;
	public final static int MSPOWERPOINT=3;
	private final String MIMETYPE = "text/xml";
	private final String docId = "sd";
	
	private ZipFile zipFile;
	private ZipEntry entry;
	private NextAction nextAction;
	private URI docURI;
	private Enumeration<? extends ZipEntry> entries;
	private int subDocId;
	private LinkedList<Event> queue;
	private String srcLang;
	private OpenXMLFilter openXMLFilter;
	private Parameters params=null;
	private int nZipType=MSWORD;
	private int dbg=0;
	private boolean bSquishable=true;
	private ITranslator translator=null;
	private String sOutputLanguage="en-US";

	public OpenXMLZipFilter () {
//		openXMLFilter = new OpenXMLFilter();
//		params = (Parameters)openXMLFilter.getParameters(); these would be for whole zip, but need each file
	}
	
	public OpenXMLZipFilter(ITranslator translator, String sOutputLanguage) {
		this.translator = translator;
		this.sOutputLanguage = sOutputLanguage;
	}

	public void cancel () {
		// TODO Auto-generated method stub
	}

	public void close () {
		try {
			nextAction = NextAction.DONE;
			if ( zipFile != null ) {
				zipFile.close();
				zipFile = null;
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setDbg(int dbg) // set debug level
	{
		this.dbg = dbg;
	}
	
	public ISkeletonWriter createSkeletonWriter () {
		return null; // There is no corresponding skeleton writer
	}
	
	public IFilterWriter createFilterWriter () {
		return new ZipFilterWriter();
	}

	public String getName () {
		return "okf_openxml";
	}

	public String getMimeType () {
		return MIMETYPE;
	}

	public IParameters getParameters () {
		return params;
	}

	public boolean hasNext () {
		return ((( queue != null ) && ( !queue.isEmpty() )) || ( nextAction != NextAction.DONE ));
	}

	public Event next () {
		// Send remaining event from the queue first
		if ( queue.size() > 0 ) {
			return queue.poll();
		}
		
		// When the queue is empty: process next action
		switch ( nextAction ) {
		case OPENZIP:
			return openZipFile();
		case NEXTINZIP:
			return nextInZipFile();
		case NEXTINSUBDOC:
			return nextInSubDocument();
		default:
			throw new RuntimeException("Invalid next() call.");
		}
	}

	public void open (InputStream input) {
		// Not supported for this filter
		throw new UnsupportedOperationException(
			"Method is not supported for this filter.");
	}

	public void open (CharSequence inputText) {
		// Not supported for this filter
		throw new UnsupportedOperationException(
			"Method is not supported for this filter.");
	}

	public void open (URI inputURI)
	{
		open(inputURI,true,0); // DWH 2-26-09 just a default
	}
	
	public void open (URI inputURI, boolean bSquishable) {
		open(inputURI, bSquishable, 0);
	}
	
	public void open (URI inputURI, boolean bSquishable, int dbg) {
		close();
		docURI = inputURI;
		nextAction = NextAction.OPENZIP;
		queue = new LinkedList<Event>();
		openXMLFilter = new OpenXMLFilter();
		this.dbg = dbg;
		openXMLFilter.setDbg(dbg);
		this.bSquishable = bSquishable;
	}

	public void setOptions (String sourceLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		setOptions(sourceLanguage, null, defaultEncoding, generateSkeleton);
	}

	public void setOptions (String sourceLanguage,
		String targetLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		srcLang = sourceLanguage;
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	private Event openZipFile () {
		File fZip;
		String sEntryName,sZipType;
		int iCute;
		try
		{
			fZip = new File(docURI.getPath());
			zipFile = new ZipFile(fZip);
			entries = zipFile.entries();

			nZipType = -1;
			while( entries.hasMoreElements() )
			{ // note that [Content_Types].xml is always first
				entry = entries.nextElement();
				sEntryName = entry.getName();
				iCute = 0;
			    iCute = sEntryName.indexOf("/");
			    if (iCute>0)
			    {
				    sZipType = sEntryName.substring(0,iCute);
				    if (sZipType.equals("xl"))
				    	nZipType = MSEXCEL;
				    else if (sZipType.equals("word"))
				    	nZipType = MSWORD;
				    else if (sZipType.equals("ppt"))
				    	nZipType = MSPOWERPOINT;
				    else
				    	continue;
				    break;
			    }
			}
			if (nZipType==-1)
				throw (new IOException("This is not a Microsoft Office 2007 Word, Excel, or Powerpoint file.")); 
			openXMLFilter.setUpConfig(nZipType);
			  // DWH 3-4-09 sets Parameters inside OpenXMLFilter based on file type
			params = (Parameters)openXMLFilter.getParameters();
			  // DWH 3-4-09 params for OpenXMLZipFilter
			
			entries = zipFile.entries();
			openXMLFilter.initFileTypes(); // new HashTable for file types in zip file
			subDocId = 0;
			nextAction = NextAction.NEXTINZIP;
			
			StartDocument startDoc = new StartDocument(docId);
			startDoc.setName(docURI.getPath());
			startDoc.setLanguage(srcLang);
			startDoc.setMimeType(MIMETYPE);
			ZipSkeleton skel = new ZipSkeleton(zipFile);
			return new Event(EventType.START_DOCUMENT, startDoc, skel);
		}
		catch ( ZipException e )
		{
			throw new RuntimeException(e);
		}
		catch ( IOException e )
		{
			throw new RuntimeException(e);
		}
	}
	
	private Event nextInZipFile () {
		String sEntryName; // DWH 2-26-09
		String sDocType; // DWH 2-26-09
		int iCute; // DWH 2-26-09
		while( entries.hasMoreElements() ) { // note that [Content_Types].xml is always first
			entry = entries.nextElement();
			sEntryName = entry.getName();
			sDocType = openXMLFilter.getContentType("/"+sEntryName);
		    iCute = sDocType.lastIndexOf('.', sDocType.length()-1);
		    if (iCute>0)
			    sDocType = sDocType.substring(iCute+1);
			if ( bSquishable && sEntryName.endsWith(".xml") &&
				    ((nZipType==MSWORD && sDocType.equals("main+xml")) ||
				   	 (nZipType==MSPOWERPOINT && sDocType.equals("slide+xml")))) {
				if (dbg>2)
					System.out.println("\n\n<<<<<<< "+sEntryName+" : "+sDocType+" >>>>>>>");
				return openSubDocument(true);
			}
			else if ( sEntryName.equals("[Content_Types].xml") ||
				   (sEntryName.endsWith(".xml") &&
				    ((nZipType==MSWORD &&
				    	   (sDocType.equals("main+xml") ||
			   				sDocType.equals("footnotes+xml") ||
			   				sDocType.equals("endnotes+xml") ||
		                    sDocType.equals("header+xml") ||
		                    sDocType.equals("footer+xml") ||
		                    sDocType.equals("comments+xml") ||
		                    sDocType.equals("glossary+xml"))) ||
		             (nZipType==MSEXCEL &&
		            	   (sDocType.equals("main+xml") ||
				   	  	    sDocType.equals("worksheet+xml") ||
				   			sDocType.equals("sharedStrings+xml") ||
				   			sDocType.equals("table+xml") ||
				   			sDocType.equals("comments+xml"))) ||
				   	 (nZipType==MSPOWERPOINT &&
				   	       (sDocType.equals("slide+xml") ||
				   	        sDocType.equals("notesSlide+xml")))))) {
				if (dbg>2)
					System.out.println("\n\n<<<<<<< "+sEntryName+" : "+sDocType+" >>>>>>>");
				return openSubDocument(false);
			}
			else {
				DocumentPart dp = new DocumentPart(entry.getName(), false);
				ZipSkeleton skel = new ZipSkeleton(entry);
				return new Event(EventType.DOCUMENT_PART, dp, skel);
			}
		}

		// No more sub-documents: end of the ZIP document
		close();
		Ending ending = new Ending("ed");
		return new Event(EventType.END_DOCUMENT, ending);
	}
	
	private Event openSubDocument (boolean bSquishing) {
		PipedInputStream squishedInputStream;
		PipedOutputStream pios=null;
		BufferedInputStream bis; // DWH 3-5-09
		InputStream isInputStream;
		openXMLFilter.close(); // Make sure the previous is closed
		openXMLFilter.setParameters(params);
		openXMLFilter.setOptions(srcLang, "UTF-8", true);
		Event event;
		try
		{
			isInputStream = zipFile.getInputStream(entry);
			if (bSquishing)
			{
				if (pios!=null)
				{
					try
					{
						pios.close();
					} catch (IOException e) {
						// e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
				pios = new PipedOutputStream(); // DWH 2-19-09 this may need to be final
				squishedInputStream = (PipedInputStream)openXMLFilter.combineRepeatedFormat(isInputStream,pios); // DWH 2-3-09
//				openXMLFilter.open(squishedInputStream); // DWH 2-3-09 was isInputStream
				bis = new BufferedInputStream(squishedInputStream); // DWH 3-5-09 allows mark and reset
			}
			else
			{
//				openXMLFilter.open(isInputStream);
				bis = new BufferedInputStream(isInputStream); // DWH 3-5-09 allows mark and reset
			}

			openXMLFilter.open(bis); // DWH 3-5-09
			//			openXMLFilter.next(); // START
			event = openXMLFilter.next(); // START_DOCUMENT
			if (dbg>2)
			{
				String glorp = openXMLFilter.getParameters().toString();
				System.out.println(glorp); // This lists what YAML actually read out of the configuration file
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		// Change the START_DOCUMENT event to START_SUBDOCUMENT
		StartSubDocument sd = new StartSubDocument(docId, String.valueOf(++subDocId));
		sd.setName(entry.getName());
		nextAction = NextAction.NEXTINSUBDOC;
		ZipSkeleton skel = new ZipSkeleton(
			(GenericSkeleton)event.getResource().getSkeleton(), entry);
		return new Event(EventType.START_SUBDOCUMENT, sd, skel);
	}
	
	private Event nextInSubDocument () {
		Event event;
		while ( openXMLFilter.hasNext() ) {
			event = openXMLFilter.next();
			switch ( event.getEventType() ) {
				case TEXT_UNIT:
					if (translator!=null)
					{
						TextUnit tu = (TextUnit)event.getResource();
						TextFragment tf = tu.getSourceContent();
						String sauce = tf.getCodedText();
						String torg = translator.translate(sauce);
						TextContainer tc = new TextContainer(torg);
						tu.setTarget(sOutputLanguage, tc);
					}
					if (dbg>2)
						openXMLFilter.displayOneEvent(event);
					return event;
				case END_DOCUMENT:
					// Read the FINISHED event
	//				openXMLFilter.next();
					// Change the END_DOCUMENT to END_SUBDOCUMENT
					Ending ending = new Ending(String.valueOf(subDocId));
					nextAction = NextAction.NEXTINZIP;
					ZipSkeleton skel = new ZipSkeleton(
						(GenericSkeleton)event.getResource().getSkeleton(), entry);
					return new Event(EventType.END_SUBDOCUMENT, ending, skel);
				
				default: // Else: just pass the event through
					if (dbg>2)
						openXMLFilter.displayOneEvent(event);
					return event;
			}
		}
		return null; // Should not get here
	}
	
}
