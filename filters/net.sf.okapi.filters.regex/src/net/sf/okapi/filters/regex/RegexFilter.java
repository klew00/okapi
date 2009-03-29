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

package net.sf.okapi.filters.regex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.LinkedList;
import java.util.Stack;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.BOMNewlineEncodingDetector;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filterwriter.GenericFilterWriter;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

public class RegexFilter implements IFilter {

	private boolean canceled;
	private Parameters params;
	private String encoding;
	private String inputText;
	private Stack<StartGroup> groupStack;
	private int tuId;
	private int otherId;
	private String docName;
	private TextUnit tuRes;
	private LinkedList<Event> queue;
	private int startSearch;
	private int startSkl;
	private int parseState = 0;
	private String srcLang;
	private String trgLang;
	private String lineBreak;
	private boolean hasUTF8BOM;
	
	public RegexFilter () {
		params = new Parameters();
	}

	public void cancel () {
		canceled = true;
	}

	public void close () {
		inputText = null;
		parseState = 0;
	}

	public String getName () {
		return "okf_regex";
	}
	
	public String getMimeType () {
		return "text/x-regex";
	}

	public IParameters getParameters () {
		return params;
	}

	public boolean hasNext () {
		return (parseState > 0);
	}

	// Rev = 1241
/*	public Event nextOriginal () {
		// Cancel if requested
		if ( canceled ) {
			parseState = 0;
			queue.clear();
			queue.add(new Event(EventType.CANCELED));
		}
		
		// Process queue if it's not empty yet
		if ( queue.size() > 0 ) {
			return nextEvent();
		}

		// Get the first best match among the rules
		// trying to match ((start)(.*?)(end))
		Rule bestRule = null;
		int bestPosition = inputText.length()+99;
		MatchResult startResult = null;
		MatchResult endResult = null;
		int i = 0;
		
		while ( true ) {
			for ( Rule rule : params.rules ) {
				Matcher m = rule.pattern.matcher(inputText);
				if ( m.find(startSearch) ) {
					if ( m.start() < bestPosition ) {
						bestPosition = m.start();
						bestRule = rule;
					}
				}
				i++;
			}
			
			if ( bestRule != null ) {
				// Find the start pattern
				Pattern p = Pattern.compile(bestRule.start, params.regexOptions);
				Matcher m = p.matcher(inputText);
				if ( m.find(bestPosition) ) {
					startResult = m.toMatchResult();
				}
				else throw new RuntimeException("Inconsistant rule finding.");
				// Find the end pattern
				p = Pattern.compile(bestRule.end, params.regexOptions);
				m = p.matcher(inputText);
				if ( m.find(startResult.end()) ) {
					endResult = m.toMatchResult();
				}
				else throw new RuntimeException("Inconsistant rule finding.");
				// Check for empty content
				if ( startResult.start() == endResult.start() ) {
					if ( startSearch+1 < inputText.length() ) {
						startSearch++;
						continue;
					}
					else break; // Done
				}
				// Check for boundary to avoid infinite loop
				else if ( startResult.start() != inputText.length() ) {
					// Process the match we just found
					return processMatch(bestRule, startResult, endResult);
				}
				else break; // Done
			}
			else break; // Done
		}
		
		// Else: Send end of the skeleton if needed
		if ( startSearch <= inputText.length() ) {
			// Treat strings outside rules
//TODO: implement extract string out of rules
			// Send the skeleton
			addSkeletonToQueue(inputText.substring(startSkl, inputText.length()), true);
		}

		// End finally set the end
		// Set the ending call
		Ending ending = new Ending(String.format("%d", ++otherId));
		queue.add(new Event(EventType.END_DOCUMENT, ending));
		return nextEvent();
	}
*/
	public Event next () {
		// Cancel if requested
		if ( canceled ) {
			parseState = 0;
			queue.clear();
			queue.add(new Event(EventType.CANCELED));
		}
		
		// Process queue if it's not empty yet
		if ( queue.size() > 0 ) {
			return nextEvent();
		}

		// Get the first best match among the rules
		// trying to match expression
		Rule bestRule = null;
		int bestPosition = inputText.length()+99;
		MatchResult result = null;
		int i = 0;
		
		while ( true ) {
			for ( Rule rule : params.rules ) {
				Matcher m = rule.pattern.matcher(inputText);
				if ( m.find(startSearch) ) {
					if ( m.start() < bestPosition ) {
						bestPosition = m.start();
						bestRule = rule;
					}
				}
				i++;
			}
			
			if ( bestRule != null ) {
				// Get the matching result
				Matcher m = bestRule.pattern.matcher(inputText);
				if ( m.find(bestPosition) ) {
					result = m.toMatchResult();
				}
				// Check for empty content
				if ( result.start() == result.end() ) {
					if ( startSearch+1 < inputText.length() ) {
						startSearch++;
						continue;
					}
					else break; // Done
				}
				// Check for boundary to avoid infinite loop
				else if ( result.start() != inputText.length() ) {
					// Process the match we just found
					return processMatch(bestRule, result);
				}
				else break; // Done
			}
			else break; // Done
		}
		
		// Else: Send end of the skeleton if needed
		if ( startSearch <= inputText.length() ) {
			// Treat strings outside rules
//TODO: implement extract string out of rules
			// Send the skeleton
			addSkeletonToQueue(inputText.substring(startSkl, inputText.length()), true);
		}

		// Any group to close automatically?
		closeGroups();
		
		// End finally set the end
		// Set the ending call
		Ending ending = new Ending(String.format("%d", ++otherId));
		queue.add(new Event(EventType.END_DOCUMENT, ending));
		return nextEvent();
	}
	
