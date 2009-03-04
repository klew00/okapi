/*===========================================================================*/
/* Copyright (C) 2008 by the Okapi Framework contributors                    */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.common.pipeline;

import java.io.InputStream;
import java.net.URI;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.MemMappedCharSequence;
import net.sf.okapi.common.filters.IFilter;

public class FileResourcePipelineStepAdaptor extends BasePipelineStep implements IInitialStep {
	private IFilter filter;
	
	/* (non-Javadoc)
	 * @see net.sf.okapi.common.pipeline.IInitialStep#setInput(java.net.URI)
	 */
	public void setInput(URI input) {
		filter.close();
		filter.open(input);
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.pipeline.IInitialStep#setInput(java.io.InputStream)
	 */
	public void setInput(InputStream input) {
		filter.close();
		filter.open(input);
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.pipeline.IInitialStep#setInput(net.sf.okapi.common.MemMappedCharSequence)
	 */
	public void setInput(MemMappedCharSequence input) {
		filter.close();
		filter.open(input);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.okapi.common.pipeline.IInitialStep#setInput(CharSequence)
	 */
	public void setInput(CharSequence input) {
		filter.close();
		filter.open(input);
	}
	
	public FileResourcePipelineStepAdaptor(IFilter filter) {
		this.filter = filter;
	}
	
	public IFilter getFilter() {
		return filter;
	}
	
	public String getName() {		
		return filter.getName();
	}		

	@Override
	public Event handleEvent(Event event) {		
		return filter.next();
	}
	
	@Override
	public void preprocess() {}

	@Override
	public void postprocess() {
		filter.close();
	}
	
	@Override
	public void close() {
		filter.close();
	}
	
	public void cancel() {
		filter.cancel();
	}

	/* (non-Javadoc)
	 * @see net.sf.okapi.common.pipeline.IInitialStep#hasNext()
	 */
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}
}
