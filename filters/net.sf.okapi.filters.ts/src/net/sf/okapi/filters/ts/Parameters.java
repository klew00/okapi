package net.sf.okapi.filters.ts;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.filters.InlineCodeFinder;

public class Parameters extends BaseParameters{

	public boolean escapeGT;
	public boolean decodeByteValues;

	
	public boolean useCodeFinder;
	public InlineCodeFinder codeFinder;
	
	public Parameters () {
		codeFinder = new InlineCodeFinder();
		reset();
		toString(); // fill the list
	}
	
	public void reset () {
		
		escapeGT = false;
		
		useCodeFinder = true;
		codeFinder.reset();
		codeFinder.setSample("%s, %d, {1}, \\n, \\r, \\t, etc.");
		codeFinder.setUseAllRulesWhenTesting(true);
		// Default in-line codes: special escaped-chars and printf-style variable
		codeFinder.addRule("%(([-0+#]?)[-0+#]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpn]");
		codeFinder.addRule("(\\\\r\\\\n)|\\\\a|\\\\b|\\\\f|\\\\n|\\\\r|\\\\t|\\\\v");
		//TODO: Add Java-style variables. this is too basic
		codeFinder.addRule("\\{\\d.*?\\}");
		
		decodeByteValues = false;
	}

	@Override
	public String toString () {
		buffer.reset();
		buffer.setBoolean("escapeGT", escapeGT);
		buffer.setBoolean("decodeByteValues", decodeByteValues);
		buffer.setBoolean("useCodeFinder", useCodeFinder);
		buffer.setGroup("codeFinderRules", codeFinder.toString());
		return buffer.toString();
	}
	
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		escapeGT = buffer.getBoolean("escapeGT", escapeGT);
		decodeByteValues = buffer.getBoolean("decodeByteValues", decodeByteValues);
		useCodeFinder = buffer.getBoolean("useCodeFinder", useCodeFinder);
		codeFinder.fromString(buffer.getGroup("codeFinderRules", ""));
	}
}
