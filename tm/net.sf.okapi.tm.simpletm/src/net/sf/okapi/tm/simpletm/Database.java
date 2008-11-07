package net.sf.okapi.tm.simpletm;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.translation.QueryResult;

/**
 * Simple database to store align source and target with some context info.
 * This is for simple exact match retrieval for now.
 */
public class Database {

	public static final String TBLNAME      = "Source";

	public static final int  KEY          = 0;
	public static final String NKEY       = "Key";
	public static final int  NAME         = 1;
	public static final String NNAME      = "Name";
	public static final int  TYPE         = 2;
	public static final String NTYPE      = "Type";
	public static final int  SRCTEXT      = 3;
	public static final String NSRCTEXT   = "SrcText";
	public static final int  SRCCODES     = 4;
	public static final String NSRCCODES  = "SrcCodes";
	public static final int  TRGTEXT      = 5;
	public static final String NTRGTEXT   = "TrgText";
	public static final int  TRGCODES     = 6;
	public static final String NTRGCODES  = "TrgCodes";
	public static final int  GRPNAME      = 7;
	public static final String NGRPNAME   = "GrpName";
	
	public static final String DATAFILE_EXT = ".data.db";

	private Connection  conn = null;
	private PreparedStatement qstm = null;

	public Database () {
		try {
			// Initialize the driver
			Class.forName("org.h2.Driver");
		}
		catch ( ClassNotFoundException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public void close () {
		try {
			if ( qstm != null ) {
				qstm.close();
				qstm = null;
			}
			if ( conn != null ) {
				conn.close();
				conn = null;
			}
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}

	private void deleteFiles (String pathAndPattern) {
		class WildcharFilenameFilter implements FilenameFilter {
			public boolean accept(File dir, String name) {
				return Pattern.matches(".*?\\..*?\\.db", name);
			}
		}
		String dir = Util.getDirectoryName(pathAndPattern);
		File d = new File(dir);
		File[] list = d.listFiles(new WildcharFilenameFilter());
		for ( File f : list ) {
			f.delete();
		}
	}
	
	public void create (String path,
		boolean deleteExistingDB)
	{
		Statement stm = null;
		try {
			close();
			if ( (new File(path+DATAFILE_EXT)).exists() ) {
				if ( !deleteExistingDB ) return;
				deleteFiles(path+".*");
			}
			else Util.createDirectories(path);
			
			// Open the connection, this creates the DB if none exists
			conn = DriverManager.getConnection("jdbc:h2:"+path, "sa", "");
	
			// Create the source table
			stm = conn.createStatement();
			stm.execute("CREATE TABLE " + TBLNAME + " ("
				+ NKEY + " INTEGER IDENTITY PRIMARY KEY,"
				+ NNAME + " VARCHAR,"
				+ NTYPE + " VARCHAR,"
				+ NSRCTEXT + " VARCHAR,"
				+ NSRCCODES + " VARCHAR,"
				+ NTRGTEXT + " VARCHAR,"
				+ NTRGCODES + " VARCHAR,"
				+ NGRPNAME + " VARCHAR,"
				+ ")");
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( stm != null ) {
					stm.close();
					stm = null;
				}
			}
			catch ( SQLException e ) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void open (String path) {
		try {
			close();
			if ( !(new File(path+DATAFILE_EXT)).exists() ) return;
			conn = DriverManager.getConnection("jdbc:h2:"+path, "sa", "");
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public int getEntryCount () {
		Statement stm = null;
		try {
			stm = conn.createStatement();
			ResultSet result = stm.executeQuery("SELECT COUNT(" + NKEY + ") FROM " + TBLNAME);
			if ( !result.first() ) return 0;
			return result.getInt(1);
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( stm != null ) {
					stm.close();
					stm = null;
				}
			}
			catch ( SQLException e ) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public int addEntry (TextUnit tu,
		String grpName)
	{
		int count = 0;
		PreparedStatement pstm = null;
		try {
			if ( !tu.hasTarget() ) return 0;

			//TODO: make this pstm class-level
			pstm = conn.prepareStatement(String.format("INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s) VALUES(?,?,?,?,?,?,?);",
				TBLNAME, NNAME, NTYPE, NSRCTEXT, NSRCCODES, NTRGTEXT, NTRGCODES, NGRPNAME));
			
			// Store the data
			TextContainer srcCont = tu.getSourceContent();
			TextContainer trgCont = tu.getTargetContent();
			pstm.setString(1, tu.getName());
			pstm.setString(2, tu.getType());
			pstm.setString(7, tu.getName()); // group name is TU name for now

			// Store the segments if possible
			if ( srcCont.isSegmented() && trgCont.isSegmented() ) {
				TextFragment trgFrag;
				int seg = 0;
				for ( TextFragment srcFrag : srcCont.getSegments() ) {
					pstm.setString(3, srcFrag.getCodedText());
					pstm.setString(4, Code.codesToString(srcFrag.getCodes()));
					trgFrag = trgCont.getSegments().get(seg);
					pstm.setString(5, trgFrag.getCodedText());
					pstm.setString(6, Code.codesToString(trgFrag.getCodes()));
					pstm.execute();
					count++;
					seg++;
				}
			}
			else {
				pstm.setString(3, srcCont.getCodedText());
				pstm.setString(4, Code.codesToString(srcCont.getCodes()));
				pstm.setString(5, trgCont.getCodedText());
				pstm.setString(6, Code.codesToString(trgCont.getCodes()));
				pstm.execute();
				count++;
			}
			return count;
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if ( pstm != null ) {
					pstm.close();
					pstm = null;
				}
			}
			catch ( SQLException e ) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void setAttributes (LinkedHashMap<String, String> attributes) {
		try {
			// prepare the query with or without context condition
			if ( attributes == null ) {
				qstm = conn.prepareStatement(String.format("SELECT %s,%s,%s,%s FROM %s WHERE %s=?",
					NSRCTEXT, NSRCCODES, NTRGTEXT, NTRGCODES, TBLNAME, NSRCTEXT));
			}
			else {
				StringBuilder tmp = new StringBuilder();
				tmp.append(String.format("SELECT %s,%s,%s,%s FROM %s WHERE %s=?",
					NSRCTEXT, NSRCCODES, NTRGTEXT, NTRGCODES, TBLNAME, NSRCTEXT));
				for ( String name : attributes.keySet() ) {
					tmp.append(" AND "+name+"=?");
				}
				qstm = conn.prepareStatement(tmp.toString());
			}
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public List<QueryResult> query (TextFragment query,
		LinkedHashMap<String, String> attributes,
		int maxCount)
	{
		try {
			// prepare the query with or without context condition
			if ( qstm == null ) {
				// Create the statment if needed
				setAttributes(attributes);
			}
			// Fill the parameters
			if ( attributes != null ) {
				int i = 2;
				for ( String name : attributes.keySet() ) {
					qstm.setString(i, attributes.get(name));
					i++;
				}
			}
			qstm.setString(1, query.getCodedText());
			ResultSet result = qstm.executeQuery();
			if ( !result.first() ) return null;
			ArrayList<QueryResult> list = new ArrayList<QueryResult>(); 
			do {
				QueryResult qr = new QueryResult();
				qr.score = 100;
				qr.source = new TextFragment();
				qr.source.setCodedText(result.getString(1),
					Code.stringToCodes(result.getString(2)), false);
				qr.target = new TextFragment();
				qr.target.setCodedText(result.getString(3),
					Code.stringToCodes(result.getString(4)), false);
				list.add(qr);
			} while ( result.next() && ( list.size() < maxCount ));
			return list;
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}
	
}
