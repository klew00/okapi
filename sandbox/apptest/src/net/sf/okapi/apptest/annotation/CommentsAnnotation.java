package net.sf.okapi.apptest.annotation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.okapi.apptest.common.IAnnotation;

public class CommentsAnnotation implements IAnnotation, Iterable<String> {
		private List<String> comments;
		
		public CommentsAnnotation() {
			comments = new LinkedList<String>();
		}
		
		public void add(String comment) {
			comments.add(comment);
		}
		
		public boolean isEmpty() {
			return comments.isEmpty();
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		public Iterator<String> iterator() {			
			return comments.iterator();
		}
}
