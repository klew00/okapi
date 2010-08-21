/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.virtualdb.jdbc.h2;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.virtualdb.IVDocument;
import net.sf.okapi.virtualdb.IVItem;
import net.sf.okapi.virtualdb.IVItem.ItemType;
import net.sf.okapi.virtualdb.jdbc.IDBAccess;

public class H2Access implements IDBAccess {

	public static final int ITEMKIND_DOCUMENT = 0;
	public static final int ITEMKIND_SUBDOCUMENT = 1;
	public static final int ITEMKIND_GROUP = 2;
	public static final int ITEMKIND_TEXTUNIT = 3;

	public static final String H2DB_EXT = ".h2.db";
	
	public static final String DOCS_TBLNAME = "DOCS";
	public static final String DOCS_KEY = "KEY";
	public static final String DOCS_XID = "XID";
	public static final String DOCS_NAME = "NAME";
	public static final String DOCS_TYPE = "TYPE";

	public static final String ITMS_TBLNAME = "ITMS";
	public static final String ITMS_KEY = "KEY";
	public static final String ITMS_DKEY = "DKEY";
	public static final String ITMS_PARENT = "PARENT";
	public static final String ITMS_FCHILD = "FCHILD";
	public static final String ITMS_PREV = "PREV";
	public static final String ITMS_NEXT = "NEXT";
	public static final String ITMS_KIND = "KIND";
	public static final String ITMS_LEVEL = "LEVEL";
	public static final String ITMS_XID = "XID";
	public static final String ITMS_NAME = "NAME";
	public static final String ITMS_TYPE = "TYPE";

	public static final String TUNS_TBLNAME = "TUNS";
	public static final String TUNS_KEY = "KEY";
	public static final String TUNS_IKEY = "IKEY";
	public static final String TUNS_CTEXT = "CTEXT";
	public static final String TUNS_CODES = "CODES";
	public static final String TUNS_TRGCTEXT = "TRGCTEXT";
	public static final String TUNS_TRGCODES = "TRGCODES";

	private H2Access self;
	private RepositoryType repoType;
	private String baseDir;
	private Connection conn = null;
	
	// Variables used during import
	private IFilterConfigurationMapper fcMapper;

	/**
	 * Creates the repository in memory.
	 * @param fcMapper the filter configuration mapper to use for importing files. Can be null
	 * if no file is imported during the session.
	 */
	public H2Access (IFilterConfigurationMapper fcMapper) {
		initialize(RepositoryType.INMEMORY, fcMapper);
	}
	
	/**
	 * Creates the repository to work in a given folder.
	 * @param baseDirectory the folder where to work for this repository.
	 * @param fcMapper the filter configuration mapper to use for importing files. Can be null
	 * if no file is imported during the session.
	 */
	public H2Access (String baseDirectory,
		IFilterConfigurationMapper fcMapper)
	{
		initialize(RepositoryType.LOCAL, fcMapper);
		baseDir = baseDirectory;
		if ( !baseDir.endsWith("/") || !baseDir.endsWith("\\") ) {
			baseDir += "/";
		}
	}

	// Not tested
//	public H2Access (URL baseURL,
//		IFilterConfigurationMapper fcMapper)
//	{
//		initialize(RepositoryType.REMOTE, fcMapper);
//	}
	
