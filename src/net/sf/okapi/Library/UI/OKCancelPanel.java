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

import net.sf.okapi.Library.Base.Utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Default panel for Help/Ok/cancel buttons
 */
public class OKCancelPanel extends Composite {

	public Button       m_btOK;
	public Button       m_btCancel;
	public Button       m_btHelp;

	/**
	 * Creates a new panel for Help/OK/Cancel buttons.
	 * @param p_Parent Parent control.
	 * @param p_nFlags Style flags.
	 * @param p_Action Action to execute when any of the buttons is clicked.
	 * The receiving event, the widget'sa data is marked: 'c' for the Cancel
	 * button, 'o' for OK, and 'h' for help.
	 * @param p_bShowHelp True to display the Help button.
	 */
	public OKCancelPanel(Composite p_Parent,
		int p_nFlags,
		SelectionAdapter p_Action,
		boolean p_bShowHelp)
	{
		super(p_Parent, SWT.NONE);
		createContent(p_Action, p_bShowHelp);
	}
	
	private void createContent (SelectionAdapter p_Action,
		boolean p_bShowHelp)
	{
		GridLayout layTmp = new GridLayout(2, true);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);
		int nWidth = 80;

		m_btHelp = new Button(this, SWT.PUSH);
		m_btHelp.setText("Help");
		m_btHelp.setData("h");
		m_btHelp.addSelectionListener(p_Action);
		GridData gdTmp = new GridData();
		gdTmp.widthHint = nWidth;
		m_btHelp.setLayoutData(gdTmp);
		m_btHelp.setVisible(p_bShowHelp);
		
		Composite cmpTmp = new Composite(this, SWT.NONE);
		RowLayout layRow = new RowLayout(SWT.HORIZONTAL);
		layRow.marginWidth = 0;
		layRow.marginHeight = 0;
		cmpTmp.setLayout(layRow);
		gdTmp = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gdTmp.grabExcessHorizontalSpace = true;
		cmpTmp.setLayoutData(gdTmp);

		// Create the buttons in a platform-specific order
		if ( Utils.getPlatformType() == Utils.PFTYPE_WIN ) {
			m_btOK = new Button(cmpTmp, SWT.PUSH);
			m_btCancel = new Button(cmpTmp, SWT.PUSH);
		}
		else {
			m_btCancel = new Button(cmpTmp, SWT.PUSH);
			m_btOK = new Button(cmpTmp, SWT.PUSH);
		}

		m_btOK.setText("OK");
		m_btOK.setData("o");
		m_btOK.addSelectionListener(p_Action);
		RowData rdTmp = new RowData();
		rdTmp.width = nWidth;
		m_btOK.setLayoutData(rdTmp);
		
		m_btCancel.setText("Cancel");
		m_btCancel.setData("c");
		m_btCancel.addSelectionListener(p_Action);
		rdTmp = new RowData();
		rdTmp.width = nWidth;
		m_btCancel.setLayoutData(rdTmp);
	}
	
	public void setOKText (String p_sText) {
		m_btOK.setText(p_sText);
	}
}
