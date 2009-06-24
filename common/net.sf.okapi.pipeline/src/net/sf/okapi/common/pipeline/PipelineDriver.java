/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.pipeline;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.okapi.common.resource.RawDocument;

/**
 * Default implementation of the {@link IPipelineDriver} interface.
 */
public class PipelineDriver implements IPipelineDriver {

	/**
	 * Logger for this driver.
	 */
	protected final Logger logger = Logger.getLogger(getClass().getName());
	
	private IPipeline pipeline;
	private List<IBatchItemContext> batchItems;
	
	/**
	 * Creates an new PipelineDriver object with an empty pipeline.
	 */
	public PipelineDriver () {
		pipeline = new Pipeline();
		batchItems = new ArrayList<IBatchItemContext>();
	}

	public void setPipeline (IPipeline pipeline) {
		this.pipeline = pipeline;
	}

	public IPipeline getPipeline () {
		return pipeline;
	}

	public int inputCountRequested () {
		return pipeline.inputCountRequested();
	}
	
	public boolean needsOutput (int inputIndex) {
		return pipeline.needsOutput(inputIndex);
	}
	
	public void addStep (IPipelineStep step) {
		pipeline.addStep(step);
	}

	public void processBatch (List<IBatchItemContext> batchItems) {
		this.batchItems = batchItems;
		processBatch();
	}
	
	public void processBatch () {
		pipeline.startBatch();
		for ( IBatchItemContext item : batchItems ) {
			displayInput(item);
			pipeline.getContext().setBatchItemContext(item);
			pipeline.process(item.getRawDocument(0));
		}
		pipeline.endBatch();
	}

	public void addBatchItem (IBatchItemContext item) {
		batchItems.add(item);
	}

	public void addBatchItem (RawDocument... rawDocs)
	{
		BatchItemContext item = new BatchItemContext();
		for ( RawDocument rawDoc : rawDocs ) {
			DocumentData ddi = new DocumentData();
			ddi.rawDocument = rawDoc;
			item.add(ddi);
		}
		batchItems.add(item);
	}
	
	public void addBatchItem (RawDocument rawDoc,
		URI outputURI,
		String outputEncoding)
	{
		DocumentData ddi = new DocumentData();
		ddi.rawDocument = rawDoc;
		ddi.outputURI = outputURI;
		ddi.outputEncoding = outputEncoding;
		BatchItemContext item = new BatchItemContext();
		item.add(ddi);
		batchItems.add(item);
	}
	
	public void addBatchItem (URI inputURI,
		String defaultEncoding,
		String filterConfigId,
		String srcLang,
		String trgLang)
	{
		DocumentData ddi = new DocumentData();
		ddi.rawDocument = new RawDocument(inputURI, defaultEncoding, srcLang, trgLang);
		ddi.rawDocument.setFilterConfigId(filterConfigId);
		BatchItemContext item = new BatchItemContext();
		item.add(ddi);
		batchItems.add(item);
	}
	
	public void clearItems () {
		batchItems.clear();
	}

	/**
	 * Logs the information about which batch item is about to be processed. This
	 * method is called inside the loop that process the batch.
	 * @param item the batch item that is about to be processed.
	 */
	protected void displayInput (IBatchItemContext item) {
		if ( item.getRawDocument(0).getInputURI() != null ) {
			logger.info(String.format("\n-- Input: %s",
				item.getRawDocument(0).getInputURI().getPath()));
		}
		else {
			logger.info("\n-- Input (No path available)");
		}
	}

	public void clearSteps() {
		pipeline.clearSteps();		
	}
}
