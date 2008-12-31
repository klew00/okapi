package net.sf.okapi.applications.rainbow;

import java.io.File;
import java.net.URLDecoder;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Shell;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.applications.rainbow.lib.LanguageManager;
import net.sf.okapi.applications.rainbow.lib.Utils;
import net.sf.okapi.applications.rainbow.plugins.PluginsAccess;
import net.sf.okapi.common.Util;

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
	
	public void execute (Shell shell,
		String[] args)
	{
		try {
			this.shell = shell;
			printBanner();
			if ( args == null ) return;
			if ( args.length < 1 ) return;
		
			initialize();
			
			// First parameter is the project file
			prj = new Project(lm);
			prj.load(args[0]);
			
			
			// Then execute the commands
			for ( int i=1; i<args.length; i++ ) {
				launchUtility(args[i]);
			}
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
		finally {
			System.out.println("--- end Rainbow session ---");
		}
	}
	
	private void printBanner () {
		System.out.println("---------------------------");
		System.out.println("Rainbow - Command line mode");
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
			ud = new UtilityDriver(log, fa, plugins);
		}
		// Get the data for the utility and instantiate it
		ud.setData(prj, utilityID);
		// Run it
		//if ( !ud.checkParameters(shell) ) return;
		ud.execute(shell);
	}

	
}
