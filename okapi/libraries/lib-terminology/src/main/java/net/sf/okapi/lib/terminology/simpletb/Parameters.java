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

package net.sf.okapi.lib.terminology.simpletb;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

public class Parameters extends BaseParameters implements IEditorDescriptionProvider {

	private static final String GLOSSARYPATH = "glossaryPath";
	private static final String SOURCELOCALE = "sourceLocale";
	private static final String TARGETLOCALE = "targetLocale";

	private String glossaryPath;
	private LocaleId sourceLocale;
	private LocaleId targetLocale;
	
	public Parameters () {
		reset();
		toString();
	}
	
	public String getGlossaryPath () {
		return glossaryPath;
	}

	public void setGlossaryPath (String glossaryPath) {
		this.glossaryPath = glossaryPath;
	}

	public LocaleId getSourceLocale () {
		return sourceLocale;
	}
	
	public void setSourceLocale (LocaleId locId) {
		sourceLocale = locId;
	}
	
	public LocaleId getTargetLocale () {
		return targetLocale;
	}
	
	public void setTargetLocale (LocaleId locId) {
		targetLocale = locId;
	}
	
	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		glossaryPath = buffer.getString(GLOSSARYPATH, glossaryPath);
		sourceLocale = LocaleId.fromString(buffer.getString(SOURCELOCALE, sourceLocale.toString()));
		targetLocale = LocaleId.fromString(buffer.getString(TARGETLOCALE, targetLocale.toString()));
	}

	@Override
	public void reset () {
		glossaryPath = "";
		sourceLocale = LocaleId.ENGLISH;
		targetLocale = LocaleId.FRENCH;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setString(GLOSSARYPATH, glossaryPath);
		buffer.setString(SOURCELOCALE, sourceLocale.toString());
		buffer.setString(TARGETLOCALE, targetLocale.toString());
		return buffer.toString();
	}

	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(GLOSSARYPATH, "TBX document", "Full path of the TBX document");
		desc.add(SOURCELOCALE, "Source locale", "Locale identifier for the source");
		desc.add(TARGETLOCALE, "Target locale", "Locale identifier for the target");
		return desc;
	}

	@Override
	public EditorDescription createEditorDescription (ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("SimpleTB Connector Settings", true, false);
		desc.addPathInputPart(paramsDesc.get(Parameters.GLOSSARYPATH), "TBX File", false);
		desc.addTextInputPart(paramsDesc.get(Parameters.SOURCELOCALE));
		desc.addTextInputPart(paramsDesc.get(Parameters.TARGETLOCALE));
		return desc;
	}

}
