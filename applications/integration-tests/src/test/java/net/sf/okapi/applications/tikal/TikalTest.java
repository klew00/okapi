package net.sf.okapi.applications.tikal;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.sf.okapi.common.FileCompare;
import net.sf.okapi.common.Util;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TikalTest {
	
	private String javaTikal;
	private String root;
	private File rootAsFile;
	private FileCompare fc = new FileCompare();

	@Before
	public void setUp () throws URISyntaxException {
		//TODO: make this relative and OS-independent
		File file = new File(getClass().getResource("/htmltest.html").toURI());
		root = Util.getDirectoryName(file.getAbsolutePath());
		rootAsFile = new File(root);

		String distDir;
		String osName = System.getProperty("os.name");
		if ( osName.startsWith("Mac OS") ) { // Macintosh case
			distDir = "dist_cocoa-macosx";
			//TODO: How to detect carbon vs cocoa?
		}
		else if ( osName.startsWith("Windows") ) { // Windows case
			distDir = "dist_win32-x86";
		}
		else { // Assumes Unix or Linux
			if ( System.getProperty("os.arch").equals("x86_64") ) {
				distDir = "dist_gtk2-linux-x86_64";
			}
			else {
				distDir = "dist_gtk2-linux-x86";
			}
		}
		
		// Set the path for the jar
		String libDir = Util.getDirectoryName(root); // Go up one dir
		libDir = Util.getDirectoryName(libDir); // Go up one dir
		libDir = Util.getDirectoryName(libDir); // Go up one dir
		libDir = Util.getDirectoryName(libDir); // Go up one dir
		libDir += String.format("%sdeployment%smaven%s%s%slib%s",
			File.separator, File.separator, File.separator, distDir, File.separator, File.separator);
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
    public void testExtractMergeDTD () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("dtdtest.dtd.xlf"));
    	assertTrue(deleteOutputFile("dtdtest.out.dtd"));
    	// Extract
    	assertEquals(0, runTikal("-x dtdtest.dtd"));
    	assertTrue("File different from gold", compareWithGoldFile("dtdtest.dtd.xlf"));
    	// Merge
    	assertEquals(0, runTikal("-m dtdtest.dtd.xlf"));
    	assertTrue("File different from gold", compareWithGoldFile("dtdtest.out.dtd"));
    }

    @Test
    public void testExtractMergeHTML () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("htmltest.html.xlf"));
    	assertTrue(deleteOutputFile("htmltest.out.html"));
    	// Extract
    	assertEquals(0, runTikal("-x htmltest.html"));
    	assertTrue("File different from gold", compareWithGoldFile("htmltest.html.xlf"));
    	// Merge
    	assertEquals(0, runTikal("-m htmltest.html.xlf"));
    	assertTrue("File different from gold", compareWithGoldFile("htmltest.out.html"));
    }

    @Test
    public void testExtractMergeJSON () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("jsontest.json.xlf"));
    	assertTrue(deleteOutputFile("jsontest.out.json"));
    	// Extract
    	assertEquals(0, runTikal("-x jsontest.json"));
    	assertTrue("File different from gold", compareWithGoldFile("jsontest.json.xlf"));
    	// Merge
    	assertEquals(0, runTikal("-m jsontest.json.xlf"));
    	assertTrue("File different from gold", compareWithGoldFile("jsontest.out.json"));
    }

    @Test
    public void testExtractMergePO () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("potest.po.xlf"));
    	assertTrue(deleteOutputFile("potest.out.po"));
    	// Extract
    	assertEquals(0, runTikal("-x potest.po"));
    	assertTrue("File different from gold", compareWithGoldFile("potest.po.xlf"));
    	// Merge
    	assertEquals(0, runTikal("-m potest.po.xlf"));
    	assertTrue("File different from gold", compareWithGoldFile("potest.out.po"));
    }

    @Test
    public void testExtractMergePOMono () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("potest-mono.po.xlf"));
    	assertTrue(deleteOutputFile("potest-mono.out.po"));
    	// Extract
    	assertEquals(0, runTikal("-x potest-mono.po -fc okf_po-monolingual"));
    	assertTrue("File different from gold", compareWithGoldFile("potest-mono.po.xlf"));
    	// Merge
    	assertEquals(0, runTikal("-m potest-mono.po.xlf -fc okf_po-monolingual"));
    	assertTrue("File different from gold", compareWithGoldFile("potest-mono.out.po"));
    }

    @Test
    public void testExtractMergeODT () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("odttest.odt.xlf"));
    	assertTrue(deleteOutputFile("odttest.out.odt"));
    	// Extract
    	assertEquals(0, runTikal("-x odttest.odt"));
//TODO: zip    	assertTrue("File different from gold", compareWithGoldFile("odttest.odt.xlf"));
    	// Merge
    	assertEquals(0, runTikal("-m odttest.odt.xlf"));
//TODO: zip    	assertTrue("File different from gold", compareWithGoldFile("odttest.out.odt"));
    }

    @Test
    public void testExtractMergeTMX () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("tmxtest-attributes.tmx.xlf"));
    	assertTrue(deleteOutputFile("tmxtest-attributes.tmx.out.po"));
    	// Extract
    	assertEquals(0, runTikal("-x tmxtest-attributes.tmx -sl EN-US -tl FR-FR"));
    	assertTrue("File different from gold", compareWithGoldFile("tmxtest-attributes.tmx.xlf"));
    	// Merge
    	assertEquals(0, runTikal("-m tmxtest-attributes.tmx.xlf -sl EN-US -tl FR-FR"));
    	assertTrue("File different from gold", compareWithGoldFile("tmxtest-attributes.out.tmx"));
    }

    @Test
    public void testImportPensieve () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputDir("pensieveTM", true));
    	// Import
    	assertEquals(0, runTikal("-imp pensieveTM tmxtest-attributes.tmx -sl EN-US -tl FR-FR"));
    	// Check if we can query the TM (does not check the result)
    	assertEquals(0, runTikal("-q \"One entry in the TM.\" -pen pensieveTM -sl EN-US -tl FR-FR"));
    }

    private boolean compareWithGoldFile (String outputBase) {
    	String outputPath = root + File.separator + outputBase;
    	String goldPath = root + File.separator + "gold" + File.separator + outputBase; 
    	return fc.filesExactlyTheSame(outputPath, goldPath);
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
