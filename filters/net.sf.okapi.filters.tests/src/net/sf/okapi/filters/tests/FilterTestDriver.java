/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.tests;

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;

/**
 * Driver to test filter output.
 */
public class FilterTestDriver {

	private boolean showSkeleton = true;
	private int displayLevel = 0;
	private int warnings;
	private boolean ok;

	static public boolean compareEvents(ArrayList<Event> manual, ArrayList<Event> generated) {
		if (manual.size() != generated.size()) {
			return false;
		}

		Iterator<Event> manualIt = manual.iterator();
		for (Event ge : generated) {
			Event me = manualIt.next();
			if (ge.getEventType() != me.getEventType()) {
				return false;
			}
			IResource mr = me.getResource();
			IResource gr = ge.getResource();
			if (mr != null && gr != null && mr.getSkeleton() != null && gr.getSkeleton() != null) {
				if (!(mr.getSkeleton().toString().equals(gr.getSkeleton().toString()))) {
					return false;
				}
			}

			switch (ge.getEventType()) {
			case DOCUMENT_PART:
				DocumentPart mdp = (DocumentPart) mr;
				DocumentPart gdp = (DocumentPart) gr;
				if (mdp.isReferent() != gdp.isReferent()) {
					return false;
				}
				if (mdp.isTranslatable() != gdp.isTranslatable()) {
					return false;
				}
				if (!(mdp.getSourcePropertyNames().equals(gdp.getSourcePropertyNames()))) {
					return false;
				}

				for (String propName : gdp.getSourcePropertyNames()) {
					Property gdpProp = gdp.getSourceProperty(propName);
					Property mdpProp = mdp.getSourceProperty(propName);
					if (gdpProp.isReadOnly() != mdpProp.isReadOnly()) {
						return false;
					}
				}
				break;
			case TEXT_UNIT:
				TextUnit mtu = (TextUnit) mr;
				TextUnit gtu = (TextUnit) gr;
				
				if (mtu.preserveWhitespaces() != gtu.preserveWhitespaces()) {
					return false;
				}
								
				if (!(mtu.toString().equals(gtu.toString()))) {
					return false;
				}

				if (mtu.getSource().getCodes().size() != gtu.getSource().getCodes().size()) {
					return false;
				}

				int i = -1;
				for (Code c : mtu.getSource().getCodes()) {
					i++;
					if (c.getType() != null) {
						if (!c.getType().equals(gtu.getSource().getCode(i).getType())) {
							return false;
						}
					}
				}

				break;
			}
		}
		return true;
	}

	/**
	 * Indicates to this driver to display the skeleton data.
	 * 
	 * @param value
	 *            True to display the skeleton, false to not display the
	 *            skeleton.
	 */
	public void setShowSkeleton(boolean value) {
		showSkeleton = value;
	}

	/**
	 * Indicates what to display.
	 * 
	 * @param value
	 *            0=display nothing, 1=display TU only, >1=display all.
	 */
	public void setDisplayLevel (int value) {
		displayLevel = value;

	}

	/**
	 * Process the input document. You must have called the setOptions() and
	 * open() methods of the filter before calling this method.
	 * 
	 * @param filter
	 *            Filter to process.
	 * @return False if an error occurred, true if all was OK.
	 */
	public boolean process(IFilter filter) {
		ok = true;
		warnings = 0;
		int startDoc = 0;
		int endDoc = 0;
		int startGroup = 0;
		int endGroup = 0;
		int startSubDoc = 0;
		int endSubDoc = 0;

		Event event;
		while (filter.hasNext()) {
			event = filter.next();
			switch (event.getEventType()) {
			case START_DOCUMENT:
				startDoc++;
				checkStartDocument((StartDocument) event.getResource());
				if ( displayLevel < 2 ) break;
				System.out.println("---Start Document");
				printSkeleton(event.getResource());
				break;
			case END_DOCUMENT:
				endDoc++;
				if ( displayLevel < 2 ) break;
				System.out.println("---End Document");
				printSkeleton(event.getResource());
				break;
			case START_SUBDOCUMENT:
				startSubDoc++;
				if ( displayLevel < 2 ) break;
				System.out.println("---Start Sub Document");
				printSkeleton(event.getResource());
				break;
			case END_SUBDOCUMENT:
				endSubDoc++;
				if ( displayLevel < 2 ) break;
				System.out.println("---End Sub Document");
				printSkeleton(event.getResource());
				break;
			case START_GROUP:
				startGroup++;
				if ( displayLevel < 2 ) break;
				System.out.println("---Start Group");
				printSkeleton(event.getResource());
				break;
			case END_GROUP:
				endGroup++;
				if ( displayLevel < 2 ) break;
				System.out.println("---End Group");
				printSkeleton(event.getResource());
				break;
			case TEXT_UNIT:
				TextUnit tu = (TextUnit)event.getResource();
				if ( displayLevel < 1 ) break;
				printTU(tu);
				if ( displayLevel < 2 ) break;
				printResource(tu);
				printSkeleton(tu);
				break;
			case DOCUMENT_PART:
				if ( displayLevel < 2 ) break;
				System.out.println("---Document Part");
				printResource((INameable) event.getResource());
				printSkeleton(event.getResource());
				break;
			}
		}

		if ( startDoc != 1 ) {
			System.err.println(String.format("ERROR: START_DOCUMENT = %d", startDoc));
			ok = false;
		}
		if ( endDoc != 1 ) {
			System.err.println(String.format("ERROR: END_DOCUMENT = %d", endDoc));
			ok = false;
		}
		if ( startSubDoc != endSubDoc ) {
			System.err.println(String.format("ERROR: START_SUBDOCUMENT=%d, END_SUBDOCUMENT=%d", startSubDoc,
				endSubDoc));
			ok = false;
		}
		if ( startGroup != endGroup ) {
			System.out.println(String.format("ERROR: START_GROUP=%d, END_GROUP=%d", startGroup, endGroup));
			ok = false;
		}
		return ok;
	}