	private void closeGroups () {
		if ( groupStack.size() > 0 ) {
			Ending ending = new Ending(String.format("%d", ++otherId));
			queue.add(new Event(EventType.END_GROUP, ending));
			groupStack.pop();
		}
	}
	
	public void open (InputStream input) {
		BufferedReader reader = null;
		try {
			// Open the input reader from the provided stream
			BOMAwareInputStream bis = new BOMAwareInputStream(input, encoding);
			encoding = bis.detectEncoding();
			hasUTF8BOM = bis.hasUTF8BOM();
			reader = new BufferedReader(new InputStreamReader(bis, encoding));

			//TODO: Optimize this with a better 'readToEnd()'
			//TODO: Fix issue that this code cannot read a lone \n at the end.
			StringBuilder tmp = new StringBuilder();
			char[] buf = new char[2048];
			int count = 0;
			while (( count = reader.read(buf)) != -1 ) {
				tmp.append(buf, 0, count);
			}
			reader.readLine();
			
			// Detect line break type
			lineBreak = BOMNewlineEncodingDetector.getNewlineType(tmp).toString();
			// Common open (and normalize line-breaks
			commonOpen(tmp.toString().replace(lineBreak, "\n"));
		}
		catch ( UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		catch ( IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			if ( reader != null ) {
				try {
					reader.close();
				}
				catch ( IOException e ) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	public void open (URI inputURI) {
		try {
			docName = inputURI.getPath();
			open(inputURI.toURL().openStream());
		}
		catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	public void open (CharSequence inputText) {
		encoding = "UTF-16";
		hasUTF8BOM = false;
		commonOpen(inputText.toString());
	}

	public void setOptions (String sourceLanguage,
		String targetLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		encoding = defaultEncoding;
		srcLang = sourceLanguage;
		trgLang = targetLanguage;
	}

	public void setOptions (String sourceLanguage,
		String defaultEncoding,
		boolean generateSkeleton)
	{
		setOptions(sourceLanguage, null, defaultEncoding, generateSkeleton);
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	public ISkeletonWriter createSkeletonWriter() {
		return new GenericSkeletonWriter();
	}

	public IFilterWriter createFilterWriter () {
		return new GenericFilterWriter(createSkeletonWriter());
	}

	private void commonOpen (String text) {
		close(); // Just in case resources need to be freed
		
		// Set the input string
		inputText = text;

		parseState = 1;
		canceled = false;
		groupStack = new Stack<StartGroup>();
		startSearch = 0;
		startSkl = 0;
		tuId = 0;
		otherId = 0;

		// Prepare the filter rules
		params.compileRules();

		// Set the start event
		queue = new LinkedList<Event>();
		StartDocument startDoc = new StartDocument(String.valueOf(++otherId));
		startDoc.setName(docName);
		startDoc.setEncoding(encoding, hasUTF8BOM);
		startDoc.setLanguage(srcLang);
		startDoc.setLineBreak(lineBreak);
		startDoc.setFilterParameters(getParameters());
		startDoc.setType(params.mimeType);
		startDoc.setMimeType(params.mimeType);
		startDoc.setMultilingual(hasRulesWithTarget());
		queue.add(new Event(EventType.START_DOCUMENT, startDoc));
	}
	
	/*
	private Event processMatch (Rule rule,
		MatchResult startResult,
		MatchResult endResult)
	{
		GenericSkeleton skel;
		switch ( rule.ruleType ) {
		case Rule.RULETYPE_NOTRANS:
		case Rule.RULETYPE_COMMENT:
			// Skeleton data include the content
			skel = new GenericSkeleton(inputText.substring(startSkl, endResult.start()));
			// Update starts for next read
			startSearch = endResult.end();
			startSkl = endResult.start();
			// If comment: process the comment for directives
			if ( rule.ruleType == Rule.RULETYPE_COMMENT ) {
				params.locDir.process(skel.toString());
			}
			// Then just return one skeleton event
			return new Event(EventType.DOCUMENT_PART,
				new DocumentPart(String.format("%d", ++otherId), false, skel));
			
		case Rule.RULETYPE_OPENGROUP:
		case Rule.RULETYPE_CLOSEGROUP:
			// Skeleton data include the content
			skel = new GenericSkeleton(inputText.substring(startSkl, endResult.start()));
			// Update starts for next read
			startSearch = endResult.end();
			startSkl = endResult.start();
			//TODO: return group event, and deal with skeleton
			if ( rule.ruleType == Rule.RULETYPE_OPENGROUP ) {
				StartGroup startGroup = new StartGroup(null);
				startGroup.setId(String.valueOf(++otherId));
				startGroup.setSkeleton(skel);
				if ( rule.nameStart.length() > 0 ) {
					String name = getMatch(startResult.group(), rule.nameStart, rule.nameEnd);
					if ( name != null ) { // Process the name format if needed
						if ( rule.nameFormat.length() > 0 ) {
							String tmp = rule.nameFormat.replace("<parentName>",
								(groupStack.size()>0 ? groupStack.peek().getName() : "" ));
							startGroup.setName(tmp.replace("<self>", name));
						}
						else startGroup.setName(name);
					}
				}
				groupStack.push(startGroup);
				return new Event(EventType.START_GROUP, startGroup);
			}
			else { // Close group
				groupStack.pop();
				Ending ending = new Ending(String.valueOf(++otherId));  
				ending.setSkeleton(skel);
				return new Event(EventType.END_GROUP, ending);
				
			}
		}
		
		//--- Otherwise: process the content

		// Set skeleton data if needed
		if ( startResult.end() > startSkl ) {
			addSkeletonToQueue(inputText.substring(startSkl, startResult.end()), false);
		}
		
		// Set start positions for next read
		startSearch = endResult.end();
		startSkl = endResult.start();

		// Check localization directives
		if ( !params.locDir.isLocalizable(true) ) {
			// If not to be localized: make it a skeleton unit
			addSkeletonToQueue(inputText.substring(startResult.end(),
				endResult.start()), false);
			// And return
			return nextEvent();
		}

		//--- Else: We extract

		// Any resname we can use?
		String name = getMatch(startResult.group(), rule.nameStart,
			rule.nameEnd);

		// Process the data, this will create a queue of events if needed
		if ( rule.ruleType == Rule.RULETYPE_CONTENT ) {
			processContent(rule, name, inputText.substring(
				startResult.end(), endResult.start()));
		}
		else if ( rule.ruleType == Rule.RULETYPE_STRING ) {
			processStrings(rule, name, inputText.substring(
				startResult.end(), endResult.start()));
		}
		return nextEvent();
	}
*/
	
	private Event processMatch (Rule rule,
		MatchResult result)
	{
		GenericSkeleton skel;
		switch ( rule.ruleType ) {
		case Rule.RULETYPE_NOTRANS:
		case Rule.RULETYPE_COMMENT:
			// Skeleton data is the whole expression's match
			//TODO: line-breaks conversion!!
			skel = new GenericSkeleton(
				inputText.substring(startSkl, result.end()).replace("\n", lineBreak));
			// Update starts for next read
			startSearch = result.end();
			startSkl = result.end();
			// If comment: process the source content for directives
			if ( rule.ruleType == Rule.RULETYPE_COMMENT ) {
				params.locDir.process(result.group(rule.sourceGroup));
			}
			// Then just return one skeleton event
			return new Event(EventType.DOCUMENT_PART,
				new DocumentPart(String.format("%d", ++otherId), false, skel));
			
		case Rule.RULETYPE_OPENGROUP:
		case Rule.RULETYPE_CLOSEGROUP:
			// Skeleton data include the content
			skel = new GenericSkeleton(
				inputText.substring(startSkl, result.end()).replace("\n", lineBreak));
			// Update starts for next read
			startSearch = result.end();
			startSkl = result.end();
			if ( rule.ruleType == Rule.RULETYPE_OPENGROUP ) {
				// See if we need to auto-close the groups
				if ( params.oneLevelGroups && (groupStack.size() > 0 )) {
					closeGroups();
				}
				// Start the new one
				StartGroup startGroup = new StartGroup(null);
				startGroup.setId(String.valueOf(++otherId));
				startGroup.setSkeleton(skel);
				if ( rule.nameGroup != -1 ) {
					String name = result.group(rule.nameGroup);
					if ( name.length() > 0 ) {
						startGroup.setName(name);
					}
				}
				groupStack.push(startGroup);
				queue.add(new Event(EventType.START_GROUP, startGroup));
				return queue.poll();
			}
			else { // Close group
				if ( groupStack.size() == 0 ) {
					throw new RuntimeException("Rule for closing a group detected, but no group is open.");
				}
				groupStack.pop();
				Ending ending = new Ending(String.valueOf(++otherId));  
				ending.setSkeleton(skel);
				return new Event(EventType.END_GROUP, ending);
				
			}
		}
		
		//--- Otherwise: process the content

		// Set skeleton data if needed
		if ( result.start() > startSkl ) {
			addSkeletonToQueue(inputText.substring(startSkl, result.start()), false);
		}
		startSkl = result.start();
		
		startSearch = result.end(); // For the next read
		
		// Check localization directives
		if ( !params.locDir.isLocalizable(true) ) {
			// If not to be localized: make it a skeleton unit
			addSkeletonToQueue(inputText.substring(startSkl, result.end()), false);
			startSkl = result.end(); // For the next read
			// And return
			return nextEvent();
		}

		//--- Else: We extract


		// Process the data, this will create a queue of events if needed
		if ( rule.ruleType == Rule.RULETYPE_CONTENT ) {
			processContent(rule, result);
		}
		else if ( rule.ruleType == Rule.RULETYPE_STRING ) {
			// Skeleton before
			if ( result.start(rule.sourceGroup) > result.start() ) {
				addSkeletonToQueue(inputText.substring(startSkl, result.start(rule.sourceGroup)), false);
			}
			// Extract the string(s)
			processStrings(rule, result);
			// Skeleton after
			if ( result.end(rule.sourceGroup) < result.end() ) {
				addSkeletonToQueue(inputText.substring(result.end(rule.sourceGroup), result.end()), false);
			}
			startSkl = result.end(); // For the next read
		}
		return nextEvent();
	}

	/*
	private String getMatch (String text,
		String start,
		String end)
	{
		if (( start == null ) || ( start.length() == 0 )) return null;
		if (( end == null ) || ( end.length() == 0 )) return null;
		
		Pattern p = Pattern.compile(start, params.regexOptions);
		Matcher m1 = p.matcher(text);
		if ( m1.find() ) {
			p = Pattern.compile(end, params.regexOptions);
			Matcher m2 = p.matcher(text);
			if ( m2.find(m1.end()) ) {
				return text.substring(m1.end(), m2.start());
			}
		}
		return null;
	}
	*/
	/*
	private void processContent (Rule rule,
		String name,
		String data)
	{
		tuRes = new TextUnit(String.valueOf(++tuId), data);
		//TODO: handle un-escaping and mime-type
		
		if ( rule.preserveWS ) {
			tuRes.setPreserveWhitespaces(true);
		}
		else { // Unwrap the content
			TextFragment.unwrap(tuRes.getSourceContent());
		}

		if ( rule.useCodeFinder ) {
			rule.codeFinder.process(tuRes.getSourceContent());
		}

		if ( name != null ) {
			if ( rule.nameFormat.length() > 0 ) {
				String tmp = rule.nameFormat.replace("<parentName>",
					(groupStack.size()>0 ? groupStack.peek().getName() : "" ));
				tuRes.setName(tmp.replace("<self>", name));
			}
			else tuRes.setName(name);
		}

		queue.add(new Event(EventType.TEXT_UNIT, tuRes));
	}
	*/

	private void processContent (Rule rule,
		MatchResult result)
	{
		// Create the new text unit and its skeleton
		//TODO: handle un-escaping and mime-type
		tuRes = new TextUnit(String.valueOf(++tuId), result.group(rule.sourceGroup));
		GenericSkeleton skel = new GenericSkeleton();
		tuRes.setSkeleton(skel);
		boolean hasTarget = (rule.targetGroup != -1);
		
		if ( hasTarget ) {
			// Add the target data
			tuRes.setTargetContent(trgLang, new TextFragment(result.group(rule.targetGroup)));
			// Case of source before target
			if ( result.start(rule.targetGroup) > result.start(rule.sourceGroup) ) {
				// Before the source
				if ( result.start(rule.sourceGroup) > startSkl ) {
					skel.append(inputText.substring(
						startSkl, result.start(rule.sourceGroup)).replace("\n", lineBreak));
				}
				// The source
				skel.addContentPlaceholder(tuRes);
				// Between the source and the target
				skel.append(inputText.substring(
					result.end(rule.sourceGroup), result.start(rule.targetGroup)).replace("\n", lineBreak));
				// The target
				skel.addContentPlaceholder(tuRes, trgLang);
				// After the target
				if ( result.end(rule.targetGroup) < result.end() ) {
					skel.append(inputText.substring(
						result.end(rule.targetGroup), result.end()).replace("\n", lineBreak));
				}
			}
			else { // Case of target before the source
				// Before the target
				if ( result.start(rule.targetGroup) > startSkl ) {
					skel.append(inputText.substring(
						startSkl, result.start(rule.targetGroup)).replace("\n", lineBreak));
				}
				// The target
				skel.addContentPlaceholder(tuRes, trgLang);
				// Between the target and the source
				skel.append(inputText.substring(
					result.end(rule.targetGroup), result.start(rule.sourceGroup)).replace("\n", lineBreak));
				// The source
				skel.addContentPlaceholder(tuRes);
				// After the source
				if ( result.end(rule.sourceGroup) < result.end() ) {
					skel.append(inputText.substring(
						result.end(rule.sourceGroup), result.end()).replace("\n", lineBreak));
				}
			}
		}
		else { // No target
			if ( result.start(rule.sourceGroup) > startSkl ) {
				skel.append(inputText.substring(
					startSkl, result.start(rule.sourceGroup)).replace("\n", lineBreak));
			}
			skel.addContentPlaceholder(tuRes);
			if ( result.end(rule.sourceGroup) < result.end() ) {
				skel.append(inputText.substring(
					result.end(rule.sourceGroup), result.end()).replace("\n", lineBreak));
			}
		}

		// Move the skeleton start for next read
		startSkl = result.end();
		
		if ( rule.preserveWS ) {
			tuRes.setPreserveWhitespaces(true);
		}
		else { // Unwrap the content
			TextFragment.unwrap(tuRes.getSourceContent());
			if ( hasTarget ) TextFragment.unwrap(tuRes.getTargetContent(trgLang));
		}

		if ( rule.useCodeFinder ) {
			rule.codeFinder.process(tuRes.getSourceContent());
			if ( hasTarget ) rule.codeFinder.process(tuRes.getTargetContent(trgLang));
		}

		if ( rule.nameGroup != -1 ) {
			String name = result.group(rule.nameGroup);
			if ( name.length() > 0 ) {
				tuRes.setName(name);
			}
		}
		
		if ( rule.noteGroup != -1 ) {
			String note = result.group(rule.noteGroup);
			if ( note.length() > 0 ) {
				tuRes.setProperty(new Property(Property.NOTE, note, true));
			}
		}

		queue.add(new Event(EventType.TEXT_UNIT, tuRes));
	}
	
	/*
	private void processStrings (Rule rule,
		String name,
		String data)
	{
		int i = -1;
		int startSearch = 0;
		String mark = params.startString;
		
		while ( true  ) {
			int j = 0;
			int start = startSearch;
			int end = -1;
			int state = 0;

			// Search string one by one
			while ( end == -1 ) {
				if ( ++i >= data.length() ) break;
				
				// Deal with \\, \" and \' escapes
				if ( state > 0 ) {
					if ( params.useBSlashEscape ) {
						while ( data.codePointAt(i) == '\\' ) {
							if ( i+2 < data.length() ) i += 2; // Now point to next
							else throw new RuntimeException("Escape syntax error in ["+data+"]");
						}
					}
				}
			
				// Check characters
				switch ( state ) {
				case 0:
					if ( data.codePointAt(i) == mark.codePointAt(j) ) {
						if ( ++j == mark.length() ) {
							// Start of string match found, set search info for end
							start = i+1; // Start of the string content
							state = 2;
							mark = params.endString;
							j = 0;
						}
						else state = 1;
					}
					break;
				case 1: // Look if we can finish a start match
					if ( data.codePointAt(i) == mark.codePointAt(j) ) {
						if ( ++j == mark.length() ) {
							// Start of string match found, set search info for end
							start = i+1; // Start of the string content
							state = 2;
							mark = params.endString;
							j = 0;
						}
						// Else: keep moving
					}
					else { // Was not a match
						state = 0;
						i -= (j-1); // Go back just after the trigger
						j = 0; // And reset the mark index
					}
					break;
				case 2: // Look for an end mark
					if ( data.codePointAt(i) == mark.codePointAt(j) ) {
						if ( ++j == mark.length() ) {
							// End of string match found
							// Set the end of the string position (will stop the loop too)
							end = i-j+1;
							// Check for empty strings
							if ( end == start ) {
								end = -1;
								state = 0;
								j = 0;
							}
						}
						else state = 3;
					}
					break;
				case 3: // Look if we can finish an end match
					if ( data.codePointAt(i) == mark.codePointAt(j) ) {
						if ( ++j == mark.length() ) {
							// End of string match found
							// Set the end of the string position (will stop the loop too)
							end = i-j+1;
							// Check for empty strings
							if ( end == start ) {
								end = -1;
								state = 0;
								j = 0;
							}
						}
						// Else: Keep moving
					}
					else { // Was not a match
						state = 2;
						i -= (j-1); // Go back just after the trigger
						j = 0; // And reset the mark index
					}
					break;
				}
			} // End of while end == -1
			
			// If we have found a string: process it
			if ( end != -1 ) {
				// Skeleton part before
				if ( start > startSearch ) {
					addSkeletonToQueue(data.substring(startSearch, start), false);
				}

				// Item to extract
				tuRes = new TextUnit(String.valueOf(++tuId),
					data.substring(start, end));
				tuRes.setMimeType("text/x-regex"); //TODO: work-out something for escapes in regex
				if ( rule.preserveWS ) {
					tuRes.setPreserveWhitespaces(true);
				}
				else { // Unwrap the string
					TextFragment.unwrap(tuRes.getSourceContent());
				}
				
				if ( rule.useCodeFinder ) {
					rule.codeFinder.process(tuRes.getSourceContent());
				}

				if ( name != null ) {
					if ( rule.nameFormat.length() > 0 ) {
						String tmp = rule.nameFormat.replace("<parentName>",
							(groupStack.size()>0 ? groupStack.peek().getName() : "" ));
						tuRes.setName(tmp.replace("<self>", name));
					}
					else tuRes.setName(name);
				}
				queue.add(new Event(EventType.TEXT_UNIT, tuRes));
				// Reset the pointers: next skeleton will start from startSearch, end reset to -1
				startSearch = end;
			}

			// Make sure we get out of the loop if needed
			if ( i >= data.length() ) break;
			
		} // End of while true

		// Skeleton part after the last string
		if ( startSearch < data.length() ) {
			addSkeletonToQueue(data.substring(startSearch), false);
		}
	}
	*/
	
	private void processStrings (Rule rule,
		MatchResult result)
	{
		int i = -1;
		int startSearch = 0;
		int count = 0;
		String mark = params.startString;
		String data = result.group(rule.sourceGroup);
		
		while ( true  ) {
			int j = 0;
			int start = startSearch;
			int end = -1;
			int state = 0;

			// Search string one by one
			while ( end == -1 ) {
				if ( ++i >= data.length() ) break;
				
				// Deal with \\, \" and \' escapes
				if ( state > 0 ) {
					if ( params.useBSlashEscape ) {
						while ( data.codePointAt(i) == '\\' ) {
							if ( i+2 < data.length() ) i += 2; // Now point to next
							else throw new RuntimeException("Escape syntax error in ["+data+"]");
						}
					}
				}
			
				// Check characters
				switch ( state ) {
				case 0:
					if ( data.codePointAt(i) == mark.codePointAt(j) ) {
						if ( ++j == mark.length() ) {
							// Start of string match found, set search info for end
							start = i+1; // Start of the string content
							state = 2;
							mark = params.endString;
							j = 0;
						}
						else state = 1;
					}
					break;
				case 1: // Look if we can finish a start match
					if ( data.codePointAt(i) == mark.codePointAt(j) ) {
						if ( ++j == mark.length() ) {
							// Start of string match found, set search info for end
							start = i+1; // Start of the string content
							state = 2;
							mark = params.endString;
							j = 0;
						}
						// Else: keep moving
					}
					else { // Was not a match
						state = 0;
						i -= (j-1); // Go back just after the trigger
						j = 0; // And reset the mark index
					}
					break;
				case 2: // Look for an end mark
					if ( data.codePointAt(i) == mark.codePointAt(j) ) {
						if ( ++j == mark.length() ) {
							// End of string match found
							// Set the end of the string position (will stop the loop too)
							end = i-j+1;
							// Check for empty strings
							if ( end == start ) {
								end = -1;
								state = 0;
								j = 0;
							}
						}
						else state = 3;
					}
					break;
				case 3: // Look if we can finish an end match
					if ( data.codePointAt(i) == mark.codePointAt(j) ) {
						if ( ++j == mark.length() ) {
							// End of string match found
							// Set the end of the string position (will stop the loop too)
							end = i-j+1;
							// Check for empty strings
							if ( end == start ) {
								end = -1;
								state = 0;
								j = 0;
							}
						}
						// Else: Keep moving
					}
					else { // Was not a match
						state = 2;
						i -= (j-1); // Go back just after the trigger
						j = 0; // And reset the mark index
					}
					break;
				}
			} // End of while end == -1
			
			// If we have found a string: process it
			if ( end != -1 ) {
				count++;
				// Skeleton part before
				if ( start > startSearch ) {
					addSkeletonToQueue(data.substring(startSearch, start), false);
				}

				// Item to extract
				tuRes = new TextUnit(String.valueOf(++tuId),
					data.substring(start, end));
				tuRes.setMimeType("text/x-regex"); //TODO: work-out something for escapes in regex
				if ( rule.preserveWS ) {
					tuRes.setPreserveWhitespaces(true);
				}
				else { // Unwrap the string
					TextFragment.unwrap(tuRes.getSourceContent());
				}
				
				if ( rule.useCodeFinder ) {
					rule.codeFinder.process(tuRes.getSourceContent());
				}

				if ( rule.nameGroup != -1 ) {
					String name = result.group(rule.nameGroup);
					if ( name.length() > 0 ) {
						if ( count > 1 ) { // Add a number after the first string
							tuRes.setName(String.format("%s%d", name, count));
						}
						else {
							tuRes.setName(name);
						}
					}
				}
				
				if ( rule.noteGroup != -1 ) {
					String note = result.group(rule.noteGroup);
					if ( note.length() > 0 ) {
						tuRes.setProperty(new Property(Property.NOTE, note, true));
					}
				}

				queue.add(new Event(EventType.TEXT_UNIT, tuRes));
				// Reset the pointers: next skeleton will start from startSearch, end reset to -1
				startSearch = end;
			}

			// Make sure we get out of the loop if needed
			if ( i >= data.length() ) break;
			
		} // End of while true

		// Skeleton part after the last string
		if ( startSearch < data.length() ) {
			addSkeletonToQueue(data.substring(startSearch), false);
		}
	}
	
	private void addSkeletonToQueue (String data,
		boolean forceNewEntry)
	{
		GenericSkeleton skel;
		if ( !forceNewEntry && ( queue.size() > 0 )) {
			if ( queue.getLast().getResource() instanceof DocumentPart ) {
				// Append to the last queue entry if possible
				skel = (GenericSkeleton)queue.getLast().getResource().getSkeleton();
				skel.append(data.replace("\n", lineBreak));
				return;
			}
		}
		// Else: create a new skeleton entry
		skel = new GenericSkeleton(data.replace("\n", lineBreak));
		queue.add(new Event(EventType.DOCUMENT_PART,
			new DocumentPart(String.valueOf(++otherId), false, skel)));
	}

	private Event nextEvent () {
		if ( queue.size() == 0 ) return null;
		if ( queue.peek().getEventType() == EventType.END_DOCUMENT ) {
			parseState = 0; // No more event after
		}
		return queue.poll();
	}

	// Tells if at least one rule has a target
	private boolean hasRulesWithTarget () {
		for ( Rule rule : params.rules ) {
			if ( rule.targetGroup != -1 ) return true;
		}
		return false;
	}

}
