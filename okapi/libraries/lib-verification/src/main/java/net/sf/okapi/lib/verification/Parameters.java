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

package net.sf.okapi.lib.verification;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.BaseParameters;

public class Parameters extends BaseParameters {
	
	private static final String OUTPUTPATH = "outputPath";
	private static final String AUTOOPEN = "autoOpen";
	private static final String LEADINGWS = "leadingWS";
	private static final String TRAILINGWS = "trailingWS";
	private static final String EMPTYTARGET = "emptyTarget";
	private static final String TARGETSAMEASSOURCE = "targetSameAsSource";
	private static final String TARGETSAMEASSOURCE_WITHCODES = "targetSameAsSourceWithCodes";
	private static final String CODEDIFFERENCE = "codeDifference";
	private static final String CHECKPATTERNS = "checkPatterns";
	private static final String PATTERNCOUNT = "patternCount";
	private static final String USEPATTERN = "usePattern";
	private static final String SOURCEPATTERN = "sourcePattern";
	private static final String TARGETPATTERN = "targetPattern";
	private static final String CHECKWITHLT = "checkWithLT";
	private static final String SERVERURL = "serverURL";
	private static final String TRANSLATELTMSG = "translateLTMsg";
	private static final String LTTRANSLATIONSOURCE = "ltTranslationSource";
	private static final String LTTRANSLATIONTARGET = "ltTranslationTarget";
	private static final String SAVESESSION = "saveSession";
	private static final String SESSIONPATH = "sessionPath";
	private static final String DOUBLEDWORD = "doubledWord";
	private static final String CHECKMAXCHARLENGTH = "checkMaxCharLength";
	private static final String MAXCHARLENGTH = "maxCharLength";
	private static final String CHECKMINCHARLENGTH = "checkMinCharLength";
	private static final String MINCHARLENGTH = "minCharLength";

	String outputPath;
	boolean autoOpen;
	boolean leadingWS;
	boolean trailingWS;
	boolean emptyTarget;
	boolean targetSameAsSource;
	boolean targetSameAsSourceWithCodes;
	boolean codeDifference;
	boolean checkPatterns;
	List<PatternItem> patterns;
	boolean checkWithLT;
	String serverURL;
	boolean translateLTMsg;
	String ltTranslationSource;
	String ltTranslationTarget;
	boolean saveSession;
	String sessionPath;
	boolean doubledWord;
	boolean checkMaxCharLength;
	int maxCharLength;
	boolean checkMinCharLength;
	int minCharLength;

	public Parameters () {
		reset();
	}
	
	public boolean getCheckMaxCharLength () {
		return checkMaxCharLength;
	}

	public void setCheckMaxCharLength (boolean checkMaxCharLength) {
		this.checkMaxCharLength = checkMaxCharLength;
	}

	public int getMaxCharLength () {
		return maxCharLength;
	}

	public void setMaxCharLength (int maxCharLength) {
		this.maxCharLength = maxCharLength;
	}

	public boolean getCheckMinCharLength() {
		return checkMinCharLength;
	}

	public void setCheckMinCharLength (boolean checkMinCharLength) {
		this.checkMinCharLength = checkMinCharLength;
	}

	public int getMinCharLength() {
		return minCharLength;
	}

	public void setMinCharLength(int minCharLength) {
		this.minCharLength = minCharLength;
	}

	public boolean getDoubledWord () {
		return doubledWord;
	}

	public void setDoubledWord (boolean doubledWord) {
		this.doubledWord = doubledWord;
	}

	public boolean getSaveSession () {
		return saveSession;
	}

	public void setSaveSession (boolean saveSession) {
		this.saveSession = saveSession;
	}

	public String getSessionPath () {
		return sessionPath;
	}

	public void setSessionPath (String sessionPath) {
		this.sessionPath = sessionPath;
	}

	public String getOutputPath () {
		return outputPath;
	}

	public void setOutputPath (String outputPath) {
		this.outputPath = outputPath;
	}

	public boolean getAutoOpen () {
		return autoOpen;
	}

	public void setAutoOpen (boolean autoOpen) {
		this.autoOpen = autoOpen;
	}

	public boolean getLeadingWS () {
		return leadingWS;
	}

	public void setLeadingWS (boolean leadingWS) {
		this.leadingWS = leadingWS;
	}

	public boolean getTrailingWS () {
		return trailingWS;
	}

	public void setTrailingWS (boolean trailingWS) {
		this.trailingWS = trailingWS;
	}

