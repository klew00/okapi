package net.sf.okapi.connectors.tinytm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Translation.IMatch;
import net.sf.okapi.Translation.ITMQuery;
import net.sf.okapi.Translation.Match;
import net.sf.okapi.ferret.Core.TMBase;

public class Connector implements ITMQuery {

	private Connection       conn = null;
	private boolean          isTMOpened = false;
	private ILog             log;
	private String           server;
	private String           username;
	private String           password;
	private int              currentMatch = -1;
	private Vector<Match>    hits = new Vector<Match>();
	
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
				conn.close();
				conn = null;
				isTMOpened = false;
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
		return 50;
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

	public void open (String name) {
		try {
			String sTmp = String.format("jdbc:postgresql://%s/%s", server, name);
			conn = DriverManager.getConnection(sTmp, username, password);
			isTMOpened = true;
		}
		catch ( Exception E ) {
			log.error(E.getLocalizedMessage());
		}
	}

	public int query (String text) {
		int nCount = 0;
		Statement Stm = null;
		hits.clear();
		currentMatch = -1;
		try {
			String sTmp = String.format(
				"SELECT * FROM tinytm_get_fuzzy_matches('en', 'de','%s','', '');",
				text);
			Stm = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rsHits = Stm.executeQuery(sTmp);
			if ( rsHits.last() ) {
				nCount = rsHits.getRow();
				//if ( nCount > m_nMax ) nCount = m_nMax;
			}
			if ( nCount == 0 ) return 0;
			
			// Fill the matches
			rsHits.beforeFirst();
			for ( int i=0; i<nCount; i++ ) {
				if ( !rsHits.next() ) break;
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
		finally {
			if ( Stm != null ) {
				try { Stm.close(); } catch ( Exception E ) {};
				Stm = null;
			}
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
		// TODO Auto-generated method stub
	}
}
