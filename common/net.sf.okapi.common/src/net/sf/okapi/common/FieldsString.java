/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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

/**
 * Helper class to store and retrieve key-values pairs.
 */
public class FieldsString {
	
	public static final char FIELDMARKER    = '\u009C';
	public static final char GROUPMARKER    = '\u009D';
	
	private StringBuilder  data;


	public FieldsString () {
		data = new StringBuilder();
		reset();
	}
	
	public FieldsString (String data) {
		if ( data != null ) this.data = new StringBuilder(data);
		else {
			this.data = new StringBuilder();
			reset();
		}
	}

	public void reset () {
		data = new StringBuilder();
		data.append(FIELDMARKER);
	}

	public String toString () {
		return data.toString();
	}

	public void set (String name,
		String value)
	{
		// Create the full name+value string
		if ( value != null ) {
			value = String.format("%s=%s%s", name.toLowerCase(),
				value.replace("\r", "$0d$"), FIELDMARKER);
		}

		// Search if the the field exists
		String tmpName = FIELDMARKER + name.toLowerCase() + "=";
		int pos1 = data.toString().indexOf(tmpName);
		if ( pos1 < 0 ) { // Not found: add it
			if ( value != null ) {
				data.append(value);
			}
			return;
		}

		// Else: It exists, replace it
		// Search for the value
		int pos2 = data.toString().indexOf(FIELDMARKER, pos1+1);
		if ( pos2 < 0 ) pos2 = pos1; // No end marker, no value;

		// Replace the value
		data.delete(pos1+1, pos2); //LEN=pos2-pos1
		if ( value != null )
			data.insert(pos1+1, value);
	}

	public void add (String name,
		String value)
	{
		if ( value == null ) {
			data.append(String.format("%s=%s%s", name.toLowerCase(),
				"", FIELDMARKER));
		}
		else {
			data.append(String.format("%s=%s%S", name.toLowerCase(),
				value.replace("\r", "$0d$"), FIELDMARKER));
		}
	}

	public void add (String name,
		boolean value)
	{
		data.append(String.format("%s=%s%S", name.toLowerCase(),
			(value ? 1 : 0), FIELDMARKER));
	}

	public void add (String name,
		int value)
	{
		data.append(String.format("%s=%s%S", name.toLowerCase(),
			value, FIELDMARKER));
	}

	public void add (String name,
		char value)
	{
		data.append(String.format("%s=%s%S", name.toLowerCase(),
			value, FIELDMARKER));
	}
	
	public void addGroup (String name,
		String value)
	{
		data.append(String.format("%s%s=%s%s%s", GROUPMARKER, 
			name.toLowerCase(), value, GROUPMARKER, FIELDMARKER));
	}

	public String get (String name,
		String defaultValue)
	{
		try {
			// Search for the field name
			String tmpName = FIELDMARKER + name.toLowerCase() + "=";
			int pos1 = data.toString().indexOf(tmpName);
			if ( pos1 < 0 ) return defaultValue; // Field name not found

			// Search for the value
			pos1 += tmpName.length();
			int pos2 = data.toString().indexOf(FIELDMARKER, pos1);
			if ( pos2 < 0 ) return defaultValue; // No value found

			// Get the value
			return data.toString().substring(
				pos1, pos2).replace("$0d$", "\r"); //LEN=(pos2-pos1)
		}
		catch (Exception e) {
			return defaultValue;
		}
	}

	public boolean get (String name,
		boolean defaultValue)
	{
		try {
			String sTmp = get(name, null);
			if ( sTmp == null ) return defaultValue;
			return sTmp.equals("1");
		}
		catch (Exception e) {
			return defaultValue;
		}
	}

	public int get (String name,
		int defaultValue)
	{
		try {
			String tmp = get(name, null);
			if ( tmp == null ) return defaultValue;
			return Integer.parseInt(tmp);
		}
		catch (Exception e) {
			return defaultValue;
		}
	}

	public char get (String name,
		char defaultValue)
	{
		try {
			String tmp = get(name, null);
			if (( tmp == null ) || ( tmp.length() == 0 )) return defaultValue;
			return tmp.charAt(0);
		}
		catch (Exception e) {
			return defaultValue;
		}
	}

	public String getGroup (String name,
		String defaultValue)
	{
		try {
			// Search for the field name
			String tmpName = GROUPMARKER + name.toLowerCase() + "=";
			int pos1 = data.toString().indexOf(tmpName);
			if ( pos1 < 0 ) return defaultValue; // Field name not found

			// Search for the value
			pos1 += tmpName.length();
			int pos2 = data.toString().indexOf(GROUPMARKER, pos1);
			if ( pos2 < 0 ) return defaultValue; // No value found

			// Get the value
			return data.toString().substring(pos1, pos2); //LEN=(pos2-pos1)
		}
		catch (Exception e) {
			return defaultValue;
		}
	}

}
