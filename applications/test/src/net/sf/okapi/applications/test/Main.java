/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation                   */
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

package net.sf.okapi.applications.test;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.sf.okapi.common.filters.CodeFragment;
import net.sf.okapi.common.filters.Container;
import net.sf.okapi.common.filters.IContainer;
import net.sf.okapi.common.filters.IFragment;

public class Main {

	static final File INDEX_DIR = new File("index");

	private static void testContainer () {
		try {
			System.out.println("---start testNewObjects---");
			IContainer cnt = new Container();
			cnt.append("t1");
			cnt.append(new CodeFragment(IContainer.CODE_ISOLATED, 1, "<br/>"));
			cnt.append("t2");
			System.out.println("out 1: " + cnt.toString());
			String s1 = cnt.getCodedText();
			cnt.setContent(s1);
			System.out.println("out 2: " + cnt.toString());
			Map<Integer, IFragment> codes = cnt.getCodes();
			cnt.setContent(s1, codes);
			System.out.println("out 3: " + cnt.toString());
			
			List<IFragment> list = cnt.getFragments();
			for ( IFragment frag : list ) {
				System.out.println(String.format("istext=%s, content='%s'",
					(frag.isText() ? "yes" : "no "),
					frag.toString()));
			}
			
			cnt.setProperty("test1", "value1");
			System.out.println(String.format("name='test1' value='%s'",
				cnt.getProperty("test1")));
			cnt.setProperty("test1", null);
			System.out.println(String.format("name='test1' value='%s'",
				cnt.getProperty("test1")));
			cnt.setProperty("test1", "value1 again");
			System.out.println(String.format("name='test1' value='%s'",
				cnt.getProperty("test1")));
			cnt.clearProperties();
			System.out.println(String.format("name='test1' value='%s'",
				cnt.getProperty("test1")));
		}		
		catch ( Exception E ) {
			System.out.println(E.getLocalizedMessage());
		}
		System.out.println("---end testNewObjects---");
	}
	
	public static void main(String[] args) throws Exception
	{
		testContainer();
	}		
		
}
