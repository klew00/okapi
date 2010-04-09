/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

package net.sf.okapi.connectors.pensieve;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.ITMQuery;
import net.sf.okapi.lib.translation.QueryResult;
import net.sf.okapi.tm.pensieve.common.Metadata;
import net.sf.okapi.tm.pensieve.common.MetadataType;
import net.sf.okapi.tm.pensieve.common.TmHit;
import net.sf.okapi.tm.pensieve.seeker.ITmSeeker;
import net.sf.okapi.tm.pensieve.seeker.TmSeekerFactory;

import java.util.ArrayList;
import java.util.List;

public class PensieveTMConnector implements ITMQuery {

	private int maxHits = 25;
	private int threshold = 95;
	private List<QueryResult> results;
	private int current = -1;
	private LocaleId srcLoc;
	private LocaleId trgLoc;
	private Parameters params;
	private ITmSeeker seeker;
	private Metadata attrs;
	private String rootDir;
	
	public PensieveTMConnector () {
		params = new Parameters();
		attrs = new Metadata();
	}

	@Override
	public String getName() {
		return "Pensieve TM";
	}

	@Override
	public String getSettingsDisplay () {
		return "Database: " + (Util.isEmpty(params.getDbDirectory())
			? "<To be specified>"
			: params.getDbDirectory());
	}

	@Override
	public void setMaximumHits (int max) {
		if ( max < 1 ) {
			maxHits = 1;
		} 
		else {
			maxHits = max;
		}
	}

	@Override
	public void setThreshold (int threshold) {
		this.threshold = threshold;
	}

	@Override
	public void close () {
		seeker.close();
	}

	@Override
	public boolean hasNext () {
		if ( results == null ) {
			return false;
		}
		if ( current >= results.size() ) {
			current = -1;
		}
		return (current > -1);
	}

	@Override
	public QueryResult next () {
		if ( results == null ) {
			return null;
		}
		if (( current > -1 ) && ( current < results.size() )) {
			current++;
			return results.get(current-1);
		}
		current = -1;
		return null;
	}

	@Override
	public void open () {
		// Create a seeker (the TM must exist: we are just querying)
		seeker = TmSeekerFactory.createFileBasedTmSeeker(
			Util.fillRootDirectoryVariable(params.getDbDirectory(), rootDir));
	}

	@Override
	public int query (String plainText) {
		TextFragment tf = new TextFragment(plainText);
		return query(tf);
	}

	@Override
	public int query (TextFragment text) {
		results = new ArrayList<QueryResult>();
		current = -1;
		List<TmHit> list;
		if ( threshold >= 100 ) { 
			list = seeker.searchExact(text, attrs);
		}
		else {
			list = seeker.searchFuzzy(text, threshold, maxHits, attrs);
		}

		// Convert to normalized results
		for ( TmHit hit : list ) {
			Float f = hit.getScore();
			QueryResult qr = new QueryResult();
			qr.score = f.intValue();
			qr.source = hit.getTu().getSource().getContent();
			qr.target = hit.getTu().getTarget().getContent();
			results.add(qr);
		}
		if ( results.size() > 0 ) {
			current = 0;
		}
		return results.size();
	}

	@Override
	public void setAttribute (String name,
		String value)
	{
		if ( "resname".equals(name) ) {
			attrs.put(MetadataType.ID, value);
		}
		else if ( "restype".equals(name) ) {
			attrs.put(MetadataType.TYPE, value);
		}
		else if ( "GroupName".equals(name) ) {
			attrs.put(MetadataType.GROUP_NAME, value);
		}
		else if ( "FileName".equals(name) ) {
			attrs.put(MetadataType.FILE_NAME, value);
		}
	}

	@Override
	public void clearAttributes () {
		attrs.clear();
	}

	@Override
	public void removeAttribute (String name) {
		if ( "resname".equals(name) ) {
			attrs.remove(MetadataType.ID);
		}
		else if ( "restype".equals(name) ) {
			attrs.remove(MetadataType.TYPE);
		}
		else if ( "GroupName".equals(name) ) {
			attrs.remove(MetadataType.GROUP_NAME);
		}
		else if ( "FileName".equals(name) ) {
			attrs.remove(MetadataType.FILE_NAME);
		}
	}

	@Override
	public void setLanguages (LocaleId sourceLocale,
		LocaleId targetLocale)
	{
		srcLoc = sourceLocale;
		trgLoc = targetLocale;
	}

	@Override
	public LocaleId getSourceLanguage () {
		return srcLoc;
	}

	@Override
	public LocaleId getTargetLanguage () {
		return trgLoc;
	}

	@Override
	public int getMaximumHits () {
		return maxHits;
	}

	@Override
	public int getThreshold () {
		return threshold;
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	@Override
	public void setRootDirectory (String rootDir) {
		this.rootDir = rootDir;
	}
}
