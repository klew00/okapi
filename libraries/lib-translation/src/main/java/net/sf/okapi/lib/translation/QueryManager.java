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

package net.sf.okapi.lib.translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.annotation.ScoresAnnotation;
import net.sf.okapi.common.filterwriter.TMXWriter;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

/**
 * Provides a wrapper to manage and query several translation resources at the 
 * same time. For example, a local TM, a remote TM and a MT server.
 */
public class QueryManager {

	private final Logger logger = Logger.getLogger(getClass().getName());
	
	private LinkedHashMap<Integer, ResourceItem> resList;
	private ArrayList<QueryResult> results;
	private int current = -1;
	private int lastId = 0;
	private String srcLang;
	private String trgLang;
	private LinkedHashMap<String, String> attributes;
	private int threshold = 75;
	private int maxHits = 5;
	private int totalSegments;
	private int leveragedSegments;
	
	/**
	 * Creates a new QueryManager object.
	 */
	public QueryManager () {
		resList = new LinkedHashMap<Integer, ResourceItem>();
		results = new ArrayList<QueryResult>();
		attributes = new LinkedHashMap<String, String>();
	}
	
	/**
	 * Adds a translation resource to the manager.
	 * @param connector The translation resource connector to add.
	 * @param name Name of the translation resource to add.
	 * @return The ID for the added translation resource. This ID can be
	 * used later to access specifically the added translation resource.
	 */
	public int addResource (IQuery connector, String name) {
		assert(connector!=null);
		ResourceItem ri = new ResourceItem();
		ri.query = connector;
		ri.enabled = true;
		ri.name = name;
		resList.put(++lastId, ri);
		return lastId;
	}
	
	/**
	 * Adds a translation resource to the manager and initializes it with the 
	 * current source and target language of this manager, as well as any
	 * attributes that is set, and the current threshold and maximum hits if it is relevant.
	 * @param connector The translation resource connector to add.
	 * @param resourceName Name of the translation resource to add.
	 * @param params the parameters for this connector.
	 * @return The identifier for the added translation resource. This identifier
	 * can be used later to access specifically the added translation resource.
	 */
	public int addAndInitializeResource (IQuery connector,
		String resourceName,
		IParameters params)
	{
		// Add the resource
		int id = addResource(connector, resourceName);
		// Set the parameters and open 
		connector.setParameters(params);
		connector.open();
		if (( srcLang != null ) && ( trgLang != null )) {
			connector.setLanguages(srcLang, trgLang);
		}
		for ( String name : attributes.keySet() ) {
			connector.setAttribute(name, attributes.get(name));
		}
		if ( connector instanceof ITMQuery ) {
			((ITMQuery)connector).setThreshold(threshold);
			((ITMQuery)connector).setMaximumHits(maxHits);
		}
		return id;
	}
	
	/**
	 * Enables or disables a given translation resource.
	 * @param resourceId ID of the translation resource to enable or disable.
	 * @param enabled True to enable the resource, false to disable it.
	 */
	public void setEnabled (int resourceId,
		boolean enabled)
	{
		resList.get(resourceId).enabled = enabled;
	}
	
	/**
	 * Removes a given translation resource.
	 * @param resourceId ID of the translation resource to remove.
	 */
	public void remove (int resourceId) {
		resList.remove(resourceId);
	}
	
	/**
	 * Gets the IQuery interface for a given translation resource.
	 * @param resourceId ID of the translation resource to lookup.
	 * @return The IQuery interface for the given translation resource, or null
	 * if the ID is not found.
	 */
	public IQuery getInterface (int resourceId) {
		return resList.get(resourceId).query;
	}
	
	/**
	 * Gets the configuration data for a given translation resource.
	 * @param resourceId ID of the translation resource to lookup.
	 * @return A ResourceItem object that contains the configuration data for 
	 * the given translation resource, or null if the ID is not found.
	 */
	public ResourceItem getResource (int resourceId) {
		return resList.get(resourceId);
	}
	
	/**
	 * Gets the name for a given translation resource.
	 * @param resourceId ID of the translation resource to lookup.
	 * @return The name of the given translation resource, or null
	 * if the ID is not found.
	 */
	public String getName (int resourceId) {
		return resList.get(resourceId).name;
	}
	
	/**
	 * Gets the configuration data for all the translation resources in this manager.
	 * @return A map of ID+ResourceItem objects pairs that contains the
	 * configuration data for each translation resource. the map can be empty.
	 */
	public Map<Integer, ResourceItem> getResources () {
		return resList;
	}

	/**
	 * Closes all translation resources in this manager.
	 */
	public void close () {
		for ( ResourceItem ri : resList.values() ) {
			ri.query.close();
		}
	}

