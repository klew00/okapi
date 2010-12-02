/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.filters.idml;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;

public class Simplifier {

	class CodeNode {

		int offset;
		int intIndex;
		char charIndex;
		Code code;
		String marker;
		String markerFlag;
		boolean adjacentPrev = false;
		boolean adjacentNext = false;
		
		public CodeNode (int offset, int intIndex, char charIndex, Code code) {
			this.offset = offset;
			this.intIndex = intIndex;
			this.charIndex = charIndex;
			this.code = code;
		}
	}
	
	class StartCodeNode extends CodeNode {
		
		EndCodeNode endNode;
		
		StartCodeNode (int offset, int intIndex, char charIndex, Code code) {
			super(offset, intIndex, charIndex, code);
			marker = new String("\ue101"+charIndex);
			markerFlag = new String("\ue101");
		}
	}
	
	class EndCodeNode extends CodeNode {

		StartCodeNode beginNode;
		
		EndCodeNode (int offset, int intIndex, char charIndex, Code code) {
			super(offset, intIndex, charIndex, code);
			marker = new String("\ue102"+charIndex);
			markerFlag = new String("\ue102");
		}
	}
	
	class PhCodeNode extends CodeNode {

		PhCodeNode (int offset, int intIndex, char charIndex, Code code) {
			super(offset, intIndex, charIndex, code);
			marker = new String("\ue103"+charIndex);
			markerFlag = new String("\ue102");
		}
	}
	
	private LinkedList<CodeNode> codeNodesList;
	private String codedText;
	
	/*
	 * Prepare simplifier for simplifying
	 */
	private void prepare (String pCodedText, List<Code> pCodes) {

		codeNodesList = new LinkedList<CodeNode>();
		codedText = pCodedText;
		
		Stack<StartCodeNode> codeNodesStack = new Stack<StartCodeNode>();
		
		for (int i = 0; i < codedText.length(); i++){ 
			
		    char c = codedText.charAt(i);
		    
		    if(c == '\ue101'){
		    	
		    	StartCodeNode cn = new StartCodeNode(i, TextFragment.toIndex(codedText.charAt(i+1)), codedText.charAt(i+1), pCodes.get(TextFragment.toIndex(codedText.charAt(i+1))));

		    	codeNodesList.add(cn);
		    	codeNodesStack.push(cn);

		    }else if(c == '\ue102'){
		    	
		    	EndCodeNode cn = new EndCodeNode(i, TextFragment.toIndex(codedText.charAt(i+1)), codedText.charAt(i+1), pCodes.get(TextFragment.toIndex(codedText.charAt(i+1))));
		    	
		    	codeNodesList.add(cn);
		    	codeNodesStack.pop().endNode = cn;
		    	
		    }else if(c == '\ue103'){
		    	
		    	PhCodeNode cn = new PhCodeNode(i, TextFragment.toIndex(codedText.charAt(i+1)), codedText.charAt(i+1), pCodes.get(TextFragment.toIndex(codedText.charAt(i+1))));
		    	
		    	codeNodesList.add(cn);
		    	
		    }
		}
		updateAdjacentFlags();
	}
	
	/*
	 * Update the adjacent flags of all the code nodes 
	 */
	private void updateAdjacentFlags () {
		
		CodeNode peekCn;
		
		for(int i=0; i< codeNodesList.size(); i++){
		
			CodeNode cn = codeNodesList.get(i);
			
			if(i+1 < codeNodesList.size()){
		
				peekCn = codeNodesList.get(i+1);
				
				if(adjacentMarkers(cn, peekCn)){

					cn.adjacentNext = true;
					peekCn.adjacentPrev = true;

				}
			}
		}
	}
	
