/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.Library.Base;

import java.io.File;
import java.util.Locale;

import net.sf.okapi.Library.Base.Utils;

public class PathBuilder {
	public static final char      FLDSEP = '\b';
	public static final String    FLDSEPSTR = "\b";

	public static final int       EXTTYPE_PREPEND     = 0;
	public static final int       EXTTYPE_APPEND      = 1;
	public static final int       EXTTYPE_REPLACE     = 2;

	private boolean     m_bUseSubfolder;
	private String      m_sSubfolder;
	private boolean     m_bUseExt;
	private String      m_sExt;
	private int         m_nExtType;
	private String      m_sPrefix;
	private boolean     m_bUsePrefix;
	private String      m_sSuffix;
	private boolean     m_bUseSuffix;
	private boolean     m_bUseReplace;
	private String      m_sSearch;
	private String      m_sReplace;

	static public String replaceVariables (String p_sInput,
		String p_sLang)
	{
		// Macros
		if ( p_sInput.indexOf("<$") == -1 ) return p_sInput;

		p_sInput = p_sInput.replace("<$LCodeU>", p_sLang.toUpperCase());
		p_sInput = p_sInput.replace("<$LCodeL>", p_sLang.toLowerCase());
		p_sInput = p_sInput.replace("<$LCode>", p_sLang.toLowerCase());

		Locale LC;
		try {
			String[] aRes = Utils.splitLanguageCode(p_sLang);
			LC = new Locale(aRes[0], aRes[1]);
		}
		catch (Exception E ) {
			// On error, replace by error place-holder
			p_sInput = p_sInput.replace("<$LNameE>", Res.getString("BLDPATH_NOVARVALUE"));
			p_sInput = p_sInput.replace("<$LWin3>", Res.getString("BLDPATH_NOVARVALUE"));
			p_sInput = p_sInput.replace("<$LCID>", Res.getString("BLDPATH_NOVARVALUE"));
			return p_sInput.replace("<$LNameN>", Res.getString("BLDPATH_NOVARVALUE"));
		}
		p_sInput = p_sInput.replace("<$LNameE>", "TODO"); //CI.EnglishName);
		//TODO p_sInput = p_sInput.replace("<$LWin3>", "TODO"); //CI.ThreeLetterWindowsLanguageName);
		//TODO p_sInput = p_sInput.replace("<$LCID>", "TODO"); //CI.LCID.ToString());
		return p_sInput.replace("<$LNameN>", "TODO"); //CI.NativeName);
	}

	public PathBuilder () {
		reset();
	}

	public String getSubfolder () {
		return m_sSubfolder;
	}
	
	public void setSubfolder (String p_sValue) {
		m_sSubfolder = p_sValue;
	}

	public boolean useSubfolder () {
		return m_bUseSubfolder;
	}
	
	public void setUseSubfolder (boolean p_bValue) {
		m_bUseSubfolder = p_bValue;
	}

	public String getExtension () {
		return m_sExt;
	}
	
	public void setExtension (String p_sValue) {
		m_sExt = p_sValue;
	}

	public boolean useExtension () {
		return m_bUseExt;
	}
	
	public void setUseExtension (boolean p_bValue) {
		m_bUseExt = p_bValue;
	}

	public int getExtensionType () {
		return m_nExtType;
	}
	
	public void setExtensionType (int p_nValue) {
		m_nExtType = p_nValue;
	}

	public String getPrefix () {
		return m_sPrefix;
	}
	
	public void setPrefix (String p_sValue) {
		m_sPrefix = p_sValue;
	}

	public boolean usePrefix () {
		return m_bUsePrefix;
	}
	
	public void setUsePrefix (boolean p_bValue) {
		m_bUsePrefix = p_bValue;
	}

	public String getSuffix () {
		return m_sSuffix;
	}
	
	public void setSuffix (String p_sValue) {
		m_sSuffix = p_sValue;
	}

	public boolean useSuffix () {
		return m_bUseSuffix;
	}
	
	public void setUseSuffix (boolean p_bValue) {
		m_bUseSuffix = p_bValue;
	}