	/**
	 * Gets the list of all hit results of the last query.
	 * @return A list of all hit results of the last query.
	 */
	public List<QueryResult> getResults () {
		return results;
	}
	
	/**
	 * Resets the current result to the first one if there is one.
	 */
	public void rewind () {
		if ( results.size() > 0 ) current = 0;
		else current = -1;
	}
	
	/**
	 * Indicates of there is a hit available.
	 * @return True if a hit is available, false if not.
	 */
	public boolean hasNext() {
		if ( current >= results.size() ) {
			current = -1;
		}
		return (current > -1);
	}

	/**
	 * Gets the next hit for the last query.
	 * @return A QueryResult object that holds the source and target text of
	 * the hit, or null if there is no more hit.
	 */
	public QueryResult next () {
		if (( current > -1 ) && ( current < results.size() )) {
			current++;
			return results.get(current-1);
		}
		current = -1;
		return null;
	}

	/**
	 * Queries all enabled translation resources for a given plain text. 
	 * @param plainText The text to query.
	 * @return The number of hits for the given query.
	 */
	public int query (String plainText) {
		results.clear();
		ResourceItem ri;
		for ( int id : resList.keySet() ) {
			ri = resList.get(id);
			if ( !ri.enabled ) continue; // Skip disabled entries
			if ( ri.query.query(plainText) > 0 ) {
				QueryResult res;
				while ( ri.query.hasNext() ) {
					res = ri.query.next();
					res.connectorId = id;
					results.add(res);
				}
			}
		}
		if ( results.size() > 0 ) current = 0;
		Collections.sort(results); // Sort by weights
		return results.size();
	}

	/**
	 * Queries all enabled translation resources for a given text fragment. 
	 * @param text The text to query.
	 * @return The number of hits for the given query.
	 */
	public int query (TextFragment text) {
		results.clear();
		ResourceItem ri;
		for ( int id : resList.keySet() ) {
			ri = resList.get(id);
			if ( !ri.enabled ) continue; // Skip disabled entries
			if ( ri.query.query(text) > 0 ) {
				QueryResult res;
				while ( ri.query.hasNext() ) {
					res = ri.query.next();
					res.connectorId = id;
					results.add(res);
				}
			}
		}
		if ( results.size() > 0 ) current = 0;
		Collections.sort(results); // Sort by weights
		return results.size();
	}

	/**
	 * Sets an attribute for this manager and all translation resources in
	 * this manager.
	 * @param name name of the attribute.
	 * @param value Value of the attribute.
	 */
	public void setAttribute (String name,
		String value)
	{
		attributes.put(name, value);
		for ( ResourceItem ri : resList.values() ) {
			ri.query.setAttribute(name, value);
		}
	}
	
	/**
	 * Removes a given attribute from this manager and all translation
	 * resources in this manager.
	 * @param name The name of the attribute to remove.
	 */
	public void removeAttribute (String name) {
		attributes.remove(name);
		for ( ResourceItem ri : resList.values() ) {
			ri.query.removeAttribute(name);
		}
	}
	
	/**
	 * Removes all attributes from this manager and all the translation
	 * resources in this manager.
	 */
	public void clearAttributes () {
		attributes.clear();
		for ( ResourceItem ri : resList.values() ) {
			ri.query.clearAttributes();
		}
	}
	
	/**
	 * Sets the source and target languages for this manager and for all
	 * translation resources in this manager.
	 * @param sourceLang Code of the source language to set.
	 * @param targetLang Code of the target language to set.
	 */
	public void setLanguages (String sourceLang,
		String targetLang)
	{
		srcLang = sourceLang;
		trgLang = targetLang;
		for ( ResourceItem ri : resList.values() ) {
			ri.query.setLanguages(srcLang, trgLang);
		}
	}

	/**
	 * Gets the current sourcelanguage for this manager.
	 * @return Code of the current source language for this manager.
	 */
	public String getSourceLanguage () {
		return srcLang;
	}

	/**
	 * Gets the current target language for this manager.
	 * @return Code of the current target language for this manager.
	 */
	public String getTargetLanguage () {
		return trgLang;
	}

	/**
	 * Sets the threshold for this query manager and all the relevant
	 * translation resources it holds.
	 * @param value the threshold value to set.
	 */
	public void setThreshold (int value) {
		threshold = value;
		for ( ResourceItem ri : resList.values() ) {
			if ( ri.query instanceof ITMQuery ) {
				((ITMQuery)ri.query).setThreshold(threshold);
			}
		}
	}
	
