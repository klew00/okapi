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

package net.sf.okapi.steps.simplekit.common;

import java.io.File;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.filters.simplekit.Manifest;
import net.sf.okapi.filters.simplekit.MergingInfo;

public class Merger {

	private static final Logger LOGGER = Logger.getLogger(Merger.class.getName());

	private IFilter filter;
	private IFilterWriter writer;
	private Manifest manifest;
	private LocaleId trgLoc;
	private IFilterConfigurationMapper fcMapper;
	
	public Merger (Manifest manifest,
		IFilterConfigurationMapper fcMapper)
	{
		this.fcMapper = fcMapper;
		this.manifest = manifest;
		trgLoc = manifest.getTargetLocale();
	}
	
	public void close () {
		if ( writer != null ) {
			writer.close();
			writer = null;
		}
		if ( filter != null ) {
			filter.close();
			filter = null;
		}
	}

	public Event handleEvent (Event event) {
		switch ( event.getEventType() ) {
		case TEXT_UNIT:
			processTextUnit(event);
			break;
		case END_DOCUMENT:
			processEndDocument();
			break;
		}
		return event;
	}

	public void startMerging (MergingInfo info) {
		// Create the filter for this original file
		filter = fcMapper.createFilter(info.getFilterId(), filter);
		if ( filter == null ) {
			throw new OkapiBadFilterInputException(String.format("Filter cannot be created (%s).", info.getFilterId()));
		}
		IParameters fprm = filter.getParameters();
		if ( fprm != null ) {
			fprm.fromString(info.getFilterParameters());
		}

		File file = new File(manifest.getOriginalDirectory() + info.getRelativeInputPath());
		RawDocument rd = new RawDocument(file.toURI(), info.getInputEncoding(),
			manifest.getSourceLocale(), trgLoc);
		
		filter.open(rd);
		writer = filter.createFilterWriter();
		writer.setOptions(trgLoc, info.getTargetEncoding());
		writer.setOutput(manifest.getMergeDirectory()+info.getRelativeTargetPath());
		
		Event event = null;
		if ( filter.hasNext() ) {
			// Should be the start-document event
			event = filter.next();
		}
		if (( event == null ) || ( event.getEventType() != EventType.START_DOCUMENT )) {
			LOGGER.severe("The start document event is missing when parsing the original file.");
			return;
		}
		writer.handleEvent(event);
	}

	private void processEndDocument () {
		// Finish to go through the original file
		while ( filter.hasNext() ) {
			writer.handleEvent(filter.next());
		}
	}
	
	private void processTextUnit (Event event) {
		TextUnit traTu = event.getTextUnit();
		Event oriEvent = processUntilTextUnit();
		if ( oriEvent == null ) {
			LOGGER.severe(String.format("No corresponding text unit for id='%s' in the original file.",
				traTu.getId()));
			return;
		}
		TextUnit oriTu = oriEvent.getTextUnit();

		// Check the IDs
		if ( !traTu.getId().equals(oriTu.getId()) ) {
			LOGGER.severe(String.format("De-synchronized files: translated TU id='%s', Original TU id='%s'.",
				traTu.getId(), oriTu.getId()));
			return;
		}
		
		// Check if we have a translation
		TextContainer tc = traTu.getTarget(trgLoc);
		if ( tc == null ) {
			LOGGER.warning(String.format("No translation found for TU id='%s'.", traTu.getId()));
			writer.handleEvent(oriEvent); // Use the source
			return;
		}
		
		// Do we need to preserve the segmentation for merging (e.g. TTX case)
//TODO		boolean mergeAsSegments = false;
//		if ( oriTu.getMimeType() != null ) { 
//			if ( oriTu.getMimeType().equals(MimeTypeMapper.TTX_MIME_TYPE)
//				|| oriTu.getMimeType().equals(MimeTypeMapper.XLIFF_MIME_TYPE) ) {
//				mergeAsSegments = true;
//			}
//		}
		
		TextFragment traTrgTf = tc.getUnSegmentedContentCopy();
		TextFragment oriSrcTf = oriTu.getSource().getUnSegmentedContentCopy();
		
		TextUnitUtil.adjustTargetCodes(oriSrcTf, traTrgTf, true, true, null, oriTu);
		oriTu.setTargetContent(trgLoc, traTrgTf);
		
		writer.handleEvent(oriEvent);
	}

	/**
	 * Get events in the original document until the next text unit.
	 * Any event before is passed to the writer.
	 * @return the event of the next text unit, or null if no next text unit is found.
	 */
	private Event processUntilTextUnit () {
		while ( filter.hasNext() ) {
			Event event = filter.next();
			if ( event.getEventType() == EventType.TEXT_UNIT ) {
				return event;
			}
			// Else: write out the event
			writer.handleEvent(event);
		}
		// This text unit is extra in the translated file
		//TODO: log error
		return null;
	}
}
