/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.wordcount.common;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.steps.tokenization.Tokenizer;
import net.sf.okapi.steps.tokenization.common.TokensAnnotation;
import net.sf.okapi.steps.tokenization.tokens.Tokens;

public abstract class TokenCountStep extends BaseCountStep {

	protected abstract String[] getTokenNames();
	
	@Override
	protected long count(TextContainer textContainer, LocaleId locale) {		
		TokensAnnotation ta = textContainer.getAnnotation(TokensAnnotation.class);
		Tokens tokens = null;
		
		if (ta != null)			
			tokens = ta.getFilteredList(getTokenNames());			
		else
			tokens = Tokenizer.tokenize(textContainer, locale, getTokenNames());
		
		if (tokens == null) return 0;
		//logMessage(Level.FINE, tokens.toString());
		return tokens.size();		
	}
			
	@Override
	protected long count(Segment segment, LocaleId locale) {		
		TokensAnnotation ta = segment.getAnnotation(TokensAnnotation.class);
		Tokens tokens = null;
		
		if (ta != null)			
			tokens = ta.getFilteredList(getTokenNames());			
		else
			tokens = Tokenizer.tokenize(segment.getContent(), locale, getTokenNames());
		
		if (tokens == null) return 0;
		
		return tokens.size();		
	}
	
	@Override
	protected long countInTextUnit(ITextUnit textUnit) {
		if (textUnit == null) return 0;
		
		LocaleId srcLocale = getSourceLocale();		
		TextContainer source = textUnit.getSource();
		
		// Individual segments metrics
		long segCount = 0;
		long segmentsCount = 0;
		long textContainerCount = 0;
		
		ISegments segs = source.getSegments();
		if (segs != null) {
			for (Segment seg : segs) {
				segCount = count(seg, srcLocale);
				segmentsCount += segCount;
				saveToMetrics(seg, segCount);
			}
		}
		// TC metrics
		textContainerCount = count(source, srcLocale);
		saveToMetrics(source, textContainerCount);
		
		if (textContainerCount > 0) return textContainerCount;  
		if (segmentsCount > 0) return segmentsCount;
		return 0;
	}
}
