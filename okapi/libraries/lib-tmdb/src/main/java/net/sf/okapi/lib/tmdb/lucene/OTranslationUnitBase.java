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

package net.sf.okapi.lib.tmdb.lucene;

import net.sf.okapi.lib.tmdb.DbUtil;

/**
 * All files in this package are based on the files by @author HaslamJD and @author HARGRAVEJE in the okapi-tm-pensieve project amd in most cases there are only minor changes.
 */

/**
 * @author fliden
 *
 */
public abstract class OTranslationUnitBase {

	public static final String DEFAULT_ID_NAME = DbUtil.SEGKEY_NAME;
	private OFields fields;
	private OField id;

	public OTranslationUnitBase (OField id) {
		fields = new OFields();
		this.id = id;
	}
	
	public OTranslationUnitBase (String idValue) {
		fields = new OFields();
		this.id = new OField(DEFAULT_ID_NAME, idValue);
	}
	
	public OTranslationUnitBase (OField id,
		OFields fields)
	{
		this.fields = fields;
		this.id = id;
	}
	
	public OTranslationUnitBase (String idValue,
		OFields fields)
	{
		this.fields = fields;
		this.id = new OField(DEFAULT_ID_NAME, idValue);
	}

	/**
	 * Return the id field
	 * @return
	 */
	public OField getId () {
		return id;
	}
	
	/**
	 * Return the value of the id field
	 * @return
	 */
	public String getIdValue () {
		return id.getValue();
	}
	
	/**
	 * Return the value of the id field
	 * @return
	 */
	public String getIdName () {
		return id.getName();
	}
	
	/**
	 * Gets the fields
	 * @return the fields
	 */
	public OFields getFields () {
		return fields;
	}

	/**
	 * Sets all fields at once and replaces any existing fields.
	 * @param fields the fields to set
	 */
	public void setFields (OFields fields) {
		this.fields = fields;
	}

	/**
	 * Gets the field for a specific key
	 * @param name the name for the field we want
	 * @return the field for a give field name
	 */
	public OField getField (String name) {
		return fields.get(name);
	}
	
	/**
	 * Sets the field for a give field name field
	 * @param field the field we want set
	 */
	public void setField (OField field) {
		fields.put(field.getName(), field);
	}
	
	/**
	 * Sets the field for a give field name field
	 * @param name the name for the field we want to delete
	 */
	public void deleteField (String name) {
		fields.remove(name);
	}

}
