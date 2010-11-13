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

package net.sf.okapi.steps.simplekit.xliff;

import java.io.File;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filterwriter.XLIFFWriter;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.steps.simplekit.common.BasePackageReader;
import net.sf.okapi.steps.simplekit.common.BasePackageWriter;
import net.sf.okapi.steps.simplekit.common.ManifestItem;
import net.sf.okapi.steps.simplekit.creation.Parameters;

/**
 * Implements {@link IPackageReader} for generic XLIFF translation packages.
 */
public class XLIFFPackageReader extends BasePackageReader {

	@Override
	protected void postProcessPackageItem (String path, LocaleId sourceLocale,
		LocaleId targetLocale) {
		// TODO Auto-generated method stub
		
	}

}
