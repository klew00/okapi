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

package net.sf.okapi.Filter;

import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.IParameters;

/**
 * Provides a common way of reading the localizable content of an arbitrary input.
 * It also offers the option to re-write the input into a new output after 
 * having made modification to the translatable parts. 
 */
public interface IFilter {
	
	/**
	 * Gets the parameters object for the filter.
	 */
	public IParameters getParameters ();
	
	/**
	 * Sets the parameters object for the filter.
	 * @param paramsObject The new parameters object.
	 */
	public void setParameters (IParameters paramsObject);
	
	/**
	 * Closes the current input (either file or string).
	 */
	public void closeInput ();

	/**
	 * Closes the current output (either file or string).
	 * @return If the output was a string the final output string is returned.
	 * Otherwise the method returns null.
	 */
	public String closeOutput ();

	public String[] generateAncillaryData ();

	/**
	 * Gets the name of the current sourceEncoding.
	 * @return The name of the current sourceEncoding.
	 */
	public String getCurrentEncoding ();

	/**
	 * Gets the code of the language currently active in the filter. Note that,
	 * depending on the filter, this language may be neither the input or output
	 * language.
	 * @return The language code.
	 */
	public String getCurrentLanguage ();

	/**
	 * Gets the default datatype identifier for the filter.
	 * @return The string identifying the datatype.
	 */
	public String getDefaultDatatype ();

	/**
	 * Gets the unique identifier for the filter.
	 * @return The string identifying uniquely the filter (for example: "okf_xml").
	 */
	public String getIdentifier ();
	
	/**
	 * Gets the code of the input language.
	 * @return An ISO language code.
	 */
	public String getInputLanguage ();

	/**
	 * Gets the last item read from the input. Any modification of this item may
	 * impact the way the item is written later on.
	 * @return The last item read.
	 */
	public IFilterItem getItem ();

	/**
	 * Gets the value of the last item ID used by the filter. 
	 * @return The last item ID used.
	 */
	public int getLastItemID ();
	
	/**
	 * Sets the value of the item ID for the filter. This is used to synchronize
	 * IDs values when a first filter calls a secondary filter.
	 * @param id New last value.
	 */
	public void setLastItemID (int id);

	public ILocalizationDirectives getLocalizationDirectives ();

	/**
	 * Gets the name of the filter.
	 * @return Name of the filter.
	 */
	public String getName ();

	/**
	 * Gets a short description of the filter.
	 * @return Short description of the filter.
	 */
	public String getDescription ();

	/**
	 * Gets the language code for the current output language.
	 * @return A language code.
	 */
	public String getOutputLanguage ();

	/**
	 * Gets the FilterItem object for the translated item of the current
	 * item. The returned object is meaningless if isTranslated() of the
	 * current source item returns false. 
	 * @return The FilterItem object for the translated item. It can be null.
	 */
	public IFilterItem getTranslatedItem ();

	/**
	 * Initializes the filter.
	 * @param log Log object where to report error and warning.
	 */
	public void initialize (ILog log);

	/**
	 * Opens an input file to be processed by the filter.
	 * @param path The full path to the input file.
	 * @param language The source language of the text to extract.
	 * @param encoding The default sourceEncoding for the file.
	 * @return True if success, false otherwise.
	 */
	public boolean openInputFile (String path,
		String language,
		String encoding);

	public boolean openInputString (String data,
		String language,
		String encoding,
		long offsetInFile);

	public boolean openOutputFile (String path);

	public boolean openOutputString ();

	/**
	 * Query the filter for specific properties.
	 * @param propertyID Identifier of the property to query.
	 * @return True if the filter supports the given property, false if not.
	 */
	public boolean queryProperty (int propertyID);

	/**
	 * Reads the next item from the input.
	 * @return The type of the item read.
	 * That is one of of the values of {@link #FilterItemType FilterItemType}.
	 */
	public int readItem ();

	public void resetInput ();
	
	/**
	 * Saves the filter settings data to a given file.
	 * @param path Full path of the file where to save the data.
	 * @param prefix Optional prefix. If not null, it must be placed in front of any
	 * settings-related additional file(s) the filter saves along with the parameters
	 * file.
	 *
	public boolean saveParameters (String path,
		String prefix);*/

	public void setAncillaryDirectory (String inputRoot,
		String ancillaryRoot);

	/**
	 * Sets the localization directives options for the filter.
	 * @param directives The localization directives options.
	 * @return True if success, false otherwise.
	 */
	public boolean setLocalizationDirectives (ILocalizationDirectives directives);

	/**
	 * Sets the options for the output.
	 * @param language Code of the output language.
	 * @param encoding Encoding name of the output.
	 * @return True if success, false otherwise.
	 */
	public boolean setOutputOptions (String language,
		String encoding);

	/**
	 * Specifies what layer to use for the output, and the codes to use.
	 * @param layerType Type of layer to use. It should be one of the values define
	 * in FilterOutputLayer. 
	 * @param startDocument Codes for the start of the document.
	 * @param endDocument Codes for the end of the document.
	 * @param startCode Codes for the start of an external code section.
	 * @param endCode Codes for the end of an external code section.
	 * @param startInline Codes for the start of an inline code section.
	 * @param endInline Codes for the end of an inline code section.
	 * @param startText Codes for the start of a text section.
	 * @param endText Codes for the end of a text section.
	 * @return True if there is no error, false otherwise.
	 */
	public boolean useOutputLayer (int layerType,
		String startDocument,
		String endDocument,
		String startCode,
		String endCode,
		String startInline,
		String endInline,
		String startText,
		String endText);
	
	/**
	 * Get the information about the output layer used in the filter.
	 * @return An array of string. The strings returned correspond to the parameters
	 * passed to the {@link useOutputLayer(int,String,String,String,String,String,String,String,String) useOutputLayer}
	 * method: 0=layer type, 1=Start of document, 2=End of document, 3=Start of code,
	 * 4=End of code, 5=Start of inline, 6=End of inline, 7=Start of text,
	 * 8=End of text.
	 */
	public String[] getOutputLayer ();

	/**
	 * Writes the last item read to the output. The text of the item may
	 * have been modified between the reading the this writing, but it should
	 * preserve all the inline codes. 
	 */
	public void writeItem ();

}
