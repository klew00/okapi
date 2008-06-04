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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reference implementation of IExtractionItem.
 */
public class ExtractionItem implements IExtractionItem {

	protected String                   id;
	protected boolean                  isTranslatable;
	protected boolean                  hasTarget;
	protected String                   resname;
	protected String                   restype;
	protected boolean                  preserveFormatting;
	protected String                   notes;

	private IContainer                 main;
	private ArrayList<IExtractionItem> children;
	private ArrayList<IContainer>      segments;
	private Map<String, Object>        props;


	public ExtractionItem () {
		// Default values different from initial
		isTranslatable = true;
		main = new Container();
	}
	
	public void addChild (IExtractionItem child) {
		if ( children == null ) {
			children = new ArrayList<IExtractionItem>();
		}
		children.add(child);
	}

	public boolean isEmpty () {
		return main.isEmpty();
	}
	
	public List<IExtractionItem> getChildren () {
		if ( children == null ) {
			children = new ArrayList<IExtractionItem>();
		}
		return children;
	}

	public IContainer getContent () {
		return main;
	}

	public List<IContainer> getSegments () {
		if ( segments == null ) {
			segments = new ArrayList<IContainer>();
		}
		return segments;
	}

	public void setContent (IContainer data) {
		if ( data == null ) throw new NullPointerException(); 
		main = data;
	}

	public String getID () {
		return id;
	}

	public String getName () {
		if ( resname == null ) return "";
		else return resname;
	}

	public String getType () {
		if ( restype == null ) return "";
		else return restype;
	}

	public boolean hasTarget () {
		return hasTarget;
	}

	public boolean isTranslatable () {
		return isTranslatable;
	}

	public void setID (String newId) {
		id = newId;
	}

	public void setName (String newResname) {
		resname = newResname;
	}

	public void setType (String newRestype) {
		restype = newRestype;
	}

	public void setHasTarget (boolean newHasTarget) {
		hasTarget = newHasTarget;
	}

	public void setIsTranslatable (boolean newIsTranslatable) {
		isTranslatable = newIsTranslatable;
	}

	public Object getProperty (String name) {
		if ( props == null ) return null;
		return props.get(name);
	}

	public void setProperty (String name,
		Object value)
	{
		if ( props == null ) {
			props = new HashMap<String, Object>();
		}
		props.put(name, value);
	}

	public void clearProperties () {
		if ( props != null ) {
			props.clear();
		}
	}

	public boolean preserveFormatting () {
		return preserveFormatting;
	}

	public void setPreserveFormatting (boolean preserve) {
		preserveFormatting = preserve;
	}

	public boolean hasNote () {
		return (( notes != null ) && ( notes.length() > 0 ));
	}
	
	public String getNote () {
		return notes;
	}

	public void setNote (String text) {
		notes = text;
	}
}
