/*******************************************************************************
 * Copyright 2008 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sf.okapi.common.filters;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.sf.okapi.common.resource.IBaseResource;
import net.sf.okapi.common.resource.IExtractionItem;
import net.sf.okapi.common.resource.IGroupResource;

/**
 * Parsers open up a data source and break the content down into chunks useful
 * for localization tasks.
 */
public interface IParser {

	public static enum PARSER_TOKEN_TYPE {ENDINPUT, STARTGROUP, ENDGROUP, TRANSUNIT, SKELETON};
	
	/**
	 * Open a streamed byte source and prepare for parsing.
	 * 
	 * @param input
	 *            streaming input source.
	 */
	public void open(InputStream input);

	/**
	 * Open an in memory Unicode source and prepare for parsing.
	 * 
	 * @param input
	 *            in memory input source
	 * @throws IOException
	 */
	public void open(CharSequence input);

	/**
	 * Open a URL-based source and prepare for parsing.
	 * 
	 * @param input
	 *            URL input source
	 * @throws IOException
	 */
	public void open(URL input);

	/**
	 * Get the current {@link IGroupResource} or {@link IExtractionItem} or
	 * {@link ISkeletonResource}.
	 * 
	 * @return {@link IGroupResource} or {@link IExtractionItem} or
	 *         {@link ISkeletonResource} or null if there is no current
	 *         resource. May return null if there is no associated resource for
	 *         the current parsed event.
	 */
	public IBaseResource getResource();

	/**
	 * Get the next parsed event.
	 * 
	 * @return PARSER_TOKEN_TYPE represents the token event type as defined by the parser
	 */
	public PARSER_TOKEN_TYPE parseNext();

	/**
	 * Close the source input and cleanup any other opened resources.
	 */
	public void close();
}
