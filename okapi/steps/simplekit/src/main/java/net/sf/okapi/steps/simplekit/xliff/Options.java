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

package net.sf.okapi.steps.simplekit.xliff;

import net.sf.okapi.common.BaseParameters;

public class Options extends BaseParameters {

	private static final String GMODE = "gMode"; //$NON-NLS-1$
	private static final String INCLUDENOTRANSLATE = "includeNoTranslate"; //$NON-NLS-1$ 
	private static final String SETAPPROVEDASNOTRANSLATE = "setApprovedAsNoTranslate"; //$NON-NLS-1$
	private static final String COPYSOURCE = "copySource"; //$NON-NLS-1$
	private static final String INCLUDEALTTRANS = "includeAltTrans"; //$NON-NLS-1$
	
	private boolean gMode;
	private boolean includeNoTranslate;
	private boolean setApprovedAsNoTranslate;
	private boolean copySource;
	private boolean includeAltTrans;

	public Options () {
		reset();
	}
	
	@Override
	public void reset() {
		gMode = false;
		includeNoTranslate = true;
		setApprovedAsNoTranslate = false;
		copySource = true;
		includeAltTrans = true;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		gMode = buffer.getBoolean(GMODE, gMode);
		includeNoTranslate = buffer.getBoolean(INCLUDENOTRANSLATE, includeNoTranslate);
		setApprovedAsNoTranslate = buffer.getBoolean(SETAPPROVEDASNOTRANSLATE, setApprovedAsNoTranslate);
		copySource = buffer.getBoolean(COPYSOURCE, copySource);
		includeAltTrans = buffer.getBoolean(INCLUDEALTTRANS, includeAltTrans);
		
		// Make sure the we can merge later
		if ( !includeNoTranslate ) {
			setApprovedAsNoTranslate = false;
		}
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setParameter(GMODE, gMode);
		buffer.setBoolean(INCLUDENOTRANSLATE, includeNoTranslate);
		buffer.setBoolean(SETAPPROVEDASNOTRANSLATE, setApprovedAsNoTranslate);
		buffer.setBoolean(COPYSOURCE, copySource);
		buffer.setBoolean(INCLUDEALTTRANS, includeAltTrans);
		return buffer.toString();
	}

	public boolean getGMode () {
		return gMode;
	}

	public void setGMode (boolean gMode) {
		this.gMode = gMode;
	}
	
	public boolean getIncludeNoTranslate () {
		return includeNoTranslate;
	}

	public void setIncludeNoTranslate (boolean includeNoTranslate) {
		this.includeNoTranslate = includeNoTranslate;
	}
	
	public boolean getSetApprovedAsNoTranslate () {
		return setApprovedAsNoTranslate;
	}

	public void setSetApprovedAsNoTranslate (boolean setApprovedAsNoTranslate) {
		this.setApprovedAsNoTranslate = setApprovedAsNoTranslate;
	}

	public boolean getCopySource () {
		return copySource;
	}
	
	public void setCopySource (boolean copySource) {
		this.copySource = copySource;
	}

	public boolean getIncludeAltTrans () {
		return includeAltTrans;
	}

	public void setIncludeAltTrans (boolean includeAltTrans) {
		this.includeAltTrans = includeAltTrans;
	}

}
