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

package net.sf.okapi.tm.simpletm;

import java.util.LinkedHashMap;
import java.util.List;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.ITMQuery;
import net.sf.okapi.lib.translation.QueryResult;

public class SimpleTMConnector implements ITMQuery {
	
	private Database db;
	private int maxHits = 5;
	private int threshold = 98;
	private List<QueryResult> results;
	private int current = -1;
	private String srcLang;
	private String trgLang;
	private LinkedHashMap<String, String> attributes;
	private Parameters params;

	public SimpleTMConnector () {
		params = new Parameters();
		db = new Database();
		attributes = new LinkedHashMap<String, String>();
	}
	
	public String getName () {
		return "SimpleTM";
	}

	public void setMaximumHits (int max) {
		if ( max < 1 ) maxHits = 1;
		else maxHits = max;
	}

	public void setThreshold (int threshold) {
		this.threshold = threshold;
	}

	public void close() {
		db.close();
	}

	public boolean hasNext () {
		if ( results == null ) return false;
		if ( current >= results.size() ) {
			current = -1;
		}
		return (current > -1);
	}
	
	public QueryResult next () {
		if ( results == null ) return null;
		if (( current > -1 ) && ( current < results.size() )) {
			current++;
			return results.get(current-1);
		}
		current = -1;
		return null;
	}

	public void open () {
		db.open(params.dbPath);
	}

	public int query (String plainText) {
		TextFragment tf = new TextFragment(plainText);
		return query(tf);
	}
	
	public int query (TextFragment text) {
		current = -1;
		results = db.query(text, attributes, maxHits, threshold);
		if ( results == null ) return 0;
		current = 0;
		return results.size();
	}
	
	public void setAttribute (String name,
		String value)
	{
		assert(value!=null);
		if ( "resname".equals(name) ) name = Database.NNAME;
		if ( "restype".equals(name) ) name = Database.NTYPE;
		if ( attributes.put(name, value) == null ) {
			// Update the query if this attribute did not exist yet
			db.setAttributes(attributes);
		}
	}
	
	public void removeAttribute (String name) {
		if ( attributes.containsKey(name) ) {
			attributes.remove(name);
			db.setAttributes(attributes);
		}
	}

	public void setLanguages (String sourceLang,
		String targetLang)
	{
		srcLang = sourceLang;
		trgLang = targetLang;
	}

	public String getSourceLanguage () {
		return srcLang;
	}
	
	public String getTargetLanguage () {
		return trgLang;
	}

	public void export (String outputPath) {
		db.exportToTMX(outputPath, srcLang, trgLang);
	}

	public int getMaximunHits () {
		return maxHits;
	}

	public int getThreshold () {
		return threshold;
	}

	public IParameters getParameters() {
		return params;
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

}