	/**
	 * Sets the maximum number of hits to return for this query manager
	 * and all the relevant translation resources it holds.
	 * @param max the maximum value to set.
	 */
	public void setMaximumHits (int max) {
		maxHits = max;
		for ( ResourceItem ri : resList.values() ) {
			if ( ri.query instanceof ITMQuery ) {
				((ITMQuery)ri.query).setMaximumHits(maxHits);
			}
		}
	}

	/**
	 * Leverages a text unit (segmented or not) based on the current settings.
	 * Any options or attributes needed must be set before calling this method.
	 * @param tu the text unit to leverage.
	 * @param tmxWriter TMX writer object where to also write the results (can be null).
	 * @param fillTarget true to put the leveraged text into the target, false to not.
	 */
	public void leverage (TextUnit tu,
		TMXWriter tmxWriter,
		boolean fillTarget)
	{
		if ( !tu.isTranslatable() ) return;
		
		TextContainer tc = tu.getSource().clone();
		ScoresAnnotation scores = new ScoresAnnotation();
		QueryResult qr;
		int count;
		int leveraged = 0;
		boolean makeSS = false;
		
		if ( tc.isSegmented() ) {
			List<Segment> segList = tc.getSegments();
			for (Segment segment : segList) {
				// Query if needed
				if ( segment.text.hasText(false) ) {
					totalSegments++;
					count = query(segment.text);
				}
				else count = 0;
				
				// Process results
				if ( count == 0 ) {
					scores.add(0);
					continue;
				}
				qr = next();
				// It's a 100% match
				if ( qr.score == 100 ) {
					// Check if they are several and if they have the same translation
					if ( !exactsHaveSameTranslation() ) {
						if ( threshold == 100 ) {
							scores.add(0);
							continue;
						}
						// If we do: Use the first one and lower the score to 99%
						scores.add(99);
						segment.text = adjustNewFragment(segment.text, qr.target, true, tu);
						leveraged++;
						continue;
					}
					// Else: First is 100%, possibly several that have the same translations
					scores.add(qr.score); // That's 100% then
					segment.text = adjustNewFragment(segment.text, qr.target, true, tu);
					leveraged++;
					continue;
				}
				// First is not 100%: use it and move on
				scores.add(qr.score);
				segment.text = adjustNewFragment(segment.text, qr.target, true, tu);
				leveraged++;
			}
		}
		else { // Case of un-segmented entries
			// Query if needed
			if ( tc.hasText(false) ) {
				totalSegments++;
				count = query(tc);
			}
			else count = 0;
			
			// Process results
			if ( count == 0 ) {
				scores.add(0);
			}
			else {
				qr = next();
				// First is not 100%: use it and move on
				if ( qr.score < 100 ) {
					scores.add(qr.score);
					tc.setCodedText(qr.target.getCodedText(), false);
					makeSS = true;
				}
				// Else: one or more matches, first is 100%
				// Check if they are several and if they have the same translation
				else if ( !exactsHaveSameTranslation() ) {
					if ( threshold >= 100 ) {
						scores.add(0);
					}
					else {
						// If we do: Use the first one and lower the score to 99%
						scores.add(99);
						tc.setCodedText(qr.target.getCodedText(), false);
						makeSS = true;
					}
				}
				// Else: Only one 100% or several that have the same translations
				else {
					scores.add(qr.score); // That's 100% then
					tc.setCodedText(qr.target.getCodedText(), false);
					makeSS = true;
				}
			}
		}

		// Set the scores only if there is something to report
		if (( leveraged > 0 ) || makeSS ) {
			// Set the target and attach the score
			tc.setAnnotation(scores);
			tu.setTarget(trgLang, tc);
			if ( makeSS ) {
				// Un-segmented entries that we have leveraged should be like
				// a text unit with a single segment
				makeSingleSegment(tu);
				leveraged++;
			}
			leveragedSegments += leveraged;

			if ( tmxWriter != null ) {
				tmxWriter.writeItem(tu, null);
			}
			if ( !fillTarget ) {
				tu.removeTarget(trgLang);
			}
		}	
	}
	
	private boolean exactsHaveSameTranslation () {
		rewind();
		QueryResult qr = next();
		if ( qr == null ) return false;
		if ( qr.score < 100 ) return false;
		TextFragment firstFrag = qr.target;
		while ( hasNext() ) {
			qr = next();
			if ( qr.score < 100 ) return true;
			if ( qr.target.compareTo(firstFrag, true) != 0 ) return false;
		}
		return true;
	}

	private void makeSingleSegment (TextUnit tu) {
		TextContainer srcTc = tu.getSource();
		// Leave it alone if it's just whitespaces
		if ( !srcTc.hasText(false) ) return;
		// Else create a single segment that is the whole content
		srcTc.createSegment(0, -1);
		TextContainer tc = tu.getTarget(trgLang);
		if ( tc == null ) tc = tu.createTarget(trgLang, false, IResource.CREATE_EMPTY);
		tc.createSegment(0, -1);
	}

