package net.sf.okapi.apptest.annotation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.apptest.common.IAnnotation;

public class TmAnnotation implements IAnnotation, Iterable<TmMatch> {
	List<TmMatch> tmMatches;
	
	public TmAnnotation(){
		tmMatches = new LinkedList<TmMatch>();
	}
	
	public void add(TmMatch match) {
		tmMatches.add(match);
	}
	
	public boolean isEmpty() {
		return tmMatches.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<TmMatch> iterator() {
		return tmMatches.iterator();
	}
	
}
