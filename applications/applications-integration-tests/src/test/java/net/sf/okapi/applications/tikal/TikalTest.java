package net.sf.okapi.applications.tikal;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.sf.okapi.common.Util;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TikalTest {
	
	private String javaTikal;
	private String root;
	private File rootAsFile;

	@Before
	public void setUp () throws URISyntaxException {
		//TODO: make this relative and OS-independent
		File file = new File(getClass().getResource("/htmltest.html").toURI());
		root = Util.getDirectoryName(file.getAbsolutePath());
		rootAsFile = new File(root);

		// Set the path for the jar
		String libDir = Util.getDirectoryName(root); // Go up one dir
		libDir = Util.getDirectoryName(libDir); // Go up one dir
		libDir = Util.getDirectoryName(libDir); // Go up one dir
		libDir = Util.getDirectoryName(libDir); // Go up one dir
		libDir += String.format("%sdeployment%smaven%sdist_win32-x86%slib%s",
			File.separator, File.separator, File.separator, File.separator, File.separator);
		javaTikal = "java -jar " + libDir + "tikal.jar";
	}
	
    @Test
    public void testSimpleCall () throws IOException, InterruptedException {
    	// Simple run, no arguments
    	assertEquals(0, runTikal(null));
    }

    @Test
    public void testHelpCall () throws IOException, InterruptedException {
    	// Simple run, one invalid argument
    	assertEquals(0, runTikal("-?"));
    }

    @Test
    public void testExtractMergeHTML () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("htmltest.html.xlf"));
    	assertTrue(deleteOutputFile("htmltest.out.html"));
    	// Extract
    	assertEquals(0, runTikal("-x htmltest.html"));
    	//TODO: compare output with gold one
    	// Merge
    	assertEquals(0, runTikal("-m htmltest.html.xlf"));
    	//TODO: compare output with gold one
    }

    @Test
    public void testExtractMergeTMX () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("withattributes.tmx.xlf"));
    	assertTrue(deleteOutputFile("withattributes.out.tmx"));
    	// Extract
    	assertEquals(0, runTikal("-x withattributes.tmx -sl EN-US -tl FR-FR"));
    	// Merge
    	assertEquals(0, runTikal("-m withattributes.tmx.xlf -sl EN-US -tl FR-FR"));
    	//TODO: compare output with gold one
    }

    @Test
    public void testImportPensieve () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputDir("pensieveTM", true));
    	// Import
    	assertEquals(0, runTikal("-imp pensieveTM withattributes.tmx -sl EN-US -tl FR-FR"));
    	//TODO: compare output with gold one
    }

    private boolean deleteOutputFile (String filename) {
    	File f = new File(root + File.separator + filename);
    	if ( f.exists() ) {
    		return f.delete();
    	}
    	else return true;
    }
    
    public boolean deleteOutputDir (String dirname, boolean relative) {
    	File d;
    	if ( relative ) d = new File(root + File.separator + dirname);
    	else d = new File(dirname);
    	if ( d.isDirectory() ) {
    		String[] children = d.list();
    		for ( int i=0; i<children.length; i++ ) {
    			boolean success = deleteOutputDir(d.getAbsolutePath() + File.separator + children[i], false);
    			if ( !success ) {
    				return false;
    			}
    		}
    	}
    	if ( d.exists() ) return d.delete();
    	else return true;
    }
    
    private int runTikal (String extraArgs) throws IOException, InterruptedException {
    	Process p = Runtime.getRuntime().exec(javaTikal + ((extraArgs==null) ? "" : " "+extraArgs),
    		null, rootAsFile);
    	StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "err");            
    	StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "out");
    	errorGobbler.start();
    	outputGobbler.start();
    	p.waitFor();
    	return p.exitValue();
    }

}
