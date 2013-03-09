/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Test;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filters.FilterTestDriver;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextUnitUtil;
import net.sf.okapi.common.skeleton.ZipSkeleton;

public class TestVersions {

	@Test // Make sure we have at least one test to avoid build errors
	public void testDescription () {
		net.sf.okapi.lib.beans.v0.JSONPersistenceSession skelSession = 
			new net.sf.okapi.lib.beans.v0.JSONPersistenceSession(Event.class);
		assertFalse(skelSession.isActive());
	}
	
	// DEBUG @Test
	public void testOldPersistenceRoundtrip() throws IOException{
		Event event1 = new Event(EventType.TEXT_UNIT);
		ITextUnit tu1 = TextUnitUtil.buildTU("source-text1" + (char) 2 + '"' + " : " + '"' + 
				'{' + '"' + "ssssss " + ':' + '"' + "ddddd" + "}:" + '<' + '>' + "sssddd: <>dsdd");
		String zipName = this.getClass().getResource("sample1.en.fr.zip").getFile();
		tu1.setSkeleton(new ZipSkeleton(new ZipFile(new File(zipName)), null));
		event1.setResource(tu1);
		tu1.setTarget(LocaleId.FRENCH, new TextContainer("french-text1"));
		tu1.setTarget(LocaleId.TAIWAN_CHINESE, new TextContainer("chinese-text1"));
				
		Event event2 = new Event(EventType.TEXT_UNIT);
		ITextUnit tu2 = TextUnitUtil.buildTU("source-text2" + (char) 2 + '"' + " : " + '"' + 
				'{' + '"' + "ssssss " + ':' + '"' + "ddddd" + "}:" + '<' + '>' + "sssddd: <>dsdd");
		tu2.setSkeleton(new ZipSkeleton(null, new ZipEntry("aa1/content/content.gmx")));
		event2.setResource(tu2);
		tu2.setTarget(LocaleId.FRENCH, new TextContainer("french-text2"));
		tu2.setTarget(LocaleId.TAIWAN_CHINESE, new TextContainer("chinese-text2"));
						
		net.sf.okapi.lib.beans.v0.JSONPersistenceSession skelSession = 
			new net.sf.okapi.lib.beans.v0.JSONPersistenceSession(Event.class);
		
		File tempSkeleton = null;
		tempSkeleton = File.createTempFile("~aaa", ".txt");
		tempSkeleton.deleteOnExit();
		
		skelSession.start(new FileOutputStream(tempSkeleton));
		
		ArrayList<Event> events = new ArrayList<Event>();
		events.add(event1);
		events.add(event2);
		
		skelSession.serialize(event1);
		skelSession.serialize(event2);
		skelSession.end();
		
		FileInputStream fis = new FileInputStream(tempSkeleton);
		skelSession.start(fis);		
		
		Event event11 = (Event) skelSession.deserialize();
		Event event22 = (Event) skelSession.deserialize();
		
		skelSession.end();
				
		ArrayList<Event> events2 = new ArrayList<Event>();
		events2.add(event11);
		events2.add(event22);
		
		FilterTestDriver.compareEvents(events, events2);
		FilterTestDriver.laxCompareEvents(events, events2);
	}

}
