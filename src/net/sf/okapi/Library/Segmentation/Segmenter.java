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

package net.sf.okapi.Library.Segmentation;

import java.util.ArrayList;

import net.sf.okapi.Filter.FilterItem;
import net.sf.okapi.Filter.FilterItemText;
import net.sf.okapi.Filter.IFilterItem;

/**
 * Default implementation of the ISegmenter interface. 
 */
public class Segmenter implements ISegmenter {
	
	private FilterItem            m_FIText;
	private FilterItem            m_FI;
	private ArrayList<Integer>    m_aStart;
	private ArrayList<Integer>    m_aLength;
	private int                   m_nCurrent;
	

	public int Segment (String p_sText) {
		if ( m_FIText == null ) m_FIText = new FilterItem();
		m_FIText.setText(p_sText);
		return Segment(m_FIText);
	}

	public int Segment (IFilterItem p_FI) {
		String sText = p_FI.getText(FilterItemText.CODED);
		m_FI = new FilterItem();
		m_FI.copyFrom(p_FI);
		m_aStart = new ArrayList<Integer>();
		m_aLength = new ArrayList<Integer>();
		m_nCurrent = 0;
	
		//TODO
		
		return m_aStart.size();
	}

	public int getCount () {
		return (m_aStart == null) ? 0 : m_aStart.size();
	}

	public String getLanguage () {
		// TODO Auto-generated method stub
		return null;
	}

	public IFilterItem getNext () {
		if ( m_aStart == null ) return null;
		if (( m_nCurrent == 0 ) && ( m_aStart.size() == 0 ))
		{
			// Remove leading and trailing spaces
			String sText = m_FI.getText(FilterItemText.CODED);
			m_FI.setText(sText.trim());
			m_nCurrent++;
			return m_FI;
		}

		if ( m_nCurrent < m_aStart.size() )
		{
			IFilterItem FI = m_FI.extract(m_aStart.get(m_nCurrent),
				m_aLength.get(m_nCurrent), false); //TODO Data.m_bAddMissingCodes);
			m_nCurrent++;
			return FI;
		}

		// No more segment
		return null;
	}

	public void loadRules (String p_sPath) {
		// TODO Auto-generated method stub
		
	}

	public void saveRules (String p_sPath,
		boolean p_bSaveAsSRX) {
		// TODO Auto-generated method stub
		
	}

	public void setLanguage (String p_bCode) {
		// TODO Auto-generated method stub
		
	}
}
