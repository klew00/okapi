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

package net.sf.okapi.apptest.resource;

import java.util.Hashtable;

public class LocaleProperties {

	private Hashtable<String, String>  props;
	
	
	public String getProperty (String name) {
		if ( props == null ) return null;
		return props.get(name);
	}
	
	public void setProperty (String name,
		String value)
	{
		if ( props == null ) props = new Hashtable<String, String>();
		props.put(name, value);
	}
	
	public Hashtable<String, String> getProperties () {
		if ( props == null ) props = new Hashtable<String, String>();
		return props;
	}

}

