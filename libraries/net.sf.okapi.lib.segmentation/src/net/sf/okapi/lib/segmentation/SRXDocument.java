/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.lib.segmentation;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.okapi.common.DefaultEntityResolver;
import net.sf.okapi.common.NSContextManager;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.resource.TextFragment;

public class SRXDocument {
	
	private static final String   NSURI_SRX20 = "http://www.lisa.org/srx20";
	private static final String   NSURI_SRX10 = "http://www.lisa.org/srx10";
	private static final String   NSURI_OKPSRX = "http://okapi.sf.net/srx-extensions";
	private static final String   NSPREFIX_OKPSRX = "okpsrx";

	// Do not include segment markers because they should not be present on text to segment
	private static final String   INLINECODES_PATTERN = String.format("(([\\u%X\\u%X\\u%X].)*)",
		TextFragment.MARKER_OPENING, TextFragment.MARKER_CLOSING, TextFragment.MARKER_ISOLATED);
	private static final String   NOAUTO = "[noauto]";
			
	private boolean cascade;
	private boolean segmentSubFlows;
	private boolean includeStartCodes;
	private boolean includeEndCodes;
	private boolean includeIsolatedCodes;
	private boolean oneSegmentIncludesAll;
	private boolean trimLeadingWS;
	private boolean trimTrailingWS;
	private String version = "2.0";
	private String warning;
	private String sampleText;
	private String sampleLanguage;
	private boolean modified;
	private boolean testOnSelectedGroup; 
	private ArrayList<LanguageMap> langMaps;
	private LinkedHashMap<String, ArrayList<Rule>> langRules;
	private String maskRule;

	public SRXDocument () {
		resetAll();
	}

	public String getVersion () {
		return version;
	}
	
	public boolean hasWarning () {
		return (( warning != null ) && ( warning.length() > 0 ));
	}

	public String getWarning () {
		if ( warning == null ) return "";
		else return warning;
	}
	
	public void resetAll () {
		langMaps = new ArrayList<LanguageMap>();
		langRules = new LinkedHashMap<String, ArrayList<Rule>>();
		maskRule = null;
		modified = false;

		segmentSubFlows = true; // SRX default
		cascade = false; // There is no SRX default for this
		includeStartCodes = false; // SRX default
		includeEndCodes = true; // SRX default
		includeIsolatedCodes = false; // SRX default
		
		oneSegmentIncludesAll = false; // Extension
		trimLeadingWS = false; // Extension
		trimTrailingWS = false; // Extension

		sampleText = "Mr. Holmes is from the <I>U.K.</I> <B>Is Dr. Watson from there too?</B> Yes: both are.<BR/>";
		sampleLanguage = "en";
	}
	
	public LinkedHashMap<String, ArrayList<Rule>> getAllLanguageRules () {
		return langRules;
	}
	
	public ArrayList<Rule> getLanguageRules (String ruleName) {
		return langRules.get(ruleName);
	}
	
	public ArrayList<LanguageMap> getAllLanguagesMaps () {
		return langMaps;
	}
	
	public boolean segmentSubFlows () {
		return segmentSubFlows;
	}
	
	public void setSegmentSubFlows (boolean value) {
		segmentSubFlows = value;
	}
	
	public boolean cascade () {
		return cascade;
	}
	
	public void setCascade (boolean value) {
		if ( value != cascade ) {
			cascade = value;
			modified = true;
		}
	}
	
	/**
	 * Indicates if when there is a single segment in a text it should include
	 * the whole text (no spaces or codes trim left/right)
	 * @return True if a text with a single segment should include the whole
	 * text.
	 */
	public boolean oneSegmentIncludesAll () {
		return oneSegmentIncludesAll;
	}
	
	/**
	 * Sets the indicator that tells if when there is a single segment in a 
	 * text it should include the whole text (no spaces or codes trim left/right)
	 * text.
	 * @param value The new value to set.
	 */
	public void setOneSegmentIncludesAll (boolean value) {
		if ( value != oneSegmentIncludesAll ) {
			oneSegmentIncludesAll = value;
			modified = true;
		}
	}

