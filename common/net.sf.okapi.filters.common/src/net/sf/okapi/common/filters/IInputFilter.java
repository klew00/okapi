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

import java.io.InputStream;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.pipeline.IOutputPipe;

public interface IInputFilter extends IOutputPipe {

	public static final int FEATURE_TEXTBASED    = 1;
	public static final int FEATURE_BILINGUAL    = 2;
	

	/** 
	 * Indicates if the filter supports a given feature.
	 * @param feature Code of the feature to query. The value must be one of the
	 * FEATURE_* values.
	 * @return True if the feature is supported, false otherwise.
	 */
	boolean supports (int feature);
	
	/**
	 * Initializes the input to process.
	 * @param reader The reader to use for the input.
	 * @param name The name associated with the input (e.g. file name).
	 * @param inputPath The full path of the input.
	 * @param filterSettings The filter settings.
	 * @param params The parameters to use. 
	 * @param encoding The default encoding for the input.
	 * @param sourceLanguage Language code of the source.
	 * @param targeLanguage Language code of the target (can be null 
	 * in monolingual input).
	 */
	//TODO: Need to resolve: either path or stream
	void initialize (InputStream input,
		String inputPath,
		String name,
		String filterSettings,
		String encoding,
		String sourceLanguage,
		String targetLanguage);
	
	/**
	 * Gets the current parameters object for the filter.
	 */
	IParameters getParameters ();
	
	/**
	 * Closes the current input and free any associated resources.
	 */
	void close ();

	/**
	 * Process the input and feed it to the output pipe.
	 */
	void process ();
	
	/**
	 * Cancels the process. The caller can this method in listener event to
	 * cancel the process. The actual cancellation may occur next time the process
	 * method send a resource to the output (instead of immediately).
	 */
	void cancel ();
}
