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

package net.sf.okapi.common.pipeline.threaded;

import net.sf.okapi.common.pipeline.IPipelineStep;

abstract class BaseThreadedPipelineStepAdaptor implements Runnable {
	private IPipelineStep step;
	private volatile boolean cancelled;
	private volatile boolean destroyed;

	public BaseThreadedPipelineStepAdaptor(IPipelineStep step) {
		this.step = step;
		cancelled = false;
		destroyed = false;
	}

	public String getName() {
		return step.getName();
	}

	public void cancel() {
		step.cancel();
		cancelled = true;
	}

	public void destroy() {
		step.destroy();
		destroyed = true;
	}

	public IPipelineStep getStep() {
		return step;
	}

	protected abstract void processBlockingQueue();

	protected abstract void clearBlockingQueues();

	public void run() {
		// start the thread loop by processing the queues
		while (!cancelled && !destroyed) {
			processBlockingQueue();
		}

		if (destroyed) {
			clearBlockingQueues();
			return;
		}

		if (cancelled) {
			synchronized (this) {
				while (cancelled)
					try {
						wait();
					} catch (InterruptedException e) {
						clearBlockingQueues();
						return;
					}
			}
		}
	}
}
