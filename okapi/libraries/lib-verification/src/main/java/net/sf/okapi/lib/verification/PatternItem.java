/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.lib.verification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.sf.okapi.common.exceptions.OkapiIOException;

public class PatternItem {
	
	public static final String SAME = "<same>";

	public String source;
	public String target;
	public boolean enabled;
	public String message;
	
	private Pattern srcPat;
	private Pattern trgPat;

	public static List<PatternItem> loadFile (String path) {
		ArrayList<PatternItem> list = new ArrayList<PatternItem>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
			String line = br.readLine();
			while ( line != null ) {
				if ( line.trim().length() == 0 ) continue;
				if ( line.startsWith("#") ) continue;
				String[] parts = line.split("\t", -2);
				if ( parts.length < 3 ) {
					throw new OkapiIOException("Missing one or more tabs in line:\n"+line);
				}
				list.add(new PatternItem(parts[1], parts[2], parts[0].equals("1")));
				line = br.readLine();
			}
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error reading pattern file.", e);
		}
		finally {
			if ( br != null ) {
				try {
					br.close();
				}
				catch ( IOException e ) {
					throw new OkapiIOException("Error closing pattern file.", e);
				}
			}
		}
		return list;
	}
	
	public static List<PatternItem> saveFile (String path,
		List<PatternItem> list)
	{
		PrintWriter pr = null;
		final String lineBreak = System.getProperty("line.separator");
		try {
			pr = new PrintWriter(path);
			for ( PatternItem item : list ) {
				pr.write((item.enabled ? "1" : "0")
					+ "\t" + item.source
					+ "\t" + item.target
					+ lineBreak);
			}
			pr.flush();
		}
		catch ( IOException e ) {
			throw new OkapiIOException("Error reading pattern file.", e);
		}
		finally {
			if ( pr != null ) {
				pr.close();
			}
		}
		return list;
	}
	
	public PatternItem (String source,
		String target,
		boolean enabled)
	{
		create(source, target, enabled, null);
	}

	public PatternItem (String source,
		String target,
		boolean enabled,
		String message)
	{
		create(source, target, enabled, message);
	}

	private void create (String source,
		String target,
		boolean enabled,
		String message)
	{
		this.source = source;
		this.target = target;
		this.enabled = enabled;
		this.message = message;
	}

	public void compile () {
		srcPat = Pattern.compile(source);
		if ( !target.equals(SAME) ) {
			trgPat = Pattern.compile(target);
		}
	}

	public Pattern getSourcePattern () {
		return srcPat; 
	}

	public Pattern getTargetPattern () {
		return trgPat; 
	}

}
