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

package net.sf.okapi.common.locale;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.Util;

/**
 * Holds the normalized identifier for a given language/locale. 
 */
public final class LocaleId implements Comparable<Object> {

	private String locId;
	
	private static final int ID_LANGUAGE = 5;
	private static final int ID_REGION = 10;
	private static final int ID_USER = 18;
	
	private static final int POSIX_LANGUAGE= 1;
	private static final int POSIX_REGION = 3;
	private static final int POSIX_VARIANT = 7;
	
	// Java-Locale to LocaleID mapping
	private static final String[][] JAVALOCMAP = {
	//	{ Java ID,      LocaleID,
		{ "ja_JP_JP",   "ja-jp-x-calja"},
		{ "no_NO_NY",   "nn-no"},
		{ "th_TH_TH",   "th-th-x-numth"}
	};

	// Pattern to parser/validate BCP-47 language tags
	// From Addison Phillips (http://www.langtag.net/)
	private static final Pattern BCP_PATTERN = Pattern.compile(
    	"(\\A[xX]([\\x2d]\\p{Alnum}{1,8})*\\z)"
    	+ "|(((\\A\\p{Alpha}{2,8}(?=\\x2d|\\z)){1}"
    	+ "(([\\x2d]\\p{Alpha}{3})(?=\\x2d|\\z)){0,3}"
    	+ "([\\x2d]\\p{Alpha}{4}(?=\\x2d|\\z))?"
    	+ "([\\x2d](\\p{Alpha}{2}|\\d{3})(?=\\x2d|\\z))?"
    	+ "([\\x2d](\\d\\p{Alnum}{3}|\\p{Alnum}{5,8})(?=\\x2d|\\z))*)"
    	+ "(([\\x2d]([a-wyzA-WYZ](?=\\x2d))([\\x2d](\\p{Alnum}{2,8})+)*))*"
    	+ "([\\x2d][xX]([\\x2d]\\p{Alnum}{1,8})*)?)\\z");

	// Pattern to parse/validate POSIX locale identifiers
	private static final Pattern POSIXPATTERN = Pattern.compile("\\A(\\p{Alpha}{2,3})"
		+ "(_(\\p{Alpha}*?))?(\\.([\\p{Alnum}_-]*?))?(@([\\p{Alnum}_-]*?))?\\z");

	// Pattern to parse/validate LocaleID identifier
	// We may want to allow additional fields if needed, for now it's like BCP-47
	private static final Pattern ID_PATTERN = Pattern.compile(
    	"(\\A[xX]([\\x2d]\\p{Alnum}{1,8})*\\z)"
    	+ "|(((\\A\\p{Alpha}{2,8}(?=\\x2d|\\z)){1}"
    	+ "(([\\x2d]\\p{Alpha}{3})(?=\\x2d|\\z)){0,3}"
    	+ "([\\x2d]\\p{Alpha}{4}(?=\\x2d|\\z))?"
    	+ "([\\x2d](\\p{Alpha}{2}|\\d{3})(?=\\x2d|\\z))?"
    	+ "([\\x2d](\\d\\p{Alnum}{3}|\\p{Alnum}{5,8})(?=\\x2d|\\z))*)"
    	+ "(([\\x2d]([a-wyzA-WYZ](?=\\x2d))([\\x2d](\\p{Alnum}{2,8})+)*))*"
    	+ "([\\x2d][xX]([\\x2d]\\p{Alnum}{1,8})*)?)\\z");
	
	/**
	 * Creates a new LocaleID object from a locale identifier.
	 * @param locId a LocaleID string
	 * @param normalize true if it needs to be normalized the string,
	 * false to use as-it. When use as-it, the identifier is expected to be in lower-cases and use '-'
	 * for separator.
	 * @throws IllegalArgumentException if the argument in invalid.
	 */
	public LocaleId (String locId, boolean normalize) {
		if ( normalize ) this.locId = normalize(locId);
		else this.locId = locId;
	}
	
