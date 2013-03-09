/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Provides the different information common to all types of parameters used
 * to configure steps, filters, and other okapi components.
 */
public interface IParameterDescriptor {

	/**
	 * Gets the programming name of this parameter. the name must follow the 
	 * JavaBean naming conventions. For example, a parameter accessible by
	 * <code>getMyText</code> and <code>setMyText</code> must be named <code>myText</code>
	 * @return the programming name of this parameter.
	 */
	public String getName ();

	/**
	 * Gets the localizable name of this parameter.
	 * @return the localizable name of this parameter.
	 */
	public String getDisplayName ();

	/**
	 * Sets the localizable name of this parameter.
	 * @param displayName the new localizable name of this parameter.
	 */
	public void setDisplayName (String displayName);

	/**
	 * Gets the short localizable description of this parameter.
	 * @return the short localizable description of this parameter.
	 */
	public String getShortDescription ();
	
	/**
	 * Gets the short localizable description of this parameter.
	 * @param shortDescription the new short localizable description of this parameter.
	 */
	public void setShortDescription (String shortDescription);

	/**
	 * Gets the type of this parameter.
	 * @return the type of this parameter.
	 */
	public Type getType ();
	
	/**
	 * Gets the method to obtain the current value of this parameter.
	 * @return the method to get the current value of this parameter.
	 */
	public Method getReadMethod ();
	
	/**
	 * Gets the method to set a new value for this this parameter.
	 * @return the method to set a new value of this parameter.
	 */
	public Method getWriteMethod ();
	
	/**
	 * Gets the object where this parameter is instantiated.
	 * @return the object where this parameter is instantiated.
	 */
	public Object getParent ();

}
