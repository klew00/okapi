package net.sf.okapi.lib.segmentation;

import java.util.ArrayList;
import java.util.Hashtable;

import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.resource.IContainer;

public class Segmenter {
	
	private final String     NSURI_SRX20 = "http://www.lisa.org/srx20";
	
	private boolean     segmentSubFlows = true; // SRX default
	private boolean     cascade = false; // There is no SRX default for this
	private boolean     includeStartCodes = false; // SRX default
	private boolean     includeEndCodes = true; // SRX default
	private boolean     includeIsolatedCodes = false; // SRX default
	
	private ArrayList<LanguageMap>          langMaps;
	private Hashtable<String, LanguageRule> langRules;


	public Segmenter () {
		langMaps = new ArrayList<LanguageMap>();
		langRules = new Hashtable<String, LanguageRule>();
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
		LanguageRule langRule)
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
		//TODO: implement segment
		return 0;
	}
	
	/**
	 * Sets the language this segmenter works with. This method
	 * applies the language code you specify to the language mappings
	 * currently available in the segmenter and compile the rules
	 * when a language rule is found.
	 * @param languageCode The language code. the value must be a 
	 * BCP-47 value (e.g. "de", "fr-ca", etc.)
	 */
	public void selectLanguageRule (String languageCode) {
		//TODO: set language
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
			writer.writeAttributeString("type", "end");
			writer.writeAttributeString("include", (includeEndCodes ? "yes" : "no"));
			writer.writeEndElement(); // formathandle
			writer.writeAttributeString("type", "isolated");
			writer.writeAttributeString("include", (includeIsolatedCodes ? "yes" : "no"));
			writer.writeEndElement(); // formathandle
			writer.writeEndElement(); // header

			writer.writeStartElement("body");
	
			writer.writeStartElement("languageRules");
			for ( String ruleName : langRules.keySet() ) {
				writer.writeStartElement("languageRule");
				writer.writeAttributeString("languagerulename", ruleName);
				LanguageRule langRule = langRules.get(ruleName);
				for ( Rule rule : langRule.rules ) {
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
