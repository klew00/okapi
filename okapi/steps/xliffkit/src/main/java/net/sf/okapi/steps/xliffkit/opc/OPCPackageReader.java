/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit.opc;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.AbstractFilter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.filters.xliff.XLIFFFilter;
import net.sf.okapi.lib.beans.sessions.OkapiJsonSession;
import net.sf.okapi.steps.xliffkit.reader.TextUnitMerger;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;

public class OPCPackageReader extends AbstractFilter {

	private OPCPackage pack;
	private OkapiJsonSession session = new OkapiJsonSession();
	private Event event;
	private LinkedList<PackagePart> coreParts = new LinkedList<PackagePart>();
	private PackagePart activePart;
	private PackagePart resourcesPart;
	private XLIFFFilter xliffReader;
	private TextUnitMerger merger;
	private LocaleId srcLoc;
	private String outputEncoding;
	private IFilterWriter filterWriter;
	private boolean generateTargets = false;
	private String outputPath;
	private boolean cacheEvents = false;
	private LinkedList<Event> events = new LinkedList<Event>();
	//private Event sde;
//	private LocaleId trgLoc;
	
	public OPCPackageReader(TextUnitMerger merger) {
		super();
		this.merger = merger;
	}

	@Override
	protected boolean isUtf8Bom() {
		return false;
	}

	@Override
	protected boolean isUtf8Encoding() {
		return false;
	}

	private void writeEvent(Event event) {
		if (!generateTargets) return;
		if (filterWriter == null) return;
		if (events == null) return;
		
		if (cacheEvents) {
			events.add(event);
		}
		else {
			while (events.size() > 0)
				filterWriter.handleEvent(events.poll());
			filterWriter.handleEvent(event);
		}
	}
	
	@Override
	public void close() {
		clearParts();
		session.end();
		try {
			pack.close();
		} catch (IOException e) {
			throw new OkapiIOException("OPCPackageReader: cannot close package");
		}
	}

	private void clearParts() {
		coreParts.clear();
		activePart = null;
		resourcesPart = null;		
	}

	@Override
	public IParameters getParameters() {
		return null;
	}

	@Override
	public boolean hasNext() {
		return event != null;
	}

	@Override
	public Event next() {
		Event prev = event;
		event = deserializeEvent();
		return prev;
	}

	/*
	 * Deserializes events from JSON files in OPC package
	 * @return null if no events are available 
	 */
	private Event deserializeEvent() {
		Event event = null;
		if (activePart == null) {
			activePart = coreParts.poll();
			if (activePart == null) 
				return null;
			else
				resourcesPart = OPCPackageUtil.getResourcesPart(activePart);
				try {
					if (resourcesPart != null)
						session.start(resourcesPart.getInputStream());
				} catch (IOException e) {
					throw new OkapiIOException("OPCPackageReader: cannot get resources from package", e);
				}
				
				// Create XLIFF filter for the core document
				if (xliffReader != null) {
					xliffReader.close();
					xliffReader = null;
				}
				xliffReader = new XLIFFFilter();
				try {
					// Here targetLocale is set to srcLoc, actual target locale is taken from the StartSubDocument's property targetLocale
					xliffReader.open(new RawDocument(activePart.getInputStream(), "UTF-8", srcLoc, srcLoc));
				} catch (IOException e) {
					throw new RuntimeException(String.format("OPCPackageReader: cannot open input stream for %s", 
							activePart.getPartName().getName()), e);
				}
			}
		event = session.deserialize(Event.class);
		if (event == null) {			
			session.end();
			activePart = null;
			return deserializeEvent(); // Recursion until all parts are tried
		} else 
			switch (event.getEventType()) {				
			case START_DOCUMENT:
				processStartDocument(event);
				break;

			case END_DOCUMENT:
				processEndDocument(event);
				break;
			
			case TEXT_UNIT:
				processTextUnit(event); // updates tu with a target from xliff
				break;
				
			case START_SUBDOCUMENT:
			case START_GROUP:
			case END_SUBDOCUMENT:
			case END_GROUP:
			case DOCUMENT_PART:
					writeEvent(event);
			}
		return event;
	}

	@Override
	public void open(RawDocument input) {
		open(input, false);		
	}

	@Override
	public void open(RawDocument input, boolean generateSkeleton) {
		try {
			srcLoc = input.getSourceLocale();
			//trgLoc = input.getTargetLocale();
			pack = OPCPackage.open(input.getStream());
		} catch (Exception e) {
			throw new OkapiIOException("OPCPackageReader: cannot open package", e);
		}
		
		clearParts();
		coreParts.addAll(OPCPackageUtil.getCoreParts(pack));		
		event = deserializeEvent();
	}

	@Override
	public void setParameters(IParameters params) {
	}

	private ITextUnit getNextXliffTu() {
		if (xliffReader == null)
			throw new RuntimeException("OPCPackageReader: xliffReader is not initialized");
		
		Event ev = null;
		while (xliffReader.hasNext()) {
			ev = xliffReader.next();
			if (ev == null) return null;
			if (ev.getEventType() == EventType.START_SUBDOCUMENT) {
				StartSubDocument startSubDoc = (StartSubDocument)ev.getResource();
				Property prop = startSubDoc.getProperty("targetLanguage");
				if ( prop != null ) {
					LocaleId trgLoc = LocaleId.fromString(prop.getValue());
					merger.setTrgLoc(trgLoc);
					filterWriter.setOptions(trgLoc, outputEncoding);
					cacheEvents = false;
				}
			}
			if (ev.getEventType() == EventType.TEXT_UNIT) {
				return ev.getTextUnit();
			}
		}
		return null;
	}
	
	private void processStartDocument (Event event) {
		// Translate src doc name for writers
		StartDocument startDoc = (StartDocument)event.getResource();
		String srcName = startDoc.getName();		
		String partName = activePart.getPartName().toString();
		String outFileName = outputPath + Util.getDirectoryName(partName) + "/" + Util.getFilename(srcName, true);
		
		filterWriter = startDoc.getFilterWriter();
		//System.out.println(startDoc.getName());
		if (generateTargets) {
			File outputFile = new File(outFileName);
			Util.createDirectories(outputFile.getAbsolutePath());
			filterWriter.setOutput(outputFile.getAbsolutePath());
			//sde = event; // Store for delayed processing
			cacheEvents = true; // In case output locale is not known until START_SUBDOCUMENT 
			writeEvent(event);
		}
	}
	
	private void processEndDocument (Event event) {
		writeEvent(event);
		if (generateTargets)
			filterWriter.close();
	}
	
	private void processTextUnit(Event event) {
		if (merger == null) return;
		
		ITextUnit tu = event.getTextUnit(); 
		ITextUnit xtu = getNextXliffTu();
		if (xtu == null) return;
		
//		// Set tu source from xtu source
//		TextContainer tc = tu.getSource(); // tu source is empty string + codes in JSON
//		TextFragment xtf = xtu.getSource().getUnSegmentedContentCopy();
//		tc.append(xtf.getCodedText());
//		//tu.setSource(xtu.getSource());
		
		merger.mergeTargets(tu, xtu);
		writeEvent(event);
	}

	public void setGeneratorOptions(String outputEncoding, String outputPath) {
		this.outputEncoding = outputEncoding;
		this.generateTargets = !Util.isEmpty(outputPath);
		this.outputPath = outputPath;
	}
}
