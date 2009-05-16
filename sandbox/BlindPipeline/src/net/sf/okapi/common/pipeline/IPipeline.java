/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

import net.sf.okapi.common.Event;
import net.sf.okapi.common.resource.RawDocument;

/**
 * Common set of methods to drive an event-driven process. A pipeline is made of a chain of {@link IPipelineStep}
 * objects through which documents are processed.
 */
public interface IPipeline {

	/**
	 * Starts {@link IPipeline} processing with a {@link RawDocument} as input.
	 * This is nothing more than a convenience method that calls process(Event
	 * input).
	 * 
	 * @param input the RawDocument to process.
	 * @return the terminal {@link Event} generated by the final step.
	 */
	public Event process (RawDocument input);

	/**
	 * Starts {@link IPipeline} processing with a {@link Event} as input.
	 * 
	 * @param input Event that primes the {@link IPipeline}
	 * @return the terminal {@link Event} generated by the final step.
	 */
	public Event process (Event input);

	/**
	 * Gets the current pipeline state.
	 * 
	 * @return the current state of the pipeline.
	 */
	public PipelineReturnValue getState();

	/**
	 * Cancels processing on the pipeline.
	 */
	public void cancel();

	/**
	 * Adds a step to the pipeline. Steps are executed in the order they are
	 * added.
	 * 
	 * @param step
	 */
	public void addStep(IPipelineStep step);

	public PipelineContext getContext ();
	
	public void setContext (PipelineContext context);
	
	/**
	 * Starts a batch of inputs.
	 */
	public void startBatch ();

	/**
	 * Finishes a batch of inputs and return the final {@link Event}
	 * @return the final {@link Event} for this batch.
	 */
	public Event endBatch();

	/**
	 * Closes down {@link IPipeline} and recover resources from all steps.
	 */
	public void destroy();

	/**
	 * Indicates the highest number of inputs that was requested by any of the steps
	 * in this pipeline. 
	 * @return highest number of input requested by this pipeline.
	 */
	public int inputCountRequested ();

	/**
	 * Indicates if an output is needed for a given input.
	 * @param inputIndex the index of the input to query. Use 0 for the main input.
	 * @return true if an output is needed for the given input.
	 */
	public boolean needsOutput (int inputIndex);

}
