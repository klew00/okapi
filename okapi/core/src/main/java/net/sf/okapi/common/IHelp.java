/*===========================================================================
  Copyright (C) 2009-2010 by the Okapi Framework contributors
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

/**
 * Common way of calling the help topics, regardless of the underlying system
 * (for example: eclipse application or simple Java application).
 */
public interface IHelp {

	/**
	 * Shows a given topic of the Okapi Wiki.
	 * @param topic the name of the topic. Any space will be replaced
	 * automatically by '_'.
	 */
	public void showWiki (String topic);

	/**
	 * Shows the help for a given topic.
	 * @param object the object for which the help is to be displayed. The package
	 * name of this parameter is used to compute the location of the help file. 
	 * @param filename the filename of the topic to call. The location is computed
	 * for the package path of the object parameter.
	 * @param query an option query string, or null.
	 */
	public void showTopic (Object object,
		String filename,
		String query);

	/**
	 * Shows the help for a given topic.
	 * @param object the object for which the help is to be displayed. The package
	 * name of this parameter is used to compute the location of the help file. 
	 * @param filename the filename of the topic to call. The location is computed
	 * for the package path of the object parameter.
	 */
	public void showTopic (Object object,
		String filename);

}
