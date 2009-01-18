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

package net.sf.okapi.applications.rainbow.utilities.transcomparison;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {

	public boolean generateTMX;
	public String tmxPath;
	public boolean generateHTML;
	public boolean autoOpen;
	public boolean ignoreCase;
	public boolean ignoreWS;
	public boolean ignorePunct;

	public Parameters () {
		reset();
	}
	
	public void reset() {
		generateTMX = true;
		tmxPath = "";
		generateHTML = true;
		autoOpen = true;
		ignoreCase = false;
		ignoreWS = false;
		ignorePunct = false;
	}

	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		generateTMX = buffer.getBoolean("generateTMX", generateTMX);
		tmxPath = buffer.getString("tmxPath", tmxPath);
		generateHTML = buffer.getBoolean("generateHTML", generateHTML);
		autoOpen = buffer.getBoolean("autoOpen", autoOpen);
		ignoreCase = buffer.getBoolean("ignoreCase", ignoreCase);
		ignoreWS = buffer.getBoolean("ignoreWS", ignoreWS);
		ignorePunct = buffer.getBoolean("ignorePunct", ignorePunct);
	}

	public String toString() {
		buffer.reset();
		buffer.setParameter("generateTMX", generateTMX);
		buffer.setParameter("tmxPath", tmxPath);
		buffer.setParameter("generateHTML", generateHTML);
		buffer.setParameter("autoOpen", autoOpen);
		buffer.setParameter("ignoreCase", ignoreCase);
		buffer.setParameter("ignoreWS", ignoreWS);
		buffer.setParameter("ignorePunct", ignorePunct);
		return buffer.toString();
	}

}
