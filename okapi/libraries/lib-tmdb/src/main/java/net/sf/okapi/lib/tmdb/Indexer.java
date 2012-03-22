/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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
import java.util.LinkedHashMap;
import java.util.List;

import net.sf.okapi.lib.tmdb.IProgressCallback;
import net.sf.okapi.lib.tmdb.ITm;
import net.sf.okapi.lib.tmdb.DbUtil.PageMode;

public class Indexer implements Runnable {

	private final IProgressCallback callback;
	private final IRepository repo;
	private final String tmName;
	
	public Indexer (IProgressCallback progressCallback,
		IRepository repo,
		String tmName)
	{
		this.callback = progressCallback;
		this.repo = repo;
		this.tmName = tmName;
	}
	
	@Override
	public void run () {
		long totalCount = 0;
		ITm tm = null;
		boolean canceled = false;
		IIndexAccess ia = null;
		
		try {
			callback.startProcess("Indexing TM...");
			
			//=== Split entries
			
			// Get the original TM and set it for iteration
			tm = repo.openTm(tmName);
			tm.setRecordFields(tm.getAvailableFields());
			tm.setPageMode(PageMode.ITERATOR);

			ia = repo.getIndexAccess();
			
			IRecordSet rs = tm.getFirstPage();
			while  (( rs != null ) && !canceled ) {
				while ( rs.next() && !canceled ) {
					totalCount++;
			
					//TODO
					
					
					// Update UI from time to time
					if ( (totalCount % 652) == 0 ) {
						// And check for cancellation
						if ( !callback.updateProgress(totalCount) ) {
							if ( !canceled ) {
								callback.logMessage(1, "Process interrupted by user.");
								canceled = true;
							}
						}
					}
				}
				if ( !canceled ) {
					rs = tm.getNextPage();
				}
			}
			
		}
		catch ( Throwable e ) {
			callback.logMessage(IProgressCallback.MSGTYPE_ERROR, e.getMessage());
		}
		finally {
			if ( ia != null ) ia.close();
			callback.endProcess(totalCount, true);
		}
	}

}
