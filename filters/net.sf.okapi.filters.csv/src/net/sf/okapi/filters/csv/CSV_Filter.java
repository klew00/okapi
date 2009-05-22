/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.csv;

import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.filters.plaintext.PlainTextFilter;
import net.sf.okapi.filters.plaintext.TextProcessingResult;

public class CSV_Filter extends PlainTextFilter {
	
	private String chunkToProcess = "";
	private Parameters params;
	private String fieldDelimiter;
	private boolean sendEmptyFields;
		
	public CSV_Filter() {
		
		setName("okf_csv");
		setMimeType(MimeTypeMapper.CSV_MIME_TYPE);		
		setParameters(new Parameters());	// CSV Filter's parameters
	}

	protected void init() {
		chunkToProcess = "";
		fieldDelimiter = "";
		sendEmptyFields = false;
		
		IParameters punk = getParameters();
		
		if (punk instanceof Parameters) {
			params = (Parameters) punk;
			fieldDelimiter = params.fieldDelimiter;
			sendEmptyFields = params.sendEmptyFields;
		}
		else			
			params = null;			
	}
	
	@Override
	protected boolean checkTU(String tu) {

		if (Util.isEmpty(tu))			
			return sendEmptyFields;		
		else
			return true;		
	}
	
	protected TextProcessingResult processChunk(String chunk) {
		
		return super.processLine(chunk);
	}

	private String[] splitLine(String line) {
		
		if (Util.isEmpty(fieldDelimiter)) return new String[] {line};  // The whole line is considered a chunk
		
		// Block delimiters inside quoted fields
			if (!Util.isEmpty(params.textQualifier)) {
				
			}
		// Block quotations inside quotations
		
			
		// Split line into fields									
		String[] res = line.split(params.fieldDelimiter);
		
		// If numColumns <> -1, analyze chunks, add empty missing or delete extra ones
		
		return res;
	}
	
	@Override
	protected TextProcessingResult processLine(String line) {
				
		if (Util.isEmpty(line)) return TextProcessingResult.REJECTED;
		
		String[] chunks = splitLine(line);
		if (chunks == null) return super.processLine(line); // No chunks, process the whole line
		
		boolean tuSent = false;
		int startGroupIndex = getQueueSize();
		
		// Process chunks
		for (int i = 0; i < chunks.length; i++) {
			String chunk = chunks[i];
									
			if (Util.isEmpty(chunkToProcess)) chunkToProcess = chunk;
			
			switch (processChunk(chunkToProcess)) { 			
			
			case REJECTED:				
				if (getActiveSkeleton() != null) {
					
				// Add the whole line to skeleton
					getActiveSkeleton().append(chunk);
					
				// Add field delimiter to skeleton
					if (i < chunks.length - 1) // If not the last field in the line
						getActiveSkeleton().append(fieldDelimiter);
				}
				chunkToProcess = "";
				continue;
				
			case ACCEPTED:						
				// Add field delimiter to skeleton
				if (i < chunks.length - 1) // If not the last field in the line
					if (getActiveSkeleton() != null) getActiveSkeleton().append(fieldDelimiter);
		
				tuSent = true;
				chunkToProcess = "";
				break;
				
			case COMBINE_WITH_NEXT:
				chunkToProcess+= chunk;
				continue;
		}
						
		} // for-loop
		
		// Wrap the record with Start group/End group
		if (tuSent) {
			sendEvent(startGroupIndex, EventType.START_GROUP, new StartGroup(""));
			sendEvent(EventType.END_GROUP, new Ending(""));
		}
		
		return (tuSent) ? TextProcessingResult.ACCEPTED : TextProcessingResult.REJECTED;
	}

}