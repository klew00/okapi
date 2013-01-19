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

package net.sf.okapi.lib.terminology.tsv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.lib.terminology.ConceptEntry;
import net.sf.okapi.lib.terminology.IGlossaryReader;

public class TSVReader implements IGlossaryReader {

	private ConceptEntry nextEntry;
	private BufferedReader reader;
	private LocaleId srcLoc;
	private LocaleId trgLoc;

	public TSVReader (LocaleId srcLoc,
		LocaleId trgLoc)
	{
		this.srcLoc = srcLoc;
		this.trgLoc = trgLoc;
	}
	
	@Override
	public void open (File file) {
		try {
			open(new FileInputStream(file));
		}
		catch ( Throwable e) {
			throw new OkapiIOException("Error opening the URI.\n" + e.getLocalizedMessage());
		}
	}

	@Override
	public void open (InputStream input) {
		try {
			close();

			// Deal with the potential BOM
			String encoding = "UTF-8";
			BOMAwareInputStream bis = new BOMAwareInputStream(input, encoding);
			encoding = bis.detectEncoding();
			// Open the input document with BOM-aware reader
			reader = new BufferedReader(new InputStreamReader(bis, encoding));
			
			// Read the first entry
			readNext();
		}
		catch ( Throwable e) {
			throw new OkapiIOException("Error opening the URI.\n" + e.getLocalizedMessage());
		}
	}

	@Override
	public void close () {
		nextEntry = null;
		try {
			if ( reader != null ) {
				reader.close();
				reader = null;
			}
		}
		catch ( IOException e) {
			throw new OkapiIOException(e);
		}
	}

	@Override
	public boolean hasNext () {
		return (nextEntry != null);
	}

	@Override
	public ConceptEntry next () {
		ConceptEntry currentEntry = nextEntry; // Next entry becomes the current one
		readNext(); // Parse the new next entry
		return currentEntry; // Send the current entry
	}

	private void readNext () {
		try {
			nextEntry = null;
			String parts[];
			
			while ( true ) {
				String line = reader.readLine();
				// Check if we reached the end
				if ( line == null ) return;
				// Skip empty and blank lines
				line = line.trim();
				if ( line.isEmpty() ) continue;
				// Split the line into fields
				parts = line.split("\\t");
				// Use only if we have at least source and target
				if ( parts.length > 1 ) break;
			}
			
			ConceptEntry cent = new ConceptEntry();
			cent.addTerm(srcLoc, parts[0]);
			cent.addTerm(trgLoc, parts[1]);
			nextEntry = cent;
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error when reading." + e.getLocalizedMessage(), e);
		}
	}

}
