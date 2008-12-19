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

import java.util.List;
import java.util.Stack;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;

public abstract class BaseFilter implements IFilter {
	private int startGroupId = 0;
	private int endGroupId = 0;
	private int textUnitId = 0;
	private int subDocumentId = 0;
	private int documentPartId = 0;
	private Stack<StartGroup> groupStack;
	private Stack<TextUnit> textUnitStack;
	private Stack<Code> codeStack;
	private Stack<FilterEvent> filterEvents;
	private Stack<FilterEvent> referencableFilterEvents;
	private boolean canceled;

	protected GenericSkeleton currentSkeleton;

	public BaseFilter() {
		reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IFilter#hasNext()
	 */
	public boolean hasNext() {
		if (!referencableFilterEvents.isEmpty() && !filterEvents.isEmpty()) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IFilter#next()
	 */
	public FilterEvent next() {
		if (!referencableFilterEvents.isEmpty()) {
			return referencableFilterEvents.pop();
		}
		if (!filterEvents.isEmpty()) {
			return filterEvents.pop();
		}
		return null;
	}

	public void cancel() {
		canceled = true;
		// flush out all pending events
		filterEvents.clear();
		referencableFilterEvents.clear();
		
		FilterEvent event = new FilterEvent(FilterEventType.CANCELED);
		filterEvents.push(event);
	}
	
	private void addPropertiesToResource(INameable resource, List<Property> properties) {
		if (properties != null) {
			for (Property property : properties) {
				resource.setProperty(property);
			}
		}
	}

	private TextUnit getCurrentTextUnit() {
		if (textUnitStack.isEmpty()) {
			return null;
		}
		return textUnitStack.peek();
	}

	private StartGroup getCurrentGroup() {
		if (groupStack.isEmpty()) {
			return null;
		}
		return groupStack.peek();
	}

	private Code getCurrentCode() {
		if (codeStack.isEmpty()) {
			return null;
		}
		return codeStack.peek();
	}

	protected boolean hasQueuedEvents() {
		if (filterEvents.isEmpty()) {
			return false;
		}
		return true;
	}

	protected boolean hasUnfinishedTextUnits() {
		if (textUnitStack.isEmpty()) {
			return false;
		}
		return true;
	}

	protected boolean hasUnfinishedGroups() {
		if (groupStack.isEmpty()) {
			return false;
		}
		return true;
	}

	protected boolean hasUnfinishedCodes() {
		if (codeStack.isEmpty()) {
			return false;
		}
		return true;
	}

	protected boolean hasUnfinishedSkeleton() {
		if (currentSkeleton == null) {
			return false;
		}
		return true;
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

		canceled = false;

		referencableFilterEvents.clear();
		filterEvents.clear();
		groupStack.clear();
		textUnitStack.clear();
		codeStack.clear();
	}

	protected boolean isCanceled() {
		return canceled;
	}

	// ////////////////////////////////////////////////////////////////////////
	// Start and Finish Methods
	// ////////////////////////////////////////////////////////////////////////

	protected void start() {
		FilterEvent event = new FilterEvent(FilterEventType.START);
		filterEvents.push(event);
	}

	protected void finish() {
		FilterEvent event = new FilterEvent(FilterEventType.FINISHED);
		filterEvents.push(event);
	}

	// ////////////////////////////////////////////////////////////////////////
	// TextUnit Methods
	// ////////////////////////////////////////////////////////////////////////

	protected void startTextUnit(String text) {
		startTextUnit(text, null, null);
	}
	
	protected void startTextUnit() {
		startTextUnit(null, null, null);
	}

	protected void endTextUnit() {
		endTextUnit(null, null);
	}

	protected void startTextUnit(ISkeleton startMarker) {
		startTextUnit(null, startMarker, null);
	}

	protected void endTextUnit(ISkeleton endMarker) {
		endTextUnit(endMarker, null);
	}

	protected void startTextUnit(String text, ISkeleton startMarker, List<Property> properties) {
		if (hasUnfinishedSkeleton()) {
			endSkeleton();
		}
		
		TextUnit tu = new TextUnit(String.format("s%d", ++textUnitId), text);
		textUnitStack.push(tu);

		addPropertiesToResource(tu, properties);

		if (startMarker != null) {
			currentSkeleton = new GenericSkeleton((GenericSkeleton)startMarker);
			currentSkeleton.addRef(tu);
		}
	}

	protected void endTextUnit(ISkeleton endMarker, List<Property> properties) {
		TextUnit tu = textUnitStack.pop();
		if (endMarker != null) {
			currentSkeleton.add((GenericSkeleton)endMarker);
		}

		addPropertiesToResource(tu, properties);

		filterEvents.push(new FilterEvent(FilterEventType.TEXT_UNIT, tu, currentSkeleton));
		currentSkeleton = null;
	}

	protected void addToTextUnit(String text) {
		getCurrentTextUnit().getSource().append(text);
	}

	// ////////////////////////////////////////////////////////////////////////
	// Group Methods
	// ////////////////////////////////////////////////////////////////////////

	protected void startGroup(ISkeleton startMarker) {
		startGroup(startMarker, null);
	}

	protected void endGroup(ISkeleton endMarker) {
		endGroup(endMarker, null);
	}

	protected void startGroup(ISkeleton startMarker, List<Property> properties) {
		if (hasUnfinishedSkeleton()) {
			endSkeleton();
		}
		
		String parentId = String.format("s%d", subDocumentId);
		if (!groupStack.isEmpty()) {
			StartGroup g = groupStack.lastElement();
			parentId = g.getId();
		}

		StartGroup g = new StartGroup(parentId, String.format("s%d", ++startGroupId));

		addPropertiesToResource(g, properties);

		groupStack.push(g);
		GenericSkeleton skel = new GenericSkeleton((GenericSkeleton)startMarker);
		if (getCurrentTextUnit() == null) {
			filterEvents.push(new FilterEvent(FilterEventType.START_GROUP, g, skel));
		} else {
			// These groups are referencables from an existing TextUnit. We add
			// these to a separate list.
			referencableFilterEvents.push(new FilterEvent(FilterEventType.START_GROUP, g, skel));
		}
	}

	protected void endGroup(ISkeleton endMarker, List<Property> properties) {
		GenericSkeleton skel = new GenericSkeleton((GenericSkeleton)endMarker);
		StartGroup g = groupStack.pop();

		addPropertiesToResource(g, properties);

		filterEvents.push(new FilterEvent(FilterEventType.END_GROUP, new Ending(String.format("s%d", ++endGroupId)),
				skel));
	}

	// ////////////////////////////////////////////////////////////////////////
	// Property Methods
	// ////////////////////////////////////////////////////////////////////////

	protected void addProperty(Property property, String language) {
		DocumentPart dp = new DocumentPart(String.format("s%d", ++documentPartId), true);
		dp.setTargetProperty(language, property);
		referencableFilterEvents.push(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));
	}

	protected void addProperties(List<Property> properties, String language) {
		for (Property property : properties) {
			addProperty(property, language);
		}
	}

	protected void addProperty(Property property) {
		DocumentPart dp = new DocumentPart(String.format("s%d", ++documentPartId), true);
		dp.setProperty(property);
		referencableFilterEvents.push(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));
	}

