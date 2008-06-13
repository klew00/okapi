/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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

package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Util;

/**
 * Reference implementation of IExtractionItem.
 */
public class ExtractionItem extends CommonResource implements IExtractionItem {

	private String                     note;
	private IContainer                 main;
	private ArrayList<IExtractionItem> children;
	private ArrayList<IContainer>      segments;
	private boolean                    isSegmented;
	private IExtractionItem            target;
	private IExtractionItem            parent;
	private int                        currentIndex;
	private ArrayList<IExtractionItem> allItems;


	public ExtractionItem () {
		main = new Container();
	}

	public int getKind () {
		return KIND_ITEM;
	}
	
	@Override
	public String toString () {
		if ( isSegmented )
			return buildCompiledSegment(false).toString();
		else
			return main.toString();
	}
	
	public void addChild (IExtractionItem child) {
		//TODO: This can't work as it: we need a reference to where the child
		// is going in the parent, for merging (or can we just use indices?)
		if ( children == null ) {
			children = new ArrayList<IExtractionItem>();
		}
		// We assume all item in a given item are items of the same
		// implementation.
		((ExtractionItem)child).parent = this;
		children.add(child);
	}

	public boolean isEmpty () {
		if ( isSegmented ) return buildCompiledSegment(false).isEmpty();
		else return main.isEmpty();
	}
	
	public List<IExtractionItem> getChildren () {
		if ( children == null ) {
			children = new ArrayList<IExtractionItem>();
		}
		return children;
	}

	public IContainer getContent () {
		if ( isSegmented ) {
			return buildCompiledSegment(false);
		}
		else return main;
	}

	public List<IContainer> getSegments () {
		if ( isSegmented ) return segments;
		// Else: Make a temporary list of all segments
		List<IContainer> tmpSegments = new ArrayList<IContainer>();
		tmpSegments.add(main);
		return tmpSegments;
	}
	
	public void setContent (IContainer data) {
		if ( data == null ) throw new NullPointerException(); 
		main = data;
		if ( isSegmented ) {
			segments.clear();
			isSegmented = false;
		}
	}

	public boolean hasTarget () {
		return (target != null);
	}

	public boolean hasNote () {
		return (( note != null ) && ( note.length() > 0 ));
	}
	
	public String getNote () {
		return note;
	}

	public void setNote (String text) {
		note = text;
	}
	
	public void removeSegmentation () {
		buildCompiledSegment(true);
	}
	
	public void addSegment (IContainer segment) {
		if ( !isSegmented ) {
			if ( segments == null ) segments = new ArrayList<IContainer>();
			else segments.clear();
			if ( !main.isEmpty() ) segments.add(main);
			isSegmented = true;
		}
		segments.add(segment);
	}

	private IContainer buildCompiledSegment (boolean removeSegmentation) {
		if ( !isSegmented ) return main;

		// If needed: compile all segments into one object
		Container compiled = new Container();
		for ( IContainer seg : segments ) {
			compiled.append(seg);
		}
		
		// Remove the segmentation if requested
		if ( removeSegmentation ) {
			main = compiled;
			segments.clear();
			isSegmented = false;
		}
		
		// Return the compiled segment
		return compiled;
	}

	public IExtractionItem getTarget () {
		return target;
	}

	public void setTarget (IExtractionItem item) {
		target = item;
	}

	public boolean hasChild () {
		if ( children == null ) return false;
		else return !children.isEmpty();
	}

	public IExtractionItem getParent () {
		return parent;
	}

	/**
	 * Store recursively the children items of an items in the allItems list.
	 * @param parentItem The parent item to store.
	 */
	private void storeItems (IExtractionItem parentItem) {
		if ( parentItem.hasChild() ) {
			for ( IExtractionItem item : parentItem.getChildren() ) {
				storeItems(item);
			}
		}
		allItems.add(parentItem);
	}
	
	public IExtractionItem getFirstItem () {
		if ( this.hasChild() ) {
			allItems = new ArrayList<IExtractionItem>();
			storeItems(this);
			currentIndex = -1;
			return getNextItem();
		}
		else {
			allItems = null;
			return this;
		}
	}
	
	public IExtractionItem getNextItem () {
		if ( allItems == null ) return null;
		if ( ++currentIndex < allItems.size() ) return allItems.get(currentIndex);
		else return null;
	}

	private static void itemToXML (IExtractionItem item,
		StringBuilder text)
	{
		text.append(String.format("<item id=\"%s\" type=\"%s\" name=\"%s\"",
			Util.escapeToXML(item.getID(), 3, false),
			Util.escapeToXML(item.getType(), 3, false),
			Util.escapeToXML(item.getName(), 3, false)));
		text.append(String.format(" translate=\"%s\" xml:space=\"%s\">",
			(item.isTranslatable() ? "yes" : "no"),
			(item.preserveSpaces() ? "preserve" : "default")));

		((ExtractionItem)item).propertiesToXML(text);
		((ExtractionItem)item).extensionsToXML(text);
		
		if ( item.hasChild() ) {
			text.append("<children>");
			for ( IExtractionItem subItem : item.getChildren() ) {
				itemToXML(subItem, text);
			}
			text.append("</children>");
		}
		
		text.append("<src>");
		text.append(item.getContent().toXML());
		text.append("</src>");
		text.append("<trg>");
		if ( item.hasTarget() ) {
			text.append(item.getTarget().getContent().toXML());
		}
		text.append("</trg>");
		
		if ( item.hasNote() ) {
			text.append("<note>");
			text.append(Util.escapeToXML(item.getNote(), 0, false));
			text.append("</note>");
		}

		text.append("</item>");
	}
	
	public String toXML () {
		StringBuilder text = new StringBuilder();
		itemToXML(this, text);
		return text.toString();
	}
}
