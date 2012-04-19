/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.filters.drupal;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IdGenerator;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.filters.html.HtmlFilter;

/**
 * Implements the IFilter interface for Drupal content.
 */
@UsingParameters(Parameters.class)
public class DrupalFilter implements IFilter {

	private static final String MIMETYPE = "application/x-drupal";
	
	private final Logger logger = Logger.getLogger(getClass().getName());

	private Parameters params;
	private Project proj;
	private IdGenerator otherId;
	private HtmlFilter htmlFilter;
	private Iterator<NodeInfo> nodeIter;
	private DrupalConnector cli;
	private boolean canceled;
	private LinkedList<Event> queue;
	private boolean hasNext;
	private boolean hasMoreDoc;
	private IFilterWriter writer;
	private String docId;
	private String subDocId;
	private String srcLang;
	private ArrayList<Field> fields;
	private int fieldIndex;
	
	public DrupalFilter () {
		params = new Parameters();
	}
	
	public void cancel () {
		canceled = true;
	}

	public void close () {
		if (( cli != null ) && cli.isLoggedIn() ) {
			cli.logout();
		}
	}

	public ISkeletonWriter createSkeletonWriter () {
		return createFilterWriter().getSkeletonWriter();
	}

	public IFilterWriter createFilterWriter () {
		if ( writer == null ) {
			writer = new DrupalFilterWriter();
			writer.setOptions(proj.getTargetLocale(), null);
		}
		return writer;
	}

