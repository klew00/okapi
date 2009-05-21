package net.sf.okapi.common.pipeline;

import java.net.URI;
import java.util.List;

import net.sf.okapi.common.resource.RawDocument;

public interface IPipelineDriver {
	
	public void setPipeline (IPipeline pipeline);

	public IPipeline getPipeline ();

	public void load (URI inputURI);
	
	public void save (String outputPath);

	public int inputCountRequested ();
	
	public boolean needsOutput (int inputIndex);
	
	public void addStep (IPipelineStep step);

	public void processBatch (List<IBatchItemContext> inputItems);
	
	public void addBatchItem (IBatchItemContext inputs);

	/**
	 * Adds an item item to this batch, using one or more RawDocument objects.
	 * The added item will have as many input documents as provided. All of them
	 * will be set to use the same filter configuration (the one provided). 
	 * @param filterConfig the filter configuration to use for these documents (may be null).
	 * @param rawDocs one or more RawDocuments to include in this item.
	 */
	public void addBatchItem (String filterConfig,
		RawDocument... rawDocs);
	
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
		String outputEncoding);
	
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
		String trgLang);
	
	public void resetItems ();
	
	public void processBatch ();

}
