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

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.ISkeleton;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.TextUnit;

/**
 * Driver to test filter output.
 */
public class FilterTestDriver {

	private boolean showSkeleton = true;
	private boolean showOnlyTextUnits = false;
	private int warnings;
	private boolean ok;

	static public boolean compareEvents(ArrayList<FilterEvent> manual, ArrayList<FilterEvent> generated) {
		if (manual.size() != generated.size()) {
			return false;
		}

		Iterator<FilterEvent> manualIt = manual.iterator();
		for (FilterEvent ge : generated) {
			FilterEvent me = manualIt.next();
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
	 * Indicates to this driver that only the TEXT_UNIT event should be
	 * displayed.
	 * 
	 * @param value
	 *            True to show only the text units, false to show all events.
	 */
	public void setShowOnlyTextUnits(boolean value) {
		showOnlyTextUnits = value;

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
		int start = 0;
		int finished = 0;
		int startDoc = 0;
		int endDoc = 0;
		int startGroup = 0;
		int endGroup = 0;
		int startSubDoc = 0;
		int endSubDoc = 0;

		System.out.println("================================================");
		FilterEvent event;
		while (filter.hasNext()) {
			event = filter.next();
			switch (event.getEventType()) {
			case START:
				start++;
				if (showOnlyTextUnits)
					break;
				System.out.println("---Start");
				break;
			case FINISHED:
				finished++;
				if (showOnlyTextUnits)
					break;
				System.out.println("---Finished");
				break;
			case START_DOCUMENT:
				startDoc++;
				System.out.println("---Start Document");
				checkStartDocument((StartDocument) event.getResource());
				if (showOnlyTextUnits)
					break;
				printSkeleton(event.getResource());
				break;
			case END_DOCUMENT:
				endDoc++;
				if (showOnlyTextUnits)
					break;
				System.out.println("---End Document");
				printSkeleton(event.getResource());
				break;
			case START_SUBDOCUMENT:
				startSubDoc++;
				if (showOnlyTextUnits)
					break;
				System.out.println("---Start Sub Document");
				printSkeleton(event.getResource());
				break;
			case END_SUBDOCUMENT:
				endSubDoc++;
				if (showOnlyTextUnits)
					break;
				System.out.println("---End Sub Document");
				printSkeleton(event.getResource());
				break;
			case START_GROUP:
				startGroup++;
				if (showOnlyTextUnits)
					break;
				System.out.println("---Start Group");
				printSkeleton(event.getResource());
				break;
			case END_GROUP:
				endGroup++;
				if (showOnlyTextUnits)
					break;
				System.out.println("---End Group");
				printSkeleton(event.getResource());
				break;
			case TEXT_UNIT:
				System.out.println("---Text Unit");
				TextUnit tu = (TextUnit) event.getResource();
				System.out.println("S=[" + tu.toString() + "]");
				for (String lang : tu.getTargetLanguages()) {
					System.out.println("T(" + lang + ")=[" + tu.getTarget(lang).toString() + "]");
				}
				printResource(tu);
				printSkeleton(tu);
				break;
			case DOCUMENT_PART:
				if (showOnlyTextUnits)
					break;
				System.out.println("---Document Part");
				printResource((INameable) event.getResource());
				printSkeleton(event.getResource());
				break;
			}
		}

		if (start != 1) {
			System.out.println(String.format("*****ERROR: START = %d", start));
			ok = false;
		}
		if (startDoc != 1) {
			System.out.println(String.format("*****ERROR: START_DOCUMENT = %d", startDoc));
			ok = false;
		}
		if (endDoc != 1) {
			System.out.println(String.format("*****ERROR: END_DOCUMENT = %d", endDoc));
			ok = false;
		}
		if (finished != 1) {
			System.out.println(String.format("*****ERROR: FINISHED = %d", finished));
			ok = false;
		}
		if (startSubDoc != endSubDoc) {
			System.out.println(String.format("*****ERROR: START_SUBDOCUMENT=%d, END_SUBDOCUMENT=%d", startSubDoc,
					endSubDoc));
			ok = false;
		}
		if (startGroup != endGroup) {
			System.out.println(String.format("*****ERROR: START_GROUP=%d, END_GROUP=%d", startGroup, endGroup));
			ok = false;
		}
		System.out.println(String.format("Number of warnings = %d", warnings));
		return ok;
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
		if (!showSkeleton)
			return;
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
			System.out.println("*****WARNING: No encoding specified in StartDocument.");
			warnings++;
		} else
			System.out.println("StartDocument encoding = " + tmp);

		tmp = startDoc.getLanguage();
		if ((tmp == null) || (tmp.length() == 0)) {
			System.out.println("*****WARNING: No language specified in StartDocument.");
			warnings++;
		} else
			System.out.println("StartDocument language = " + tmp);

		tmp = startDoc.getName();
		if ((tmp == null) || (tmp.length() == 0)) {
			System.out.println("*****WARNING: No name specified in StartDocument.");
			warnings++;
		} else
			System.out.println("StartDocument name = " + tmp);

		System.out.println("StartDocument MIME type = " + startDoc.getMimeType());
		System.out.println("StartDocument Type = " + startDoc.getType());
	}

}
