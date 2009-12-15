/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO Class Javadoc
 * TODO Methods Javadoc
 * TODO Tests
 */
public final class LocaleFilter {
	
	private static enum FilterType{
		
		/**
		 * The filter allows no locale. 
		 */
		None,
		
		/**
		 * The filter allows any locale.
		 */
		Any;
	}

	private final FilterType type;
	
	private final List<LocaleId> includes;
	private final List<LocaleId> excludes;
	
	private final List<String> languageIncludes;
	private final List<String> regionIncludes;
	private final List<String> userPartIncludes;
	
	private final List<String> languageExcludes;
	private final List<String> regionExcludes;
	private final List<String> userPartExcludes;
	
	private final List<Pattern> includePatterns; 
	private final List<Pattern> excludePatterns; 
		
	/**
	 * Private constructor.
	 * @param type
	 */
	private LocaleFilter(FilterType type) {
		
		includes = new ArrayList<LocaleId>();
		excludes = new ArrayList<LocaleId>();
		
		languageIncludes = new ArrayList<String>();
		regionIncludes = new ArrayList<String>();
		userPartIncludes = new ArrayList<String>();
		
		languageExcludes = new ArrayList<String>();
		regionExcludes = new ArrayList<String>();
		userPartExcludes = new ArrayList<String>();
		
		includePatterns = new ArrayList<Pattern>();
		excludePatterns = new ArrayList<Pattern>();
		
		this.type = type;
	}
	
	/**
	 * Public constructor.
	 */
	public LocaleFilter() {
		
		this(FilterType.Any);		
	}
	
	/**
	 * Creates a filter of the Any type.
	 * @return
	 */
	public static LocaleFilter any() {
		
		return new LocaleFilter();
	}
	
	/**
	 * Creates a filter of the None type.
	 * @return
	 */
	public static LocaleFilter none() {
		
		return new LocaleFilter(FilterType.None);
	}
	
	/**
	 * Creates a filter of the Lookup type. Only the given locales will match.
	 * @param localeIds
	 * @return
	 */
	public static LocaleFilter only(LocaleId ... localeIds) {
		
		if (Util.isEmpty(localeIds)) return none();
		
		LocaleFilter filter = none();		
		filter.include(localeIds);
		
		return filter;
	}
	
	/**
	 * 
	 * @param localeIds
	 * @return
	 */
	public static LocaleFilter anyExcept(LocaleId ... localeIds) {
		
		LocaleFilter filter = any();
		filter.exclude(localeIds);
		
		return filter;
	}
	
	/**
	 * 
	 * @param localeId
	 * @return
	 */
	public LocaleFilter include(LocaleId localeId) {
		
		if (localeId == null) return this;
		
		this.includes.add(0, localeId); // Later added are attended first in matches()
		
		return this;
	}
	
	public LocaleFilter include(LocaleId ... localeIds) {
		
		if (Util.isEmpty(localeIds)) return this;
		
		for (LocaleId localeId : localeIds)
			include(localeId);
				
		return this;
	}
	
	public LocaleFilter include(Set<LocaleId> localeIds) {
		
		if (localeIds == null) return this;
		
		for (LocaleId localeId : localeIds)
			include(localeId);
		
		return this;
	}
	
	public LocaleFilter include(LocaleFilter filter) {
		
		if (filter == null) return this;
		
		includes.addAll(0, filter.getIncludes());
		
		languageIncludes.addAll(0, filter.getLanguageIncludes());
		regionIncludes.addAll(0, filter.getRegionIncludes());
		userPartIncludes.addAll(0, filter.getUserPartIncludes());
		
		includePatterns.addAll(0, filter.getIncludePatterns()); 
		
		return this;
	}
	
	public LocaleFilter include(String regex, int flags) {
		
		if (Util.isEmpty(regex)) return this;
		
		includePatterns.add(0, Pattern.compile(regex, flags));
		
		return this;
	}
	
	/**
	 * Filter configuration method.
	 * @param languages
	 * @return
	 */
	public LocaleFilter includeLanguage(String ... languages) {
		
		if (Util.isEmpty(languages)) return this;
		
		for (String language : languages)
			includeLanguage(language);

		return this;
	}
	
	/**
	 * 
	 */
	public LocaleFilter includeLanguage(String language) {
		
		if (Util.isEmpty(language)) return this;
		
		this.languageIncludes.add(language);
		
		return this;
	}
	
	/**
	 * Filter configuration method.
	 * @param languages
	 * @return
	 */
	public LocaleFilter includeRegion(String ... regions) {
		
		if (Util.isEmpty(regions)) return this;
		
		for (String region : regions)
			includeRegion(region);

		return this;
	}
	
	/**
	 * 
	 */
	public LocaleFilter includeRegion(String region) {
		
		if (Util.isEmpty(region)) return this;
		
		this.regionIncludes.add(region);
		
		return this;
	}
		
	/**
	 * Filter configuration method.
	 * @param languages
	 * @return
	 */
	public LocaleFilter includeUserPart(String ... userParts) {
		
		if (Util.isEmpty(userParts)) return this;
		
		for (String userPart : userParts)
			includeUserPart(userPart);

		return this;
	}
	
	/**
	 * 
	 */
	public LocaleFilter includeUserPart(String userPart) {
		
		if (Util.isEmpty(userPart)) return this;
		
		this.userPartIncludes.add(userPart);
		
		return this;
	}
	
