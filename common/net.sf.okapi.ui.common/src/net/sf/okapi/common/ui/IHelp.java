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

package net.sf.okapi.common.ui;

/**
 * Common way of calling the help topics, regardless of the underlying system
 * (e.g. eclipse application or simple Java application).
 */
public interface IHelp {

	/**
	 * Shows the help for a given topic.
	 * @param filenameWithoutExtension The name of the topic file without an extension.
	 * The extension is assumed to be .html and is added automatically.
	 */
	public void showTopic (Object object,
		String filenameWithoutExtension);

}
