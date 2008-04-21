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

import net.sf.okapi.Borneo.Actions.IAction;
import net.sf.okapi.Package.Manifest;

/**
 * This interface specifies the methods needed by the Borneo core controller
 * to interact with a UI.
 */
public interface IControllerUI {

	/**
	 * Starts display for long process.
	 * @param p_sText Text to display while waiting, for example in a status bar.
	 * @param p_bStartLog True to start log operations.
	 */
	public void startWaiting (String p_sText,
		boolean p_bStartLog);
	
	/**
	 * Stop the display for a long process.
	 */
	public void stopWaiting ();
	
	/**
	 * Selects a project name.
	 * @param p_nDBType Type of database (0 or 1).
	 * @return The name of the project. Returns null if the user canceled or an error occurred.
	 */
	public String selectProject (int p_nDBType);
	
	/**
	 * Refresh all the UI caches and updates the display. 
	 */
	public void updateEverything();
	
	/**
	 * Resets all the UI caches and variables, like at start time with no project open.
	 */
	public void reset ();

	/**
	 * Gets the information needed to create a new project.
	 * @param p_nDBType Type of database where the project will be created.
	 * @return An array of strings: 0=Name of the new project, 1=Path of the project
	 * (if p_nDBType==0), or path of the storage (for p_nDBType=1).
	 * Returns null if the user canceled or an error occurred.
	 */
	public String[] selectNewProjectInfo (int p_nDBType);
	
	/**
	 * Select the server.
	 * @param p_Data Server data. This object will be updated with the selected data
	 * if the method return true.
	 * @return True if the server is select and no error or cancellation occurred. 
	 */
	public boolean selectServerType (DBOptions p_Data);
	
	/**
	 * Gets a set of target languages to add to the project. This method is
	 * responsible for not providing target languages already in the project.
	 * @return An array of strings, each string the language code of a target
	 * language to add. Returns null if the user canceled or an error occurred.
	 */
	public String[] selectNewTargetLanguages ();
	
	/**
	 * Gets a new directory for the source documents root.
	 * @return The new source root, or null if the user canceled or an error occurred.
	 */
	public String selectNewSourceRoot ();
	
	/**
	 * Gets the path of a translation package to import.
	 * @return The full path of the package, or null if the user canceled or an error occurred.
	 */
	public String selectImportPackage ();
	
	/**
	 * Edits the options for importing a translation package.
	 * @param p_Manifest The manifest of the package to import (already loaded).
	 * @return True to continue, false if the user canceled or an error occurred.
	 */
	public boolean editImportPackageOptions (Manifest p_Manifest);
	
	/**
	 * Gets the parameters to execute an action.
	 * @param p_Action Action to execute.
	 * @param p_nDocScope Default document scope. Use negative value for the document
	 * currently selected in the UI. 
	 * @param p_sTargetScope Default target scope. Use null for all target.
	 * @param p_sCurrentTarget Current target.
	 * @return An array of objects: 0=An int[] with all the keys of the documents to process,
	 * 1=An String[] with all the language codes of the targets to process (or nul),
	 * 2=A boolean set to true if there will be result to open after the action is done.
	 */
	public Object[] getLaunchOptions (IAction p_Action,
		int p_nDocScope,
		String p_sTargetScope,
		String p_sCurrentTarget);
	
	/**
	 * Edits the properties of a given target language.
	 * @param p_Data The properties. This object will be updated with the new values
	 * when the method returns true.
	 * @param p_sLang Language code of the target being edited.
	 * @return True if there was no errors and no user cancellation.
	 */
	public boolean editTargetProperties (DBTarget p_Data,
		String p_sLang);
	
	/**
	 * Updates all UI views.
	 */
	public void updateAllViews();
	
	/**
	 * Updates the settings view.
	 */
	public void updateSettingsView();
	
	/**
	 * Updates the documents view.
	 */
	public void updateDocumentsView();
	
	/**
	 * Updates the target view.
	 */
	public void updateTargetView();
	
	/**
	 * Updates the source view.
	 */
	public void updateSourceView();
	
	/**
	 * Sets the UI to the documents view, if one exists.
	 */
	public void setDocumentsView();
	
	/**
	 * Sets the UI to the settings view, if one exists.
	 */
	public void setSettingsView();
}
