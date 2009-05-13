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
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.IFilter;

/**
 * Common set of methods for a step within a {@link IPipeline} pipeline. 
 */
public interface IPipelineStep {
	
	/**
	 * Gets the current parameters for this step.
	 * @return the current parameters for this step.
	 */
	public IParameters getParameters ();

	/**
	 * Sets new parameters for this step.
	 * @param params the new parameters to use.
	 */
	public void setParameters (IParameters params);

	/**
	 * Gets the localizable name of this step.
	 * @return the localizable name of this step.
	 */
	public String getName();

	/**
	 * Gets a short localizable description of what this step does.
	 * @return the text of a short description of what this step does.
	 */
	public String getDescription ();
	
	void preprocess(IDocumentData inputs);

	/**
	 * Executes the post-processing actions for this step.
	 * Post-processing is done after all events are processed, and is called once per
	 * pipeline execution.
	 */
	void postprocess();

	/**
	 * Processes each event sent though the pipeline.
	 * @param event the event to process.
	 * @return the event to pass down the pipeline.
	 */
	Event handleEvent(Event event);
	
	/**
	 * Steps that can generate {@link Event}s such as {@link IFilter}s return true until 
	 * no more events can be created. 
	 * Steps which do not create {@link Event} always return false.
	 * 
	 * @return true if can generate more events, false otherwise.
	 */
	boolean hasNext();
	
	/**
	 * Executes any cleanup code for this step. Called once at the end of the pipeline lifecycle.
	 */
	void destroy();

	/**
	 * Indicates how many inputs are needed by this step for each process
	 * within a batch. The minimal value is 1 (the main document).
	 * @return
	 */
	int inputCountRequested ();
	
	/**
	 * Indicates if a given input needs corresponding output information.
	 * @param inputIndex the index of the input to query. Use 0 for the main input.
	 * @return true if the given input needs a corresponding output.
	 */
	boolean needsOutput (int inputIndex);

}
