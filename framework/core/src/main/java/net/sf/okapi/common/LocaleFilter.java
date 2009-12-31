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
public class LocaleFilter {
	
	protected static enum FilterType{
		
		/**
		 * The filter allows no locale. 
		 */
		None,
		
		/**
		 * The filter allows any locale.
		 */
		Any;
	}

	private FilterType type;
	
	private final List<LocaleId> includes;
	private final List<LocaleId> excludes;
	
	private final List<String> languageIncludes;
	private final List<String> regionIncludes;
	private final List<String> userPartIncludes;
	
	private final List<String> languageExcludes;
	private final List<String> regionExcludes;
	private final List<String> userPartExcludes;
	
	private final List<Pattern> patternIncludes; 
	private final List<Pattern> patternExcludes; 
		
	/**
	 * Private constructor.
	 * @param type filter type, either None or Any. 
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
		
		patternIncludes = new ArrayList<Pattern>();
		patternExcludes = new ArrayList<Pattern>();
		
		this.type = type;
	}
	
	/**
	 * Public constructor. Sets filter type to Any. 
	 */
	public LocaleFilter() {
		
		this(FilterType.Any);		
	}
	
	/**
	 * Public constructor. Constructs filter from a string.
	 * @param string the configuration string of locale filter. See {@link #fromString} for details.
	 */
	public LocaleFilter(String string) {
		
		this();
		fromString(string);
	}
	
	/**
	 * Creates a filter of the Any type.
	 * @return a newly created locale filter.
	 */
	public static LocaleFilter any() {
		
		return new LocaleFilter();
	}
	
	/**
	 * Creates a filter of the None type.
	 * @return a newly created locale filter.
	 */
	public static LocaleFilter none() {
		
		return new LocaleFilter(FilterType.None);
	}
	
	/**
	 * Creates a filter allowing only the given locales.
	 * @param localeIds an array of allowed LocaleId objects.
	 * @return a newly created locale filter.
	 */
	public static LocaleFilter anyOf(LocaleId ... localeIds) {
		
		if (Util.isEmpty(localeIds)) return none();
		
		LocaleFilter filter = none();		
		filter.include(localeIds);
		
		return filter;
	}
	
	/**
	 * Creates a filter filtering out the given locales.
	 * @param localeIds an array of the LocaleId objects to disallow.
	 * @return a newly created locale filter.
	 */
	public static LocaleFilter anyExcept(LocaleId ... localeIds) {
		
		LocaleFilter filter = any();
		filter.exclude(localeIds);
		
		return filter;
	}

	/**
	 * Builds a filter from a configuration string. See {@link #fromString} for details on the string format.
	 * @param string the parameters string.
	 * @return a newly created locale filter object.
	 */
	public static LocaleFilter build(String string) {
		
		return new LocaleFilter(string);
	}
	
	/**
	 * Helper method.
	 */
	private void removeFromExcludes(LocaleId localeId) {
	
		for (int i = excludes.size() - 1; i >= 0; i--) {
			
			LocaleId item = excludes.get(i);
			if (item == null) continue;
			
			if (item.equals(localeId))
				excludes.remove(i);
		}
	}
	
	/**
	 * Helper method.
	 */
	private void removeFromExcludes(List<LocaleId> localeIds) {
		
		if (localeIds == null) return;
		
		for (LocaleId localeId : localeIds)
			removeFromExcludes(localeId);
	}
	
	/**
	 * Helper method.
	 */
	private void removeFromLanguageExcludes(String languageMask) {
		
		languageExcludes.remove(languageMask);  // ArrayList.remove() uses equals() to compare objects
	}
	
	/**
	 * Helper method.
	 */
	private void removeFromLanguageExcludes(List<String> languageMasks) {
		
		if (languageMasks == null) return;
		
		for (String languageMask : languageMasks)
			removeFromLanguageExcludes(languageMask);
	}
	
	/**
	 * Helper method.
	 */
	private void removeFromRegionExcludes(String regionMask) {
		
		regionExcludes.remove(regionMask);  // ArrayList.remove() uses equals() to compare objects
	}
	
