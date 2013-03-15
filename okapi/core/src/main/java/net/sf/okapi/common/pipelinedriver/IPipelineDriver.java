/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.common.pipelinedriver;

import java.net.URI;
import java.util.List;

import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.ExecutionContext;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.pipeline.IPipeline;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.resource.RawDocument;

/**
 * Common set of methods for setting up and running a pipeline.
 */
public interface IPipelineDriver {

	/**
	 * Sets the {@link IPipeline} to use with this driver.
	 * @param pipeline
	 *            the new {@link IPipeline} to associate with this driver.
	 */
	public void setPipeline (IPipeline pipeline);

	/**
	 * Sets the filter configuration mapper object for this driver.
	 * @param fcMapper the filter configuration mapper to use.
	 */
	public void setFilterConfigurationMapper (IFilterConfigurationMapper fcMapper);
	
	/**
	 * Sets the root directories corresponding to the ${rootDir} and ${inputRootDir} variables for steps.
	 * @param rootDir the root directory of the project. If it is null, the fall-back is expected to be the user home directory.
	 * @param inputRootDir the root directory of the first set of input files. If it is null, the fall-back is expected to be an empty string.
	 */
	public void setRootDirectories (String rootDir,
		String inputRootDir);

	/**
	 * Sets the UI parent object for this driver.
	 * @param uiParent the UI parent object (window/shell/etc.). Its type depend
	 * on the caller, for example for SWT you pass the shell of the caller.
	 */
	public void setUIParent (Object uiParent);
	
	/**
	 * Sets the execution context for this driver.
	 * @param context the execution context such as batch  mode, CLI/GUI, etc.
	 */
	public void setExecutionContext (ExecutionContext context);
	
	/**
	 * Gets the {@link IPipeline} currently associated with this driver.
	 * @return the {@link IPipeline} currently associated with this driver.
	 */
	public IPipeline getPipeline ();

	/**
	 * Adds a step to the pipeline currently associated with this driver.
	 * @param step
	 *            the step to add.
	 */
	public void addStep (IPipelineStep step);

	/**
	 * Sets a new set of batch items for this driver and processes them. Any
	 * batch item that was already in the driver is removed and replaced by the
	 * ones provided to this method.
	 * @param batchItems
	 *            the list of the batch items to process.
	 */
	public void processBatch (List<IBatchItemContext> batchItems);

	/**
	 * Processes all the batch items currently in the driver.
	 */
	public void processBatch ();

	/**
	 * Adds an item to this batch.
	 * @param item
	 *            the item to add to this batch.
	 */
	public void addBatchItem (IBatchItemContext item);

	/**
	 * Adds an item to this batch, using one or more RawDocument objects. The
	 * added batch item will have as many input documents as provided.
	 * @param rawDocs
	 *            one or more RawDocuments to include in this item.
	 */
	public void addBatchItem (RawDocument... rawDocs);

	/**
	 * Adds an item to this batch, using a RawDocument object. The added item
	 * will have a single input document.
	 * @param rawDoc
	 *            the RawDocument object from which to create an entry.
	 * @param outputURI
	 *            path of the output document (can be null if no used)
	 * @param outputEncoding
	 *            encoding of the output (can be null if no used)
	 */
	public void addBatchItem (RawDocument rawDoc,
		URI outputURI,
		String outputEncoding);

	/**
	 * Adds an item to this batch, using direct parameters. The added item will
	 * have a single input document.
	 * @param inputURI
	 *            the URI of the input document.
	 * @param defaultEncoding
	 *            the default encoding of the document.
	 * @param filterConfigId
	 *            the filter configuration ID of the document (can be null if
	 *            not used).
	 * @param srcLoc
	 *            the source locale.
	 * @param trgLoc
	 *            the target locale.
	 */
	public void addBatchItem (URI inputURI,
		String defaultEncoding,
		String filterConfigId,
		LocaleId srcLoc,
		LocaleId trgLoc);

	/**
	 * Remove all the {@link IPipelineStep}s from the pipeline. Also calls the
	 * destroy() method on each step.
	 */
	public void clearSteps ();

	/**
	 * Removes all current batch items from this driver.
	 */
	public void clearItems ();

	/**
	 * Gets the highest number of input documents needed to run the pipeline
	 * for this driver. 
	 * @return the higher number of input document needed.
	 */
	public int getRequestedInputCount ();

}
