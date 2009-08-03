/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

import java.security.acl.Group;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.exceptions.OkapiIllegalFilterOperationException;
import net.sf.okapi.common.filters.PropertyTextUnitPlaceholder.PlaceholderType;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;

/**
 * BaseFilter provides a simplified API for filter writers and hides the low
 * level resource API.
 * <p>
 * The BaseFilter allows filter writers to think in terms of simple start and
 * end calls. For example, to produce a non-translatable {@link Event} you would
 * use startDocumentPart() and endDocumentPart(). For a text-based {@link Event}
 * you would use startTextUnit() and endTextUnit().
 * <p>
 * More complex cases such as tags with embedded translatable text can also be
 * handled. See the BaseMarkupFilter, HtmlFilter and OpenXmlFilter for examples
 * of using the AbstractBaseFilter.
 * <p>
 * To create a new filter extend BaseFilter and call startFilter() and
 * endFilter() methods at the beginning and end of each filter run.
 */
public class EventBuilder {
	private static final Logger LOGGER = Logger.getLogger(EventBuilder.class.getName());

	private static final String START_GROUP = "sg"; //$NON-NLS-1$
	private static final String END_GROUP = "eg"; //$NON-NLS-1$
	private static final String TEXT_UNIT = "tu"; //$NON-NLS-1$
	private static final String DOCUMENT_PART = "dp"; //$NON-NLS-1$
	private static final String START_SUBDOCUMENT = "ssd"; //$NON-NLS-1$
	private static final String END_SUBDOCUMENT = "esd"; //$NON-NLS-1$

	private String mimeType;

	private int startGroupId = 0;
	private int endGroupId = 0;
	private int textUnitId = 0;
	private int subDocumentId = 0;
	private int documentPartId = 0;

	private Stack<Event> tempFilterEventStack;

	private List<Event> filterEvents;
	private List<Event> referencableFilterEvents;

	private boolean done = false;
	private boolean preserveWhitespace;

	private GenericSkeleton currentSkeleton;
	private Code currentCode;
	private DocumentPart currentDocumentPart;

	/**
	 * Instantiates a new EventBuilder.
	 */
	public EventBuilder() {
	}

	public boolean hasNext() {
		return !done;
	}

	public Event next() {
		Event event;

		if (hasNext()) {
			if (!referencableFilterEvents.isEmpty()) {
				return referencableFilterEvents.remove(0);
			} else if (!filterEvents.isEmpty()) {
				event = filterEvents.remove(0);
				if (event.getEventType() == EventType.END_DOCUMENT)
					done = true;
				return event;
			}
		}

		return null;
	}

	public void addFilterEvent(Event event) {
		filterEvents.add(event);
	}

	public void cancel() {
		// flush out all pending events
		filterEvents.clear();
		referencableFilterEvents.clear();

		Event event = new Event(EventType.CANCELED);
		filterEvents.add(event);
	}

	/*
	 * Create a formatted ID for named resources.
	 */
	private String createId(String name, int number) {
		return String.format("%s%d", name, number); //$NON-NLS-1$
	}

	/*
	 * Return the current buffered Event without removing it.
	 */
	private Event peekTempEvent() {
		if (tempFilterEventStack.isEmpty()) {
			return null;
		}
		return tempFilterEventStack.peek();
	}

	/*
	 * Return the current buffered Event and removes it from the buffer.
	 */
	private Event popTempEvent() {
		if (tempFilterEventStack.isEmpty()) {
			return null;
		}
		return tempFilterEventStack.pop();
	}

	/**
	 * Flush all remaining events.
	 */
	public void flushRemainingEvents() {
		if (hasUnfinishedSkeleton()) {
			endDocumentPart();
		} else if (!tempFilterEventStack.isEmpty()) {
			// go through filtered object stack and close them one by one
			while (!tempFilterEventStack.isEmpty()) {
				Event fe = tempFilterEventStack.peek();
				if (fe.getEventType() == EventType.START_GROUP) {
					StartGroup sg = (StartGroup) fe.getResource();
					endGroup((GenericSkeleton) sg.getSkeleton());
				} else if (fe.getEventType() == EventType.TEXT_UNIT) {
					endTextUnit();
				}
			}
		}
	}

