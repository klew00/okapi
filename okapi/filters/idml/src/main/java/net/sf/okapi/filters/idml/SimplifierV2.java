package net.sf.okapi.filters.idml;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;


public class SimplifierV2 {

	class CodeNode{

		int offset;
		int intIndex;
		char charIndex;
		Code code;
		String marker;
		String markerFlag;
		boolean adjacentPrev = false;
		boolean adjacentNext = false;
		
		public CodeNode(int offset, int intIndex, char charIndex, Code code){
			this.offset = offset;
			this.intIndex = intIndex;
			this.charIndex = charIndex;
			this.code = code;
		}
	}
	
	class StartCodeNode extends CodeNode{
		
		EndCodeNode endNode;
		
		StartCodeNode(int offset, int intIndex, char charIndex, Code code) {
			super(offset, intIndex, charIndex, code);
			marker = new String("\ue101"+charIndex);
			markerFlag = new String("\ue101");
		}
	}
	
	class EndCodeNode extends CodeNode{

		StartCodeNode beginNode;
		
		EndCodeNode(int offset, int intIndex, char charIndex, Code code) {
			super(offset, intIndex, charIndex, code);
			marker = new String("\ue102"+charIndex);
			markerFlag = new String("\ue102");
		}
	}
	
	class PhCodeNode extends CodeNode{

		PhCodeNode(int offset, int intIndex, char charIndex, Code code) {
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
	private void prepare(String pCodedText, List<Code> pCodes){

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
	private void updateAdjacentFlags(){
		
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
	
	/*
	 * Simplify the isolated tags
	 */
	public void simplifyAll(TextFragment tf){
		
		prepare(tf.getCodedText(), tf.getCodes());
		simplifyIsolated();
		
		tf.setCodedText(getCodedText(), getCodes());
		
		prepare(tf.getCodedText(), tf.getCodes());
		simplifyOpeningClosing();
		
		tf.setCodedText(getCodedText(), getCodes());
	}
	
	/*
	 * Simplify the isolated tags
	 */
	public void simplifyIsolated(TextFragment tf){
		
		prepare(tf.getCodedText(), tf.getCodes());
		simplifyIsolated();
		
		tf.setCodedText(getCodedText(), getCodes());
	}
	
	/*
	 * Simplify the isolated tags
	 */
	private void simplifyIsolated(){
		
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
	public void simplifyOpeningClosing(TextFragment tf){
		
		prepare(tf.getCodedText(), tf.getCodes());
		simplifyOpeningClosing();
		
		tf.setCodedText(getCodedText(), getCodes());
	}
	
	/*
	 * Merges the Start tags
	 */
	private void simplifyOpeningClosing(){
		
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
	private void renumberMarkerIndexes(){
		for(int i=0; i< codeNodesList.size(); i++){

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
	private List<Code> getCodes(){
		
		List<Code> codes = new ArrayList<Code>(); 
		
		for(CodeNode cn : codeNodesList){
			codes.add(cn.code);
		}
		
		return codes;
	}
	
	/*
	 * Return codedText
	 */
	private String getCodedText(){
		
		return codedText;
	}

	/*
	 * Update the code ids  
	 */
	private void updateCodeIds(){
		
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
	private boolean adjacentMarkers(CodeNode node1, CodeNode node2){

		if((node1.offset+2) == node2.offset)
			return true;
		else 
			return false;
	}
	
	/*
	 * merges codedText and codes for start and isolated nodes
	 */
	private void mergeNodes(CodeNode node1, CodeNode node2){

		codedText = codedText.replace(node1.marker+node2.marker, node1.marker);
		
		node1.code.setData(node1.code.getData()+node2.code.getData());
		
		codeNodesList.remove(node2);
	}
	
	/*
	 * merges codedText and codes for ending nodes
	 */
	private void mergeEndNodes(CodeNode node1, CodeNode node2){

		codedText = codedText.replace(node1.marker+node2.marker, node2.marker);
		
		node2.code.setData(node1.code.getData()+node2.code.getData());
		
		codeNodesList.remove(node1);
	}
}
