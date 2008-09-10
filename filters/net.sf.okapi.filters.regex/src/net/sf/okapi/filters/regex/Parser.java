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

package net.sf.okapi.filters.regex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.filters.IParser;
import net.sf.okapi.common.resource.Group;
import net.sf.okapi.common.resource.IContainable;
import net.sf.okapi.common.resource.SkeletonUnit;
import net.sf.okapi.common.resource.TextUnit;

public class Parser implements IParser {

	protected Resource                 resource;
	
	private Stack<Group>               groupStack;
	private int                        sklID;
	private int                        groupID;
	private int                        itemID;
	private LinkedList<IContainable>   resultQueue;
	private BufferedReader             reader;
	private String                     inputText;
	private int                        startSearch;
	private int                        startSkl;
	private int                        nextAction;
	private boolean                    stop;

	private static final int NEXTACTION_TRANSUNIT     = 0;
	private static final int NEXTACTION_ENDINPUT      = 1;
	
	
	public Parser () {
		resource = new Resource();
	}

	public void close () {
		try {
			inputText = null;
			if ( reader != null ) {
				reader.close();
				reader = null;
			}
		}
		catch ( IOException e) {
			throw new RuntimeException(e);
		}
	}

	//TODO: remove after test
	// INFOSTRING rules
	private void tempSetStringInfoRules () {
		resource.params.rules.clear();

		Rule r = new Rule();
		r.ruleName = "r1";
		r.start = "^(.*?)\\t";
		r.end = "1$";
		r.nameStart = "^";
		r.nameEnd = "\\t";
		r.ruleType = Rule.RULETYPE_STRING;
		r.preserveWS = true;
		r.useCodeFinder = true;
		resource.params.regexOptions = Pattern.MULTILINE;
		List<String> list = r.codeFinder.getRules();
		list.add("#!\\[.*?\\]");
		list.add("#!\\{.*?\\}");
		list.add("\\\\[nrt]");
		r.codeFinder.compile();
		resource.params.rules.add(r);
//TODO: Fix case of translatable string not taken after no-trans string
	/*	r = new Rule();
		r.ruleName = "r2";
		r.start = "^(.*?)\\t";
		r.end = "0$";
		r.nameStart = "^";
		r.nameEnd = "\\t";
		r.ruleType = Rule.RULETYPE_NOTRANS;
		resource.params.rules.add(r);*/
}
	
	//TODO: remove after test
	/*// AZADA rules
	private void tempSetStringInfoRules () {
		resource.params.rules.clear();
		Rule r = new Rule();
		r.ruleName = "r2";
		r.start = "\\[.*?\\](\\t*)";
		r.end = "\\n(?=\\[.*?\\])|$";
		r.nameStart = "\\[";
		r.nameEnd = "\\]";
		r.ruleType = Rule.RULETYPE_CONTENT;
		r.preserveWS = true;
		
		r.useCodeFinder = true;
		List<String> list = r.codeFinder.getRules();
		list.add("\\^|\\n|\\t|\\r");
		list.add("%(([-0+ #]?)[-0+ #]?)((\\d\\$)?)(([\\d\\*]*)(\\.[\\d\\*]*)?)[dioxXucsfeEgGpn]");
		r.codeFinder.compile();
		
		resource.params.rules.add(r);
	}*/
	
