/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.lib.tmdb;

import java.sql.ResultSet;

import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.lib.tmdb.DbUtil.PageMode;

public class Exporter implements Runnable {

	private final IProgressCallback callback;
	private final ITm tm;
	private String path;

	public Exporter (IProgressCallback callback,
		ITm tm,
		String path)
	{
		this.callback = callback;
		this.tm = tm;
		this.path = path;
	}

	@Override
	public void run () {
		long count = 0;
		XMLWriter writer = null;
		try {
			
			callback.startProcess("Exporting "+path+"...");
			
//			List<String> localeCodes = tm.getLocales();
			
			// Export all fields
			tm.setRecordFields(tm.getAvailableFields());
			tm.setPageMode(PageMode.ITERATOR);
			

			writer = new XMLWriter(path);
			writer.writeStartDocument();
			writer.writeStartElement("body");
			
			ResultSet rs = tm.getFirstPage();
			while  ( rs != null ) {
				while ( rs.next() ) {
					
					writer.writeElementString("x-SegKey",
						String.valueOf(rs.getLong(ITm.SEGKEY_FIELD)));
				}
				rs = tm.getNextPage();
			}
			
			
		}
		catch ( Throwable e ) {
			callback.logMessage(3, e.getMessage());
		}
		finally {
			if ( writer != null ) {
				writer.writeEndElement(); // body
				writer.writeEndDocument();
				writer.close();
			}
			callback.endProcess(count);
		}
	}

}
