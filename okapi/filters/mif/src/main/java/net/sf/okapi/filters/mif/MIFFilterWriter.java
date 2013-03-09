/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.filters.mif;

import java.nio.charset.CharsetEncoder;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

public class MIFFilterWriter extends GenericFilterWriter {

	public MIFFilterWriter (ISkeletonWriter skelWriter,
		EncoderManager encoderManager)
	{
		super(skelWriter, encoderManager);
	}

	@Override
	public void setOptions (LocaleId locale,
		String defaultEncoding)
	{
		// Force the encoding
		if ( !Util.isEmpty(defaultEncoding) && defaultEncoding.startsWith("UTF-16") ) {
			super.setOptions(locale, defaultEncoding);
		}
		else {
			// Null encoding should make the writer get it from the start-document info
			super.setOptions(locale, null);
		}
	}

	@Override
	protected CharsetEncoder createCharsetEncoder (String encodingtoUse) {
		// Special case for FrameRoman
		if ( encodingtoUse.equals(MIFFilter.FRAMEROMAN) ) {
			return new FrameRomanCharsetProvider().charsetForName(encodingtoUse).newEncoder();
		}
		// else: normal return
		return null; // Use default otherwise
	}
	
}
