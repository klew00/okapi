package net.sf.okapi.filters.regex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Stack;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import net.sf.okapi.common.BOMAwareInputStream;
import net.sf.okapi.common.resource.GroupResource;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.ISkeletonResource;

public class RegexReader {

	public static final int       RESULT_ENDINPUT     = 0;
	public static final int       RESULT_STARTGROUP   = 1;
	public static final int       RESULT_ENDGROUP     = 2;
	public static final int       RESULT_TRANSUNIT    = 3;
	public static final int       RESULT_SKELETON     = 4;
	
	protected Resource                 resource;
	protected Stack<GroupResource>     groupResStack;
	protected IExtractionItem          item;
	
	private BufferedReader   reader;
	private String           inputText;
	private Matcher          matcher;
	private int              startPos;
	private int              nextAction;

	
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
	
	public void open (InputStream input) {
		try {
			// Close any previously non-closed file
			close();
			
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

			// Compile the rules
			resource.params.compileRules();
			matcher = Pattern.compile(resource.params.expression).matcher(inputText);
			startPos = 0;
			nextAction = -1;
		}
		catch ( UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		catch ( IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int read () {
		//TODO: process outerString if needed for each skeleton part
		switch ( nextAction ) {
		case RESULT_TRANSUNIT:
			nextAction = -1;
			return RESULT_TRANSUNIT;
		case RESULT_ENDINPUT:
			nextAction = -1;
			return RESULT_ENDINPUT;
		}
		
		/* No support for named group means we cannot use a big 'ORed' expression
		 * and just get the first rule that was matched.
		 * We have to calculate which rule matches the closer from the
		 * start position, one by one.
		 */
		//TODO: optimize by flagging and not re-using rules that never match
		//TODO: optimize by having the rules with pre-compiled pattern
		Rule bestRule = null;
		int bestPosition = inputText.length()+9;
		MatchResult mr = null;
		int i = 0;
		for ( Rule rule : resource.params.rules ) {
			Pattern p = Pattern.compile(rule.start);
			Matcher m = p.matcher(inputText);
			if ( m.find(startPos) ) {
				if ( m.start() < bestPosition ) {
					bestPosition = m.start();
					bestRule = rule;
					mr = m.toMatchResult();
				}
			}
			i++;
		}
		
		if ( bestRule != null ) {
			// Check if there is an ending
			Pattern p = Pattern.compile(bestRule.end);
			Matcher m = p.matcher(inputText);
			if ( !m.find(startPos+bestPosition+mr.group().length()) ) {
				//TODO: handle no ending
				throw new RuntimeException("No ending. TODO");
			}
			// Process the match we just found
			return processMatch(bestRule, mr);
		}
		
		// Else: Send end of the skeleton if needed
		if ( startPos < inputText.length() ) {
			resource.sklRes.setData(
				inputText.substring(startPos, inputText.length()));
			nextAction = RESULT_ENDINPUT;
			return RESULT_SKELETON;
		}

		// Else: we have reach the end
		return RESULT_ENDINPUT;
	}

	private int processMatch (Rule rule,
		MatchResult result)
	{
		int retValue = RESULT_TRANSUNIT;
		int startContent = startPos+result.end();
		// startPos to bestPosition+mr.group().length()
		//    = skeleton before (including start pattern)
		if ( startContent > startPos ) {
			// Set skeleton data if needed
			resource.sklRes.setData(
				inputText.substring(startPos, startContent));
			retValue = RESULT_SKELETON;
			nextAction = RESULT_TRANSUNIT;
		}
		return retValue;
	}
	
	public IExtractionItem getItem () {
		return item;
	}
	
	public ISkeletonResource getSkeleton () {
		return resource.sklRes;
	}

}
