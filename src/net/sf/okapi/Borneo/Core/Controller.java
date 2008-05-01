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

package net.sf.okapi.Borneo.Core;

import java.io.File;
import java.util.Hashtable;

import net.sf.okapi.Borneo.Core.DBBase;
import net.sf.okapi.Borneo.Core.DBOptions;
import net.sf.okapi.Borneo.Core.H2Backend;
import net.sf.okapi.Borneo.Core.MySQLBackend;
import net.sf.okapi.Borneo.Actions.BaseAction;
import net.sf.okapi.Borneo.Actions.ExportPackage;
import net.sf.okapi.Borneo.Actions.ExtractSource;
import net.sf.okapi.Borneo.Actions.GenerateTarget;
import net.sf.okapi.Borneo.Actions.IAction;
import net.sf.okapi.Borneo.Actions.ImportPackage;
import net.sf.okapi.Borneo.Actions.ImportTranslation;
import net.sf.okapi.Borneo.Actions.UpdateSource;
import net.sf.okapi.Borneo.Actions.UpdateTarget;
import net.sf.okapi.Filter.FilterAccess;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.IParameters;
import net.sf.okapi.Library.Base.IParametersProvider;
import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Library.UI.LanguageManager;
import net.sf.okapi.Package.Manifest;

public class Controller implements IParametersProvider {

	private ILog                       m_Log;
	private IControllerUI              m_UI;
	private DBBase                     m_DB = null;
	private DBOptions                  m_DBOpt;
	private FilterAccess               m_FA;
	private Hashtable<String, String>  m_ActionData;
	private String                     sharedFolder;

	public Controller (IControllerUI p_UI) {
		m_UI = p_UI;
	}

	public void initialize (ILog log,
		String rootFolder)
		throws Exception
	{
		sharedFolder = Utils.getOkapiSharedFolder(rootFolder); 
		m_Log = log;
		m_DBOpt = new DBOptions();
		m_ActionData = new Hashtable<String, String>();
		m_FA = new FilterAccess(m_Log);
		m_FA.loadList(sharedFolder + File.separator + "filters.xml");
	}
	
	public boolean isProjectOpened () {
		return (( m_DB != null ) && m_DB.isProjectOpened() );
	}
	
	public DBBase getDB () {
		return m_DB;
	}
	
	public DBOptions getDBOptions () {
		return m_DBOpt;
	}
	
	public String[] getDBInfo () {
		
		if ( m_DB == null ) return new String[3];
		else return m_DB.getDBInfo();
	}