	private void printTU (TextUnit tu) {
		System.out.println("---Text Unit");
		System.out.println("S=[" + tu.toString() + "]");
		for (String lang : tu.getTargetLanguages()) {
			System.out.println("T(" + lang + ")=[" + tu.getTarget(lang).toString() + "]");
		}
	}

	private void printResource(INameable res) {
		if (res == null) {
			System.out.println("NULL resource.");
			ok = false;
		}
		System.out.print("  id='" + res.getId() + "'");
		System.out.print(" name='" + res.getName() + "'");
		System.out.print(" type='" + res.getType() + "'");
		System.out.println(" mimeType='" + res.getMimeType() + "'");
	}

	private void printSkeleton(IResource res) {
		if ( !showSkeleton ) return;
		ISkeleton skel = res.getSkeleton();
		if (skel != null) {
			System.out.println("---");
			System.out.println(skel.toString());
			System.out.println("---");
		}
	}

	private void checkStartDocument(StartDocument startDoc) {
		String tmp = startDoc.getEncoding();
		if ((tmp == null) || (tmp.length() == 0)) {
			System.err.println("WARNING: No encoding specified in StartDocument.");
			warnings++;
		} else if ( displayLevel > 1 )
			System.out.println("StartDocument encoding = " + tmp);

		tmp = startDoc.getLanguage();
		if ((tmp == null) || (tmp.length() == 0)) {
			System.err.println("WARNING: No language specified in StartDocument.");
			warnings++;
		} else if ( displayLevel > 1 )
			System.out.println("StartDocument language = " + tmp);

		tmp = startDoc.getName();
		if ((tmp == null) || (tmp.length() == 0)) {
			System.err.println("WARNING: No name specified in StartDocument.");
			warnings++;
		} else if ( displayLevel > 1 )
			System.out.println("StartDocument name = " + tmp);

		if ( displayLevel < 2 ) return;
		System.err.println("StartDocument MIME type = " + startDoc.getMimeType());
		System.err.println("StartDocument Type = " + startDoc.getType());
	}

	/**
	 * create a string output from a list of events.
	 * @param list The list of events.
	 * @param original The original string.
	 * @param trgLang Code of the target (output) language.
	 * @return The generated output string
	 */
	public static String generateOutput(ArrayList<Event> list,
		String original,
		String trgLang) {
		GenericSkeletonWriter writer = new GenericSkeletonWriter();
		StringBuilder tmp = new StringBuilder();
		for (Event event : list) {
			switch (event.getEventType()) {
			case START_DOCUMENT:
				tmp.append(writer.processStartDocument(trgLang, "utf-8", null, new EncoderManager(),
					(StartDocument) event.getResource()));
				break;
			case END_DOCUMENT:
				tmp.append(writer.processEndDocument((Ending)event.getResource()));
				break;
			case TEXT_UNIT:
				TextUnit tu = (TextUnit) event.getResource();
				tmp.append(writer.processTextUnit(tu));
				break;
			case DOCUMENT_PART:
				DocumentPart dp = (DocumentPart) event.getResource();
				tmp.append(writer.processDocumentPart(dp));
				break;
			case START_GROUP:
				StartGroup startGroup = (StartGroup) event.getResource();
				tmp.append(writer.processStartGroup(startGroup));
				break;
			case END_GROUP:
				tmp.append(writer.processEndGroup((Ending)event.getResource()));
				break;
			}
		}
		writer.close();
		return tmp.toString();
	}

	/**
	 * Gets the Nth text unit found in the given list of events.
	 * @param list The list of events
	 * @param tuNumber The number of the unit to return: 1 for the first one, 2 for the second, etc.
	 * @return The text unit found, or null.
	 */
	public static TextUnit getTextUnit (ArrayList<Event> list,
		int tuNumber)
	{
		int n = 0;
		for (Event event : list) {
			if ( event.getEventType() == EventType.TEXT_UNIT ) {
				if ( ++n == tuNumber ) {
					return (TextUnit)event.getResource();
				}
			}
		}
		return null;
	}

	/**
	 * Gets the Nth group found in the given list of events.
	 * @param list The list of events
	 * @param tuNumber The number of the group to return: 1 for the first one, 2 for the second, etc.
	 * @return The group found, or null.
	 */
	public static StartGroup getGroup (ArrayList<Event> list,
		int tuNumber)
	{
		int n = 0;
		for (Event event : list) {
			if ( event.getEventType() == EventType.START_GROUP ) {
				if ( ++n == tuNumber ) {
					return (StartGroup)event.getResource();
				}
			}
		}
		return null;
	}

	/**
	 * Gets the start document in the given list of events.
	 * @param list The list of events
	 * @return The start document found, or null.
	 */
	public static StartDocument getStartDocument (ArrayList<Event> list) {
		for (Event event : list) {
			if ( event.getEventType() == EventType.START_DOCUMENT ) {
				return (StartDocument)event.getResource();
			}
		}
		return null;
	}

}