	/**
	 * Indicates if leading white-spaces should be left outside the segments.
	 * @return True if the leading white-spaces should be trimmed.
	 */
	public boolean trimLeadingWhitespaces () {
		return trimLeadingWS;
	}
	
	/**
	 * Sets the indicator that tells if leading white-spaces should be left outside 
	 * the segments.
	 * @param value The new value to set.
	 */
	public void setTrimLeadingWhitespaces (boolean value) {
		if ( value != trimLeadingWS ) {
			trimLeadingWS = value;
			modified = true;
		}
	}
	
	/**
	 * Indicates if trailing white-spaces should be left outside the segments.
	 * @return True if the trailing white-spaces should be trimmed.
	 */
	public boolean trimTrailingWhitespaces () {
		return trimTrailingWS;
	}
	
	/**
	 * Sets the indicator that tells if trailing white-spaces should be left outside 
	 * the segments.
	 * @param value The new value to set.
	 */
	public void setTrimTrailingWhitespaces (boolean value) {
		if ( value != trimTrailingWS ) {
			trimTrailingWS = value;
			modified = true;
		}
	}
	
	/**
	 * Indicates if start codes should be included (See SRX implementation notes).
	 * @return True if they should be included, false otherwise.
	 */
	public boolean includeStartCodes () {
		return includeStartCodes;
	}
	
	/**
	 * Sets the indicator that tells if start codes should be included or not.
	 * (See SRX implementation notes).
	 * @param value The new value to use.
	 */
	public void setIncludeStartCodes (boolean value) {
		if ( value != includeStartCodes ) {
			includeStartCodes = value;
			modified = true;
		}
	}
	
	/**
	 * Indicates if end codes should be included (See SRX implementation notes).
	 * @return True if they should be included, false otherwise.
	 */
	public boolean includeEndCodes () {
		return includeEndCodes;
	}
	
	/**
	 * Sets the indicator that tells if end codes should be included or not.
	 * (See SRX implementation notes).
	 * @param value The new value to use.
	 */
	public void setIncludeEndCodes (boolean value) {
		if ( value != includeEndCodes ) {
			includeEndCodes = value;
			modified = true;
		}
	}
	
	/**
	 * Indicates if isolated codes should be included (See SRX implementation notes).
	 * @return True if they should be included, false otherwise.
	 */
	public boolean includeIsolatedCodes () {
		return includeIsolatedCodes;
	}
	
	/**
	 * Sets the indicator that tells if isolated codes should be included or not.
	 * (See SRX implementation notes).
	 * @param value The new value to use.
	 */
	public void setIncludeIsolatedCodes (boolean value) {
		if ( value != includeIsolatedCodes ) {
			includeIsolatedCodes = value;
			modified = true;
		}
	}

	/**
	 * Gets the current pattern of the mask rule.
	 * @return The current pattern of the mask rule.
	 */
	public String getMaskRule () {
		return maskRule;
	}
	
	/**
	 * Sets the pattern for the mask rule.
	 * @param pattern The pattern to use for the mask rule.
	 */
	public void setMaskRule (String pattern) {
		if ( pattern != null ) {
			if ( !pattern.equals(maskRule) ) {
				modified = true;
			}
		}
		else if ( maskRule != null ) {
			modified = true;
		}
		maskRule = pattern;
	}
	
	/**
	 * Gets the current sample text. This text is an example string that can be used
	 * to test the various rules. It can be handy to be able to save it along with
	 * the SRX document.
	 * @return The sample text, or an empty string.
	 */
	public String getSampleText () {
		if ( sampleText == null ) return "";
		else return sampleText;
	}
	
	/**
	 * Sets the sample text.
	 * @param value Sample text.
	 */
	public void setSampleText (String value) {
		if ( value != null ) {
			if ( !value.equals(sampleText) ) {
				modified = true;
			}
		}
		else if ( sampleText != null ) {
			modified = true;
		}
		sampleText = value;
	}
	
	/**
	 * Gets the current sample language code.
	 * @return The sample language code.
	 */
	public String getSampleLanguage () {
		return sampleLanguage;
	}
	
