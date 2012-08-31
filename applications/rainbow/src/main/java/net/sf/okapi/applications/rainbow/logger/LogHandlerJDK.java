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

package net.sf.okapi.applications.rainbow.logger;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.sf.okapi.applications.rainbow.lib.ILog;

class LogHandlerJDK extends Handler implements ILogHandler {
	private ILog       log;
	private int currentLogLevel = LogLevel.INFO;
	
	public void initialize(ILog log) {
		if( log == null ) return;

		this.log = log;
	}
	public void setLogLevel(int level) {
		currentLogLevel = level;
	}
	public int getLogLevel() {
		return currentLogLevel;
	}
	
	@Override
	public void close ()
		throws SecurityException
	{
		// Do nothing
	}

	@Override
	public void flush () {
		// Do nothing
	}

	@Override
	public void publish (LogRecord record) {
		if( log == null ) return;

		if ( record.getLevel() == Level.SEVERE ) {
			log.error(record.getMessage());
			Throwable e = record.getThrown();
			if ( e != null ) {
				log.message(e.getMessage());
				log.message(" @ "+e.toString()); //$NON-NLS-1$
			}
		}
		else if ( record.getLevel() == Level.WARNING ) {
			// Filter out Axis warnings
			if ( "org.apache.axis.utils.JavaUtils".equals(record.getLoggerName()) ) return;
			// Otherwise print
			log.warning(record.getMessage());
		}
		else if ( record.getLevel() == Level.INFO ) {
			log.message(record.getMessage());
		}
	}
}
