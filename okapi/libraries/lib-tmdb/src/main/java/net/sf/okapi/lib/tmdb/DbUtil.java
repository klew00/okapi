package net.sf.okapi.lib.tmdb;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;

public class DbUtil {

	private static GenericContent fmt = new GenericContent();
	
	public static String[] fragmentToTmFields (TextFragment frag) {
		String[] res = new String[2];
		res[0] = fmt.setContent(frag).toString();
		res[1] = Code.codesToString(frag.getCodes());
		return res;
	}
	
	public static String toDbLang (LocaleId locId) {
		String tmp = locId.toString();
		return tmp.replace('-', '_');
	}
}
