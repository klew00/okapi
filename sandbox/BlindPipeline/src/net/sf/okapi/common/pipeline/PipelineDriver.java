package net.sf.okapi.common.pipeline;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.resource.RawDocument;

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
	
	public void addBatchItem (IDocumentData inputs) {
		inputItems.add(inputs);
	}

	/**
	 * Adds an item item to this batch, using one or more RawDocument objects.
	 * The added item will have as many input documents as provided. All of them
	 * will be set to use the same filter configuration (the one provided). 
	 * @param filterConfig the filter configuration to use for these documents (may be null).
	 * @param rawDocs one or more RawDocuments to include in this item.
	 */
	public void addBatchItem (String filterConfig,
		RawDocument... rawDocs)
	{
		DocumentData dd = new DocumentData();
		for ( RawDocument rawDoc : rawDocs ) {
			if ( dd.srcLang == null ) {
				dd.srcLang = rawDoc.getSourceLanguage();
			}
			if ( dd.trgLang == null ) {
				dd.trgLang = rawDoc.getTargetLanguage();
			}
			DocumentDataItem ddi = new DocumentDataItem();
			ddi.inputURI = rawDoc.getInputURI();
			ddi.defaultEncoding = rawDoc.getEncoding();
			ddi.filterConfig = filterConfig;
			dd.list.add(ddi);
		}
		inputItems.add(dd);
	}
	
	/**
	 * Adds an item to this batch, using a RawDocument object. The added item
	 * will have a single input document. 
	 * @param rawDoc the RawDocument object from which to create an entry.
	 * @param filterConfig the filter configuration to use for this document (may be null).
	 * @param outputPath path of the output document (may be null if no output is used)
	 * @param outputEncoding encoding of the output  (may be null if no output is used)
	 */
	public void addBatchItem (RawDocument rawDoc,
		String filterConfig,
		String outputPath,
		String outputEncoding)
	{
		DocumentDataItem ddi = new DocumentDataItem();
		ddi.inputURI = rawDoc.getInputURI();
		ddi.defaultEncoding = rawDoc.getEncoding();
		ddi.filterConfig = filterConfig;
		ddi.outputPath = outputPath;
		ddi.outputEncoding = outputEncoding;
		DocumentData dd = new DocumentData();
		dd.srcLang = rawDoc.getSourceLanguage();
		dd.trgLang = rawDoc.getTargetLanguage();
		dd.list.add(ddi);
		inputItems.add(dd);
	}
	
	/**
	 * Adds an item to this batch, using direct parameters. The added item
	 * will have a single input document. 
	 * @param inputURI the URI of the input document.
	 * @param defaultEncoding the default encoding of the document.
	 * @param filterConfig the filter configuration of the document.
	 * @param srcLang the source language.
	 * @param trgLang the target language.
	 */
	public void addBatchItem (URI inputURI,
		String defaultEncoding,
		String filterConfig,
		String srcLang,
		String trgLang)
	{
		DocumentDataItem ddi = new DocumentDataItem();
		ddi.inputURI = inputURI;
		ddi.defaultEncoding = defaultEncoding;
		ddi.filterConfig = filterConfig;
		DocumentData dd = new DocumentData();
		dd.srcLang = srcLang;
		dd.trgLang = trgLang;
		dd.list.add(ddi);
		inputItems.add(dd);
	}
	
	public void resetInputs () {
		inputItems.clear();
	}
	
	public void processBatch () {
		pipeline.startBatch();
		for ( IDocumentData inputs : inputItems ) {
			pipeline.getContext().setDocumentData(inputs);
			pipeline.process(inputs.getRawDocument(0));
		}
		pipeline.endBatch();
	}

}
