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

package net.sf.okapi.lib.terminology.csv;

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

public class CSVReader implements IGlossaryReader {

	private final int MAX_COLUMNS = 2;
	private final char DELIMITER = ',';
	private final char QUALIFIER = '"';
	
	private ConceptEntry nextEntry;
	private BufferedReader reader;
	private LocaleId srcLoc;
	private LocaleId trgLoc;
	
	private int currentLine=0;
	
	public CSVReader (LocaleId srcLoc,
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
				currentLine++;
				parts = parseCsvLine(line);
				// Use only if we have at least source and target
				
				if (parts[0]!=null && parts[1]!=null && parts[0].length()>0 && parts[1].length()>0 ) 
					break;
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

	
	private String[] parseCsvLine(String line){
		
		String[] entry = new String[MAX_COLUMNS+1];
		int column = 0;
		StringBuilder sb = new StringBuilder();

		boolean insideColumn = false;
		boolean qualified = false;

		
		for(int i=0; i<line.length() && column<MAX_COLUMNS ; i++){

			//--at this point we're starting to read a new column--
			if(!insideColumn){
				
				if(line.charAt(i)==' '){

					//--ignoring all whitespaces after DELIMITER--
					
				}else if (line.charAt(i)==DELIMITER){
			
					//--create empty--
					entry[column] = sb.toString();
					column++;
					sb.setLength(0);
					
				}else{
					insideColumn = true;

					if(line.charAt(i)==QUALIFIER){
						qualified = true;

					}else{
						qualified = false;
						sb.append(line.charAt(i));
					}
				}
				
			}else{
				
				if (line.charAt(i)==DELIMITER){
					//--character is a delimiter
 
					if(qualified){
						//--within qualified text
						
						sb.append(line.charAt(i));
						
					}else if(!qualified){
						//--complete column--
						
						entry[column] = sb.toString().trim();
						column++;
						sb.setLength(0);
						
						insideColumn = false;

					}
					
				}else if(line.charAt(i)==QUALIFIER){
					//--character is a qualifier
					
					if(i+1 < line.length() && line.charAt(i+1)==QUALIFIER){
						//--if the qualifier is escaped write it out--
						
						sb.append(line.charAt(i));
						i++;
						
					}else{

						if(insideColumn && !qualified){
							throw new OkapiIOException("Row "+currentLine+": Incorrect place for qualifier");
						}

						//--if the qualifier is not escaped it ends the column--
						insideColumn = false;
						
					}

				}else{
					sb.append(line.charAt(i));
				}
			}
		}
		entry[column] = sb.toString();
		
		return entry;
	}
	
}