	protected void addProperties(List<Property> properties) {
		for (Property property : properties) {
			addProperty(property);
		}
	}

	protected void addProperty(Property property, Code code) {
		DocumentPart dp = new DocumentPart(String.format("s%d", ++documentPartId), true);
		dp.setProperty(property);
		referencableFilterEvents.push(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));
	}

	protected void addProperties(List<Property> properties, Code code) {
		for (Property property : properties) {
			addProperty(property, code);
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	// Code Methods
	// ////////////////////////////////////////////////////////////////////////

	protected void startCode(Code code) {
		codeStack.push(code);
	}

	protected void endCode() {
		Code code = codeStack.pop();
		getCurrentTextUnit().getSource().append(code);
	}

	protected void addToCode(String data) {
	}

	protected void addToCode(Property property) {
		String refMarker = TextFragment.makeRefMarker(getCurrentTextUnit().getId());
		getCurrentCode().append(refMarker);
		getCurrentCode().setHasReference(true);

		addProperty(property);
	}

	// ////////////////////////////////////////////////////////////////////////
	// Skeleton Methods
	// ////////////////////////////////////////////////////////////////////////
	
	private void startSkeleton(String skel) {
		currentSkeleton = new GenericSkeleton(skel);
	}

	protected void endSkeleton(String skel) {
		if (skel != null) {
			currentSkeleton.append(skel);
		}
		filterEvents.push(new FilterEvent(FilterEventType.DOCUMENT_PART, new DocumentPart(String.format("s%d",
				++documentPartId), false), currentSkeleton));
		currentSkeleton = null;
	}

	private void endSkeleton() {
		endSkeleton(null);
	}

	protected void addToSkeleton(String skel) {
		if (currentSkeleton == null) {
			startSkeleton(skel);
			return;
		}
		currentSkeleton.append(skel);
	}
}
