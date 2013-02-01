/*===========================================================================
  Copyright (C) 2009-2013 by the Okapi Framework contributors
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

package net.sf.okapi.lib.segmentation.opennlp;

import java.util.Collections;
import java.util.Set;

import net.sf.okapi.common.LocaleId;
import opennlp.tools.sentdetect.DefaultEndOfSentenceScanner;
import opennlp.tools.sentdetect.DefaultSDContextGenerator;
import opennlp.tools.sentdetect.EndOfSentenceScanner;
import opennlp.tools.sentdetect.SDContextGenerator;
import opennlp.tools.sentdetect.lang.Factory;
import opennlp.tools.sentdetect.lang.th.SentenceContextGenerator;

public class OkapiSentenceDetectorFactory extends Factory {
	private LocaleId locale;
	private char[] segchar = {'.', '!', '?', '"', '\'', ')'};

	public OkapiSentenceDetectorFactory(LocaleId locale) {
		this.locale = locale;
	}

	@Override
	public EndOfSentenceScanner createEndOfSentenceScanner(String languageCode) {
		if ("th".equals(languageCode)) {
			return new DefaultEndOfSentenceScanner(new char[] { ' ', '\n' });
		}

		return new DefaultEndOfSentenceScanner(new char[] { '.', '!', '?' });
	}

	@Override
	public SDContextGenerator createSentenceContextGenerator(String languageCode, Set<String> abbreviations) {
		if ("th".equals(languageCode)) {
			return new SentenceContextGenerator();
		}

		return new DefaultSDContextGenerator(abbreviations, segchar); //new char[] { '.',	'!', '?' });
	}

	@Override
	public SDContextGenerator createSentenceContextGenerator(String languageCode) {
		return createSentenceContextGenerator(languageCode, Collections.<String> emptySet());
	}
}
