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

package net.sf.okapi.filters.table.base;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.EventType;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.filters.plaintext.base.BasePlainTextFilter;
import net.sf.okapi.filters.plaintext.common.AbstractLineFilter;
import net.sf.okapi.filters.plaintext.common.ListUtils;
import net.sf.okapi.filters.plaintext.common.TextProcessingResult;
import net.sf.okapi.filters.plaintext.common.TextUnitUtils;

/**
 * 
 * 
 * @version 0.1, 09.06.2009
 * @author Sergei Vasilyev
 */

public class BaseTableFilter extends BasePlainTextFilter {
	
	public static final String FILTER_NAME		= "okf_csv";
	public static final String FILTER_MIME		= MimeTypeMapper.CSV_MIME_TYPE;	
	public static final String FILTER_CONFIG	= "okf_csv";
	
	public static String ROW_NUMBER		= "row_number";
	public static String COLUMN_NUMBER	= "column_number";			
	
	private Parameters params; // Base Table Filter parameters
	
	protected List<Integer> sourceColumns;
	protected List<Integer> targetColumns;
	protected List<Integer> targetSourceRefs;
	protected List<Integer> commentColumns;
	protected List<Integer> commentSourceRefs;	
	protected List<Integer> sourceIdColumns;
	protected List<Integer> sourceIdSourceRefs;
	
	protected List<String> columnNames;
	protected List<String> sourceIdSuffixes;
	protected List<String> targetLanguages;

	protected ArrayList<TextUnit> tuCache;
	
	private int rowNumber = 0;
	private boolean inHeaderArea = true;
	private boolean sendListedMode = false;
	private boolean inMultilineColumnNames = false;
	private boolean isHeaderLine;
	private boolean isColumnNames;
	private boolean isFixedNumColumns;
	
	public BaseTableFilter() {
		
		super();

		setName(FILTER_NAME);
		setMimeType(FILTER_MIME);
		
//		addConfiguration(true, // Do not inherit configurations from Base Plain Text
//				FILTER_CONFIG,
//				"Table Filter",
//				"Table-like files such as tab-delimited, CSV, fixed-width columns, etc.", 
//				null);
		
		columnNames = new ArrayList<String>();
		tuCache = new ArrayList<TextUnit>();
				
		
		setParameters(new Parameters());	// Base Table Filter parameters
		
	}

	@Override
	protected void filter_init() {
		
		// Commons, should be included in all descendants introducing own params
		params = getParameters(Parameters.class);	// Throws OkapiBadFilterParametersException
		super.filter_init();
		
		// Initialization
		sourceIdColumns = ListUtils.stringAsIntList(params.sourceIdColumns);
		sourceColumns = ListUtils.stringAsIntList(params.sourceColumns);
		targetColumns = ListUtils.stringAsIntList(params.targetColumns);
		targetLanguages = ListUtils.stringAsList(params.targetLanguages);
		commentColumns = ListUtils.stringAsIntList(params.commentColumns);
		targetSourceRefs = ListUtils.stringAsIntList(params.targetSourceRefs);
		commentSourceRefs = ListUtils.stringAsIntList(params.commentSourceRefs);
		sourceIdSourceRefs = ListUtils.stringAsIntList(params.sourceIdSourceRefs);
		sourceIdSuffixes = ListUtils.stringAsList(params.sourceIdSuffixes);
										
		sendListedMode = params.sendColumnsMode == Parameters.SEND_COLUMNS_LISTED;
		
		if (params.trimMode != Parameters.TRIM_NONE) {
			
			params.trimLeading = true;
			params.trimTrailing = true;
		}
		
		rowNumber = 0;
		
		if (columnNames != null) 
			columnNames.clear();
		else
			columnNames = new ArrayList<String>();
		
		inMultilineColumnNames = false;
	}