	/**
	 * Sets the sample language code. Null or empty strings are changed
	 * to the default value.
	 * @param value The new sample language code.
	 */
	public void setSampleLanguage (String value) {
		if (( value == null ) || ( value.length() == 0 )) {
			sampleLanguage = "en";
			modified = true;
		}
		else {
			if ( !value.equals(sampleLanguage) ) {
				sampleLanguage = value;
				modified = true;
			}
		}
	}
	
	/**
	 * Indicates that, when sampling the rules, the sample should be
	 * computed using only a selected group of rules.
	 * @return True to test using only a selected group of rules.
	 * False to test using all the rules matching a given language.
	 */
	public boolean testOnSelectedGroup () {
		return testOnSelectedGroup;
	}
	
	/**
	 * Sets the indicator on how to apply rules for samples.
	 * @param value True to test using only a selected group of rules.
	 * False to test using all the rules matching a given language.
	 */
	public void setTestOnSelectedGroup (boolean value) {
		if ( value != testOnSelectedGroup ) {
			testOnSelectedGroup = value;
			modified = true;
		}
	}
	
	/**
	 * Indicates if the segmenter data have been modified since the
	 * last load or save.
	 * @return True if they have been modified, false if there is no changes.
	 */
	public boolean isModified () {
		return modified;
	}
	
	/**
	 * Sets the flag indicating if the segmenter data have been
	 * modified since the last load or save. If you make change to the rules or
	 * language maps directly to the lists, make sure to set this flag to true.
	 * @param value The new value.
	 */
	public void setIsModified (boolean value) {
		modified = value;
	}
	
	/**
	 * Adds a language rule to this SRX document. If another language rule
	 * with the same name exists already it will be replaced by the
	 * new one, without warning.
	 * @param name Name of the language rule to add.
	 * @param langRule Language rule object to add.
	 */
	public void addLanguageRule (String name,
		ArrayList<Rule> langRule)
	{
		langRules.put(name, langRule);
		modified = true;
	}
	
	/**
	 * Adds a language map to this SRX document. The new map is added
	 * at the end of the one already there.
	 * @param langMap The language map object to add.
	 */
	public void addLanguageMap (LanguageMap langMap) {
		langMaps.add(langMap);
		modified = true;
	}
	
	/**
	 * Sets the rules for the segmentation. This method
	 * applies the language code you specify to the language mappings
	 * currently available in the segmenter and compile the rules
	 * when one or more language map is found. The matching is done in
	 * the order of the list of language maps and more than one can be 
	 * selected if {@link #cascade()} is true.
	 * @param languageCode The language code. the value should be a 
	 * BCP-47 value (e.g. "de", "fr-ca", etc.)
	 * @param existingSegmenter Optional existing segmenter object to re-use.
	 * Use null for not re-using anything.
	 */
	public Segmenter applyLanguageRules (String languageCode,
		Segmenter existingSegmenter)
	{
		Segmenter segmenter = existingSegmenter;
		if ( segmenter != null ) {
			// Check if we really need to re-compile
			if ( languageCode != null ) {
				if ( languageCode.equals(segmenter.getLanguage())
					&& (cascade == segmenter.cascade()) )
					return segmenter;
			}
			segmenter.reset();
		}
		else {
			segmenter = new Segmenter();
		}
		
		segmenter.setCascade(cascade);
		segmenter.setOptions(segmentSubFlows, includeStartCodes,
			includeEndCodes, includeIsolatedCodes, 	oneSegmentIncludesAll,
			trimLeadingWS, trimTrailingWS);
		
		for ( LanguageMap langMap : langMaps ) {
			if ( Pattern.matches(langMap.pattern, languageCode) ) {
				compileRules(segmenter, langMap.ruleName);
				if ( !segmenter.cascade() ) break; // Stop at the first matching map
			}
		}

		segmenter.setLanguage(languageCode);
		return segmenter;
	}
	
