package net.sf.okapi.apptest.annotation;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class TargetsAnnotation implements IAnnotation, Iterable<String> {
	private ConcurrentHashMap<String, String> targets;

	public TargetsAnnotation() {
		targets = new ConcurrentHashMap<String, String>();
	}

	public void add(String locale, String segment) {
		targets.put(locale, segment);
	}

	public String get(String locale) {
		return targets.get(locale);
	}

	public boolean isEmpty() {
		return targets.isEmpty();
	}

	/*
	 * Iterate over all locales
	 */
	public Iterator<String> iterator() {
		IterableEnumeration<String> iterableLocales = new IterableEnumeration<String>(targets.keys());
		return iterableLocales.iterator();
	}
}
