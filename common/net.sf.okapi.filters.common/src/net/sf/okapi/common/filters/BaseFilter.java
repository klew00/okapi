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
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;

public abstract class BaseFilter implements IFilter {
	private static final String START_GROUP = "sg"; //$NON-NLS-1$
	private static final String END_GROUP = "eg"; //$NON-NLS-1$
	private static final String TEXT_UNIT = "tu"; //$NON-NLS-1$
	private static final String DOCUMENT_PART = "dp"; //$NON-NLS-1$
	private static final String START_DOCUMENT = "sd"; //$NON-NLS-1$
	private static final String END_DOCUMENT = "ed"; //$NON-NLS-1$
	private static final String START_SUBDOCUMENT = "ssd"; //$NON-NLS-1$
	private static final String END_SUBDOCUMENT = "esd"; //$NON-NLS-1$

	private String encoding;
	private String srcLang;
	private String mimeType;

	private int startGroupId = 0;
	private int endGroupId = 0;
	private int textUnitId = 0;
	private int subDocumentId = 0;
	private int documentId = 0;
	private int documentPartId = 0;

	private Stack<FilterEvent> tempFilterEventStack;

	private List<FilterEvent> filterEvents;
	private List<FilterEvent> referencableFilterEvents;

	private boolean canceled = false;
	private boolean done = false;

	private GenericSkeleton currentSkeleton;
	private Code currentCode;
	private DocumentPart currentDocumentPartForStandaloneProperties;

