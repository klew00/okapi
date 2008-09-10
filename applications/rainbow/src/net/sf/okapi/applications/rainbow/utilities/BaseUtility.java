/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel and the Okapi Framework contributors     */
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

package net.sf.okapi.applications.rainbow.utilities;

import javax.swing.event.EventListenerList;

import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.lib.FilterAccess;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;

public abstract class BaseUtility extends ThrougputPipeBase implements IUtility {

	protected final Logger        logger = LoggerFactory.getLogger("net.sf.okapi.logging");
	protected EventListenerList   listenerList = new EventListenerList();
	protected FilterAccess        fa;
	protected String              paramsFolder;
	protected Shell               shell;


	public void addCancelListener (CancelListener listener) {
		listenerList.add(CancelListener.class, listener);
	}

	public void removeCancelListener (CancelListener listener) {
		listenerList.remove(CancelListener.class, listener);
	}

	public void setContextUI (Object contextUI) {
		shell = (Shell)contextUI;
	}

	public void setFilterAccess (FilterAccess filterAccess,
		String paramsFolder)
	{
		fa = filterAccess;
		this.paramsFolder = paramsFolder;
	}

	protected void fireCancelEvent (CancelEvent event) {
		Object[] listeners = listenerList.getListenerList();
		for ( int i=0; i<listeners.length; i+=2 ) {
			if ( listeners[i] == CancelListener.class ) {
				((CancelListener)listeners[i+1]).cancelOccurred(event);
			}
		}
	}

}
