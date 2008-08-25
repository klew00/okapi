/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel                                          */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.lib.segmentation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

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
import org.xml.sax.SAXException;

import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.resource.TextFragment;

public class SRXDocument {
	
	private final String     NSURI_SRX20 = "http://www.lisa.org/srx20";
	private final String     NSURI_OKPSRX = "http://okapi.sf.net/srx-extensions";
	
	static String INLINECODES_PATTERN = String.format("(([\\u%X\\u%X\\u%X].)*?)",
		TextFragment.MARKER_OPENING, TextFragment.MARKER_CLOSING, TextFragment.MARKER_ISOLATED);
			
	private boolean     cascade;
	private boolean     segmentSubFlows;
	private boolean     includeStartCodes;
	private boolean     includeEndCodes;
	private boolean     includeIsolatedCodes;
	
	private String      sampleText;
	private String      sampleLanguage;
	private boolean     modified;
	private boolean     sampleOnMappedRules; 
	
	private ArrayList<LanguageMap>                    langMaps;
	private LinkedHashMap<String, ArrayList<Rule>>    langRules;


	public SRXDocument () {
		resetAll();
	}

	public void resetAll () {
		langMaps = new ArrayList<LanguageMap>();
		langRules = new LinkedHashMap<String, ArrayList<Rule>>();
		modified = false;

		segmentSubFlows = true; // SRX default
		cascade = false; // There is no SRX default for this
		includeStartCodes = false; // SRX default
		includeEndCodes = true; // SRX default
		includeIsolatedCodes = false; // SRX default

		sampleText = "Mr. Holmes is from the <A>U.K.</A> <B>Is Dr. Watson from there too?</B> Yes: both are.<C/>";
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
	 * Indicates of (when sampling the rules) the sample should be
	 * computed from all the active rules.
	 * @return True to compute from all matching rules, false to use 
	 * a single given language rule set.
	 */
	public boolean sampleOnMappedRules () {
		return sampleOnMappedRules;
	}
	
	/**
	 * Sets the indicator on how to apply rules for samples.
	 * @param value True to compute from all the matching rules,
	 * false to use a single language rule set.
	 */
	public void setSampleOnMappedRules (boolean value) {
		if ( value != sampleOnMappedRules ) {
			sampleOnMappedRules = value;
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
	 * Adds a language rule to the segmenter. If another language rule
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
	 * Adds a language map to the segmenter. The new map is added
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
			includeEndCodes, includeIsolatedCodes);
		
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
			includeEndCodes, includeIsolatedCodes);
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
			segmenter.addRule(
				// The compiled rule is made of two groups: the pattern before and the pattern after
				// the break. A special pattern for in-line codes is also added transparently.
				new CompiledRule("("+rule.before+INLINECODES_PATTERN+")("+rule.after+")", rule.isBreak));
		}
	}
	
	/**
	 * Loads an SRX rules file.
	 * @param rulesPath The full path of the rules file to load.
	 */
	public void loadRules (String rulesPath) {
		try {
			DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
			Fact.setValidating(false);
			Document doc = Fact.newDocumentBuilder().parse(new File(rulesPath));
			resetAll();
			XPathFactory xpathFac = XPathFactory.newInstance();
			XPath xpath = xpathFac.newXPath();
//TODO: Handle namespaces (e.g. to allow nested SRX rules)
			XPathExpression xpe = xpath.compile("//srx");
			NodeList srxList = (NodeList)xpe.evaluate(doc, XPathConstants.NODESET);
			if ( srxList.getLength() < 1 ) return;
			
			// Treat the first occurrence (we assume there is never more in one file)
			Element srxElem = (Element)srxList.item(0);
			Element elem1 = getFirstElementByTagName("header", srxElem);
			String tmp = elem1.getAttribute("segmentsubflows");
			if ( tmp.length() > 0 ) segmentSubFlows = "yes".equals(tmp);
			tmp = elem1.getAttribute("cascade");
			if ( tmp.length() > 0 ) cascade = "yes".equals(tmp);
			// formathandle elements
			NodeList list2 = elem1.getElementsByTagName("formathandle");
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
			
			// Extensions
			//TODO: Handle namespace to read sample text
			Element elem2 = getFirstElementByTagName("okpsrx:sample", elem1);
			if ( elem2 != null ) {
				setSampleText(elem2.getTextContent());
			}
			
			// Get the body element
			elem1 = getFirstElementByTagName("body", srxElem);
			
			// languagerules
			elem2 = getFirstElementByTagName("languagerules", elem1);
			// For each languageRule
			list2 = elem2.getElementsByTagName("languagerule");
			for ( int i=0; i<list2.getLength(); i++ ) {
				Element elem3 = (Element)list2.item(i);
				ArrayList<Rule> tmpList = new ArrayList<Rule>();
				String ruleName = elem3.getAttribute("languagerulename");
				// For each rule
				NodeList list3 = elem3.getElementsByTagName("rule");
				for ( int j=0; j<list3.getLength(); j++ ) {
					Element elem4 = (Element)list3.item(j);
					Rule newRule = new Rule();
					tmp = elem4.getAttribute("break");
					if ( tmp.length() > 0 ) newRule.isBreak = "yes".equals(tmp);
					Element elem5 = getFirstElementByTagName("beforebreak", elem4);
					if ( elem5 != null ) newRule.before = elem5.getTextContent();
					elem5 = getFirstElementByTagName("afterbreak", elem4);
					if ( elem5 != null ) newRule.after = elem5.getTextContent();
					tmpList.add(newRule);
				}
				langRules.put(ruleName, tmpList);
			}

			// maprules
			elem2 = getFirstElementByTagName("maprules", elem1);
			// For each languagemap
			list2 = elem2.getElementsByTagName("languagemap");
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
			throw new RuntimeException(e);
		}
		catch ( ParserConfigurationException e ) {
			throw new RuntimeException(e);
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		catch ( XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Gets the first occurrence of a given element from a given
	 * element.
	 * @param tagName Name of the element to look for.
	 * @param elem Element where to look for.
	 * @return The first found element, or null.
	 */
	private Element getFirstElementByTagName (String tagName,
		Element elem)
	{
		NodeList list = (NodeList)elem.getElementsByTagName(tagName);
		if (( list == null ) || ( list.getLength() < 1 )) return null;
		return (Element)list.item(0);
	}
	
	/**
	 * Saves the current rules to an SRX rules file.
	 * @param rulesPath The full path of the file where to save the rules.
	 */
	public void saveRules (String rulesPath) {
		XMLWriter writer = null;
		try {
			writer = new XMLWriter();
			writer.create(rulesPath);
			writer.writeStartDocument();

			writer.writeStartElement("srx");
			writer.writeAttributeString("xmlns", NSURI_SRX20);
			writer.writeAttributeString("version", "2.0");
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
			
			writer.writeStartElement("okpsrx:sample");
			writer.writeAttributeString("xmlns:okpsrx", NSURI_OKPSRX);
			writer.writeString(getSampleText());
			writer.writeEndElementLineBreak(); // okpsrx:sample
			
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