	/**
	 * Check if the current buffered {@link Event} is a {@link TextUnit}.
	 * 
	 * @return true if TextUnit, false otherwise.
	 */
	public boolean isCurrentTextUnit() {
		Event e = peekTempEvent();
		if (e != null && e.getEventType() == EventType.TEXT_UNIT) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the current buffered {@link Event} is a complex {@link TextUnit}
	 * A complex TextUnit is one which carries along with it it's surrounding
	 * context such &lt;p> text &lt;/p> or &lt;title> text &lt;/title>
	 * 
	 * 
	 * @return true, if complex text unit, false otherwise.
	 */
	public boolean isCurrentComplexTextUnit() {
		Event e = peekTempEvent();
		if (e != null && e.getEventType() == EventType.TEXT_UNIT && e.getResource().getSkeleton() != null) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the current buffered {@link Event} is a {@link Group}
	 * 
	 * @return true, if is current group
	 */
	public boolean isCurrentGroup() {
		Event e = peekTempEvent();
		if (e != null && e.getEventType() == EventType.START_GROUP) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the filter is inside text run.
	 * 
	 * @return true, if is inside text run
	 */
	public boolean isInsideTextRun() {
		return isCurrentTextUnit();
	}

	/**
	 * Can we start new {@link TextUnit}? A new {@link TextUnit} can only be
	 * started if the current one has been ended with endTextUnit. Or no
	 * {@link TextUnit} has been created yet.
	 * 
	 * @return true, if successful
	 */
	public boolean canStartNewTextUnit() {
		if (isCurrentTextUnit()) {
			return false;
		}
		return true;
	}

	/**
	 * Checks for queued events. We queue events in the correct order as
	 * expected by the Okapi Writers (IWriter).
	 * 
	 * @return true, if successful
	 */
	public boolean hasQueuedEvents() {
		if (filterEvents.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Peek at the most recently created {@link Group}.
	 * 
	 * @return the filter event
	 */
	public Event peekMostRecentGroup() {
		if (tempFilterEventStack.isEmpty()) {
			return null;
		}
		for (Event fe : tempFilterEventStack) {
			if (fe.getEventType() == EventType.START_GROUP) {
				return fe;
			}
		}
		return null;
	}

	/**
	 * Peek At the most recently created {@link TextUnit}.
	 * 
	 * @return the filter event
	 */
	public Event peekMostRecentTextUnit() {
		if (tempFilterEventStack.isEmpty()) {
			return null;
		}
		for (Event fe : tempFilterEventStack) {
			if (fe.getEventType() == EventType.TEXT_UNIT) {
				return fe;
			}
		}
		return null;
	}

	/**
	 * Checks for unfinished skeleton aka {@link DocumentPart}
	 * 
	 * @return true, if successful
	 */
	public boolean hasUnfinishedSkeleton() {
		if (currentSkeleton == null) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if the current {@link TextUnit} has a parent.
	 * 
	 * @return true, if successful
	 */
	public boolean hasParentTextUnit() {
		if (tempFilterEventStack.isEmpty()) {
			return false;
		}
		boolean first = true;
		// skip current TextUnit - the one we are currently processing
		for (Event fe : tempFilterEventStack) {
			if (fe.getEventType() == EventType.TEXT_UNIT && !first) {
				return true;
			}
			first = false;
		}
		return false;
	}

	/*
	 * Reset {@link IFilter} for new input.
	 */
	public void reset() {
		startGroupId = 0;
		endGroupId = 0;
		textUnitId = 0;
		documentPartId = 0;
		subDocumentId = 0;

		done = false;
		preserveWhitespace = true;

		referencableFilterEvents = new LinkedList<Event>();
		filterEvents = new LinkedList<Event>();

		tempFilterEventStack = new Stack<Event>();

		currentCode = null;
		currentSkeleton = null;
		currentDocumentPart = null;
	}

	// ////////////////////////////////////////////////////////////////////////
	// Start and Finish Methods
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Send the {@link EventType} START_SUBDOCUMENT {@link Event}
	 */
	public void startSubDocument() {
		if (hasUnfinishedSkeleton()) {
			endDocumentPart();
		}

		StartSubDocument startSubDocument = new StartSubDocument(createId(START_SUBDOCUMENT, ++subDocumentId));
		Event event = new Event(EventType.START_SUBDOCUMENT, startSubDocument);
		filterEvents.add(event);
		LOGGER.log(Level.FINE, "Start Sub-Document for " + startSubDocument.getId());
	}

	/**
	 * Send the {@link EventType} END_SUBDOCUMENT {@link Event}
	 */
	public void endSubDocument() {
		Ending endDocument = new Ending(createId(END_SUBDOCUMENT, ++subDocumentId));
		Event event = new Event(EventType.END_SUBDOCUMENT, endDocument);
		filterEvents.add(event);
		LOGGER.log(Level.FINE, "End Sub-Document for " + endDocument.getId());
	}

	// ////////////////////////////////////////////////////////////////////////
	// Private methods used for processing properties and text embedded within
	// tags
	// ////////////////////////////////////////////////////////////////////////

	private TextUnit embeddedTextUnit(PropertyTextUnitPlaceholder propOrText, String tag) {
		TextUnit tu = new TextUnit(createId(TEXT_UNIT, ++textUnitId), propOrText.getValue());
		tu.setPreserveWhitespaces(this.preserveWhitespace);

		tu.setMimeType(propOrText.getMimeType());
		tu.setIsReferent(true);

		GenericSkeleton skel = new GenericSkeleton();

		skel.add(tag.substring(propOrText.getMainStartPos(), propOrText.getValueStartPos()));
		skel.addContentPlaceholder(tu);
		skel.add(tag.substring(propOrText.getValueEndPos(), propOrText.getMainEndPos()));
		tu.setSkeleton(skel);

		return tu;
	}

	private void embeddedWritableProp(INameable resource, PropertyTextUnitPlaceholder propOrText, String tag,
			String language) {
		setPropertyBasedOnLanguage(resource, language, new Property(propOrText.getName(), propOrText.getValue(), false));
		currentSkeleton.add(tag.substring(propOrText.getMainStartPos(), propOrText.getValueStartPos()));
		currentSkeleton.addValuePlaceholder(resource, propOrText.getName(), language);
		currentSkeleton.add(tag.substring(propOrText.getValueEndPos(), propOrText.getMainEndPos()));
	}

	private void embeddedReadonlyProp(INameable resource, PropertyTextUnitPlaceholder propOrText, String tag,
			String language) {
		setPropertyBasedOnLanguage(resource, language, new Property(propOrText.getName(), propOrText.getValue(), true));
		currentSkeleton.add(tag.substring(propOrText.getMainStartPos(), propOrText.getMainEndPos()));
	}

	private INameable setPropertyBasedOnLanguage(INameable resource, String language, Property property) {
		if (language == null) {
			resource.setSourceProperty(property);
		} else if (language.equals("")) {
			resource.setProperty(property);
		} else {
			resource.setTargetProperty(language, property);
		}

		return resource;
	}

	private boolean processAllEmbedded(String tag, String language,
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders, boolean inlineCode) {
		return processAllEmbedded(tag, language, propertyTextUnitPlaceholders, inlineCode, null);
	}

	private boolean isTextPlaceHoldersOnly(List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders) {
		boolean text = false;
		boolean nontext = false;
		for (PropertyTextUnitPlaceholder propOrText : propertyTextUnitPlaceholders) {
			if (propOrText.getType() == PlaceholderType.TRANSLATABLE) {
				text = true;
			} else {
				nontext = true;
			}
		}

		return (text && !nontext);

	}

	private boolean processAllEmbedded(String tag, String language,
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders, boolean inlineCode, TextUnit parentTu) {

		int propOrTextId = -1;
		boolean textPlaceholdersOnly = isTextPlaceHoldersOnly(propertyTextUnitPlaceholders);
		INameable resource = null;

		// we need to clear out the current Code data as we will append the new
		// skeleton below
		if (currentCode != null) {
			currentCode.setData("");
		}

		// set the resource that will hold all the references
		if (inlineCode) {
			if (textPlaceholdersOnly) {
				resource = parentTu;
			} else {
				resource = new DocumentPart(createId(DOCUMENT_PART, ++documentPartId), inlineCode);
			}
		} else {
			if (parentTu != null) {
				resource = parentTu;
			} else {
				resource = currentDocumentPart;
			}
		}

		// sort to make sure we do the Properties or Text in order
		Collections.sort(propertyTextUnitPlaceholders);

		// add the part up to the first prop or text
		PropertyTextUnitPlaceholder pt = propertyTextUnitPlaceholders.get(0);
		currentSkeleton.add(tag.substring(0, pt.getMainStartPos()));

		for (PropertyTextUnitPlaceholder propOrText : propertyTextUnitPlaceholders) {
			propOrTextId++;

			// add the markup between the props or text
			if (propOrTextId >= 1 && propOrTextId < propertyTextUnitPlaceholders.size()) {
				PropertyTextUnitPlaceholder pt1 = propertyTextUnitPlaceholders.get(propOrTextId - 1);
				PropertyTextUnitPlaceholder pt2 = propertyTextUnitPlaceholders.get(propOrTextId);
				currentSkeleton.add(tag.substring(pt1.getMainEndPos(), pt2.getMainStartPos()));
			}

			if (propOrText.getType() == PlaceholderType.TRANSLATABLE) {
				TextUnit tu = embeddedTextUnit(propOrText, tag);
				currentSkeleton.addReference(tu);
				referencableFilterEvents.add(new Event(EventType.TEXT_UNIT, tu));
			} else if (propOrText.getType() == PlaceholderType.WRITABLE_PROPERTY) {
				embeddedWritableProp(resource, propOrText, tag, language);
			} else if (propOrText.getType() == PlaceholderType.READ_ONLY_PROPERTY) {
				embeddedReadonlyProp(resource, propOrText, tag, language);
			} else {
				throw new OkapiIllegalFilterOperationException("Unkown Property or TextUnit type");
			}
		}

		// add the remaining markup after the last prop or text
		pt = propertyTextUnitPlaceholders.get(propertyTextUnitPlaceholders.size() - 1);
		currentSkeleton.add(tag.substring(pt.getMainEndPos()));

		// setup references based on type
		if (inlineCode) {
			if (!textPlaceholdersOnly) {
				currentCode.appendReference(resource.getId());
				resource.setSkeleton(currentSkeleton);
				// we needed to create a document part to hold the
				// writable/localizables
				referencableFilterEvents.add(new Event(EventType.DOCUMENT_PART, resource));
			} else {
				// all text - the parent TU hold the references instead of a
				// DocumentPart
				currentCode.append(currentSkeleton.toString());
				currentCode.setReferenceFlag(true);
			}
		}

		return textPlaceholdersOnly;
	}

	// ////////////////////////////////////////////////////////////////////////
	// TextUnit Methods
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Start a {@link TextUnit}. Also create a TextUnit {@link Event} and queue
	 * it.
	 * 
	 * @param text
	 *            the text used to prime the {@link TextUnit}
	 */
	public void startTextUnit(String text) {
		startTextUnit(text, null, null, null);
	}

	/**
	 * Start text unit.
	 */
	public void startTextUnit() {
		startTextUnit(null, null, null, null);
	}

	/**
	 * Start a complex {@link TextUnit}. Also create a TextUnit {@link Event}
	 * and queue it.
	 * 
	 * @param startMarker
	 *            the tag that begins the complex {@link TextUnit}
	 */
	public void startTextUnit(GenericSkeleton startMarker) {
		startTextUnit(null, startMarker, null, null);
	}

	/**
	 * Start a complex {@link TextUnit} with actionable (translatable, writable
	 * or read-only) attributes. Also create a TextUnit {@link Event} and queue
	 * it.
	 * 
	 * @param startMarker
	 *            the tag that begins the complex {@link TextUnit}
	 * @param propertyTextUnitPlaceholders
	 *            the list of actionable {@link TextUnit} or {@link Properties}
	 *            with offset information into the tag.
	 */
	public void startTextUnit(GenericSkeleton startMarker,
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders) {
		startTextUnit(null, startMarker, null, propertyTextUnitPlaceholders);
	}

	/**
	 * Start a complex {@link TextUnit} with actionable (translatable, writable
	 * or read-only) attributes. Also create a TextUnit {@link Event} and queue
	 * it.
	 * 
	 * @param startMarker
	 *            the tag that begins the complex {@link TextUnit}
	 * @param propertyTextUnitPlaceholders
	 *            the list of actionable {@link TextUnit} or {@link Properties}
	 *            with offset information into the tag.
	 * @param text
	 *            the text used to prime the {@link TextUnit}
	 */
	public void startTextUnit(String text, GenericSkeleton startMarker,
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders) {
		startTextUnit(text, startMarker, null, propertyTextUnitPlaceholders);
	}

	/**
	 * Start a complex {@link TextUnit} with actionable (translatable, writable
	 * or read-only) attributes. Also create a TextUnit {@link Event} and queue
	 * it.
	 * 
	 * @param startMarker
	 *            the tag that begins the complex {@link TextUnit}
	 * @param propertyTextUnitPlaceholders
	 *            the list of actionable {@link TextUnit} or {@link Properties}
	 *            with offset information into the tag.
	 * @param text
	 *            the text used to prime the {@link TextUnit}
	 * @param language
	 *            the language of the text
	 */
	public void startTextUnit(String text, GenericSkeleton startMarker, String language,
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders) {

		if (hasUnfinishedSkeleton()) {
			endDocumentPart();
		}

		TextUnit tu = new TextUnit(createId(TEXT_UNIT, ++textUnitId), text);
		tu.setMimeType(this.mimeType);
		tu.setPreserveWhitespaces(this.preserveWhitespace);

		if (startMarker != null && propertyTextUnitPlaceholders != null) {
			currentSkeleton = new GenericSkeleton();
			processAllEmbedded(startMarker.toString(), language, propertyTextUnitPlaceholders, false, tu);
			tu.setSkeleton(currentSkeleton);
			currentSkeleton.addContentPlaceholder(tu);
			tempFilterEventStack.push(new Event(EventType.TEXT_UNIT, tu, currentSkeleton));
			currentSkeleton = null;
			return;
		} else if (startMarker != null) {
			GenericSkeleton skel = new GenericSkeleton((GenericSkeleton) startMarker);
			skel.addContentPlaceholder(tu);
			tempFilterEventStack.push(new Event(EventType.TEXT_UNIT, tu, skel));
			return;
		} else {
			tempFilterEventStack.push(new Event(EventType.TEXT_UNIT, tu));
		}
	}

	/**
	 * End the current {@link TextUnit} and place the {@link Event} on the
	 * finished queue.
	 */
	public TextUnit endTextUnit() {
		return endTextUnit(null, null, null);
	}

	/**
	 * End the current {@link TextUnit} and place the {@link Event} on the
	 * finished queue.
	 * 
	 * @param endMarker
	 *            the tag that ends the complex {@link TextUnit}
	 */
	public TextUnit endTextUnit(GenericSkeleton endMarker) {
		return endTextUnit(endMarker, null, null);
	}

	/**
	 * End the current {@link TextUnit} and place the {@link Event} on the
	 * finished queue.
	 * 
	 * @param endMarker
	 *            the tag that ends the complex {@link TextUnit}
	 * @param propertyTextUnitPlaceholders
	 *            the list of actionable {@link TextUnit} or {@link Properties}
	 *            with offset information into the tag.
	 * @param language
	 *            the language of the text
	 * 
	 * @throws OkapiIllegalFilterOperationException
	 */
	public TextUnit endTextUnit(GenericSkeleton endMarker, String language,
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders) {
		Event tempTextUnit;
		// String sourceString; // for testing to see if there is embedded text

		if (!isCurrentTextUnit()) {
			throw new OkapiIllegalFilterOperationException("Trying to end a TextUnit that does not exist.");
		}

		/*
		 * The ability to add internal placeholders to end tags is not currently
		 * supported if (endMarker != null && propertyTextUnitPlaceholders !=
		 * null) { processAllEmbedded(endMarker.toString(), language,
		 * propertyTextUnitPlaceholders, false); }
		 */
		tempTextUnit = popTempEvent();

		if (endMarker != null) {
			GenericSkeleton skel = (GenericSkeleton) tempTextUnit.getResource().getSkeleton();
			skel.add((GenericSkeleton) endMarker);
		}

		tempTextUnit.setResource(postProcessTextUnit((TextUnit) tempTextUnit.getResource()));
		
		filterEvents.add(tempTextUnit);

		return (TextUnit) tempTextUnit.getResource();
	}

	/**
	 * Adds text to the current {@link TextUnit}
	 * 
	 * @param text
	 *            the text
	 * 
	 * @throws OkapiIllegalFilterOperationException
	 */
	public void addToTextUnit(String text) {
		if (!isCurrentTextUnit()) {
			throw new OkapiIllegalFilterOperationException("Trying to add text to a TextUnit that does not exist.");
		}

		Event tempTextUnit = peekTempEvent();
		TextUnit tu = (TextUnit) tempTextUnit.getResource();
		tu.getSource().append(text);
	}

	/**
	 * Add a {@link Code} to the current {@link TextUnit}. Nothing is actionable
	 * within the tag (i.e., no properties or text)
	 * 
	 * @param code
	 *            the code type
	 * 
	 * @throws OkapiIllegalFilterOperationException
	 */
	public void addToTextUnit(Code code, String commonType) {
		if (!isCurrentTextUnit()) {
			throw new OkapiIllegalFilterOperationException("Trying to add a Code to a TextUnit that does not exist.");
		}
		startCode(code, commonType);
		endCode();
	}

	/**
	 * Add a {@link Code} to the current {@link TextUnit}. The Code contains
	 * actionable attributes.
	 * 
	 * @param code
	 *            the code
	 * @param propertyTextUnitPlaceholders
	 *            the list of actionable {@link TextUnit} or {@link Properties}
	 *            with offset information into the tag.
	 */
	public void addToTextUnit(Code code, String commonType,
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders) {
		addToTextUnit(code, commonType, null, propertyTextUnitPlaceholders);
	}

	/**
	 * Add a {@link Code} to the current {@link TextUnit}. The Code contains
	 * actionable attributes.
	 * 
	 * @param code
	 *            the code
	 * @param propertyTextUnitPlaceholders
	 *            the list of actionable {@link TextUnit} or {@link Properties}
	 *            with offset information into the tag.
	 * @param language
	 *            the language of the text
	 * @throws OkapiIllegalFilterOperationException
	 */
	public void addToTextUnit(Code code, String commonType,
			String language,
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders) {

		if (!isCurrentTextUnit()) {
			throw new OkapiIllegalFilterOperationException("Trying to add Codes to a TextUnit that does not exist.");
		}

		currentSkeleton = new GenericSkeleton();
		TextUnit tu = (TextUnit) peekMostRecentTextUnit().getResource();
		startCode(code, commonType);
		processAllEmbedded(code.toString(), language, propertyTextUnitPlaceholders, true, tu);
		endCode();

		currentSkeleton = null;
	}

	/**
	 * Appends text to the first data part of the skeleton {@link TextUnit}
	 * 
	 * @param text
	 *            the text
	 * 
	 * @throws OkapiIllegalFilterOperationException
	 */
	public void appendToFirstSkeletonPart(String text) { // DWH 5-2-09
		Event tempTextUnit = peekTempEvent();
		GenericSkeleton skel = (GenericSkeleton) tempTextUnit.getResource().getSkeleton();
		skel.appendToFirstPart(text);
	}

	// ////////////////////////////////////////////////////////////////////////
	// Group Methods
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Create and send a {@link StartGroup} {@link Event}
	 * 
	 * @param startMarker
	 *            the tag which starts the {@link Group}
	 */
	public void startGroup(GenericSkeleton startMarker, String commonTagType) {
		startGroup(startMarker, commonTagType, null, null);
	}

	/**
	 * Create and send a {@link StartGroup} {@link Event}
	 * 
	 * @param startMarker
	 *            the tag which starts the {@link Group}
	 * @param propertyTextUnitPlaceholders
	 *            the list of actionable {@link TextUnit} or {@link Properties}
	 *            with offset information into the tag.
	 * @param language
	 *            the language of any actionable items
	 */
	public void startGroup(GenericSkeleton startMarker, String commonTagType, String language,
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders) {
		if (startMarker == null) {
			throw new OkapiIllegalFilterOperationException(
					"startMarker for Group is null");
		}

		if (hasUnfinishedSkeleton()) {
			endDocumentPart();
		}

		if (startMarker != null && propertyTextUnitPlaceholders != null) {
			processAllEmbedded(startMarker.toString(), language, propertyTextUnitPlaceholders, false);
		}

		String parentId = createId(START_SUBDOCUMENT, subDocumentId);
		Event parentGroup = peekMostRecentGroup();
		if (parentGroup != null) {
			parentId = parentGroup.getResource().getId();
		}

		String gid = createId(START_GROUP, ++startGroupId);
		StartGroup g = new StartGroup(parentId, gid);

		GenericSkeleton skel = new GenericSkeleton((GenericSkeleton) startMarker);

		Event fe = new Event(EventType.START_GROUP, g, skel);

		if (isCurrentComplexTextUnit()) {
			// add this group as a code of the complex TextUnit
			g.setIsReferent(true);
			@SuppressWarnings("null")
			Code c = new Code(TagType.PLACEHOLDER, startMarker.toString(), TextFragment.makeRefMarker(gid));
			c.setReferenceFlag(true);
			startCode(c, commonTagType);
			endCode();
			referencableFilterEvents.add(fe);
		} else {
			filterEvents.add(fe);
		}

		tempFilterEventStack.push(fe);
	}

	/**
	 * Send an {@link Ending} {@link Event} of type END_GROUP
	 * 
	 * @param endMarker
	 *            the tags that ends the {@link Group}
	 */
	public void endGroup(GenericSkeleton endMarker) {
		endGroup(endMarker, null, null);
	}

	/**
	 * Send an {@link Ending} {@link Event} of type END_GROUP
	 * 
	 * @param endMarker
	 *            the tags that ends the {@link Group}
	 * @param propertyTextUnitPlaceholders
	 *            the list of actionable {@link TextUnit} or {@link Properties}
	 *            with offset information into the tag.
	 * @param language
	 *            the language of any actionable items
	 * 
	 * @throws OkapiIllegalFilterOperationException
	 */
	public void endGroup(GenericSkeleton endMarker, String language,
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders) {

		if (!isCurrentGroup()) {
			throw new OkapiIllegalFilterOperationException(
					"Trying end a Group that does not exist. Can be cuased by unbalanced Group tags.");
		}

		GenericSkeleton skel = new GenericSkeleton((GenericSkeleton) endMarker);

		if (endMarker != null && propertyTextUnitPlaceholders != null) {
			processAllEmbedded(endMarker.toString(), language, propertyTextUnitPlaceholders, false);
		}

		popTempEvent();

		Ending eg = new Ending(createId(END_GROUP, ++endGroupId));

		filterEvents.add(new Event(EventType.END_GROUP, eg, skel));
	}

	// ////////////////////////////////////////////////////////////////////////
	// Code Methods
	// ////////////////////////////////////////////////////////////////////////

	/*
	 * Create a Code and store it for later processing.
	 */
	private void startCode(Code code, String tagType) {
		currentCode = code;
		currentCode.setType(tagType);
	}

	/*
	 * End the COde and add it to the TextUnit.
	 */
	private void endCode() {
		if (currentCode == null) {
			throw new OkapiIllegalFilterOperationException(
					"Trying to end a Code that does not exist. Did you call startCode?");
		}

		TextUnit tu = (TextUnit) peekMostRecentTextUnit().getResource();
		tu.getSourceContent().append(currentCode);
		currentCode = null;
	}

	// ////////////////////////////////////////////////////////////////////////
	// DocumentPart Methods
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * Start a {@link DocumentPart} and create an {@link Event}. Store the
	 * {@link Event} for later processing.
	 * 
	 * @param part
	 *            the {@link DocumentPart} (aka skeleton)
	 */
	public void startDocumentPart(String part) {

		if (hasUnfinishedSkeleton()) {
			endDocumentPart();
		}
		currentSkeleton = new GenericSkeleton(part);
		currentDocumentPart = new DocumentPart(createId(DOCUMENT_PART, ++documentPartId), false);
		currentDocumentPart.setSkeleton(currentSkeleton);
	}

	/**
	 * Start a {@link DocumentPart} and create an {@link Event}. Store the
	 * {@link Event} for later processing.
	 * 
	 * @param part
	 *            the {@link DocumentPart} (aka skeleton)
	 * @param name
	 *            the name
	 * @param propertyTextUnitPlaceholders
	 *            the list of actionable {@link TextUnit} or {@link Properties}
	 *            with offset information into the tag.
	 */
	public void startDocumentPart(String part, String name,
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders) {
		startDocumentPart(part, name, null, propertyTextUnitPlaceholders);
	}

	/**
	 * Start a {@link DocumentPart} and create an {@link Event}. Store the
	 * {@link Event} for later processing.
	 * 
	 * @param part
	 *            the {@link DocumentPart} (aka skeleton)
	 * @param name
	 *            the name
	 * @param propertyTextUnitPlaceholders
	 *            the list of actionable {@link TextUnit} or {@link Properties}
	 *            with offset information into the tag.
	 * @param language
	 *            the language of any actionable items
	 */
	public void startDocumentPart(String part, String name, String language,
			List<PropertyTextUnitPlaceholder> propertyTextUnitPlaceholders) {

		if (hasUnfinishedSkeleton()) {
			endDocumentPart();
		}

		currentSkeleton = new GenericSkeleton();
		currentDocumentPart = new DocumentPart(createId(DOCUMENT_PART, ++documentPartId), false);
		currentDocumentPart.setSkeleton(currentSkeleton);

		processAllEmbedded(part, language, propertyTextUnitPlaceholders, false);
	}

	/**
	 * End the {@link DocumentPart} and finalize the {@link Event}. Place the
	 * {@link Event} on the finished queue.
	 * 
	 * @param part
	 *            the {@link DocumentPart} (aka skeleton)
	 */
	public void endDocumentPart(String part) {
		if (part != null) {
			currentSkeleton.append(part);
		}
		filterEvents.add(new Event(EventType.DOCUMENT_PART, currentDocumentPart));
		currentSkeleton = null;
		currentDocumentPart = null;
	}

	/**
	 * End the {@link DocumentPart} and finalize the {@link Event}. Place the
	 * {@link Event} on the finished queue.
	 */
	public void endDocumentPart() {
		endDocumentPart(null);
	}

	/**
	 * Add to the current {@link DocumentPart}.
	 * 
	 * @param part
	 *            the {@link DocumentPart} as a String.
	 */
	public void addToDocumentPart(String part) {
		if (currentSkeleton == null) {
			startDocumentPart(part);
			return;
		}
		currentSkeleton.append(part);
	}

	/**
	 * Sets the input document mime type.
	 * 
	 * @param mimeType
	 *            the new mime type
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Tell the {@link IFilter} what to do with whitespace.
	 * 
	 * @param preserveWhitespace
	 *            the preserveWhitespace as boolean.
	 */
	public void setPreserveWhitespace(boolean preserveWhitespace) {
		this.preserveWhitespace = preserveWhitespace;
	}

	/**
	 * Gets the textUnitId (used in OpenXML with textboxes to update the id of
	 * the parent filter
	 * 
	 * @return the textUnitId
	 */
	public int getTextUnitId() {
		return textUnitId;
	}

	/**
	 * Allows implementers to set the textUnitId
	 * 
	 * @param id
	 *            the initial value for the textUnitId
	 */
	public void setTextUnitId(int id) {
		this.textUnitId = id;
	}

	/**
	 * Gets the textUnitId
	 * 
	 * @return the textUnitId
	 */
	public int getDocumentPartId() {
		return documentPartId;
	}

	/**
	 * Allows implementers to set the textUnitId
	 * 
	 * @param id
	 *            the initial value for the textUnitId
	 */
	public void setDocumentPartId(int id) {
		this.documentPartId = id;
	}
	
	/**
	 * Do any required post-processing on the TextUnit after endTextUnit is called. 
	 * Default implementation leaves TextUnit unchanged. Override this method if 
	 * you need to do format specific handing such as collapsing whitespace.
	 */
	protected TextUnit postProcessTextUnit(TextUnit textUnit) {		
		return textUnit;
	}
}