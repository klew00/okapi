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

package net.sf.okapi.filters.xliff.tests;

import java.net.URI;
import java.net.URL;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IFilter;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.resource.INameable;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.filters.tests.FilterTestDriver;
import net.sf.okapi.filters.xliff.AltTransAnnotation;
import net.sf.okapi.filters.xliff.XLIFFFilter;

import org.junit.Assert;
import org.junit.Test;

public class XLIFFFilterTest {

	@Test
	public void runTest () {
		FilterTestDriver testDriver = new FilterTestDriver();
		XLIFFFilter filter = null;		
		try {
			filter = new XLIFFFilter();
			filter.setOptions("en", "es", "UTF-8", true);
			URL url = XLIFFFilterTest.class.getResource("/JMP-11-Test01.xlf");
			filter.open(new URI(url.toString()));
			if ( !testDriver.process(filter) ) Assert.fail();
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
	
	private void process (IFilter filter) {
		System.out.println("================================================");
		Event event;
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
				printAltTrans(tu);
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

	private void printAltTrans (TextUnit res) {
		System.out.println("---AltTransAnnotation---");
		AltTransAnnotation ata = res.getAnnotation(AltTransAnnotation.class);
		if ( ata == null ) {
			System.out.println("No annotation");
		}
		else {
			ata.startIteration();
			while ( ata.moveToNext() ) {
				TextUnit tu = ata.getEntry();
				if ( ata.hasSource() ) {
					System.out.println("S("+ata.getSourceLanguage()+")=["+tu.toString()+"]");
				}
				else {
					System.out.println("No source defined.");
					
				}
				System.out.println("T("+ata.getTargetLanguage()+")=["
					+tu.getTarget(ata.getTargetLanguage()).toString()+"]");
			}
		}
		System.out.println("---end of AltTransAnnotation---");
	}

}
