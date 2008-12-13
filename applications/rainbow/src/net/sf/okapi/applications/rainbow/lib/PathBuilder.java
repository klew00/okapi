/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.applications.rainbow.lib;

import java.io.File;

import net.sf.okapi.common.ParametersString;
import net.sf.okapi.common.Util;

public class PathBuilder {

	public static final int       EXTTYPE_PREPEND     = 0;
	public static final int       EXTTYPE_APPEND      = 1;
	public static final int       EXTTYPE_REPLACE     = 2;

	private boolean useSubFolder;
	private String subFolder;
	private boolean useExt;
	private String ext;
	private int extType;
	private String prefix;
	private boolean usePrefix;
	private String suffix;
	private boolean useSuffix;
	private boolean useReplace;
	private String search;
	private String replace;

	static public String replaceVariables (String p_sInput,
		String srcLang,
		String trgLang)
	{
		// Macros
		if ( p_sInput.indexOf("${") == -1 ) return p_sInput;

		p_sInput = p_sInput.replace("${SrcLangU}", srcLang.toUpperCase());
		p_sInput = p_sInput.replace("${$SrcLang}", srcLang.toLowerCase());
		p_sInput = p_sInput.replace("${TrgLangU}", trgLang.toUpperCase());
		p_sInput = p_sInput.replace("${$TrgLang}", trgLang.toLowerCase());

//TODO		Locale LC;
		try {
//			String[] aRes = Utils.splitLanguageCode(p_sLang);
//			LC = new Locale(aRes[0], aRes[1]);
		}
		catch (Exception E ) {
			// On error, replace by error place-holder
			p_sInput = p_sInput.replace("${SrcLangNameE}", Res.getString("BLDPATH_NOVARVALUE"));
			p_sInput = p_sInput.replace("${SrcLangWin3}", Res.getString("BLDPATH_NOVARVALUE"));
			p_sInput = p_sInput.replace("${SrcLCID}", Res.getString("BLDPATH_NOVARVALUE"));
			return p_sInput.replace("${SrcLangNameN}", Res.getString("BLDPATH_NOVARVALUE"));
		}
		p_sInput = p_sInput.replace("${SrcLangNameE}", "TODO"); //CI.EnglishName);
		//TODO p_sInput = p_sInput.replace("${LWin3}", "TODO"); //CI.ThreeLetterWindowsLanguageName);
		//TODO p_sInput = p_sInput.replace("${LCID}", "TODO"); //CI.LCID.ToString());
		return p_sInput.replace("${SrcLangNameN}", "TODO"); //CI.NativeName);
	}

	public PathBuilder () {
		reset();
	}
	
	public void reset () {
		useSubFolder = false;
		subFolder = "";
		useExt = true;
		ext = ".${TrgLang}";
		extType = EXTTYPE_PREPEND;
		prefix = "";
		suffix = "";
		usePrefix = false;
		useSuffix = false;
		useReplace = false;
		search = "";
		replace = "";
	}

	public void copyFrom (PathBuilder pathBuilder) {
		useSubFolder = pathBuilder.useSubFolder;
		subFolder = pathBuilder.subFolder;
		useExt = pathBuilder.useExt;
		ext = pathBuilder.ext;
		extType = pathBuilder.extType;
		prefix = pathBuilder.prefix;
		suffix = pathBuilder.suffix;
		usePrefix = pathBuilder.usePrefix;
		useSuffix = pathBuilder.useSuffix;
		useReplace = pathBuilder.useReplace;
		search = pathBuilder.search;
		replace = pathBuilder.replace;
	}

	public String getSubfolder () {
		return subFolder;
	}
	
	public void setSubfolder (String p_sValue) {
		subFolder = p_sValue;
	}

	public boolean useSubfolder () {
		return useSubFolder;
	}
	
	public void setUseSubfolder (boolean p_bValue) {
		useSubFolder = p_bValue;
	}

	public String getExtension () {
		return ext;
	}
	
	public void setExtension (String p_sValue) {
		ext = p_sValue;
	}

	public boolean useExtension () {
		return useExt;
	}
	
	public void setUseExtension (boolean p_bValue) {
		useExt = p_bValue;
	}

	public int getExtensionType () {
		return extType;
	}
	
	public void setExtensionType (int p_nValue) {
		extType = p_nValue;
	}

	public String getPrefix () {
		return prefix;
	}
	
	public void setPrefix (String p_sValue) {
		prefix = p_sValue;
	}

	public boolean usePrefix () {
		return usePrefix;
	}
	
