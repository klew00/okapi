/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.steps.xliffkit.writer.XLIFFKitWriterStep;

@EditorFor(XLIFFKitWriterStep.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	static final String CREATEZIPFILE = "createZipFile"; //$NON-NLS-1$
	
	private boolean createZipFile;
	
	public Parameters () {
		reset();
	}
	
	public boolean getCreateZipFile () {
		return createZipFile;
	}
	
	public void setCreateZipFile (boolean createZipFile) {
		this.createZipFile = createZipFile;
	}

	public void reset () {
		createZipFile = false;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean(CREATEZIPFILE, createZipFile);
		return buffer.toString();
	}
	
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		createZipFile = buffer.getBoolean(CREATEZIPFILE, createZipFile);
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(CREATEZIPFILE,	"Create ZIP file", "Create ZIP file");
		return desc;
	}
	
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("XLIFF Kit Writer Options", true, false);	
		desc.addCheckboxPart(paramsDesc.get(CREATEZIPFILE));
		return desc;
	}

	
}