	@Override
	protected TextProcessingResult filter_exec(TextContainer lineContainer) {		
		
		if (lineContainer == null) return TextProcessingResult.REJECTED;
				
		Property lineNumProp = lineContainer.getProperty(AbstractLineFilter.LINE_NUMBER);
		long lineNum = new Long(lineNumProp.getValue());

		updateLineInfo(lineNum);
		
		if (inHeaderArea && params.sendHeaderMode == Parameters.SEND_HEADER_NONE)  
			return TextProcessingResult.REJECTED;
		
		if (inHeaderArea && !isColumnNames && params.sendHeaderMode == Parameters.SEND_HEADER_COLUMN_NAMES_ONLY) 
			return TextProcessingResult.REJECTED;
		
		if (inHeaderArea)			
			rowNumber = 0;
		else {			
			if (rowNumber <= 0)
				rowNumber = 1;
			else
				rowNumber++;
		}

		// Send regular header lines (not column names) as a whole
		if (isHeaderLine) {
			
			lineContainer.setProperty(new Property(ROW_NUMBER, String.valueOf(rowNumber), true));  // rowNumber = 0 for header rows
			return super.sendContent(lineContainer);
		}
		
		List<String> cells = new ArrayList<String>(); 
		TextProcessingResult res = extractCells(cells, lineContainer.getCodedText(), lineNum);
		
		switch (res) {
			case REJECTED:
				return res;
				
			case DELAYED_DECISION:
				if (isColumnNames) inMultilineColumnNames = true;
				return res;
		}
		
		// res = ACCEPTED
			
		if (isColumnNames) inMultilineColumnNames = false;
		if (Util.isEmpty(cells)) return super.sendContent(lineContainer); // No chunks, process the whole line
			
		if (processCells(cells, lineNum))
			return TextProcessingResult.ACCEPTED;
		else 
			return TextProcessingResult.REJECTED;
	}
	
	/**
	 * Splits line into table cells. 
	 * @param line string containing separated cells
	 * @return string array of cells
	 */
	protected TextProcessingResult extractCells(List<String> cells, String line, long lineNum) {		
		// To be overridden in descendant classes
		
		if (cells != null) cells.add(line);
		
		return TextProcessingResult.ACCEPTED; 
	}

