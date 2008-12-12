/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.filters;

import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.SkeletonFactory;

public abstract class BaseFilter<S extends ISkeleton> implements IFilter {	
	S skeletonClass;
	private TextUnit currentTextUnit;
	private int groupId = 0;
	private int textUnitId = 0;
	private List<FilterEvent> filterEvents;
	
	public BaseFilter() {
		filterEvents = new LinkedList<FilterEvent>();
		skeletonClass = SkeletonFactory.createSkeleton(skeletonClass);
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilter#getResource()
	 */
	public IResource getResource() {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilter#hasNext()
	 */
	public boolean hasNext() {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilter#next()
	 */
	public FilterEvent next() {
		return null;
	}	

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilter#setOptions(java.lang.String, java.lang.String, boolean)
	 */
	public void setOptions(String sourceLanguage, String defaultEncoding, boolean generateSkeleton) {
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.filters.IFilter#setOptions(java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void setOptions(String sourceLanguage, String targetLanguage, String defaultEncoding,
			boolean generateSkeleton) {
	}
	
	protected void start() {
		FilterEvent event = new FilterEvent(FilterEventType.START);
		filterEvents.add(event);
	}
	
	protected void finish() {
		FilterEvent event = new FilterEvent(FilterEventType.FINISHED);
		filterEvents.add(event);
	}
	
	protected void startTextUnit() {
		currentTextUnit = new TextUnit(String.format("s%d", ++textUnitId));
	}
	
	protected void endTextUnit() {
		FilterEvent event = new FilterEvent(FilterEventType.TEXT_UNIT, currentTextUnit, );
		filterEvents.add(event);
	}
}