	public BaseFilter() {
		// reset is called in initialize method - no need to call it twice
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IFilter#hasNext()
	 */
	public boolean hasNext() {
		return !done;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.okapi.common.filters.IFilter#next()
	 */
	public FilterEvent next() {
		FilterEvent event;

		if (hasNext()) {
			if (!referencableFilterEvents.isEmpty()) {				
				return referencableFilterEvents.remove(0);
			} else if (!filterEvents.isEmpty()) {
				event = filterEvents.remove(0);
				if (event.getEventType() == FilterEventType.FINISHED)
					done = true;
				return event;
			}
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
		finish();
	}

	private String createId(String name, int number) {
		return String.format("%s%d", name, number); //$NON-NLS-1$
	}

	private void addPropertiesToResource(INameable resource, List<Property> properties) {
		if (properties != null) {
			for (Property property : properties) {
				addPropertyToResource(resource, property);
			}
		}
	}

	private void addPropertyToResource(INameable resource, Property property) {
		if (property != null) {
			resource.setProperty(property);
		}
	}

	private FilterEvent peekTempEvent() {
		if (tempFilterEventStack.isEmpty()) {
			return null;
		}
		return tempFilterEventStack.peek();
	}

	private FilterEvent popTempEvent() {
		if (tempFilterEventStack.isEmpty()) {
			return null;
		}
		return tempFilterEventStack.pop();
	}

	protected String getEncoding() {
		return encoding;
	}

	protected void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	protected String getSrcLang() {
		return srcLang;
	}

	protected void setSrcLang(String srcLang) {
		this.srcLang = srcLang;
	}

	protected String getMimeType() {
		return mimeType;
	}

	protected void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	protected void initialize() {
		reset();
		start();
		startDocument();
	}

	@Override
	protected void finalize() {
		if (hasUnfinishedSkeleton()) {
			endSkeleton();
		} else if (!tempFilterEventStack.isEmpty()) {
			// go through filtered object stack and close them one by one
			while (!tempFilterEventStack.isEmpty()) {
				FilterEvent fe = tempFilterEventStack.pop();
				if (fe.getEventType() == FilterEventType.START_GROUP) {
					endGroup(new GenericSkeleton("")); //$NON-NLS-1$
				} else if (fe.getEventType() == FilterEventType.TEXT_UNIT) {
					if (fe.getResource().getSkeleton() != null) {
						endTextUnit(new GenericSkeleton("")); //$NON-NLS-1$
					} else {
						endTextUnit();
					}
				}
			}
		}

		endDocument();
		finish();
	}

	protected boolean isCurrentTextUnit() {
		FilterEvent e = peekTempEvent();
		if (e != null && e.getEventType() == FilterEventType.TEXT_UNIT) {
			return true;
		}
		return false;
	}

	protected boolean isCurrentComplexTextUnit() {
		FilterEvent e = peekTempEvent();
		if (e != null && e.getEventType() == FilterEventType.TEXT_UNIT && e.getResource().getSkeleton() != null) {
			return true;
		}
		return false;
	}

	protected boolean isCurrentGroup() {
		FilterEvent e = peekTempEvent();
		if (e != null && e.getEventType() == FilterEventType.START_GROUP) {
			return true;
		}
		return false;
	}

	protected boolean isInsideTextRun() {
		return isCurrentTextUnit();
	}

	protected boolean canStartNewTextUnit() {
		if (isCurrentTextUnit()) {
			return false;
		}
		return true;
	}

	protected boolean hasQueuedEvents() {
		if (filterEvents.isEmpty()) {
			return false;
		}
		return true;
	}

	protected FilterEvent peekMostRecentGroup() {
		if (tempFilterEventStack.isEmpty()) {
			return null;
		}
		for (FilterEvent fe : tempFilterEventStack) {
			if (fe.getEventType() == FilterEventType.START_GROUP) {
				return fe;
			}
		}
		return null;
	}

	protected FilterEvent peekMostRecentTextUnit() {
		if (tempFilterEventStack.isEmpty()) {
			return null;
		}
		for (FilterEvent fe : tempFilterEventStack) {
			if (fe.getEventType() == FilterEventType.TEXT_UNIT) {
				return fe;
			}
		}
		return null;
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
		done = false;

		referencableFilterEvents = new LinkedList<FilterEvent>();
		filterEvents = new LinkedList<FilterEvent>();

		tempFilterEventStack = new Stack<FilterEvent>();

		currentCode = null;
		currentSkeleton = null;
		currentDocumentPartForStandaloneProperties = null;
	}

	protected boolean isCanceled() {
		return canceled;
	}

	// ////////////////////////////////////////////////////////////////////////
	// Start and Finish Methods
	// ////////////////////////////////////////////////////////////////////////

	private void start() {
		FilterEvent event = new FilterEvent(FilterEventType.START);
		filterEvents.add(event);
	}

	private void finish() {
		FilterEvent event = new FilterEvent(FilterEventType.FINISHED);
		filterEvents.add(event);
	}

	protected void startDocument() {
		StartDocument startDocument = new StartDocument(createId(START_DOCUMENT, ++documentId));
		startDocument.setEncoding(getEncoding());
		startDocument.setLanguage(getSrcLang());
		startDocument.setMimeType(getMimeType());
		FilterEvent event = new FilterEvent(FilterEventType.START_DOCUMENT, startDocument);
		filterEvents.add(event);
	}

	protected void endDocument() {
		Ending endDocument = new Ending(createId(END_DOCUMENT, ++documentId));
		FilterEvent event = new FilterEvent(FilterEventType.END_DOCUMENT, endDocument);
		filterEvents.add(event);
	}

	protected void startSubDocument() {
		StartSubDocument startSubDocument = new StartSubDocument(createId(START_SUBDOCUMENT, ++subDocumentId));
		FilterEvent event = new FilterEvent(FilterEventType.START_SUBDOCUMENT, startSubDocument);
		filterEvents.add(event);
	}

	protected void endSubDocument() {
		Ending endDocument = new Ending(createId(END_SUBDOCUMENT, ++subDocumentId));
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

		TextUnit tu = new TextUnit(createId(TEXT_UNIT, ++textUnitId), text);

		addPropertiesToResource(tu, properties);

		if (startMarker != null) {
			skel = new GenericSkeleton((GenericSkeleton) startMarker);
			skel.addRef(tu);
		}

		tempFilterEventStack.push(new FilterEvent(FilterEventType.TEXT_UNIT, tu, skel));
	}

	protected void endTextUnit(ISkeleton endMarker, List<Property> properties) {
		FilterEvent tempTextUnit;

		if (!isCurrentTextUnit()) {
			throw new BaseFilterException("Found non-TextUnit event. Cannot end TextUnit");
		}

		tempTextUnit = popTempEvent();

		if (endMarker != null) {
			GenericSkeleton skel = (GenericSkeleton) tempTextUnit.getResource().getSkeleton();
			skel.add((GenericSkeleton) endMarker);
		}

		addPropertiesToResource((TextUnit) tempTextUnit.getResource(), properties);

		filterEvents.add(tempTextUnit);
	}

	protected void addToTextUnit(String text) {
		if (!isCurrentTextUnit()) {
			throw new BaseFilterException("TextUnit not found. Cannot add text");
		}

		FilterEvent tempTextUnit = peekTempEvent();
		TextUnit tu = (TextUnit) tempTextUnit.getResource();
		tu.getSource().append(text);
	}

	protected void addToTextUnit(Property property) {
		if (!isCurrentTextUnit()) {
			throw new BaseFilterException("Found non-TextUnit event. Cannot add property");
		}

		FilterEvent tempTextUnit = peekTempEvent();

		addPropertyToResource((TextUnit) tempTextUnit.getResource(), property);
	}

	// ////////////////////////////////////////////////////////////////////////
	// Group Methods
	// ////////////////////////////////////////////////////////////////////////

	protected void startGroup(ISkeleton startMarker) {
		startGroup(startMarker, null);
	}

	protected void startGroup(ISkeleton startMarker, List<Property> properties) {
		if (hasUnfinishedSkeleton()) {
			endSkeleton();
		}

		String parentId = createId(START_SUBDOCUMENT, subDocumentId);
		FilterEvent parentGroup = peekMostRecentGroup();
		if (parentGroup != null) {
			parentId = parentGroup.getResource().getId();
		}

		String gid = createId(START_GROUP, ++startGroupId);
		StartGroup g = new StartGroup(parentId, gid);
		addPropertiesToResource(g, properties);
		GenericSkeleton skel = new GenericSkeleton((GenericSkeleton) startMarker);

		FilterEvent fe = new FilterEvent(FilterEventType.START_GROUP, g, skel);

		if (isCurrentComplexTextUnit()) {
			// add this group as a cde of the complex TextUnit
			g.setIsReferent(true);
			Code c = new Code(TagType.PLACEHOLDER, startMarker.toString(), TextFragment.makeRefMarker(gid));
			c.setHasReference(true);
			startCode(c);
			endCode();
			referencableFilterEvents.add(fe);
		} else {
			filterEvents.add(fe);
		}

		tempFilterEventStack.push(fe);
	}

	protected void endGroup(ISkeleton endMarker) {
		GenericSkeleton skel = new GenericSkeleton((GenericSkeleton) endMarker);

		if (!isCurrentGroup()) {
			throw new BaseFilterException("Start group not found. Cannot end group");
		}

		popTempEvent();

		Ending eg = new Ending(createId(END_GROUP, ++endGroupId));

		filterEvents.add(new FilterEvent(FilterEventType.END_GROUP, eg, skel));
	}

	// ////////////////////////////////////////////////////////////////////////
	// Standalone Property Methods (not attached to any other resource)
	// ////////////////////////////////////////////////////////////////////////

	protected void startProperty(Property property, String language) {
		if (hasUnfinishedSkeleton()) {
			endSkeleton();
		}

		DocumentPart dp = new DocumentPart(createId(DOCUMENT_PART, ++documentPartId), false);
		dp.setTargetProperty(language, property);
		filterEvents.add(new FilterEvent(FilterEventType.DOCUMENT_PART, dp));
	}

	protected void startProperties(List<Property> properties, String language) {
		for (Property property : properties) {
			startProperty(property, language);
		}
	}

	protected void startProperty(Property property) {
		if (hasUnfinishedSkeleton()) {
			endSkeleton();
		}

		DocumentPart dp = new DocumentPart(createId(DOCUMENT_PART, ++documentPartId), false);
		dp.setProperty(property);

	}

	protected void startProperties(List<Property> properties) {
		for (Property property : properties) {
			startProperty(property);
		}
	}

	protected void endProperty() {
		filterEvents.add(new FilterEvent(FilterEventType.DOCUMENT_PART, currentDocumentPartForStandaloneProperties));
		currentDocumentPartForStandaloneProperties = null;
	}

	// ////////////////////////////////////////////////////////////////////////
	// Code Methods
	// ////////////////////////////////////////////////////////////////////////

	protected void startCode(Code code) {
		if (!isCurrentTextUnit()) {
			throw new BaseFilterException("TextUnit not found. Cannot add a Code to a non-exisitant TextUnit.");
		}
		currentCode = code;
	}

	protected void endCode() {
		if (currentCode == null) {
			throw new BaseFilterException("Code not found. Cannot end a non-exisitant code.");
		}

		TextUnit tu = (TextUnit) peekMostRecentTextUnit().getResource();
		tu.getSource().append(currentCode);
		currentCode = null;
	}

	protected void addToCode(String data) {
		if (currentCode == null) {
			throw new BaseFilterException("Code not found. Cannot add a Property to a non-exisitant code.");
		}

		currentCode.append(data);
	}

	protected void addToCode(Property property) {
		if (currentCode == null) {
			throw new BaseFilterException("Code not found. Cannot add a Property to a non-exisitant code.");
		}

		TextUnit tu = (TextUnit) peekMostRecentTextUnit().getResource();
		tu.setIsReferent(true);
		String refMarker = TextFragment.makeRefMarker(tu.getId());
		currentCode.append(refMarker);
		currentCode.setHasReference(true);

		DocumentPart dp = new DocumentPart(createId(DOCUMENT_PART, ++documentPartId), true);
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
		filterEvents.add(new FilterEvent(FilterEventType.DOCUMENT_PART, new DocumentPart(createId(DOCUMENT_PART,
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
