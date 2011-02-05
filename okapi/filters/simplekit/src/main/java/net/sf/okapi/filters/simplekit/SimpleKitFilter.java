/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.filters.simplekit;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
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
import net.sf.okapi.filters.xliff.XLIFFFilter;

@UsingParameters() // No parameters
public class SimpleKitFilter implements IFilter {

	public static final String SIMPLEKIT_MIME_TYPE = "application/x-simplekit";
	
	private boolean canceled;
	private boolean hasNext;
	private LinkedList<Event> queue;
	private Manifest manifest;
	private MergingInfo info;
	private Iterator<Integer> iter;
	private IFilter filter;
	private boolean hasMoreDoc;
	
	public SimpleKitFilter () {
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
	}

	@Override
	public String getName () {
		return "okf_simplekit";
	}

	@Override
	public String getDisplayName () {
		return "Simple Kit Filter (EXPERIMENTAL)";
	}

	@Override
	public String getMimeType () {
		return SIMPLEKIT_MIME_TYPE;
	}

	@Override
	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			SIMPLEKIT_MIME_TYPE,
			getClass().getName(),
			"Simple Translation Kit",
			"Configuration for simple translation kit.",
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
			File file = new File(manifest.getTargetDirectory()+info.getRelativeInputPath()+".xlf");
			RawDocument rd = new RawDocument(file.toURI(), info.getInputEncoding(),
				manifest.getSourceLocale(), manifest.getTargetLocale());

			// Pick the filter to use
			if ( info.getExtractionType().equals(Manifest.EXTRACTIONTYPE_XLIFF) ) {
				filter = new XLIFFFilter();
			}
			else if ( info.getExtractionType().equals(Manifest.EXTRACTIONTYPE_PO) ) {
				filter = new POFilter();
			}
			//TODO all others types
			else {
				throw new OkapiIOException("Unsupported extraction type: "+info.getExtractionType());
			}
			
			// Open the document, and start sending its events
			filter.open(rd);
			nextEventInDocument(true);
	}

	/**
	 * Get the next event in the current document.
	 * @param attach true to attach the merging information and the manifest.
	 */
	private void nextEventInDocument (boolean attach) {
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
}
