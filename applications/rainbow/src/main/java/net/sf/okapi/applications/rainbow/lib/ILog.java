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

package net.sf.okapi.applications.rainbow.lib;

import net.sf.okapi.common.IHelp;

public interface ILog {

	public boolean beginProcess (String p_sText);

	boolean beginTask (String p_sText);

	void cancel (boolean p_bAskConfirmation);

	void clear ();

	boolean canContinue ();

	void endProcess (String p_sText);

	void endTask (String p_sText);

	boolean error (String p_sText);

	public long getCallerData ();

	int getErrorAndWarningCount ();

	int getErrorCount ();

	int getWarningCount ();

	public void hide ();

	public boolean inProgress ();

	public boolean isVisible ();

	boolean message (String p_sText);

	boolean newLine ();

	void save (String p_sPath);

	public void setCallerData (long p_lData);

	public void setHelp (IHelp helpParam,
		String helpPath);

	boolean setLog (int p_nType,
		int p_nValue,
		String p_sValue);

	public void setMainProgressMode (int p_nValue);

	public boolean setOnTop (boolean p_bValue);

	void setSubProgressMode (int p_nValue);

	void setTitle (String p_sValue);

	public void show ();

	boolean warning (String p_sText);
}
