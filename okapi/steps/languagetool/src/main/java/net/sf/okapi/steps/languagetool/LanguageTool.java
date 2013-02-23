/*
 * ===========================================================================
 * Copyright (C) 2013 by the Okapi Framework contributors
 * -----------------------------------------------------------------------------
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
 * ===========================================================================
 */

package net.sf.okapi.steps.languagetool;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

import org.languagetool.JLanguageTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanguageTool {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	private Parameters params;
	private JLanguageTool proofer;
	
	/**
	 * Creates a LanguageTool object with default options.
	 */
	public LanguageTool() {

		this(null);
	}

	/**
	 * Creates a LanguageTool object with a given set of options.
	 * @param params the options to assign to this object (use null for the defaults).
	 */
	public LanguageTool(Parameters params) {

		this.params = (params == null ? new Parameters() : params);
	}
	
	/**
	 * Performs the proofreading of the text unit according to user selected options.
	 * @param tu the unit containing the text to clean
	 * @param targetLocale 
	 */
	public void run(ITextUnit tu, LocaleId targetLocale) {
		
		if (!tu.isEmpty()) {
			ISegments srcSegs = tu.getSourceSegments();
			for (Segment srcSeg : srcSegs) {
				Segment trgSeg = tu.getTargetSegment(targetLocale, srcSeg.getId(), false);

				if (trgSeg == null) {
					continue;
				}
				// TODO: call LanguageTool methods
			}
		}
	}

	/**
	 * Converts whitespace ({tab}, {space}, {CR}, {LF}) to single space.
	 * @param tu: the TextUnit containing the segments to update
	 * @param seg: the Segment to update
	 * @param targetLocale: the language for which the text should be updated
	 */
	protected void normalizeWhitespace(ITextUnit tu, Segment seg, LocaleId targetLocale) {
		
		TextFragment.unwrap(seg.getContent());
		TextFragment.unwrap(tu.getTargetSegment(targetLocale, seg.getId(), false).getContent());
	}
	
} // end class