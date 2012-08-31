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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;   
import org.apache.log4j.spi.ThrowableInformation;

import net.sf.okapi.applications.rainbow.lib.ILog;

class LogHandlerLog4j extends AppenderSkeleton implements ILogHandler {
	private ILog       log;
	private int currentLogLevel = LogLevel.INFO;

	public void initialize (ILog log) {
		if( log == null ) return;

		this.log = log;
		Logger root = Logger.getRootLogger();
		root.addAppender(this);
	}

	public void setLogLevel(int level) {
		currentLogLevel = level;
	}
	public int getLogLevel() {
		return currentLogLevel;
	}

	@Override
	public void close() {
		// Do nothing
	}

	@Override
	public boolean requiresLayout() {
		// Do nothing
		return false;
	}

	@Override
	protected void append(LoggingEvent record) {
		if( log == null ) return;

		if ( record.getLevel() == Level.ERROR || record.getLevel() == Level.FATAL ) {
			log.error(record.getRenderedMessage());
			ThrowableInformation e = record.getThrowableInformation();
			if ( e != null )
				log.message(" @ "+e.toString()); //$NON-NLS-1$
			return;
		}
		if ( record.getLevel() == Level.WARN && currentLogLevel >= LogLevel.WARN ) {
			// Filter out Axis warnings
			if ( "org.apache.axis.utils.JavaUtils".equals(record.getLoggerName()) ) return;
			// Otherwise print
			log.warning(record.getRenderedMessage());
			return;
		}
		if ( record.getLevel() == Level.INFO && currentLogLevel >= LogLevel.INFO ) {
			log.message(record.getRenderedMessage());
			return;
		}
		if ( currentLogLevel >= LogLevel.TRACE ) {
			log.message(record.getRenderedMessage());
		}
	}
}
