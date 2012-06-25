/*===========================================================================
  Copyright (C) 2009-2011 by the Okapi Framework contributors
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

package net.sf.okapi.common.pipeline.annotations;

/**
 * Types of the runtime parameters for steps.
 */
public enum StepParameterType {

	/**
	 * RawDocument object of the main input document.
	 */
	INPUT_RAWDOC,
	
	/**
	 * RawDocument object of the second input document.
	 */
	SECOND_INPUT_RAWDOC,
	
	/**
	 * RawDocument object of the third input document.
	 */
	THIRD_INPUT_RAWDOC,
	
	/**
	 * URI of the main input document.
	 */
	INPUT_URI,
	
	/**
	 * URI of the main output document.
	 */
	OUTPUT_URI,
	
	/**
	 * Source locale.
	 */
	SOURCE_LOCALE,

	/**
	 * Target locale.
	 */
	TARGET_LOCALE,

	/**
	 * List of target locales.
	 */
	TARGET_LOCALES,

	/**
	 * Filter configuration identifier for the main input document.
	 */
	FILTER_CONFIGURATION_ID,
	
	/**
	 * Filter configuration mapper.
	 */
	FILTER_CONFIGURATION_MAPPER,
	
	/**
	 * Output encoding of the main output document.
	 */
	OUTPUT_ENCODING,
	
	/**
	 * Root directory for the batch.
	 */
	ROOT_DIRECTORY,
	
	/**
	 * UI parent object of the calling application (shell, main window, etc.)
	 */
	UI_PARENT,

	/**
	 * Number of input documents in the current batch.
	 */
	BATCH_INPUT_COUNT,
	
	/**
	 * Root directory of the first set of input files.
	 */
	INPUT_ROOT_DIRECTORY
	
}
