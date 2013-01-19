/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

package net.sf.okapi.common.pipelinedriver;

import java.net.URI;

import net.sf.okapi.common.resource.RawDocument;

/**
 * Structure to hold the data associated to a document in a pipeline.
 */
public class DocumentData {

	/**
	 * The input {@link RawDocument} itself.
	 */
	public RawDocument rawDocument;
	
	/**
	 * The URI for the output.
	 */
	public URI outputURI;
	
	/**
	 * The encoding for the output.
	 */
	public String outputEncoding;

}
