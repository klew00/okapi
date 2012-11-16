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

package net.sf.okapi.applications.tikal.logger;

import java.io.PrintStream;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;   
import org.apache.log4j.spi.ThrowableInformation;

class LogHandlerLog4j extends AppenderSkeleton implements ILogHandler {
	private PrintStream       ps;

	protected LogHandlerLog4j(){}

	public void initialize (PrintStream ps) {
		if( ps == null ) return;

		this.ps = ps;
		setThreshold(Level.INFO);

		// Disable root appenders
		Logger.getRootLogger().removeAllAppenders();

		Logger.getRootLogger().addAppender(this);
	}

	public void setLogLevel(int level) {
		switch ( level ) {
			case LogLevel.DEBUG:
				this.setThreshold(Level.DEBUG);
				Logger.getRootLogger().setLevel(Level.DEBUG);
				break;
			case LogLevel.TRACE:
				this.setThreshold(Level.TRACE);
				Logger.getRootLogger().setLevel(Level.TRACE);
				break;
			default:
				this.setThreshold(Level.INFO);
				Logger.getRootLogger().setLevel(Level.INFO);
				break;
		}
	}

	@Override
	public void close() {
		// Do nothing
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent record) {
		if( ps == null ) return;

		/*
		 * LOG4J native levels:
		 *    Level.OFF   = Integer.MAX_VALUE;
		 *    Level.FATAL = 50000;
		 *    Level.ERROR = 40000;
		 *    Level.WARN  = 30000;
		 *    Level.INFO  = 20000;
		 *    Level.DEBUG = 10000;
		 *    Level.TRACE =  5000;
		 *    Level.ALL   = Integer.MIN_VALUE;
		 */

		Level lev = record.getLevel();
		if ( lev == Level.ERROR || lev == Level.FATAL ) {
			ps.println("Error: " + record.getRenderedMessage());
			ThrowableInformation e = record.getThrowableInformation();
			if ( e != null ) {
				ps.println(" @ "+e.toString()); //$NON-NLS-1$
			}
		}
		else if ( lev == Level.WARN ) {
			// Filter out Axis warnings
			if ( "org.apache.axis.utils.JavaUtils".equals(record.getLoggerName()) ) return;
			// Otherwise print
			ps.println("Warning: " + record.getRenderedMessage());
		}
		else if ( lev == Level.INFO )
			ps.println(record.getRenderedMessage());
		else // below INFO
			ps.println("Trace: " + record.getRenderedMessage());
	}
}
