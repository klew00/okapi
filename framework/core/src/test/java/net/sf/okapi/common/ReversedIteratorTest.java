package net.sf.okapi.common;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;


public class ReversedIteratorTest {

	@Test
	public void reversedList() {
		List<String> l = new LinkedList<String>();		
		l.add("A");
		l.add("B");
		l.add("C");
		
		ReversedIterator<String> ri = new ReversedIterator<String>(l);
		List<String> rl = new LinkedList<String>();
		for (String s : ri) {
			rl.add(s);
		}
		assertEquals("C", rl.remove(0));
		assertEquals("B", rl.remove(0));
		assertEquals("A", rl.remove(0));
	}
}
