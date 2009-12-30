/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow;

import java.io.File;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Shell;

import net.sf.okapi.applications.rainbow.lib.FormatManager;
import net.sf.okapi.applications.rainbow.lib.LanguageManager;
import net.sf.okapi.applications.rainbow.lib.Utils;
import net.sf.okapi.applications.rainbow.plugins.PluginsAccess;
import net.sf.okapi.applications.rainbow.utilities.IUtility;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.filters.DefaultFilters;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.ui.BaseHelp;

public class CommandLine {

	private String rootFolder;
	private String sharedFolder;
	private LanguageManager lm;
	private Project prj;
	private Shell shell;
	private UtilityDriver ud;
	private FilterConfigurationMapper mapper;
	private PluginsAccess plugins;
	private BatchLog log;
	private LogHandler logHandler;
	private String utilityId;
	private String optionsFile;
	private boolean promptForOptions = true;
	private BaseHelp help;
	
	public void execute (Shell shell,
		String[] args)
	{
		try {
			this.shell = shell;
			printBanner();
			initialize();
			if ( !parseArguments(args) ) {
				return;
			}
			if ( utilityId != null ) {
				launchUtility();
			}
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parse the command line.
	 * @return True to execute something, false if error or exit immediately.
	 * @throws Exception 
	 */
	private boolean parseArguments (String[] args) throws Exception {
		String arg;
		boolean continueAfter = false;
		
		// Creates default project
		FormatManager fm = new FormatManager();
		fm.load(null); // TODO: implement real external file, for now it's hard-coded
		prj = new Project(lm);
		prj.setInputRoot(0, rootFolder, true);
		prj.setInputRoot(1, rootFolder, true);
		prj.setInputRoot(2, rootFolder, true);
		boolean setOutSearch = false;
		int inpList = -1;
		optionsFile = null;
		
		for ( int i=0; i<args.length; i++ ) {
			arg = args[i];
			if ( "-p".equals(arg) ) { // Load a project //$NON-NLS-1$
				prj.load(nextArg(args, ++i));
			}
			else if ( "-x".equals(arg) ) { // Execute utility //$NON-NLS-1$
				utilityId = nextArg(args, ++i);
				continueAfter = true;
			}
			else if ( "-np".equals(arg) ) { // No prompt for options //$NON-NLS-1$
				promptForOptions = false;
			}
			else if (( "-h".equals(arg) ) || ( "-?".equals(arg) )) { // Help //$NON-NLS-1$ //$NON-NLS-2$
				help.showTopic(this, "index", "commandLine.html"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else if ( "-se".equals(arg) ) { // Source encoding //$NON-NLS-1$
				prj.setSourceEncoding(nextArg(args, ++i));
			}
			else if ( "-te".equals(arg) ) { // Target encoding //$NON-NLS-1$
				prj.setTargetEncoding(nextArg(args, ++i));
			}
			else if ( "-sl".equals(arg) ) { // Source language //$NON-NLS-1$
				prj.setSourceLanguage(LocaleId.fromString(nextArg(args, ++i)));
			}
			else if ( "-tl".equals(arg) ) { // Target language //$NON-NLS-1$
				prj.setTargetLanguage(LocaleId.fromString(nextArg(args, ++i)));
			}
			else if (( "-ir".equals(arg) ) || ( "-ir0".equals(arg) )) { // Input root list 0 //$NON-NLS-1$ //$NON-NLS-2$
				prj.setInputRoot(0, nextArg(args, ++i), true);
			}
			else if ( "-pd".equals(arg) ) { //$NON-NLS-1$
				prj.setCustomParametersFolder(nextArg(args, ++i));
				prj.setUseCustomParametersFolder(true);
			}
			else if ( "-opt".equals(arg) ) { //$NON-NLS-1$
				optionsFile = nextArg(args, ++i);
			}
			else if ( "-fc".equals(arg) ) { //$NON-NLS-1$
				Input inp = prj.getLastItem(inpList);
				if ( inp == null ) { 
					throw new RuntimeException(Res.getString("CommandLine.fsBeforeInputError")); //$NON-NLS-1$
				}
				else {
					inp.filterConfigId = nextArg(args, ++i);
				}
			}
			else if ( "-o".equals(arg) ) { // Output file //$NON-NLS-1$
				File f = new File(nextArg(args, ++i));
				prj.setOutputRoot(Util.getDirectoryName(f.getAbsolutePath()));
				prj.setUseOutputRoot(true);
				prj.pathBuilder.setUseExtension(false);
				prj.pathBuilder.setUseReplace(true);
				prj.pathBuilder.setReplace(Util.getFilename(f.getAbsolutePath(), true));
				setOutSearch = true;
			}
			else if ( !arg.startsWith("-") ) { // Input file //$NON-NLS-1$
				if ( ++inpList > 2 ) {
					throw new RuntimeException(Res.getString("CommandLine.tooManyInput")); //$NON-NLS-1$
				}
				File f = new File(arg);
				boolean specialDir = (MainForm.NOEXPAND_EXTENSIONS.indexOf(Util.getExtension(arg))==-1);
				String[] res = fm.guessFormat(f.getAbsolutePath(), !specialDir);
				prj.inputLists.get(inpList).clear();
				prj.setInputRoot(inpList, Util.getDirectoryName(f.getAbsolutePath()), true);
				prj.addDocument(inpList, f.getAbsolutePath(), res[0], null, res[1], false);
			}
			else {
				log.error(Res.getString("CommandLine.invalidCommand")+args[i]); //$NON-NLS-1$
				continueAfter = false;
			}
			
			// Sets the search part of the output builder if an output path was specified.
			if ( setOutSearch ) {
				Input inp = prj.getLastItem(0);
				if ( inp == null ) { 
					throw new RuntimeException(Res.getString("CommandLine.noInput")); //$NON-NLS-1$
				}
				else {
					prj.pathBuilder.setSearch(inp.relativePath);
				}
			}
		}
		return continueAfter;
	}
	
	private String nextArg (String[] args, int index) {
		if ( index >= args.length ) {
			throw new RuntimeException(Res.getString("CommandLine.missingParameter")); //$NON-NLS-1$
		}
		return args[index];
	}
	
	private void printBanner () {
		System.out.println("-------------------------------------------------------------------------------"); //$NON-NLS-1$
		System.out.println(Res.getString("CommandLine.bannerApplication")); //$NON-NLS-1$
		System.out.println(Res.getString("CommandLine.bannerVersion")+getClass().getPackage().getImplementationVersion()); //$NON-NLS-1$
		System.out.println("-------------------------------------------------------------------------------"); //$NON-NLS-1$
	}
	
	private void initialize () throws Exception {
    	// Get the location of the main class source
    	File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
    	rootFolder = URLDecoder.decode(file.getAbsolutePath(),"utf-8"); //$NON-NLS-1$
    	boolean fromJar = rootFolder.endsWith(".jar");
    	// Remove the JAR file if running an installed version
    	if ( fromJar ) rootFolder = Util.getDirectoryName(rootFolder); //$NON-NLS-1$
    	// Remove the application folder in all cases
    	rootFolder = Util.getDirectoryName(rootFolder);
		sharedFolder = Utils.getOkapiSharedFolder(rootFolder, fromJar);
		help = new BaseHelp(rootFolder+File.separator+"help"); //$NON-NLS-1$

		log = new BatchLog();
		logHandler = new LogHandler(log);
		logHandler.setLevel(Level.INFO);
		Logger.getLogger("").addHandler(logHandler); //$NON-NLS-1$
		
		lm = new LanguageManager();
		lm.loadList(sharedFolder + File.separator + "languages.xml"); //$NON-NLS-1$
		
		// Set up the filter configuration mapper
		mapper = new FilterConfigurationMapper();
		DefaultFilters.setMappings(mapper, false, true);

		plugins = new PluginsAccess();
		plugins.addAllPackages(sharedFolder);
	}
	
	private void launchUtility () {
		if ( utilityId == null ) return;
		// Create the utility driver if needed
		if ( ud == null ) {
			mapper.setCustomConfigurationsDirectory(prj.getParametersFolder());
			mapper.updateCustomConfigurations();
			ud = new UtilityDriver(log, mapper, plugins, help, false);
		}
		
		// Get default/project data for the utility and instantiate the utility object
		ud.setData(prj, utilityId);
		IUtility util = ud.getUtility();
		
		// Override the options if a file is provided from the command-line
		if ( optionsFile != null ) {
			if ( util.hasParameters() ) {
				// Ignore errors to allow to create an options file
				File f = new File(optionsFile);
				util.getParameters().load(Util.toURI(f.getAbsolutePath()), true);
			}
		}
		
		// Prompt to edit the parameters if requested 
		if ( promptForOptions ) {
			if ( !ud.checkParameters(shell) ) return;
			// Save the file if needed
			if (( optionsFile != null ) && ( util.hasParameters() )) {
				util.getParameters().save(optionsFile);
			}
		}
		
		// Run the utility
		ud.execute(shell);
	}

}