	public void setUsePrefix (boolean p_bValue) {
		usePrefix = p_bValue;
	}

	public String getSuffix () {
		return suffix;
	}
	
	public void setSuffix (String p_sValue) {
		suffix = p_sValue;
	}

	public boolean useSuffix () {
		return useSuffix;
	}
	
	public void setUseSuffix (boolean p_bValue) {
		useSuffix = p_bValue;
	}

	public String getSearch () {
		return search;
	}
	
	public void setSearch (String p_sValue) {
		search = p_sValue;
	}

	public String getReplace () {
		return replace;
	}
	
	public void setReplace (String p_sValue) {
		replace = p_sValue;
	}

	public boolean useReplace () {
		return useReplace;
	}
	
	public void setUseReplace (boolean p_bValue) {
		useReplace = p_bValue;
	}
	
	@Override
	public String toString () {
		ParametersString tmp = new ParametersString();
		tmp.setBoolean("useSubFolder", useSubfolder());
		tmp.setString("subFolder", getSubfolder());
		tmp.setBoolean("useExtension", useExtension());
		tmp.setInteger("extensionType", getExtensionType());
		tmp.setString("extension", getExtension());
		tmp.setBoolean("usePrefix", usePrefix());
		tmp.setString("prefix", getPrefix());
		tmp.setBoolean("useSuffix", useSuffix());
		tmp.setString("suffix", getSuffix());
		tmp.setBoolean("useReplace", useReplace());
		tmp.setString("search", getSearch());
		tmp.setString("replace", getReplace());
		return tmp.toString();
	}

	/* Not used currently
	public void fromString (String data) {
		reset();
		ParametersString tmp = new ParametersString(data);
		setUseSubfolder(tmp.getBoolean("useSubFolder", m_bUseSubfolder));
		setSubfolder(tmp.getString("subFolder", m_sSubfolder));
		setUseExtension(tmp.getBoolean("useExtension", m_bUseExt));
		setExtensionType(tmp.getInteger("extensionType", m_nExtType));
		setExtension(tmp.getString("extension", m_sExt));
		setUsePrefix(tmp.getBoolean("usePrefix", m_bUsePrefix));
		setPrefix(tmp.getString("prefix", m_sPrefix));
		setUseSuffix(tmp.getBoolean("useSuffix", m_bUseSuffix));
		setSuffix(tmp.getString("suffix", m_sSuffix));
		setUseReplace(tmp.getBoolean("useReplace", m_bUseReplace));
		setSearch(tmp.getString("search", m_sSearch));
		setReplace(tmp.getString("replace", m_sReplace));
	}*/

	/**
	 * Transforms a given full path to a new path.
	 * @param p_sFullPath The path to transform.
	 * @param p_sOriginalRoot Root in the path to transform.
	 * @param p_sNewRoot New root to use.  If p_sNewRoot is null, 
	 * p_sOriginalRoot is used.
	 * @param srcLang Language code.
	 * @return The transformed path.
	 */
	public String getPath (String p_sFullPath,
		String p_sOriginalRoot,
		String p_sNewRoot,
		String srcLang,
		String trgLang)
	{
		String sPath = p_sFullPath.substring(p_sOriginalRoot.length());

		// Extension
		String sExt = Utils.getExtension(sPath);
		if ( useExtension() ) {
			switch ( getExtensionType() ) {
				case EXTTYPE_REPLACE:
					sExt = getExtension();
					break;
				case EXTTYPE_APPEND:
					sExt += getExtension();
					break;
				case EXTTYPE_PREPEND:
					sExt = getExtension() + sExt;
					break;
			}
		}

		String sFile = Util.getFilename(sPath, false);
		if ( usePrefix() ) {
			sFile = getPrefix() + sFile;
		}
		if ( useSuffix() ) {
			sFile += getSuffix();
		}

		String sTmp = (((p_sNewRoot==null)||(p_sNewRoot.length()==0))
			? p_sOriginalRoot : p_sNewRoot) + File.separatorChar;
		String sSub = getSubfolder();
		if ( !useSubfolder() ) {
			sSub = Util.getDirectoryName(sPath);
			if ( sSub.length() != 0 ) sSub = sSub.substring(1);
		}
		if (( sSub != null ) && ( sSub.length() > 0 ))
			sTmp += (sSub + File.separatorChar);
		sTmp += (sFile + sExt);

		// Search/Replace text if needed
		if ( useReplace() && ( getSearch().length() != 0 ))
			sTmp = sTmp.replace(getSearch(), getReplace());

		return replaceVariables(sTmp, srcLang, trgLang);
	}
}