	/**
	 * Creates a new LocaleId for a given language code.
	 * This constructor does not take a locale identifier as argument, just a language identifier.
	 * Use {@link #LocaleId(String, boolean)} to create a new LocaleID from a locale identifier.
	 * @param language the language code (e.g. "de" for German).
	 * @throws IllegalArgumentException if the argument in invalid.
	 */
	public LocaleId (String language) {
		//TODO: should be prevent to pass language tag with more than the language?
		this(language, null);
	}
	
	/**
	 * Creates a new LocaleId for a given language code and region code.
	 * @param language the language code (e.g. "es" for Spanish).
	 * @param region the region code (e.g. "es" for Spain or "005" for South America.
	 * This parameter is ignored if null or empty.
	 * @throws IllegalArgumentException if the argument in invalid.
	 */
	public LocaleId (String language,
		String region)
	{
		if ( Util.isEmpty(language) ) {
			throw new IllegalArgumentException("The language cannot be null or empty.");
		}
		StringBuilder tmp = new StringBuilder(language);
		if ( !Util.isEmpty(region) ) {
			tmp.append("-");
			tmp.append(region);
		}
		locId = normalize(tmp.toString());
	}
	
	/**
	 * Creates a new LocaleId for the given Java Locale.
	 * @param loc the Java Locale object to use.
	 * @throws IllegalArgumentException if the argument in invalid.
	 */
	public LocaleId (Locale loc) {
		if ( loc == null ) {
			throw new IllegalArgumentException("The locale cannot be null.");
		}
        String tmp = loc.toString();
        for ( int i=0; i<JAVALOCMAP.length; i++) {
        	if ( JAVALOCMAP[i][0].equals(tmp) ) {
        		tmp = JAVALOCMAP[i][1];
        		break;
        	}
        }
        locId = normalize(tmp);
	}
	
	@Override
	public String toString () {
		return locId;
	}
	
	@Override
	public LocaleId clone () {
		// No need to duplicate since the object is immutable
		return this;
	}

	@Override
    public int hashCode() {
        return locId.hashCode();
    }

	/**
	 * Indicates if a given object is equal to this localeId object.
	 * @param arg the object to compare. This can be a LocaleId object
	 * or a string. Any other object will always return false. If the parameter
	 * is a string it is normalized before being compared.
	 * @return true if the parameter is the same object,
	 * or if it is a LocaleId with the same identifier,
	 * or if it is a string equals to the identifier. False otherwise.  
	 */
	public boolean equals (Object arg) {
		// We could use return (compareTo(arg)==0)
		// But we do the compare here to be faster 
		if ( arg == null ) {
			return false;
		}
		if ( this == arg ) {
			return true;
		}
		if ( arg instanceof LocaleId ) {
			return locId.equals(((LocaleId)arg).locId);
        }
		if ( arg instanceof String ) {
			return locId.equals(normalize((String)arg));   
        }
        return false;
    }

	/**
	 * Compares this LocaleId with a given object.
	 * @param arg the object to compare. If the parameter is a string it is normalized
	 * before being compared.
	 * @return 1 if the parameter is null. If the parameter is a LocaleId or 
	 * a string, the return is the same as the return of a comparison between
	 * the identifier of this LocaleId and the string representation of the argument.
	 * Otherwise the return is 1;
	 */
	public int compareTo (Object arg) {
		if ( arg == null ) {
			return 1;
		}
		if ( arg instanceof LocaleId ) {
			return locId.compareTo(((LocaleId)arg).locId);
        }
		if ( arg instanceof String ) {
			return locId.compareTo(normalize((String)arg));   
        }
		return 1;
	}
	
	/**
	 * Normalizes a LocaleId.
	 * @param locId the LocaleId to normalize.
	 * @return the normalized LocaleId.
	 */
	private String normalize (String locId) {
		if ( Util.isEmpty(locId) ) {
			throw new IllegalArgumentException("The locale identifier cannot be null or empty.");
		}
		String tmp = locId.replace('_', '-').toLowerCase();
		// Basic validation: at least two characters long
		// and starting with "cc" or "ccc" or "cc-" or "ccc-"
		int n = tmp.indexOf('-');
		if ((( n != 2 ) && ( n != 3 ) && ( n != -1)) || (tmp.length() < 2 )) {
			throw new IllegalArgumentException(String.format(
				"The locale identifier '%s' is invalid.", tmp));
		}
		return tmp;
	}