	private void initialize (RepositoryType repositoryType,
		IFilterConfigurationMapper fcMapper)
	{
		this.repoType = repositoryType;
		this.fcMapper = fcMapper;
		self = this;
		try {
			// Initialize the driver
			Class.forName("org.h2.Driver");
		}
		catch ( ClassNotFoundException e ) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void close () {
		try {
//			if ( qstm != null ) {
//				qstm.close();
//				qstm = null;
//			}
			if ( conn != null ) {
				conn.close();
				conn = null;
			}
		}
		catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void open (String name) {
		// Close existing connection
		close();
		// Create the connection string
		String connStr = null;
		switch ( repoType ) {
		case INMEMORY:
			connStr = "jdbc:h2:mem:"+name+";IFEXISTS=TRUE";
			break;
		case LOCAL:
			connStr = "jdbc:h2:"+baseDir+name+";IFEXISTS=TRUE";
			break;
		default:
			throw new RuntimeException("Unsupported repository type.");
		}
		// Create the connection
		try {
			conn = DriverManager.getConnection(connStr, "sa", "");
			conn.setAutoCommit(true);
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void create (String name) {
		// Close existing connection
		close();
		// Create the connection string
		String connStr = null;
		switch ( repoType ) {
		case INMEMORY:
			connStr = "jdbc:h2:mem:"+name;
			break;
		case LOCAL:
			// Check if a DB exists already
			String path = baseDir+name;
			File file = new File(path+H2DB_EXT);
			if ( file.exists() ) {
				deleteFiles(path);
			}
			else {
				Util.createDirectories(path);
			}
			// New DB
			connStr = "jdbc:h2:"+path;
			break;
		default:
			throw new RuntimeException("Unsupported repository type.");
		}
		// Create the connection
		try {
			conn = DriverManager.getConnection(connStr, "sa", "");
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
		createTables();
	}

	private void deleteFiles (String path) {
		class WildcharFilenameFilter implements FilenameFilter {
			String filename;
			public WildcharFilenameFilter (String filename) {
				this.filename = filename;
			}
			public boolean accept(File dir, String name) {
				return Pattern.matches(filename+"\\..*?\\.db", name);
			}
		}
		String dir = Util.getDirectoryName(path);
		String filename = Util.getFilename(path, false);
		File d = new File(dir);
		File[] list = d.listFiles(new WildcharFilenameFilter(filename));
		if ( list == null ) return;
		for ( File f : list ) {
			f.delete();
		}
	}
	
	@Override
	public void delete () {
		//TODO: delete repository
		throw new UnsupportedOperationException("delete()");
	}

//	@Override
//	public IVDocument getDocument (String docId) {
//		Statement stm = null;
//		IVDocument doc = null;
//		try {
//			stm = conn.createStatement();
//			ResultSet rs = stm.executeQuery(String.format(
//				"SELECT * FROM %s WHERE %s='%s'", DOCS_TBLNAME, DOCS_XID, docId));
//			if ( rs.first() ) {
//				doc = new H2Document(this, rs.getLong(DOCS_KEY), rs.getString(DOCS_XID), rs.getString(DOCS_NAME), rs.getString(DOCS_TYPE));
//			}
//		}
//		catch ( SQLException e ) {
//			throw new RuntimeException(e);
//		}
//		finally {
//			try {
//				if ( stm != null ) {
//					stm.close();
//					stm = null;
//				}
//			}
//			catch ( SQLException e ) {
//				throw new RuntimeException(e);
//			}
//		}
//		return doc;
//	}

	@Override
	public Iterable<IVDocument> documents () {
		return new Iterable<IVDocument>() {
			@Override
			public Iterator<IVDocument> iterator() {
				return new H2DocumentIterator(self);
			};
		};
	}
	
	/**
	 * Gets the list of the keys to all the documents in this repository.
	 * @return a list of the keys to all the documents in this repository.
	 */
	List<Long> getDocumentsKeys () {
		Statement stm = null;
		try {
			stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(String.format("select KEY from ITMS where KIND=%d", ITEMKIND_DOCUMENT));
			List<Long> list = new ArrayList<Long>();
			while ( rs.next() ) {
				list.add(rs.getLong(1));
			}
			return list;
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error reading keys.\n"+e.getMessage());
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

	/**
	 * Gets a list of keys for the given document.
	 * @param docKey the key of the document for which to get the items, use -1 for all documents.
	 * @param tuOnly true to get only the text unit items.
	 * @return the list of keys for the given document
	 */
	List<Long> getItemsKeys (long docKey,
		boolean tuOnly)
	{
		Statement stm = null;
		try {
			stm = conn.createStatement();
			// Construct the SQL query
			String query = "select KEY from ITMS";
			if ( docKey == -1 ) {
				if ( tuOnly ) {
					query += String.format(" where KIND=%d", ITEMKIND_TEXTUNIT);
				}
			}
			else {
				query += String.format(" where DKEY=%d", docKey);
				if ( tuOnly ) {
					query += String.format(" and KIND=%d", ITEMKIND_TEXTUNIT);
				}
			}
			// Get the list
			ResultSet rs = stm.executeQuery(query);
			List<Long> list = new ArrayList<Long>();
			while ( rs.next() ) {
				list.add(rs.getLong(1));
			}
			return list;
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error reading keys.\n"+e.getMessage());
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
	public String importDocument (RawDocument rd) {
		H2Importer imp = new H2Importer(this, fcMapper);
		imp.importDocument(rd);
		return null;
	}

	private void createTables () {
		Statement stm = null;
		try {
			stm = conn.createStatement();
			/* Create the table of documents
			CREATE TABLE DOCS (
				KEY INTEGER IDENTITY PRIMARY KEY,
				XID VARCHAR,
				NAME VARCHAR,
				TYPE VARCHAR)
			*/
//			stm.execute("CREATE TABLE " + DOCS_TBLNAME + " ("
//				+ DOCS_KEY + " INTEGER IDENTITY PRIMARY KEY,"
//				+ DOCS_XID + " VARCHAR,"
//				+ DOCS_NAME + " VARCHAR,"
//				+ DOCS_TYPE + " VARCHAR"
//				+ ")");
			
			/* Create the table of items
			CREATE TABLE ITMS (
				KEY INTEGER IDENTITY PRIMARY KEY,
				DKEY INTEGER REFERENCES DOCS(KEY),
				PARENT INTEGER,
				FCHILD INTEGER,
				PREV INTEGER,
				NEXT INTEGER,
				KIND INTEGER,
				LEVEL INTEGER,
				XID VARCHAR,
				NAME VARCHAR,
				TYPE VARCHAR)
			*/
			stm.execute("CREATE TABLE " + ITMS_TBLNAME + " ("
				+ ITMS_KEY + " INTEGER IDENTITY PRIMARY KEY,"
				+ ITMS_DKEY + " INTEGER,"
				+ ITMS_PARENT + " INTEGER,"
				+ ITMS_FCHILD + " INTEGER,"
				+ ITMS_PREV + " INTEGER,"
				+ ITMS_NEXT + " INTEGER,"
				+ ITMS_KIND + " INTEGER,"
				+ ITMS_LEVEL + " INTEGER,"
				+ ITMS_XID + " VARCHAR,"
				+ ITMS_NAME + " VARCHAR,"
				+ ITMS_TYPE + " VARCHAR"
				+ ")");
			
			/* Create the table of text units
			CREATE TABLE TUNS (
				KEY INTEGER IDENTITY PRIMARY KEY,
				IKEY INTEGER REFERENCES ITMS(KEY),
				CTEXT VARCHAR,
				CODES VARCHAR,
				TRGCTEXT VARCHAR,
				TRGCODES VARCHAR)
			*/
			stm.execute("CREATE TABLE " + TUNS_TBLNAME + " ("
				+ TUNS_KEY + " INTEGER IDENTITY PRIMARY KEY,"
				+ TUNS_IKEY + " INTEGER REFERENCES "+ITMS_TBLNAME+ "("+ITMS_KEY+") ON DELETE CASCADE,"
				+ TUNS_CTEXT + " VARCHAR,"
				+ TUNS_CODES + " VARCHAR,"
				+ TUNS_TRGCTEXT + " VARCHAR,"
				+ TUNS_TRGCODES + " VARCHAR"
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
	
	void saveDocument (H2Document doc) {
		PreparedStatement pstm = null;
		try {
			/* The things to save in a document in this implementation are:
			 * The navigation pointers for previous next (if another document was added or removed)
			 * ... and that's about it.
			 */
			pstm = conn.prepareStatement(String.format("update ITMS set %s=?, %s=? where %s=?",
				ITMS_PREV, ITMS_NEXT, ITMS_KEY));
			pstm.setLong(1, doc.previous);
			pstm.setLong(2, doc.next);
			pstm.setLong(3, doc.key);
			pstm.execute();
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
	
	protected void saveTextUnit (H2TextUnit htu) {
		PreparedStatement pstm = null;
		try {
			pstm = conn.prepareStatement(String.format("update TUNS set %s=?, %s=? where %s=?",
				TUNS_TRGCTEXT, TUNS_TRGCODES, TUNS_IKEY));
			String[] trgData = targetsToStorage(htu.getTextUnit());
			pstm.setString(1, trgData[0]); // Targets coded-text
			pstm.setString(2, trgData[1]); // Targets codes
			pstm.setLong(3, htu.itemKey);
			pstm.execute();
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
	
	IVItem getItemFromExtractionId (H2Document doc,
		String id)
	{
		PreparedStatement pstm = null;
		try {
			// Always left-join with the TUNS table so we get extra text unit info in one call
			// This is ok because most calls are for text units.
			pstm = conn.prepareStatement("select * from ITMS left join TUNS on ITMS.KEY=TUNS.IKEY where ITMS.XID=? and ITMS.DKEY=?");
			pstm.setString(1, id);
			pstm.setLong(2, doc.key);
			ResultSet rs = pstm.executeQuery();
			// Return null if nothing found
			if ( !rs.first() ) return null;
			return fillItem(doc, rs);
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error reading an item.\n"+e.getMessage());
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
	
	IVItem getItemFromItemKey (H2Document doc,
		long itemKey)
	{
		if ( itemKey == -1 ) return null;
		Statement stm = null;
		try {
			stm = conn.createStatement();
			// Always left-join with the TUNS table so we get extr text unit info in one call
			// This is ok because most calls are for text units.
			String query = String.format("select * from ITMS left join TUNS on ITMS.KEY=TUNS.IKEY WHERE ITMS.KEY=%d", itemKey);
			ResultSet rs = stm.executeQuery(query);
			// Return null if nothing found
			if ( !rs.first() ) return null;
			return fillItem(doc, rs);
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error reading an item.\n"+e.getMessage());
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

	private IVItem fillItem (H2Document doc,
		ResultSet rs)
	{
		IVItem item = null;
		try {
			switch ( rs.getInt(ITMS_KIND) ) {
			case ITEMKIND_TEXTUNIT:
				H2TextUnit htu = new H2TextUnit(rs.getLong(TUNS_IKEY), doc, rs.getString(ITMS_XID),
					rs.getString(ITMS_NAME), rs.getString(ITMS_TYPE));
				htu.fillPointers(rs.getLong(ITMS_PARENT), rs.getLong(ITMS_PREV), rs.getLong(ITMS_NEXT));
				TextUnit tu = htu.getTextUnit();
				tu.setSource(TextContainer.splitStorageToContent(rs.getString(TUNS_CTEXT), rs.getString(TUNS_CODES)));
				storageToTargets(tu, rs.getString(TUNS_TRGCTEXT), rs.getString(TUNS_TRGCODES));
				item = htu;
				break;
			case ITEMKIND_GROUP:
				H2Group grp = new H2Group(rs.getLong("ITMS.KEY"), doc, rs.getString(ITMS_XID),
					rs.getString(ITMS_NAME), rs.getString(ITMS_TYPE));
				grp.fillPointers(rs.getLong(ITMS_PARENT), rs.getLong(ITMS_FCHILD),
					rs.getLong(ITMS_PREV), rs.getLong(ITMS_NEXT));
				item = grp;
				break;
			case ITEMKIND_SUBDOCUMENT:
				H2SubDocument sd = new H2SubDocument(rs.getLong("ITMS.KEY"), doc, rs.getString(ITMS_XID),
					rs.getString(ITMS_NAME), rs.getString(ITMS_TYPE));
				sd.fillPointers(rs.getLong(ITMS_PARENT), rs.getLong(ITMS_FCHILD),
					rs.getLong(ITMS_PREV), rs.getLong(ITMS_NEXT));
				item = sd;
				break;
			case ITEMKIND_DOCUMENT:
				H2Document newDoc = new H2Document(this, rs.getLong("ITMS.KEY"), rs.getString(ITMS_XID),
					rs.getString(ITMS_NAME), rs.getString(ITMS_TYPE));
				newDoc.fillPointers(rs.getLong(ITMS_PARENT), rs.getLong(ITMS_FCHILD),
					rs.getLong(ITMS_PREV), rs.getLong(ITMS_NEXT));
				item = newDoc;
				break;
			}
			return item;
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error filling item.\n"+e.getMessage());
		}
	}
	
	@Override
	public IVDocument getDocument (long itemKey) {
		Statement stm = null;
		IVDocument doc = null;
		try {
			stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(String.format(
				"select * from ITMS where KEY=%d", itemKey));
			if ( rs.first() ) {
				doc = new H2Document(this, itemKey, rs.getString(ITMS_XID), rs.getString(ITMS_NAME), rs.getString(ITMS_TYPE));
				((H2Document)doc).fillPointers(rs.getLong(ITMS_PARENT), rs.getLong(ITMS_FCHILD),
					rs.getLong(ITMS_PREV), rs.getLong(ITMS_NEXT));
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
		return doc;
	}

	void completeItemsWriting (LinkedHashMap<Long, H2Navigator> items) {
		PreparedStatement pstm = null;
		try {
			// Create the item entry to save the spot and get a key
			pstm = conn.prepareStatement(String.format("UPDATE %s SET %s=?, %s=?, %s=?, %s=?, %s=? WHERE %s=?", ITMS_TBLNAME,
				ITMS_PARENT, ITMS_LEVEL, ITMS_FCHILD, ITMS_PREV, ITMS_NEXT, ITMS_KEY));
			for ( H2Navigator item : items.values() ) {
				pstm.setLong(1, item.parent);
				pstm.setLong(2, item.level);
				pstm.setLong(3, item.firstChild);
				pstm.setLong(4, item.previous);
				pstm.setLong(5, item.next);
				pstm.setLong(6, item.key);
				pstm.execute();
			}
		    pstm.close();
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
	
	long writeResourceData (INameable res, ItemType type, long docKey) {
		long itemKey = -1;
		PreparedStatement pstm = null;
		try {
			// Create the item entry to save the spot and get a key
			pstm = conn.prepareStatement(String.format("INSERT INTO %s (%s,%s,%s,%s,%s) VALUES(?,?,?,?,?);", ITMS_TBLNAME,
				ITMS_DKEY, ITMS_KIND, ITMS_XID, ITMS_NAME, ITMS_TYPE));
			pstm.setLong(1, docKey);
			switch ( type ) {
			case DOCUMENT:
				pstm.setShort(2, (short)ITEMKIND_DOCUMENT);
				break;
			case SUB_DOCUMENT:
				pstm.setShort(2, (short)ITEMKIND_SUBDOCUMENT);
				break;
			case GROUP:
				pstm.setShort(2, (short)ITEMKIND_GROUP);
				break;
			}
			pstm.setString(3, res.getId());
			pstm.setString(4, res.getName());
			pstm.setString(5, res.getType());
			pstm.execute();
			// Get the Item key
			ResultSet keys = pstm.getGeneratedKeys();
		    if ( keys.first() ) {
		    	itemKey = keys.getLong(1);
		    }
		    pstm.close();
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
		return itemKey;
	}

	@Override
	public void removeDocument (IVDocument vdoc) {
		PreparedStatement pstm = null;
		try {
			H2Document doc = (H2Document)vdoc;

			// The new next sibling of the document's previous sibling is now the document's next sibling
			if ( doc.previous > -1 ) {
				pstm = conn.prepareStatement(String.format("UPDATE %s SET %s=? WHERE %s=?", ITMS_TBLNAME,
					ITMS_NEXT, ITMS_KEY));
				pstm.setLong(1, doc.next);
				pstm.setLong(2, doc.previous);
				pstm.execute();
			}
			
			// The new previous sibling of the document's next sibling is now the document's previous sibling
			if ( doc.next > -1 ) {
				pstm = conn.prepareStatement(String.format("UPDATE %s SET %s=? WHERE %s=?", ITMS_TBLNAME,
					ITMS_PREV, ITMS_KEY));
				pstm.setLong(1, doc.previous);
				pstm.setLong(2, doc.next);
				pstm.execute();
			}
			
			// Delete all items related the document
			pstm = conn.prepareStatement(String.format("delete from %s where %s=?",
				ITMS_TBLNAME, ITMS_DKEY));
			pstm.setLong(1, doc.key);
			pstm.execute();
			
			// Delete the document entry itself
			pstm = conn.prepareStatement(String.format("delete from %s where %s=?",
				ITMS_TBLNAME, ITMS_KEY));
			pstm.setLong(1, doc.key);
			pstm.execute();
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
	
//	long writeSubDocumentData (StartSubDocument ssd) {
//		long itemKey = -1;
//		PreparedStatement pstm = null;
//		try {
//			// Create the item entry to save the spot and get a key
//			pstm = conn.prepareStatement(String.format("INSERT INTO %s (%s,%s,%s,%s,%s) VALUES(?,?,?,?,?);", ITMS_TBLNAME,
//				ITMS_DKEY, ITMS_KIND, ITMS_XID, ITMS_NAME, ITMS_TYPE));
//			pstm.setLong(1, -1L);
//			pstm.setShort(2, (short)ITEMKIND_DOCUMENT);
//			pstm.setString(3, ssd.getId());
//			pstm.setString(4, ssd.getName());
//			pstm.setString(5, ssd.getType());
//			pstm.execute();
//			// Get the Item key
//			ResultSet keys = pstm.getGeneratedKeys();
//		    if ( keys.first() ) {
//		    	itemKey = keys.getLong(1);
//		    }
//		    pstm.close();
//		}
//		catch ( SQLException e ) {
//			throw new RuntimeException(e);
//		}
//		finally {
//			try {
//				if ( pstm != null ) {
//					pstm.close();
//					pstm = null;
//				}
//			}
//			catch ( SQLException e ) {
//				throw new RuntimeException(e);
//			}
//		}
//		return itemKey;
//	}

	long writeTextUnitData (TextUnit tu,
		long docKey)
	{
		long itemKey = -1;
		PreparedStatement pstm = null;
		try {
			// Create the item entry to save the spot and get a key
			pstm = conn.prepareStatement(String.format("INSERT INTO %s (%s,%s,%s,%s,%s) VALUES(?,?,?,?,?);", ITMS_TBLNAME,
				ITMS_DKEY, ITMS_KIND, ITMS_XID, ITMS_NAME, ITMS_TYPE));
			pstm.setLong(1, docKey);
			pstm.setShort(2, (short)ITEMKIND_TEXTUNIT);
			pstm.setString(3, tu.getId());
			pstm.setString(4, tu.getName());
			pstm.setString(5, tu.getType());
			pstm.execute();
			// Get the Item key
			ResultSet keys = pstm.getGeneratedKeys();
		    if ( keys.first() ) {
		    	itemKey = keys.getLong(1);
		    }
		    pstm.close();

		    // Create the text unit data entry
			pstm = conn.prepareStatement(String.format("INSERT INTO %s (%s,%s,%s,%s,%s) VALUES(?,?,?,?,?);", TUNS_TBLNAME,
				TUNS_IKEY, TUNS_CTEXT, TUNS_CODES, TUNS_TRGCTEXT, TUNS_TRGCODES));
			String[] srcData = TextContainer.contentToSplitStorage(tu.getSource());
			String[] trgData = targetsToStorage(tu);
			pstm.setLong(1, itemKey);
			pstm.setString(2, srcData[0]); // Source coded-text
			pstm.setString(3, srcData[1]); // Source codes
			pstm.setString(4, trgData[0]); // Targets coded-text
			pstm.setString(5, trgData[1]); // Targets codes
			pstm.execute();
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
		return itemKey;
	}

	private String[] targetsToStorage (TextUnit tu) {
		String res[] = new String[2];
		StringBuilder tmp0 = new StringBuilder();
		StringBuilder tmp1 = new StringBuilder();
		Iterator<LocaleId> iter = tu.getTargetLocales().iterator();
		while ( iter.hasNext() ) {
			LocaleId loc = iter.next();
			TextContainer tc = tu.getTarget(loc);
			tmp0.append(loc.toString()+"|");
			String[] data = TextContainer.contentToSplitStorage(tc);
			tmp0.append(data[0]);  // Target coded text
			tmp0.append("\u0093");
			tmp1.append(data[1]); // Target codes
			tmp1.append("\u0093");
		}
		res[0] = tmp0.toString();
		res[1] = tmp1.toString();
		return res;
	}

	private void storageToTargets (TextUnit tu,
		String ctext,
		String codes)
	{
		String[] codesParts = codes.split("\u0093", -2);
		String[] ctextParts = ctext.split("\u0093", -2);
		for ( int i=0; i<ctextParts.length-1; i++ ) {
			int n = ctextParts[i].indexOf('|');
			LocaleId loc = LocaleId.fromString(ctextParts[i].substring(0, n));
			TextContainer tc = TextContainer.splitStorageToContent(ctextParts[i].substring(n+1), codesParts[i]);
			tu.setTarget(loc, tc);
		}
	}

	@Override
	public IVDocument getFirstDocument () {
		List<Long> list = getDocumentsKeys();
		if ( list.size() < 1 ) return null;
		return getDocument(list.get(0));
	}

}
