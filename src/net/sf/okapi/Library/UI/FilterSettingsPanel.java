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

package net.sf.okapi.Library.UI;

import net.sf.okapi.Filter.FilterAccess;
import net.sf.okapi.Library.Base.Utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Implements a common UI to select a filter settings string.
 */
public class FilterSettingsPanel extends Composite {

	FilterAccess        m_FA;
	Text                m_edSettings;
	Button              m_btEdit;
	Shell               m_ShellForEditors;
	
	public FilterSettingsPanel(Composite p_Parent,
		int p_nFlags)
	{
		super(p_Parent, SWT.NONE);
		createContent();
	}
	
	private void createContent () {
		GridLayout layTmp = new GridLayout();
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);

		m_edSettings = new Text(this, SWT.BORDER);
		m_edSettings.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		m_btEdit = new Button(this, SWT.PUSH);
		m_btEdit.setText("Edit...");
		GridData gdTmp = new GridData();
		gdTmp.widthHint = 80;
		m_btEdit.setLayoutData(gdTmp);
		m_btEdit.addSelectionListener(new SelectionListener () {
			public void widgetSelected(SelectionEvent e) {
				editParameters();
			}
			public void widgetDefaultSelected(SelectionEvent e) {};
		});
	}
	
	public void setData (String p_sFSettings,
		FilterAccess p_FA) {
		m_FA = p_FA;
		m_edSettings.setText(p_sFSettings);
	}
	
	public String getData () {
		return m_edSettings.getText();
	}
	
	private void editParameters () {
		try {
			String sTmp = m_edSettings.getText();
			if ( sTmp.length() == 0 ) return;
			m_FA.editFilterSettings(sTmp, false, getParent().getShell());
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
		}
	}
}
