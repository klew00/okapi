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

import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.filters.table.Parameters;
import net.sf.okapi.filters.table.csv.CommaSeparatedValuesFilter;
import net.sf.okapi.filters.table.fwc.FixedWidthColumnsFilter;
import net.sf.okapi.filters.table.tsv.TabSeparatedValuesFilter;
import net.sf.okapi.lib.extra.filters.CompoundFilter;

/**
 * Table filter, processes table-like files such as tab-delimited, CSV, fixed-width columns, etc.
 * 
 * @version 0.1, 09.06.2009
 */
@UsingParameters(Parameters.class)
public class TableFilter extends CompoundFilter {
		
	public static final String FILTER_NAME		= "okf_table";
	public static final String FILTER_MIME		= MimeTypeMapper.CSV_MIME_TYPE;	
	public static final String FILTER_CONFIG	= "okf_table";
	
	public TableFilter() {
		
		super();
		
		setName(FILTER_NAME);
		setDisplayName("Table Filter");
		setMimeType(FILTER_MIME);
		setParameters(new Parameters());	// Table Filter parameters

		addConfiguration(true, 
				FILTER_CONFIG,
				"Table Files",
				"Table-like files such as tab-delimited, CSV, fixed-width columns, etc.", 
				null);
	
		addSubFilter(CommaSeparatedValuesFilter.class);
		addSubFilter(FixedWidthColumnsFilter.class);
		addSubFilter(TabSeparatedValuesFilter.class);
	}
		
}
