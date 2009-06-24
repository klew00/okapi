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
import java.util.List;

import net.sf.okapi.common.resource.RawDocument;

/**
 * Common set of methods for setting up and running a pipeline.
 */
public interface IPipelineDriver {

	/**
	 * Sets the {@link IPipeline} to use with this driver.
	 * 
	 * @param pipeline
	 *            the new {@link IPipeline} to associate with this driver.
	 */
	public void setPipeline(IPipeline pipeline);

	/**
	 * Gets the {@link IPipeline} currently associated with this driver.
	 * 
	 * @return the {@link IPipeline} currently associated with this driver.
	 */
	public IPipeline getPipeline();

	/**
	 * Indicates what is the highest number of inputs needed by the steps
	 * currently in this driver.
	 * 
	 * @return the highest number of requested input per batch item.
	 */
	public int inputCountRequested();

	/**
	 * Indicates if any of the current steps in this driver needs output
	 * information.
	 * 
	 * @param inputIndex
	 *            the index of the input to query. Use 0 for the main input.
	 * @return true if output information is needed.
	 */

	public boolean needsOutput(int inputIndex);

	/**
	 * Adds a step to the pipeline currently associated with this driver.
	 * 
	 * @param step
	 *            the step to add.
	 */
	public void addStep(IPipelineStep step);

	/**
	 * Sets a new set of batch items for this driver and processes them. Any
	 * batch item that was already in the driver is removed and replaced by the
	 * ones provided to this method.
	 * 
	 * @param batchItems
	 *            the list of the batch items to process.
	 */
	public void processBatch(List<IBatchItemContext> batchItems);

	/**
	 * Processes all the batch items currently in the driver.
	 */
	public void processBatch();

	/**
	 * Adds an item to this batch.
	 * 
	 * @param item
	 *            the item to add to this batch.
	 */
	public void addBatchItem(IBatchItemContext item);

	/**
	 * Adds an item to this batch, using one or more RawDocument objects. The
	 * added batch item will have as many input documents as provided.
	 * 
	 * @param rawDocs
	 *            one or more RawDocuments to include in this item.
	 */
	public void addBatchItem(RawDocument... rawDocs);

	/**
	 * Adds an item to this batch, using a RawDocument object. The added item
	 * will have a single input document.
	 * 
	 * @param rawDoc
	 *            the RawDocument object from which to create an entry.
	 * @param outputURI
	 *            path of the output document (can be null if no used)
	 * @param outputEncoding
	 *            encoding of the output (can be null if no used)
	 */
	public void addBatchItem(RawDocument rawDoc, URI outputURI, String outputEncoding);

	/**
	 * Adds an item to this batch, using direct parameters. The added item will
	 * have a single input document.
	 * 
	 * @param inputURI
	 *            the URI of the input document.
	 * @param defaultEncoding
	 *            the default encoding of the document.
	 * @param filterConfigId
	 *            the filter configuration ID of the document (can be null if
	 *            not used).
	 * @param srcLang
	 *            the source language.
	 * @param trgLang
	 *            the target language.
	 */
	public void addBatchItem(URI inputURI, String defaultEncoding, String filterConfigId, String srcLang, String trgLang);

	/**
	 * Remove all the {@link IPipelineStep}s from the pipeline. Also calls the
	 * destroy() method on each step.
	 */
	public void clearSteps();

	/**
	 * Removes all current batch items from this driver.
	 */
	public void clearItems();

}
