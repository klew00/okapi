/*===========================================================================*/
/* Copyright (C) 2007 ENLASO Corporation, Okapi Development Team             */
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

import net.sf.okapi.Library.Base.*;

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
	 * Closes the current input (either file or string).
	 */
	public void closeInput ();

	/**
	 * Closes the current output (either file or string).
	 * @return If the output was a string the final output string is returned.
	 * Otherwise the method returns null.
	 */
	public String closeOutput ();

	public void generateAncillaryData (String p_sId, //out
		String p_sType,//out
		String p_sPath);//out

	/**
	 * Gets the name of the current encoding.
	 * @return The name of the current encoding.
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
	 * @param p_nID New last value.
	 */
	public void setLastItemID (int p_nID);

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
	 * Gets a string holding all the parameter option.
	 * @return String with all parameters.
	 */
	public String getOptions ();
	
	/**
	 * Gets the value for the option specified by p_sName. 
	 * @param p_sName The name of the option to retrieve.
	 * @return Value of the option retrieved.
	 */
	public String getOption (String p_sName);
	
	/**
	 * Gets the current full string settings of the filter. It includes the filter
	 * identifier and parameters information.
	 * @return The filter settings string.
	 */
	public String getSettingsString ();

	/**
	 * Gets the FilterItem object for the translated item of the current
	 * item. The returned object is meaningless if isTranslated() of the
	 * current source item returns false. 
	 * @return The FilterItem object for the translated item. It can be null.
	 */
	public IFilterItem getTranslatedItem ();

	/**
	 * Initializes the filter.
	 * @param p_Log Log object where to report error and warning.
	 */
	public void initialize (ILog p_Log);

	public boolean loadSettings (String p_sFilterSettings,
		boolean p_bIgnoreErrors);

	/**
	 * Opens an input file to be processed by the filter.
	 * @param p_sPath The full path to the input file.
	 * @param p_sLanguage The source language of the text to extract.
	 * @param p_sEncoding The default encoding for the file.
	 * @return True if success, false otherwise.
	 */
	public boolean openInputFile (String p_sPath,
		String p_sLanguage,
		String p_sEncoding);

	public boolean openInputString (String p_sInput,
		String p_sLanguage,
		String p_sEncoding,
		long p_lOffsetInFile);

	public boolean openOutputFile (String p_sPath);

	public boolean openOutputString ();

	/**
	 * Query the filter for specific properties.
	 * @param p_nProperty Identifier of the property to query.
	 * @return True if the filter supports the given property, false if not.
	 */
	public boolean queryProperty (int p_nProperty);

	/**
	 * Reads the next item from the input.
	 * @return The type of the item read.
	 * That is one of of the values of {@link #FilterItemType FilterItemType}.
	 */
	public int readItem ();

	public void resetInput ();
	
	/**
	 * Saves the filter settings data to a given file.
	 * @param p_sPath Full path of the file where to save the data.
	 * @param p_sPrefix Optional prefix. If not null, it will be placed in front of any
	 * settings-related additional file(s) the filter saves along with the parameters file.
	 */
	public void saveSettingsAs (String p_sPath,
		String p_sPrefix);

	public void setAncillaryDirectory (String p_sInputRoot,
		String p_sAncillaryRoot);

	/**
	 * Sets the localization directives options for the filter.
	 * @param p_LD The localization directives options.
	 * @return True if success, false otherwise.
	 */
	public boolean setLocalizationDirectives (ILocalizationDirectives p_LD);

	/**
	 * Sets the options for the output.
	 * @param p_sLanguage Code of the output language.
	 * @param p_sEncoding Encoding name of the output.
	 * @return True if success, false otherwise.
	 */
	public boolean setOutputOptions (String p_sLanguage,
		String p_sEncoding);

	/**
	 * Sets the options for the filter.
	 * @param p_sValue The list of options.
	 */
	public void setOptions (String p_sValue);
	
	/**
	 * Sets a given option for the filter.
	 * @param p_sName The name of the option.
	 * @param p_sValue The value for this option.
	 */
	public void setOption (String p_sName,
		String p_sValue);
	
	/**
	 * Specifies what layer to use for the output, and the codes to use.
	 * @param p_nLayer Type of layer to use. It should be one of the values define
	 * in FilterOutputLayer. 
	 * @param p_sStartDocument Codes for the start of the document.
	 * @param p_sEndDocument Codes for the end of the document.
	 * @param p_sStartCode Codes for the start of an external code section.
	 * @param p_sEndCode Codes for the end of an external code section.
	 * @param p_sStartInline Codes for the start of an inline code section.
	 * @param p_sEndInline Codes for the end of an inline code section.
	 * @param p_sStartText Codes for the start of a text section.
	 * @param p_sEndText Codes for the end of a text section.
	 * @return True if there is no error, false otherwise.
	 */
	public boolean useOutputLayer (int p_nLayer,
		String p_sStartDocument,
		String p_sEndDocument,
		String p_sStartCode,
		String p_sEndCode,
		String p_sStartInline,
		String p_sEndInline,
		String p_sStartText,
		String p_sEndText);
	
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
