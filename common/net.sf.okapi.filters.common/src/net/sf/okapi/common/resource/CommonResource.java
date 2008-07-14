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

import java.util.HashMap;
import java.util.Map;

import net.sf.okapi.common.Util;

/**
 * Reference implementation of IExtractionItem.
 */
public abstract class CommonResource implements ICommonResource {

	private String                     id;
	private String                     resname;
	private String                     restype;
	private boolean                    isTranslatable;
	private boolean                    preserveWS;
	private Map<String, String>        props;
	private Map<String, IExtension>    extensions;


	public CommonResource () {
		isTranslatable = true;
	}

	public String getID () {
		if ( id == null ) return "";
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

	public void setIsTranslatable (boolean newIsTranslatable) {
		isTranslatable = newIsTranslatable;
	}

	public boolean preserveSpaces () {
		return preserveWS;
	}

	public void setPreserveSpace (boolean preserve) {
		preserveWS = preserve;
	}

	public void clearExtensions () {
		if ( extensions != null ) extensions.clear();
	}

	public void clearProperties () {
		if ( props != null ) props.clear();
	}

	public IExtension getExtension (String name) {
		if ( extensions == null ) return null;
		return extensions.get(name);
	}

	public String getProperty (String name) {
		if ( props == null ) return null;
		return props.get(name);
	}

	public void setExtension (String name,
		IExtension value)
	{
		if ( extensions == null ) {
			extensions = new HashMap<String, IExtension>();
		}
		extensions.put(name, value);
	}

	public void setProperty (String name,
		String value)
	{
		if ( props == null ) {
			props = new HashMap<String, String>();
		}
		props.put(name, value);
		
	}
	
	protected void commonAttributesToXML (StringBuilder output) {
		output.append(" id=\"");
		output.append(Util.escapeToXML(getID(), 3, false));
		output.append("\" type=\"");
		output.append(Util.escapeToXML(getType(), 3, false));
		output.append("\" name=\"");
		output.append(Util.escapeToXML(getName(), 3, false));
		output.append("\"");
	}
	
	protected void propertiesToXML (StringBuilder output) {
		if (( props != null ) && ( props.size() > 0 )) {
			output.append("<properties>");
			for ( String key : props.keySet() ) {
				output.append("<prop name=\"");
				output.append(Util.escapeToXML(key, 3, false));
				output.append("\">");
				output.append(Util.escapeToXML(props.get(key), 0, false));
				output.append("</prop>");
			}
			output.append("</properties>");
		}
	}

	protected void extensionsToXML (StringBuilder output) {
		if (( extensions != null ) && ( extensions.size() > 0 )) {
			output.append("<extensions>");
			for ( String key : extensions.keySet() ) {
				output.append(String.format("<ext name=\"%s\" class=\"%s\">",
					key, Util.escapeToXML(extensions.get(key).getClass().getCanonicalName(), 3, false)));
				output.append(extensions.get(key).toXML());
				output.append("</ext>");
			}
			output.append("</extensions>");
		}
	}
}
