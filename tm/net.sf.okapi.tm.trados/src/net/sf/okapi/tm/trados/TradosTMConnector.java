/*===========================================================================*/
/* Copyright (C) 2008 By the Okapi Framework contributors                    */
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

package net.sf.okapi.tm.trados;

import java.util.ArrayList;
import java.util.List;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.ITMQuery;
import net.sf.okapi.lib.translation.QueryResult;

public class TradosTMConnector implements ITMQuery {
	
	private int threshold = 100;
	private int maxHits = 5;
	private List<QueryResult> results;
	private int current = -1;
	private String srcLang;
	private String trgLang;
	private ActiveXComponent tradosInstance, tmInstance;

	public void open (String connectionString) {
		tradosInstance = new ActiveXComponent("TW4Win.Application");
		tmInstance = tradosInstance.getPropertyAsComponent("TranslationMemory");
		tmInstance.invoke("Open",new Variant(connectionString),new Variant("BATCH_ANALYZER"));
	}
		
	public void close () {
		tradosInstance.invoke("quit", new Variant[] {});
	}


	public void export (String outputPath) {
		// TODO Auto-generated method stub
	}

	public int query(String plainText) {
		TextFragment tf = new TextFragment(plainText);
		return query(tf);
	}

	public int query (TextFragment tf) {
		String searchString;

		//--process plain text or parsed TextFragment if ther are codes--
		if ( !tf.hasCode() ) {
			searchString = Util.escapeToRTF(tf.toString(), true, 0, null);
		}
		else {
			RtfHelper rth = new RtfHelper();
			searchString = rth.parseTextFragmentToRtf(tf);			
		}
		
		current = -1;
		try {
	    	//ActiveXComponent xl = ActiveXComponent.connectToActiveInstance("TW4Win.Application");
	    	//if(xl==null){
	    	//	xl = new ActiveXComponent("TW4Win.Application");    		
	    	//}
			//ActiveXComponent tm = xl.getPropertyAsComponent("TranslationMemory");
			//tm.invoke("Open",new Variant("D:\\Rainbow_ID_Aligner\\tm\\testMultiple.tmw"),new Variant("BATCH_ANALYZER"));
			ActiveXComponent tu = tmInstance.getPropertyAsComponent("TranslationUnit");

			//--execute the search--
			tmInstance.invoke("Search",new Variant(searchString));
			current = -1;
			int hits = tmInstance.invoke("HitCount").getInt();
			int counter = 0;
			
			if ( hits ==0 ) {
				return 0;
			}
			else {
				results = new ArrayList<QueryResult>();
				do {
					counter++;
					QueryResult qr = new QueryResult();
					qr.score = tu.invoke("Score").getInt();
					
					RtfHelper rth = new RtfHelper();
					rth.processRtfFragment(tu.invoke("Source").getString());
					qr.source = rth.rtfToTextFragment();
					rth.clear();
					
					rth.processRtfFragment(tu.invoke("Target").getString());
					qr.target = rth.rtfToTextFragment();
					rth.clear();
					
					results.add(qr);
					
				} while (tu.invoke("Next").getBoolean() && (counter < maxHits));
			}
			current = 0;
        }
		catch (Exception e) {
    		System.out.println("Error occured");
        } 			
		return results.size();
	}

	public void removeAttribute(String anme) {
		// TODO Auto-generated method stub
	}

	public void setAttribute(String name, String value) {
		// TODO Auto-generated method stub
	}

	public boolean hasOption (int option) {
		switch ( option ) {
		case HAS_FILEPATH:
			return true;
		default:
			return false;
		}
	}
	
	public QueryResult next () {
		if ( results == null ) return null;
		if (( current > -1 ) && ( current < results.size() )) {
			current++;
			return results.get(current-1);
		}
		current = -1;
		return null;
	}
	
	public boolean hasNext () {
		if ( results == null ) return false;
		if ( current >= results.size() ) {
			current = -1;
		}
		return (current > -1);
	}

	public String getSourceLanguage () {
		return srcLang;
	}
	
	public String getTargetLanguage () {
		return trgLang;
	}	

	public void setLanguages (String sourceLang, String targetLang) {
		srcLang = sourceLang;
		trgLang = targetLang;
	}

	public void setMaximumHits (int max) {
		if ( max < 1 ) maxHits = 1;
		else maxHits = max;
	}
	
	public int getMaximunHits () {
		return maxHits;
	}

	public void setThreshold (int threshold) {
		this.threshold = threshold;
	}

	public int getThreshold () {
		return threshold;
	}
	
}