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

package net.sf.okapi.filters.table.csv;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.utils.ListUtils;
import net.sf.okapi.filters.common.framework.TextProcessingResult;
import net.sf.okapi.filters.common.utils.TextUnitUtils;
import net.sf.okapi.filters.table.base.BaseTableFilter;

/**
 * Comma-Separated Values filter. Extracts text from a comma-separated values table, 
 * optionally containing a header with field names and other info.
 * 
 * @version 0.1, 09.06.2009
 * @author Sergei Vasilyev
 */

public class CommaSeparatedValuesFilter  extends BaseTableFilter {

	public static final String FILTER_NAME		= "okf_table_csv";
	public static final String FILTER_CONFIG	= "okf_table_csv";
	
	private static String MERGE_START_TAG	= "\ue10a";
	private static String MERGE_END_TAG		= "\ue10b";
	private static String LINE_BREAK_TAG	= "\ue10c";
	private static String LINE_WRAP_TAG		= "\ue10d";

//	Debug
//	private static String MERGE_START_TAG	= "_start_";
//	private static String MERGE_END_TAG		= "_end_";
//	private static String LINE_BREAK_TAG	= "_line_";
//	private static String LINE_WRAP_TAG		= "_wrap_";
	
	private Parameters params; // CSV Filter parameters
	private List<String> buffer;
	private boolean merging = false;
	private int qualifierLen;
	
	public CommaSeparatedValuesFilter() {
		
		super();
		
		setName(FILTER_NAME);

		addConfiguration(true, // Do not inherit configurations from Base Table Filter
				FILTER_CONFIG,
				"Table (Comma-Separated Values)",
				"Comma-separated values, optional header with field names.", 
				"okf_table_csv.fprm");
		
		setParameters(new Parameters());	// CSV Filter's parameters
	}

	@Override
	protected void filter_init() {

		merging = false;		
		
		params = getParameters(Parameters.class);	// Throws OkapiBadFilterParametersException
		qualifierLen = Util.getLength(params.textQualifier);
		
		super.filter_init();
		
		if (buffer == null) 
			buffer = new ArrayList<String>();
		else
			buffer.clear();		
	}

	@Override
	protected TextProcessingResult extractCells(List<TextUnit> cells, TextContainer lineContainer, long lineNum) {		
		// Extract cells from the line, if no multi-line chunks, fill up the cells list, if there are, fill the chunk buffer.
		// The cells is always an empty non-null list ready for addition
		
		if (cells == null) return TextProcessingResult.REJECTED;
		if (lineContainer == null) return TextProcessingResult.REJECTED;
		
		String line = lineContainer.getCodedText();
		
		if (Util.isEmpty(line)) return TextProcessingResult.REJECTED;
		
		if (Util.isEmpty(params.fieldDelimiter)) return super.extractCells(cells, lineContainer, lineNum);		
		
		// Split line into fields									
		String[] chunks = line.split(params.fieldDelimiter);
		
		// Analyze chunks for being multi-line
		for (String chunk : chunks) {
			
			String trimmedChunk = chunk.trim();
			
			boolean startsQualified = trimmedChunk.startsWith(params.textQualifier);
			boolean endsQualified = trimmedChunk.endsWith(params.textQualifier);
									
			if (!merging && !startsQualified && !endsQualified)		// 000
				{buffer.add(chunk); continue;}
			
			if (!merging && !startsQualified && endsQualified)		// 001
				{buffer.add(chunk); continue;}
				
			if (!merging && startsQualified && !endsQualified)		// 010
				{startMerging(); buffer.add(chunk); continue;}
				
			if (!merging && startsQualified && endsQualified)		// 011
				{buffer.add(chunk); continue;}
				
			if (merging && !startsQualified && !endsQualified)		// 100
				{buffer.add(chunk); continue;}
				
			if (merging && !startsQualified && endsQualified)		// 101
				{buffer.add(chunk); endMerging(); continue;}
				
			if (merging && startsQualified && !endsQualified)		// 110
				{cancelMerging(); startMerging(); buffer.add(chunk); continue;}
				
			if (merging && startsQualified && endsQualified)		// 111
				{cancelMerging(); buffer.add(chunk); continue;}			
		}
		
		buffer.add(LINE_BREAK_TAG);
		buffer.add(String.valueOf(lineNum));
		
		processBuffer(false);
		
		return TextProcessingResult.DELAYED_DECISION;			
	}

	@Override
	protected boolean sendSourceCell(TextUnit tu, int column, int numColumns) {
		
		if (tu == null) return false;
		
		TextFragment src = tu.getSourceContent(); 
		if (src == null) return false;
		
		String cell = src.getCodedText();
		if (Util.isEmpty(cell)) return false;
						
		GenericSkeleton skel = TextUnitUtils.forseSkeleton(tu);
			
		String trimmedChunk = cell.trim();
		
		boolean startsQualified = trimmedChunk.startsWith(params.textQualifier);
		boolean endsQualified = trimmedChunk.endsWith(params.textQualifier);
		
		// Remove qualifiers around fields (only both ends)
		if (startsQualified && endsQualified) {		
			
			cell = trimmedChunk.substring(qualifierLen, Util.getLength(trimmedChunk) - qualifierLen);
			if (skel != null) {
				
				skel.add(params.textQualifier);
				skel.addContentPlaceholder(tu);
				skel.add(params.textQualifier);
			}
		}
			
		// Change 2 quotes inside the field to one quote
		cell = cell.replaceAll("\"\"", "\"");
		
		List<String> temp = ListUtils.stringAsList(cell, LINE_WRAP_TAG);
		
		if (temp.size() > 1) {
			
			src.setCodedText("");
			
			for (int i = 0; i < temp.size(); i++) {
				
				String st = temp.get(i);
				
				src.append(st);				
				if (i == temp.size()) break;
				
				switch (params.wrapMode) {
				
				case PLACEHOLDERS:
					src.append(new Code(TagType.PLACEHOLDER, "line break", getLineBreak()));
					break;
					
				case SPACES:
					src.append(' ');
					break;
					
				case NONE:
				default:
					src.append('\n');
				}
			}			
		}
		else
			src.setCodedText(cell); // No line wrappers found 
		
		boolean res = super.sendSourceCell(tu, column, numColumns);
				
		// Add field delimiter to skeleton
		if (res && column < numColumns) { // For all columns but the last
												
			if (skel != null) skel.add(params.fieldDelimiter);
		}
		
		return res;
	}

