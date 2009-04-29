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

package net.sf.okapi.filters.openxml.tests;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

class LogHandlerSystemOut extends Handler {
	
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
		if ( record.getLevel() == Level.SEVERE ) {
			System.out.println("LOGGER SEVERE: "+record.getMessage());
			if (record.getThrown() != null){
				System.out.println(record.getThrown().getMessage());
				System.out.println(" @ "+record.getThrown().toString());
			}
		}
		else if ( record.getLevel() == Level.WARNING ) {
			System.out.println("LOGGER SEVERE: "+record.getMessage());
		}
		else if ( record.getLevel() == Level.INFO ) {
			System.out.println("LOGGER INFO: "+record.getMessage());
		}
		else if ( record.getLevel() == Level.FINE) {
			System.out.println("LOGGER FINE: "+record.getMessage());
		}
		else if ( record.getLevel() == Level.FINER) {
			System.out.println("LOGGER FINER: "+record.getMessage());
		}
		else if ( record.getLevel() == Level.FINEST) {
			System.out.println("LOGGER FINEST: "+record.getMessage());
		}

	}
}
