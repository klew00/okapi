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

package net.sf.okapi.filters.plaintext;

import java.net.URL;
import java.util.regex.Pattern;

import net.sf.okapi.common.*;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.filters.regex.Parameters;
import net.sf.okapi.filters.regex.RegexFilter;
import net.sf.okapi.filters.regex.Rule;

/**
 * <code>PlainTextFilter</code> extracts lines of input text, separated by line terminators.
 * The filter is aware of the following line terminators:
 * <ul><li>Carriage return character followed immediately by a newline character ("\r\n")
 * <li>Newline (line feed) character ("\n")
 * <li>Stand-alone carriage return character ("\r")
 * <li>Next line character ("\u0085")
 * <li>Line separator character ("\u2028")
 * <li>Paragraph separator character ("\u2029").</ul><p> 
 */

public class PlainTextFilter implements IFilter {

	public static final String FILTER_NAME = "okf_plaintext";
	public static final String FILTER_MIME_TYPE = MimeTypeMapper.PLAIN_TEXT_MIME_TYPE;
	public static final String DEF_RULE = "^(.*?)$";
	public static final int DEF_GROUP = 1;
	public static final int DEF_OPTIONS = Pattern.MULTILINE;
	
	
	private RegexFilter regex;				// Regex aggregate
	private ParametersPT params;			// Plain Text Filter's parameters

// PlainTextFilter	
	/**
	 * Configures an internal line extractor. 
	 * If you want to set a custom rule, call this method with a modified rule.<p> 
	 * @param rule - Java regex rule used to extract lines of text. Default: "^(.*?)$".
	 * @param sourceGroup - regex capturing group denoting text to be extracted. Default: 1.  
	 * @param regexOptions - Java regex options. Default: Pattern.MULTILINE.
	 */
	public void setRule(String rule, int sourceGroup, int regexOptions) {
		if (rule == null) return;
		if (rule == "") return;
		
		Rule regexRule = _getFirstRegexRule();
		if (regexRule == null) return;
		
		regexRule.setExpression(rule);
		regexRule.setSourceGroup(sourceGroup);
		
		Parameters rp = _getRegexParams();
		if (rp == null) return;
		
		rp.regexOptions = regexOptions;
		rp.compileRules();
		
		if (this.params == null) return;
		
		this.params.rule = rule;
		this.params.sourceGroup = sourceGroup;
		this.params.regexOptions = regexOptions;
	}
			
	/**
	 * Provides access to the internal line extractor's {@link Parameters} object. 
	 * @return {@link Parameters} object; with this object you can access the line extraction rule, source group, regex options, etc.
	 */
	public Parameters getRegexParameters() {
		return _getRegexParams();
	}

	public PlainTextFilter() {
		super();
		
		// Create the regex aggregate and its parameters 
		regex = new RegexFilter(); 
		
		Parameters regexParams = new Parameters();
		regex.setParameters(regexParams);
		
		// Load the default line extraction rule from a file to regexParams
		URL paramsUrl = PlainTextFilter.class.getResource("/def_line_extraction_rule.fprm");
		regexParams.load(paramsUrl.getPath(), false);
	}

// IFilter	

	public void cancel() {
		if (regex != null) regex.cancel();
	}

	public void close() {
		if (regex != null) regex.close();
	}

	public IFilterWriter createFilterWriter() {
		return (regex != null) ? regex.createFilterWriter() : null;
	}

	public ISkeletonWriter createSkeletonWriter() {
		return (regex != null) ? regex.createSkeletonWriter() : null;
	}

	public String getMimeType() {
		return FILTER_MIME_TYPE;
	}
	
	public String getName() {
		return FILTER_NAME;
	}

	public IParameters getParameters() {
		return params;
	}

	public boolean hasNext() {
		return (regex != null) ? regex.hasNext() : null;
	}

	public Event next() {
		// Change the mime type by regex filter ("text/x-regex") to "text/plain"
		
		Event event = regex.next();
		if (event == null) return event; // Returns null
		
		IResource res = event.getResource();
		if (res == null) return event; // Do not change event
		
		if (event.getEventType() == EventType.TEXT_UNIT) {		
			// Change mime type
			if (res instanceof TextUnit) ((TextUnit) res).setMimeType(this.getMimeType());
		}
				
		return event;
	}

	public void open(RawDocument input) {
		if (input == null) throw new OkapiBadFilterInputException("Input RawDocument is not defined.");
		else
		if (regex != null) regex.open(input);
	}

	public void open(RawDocument input, boolean generateSkeleton) {
		if (input == null) throw new OkapiBadFilterInputException("Input RawDocument is not defined.");
		else
		if (regex != null) regex.open(input, generateSkeleton);		
	}

	public void setParameters(IParameters params) {
		if (params instanceof ParametersPT) {			// Also checks for null
			this.params = (ParametersPT)params;
			
			if (this.params != null)
				setRule(this.params.rule, this.params.sourceGroup, this.params.regexOptions); // To compile rules
		}
	}

// Helpers 
	
	private Parameters _getRegexParams() {
		IParameters punk;
		
		if (regex == null) return null;
		punk = regex.getParameters();
		
		return (punk instanceof Parameters) ? (Parameters)punk : null; 
	}
	
	private Rule _getFirstRegexRule() {				
		Parameters regexParams = _getRegexParams();
		
		if (regexParams == null) return null;
		if (regexParams.rules == null) return null;
		if (regexParams.rules.isEmpty()) return null;
		
		return regexParams.rules.get(0);
	}
	
}	
	