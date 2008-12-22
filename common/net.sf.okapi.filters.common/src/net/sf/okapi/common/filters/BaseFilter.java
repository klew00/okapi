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

import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;
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
	private int documentId = 0;
	private int documentPartId = 0;
	private Stack<StartGroup> groupStack;
	private Stack<TextUnitWithSkeleton> textUnitStack;
	private Stack<Code> codeStack;
	private List<FilterEvent> filterEvents;
	private List<FilterEvent> referencableFilterEvents;
	private boolean canceled;
	
	private final class TextUnitWithSkeleton {		
		private TextUnit tu;
		private GenericSkeleton skel;
		
		public TextUnitWithSkeleton(TextUnit tu, GenericSkeleton skel) {
			this.tu = tu;
			this.skel = skel;
		}
		
		public TextUnit getTu() {
			return tu;
		}

		public void setTu(TextUnit tu) {
			this.tu = tu;
		}

		public GenericSkeleton getSkel() {
			return skel;
		}

		public void setSkel(GenericSkeleton skel) {
			this.skel = skel;
		}
		
		public boolean hasSkeleton() {
			if (skel == null) {
				return false;
			}
			return true;
		}
	}

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
			return referencableFilterEvents.remove(0);
		}
		if (!filterEvents.isEmpty()) {
			return filterEvents.remove(0);
		}

		return null;
	}

	public void cancel() {
		canceled = true;
		// flush out all pending events
		filterEvents.clear();
		referencableFilterEvents.clear();

		FilterEvent event = new FilterEvent(FilterEventType.CANCELED);
		filterEvents.add(event);
	}

	protected void initialize() {
		start();
		startDocument();
		startSubDocument();
	}

	protected void finalize() {
		if (hasUnfinishedSkeleton()) {
			endSkeleton();
		} else if (hasUnfinishedTextUnits()) {
			endTextUnit();
		}
		
		endSubDocument();
		endDocument();
		finish();
	}

	private void addPropertiesToResource(INameable resource, List<Property> properties) {
		if (properties != null) {
			for (Property property : properties) {
				resource.setProperty(property);
			}
		}
	}

	private TextUnitWithSkeleton getCurrentTextUnit() {
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
	
	protected boolean currentTextUnitsHasSkeleton() {
		if (textUnitStack.isEmpty()) {
			return false;
		}
		if (!textUnitStack.peek().hasSkeleton()) {
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
		documentPartId = 0;
		subDocumentId = 0;
		
		canceled = false;

		referencableFilterEvents = new LinkedList<FilterEvent>();
		filterEvents = new LinkedList<FilterEvent>();

		groupStack = new Stack<StartGroup>();
		textUnitStack = new Stack<TextUnitWithSkeleton>();
		codeStack = new Stack<Code>();
	}

	protected boolean isCanceled() {
		return canceled;
	}

	// ////////////////////////////////////////////////////////////////////////
	// Start and Finish Methods
	// ////////////////////////////////////////////////////////////////////////

	protected void start() {
		FilterEvent event = new FilterEvent(FilterEventType.START);
		filterEvents.add(event);
	}

	protected void finish() {
		FilterEvent event = new FilterEvent(FilterEventType.FINISHED);
		filterEvents.add(event);
	}

	protected void startDocument() {
		StartDocument startDocument = new StartDocument(String.format("d%d", ++documentId));
		FilterEvent event = new FilterEvent(FilterEventType.START_DOCUMENT, startDocument);		
		filterEvents.add(event);
	}

	protected void endDocument() {
		Ending endDocument = new Ending(String.format("d%d", ++documentId));
		FilterEvent event = new FilterEvent(FilterEventType.END_DOCUMENT, endDocument);
		filterEvents.add(event);
	}

	protected void startSubDocument() {		
		StartSubDocument startSubDocument = new StartSubDocument(String.format("sd%d", ++subDocumentId));
		FilterEvent event = new FilterEvent(FilterEventType.START_SUBDOCUMENT, startSubDocument);
		filterEvents.add(event);
	}

	protected void endSubDocument() {
		Ending endDocument = new Ending(String.format("sd%d", ++subDocumentId));
		FilterEvent event = new FilterEvent(FilterEventType.END_SUBDOCUMENT, endDocument);
		filterEvents.add(event);
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
		GenericSkeleton skel = null;
		
		if (hasUnfinishedSkeleton()) {
			endSkeleton();
		}

		TextUnit tu = new TextUnit(String.format("tu%d", ++textUnitId), text);		

		addPropertiesToResource(tu, properties);

		if (startMarker != null) {
			skel = new GenericSkeleton((GenericSkeleton) startMarker);
			skel.addRef(tu);
		}
		
		textUnitStack.push(new TextUnitWithSkeleton(tu, skel));
	}

	protected void endTextUnit(ISkeleton endMarker, List<Property> properties) {
		TextUnitWithSkeleton tuWithSkel;

		try {
			tuWithSkel = textUnitStack.pop();
		} catch (EmptyStackException e) {
			throw new BaseFilterException("TextUnit not found");
		}

		if (endMarker != null) {
			tuWithSkel.getSkel().add((GenericSkeleton)endMarker);
		}

		addPropertiesToResource(tuWithSkel.getTu(), properties);

		filterEvents.add(new FilterEvent(FilterEventType.TEXT_UNIT, tuWithSkel.getTu(), tuWithSkel.getSkel()));		
	}

	protected void addToTextUnit(String text) {		
		TextUnitWithSkeleton tuWithSkel = getCurrentTextUnit();
		if (tuWithSkel == null) {
			throw new BaseFilterException("TextUnit not found");
		}
		getCurrentTextUnit().getTu().getSource().append(text);
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

		String parentId = String.format("sub%d", subDocumentId);
		if (hasUnfinishedGroups()) {
			StartGroup parent = groupStack.lastElement();
			parentId = parent.getId();
		}

		StartGroup g = new StartGroup(parentId, String.format("sg%d", ++startGroupId));

		addPropertiesToResource(g, properties);

		groupStack.push(g);
		GenericSkeleton skel = new GenericSkeleton((GenericSkeleton) startMarker);
		if (getCurrentTextUnit() == null) {
			filterEvents.add(new FilterEvent(FilterEventType.START_GROUP, g, skel));
		} else {
			// These groups are referencables from an existing TextUnit. We add
			// these to a separate list.
			referencableFilterEvents.add(new FilterEvent(FilterEventType.START_GROUP, g, skel));
		}
	}

	protected void endGroup(ISkeleton endMarker, List<Property> properties) {
		StartGroup g;

		GenericSkeleton skel = new GenericSkeleton((GenericSkeleton) endMarker);

		try {
			g = groupStack.pop();
		} catch (EmptyStackException e) {
			throw new BaseFilterException("Cannot find start group");
		}

		addPropertiesToResource(g, properties);

		filterEvents.add(new FilterEvent(FilterEventType.END_GROUP, new Ending(String.format("eg%d", ++endGroupId)),
				skel));
	}

	// ////////////////////////////////////////////////////////////////////////
	// Standalone Property Methods (not attached to any other resource)
	// ////////////////////////////////////////////////////////////////////////

	protected void addProperty(Property property, String language) {
		if (hasUnfinishedSkeleton()) {
			endSkeleton();
		}

		DocumentPart dp = new DocumentPart(String.format("dp%d", ++documentPartId), false);
		dp.setTargetProperty(language, property);
		filterEvents.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));
	}

	protected void addProperties(List<Property> properties, String language) {
		for (Property property : properties) {
			addProperty(property, language);
		}
	}

	protected void addProperty(Property property) {
		if (hasUnfinishedSkeleton()) {
			endSkeleton();
		}

		DocumentPart dp = new DocumentPart(String.format("dp%d", ++documentPartId), false);
		dp.setProperty(property);
		filterEvents.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));
	}

	protected void addProperties(List<Property> properties) {
		for (Property property : properties) {
			addProperty(property);
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	// Code Methods
	// ////////////////////////////////////////////////////////////////////////

	protected void startCode(Code code) {		
		if (!hasUnfinishedTextUnits()) {
			throw new BaseFilterException("TextUnit not found. Cannot add a Code to a non-exisitant TextUnit.");
		}
		codeStack.push(code);
	}

	protected void endCode() {
		Code code;
		try {
			code = codeStack.pop();
			getCurrentTextUnit().getTu().getSource().append(code);
		} catch (EmptyStackException e) {
			throw new BaseFilterException("Code not found. Cannot end a non-exisitant code.");
		}
	}

	protected void addToCode(String data) {
		Code code;
		try {
			code = codeStack.pop();
			code.append(data);
		} catch (EmptyStackException e) {
			throw new BaseFilterException("Code not found. Cannot add data to a non-exisitant code.");
		}
	}

	protected void addToCode(Property property) {
		Code code;
		
		code = getCurrentCode();
		
		if (code == null) {
			throw new BaseFilterException("Code not found. Cannot add a Property to a non-exisitant code.");
		}
		
		String refMarker = TextFragment.makeRefMarker(getCurrentTextUnit().getTu().getId());
		code.append(refMarker);
		code.setHasReference(true);

		DocumentPart dp = new DocumentPart(String.format("dp%d", ++documentPartId), true);
		dp.setProperty(property);
		referencableFilterEvents.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));
	}

	// ////////////////////////////////////////////////////////////////////////
	// Skeleton Methods
	// ////////////////////////////////////////////////////////////////////////

	private void startSkeleton(String skel) {
		currentSkeleton = new GenericSkeleton(skel);
	}

	private void endSkeleton(String skel) {
		if (skel != null) {
			currentSkeleton.append(skel);
		}
		filterEvents.add(new FilterEvent(FilterEventType.DOCUMENT_PART, new DocumentPart(String.format("dp%d",
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
