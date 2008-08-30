/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.lib.ILog;
import net.sf.okapi.applications.rainbow.plugins.PluginItem;
import net.sf.okapi.applications.rainbow.plugins.PluginsAccess;
import net.sf.okapi.applications.rainbow.utilities.IFilterDrivenUtility;
import net.sf.okapi.applications.rainbow.utilities.ISimpleUtility;
import net.sf.okapi.applications.rainbow.utilities.IUtility;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.filters.IOutputFilter;
import net.sf.okapi.common.ui.Dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class UtilityDriver {

	private ILog                  log;
	private Project               prj;
	private FilterAccess          fa;
	private IInputFilter          inpFilter;
	private IOutputFilter         outFilter;
	private IUtility              util;
	private IParametersEditor     editor;
	private PluginItem            pluginItem;
	private PluginsAccess         plugins;
	private final Logger          logger = LoggerFactory.getLogger("net.sf.okapi.logging");
	private String                outputFolder;
	
	public UtilityDriver (ILog log,
		FilterAccess newFA,
		PluginsAccess newPlugins)
	{
		this.log = log;
		fa = newFA;
		plugins = newPlugins;
	}
	
	/**
	 * Gets the current utility.
	 * @return The last utility loaded, or null.
	 */
	public IUtility getUtility () {
		return util;
	}

	public void setData (Project project,
		String utilityID) 
	{
		try {
			prj = project;
			
			if ( !plugins.containsID(utilityID) )
				throw new RuntimeException("Utility not found.");
			pluginItem = plugins.getItem(utilityID);
			util = (IUtility)Class.forName(pluginItem.pluginClass).newInstance();
			
			if ( pluginItem.editorClass.length() > 0 ) {
				editor = (IParametersEditor)Class.forName(pluginItem.editorClass).newInstance();
			}
			else editor = null;
		}
		catch ( InstantiationException e ) {
			throw new RuntimeException(e);
		}
		catch ( IllegalAccessException e ) {
			throw new RuntimeException(e);
		}
		catch ( ClassNotFoundException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean checkParameters (Shell shell) {
		try {
			if ( pluginItem == null ) return false;
			// If there are no options to ask for,
			// ask confirmation to launch the utility
			if ( util.hasParameters() ) {
				// Get any existing parameters for the utility in the project
				String tmp = prj.getUtilityParameters(util.getID());
				if (( tmp != null ) && ( tmp.length() > 0 )) {
					util.getParameters().fromString(tmp);
				}
				// Invoke the editor if there is one
				if ( editor != null ) {
					if ( !editor.edit(util.getParameters(), shell) ) return false;
					// Save the parameters in memory
					prj.setUtilityParameters(util.getID(), util.getParameters().toString());
				}
			}
			else {
				MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
				dlg.setMessage(String.format("You are about to execute the utility: %s\nDo you want to proceed?",
					pluginItem.name));
				dlg.setText(Util.getNameInCaption(shell.getText()));
				if ( dlg.open() != SWT.YES ) return false;
			}
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getMessage(), null);
			return false;
		}
		return true;
	}
	
	public void execute (Shell shell) {
		try {
			log.beginTask(pluginItem.name);
			
			if ( util.needsRoots() ) {
				util.setRoots(prj.getInputRoot(0), prj.buildOutputRoot(0));
			}
			util.setFilterAccess(fa, prj.getParametersFolder());

			util.doProlog(prj.getSourceLanguage(), prj.getTargetLanguage());

			util.resetLists();
			if ( prj.getList(0).size() == 0 ) {
				log.warning("There is no input document.");
			}
			
			int f = -1;
			for ( Input item : prj.getList(0) ) {
				f++;
				// Skip item without filter if the utility is filter-driven
				if ( util.isFilterDriven() && ( item.filterSettings.length() == 0 )) continue;
				// Otherwise: process
				log.message("-- Input: "+item.relativePath);
			
				// Initialize the main input
				String inputPath = prj.getInputRoot(0) + File.separator + item.relativePath;
				util.addInputData(inputPath, prj.buildSourceEncoding(item), item.filterSettings);
				// Initialize the main output
				String outputPath = prj.buildTargetPath(0, item.relativePath);
				util.addOutputData(outputPath, prj.buildTargetEncoding(item));

				// Add input/output info from other input lists if requested
				for ( int j=1; j<prj.inputLists.size(); j++ ) {
					// Does the utility requests this list?
					if ( j >= util.getInputCount() ) break; // No need to loop more
					// Do we have a corresponding input?
					if ( prj.inputLists.get(j).size() > f ) {
						// Data is available
						Input addtem = prj.getList(j).get(f);
						// Input
						util.addInputData(
							prj.getInputRoot(j) + File.separator + addtem.relativePath,
							prj.buildSourceEncoding(addtem),
							addtem.filterSettings);
						// Output
						util.addOutputData(
							prj.buildTargetPath(j, addtem.relativePath),
							prj.buildTargetEncoding(addtem));
					}
					else {
						// Case of the data not available
						// This is to allow some utilities to use a variable number of input
						// depending on their requirements. They ask for 3 inputs
						// and Rainbow gives them as much as possible or nulls otherwise.
						util.addInputData(null, null, null);
						util.addOutputData(null, null);
					}
				}
				
				if ( util.isFilterDriven() ) {
					IFilterDrivenUtility filterUtil = (IFilterDrivenUtility)util;
					// Load the filter
					Object[] filters = fa.loadFilterFromFilterSettingsType1(prj.getParametersFolder(),
						item.filterSettings, inpFilter, outFilter);
					inpFilter = (IInputFilter)filters[0];
					outFilter = (IOutputFilter)filters[1];
					
					InputStream input = new FileInputStream(inputPath);
					inpFilter.initialize(input, inputPath,
						item.filterSettings, prj.buildSourceEncoding(item),
						prj.getSourceLanguage(), prj.getTargetLanguage());
					inpFilter.setOutput(filterUtil);
					
					// Initialize the output if needed
					if ( filterUtil.needsOutputFilter() ) {
						// Initialize the output
						Util.createDirectories(outputPath);
						OutputStream output = new FileOutputStream(outputPath);
						outFilter.initialize(output, prj.buildTargetEncoding(item),
							prj.getTargetLanguage());
						filterUtil.setOutput(outFilter);
					}

					// Process the input 
					inpFilter.process();
				}
				else { // Not filter-driven, just execute with input file
					 ((ISimpleUtility)util).processInput();
				}
			}

			util.doEpilog();
		}
		catch ( Exception e ) {
			logger.error("Error with utility.", e);
		}
		finally {
			if ( util != null ) {
				outputFolder = util.getFolderAfterProcess();
			}
			log.endTask(null);
		}
	}
	
	String getFolderAfterProcess () {
		return outputFolder;
	}
}
