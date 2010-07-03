/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.lib.verification;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextUnit;

public class QualityCheckSession {

	private static final String serialSignature = "OQCS";
	private static final long serialVersionUID = 1L;

	public static String FILE_EXTENSION = ".qcs";
	
	Map<URI, RawDocument> rawDocs; // Temporary solution waiting for the DB
	IFilterConfigurationMapper fcMapper;
	private Parameters params;
	private List<Issue> issues;
	private QualityChecker checker;
	private LocaleId sourceLocale = LocaleId.ENGLISH;
	private LocaleId targetLocale = LocaleId.FRENCH;
	private IFilter filter;
	private String rootDir;
	private boolean modified;
	
	public QualityCheckSession () {
		reset();
	}
	
	public boolean isModified () {
		return modified;
	}
	
	public void setModified (boolean modified) {
		this.modified = modified;
	}

	public List<Issue> getIssues () {
		return issues;
	}
	
	public Parameters getParameters () {
		return params;
	}

	
	public void setParameters (Parameters params) {
		this.params = params;
	}
	
	public void addRawDocument (RawDocument rawDoc) {
		URI uri = rawDoc.getInputURI();
		rawDocs.put(uri, rawDoc);
		modified = true;
	}
	
	public List<RawDocument> getDocuments () {
		return new ArrayList<RawDocument>(rawDocs.values());
	}