	public String getSearch () {
		return m_sSearch;
	}
	
	public void setSearch (String p_sValue) {
		m_sSearch = p_sValue;
	}

	public String getReplace () {
		return m_sReplace;
	}
	
	public void setReplace (String p_sValue) {
		m_sReplace = p_sValue;
	}

	public boolean useReplace () {
		return m_bUseReplace;
	}
	
	public void setUseReplace (boolean p_bValue) {
		m_bUseReplace = p_bValue;
	}
	
	public String toString () {
		StringBuilder sbTmp = new StringBuilder();
		
		sbTmp.append((useSubfolder()?"1":"0")+FLDSEP);
		sbTmp.append(getSubfolder()+FLDSEP);

		sbTmp.append((useExtension()?"1":"0")+FLDSEP);
		sbTmp.append(String.format("%d", getExtensionType())+FLDSEP);
		sbTmp.append(getExtension()+FLDSEP);

		sbTmp.append((usePrefix()?"1":"0")+FLDSEP);
		sbTmp.append(getPrefix()+FLDSEP);
		sbTmp.append((useSuffix()?"1":"0")+FLDSEP);
		sbTmp.append(getSuffix()+FLDSEP);

		sbTmp.append((useReplace()?"1":"0")+FLDSEP);
		sbTmp.append(getSearch()+FLDSEP);
		sbTmp.append(getReplace()+FLDSEP);

		return sbTmp.toString();
	}


	public void fromString (String p_sData) {
		reset();
		if (( p_sData == null ) || ( p_sData.length() == 0 )) return;
		String[] aFld = p_sData.split(FLDSEPSTR, -2);

		setUseSubfolder(aFld[0].equals("1"));
		setSubfolder(aFld[1]);

		setUseExtension(aFld[2].equals("1"));
		setExtensionType(Integer.valueOf(aFld[3]));
		setExtension(aFld[4]);

		setUsePrefix(aFld[5].equals("1"));
		setPrefix(aFld[6]);
		setUseSuffix(aFld[7].equals("1"));
		setSuffix(aFld[8]);

		setUseReplace(aFld[9].equals("1"));
		setSearch(aFld[10]);
		setReplace(aFld[11]);
	}

	public void reset () {
		m_bUseSubfolder = false;
		m_sSubfolder = "";
		m_bUseExt = true;
		m_sExt = ".<$LCode>";
		m_nExtType = EXTTYPE_PREPEND;
		m_sPrefix = "";
		m_sSuffix = "";
		m_bUsePrefix = false;
		m_bUseSuffix = false;
		m_bUseReplace = false;
		m_sSearch = "";
		m_sReplace = "";
	}

	/**
	 * Transforms a given full path to a new path.
	 * @param p_sFullPath The path to transform.
	 * @param p_sOriginalRoot Root in the path to transform.
	 * @param p_sNewRoot New root to use.  If p_sNewRoot is null, 
	 * p_sOriginalRoot is used.
	 * @param p_sLang Language code.
	 * @return The transformed path.
	 */
	public String getPath (String p_sFullPath,
		String p_sOriginalRoot,
		String p_sNewRoot,
		String p_sLang)
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

		String sFile = Utils.getFilename(sPath, false);
		if ( usePrefix() ) {
			sFile = getPrefix() + sFile;
		}
		if ( useSuffix() ) {
			sFile += getSuffix();
		}

		String sTmp = (p_sNewRoot==null ? p_sOriginalRoot : p_sNewRoot) + File.separatorChar;
		String sSub = getSubfolder();
		if ( !useSubfolder() ) {
			sSub = Utils.getDirectoryName(sPath);
			if ( sSub.length() != 0 ) sSub = sSub.substring(1);
		}
		if (( sSub != null ) && ( sSub.length() > 0 ))
			sTmp += (sSub + File.separatorChar);
		sTmp += (sFile + sExt);

		// Search/Replace text if needed
		if ( useReplace() && ( getSearch().length() != 0 ))
			sTmp = sTmp.replace(getSearch(), getReplace());

		return replaceVariables(sTmp, p_sLang);
	}
}
