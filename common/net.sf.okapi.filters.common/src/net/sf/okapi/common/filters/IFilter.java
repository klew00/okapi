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

import java.io.Reader;
import java.io.Writer;

import net.sf.okapi.common.IParameters;

public interface IFilter {

	IParameters getParameters ();
	
	void setParameters (IParameters param);
	
	/**
	 * Opens the input to process.
	 * @param reader The reader to use for the input.
	 * @param language Language code of the source.
	 */
	void openInput (Reader reader,
		String language);
	
	/**
	 * Closes the current input.
	 */
	void closeInput ();
	
	/**
	 * Sets the options associated to the output.
	 * @param language Language code of the target.
	 */
	void setOutputOptions (String language);
	
	/**
	 * Opens or creates the output file.
	 * @param writer The writer to use for output.
	 */
	void openOutput (Writer writer);
	
	/**
	 * Closes the current output.
	 */
	void closeOutput ();

	int readItem ();
	
	boolean supports (int feature);
	
	IExtractionItem getSourceItem ();
	
	IExtractionItem getTargetItem ();
	
}
