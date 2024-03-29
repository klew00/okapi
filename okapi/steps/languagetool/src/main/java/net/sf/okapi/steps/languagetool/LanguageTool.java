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

import java.util.List;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.IssueAnnotation;
import net.sf.okapi.common.annotation.IssueType;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.ISegments;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.bitext.BitextRule;
import org.languagetool.tools.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanguageTool {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private final Parameters params;
	private final LocaleId srcLoc;
	private final LocaleId trgLoc;
	private final JLanguageTool srcLt;
	private final JLanguageTool trgLt;
	private final boolean skipSpelling;
	
	private List<BitextRule> bitextRules;
	
	/**
	 * Creates a LanguageTool object with a given set of options.
	 * @param params the options to assign to this object (use null for the defaults).
	 */
	public LanguageTool (Parameters params,
		LocaleId sourceLocale,
		LocaleId targetLocale)
	{
		this.params = (params == null ? new Parameters() : params);
		skipSpelling = !this.params.getCheckSpelling();
		srcLoc = sourceLocale;
		trgLoc = targetLocale;
		
		srcLt = startInstance(srcLoc);
		trgLt = startInstance(trgLoc);
		
		try {
			bitextRules = Tools.getBitextRules(srcLt.getLanguage(), trgLt.getLanguage());
		}
		catch ( Throwable e ) {
			LOGGER.warn("Cannot load bi-text rules ({}).\nOnly target rules for '{}' will be checked.",
				e.getMessage(), trgLt.getLanguage().getShortNameWithVariant());
			bitextRules = null;
		}
	}

	private JLanguageTool startInstance (LocaleId locId) {
		try {
			JLanguageTool lt = new JLanguageTool(getLTLanguage(locId));
			LOGGER.info("Using LT language '{}' for locale '{}'", lt.getLanguage().getShortNameWithVariant(), locId.toString());
			lt.activateDefaultPatternRules();
			if ( params.getEnableFalseFriends() ) {
				lt.activateDefaultFalseFriendRules();
			}
			return lt;
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Cannot create or initialize the LanguageTool object. "+e.getMessage());
		}
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
			boolean isSegmented = tu.getSource().hasBeenSegmented();
//TODO: source/target/bitext
			
			TextContainer srcTc = tu.getSource();
			TextContainer trgTc = tu.getTarget(trgLoc);
			ISegments srcSegs = srcTc.getSegments();
			ISegments trgSegs = null;
			if ( trgTc != null ) trgSegs = trgTc.getSegments();

			List<RuleMatch> srcMatches = null;
			List<RuleMatch> trgMatches = null;
			for ( Segment srcSeg : srcSegs ) {
				if ( trgSegs != null ) {
					Segment trgSeg = trgSegs.get(srcSeg.getId());
					if ( trgSeg != null ) {
						
						if ( bitextRules != null ) {
							trgMatches = Tools.checkBitext(
								srcSeg.getContent().getCodedText(), trgSeg.getContent().getCodedText(),
								srcLt, trgLt, bitextRules);
						}
						else {
							trgMatches = trgLt.check(trgSeg.getContent().getCodedText());
						}
					}
				}
				
				if ( params.getCheckSource() ) {
					srcMatches = srcLt.check(srcSeg.getContent().getCodedText());
				}

				// Attach the results
				if ( trgMatches != null ) {
					for ( RuleMatch match : trgMatches ) {
						GenericAnnotation.addAnnotation(trgTc, createAnnotation(match, true, srcSeg.getId()));
					}
				}
				if ( srcMatches != null ) {
					for ( RuleMatch match :srcMatches ) {
						GenericAnnotation.addAnnotation(srcTc, createAnnotation(match, false, srcSeg.getId()));
					}
				}
			}
		}
		catch ( Throwable e ) {
			throw new OkapiIOException("Error while checking. "+e.getMessage());
		}
	}

	/**
	 * Creates an LQI annotation based on the match and context.
	 * @param match the LT rule match.
	 * @param onTarget true if the issue was found on the target content.
	 * @param segId segment id.
	 * @return the new annotation, or null if none was created.
	 */
	private GenericAnnotation createAnnotation (RuleMatch match,
		boolean onTarget,
		String segId)
	{
		if ( skipSpelling && match.getRule().isSpellingRule() ) return null;
		String msg = match.getMessage();
		// Process the message for tags
		if ( msg.indexOf('&') > -1 ) {
			msg = msg.replace("&lt;suggestion>", "\"");
			msg = msg.replace("&lt;/suggestion>", "\"");
			msg = msg.replace("&amp;", "&");
		}
		// Create the entry
		IssueAnnotation ia = new IssueAnnotation(IssueType.LANGUAGETOOL_ERROR, msg, 2, segId,
			(onTarget ? -1 : match.getFromPos()), // start in source
			(onTarget ? -1 : match.getToPos()), // end in source
			(onTarget ? match.getFromPos() : -1 ), // start in target
			(onTarget ? match.getToPos() : -1 ), // end in target
			null);
		ia.setITSType(match.getRule().getLocQualityIssueType());
		return ia;
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
