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

package net.sf.okapi.filters.transifex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.filters.po.POFilter;
import net.sf.okapi.lib.transifex.TransifexClient;

/**
 * Implements the IFilter interface for Transifex-based files.
 */
public class TransifexFilter implements IFilter {

	private static final String MIMETYPE = "application/x-transifex";
	
	private final Logger logger = Logger.getLogger(getClass().getName());

	private Project proj;
	private POFilter pof;
	private Iterator<String> iter;
	private TransifexClient cli;
	private boolean canceled;
	private LinkedList<Event> queue;
	private boolean hasNext;
	private boolean hasMoreDoc;
	private LocaleId srcLoc;
	private LocaleId trgLoc;
	private String tempDir;
	
	public TransifexFilter () {
		proj = new Project();
		pof = new POFilter();
	}
	
	public void cancel () {
		canceled = true;
	}

	public void close () {
		pof.close();
	}

	public ISkeletonWriter createSkeletonWriter () {
		return pof.createSkeletonWriter();
	}

	public IFilterWriter createFilterWriter () {
		return pof.createFilterWriter();
	}

	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MIMETYPE,
			getClass().getName(),
			"Transifex Project",
			"Configuration for Transifex project",
			null,
			".txp;"));
		return list;
	}

	public EncoderManager getEncoderManager () {
		return pof.getEncoderManager();
	}

	public String getDisplayName () {
		return "Transifex Filter";
	}

	public String getMimeType () {
		return MIMETYPE;
	}

	public String getName () {
		return "okf_transifex";
	}

	public IParameters getParameters () {
		return null; // No parameters
	}

	public boolean hasNext () {
		return hasNext;
	}

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
				nextEventInDocument();
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

	public void open (RawDocument input) {
		open(input, true);
	}

	public void open (RawDocument input,
		boolean generateSkeleton)
	{
		try {
			close();
			canceled = false;

			File temp = File.createTempFile("transifex-", null);
			temp.delete();
			temp.mkdirs();
			tempDir = temp.getAbsolutePath();
			
			srcLoc = input.getSourceLocale();
			trgLoc = input.getTargetLocale();
			
			// read the project file
			proj.read(new BufferedReader(input.getReader()));
			
			// Initialize the client
			cli = new TransifexClient(proj.getHost());
			cli.setCredentials(proj.getUser(), proj.getPassword());
			cli.setProject(proj.getProjectId());
			
			// Refresh the list of resources
			refreshResourceList();

			// Initialize the iteration
			iter = proj.getResourceIds().iterator();
			hasMoreDoc = true;
			queue = new LinkedList<Event>();
			hasNext = true;
			nextDocument();
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error processing input.\n"+e.getMessage(), e);
		}
	}

	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		// Not used
	}

	public void setParameters (IParameters params) {
		// Not used
	}

	private void nextDocument () {
		if ( iter.hasNext() ) {
			if ( prepareDocument(iter.next()) ) {
				nextEventInDocument();
				return;
			}
			// Else: error with that file, move to the next one
		}
		else {
			// No more document
			hasMoreDoc = false;
		}
	}
	
	private void nextEventInDocument () {
		// Filter-based case
		if ( pof.hasNext() ) {
			Event e = pof.next();
			queue.add(e);
		}
		else { // No more event: close the filter and move to the next document
			pof.close();
			// Send end-of-batch-item for this document
			// (if it's not the last one)
			//TODO
			
			// Move to the next document
			nextDocument();
		}
	}

	private boolean prepareDocument (String resId) {
//		// Check the information on this file
//		Object[] res = cli.getInformation(resId, trgLoc);
//		if ( res[0] == null ) {
//			logger.warning(String.format("Cannot get information for resource '%s' (%s).", resId, trgLoc.toPOSIXLocaleId()));
//			return false;
//		}
//		//TODO: avoid re-downloading if we re-write here and the existing file is newer

		// Download the PO for this resource and the given target language
		String outputPath = tempDir + File.separator + resId;
		String[] res = cli.getResource(resId, trgLoc, outputPath);
		if ( res[0] == null ) {
			logger.severe(String.format("Could not download the resource '%s'.", resId));
			return false;
		}

		// Opeen the local copy for processing
		RawDocument rd = new RawDocument(new File(outputPath).toURI(), "UTF-8", srcLoc, trgLoc);
		pof.open(rd);
		
		return true;
	}

	@SuppressWarnings("unchecked")
	private void refreshResourceList () {
		// Get the list of resources in the given project
		Object[] res = cli.getResourceList(srcLoc);
		if ( res[0] == null ) {
			logger.severe((String)res[1]);
			return;
		}
		Map<String, String> map = (Map<String, String>)res[2];
		List<String> list = proj.getResourceIds();
		list.clear();
		for ( String resId : map.keySet() ) {
			list.add(resId);
		}
	}

}
