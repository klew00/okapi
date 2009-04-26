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

package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.sf.okapi.common.resource.TextFragment.TagType;

/**
 * Represents an abstracted in-line code used in a TextFragment object.
 * For example, a <code>&lt;b></code> tag in an HTML paragraph.
 */
public class Code {

	public static final String TYPE_BOLD = "bold";
	public static final String TYPE_ITALIC = "italic";
	public static final String TYPE_UNDERLINED = "underlined";
	public static final String TYPE_LB = "lb"; // Line-break
	public static final String TYPE_LINK = "link";
	public static final String TYPE_IMAGE = "image";
	public static final String TYPE_COMMENT = "comment";
	public static final String TYPE_XML_PROCESSING_INSTRUCTION = "processing-instruction";
	
	/** Initial capacity for creating annotations maps.
	 * Keeping it small to save space.
	 */
	private static final int ANNOTATIONS_INITCAP = 2;
	
	/**
	 * Indicates that this code has one reference or more in its data part.
	 */
	protected static final int HASREF       = 0x01;
	
	/**
	 * Indicates that this code can be duplicated in the text.
	 */
	protected static final int CLONEABLE    = 0x02;
	
	/**
	 * Indicates that this code can be removed from the text.
	 */
	protected static final int DELETEABLE   = 0x04;
	
	protected TagType tagType;
	protected int id;
	protected String type;
	protected String data;
	protected String outerData;
	protected int flag;
	protected LinkedHashMap<String, InlineAnnotation> annotations;

	/**
	 * Helper method to convert a list of codes into a string.
	 * @param list the list of the codes to flatten into a string.
	 * @return the string with all the codes.
	 * @see #stringToCodes(String)
	 */
	public static String codesToString (List<Code> list) {
		StringBuilder tmp = new StringBuilder();
		for ( Code code : list ) {
			tmp.append(String.format("%s\u009C%d\u009C%s\u009C%s\u009C%s\u009C%s\u009D",
				code.tagType, code.id, code.type, code.data,
				code.flag, annotationsToString(code.annotations)));
		}
		return tmp.toString();
	}
	
	/**
	 * Gets a string storage representation of the annotations of a Code.
	 * @param map the list of annotations.
	 * @return the storage string.
	 */
	private static String annotationsToString (LinkedHashMap<String, InlineAnnotation> map) {
		if (( map == null ) || map.isEmpty() ) return "";
		StringBuilder tmp = new StringBuilder();
		InlineAnnotation annotation;
		for ( String key : map.keySet() ) {
			tmp.append(key+"\u009E");
			annotation = map.get(key);
			tmp.append((annotation==null) ? "" : annotation.toString());
			tmp.append('\u009F');
		}
		return tmp.toString();
	}
	
	/**
	 * Helper method to convert a storage string into a list of codes.
	 * @param data the storage string to convert.
	 * @return a list of the codes in the storage string.
	 * @see #codesToString(List)
	 */
	public static List<Code> stringToCodes (String data) {
		ArrayList<Code> list = new ArrayList<Code>();
		if ( data != null ) {
			String[] tmpCodes = data.split("\u009D");
			for ( String tmp : tmpCodes ) {
				if ( tmp.length() == 0 ) continue;
				String[] tmpFields = tmp.split("\u009C");
				Code code = new Code(TagType.valueOf(tmpFields[0]), tmpFields[2], tmpFields[3]);
				code.id = Integer.valueOf(tmpFields[1]);
				code.flag = Integer.valueOf(tmpFields[4]);
				if ( tmpFields.length > 5 ) {
					code.annotations = stringToAnnotations(tmpFields[5]);
				}
				else code.annotations = null;
				list.add(code);
			}
		}
		return list;
	}
	
	private static LinkedHashMap<String, InlineAnnotation> stringToAnnotations (String data) {
		//TODO: ISSUE here: the annotations of the closing code should point to the same as the opening code!				
		if (( data == null ) || ( data.length() == 0 )) return null;
		// Create the map with low initial capacity
		LinkedHashMap<String, InlineAnnotation> map = new LinkedHashMap<String, InlineAnnotation>(ANNOTATIONS_INITCAP);
		InlineAnnotation annotation;
		String[] tmpEntries = data.split("\u009F");
		for ( String tmp : tmpEntries ) {
			if ( tmp.length() == 0 ) continue;
			String[] tmpPair = tmp.split("\u009E");
			if ( tmpPair.length > 1 ) {
				annotation = new InlineAnnotation();
				annotation.fromString(tmpPair[1]);
			}
			else annotation = null;
			map.put(tmpPair[0], annotation);
		}
		return map;
	}
	
