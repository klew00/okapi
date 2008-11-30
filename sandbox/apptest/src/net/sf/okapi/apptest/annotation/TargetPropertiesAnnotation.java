package net.sf.okapi.apptest.annotation;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.okapi.apptest.common.IAnnotation;
import net.sf.okapi.apptest.resource.Property;

public class TargetPropertiesAnnotation implements IAnnotation, Iterable<String> {

	private ConcurrentHashMap<String, Hashtable<String, Property>> targets;

	public TargetPropertiesAnnotation () {
		targets = new ConcurrentHashMap<String, Hashtable<String, Property>>();
	}

	public void set (String locale, Hashtable<String, Property> properties) {
		targets.put(locale, properties);
	}

	public Hashtable<String, Property> get (String locale) {
		return targets.get(locale);
	}

	public boolean isEmpty () {
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
