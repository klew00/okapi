/*===========================================================================*/
/* Copyright (C) 2008 Asgeir Frimannsson, Jim Hargrave, Yves Savourel        */
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

package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.Hashtable;

public class ResourceContainer extends ArrayList<IContainable>
	implements IResourceContainer, IAnnotatable
{
	private static final long serialVersionUID = 1L;

	protected String                        name;
	protected String                        type;
	protected boolean                       preserveWS;
	protected Hashtable<String, String>     propList;
	protected Hashtable<String, IExtension> extList;

	
	public String getName () {
		return name;
	}

	public void setName (String value) {
		name = value;
	}

	public String getType () {
		return type;
	}

	public void setType (String value) {
		type = value;
	}

	public boolean preserveWhitespaces() {
		return preserveWS;
	}

	public void setPreserveWhitespaces (boolean value) {
		preserveWS = value;
	}

	public String getProperty (String name) {
		if ( propList == null ) return null;
		return propList.get(name);
	}

	public void setProperty (String name,
		String value)
	{
		if ( propList == null ) propList = new Hashtable<String, String>();
		propList.put(name, value);
	}
	
	public Hashtable<String, String> getProperties () {
		if ( propList == null ) propList = new Hashtable<String, String>();
		return propList;
	}

	public IExtension getExtension (String name) {
		if ( extList == null ) return null;
		return extList.get(name);
	}

	public void setExtension (String name,
		IExtension value)
	{
		if ( extList == null ) extList = new Hashtable<String, IExtension>();
		extList.put(name, value);
	}

	public Hashtable<String, IExtension> getExtensions () {
		if ( extList == null ) extList = new Hashtable<String, IExtension>();
		return extList;
	}

}
