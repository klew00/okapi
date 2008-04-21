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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class AboutForm {
	
	private Shell            m_Shell;

	public AboutForm (Shell p_Parent,
		String p_sProduct,
		String p_sVersion)
	{
		m_Shell = new Shell(p_Parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		m_Shell.setText("About Borneo");
		m_Shell.setImage(p_Parent.getImage());
		GridLayout layTmp = new GridLayout();
		m_Shell.setLayout(layTmp);
		
		Composite cmpTmp = new Composite(m_Shell, SWT.BORDER);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		cmpTmp.setLayoutData(gdTmp);
		
		CLabel stProduct = new CLabel(cmpTmp, SWT.NONE);
		stProduct.setText(p_sProduct);
		
		CLabel stVersion = new CLabel(cmpTmp, SWT.NONE);
		stVersion.setText(p_sVersion);
		
		// Close
		Button btClose = new Button(m_Shell, SWT.PUSH);
		btClose.setText("Close");
		gdTmp = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gdTmp.widthHint = 80;
		btClose.setLayoutData(gdTmp);
		btClose.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_Shell.close();
			};
		});

		m_Shell.pack();
		Rectangle Rect = m_Shell.getBounds();
		m_Shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(m_Shell, p_Parent);
	}
	
	public void showDialog () {
		m_Shell.open();
		while ( !m_Shell.isDisposed() ) {
			if ( !m_Shell.getDisplay().readAndDispatch() )
				m_Shell.getDisplay().sleep();
		}
	}
}
