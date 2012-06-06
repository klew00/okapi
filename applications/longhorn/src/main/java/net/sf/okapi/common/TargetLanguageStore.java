package net.sf.okapi.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class TargetLanguageStore {

	private static TargetLanguageStore INSTANCE;

	private Map<Integer, Set<String>> localesForProject = new ConcurrentHashMap<Integer, Set<String>>();

	private TargetLanguageStore() {}

	public static synchronized TargetLanguageStore getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TargetLanguageStore();
		}

		return INSTANCE;
	}

	public void setTargetLocalesForProject(final int projectId, final Set<String> targetLocales) {
		localesForProject.put(projectId, Collections.unmodifiableSet(new HashSet<String>(targetLocales)));
	}

	public Set<String> getTargetLocalesForProject(final int projectId) {
		return localesForProject.get(projectId);
	}

	public void clearTargetLocalesForProject(final int projectId) {
		localesForProject.remove(projectId);
	}

}