	public boolean getEmptyTarget () {
		return emptyTarget;
	}

	public void setEmptyTarget (boolean emptyTarget) {
		this.emptyTarget = emptyTarget;
	}

	public boolean getTargetSameAsSource () {
		return targetSameAsSource;
	}

	public void setTargetSameAsSource (boolean targetSameAsSource) {
		this.targetSameAsSource = targetSameAsSource;
	}

	public boolean getTargetSameAsSourceWithCodes () {
		return targetSameAsSourceWithCodes;
	}

	public void setTargetSameAsSourceWithCodes (boolean targetSameAsSourceWithCodes) {
		this.targetSameAsSourceWithCodes = targetSameAsSourceWithCodes;
	}

	public boolean getCodeDifference () {
		return codeDifference;
	}

	public void setCodeDifference (boolean codeDifference) {
		this.codeDifference = codeDifference;
	}

	public boolean getCheckPatterns () {
		return checkPatterns;
	}

	public void setCheckPatterns (boolean patterns) {
		this.checkPatterns = patterns;
	}
	
	public List<PatternItem> getPatterns () {
		return this.patterns;
	}
	
	public void setPatterns (List<PatternItem> patterns) {
		this.patterns = patterns;
	}
	
	public boolean getCheckWithLT () {
		return this.checkWithLT;
	}

	public void setCheckWithLT (boolean checkWithLT) {
		this.checkWithLT = checkWithLT;
	}
	
	public String getServerURL () {
		return this.serverURL;
	}
	
	public void setServerURL (String serverURL) {
		this.serverURL = serverURL;
	}
	
	public boolean getTranslateLTMsg () {
		return translateLTMsg;
	}

	public void setTranslateLTMsg (boolean translateLTMsg) {
		this.translateLTMsg = translateLTMsg;
	}

	public String getLtTranslationSource () {
		return ltTranslationSource;
	}

	public void setLtTranslationSource (String ltTranslationSource) {
		this.ltTranslationSource = ltTranslationSource;
	}

	public String getLtTranslationTarget () {
		return ltTranslationTarget;
	}

	public void setLtTranslationTarget (String ltTranslationTarget) {
		this.ltTranslationTarget = ltTranslationTarget;
	}