	/**
	 * Creates a new code. By default codes can be both deleted and cloned.
	 * @param tagType the tag type.
	 * @param type the type of code (e.g. the name of the tag). The type must be
	 * exactly the same between the opening and closing codes.
	 * @param data the content of the code.
	 */
	public Code (TagType tagType,
		String type,
		String data)
	{
		id = -1;
		this.tagType = tagType;
		flag = 0; // Default: not cloneable, not deleteable
		// Never let the type to be null
		if ( type == null ) this.type = "null";
		else this.type = type;
		// Use "" for null data
		if ( data == null ) this.data = "";
		this.data = data;
	}

	/**
	 * Creates a new code with empty data.
	 * @param tagType the tag type.
	 * @param type the type of code (e.g. the name of the tag).
	 */
	public Code (TagType tagType,
		String type)
	{
		this(tagType, type, "");
	}
	
	/**
	 * Creates a new code with a null type, and empty data. You must set the type later on.	 
	 * @param type the type of code (e.g. the name of the tag).
	 */
	public Code (String type) {
		this(null, type, "");
	}
	
	/**
	 * Appends data to the current code data
	 * @param data the data to append.
	 */
	public void append (String data) {
		// TODO: Make this.data a StringBuilder for speed? But this method is probably not used often
		this.data += data;
	}
	
	/**
	 * Appends a reference marker to the current code data.
	 * @param id the identifier of the referent resource.
	 */
	public void appendReference (String id) {
		this.data += TextFragment.makeRefMarker(id);
		setReferenceFlag(true);
	}
	
	/**
	 * Appends a reference marker for a given property to the current code data.
	 * @param id the identifier of the referent resource where the property is located.
	 * @param propertyName the name of the property.
	 */
	public void appendReference (String id,
		String propertyName)
	{
		this.data += TextFragment.makeRefMarker(id, propertyName);
		setReferenceFlag(true);
	}
	
	/**
	 * Gets the string representation of this code: its data.
	 * @return the raw data of this code.
	 */
	@Override
	public String toString () {
		return data;
	}

	/**
	 * Indicates if this code has any type of annotation.
	 * @return true when this code has any type of annotation.
	 */
	public boolean hasAnnotation () {
		return (( annotations != null ) && ( annotations.size() > 0 ));
	}

	/**
	 * Indicates if this code has a given type of annotation.
	 * @return the type of annotation for this code or and empty string.
	 */
	public boolean hasAnnotation (String type) {
		if ( annotations == null ) return false;
		return annotations.containsKey(type);
	}
	
	/**
	 * Indicates if this code has data.
	 * @return true if this code has data. 
	 */
	public boolean hasData () {
		return (data.length()>0);
	}
	
	/**
	 * Clone the code. Note that this method does not check if this code can be
	 * duplicated or not. Use {@link #isCloneable()} to check.
	 * @return a copy of the object.
	 */
	@Override
	public Code clone () {
		Code newCode = new Code(tagType, type, data);
		newCode.id = id;
		newCode.outerData = outerData;
		newCode.flag = flag;
		// Clone the annotations
		if ( annotations != null ) {
			InlineAnnotation annot;
			newCode.annotations = new LinkedHashMap<String, InlineAnnotation>();
			for ( String type : annotations.keySet() ) {
				annot = annotations.get(type);
				if ( annot == null ) newCode.annotations.put(type, null);
				else newCode.annotations.put(type, annot.clone());
			}
		}
		return newCode;
	}
	
	/**
	 * Gets the tag type of the code.
	 * @return the tag type of the code.
	 */
	public TagType getTagType () {
		return tagType;
	}
	
	/**
	 * Sets the tag type for the code.
	 * @param value the new tag type to apply. The value must be one of the
	 * values of {@link TagType}.
	 */
	public void setTagType (TagType value) {
		tagType = value;
	}
	
	/**
	 * Gets the abstract type for the code. For example: "bold".
	 * @return the abstract type of the code.
	 */
	public String getType () {
		return type;
	}
	
	/**
	 * Sets the abstract type of the code. This member is used to match up
	 * together opening and closing codes.
	 * @param value the new abstract type of the code.
	 */
	public void setType (String value) {
		if ( value == null ) type = "null";
		else type = value;
	}
	
	/**
	 * Gets the raw data for the code. This does not build a string
	 * with sub-flows content.
	 * @return the raw data of the code.
	 */
	public String getData () {
		return data;
	}
	
