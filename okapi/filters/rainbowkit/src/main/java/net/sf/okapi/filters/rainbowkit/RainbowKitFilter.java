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

package net.sf.okapi.filters.rainbowkit;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.filters.po.POFilter;
import net.sf.okapi.filters.rtf.RTFFilter;
import net.sf.okapi.filters.xliff.XLIFFFilter;

@UsingParameters() // No parameters
public class RainbowKitFilter implements IFilter {

	public static final String RAINBOWKIT_MIME_TYPE = "application/x-rainbowkit";
	
	private static final Logger LOGGER = Logger.getLogger(RainbowKitFilter.class.getName());

	private boolean canceled;
	private boolean hasNext;
	private LinkedList<Event> queue;
	private Manifest manifest;
	private MergingInfo info;
	private Iterator<Integer> iter;
	private IFilter filter;
	private RTFFilter rtfFilter;
	private String extension;
	private boolean hasMoreDoc;
	
	public RainbowKitFilter () {
	}
	
	@Override
	public void cancel () {
		canceled = true;
	}

	@Override
	public void close () {
		// Just in case
		if ( filter != null ) {
			filter.close();
			filter = null;
		}
		if ( rtfFilter != null ) {
			rtfFilter.close();
			rtfFilter = null;
		}
	}

	@Override
	public String getName () {
		return "okf_rainbowkit";
	}

	@Override
	public String getDisplayName () {
		return "Rainbow Kit Filter (EXPERIMENTAL)";
	}

	@Override
	public String getMimeType () {
		return RAINBOWKIT_MIME_TYPE;
	}