	/**
	 * Adjusts the inline codes of a new text fragment based on an original one.
	 * @param oriFrag the original text fragment.
	 * @param newFrag the new text fragment.
	 * @param parent the parent text unit (used for error information only)
	 * @return the new text fragment
	 */
	public TextFragment  adjustNewFragment (TextFragment oriFrag,
		TextFragment newFrag,
		boolean removeExtra,
		TextUnit parent)
	{
		// If both new and original have no code, return the new fragment
		if ( !newFrag.hasCode() && !oriFrag.hasCode() ) {
			return newFrag;
		}
		List<Code> oriCodes = oriFrag.getCodes();
		List<Code> newCodes = newFrag.getCodes();
		
		int[] oriIndices = new int[oriCodes.size()];
		for ( int i=0; i<oriIndices.length; i++ ) oriIndices[i] = i;
		
		int done = 0;
		boolean orderWarning;
		Code newCode, oriCode;
//		StringBuilder text = new StringBuilder(newFrag.getCodedText());
		
//		for ( int i=0; i<text.length(); i++ ) {
//			if ( !TextFragment.isMarker(text.charAt(i)) ) {
//				continue;
//			}
//			
//			newCode = newFrag.getCode(text.charAt(++i));
//			newCode.setOuterData(null); // Remove outer codes
//
//			// Get the data from the original code (match on id)
//			oriCode = null;
//			orderWarning = true;
//			for ( int j=0; j<oriIndices.length; j++ ) {
//				if ( oriIndices[j] == -1) continue; // Used already
//				if ( oriCodes.get(oriIndices[j]).getId() == newCode.getId() ) {
//					oriCode = oriCodes.get(oriIndices[j]);
//					oriIndices[j] = -1;
//					done++;
//					break;
//				}
//				if ( orderWarning ) {
//					logger.warning(String.format("The target code id='%d' has been moved (item id='%s', name='%s')",
//						newCode.getId(), parent.getId(), (parent.getName()==null ? "" : parent.getName())));
//					orderWarning = false; 
//				}
//			}
//		}
		
		for ( int i=0; i<newCodes.size(); i++ ) {
			newCode = newCodes.get(i);
			newCode.setOuterData(null); // Remove XLIFF outer codes if needed

			// Get the data from the original code (match on id)
			oriCode = null;
			orderWarning = true;
			for ( int j=0; j<oriIndices.length; j++ ) {
				if ( oriIndices[j] == -1) continue; // Used already
				if ( oriCodes.get(oriIndices[j]).getId() == newCode.getId() ) {
					oriCode = oriCodes.get(oriIndices[j]);
					oriIndices[j] = -1;
					done++;
					break;
				}
				if ( orderWarning ) {
					logger.warning(String.format("The target code id='%d' has been re-ordered (item id='%s', name='%s')",
						newCode.getId(), parent.getId(), (parent.getName()==null ? "" : parent.getName())));
					orderWarning = false; 
				}
			}
			
			if ( oriCode == null ) { // Not found in original (extra in target)
				if (( newCode.getData() == null )
					|| ( newCode.getData().length() == 0 )) {
					// Leave it like that
					logger.warning(String.format("The extra target code id='%d' does not have corresponding data (item id='%s', name='%s')",
						newCode.getId(), parent.getId(), (parent.getName()==null ? "" : parent.getName())));
				}
				
			}
			else { // A code with same ID existed in the original
				// Get the data from the original
				newCode.setData(oriCode.getData());
				newCode.setOuterData(oriCode.getOuterData());
				newCode.setReferenceFlag(oriCode.hasReference());
			}
		}
		
		// If needed, check for missing codes in new fragment
		if ( oriCodes.size() > done ) {
			// Any index > -1 in source means it was was deleted in target
			for ( int i=0; i<oriIndices.length; i++ ) {
				if ( oriIndices[i] != -1 ) {
					Code code = oriCodes.get(oriIndices[i]);
					if ( !code.isDeleteable() ) {
						logger.warning(String.format("The code id='%d' (%s) is missing in target (item id='%s', name='%s')",
							code.getId(), code.getData(), parent.getId(), (parent.getName()==null ? "" : parent.getName())));
						logger.info("Source='"+parent.getSource().toString()+"'");
					}
				}
			}
		}
		
		return newFrag;
	}

	public void resetCounters () {
		totalSegments = 0;
		leveragedSegments = 0;
	}
	
	public int getTotalSegments () {
		return totalSegments;
	}
	
	public int getLeveragedSegments () {
		return leveragedSegments;
	}
}
