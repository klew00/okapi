/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.lib.ILog;
import net.sf.okapi.applications.rainbow.plugins.PluginItem;
import net.sf.okapi.applications.rainbow.plugins.PluginsAccess;
import net.sf.okapi.applications.rainbow.utilities.CancelEvent;
import net.sf.okapi.applications.rainbow.utilities.CancelListener;
import net.sf.okapi.applications.rainbow.utilities.ISimpleUtility2;
import net.sf.okapi.applications.rainbow.utilities.IFilterDrivenUtility2;
import net.sf.okapi.applications.rainbow.utilities.IUtility2;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterWriter;
import net.sf.okapi.common.ui.Dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class UtilityDriver2 implements CancelListener {

	private final Logger logger = LoggerFactory.getLogger("net.sf.okapi.logging");
	private ILog log;
	private Project prj;
	private FilterAccess fa;
	private IFilter filter;
	private IFilterWriter filterWriter;
	private IUtility2 utility;
	private IParametersEditor editor;
	private PluginItem pluginItem;
	private PluginsAccess plugins;
	private String outputFolder;
	private boolean stopProcess;
	
	public UtilityDriver2 (ILog log,
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
	public IUtility2 getUtility () {
		return utility;
	}

	public void setData (Project project,
		String utilityName) 
	{
		try {
			prj = project;
			if ( !plugins.containsID(utilityName) )
				throw new RuntimeException("Utility not found: "+utilityName);
			pluginItem = plugins.getItem(utilityName);
			utility = (IUtility2)Class.forName(pluginItem.pluginClass).newInstance();
			
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
			if ( utility.hasParameters() ) {
				// Get any existing parameters for the utility in the project
				String tmp = prj.getUtilityParameters(utility.getName());
				if (( tmp != null ) && ( tmp.length() > 0 )) {
					utility.getParameters().fromString(tmp);
				}
				// Invoke the editor if there is one
				if ( editor != null ) {
					if ( !editor.edit(utility.getParameters(), shell) ) return false;
					// Save the parameters in memory
					prj.setUtilityParameters(utility.getName(), utility.getParameters().toString());
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
			stopProcess = false;

			// Set its parameters as required
			if ( !checkParameters(shell) ) return; // User cancel or error
			// Set the run-time parameters
			//TODO (root), etc.
			
			// Warning for empty list
			if ( prj.getList(0).size() == 0 ) {
				log.warning("There is no input document.");
			}

			// For each input file
			int f = -1;
			for ( Input item : prj.getList(0) ) {
				f++;
				// Skip item without filter if it's a filter-driven utility
				if ( utility.isFilterDriven() && ( item.filterSettings.length() == 0 )) continue;
				// Otherwise: process
				log.message("-- Input: "+item.relativePath);

				// Initialize the main input
				String inputPath = prj.getInputRoot(0) + File.separator + item.relativePath;
				utility.addInputData(inputPath, prj.buildSourceEncoding(item), item.filterSettings);
				// Initialize the main output
				String outputPath = prj.buildTargetPath(0, item.relativePath);
				utility.addOutputData(outputPath, prj.buildTargetEncoding(item));

				// Add input/output data from other input lists if requested
				for ( int j=1; j<prj.inputLists.size(); j++ ) {
					// Does the utility requests this list?
					if ( j >= utility.getRequestedInputCount() ) break; // No need to loop more
					// Do we have a corresponding input?
					if ( prj.inputLists.get(j).size() > f ) {
						// Data is available
						Input addtem = prj.getList(j).get(f);
						// Input
						utility.addInputData(
							prj.getInputRoot(j) + File.separator + addtem.relativePath,
							prj.buildSourceEncoding(addtem),
							addtem.filterSettings);
						// Output
						utility.addOutputData(
							prj.buildTargetPath(j, addtem.relativePath),
							prj.buildTargetEncoding(addtem));
					}
					else throw new RuntimeException("No more input files available.");
				}
				
				// Feedback event handling
				utility.addCancelListener(this);

				// Executes the utility
				if ( utility.isFilterDriven() ) {
					// Set the proper type of utility
					IFilterDrivenUtility2 filterUtility = (IFilterDrivenUtility2)utility;

					// Load the filter and the filterWriter if needed
					Object[] filters = fa.loadFilterFromFilterSettingsType1(prj.getParametersFolder(),
						item.filterSettings, filter, filterWriter);
					
					// Set the filter
					filter = (IFilter)filters[0];
					filter.setOptions(prj.getSourceLanguage(), prj.buildSourceEncoding(item));
					
					// Set the filter writer
					filterWriter = (IFilterWriter)filters[1];
					filterWriter.setOptions(prj.getTargetLanguage(), prj.buildTargetEncoding(item));
					
					// Process
					FilterEvent event;
					filter.open(inputPath);
					while ( filter.hasNext() ) {
						event = filter.next();
						filterUtility.handleEvent(event);
						filterWriter.handleEvent(event);
					}
					filter.close();
				}
				else {
					((ISimpleUtility2)utility).processInput();
				}
			}			
			
		}
		catch ( Exception e ) {
			if ( filter != null ) filter.close();
			if ( filterWriter != null ) filterWriter.close();
			if ( utility != null ) utility.finish();
			logger.error("Error with utility.", e);
		}
		finally {
			if ( stopProcess ) {
				logger.warn("Process interrupted by user.");
			}
//			if ( utility != null ) {
//				outputFolder = util.getFolderAfterProcess();
//			}
			log.endTask(null);
		}
	}
	
	private void executeFilterDrivenUtility (Input item) {
		
		
	}
	
	String getFolderAfterProcess () {
		return outputFolder;
	}

	public void cancelOccurred (CancelEvent event) {
		stopProcess = true;
		if ( filter != null ) filter.cancel();
	}
}
