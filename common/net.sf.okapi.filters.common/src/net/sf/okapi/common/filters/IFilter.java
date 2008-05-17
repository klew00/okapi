/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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

package net.sf.okapi.common.filters;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import net.sf.okapi.common.IParameters;

public interface IFilter {

	public static final int RESULT_ERROR         = 0;
	public static final int RESULT_CANCELLATION  = 1;
	public static final int RESULT_END           = 2;
	public static final int RESULT_ITEM          = 3;
	public static final int RESULT_DATA          = 4;
	public static final int RESULT_STARTGROUP    = 5;
	public static final int RESULT_ENGROUP       = 6;
	
	public static final int FEATURE_TEXTBASED    = 1;
	public static final int FEATURE_OUTPUT       = 2;
	public static final int FEATURE_BILINGUAL    = 3;
	

	/** Cancellation 
	 * Indicates if the filter supports a given feature.
	 * @param feature Code of the feature to query. The value must be one of the
	 * FEATURE_* values.
	 * @return True if the feature is supported, false otherwise.
	 */
	boolean supports (int feature);
	
	/**
	 * Gets the current parameters for the filter.
	 * @return An IParameters object of the current parameters.
	 */
	IParameters getParameters ();
	
	/**
	 * Sets the parameters for the filter. Parameters must be set before opening the input.
	 * @param param The new IParameters object to use.
	 */
	void setParameters (IParameters param);
	
	/**
	 * Opens the input to process.
	 * @param reader The reader to use for the input.
	 * @param language Language code of the source.
	 * @param encoding The default encoding for the input.
	 */
	void openInput (InputStream input,
		String language,
		String encoding)
		throws Exception; //TODO: Specific exception?
	
	/**
	 * Closes the current input.
	 */
	void closeInput ()
		throws Exception; //TODO: Specific exception?
	
	/**
	 * Sets the options associated to the output.
	 * @param language Language code of the target.
	 * @param encoding Name of the encoding to use for the output.
	 */
	//TODO: How to deal with encoding? The one of the riter should be the same as the one used internally for escapes or not necessarily?
	void setOutputOptions (String language, String encoding);
	
	/**
	 * Opens or creates the output file.
	 * @param writer The writer to use for output.
	 */
	void openOutput (Writer writer)
		throws Exception; //TODO: Specific exception?
	
	/**
	 * Closes the current output.
	 */
	void closeOutput ()
		throws Exception; //TODO: Specific exception?

	/**
	 * Reads the next item in the input.
	 * If the result is {@link #RESULT_ITEM} or {@link #RESULT_STARTGROUP} you can use
	 * {@link #getSourceItem()} to get extracted data:
	 * <p>For {@link #RESULT_ITEM}: all methods are relevant.
	 * <p>For {@link #RESULT_STARTGROUP}: only {@link IExtractionItem#getResname()},
	 * {@link IExtractionItem#getRestype()} re applicable.
	 * <p>If the result is either {@link #RESULT_ERROR}, {@link #RESULT_CANCELLATION},
	 * or {@link #RESULT_END}, you should not call {@link #readItem()} again. 
	 * @return One of the RESULT_* values.
	 */
	int readItem ();
	
	/**
	 * Writes to the output the last item read from the input.
	 */
	void writeItem ();
	
	/**
	 * Gets the last source item read from the input.
	 * @return The IExtractionItem object for the last source item read. 
	 */
	IExtractionItem getSourceItem ();
	
	/**Gets the last target item read from the input. Not all filters support bilingual input
	 * @return The IExtractionItem object for the last target item read, or null, if there 
	 * is no corresponding target item for the last source item. 
	 */
	IExtractionItem getTargetItem ();
	
}
