/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.pipeline;

import java.net.URI;

import net.sf.okapi.common.resource.RawDocument;

/**
 * Common set of methods for a pipeline batch item.
 * A batch item corresponds to the data provided by the caller application to
 * execute one process of all the steps of a pipeline. Most of the time a batch item
 * is composed of a single input file with possibly some output information if
 * some of the steps generate output for each batch item. Some steps require
 * more than one input document per batch item, for example a steps that align the 
 * content of two files will request two input per batch item: the source file and 
 * its corresponding translated file.
 */
public interface IBatchItemContext {

	/**
	 * Gets the filter configuration identifier for a given input document of 
	 * this batch item. 
	 * @param index the zero-based index of the input document.
	 * @return the filter configuration identifier of the given the input document.
	 */
	public String getFilterConfigurationId (int index);
	
	/**
	 * Gets a RawDocument object from the given input document of this batch item.
	 * @param index the zero-based index of the input document.
	 * @return a new RawDocument object from the given input document.
	 */
	public RawDocument getRawDocument (int index);
	
	/**
	 * Gets the output URI for a given input document of this batch item. 
	 * @param index the zero-based index of the input document.
	 * @return the output URI of the given the input document.
	 */
	public URI getOutputURI (int index);
	
	/**
	 * Gets the output encoding for a given input document of this batch item. 
	 * @param index the zero-based index of the input document.
	 * @return the output encoding of the given the input document.
	 */
	public String getOutputEncoding (int index);
	
	/**
	 * Gets the source language for a given input document of this batch item. 
	 * @param index the zero-based index of the input document.
	 * @return the source language of the given the input document.
	 */
	public String getSourceLanguage (int index);
	
	/**
	 * Gets the target language for a given input document of this batch item. 
	 * @param index the zero-based index of the input document.
	 * @return the target language of the given the input document.
	 */
	public String getTargetLanguage (int index);
	
}
