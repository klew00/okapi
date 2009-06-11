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

package net.sf.okapi.filters.table;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.filters.plaintext.PlainTextFilter;

public class test1 extends PlainTextFilter {
	//private Parameters params = null;
	@SuppressWarnings("unused")
	private boolean chunkMerging;
	@SuppressWarnings("unused")
	private String cumulativeChunk;
//	private int startGroupIndex;
		
	public test1() {
		
		setName("okf_csv");
		setMimeType(MimeTypeMapper.CSV_MIME_TYPE);		
		setParameters(new Parameters());	// CSV Filter's parameters
		
		addConfiguration(true, 
				"",
				"CSV File",
				"Comma-separated values, optional header fith field names", 
				null);
		addConfiguration(false,
				"-table",
				"CSV Table",
				"Tab-separated columns with optional column captions", 
				null);
		addConfiguration(false,
				"-fwc",
				"Fixed-Width Columns",
				"Fixed-width columns padded with white-spaces", 
				null);
	}

	protected void init(IParameters params) {
		
//		if (params instanceof Parameters)
//			this.params = (Parameters) params;
//		else
//			throwParametersException(this.params, params);
					
		cumulativeChunk = "";
		chunkMerging = false;
	}
		
//	protected TextProcessingResult processChunk(String chunk) {
//		
//		if (!Util.isEmpty(chunk)) {
//			String trimmedChunk = chunk.trim();
//			int len = Util.getLength(params.textQualifier);
//			
//			if (trimmedChunk.startsWith(params.textQualifier) && trimmedChunk.endsWith(params.textQualifier)) {				
//				chunk = trimmedChunk.substring(len, Util.getLength(trimmedChunk) - len);
//				// return sendContent(chunk);
//			}
//			
//			if (trimmedChunk.startsWith(params.textQualifier) && !trimmedChunk.endsWith(params.textQualifier)) {
//				return TextProcessingResult.DELAYED_DECISION;
//			}
//		}
//		return null;
//		
//		// return super.sendContent(chunk);
//	}
//
//	@SuppressWarnings("unused")
//	private String[] splitLine(String line) {
//		
//		if (Util.isEmpty(params.fieldDelimiter)) return new String[] {line};  // The whole line is considered a chunk
//		
//		// Block delimiters inside quoted fields
//			if (!Util.isEmpty(params.textQualifier)) {
//				
//			}
//		// Block quotations inside quotations
//		
//			
//		// Split line into fields									
//		String[] res = line.split(params.fieldDelimiter);
//		
//		// If numColumns <> -1, analyze chunks, add empty missing or delete extra ones
//		
//		return res;
//	}
	
//	@Override
//	protected TextProcessingResult processLine(TextContainer lineContainer) {
//				
//		if (Util.isEmpty(lineContainer)) return TextProcessingResult.REJECTED;
//		
//		String line = lineContainer.getCodedText();
//		
//		String[] chunks = splitLine(line);
//		if (chunks == null) return super.processLine(lineContainer); // No chunks, process the whole line
//		
//		boolean tuSent = false;
//		startGroupIndex = getQueueSize();
//		String chunkToProcess = "";
//		
//		// Process chunks
//		for (int i = 0; i < chunks.length; i++) {
//			String chunk = chunks[i];
//									
//			if (chunkMerging) {
//				cumulativeChunk += params.fieldDelimiter + chunk;
//				chunkToProcess = cumulativeChunk;
//			}
//			else
//				chunkToProcess = chunk;
//			
//			switch (processChunk(chunkToProcess)) { 			
//			
//			case REJECTED:				
//				if (getActiveSkeleton() != null) {
//					
//				// Add the whole line to skeleton
//					if (chunkMerging) 
//						getActiveSkeleton().append(cumulativeChunk);
//					else
//						getActiveSkeleton().append(chunk);
//					
//				// Add field delimiter to skeleton
//					if (i < chunks.length - 1) // If not the last field in the line
//						getActiveSkeleton().append(params.fieldDelimiter);
//				}
//				chunkMerging = false;
//				continue;
//				
//			case ACCEPTED:						
//				// Add field delimiter to skeleton
//				if (i < chunks.length - 1) // If not the last field in the line
//					if (getActiveSkeleton() != null) getActiveSkeleton().append(params.fieldDelimiter);
//		
//				tuSent = true;
//				chunkMerging = false;
//				break;
//				
//			case COMBINE_WITH_NEXT:
//				if (!chunkMerging) {
//					chunkMerging = true;
//					cumulativeChunk = chunkToProcess;
//				}
//				
//				continue;
//		}
//						
//		} // for-loop
//		
//		if (chunkMerging) {
//			return net.sf.okapi.filters.plaintext.common.COMBINE_WITH_NEXT;
//		}
//		else {
//			// Wrap the record with Start group/End group
//			if (tuSent) {
//				sendEvent(startGroupIndex, EventType.START_GROUP, new StartGroup(""));
//				sendEvent(EventType.END_GROUP, new Ending(""));
//			}
//			
//			return (tuSent) ? TextProcessingResult.ACCEPTED : TextProcessingResult.REJECTED;
//		}
//					
//	}

}
