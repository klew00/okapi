/*===========================================================================
  Copyright (C) 2012 by the Okapi Framework contributors
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

package net.sf.okapi.steps.tradosutils;

import java.io.File;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.RawDocument;

@UsingParameters(ParametersSearchAndReplaceWord.class)
public class SearchAndReplaceWordStep extends BasePipelineStep{

	private ParametersSearchAndReplaceWord params;
	private String search[];
	private String replace[];
	
	ActiveXComponent oWord;
	
	public SearchAndReplaceWordStep () {
		params = new ParametersSearchAndReplaceWord();
	}

	@Override
	protected Event handleRawDocument (Event event) {

		RawDocument rawDoc = event.getRawDocument();
		
		String inputPath = new File(rawDoc.getInputURI()).getPath();
		
		Dispatch oDocuments = oWord.getProperty("Documents").toDispatch(); 
		Dispatch oDocument = Dispatch.call(oDocuments, "Open", inputPath).toDispatch(); 
	    
	    Dispatch oSelection = oWord.getProperty("Selection").toDispatch(); 
	    Dispatch oFind = Dispatch.call(oSelection, "Find").toDispatch();
	    Dispatch oReplacement = Dispatch.get(oFind, "Replacement").toDispatch(); 

	    Variant f = new Variant(false);
    	Variant t = new Variant(true);
	    
    	for ( int i=0; i<params.rules.size(); i++ ) {
			if ( params.rules.get(i)[0].equals("true") ) {
				
				Boolean hasFormatting = false;
				
				Dispatch.call(oFind, "ClearFormatting");
				String findStyle = params.rules.get(i)[3];
				if(findStyle != null && findStyle.length() > 0 ){
					Dispatch.put(oFind, "Style", findStyle);
					hasFormatting = true;
				}
				
				Dispatch.call(oReplacement, "ClearFormatting");
				String replacementStyle = params.rules.get(i)[4];
				if(replacementStyle != null && replacementStyle.length() > 0 ){
					Dispatch.put(oReplacement, "Style", replacementStyle);
					hasFormatting = true;
				}
				
		    	Dispatch.callN(oFind, "Execute",new Variant[]{
		    			new Variant(params.rules.get(i)[1]), 	/*FindText*/
		    			new Variant(params.matchCase), 			/*MatchCase*/
		    			new Variant(params.wholeWord),			/*MatchWholeWord*/
		    			new Variant(params.regEx), 				/*MatchWildcards*/
		    			f,										/*MatchSoundsLike*/
		    			f,										/*MatchAllWordForms*/
		    			t,										/*Forward*/
		    			new Variant(2),							/*Wrap*/
		    			new Variant(hasFormatting),				/*Format*/
		    			new Variant(replace[i]), 				/*ReplaceWith*/
		    			new Variant(2)});						/*Replace*/
			}
		}
    	
		//Dispatch.call(oDocument, "SaveAs", outputPath, new Variant(params.getFormat()));
    	Dispatch.call(oDocument, "Save");
    	Dispatch.call(oDocument, "Close"); 
    	
		return event;
	}

	@Override
	protected Event handleStartBatch(final Event event) {
		
		oWord = new ActiveXComponent("Word.Application");
		oWord.setProperty("Visible", new Variant(true));
		
		// Compile the replacement strings
		search = new String[params.rules.size()];
		for (int i = 0; i < params.rules.size(); i++) {
			search[i] = params.rules.get(i)[1];
		}
		
		// Compile the replacement strings
		replace = new String[params.rules.size()];
		for (int i = 0; i < params.rules.size(); i++) {
			replace[i] = params.rules.get(i)[2];
		}
		
		return event;
	}
	
	@Override
	protected Event handleEndBatch(final Event event) {
		oWord.invoke("Quit");
		oWord = null;
		return event;
	}
	
	@Override
	public String getName() {
		return "MS Word Search and Replace";
	}

	@Override
	public String getDescription() {
		return "Search and Replace in word document."
				+ " Expects: raw document. Sends back: raw document.";
	}
	
	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (ParametersSearchAndReplaceWord)params;
	}
}
