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
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Shell;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.lib.FormatManager;
import net.sf.okapi.applications.rainbow.lib.LanguageManager;
import net.sf.okapi.applications.rainbow.lib.Utils;
import net.sf.okapi.applications.rainbow.plugins.PluginsAccess;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.ui.BaseHelp;

public class CommandLine {

	private String rootFolder;
	private String sharedFolder;
	private LanguageManager lm;
	private Project prj;
	private Shell shell;
	private UtilityDriver ud;
	private FilterAccess fa;
	private PluginsAccess plugins;
	private BatchLog log;
	private LogHandler logHandler;
	private String utilityId;
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
				launchUtility(utilityId);
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
		prj = new Project(lm);
		prj.setInputRoot(0, rootFolder);
		prj.setInputRoot(1, rootFolder);
		prj.setInputRoot(2, rootFolder);
		boolean setOutSearch = false;
		int inpList = -1;
		
		for ( int i=0; i<args.length; i++ ) {
			arg = args[i];
			if ( "-p".equals(arg) ) { // Load a project
				prj.load(nextArg(args, ++i));
			}
			else if ( "-x".equals(arg) ) { // Execute utility
				utilityId = nextArg(args, ++i);
				continueAfter = true;
			}
			else if ( "-np".equals(arg) ) { // No prompt for options
				promptForOptions = false;
			}
			else if (( "-h".equals(arg) ) || ( "-?".equals(arg) )) { // Help
				help.showTopic(this, "index");
			}
			else if ( "-se".equals(arg) ) { // Source encoding
				prj.setSourceEncoding(nextArg(args, ++i));
			}
			else if ( "-te".equals(arg) ) { // Target encoding
				prj.setTargetEncoding(nextArg(args, ++i));
			}
			else if ( "-sl".equals(arg) ) { // Source language
				prj.setSourceLanguage(nextArg(args, ++i));
			}
			else if ( "-tl".equals(arg) ) { // Target language
				prj.setTargetLanguage(nextArg(args, ++i));
			}
			else if (( "-ir".equals(arg) ) || ( "-ir0".equals(arg) )) { // Input root list 0
				prj.setInputRoot(0, nextArg(args, ++i));
			}
			else if ( "-pd".equals(arg) ) {
				prj.setCustomParametersFolder(nextArg(args, ++i));
				prj.setUseCustomParametersFolder(true);
			}
			else if ( "-fs".equals(arg) ) {
				Input inp = prj.getLastItem(0);
				if ( inp == null ) { 
					throw new RuntimeException("-fs parameter defined before input path.");
				}
				else {
					inp.filterSettings = nextArg(args, ++i);
				}
			}
			else if ( "-o".equals(arg) ) { // Output file
				File f = new File(nextArg(args, ++i));
				prj.setOutputRoot(Util.getDirectoryName(f.getAbsolutePath()));
				prj.setUseOutputRoot(true);
				prj.pathBuilder.setUseExtension(false);
				prj.pathBuilder.setUseReplace(true);
				prj.pathBuilder.setReplace(Util.getFilename(f.getAbsolutePath(), true));
				setOutSearch = true;
			}
			else if ( !arg.startsWith("-") ) { // Input file
				if ( ++inpList > 2 ) {
					throw new RuntimeException("Too many input files.");
				}
				File f = new File(arg);
				String[] res = fm.guessFormat(f.getAbsolutePath());
				prj.inputLists.get(inpList).clear();
				prj.setInputRoot(inpList, Util.getDirectoryName(f.getAbsolutePath()));
				prj.addDocument(inpList, f.getAbsolutePath(), res[0], null, res[1]);
			}
			else {
				log.error("Invalid command line argument: "+args[i]);
				continueAfter = false;
			}
			
			// Sets the search part of the output builder if an output path was specified.
			if ( setOutSearch ) {
				Input inp = prj.getLastItem(0);
				if ( inp == null ) { 
					throw new RuntimeException("No input file specified.");
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
			throw new RuntimeException("Missing parameter in the command line.");
		}
		return args[index];
	}
	
	private void printBanner () {
		System.out.println("---------------------------");
		System.out.println("Rainbow - Command Line Mode");
		System.out.println("---------------------------");
	}
	
	private void initialize () throws Exception {
    	// Get the location of the main class source
    	File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
    	rootFolder = URLDecoder.decode(file.getAbsolutePath(),"utf-8"); //$NON-NLS-1$
    	// Remove the JAR file if running an installed version
    	if ( rootFolder.endsWith(".jar") ) rootFolder = Util.getDirectoryName(rootFolder); //$NON-NLS-1$
    	// Remove the application folder in all cases
    	rootFolder = Util.getDirectoryName(rootFolder);
		sharedFolder = Utils.getOkapiSharedFolder(rootFolder);
		help = new BaseHelp(rootFolder+File.separator+"help");

		log = new BatchLog();
		logHandler = new LogHandler(log);
	    Logger.getLogger("net.sf.okapi.logging").addHandler(logHandler); //$NON-NLS-1$
		
		lm = new LanguageManager();
		lm.loadList(sharedFolder + File.separator + "languages.xml"); //$NON-NLS-1$
		fa = new FilterAccess();
		fa.loadList(sharedFolder + File.separator + "filters.xml"); //$NON-NLS-1$
		plugins = new PluginsAccess();
		plugins.addAllPackages(sharedFolder);
	}
	
	private void launchUtility (String utilityID) {
		if ( utilityID == null ) return;
		// Save any pending data
		if ( ud == null ) {
			ud = new UtilityDriver(log, fa, plugins, help);
		}
		// Get the data for the utility and instantiate it
		ud.setData(prj, utilityID);
		// Run it
		if ( promptForOptions ) {
			if ( !ud.checkParameters(shell) ) return;
		}
		ud.execute(shell);
	}

}
