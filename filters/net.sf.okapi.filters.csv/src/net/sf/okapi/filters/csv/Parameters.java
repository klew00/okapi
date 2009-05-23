/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.filters.csv;

/**
 * CSV Filter parameters 
 */

public class Parameters extends net.sf.okapi.filters.plaintext.Parameters {
	
	/**
	 * @see detectColumnsMode
	 */
	public static int DETECT_COLUMNS_NONE = 0;
	public static int DETECT_COLUMNS_FIELD_NAMES = 1;
	public static int DETECT_COLUMNS_FIXED_NUMBER = 2;
	
	/**
	 * @see sendHeaderMode
	 */
	public static int SEND_HEADER_NONE = 0;
	public static int SEND_HEADER_FIELD_NAMES_ONLY = 1;
	public static int SEND_HEADER_ALL = 2;
	
	/**
	 * @see trimMode
	 */
	public static int TRIM_NONE = 0;
	public static int TRIM_NONQUALIFIED_ONLY = 1;
	public static int TRIM_ALL = 2;
	
	/**
	 * Specifies whether empty TUs are created for empty field values.<p>
	 * This option is active only if detectColumnsMode = DETECT_COLUMNS_NONE, otherwise empty TUs are always created.<p>
	 * Default: true (create empty TUs)
	 */
	public boolean sendEmptyFields = true;
	
	/**
	 * Index of the raw (1-based) containing filed names (column captions).<p>
	 * Default: 2
	 */
	public int fieldNamesRaw = 1;
	
	/**
	 * Index of the raw (1-based) where actual data start after the header.<p>
	 * Default: 2
	 */
	public int valuesStartRaw = 2;
	
	/**
	 * The filter can detect number of columns in the input. This option specifies the way of columns number detection:
	 * <li>DETECT_COLUMNS_NONE = 0 - no detection is performed, if different raws contain different number of values, then different 
	 * number of TUs will be sent for different raws  
	 * <li>DETECT_COLUMNS_FIELD_NAMES = 1 - number of columns is determined by the number of field names listed in the raw 
	 * specified by the fieldNamesRaw parameter. 
	 * <li>DETECT_COLUMNS_FIXED_NUMBER = 2 - number of columns is explicitly specified by the numColumns parameter.<p>
	 * Default: DETECT_COLUMNS_NONE
	 */
	public int detectColumnsMode = DETECT_COLUMNS_NONE;
	
	/**
	 * Number of columns in the input. This option is active only if detectColumnsMode = DETECT_COLUMNS_FIXED_NUMBER.<p>
	 * Extra columns are dropped, empty TUs are created for missing columns.<p>
	 */
	public int numColumns = 1;
	
	/**
	 * If there are one or more lines containing description of the data, names of fields etc., 
	 * and actual data don't start in the first line, then such first lines are considered a header, and this option specifies how to handle them:
	 * <li>SEND_HEADER_NONE = 0 - none of the header lines are sent as text units
	 * <li>SEND_HEADER_FIELD_NAMES_ONLY = 1 - only the values in the line specified by fieldNamesLineNum are sent as text units
	 * <li>SEND_HEADER_ALL = 2 - values in all header lines are sent as text units
	 * @see valuesStartLineNum
	 * @see fieldNamesLineNum
	 */
	public int sendHeaderMode = SEND_HEADER_FIELD_NAMES_ONLY;
	
	/**
	 * Specifies how field values are trimmed of spaces:
	 * <li>TRIM_NONE = 0 - field values are not trimmed
	 * <li>TRIM_NONQUOTED_ONLY = 1 - only non-qualified field values are trimmed, leading and trailing spaces remain in qualified fields
	 * <li>TRIM_ALL = 2 - both non-qualified and qualified field values are trimmed of leading and trailing spaces.
	 * Default: TRIM_NONQUALIFIED_ONLY
	 * @see textQualifier
	 */
	public int trimMode = TRIM_NONQUALIFIED_ONLY;
	
	/**
	 * Symbol or a string separating fields in a raw. <p>
	 * Default: , (comma)
	 */
	public String fieldDelimiter = ",";
	
	/** 
	 * Symbol or a string before and after field value to allow special characters inside the field. 
	 * For instance, this field will not be broken into parts: "Field, containing comma, \", "" and \n".
	 * The qualifiers are not included in translation units.<p>  
	 * Default: " (quotation mark)
	 */ 
	public String textQualifier = "\"";
	
	/** 
	 * Indicates which columns contain ID. Can be represented by one of the following string types:
	 *<li>"1" - index (1-based) of the column, containing ID
	 *<li>"ID" - name of the column, containing ID
	 *<li>"1,2,5" - comma-delimited list (1-based) of indexes of the columns, containing ID
	 *<li>"ID,ParentID" - comma-delimited list of names of the columns, containing ID.<p>
	 * The values acquired from multiple columns are sent in one TU delimited with compoundTuDelimiter. 
	 */
	public String idColumns = "";			
	
