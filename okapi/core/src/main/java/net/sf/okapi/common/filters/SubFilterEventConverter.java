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

package net.sf.okapi.common.filters;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.IdGenerator;
import net.sf.okapi.common.resource.EndSubfilter;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubfilter;

/**
 * Helper class to start and stop a sub-filter called from a parent filter.
 * @author HargraveJE
 */
public class SubFilterEventConverter {

	private String parentId;
	private IdGenerator groupIdGenerator;
	private ISkeleton startGroupSkeleton;
	private ISkeleton endGroupSkeleton;

	/**
	 * Creates a new SubFilterEventConverter object.
	 * @param parentId the parent id to use for the group where the sub-filter events are to be stored.
	 * @param startGroupSkeleton the optional skeleton to put with the start group.
	 * @param endGroupSkeleton the optional skeleton to put with the end group.
	 */
	public SubFilterEventConverter(String parentId, ISkeleton startGroupSkeleton,
			ISkeleton endGroupSkeleton) {
		this.groupIdGenerator = new IdGenerator(parentId);
		this.parentId = parentId;
		this.startGroupSkeleton = startGroupSkeleton;
		this.endGroupSkeleton = endGroupSkeleton;
	}

	/**
	 * Creates a new SubFilterEventConverter object using a given id generator.
	 * @param parentId the parent id to use for the group where the sub-filter events are to be stored.
	 * @param startGroupSkeleton the optional skeleton to put with the start group.
	 * @param endGroupSkeleton the optional skeleton to put with the end group.
	 * @param idGenerator the id generator to use.
	 */
	public SubFilterEventConverter(String parentId, ISkeleton startGroupSkeleton,
			ISkeleton endGroupSkeleton, IdGenerator idGenerator) {
		idGenerator.reset(parentId);
		this.groupIdGenerator = idGenerator; 
		this.parentId = parentId;
		this.startGroupSkeleton = startGroupSkeleton;
		this.endGroupSkeleton = endGroupSkeleton;
	}
	
	public SubFilterEventConverter(String parentId) {
		this.groupIdGenerator = new IdGenerator(parentId); 
		this.parentId = parentId;
		this.startGroupSkeleton = null;
		this.endGroupSkeleton = null;
	}

	/**
	 * Converts an event.
	 * @param event the event coming from the sub-filter.
	 * @return the event after possible conversion.
	 */
	public Event convertEvent(Event event) {
		// we convert start-document to start group
		// and end document to end group
		switch (event.getEventType()) {
		case START_DOCUMENT:
			StartSubfilter startSubFilter = new StartSubfilter(parentId, groupIdGenerator.createId());
			startSubFilter.setMimeType(((StartDocument) event.getResource()).getMimeType());
			startSubFilter.setSkeleton(startGroupSkeleton);
			startSubFilter.setName(IFilter.SUB_FILTER + ((StartDocument) event.getResource()).getName());			
			event = new Event(EventType.START_GROUP, startSubFilter);
			break;

		case END_DOCUMENT:
			EndSubfilter endSubfilter = new EndSubfilter(groupIdGenerator.getLastId());
			endSubfilter.setSkeleton(endGroupSkeleton);
			event = new Event(EventType.END_GROUP, endSubfilter);
			break;

		default:
			break;
		}

		return event;
	}
}
