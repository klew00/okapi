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

package net.sf.okapi.tm.trados;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;

public class RtfHelper {

	ArrayList<String> codes = new ArrayList<String>();
	String procString="";
	int codeCounter = 0;

	/**
	 * clears the state for a new rtf fragment--
	 */	
	public void clear(){
		codes.clear();
		procString="";
		codeCounter = 0;
	}
	
	/**
	 * Takes a resulting RTF string and parses it into a temporary state--
	 * @param sParam RTF string to process
	 */	
	public void processRtfFragment(String sParam){
		
		//--repeat until there are no more groups--
		while(containsStartGroup(sParam)){
			sParam = processInnerGroup(sParam);
		}
		//--this string should be properly parsed--
		procString = sParam;
	}
	
	/**
	 * Takes the intermediate TextFragment string and generates a real TextFragment--
	 * @return      TextFragment.
	 */			
	public TextFragment rtfToTextFragment(){
		
		int start = 0;
		int end = 0;
		int counter=0;
		String tempCode;
		
		TextFragment tf = new TextFragment();
	
		//--basically parses the temporary TextFragment string and--
		//--this could probably be done better with Regular Expression--
		for (String code : codes){

			counter++;

			//--this tempcode is in cases where there are more than 9 placeholders--
			tempCode ="#$code"+counter+"$#"; 
			int loc = procString.indexOf(tempCode, start);
			end = loc+tempCode.length();
			
			//--1. append everything up to the code--
			tf.append(procString.substring(start,loc));

			//--2. append code placeholder--
			tf.append(TagType.PLACEHOLDER, "image", code);

			//--set new position to start working from--
			start = end;
			
		}
		//--3. finally append the last text string after the last code--
		tf.append(procString.substring(end));
		return tf;
	}

	/**
	 * Checks the string to see if there are any rtf start groups to process--
	 * @param sParam Takes the entire string and locates the opening of a group.
	 * @return search result.
	 */		
	public boolean containsStartGroup(String sParam){

		if(sParam == null || sParam.length()==0){
			return false;
		}

		//--remove all escaped openings-
		sParam = sParam.replaceAll("\\\\\\{","");

		//--now search for group openings--
		int grpStart = sParam.indexOf("{");

		if(grpStart>-1)
			return true;
		else
			return false;
	}	
	
	/**
	 * Uses a stack to process the first group that doesn't contain any nested group--
	 * Used in the for loop to process all groups. 
	 * @param  sParam  	Takes the entire string and processes one group.
	 * @return      resulting processed string.
	 */		
	String processInnerGroup(String sParam){
		
		Stack<Integer> stack = new Stack<Integer>();
		StringBuilder sb = new StringBuilder();
		
		for(int i=0;i<sParam.length();i++){
			char ch = sParam.charAt(i);
			
			if(i>0 && sParam.charAt(i-1)=='\\'){
				continue;
			}
				
			if(ch=='{'){
				stack.push(i);
			}else if(ch=='}'){
				
				//--begin and end of an inner group-- 
				int begin = stack.pop();
				int end = i;

				//--append the everything up to the beginning of the group--
				sb.append(sParam.substring(0,begin));

				//--isolate the group string--
				String groupString = sParam.substring(begin, end+1);
				
				if(isInlineGroup(groupString)){
					
					codes.add(removeControlWords(groupString));
					sb.append("#$code"+(++codeCounter)+"$#");
					sb.append(unescapeRTF(sParam.substring(end+1,sParam.length())));
					
				}else{
					//--TODO: 	Currently this ignores the non-inline group assuming it's formatting. Need to do more research on rtf.
					//--		May be a problem if the group somehow nests the group with the internal codes. 
					sb.append(unescapeRTF(sParam.substring(end+1,sParam.length())));
				}
				return sb.toString();
			}
		}
		return sb.toString();
	}
	
	/**
	 * Uses regular expression to filter out the control words. 
	 * @param sParam Parameter string to use for search and replace
	 * @return Resulting string
	 */
	String removeControlWords(String sParam){
		String result = sParam.replaceAll("\\\\[a-zA-Z_0-9]+[^\\\\]", "");
		return result.substring(1, result.length()-1);
	}
	
	/**
	 * Checks the string for \\cs6 to see if the group is an inline code. 
	 * @param sParam Parameter string to use for check
	 * @return Resulting string
	 */
	boolean isInlineGroup(String sParam){
		return sParam.contains("\\cs6");
	}		
	
	/**
	 * Takes a RTF string and unescapes special characters. 
	 * @param rtfStr String to be processed.
	 * @return Resulting unescaped string.
	 */	
	public String unescapeRTF (String rtfStr) {
		//--TODO: Need add additional replacement characters--
		String temp = rtfStr.replace("\\{","{");
		temp = temp.replace("\\}","}");
		temp = temp.replace("\\\\","\\");
		temp = temp.replace("\\~","\u00a0");
		temp = temp.replace("\\~","\t");
		temp = temp.replace("\\bullet","\u2022");
		temp = temp.replace("\\lquote","\u2018");
		temp = temp.replace("\\rquote","\u2019");				
		return temp;
	}	
	
	/**
	 * Takes a TextFragments and returns an RTF string. 
	 * @param tf TextFragment to be parsed.
	 * @return Resulting RTF string.
	 */
	public String parseTextFragmentToRtf(TextFragment tf){
		
		//--TODO Not sure what the effect of the contexts are--
		int context = 0;
		List<Code> codes = tf.getCodes();
		StringBuilder sb = new StringBuilder();
		String text = tf.getCodedText();
		Code code;
		String codeTmp;
		for ( int i=0; i<text.length(); i++ ) {
			switch ( text.charAt(i) ) {
			case TextFragment.MARKER_OPENING:
			case TextFragment.MARKER_CLOSING:
			case TextFragment.MARKER_ISOLATED:
			case TextFragment.MARKER_SEGMENT:
				code = codes.get(TextFragment.toIndex(text.charAt(++i)));
				codeTmp = "{\\cs6\\f1\\cf6\\lang1024 "+ Util.escapeToRTF(code.getData(), true, context, null)+ "}";
				sb.append(codeTmp);
				break;
			default:
				sb.append(Util.escapeToRTF(String.valueOf(text.charAt(i)), true, context, null));
				break;
			}
		}
		return sb.toString();
	}

}
