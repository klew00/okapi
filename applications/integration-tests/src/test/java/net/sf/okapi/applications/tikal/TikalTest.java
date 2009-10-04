package net.sf.okapi.applications.tikal;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.sf.okapi.common.FileCompare;
import net.sf.okapi.common.StreamGobbler;
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
    	assertEquals(0, runTikal("-x dtdtest.dtd -ie windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("dtdtest.dtd.xlf", "UTF-8"));
    	// Merge
    	assertEquals(0, runTikal("-m dtdtest.dtd.xlf -oe windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("dtdtest.out.dtd", "windows-1252"));
    }

    @Test
    public void testExtractMergeHTML () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("htmltest.html.xlf"));
    	assertTrue(deleteOutputFile("htmltest.out.html"));
    	// Extract
    	assertEquals(0, runTikal("-x htmltest.html -ie windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("htmltest.html.xlf", "UTF-8"));
    	// Merge
    	assertEquals(0, runTikal("-m htmltest.html.xlf -oe windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("htmltest.out.html", "windows-1252"));
    }

    @Test
    public void testExtractMergeJSON () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("jsontest.json.xlf"));
    	assertTrue(deleteOutputFile("jsontest.out.json"));
    	// Extract
    	assertEquals(0, runTikal("-x jsontest.json -ie windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("jsontest.json.xlf", "UTF-8"));
    	// Merge
    	assertEquals(0, runTikal("-m jsontest.json.xlf -oe windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("jsontest.out.json", "windows-1252"));
    }

    @Test
    public void testExtractMergePO () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("potest.po.xlf"));
    	assertTrue(deleteOutputFile("potest.out.po"));
    	// Extract
    	assertEquals(0, runTikal("-x potest.po -ie windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("potest.po.xlf", "UTF-8"));
    	// Merge
    	assertEquals(0, runTikal("-m potest.po.xlf -oe windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("potest.out.po", "windows-1252"));
    }

    @Test
    public void testExtractMergePOMono () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("potest-mono.po.xlf"));
    	assertTrue(deleteOutputFile("potest-mono.out.po"));
    	// Extract
    	assertEquals(0, runTikal("-x potest-mono.po -fc okf_po-monolingual -ie windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("potest-mono.po.xlf", "UTF-8"));
    	// Merge
    	assertEquals(0, runTikal("-m potest-mono.po.xlf -fc okf_po-monolingual -oe windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("potest-mono.out.po", "windows-1252"));
    }

    @Test
    public void testExtractMergeProperties () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("proptest.properties.xlf"));
    	assertTrue(deleteOutputFile("proptest.out.properties"));
    	// Extract
    	assertEquals(0, runTikal("-x proptest.properties -ie windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("proptest.properties.xlf", "UTF-8"));
    	// Merge
    	assertEquals(0, runTikal("-m proptest.properties.xlf -oe windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("proptest.out.properties", "windows-1252"));
    }

    @Test
    public void testExtractMergePropertiesNoEscapes () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("proptest.properties.xlf"));
    	assertTrue(deleteOutputFile("proptest.out.noesc.properties"));
    	// Extract
    	assertEquals(0, runTikal("-x proptest.properties -fc okf_properties-outputNotEscaped -ie windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("proptest.properties.xlf", "UTF-8"));
    	// Merge
    	assertEquals(0, runTikal("-m proptest.properties.xlf -fc okf_properties-outputNotEscaped -oe windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("proptest.out.properties", "proptest.out.noesc.properties", "windows-1252"));
    }

    @Test
    public void testExtractMergeODT () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("odttest.odt.xlf"));
    	assertTrue(deleteOutputFile("odttest.out.odt"));
    	// Extract
    	assertEquals(0, runTikal("-x odttest.odt -ie windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("odttest.odt.xlf", "UTF-8"));
    	// Merge
    	assertEquals(0, runTikal("-m odttest.odt.xlf"));
//TODO: zip    	assertTrue("File different from gold", compareWithGoldFile("odttest.out.odt"));
    }

    @Test
    public void testExtractMergeDOCX () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("docxtest.docx.xlf"));
    	assertTrue(deleteOutputFile("docxtest.out.docx"));
    	// Extract
    	assertEquals(0, runTikal("-x docxtest.docx -ie windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("docxtest.docx.xlf", "UTF-8"));
    	// Merge
    	assertEquals(0, runTikal("-m docxtest.docx.xlf"));
//TODO: zip    	assertTrue("File different from gold", compareWithGoldFile("odttest.out.odt"));
    }

    @Test
    public void testExtractMergeTMX () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("tmxtest-attributes.tmx.xlf"));
    	assertTrue(deleteOutputFile("tmxtest-attributes.tmx.out.po"));
    	// Extract
    	assertEquals(0, runTikal("-x tmxtest-attributes.tmx -sl EN-US -tl FR-FR -ie windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("tmxtest-attributes.tmx.xlf", "UTF-8"));
    	// Merge
    	assertEquals(0, runTikal("-m tmxtest-attributes.tmx.xlf -sl EN-US -tl FR-FR -oe windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("tmxtest-attributes.out.tmx", "windows-1252"));
    }

    @Test
    public void testExtractMergeXLIFF () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("xlifftest.xlf.xlf"));
    	assertTrue(deleteOutputFile("xlifftest.out.xlf"));
    	// Extract
    	assertEquals(0, runTikal("-x xlifftest.xlf"));
    	assertTrue("File different from gold", compareWithGoldFile("xlifftest.xlf.xlf", "UTF-8"));
    	// Merge
    	assertEquals(0, runTikal("-m xlifftest.xlf.xlf -oe windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("xlifftest.out.xlf", "UTF-8"));
    }

    @Test
    public void testExtractMergeXML () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("xmltest1.xml.xlf"));
    	assertTrue(deleteOutputFile("xmltest1.out.xml"));
    	// Extract
    	assertEquals(0, runTikal("-x xmltest1.xml -ie UTF-8"));
    	assertTrue("File different from gold", compareWithGoldFile("xmltest1.xml.xlf", "UTF-8"));
    	// Merge
    	assertEquals(0, runTikal("-m xmltest1.xml.xlf -oe UTF-8"));
    	assertTrue("File different from gold", compareWithGoldFile("xmltest1.out.xml", "UTF-8"));
    }

    @Test
    public void testExtractMergeTSV () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("tsvtest.txt.xlf"));
    	assertTrue(deleteOutputFile("tsvtest.out.txt"));
    	// Extract
    	assertEquals(0, runTikal("-x tsvtest.txt -ie windows-1252"));
    	assertTrue("File different from gold", compareWithGoldFile("tsvtest.txt.xlf", "UTF-8"));
    	// Merge
    	assertEquals(0, runTikal("-m tsvtest.txt.xlf -oe UTF-8"));
    	assertTrue("File different from gold", compareWithGoldFile("tsvtest.out.txt", "UTF-8"));
    }

    @Test
    public void testExtractMergeRESX () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("resxtest.resx.xlf"));
    	assertTrue(deleteOutputFile("resxtest.out.resx"));
    	// Extract
    	assertEquals(0, runTikal("-x resxtest.resx")); // Auto-assign okf_xml-resx
    	assertTrue("File different from gold", compareWithGoldFile("resxtest.resx.xlf", "UTF-8"));
    	// Merge
    	assertEquals(0, runTikal("-m resxtest.resx.xlf -ie windows-1252")); // Auto-assign okf_xml-resx
    	assertTrue("File different from gold", compareWithGoldFile("resxtest.out.resx", "windows-1252"));
    }

//    @Test
//    public void testExtractMergeTS () throws IOException, InterruptedException {
//    	// Delete previous output
//    	assertTrue(deleteOutputFile("tstest.ts.xlf"));
//    	assertTrue(deleteOutputFile("tstest.out.ts"));
//    	// Extract
//    	assertEquals(0, runTikal("-x tstest.ts -ie UTF-8")); // Auto-assign okf_ts
//    	assertTrue("File different from gold", compareWithGoldFile("tstest.ts.xlf", "UTF-8"));
//    	// Merge
//    	assertEquals(0, runTikal("-m tstest.ts.xlf -oe UTF-8")); // Auto-assign okf_ts
//    	assertTrue("File different from gold", compareWithGoldFile("tstest.out.ts", "UTF-8"));
//    }

    @Test
    public void testExtractMergeSRT () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputFile("srttest.srt.xlf"));
    	assertTrue(deleteOutputFile("srttest.out.srt"));
    	// Extract
    	assertEquals(0, runTikal("-x srttest.srt")); // Auto-assign okf_regex-srt
    	assertTrue("File different from gold", compareWithGoldFile("srttest.srt.xlf", "UTF-8"));
    	// Merge
    	assertEquals(0, runTikal("-m srttest.srt.xlf -ie UTF-8")); // Auto-assign okf_regex-srt
    	assertTrue("File different from gold", compareWithGoldFile("srttest.out.srt", "UTF-8"));
    }

    @Test
    public void testImportExportPensieve () throws IOException, InterruptedException {
    	// Delete previous output
    	assertTrue(deleteOutputDir("pensieveTM", true));
    	// Import
    	assertEquals(0, runTikal("-imp pensieveTM tmxtest-attributes.tmx -sl EN-US -tl FR-FR"));
    	// Check if we can query the TM (does not check the result)
    	assertEquals(0, runTikal("-q \"One entry in the TM.\" -pen pensieveTM -sl EN-US -tl FR-FR"));
    	
    	// Export now
    	assertEquals(0, runTikal("-2tmx pensieveTM -fc okf_pensieve -sl EN-US -tl FR-FR"));
    	assertTrue("File different from gold", compareWithGoldFile("pensieveTM.tmx", "UTF-8"));
    }

    @Test
    public void testConvertToTMX () throws IOException, InterruptedException {
    	// Test normal conversion
    	assertTrue(deleteOutputFile("potest.po.tmx"));
    	assertEquals(0, runTikal("-2tmx potest.po"));
    	assertTrue("File different from gold", compareWithGoldFile("potest.po.tmx", "potest.po.normal.tmx", "UTF-8"));
    	// Test normal conversion (target=source)
    	assertTrue(deleteOutputFile("potest.po.tmx"));
    	assertEquals(0, runTikal("-2tmx potest.po -trgsource"));
    	assertTrue("File different from gold", compareWithGoldFile("potest.po.tmx", "potest.po.source.tmx", "UTF-8"));
    	// Test normal conversion (target=empty)
    	assertTrue(deleteOutputFile("potest.po.tmx"));
    	assertEquals(0, runTikal("-2tmx potest.po -trgempty"));
    	assertTrue("File different from gold", compareWithGoldFile("potest.po.tmx", "potest.po.empty.tmx", "UTF-8"));
    }

    @Test
    public void testConvertToTable () throws IOException, InterruptedException {
    	// Test csv conversion
    	assertTrue(deleteOutputFile("potest.po.txt"));
    	assertEquals(0, runTikal("-2tbl potest.po -csv"));
    	assertTrue("File different from gold", compareWithGoldFile("potest.po.txt", "potest.po.normal.csv", "UTF-8"));
    	// Test csv conversion (codes=generic)
    	assertTrue(deleteOutputFile("potest.po.txt"));
    	assertEquals(0, runTikal("-2tbl potest.po -csv -generic"));
    	assertTrue("File different from gold", compareWithGoldFile("potest.po.txt", "potest.po.generic.csv", "UTF-8"));
    	// Test csv conversion (codes=tmx)
    	assertTrue(deleteOutputFile("potest.po.txt"));
    	assertEquals(0, runTikal("-2tbl potest.po -csv -tmx"));
    	assertTrue("File different from gold", compareWithGoldFile("potest.po.txt", "potest.po.tmx.csv", "UTF-8"));
    	// Test table conversion
    	assertTrue(deleteOutputFile("potest.po.txt"));
    	assertEquals(0, runTikal("-2tbl potest.po -tab"));
    	assertTrue("File different from gold", compareWithGoldFile("potest.po.txt", "potest.po.normal.tab", "UTF-8"));
    	// Test table conversion (codes=generic)
    	assertTrue(deleteOutputFile("potest.po.txt"));
    	assertEquals(0, runTikal("-2tbl potest.po -tab -generic"));
    	assertTrue("File different from gold", compareWithGoldFile("potest.po.txt", "potest.po.generic.tab", "UTF-8"));
    	// Test table conversion (codes=xliffgx)
    	assertTrue(deleteOutputFile("potest.po.txt"));
    	assertEquals(0, runTikal("-2tbl potest.po -tab -xliffgx"));
    	assertTrue("File different from gold", compareWithGoldFile("potest.po.txt", "potest.po.xliffgx.tab", "UTF-8"));
    }

    @Test
    public void testConvertToPO () throws IOException, InterruptedException {
    	// Test normal conversion
    	assertTrue(deleteOutputFile("tmxtest-attributes.tmx.po"));
    	assertEquals(0, runTikal("-2po tmxtest-attributes.tmx -sl EN-US -tl FR-FR"));
    	assertTrue("File different from gold", compareWithGoldFile("tmxtest-attributes.tmx.po", "UTF-8"));
    }

    @Test
    public void testQuerySeveralConnectors () throws IOException, InterruptedException {
    	assertEquals(0, runTikal("-imp pensieveTM tmxtest-attributes.tmx -sl EN-US -tl FR-FR"));
    	// Query several services
    	assertEquals(0, runTikal("-q \"Close the <b>application</b>.\" -sl EN-US -tl FR-FR -pen pensieveTM -mm mmDemo123 -opentran"));
    }

    
    private boolean compareWithGoldFile (String outputBase, String encoding) {
    	String outputPath = root + File.separator + outputBase;
    	String goldPath = root + File.separator + "gold" + File.separator + outputBase; 
    	return fc.compareFilesPerLines(outputPath, goldPath, encoding);
    }
    
    private boolean compareWithGoldFile (String outputBase,
    	String goldBase,
    	String encoding)
    {
    	String outputPath = root + File.separator + outputBase;
    	String goldPath = root + File.separator + "gold" + File.separator + goldBase; 
    	return fc.compareFilesPerLines(outputPath, goldPath, encoding);
    }
    
    private boolean deleteOutputFile (String filename) {
    	File f = new File(root + File.separator + filename);
    	if ( f.exists() ) {
    		return f.delete();
    	}
    	else return true;
    }
    
    private boolean deleteOutputDir (String dirname, boolean relative) {
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
