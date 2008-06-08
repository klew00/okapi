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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class UtilityDriver {

	private ILog                  log;
	private Project               prj;
	private FilterAccess          fa;
	private IUtility              util;
	private IParametersEditor     editor;
	private PluginItem            pluginItem;
	private PluginsAccess         plugins;
	private final Logger          logger = LoggerFactory.getLogger(UtilityDriver.class);
	
	public UtilityDriver (ILog log,
		FilterAccess newFA,
		PluginsAccess newPlugins)
	{
		this.log = log;
		fa = newFA;
		plugins = newPlugins;
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
		catch ( Exception e ) {
			throw new RuntimeException(e);
		}
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
			
			if ( util.needsRoots() ) {
				util.setRoots(prj.getInputRoot(), prj.buildOutputRoot());
			}

			util.doProlog(prj.getSourceLanguage(), prj.getTargetLanguage());
			
			for ( Input item : prj.inputList ) {
				// Skip item without filter if the utility is filter-driven
				if ( util.isFilterDriven() && ( item.filterSettings.length() == 0 )) continue;
				// Otherwise: process
				log.message("Input: "+item.relativePath);
			
				// Initialize the input/output data
				String inputPath = prj.getInputRoot() + File.separator + item.relativePath;
				String outputPath = prj.buildTargetPath(item.relativePath);
				util.setInputData(inputPath, prj.buildSourceEncoding(item), item.filterSettings);
				util.setOutputData(outputPath, prj.buildTargetEncoding(item));

				if ( util.isFilterDriven() ) {
					IFilterDrivenUtility filterUtil = (IFilterDrivenUtility)util;
					// Load the filter
					fa.loadFilterFromFilterSettingsType1(prj.getParametersFolder(),
						item.filterSettings);
					InputStream input = new FileInputStream(inputPath);
					fa.inputFilter.initialize(input, inputPath,
						item.filterSettings, prj.buildSourceEncoding(item),
						prj.getSourceLanguage(), prj.getTargetLanguage());
					fa.inputFilter.setOutput(filterUtil);
					
					// Initialize the output if needed
					if ( filterUtil.needsOutputFilter() ) {
						// Initialize the output
						OutputStream output = new FileOutputStream(outputPath);
						fa.outputFilter.initialize(output, prj.buildTargetEncoding(item),
								prj.getTargetLanguage());
						filterUtil.setOutput(fa.outputFilter);
					}

					// Process the input 
					fa.inputFilter.process();
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
			log.endTask(null);
		}
	}
}
