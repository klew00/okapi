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

public class Parameters extends BaseParameters {

	static final String PACKAGENAME = "packageName"; //$NON-NLS-1$
	static final String PACKAGEDIRECTORY = "packageDirectory"; //$NON-NLS-1$ 
	static final String MESSAGE = "message"; //$NON-NLS-1$
	static final String USEMANIFEST = "useManifest"; //$NON-NLS-1$
	
	private String packageName;
	private String packageDirectory;
	private String message;
	private boolean useManifest;

	public Parameters () {
		reset();
	}
	
	@Override
	public void reset() {
		packageName = "pack1";
		packageDirectory = "${rootDir}";
		// Internal
		message = "";
		useManifest = true;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		
		packageName = buffer.getString(PACKAGENAME, packageName);
		packageDirectory = buffer.getString(PACKAGEDIRECTORY, packageDirectory);

		// Internal
		message = buffer.getString(MESSAGE, message);
		useManifest = buffer.getBoolean(USEMANIFEST, useManifest);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setParameter(PACKAGENAME, packageName);
		buffer.setParameter(PACKAGEDIRECTORY, packageDirectory);
		// Internal
		buffer.setParameter(MESSAGE, message);
		buffer.setParameter(USEMANIFEST, useManifest);
		return buffer.toString();
	}

	public String getMessage () {
		return message;
	}

	public String getPackageName () {
		return packageName;
	}

	public void setPackageName (String packageName) {
		this.packageName = packageName;
	}

	public String getPackageDirectory () {
		return packageDirectory;
	}

	public void setPackageDirectory (String packageDirectory) {
		this.packageDirectory = packageDirectory;
	}
	
	public void setMessage (String message) {
		this.message = message;
	}
	
	public void setUseManifest (boolean useManifest) {
		this.useManifest = useManifest;
	}

	public boolean getUseManifest () {
		return useManifest;
	}

}
