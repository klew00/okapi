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

import net.sf.okapi.common.filters.CodeFragment;
import net.sf.okapi.common.filters.Container;
import net.sf.okapi.common.filters.IContainer;

public class Main {

	static final File INDEX_DIR = new File("index");

	private static void testContainer () {
		try {
			System.out.println("---start testNewObjects---");
			IContainer cnt = new Container();
			cnt.append("t1");
			cnt.append(new CodeFragment(IContainer.CODE_ISOLATED, null, "<br/>"));
			cnt.append("t2");
			System.out.println("out 1: " + cnt.toString());
			String s1 = cnt.getCodedText();
			cnt.setContent(s1);
			System.out.println("out 2: " + cnt.toString());
			cnt.setContent("");
			System.out.println("out 3: " + cnt.toString());
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
