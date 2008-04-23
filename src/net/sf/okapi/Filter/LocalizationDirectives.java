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

package net.sf.okapi.Filter;

import net.sf.okapi.Library.Base.*;
import java.util.Stack;

class LDContext
{
	public boolean    m_bGroup;
	public boolean    m_bExtract;

	LDContext ()
	{
		m_bGroup = true;
		m_bExtract = true;
	}

	LDContext (boolean p_bGroup,
		boolean p_bExtract)
	{
		m_bGroup = p_bGroup;
		m_bExtract = p_bExtract;
	}
}

public class LocalizationDirectives implements ILocalizationDirectives {
	
	private ILog             m_Log;
	private boolean          m_bUseDirectives;
	private boolean          m_bLocalizeOutside;
	private Stack<LDContext> m_stkCtx;
	private boolean          m_bUseDNLFile;
	private DNLFile          m_DNLF = null;
	
	public LocalizationDirectives () {
		reset();
	}
	
	public void setLog (ILog p_Log) {
		m_Log = p_Log;
	}

	public boolean isInDNLList (IFilterItem p_Item) {
		if ( !m_bUseDNLFile || ( m_DNLF == null )) return false;
		return m_DNLF.Find(p_Item.getResName() + p_Item.getResType()
			+ p_Item.getText(FilterItemText.ORIGINAL));
	}

	public boolean isLocalizable (boolean p_bPopSingle) {
		boolean bRes = m_bLocalizeOutside; // Default
		// If LD not used all is always localizable
		if ( !m_bUseDirectives ) return true;
		if ( m_stkCtx.size() > 0 )
		{
			bRes = m_stkCtx.peek().m_bExtract;
			if ( p_bPopSingle )
			{
				// Pop only the non-group properties
				if ( !m_stkCtx.peek().m_bGroup )
				{
					pop();
				}
			}
		}
		return bRes;
	}

	public boolean isWithinScope () {
		return ( m_stkCtx.size() > 0 );
	}

	public void loadDNLFile (String p_sBasePath) {
		try
		{
			if ( m_DNLF == null ) m_DNLF = new DNLFile();
			m_DNLF.Load(p_sBasePath+DNLFile.EXTENSION);
		}
		catch ( Exception E )
		{
			m_Log.error(E.getMessage() + "\n" + E.getStackTrace());
		}
	}

	public boolean localizeOutside () {
		// If LD not used all is always localizable
		if ( !m_bUseDirectives ) return true;
		return m_bLocalizeOutside;
	}

	public void process (String p_sText) {
		// LD not in used.
		if ( !m_bUseDirectives ) return;

		if ( p_sText.toLowerCase().lastIndexOf("_skip") > -1 )
		{
			push(false, false);
		}
		else if ( p_sText.toLowerCase().lastIndexOf("_bskip") > -1 )
		{
			push(true, false);
		}
		else if ( p_sText.toLowerCase().lastIndexOf("_eskip") > -1 )
		{
			int nRes = getLastGroup();
			if ( nRes == -1 )
			{
				// _eskip found, but not _bskip
				m_Log.warning(Res.getString("LD_NOBSKIP"));
			}
			else if ( nRes == 1 )
			{
				// _eskip found after _btext
				m_Log.warning(Res.getString("LD_ESKIPAFTERBTEXT"));
			}
			pop();
		}
		else if ( p_sText.toLowerCase().lastIndexOf("_text") > -1 )
		{
			push(false, true);
		}
		else if ( p_sText.toLowerCase().lastIndexOf("_btext") > -1 )
		{
			push(true, true);
		}
		else if ( p_sText.toLowerCase().lastIndexOf("_etext") > -1 )
		{
			int nRes = getLastGroup();
			if ( nRes == -1 )
			{
				// _etext found, but not _btext
				m_Log.warning(Res.getString("LD_NOBTEXT"));
			}
			else if ( nRes == 0 )
			{
				// _etext found after _bskip
				m_Log.warning(Res.getString("LD_ETEXTAFTERBSKIP"));
			}
			pop();
		}
	}

	public void reset () {
		m_stkCtx = new Stack<LDContext>();
		setOptions(true, true, true);
		m_DNLF = null;
	}

	public void setOptions (boolean p_bUseDirectives,
		boolean p_bLocalizeOutside,
		boolean p_bUseDNLFile)
	{
		m_bUseDirectives = p_bUseDirectives;
		m_bLocalizeOutside = p_bLocalizeOutside;
		m_bUseDNLFile = p_bUseDNLFile;
	}

	public boolean useDirectives () {
		return m_bUseDirectives;
	}

	public boolean useDNLFile () {
		return m_bUseDNLFile;
	}

	private void popSingle () {
		// Pop only the non-group properties
		if ( m_stkCtx.size() > 0 )
		{
			if ( !m_stkCtx.peek().m_bGroup )
			{
				pop();
			}
		}
	}

	private int getLastGroup () {
		for ( int i=m_stkCtx.size(); i>-1; i-- )
		{
			if ( m_stkCtx.peek().m_bGroup )
			{
				if ( m_stkCtx.peek().m_bExtract ) return 1;
				else return 0;
			}
		}
		return -1; // No group
	}

	private void push (boolean p_bGroup,
		boolean p_bExtract)
	{
		popSingle(); // Only one single at the top
		m_stkCtx.push(new LDContext(p_bGroup, p_bExtract));
	}

	private void pop () {
		if ( m_stkCtx.size() > 0 ) m_stkCtx.pop();
	}

}