	/**
	 * Creates a new LocaleId from a locale identifier. calling this method is the same as calling
	 * <code>new LocaleId(locId, true);</code>
	 * @param locId the locale identifier to use (it will be normalized).
	 * @return a new localeId object from the given identifier.
	 * @throws IllegalArgumentException if the argument in invalid.
	 */
	static public LocaleId fromString (String locId) {
		return new LocaleId(locId, true);
	}

	/**
	 * Creates a new LocaleId from a POSIX locale identifier.
	 * @param locId the POSIX locale identifier (e.g. "de-at.UTF-8@EURO")
	 * @return a new LocaleId or null if an error occurred.
	 */
	static public LocaleId fromPOSIXLocale (String locId) {
		// POSIX syntax: language[_territory][.encoding][@modifier]
		if ( Util.isEmpty(locId) ) {
			throw new IllegalArgumentException("The locale identifier cannot be null or empty.");
		}
		Matcher m = POSIXPATTERN.matcher(locId);
		if ( m.find() ) {
//			for ( int i=1; i<m.groupCount(); i++ ) {
//				System.out.println(String.format("g=%d [%s]", i, m.group(i)));
//			}
			StringBuilder tag = new StringBuilder();
			String tmp = m.group(POSIX_LANGUAGE);
			if ( !Util.isEmpty(tmp) ) {
				tag.append(tmp);
			}
			tmp = m.group(POSIX_REGION);
			if ( !Util.isEmpty(tmp) ) {
				tag.append('-');
				tag.append(tmp);
			}
			tmp = m.group(POSIX_VARIANT);
			if ( !Util.isEmpty(tmp) ) {
				tag.append("-x-");
				tag.append(tmp);
			}
			return new LocaleId(tag.toString(), true);
		}
		else {
			throw new IllegalArgumentException(String.format(
				"The POSIX locale '%s' is invalid.", locId));
		}
	}
	
	public String toPOSIXLocaleId () {
		//TODO: Make it simpler, and complete it
		String tmp = getLanguage();
		if ( getRegion() != null ) {
			tmp += ("_" + getRegion().toUpperCase());
		}
		return tmp;
	}
	
	/**
	 * Creates a new LocaleId from a BCP-47 language tag. 
	 * @param langtag the language tag to use (e.g. "fr-CA")
	 * @return a new LocaleId, or null if an error occurred.
	 */
	static public LocaleId fromBCP47 (String langtag) {
		if ( Util.isEmpty(langtag) ) {
			throw new IllegalArgumentException("The language tag cannot be null or empty.");
		}
		Matcher m = BCP_PATTERN.matcher(langtag);
		if ( m.find() ) {
//			for ( int i=1; i<m.groupCount(); i++ ) {
//				System.out.println(String.format("g=%d [%s]", i, m.group(i)));
//			}
			// Because LocaleId is a sub-set of BCP-47 we can just normalize the cases
			// and create the object without re-checking.
			return new LocaleId(langtag.toLowerCase(), false);
		}
		else {
			throw new IllegalArgumentException(String.format(
				"The BCP-47 language tag '%s' is invalid.", langtag));
		}
	}
	
	/**
	 * Gets the BCP-47 language tag for this LocaleId.
	 * @return the BCP-47 language tag for the given LocaleId.
	 */
	public String toBCP47 () {
		//TODO: restore the casing, but BCP-47 is not case-sensitive
		return locId;
	}
	