	/**
	 * Sets the raw data for the code.
	 * @param value the new raw data of the code.
	 */
	public void setData (String value) {
		data = value;
	}
	
	/**
	 * Gets the identifier of the code.
	 * @return The identifier of the code.
	 */
	public int getId () {
		return id;
	}

	/**
	 * Sets the identifier of the code. Be aware that identifiers for in-line codes are
	 * generated automatically when used in {@link TextFragment} and overriding the
	 * values may result in codes with duplicate IDs.
	 * @param value the new identifier value to be applied.
	 */
	public void setId (int value) {
		id = value;
	}

	/**
	 * Indicates whether the code has at least one sub-flow part. A sub-flow is a text
	 * unit contained within the code. For example: the text of the ALT attribute in the
	 * HTML IMG element: If the IMG tag is a code, the value of ALT is one of its
	 * sub-flows.
	 * @return true if the code has at least one sub-flow.
	 */
	public boolean hasReference () {
		return ((flag & HASREF) == HASREF);
	}
	
	/**
	 * Sets the flag that indicates if this code has a reference (sub-flow) or not. 
	 * @param value the new value to apply.
	 */
	public void setReferenceFlag (boolean value) {
		if ( value ) flag |= HASREF;
		else flag &= ~HASREF;
	}

	/**
	 * Sets the complete data for this in-line code (inner data and outer).
	 * Outer data is used for format that implements in-line codes like TMX or XLIFF.
	 * For example "&lt;ph id='1'>code&lt;/ph>" is the outer data, and "code" in the
	 * inner data.
	 * @param value the data to set (can be null).
	 */
	public void setOuterData (String value) {
		outerData = value;
	}
	
	/**
	 * Gets the outer data for this in-line code. If there is no outer data,
	 * the inner data is returned (same as {@link #getData()}).
	 * @return the outer data or, if there is none, the inner data.
	 */
	public String getOuterData () {
		if ( outerData != null ) return outerData;
		else return data; // Returns data if no outer-data is set
	}

	/**
	 * Indicates if this in-line code can be duplicated in the text.
	 * For example a HTML bold element could be duplicated, while a <code>%s</code> should not.
	 * @return true if this in-line code can be duplicated.
	 */
	public boolean isCloneable () {
		return ((flag & CLONEABLE) == CLONEABLE);
	}

	/**
	 * Sets the flag of this in-line code to indicate if it can be duplicated or not.
	 * @param value true to allow duplication, false to forbid it.
	 */
	public void setCloneable (boolean value) {
		if ( value ) flag |= CLONEABLE;
		else flag &= ~CLONEABLE;
	}
	
	/**
	 * Indicates if this in-line code can be removed from the text.
	 * For example a HTML bold element could be removed, while a <code>%s</code> should not.
	 * @return true if this in-line code can be removed.
	 */
	public boolean isDeleteable () {
		return ((flag & DELETEABLE) == DELETEABLE);
	}
	
	/**
	 * Sets the flag of this in-line code to indicate if it can be removed or not.
	 * @param value true to allow deletion, false to forbid it.
	 */
	public void setDeleteable (boolean value) {
		if ( value ) flag |= DELETEABLE;
		else flag &= ~DELETEABLE;
	}

	/**
	 * Sets the annotation for this code.
	 * @param type type of the annotation to set.
	 * @param annotation the annotation to set. This parameter can be null for example
	 * when the annotation is used like a boolean flag.
	 */
	public void setAnnotation (String type,
		InlineAnnotation annotation)
	{
		// Use a small initial capacity to save space
		if ( annotations == null ) {
			annotations = new LinkedHashMap<String, InlineAnnotation>(ANNOTATIONS_INITCAP);
		}
		annotations.put(type, annotation);
	}
	
	/**
	 * Gets the annotation of a given type.
	 * @param type the type of annotation to retrieve.
	 * @return the annotation of the given type, or null if there is no such
	 * annotation for this code.
	 */
	public InlineAnnotation getAnnotation (String type) {
		if ( annotations == null ) return null;
		return annotations.get(type);
	}

	/**
	 * Removes all annotations from this code.
	 */
	public void removeAnnotations () {
		if ( annotations != null ) {
			annotations.clear();
			annotations = null;
		}
		//TODO: update closing code if needed (when they'll be linked)
	}

	/**
	 * Removes the annotation of a given type in this code.
	 * @param type the type of annotation to remove.
	 */
	public void removeAnnotation (String type) {
		if ( annotations != null ) {
			annotations.remove(type);
		}
		//TODO: update closing code if needed (when they'll be linked)
	}

}
