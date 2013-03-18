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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.rules.RuleMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class LanguageTool {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private Parameters params;
	private LocaleId srcLoc;
	private LocaleId trgLoc;
	private JLanguageTool proofer;
	
	/**
	 * Creates a LanguageTool object with a given set of options.
	 * @param params the options to assign to this object (use null for the defaults).
	 */
	public LanguageTool (Parameters params,
		LocaleId sourceLocale,
		LocaleId targetLocale)
	{
		this.params = (params == null ? new Parameters() : params);
		srcLoc = sourceLocale;
		trgLoc = targetLocale;
	}
	
	/**
	 * Performs the proofreading of the text unit according to user selected options.
	 * @param tu the unit to process.
	 */
	public void run (ITextUnit tu) {
		// Filter out text units we don't need to check
		if ( !tu.isTranslatable() ) return;
		if ( tu.isEmpty() ) return;

		try {
			if ( proofer == null ) {
				proofer = new JLanguageTool(getLTLanguage(trgLoc));
				proofer.activateDefaultPatternRules();
				if ( params.getEnableFalseFriends() ) {
					proofer.activateDefaultFalseFriendRules();
				}
			}

			boolean isSegmented = tu.getSource().hasBeenSegmented();
			
			ISegments srcSegs = tu.getSourceSegments();
			for ( Segment srcSeg : srcSegs ) {
				Segment trgSeg = tu.getTargetSegment(trgLoc, srcSeg.getId(), false);
				if ( trgSeg == null ) continue;
				
				List<RuleMatch> matches = proofer.check(trgSeg.getContent().getCodedText());
				for ( RuleMatch match : matches ) {
					GenericAnnotation ann = new GenericAnnotation(GenericAnnotationType.LQI);
					ann.setString(GenericAnnotationType.LQI_TYPE, match.getRule().getLocQualityIssueType());
					ann.setString(GenericAnnotationType.LQI_COMMENT, match.getMessage());
					ann.setInteger(GenericAnnotationType.LQI_XSTART, match.getFromPos());
					ann.setInteger(GenericAnnotationType.LQI_XEND, match.getToPos());
					GenericAnnotation.addAnnotation(tu, ann);
				}
			}
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Cannot create or initialize the LanguageTool object. "+e.getMessage());
		}
	}

	/**
	 * Gets the LT language code for the given locale Id
	 * @param localeId the locale id to map.
	 * @return the LT language code, or null if the locale id could not be mapped.
	 */
	private Language getLTLanguage (LocaleId locId) {
		// Get the language from the Java Locale
		Language lang = Language.getLanguageForLocale(locId.toJavaLocale());
		// Check if it's a fall-back
		if ( lang == Language.AMERICAN_ENGLISH ) {
			if ( !locId.toString().equals("en-us") ) {
				LOGGER.warn("The locale Id '{}' is not supported. Using American English as a fall-back.", locId.toString());
			}
		}
		return lang;
	}
	
}
