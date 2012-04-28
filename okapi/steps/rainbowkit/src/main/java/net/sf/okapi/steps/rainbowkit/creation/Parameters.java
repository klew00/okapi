/*===========================================================================
  Copyright (C) 2008-2012 by the Okapi Framework contributors
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

package net.sf.okapi.steps.rainbowkit.creation;

import java.util.List;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.ListUtil;
import net.sf.okapi.common.Util;

public class Parameters extends BaseParameters {

	static public String SUPPORTFILE_SEP = "\t"; // Separator between two support file entries
	static public String SUPPORTFILEDEST_SEP = ">"; // Separator between the file pattern and its destination
	static public String SUPPORTFILE_SAMENAME = "<same>"; // Marker to indicate to use the same file name
	
	static final String WRITERCLASS = "writerClass"; //$NON-NLS-1$
	static final String WRITEROPTIONS = "writerOptions"; //$NON-NLS-1$
	static final String PACKAGENAME = "packageName"; //$NON-NLS-1$
	static final String PACKAGEDIRECTORY = "packageDirectory"; //$NON-NLS-1$ 
	static final String MESSAGE = "message"; //$NON-NLS-1$
	static final String OUTPUTMANIFEST = "outputManifest"; //$NON-NLS-1$
	static final String CREATEZIP = "createZip"; //$NON-NLS-1$
	static final String SENDOUTPUT = "sendOutput"; //$NON-NLS-1$
	static final String SUPPORTFILES = "supportFiles"; //$NON-NLS-1$
	
	private String writerClass;
	private String writerOptions;
	private String packageName;
	private String packageDirectory;
	private String message;
	private boolean outputManifest;
	private boolean createZip;
	private boolean sendOutput;
	/*List of the support files. The storage is done:
	 * origin1>destination1\torigin2>destination2\t...
	 * where origin is a path or path with pattern
	 * and destination is a directory relative to the root of the package,
	 * plus the file name, or <same> for the same filename
	 */
	private String supportFiles;

	public Parameters () {
		reset();
	}
	
	@Override
	public void reset() {
		writerClass = "net.sf.okapi.steps.rainbowkit.xliff.XLIFFPackageWriter";
		writerOptions = "";
		packageName = "pack1";
		packageDirectory = Util.INPUT_ROOT_DIRECTORY_VAR;
		supportFiles = "";
		// Internal
		message = "";
		outputManifest = true;
		createZip = false;
		sendOutput = false;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		writerClass = buffer.getString(WRITERCLASS, writerClass);
		writerOptions = buffer.getGroup(WRITEROPTIONS);
		packageName = buffer.getString(PACKAGENAME, packageName);
		packageDirectory = buffer.getString(PACKAGEDIRECTORY, packageDirectory);
		sendOutput = buffer.getBoolean(SENDOUTPUT, sendOutput);
		supportFiles = buffer.getString(SUPPORTFILES, supportFiles);
		// Internal
		message = buffer.getString(MESSAGE, message);
		outputManifest = buffer.getBoolean(OUTPUTMANIFEST, outputManifest);
		createZip = buffer.getBoolean(CREATEZIP, createZip);
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setParameter(WRITERCLASS, writerClass);
		buffer.setGroup(WRITEROPTIONS, writerOptions);
		buffer.setParameter(PACKAGENAME, packageName);
		buffer.setParameter(PACKAGEDIRECTORY, packageDirectory);
		buffer.setBoolean(SENDOUTPUT, sendOutput);
		buffer.setString(SUPPORTFILES, supportFiles);
		// Internal
		buffer.setParameter(MESSAGE, message);
		buffer.setParameter(OUTPUTMANIFEST, outputManifest);
		buffer.setParameter(CREATEZIP, createZip);
		return buffer.toString();
	}

	public String getWriterClass () {
		return writerClass;
	}

	public void setWriterClass (String writerClass) {
		this.writerClass = writerClass;
	}

	public String getWriterOptions () {
		return writerOptions;
	}

	public void setWriterOptions (String writerOptions) {
		this.writerOptions = writerOptions;
	}
	
	public String getMessage () {
		return message;
	}

	public void setMessage (String message) {
		this.message = message;
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
	
	public String getSupportFiles () {
		return supportFiles;
	}

	public void setSupportFiles (String supportFiles) {
		this.supportFiles = supportFiles;
	}
	
	public boolean getOutputManifest () {
		return outputManifest;
	}

	public void setOuputManifest (boolean outputManifest) {
		this.outputManifest = outputManifest;
	}

	public boolean getCreateZip () {
		return createZip;
	}

	public void setCreateZip(boolean createZip) {
		this.createZip = createZip;
	}

	public boolean getSendOutput () {
		return sendOutput;
	}

	public void setSendOutput (boolean sendOutput) {
		this.sendOutput = sendOutput;
	}

	public List<String> convertSupportFilesToList (String data) {
		return ListUtil.stringAsList(data, SUPPORTFILE_SEP);
	}
	
	public String convertSupportFilesToString (List<String> list) {
		return ListUtil.listAsString(list, SUPPORTFILE_SEP);
	}
}
