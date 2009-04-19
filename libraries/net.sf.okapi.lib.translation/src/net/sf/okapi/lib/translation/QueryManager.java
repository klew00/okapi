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

import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.annotation.ScoresAnnotation;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextUnit;

/**
 * Provides a wrapper to manage and query several translation resources at the 
 * same time. For example, a local TM, a remote TM and a MT server.
 */
public class QueryManager {

	private LinkedHashMap<Integer, ResourceItem> resList;
	private ArrayList<QueryResult> results;
	private int current = -1;
	private int lastId = 0;
	private String srcLang;
	private String trgLang;
	private LinkedHashMap<String, String> attributes;
	
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
	 * attributes that is set.
	 * @param connector The translation resource connector to add.
	 * @param resourceName Name of the translation resource to add.
	 * @param connectionString the string connection to open this translation resource.
	 * @return The ID for the added translation resource. This ID can be
	 * used later to access specifically the added translation resource.
	 */
	public int addAndInitializeResource (IQuery connector,
		String resourceName,
		String connectionString)
	{
		// Add the resource
		int id = addResource(connector, resourceName);
		// open it and set the current options
		connector.open(connectionString);
		if (( srcLang != null ) && ( trgLang != null )) {
			connector.setLanguages(srcLang, trgLang);
		}
		for ( String name : attributes.keySet() ) {
			connector.setAttribute(name, attributes.get(name));
		}
		// Set the connection string
		ResourceItem ri = resList.get(id);
		ri.connectionString = connectionString;
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
		TextFragment tf = new TextFragment(plainText);
		return query(tf);
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
	 * Remove a given attribute from this manager and all translation
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
	 * Leverages a text unit (segmented or not) based on the current settings.
	 * Any options or attributes needed must be set before calling this method.
	 * @param tu The text unit to modify.
	 */
	public void leverage (TextUnit tu) {
		if ( !tu.isTranslatable() ) return;
		if ( tu.hasTarget(trgLang) ) return;
		
		TextContainer tc = tu.setTarget(trgLang, tu.getSource().clone());
		ScoresAnnotation scores = new ScoresAnnotation();
		QueryResult qr;
		int count;
		int leveraged = 0;
		
		if ( tc.isSegmented() ) {
			List<Segment> segList = tc.getSegments();
			for ( int i=0; i<segList.size(); i++ ) {
				count = query(segList.get(i).text);
				if ( count == 0 ) {
					scores.add(0);
					continue;
				}
				qr = next();
				segList.get(i).text = qr.target;
				// First is not 100%: use it and move on
				if ( qr.score < 100 ) {
					scores.add(qr.score);
					leveraged++;
					continue;
				}
				// Else: one or more matches, first is 100%
				// Check if they are several and if they have the same translation
				if ( !exactsHaveSameTranslation() ) {
					// If we do: Use the first one and lower the score to 99%
					scores.add(99);
					leveraged++;
					continue;
				}
				// Else: Only one 100% or several that have the same translations 
				scores.add(qr.score); // That's 100% then
				leveraged++;
			}
		}
		else { // Case of un-segmented entries
			count = query(tc);
			if ( count == 0 ) {
				scores.add(0);
			}
			else {
				qr = next();
				tc.setCodedText(qr.target.getCodedText(), false);
				// Un-segmented entries that we have leveraged should be like
				// a text unit with a single segment
				makeSingleSegment(tu);

				// First is not 100%: use it and move on
				if ( qr.score < 100 ) {
					scores.add(qr.score);
					leveraged++;
				}
				// Else: one or more matches, first is 100%
				// Check if they are several and if they have the same translation
				else if ( !exactsHaveSameTranslation() ) {
					// If we do: Use the first one and lower the score to 99%
					scores.add(99);
					leveraged++;
				}
				// Else: Only one 100% or several that have the same translations
				else {
					scores.add(qr.score); // That's 100% then
					leveraged++;
				}
			}
		}

		// Set the scores only if there is something to report
		if ( leveraged > 0 ) {
			tc.setAnnotation(scores);
		}
	}
	
	private boolean exactsHaveSameTranslation () {
		rewind();
		QueryResult qr = next();
		if ( qr == null ) return false;
		if ( qr.score < 100 ) return false;
		TextFragment firsFrag = qr.target;
		while ( hasNext() ) {
			qr = next();
			if ( qr.score < 100 ) return true;
			if ( qr.target.compareTo(firsFrag) != 0 ) return false;
		}
		return true;
	}

	private void makeSingleSegment (TextUnit tu) {
		TextContainer srcTc = tu.getSource();
		// Leave it alone if it's just whitespaces
		if ( !srcTc.hasText(false) ) return;
		// Else create a single segment that is the whole content
		srcTc.createSegment(0, -1);
		tu.getTarget(trgLang).createSegment(0, -1);
	}
	
	/**
	 * Saves all the settings to a file.
	 * @param path Full path of the file to save.
	 */
	public void save (String path) {
		XMLWriter writer = null;
		try {
			writer = new XMLWriter();
			writer.writeStartDocument();
			writer.writeStartElement("okapiQMSettings");
			writer.writeAttributeString("version", "1.0");

			ResourceItem ri;
			for ( int id : resList.keySet() ) {
				ri = resList.get(id);
				writer.writeStartElement("resource");
				writer.writeAttributeString("id", String.valueOf(id));
				writer.writeAttributeString("name", ri.name);
				writer.writeAttributeString("connection", ri.connectionString);
				//TODO: save options, etc
				writer.writeEndElement(); // resource
			}
			
			writer.writeEndElement(); // okapiQMSettings
		}
		finally {
			if ( writer != null ) {
				writer.writeEndDocument();
				writer.close();
			}
		}
	}
	
}
