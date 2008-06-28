package net.sf.okapi.lib.segmentation;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.resource.IContainer;

public class Segmenter {
	
	private final String     NSURI_SRX20 = "http://www.lisa.org/srx20";
	
	private boolean     segmentSubFlows = true; // SRX default
	private boolean     cascade = false; // There is no SRX default for this
	private boolean     includeStartCodes = false; // SRX default
	private boolean     includeEndCodes = true; // SRX default
	private boolean     includeIsolatedCodes = false; // SRX default
	private String      langCode = null;
	private String      inlineCodes;
	
	private ArrayList<LanguageMap>               langMaps;
	private Hashtable<String, ArrayList<Rule>>   langRules;
	private ArrayList<CompiledRule>              rules;
	private LinkedHashMap<Integer, Boolean>      splits;


	public Segmenter () {
		langMaps = new ArrayList<LanguageMap>();
		langRules = new Hashtable<String, ArrayList<Rule>>();
		// Pattern for handling inline codes 
		inlineCodes = String.format("((\\u%04x|\\u%04x|\\u%04x).)?",
			IContainer.CODE_OPENING, IContainer.CODE_CLOSING, IContainer.CODE_ISOLATED); 
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
		cascade = value;
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
		includeStartCodes = value;
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
		includeEndCodes = value;
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
		includeIsolatedCodes = value;
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
	}
	
	/**
	 * Adds a language map to the segmenter. The new map is added
	 * at the end of the one already there.
	 * @param langMap The language map object to add.
	 */
	public void addLanguageMap (LanguageMap langMap) {
		langMaps.add(langMap);
	}
	
	/**
	 * Segments a given IContainer object.
	 * @param original The object to segment.
	 * @return The number of segment found.
	 */
	public int segment (IContainer original) {
		if ( langCode == null ) {
			//TODO: throw an exception?
			return -1; // Need to call selectLanguageRule()
		}
		
		// Build the list of split positions
		String codedText = original.getCodedText();
		splits = new LinkedHashMap<Integer, Boolean>();
		Matcher m;
		for ( CompiledRule rule : rules ) {
			m = rule.pattern.matcher(codedText);
			while ( m.find() ) {
				int n = m.start()+m.group(1).length();
				if ( n > codedText.length() ) continue; // Match the end
				if ( splits.containsKey(n) ) {
					// Do not update if we found a no-break before
					if ( !splits.get(n) ) continue;
				}
				// Add or update split
				splits.put(n, rule.isBreak);
			}
		}

		// Count breaks and adjust positions
		int breakCount = 0;
		StringBuilder tmp = new StringBuilder();
		int start = 0;
		for ( int pos : splits.keySet() ) {
			if ( splits.get(pos) ) {
				breakCount++;
				tmp.append("["+codedText.substring(start, pos)+"]");
				start = pos;
			}
		}
		// Last one
		tmp.append("["+codedText.substring(start)+"]");
		System.out.println(tmp.toString());
		
		// Return the number of segment found
		return breakCount+1;
	}
	
	/**
	 * Sets the language this segmenter works with. This method
	 * applies the language code you specify to the language mappings
	 * currently available in the segmenter and compile the rules
	 * when one or more language map is found. The matching is done in
	 * the order of the list of language maps and more than one can be 
	 * selected if {@link #cascade()} is true.
	 * @param languageCode The language code. the value must be a 
	 * BCP-47 value (e.g. "de", "fr-ca", etc.)
	 * @return The number of language map that match the language code.
	 */
	public int selectLanguageRule (String languageCode) {
		int count = 0;
		resetRules();
		for ( LanguageMap langMap : langMaps ) {
			if ( Pattern.matches(langMap.pattern, languageCode) ) {
				compileRules(langMap.ruleName);
				count++;
				if ( !cascade() ) break; // Stop at the first matching map
			}
		}
		langCode = languageCode;
		return count;
	}

	/**
	 * Reset the set of active rules to nothing.
	 */
	private void resetRules () {
		langCode = null;
		rules = new ArrayList<CompiledRule>();
	}
	
	/**
	 * Compiles a language rule into the current set of active rules.
	 * @param ruleName The name of the language rule to compile.
	 */
	private void compileRules (String ruleName) {
		if ( !langRules.containsKey(ruleName) ) {
			throw new SegmentationRuleException("language rule '"+ruleName+"' not found.");
		}
		ArrayList<Rule> langRule = langRules.get(ruleName);
		for ( Rule rule : langRule ) {
			rules.add(
				new CompiledRule("("+rule.before+")("+inlineCodes+rule.after+")", rule.isBreak));
		}
	}
	
	/**
	 * Loads an SRX rules file.
	 * @param rulesPath The full path of the rules file to load.
	 */
	public void loadRules (String rulesPath) {
		//TODO: load SRX (possibly embedded in another  doc)
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

			writer.writeStartElement("header");
			writer.writeAttributeString("segmentsubflows", (segmentSubFlows ? "yes" : "no"));
			writer.writeAttributeString("cascade", (cascade ? "yes": "no"));

			writer.writeStartElement("formathandle");
			writer.writeAttributeString("type", "start");
			writer.writeAttributeString("include", (includeStartCodes ? "yes" : "no"));
			writer.writeEndElement(); // formathandle
			
			writer.writeStartElement("formathandle");
			writer.writeAttributeString("type", "end");
			writer.writeAttributeString("include", (includeEndCodes ? "yes" : "no"));
			writer.writeEndElement(); // formathandle
			
			writer.writeStartElement("formathandle");
			writer.writeAttributeString("type", "isolated");
			writer.writeAttributeString("include", (includeIsolatedCodes ? "yes" : "no"));
			writer.writeEndElement(); // formathandle
			
			writer.writeEndElement(); // header

			writer.writeStartElement("body");
	
			writer.writeStartElement("languageRules");
			for ( String ruleName : langRules.keySet() ) {
				writer.writeStartElement("languageRule");
				writer.writeAttributeString("languagerulename", ruleName);
				ArrayList<Rule> langRule = langRules.get(ruleName);
				for ( Rule rule : langRule ) {
					writer.writeStartElement("rule");
					writer.writeAttributeString("break", (rule.isBreak ? "yes" : "no"));
					writer.writeElementString("beforebreak", rule.before);
					writer.writeElementString("afterbreak", rule.after);
					writer.writeEndElement(); // rule
				}
				writer.writeEndElement(); // languagerule
			}
			writer.writeEndElement(); // languagerules
			
			writer.writeStartElement("maprules");
			for ( LanguageMap langMap : langMaps ) {
				writer.writeStartElement("languagemap");
				writer.writeAttributeString("languagepattern", langMap.pattern);
				writer.writeAttributeString("languagerulename", langMap.ruleName);
				writer.writeEndElement(); // languagemap
			}
			writer.writeEndElement(); // maprules
			
			writer.writeEndElement(); // body
			
			writer.writeEndElement(); // srx
			writer.writeEndDocument();
		}
		finally {
			if ( writer != null ) writer.close();
		}
	}
}
