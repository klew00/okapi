/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.lib.reporting;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Test;

public class DateTest {

	@Test
	public void testDate() {
		Date date;
//		DateFormat df;
//		SimpleDateFormat sdf;
//		TimeZone tz;
		
		date = new Date();
		Logger localLogger = LoggerFactory.getLogger(getClass());
		localLogger.debug(date.toString());
	}
}
