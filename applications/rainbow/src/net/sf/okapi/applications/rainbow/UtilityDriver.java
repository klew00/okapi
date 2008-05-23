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

package net.sf.okapi.applications.rainbow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.lib.ILog;
import net.sf.okapi.applications.rainbow.plugins.PluginItem;
import net.sf.okapi.applications.rainbow.plugins.PluginsAccess;
import net.sf.okapi.applications.rainbow.utilities.IUtility;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class UtilityDriver {

	ILog                log;
	Project             prj;
	FilterAccess        fa;
	IUtility            util;
	IParametersEditor   editor;
	PluginItem          pluginItem;
	String              utilityID;
	PluginsAccess       plugins;
	Shell               shell;
	
	public UtilityDriver (ILog newLog,
		FilterAccess newFA,
		PluginsAccess newPlugins,
		Shell newShell)
	{
		log = newLog;
		fa = newFA;
		plugins = newPlugins;
		shell = newShell;
	}

	public void setData (Project newProject,
		String newUtilityID)
		throws Exception
	{
		prj = newProject;
		utilityID = newUtilityID;
		
		if ( !plugins.containsID(newUtilityID) )
			throw new Exception("Utility not found.");
		pluginItem = plugins.getItem(newUtilityID);
		util = (IUtility)Class.forName(pluginItem.pluginClass).newInstance();
		
		if ( pluginItem.editorClass.length() > 0 ) {
			editor = (IParametersEditor)Class.forName(pluginItem.editorClass).newInstance();
		}
		else editor = null;
	}
	
	public void execute (Shell shell) {
		try {
			if ( pluginItem == null ) return;
			// If there are no options to ask for,
			// ask confirmation to launch the utility
			if ( util.hasParameters() ) {
				if ( editor != null ) {
					if ( !editor.edit(util.getParameters(), shell) ) return;
				}
			}
			else {
				MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
				dlg.setMessage(String.format("You are about to execute the utility: %s\nDo you want to proceed?",
					pluginItem.name));
				dlg.setText(Util.getNameInCaption(shell.getText()));
				if ( dlg.open() != SWT.YES ) return;
			}
			
			log.beginTask(pluginItem.name);
			
			if ( util.needRoot() ) {
				util.setRoot(prj.inputRoot);
			}

			util.doProlog(prj.sourceLanguage, prj.targetLanguage);
			
			for ( Input item : prj.inputList ) {
				if ( item.filterSettings.length() == 0 ) continue;
				log.message("Input: "+item.relativePath);
				
				// Load the filter
				fa.loadFilterFromFilterSettingsType1(prj.paramsFolder,
					item.filterSettings);
				
				// Initialize the input
				String inputPath = prj.inputRoot + File.separator + item.relativePath;
				InputStream input = new FileInputStream(inputPath);
				fa.inputFilter.initialize(input, inputPath, prj.buildSourceEncoding(item),
					prj.sourceLanguage, prj.targetLanguage);
				fa.inputFilter.setOutput(util);
				
				if ( util.needOutput() ) {
					// Initialize the output
					OutputStream output = new FileOutputStream(prj.buildTargetPath(item.relativePath));
					fa.outputFilter.initialize(output, prj.buildTargetEncoding(item),
							prj.targetLanguage);
					util.setOutput(fa.outputFilter);
				}
				
				// Process the input 
				fa.inputFilter.process();
			}

			util.doEpilog();
		}
		catch ( Exception e ) {
			e.printStackTrace();
			log.error(e.getLocalizedMessage());
		}
		finally {
			log.endTask(null);
		}
	}
}
