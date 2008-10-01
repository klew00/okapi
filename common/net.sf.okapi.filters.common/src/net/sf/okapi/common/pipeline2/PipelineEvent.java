/*===========================================================================*/
/* Copyright (C) 2008 Jim Hargrave                                           */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.pipeline2;

import java.util.concurrent.ConcurrentHashMap;

public class PipelineEvent implements IPipelineEvent {

	public static enum PipelineEventType {
		START_RESOURCE, END_RESOURCE, TEXTUNIT, SKELETON, START_GROUP, END_GROUP, START, FINISHED
	};

	private final int order;
	private final PipelineEventType pipelineEventType;
	private final Object data; // TextUnit, Skeleton, Group or other data object
	private final ConcurrentHashMap<String, Object> metadata; // annotations
	
	public PipelineEvent(PipelineEventType pipelineEventType, Object data, int order) {
		this.pipelineEventType = pipelineEventType;
		this.data = data;
		this.order = order;
		this.metadata = new ConcurrentHashMap<String, Object>();
	}
	
	public Object getData() {	
		return data;
	}
	
	public Enum<?> getEventType() {		
		return pipelineEventType;
	}
	
	public ConcurrentHashMap<String, Object> getMetadata() {		
		return metadata;
	}
	
	public int getOrder() {
		return order;
	}	
}