	@Override
	public void reset () {
		outputPath = "${rootDir}/qa-report.html";
		autoOpen = false;
		leadingWS = true;
		trailingWS = true;
		emptyTarget = true;
		targetSameAsSource = true;
		targetSameAsSourceWithCodes = true;
		codeDifference = true;
		checkPatterns = true;
		checkWithLT = false;
		serverURL = "http://localhost:8081/"; // Default
		translateLTMsg = false;
		ltTranslationSource = "";
		ltTranslationTarget = "en";
		saveSession = true;
		sessionPath = "${rootDir}/qa-session"+QualityCheckSession.FILE_EXTENSION;
		doubledWord = true;
		
		checkMaxCharLength = true;
		maxCharLength = 190;
		checkMinCharLength = true;
		minCharLength = 50;

		patterns = new ArrayList<PatternItem>();
		// Parentheses
		patterns.add(new PatternItem(
			"[\\(\\)]", "<same>",
			true, "Parentheses"));
		// Bracketing characters (except parentheses)
		patterns.add(new PatternItem(
			"[\\p{Ps}\\p{Pe}&&[^\\(\\)]]", "<same>",
			true, "Bracketing characters (except parentheses)"));
		// Email addresses
		patterns.add(new PatternItem(
			"[\\w\\.\\-]+@[\\w\\.\\-]+", "<same>",
			true, "Email addresses"));
		// URLs
		patterns.add(new PatternItem( //TODO: file URL
			"https?:[\\w/\\.:;+\\-~\\%#\\$?=&,()]+|www\\.[\\w/\\.:;+\\-~\\%#\\$?=&,()]+|ftp:[\\w/\\.:;+\\-~\\%#?=&,]+", "<same>",
			true, "URLs"));
		// IP addresses
		patterns.add(new PatternItem(
			"\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b", "<same>",
			true, "IP addresses"));
		// C-style printf 
		patterns.add(new PatternItem(
			"%(([-0+#]?)[-0+#]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpn]", "<same>",
			true, "C-style printf codes"));
		
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		outputPath = buffer.getString(OUTPUTPATH, outputPath);
		autoOpen = buffer.getBoolean(AUTOOPEN, autoOpen);
		leadingWS = buffer.getBoolean(LEADINGWS, leadingWS);
		trailingWS = buffer.getBoolean(TRAILINGWS, trailingWS);
		emptyTarget = buffer.getBoolean(EMPTYTARGET, emptyTarget);
		targetSameAsSource = buffer.getBoolean(TARGETSAMEASSOURCE, targetSameAsSource);
		targetSameAsSourceWithCodes = buffer.getBoolean(TARGETSAMEASSOURCE_WITHCODES, targetSameAsSourceWithCodes);
		codeDifference = buffer.getBoolean(CODEDIFFERENCE, codeDifference);
		checkWithLT = buffer.getBoolean(CHECKWITHLT, checkWithLT);
		serverURL = buffer.getString(SERVERURL, serverURL);
		translateLTMsg = buffer.getBoolean(TRANSLATELTMSG, translateLTMsg);
		ltTranslationSource = buffer.getString(LTTRANSLATIONSOURCE, ltTranslationSource);
		ltTranslationTarget = buffer.getString(LTTRANSLATIONTARGET, ltTranslationTarget);
		saveSession = buffer.getBoolean(SAVESESSION, saveSession);
		sessionPath = buffer.getString(SESSIONPATH, sessionPath);
		doubledWord = buffer.getBoolean(DOUBLEDWORD, doubledWord);
		checkMaxCharLength = buffer.getBoolean(CHECKMAXCHARLENGTH, checkMaxCharLength);
		maxCharLength = buffer.getInteger(MAXCHARLENGTH, maxCharLength);
		checkMinCharLength = buffer.getBoolean(CHECKMINCHARLENGTH, checkMinCharLength);
		minCharLength = buffer.getInteger(MINCHARLENGTH, minCharLength);
		// Patterns
		checkPatterns = buffer.getBoolean(CHECKPATTERNS, checkPatterns);
		int count = buffer.getInteger(PATTERNCOUNT, 0);
		if ( count > 0 ) patterns.clear(); // Clear the defaults
		for ( int i=0; i<count; i++ ) {
			boolean enabled = buffer.getBoolean(String.format("%s%d", USEPATTERN, i), true);
			String source = buffer.getString(String.format("%s%d", SOURCEPATTERN, i), "");
			String target = buffer.getString(String.format("%s%d", TARGETPATTERN, i), PatternItem.SAME);
			patterns.add(new PatternItem(source, target, enabled));
		}
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setString(OUTPUTPATH, outputPath);
		buffer.setBoolean(AUTOOPEN, autoOpen);
		buffer.setBoolean(LEADINGWS, leadingWS);
		buffer.setBoolean(TRAILINGWS, trailingWS);
		buffer.setBoolean(EMPTYTARGET, emptyTarget);
		buffer.setBoolean(TARGETSAMEASSOURCE, targetSameAsSource);
		buffer.setBoolean(TARGETSAMEASSOURCE_WITHCODES, targetSameAsSourceWithCodes);
		buffer.setBoolean(CODEDIFFERENCE, codeDifference);
		buffer.setBoolean(CHECKWITHLT, checkWithLT);
		buffer.setString(SERVERURL, serverURL);
		buffer.setBoolean(TRANSLATELTMSG, translateLTMsg);
		buffer.setString(LTTRANSLATIONSOURCE, ltTranslationSource);
		buffer.setString(LTTRANSLATIONTARGET, ltTranslationTarget);
		buffer.setBoolean(SAVESESSION, saveSession);
		buffer.setString(SESSIONPATH, sessionPath);
		buffer.setBoolean(DOUBLEDWORD, doubledWord);
		buffer.setBoolean(CHECKMAXCHARLENGTH, checkMaxCharLength);
		buffer.setInteger(MAXCHARLENGTH, maxCharLength);
		buffer.setBoolean(CHECKMINCHARLENGTH, checkMinCharLength);
		buffer.setInteger(MINCHARLENGTH, minCharLength);
		// Patterns
		buffer.setBoolean(CHECKPATTERNS, checkPatterns);
		buffer.setInteger(PATTERNCOUNT, patterns.size());
		for ( int i=0; i<patterns.size(); i++ ) {
			buffer.setBoolean(String.format("%s%d", USEPATTERN, i), patterns.get(i).enabled);
			buffer.setString(String.format("%s%d", SOURCEPATTERN, i), patterns.get(i).source);
			buffer.setString(String.format("%s%d", TARGETPATTERN, i), patterns.get(i).target);
		}
		return buffer.toString();
	}
	
}