	/**
	 * Applies a single language rule group to do the segmentation.
	 * @param ruleName The name of the rule group to apply.
	 */
	public Segmenter applySingleLanguageRule (String ruleName,
		Segmenter existingSegmenter)
	{
		Segmenter segmenter = existingSegmenter;
		if ( segmenter != null ) {
			// Check if we really need to re-compile
			if ( ruleName != null ) {
				if ( ("__"+ruleName).equals(segmenter.getLanguage()) )
					return segmenter;
			}
			segmenter.reset();
		}
		else {
			segmenter = new Segmenter();
		}

		segmenter.setOptions(segmentSubFlows, includeStartCodes,
			includeEndCodes, includeIsolatedCodes, oneSegmentIncludesAll,
			trimLeadingWS, trimTrailingWS);
		compileRules(segmenter, ruleName);
		segmenter.setLanguage("__"+ruleName);
		return segmenter;
	}

	/**
	 * Compiles a language rule into the current set of active rules.
	 * @param ruleName The name of the language rule to compile.
	 */
	private void compileRules (Segmenter segmenter,
		String ruleName)
	{
		if ( !langRules.containsKey(ruleName) ) {
			throw new SegmentationRuleException("language rule '"+ruleName+"' not found.");
		}
		ArrayList<Rule> langRule = langRules.get(ruleName);
		for ( Rule rule : langRule ) {
			if ( rule.isActive ) {
				if ( rule.before.endsWith(NOAUTO)) {
					segmenter.addRule(
						// If the rule.before ends with NOAUTO, then we do not put pattern for in-line codes
						new CompiledRule("("+rule.before.substring(0, rule.before.length()-NOAUTO.length())
							+")("+rule.after+")", rule.isBreak));
				}
				else {
					segmenter.addRule(
						// The compiled rule is made of two groups: the pattern before and the pattern after
						// the break. A special pattern for in-line codes is also added transparently.
						new CompiledRule("("+rule.before+INLINECODES_PATTERN+")("+rule.after+")",
							rule.isBreak));
				}
			}
		}
		
		// Range rules
		segmenter.setMaskRule(maskRule);
	}
	
	/**
	 * Loads an SRX rules file.
	 * @param data The character sequence to load.
	 * The rules can be embedded inside another vocabulary.
	 */
	public void loadRules (CharSequence data) {
		loadRules(data, 1);
		modified = true;
	}
	
	/**
	 * Loads an SRX rules file.
	 * @param pathOrURL The full path or URL of the rules file to load.
	 * The rules can be embedded inside another vocabulary.
	 */
	public void loadRules (String pathOrURL) {
		loadRules(pathOrURL, 0);
	}			
			
