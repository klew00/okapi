/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
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
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.ferret.Core;

import java.util.Vector;

/* See http://dev.mysql.com/tech-resources/articles/full-text-revealed.html
 * And http://www.ranks.nl/stopwords/
 */

public class StopWords {
	
	private Vector<String>   m_aList = null;

	public Vector<String> getList () {
		return m_aList;
	}
	
	public void loadList ()
	{
		if ( m_aList != null ) {
			m_aList.clear();
		}
		m_aList = new Vector<String>();
		m_aList.add("i");
		m_aList.add("a");
//		m_aList.add("about");
		m_aList.add("an");
		m_aList.add("are");
		m_aList.add("as");
		m_aList.add("at");
		m_aList.add("be");
		m_aList.add("by");
		m_aList.add("for");
//		m_aList.add("from");
//		m_aList.add("how");
		m_aList.add("in");
		m_aList.add("is");
		m_aList.add("it");
		m_aList.add("of");
		m_aList.add("on");
		m_aList.add("or");
		m_aList.add("that");
		m_aList.add("the");
		m_aList.add("the");
		m_aList.add("this");
		m_aList.add("to");
		m_aList.add("was");
		m_aList.add("were");
//		m_aList.add("what");
//		m_aList.add("when");
//		m_aList.add("where");
//		m_aList.add("who");
		m_aList.add("will");
		m_aList.add("with");
	};	 	
}
