package net.sf.okapi.test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.DocumentData;
import net.sf.okapi.common.pipeline.FilterEventsToRawDocumentStep;
import net.sf.okapi.common.pipeline.FilterEventsWriterStep;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.pipeline.Pipeline;
import net.sf.okapi.common.pipeline.PipelineDriver;
import net.sf.okapi.common.pipeline.RawDocumentToFilterEventsStep;
import net.sf.okapi.common.pipeline.RawDocumentWriterStep;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.steps.bomconversion.BOMConversionStep;
import net.sf.okapi.steps.textmodification.TextModificationStep;

public class RunTest {
	
	private ArrayList<ProjectItem> proj = new ArrayList<ProjectItem>();
	private PipelineDriver driver;

	public static void main (String[] args) {
		RunTest rt = new RunTest();
		rt.run();
	}
	public RunTest () {
		driver = new PipelineDriver();
		
		URL url = RunTest.class.getResource("/input1_en.properties");
		String root = Util.getDirectoryName(url.getPath());
		root = Util.getDirectoryName(root) + "/data/";

		ProjectItem pi = new ProjectItem();
		pi.inputPaths[0] = root+"input1_en.properties";
		pi.encodings[0] = "UTF-8";
		pi.filterConfigs[0] = "okf_properties";
		proj.add(pi);

		pi = new ProjectItem();
		pi.inputPaths[0] = root+"input2_en.properties";
		pi.encodings[0] = "UTF-8";
		pi.filterConfigs[0] = "okf_properties";
		proj.add(pi);
	}

	public void run () {
		driver.setPipeline(createPipelineOne());
		feedDriver();
		//driver.processBatch();

		driver.setPipeline(createPipelineTwo());
		feedDriver();
		//driver.processBatch();

		driver.setPipeline(createPipelineThree());
		feedDriver();
		driver.processBatch();
	}
	
	private void feedDriver () {
		driver.resetInputs();
		for ( ProjectItem item : proj ) {
			ArrayList<DocumentData> inputList = new ArrayList<DocumentData>();
			for ( int i=0; i<driver.inputCountRequested(); i++ ) {
				if ( i > 2 ) {
					throw new RuntimeException("Application does not support more than 3 input at the same time.");
				}
				DocumentData dd = new DocumentData();
				// OK to have null, as some steps may use 1 *or* more input.
				// E.g. a bilingual file, vs 1 source input and 1 target input
				dd.inputURI = (new File(item.inputPaths[i])).toURI();
				dd.defaultEncoding = item.encodings[i];
				dd.filterConfig = item.filterConfigs[i];
				dd.srcLang = "en";
				dd.trgLang = "fr";
				
				// Do we need output for this entry?
				if ( driver.needsOutput(i) ) {
					// Output encoding same as the input
					dd.outputEncoding = item.encodings[i];
					dd.outputPath = Util.getFilename(item.inputPaths[i], false)
						+ ".out" + Util.getExtension(item.inputPaths[i]);
				}
				// Add the data to the list
				inputList.add(dd);
			}
			driver.addInputItem(inputList);
		}
	}
	
	private IPipeline createPipelineOne () {
		// First pipeline: simple BOM conversion
		IPipeline pipeline = new Pipeline();
	
		BOMConversionStep step = new BOMConversionStep();
		net.sf.okapi.steps.bomconversion.Parameters params
			= (net.sf.okapi.steps.bomconversion.Parameters)step.getParameters();
		params.removeBOM = true; // Remove thge BOM
		pipeline.addStep(step);
		
		pipeline.addStep(new RawDocumentWriterStep());
		return pipeline;
	}
	
	private IPipeline createPipelineTwo () {
		IPipeline pipeline = new Pipeline();
		pipeline.addStep(new RawDocumentToFilterEventsStep());
		
		// Text modification step
		// The params are set here. Basically they would be changed by the application
		// not after starting a batch.
		TextModificationStep step = new TextModificationStep();
		net.sf.okapi.steps.textmodification.Parameters params
			= (net.sf.okapi.steps.textmodification.Parameters)step.getParameters();
		params.type = params.TYPE_EXTREPLACE;
		pipeline.addStep(step);
		
		pipeline.addStep(new FilterEventsWriterStep());
		return pipeline;
	}
	
	private IPipeline createPipelineThree () {
		IPipeline pipeline = new Pipeline();

		// Convert Raw document to filter events
		pipeline.addStep(new RawDocumentToFilterEventsStep());

		// Text modification step
		// The params are set here. Basically they would be changed by the application
		// not after starting a batch.
		TextModificationStep step1 = new TextModificationStep();
		net.sf.okapi.steps.textmodification.Parameters params1
			= (net.sf.okapi.steps.textmodification.Parameters)step1.getParameters();
		params1.type = params1.TYPE_EXTREPLACE;
		pipeline.addStep(step1);

		// Convert back filter events to raw document
		pipeline.addStep(new FilterEventsToRawDocumentStep());
		
		// Remove the BOM
		BOMConversionStep step2 = new BOMConversionStep();
		net.sf.okapi.steps.bomconversion.Parameters params2
			= (net.sf.okapi.steps.bomconversion.Parameters)step2.getParameters();
		params2.removeBOM = true; // Remove thge BOM
		pipeline.addStep(step2);
		
		// Write the output
		pipeline.addStep(new RawDocumentWriterStep());
		return pipeline;
	}
	
}