	/**
	 * Helper method.
	 */
	private void removeFromRegionExcludes(List<String> regionMasks) {
		
		if (regionMasks == null) return;
		
		for (String regionMask : regionMasks)
			removeFromRegionExcludes(regionMask);
	}

	/**
	 * Helper method.
	 */
	private void removeFromUserPartExcludes(String userPartMask) {
		
		userPartExcludes.remove(userPartMask);  // ArrayList.remove() uses equals() to compare objects
	}
	
	/**
	 * Helper method.
	 */
	private void removeFromUserPartExcludes(List<String> userPartMasks) {
		
		if (userPartMasks == null) return;
		
		for (String userPartMask : userPartMasks)
			removeFromUserPartExcludes(userPartMask);
	}

	/**
	 * Helper method.
	 */
	private void removeFromPatternExcludes(String regex, int flags) {
		
		for (int i = patternExcludes.size() - 1; i >= 0; i--) {
			
			Pattern item = patternExcludes.get(i);
			if (item == null) continue;
			
			if (item.toString().equals(regex) && (item.flags() == flags))
				patternExcludes.remove(i);
		}
	}
	
	/**
	 * Helper method.
	 */
	private void removeFromPatternExcludes(List<Pattern> patterns) {
		
		if (patterns == null) return;
		
		for (Pattern pattern : patterns)
			if (pattern != null)
				removeFromPatternExcludes(pattern.toString(), pattern.flags());
	}
	
	/**
	 * 
	 * @param localeId
	 * @return
	 */
	public LocaleFilter include(LocaleId localeId) {
		
		if (localeId == null) return this;
		
		removeFromExcludes(localeId);
		this.includes.add(0, localeId); // Later-added are attended first in matches()
		
		return this;
	}
	
	/**
	 * 
	 * @param localeIds
	 * @return
	 */
	public LocaleFilter include(LocaleId ... localeIds) {
		
		if (Util.isEmpty(localeIds)) return this;
		
		for (LocaleId localeId : localeIds)
			include(localeId);
				
		return this;
	}
	
	/**
	 * 
	 * @param localeIds
	 * @return
	 */
	public LocaleFilter include(Set<LocaleId> localeIds) {
		
		if (localeIds == null) return this;
		
		for (LocaleId localeId : localeIds)
			include(localeId);
		
		return this;
	}
	
	/**
	 * 
	 * @param localeIds
	 * @return
	 */
	public LocaleFilter include(List<LocaleId> localeIds) {
		
		if (localeIds == null) return this;
		
		for (LocaleId localeId : localeIds)
			include(localeId);
		
		return this;
	}
	
	/**
	 * 
	 * @param filter
	 * @return
	 */
	public LocaleFilter include(LocaleFilter filter) {
		
		if (filter == null) return this;
		
		includes.addAll(0, filter.getIncludes());
		removeFromExcludes(filter.getIncludes());
		
		languageIncludes.addAll(0, filter.getLanguageIncludes());
		removeFromLanguageExcludes(filter.getLanguageIncludes());
		
		regionIncludes.addAll(0, filter.getRegionIncludes());
		removeFromRegionExcludes(filter.getRegionIncludes());
		
		userPartIncludes.addAll(0, filter.getUserPartIncludes());
		removeFromUserPartExcludes(filter.getUserPartIncludes());
				
		patternIncludes.addAll(0, filter.getPatternIncludes());
		removeFromPatternExcludes(filter.getPatternIncludes());
		
		return this;
	}
	
	/**
	 * 
	 * @param regex
	 * @param flags
	 * @return
	 */
	public LocaleFilter includePattern(String regex, int flags) {
		
		if (Util.isEmpty(regex)) return this;
		
		removeFromPatternExcludes(regex, flags);
		patternIncludes.add(0, Pattern.compile(regex, flags));
		
		return this;
	}
	
