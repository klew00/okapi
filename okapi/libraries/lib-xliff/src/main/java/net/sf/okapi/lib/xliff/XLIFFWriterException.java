/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.lib.xliff;

/**
 * Signals that an XLIFF writer had an error.  
 */
public class XLIFFWriterException extends RuntimeException {

	/**
	 * Serialization version id.
	 */
	private static final long serialVersionUID = -0100;

	/**
	 * Creates an empty new XLIFFWriterException object.
	 */
	public XLIFFWriterException () {
		super();
	}

	/**
	 * Creates a new XLIFFWriterException object with a given message.
	 * @param message text of the message.
	 */
	public XLIFFWriterException (String message) {
		super(message);		
	}

	/**
	 * Creates a new XLIFFWriterException object with a given parent 
	 * exception cause.
	 * @param cause the parent exception cause.
	 */
	public XLIFFWriterException (Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new XLIFFWriterException object with a given message and 
	 * a given parent exception cause.
	 * @param message the message.
	 * @param cause the cause.
	 */
	public XLIFFWriterException (String message, Throwable cause) {
		super(message, cause);
	}

}
