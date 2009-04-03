/*===========================================================================*/
/* Copyright (C) 2008 Jim Hargrave                                           */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.pipeline;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.IFilter;

public interface IPipelineStep {
	
	/**
	 * Gets the current parameters for this filter.
	 * @return The current parameters for this filter.
	 */
	public IParameters getParameters ();

	/**
	 * Sets new parameters for this filter.
	 * @param params The new parameters to use.
	 */
	public void setParameters (IParameters params);

	/**
	 * Get the UI displayable name of this step.
	 */
	public String getName();

	/**
	 * Preprocessing is done before any events are processed. Called once per
	 * pipeline execution.
	 */
	void preprocess();

	/**
	 * Postprocessing is done after all events are processed. Called once per
	 * pipeline execution.
	 */
	void postprocess();

	/**
	 * Process each event sent though the pipeline.
	 */
	Event handleEvent(Event event);
	
	/**
	 * Steps that can generate {@link Event}s such as {@link IFilter}s return true until no more events can be created. 
	 * Steps which do not create {@link Event} always return false.
	 * 
	 * @return true if can generate more events, false otherwise.
	 */
	boolean hasNext();
	
	/**
	 * Cleanup code should go here. Called once at the end of the pipeline lifecycle.
	 */
	void destroy();
}
