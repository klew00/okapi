package net.sf.okapi.lib.tmdb;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.lib.tmdb.DbUtil;

public class DbUtilTest {
	
	@Test
	public void testLocaleCodes ()
	{
		String code = DbUtil.toOlifantLocaleCode(LocaleId.ENGLISH);
		assertEquals("EN", code);
		
		code = DbUtil.toOlifantLocaleCode(LocaleId.fromString("de-DE-u-email-co-phonebk-x-linux"));
		assertEquals("DE_DE-u-email-co-phonebk-x-linux", code);
		
		LocaleId locId = DbUtil.fromOlifantLocaleCode(code);
		assertEquals("de-de-u-email-co-phonebk-x-linux", locId.toString());
		
		code = DbUtil.toOlifantLocaleCode(LocaleId.fromString("de-DE"));
		assertEquals("DE_DE", code);
		
		code = DbUtil.toOlifantLocaleCode(LocaleId.fromString("es-419"));
		assertEquals("ES_419", code);
	}
}