	/** Creates a new Java Locale object from this LocaleId.
	 * @return a new Java Locale object based on the best match for the given LocaleId,
	 * or null if an error occurred.
	 */
	public Locale toJavaLocale () {
		String[] parts = null;
		// Check for special cases first
        for ( int i=0; i<JAVALOCMAP.length; i++) {
        	if ( JAVALOCMAP[i][1].equals(locId) ) {
        		parts = JAVALOCMAP[i][0].split("_");
        		break;
        	}
        }
        // Not a special one, so make it from its parts
        if ( parts == null ) {
        	parts = splitParts(locId);
        }
        // Build the Java Locale
        if ( !Util.isEmpty(parts[0]) ) {
        	if ( !Util.isEmpty(parts[1]) ) {
        		if ( !Util.isEmpty(parts[2]) ) {
        			return new Locale(parts[0], parts[1].toUpperCase(), parts[2].toUpperCase());
        		}
    			return new Locale(parts[0], parts[1].toUpperCase());
        	}
			return new Locale(parts[0]);
        }
        return Locale.getDefault();
	}

	/**
	 * Gets the language code for this LocaleId.
	 * @return the language code.
	 */
	public String getLanguage () {
		// locId should never be null, so no need to check: if ( locId == null ) return null;
		int n = locId.indexOf('-');
		if ( n > 0 ) return locId.substring(0, n);
		else return locId;
	}

	/**
	 * Gets the region code for this LocaleId.
	 * @return the region code or null if there is none or if an error occurs.
	 */
	public String getRegion () {
		try {
			Matcher m = ID_PATTERN.matcher(locId);
			m.find();
			return m.group(ID_REGION);
		}
		catch ( Throwable e ) {
			return null;
		}
	}
	
	/**
	 * Indicates if the language of a given LocaleId is the same as the one of this LocaleId.
	 * For example: "en" and "en-us" returns true, "es-es" and "ca-es" return false.
	 * @param other the LocaleId object to compare.
	 * @return true if the languages of two given LocaleIds are the same.
	 */
	public boolean sameLanguageAs (LocaleId other) {
		if ( other == null ) {
			return false; // locId is not null
		}
		return sameLanguageAs(other.toString(), false);
	}
	
	/**
	 * Indicates if a given string has the same language as the one of this LocaleId.
	 * For example: "en" and "en-us" returns true, "es-es" and "ca-es" return false.
	 * @param other the string to compare.
	 * @return true if the languages of both objects are the same.
	 */
	public boolean sameLanguageAs (String langCode) {
		return sameLanguageAs(langCode, true);
	}
	
	private boolean sameLanguageAs (String otherLocId,
		boolean normalize)
	{
		if ( otherLocId == null ) {
			return false; // locId is not null
		}
		if ( normalize ) {
			otherLocId = normalize(otherLocId);
		}
		// Get the '-' position
		int n1 = locId.indexOf('-');
		int n2 = otherLocId.indexOf('-');
		// If not present it means the whole string is the language
		if ( n1 == -1 ) n1 = locId.length();
		if ( n2 == -1 ) n2 = otherLocId.length();
		// Not the same length means they are different
		if ( n1 != n2 ) return false;
		// Now n1 == n2, and both are > -1
		for ( int i=0; i<n1; i++ ) {
			if ( locId.charAt(i) != otherLocId.charAt(i) ) return false;
		}
		return true;
	}
	
	/**
	 * Gets an array of the LocaleId objects for all the Java locales installed on the system.
	 * @return an array of the LocaleId objects for all the available Java locales.
	 */
	static public LocaleId[] getAvailableLocales () {
		Locale[] jlocales = Locale.getAvailableLocales();
		LocaleId[] locIds = new LocaleId[jlocales.length];
		for ( int i=0; i<jlocales.length; i++ ) {
			locIds[i] = new LocaleId(jlocales[i]);
		}
		return locIds;
	}

	static private String[] splitParts (String locId) {
		String[] parts = new String[3];
		Matcher m = ID_PATTERN.matcher(locId);
		if ( m.find() ) {
			parts[0] = m.group(ID_LANGUAGE);
			parts[1] = m.group(ID_REGION);
			parts[2] = m.group(ID_USER);
		}
		return parts;
	}

}
