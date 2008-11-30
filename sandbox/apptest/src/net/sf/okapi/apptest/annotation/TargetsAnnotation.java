package net.sf.okapi.apptest.annotation;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.okapi.apptest.common.IAnnotation;
import net.sf.okapi.apptest.resource.TextContainer;

public class TargetsAnnotation implements IAnnotation, Iterable<String> {

	private ConcurrentHashMap<String, TextContainer> targets;

	public TargetsAnnotation() {
		targets = new ConcurrentHashMap<String, TextContainer>();
	}

	public void set (String language, TextContainer tt) {
		targets.put(language, tt);
	}

	public TextContainer get (String language) {
		return targets.get(language);
	}

	public boolean isEmpty() {
		return targets.isEmpty();
	}

	public Iterator<String> iterator () {
		IterableEnumeration<String> iterableLocales = new IterableEnumeration<String>(targets.keys());
		return iterableLocales.iterator();
	}

	public Set<String> getLanguages () {
		return targets.keySet();
	}
}
