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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Implements a panel for selecting localization directives options.
 */
public class LDPanel extends Composite {

	public Button       m_chkUseLD;
	public Button       m_chkLocalizeOutside;
	
	public LDPanel(Composite p_Parent,
		int p_nFlags)
	{
		super(p_Parent, p_nFlags);
		createContent();
	}
	
	private void createContent () {
		GridLayout layTmp = new GridLayout();
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);

		m_chkUseLD = new Button(this, SWT.CHECK);
		m_chkUseLD.setText("Use localization directives when they are present");
		m_chkUseLD.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateDisplay();
			};
		});
		
		m_chkLocalizeOutside = new Button(this, SWT.CHECK);
		m_chkLocalizeOutside.setText("Extract items outside the scope of localization directives");
		GridData gdTmp = new GridData();
		gdTmp.horizontalIndent = 16;
		m_chkLocalizeOutside.setLayoutData(gdTmp);
		
		updateDisplay();
	}
	
	public void updateDisplay () {
		m_chkLocalizeOutside.setEnabled(m_chkUseLD.getSelection());
	}
}
