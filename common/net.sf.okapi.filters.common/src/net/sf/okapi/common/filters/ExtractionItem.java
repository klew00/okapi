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

package net.sf.okapi.common.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reference implementation of IExtractionItem.
 */
public class ExtractionItem implements IExtractionItem {

	private IContainer                 main;
	private ArrayList<IExtractionItem> children;
	private ArrayList<IContainer>      segments;
	private Map<String, Object>        props;
	public int                         id;
	public boolean                     isTranslatable;
	public boolean                     hasTarget;
	public String                      resname;
	public String                      restype;
	public boolean                     preserveFormatting;
	
	public void addChild (IExtractionItem child) {
		if ( children == null ) {
			children = new ArrayList<IExtractionItem>();
		}
		children.add(child);
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
		main = data;
	}

	public int getID () {
		return id;
	}

	public String getResname () {
		return resname;
	}

	public String getRestype () {
		return restype;
	}

	public boolean hasTarget () {
		return hasTarget;
	}

	public boolean isTranslatable () {
		return isTranslatable;
	}

	public void setID (int newId) {
		id = newId;
	}

	public void setResname (String newResname) {
		resname = newResname;
	}

	public void setRestype (String newRestype) {
		restype = newRestype;
	}

	public void setHasTarget (boolean newHasTarget) {
		hasTarget = newHasTarget;
	}

	public void setIsTranslatable(boolean newIsTranslatable) {
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
}
