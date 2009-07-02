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

package net.sf.okapi.ui.common.editors;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.utils.ListUtils;
import net.sf.okapi.common.utils.Util2;
import net.sf.okapi.ui.common.utils.SWTUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * A wrapper around SWT table for manipulations with table data
 * 
 * @version 0.1 30.06.2009
 * @author Sergei Vasilyev
 */

public class TableAdapter {

	public static final int DUPLICATE_ALLOW = 1;
	public static final int DUPLICATE_REJECT = 2;
	public static final int DUPLICATE_REPLACE = 3;
	
	Table table;
	double[] columnPoints = null;
	TableItem saveSelItem = null;
	
	public TableAdapter(Table table) {
		super();
		
		this.table = table;
	}

	public void updateColumnWidths(boolean blockRedraw) {
		
		if (columnPoints == null) return;
		
		float pointsWidth = 0;
		
		for (int i = 0; i < table.getColumnCount(); i++)
			pointsWidth += ((i < columnPoints.length - 1) ? columnPoints[i]: 1);
			
		float coeff = table.getClientArea().width / pointsWidth;
		
		if (blockRedraw) table.setRedraw(false);

		try {
			for (int i = 0; i < table.getColumnCount(); i++)
				table.getColumn(i).setWidth((int)(((i < columnPoints.length - 1) ? columnPoints[i]: 1) * coeff));		
		}
		
		finally {				
			if (blockRedraw) table.setRedraw(true);
		}
	}

	/**
	 * @param columnPoints
	 */
	public void setRelColumnWidths(double[] columnPoints) {
		
		this.columnPoints = columnPoints;
		
		table.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				updateColumnWidths(false);
			}			
		});
	}

	public void addRow(String[] rowData) {
		
		TableItem item = new TableItem (table, SWT.NONE);
		item.setText(rowData);	
		table.select(table.indexOf(item));
	}
	
	public void addRow(int intValue, int columnNumber) {
		
		addRows(Util2.intToStr(intValue), columnNumber);
	}
	
	public void modifyRow(TableItem item, String[] rowData) {
		
		if (item == null) return;
		
		item.setText(rowData);
		table.select(table.indexOf(item));
	}
	
	/**
	 * 
	 * @param rowData
	 * @param keyColNumber
	 * @param dupMode
	 */
	public void addModifyRow(String[] rowData, int keyColNumber, int dupMode) {
		
		addModifyRow(null, rowData, keyColNumber, dupMode);
	}
	
	/**
	 * 
	 * @param item
	 * @param rowData
	 * @param keyColNumber
	 * @param dupMode
	 */	
	public void addModifyRow(TableItem item, String[] rowData, int keyColNumber, int dupMode) {
				
		
		String st = Util2.get(rowData, keyColNumber - 1);
		
		TableItem item2 = findValue(st, keyColNumber);
		
		if (item2 != null) { // Already exists
		
			switch (dupMode) {
			
			case DUPLICATE_ALLOW:
				
				addRow(rowData);
				break;
				
			case DUPLICATE_REJECT:
				
				break;
				
			case DUPLICATE_REPLACE:
				
				modifyRow(item2, rowData);
				break;
			}
		}
		else
			addRow(rowData);
		
		// table.select(table.indexOf(item));
	}
	
	/**
	 * 
	 */
	public void unselect() {
		
		storeSelection();
		table.setSelection(-1);
	}

//	/**
//	 * If the current row exists, replaces its data with the given rowData, otherwise creates a new row and fills it up with rowData.
//	 * @param keyColNumber -- number (1-based) of the column which value should not be duplicated
//	 */
//	public void addModifyCurRow(String[] rowData, int keyColNumber, boolean allowReplace) {
//		
//		String st = 
//		//if (!valueExists(st, columnNumber));
//	}
//	
//	/**
//	 * If the given row exists, replaces its data with the given rowData, otherwise creates a new row and fills it up with rowData.
//	 * @param keyColNumber -- number (1-based) of the column which value should not be duplicated
//	 */
//	public void addModifyRow(TableItem item, String[] rowData, int keyColNumber, boolean allowReplace) {		
//		
//		
//	}

	/**
	 * @param values
	 * @param i
	 */
	public void addRows(String values, int columnNumber) {
		
		List<String> valList = ListUtils.stringAsList(values);
		
		for (String st : valList) {	

			if (Util.isEmpty(st)) continue;
			addModifyRow(new String[] {st}, columnNumber, DUPLICATE_REJECT);
		}
	}
	
	public TableItem findValue(String value, int columnNumber) {
		
		if (Util.isEmpty(value)) return null;
		
		for (TableItem item : table.getItems()) {
			
			if (item == null) continue;
			
			if (value.equalsIgnoreCase(item.getText(columnNumber - 1)))
				return item;
		}
		
		return null;
	}
	
	public boolean valueExists(String value, int columnNumber) {
	
		return findValue(value, columnNumber) != null;
	}
		
    // String Comparator
	private int colIndex = 0;
    private boolean ascending = true;
    
