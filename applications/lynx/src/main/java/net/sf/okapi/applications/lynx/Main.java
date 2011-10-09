/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.applications.lynx;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;

import net.sf.okapi.lib.xliff.Fragment;

public class Main {

	protected final static int CMD_READ = 0;
	protected final static int CMD_REWRITE = 1;

	public static void main (String[] originalArgs) {
		
		Main prog = new Main();
		prog.printBanner();
		
		// Remove all empty arguments
		// This is to work around the "$1" issue in bash
		ArrayList<String> args = new ArrayList<String>();
		for ( String tmp : originalArgs ) {
			if ( tmp.length() > 0 ) args.add(tmp);
		}
		if ( args.size() == 0 ) {
			prog.printUsage();
			return;
		}
		if ( args.contains("-?") ) {
			prog.printUsage();
			return; // Overrides all arguments 
		}
		if ( args.contains("-h") || args.contains("--help") || args.contains("-help") ) {
			prog.printUsage();
			return; // Overrides all arguments
		}
		if ( args.contains("-i") || args.contains("--info")  || args.contains("-info") ) {
			prog.printInfo();
			return; // Overrides all arguments 
		}
		
		int command = CMD_READ;
		boolean verbose = false;
		int outputStyle = Fragment.STYLE_NODATA;
		ArrayList<File> inputFiles = new ArrayList<File>();
		
		for ( String arg : args ) {
			String opt = arg.toLowerCase();
			if ( opt.equals("-r") ) {
				// This is the default: nothing to do
				// But we have to catch the case to avoid syntax error
			}
			else if ( opt.equals("-rw") ) {
				command = CMD_REWRITE;
			}
			else if ( opt.equals("-verbose") ) {
				verbose = true;
			}
			else if ( opt.equals("-inside") ) {
				outputStyle = Fragment.STYLE_DATAINSIDE;
			}
			else if ( opt.equals("-outside") ) {
				outputStyle = Fragment.STYLE_DATAOUTSIDE;
			}
			else if ( arg.startsWith("-") ) {
				System.out.println("Invalid option: "+arg);
				prog.printUsage();
				return;
			}
			else {
				inputFiles.add(new File(arg));
			}
		}
		
		Rewriter rewriter = new Rewriter((command == CMD_READ) ? true : verbose,
			(command == CMD_REWRITE), outputStyle);
		rewriter.process(inputFiles);

	}

	private void printBanner () {
		System.out.println("-------------------------------------------------------------------------------"); //$NON-NLS-1$
		System.out.println("Okapi Lynx - Testing Tool for XLIFF 2.0");
		// The version will show as 'null' until the code is build as a JAR.
		System.out.println(String.format("Library version: %s", getClass().getPackage().getImplementationVersion()));
		System.out.println("-------------------------------------------------------------------------------"); //$NON-NLS-1$

	}
	
	private void printUsage () {
		System.out.println("Shows this screen: -? or -h");
		System.out.println("Shows version and other information: -i or -info");
		System.out.println("Reads the input file and displays the parsed results (default command):");
		System.out.println("   [-r] inputFile1 [inputFile2...]");
		System.out.println("Rewrites the input file to a new file (same name with an extra '.out'):");
		System.out.println("   -rw [-verbose] [-inside|-outside] inputFile1 [inputFile2...]"); 
	}
	
	private void printInfo () {
		Runtime rt = Runtime.getRuntime();
		rt.runFinalization();
		rt.gc();
		System.out.println("Java version: " + System.getProperty("java.version")); //$NON-NLS-1$
		System.out.println(String.format("Platform: %s, %s, %s",
			System.getProperty("os.name"), //$NON-NLS-1$ 
			System.getProperty("os.arch"), //$NON-NLS-1$
			System.getProperty("os.version"))); //$NON-NLS-1$
		NumberFormat nf = NumberFormat.getInstance();
		System.out.println(String.format("Java VM memory: free=%s KB, total=%s KB", //$NON-NLS-1$
			nf.format(rt.freeMemory()/1024),
			nf.format(rt.totalMemory()/1024)));
		System.out.println("-------------------------------------------------------------------------------"); //$NON-NLS-1$
	}
}
