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

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.resource.FileResource;

public class FileResourcePipelineStepAdaptor extends BasePipelineStep {	
	private FileResource fileResource;
	private boolean eventSent;

	public FileResourcePipelineStepAdaptor(FileResource fileResource) {
		this.fileResource = fileResource;
		eventSent = false;
	}

	public FileResource getFileResource() {
		return fileResource;
	}

	public String getName() {
		return fileResource.getId();
	}

	@Override
	public Event handleEvent(Event event) {
		eventSent = true;
		return new Event(EventType.FILE_RESOURCE, fileResource);
	}

	@Override
	public void destroy() {
		fileResource.close();
	}

	public void cancel() {
		destroy();
	}

	public boolean hasNext() {
		return !eventSent;
	}
}
