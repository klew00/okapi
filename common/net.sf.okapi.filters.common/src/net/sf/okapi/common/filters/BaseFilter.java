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
import java.util.Stack;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;

public abstract class BaseFilter implements IFilter {
	private TextUnit currentTextUnit;
	private int startGroupId = 0;
	private int endGroupId = 0;
	private int textUnitId = 0;
	private int subDocumentId = 0;
	private int documentPartId = 0;
	private Stack<StartGroup> groupStack;
	private StartGroup currentStartGroup;
	private GenericSkeleton currentSkeleton;
	private List<FilterEvent> filterEvents;
	private List<FilterEvent> referencableFilterEvents;

	public BaseFilter() {
		reset();
	}

	/**
	 * Reset parser for new input.
	 */
	protected void reset() {
		startGroupId = 0;
		endGroupId = 0;
		textUnitId = 0;
		subDocumentId = 0;
		documentPartId = 0;

		referencableFilterEvents = new LinkedList<FilterEvent>();
		filterEvents = new LinkedList<FilterEvent>();
		groupStack = new Stack<StartGroup>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IFilter#getResource()
	 */
	public IResource getResource() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IFilter#hasNext()
	 */
	public boolean hasNext() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IFilter#next()
	 */
	public FilterEvent next() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IFilter#setOptions(java.lang.String,
	 * java.lang.String, boolean)
	 */
	public void setOptions(String sourceLanguage, String defaultEncoding, boolean generateSkeleton) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IFilter#setOptions(java.lang.String,
	 * java.lang.String, java.lang.String, boolean)
	 */
	public void setOptions(String sourceLanguage, String targetLanguage, String defaultEncoding,
			boolean generateSkeleton) {
	}

	private void addPropertiesToResource(INameable resource, List<Property> properties) {
		if (properties != null) {
			for (Property property : properties) {
				resource.setProperty(property);
			}
		}
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
		startTextUnit(null, null);
	}

	protected void endTextUnit() {
		endTextUnit(null, null);
	}

	protected void startTextUnit(String startMarker) {
		startTextUnit(startMarker, null);
	}

	protected void endTextUnit(String endMarker) {
		endTextUnit(endMarker, null);
	}

	protected void startTextUnit(String startMarker, List<Property> properties) {
		currentTextUnit = new TextUnit(String.format("s%d", ++textUnitId));

		addPropertiesToResource(currentTextUnit, properties);

		if (startMarker != null) {
			currentSkeleton = new GenericSkeleton(startMarker);
			currentSkeleton.addRef(currentTextUnit);
		}
	}

	protected void endTextUnit(String endMarker, List<Property> properties) {
		if (endMarker != null) {
			currentSkeleton.add(endMarker);
		}

		addPropertiesToResource(currentTextUnit, properties);

		filterEvents.add(new FilterEvent(FilterEventType.TEXT_UNIT, currentTextUnit, currentSkeleton));
		currentSkeleton = null;
		currentTextUnit = null;
	}

	protected void addToTextUnit(String text) {
		currentTextUnit.getSource().append(text);
	}	

//	protected void addToTextUnit(Code code, List<Property> properties) {
//		String data = code.getData();
//		for (Property property : properties) {
//			String value = property.getValue();
//			String refMarker = TextFragment.makeRefMarker(currentTextUnit.getId());
//			if (property.isReadOnly()) {
//				refMarker = TextFragment.makeRefMarker();
//			}
//			data.replaceFirst("", refMarker);
//		}
//		code.setData(data);
//		addToTextUnit(code);
//		code.setHasReference(true);
//	}
	
	protected void addToTextUnit(Code code) {
		currentTextUnit.getSource().append(code);
	}
	
	protected void startGroup(String startMarker) {
		startGroup(startMarker, null);
	}

	protected void endGroup(String endMarker) {
		endGroup(endMarker, null);
	}

	protected void startGroup(String startMarker, List<Property> properties) {
		String parentId = String.format("s%d", subDocumentId);
		if (groupStack.capacity() > 0) {
			StartGroup g = groupStack.lastElement();
			parentId = g.getId();
		}

		currentStartGroup = new StartGroup(parentId, String.format("s%d", ++startGroupId));

		addPropertiesToResource(currentStartGroup, properties);

		groupStack.push(currentStartGroup);
		GenericSkeleton skel = new GenericSkeleton(startMarker);
		if (currentTextUnit == null) {
			filterEvents.add(new FilterEvent(FilterEventType.START_GROUP, currentStartGroup, skel));
		} else {
			// These groups are referencables from an existing TextUnit. We add
			// these to a separate list.
			referencableFilterEvents.add(new FilterEvent(FilterEventType.START_GROUP, currentStartGroup, skel));
		}
	}

	protected void endGroup(String endMarker, List<Property> properties) {
		GenericSkeleton skel = new GenericSkeleton(endMarker);
		StartGroup startGroup = groupStack.pop();

		addPropertiesToResource(startGroup, properties);

		filterEvents.add(new FilterEvent(FilterEventType.END_GROUP, new Ending(String.format("s%d", ++endGroupId)),
				skel));

		currentStartGroup = null;
	}

	protected void addProperty(Property property, String language) {
		DocumentPart dp = new DocumentPart(String.format("s%d", ++documentPartId), true);
		dp.setTargetProperty(language, property);
		referencableFilterEvents.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));
	}

	protected void addProperties(List<Property> properties, String language) {
		for (Property property : properties) {
			addProperty(property, language);
		}
	}

	protected void addProperty(Property property) {
		DocumentPart dp = new DocumentPart(String.format("s%d", ++documentPartId), true);
		dp.setProperty(property);
		referencableFilterEvents.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));
	}

	protected void addProperties(List<Property> properties) {
		for (Property property : properties) {
			addProperty(property);
		}
	}
	
	protected void addProperty(Property property, Code code) {
		DocumentPart dp = new DocumentPart(String.format("s%d", ++documentPartId), true);
		dp.setProperty(property);
		referencableFilterEvents.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));
	}

	protected void addProperties(List<Property> properties, Code code) {
		for (Property property : properties) {
			addProperty(property, code);
		}
	}
}