	/**
	 * Simplifies all possible tags in a given text fragment.
	 * @param tf the text fragment to modify.
	 */
	public void simplifyAll (TextFragment tf) {
		prepare(tf.getCodedText(), tf.getCodes());
		simplifyIsolated();
		tf.setCodedText(getCodedText(), getCodes());
		
		prepare(tf.getCodedText(), tf.getCodes());
		simplifyOpeningClosing();
		tf.setCodedText(getCodedText(), getCodes());
	}

	/**
	 * Simplifies the place-holders in a given text fragment.
	 * @param tf the text fragment to modify.
	 */
	public void simplifyIsolated (TextFragment tf) {
		prepare(tf.getCodedText(), tf.getCodes());
		simplifyIsolated();
		tf.setCodedText(getCodedText(), getCodes());
	}
	
	private void simplifyIsolated () {
		
		CodeNode peekCn;
		
		for(int i=0; i< codeNodesList.size(); i++){
		
			CodeNode cn = codeNodesList.get(i);
			
			if(i+1 < codeNodesList.size()){
		
				peekCn = codeNodesList.get(i+1);
				
				if(cn.adjacentNext && peekCn.adjacentPrev){
					
					if(cn.code.getTagType() == TagType.PLACEHOLDER || peekCn.code.getTagType() == TagType.PLACEHOLDER ){
						
						//TODO: Possibly update it to direct where the PH should be added, open or closing. 
						//      Possibly do two runs one forward and one backwards.

						mergeNodes(cn,peekCn);
						i--;
						continue;
					}
				}
			}
		}
		renumberMarkerIndexes();
		updateCodeIds();
	}
		
	/*
	 * Simplify the isolated tags
	 */
	public void simplifyOpeningClosing (TextFragment tf) {
		prepare(tf.getCodedText(), tf.getCodes());
		simplifyOpeningClosing();
		tf.setCodedText(getCodedText(), getCodes());
	}
	
	/*
	 * Merges the Start tags
	 */
	private void simplifyOpeningClosing () {
		
		CodeNode peekCn;
		
		for(int i=0; i< codeNodesList.size(); i++){
		
			CodeNode cn = codeNodesList.get(i);
			
			if(i+1 < codeNodesList.size()){
		
				peekCn = codeNodesList.get(i+1);
				
				if(cn.adjacentNext && peekCn.adjacentPrev){
					
					if(cn.code.getTagType() == TagType.OPENING && peekCn.code.getTagType() == TagType.OPENING){
						
						StartCodeNode scn1 = (StartCodeNode)cn;
						StartCodeNode scn2 = (StartCodeNode)peekCn;

						EndCodeNode ecn2 = scn2.endNode;
						EndCodeNode ecn1 = scn1.endNode;

						if (adjacentMarkers(ecn2, ecn1)){

							mergeEndNodes(ecn2, ecn1);
							mergeNodes(scn1, scn2);

							i--;
							continue;
						}
					}
				}
			}
		}
		renumberMarkerIndexes();
		updateCodeIds();
	}
	
	/*
	 * Renumber marker indexes
	 */
	private void renumberMarkerIndexes () {
		for (int i=0; i< codeNodesList.size(); i++) {
			CodeNode cn = codeNodesList.get(i);
			char newCharIndex = TextFragment.toChar(i);
			String newMarker = new String(cn.markerFlag+newCharIndex);
			codedText = codedText.replace(cn.marker, newMarker);
			cn.intIndex = i;
			cn.charIndex = newCharIndex;
			cn.marker = newMarker;
		}
	}
	
	/*
	 * Generates the list of codes from the code node list
	 */
	private List<Code> getCodes () {
		List<Code> codes = new ArrayList<Code>(); 
		for(CodeNode cn : codeNodesList){
			codes.add(cn.code);
		}
		return codes;
	}
	
	/*
	 * Return codedText
	 */
	private String getCodedText () {
		return codedText;
	}

