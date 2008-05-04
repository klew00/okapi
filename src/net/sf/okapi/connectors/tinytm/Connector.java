/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.connectors.tinytm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Translation.IMatch;
import net.sf.okapi.Translation.ITMQuery;
import net.sf.okapi.Translation.Match;

public class Connector implements ITMQuery {

	private Connection            conn = null;
	private boolean               isTMOpened = false;
	private ILog                  log;
	private String                server;
	private String                username;
	private String                password;
	private int                   currentMatch = -1;
	private Vector<Match>         hits = new Vector<Match>();
	private int                   maxHits = 20;
	private PreparedStatement     prepStm;
	private String                sourceLang;
	private String                targetLang;
	
	public Connector (ILog log) {
		this.log = log;
	}
	
	protected void finalize ()
		throws Throwable
	{
		try {
			logout();
		} finally {
			super.finalize();
		}
	}

	public void close () {
		try {
			if ( conn != null ) {
				if ( prepStm != null ) {
					try { prepStm.close(); } catch ( Exception E ) {};
					prepStm = null;
				}
				conn.close();
				conn = null;
				isTMOpened = false;
			}
			if ( prepStm != null ) {
				prepStm.close();
				prepStm = null;
			}
		}
		catch ( Exception E ) {
			log.error(E.getLocalizedMessage());
		}
	}

	public int getCount() {
		return hits.size();
	}

	public int getMaximum () {
		return maxHits;
	}

	public IMatch getNextMatch() {
		if ( currentMatch+1 >= hits.size() ) return null;
		return hits.get(++currentMatch);
	}

	public boolean isTMOpened () {
		return isTMOpened;
	}

	// connection = URL of the server + port 
	// Example: "www.tinytm.org:5432" or "www.tinytm.org"
	public void login (String connection,
		String username,
		String password) {
		try {
			logout();
			this.server = connection; 
			this.username = username;
			this.password = password;
			Class.forName("org.postgresql.Driver");
		}
		catch ( Exception E ) {
			log.error(E.getLocalizedMessage());
		}
	}

	public void logout () {
		close();
	}

	public void open (String tmName,
		String sourceLang,
		String targetLang) {
		try {
			String sTmp = String.format("jdbc:postgresql://%s/%s", server, tmName);
			conn = DriverManager.getConnection(sTmp, username, password);
			this.sourceLang = sourceLang;
			this.targetLang = targetLang;
			isTMOpened = true;
		}
		catch ( Exception E ) {
			log.error(E.getLocalizedMessage());
		}
	}

	public int query (String text) {
		int nCount = 0;
		hits.clear();
		currentMatch = -1;
		try {
			nCount = 0;
			if ( prepStm == null ) {
				prepStm = conn.prepareStatement(String.format(
					"SELECT * FROM tinytm_get_fuzzy_matches('%s', '%s',?,'', '');",
					sourceLang, targetLang));
				prepStm.setMaxRows(maxHits);
			}
			prepStm.setString(1, text);
			ResultSet rsHits = prepStm.executeQuery();
			while ( rsHits.next() ) {
				if ( nCount+1 > maxHits ) return nCount;
				nCount++;
				// Fill the matches
				Match M = new Match();
				M.setScore(rsHits.getInt(1));
				M.setSourceText(rsHits.getString(2));
				M.setTargetText(rsHits.getString(3));
				hits.add(M);
			}
		}
		catch ( Exception E ) {
			Utils.showError(E.getMessage(), null);
			hits.clear();
			currentMatch = -1;
		}
		return nCount;
	}

	public int query (IFilterItem filterItem) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void resetList () {
		currentMatch = -1;
	}

	public void setMaximum (int value) {
		try {
			maxHits = value;
			if ( prepStm != null ) {
				prepStm.setMaxRows(maxHits);
			}
		}
		catch ( Exception E ) {
			log.error(E.getLocalizedMessage());
		}
	}
}
