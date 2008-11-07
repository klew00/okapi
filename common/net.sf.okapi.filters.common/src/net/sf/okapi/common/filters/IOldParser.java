/*===========================================================================*/
/* Copyright (C) 2008 Jim Hargrave                                           */
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

package net.sf.okapi.common.filters;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.sf.okapi.common.resource.IContainable;

/**
 * Parsers open up a data source and break the content down into chunks useful
 * for localization tasks.
 */
public interface IOldParser {

	public static enum ParserTokenType {ENDINPUT, STARTGROUP, ENDGROUP, TRANSUNIT, SKELETON, NONE};
	
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
	 * Get the current {@link Group} or {@link TextUnit} or
	 * {@link SkeletonUnit}.
	 * 
	 * @return {@link Group} or {@link TextUnit} or
	 *         {@link SkeletonUnit} or null if there is no current
	 *         resource. May return null if there is no associated resource for
	 *         the current parsed event.
	 */
	public IContainable getResource();

	/**
	 * Get the next parsed event.
	 * 
	 * @return PARSER_TOKEN_TYPE represents the token event type as defined by the parser
	 */
	public ParserTokenType parseNext();

	/**
	 * Close the source input and cleanup any other opened resources.
	 */
	public void close();

	/**
	 * Cancels the process. The caller can this method in listener event to
	 * cancel the process.
	 */
	void cancel ();
}
