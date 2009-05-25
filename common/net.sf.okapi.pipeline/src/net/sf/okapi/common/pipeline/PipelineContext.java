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

import net.sf.okapi.common.BaseContext;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.resource.RawDocument;

/**
 * Context for running a pipeline. 
 */
public class PipelineContext extends BaseContext implements IBatchItemContext {

	private IBatchItemContext docData;
	private IFilterConfigurationMapper configMapper;

	/**
	 * Creates a new empty PipelineContext.
	 */
	public PipelineContext () {
	}
	
	/**
	 * Sets the {@link IFilterConfigurationMapper} object to use with this context.
	 * @param configMapper the {@link IFilterConfigurationMapper} to set. 
	 */
	public void setFilterConfigurationMapper (IFilterConfigurationMapper configMapper) {
		this.configMapper = configMapper;
	}
	
	/**
	 * Gets the current {@link IFilterConfigurationMapper} object associated
	 * with this context.
	 * @return the {@link IFilterConfigurationMapper} currently associated with
	 * this context.
	 */
	public IFilterConfigurationMapper getFilterConfigurationMapper () {
		return configMapper;
	}
	
	/**
	 * Sets the {@link IBatchItemContext} object to use with this context.
	 * This object should be reset each time before the pipeline processes
	 * a new batch item. 
	 * @param itemContext the {@link IBatchItemContext} to use.
	 */
	public void setBatchItemContext (IBatchItemContext itemContext) {
		this.docData = itemContext;
	}
	
	public String getFilterConfigurationId (int index) {
		return docData.getFilterConfigurationId(index);
	}

	public String getOutputEncoding (int index) {
		return docData.getOutputEncoding(index);
	}

	public URI getOutputURI (int index) {
		return docData.getOutputURI(index);
	}

	public RawDocument getRawDocument (int index) {
		return docData.getRawDocument(index);
	}

	public String getSourceLanguage (int index) {
		return docData.getSourceLanguage(index);
	}

	public String getTargetLanguage (int index) {
		return docData.getTargetLanguage(index);
	}
	
}