	public void open (InputStream input) {
		try {
			// Close any previously non-closed file
			close();
			stop = false;
			
			// Open the input reader from the provided stream
			BOMAwareInputStream bis = new BOMAwareInputStream(input, resource.getSourceEncoding());
			reader = new BufferedReader(
				new InputStreamReader(bis, bis.detectEncoding()));

			// Read the whole file into one string
			//TODO: detect the original line-break type
			//TODO: Optimize this with a better 'readToEnd()'
			StringBuilder tmp = new StringBuilder();
			String buffer;
			while ( (buffer = reader.readLine()) != null ) {
				if ( tmp.length() > 0 ) tmp.append("\n");
				tmp.append(buffer);
			}
			
			close(); // We can free the file handle now
			// Make sure to close before inputText is set
			// so it does not get reset to null
			inputText = tmp.toString();
			resultQueue = new LinkedList<IContainable>();
			groupStack = new Stack<Group>();
			startSearch = 0;
			startSkl = 0;
			itemID = 0;
			sklID = 0;
			groupID = 0;
			nextAction = -1;

			//For test
			tempSetStringInfoRules();
			
			// Compile the rules
			resource.params.compileRules();
		}
		catch ( UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		catch ( IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void open (CharSequence input) {
		// TODO open(CharSequence input)
	}

	public void open (URL input) {
		// TODO open(URL input)
	}
	
	public IContainable getResource() {
		return resource.currentRes;
	}
	
	public ParserTokenType parseNext () {
		if ( stop ) {
			nextAction = -1;
			return ParserTokenType.ENDINPUT;
		}
		
		//TODO: process outerString if needed for each skeleton part
		switch ( nextAction ) {
		case NEXTACTION_TRANSUNIT:
			nextAction = -1;
			return ParserTokenType.TRANSUNIT;
		case NEXTACTION_ENDINPUT:
			nextAction = -1;
			return ParserTokenType.ENDINPUT;
		case -2: // Pop group
			nextAction = -1;
			break;
		}
		
		// Return from the queue if needed
		if ( resultQueue.size() > 0 ) return nextEvent();
		
		/* No support for named group means we cannot use a big 'ORed' expression
		 * and just get the first rule that was matched.
		 * We have to calculate which rule matches the closer from the
		 * start position, one by one.
		 */
		//TODO: optimize by flagging and not re-using rules that never match
		//TODO: optimize by having the rules with pre-compiled pattern
		Rule bestRule = null;
		int bestPosition = inputText.length()+9;
		MatchResult startResult = null;
		MatchResult endResult = null;
		int i = 0;
		for ( Rule rule : resource.params.rules ) {
			Pattern p = Pattern.compile(rule.start, resource.params.regexOptions);
			Matcher m = p.matcher(inputText);
			if ( m.find(startSearch) ) {
				if ( m.start() < bestPosition ) {
					// Try to find the corresponding end
					p = Pattern.compile(rule.end, resource.params.regexOptions);
					Matcher me = p.matcher(inputText);
					if ( me.find(m.end()) ) {
						bestPosition = m.start();
						bestRule = rule;
						startResult = m.toMatchResult();
						endResult = me.toMatchResult();
					}
				}
			}
			i++;
		}
		
		if ( bestRule != null ) {
			// Process the match we just found
			return processMatch(bestRule, startResult, endResult);
		}
		
		// Else: Send end of the skeleton if needed
		if ( startSearch < inputText.length() ) {
			SkeletonUnit skl = new SkeletonUnit();
			skl.setData(inputText.substring(startSkl, inputText.length()));
			skl.setID(String.format("%d", ++sklID));
			resource.currentRes = skl;
			nextAction = NEXTACTION_ENDINPUT;
			return ParserTokenType.SKELETON;
		}

		// Else: we have reach the end
		return ParserTokenType.ENDINPUT;
	}

	private ParserTokenType processMatch (Rule rule,
		MatchResult startResult,
		MatchResult endResult)
	{
		SkeletonUnit skl;
		switch ( rule.ruleType ) {
		case Rule.RULETYPE_NOTRANS:
		case Rule.RULETYPE_COMMENT:
			// Skeleton data include the content
			skl = new SkeletonUnit();
			skl.setData(inputText.substring(startSkl, endResult.start()));
			skl.setID(String.format("%d", ++sklID));
			resource.currentRes = skl;
			// Update starts for next read
			startSearch = endResult.end();
			startSkl = endResult.start();
			// If comment: process the comment for directives
			if ( rule.ruleType == Rule.RULETYPE_COMMENT ) {
				resource.params.locDir.process(skl.toString());
			}
			// Then just return one skeleton event
			return ParserTokenType.SKELETON;
			
		case Rule.RULETYPE_OPENGROUP:
		case Rule.RULETYPE_CLOSEGROUP:
			// Skeleton data include the content
			skl = new SkeletonUnit();
			skl.setData(inputText.substring(startSkl, endResult.start()));
			skl.setID(String.format("%d", ++sklID));
			resource.currentRes = skl;
			// Update starts for next read
			startSearch = endResult.end();
			startSkl = endResult.start();
			//TODO: return group event, and deal with skeleton
			if ( rule.ruleType == Rule.RULETYPE_OPENGROUP ) {
				Group groupRes = new Group();
				groupRes.setID(String.valueOf(++groupID));
				if ( rule.nameStart.length() > 0 ) {
					String name = getMatch(startResult.group(), rule.nameStart, rule.nameEnd);
					if ( name != null ) {
						if ( rule.nameFormat.length() > 0 ) {
							String tmp = rule.nameFormat.replace("<parentName>",
								(groupStack.size()>0 ? groupStack.peek().getName() : "" ));
							groupRes.setName(tmp.replace("<self>", name));
						}
						else groupRes.setName(name);
					}
				}
				groupStack.push(groupRes);
			}
			else { // Rule.RULETYPE_CLOSEGROUP
				groupStack.pop();
			}
			return ParserTokenType.SKELETON;
		}
		
		// Otherwise: process the content

		// Set skeleton data if needed
		if ( startResult.end() > startSkl ) {
			addSkeletonToQueue(inputText.substring(startSkl, startResult.end()), false);
		}
		
		// Set start positions for next read
		startSearch = endResult.end();
		startSkl = endResult.start();

		// Check localization directives
		if ( !resource.params.locDir.isLocalizable(true) ) {
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

	private String getMatch (String text,
		String start,
		String end)
	{
		if (( start == null ) || ( start.length() == 0 )) return null;
		if (( end == null ) || ( end.length() == 0 )) return null;
		
		Pattern p = Pattern.compile(start, resource.params.regexOptions);
		Matcher m1 = p.matcher(text);
		if ( m1.find() ) {
			p = Pattern.compile(end, resource.params.regexOptions);
			Matcher m2 = p.matcher(text);
			if ( m2.find(m1.end()) ) {
				return text.substring(m1.end(), m2.start());
			}
		}
		return null;
	}
	
	private void processContent (Rule rule,
		String name,
		String data)
	{
		TextUnit item = new TextUnit(String.valueOf(++itemID), data);
		item.setPreserveWhitespaces(rule.preserveWS);

		if ( rule.useCodeFinder ) {
			rule.codeFinder.process(item.getSourceContent());
		}

		//splitItem(item, rule.splitters);
		
		if ( name != null ) {
			if ( rule.nameFormat.length() > 0 ) {
				String tmp = rule.nameFormat.replace("<parentName>",
					(groupStack.size()>0 ? groupStack.peek().getName() : "" ));
				item.setName(tmp.replace("<self>", name));
			}
			else item.setName(name);
		}

		resultQueue.add(item);
	}
	
	/*
	private void splitItem (TextUnit item,
		String pattern)
	{
		if (( pattern == null ) || ( pattern.length() == 0 )) return;
		return;
		//TODO: REDO splitItem
		
		//TODO: optimize by compiling once
		Pattern p = Pattern.compile(pattern, Pattern.MULTILINE);
		TextContainer src = item.getSourceContent();
		String codedText = src.getCodedText();
		Matcher m = p.matcher(codedText);
		ArrayList<IPart> tmpList = new ArrayList<IPart>();
		int start = 0;
		int id = 0;
		CodeFragment cf = new CodeFragment(IContainer.CODE_ISOLATED, ++id, null);
		
		while ( true ) {
			if ( m.find(start) ) {
				if ( start < m.start() ) {
					// If there is a segment, add the code buffer
					if ( cf.toString() != null ) tmpList.add(new Part(cf, false));
					else --id; // Adjust ID value (we didn't use it)
					// And add the segment
					tmpList.add(src.copy(start, m.start()));
					// New fragment buffer for next time
					cf = new CodeFragment(IContainer.CODE_ISOLATED, ++id, m.group());
				}
				else {
					// Else: the code is simply compiled with any previous
					// in the code buffer. this allows to group matches and to have only
					// one code fragment between segments.
					cf.append(m.group());
				}
				// Set the new start position
				start = m.end();
			}
			else { // End of the container
				if ( tmpList.size() > 0 ) {
					if ( start < codedText.length()-1 ) {
						if ( cf.toString() != null ) tmpList.add(new Part(cf, false));
						tmpList.add(src.copy(start));
					}
					else if ( cf.toString() != null ) {
						tmpList.add(new Part(cf, false));
					}
				}
				break; // Exit loop
			}
		}
		
		// Now update the item with the segments created (if there are any)
		if ( tmpList.size() > 0 ) {
			src.reset();
			for ( IPart part : tmpList ) {
				src.append(part);
			}
		}
	}*/
	
	private void processStrings (Rule rule,
		String name,
		String data)
	{
		int state = 0;
		int i = -1;
		int j = 0;
		int start = -1;
		int end = -1;
		String mark = resource.params.startString;
		
		while (( ++i < data.length() ) && ( end == -1 )) {
			
			// Deal with \\, \" and \' escapes
			if ( state > 0 ) {
				if ( resource.params.useBSlashEscape ) {
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
						mark = resource.params.endString;
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
						mark = resource.params.endString;
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
		}

		// If we have found a string: process it
		if ( end != -1 ) {
			// Skeleton part before
			if ( start > 0 ) {
				addSkeletonToQueue(data.substring(0, start), false);
			}
			
			// Item to extract
			TextUnit item = new TextUnit(String.valueOf(++itemID),
				data.substring(start, end));
			item.setPreserveWhitespaces(rule.preserveWS);
			
			if ( rule.useCodeFinder ) {
				rule.codeFinder.process(item.getSourceContent());
			}

			//splitItem(item, rule.splitters);
			
			if ( name != null ) {
				if ( rule.nameFormat.length() > 0 ) {
					String tmp = rule.nameFormat.replace("<parentName>",
						(groupStack.size()>0 ? groupStack.peek().getName() : "" ));
					item.setName(tmp.replace("<self>", name));
				}
				else item.setName(name);
			}
			resultQueue.add(item);
			
			// Skeleton part after
			if ( end < data.length() ) {
				addSkeletonToQueue(data.substring(end), false);
			}
		}
		else { // No string in this entry, all is skeleton
			addSkeletonToQueue(data, true);
		}
	}
	
	private void addSkeletonToQueue (String data,
		boolean forceNewEntry)
	{
		SkeletonUnit skl;
		if ( !forceNewEntry && ( resultQueue.size() > 0 )) {
			if ( resultQueue.getLast() instanceof SkeletonUnit ) {
				// Append to the last queue entry if possible
				skl = (SkeletonUnit)resultQueue.getLast();
				skl.appendData(data);
				return;
			}
		}
		// Else: create a new skeleton entry
		skl = new SkeletonUnit(String.valueOf(++sklID), data);
		resultQueue.add(skl);
	}
	
	private ParserTokenType nextEvent () {
		if ( resultQueue.size() == 0 ) return ParserTokenType.NONE;
		if ( resultQueue.peek() instanceof SkeletonUnit ) {
			resource.currentRes = (SkeletonUnit)resultQueue.poll();
			return ParserTokenType.SKELETON;
		}
		// Else: it's an item
		resource.currentRes = (TextUnit)resultQueue.poll();
		return ParserTokenType.TRANSUNIT;
	}

	public void cancel () {
		stop = true;
	}
}
