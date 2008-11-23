package net.sf.okapi.apptest.annotation;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.okapi.apptest.common.IAnnotation;
import net.sf.okapi.apptest.resource.TextUnit;

public class TargetsAnnotation implements IAnnotation, Iterable<String> {

	private ConcurrentHashMap<String, TextUnit> targets;

	public TargetsAnnotation() {
		targets = new ConcurrentHashMap<String, TextUnit>();
	}

	public void set (String locale, TextUnit textUnit) {
		targets.put(locale, textUnit);
	}

	public TextUnit get (String locale) {
		return targets.get(locale);
	}

	public boolean isEmpty() {
		return targets.isEmpty();
	}

	public Iterator<String> iterator() {
		IterableEnumeration<String> iterableLocales = new IterableEnumeration<String>(targets.keys());
		return iterableLocales.iterator();
	}
}