	public void loadRules (Object input,
		int inputType )
	{
		try {
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Fact.setNamespaceAware(true);
			DocumentBuilder docBuilder;
			docBuilder = Fact.newDocumentBuilder();
			docBuilder.setEntityResolver(new DefaultEntityResolver());

			Document doc;
			if ( inputType == 0 ) {
				String pathOrURL = (String)input;
				doc = docBuilder.parse(Util.makeURIFromPath(pathOrURL));
			}
			else {
				CharSequence data = (CharSequence)input;
				doc = docBuilder.parse(new InputSource(new StringReader(data.toString())));
			}

			resetAll();
			// Macintosh work-around
			// When you use -XstartOnFirstThread as a java -Xarg on Leopard, your ContextClassloader gets set to null.
			// That is not the case on 10.4 or with Windows or Linux flavors
			// This allows XPathFactory.newInstance() to have a non-null context
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			// end work-around
			XPathFactory xpathFac = XPathFactory.newInstance();

			XPath xpath = xpathFac.newXPath();
			NSContextManager nsContext = new NSContextManager();
			nsContext.add("srx", NSURI_SRX20);
			nsContext.add(NSPREFIX_OKPSRX, NSURI_OKPSRX);
			nsContext.add("srx1", NSURI_SRX10);
			xpath.setNamespaceContext(nsContext);

			// Try to get the root and detect if namespaces are used or not. 
			String ns = NSURI_SRX20;
			XPathExpression xpe = xpath.compile("//srx:srx");
			NodeList srxList = (NodeList)xpe.evaluate(doc, XPathConstants.NODESET);
			if ( srxList.getLength() < 1 ) {
				xpe = xpath.compile("//srx1:srx");
				srxList = (NodeList)xpe.evaluate(doc, XPathConstants.NODESET);
				if ( srxList.getLength() < 1 ) {
					xpe = xpath.compile("//srx");
					srxList = (NodeList)xpe.evaluate(doc, XPathConstants.NODESET);
					if ( srxList.getLength() < 1 ) {
						return;
					}
					ns = "";
				}
				else ns = NSURI_SRX10;
			}
			
			// Treat the first occurrence (we assume there is never more in one file)
			Element srxElem = (Element)srxList.item(0);
			String tmp = srxElem.getAttribute("version");
			if ( tmp.equals("1.0") ) {
				version = tmp;
				warning = "SRX version 1.0 rules are subject to different interpretation.\nRead the help for more information.";
			}
			else if ( tmp.equals("2.0") ) {
				version = tmp;
				warning = null;
			}
			else throw new OkapiIOException("Invalid version value.");
			
			Element elem1 = getFirstElementByTagNameNS(ns, "header", srxElem);
			tmp = elem1.getAttribute("segmentsubflows");
			if ( tmp.length() > 0 ) segmentSubFlows = "yes".equals(tmp);
			tmp = elem1.getAttribute("cascade");
			if ( tmp.length() > 0 ) cascade = "yes".equals(tmp);

			// formathandle elements
			NodeList list2 = elem1.getElementsByTagNameNS(ns, "formathandle");
			for ( int i=0; i<list2.getLength(); i++ ) {
				Element elem2 = (Element)list2.item(i);
				tmp = elem2.getAttribute("type");
				if ( "start".equals(tmp) ) {
					tmp = elem2.getAttribute("include");
					if ( tmp.length() > 0 ) includeStartCodes = "yes".equals(tmp); 
				}
				else if ( "end".equals(tmp) ) {
					tmp = elem2.getAttribute("include");
					if ( tmp.length() > 0 ) includeEndCodes = "yes".equals(tmp); 
				}
				else if ( "isolated".equals(tmp) ) {
					tmp = elem2.getAttribute("include");
					if ( tmp.length() > 0 ) includeIsolatedCodes = "yes".equals(tmp); 
				}
			}
			
			// Extension: options
			Element elem2 = getFirstElementByTagNameNS(NSURI_OKPSRX, "options", elem1);
			if ( elem2 != null ) {
				tmp = elem2.getAttribute("oneSegmentIncludesAll");
				if ( tmp.length() > 0 ) oneSegmentIncludesAll = "yes".equals(tmp);
				
				tmp = elem2.getAttribute("trimLeadingWhitespaces");
				if ( tmp.length() > 0 ) trimLeadingWS = "yes".equals(tmp);
				
				tmp = elem2.getAttribute("trimTrailingWhitespaces");
				if ( tmp.length() > 0 ) trimTrailingWS = "yes".equals(tmp);
			}

			// Extension: sample
			elem2 = getFirstElementByTagNameNS(NSURI_OKPSRX, "sample", elem1);
			if ( elem2 != null ) {
				setSampleText(Util.getTextContent(elem2));
				tmp = elem2.getAttribute("language");
				if ( tmp.length() > 0 ) setSampleLanguage(tmp);
				tmp = elem2.getAttribute("useMappedRules");
				if ( tmp.length() > 0 ) setTestOnSelectedGroup("no".equals(tmp));
			}
			
			// Extension: rangeRule
			elem2 = getFirstElementByTagNameNS(NSURI_OKPSRX, "rangeRule", elem1);
			if ( elem2 != null ) {
				setMaskRule(Util.getTextContent(elem2));
			}
			
			// Get the body element
			elem1 = getFirstElementByTagNameNS(ns, "body", srxElem);
			
			// languagerules
			elem2 = getFirstElementByTagNameNS(ns, "languagerules", elem1);
			// For each languageRule
			list2 = elem2.getElementsByTagNameNS(ns, "languagerule");
			for ( int i=0; i<list2.getLength(); i++ ) {
				Element elem3 = (Element)list2.item(i);
				ArrayList<Rule> tmpList = new ArrayList<Rule>();
				String ruleName = elem3.getAttribute("languagerulename");
				// For each rule
				NodeList list3 = elem3.getElementsByTagNameNS(ns, "rule");
				for ( int j=0; j<list3.getLength(); j++ ) {
					Element elem4 = (Element)list3.item(j);
					Rule newRule = new Rule();
					tmp = elem4.getAttribute("break");
					if ( tmp.length() > 0 ) newRule.isBreak = "yes".equals(tmp);
					tmp = elem4.getAttributeNS(NSURI_OKPSRX, "active");
					if ( tmp.length() > 0 ) newRule.isActive = "yes".equals(tmp);
					Element elem5 = getFirstElementByTagNameNS(ns, "beforebreak", elem4);
					if ( elem5 != null ) newRule.before = Util.getTextContent(elem5);
					elem5 = getFirstElementByTagNameNS(ns, "afterbreak", elem4);
					if ( elem5 != null ) newRule.after = Util.getTextContent(elem5);
					tmpList.add(newRule);
				}
				langRules.put(ruleName, tmpList);
			}

			// maprules
			elem2 = getFirstElementByTagNameNS(ns, "maprules", elem1);
			// For each languagemap
			list2 = elem2.getElementsByTagNameNS(ns, "languagemap");
			for ( int i=0; i<list2.getLength(); i++ ) {
				Element elem3 = (Element)list2.item(i);
				LanguageMap langMap = new LanguageMap();
				tmp = elem3.getAttribute("languagepattern");
				if ( tmp.length() > 0 ) langMap.pattern = tmp;
				tmp = elem3.getAttribute("languagerulename");
				if ( tmp.length() > 0 ) langMap.ruleName = tmp;
				langMaps.add(langMap);
			}
			modified = false;
		}
		catch ( SAXException e ) {
			throw new OkapiIOException(e);
		}
		catch ( ParserConfigurationException e ) {
			throw new OkapiIOException(e);
		}
		catch ( IOException e ) {
			throw new OkapiIOException(e);
		}
		catch ( XPathExpressionException e) {
			throw new OkapiIOException(e);
		}
	}
	
