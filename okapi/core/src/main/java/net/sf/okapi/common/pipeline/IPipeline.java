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

import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.resource.RawDocument;

/**
 * Common set of methods to drive an event-driven process. A pipeline is made of a chain of {@link IPipelineStep}
 * objects through which documents are processed.
 */
public interface IPipeline {

	/**
	 * Starts {@link IPipeline} processing with a {@link RawDocument} as input. This is a convenience method that calls
	 * {@link #process(Event)}.
	 * 
	 * @param input
	 *            the RawDocument to process.
	 */
	public void process(RawDocument input);

	/**
	 * Starts {@link IPipeline} processing with a {@link Event} as input.
	 * 
	 * @param input
	 *            event that primes the {@link IPipeline}
	 */
	public void  process(Event input);

	/**
	 * Gets the current pipeline state.
	 * 
	 * @return the current state of the pipeline.
	 */
	public PipelineReturnValue getState();

	/**
	 * Cancels processing on this pipeline.
	 */
	public void cancel();

	/**
	 * Adds a step to this pipeline. Steps are executed in the order they are added.
	 * 
	 * @param step
	 *            the step to add.
	 */
	public void addStep(IPipelineStep step);

	/**
	 * Gets the list of all steps in this pipeline.
	 * 
	 * @return a list of all steps in this pipeline, the list may be empty.
	 */
	public List<IPipelineStep> getSteps();

	/**
	 * Starts a batch of inputs.
	 */
	public void startBatch();

	/**
	 * Finishes a batch of inputs.
	 * 
	 */
	public void endBatch();

	/**
	 * Frees all resources from all steps in this pipeline.
	 */
	public void destroy();

	/**
	 * Remove all the {@link IPipelineStep}s from the pipeline. Also calls the destroy() method on each step.
	 */
	public void clearSteps();

	/**
	 * Set the pipelines identifier.
	 */
	public void setId(String id);

	/**
	 * Get the Pipelines identifier.
	 * 
	 * @return String identifier
	 */
	public String getId();
}
