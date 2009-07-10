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
 * Signals that an editor could not be created. This error occurs for example when
 * trying to edit the parameters of a filter or of a step and the corresponding
 * editor cannot be created.
 */
public class OkapiEditorCreationException extends RuntimeException {
	
	private static final long serialVersionUID = -4301626737744375525L;

	/**
	 * Creates an empty new OkapiEditorCreationException object.
	 */
	public OkapiEditorCreationException () {
		super();
	}

	/**
	 * Creates a new OkapiEditorCreationException object with a given message.
	 * @param message text of the message.
	 */
	public OkapiEditorCreationException (String message) {
		super(message);
	}

	/**
	 * Creates a new OkapiEditorCreationException object with a given parent 
	 * exception cause.
	 * @param cause the parent exception cause.
	 */
	public OkapiEditorCreationException (Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new OkapiEditorCreationException object with a given message and 
	 * a given parent exception cause.
	 * @param message the message.
	 * @param cause the cause.
	 */
	public OkapiEditorCreationException (String message, Throwable cause) {
		super(message, cause);
	}

}