	/**
	 * Gets the first occurrence of a given element in a given namespace
	 * from a given element.
	 * @param ns The namespace URI to look for.
	 * @param tagName Name of the element to look for.
	 * @param elem Element where to look for.
	 * @return The first found element, or null.
	 */
	private Element getFirstElementByTagNameNS (String ns,
		String tagName,
		Element elem)
	{
		NodeList list = (NodeList)elem.getElementsByTagNameNS(ns, tagName);
		if (( list == null ) || ( list.getLength() < 1 )) return null;
		return (Element)list.item(0);
	}

	/**
	 * Saves the current rules to an SRX string.
	 * @param saveExtensions True to save Okapi SRX extensions, false to not save them.
	 * @param saveNonValidInfo True to save non-SRX-valid attributes, false to not save them
	 * @return The string containing the saved SRX rules.
	 */
	public String saveRulesToString (boolean saveExtensions,
		boolean saveNonValidInfo)
	{
		XMLWriter writer = new XMLWriter();
		writer.create();
		boolean current = modified;
		saveRules(writer, saveExtensions, saveNonValidInfo);
		modified = current; // Keep the same state for modified
		return writer.getStringOutput();
	}

	/**
	 * Saves the current rules to an SRX rules file.
	 * @param rulesPath The full path of the file where to save the rules.
	 * @param saveExtensions True to save Okapi SRX extensions, false to not save them.
	 * @param saveNonValidInfo True to save non-SRX-valid attributes, false to not save them
	 */
	public void saveRules (String rulesPath,
		boolean saveExtensions,
		boolean saveNonValidInfo)
	{
		XMLWriter writer = new XMLWriter();
		writer.create(rulesPath);
		saveRules(writer, saveExtensions, saveNonValidInfo);
	}
	
