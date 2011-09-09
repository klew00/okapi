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

package net.sf.okapi.lib.tmdb.local;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.lib.tmdb.DbUtil;
import net.sf.okapi.lib.tmdb.IRepository;
import net.sf.okapi.lib.tmdb.ITm;

public class Repository implements IRepository {

	public static final String DATAFILE_EXT = ".h2.db";

	private Connection  conn = null;
	private String name;

	private static String localeIdToDbLang (LocaleId locId) {
		return locId.toPOSIXLocaleId().toUpperCase();
	}
	
	static public void delete (String path) {
		String pathNoExt = path;
		if ( pathNoExt.endsWith(DATAFILE_EXT) ) {
			pathNoExt = pathNoExt.substring(0, pathNoExt.length()-DATAFILE_EXT.length());
		}
		File file = new File(pathNoExt+DATAFILE_EXT);
		if ( file.exists() ) {
			file.delete();
		}
	}

	/**
	 * Creates a new Repository object. the local back-end files are created if 
	 * they do not exists yet. If the files exist they are used.
	 * @param path the path of the main storage file (without extension normally).
	 */
	public Repository (String path) {
		Statement stm = null;
		try {
			// Initialize the driver
			Class.forName("org.h2.Driver");
			boolean exist = false;
			
			if ( path == null ) {
				// Open the connection, this creates the DB if none exists
				conn = DriverManager.getConnection("jdbc:h2:mem:", "sa", "");
				name = "In-Memory Repository";
			}
			else {
				String pathNoExt = path;
				if ( pathNoExt.endsWith(DATAFILE_EXT) ) {
					pathNoExt = pathNoExt.substring(0, pathNoExt.length()-DATAFILE_EXT.length());
				}
				name = Util.getFilename(pathNoExt, false);
			
				// Check if the database exists
				exist = (new File(pathNoExt+DATAFILE_EXT)).exists();
				if ( !exist ) {
					// Create the directory if needed
					Util.createDirectories(pathNoExt);
				}
				
				// Open the connection, this creates the DB if none exists
				conn = DriverManager.getConnection("jdbc:h2:"+pathNoExt, "sa", "");
			}
	
			if ( !exist ) {
				// Create the source table
				stm = conn.createStatement();
				stm.execute("CREATE TABLE REPO ("
					+ "NAME INTEGER,"
					+ "DESCRIPTION VARCHAR"
					+ ")");
			
				stm.execute("CREATE TABLE TMLIST ("
					+ "UUID VARCHAR,"
					+ "NAME VARCHAR,"
					+ "DESCRIPTION VARCHAR"
					+ ")");
			}
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
		catch ( ClassNotFoundException e ) {
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
	
	@Override
	protected void finalize() throws Throwable {
        close();
        super.finalize();
	}

	@Override
	public void close () {
		try {
			if ( conn != null ) {
				conn.close();
				conn = null;
			}
			name = null;
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getName () {
		return name;
	}
	
	Connection getConnection () {
		return conn;
	}
	
	@Override
	public void deleteTm (String name) {
		Statement stm = null;
		try {
			//name = name.toUpperCase();
			stm = conn.createStatement();
			stm.execute("DROP TABLE \""+name+"_TU\"");
			stm.execute("DROP TABLE \""+name+"_SEG\"");
			stm.executeUpdate("DELETE FROM TMLIST WHERE NAME='"+name+"'");
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

	String[] getTmData (String uuid) {
		String[] res = new String[2];
		Statement stm = null;
		try {
			stm = conn.createStatement();
			ResultSet result = stm.executeQuery("SELECT NAME, DESCRIPTION FROM TMLIST WHERE UUID='"+uuid+"'");
			if ( !result.first() ) {
				// Invalid TM key
				throw new RuntimeException(String.format("Invalid TM uuid '%s'.", uuid));
			}
			res[0] = result.getString(1);
			res[1] = result.getString(2);
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
		return res;
	}
	
	@Override
	public ITm getTm (String name) {
		ITm tm = null;
		Statement stm = null;
		try {
			//name = name.toUpperCase();
			stm = conn.createStatement();
			ResultSet result = stm.executeQuery("SELECT UUID FROM TMLIST WHERE NAME='"+name+"'");
			if ( result.first() ) {
				tm = new Tm(this, result.getString(1), name);
			}
			else { // TM does not exist
				throw new RuntimeException(String.format("The TM '%s' does not exists.", name));
			}
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
		return tm;
	}
	
	@Override
	public ITm createTm (String name,
		String description,
		LocaleId locId)
	{
		String uuid = null;
		ITm tm = null;
		Statement stm = null;
		PreparedStatement pstm = null;
		String lang = localeIdToDbLang(locId);
		try {
			//name = name.toUpperCase();
			// Checks if the name is already used
			stm = conn.createStatement();
			ResultSet result = stm.executeQuery("SELECT NAME FROM TMLIST WHERE NAME='"+name+"'");
			if ( result.first() ) {
				// TM exists already
				return getTm(name);
			}
			
			// Create the TU-level table for the new TM
			stm.execute("CREATE TABLE \""+name+"_TU"+"\" ("
				+ "TUKEY INTEGER IDENTITY PRIMARY KEY"
				+ ")");
			
			// Create the SEG-level table for the new TM
			stm.execute("CREATE TABLE \""+name+"_SEG"+"\" ("
				+ "SEGKEY INTEGER IDENTITY PRIMARY KEY,"
				+ "TUREF INTEGER,"
				+ "FLAG BOOLEAN,"
				// One language
				+ "\""+DbUtil.TEXT_PREFIX+lang+"\" VARCHAR,"
				+ "\""+DbUtil.CODES_PREFIX+lang+"\" VARCHAR"
				+ ")");

			// Update the TMLIST
			pstm = conn.prepareStatement("INSERT INTO TMLIST (UUID,NAME,DESCRIPTION) VALUES(?,?,?)");
			uuid = UUID.randomUUID().toString();
			pstm.setString(1, uuid);
			pstm.setString(2, name);
			pstm.setString(3, description);
			pstm.executeUpdate();
			tm = new Tm(this, uuid, name);
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
				if ( pstm != null ) {
					pstm.close();
					pstm = null;
				}
			}
			catch ( SQLException e ) {
				throw new RuntimeException(e);
			}
		}
		return tm;
	}

	ArrayList<String> getFields (String tmName,
		boolean segmentTable)
	{
		ArrayList<String> list = new ArrayList<String>();
		Statement stm = null;
		try {
			stm = conn.createStatement();
			ResultSet result = stm.executeQuery(String.format("SHOW COLUMNS FROM \"%s%s\"", tmName, (segmentTable ? "_SEG" : "_TU")));
			result.first(); //Skip key
			if ( segmentTable ) result.next(); // Skip FLAG
			while ( result.next() ) {
				list.add(result.getString(1));
			}
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
		return list;
	}

	void createNewFields (String tmName,
		boolean inSegmentTable,
		LinkedHashMap<String, String> newFields,
		ArrayList<String> existingFields)
	{
		if ( Util.isEmpty(newFields) ) {
			return;
		}
		Statement stm = null;
		try {
			StringBuilder tmp = new StringBuilder();
			for ( String name : newFields.keySet() ) {
				tmp.append(String.format("ALTER TABLE \"%s%s\" ADD \"%s\" %s; ",
					tmName, (inSegmentTable ? "_SEG" : "_TU"),
					name, newFields.get(name)));
			}
			stm = conn.createStatement();
			stm.execute(tmp.toString());
			// Update the live list of existing fields
			existingFields.addAll(newFields.keySet());
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
	
	List<String> getAvailableFields (String tmName) {
		List<String> list = new ArrayList<String>();
		Statement stm = null;
		try {
			stm = conn.createStatement();
			ResultSet result = stm.executeQuery("SHOW COLUMNS FROM \""+tmName+"_TU\"");
			result.first(); //Skip TUKEY
			while ( result.next() ) {
				list.add(result.getString(1));
			}
			result = stm.executeQuery("SHOW COLUMNS FROM \""+tmName+"_SEG\"");
			result.first(); //Skip SEGKEY
			result.next(); list.add(result.getString(1)); // Include TUREF
			result.next(); //Skip FLAG
			while ( result.next() ) {
				list.add(result.getString(1));
			}
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
		return list;
	}

	@Override
	public List<String> getTmNames () {
		List<String> list = new ArrayList<String>();
		Statement stm = null;
		try {
			stm = conn.createStatement();
			ResultSet result = stm.executeQuery("SELECT NAME FROM TMLIST");
			while ( result.next() ) {
				list.add(result.getString(1));
			}
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
		return list;
	}

}
