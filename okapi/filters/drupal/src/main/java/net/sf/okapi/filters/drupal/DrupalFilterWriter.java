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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

/**
 * Implementation of {@link IFilterWriter} for Drupal project.
 */
public class DrupalFilterWriter implements IFilterWriter {

	private final Logger logger = Logger.getLogger(getClass().getName());

	private IFilterWriter writer;
	private LocaleId trgLoc;
	private DrupalConnector cli;
	private ByteArrayOutputStream outStream;
	private HashMap<String, String> outFields;
	private String groupType;
	private FilterWriterAnnotation ann;
	
	@Override
	public void cancel () {
		close();
	}

	@Override
	public void close () {
		if ( writer != null ) {
			writer.close();
		}
		if ( cli != null ) {
			cli.logout();
		}
	}

	@Override
	public EncoderManager getEncoderManager () {
		if ( writer == null ) return null;
		else return writer.getEncoderManager();
	}

	@Override
	public String getName () {
		return "DrupalFilterWriter";
	}

	@Override
	public IParameters getParameters () {
		// Not used
		return null;
	}

	@Override
	public ISkeletonWriter getSkeletonWriter () {
		if ( writer == null ) return null;
		else return writer.getSkeletonWriter();
	}

	@Override
	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case START_SUBDOCUMENT:
			return processStartSubDocument(event);
		case END_SUBDOCUMENT:
			return processEndSubDocument(event);
		case END_DOCUMENT:
			return processEndDocument(event);
		case START_GROUP:
			return processStartGroup(event);
		case END_GROUP:
			return processEndGroup(event);
		}
		// Else: process the events
		if ( writer != null ) {
			event = writer.handleEvent(event);
		}
		return event;
	}

	@Override
	public void setOptions (LocaleId locale,
		String defaultEncoding)
	{
		trgLoc = locale;
		// Encoding is hard-coded
	}

	/**
	 * Does nothing: A Drupal project always re-write onto itself. 
	 */
	@Override
	public void setOutput (String path) {
		// Nothing to do
	}

	/**
	 * Does nothing: A Drupal project always re-write onto itself. 
	 */
	@Override
	public void setOutput (OutputStream output) {
		// Nothing to do
	}

	@Override
	public void setParameters (IParameters params) {
		// Not used
	}

	private Event processStartSubDocument (Event event) {
		// Get ready for a new node
		outFields = new HashMap<String, String>();
		return event;
	}
	
	private Event processStartGroup (Event event) {
		StartGroup sg = event.getStartGroup();
		ann = sg.getAnnotation(FilterWriterAnnotation.class);
		if ( ann != null ) {
			// It's a drupal-generated group
			groupType = sg.getType();
			writer = ann.getFilterWriter();
			writer.setOptions(trgLoc, "UTF-8");
			outStream = new ByteArrayOutputStream();
			writer.setOutput(outStream);
			// Change the event to the start-document event
			event = new Event(EventType.START_DOCUMENT, ann.getResource());
		}
		// In all case: process the event with the writer
		return writer.handleEvent(event);
	}
	
	private Event processEndGroup (Event event) {
		ann = event.getEndGroup().getAnnotation(FilterWriterAnnotation.class);
		if ( ann == null ) {
			// That is not a Drupal-generated group, but some sub-filter group
			// Just keep on processing
			return writer.handleEvent(event);
		}
		// Process the event
		event = writer.handleEvent(event);
		writer.handleEvent(new Event(EventType.END_DOCUMENT, ann.getResource()));

		// Put the result of the output into a field
		// so we can update the node when we close the sub-document
		try {
			writer.close();
			writer = null;
			outFields.put(groupType, outStream.toString("UTF-8"));
		}
		catch ( UnsupportedEncodingException e ) {
			throw new OkapiIOException("Error in stream output.\n"+e.getMessage(), e);
		}
		return event;
	}

	private Event processEndSubDocument (Event event) {
		// The node has been processed
		// Now we can push the fields back to Drupal
		
		// Set the client if not done yet
		if ( cli == null ) {
			cli = new DrupalConnector(ann.getProject().getHost());
			cli.setCredentials(ann.getProject().getUser(), ann.getProject().getPassword());
			cli.login();
		}
		String nid = event.getEnding().getId();
		
		// Get the node from the server
		Node node;
		try {
			node = cli.getNode(nid, ann.getProject().getSourceLocale().toString(), ann.getProject().getNeutralLikeSource());
		}
		catch ( Throwable e ) {
			logger.warning(String.format("Could not find node %s in server when trying to merge.", nid));
			//TODO: log error
			// Move to the next node
			return event;
		}
		
		boolean neutralLikeSource = ann.getProject().getNeutralLikeSource();
		String trgLang = trgLoc.getLanguage(); 
		
		// Detect if we have a field for the given target language
		//TODO
		boolean trgBodyExists = node.hasLanguageForBody(trgLang);
		boolean trgTitleExists = node.hasLanguageForTitle(trgLang);
		
		// Update the fields
		node.setTitle(trgLang,
			convertToValueHTML(outFields.get("title")),
			neutralLikeSource);
		
		node.setBody(trgLang,
			convertToValueHTML(outFields.get("body")),
			convertToValueHTML(outFields.get("summary")),
			neutralLikeSource);

		// Push the updated field
		cli.updateNode(node);
		
		return event;
	}
	
	private String convertToValueHTML (String content) {
		if ( content == null ) return null;
		// For now, just replace the paragraph elements by a line break
		return content.replaceAll("<p[^>]*>", "").replace("</p>", "\r\n"); 
	}
	
	/**
	 * Log out at the end of the document
	 * @param event
	 * @return
	 */
	private Event processEndDocument (Event event) {
		cli.logout();
		return event;
	}
}
