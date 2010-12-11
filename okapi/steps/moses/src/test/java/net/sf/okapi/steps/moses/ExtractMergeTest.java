package net.sf.okapi.steps.moses;

import java.io.File;
import java.net.URL;

import net.sf.okapi.common.FileCompare;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipelinedriver.BatchItemContext;
import net.sf.okapi.common.pipelinedriver.PipelineDriver;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.filters.xliff.XLIFFFilter;
import net.sf.okapi.steps.common.FilterEventsToRawDocumentStep;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExtractMergeTest {

	private String root;
	private LocaleId locEN = LocaleId.ENGLISH;
	private LocaleId locJA = LocaleId.JAPANESE;
	private FileCompare fc;

	public ExtractMergeTest () {
		URL url = ExtractMergeTest.class.getResource("/Test-XLIFF01.xlf");
		root = Util.getDirectoryName(url.getPath()) + File.separator;
		fc = new FileCompare();
	}

	@Test
	public void testExtraction () {
		// Make sure output does not exists
		File inFile = new File(root+"/Test-XLIFF01.xlf");
		File out1File = new File(root+"/Test-XLIFF01.xlf.txt");
		File gold1File = new File(root+"/Test-XLIFF01.xlf.txt_gold");
		File transFile = new File(root+"/Test-XLIFF01.xlf.txt_trans");
		File out2File = new File(root+"/Test-XLIFF01.out.xlf");
		
		// Make sure output are deleted
		out1File.delete();
		assertFalse(out1File.exists());
		out2File.delete();
		assertFalse(out2File.exists());
		
		// Set up the extraction pipeline
		PipelineDriver pd = new PipelineDriver();
		pd.addStep(new RawDocumentToFilterEventsStep(new XLIFFFilter()));
		pd.addStep(new ExtractionStep());
		pd.addBatchItem(inFile.toURI(), "UTF-8", "okf_xliff", locEN, locJA);
		// Execute it
		pd.processBatch();

		// Check output
		assertTrue(out1File.exists());
		assertTrue(fc.compareFilesPerLines(
			out1File.getAbsolutePath(), gold1File.getAbsolutePath(), "UTF-8"));

		// Setup the merging pipeline
		pd = new PipelineDriver();
		pd.addStep(new RawDocumentToFilterEventsStep(new XLIFFFilter()));
		pd.addStep(new MergingStep());
		pd.addStep(new FilterEventsToRawDocumentStep());
		// Two parallel inputs: 1=the original file, 2=the Moses translated file
		RawDocument rd1 = new RawDocument(inFile.toURI(), "UTF-8", locEN, locJA, "okf_xliff");
		RawDocument rd2 = new RawDocument(transFile.toURI(), "UTF-8", locJA);
		pd.addBatchItem(new BatchItemContext(rd1, out2File.toURI(), "UTF-8", rd2));
		// Execute it
		pd.processBatch();

		// Check output
		assertTrue(out2File.exists());
	}

}