	/**
	 * 
	 * @param regex
	 * @return
	 */
	public LocaleFilter includePattern(String regex) {
		
		return includePattern(regex, 0);
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
	 * @param language
	 * @return
	 */
	public LocaleFilter includeLanguage(String language) {
		
		if (Util.isEmpty(language)) return this;
		
		String languageMask = language;
		removeFromLanguageExcludes(languageMask);
		this.languageIncludes.add(languageMask);
		
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
	 * @param region
	 * @return
	 */
	public LocaleFilter includeRegion(String region) {
		
		if (Util.isEmpty(region)) return this;
		
		String regionMask = "xx-" + region;
		removeFromRegionExcludes(regionMask);
		this.regionIncludes.add(regionMask);
		
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
	 * @param userPart
	 * @return
	 */
	public LocaleFilter includeUserPart(String userPart) {
		
		if (Util.isEmpty(userPart)) return this;
		
		String userPartMask = "xx-xx-x-" + userPart;
		removeFromUserPartExcludes(userPartMask);
		this.userPartIncludes.add(userPartMask);
		
		return this;
	}
	
	/**
	 * Disallows a given LocaleId object.
	 * @param localeId the given LocaleId object to disallow.
	 * @return this locale filter.
	 */
	public LocaleFilter exclude(LocaleId localeId) {
		
		if (localeId == null) return this;
		
		this.excludes.add(0, localeId); // Later-added are attended first in matches()
		
		return this;
	}
	
	/**
	 * 
	 * @param localeIds
	 * @return
	 */
	public LocaleFilter exclude(LocaleId ... localeIds) {
		
		if (Util.isEmpty(localeIds)) return this;
		
		for (LocaleId localeId : localeIds)
			exclude(localeId);
				
		return this;
	}
	
	/**
	 * 
	 * @param localeIds
	 * @return
	 */
	public LocaleFilter exclude(Set<LocaleId> localeIds) {
		
		if (localeIds == null) return this;
		
		for (LocaleId localeId : localeIds)
			exclude(localeId);
				
		return this;
	}
	
	/**
	 * Disallows a given list of LocaleId objects.
	 * @param localeIds the given list of LocaleId objects.
	 * @return this locale filter.
	 */
	public LocaleFilter exclude(List<LocaleId> localeIds) {
		
		if (localeIds == null) return this;
		
		for (LocaleId localeId : localeIds)
			exclude(localeId);
				
		return this;
	}
	
	/**
	 * Disallows in this filter everything allowed in a given filter.
	 * @param filter the given filter.
	 * @return this locale filter.
	 */
	public LocaleFilter exclude(LocaleFilter filter) {
		
		if (filter == null) return this;
		
		excludes.addAll(0, filter.getIncludes());
		
		languageExcludes.addAll(0, filter.getLanguageIncludes());
		regionExcludes.addAll(0, filter.getRegionIncludes());
		userPartExcludes.addAll(0, filter.getUserPartIncludes());
		
		patternExcludes.addAll(0, filter.getPatternIncludes());
		
		return this;
	}
	
	/**
	 * 
	 * @param regex
	 * @param flags
	 * @return
	 */
	public LocaleFilter excludePattern(String regex, int flags) {
		
		if (Util.isEmpty(regex)) return this;
		
		patternExcludes.add(0, Pattern.compile(regex, flags));
		
		return this;
	}
	
	/**
	 * 
	 * @param regex
	 * @return
	 */
	public LocaleFilter excludePattern(String regex) {
		
		return excludePattern(regex, 0);
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
	 * @param language
	 * @return
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
	 * @param region
	 * @return
	 */
	public LocaleFilter excludeRegion(String region) {
		
		if (Util.isEmpty(region)) return this;
		
		this.regionExcludes.add("xx-" + region);
		
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
	 * @param userPart
	 * @return
	 */
	public LocaleFilter excludeUserPart(String userPart) {
		
		if (Util.isEmpty(userPart)) return this;
		
		this.userPartExcludes.add("xx-xx-x-" + userPart);
		
		return this;
	}

	/**
	 * 
	 * @return
	 */
	public LocaleFilter reset() {
		
		includes.clear();
		excludes.clear();
		
		languageIncludes.clear();
		regionIncludes.clear();
		userPartIncludes.clear();
		
		languageExcludes.clear();
		regionExcludes.clear();
		userPartExcludes.clear();
		
		patternIncludes.clear(); 
		patternExcludes.clear();
		
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
		
		for (Pattern pattern : patternExcludes) {
			
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
		
		for (Pattern pattern : patternIncludes) {
			
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
		
		for (LocaleId localeId : localeIds)
			if (matches(localeId))
				locales.add(localeId);

		return locales;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<LocaleId> getIncludes() {
		return includes;
	}

	/**
	 * 
	 * @return
	 */
	public List<LocaleId> getExcludes() {
		return excludes;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getLanguageIncludes() {
		return languageIncludes;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getRegionIncludes() {
		return regionIncludes;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getUserPartIncludes() {
		return userPartIncludes;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getLanguageExcludes() {
		return languageExcludes;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getRegionExcludes() {
		return regionExcludes;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getUserPartExcludes() {
		return userPartExcludes;
	}

	/**
	 * 
	 * @return
	 */
	public List<Pattern> getPatternIncludes() {
		return patternIncludes;
	}

	/**
	 * 
	 * @return
	 */
	public List<Pattern> getPatternExcludes() {
		return patternExcludes;
	}

	/**
	 * 
	 * @return
	 */
	protected FilterType getType() {
		return type;
	}
	
	/**
	 * 
	 * @param type
	 */
	protected void setType(FilterType type) {
		this.type = type;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		
		return 
			Util.isEmpty(includes) &&
			Util.isEmpty(excludes) &&
			
			Util.isEmpty(languageIncludes) && 
			Util.isEmpty(regionIncludes) && 
			Util.isEmpty(userPartIncludes) &&
			
			Util.isEmpty(languageExcludes) && 
			Util.isEmpty(regionExcludes) && 
			Util.isEmpty(userPartExcludes) &&
			
			Util.isEmpty(patternIncludes) && 
			Util.isEmpty(patternExcludes); 
	}
	
	/**
	 * Helper method.
	 */
	private void setFilterType(boolean excludeMode) {
		
		if (excludeMode)
			setType(FilterType.None);
		else
			setType(FilterType.Any);
	}
	
	/**
	 * Helper method.
	 */
	private void setLanguage(boolean excludeMode, String language) {
		
		if (Util.isEmpty(language)) return;
		
		if (excludeMode)
			excludeLanguage(language);
		else
			includeLanguage(language);
	}
	
	/**
	 * Helper method.
	 */
	private void setRegion(boolean excludeMode, String region) {
		
		if (Util.isEmpty(region)) return;
		
		if (excludeMode)
			excludeRegion(region);
		else
			includeRegion(region);
	}
	
	/**
	 * Helper method.
	 */
	private void setUserPart(boolean excludeMode, String userPart) {
		
		if (Util.isEmpty(userPart)) return;
		
		if (excludeMode)
			excludeUserPart(userPart);
		else
			includeUserPart(userPart);
	}
	
	/**
	 * Helper method.
	 */
	private void setPattern(boolean excludeMode, String pattern, int flags) {
		
		if (Util.isEmpty(pattern)) return;
		
		if (excludeMode)
			excludePattern(pattern, flags);
		else
			includePattern(pattern, flags);
	}
	
	private void setLocale(boolean excludeMode, String language, String region) {
		
		if (Util.isEmpty(language)) return;
		if (Util.isEmpty(region)) return;
		
		if (excludeMode)
			exclude(new LocaleId(language, region));
		else
			include(new LocaleId(language, region));
	}
	
	/**
	 * Helper method.
	 */
	private void setLocale(boolean excludeMode, String language, String region, String userPart) {
		
		if (Util.isEmpty(language)) return;
		if (Util.isEmpty(region)) return;
		if (Util.isEmpty(userPart)) return;
		
		if (excludeMode)
			exclude(new LocaleId(language, region, userPart));
		else
			include(new LocaleId(language, region, userPart));
	}
	
	/**
	 * 
	 * @param string
	 * The string consists of chunks. The chunks are delimited with a comma or space.<p>
	 * <b>*</b> -- field mask, can be used in either language, region, or user part fields.<br>  
	 * <b>!</b> -- exclude prefix.<br>
	 * <b>@</b> -- regex prefix.<br>
	 * <b>^</b> -- regex flags prefix.<p>
	 * <b>Examples:</b>
	 * 
	 * <li>all locales except English: <b>!en</b><br>
	 * <li>only English locales except en-nz: <b>en !en-nz</b><br>
	 * <li>all locales except US region: <b>!*-us</b><br>
	 * <li>only locales with "win" as user part: <b>*-*-win</b><br>
	 * <li>regular expression with flags: <b>@e.?-us ^8</b>
	 *   
	 */
	public LocaleFilter fromString(String string) {
			
		String commaPlaceholder = "<comma>";
		
		// Protect commas in regex
		string = RegexUtil.replaceAll(string, "\\{.*?(,).*?\\}", 1, commaPlaceholder);
		
		String[] commaChunks = StringUtil.split(string, ",\\p{Space}*");
		String[] spaceChunks = StringUtil.split(string, "[^,\\p{Space}](\\p{Space}+)", 1);
		String[] chunks = commaChunks;
		
		if (commaChunks.length < spaceChunks.length)
			chunks = spaceChunks;
		
		for (int i = 0; i < chunks.length; i++) {
			
			if (chunks[i].contains(commaPlaceholder))
				// Restore commas
				chunks[i] = chunks[i].replaceAll(commaPlaceholder, ",");
		}
		
		reset();
		
		// The filter type is figured out from the first chunk, always Any if no chunks are found (an empty string)
		if (chunks.length > 0 && !chunks[0].startsWith("!"))
			setType(FilterType.None);
		else
			setType(FilterType.Any);
		
		for (int i = 0; i < chunks.length; i++) {
		
			String chunk = chunks[i];
			
			boolean excludeMode = chunk.startsWith("!");
			if (excludeMode)
				chunk = chunk.substring(1);
			
			boolean regexMode = chunk.startsWith("@");
			if (regexMode)
				chunk = chunk.substring(1);
			
			if (!regexMode) {
				
				String[] fields = chunk.split("-");
				
				String language = "";
				String region = "";
				String userPart = "";
				
				if (fields.length > 3) // Dash in user part
					fields[2] = ListUtil.merge(fields, 2, fields.length - 1, "-");
				
				if (fields.length > 0) language = fields[0];
				if (fields.length > 1) region = fields[1];
				if (fields.length > 2) userPart = fields[2];
					
				boolean anyLanguage = language.equals("*"); 
				boolean anyRegion = region.equals("*");
				boolean anyUserPart = userPart.equals("*");
				
				// en
				if (fields.length == 1) {
									
					if (anyLanguage) // 0				
						// Explicit filter type setting: * = Any, !* = None
						setFilterType(excludeMode);						
					else // 1						
						setLanguage(excludeMode, language);
					
				// en-us
				} else if (fields.length == 2) {
					
					if (anyLanguage && anyRegion) // 00					
						// Explicit filter type setting: *-* = Any, !*-* = None
						setFilterType(excludeMode);
						
					else if (anyLanguage && !anyRegion) // 01						
						setRegion(excludeMode, region);
						
					else if (!anyLanguage && anyRegion) // 10						
						setLanguage(excludeMode, language);
						
					else if (!anyLanguage && !anyRegion) // 11						
						setLocale(excludeMode, language, region);
					
				// en-us-win
				} else if (fields.length == 3) {
						
					if (anyLanguage && anyRegion && anyUserPart) // 000						
						// Explicit filter type setting: *-*-* = Any, !*-*-* = None
						setFilterType(excludeMode);
					
					else if (anyLanguage && anyRegion && !anyUserPart) // 001						
						setUserPart(excludeMode, userPart);
						
					else if (anyLanguage && !anyRegion && anyUserPart) // 010
						setRegion(excludeMode, region);
						
					else if (anyLanguage && !anyRegion && !anyUserPart) { // 011
						
						setPattern(excludeMode, "[^-]+-" + region + "-x-" + userPart, 0);
						
					} else if (!anyLanguage && anyRegion && anyUserPart) // 100
						setLanguage(excludeMode, language);
					
					else if (!anyLanguage && anyRegion && !anyUserPart) { // 101
						
						setPattern(excludeMode, language + "-[^-]+-x-" + userPart, 0);
						
					} else if (!anyLanguage && !anyRegion && anyUserPart) // 110
						setPattern(excludeMode, language + "-" + region + "-x-[^-]+", 0);
						
					else if (!anyLanguage && !anyRegion && !anyUserPart) // 111
						setLocale(excludeMode, language, region, userPart);					
				}				
				
			} else { // regexMode
				
				if (i < chunks.length - 1) { // Not the last chunk
					
					String flagsChunk = chunks[i + 1];
					
					int flags = 0;
					if (flagsChunk.startsWith("^")) {
						
						flags = Util.strToInt(flagsChunk.substring(1), 0);
						i++; // Skip the flags chunk
					}
					
					setPattern(excludeMode, chunk, flags);
				} else
					setPattern(excludeMode, chunk, 0);
				 
			}
		}
		
		return this;
	}
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		boolean hasIncludes = (includes.size() != 0) ||				
				(patternIncludes.size() != 0) || 
				(languageIncludes.size() != 0) || 
				(regionIncludes.size() != 0) ||
				(userPartIncludes.size() != 0);
				
		boolean hasExcludes = (excludes.size() != 0) || 
				(patternExcludes.size() != 0) ||
				(languageExcludes.size() != 0) || 
				(regionExcludes.size() != 0) ||
				(userPartExcludes.size() != 0);
		
		switch (type) {
		
		case Any:
			
			// Explicitly specify the filter type if fromString() won't be able to figure it out correctly
			if (!hasExcludes && hasIncludes) {
				
				sb.append("*");
				sb.append(" ");
			}
							
			processExcludes(sb);
			processPatternExcludes(sb);
			processLanguageExcludes(sb);
			processRegionExcludes(sb);
			processUserPartExcludes(sb);
			processIncludes(sb);
			processPatternIncludes(sb);
			processLanguageIncludes(sb);
			processRegionIncludes(sb);
			processUserPartIncludes(sb);
						
			break;
						
		case None:
			
			// Explicitly specify the filter type if fromString() won't be able to figure it out correctly
			if (!hasIncludes) {
				
				sb.append("!*");
				sb.append(" ");
			}
			
			processIncludes(sb);
			processPatternIncludes(sb);
			processLanguageIncludes(sb);
			processRegionIncludes(sb);
			processUserPartIncludes(sb);
			processExcludes(sb);
			processPatternExcludes(sb);
			processLanguageExcludes(sb);
			processRegionExcludes(sb);
			processUserPartExcludes(sb);			
		}
		
		return sb.toString().trim();
	}

	/**
	 * Helper method.
	 */
	private void processUserPartIncludes(StringBuilder sb) {
		
		if (sb == null) return;
		
		for (String userPart : ListUtil.invert(userPartIncludes)) {
			
			sb.append("*-*-");
			sb.append(userPart);
			sb.append(" ");
		}		
	}

	/**
	 * Helper method.
	 */
	private void processRegionIncludes(StringBuilder sb) {
		
		if (sb == null) return;
		
		for (String region : ListUtil.invert(regionIncludes)) {
			
			sb.append("*-");
			sb.append(region);
			sb.append(" ");
		}
	}

	/**
	 * Helper method.
	 */
	private void processLanguageIncludes(StringBuilder sb) {
		
		if (sb == null) return;
		
		for (String language : ListUtil.invert(languageIncludes)) {
			
			sb.append(language);
			sb.append(" ");
		}
	}

	/**
	 * Helper method.
	 */
	private void processPatternIncludes(StringBuilder sb) {
		
		if (sb == null) return;
		
		for (Pattern pattern : ListUtil.invert(patternIncludes)) {
			
			sb.append("@");
			sb.append(pattern.toString());
			sb.append(" ");
			
			if (pattern.flags() != 0) {
				
				sb.append("^");
				sb.append(pattern.flags());
				sb.append(" ");
			}
		}
	}

	/**
	 * Helper method.
	 */
	private void processIncludes(StringBuilder sb) {
		
		if (sb == null) return;
		
		for (LocaleId localeId : ListUtil.invert(includes)) {
			
			sb.append(localeId.getLanguage());
			
			if (!Util.isEmpty(localeId.getRegion())) {
				
				sb.append("-");
				sb.append(localeId.getRegion());
				
				if (!Util.isEmpty(localeId.getUserPart())) {
					sb.append("-");
					sb.append(localeId.getUserPart());
				}
			}
			
			sb.append(" ");
		}
	}

	/**
	 * Helper method.
	 */
	private void processUserPartExcludes(StringBuilder sb) {
		
		if (sb == null) return;
		
		for (String userPart : ListUtil.invert(userPartExcludes)) {
			
			sb.append("!");
			sb.append("*-*-");
			sb.append(userPart);
			sb.append(" ");
		}
	}

	/**
	 * Helper method.
	 */
	private void processRegionExcludes(StringBuilder sb) {
		
		if (sb == null) return;
		
		for (String region : ListUtil.invert(regionExcludes)) {
		
			sb.append("!");
			sb.append("*-");
			sb.append(region);
			sb.append(" ");
		}
	}

	/**
	 * Helper method.
	 */
	private void processLanguageExcludes(StringBuilder sb) {
		
		if (sb == null) return;
		
		for (String language : ListUtil.invert(languageExcludes)) {
			
			sb.append("!");
			sb.append(language);
			sb.append(" ");
		}
	}

	/**
	 * Helper method.
	 */
	private void processPatternExcludes(StringBuilder sb) {
		
		if (sb == null) return;
		
		for (Pattern pattern : ListUtil.invert(patternExcludes)) {
			
			sb.append("!");
			sb.append("@");
			sb.append(pattern.toString());
			sb.append(" ");
			
			if (pattern.flags() != 0) {
				
				sb.append("^");
				sb.append(pattern.flags());
				sb.append(" ");
			}
		}
	}

	/**
	 * Helper method.
	 */
	private void processExcludes(StringBuilder sb) {
		
		if (sb == null) return;
		
		for (LocaleId localeId : ListUtil.invert(excludes)) {
			
			sb.append("!");
			sb.append(localeId.getLanguage());
			
			if (!Util.isEmpty(localeId.getRegion())) {
			
				sb.append("-");
				sb.append(localeId.getRegion());
				
				if (!Util.isEmpty(localeId.getUserPart())) {
					sb.append("-");
					sb.append(localeId.getUserPart());
				}
			}
						
			sb.append(" ");
		}
	}

	/**
	 * If the filter contains only explicitly listed locale IDs (no regex patterns or masks), then return the list of those locale ID.
	 * If there's at least one mask or pattern, an empty list is returned.
	 * @return
	 */
	public List<String> getExplicitLocaleIds() {

		List<String> res = new ArrayList<String>(); 
		
		// Return an empty list if there are patterns or masks
		if ((patternIncludes.size() != 0) || 
				(regionIncludes.size() != 0) || 
				(userPartIncludes.size() != 0) ||				
				(excludes.size() != 0) || 
				(patternExcludes.size() != 0) ||
				(languageExcludes.size() != 0) || 
				(regionExcludes.size() != 0) ||
				(userPartExcludes.size() != 0)) 
			return res; 

		for (LocaleId localeId : includes)
			res.add(localeId.toString());
			
		for (String language : languageIncludes)
			res.add(language);

		return res;
	}

	/**
	 * Determines if a given string contains only explicitly listed locale IDs.
	 * If the given strin contains only explicitly listed locale IDs (no regex patterns or masks), then returns 
	 * a string with a space-delimited list of those locale ID.
	 * If there's at least one mask or pattern, an empty string is returned.
	 * @param string the given locale filter configuration string. See {@link #fromString} for details.
	 * @return
	 */
	public static String getExplicitLocaleIds(String string) {
		
		LocaleFilter filter = new LocaleFilter(string);
		return ListUtil.listAsString(filter.getExplicitLocaleIds(), " ");
	}
}
