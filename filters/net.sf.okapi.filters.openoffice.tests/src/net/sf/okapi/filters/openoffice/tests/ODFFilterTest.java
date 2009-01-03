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

package net.sf.okapi.filters.openoffice.tests;

import java.net.URL;

import net.sf.okapi.common.filters.FilterEvent;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.ISkeleton;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.IResource;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;
import net.sf.okapi.common.writer.GenericFilterWriter;
import net.sf.okapi.filters.openoffice.ODFFilter2;

import org.junit.Assert;
import org.junit.Test;

public class ODFFilterTest {

	@Test
	public void runTest () {
		ODFFilter2 filter = null;		
		try {
			filter = new ODFFilter2();
			filter.setOptions("en", "UTF-8", true);
			URL url = ODFFilterTest.class.getResource("/content_TestDocument01.odt.xml");
			filter.open(url);
			process(filter);
			filter.close();
			
			filter.open(url);
			rewrite(filter);
			filter.close();
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			Assert.fail();
		}
		finally {
			if ( filter != null ) filter.close();
		}
	}
	
	private void rewrite (IFilter filter) {
		GenericFilterWriter writer = null;
		try {
			writer = new GenericFilterWriter(new GenericSkeletonWriter());
			writer.setOptions("FR", "UTF-8");
			writer.setOutput("content_TestDocument01.odt_out.xml");
			while ( filter.hasNext() ) {
				writer.handleEvent(filter.next());
			}
		}
		finally {
			if ( writer != null ) writer.close();
		}
		
	}
	
	private void process (IFilter filter) {
		System.out.println("================================================");
		FilterEvent event;
		while ( filter.hasNext() ) {
			event = filter.next();
			switch ( event.getEventType() ) {
			case START:
				System.out.println("---Start");
				break;
			case FINISHED:
				System.out.println("---Finished");
				break;
			case START_DOCUMENT:
				System.out.println("---Start Document");
				printSkeleton(event.getResource());
				break;
			case END_DOCUMENT:
				System.out.println("---End Document");
				printSkeleton(event.getResource());
				break;
			case START_SUBDOCUMENT:
				System.out.println("---Start Sub Document");
				printSkeleton(event.getResource());
				break;
			case END_SUBDOCUMENT:
				System.out.println("---End Sub Document");
				printSkeleton(event.getResource());
				break;
			case START_GROUP:
				System.out.println("---Start Group");
				printSkeleton(event.getResource());
				break;
			case END_GROUP:
				System.out.println("---End Group");
				printSkeleton(event.getResource());
				break;
			case TEXT_UNIT:
				System.out.println("---Text Unit");
				TextUnit tu = (TextUnit)event.getResource();
				printResource(tu);
				System.out.println("S=["+tu.toString()+"]");
				for ( String lang : tu.getTargetLanguages() ) {
					System.out.println("T=["+tu.getTarget(lang).toString()+"]");
				}
				printSkeleton(tu);
				break;
			case DOCUMENT_PART:
				System.out.println("---Document Part");
				printResource((INameable)event.getResource());
				printSkeleton(event.getResource());
				break;
			}
		}
	}
	
	private void printResource (INameable res) {
		System.out.println("  id="+res.getId());
		System.out.println("  name="+res.getName());
		System.out.println("  type="+res.getType());
		System.out.println("  mimeType="+res.getMimeType());
	}

	private void printSkeleton (IResource res) {
		ISkeleton skel = res.getSkeleton();
		if ( skel != null ) {
			System.out.println("---");
			System.out.println(skel.toString());
			System.out.println("---");
		}
	}

}
