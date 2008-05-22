/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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

package net.sf.okapi.common.ui;

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

	public Button       chkUseLD;
	public Button       chkLocalizeOutside;
	
	public LDPanel (Composite parent,
		int flags)
	{
		super(parent, flags);
		createContent();
	}
	
	private void createContent () {
		GridLayout layTmp = new GridLayout();
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);

		chkUseLD = new Button(this, SWT.CHECK);
		chkUseLD.setText("Use localization directives when they are present");
		chkUseLD.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateDisplay();
			};
		});
		
		chkLocalizeOutside = new Button(this, SWT.CHECK);
		chkLocalizeOutside.setText("Extract items outside the scope of localization directives");
		GridData gdTmp = new GridData();
		gdTmp.horizontalIndent = 16;
		chkLocalizeOutside.setLayoutData(gdTmp);
		
		updateDisplay();
	}
	
	public void updateDisplay () {
		chkLocalizeOutside.setEnabled(chkUseLD.getSelection());
	}
}
