package net.sf.okapi.common.pipeline;

import java.net.URI;
import java.util.ArrayList;

import net.sf.okapi.common.resource.RawDocument;

public class PipelineDriver {
	
	private IPipeline pipeline;
	private ArrayList<ArrayList<DocumentData>> inputItems;
	
	public PipelineDriver () {
		pipeline = new Pipeline();
		inputItems = new ArrayList<ArrayList<DocumentData>>();
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

	public void addStep (IPipelineStep step) {
		pipeline.addStep(step);
	}

	public void processBatch (ArrayList<ArrayList<DocumentData>> inputItems) {
		this.inputItems = inputItems;
		processBatch();
	}
	
	public void addInputItem (URI inputURI,
		String defaultEncoding,
		String srcLang,
		String trgLang,
		String filterConfig)
	{
		addInputItem(inputURI, defaultEncoding, srcLang, trgLang,
			filterConfig, null, null);
	}
	
	public void addInputItem (URI inputURI,
		String defaultEncoding,
		String srcLang,
		String trgLang,
		String filterConfig,
		String outputPath,
		String outputEncoding)
	{
		DocumentData dd = new DocumentData();
		dd.inputURI = inputURI;
		dd.defaultEncoding = defaultEncoding;
		dd.srcLang = srcLang;
		dd.trgLang = trgLang;
		dd.filterConfig = filterConfig;
		dd.outputPath = outputPath;
		dd.outputEncoding = outputEncoding;
		ArrayList<DocumentData> list = new ArrayList<DocumentData>();
		list.add(dd);
		inputItems.add(list);
	}
		
	public void addInputItem (DocumentData... inputs) {
		ArrayList<DocumentData> list = new ArrayList<DocumentData>();
		for ( DocumentData dd : inputs ) {
			list.add(dd);
		}
		inputItems.add(list);
	}
	
	public void processBatch () {
		pipeline.startBatch();
		for ( ArrayList<DocumentData> inputList : inputItems ) {
			pipeline.initialize();
			pipeline.preprocess(inputList);
			DocumentData mainDoc = inputList.get(0); 
			pipeline.processDocument(new RawDocument(mainDoc.inputURI, mainDoc.defaultEncoding,
					mainDoc.srcLang, mainDoc.trgLang));
			pipeline.postprocess();
		}
		pipeline.finishBatch();
	}

}