	public List<FilterConfiguration> getConfigurations () {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MIMETYPE,
			getClass().getName(),
			"Drupal Project",
			"Default Drupal project",
			null,
			Project.PROJECT_EXTENSION+";"));
		list.add(new FilterConfiguration(getName()+"-noPrompt",
			MIMETYPE,
			getClass().getName(),
			"Drupal Project (without prompt)",
			"Drupal project without prompt when starting",
			"noPrompt.fprm"));
		return list;
	}

	public EncoderManager getEncoderManager () {
		return null;
	}

	public String getDisplayName () {
		return "Drupal Filter";
	}

	public String getMimeType () {
		return MIMETYPE;
	}

	public String getName () {
		return "okf_drupal";
	}

	public IParameters getParameters () {
		return params;
	}

	public boolean hasNext () {
		return hasNext || !queue.isEmpty();
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
				nextEventInField();
				if ( !hasMoreDoc ) {
					// All documents have been processed
					hasNext = false;
				}
			}
			
			// Return the head of the queue
			return queue.poll();
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error reading the site.", e);
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

			// Read the project file
			proj = new Project();
			proj.read(new BufferedReader(input.getReader()), input.getSourceLocale(), input.getTargetLocale());
			proj.setPath(input.getInputURI().getPath());
			
			// Refresh the list of resources
			// Prompt the user if requested
			if ( params.getOpenProject() ) {
				if ( !editProjectFile() ) {
					return;
				}
			}
			
			srcLang = input.getSourceLocale().toString();
			
			htmlFilter = new HtmlFilter();
			otherId = new IdGenerator(null, "o");
			queue = new LinkedList<Event>();
			
			// Initialize the client
			cli = new DrupalConnector(proj.getHost());
			cli.setCredentials(proj.getUser(), proj.getPassword());
			cli.login();
			
			String docName = null;
			if ( input.getInputURI() != null ) {
				docName = input.getInputURI().getPath();
			}

			docId = otherId.createId();
			StartDocument startDoc = new StartDocument(docId);
			startDoc.setName(docName);
			startDoc.setEncoding("UTF-8", false);
			startDoc.setLocale(input.getSourceLocale());
			startDoc.setFilterParameters(params);
			startDoc.setFilterWriter(createFilterWriter());
			startDoc.setType(MIMETYPE);
			startDoc.setMimeType(getMimeType());
			queue.add(new Event(EventType.START_DOCUMENT, startDoc));
			
			// Initialize the iteration
			
			nodeIter = proj.getEntries().iterator();
			hasMoreDoc = true;
			hasNext = true;
			nextNode();
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error processing input.\n"+e.getMessage(), e);
		}
	}
	
	public boolean editProjectFile () {
		String className = "net.sf.okapi.filters.drupal.ui.ProjectDialog";
		try {
			IProjectEditor dlg = (IProjectEditor)Class.forName(className).newInstance();
			if ( !dlg.edit(null, proj, true) ) {
				canceled = true;
				return false; // Canceled
			}
		}
		catch ( Throwable e ) {
			logger.severe(String.format("Cannot create the editor (%s)\n"+e.getMessage(), className));
			// And move on
			return false;
		}
		return true;
	}

	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper) {
		// Not used
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	private boolean nextNode () {
		while ( nodeIter.hasNext() ) {
			NodeInfo info = nodeIter.next();
			// Check if the node should be extracted
			if ( !info.getSelected() ) {
				continue;
			}
			if ( prepareNode(info) ) {
				nextEventInField();
				return true;
			}
			// Else: error with that file: move to the next one
		}
		// No more node

		Ending ending = new Ending(docId);
		queue.add(new Event(EventType.END_DOCUMENT, ending));
		hasMoreDoc = false;
		return false;
	}
	
	private void nextEventInField () {
		// Filter-based case
		if ( htmlFilter.hasNext() ) {
			Event e = htmlFilter.next();
			
			//=== Temporary work around
			// Converts the start/end document into start/group
			switch ( e.getEventType() ) {
			case START_DOCUMENT:
				StartGroup sg = new StartGroup(subDocId);
				sg.setType(fields.get(fieldIndex).getType());
				sg.setId(subDocId+"-"+sg.getType());
				// Set the annotation
				FilterWriterAnnotation ann = new FilterWriterAnnotation();
				ann.setData(proj, e.getResource(), htmlFilter.createFilterWriter());
				sg.setAnnotation(ann);
				// Create the new event
				e = new Event(EventType.START_GROUP, sg);
				break;
			
			case END_DOCUMENT:
				Ending ending = new Ending(subDocId+"-"+fields.get(fieldIndex).getType());
				// Set the annotation
				ann = new FilterWriterAnnotation();
				ann.setData(proj, e.getResource(), null);
				ending.setAnnotation(ann);
				e = new Event(EventType.END_GROUP, ending);
				break;
			}
			// Add the event (possibly converted) to the queue
			queue.add(e);
		}
		else { // No more event
			// Move to the next field
			nextFieldInNode();
		}
	}

	/*
	 * Reads the node, extract the title and prepare for the extraction of the body.
	 */
	private boolean prepareNode (NodeInfo info) {
		logger.info("Node: " + info.getNid() + " " + info.getType());
		// Get the node
		Node node = cli.getNode(info.getNid(), proj.getSourceLocale().toString(), proj.getNeutralLikeSource());
		
		subDocId = info.getNid();
		StartSubDocument ssd = new StartSubDocument(docId, subDocId);
		ssd.setName("node-"+subDocId);
		ssd.setMimeType(MIMETYPE);
		queue.add(new Event(EventType.START_SUBDOCUMENT, ssd));
		
		fields = new ArrayList<Field>();
		fieldIndex = -1;
		
		// Get the title
		String data = node.getTitle(srcLang);
		if ( !Util.isEmpty(data) ) {
			fields.add(new Field("title", data)); // Title
		}

		// Get the content
		data = node.getContent(srcLang);
		if ( !Util.isEmpty(data) ) {
			fields.add(new Field("body", data));
		}
		
		// Get the summary
		data = node.getSummary(srcLang);
		if ( !Util.isEmpty(data) ) {
			fields.add(new Field("summary", data));
		}
		
		return nextFieldInNode();
	}

	private boolean nextFieldInNode () {
		if ( fieldIndex >= fields.size()-1 ) {
			// We are done with this node
			// post an end of sub-document
			htmlFilter.close();
			Ending ending = new Ending(subDocId);
			queue.add(new Event(EventType.END_SUBDOCUMENT, ending));
			return nextNode();
		}
		fieldIndex++;
		
		// Apply the HTML filter
		RawDocument rd = new RawDocument(fields.get(fieldIndex).getContent(), proj.getSourceLocale());
		htmlFilter.setParentId(subDocId);
		htmlFilter.open(rd);
		nextEventInField();
		return true;
	}

}