	/*
	 * Update the code ids  
	 */
	private void updateCodeIds () {
		
		for(int i=0; i< codeNodesList.size(); i++){
		
			CodeNode cn = codeNodesList.get(i);

			if(cn.code.getTagType() == TagType.OPENING){
				
				StartCodeNode scn = (StartCodeNode)cn;
				scn.code.setId(i+1);
				scn.endNode.code.setId(i+1);
				
			}else if(cn.code.getTagType() == TagType.PLACEHOLDER){

				cn.code.setId(i+1);
			}
		}
	}
	
	/*
	 * Check if markers of two codes are adjacent
	 */
	private boolean adjacentMarkers (CodeNode node1, CodeNode node2) {
		if (( node1.offset+2 ) == node2.offset ) {
			return true;
		}
		else { 
			return false;
		}
	}
	
	/*
	 * merges codedText and codes for start and isolated nodes
	 */
	private void mergeNodes (CodeNode node1, CodeNode node2) {
		codedText = codedText.replace(node1.marker+node2.marker, node1.marker);
		node1.code.setData(node1.code.getData()+node2.code.getData());
		codeNodesList.remove(node2);
	}
	
	/*
	 * merges codedText and codes for ending nodes
	 */
	private void mergeEndNodes (CodeNode node1, CodeNode node2) {
		codedText = codedText.replace(node1.marker+node2.marker, node2.marker);
		node2.code.setData(node1.code.getData()+node2.code.getData());
		codeNodesList.remove(node1);
	}

}

//TextFragment oriFrag;
//ArrayDeque<Slice> slices;
//
//class Slice {
//	
//	public Slice (boolean isString) {
//		this.isString = isString;
//		if ( isString ) buffer = new StringBuffer();
//	}
//	
//	boolean isString;
//	StringBuffer buffer;
//	
//}
//
//public void simplify (TextFragment fragment) {
//	oriFrag = fragment;
//
//	int state = 0;
//	Stack<String> left = new Stack<String>();
//	ArrayList<String> right = new ArrayList<String>();
//	slices = new ArrayDeque<Slice>();
//	String ctext = oriFrag.getCodedText();
//	
//	for ( int i=0; i<ctext.length(); i++ ) {
//		switch ( ctext.charAt(i) ) {
//		case TextFragment.MARKER_OPENING:
//		case TextFragment.MARKER_CLOSING:
//		case TextFragment.MARKER_ISOLATED:
//			char m = ctext.charAt(i);
//			if ( state == 0 ) {
//				if ( left.isEmpty() ) state = 1; // Left side
//				else state = 2; // Right side of the text
//			}
//			Code code = oriFrag.getCode(ctext.charAt(++i));
//			String sig = String.format("%c%d", m, code.getId());
//			if ( state == 1 ) { // Left mode: Stack the signatures
//				left.push(sig);
//			}
//			else { // state = 2, right side
//				if ( m != TextFragment.MARKER_ISOLATED ) {
//					if ( !left.isEmpty() && right.isEmpty() ) {
//						if ((( m == TextFragment.MARKER_CLOSING ) && ( left.peek().charAt(0) == TextFragment.MARKER_OPENING ))
//							&& ( sig.substring(1).equals(left.peek().substring(1) )))
//						{ // If we have open/close of the same code: pop it and go to the next
//							left.pop();
//							continue;
//						}
//						// Else: fall thru to fill right list
//					}
//					// Else: fall thru to fill right list
//				}
//				// Else: No more right/left matches, start right list
//				right.add(sig);
//			}
//			break;
//		default: // Text content
//			if ( state == 2 ) { // End of right
//				// left stack and right list have the non-matching codes
//				//TODO: create slices
//				// Clear the sides for next time
//				left.clear();
//				right.clear();
//			}
//			if ( state != 0 ) {
//				state = 0; // Back to text mode
//				slices.add(new Slice(true));
//			}
//			if ( state == 0 ) {
//				slices.peekLast().buffer.append(ctext.charAt(i));
//			}
//		}
//	}
//	
//	// Treat the possible leftover
//	if ( state == 2 ) {
//		
//	}
//	
//}