	protected boolean processCells(List<String> cells, long lineNum) {
		// Processes cells of one line
		// To be called from descendants, least likely overridden

		if (params.sendColumnsMode == Parameters.SEND_COLUMNS_NONE) return false;
		if (cells == null) return false;		
		
		updateLineInfo(lineNum);
		
		// If a fixed number of columns is expected, truncate extra chunks, or pad with empty chunks for missing
		if (isFixedNumColumns) {
						
			if (cells.size() < params.numColumns)
				for (int i = cells.size(); i < params.numColumns; i++)
					cells.add("");

			if (cells.size() > params.numColumns)
				cells.subList(params.numColumns, cells.size()).clear();
		}
								
		if (isColumnNames) {
			
			ListUtils.listTrimRight(cells);
			
			columnNames.clear();			
			columnNames.addAll(ListUtils.listTrimValues(cells));
			
			if (params.detectColumnsMode == Parameters.DETECT_COLUMNS_COL_NAMES)
				params.numColumns = cells.size();
		}
				
		boolean tuSent = false;
		int startGroupIndex = getQueueSize();
		
		// Send all cells
		if (params.sendColumnsMode == Parameters.SEND_COLUMNS_ALL || inHeaderArea) {
			
			for (int i = 0; i < cells.size(); i++)	{
								
				String cell = cells.get(i);
				int colNumber = i + 1;
				
				//if (Util.isEmpty(cell, true)) continue;
				if (Util.isEmpty(cell, true)) {
					
					sendSkeletonCell(cell, getActiveSkeleton(), colNumber, cells.size());
					continue;
				}					
								
				TextUnit tu = new TextUnit("", cell);
				if (tu == null) continue;

				tu.setSourceProperty(new Property(AbstractLineFilter.LINE_NUMBER, String.valueOf(lineNum), true));				
				tu.setSourceProperty(new Property(COLUMN_NUMBER, String.valueOf(colNumber), true));
				tu.setSourceProperty(new Property(ROW_NUMBER, String.valueOf(rowNumber), true));  // rowNumber = 0 for header rows
				
				if (!sendCell(tu, colNumber, cells.size())) continue;
				tuSent = true;
			}					
		}
		// Send only listed cells (id, source, target, comment)
		else if (sendListedMode) {
		
			if (tuCache == null) return false;			
			tuCache.clear();
			
			// Create text units for source cells			
			for (int i = 0; i < cells.size(); i++)	{
				
				String cell = cells.get(i);
				if (Util.isEmpty(cell, true)) {
					
					tuCache.add(null);
					continue;
				}
			
				int colNumber = i + 1;
				if (isSource(colNumber)) {
			
					TextUnit tu = new TextUnit("", cell);
					tuCache.add(tu);
					
					if (tu == null) continue;

					tu.setSourceProperty(new Property(AbstractLineFilter.LINE_NUMBER, String.valueOf(lineNum), true));				
					tu.setSourceProperty(new Property(COLUMN_NUMBER, String.valueOf(colNumber), true));
					tu.setSourceProperty(new Property(ROW_NUMBER, String.valueOf(rowNumber), true));  // rowNumber = 0 for header rows
				}
				else
					tuCache.add(null);
			}
					
			// Add content of other columns to the created sources
			for (int i = 0; i < cells.size(); i++)	{
				
				String cell = cells.get(i); // Can be empty				
				String trimmedCell = Util.trim(cell);
				
				int colNumber = i + 1;
				boolean isRecognized = false;
				
				if (isSourceId(colNumber)) {
					
					isRecognized = true;
					sendSkeletonCell(cell, getActiveSkeleton(), colNumber, cells.size());
					
					TextUnit tu = getSourceFromIdRef(colNumber);
					if (tu == null) continue;										
					
					if (Util.isEmpty(cell, true)) {

						String recordID = ""; 
						int index = params.recordIdColumn - 1;
						
						if (Util.checkIndex(index, cells))
							recordID = cells.get(index);
						
						if (recordID != null) recordID = recordID.trim();
						
						String colSuffix = getSuffixFromSourceRef(colNumber);
						
						if (!Util.isEmpty(recordID) && !Util.isEmpty(colSuffix))
							tu.setName(recordID + colSuffix);
					}
					else
						tu.setName(trimmedCell);
															
					continue;
				}
				
				if (isTarget(colNumber)) {
					
					isRecognized = true;
					
					TextUnit tu = getSourceFromTargetRef(colNumber);
					if (tu == null) {
						
						sendSkeletonCell(cell, getActiveSkeleton(), colNumber, cells.size());
						continue;
					}
					
					String language = getLanguageFromTargetRef(colNumber);
					if (Util.isEmpty(language)) {
						
						sendSkeletonCell(cell, getActiveSkeleton(), colNumber, cells.size());
						continue;
					}
					
					TextContainer trg = new TextContainer(cell);					
					tu.setTarget(language, trg);
					
					// Do not add to the TU's skeleton as it might be sent before or later
					GenericSkeleton skel = new GenericSkeleton();
					
					TextUnitUtils.trimLeading(trg, skel);
					skel.addContentPlaceholder(tu, language);
					TextUnitUtils.trimTrailing(trg, skel);
					
					sendSkeletonPart(skel);										
					
					continue;
				}
				
				if (isComment(colNumber)) {
					
					isRecognized = true;
					sendSkeletonCell(cell, getActiveSkeleton(), colNumber, cells.size());
					
					TextUnit tu = getSourceFromCommentRef(colNumber);
					if (tu == null) continue;
					if (Util.isEmpty(trimmedCell)) continue;
					
					tu.setProperty(new Property(Property.NOTE, trimmedCell));
					
					continue;
				}
				
				if (isSource(colNumber)) {
					
					isRecognized = true;
					
					TextUnit tu = getFromTuCache(i);
					if (tu == null) continue;
					
					// After sending a TU it's still accessible for modifications (TU is not cloned for the queue), so no additional loop is needed
					if (!sendCell(tu, colNumber, cells.size())) {
						
						sendSkeletonCell(cell, getActiveSkeleton(), colNumber, cells.size());
						continue; 
					}
					tuSent = true;
					
					continue;
				}
				
				// Unknown type goes to the skeleton 
				if (!isRecognized)
					sendSkeletonCell(cell, getActiveSkeleton(), colNumber, cells.size());
			}
									
		}
		
		if (tuSent) {
			sendEvent(startGroupIndex, EventType.START_GROUP, new StartGroup(""));
			sendEvent(EventType.END_GROUP, new Ending(""));
		}
		
		return true;		
	}
	
