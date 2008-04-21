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

package net.sf.okapi.Filter;

import java.io.File;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.okapi.Library.Base.FilterSettingsMarkers;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.IParameters;
import net.sf.okapi.Library.Base.IParametersEditor;
import net.sf.okapi.Library.Base.Utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FilterAccess {
	
	private Hashtable<String, FilterAccessItem>  m_htFilters;
	private IFilter                              m_Flt;
	private ILog                                 m_Log;
	private Hashtable<String, FilterAccessItem>  m_htEditors;
	private IParametersEditor                    m_Editor;
	private IParameters                          m_Params;
	
	public FilterAccess (ILog p_Log) {
		m_Log = p_Log;
		m_htFilters = new Hashtable<String, FilterAccessItem>();
		m_htEditors = new Hashtable<String, FilterAccessItem>();
	}
	
	public ILog getLog () {
		return m_Log;
	}
	
	/**
	 * Gets an IFilter interface to the filter currently loaded.
	 * @return The IFilter interface, or null if no filter is loaded.
	 */
	public IFilter getFilter () {
		return m_Flt;
	}
	
	public String getFilterIdentifier () {
		if ( m_Flt == null ) return null;
		else return m_Flt.getIdentifier();
	}
	
	/**
	 * Loads the list of accessible filters.
	 * The list is stored in an XML file of the following format:
	 * <okapiFilters>
	 *  <filter id="okf_json" class="net.sf.okapi.Filter.JSON.Filter"/>
	 *  ...
	 *  <editor id="okf_json" class="net.sf.okapi.Filter.JSON.ParametersForm"/>
	 *  ...
	 * </okapiFilters>
	 * @param p_sPath Full path of the list file to load.
	 */
	public void loadList (String p_sPath)
		throws Exception
	{
		try {
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Document Doc = Fact.newDocumentBuilder().parse(new File(p_sPath));
			
			NodeList NL = Doc.getElementsByTagName("filter");
			m_htFilters.clear();
			FilterAccessItem FAI;
			for ( int i=0; i<NL.getLength(); i++ ) {
				Node N = NL.item(i).getAttributes().getNamedItem("id");
				if ( N == null ) throw new Exception("The attribute 'id' is missing.");
				FAI = new FilterAccessItem();
				String sID = N.getTextContent();
				N = NL.item(i).getAttributes().getNamedItem("class");
				if ( N == null ) throw new Exception("The attribute 'class' is missing.");
				FAI.m_sClass = N.getTextContent();
				m_htFilters.put(sID, FAI);
			}

			NL = Doc.getElementsByTagName("editor");
			m_htEditors.clear();
			for ( int i=0; i<NL.getLength(); i++ ) {
				Node N = NL.item(i).getAttributes().getNamedItem("id");
				if ( N == null ) throw new Exception("The attribute 'id' is missing.");
				FAI = new FilterAccessItem();
				String sID = N.getTextContent();
				N = NL.item(i).getAttributes().getNamedItem("class");
				if ( N == null ) throw new Exception("The attribute 'class' is missing.");
				FAI.m_sClass = N.getTextContent();
				m_htEditors.put(sID, FAI);
			}
		}
		catch ( Exception E ) {
			throw E;
		}
	}
	
	/**
	 * Loads a filter and its parameters (if necessary). If the filter is the 
	 * filter currently loaded, it is not re-loaded. The parameters are re-loaded if
	 * the filter is different, if the parameters have changed, or if the file holding
	 * the parameters is newer than the last time the parameters where loaded. 
	 * @param p_sSettings Filter settings string that indicates the filter and 
	 * the parameters to load.
	 */
	public void loadFilter (String p_sSettings)
		throws Exception
	{
		try {
			// Parse the filter settings string
			String[] aRes = Utils.splitFilterSettings(p_sSettings);

			// If the filter ID starts with NNN. (e.g. 123.okf_xml...)
			// we remove the NNN. part. That part is reserved for multi-file storage info
			if ( Character.isDigit(aRes[1].charAt(0)) ) {
				int n = aRes[1].indexOf('.');
				if ( n != -1 ) aRes[1] = aRes[1].substring(n+1);
			}

			// Map the ID to the class, and instantiate the filter
			if ( !m_htFilters.containsKey(aRes[1]) )
				throw new Exception("Undefined filter ID.");
			m_Flt = (IFilter)Class.forName(m_htFilters.get(aRes[1]).m_sClass).newInstance();
			m_Flt.initialize(m_Log);
			
			// Load the parameters
			m_Flt.loadSettings(p_sSettings, false);
		}
		catch ( Exception E ) {
			throw E;
		}
	}

	public void loadEditor (String p_sSettings)
		throws Exception
	{
		try {
			// Parse the filter settings string
			String[] aRes = Utils.splitFilterSettings(p_sSettings);
	
			// If the filter ID starts with NNN. (e.g. 123.okf_xml...)
			// we remove the NNN. part. That part is reserved for multi-file storage info
			if ( Character.isDigit(aRes[1].charAt(0)) ) {
				int n = aRes[1].indexOf('.');
				if ( n != -1 ) aRes[1] = aRes[1].substring(n+1);
			}
	
			// Map the ID to the class, and instantiate the filter
			if ( !m_htEditors.containsKey(aRes[1]) )
				throw new Exception("Undefined editor ID.");
			m_Editor = (IParametersEditor)Class.forName(m_htEditors.get(aRes[1]).m_sClass).newInstance();
			
			// Load the parameters
			m_Params = m_Editor.createParameters();
			m_Params.load(Utils.makeParametersFullPath(p_sSettings), false);
		}
		catch ( Exception E ) {
			throw E;
		}
	}
	
	/**
	 * Edits the parameters for a given filter settings file. On success
	 * the parameter file pointed by the given filter settings string is saved
	 * with the changes. 
	 * @param p_sSettings The filter setting string of the parameters
	 * file to edit.
	 * @param p_bNew True if this parameters file is to be created.
	 * @return True if the edit was successful, false if the use canceled
	 * or if an error occurred.
	 */
	public boolean editFilterSettings (String p_sSettings,
		boolean p_bNew,
		Object p_Object)
	{
		try {
			if ( p_sSettings.indexOf(FilterSettingsMarkers.PARAMETERSSEP) == -1 ) {
				// Cannot edit the default
				throw new Exception(Res.getString("CANTEDIT_DEFAULTPARAMETERS"));
			}
			String sPath = Utils.makeParametersFullPath(p_sSettings);

			if ( p_bNew ) {
				// Create an empty file if needed
				Utils.createDirectories(sPath); // Create the folders if needed
				File F = new File(sPath);
				F.createNewFile();
			}
			loadEditor(p_sSettings); // Also creates and loads the parameters
			m_Editor.edit(m_Params, p_Object);
			m_Params.save(sPath);
		}
		catch ( Exception E ) {
			return false;
		}
		return true;
	}
}