	public Map<URI, RawDocument> getDocumentsMap () {
		return rawDocs;
	}

	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
	}

	public IFilterConfigurationMapper getFilterConfigurationMapper () {
		return fcMapper;
	}

	public LocaleId getSourceLocale () {
		return sourceLocale;
	}

	public void setSourceLocale (LocaleId sourceLocale) {
		if ( !this.sourceLocale.equals(sourceLocale) ) modified = true;
		this.sourceLocale = sourceLocale;
	}

	public LocaleId getTargetLocale () {
		return targetLocale;
	}

	public void setTargetLocale (LocaleId targetLocale) {
		if ( !this.targetLocale.equals(targetLocale) ) modified = true;
		this.targetLocale = targetLocale;
	}

	public void reset () {
		rawDocs = new HashMap<URI, RawDocument>();
		issues = new ArrayList<Issue>();
		params = new Parameters();
		checker = new QualityChecker();
	}
	
	public void resetDisabledIssues () {
		for ( Issue issue : issues ) {
			issue.enabled = true;
		}
	}
	
	public int getDocumentCount () {
		return rawDocs.size();
	}
	
	public void recheckAll (List<String> sigList) {
		if ( rawDocs.size() == 0 ) return;
		startProcess(targetLocale, null);
		for ( RawDocument rd : rawDocs.values() ) {
			executeRecheck(rd, sigList);
		}
	}
	
	private void executeRecheck (RawDocument rd,
		List<String> sigList)
	{
		try {
			// Process the document
			filter = fcMapper.createFilter(rd.getFilterConfigId(), filter);
			if ( filter == null ) {
				throw new RuntimeException("Unsupported filter type.");
			}
			filter.open(rd);
			while ( filter.hasNext() ) {
				Event event = filter.next();
				switch ( event.getEventType() ) {
				case START_DOCUMENT:
					StartDocument sd = (StartDocument)event.getResource();
					// If signatures exists, don't create the list from the current issues
					if ( sigList == null ) {
						sigList = clearIssues(rd.getInputURI(), true);
					}
					else {
						clearIssues(rd.getInputURI(), false);
					}
					processStartDocument(sd, sigList);
					break;
				case TEXT_UNIT:
					processTextUnit(event.getTextUnit());
					break;
				}
			}
		}
		finally {
			if ( filter != null ) filter.close();
		}
	}
	
	// Gets all signatures 
	private List<String> getAllSignatures () {
		ArrayList<String> list = new ArrayList<String>();
		Iterator<Issue> iter = issues.iterator();
		while ( iter.hasNext() ) {
			Issue issue = iter.next();
			if ( !issue.enabled ) {
				list.add(issue.getSignature());
			}
		}
		return list;
	}
	
	private List<String> clearIssues (URI docId,
		boolean generateSigList)
	{
		ArrayList<String> sigList = null;
		// Create signature list if needed
		if ( generateSigList ) {
			sigList = new ArrayList<String>();
		}
		
		Iterator<Issue> iter = issues.iterator();
		while ( iter.hasNext() ) {
			Issue issue = iter.next();
			if ( issue.docId.equals(docId) ) {
				// Generate signature if the issue is disabled
				if ( generateSigList && !issue.enabled ) {
					sigList.add(issue.getSignature());
				}
				// Remove issue
				iter.remove();
			}
		}
		return sigList;
	}
	
	public void saveSession (String path) {
		// Temporary code, waiting for DB
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(new FileOutputStream(path));
			
			// Header
			dos.writeBytes(serialSignature);
			dos.writeLong(serialVersionUID);
			
			// Locales
			dos.writeUTF(sourceLocale.toBCP47());
			dos.writeUTF(targetLocale.toBCP47());
			
			// Parameters
			dos.writeUTF(params.toString());
			
			// Document list
			dos.writeInt(rawDocs.size());
			for ( RawDocument rd : rawDocs.values() ) {
				dos.writeUTF(rd.getInputURI().toString());
				dos.writeUTF(rd.getFilterConfigId());
				dos.writeUTF(rd.getEncoding());
			}
			
			// Issues to keep disabled
			List<String> list = getAllSignatures();
			dos.writeInt(list.size());
			for ( String sig : list ) {
				dos.writeUTF(sig);
			}
			modified = false;
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error while saving session.", e);
		}
		finally {
			if ( dos != null ) {
				try {
					dos.close();
				}
				catch ( IOException e ) {
					throw new OkapiIOException("Error closing session file.", e);
				}
			}
		}
	}
	
	public void loadSession (String path) {
		reset();
		// Temporary code, waiting for DB
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new FileInputStream(path));

			// Header
			byte[] buf = new byte[4];
			dis.read(buf, 0, 4);
			String tmp = new String(buf);
			if ( !tmp.equals(serialSignature) ) {
				throw new OkapiIOException("Invalid signature: This file is not a QCS file, or is corrupted.");
			}
			long version = dis.readLong();
			if ( version != serialVersionUID ) {
				// For now just check the number, later we may have different ways of reading
				throw new OkapiIOException("Invalid version number: This file is not a QCS file, or is corrupted.");
			}
			
			// Locales
			tmp = dis.readUTF(); // Source
			sourceLocale = LocaleId.fromBCP47(tmp);
			tmp = dis.readUTF(); // Target
			targetLocale = LocaleId.fromBCP47(tmp);
			
			// Parameters
			tmp = dis.readUTF();
			params.fromString(tmp);
			
			// Document list
			int count = dis.readInt();
			for ( int i=0; i<count; i++ ) {
				tmp = dis.readUTF();
				URI uri = new URI(tmp);
				String configId = dis.readUTF();
				String encoding = dis.readUTF();
				RawDocument rd = new RawDocument(uri, encoding, sourceLocale, targetLocale);
				rd.setFilterConfigId(configId);
				rawDocs.put(uri, rd);
			}
			
			// Signatures of issues to keep disabled
			List<String> sigList = new ArrayList<String>(); 
			count = dis.readInt();
			for ( int i=0; i<count; i++ ) {
				sigList.add(dis.readUTF());
			}
			recheckAll(sigList);
			modified = false;
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error reading session file.\n"+e.getMessage(), e);
		}
		finally {
			if ( dis != null ) {
				try {
					dis.close();
				}
				catch ( IOException e ) {
					throw new OkapiIOException("Error closing session file.", e);
				}
			}
		}
	}

	public void startProcess (LocaleId locId,
		String rootDir)
	{
		this.rootDir = rootDir;
		checker.startProcess(locId, params, issues);
	}
	
	public void processStartDocument (StartDocument startDoc,
		List<String> sigList)
	{
		checker.processStartDocument(startDoc, sigList);
	}

	public void processTextUnit (TextUnit textUnit) {
		checker.processTextUnit(textUnit);
	}

	public void generateReport () {
		XMLWriter writer = null;
		try {
			// Create the output file
			String finalPath = Util.fillRootDirectoryVariable(params.getOutputPath(), rootDir);
			writer = new XMLWriter(finalPath);
			writer.writeStartDocument();
			writer.writeStartElement("html");
			writer.writeRawXML("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
				+ "<title>Quality Check Report</title><style type=\"text/css\">"
				+ "body { font-family: Verdana; font-size: smaller; }"
				+ "h1 { font-size: 110%; }"
				+ "h2 { font-size: 100%; }"
				+ "h3 { font-size: 100%; }"
				+ "p.item { font-family: Courier New, courier; font-size: 100%; white-space: pre;"
				+ "   border: solid 1px; padding: 0.5em; border-color: silver; background-color: whitesmoke; }"
	      		+ "pre { font-family: Courier New, courier; font-size: 100%;"
	      		+ "   border: solid 1px; padding: 0.5em; border-color: silver; background-color: whitesmoke; }"
				+ "span.hi { background-color: #FFFF00; }"
				+ "</style></head>");
			writer.writeStartElement("body");
			writer.writeLineBreak();
			writer.writeElementString("h1", "Quality Check Report");

			// Process the issues
			URI docId = null;
			for ( Issue issue : issues ) {
				// Skip disabled issues
				if ( !issue.enabled ) continue;
				// Do we start a new input document?
				if (( docId == null ) || !docId.equals(issue.docId) ) {
					// Ruler only after first input document
					if ( docId != null ) writer.writeRawXML("<hr />");
					docId = issue.docId;
					writer.writeElementString("p", "Input: "+docId.toString());
				}

				String position = String.format("ID=%s", issue.tuId);
//				if ( issue.tuName != null ) {
//					position += (" ("+issue.tuName+")");
//				}
				if ( issue.segId != null ) {
					position += String.format(", segment=%s", issue.segId);
				}
				writer.writeElementString("p", position+": "+issue.message);
				writer.writeRawXML("<p class=\"item\">");
				writer.writeString("Source: ["+issue.oriSource+"]");
				writer.writeRawXML("<br />");
				writer.writeString("Target: ["+issue.oriTarget+"]");
				writer.writeRawXML("</p>");
				writer.writeLineBreak();

			} // End of for issues

			// Write end of document
			writer.writeEndElementLineBreak(); // body
			writer.writeEndElementLineBreak(); // html
			writer.writeEndDocument();
		}
		finally {
			if ( writer != null ) writer.close();
		}
	}

}