	public List<String> getColumnNames() {
		
		if (columnNames == null)
			columnNames = new ArrayList<String>();
				
		return columnNames;
	}
	
	private boolean isSource(int colNumber) {return (sourceColumns == null) ? null : sourceColumns.contains(colNumber);}
	private boolean isSourceId(int colNumber) {return (sourceIdColumns == null) ? null : sourceIdColumns.contains(colNumber);}	
	private boolean isTarget(int colNumber) {return (targetColumns == null) ? null : targetColumns.contains(colNumber);}
	private boolean isComment(int colNumber) {return (commentColumns == null) ? null : commentColumns.contains(colNumber);}	

	private TextUnit getSource(int colNum, List<Integer> columnsList, List<Integer> refList) {
		
		if (columnsList == null) return null;		
		int index = columnsList.indexOf(colNum); 
		
		if (!Util.checkIndex(index, refList)) return null;
		int ref = refList.get(index);
		
		return getFromTuCache(--ref); // ref is 1-based
	}
	
	private TextUnit getSourceFromTargetRef(int colNum) {

		return getSource(colNum, targetColumns, targetSourceRefs);
	}
			
	private TextUnit getSourceFromIdRef(int colNum) {
		
		return getSource(colNum, sourceIdColumns, sourceIdSourceRefs);
	}
	
	private TextUnit getSourceFromCommentRef(int colNum) {
		
		return getSource(colNum, commentColumns, commentSourceRefs);
	}
	
	private TextUnit getFromTuCache(int cacheIndex) {
		
		if (!Util.checkIndex(cacheIndex, tuCache)) return null;
		
		return tuCache.get(cacheIndex);
	}
	
	private String getLanguageFromTargetRef(int colNum) {
		
		if (targetColumns == null) return "";		
		int index = targetColumns.indexOf(colNum); 
		
		if (!Util.checkIndex(index, targetLanguages)) return "";
		return targetLanguages.get(index);
	}
	
	private String getSuffixFromSourceRef(int colNum) {
		
		if (sourceIdColumns == null) return "";		
		int index = sourceIdColumns.indexOf(colNum);
		
		if (!Util.checkIndex(index, sourceIdSuffixes)) return "";
		return sourceIdSuffixes.get(index);
	}

	protected boolean sendCell(TextUnit tu, int column, int numColumns) {
		// Can be overridden in descendant classes
		
		return sendContent(tu) == TextProcessingResult.ACCEPTED;
	}

	protected void sendSkeletonCell(String cell, GenericSkeleton skel, int column, int numColumns) {
		
		if (skel == null) return;
		if (Util.isEmpty(cell)) return;
		
		skel.add(cell);
	}
	
	private void updateLineInfo(long lineNum) {
		inHeaderArea = lineNum < params.valuesStartLineNum;
		
		isColumnNames = 
			inHeaderArea && 
					(lineNum == params.columnNamesLineNum || 
					(lineNum > params.columnNamesLineNum && inMultilineColumnNames));
		
		isHeaderLine = inHeaderArea && !isColumnNames;
		
		isFixedNumColumns = 		
			(params.detectColumnsMode == Parameters.DETECT_COLUMNS_FIXED_NUMBER && params.numColumns > 0) ||
			(params.detectColumnsMode == Parameters.DETECT_COLUMNS_COL_NAMES && !inHeaderArea);
	}
}
