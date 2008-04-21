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

package net.sf.okapi.Filter;

import java.util.Hashtable;
import java.io.*;

public class DNLFile {

	public static final String    EXTENSION = ".dnl";
	
	private Hashtable<String, String>  m_Entries;

	public DNLFile ()
	{
		m_Entries = new Hashtable<String, String>();
	}
	
	public void Reset ()
	{
		m_Entries.clear();
	}

	public void AddEntry (String p_sKey)
	{
		if ( !m_Entries.containsKey(p_sKey) )
			m_Entries.put(p_sKey, null);
	}

	public boolean Find (String p_sKey)
	{
		return m_Entries.containsKey(p_sKey);
	}

	public void Load (String p_sPath) throws Exception
	{
		try
		{
			File F = new File(p_sPath);
			if ( !F.exists() ) return;
			
			Reset(); // Reset all before we try to load
			// Open the file
			BufferedReader BR = new BufferedReader(new FileReader(p_sPath));

			// Read the entries
			String sEntry;
			while ( (sEntry = BR.readLine()) != null )
			{
				if ( !m_Entries.containsKey(sEntry) )
					m_Entries.put(sEntry, null);
			}
			// Close the file
			if ( BR != null ) BR.close();
		}
		catch ( Exception E )
		{
			throw E;
		}
	}
	
	public void Save (String p_sPath) throws Exception
	{
		try
		{
			// Check if we need a file
			if ( m_Entries.size() == 0 )
			{
				// Delete the file if needed
				File F = new File(p_sPath);
				if ( F.exists() ) F.delete();
				return;
			}

			// Write the file
			BufferedWriter BW = new BufferedWriter(new FileWriter(p_sPath));
			for ( String sKey : m_Entries.keySet() )
			{
				BW.write(sKey);
				BW.newLine();
			}
			if ( BW != null ) BW.close();
		}
		catch ( Exception E )
		{
			throw E;
		}
	}
}
