package net.sf.okapi.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class ListUtilsTest {

	@Test
	public void testListToArray() {
		
		 List<String> list = new ArrayList<String>();
		    list.add("Blobbo");
		    list.add("Cracked");
		    list.add("Dumbo");
		    list.add("");

		    assertEquals(4, list.size());

		    String[] sl = (String[]) list.toArray(new String[] {});
		    assertEquals(4, sl.length);
	}
	
	@Test
	public void testStringAsList() {
		
		String st = "1,2,3,4";
		List<String> list = ListUtils.stringAsList(st);
		
		assertNotNull(list);
		
		assertEquals(4, list.size());
		
		assertEquals("1", list.get(0));
		assertEquals("2", list.get(1));
		assertEquals("3", list.get(2));
		assertEquals("4", list.get(3));
		
		
		st = "1,2,3,4,  ";
		list = ListUtils.stringAsList(st);
		
		assertNotNull(list);
		
		assertEquals(5, list.size());
		
		assertEquals("1", list.get(0));
		assertEquals("2", list.get(1));
		assertEquals("3", list.get(2));
		assertEquals("4", list.get(3));
		assertEquals("", list.get(4));
		
		String[] s = ListUtils.stringAsArray(st);
		assertEquals(5, s.length);
		
		assertEquals("1", s[0]);
		assertEquals("2", s[1]);
		assertEquals("3", s[2]);
		assertEquals("4", s[3]);
		assertEquals("", s[4]);
	}
}