	@Override
	protected boolean sendSkeletonCell(TextUnit cell0, GenericSkeleton skel, int column, int numColumns) {
		
		String cell = TextUnitUtils.getSourceText(cell0);
		
		if (column < numColumns) cell = cell + params.fieldDelimiter;
		
		switch (params.wrapMode) {
		
		case SPACES:
			cell = cell.replaceAll(LINE_WRAP_TAG, " ");
			break;
			
		case PLACEHOLDERS:	
		case NONE:
		default:
			cell = cell.replaceAll(LINE_WRAP_TAG, "\n");
		}
		
		skel.add(cell);
		return true;
	}
	
	@Override
	protected void filter_idle(boolean lastChance) {
		
		super.filter_idle(lastChance);
		processBuffer(lastChance);		
	}
	
	@Override
	protected void filter_done() {
				
		super.filter_done();
	}	
	
	private void startMerging() {

		if (merging) return;
		
		buffer.add(MERGE_START_TAG);
		merging = true;
	}
	
	private void endMerging() {
		
		if (!merging) return;
		
		buffer.add(MERGE_END_TAG);
		merging = false;
	}
	
	private void cancelMerging() {

		if (!merging) return;
		
		// Remove the last merging start marker
		int start = buffer.lastIndexOf(MERGE_START_TAG);
		int end = buffer.lastIndexOf(MERGE_END_TAG);
		
		if (Util.checkIndex(start, buffer) && (end == -1 || (end > -1 && end < start)))
			buffer.remove(start);
		
		merging = false;
	}
	
	private void processBuffer(boolean forceEnding) {
		// Scans the buffer for a line, merges chunks, removes and returns the line's chunks
		
		if (buffer == null) return;
		if (buffer.isEmpty()) return;
		
		int start = -1;
		int end = -1;
		
		// Locate ready merging areas, merge them, and remove contained line breaks				
		while (true) {
			
			start = buffer.indexOf(MERGE_START_TAG);
			end = buffer.indexOf(MERGE_END_TAG);
			
			if (start == -1 || end == -1) break;
			if (start >= end) break;
			
			List<String> buf = ListUtils.copyItems(buffer, start + 1, end - 1);

			while (true) {
				int index = buf.indexOf(LINE_BREAK_TAG);		
				if (index == -1) break;
		
				buf.set(index, LINE_WRAP_TAG); 
				if (Util.checkIndex(index + 1, buf)) buf.remove(index + 1); // Line num
			}
			
			while (true) {
				int index = buf.indexOf(LINE_WRAP_TAG);
				
				if (index == -1) break;
				if (!Util.checkIndex(index - 1, buf)) break;
				if (!Util.checkIndex(index + 1, buf)) break;
		
				String mergedChunk = ListUtils.listAsString(buf.subList(index - 1, index + 2), "");
				buf.subList(index, index + 2).clear();
				
				buf.set(index - 1, mergedChunk);
			}
			
			String mergedChunk = ListUtils.listAsString(buf, params.fieldDelimiter);
			
			buffer.subList(start + 1, end + 1).clear();
			
			buffer.set(start, mergedChunk);			

		}
		
//		if (!(start == -1 && end == -1)) return;
		
		// Extract a line
		int index = buffer.indexOf(LINE_BREAK_TAG);
		
		if (forceEnding) {
			// Remove hanging start tag
			
			if (start > -1 && index > -1 && index > start) {
				buffer.remove(start);
				index--;
			}
			
		}
		else
			if (index >= start && start > -1) return;
		
		if (!Util.checkIndex(index, buffer)) return; // = -1, no complete line of chunks
		if (!Util.checkIndex(index + 1, buffer)) return; // No line num item 
		long lineNum = new Long(buffer.get(index + 1));
		
		buffer.remove(index); // Line break tag
		buffer.remove(index); // Line num

		if (index == 0) return; // No chunks before line break tag 
		
		// Transfer chunks to a temp buffer, process
		
//		List<String> buf0 = ListUtils.moveItems(buffer, 0, index - 1);
		
//		List<String> buf = new ArrayList<String>();
//		buf.addAll(buffer.subList(0, index));		
//		buffer.subList(0, index).clear();
		
		addLineBreak(); // Insert a line break after the previous line
		
		List<TextUnit> buf = new ArrayList<TextUnit>();
		
		for (int i = 0; i < index; i++)			
			buf.add(TextUnitUtils.buildTU(buffer.get(i)));
		
		buffer.subList(0, index).clear();
		
		processCells(buf, lineNum);
	}
	
}