	/** 
	 * Indicates which columns contain source text. Can be represented by one of the following string types:
	 *<li>"1" - index (1-based) of the column, containing source text
	 *<li>"ID" - name of the column, containing source text
	 *<li>"1,2,5" - comma-delimited list (1-based) of indexes of the columns, containing source text
	 *<li>"ID,ParentID" - comma-delimited list of names of the columns, containing source text.<p>
	 * The values acquired from multiple columns are sent in one TU delimited with compoundTuDelimiter. 
	 */
	public String sourceColumns = "";		
	
	/** 
	 * Indicates which columns contain target text. Can be represented by one of the following string types:
	 *<li>"1" - index (1-based) of the column, containing target text
	 *<li>"ID" - name of the column, containing target text
	 *<li>"1,2,5" - comma-delimited list (1-based) of indexes of the columns, containing target text
	 *<li>"ID,ParentID" - comma-delimited list of names of the columns, containing target text.<p>
	 * The values acquired from multiple columns are sent in one TU delimited with compoundTuDelimiter. 
	 */
	public String targetColumns = "";		
	
	/** 
	 * Indicates which columns contain comments. Can be represented by one of the following string types:
	 *<li>"1" - index (1-based) of the column, containing a comment
	 *<li>"ID" - name of the column, containing a comment
	 *<li>"1,2,5" - comma-delimited list (1-based) of indexes of the columns, containing comments
	 *<li>"ID,ParentID" - comma-delimited list of names of the columns, containing comments.<p>
	 * The values acquired from multiple columns are sent in one TU delimited with compoundTuDelimiter. 
	 */
	public String commentColumns = "";
	
	/**
	 * If multiple columns were specified for ID, source, target, or comment, 
	 * the values acquired from multiple columns will be combined in one TU delimited with this value.<p>
	 * Default: LF (line feed)
	 */
	public String compoundTuDelimiter = "\n";
	
//----------------------------------------------------------------------------------------------------------------------------	
	
	public Parameters() {
		
		super();		
		
		reset();
		toString(); // fill the list
	}

	public void reset() {
		
		super.reset();
		
		// All parameters are set to defaults here
		sendEmptyFields = true;
		fieldNamesRaw = 1;
		valuesStartRaw = 2;
		detectColumnsMode = DETECT_COLUMNS_NONE;
		numColumns = 1;
		sendHeaderMode = SEND_HEADER_FIELD_NAMES_ONLY;
		trimMode = TRIM_NONQUALIFIED_ONLY;
		fieldDelimiter = ",";
		textQualifier = "\"";
		idColumns = "";
		sourceColumns = "";
		targetColumns = "";
		commentColumns = "";
		compoundTuDelimiter = "\n";		
	}

	public void fromString(String data) {
		
		reset();
		
		super.fromString(data);
		buffer.fromString(data);
		
		// All parameters are retrieved here		
		sendEmptyFields = buffer.getBoolean("sendEmptyFields", true);
		fieldNamesRaw = buffer.getInteger("fieldNamesLineNum", 1);
		valuesStartRaw = buffer.getInteger("valuesStartLineNum", 2);
		detectColumnsMode = buffer.getInteger("detectColumnsMode", DETECT_COLUMNS_NONE);
		numColumns = buffer.getInteger("numColumns", 1);
		sendHeaderMode = buffer.getInteger("sendHeaderMode", SEND_HEADER_FIELD_NAMES_ONLY);
		trimMode = buffer.getInteger("trimMode", TRIM_NONQUALIFIED_ONLY);		
		fieldDelimiter = buffer.getString("fieldDelimiter", ",");
		textQualifier = buffer.getString("textQualifier", "\"");
		idColumns = buffer.getString("idColumns", "");
		sourceColumns = buffer.getString("sourceColumns", "");
		targetColumns = buffer.getString("targetColumns", "");
		commentColumns = buffer.getString("commentColumns", "");
		compoundTuDelimiter = buffer.getString("compoundTuDelimiter", "\n");
	}
	
	@Override
	public String toString () {
		
		buffer.reset();
		
		String st = super.toString();
		
		// All parameters are set here		
		buffer.setBoolean("sendEmptyFields", sendEmptyFields);
		buffer.setInteger("fieldNamesLineNum", fieldNamesRaw);
		buffer.setInteger("valuesStartLineNum", valuesStartRaw);
		buffer.setInteger("detectColumnsMode", detectColumnsMode);
		buffer.setInteger("numColumns", numColumns);
		buffer.setInteger("sendHeaderMode", sendHeaderMode);
		buffer.setInteger("trimMode", trimMode);		
		buffer.setString("fieldDelimiter", fieldDelimiter);
		buffer.setString("textQualifier", textQualifier);
		buffer.setString("idColumns", idColumns);
		buffer.setString("sourceColumns", sourceColumns);
		buffer.setString("targetColumns", targetColumns);
		buffer.setString("commentColumns", commentColumns);
		buffer.setString("compoundTuDelimiter", compoundTuDelimiter);
		
		return st + buffer.toString();
	}
	
}