	private void saveRules (XMLWriter writer,
		boolean saveExtensions,
		boolean saveNonValidInfo)
	{
		try {
			writer.writeStartDocument();
			writer.writeStartElement("srx");
			writer.writeAttributeString("xmlns", NSURI_SRX20);
			if ( saveExtensions ) {
				writer.writeAttributeString("xmlns:"+NSPREFIX_OKPSRX, NSURI_OKPSRX);
			}
			writer.writeAttributeString("version", "2.0");
			version = "2.0";
			writer.writeLineBreak();
			
			writer.writeStartElement("header");
			writer.writeAttributeString("segmentsubflows", (segmentSubFlows ? "yes" : "no"));
			writer.writeAttributeString("cascade", (cascade ? "yes": "no"));
			writer.writeLineBreak();

			writer.writeStartElement("formathandle");
			writer.writeAttributeString("type", "start");
			writer.writeAttributeString("include", (includeStartCodes ? "yes" : "no"));
			writer.writeEndElementLineBreak(); // formathandle
			
			writer.writeStartElement("formathandle");
			writer.writeAttributeString("type", "end");
			writer.writeAttributeString("include", (includeEndCodes ? "yes" : "no"));
			writer.writeEndElementLineBreak(); // formathandle
			
			writer.writeStartElement("formathandle");
			writer.writeAttributeString("type", "isolated");
			writer.writeAttributeString("include", (includeIsolatedCodes ? "yes" : "no"));
			writer.writeEndElementLineBreak(); // formathandle
			
			if ( saveExtensions ) {
				writer.writeStartElement(NSPREFIX_OKPSRX+":options");
				writer.writeAttributeString("oneSegmentIncludesAll",
					(oneSegmentIncludesAll ? "yes" : "no"));
				writer.writeAttributeString("trimLeadingWhitespaces",
					(trimLeadingWS ? "yes" : "no"));
				writer.writeAttributeString("trimTrailingWhitespaces",
					(trimTrailingWS ? "yes" : "no"));
				writer.writeEndElementLineBreak(); // okpsrx:options

				writer.writeStartElement(NSPREFIX_OKPSRX+":sample");
				writer.writeAttributeString("language", getSampleLanguage());
				writer.writeAttributeString("useMappedRules", (testOnSelectedGroup() ? "no" : "yes"));
				writer.writeString(getSampleText());
				writer.writeEndElementLineBreak(); // okpsrx:sample
			
				writer.writeStartElement(NSPREFIX_OKPSRX+":rangeRule");
				writer.writeString(getMaskRule());
				writer.writeEndElementLineBreak(); // okpsrx:rangeRule
			}

			writer.writeEndElementLineBreak(); // header

			writer.writeStartElement("body");
			writer.writeLineBreak();
			
			writer.writeStartElement("languagerules");
			writer.writeLineBreak();
			for ( String ruleName : langRules.keySet() ) {
				writer.writeStartElement("languagerule");
				writer.writeAttributeString("languagerulename", ruleName);
				writer.writeLineBreak();
				ArrayList<Rule> langRule = langRules.get(ruleName);
				for ( Rule rule : langRule ) {
					writer.writeStartElement("rule");
					writer.writeAttributeString("break", (rule.isBreak ? "yes" : "no"));
					// Start of non-standard SRX 2.0 (non-SRX attributes not allowed)
					if ( saveExtensions && saveNonValidInfo ) {
						writer.writeAttributeString(NSPREFIX_OKPSRX+":active", (rule.isActive ? "yes" : "no"));
					}
					// End of non-Standard SRX
					writer.writeLineBreak();
					writer.writeElementString("beforebreak", rule.before);
					writer.writeLineBreak();					
					writer.writeElementString("afterbreak", rule.after);
					writer.writeLineBreak();					
					writer.writeEndElementLineBreak(); // rule
				}
				writer.writeEndElementLineBreak(); // languagerule
			}
			writer.writeEndElementLineBreak(); // languagerules
			
			writer.writeStartElement("maprules");
			writer.writeLineBreak();			
			for ( LanguageMap langMap : langMaps ) {
				writer.writeStartElement("languagemap");
				writer.writeAttributeString("languagepattern", langMap.pattern);
				writer.writeAttributeString("languagerulename", langMap.ruleName);
				writer.writeEndElementLineBreak(); // languagemap
			}
			writer.writeEndElementLineBreak(); // maprules
			
			writer.writeEndElementLineBreak(); // body
			
			writer.writeEndElementLineBreak(); // srx
			writer.writeEndDocument();
			modified = false;
		}
		finally {
			if ( writer != null ) writer.close();
		}
	}
}