	public void close () {
		try {
			if ( m_DB != null ) {
				m_DB.logout();
				m_DB = null;
			}
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}

	public void openProject (String p_sName) {
		try {
			// Select a server if needed
			if ( m_DB == null ) {
				if ( !selectProjectServer() ) return;
			}
			
			// User the project parameter first
			String sName = p_sName;

			// If there is no project parameter, use the UI to select a project
			if ( sName == null ) {
				sName = m_UI.selectProject(m_DBOpt.getDBType());
				if ( sName == null ) return;
			}
			m_UI.startWaiting(Res.getString("OPENING_PROJECT"), false);
			m_DB.openProject(sName);
            m_UI.updateEverything();
            m_UI.setDocumentsView();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
		finally {
			m_UI.stopWaiting();
		}
	}
	
	public void closeProject () {
		try {
			if ( m_DB != null ) {
				m_DB.closeProject();
			}
			m_UI.reset();
			m_UI.updateEverything();
			m_UI.setSettingsView();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}

	public void createProject () {
		try {
			if ( m_DB == null ) {
				if ( !selectProjectServer() ) return;
			}
			String[] aData = m_UI.selectNewProjectInfo(m_DBOpt.getDBType());
			if ( aData == null ) return;
			m_UI.startWaiting(Res.getString("CREATING_PROJECT"), false);
			LanguageManager LM = new LanguageManager();
			LM.loadList(sharedFolder + File.separator + "languages.xml");
			String sLang = Utils.getDefaultSourceLanguage();
			m_DB.createProject(aData[0], aData[1], sLang,
				LM.getDefaultEncodingFromCode(sLang, Utils.getPlatformType()));
			m_UI.updateEverything();
            m_UI.setSettingsView();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
		finally {
			m_UI.stopWaiting();
		}
	}
	
	public boolean selectProjectServer ()
	{
    	try {
    		if ( !m_UI.selectServerType(m_DBOpt) ) return false;
    		closeProject();
			if ( m_DB != null ) m_DB.logout();
			if ( m_DBOpt.getDBType() == 0 ) {
				Class.forName("org.h2.Driver");
				if ( m_DB != null ) m_DB.logout();
				m_DB = new H2Backend();
			}
			else {
				Class.forName("com.mysql.jdbc.Driver");
				if ( m_DB != null ) m_DB.logout();
				m_DB = new MySQLBackend();
			}

			m_UI.startWaiting(Res.getString("CONNECTING_SERVER"), false);
			m_DB.login(m_DBOpt);
    	}
    	catch ( Exception E ) {
    		Utils.showError(E.getLocalizedMessage(), null);
    		if ( m_DB != null ) {
    			try {
    				m_DB.logout();
    			} catch ( Exception E2 ) {}
    			m_DB = null;
    		}
    		return false;
    	}
    	finally {
			m_UI.updateSettingsView();
    		m_UI.stopWaiting();
    	}
    	return true;
	}

	public void addTargetLanguages () {
		boolean bRes = false;
		try {
			String[] aLangs = m_UI.selectNewTargetLanguages();
			if ( aLangs == null ) return;
			m_UI.startWaiting(Res.getString("ADDING_TARGETS"), false);
			m_DB.startBatchMode();
			for ( int i=0; i<aLangs.length; i++ ) {
				m_DB.addTargetLanguage(aLangs[i]);
			}
			bRes = true;
		}
    	catch ( Exception E ) {
    		Utils.showError(E.getLocalizedMessage(), null);
    		bRes = false;
    	}
    	finally {
			try {
				m_DB.stopBatchMode(bRes);
			}
			catch ( Exception E ) {
				Utils.showError(E.getLocalizedMessage(), null);
			}
			m_UI.updateSettingsView();
			m_UI.updateDocumentsView();
    		m_UI.stopWaiting();
    	}
	}
	
	public void removeTargetLanguage (String p_sLang) {
		try {
			if ( p_sLang == null ) return;
			m_UI.startWaiting(Res.getString("REMOVING_TARGETS"), false);
			m_DB.removeTargetLanguage(p_sLang);
		}
    	catch ( Exception E ) {
    		Utils.showError(E.getLocalizedMessage(), null);
    	}
    	finally {
			m_UI.updateSettingsView();
			m_UI.updateDocumentsView();
    		m_UI.stopWaiting();
    	}
	}
	
	public void editTargetProperties (String p_sLang) {
		try {
			if ( p_sLang == null ) return;
			DBTarget DBT = m_DB.getTargetData(p_sLang);
			if ( DBT == null ) return;
			if ( !m_UI.editTargetProperties(DBT, p_sLang) ) return;
			m_UI.startWaiting(Res.getString("UPDATING_PROJECT"), false);
			m_DB.updateTargetData(p_sLang, DBT);
		}
    	catch ( Exception E ) {
    		Utils.showError(E.getLocalizedMessage(), null);
    	}
    	finally {
    		m_UI.stopWaiting();
    	}
	}
	
	public void changeSourceRoot () {
		try {
			String sNew = m_UI.selectNewSourceRoot();
			if ( sNew == null ) return;
			
			m_UI.startWaiting(Res.getString("UPDATING_PROJECT"), false);
			m_DB.setSourceRoot(sNew);
			m_DB.saveSettings();
		}
		catch ( Exception E ) {
    		Utils.showError(E.getLocalizedMessage(), null);
		}
    	finally {
			m_UI.updateDocumentsView();
			m_UI.stopWaiting();
    	}
	}
	
	public void importPackage (String p_sPath) {
		try {
			// get the manifest if we don't have it yet
			if ( p_sPath == null ) {
				p_sPath = m_UI.selectImportPackage();
				if ( p_sPath == null ) return;
			}
		
			m_UI.startWaiting(Res.getString("PROCESSING_DOCUMENTS"), true);

			// Read the manifest
			Manifest Mnf = new Manifest(m_Log);
			Mnf.load(p_sPath);

			// Check that we have the proper project
			if ( !m_DB.getProjectID().equals(Mnf.getProjectID()) ) {
				m_Log.error(String.format("This package is for project '%s'. The current project is '%s'.",
					Mnf.getProjectID(), m_DB.getProjectID()));
				return;
			}
			
			// Check and set the target language
			
			if ( m_DB.getTargetData(Mnf.getTargetLanguage()) == null )
			{
				//TODO: Externalize text
				m_Log.error(String.format("The manifest file indicates that the package is for '%s' which is not a target language in the project.",
						Mnf.getTargetLanguage()));
				return;
			}

			// Check the files
			Mnf.checkPackageContent();
			
			// Prompt the user to select which files to merge
			if ( !m_UI.editImportPackageOptions(Mnf) ) return;
			
			ImportPackage Actn = new ImportPackage(m_FA, m_DB);
			Actn.setManifest(Mnf);

			Actn.start();
			// Parameters not used in this action, all comes from the manifest
			Actn.execute(null, null);
			Actn.stop();
		}
		catch ( Exception E ) {
    		Utils.showError(E.getLocalizedMessage(), null);
		}
    	finally {
			m_UI.updateTargetView();
			m_UI.updateDocumentsView();
			m_UI.stopWaiting();
    	}
	}

	/**
	 * Launches a given action.
	 * @param p_sActionID Identifier of the action to execute.
	 * @param p_nDocScope A value < 0 for all selected documents, the DKey of the
	 * document to process otherwise.
	 * @param p_sTargetScope The language code of the target to process,
	 * or null for all targets.
	 * @param p_sCurrentTarget The language code of the current target.
	 */
	public void launchAction (String p_sActionID,
		int p_nDocScope,
		String p_sTargetScope,
		String p_sCurrentTarget)
	{
		IAction Actn = null;
		try {
			//TODO: Replace ifs by dynamic load, like for the filters
			if ( p_sActionID.equals(BaseAction.ID_EXTRACTSOURCE) ) {
				Actn = new ExtractSource(m_FA, m_DB);
			}
			else if ( p_sActionID.equals(BaseAction.ID_UPDATESOURCE) ) {
				Actn = new UpdateSource(m_FA, m_DB);
			}
			else if ( p_sActionID.equals(BaseAction.ID_UPDATETARGET) ) {
				Actn = new UpdateTarget(m_FA, m_DB);
			}
			else if ( p_sActionID.equals(BaseAction.ID_GENERATETARGET) ) {
				Actn = new GenerateTarget(m_FA, m_DB);
			}
			else if ( p_sActionID.equals(BaseAction.ID_EXPORTPACKAGE) ) {
				Actn = new ExportPackage(m_FA, m_DB);
			}
			else if ( p_sActionID.equals(BaseAction.ID_IMPORTTRANSLATION) ) {
				Actn = new ImportTranslation(m_FA, m_DB);
			}
			else {
				Utils.showError(String.format("Unknown action ID '%s'", p_sActionID), null);
				return;
			}

			// Get the action's options
			if ( Actn.hasOptions() ) {
				// If the storage exists, retrieve it
				IParameters Opt = Actn.getOptions(); 
				if ( m_ActionData.containsKey(Actn.getID()) )
					Opt.fromString(m_ActionData.get(Actn.getID()));
			}

			// Get the options
			Object[] aRes = m_UI.getLaunchOptions(Actn, p_nDocScope, p_sTargetScope, p_sCurrentTarget);
			if ( aRes == null ) return;
			m_UI.startWaiting(Res.getString("PROCESSING_DOCUMENTS"), true);

			// Store the options if needed
			if ( Actn.hasOptions() ) {
				m_ActionData.put(Actn.getID(), Actn.getOptions().toString());
			}
			
			// Cast the options
			int[] aDKeys = (int[])aRes[0];
			String[] aTargets = (String[])aRes[1];
			boolean bOpenResults = (aRes[2] != null);
			
			Actn.start();
			boolean bRes = Actn.execute(aDKeys, aTargets);
			Actn.stop();
			
			if ( bRes && bOpenResults ) {
				Actn.openResult();
			}
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
    	finally {
			m_UI.updateAllViews();
    		m_UI.stopWaiting();
    	}
	}

	// The location is a filter settings string
	public IParameters load (String location)
		throws Exception
	{
		String[] aRes = FilterAccess.splitFilterSettingsType1(m_DB.getParametersFolder(), location);
		m_FA.loadFilter(aRes[1], aRes[3]);
		return m_FA.getFilter().getParameters();
	}

	public IParameters createParameters (String location)
		throws Exception
	{
		String[] aRes = FilterAccess.splitFilterSettingsType1(m_DB.getParametersFolder(), location);
		m_FA.loadFilter(aRes[1], null);
		return m_FA.getFilter().getParameters();
	}

	
	public void save (String location,
		IParameters paramObject)
		throws Exception
	{
		String[] aRes = FilterAccess.splitFilterSettingsType1(m_DB.getParametersFolder(), location);
		paramObject.save(aRes[3], null);
	}

	public String[] splitLocation(String location) {
		return FilterAccess.splitFilterSettingsType1(m_DB.getParametersFolder(), location);
	}

}
