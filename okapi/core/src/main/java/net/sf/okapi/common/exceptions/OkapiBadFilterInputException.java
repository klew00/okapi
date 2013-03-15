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

package net.sf.okapi.common.exceptions;

/**
 * Signals that a filter had an error when trying to open its input. This is likely
 * a problem in the type of input used to open the document.  
 */
public class OkapiBadFilterInputException extends RuntimeException {

	private static final long serialVersionUID = 1122090108070908960L;

	/**
	 * Creates an empty new OkapiBadFilterInputException object.
	 */
	public OkapiBadFilterInputException () {
		super();
	}

	/**
	 * Creates a new OkapiBadFilterInputException object with a given message.
	 * @param message text of the message.
	 */
	public OkapiBadFilterInputException (String message) {
		super(message);		
	}

	/**
	 * Creates a new OkapiBadFilterInputException object with a given parent 
	 * exception cause.
	 * @param cause the parent exception cause.
	 */
	public OkapiBadFilterInputException (Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new OkapiBadFilterInputException object with a given message and 
	 * a given parent exception cause.
	 * @param message the message.
	 * @param cause the cause.
	 */
	public OkapiBadFilterInputException (String message, Throwable cause) {
		super(message, cause);
	}

}
