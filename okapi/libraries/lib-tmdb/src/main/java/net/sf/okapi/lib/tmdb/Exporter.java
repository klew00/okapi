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
import java.sql.SQLException;
import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.TMXWriter;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.tmdb.DbUtil.PageMode;

public class Exporter implements Runnable {

	private final IProgressCallback callback;
	private String tmName;
	private final IRepository repo;
	private final DbUtil util;
	private String path;
	private List<String> locales;
	private List<String> availableFields;

	public Exporter (IProgressCallback callback,
		IRepository repo,
		String tmName,
		String path)
	{
		util = new DbUtil();
		this.callback = callback;
		this.repo = repo;
		this.path = path;
		this.tmName = tmName;
	}

	@Override
	public void run () {
		long count = 0;
		TMXWriter writer = null;
		ITm tm = null;
		try {
			callback.startProcess("Exporting "+path+"...");
			
			tm = repo.openTm(tmName);
			availableFields = tm.getAvailableFields();
			tm.setRecordFields(availableFields);
			tm.setPageMode(PageMode.ITERATOR);
//temporary			
			locales = tm.getLocales();
			LocaleId srcLoc = DbUtil.fromOlifantLocaleCode(locales.get(0));
			LocaleId trgLoc = srcLoc;
			
			writer = new TMXWriter(path);
			writer.writeStartDocument(srcLoc, trgLoc, getClass().getCanonicalName(), "1", null, null, null);
			boolean canceled = false;
			
			ResultSet rs = tm.getFirstPage();
			while  (( rs != null ) && !canceled ) {
				while ( rs.next() && !canceled ) {
					count++;
					TextUnit tu = toTextUnit(rs, srcLoc);
					writer.writeTUFull(tu);
					// Update UI from time to time
					if ( (count % 652) == 0 ) {
						// And check for cancellation
						if ( !callback.updateProgress(count) ) {
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
			if ( writer != null ) {
				writer.writeEndDocument();
				writer.close();
			}
			callback.endProcess(count, false);
		}
	}

	private TextUnit toTextUnit (ResultSet rs, LocaleId srcLoc)
		throws SQLException 
	{
		TextUnit tu = new TextUnit(rs.getString(DbUtil.SEGKEY_NAME));
		String srcDbLoc = DbUtil.toOlifantLocaleCode(srcLoc);

		// Source
		String codesFld;
		String textFld = rs.getString(DbUtil.TEXT_PREFIX + srcDbLoc);
		if ( textFld != null ) { 
			codesFld = rs.getString(DbUtil.CODES_PREFIX + srcDbLoc);
			tu.setSourceContent(util.tmFieldsToFragment(textFld, codesFld));
		}
		
		// Targets
		for ( String loc : locales ) {
			if ( loc.equals(srcDbLoc) ) continue; // Skip the source
			textFld = rs.getString(DbUtil.TEXT_PREFIX + loc);
			if ( textFld == null ) continue; // Skip non-existing target
			codesFld = rs.getString(DbUtil.CODES_PREFIX + loc);
			tu.setTargetContent(DbUtil.fromOlifantLocaleCode(loc),
				util.tmFieldsToFragment(textFld, codesFld));
		}

		// Properties
		for ( String fn : availableFields ) {
			if ( DbUtil.isSegmentField(fn) ) {
				if ( fn.equals(DbUtil.TUREF_NAME) ) {
					// Do nothing
				}
				else if ( fn.equals(DbUtil.SEGKEY_NAME) ) {
					// Do nothing
				}
				else if ( fn.startsWith(DbUtil.CODES_PREFIX) || fn.startsWith(DbUtil.TEXT_PREFIX) ) {
					// Do nothing
				}
				else {
					String loc = DbUtil.getFieldLocale(fn);
					String rn = DbUtil.getFieldRoot(fn);
					if ( loc.equals(srcDbLoc) ) {
						tu.setSourceProperty(new Property(rn, rs.getString(fn)));
					}
					else {
						tu.setTargetProperty(DbUtil.fromOlifantLocaleCode(loc),
							new Property(rn, rs.getString(fn)));
					}
				}
			}
			else {
				if ( fn.equals("tuid") ) {
					tu.setName(rs.getString(fn));
				}
				else {
					tu.setProperty(new Property(fn, rs.getString(fn)));
				}
			}
		}
		
		// Done
		return tu;
	}
	
}
