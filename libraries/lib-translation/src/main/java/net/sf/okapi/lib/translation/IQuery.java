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

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.resource.TextFragment;

/**
 * Provides the methods common to all query engines of translation resources 
 * that can be queried for translating text units. 
 */
public interface IQuery {
	
	/**
	 * Gets the name of the connector.
	 * @return the name of the connector.
	 */
	public String getName ();

	/**
	 * Sets the source and target languages for this query engine.
	 * @param sourceLang Code of the source language.
	 * @param targetLang Code of the target language.
	 */
	public void setLanguages (String sourceLang,
		String targetLang);
	
	/**
	 * Gets the current source language for this query engine.
	 * @return Code of the source language.
	 */
	public String getSourceLanguage ();
	
	/**
	 * Gets the current target language for this query engine.
	 * @return Code of the target language.
	 */
	public String getTargetLanguage ();
	
	/**
	 * Sets an attribute for this query engine.
	 * @param name name of the attribute.
	 * @param value Value of the attribute.
	 */
	public void setAttribute (String name,
		String value);
	
	/**
	 * Remove a given attribute from this query engine.
	 * @param name The name of the attribute to remove.
	 */
	public void removeAttribute (String name);

	/**
	 * Sets the parameters for opening and querying this connector.
	 * @param params the parameters to set.
	 */
	public void setParameters (IParameters params);
	
	/**
	 * Gets the current parameters of this connector.
	 * @return the current parameters of this connector
	 * or null if no parameters are used.
	 */
	public IParameters getParameters ();
	
	/**
	 * Opens this query engine.
	 */
	public void open ();
	
	/**
	 * Closes this conector.
	 */
	public void close ();
	
	/**
	 * Starts a query for a give plain text. 
	 * @param plainText text to query.
	 * @return The number of hits for the given query.
	 */
	public int query (String plainText);
	
	/**
	 * Starts a query for a given text.
	 * @param text The text to query.
	 * @return The number of hits for the given query.
	 */
	public int query (TextFragment text);
	
	/**
	 * Indicates of there is a hit available.
	 * @return True if a hit is available, false if not.
	 */
	public boolean hasNext ();

	/**
	 * Gets the next hit for the last query.
	 * @return A QueryResult object that holds the source and target text of
	 * the hit, or null if there is no more hit. 
	 */
	public QueryResult next ();

}
