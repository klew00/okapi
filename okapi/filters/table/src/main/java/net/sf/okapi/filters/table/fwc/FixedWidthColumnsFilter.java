/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.table.fwc;

import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.common.ListUtil;
import net.sf.okapi.filters.table.base.BaseTableFilter;
import net.sf.okapi.lib.extra.filters.TextProcessingResult;

/**
 * Fixed-Width Columns filter. Extracts text from a fixed-width columns table padded with white-spaces.
 * 
 * @version 0.1, 09.06.2009
 */
public class FixedWidthColumnsFilter extends BaseTableFilter {

	public static final String FILTER_NAME		= "okf_table_fwc";	
	public static final String FILTER_CONFIG	= "okf_table_fwc";
	
	public static String COLUMN_WIDTH	= "column_width";
	
	private Parameters params; // Fixed-Width Columns Filter parameters
	//protected List<Integer> columnWidths;
	protected List<Integer> columnStartPositions;
	protected List<Integer> columnEndPositions;
	
//	public void component_create() {
//
//		super.component_create();
		
	public FixedWidthColumnsFilter() {
	
		setName(FILTER_NAME);

		addConfiguration(true, // Do not inherit configurations from Base Table Filter
						FILTER_CONFIG,
						"Table (Fixed-Width Columns)",
						"Fixed-width columns table padded with white-spaces.", 
						"okf_table_fwc.fprm");
		
		setParameters(new Parameters());	// Fixed-Width Columns Filter parameters
	}

	@Override
	protected void component_init() {
		
		// Commons, should be included in all descendants introducing own params
		params = getParameters(Parameters.class);	// Throws OkapiBadFilterParametersException
		super.component_init();
		
		// Initialization
		//columnWidths = ListUtil.stringAsIntList(params.columnWidths);
		columnStartPositions = ListUtil.stringAsIntList(params.columnStartPositions);
		columnEndPositions = ListUtil.stringAsIntList(params.columnEndPositions);
	}

	@Override
	protected TextProcessingResult extractCells(List<ITextUnit> cells, TextContainer lineContainer, long lineNum) {
		
		if (cells == null) return TextProcessingResult.REJECTED;
		if (lineContainer == null) return TextProcessingResult.REJECTED;
		
		String line = lineContainer.getCodedText();		
		if (Util.isEmpty(line)) return TextProcessingResult.REJECTED;
		
//		int pos = 0;
		//for (int i = 0; i < columnWidths.size(); i++) {
		int len = Math.min(columnStartPositions.size(), columnEndPositions.size());
		for (int i = 0; i < len; i++) {
			
//			int start = 0;
//			if (pos >= line.length())
//				start = line.length();
//			else				
//				start = pos;
//			
//			pos += columnWidths.get(i);
//			
//			int end = 0;
//			if (pos >= line.length())
//				end = line.length();
//			else				
//				end = pos; 

			int start = columnStartPositions.get(i) - 1; // 0-base
			int end = columnEndPositions.get(i) - 1; // 0-base
			
//			int start = columnStartPositions.get(i) - 1; // 0-base
//			
//			int end;
//			
//			if (i < len - 1)
//				end = columnStartPositions.get(i + 1) - 1; // 0-base
//			else
//				end = line.length();
						
			if (start >= end) continue;
			if (start >= line.length()) continue;
			if (end > line.length()) end = line.length(); 
			
			int skelEnd;
			if (i < len - 1)
				skelEnd = columnStartPositions.get(i + 1) - 1; // start of next column
			else
				skelEnd = line.length();

			if (skelEnd > line.length()) skelEnd = line.length();
			
			String srcPart = line.substring(start, end); // end is excluded
			String skelPart = line.substring(end, skelEnd);
			
			ITextUnit cell = TextUnitUtil.buildTU(srcPart, skelPart);
			// TODO check (end - start)
			cell.setSourceProperty(new Property(COLUMN_WIDTH, String.valueOf(end - start), true));
			cells.add(cell);
		}
		
		return TextProcessingResult.ACCEPTED;
	}

//	@Override
//	//protected boolean sendSourceCell(TextUnit tu, int column, int numColumns) {
//	protected TextProcessingResult sendAsSource(TextUnit textUnit) {
//		// column is 1-based
//		
//		if (tu == null) return false;
//		
//		int index = column - 1; 
//		//if (!Util.checkIndex(index, columnWidths)) return false;
//		if (!Util.checkIndex(index, columnStartPositions)) return false;
//		if (!Util.checkIndex(index, columnEndPositions)) return false;
//		
//		//int colWidth = columnWidths.get(index);
//		int colWidth = columnEndPositions.get(index) - columnStartPositions.get(index); 
//		
//		tu.setSourceProperty(new Property(COLUMN_WIDTH, String.valueOf(colWidth), true));
//		
//		boolean res = super.sendSourceCell(tu, column, numColumns);
//		
////		if (column < numColumns) {
////			
////			String gap = new String();
////			
////			sendSkeletonCell(gap, getActiveSkeleton(), column, numColumns);
////		}
////				
//		return res;
//	}
	
}