//	private Collator col = Collator.getInstance(Locale.getDefault());
	
    private Comparator<Object> strComparator = new Comparator<Object>()
    {
        public int compare(Object arg0, Object arg1) {

            TableItem t1 = (TableItem)arg0;
            TableItem t2 = (TableItem)arg1;

            int v1 = Util2.strToInt(t1.getText(colIndex), 0);
            int v2 = Util2.strToInt(t2.getText(colIndex), 0);

            return ((ascending && (v1 > v2)) || (!ascending && (v1 < v2)) ? 1: -1);
        }    
    };

    private String[] getData(TableItem t)
    {
        Table table = t.getParent();
        
        int colCount = table.getColumnCount();
        String [] s = new String[colCount];
        
        for (int i = 0; i < colCount;i++)
            s[i] = t.getText(i);
                
        return s;
        
    }
	
	public void sort(int sortColNum, boolean ascending) {
		
		if (sortColNum == 0) return;

		//ArrayList<TableItem> items = (ArrayList<TableItem>) Arrays.asList(table.getItems());
		
		TableItem[] items = table.getItems();
		colIndex = sortColNum - 1;
		this.ascending = ascending;
		
		storeSelection();		
		table.setRedraw(false);

		try{
			Arrays.sort(items, strComparator);
			
	        for (int i = 0; i < items.length; i++)
	        {   
	        	TableItem item = new TableItem(table, SWT.NONE, i);
	            item.setText(getData(items[i]));
	            
	            if (saveSelItem != null && saveSelItem.equals(items[i]))
	            		saveSelItem = item;
	            
	            items[i].dispose();
	        }
		} 
		finally {			
			
			table.setRedraw(true);
			restoreSelection();
		}        
	}
	
	public void sort(int sortColNum) {
		
		sort(sortColNum, true);
	}
	
	public void sort(TableColumn sortColumn) {
		
		sort(SWTUtils.getColumnIndex(sortColumn) + 1, true);
	}

	/**
	 * 
	 */
	public boolean removeSelected() {
		
		int index = table.getSelectionIndex();
		if (index == -1) return false;
		
		table.remove(index);
		
		if (index > table.getItemCount() - 1) index = table.getItemCount() - 1;
		if (index > -1)	table.select(index);
		
		return true;
	}

	public void storeSelection() {
		
		if (table.getSelection().length > 0)
			saveSelItem = table.getSelection()[0];
		else
			saveSelItem = null;
	}
	
	public void restoreSelection() {
		
		if (saveSelItem == null)
			table.select(-1);
		else
			table.setSelection(saveSelItem);
	}

	/**
	 * 
	 * @param item
	 * @param colNum
	 * @param value
	 */	
	public void setValue(TableItem item, int colNum, String value) {
		
		if (item == null) return;
		
		item.setText(colNum - 1, value);
	}
	
	public void setValue(int rowNum, int colNum, String value) {
		
		if (!SWTUtils.checkRowIndex(table, rowNum - 1)) return;
		
		TableItem item = table.getItem(rowNum - 1);
		if (item == null) return;
		
		item.setText(colNum - 1, value);
	}

	public int getNumRows() {
		
		return table.getItemCount();
	}
	
	public int getNumColumns() {
		
		return table.getColumnCount();
	}

	public String getValue(int rowNum, int colNum) {
		
		if (!SWTUtils.checkRowIndex(table, rowNum - 1)) return "";
		TableItem item = table.getItem(rowNum - 1);
		if (item == null) return "";
			
		return item.getText(colNum - 1);
	}

	/**
	 * 
	 */
	public void clear() {
		
		table.removeAll();
	}
	
}
