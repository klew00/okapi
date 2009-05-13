package net.sf.okapi.common.pipeline;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class PipelineDriver {
	
	private IPipeline pipeline;
	private List<IDocumentData> inputItems;
	
	public PipelineDriver () {
		pipeline = new Pipeline();
		inputItems = new ArrayList<IDocumentData>();
	}

	public void setPipeline (IPipeline pipeline) {
		this.pipeline = pipeline;
	}
	
	public void load (URI inputURI) {
		//TODO
	}
	
	public void save (String outputPath) {
		//TODO
	}

	public int inputCountRequested () {
		return pipeline.inputCountRequested();
	}
	
	public boolean needsOutput(int inputIndex) {
		return pipeline.needsOutput(inputIndex);
	}
	
	public void addStep (IPipelineStep step) {
		pipeline.addStep(step);
	}

	public void processBatch (List<IDocumentData> inputItems) {
		this.inputItems = inputItems;
		processBatch();
	}
	
	public void addInputItem (IDocumentData inputs) {
		inputItems.add(inputs);
	}
	
	public void resetInputs () {
		inputItems.clear();
	}
	
	public void processBatch () {
		pipeline.startBatch();
		for ( IDocumentData inputs : inputItems ) {
			pipeline.initialize();
			pipeline.preprocess(inputs);
			pipeline.processDocument(inputs.getRawDocument(0));
			pipeline.postprocess();
		}
		pipeline.finishBatch();
	}

}
