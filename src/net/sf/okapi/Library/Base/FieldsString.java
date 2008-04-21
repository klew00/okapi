/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
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

package net.sf.okapi.Library.Base;

public class FieldsString {
	public static final char FIELDMARKER    = '\u009C';
	public static final char GROUPMARKER    = '\u009D';
	
	private StringBuilder  m_sbData;

	public FieldsString ()
	{
		m_sbData = new StringBuilder();
		reset();
	}
	
	public FieldsString (String p_sData)
	{
		if ( p_sData != null ) m_sbData = new StringBuilder(p_sData);
		else
		{
			m_sbData = new StringBuilder();
			reset();
		}
	}

	public void reset ()
	{
		m_sbData = new StringBuilder();
		m_sbData.append(FIELDMARKER);
	}

	public String toString ()
	{
		return m_sbData.toString();
	}

	public void set (String p_sName,
		String p_sValue)
	{
		// Create the full name+value string
		if ( p_sValue != null )
		{
			p_sValue = String.format("%s=%s%s", p_sName.toLowerCase(),
				p_sValue.replace("\r", "$0d$"), FIELDMARKER);
		}

		// Search if the the field exists
		String sName = FIELDMARKER + p_sName.toLowerCase() + "=";
		int nPos1 = m_sbData.toString().indexOf(sName);
		if ( nPos1 < 0 ) // Not found: add it
		{
			if ( p_sValue != null ) {
				m_sbData.append(p_sValue);
			}
			return;
		}

		// Else: It exists, replace it
		// Search for the value
		int nPos2 = m_sbData.toString().indexOf(FIELDMARKER, nPos1+1);
		if ( nPos2 < 0 ) nPos2 = nPos1; // No end marker, no value;

		// Replace the value
		m_sbData.delete(nPos1+1, nPos2); //LEN=nPos2-nPos1
		if ( p_sValue != null )
			m_sbData.insert(nPos1+1, p_sValue);
	}

	public void add (String p_sName,
		String p_sValue)
	{
		if ( p_sValue == null )
		{
			m_sbData.append(String.format("%s=%s%s", p_sName.toLowerCase(),
				"", FIELDMARKER));
		}
		else
		{
			m_sbData.append(String.format("%s=%s%S", p_sName.toLowerCase(),
				p_sValue.replace("\r", "$0d$"), FIELDMARKER));
		}
	}

	public void add (String p_sName,
		boolean p_bValue)
	{
		m_sbData.append(String.format("%s=%s%S", p_sName.toLowerCase(),
			(p_bValue ? 1 : 0), FIELDMARKER));
	}

	public void add (String p_sName,
		int p_nValue)
	{
		m_sbData.append(String.format("%s=%s%S", p_sName.toLowerCase(),
			p_nValue, FIELDMARKER));
	}

	public void add (String p_sName,
		char p_chValue)
	{
		m_sbData.append(String.format("%s=%s%S", p_sName.toLowerCase(),
			p_chValue, FIELDMARKER));
	}
	
	public void addGroup (String p_sName,
		String p_sValue)
	{
		m_sbData.append(String.format("%s%s=%s%s%s", GROUPMARKER, 
			p_sName.toLowerCase(), p_sValue, GROUPMARKER, FIELDMARKER));
	}

	public String get (String p_sName,
		String p_sDefaultValue)
	{
		try
		{
			// Search for the field name
			String sName = FIELDMARKER + p_sName.toLowerCase() + "=";
			int nPos1 = m_sbData.toString().indexOf(sName);
			if ( nPos1 < 0 ) return p_sDefaultValue; // Field name not found

			// Search for the value
			nPos1 += sName.length();
			int nPos2 = m_sbData.toString().indexOf(FIELDMARKER, nPos1);
			if ( nPos2 < 0 ) return p_sDefaultValue; // No value found

			// Get the value
			return m_sbData.toString().substring(
				nPos1, nPos2).replace("$0d$", "\r"); //LEN=(nPos2-nPos1)
		}
		catch (Exception E)
		{
			return p_sDefaultValue;
		}
	}

	public boolean get (String p_sName,
		boolean p_bDefaultValue)
	{
		try
		{
			String sTmp = get(p_sName, null);
			if ( sTmp == null ) return p_bDefaultValue;
			return sTmp.equals("1");
		}
		catch (Exception E)
		{
			return p_bDefaultValue;
		}
	}

	public int get (String p_sName,
		int p_nDefaultValue)
	{
		try
		{
			String sTmp = get(p_sName, null);
			if ( sTmp == null ) return p_nDefaultValue;
			return Integer.parseInt(sTmp);
		}
		catch (Exception E)
		{
			return p_nDefaultValue;
		}
	}

	public char get (String p_sName,
		char p_chDefaultValue)
	{
		try
		{
			String sTmp = get(p_sName, null);
			if (( sTmp == null ) || ( sTmp.length() == 0 )) return p_chDefaultValue;
			return sTmp.charAt(0);
		}
		catch (Exception E)
		{
			return p_chDefaultValue;
		}
	}

	public String getGroup (String p_sName,
		String p_sDefaultValue)
	{
		try
		{
			// Search for the field name
			String sName = GROUPMARKER + p_sName.toLowerCase() + "=";
			int nPos1 = m_sbData.toString().indexOf(sName);
			if ( nPos1 < 0 ) return p_sDefaultValue; // Field name not found

			// Search for the value
			nPos1 += sName.length();
			int nPos2 = m_sbData.toString().indexOf(GROUPMARKER, nPos1);
			if ( nPos2 < 0 ) return p_sDefaultValue; // No value found

			// Get the value
			return m_sbData.toString().substring(nPos1, nPos2); //LEN=(nPos2-nPos1)
		}
		catch (Exception E)
		{
			return p_sDefaultValue;
		}
	}

}
