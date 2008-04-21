/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
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

package net.sf.okapi.Ferret.Core;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;

import net.sf.okapi.Filter.FilterItem;
import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.IFilterItem;
import net.sf.okapi.Format.TMX.Item;
import net.sf.okapi.Format.TMX.Reader;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Translation.IMatch;
import net.sf.okapi.Translation.ITMQuery;
import net.sf.okapi.Translation.Match;

public class Engine implements ITMQuery {

	public static final String    FIELD_STEXT    = "ST";
	public static final String    FIELD_TTEXT    = "TT";

	private ILog        m_Log;
	private IndexReader m_Reader;
	private Searcher    m_Searcher = null;
	private Analyzer    m_Analyzer = null;
	private QueryParser m_Parser = null;
	private int         m_nMax = 30;
	private Hits        m_Hits = null;
	private int         m_nCurrent = -1;
	private String      m_sPath;
	private boolean     m_bGetAll = false;
	
	public Engine (ILog p_Log) {
		m_Log = p_Log;
	}

	public boolean isTMOpened () {
		return (m_Searcher != null);
	}

	public void createTM (String p_sPath) {
		IndexWriter wrtTmp = null;
		try {
			m_sPath = p_sPath;
			wrtTmp = new IndexWriter(m_sPath, new StandardAnalyzer(), true);
			if ( wrtTmp != null ) {
				wrtTmp.close();
				wrtTmp = null;
			}
			open(p_sPath);
		}		
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
			Utils.showError(E.getLocalizedMessage(), null);
		}
		finally
		{
			if ( wrtTmp != null ) {
		        try { wrtTmp.close(); }
		        catch ( Exception E ) {
					m_Log.error(E.getLocalizedMessage());
					Utils.showError(E.getLocalizedMessage(), null);
		        }
			}
		}
	}

	public void importTMX (String p_sPath) {
		Reader TR = null;
		IndexWriter wrtTmp = null;
		try {
			int nCount = 0;
			TR = new Reader(m_Log);
			TR.open(p_sPath, null, null);
			wrtTmp = new IndexWriter(m_sPath, new StandardAnalyzer(), false);

			Item ITM;
			IFilterItem FI;
			Document docTmp;

			while ( TR.readItem() > Reader.RESULT_ENDOFDOC ) {
				FI = TR.getSource();
				if ( !FI.hasText(false) ) continue;
				while ( (ITM = TR.getNextTarget()) != null ) {
					docTmp = new Document();
					docTmp.add(new Field(FIELD_STEXT, FI.getText(FilterItemText.PLAIN),
						Field.Store.YES, Field.Index.TOKENIZED, Field.TermVector.YES));
					docTmp.add(new Field(FIELD_TTEXT, ITM.getFI().getText(FilterItemText.PLAIN),
						Field.Store.YES, Field.Index.NO, Field.TermVector.NO));
					wrtTmp.addDocument(docTmp);
					nCount++;
				}
			}
	        wrtTmp.optimize();
	        m_Log.message(String.format("Number of entries imported: %d", nCount));
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
			Utils.showError(E.getLocalizedMessage(), null);
		}
		finally
		{
			if ( TR != null ) {
				try { TR.close(); } catch (Exception E) {};
				TR = null;
			}
			if ( wrtTmp != null ) {
		        try { wrtTmp.close(); }
		        catch ( Exception E ) {
					m_Log.error(E.getLocalizedMessage());
					Utils.showError(E.getLocalizedMessage(), null);
		        }
			}
		}
	}

	public void exportTMX (String p_sPath) {
		net.sf.okapi.Format.TMX.Writer Wrt = null;
		try {
			
			Wrt = new net.sf.okapi.Format.TMX.Writer(p_sPath);
			Wrt.writeStartDocument();
			Document docTmp;
			FilterItem FI = new FilterItem();
			for ( int i=0; i<m_Reader.maxDoc(); i++ ) {
				if ( m_Reader.isDeleted(i) ) continue;
				docTmp = m_Reader.document(i);
				Wrt.writeStartTU(String.format("%d", i));
				FI.setText(docTmp.get(FIELD_STEXT));
				Wrt.writeTUV("TODOsrclang", FI);
				FI.setText(docTmp.get(FIELD_TTEXT));
				Wrt.writeTUV("TODOtrglang", FI);
				//Wrt.writeProperties("x-fstrval", sbTmp.toString()); 
				Wrt.writeEndTU();
			}
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
			Utils.showError(E.getLocalizedMessage(), null);
		}
		finally {
			if ( Wrt != null ) {
				try {
					Wrt.writeEndDocument();
					Wrt.close();
				}
				catch ( Exception E ) {
					m_Log.error(E.getLocalizedMessage());
					Utils.showError(E.getLocalizedMessage(), null);
				}
			}
		}
	}
	
	public int queryAllEntries () {
		m_nCurrent = -1;
		m_Hits = null;
		m_bGetAll = true;
		return m_Reader.numDocs();
	}
	
	public void close() {
		try {
			if ( m_Searcher != null ) {
				m_Searcher.close();
				m_Searcher = null;
			}
			m_Hits = null;
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}

	public int getCount () {
		if ( m_bGetAll ) return m_Reader.numDocs();
		if ( m_Hits == null ) return 0;
		return m_Hits.length();
	}

	public int getMaximum () {
		return m_nMax;
	}

	public IMatch getNextMatch () {
		Match M = null;
		try {
			Document docTmp;
			if ( m_bGetAll ) {
				while ( true ) {
					m_nCurrent++;
					if ( m_nCurrent >= m_Reader.maxDoc() ) return null;
					if ( !m_Reader.isDeleted(m_nCurrent) ) break;
				}
				docTmp = m_Reader.document(m_nCurrent);
			}
			else {
				if ( m_Hits == null ) return null;
				if ( m_nCurrent+1 >= m_Hits.length() ) return null;
				m_nCurrent++;
				docTmp = m_Hits.doc(m_nCurrent);
			}
			
			M = new Match();
			if ( m_bGetAll ) M.setScore(100);
			else M.setScore((int)(m_Hits.score(m_nCurrent)*100));
			M.setSourceText(docTmp.get(FIELD_STEXT));
			M.setTargetText(docTmp.get(FIELD_TTEXT));
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
			Utils.showError(E.getLocalizedMessage(), null);
		}
		return M;
	}

	public void login (String p_sConnection,
		String p_sUsername,
		String p_sPassword)
	{
		// Nothing to do in the implementation
	}

	public void logout () {
		close();
	}

	public void open (String p_sName) {
		try {
			m_sPath = p_sName;
			m_Reader = IndexReader.open(m_sPath);
			m_Searcher = new IndexSearcher(m_Reader);
			m_Analyzer = new StandardAnalyzer();
			m_Parser = new QueryParser(FIELD_STEXT, m_Analyzer);
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}

	public int query (String p_sText) {
		try {
			m_Hits = null;
			m_nCurrent = -1;
			m_bGetAll = false;
			m_Hits = m_Searcher.search(m_Parser.parse(p_sText));
		}
		catch ( Exception E ) {
			m_Log.error(E.getLocalizedMessage());
			Utils.showError(E.getLocalizedMessage(), null);
		}
		if ( m_Hits == null ) return 0;
		return m_Hits.length();
	}

	public int query (IFilterItem p_FI) {
		// Better than nothing: use the text-only part
		return query(p_FI.getText(FilterItemText.PLAIN));
	}

	public void resetList () {
		m_nCurrent = -1;
	}

	public void setMaximum (int p_nValue) {
		m_nMax = p_nValue;
	}
}
