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

package net.sf.okapi.applications.olifant;

import java.util.LinkedHashMap;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.lib.tmdb.DbUtil;
import net.sf.okapi.lib.tmdb.ITm;

public class Importer extends ObservableRunnable {

	private final IFilterConfigurationMapper fcMapper;
	private final RawDocument rd;
	private final ITm tm;
	private final LogPanel logPanel;
	private final DbUtil dbUtil = new DbUtil();
	
	public Importer (IFilterConfigurationMapper fcMapper,
		ITm tm,
		RawDocument rd,
		LogPanel logPanel)
	{
		this.fcMapper = fcMapper;
		this.tm = tm;
		this.rd = rd;
		this.logPanel = logPanel;
	}
	
	public ITm process () {
		long count = 0;
		IFilter filter = null;
		try {
			updateUI(0, 0, null);
			filter = fcMapper.createFilter(rd.getFilterConfigId());
			filter.open(rd);
	
			LinkedHashMap<String, Object> mapTUProp = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, Object> mapSrcProp = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, Object> mapTrgProp = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			String[] trgFields;
			String srcDbLang = DbUtil.toOlifantLocaleCode(rd.getSourceLocale());

//TODO: implement check for duplicates
//TODO: implement filter for fields
			tm.startImport();
			while ( filter.hasNext() ) {
				Event event = filter.next();
				if ( !event.isTextUnit() ) continue;
				
				ITextUnit tu = event.getTextUnit();
				ISegments srcSegs = tu.getSourceSegments();
				
				// Get text-unit level properties
				mapTUProp.clear();
				for ( String name : tu.getPropertyNames() ) {
					mapTUProp.put(DbUtil.checkFieldName(name), tu.getProperty(name).getValue());
				}
				
				// Get source container properties
				mapSrcProp.clear();
				for ( String name : tu.getSourcePropertyNames() ) {
					if ( name.equals("lang") ) continue;
					mapSrcProp.put(DbUtil.checkFieldName(name)+DbUtil.LOC_SEP+srcDbLang, tu.getSourceProperty(name).getValue());
				}
	
				// For each source segment
				for ( net.sf.okapi.common.resource.Segment srcSeg : srcSegs ) {
	
					// Get the source fields
					String[] srcFields = dbUtil.fragmentToTmFields(srcSeg.getContent());
					map.clear();
					map.put(DbUtil.TEXT_PREFIX+srcDbLang, srcFields[0]);
					map.put(DbUtil.CODES_PREFIX+srcDbLang, srcFields[1]);
					long tuKey = -1;
	
					// For each target
					for ( LocaleId locId : tu.getTargetLocales() ) {
						String trgDbLang = DbUtil.toOlifantLocaleCode(locId);
						
						mapTrgProp.clear();
						for ( String name : tu.getTargetPropertyNames(locId) ) {
							if ( name.equals("lang") ) continue;
							mapTrgProp.put(DbUtil.checkFieldName(name)+DbUtil.LOC_SEP+trgDbLang, tu.getTargetProperty(locId, name).getValue());
						}
						
						// Get the target segment
						net.sf.okapi.common.resource.Segment trgSeg = tu.getTargetSegments(locId).get(srcSeg.getId());
						if ( trgSeg != null ) {
							trgFields = dbUtil.fragmentToTmFields(trgSeg.getContent());
						}
						else {
							trgFields = new String[2];
						}
						map.put(DbUtil.TEXT_PREFIX+trgDbLang, trgFields[0]);
						map.put(DbUtil.CODES_PREFIX+trgDbLang, trgFields[1]);
					}
					// Add the record to the database
					map.putAll(mapSrcProp);
					map.putAll(mapTrgProp);
					tuKey = tm.addRecord(tuKey, mapTUProp, map);
					// Update UI from time to time
		//TODO: check for cancellation!!
					if ( (++count % 152) == 0 ) {
						updateUI(count, 1, null);
					}
				}
			}
			// Final update (includes notifying the observers that we are done)
			tm.finishImport();
			updateUI(count, 2, null);
		}
		catch ( Throwable e ) {
			updateUI(count, 3, e.getMessage());
		}
		finally {
			tm.finishImport();
			if ( filter != null ) {
				filter.close();
			}
		}
		return tm;
	}

	private void updateUI (long p_count,
		int p_state,
		String p_text)
	{
		final long count = p_count;
		final int state = p_state;
		final String text = p_text;
		logPanel.getDisplay().asyncExec(new Runnable() {
			public void run () {
				switch ( state ) {
				case 0:
					logPanel.startTask("Importing "+rd.getInputURI().getPath()+"...");
					break;
				case 1:
					logPanel.setInfo(String.valueOf(count));
					break;
				case 3: // Error
					logPanel.log("ERROR: "+text);
					// Done too. Fall through
				case 2: // Done
					logPanel.endTask(String.format("Entries processed: %d", count));
					notifyObservers();
					break;
				}
			}
		});		
	}

	@Override
	public void run () {
		process();
	}
}