	/**
	 * 
	 * @param localeId
	 * @return
	 */
	public LocaleFilter exclude(LocaleId localeId) {
		
		if (localeId == null) return this;
		
		this.excludes.add(0, localeId); // Later added are attended first in matches()
		
		return this;
	}
	
	public LocaleFilter exclude(LocaleId ... localeIds) {
		
		if (Util.isEmpty(localeIds)) return this;
		
		for (LocaleId localeId : localeIds)
			exclude(localeId);
				
		return this;
	}
	
	public LocaleFilter exclude(Set<LocaleId> localeIds) {
		
		if (localeIds == null) return this;
		
		for (LocaleId localeId : localeIds)
			exclude(localeId);
				
		return this;
	}
	
	public LocaleFilter exclude(LocaleFilter filter) {
		
		if (filter == null) return this;
		
		excludes.addAll(0, filter.getIncludes());
		
		languageExcludes.addAll(0, filter.getLanguageIncludes());
		regionExcludes.addAll(0, filter.getRegionIncludes());
		userPartExcludes.addAll(0, filter.getUserPartIncludes());
		
		excludePatterns.addAll(0, filter.getIncludePatterns());
		
		return this;
	}
	
	public LocaleFilter exclude(String regex, int flags) {
		
		if (Util.isEmpty(regex)) return this;
		
		excludePatterns.add(0, Pattern.compile(regex, flags));
		
		return this;
	}

	
	/**
	 * Filter configuration method.
	 * @param languages
	 * @return
	 */
	public LocaleFilter excludeLanguage(String ... languages) {
		
		if (Util.isEmpty(languages)) return this;
		
		for (String language : languages)
			excludeLanguage(language);

		return this;
	}
	
	/**
	 * 
	 */
	public LocaleFilter excludeLanguage(String language) {
		
		if (Util.isEmpty(language)) return this;
		
		this.languageExcludes.add(language);
		
		return this;
	}
	
	/**
	 * Filter configuration method.
	 * @param languages
	 * @return
	 */
	public LocaleFilter excludeRegion(String ... regions) {
		
		if (Util.isEmpty(regions)) return this;
		
		for (String region : regions)
			excludeRegion(region);

		return this;
	}
	
	/**
	 * 
	 */
	public LocaleFilter excludeRegion(String region) {
		
		if (Util.isEmpty(region)) return this;
		
		this.regionExcludes.add(region);
		
		return this;
	}
		
	/**
	 * Filter configuration method.
	 * @param languages
	 * @return
	 */
	public LocaleFilter excludeUserPart(String ... userParts) {
		
		if (Util.isEmpty(userParts)) return this;
		
		for (String userPart : userParts)
			excludeUserPart(userPart);

		return this;
	}
	
	/**
	 * 
	 */
	public LocaleFilter excludeUserPart(String userPart) {
		
		if (Util.isEmpty(userPart)) return this;
		
		this.userPartExcludes.add(userPart);
		
		return this;
	}
	
	/**
	 * 
	 * @param localeId
	 * @return
	 */
	public boolean matches(LocaleId localeId) {
		
		// Excludes
		for (LocaleId item : excludes)				
			if (item.equals(localeId))
				return false;
		
		for (Pattern pattern : excludePatterns) {
			
			Matcher matcher = pattern.matcher(localeId.toString());			
			if (matcher.matches())
				return false;				
		}
		
		for (String language : languageExcludes)
			if (localeId.sameLanguageAs(language))
				return false;
		
		for (String region : regionExcludes)
			if (localeId.sameRegionAs(region))
				return false;
		
		for (String userPart : userPartExcludes)
			if (localeId.sameUserPartAs(userPart))
				return false;			
		
		
		// Includes
		for (LocaleId item : includes)				
			if (item.equals(localeId))
				return true;
		
		for (Pattern pattern : includePatterns) {
			
			Matcher matcher = pattern.matcher(localeId.toString());			
			if (matcher.matches())
				return true;				
		}
		
		for (String language : languageIncludes)
			if (localeId.sameLanguageAs(language))
				return true;
		
		for (String region : regionIncludes)
			if (localeId.sameRegionAs(region))
				return true;
		
		for (String userPart : userPartIncludes)
			if (localeId.sameUserPartAs(userPart))
				return true;
		
		switch (type) {
		
		case Any:
			return true;
			
		case None:
			return false;
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param localeIds
	 * @return
	 */
	public Set<LocaleId> filter(LocaleId ... localeIds) {
		
		Set<LocaleId> locales = new HashSet<LocaleId>();
		
		for (LocaleId localeId : locales)
			if (matches(localeId))
				locales.add(localeId);

		return locales;
	}

	public FilterType getType() {
		return type;
	}

	public List<LocaleId> getIncludes() {
		return includes;
	}

	public List<LocaleId> getExcludes() {
		return excludes;
	}

	public List<String> getLanguageIncludes() {
		return languageIncludes;
	}

	public List<String> getRegionIncludes() {
		return regionIncludes;
	}

	public List<String> getUserPartIncludes() {
		return userPartIncludes;
	}

	public List<String> getLanguageExcludes() {
		return languageExcludes;
	}

	public List<String> getRegionExcludes() {
		return regionExcludes;
	}

	public List<String> getUserPartExcludes() {
		return userPartExcludes;
	}

	public List<Pattern> getIncludePatterns() {
		return includePatterns;
	}

	public List<Pattern> getExcludePatterns() {
		return excludePatterns;
	}

}
