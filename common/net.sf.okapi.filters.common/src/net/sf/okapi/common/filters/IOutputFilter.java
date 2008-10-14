/*===========================================================================*/
/* Copyright (C) 2008 Asgeir Frimannsson, Jim Hargrave, Yves Savourel        */
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

import java.io.OutputStream;

import net.sf.okapi.common.pipeline.IResourceBuilder;

public interface IOutputFilter extends IResourceBuilder {

	/**
	 * Initializes the output.
	 * @param output Stream where to do the output.
	 * @param outputPath Full path of the output.
	 * @param encoding Encoding for the output.
	 * @param targeLanguage Language code of the target.
	 */
	//TODO: Need to work out better init: either path or stream?
	void initialize (OutputStream output,
		String outputPath,
		String encoding,
		String targetLanguage);

	/**
	 * Closes the current output and free any associated resources.
	 */
	void close ();

}
