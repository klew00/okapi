/* Copyright (C) 2008 Jim Hargrave
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA              */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.filters.html;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftTagTypes;
import net.htmlparser.jericho.PHPTagTypes;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.sf.okapi.common.filters.IParser;
import net.sf.okapi.common.resource.IContainable;
import static net.sf.okapi.filters.html.ElementExtractionRule.EXTRACTION_RULE_TYPE.*;
import static net.sf.okapi.filters.html.ConditionalAttribute.CONDITIONAL_ATTRIBUTE_TYPE.*;


public class HtmlParser implements IParser {
	private Source htmlDocument;
	private Iterator<Segment> nodeIterator;
	private boolean first = true;

	public HtmlParser() {
	}

	public void close() {
	}

	public void open(CharSequence input) {
		htmlDocument = new Source(input);
		initialize();
	}

	public void open(InputStream input) {
		try {
			htmlDocument = new Source(input);
		} catch (IOException e) {
			// TODO Wrap unchecked exception
			throw new RuntimeException(e);
		}
		initialize();
	}

	public void open(URL input) {
		try {
			htmlDocument = new Source(input);
		} catch (IOException e) {
			// TODO Wrap unchecked exception
			throw new RuntimeException(e);
		}
		initialize();
	}

	private void initialize() {
		// Segment iterator
		nodeIterator = htmlDocument.getNodeIterator();

		// register custom tags
		// TODO: Make a parameter, How to create custom tags in bulk?
		MicrosoftTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags, otherwise
											// they override processing
											// instructions
		MasonTagTypes.register();
	}

	public IContainable getResource() {
		return null;
	}

	public ParserTokenType parseNext() {
		boolean endExtraction;
		if (nodeIterator.hasNext()) {
			Segment segment = nodeIterator.next();
			endExtraction = false;
			do {

			} while (endExtraction);
		} else {
			// end of parsing finalize resource construction
		}
		return null;
	}	
}
