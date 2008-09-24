/*===========================================================================*/
/* Copyright (C) 2008 The Okapi framework contributors                       */
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

package net.sf.okapi.common;

import java.util.Enumeration;
import java.util.Hashtable;

public class ParametersString {

	private Hashtable<String, Object>  list;
	
	public ParametersString () {
		list = new Hashtable<String, Object>();
	}

	public ParametersString (String data) {
		list = new Hashtable<String, Object>();
		buildList(null, data);
	}

	@Override
	public String toString () {
		return buildString(null);
	}

	public void fromString (String data) {
		list.clear();
		buildList(null, data);
	}
	
	public void reset () {
		list.clear();
	}
	
	public void remove (String name) {
		if ( list.containsKey(name) ) {
			list.remove(name);
		}
	}
	
	public void removeGroup (String groupName) {
		groupName += ".";
		Enumeration<String> en = list.keys();
		String key;
		while ( en.hasMoreElements() ) {
			key = en.nextElement();
			if ( key.startsWith(groupName) ) {
				list.remove(key);
			}
		}
	}
	
	private String escape (String value) {
		if ( value == null ) return value;
		return value.replace("\n", "$0a$");
	}
	
	private String unescape (String value) {
		if ( value == null ) return value;
		return value.replace("$0a$", "\n");
	}
	
	private String buildString (String prefix) {
		StringBuilder tmp = new StringBuilder();
		Object value;
		if ( prefix != null ) prefix += ".";
		for ( String key : list.keySet() ) {
			// Check the group prefix if required
			if ( prefix != null ) {
				if ( !key.startsWith(prefix) ) continue;
				tmp.append("\n"+key.substring(prefix.length()));
			}
			else tmp.append("\n"+key);
			// Add to the string
			value = list.get(key);
			if ( value instanceof String ) {
				//TODO: escape the string content
				tmp.append("="+escape((String)value));
			}
			else if ( value instanceof Integer ) {
				tmp.append(".i="+String.valueOf(value));
			}
			else if ( value instanceof Boolean ) {
				tmp.append(".b="+((Boolean)value ? "true" : "false"));
			}
			else {
				throw new RuntimeException("Invalide type: "+key);
			}
		}
		return tmp.toString();
	}
	
	private void buildList (String prefix,
		String data)
	{
		if ( prefix == null ) prefix = "";
		else prefix += ".";
		
		String[] lines = unescape(data).split("\n", 0);
		int n;
		String name;
		
		for ( String line : lines ) {
			if ( line.length() == 0 ) continue;
			if ( line.charAt(0) == '#' ) continue;
			if (( n = line.indexOf('=')) == -1 ) continue;
			name = line.substring(0, n);
			if ( name.endsWith(".b") ) {
				list.put(prefix+line.substring(0, n-2),
					"true".equals(line.substring(n+1)));
			}
			else if ( name.endsWith(".i") ) {
				list.put(prefix+line.substring(0, n-2),
					(int)Integer.valueOf(line.substring(n+1)));
			}
			else {
				list.put(prefix+name, line.substring(n+1));
			}
		}
	}
	
	public String getGroup (String name,
		String defaultValue)
	{
		String tmp = buildString(name);
		if ( tmp.length() > 0 ) return tmp;
		// Else: return default value
		return defaultValue;
	}
	
	public String getGroup (String name) {
		return buildString(name);
	}
	
	public void setGroup (String name,
		String data)
	{
		buildList(name, data);
	}
	
	public void setGroup (String name,
		ParametersString params)
	{
		if ( name == null ) name = "";
		else name += ".";
		for ( String key : params.list.keySet() ) {
			list.put(name+key, params.list.get(key));
		}
	}
	
	public String getString (String name,
		String defaultValue)
	{
		if ( list.containsKey(name) )
			return (String)list.get(name);
		// Else: return default value.
		return defaultValue;
	}

	public String getString (String name) {
		return getString(name, "");
	}
	
	public void setString (String name,
		String value)
	{
		if ( value == null ) list.remove(name);
		else list.put(name, value);
	}
	
	public boolean getBoolean (String name,
		boolean defaultValue)
	{
		if ( list.containsKey(name) )
			return (Boolean)list.get(name);
		// Else: return false by default.
		return defaultValue;
	}
	
	public boolean getBoolean (String name) {
		return getBoolean(name, false);
	}
	
	public void setBoolean (String name,
		boolean value)
	{
		list.put(name, value);
	}
	
	public int getInteger (String name,
		int defaultValue)
	{
		if ( list.containsKey(name) )
			return (Integer)list.get(name);
		// Else: return zero by default.
		return defaultValue;
	}
	
	public int getInteger (String name) {
		return getInteger(name, 0);
	}
	
	public void setInteger (String name,
		int value)
	{
		list.put(name, value);
	}
}