	@Override
	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			RAINBOWKIT_MIME_TYPE,
			getClass().getName(),
			"Rainbow Translation Kit",
			"Configuration for Rainbow translation kit.",
			null,
			Manifest.MANIFEST_EXTENSION+";"));
		return list;
	}
	
	@Override
	public EncoderManager getEncoderManager () {
		if ( filter != null ) {
			return filter.getEncoderManager();
		}
		else {
			return null;
		}
	}
	
	@Override
	public IParameters getParameters () {
		return null;
	}

	@Override
	public boolean hasNext () {
		return hasNext;
	}

	@Override
	public Event next () {
		try {
			// Check for cancellation first
			if ( canceled ) {
				queue.clear();
				queue.add(new Event(EventType.CANCELED));
				hasNext = false;
			}
			
			// Parse next if nothing in the queue
			if ( queue.isEmpty() ) {
				nextEventInDocument(false);
				if ( !hasMoreDoc ) {
					// All documents in the manifest have been processed
					// No need to send an END_BATCH_ITEM, as the one for the initial raw document will be sent
					// Use an no-operation event to flush the queue
					hasNext = false;
					queue.add(Event.NOOP_EVENT);
				}
			}
			
			// Return the head of the queue
			return queue.poll();
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error reading the package.", e);
		}
	}

	@Override
	public void open (RawDocument input) {
		open(input, true);
	}
	
	@Override
	public void open (RawDocument input,
		boolean generateSkeleton)
	{
		close();
		canceled = false;
		hasMoreDoc = true;
		queue = new LinkedList<Event>();
		hasNext = true;
		
		manifest = new Manifest();
		if ( input.getInputURI() == null ) {
			throw new OkapiIOException("This filter requires URI-based raw documents only.");
		}
		manifest.load(new File(input.getInputURI()));
		
		// Create the iterator
		iter = manifest.getItems().keySet().iterator();
		// Start passing the documents
		nextDocument();
	}
	
	@Override
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		// Not used
	}

	@Override
	public void setParameters (IParameters params) {
		// Not used
	}

	@Override
	public ISkeletonWriter createSkeletonWriter() {
		if ( filter != null ) {
			return filter.createSkeletonWriter();
		}
		else {
			return null;
		}
	}

	@Override
	public IFilterWriter createFilterWriter () {
		if ( filter != null ) {
			return filter.createFilterWriter();
		}
		else {
			return null;
		}
	}

	private void nextDocument () {
		if ( iter.hasNext() ) { // New document
			// Get the current item
			int id = iter.next();
			info = manifest.getItem(id);
			startDocument();
		}
		else {
			// Else: No more document
			// Empty queue will trigger the end
			hasMoreDoc = false;
		}
	}

	private void startDocument () {
		// Pick the filter to use (if any)
		if ( info.getExtractionType().equals(Manifest.EXTRACTIONTYPE_XLIFF) ) {
			filter = new XLIFFFilter();
			extension = ".xlf";
		}
		else if ( info.getExtractionType().equals(Manifest.EXTRACTIONTYPE_PO) ) {
			filter = new POFilter();
			extension = ".po";
		}
		else if ( info.getExtractionType().equals(Manifest.EXTRACTIONTYPE_RTF) ) {
			postprocessRTF();
			// We send a no-operation event rather than call nextDocument() to avoid
			// having nested calls that would keep going deeper 
			queue.add(Event.NOOP_EVENT);
			return;
		}
		else {
			throw new OkapiIOException("Unsupported extraction type: "+info.getExtractionType());
		}
		
		File file = new File(manifest.getTargetDirectory()+info.getRelativeInputPath()+extension);
		RawDocument rd = new RawDocument(file.toURI(), info.getInputEncoding(),
			manifest.getSourceLocale(), manifest.getTargetLocale());
			
		// Open the document, and start sending its events
		filter.open(rd);
		nextEventInDocument(true);
	}

	/**
	 * Get the next event in the current document.
	 * @param attach true to attach the merging information and the manifest.
	 */
	private void nextEventInDocument (boolean attach) {
		// No filter cases
		if ( filter == null ) {
			nextDocument();
			return;
		}
		
		// Filter-based case
		if ( filter.hasNext() ) {
			if ( attach ) {
				// Attach the manifest and the current merging info to the start document
				Event event = filter.next();
				StartDocument sd = event.getStartDocument();
				sd.setAnnotation(info);
				sd.setAnnotation(manifest);
				queue.add(event);
			}
			else {
				Event e = filter.next();
				queue.add(e);
			}
		}
		else { // No more event: close the filter and move to the next document
			filter.close();
			nextDocument();
		}
	}

	private void postprocessRTF () {
		OutputStreamWriter writer = null;
		try {
			// Instantiate the reader if needed
			if ( rtfFilter == null ) {
				rtfFilter = new RTFFilter();
			}

			//TODO: get LB info from original
			String lineBreak = Util.LINEBREAK_DOS;
			
			// Open the RTF input
			//TODO: guess encoding based on language
			File file = new File(manifest.getTargetDirectory()+info.getRelativeInputPath()+".rtf");
			rtfFilter.open(new RawDocument(file.toURI(), "windows-1252", manifest.getTargetLocale()));
				
			// Open the output document
			// Initializes the output
			String outputFile = manifest.getMergeDirectory()+info.getRelativeTargetPath();
			Util.createDirectories(outputFile);
			writer = new OutputStreamWriter(new BufferedOutputStream(
				new FileOutputStream(outputFile)), info.getTargetEncoding());
			//TODO: check BOM option from original
			Util.writeBOMIfNeeded(writer, false, info.getTargetEncoding());
				
			// Process
			StringBuilder buf = new StringBuilder();
			while ( rtfFilter.getTextUntil(buf, -1, 0) == 0 ) {
				writer.write(buf.toString());
				writer.write(lineBreak);
			}
		}		
		catch ( Exception e ) {
			// Log and move on to the next file
			Throwable e2 = e.getCause();
			LOGGER.severe("RTF Conversion error. " + ((e2!=null) ? e2.getMessage() : e.getMessage()));
		}
		finally {
			if ( rtfFilter != null ) {
				rtfFilter.close();
			}
			if ( writer != null ) {
				try {
					writer.close();
				}
				catch ( IOException e ) {
					LOGGER.severe("RTF Conversion error when closing file. " + e.getMessage());
				}
			}
		}
	}
}