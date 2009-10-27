package net.sf.okapi.common;

import java.util.HashSet;
import java.util.Set;

public final class LocaleFilter {

	private static enum StartQualifier{
		None,
		Any;
	}

	private final StartQualifier qualifier;
	private final Set<LocaleId> includes;
	private final Set<LocaleFilter> excepts;
	private final Set<String> regions;
	private final Set<String> countries;
	private final Set<String> regionMatchers;
	private final Set<String> countryMatchers;
	
	private LocaleFilter(StartQualifier qualifier) {
		includes = new HashSet<LocaleId>();
		excepts = new HashSet<LocaleFilter>();
		regions = new HashSet<String>();
		countries = new HashSet<String>();
		regionMatchers = new HashSet<String>();
		countryMatchers = new HashSet<String>();
		this.qualifier = qualifier;
	}
	
	public static LocaleFilter any() {
		return new LocaleFilter(StartQualifier.Any);
	}
	
	public static LocaleFilter none() {
		return new LocaleFilter(StartQualifier.None);
	}
	
	public static LocaleFilter anyOf(LocaleId ... localeIds) {
		LocaleFilter filter = new LocaleFilter(StartQualifier.None);
		for (LocaleId localeId : localeIds) {
			filter.includes.add(localeId);
		}
		return filter;
	}
	
	public static LocaleFilter anyExcept(LocaleId ... localeIds) {
		LocaleFilter filter = new LocaleFilter(StartQualifier.Any);
		filter.excepts.add(LocaleFilter.anyOf(localeIds));
		return filter;
	}
	
	public LocaleFilter except(LocaleFilter filter) {
		this.excepts.add(filter);
		return this;
	}
	
	public LocaleFilter regionIn(String ... regions) {
		for (String region : regions) {
			this.regions.add(region);
		}
		return this;
	}
	public LocaleFilter regionMatches(String regex) {
		this.regionMatchers.add(regex);
		return this;
	}

	public LocaleFilter countryIn(String ... countries) {
		for (String country : countries) {
			this.countries.add(country);
		}
		return this;
	}
	public LocaleFilter countryMatches(String regex) {
		countryMatchers.add(regex);
		return this;
	}

	public boolean matches(LocaleId localeId) {
		switch(qualifier) {
		case Any:
			return true;
		case None:
			return false;
		}
		return false;
	}
	
	public Set<LocaleId> filter(LocaleId ... localeIds) {
		Set<LocaleId> locales = new HashSet<LocaleId>();
		for (LocaleId localeId : locales) {
			if( matches(localeId)) {
				locales.add(localeId);
			}
		}
		return locales;
	}
	
}
