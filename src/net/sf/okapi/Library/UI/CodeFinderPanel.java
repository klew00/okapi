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

import net.sf.okapi.Filter.InlineCodeFinder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Implements a panel for setting rules for the inline codes finder.
 */
public class CodeFinderPanel extends Composite {

	private Button           m_chkUseCF;
	private Text             m_edExp;
	private Button           m_btEdit;
	private InlineCodeFinder m_CF;
	
	public CodeFinderPanel (Composite p_Parent,
		int p_nFlags)
	{
		super(p_Parent, p_nFlags);
		createContent();
	}
	
	public void setData (boolean p_bUseCF,
		InlineCodeFinder p_CF)
	{
		m_CF = p_CF;
		m_chkUseCF.setSelection(p_bUseCF);
		m_edExp.setText(m_CF.getExpression());
		updateDisplay();
	}
	
	private void createContent () {
		GridLayout layTmp = new GridLayout();
		setLayout(layTmp);

		m_chkUseCF = new Button(this, SWT.CHECK);
		m_chkUseCF.setText("Mark as inline codes the text parts matching this regular expression:");
		m_chkUseCF.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateDisplay();
			};
		});

		m_edExp = new Text(this, SWT.BORDER | SWT.WRAP);
		m_edExp.setEditable(false);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.widthHint = 300;
		m_edExp.setLayoutData(gdTmp);
		
		m_btEdit = new Button(this, SWT.PUSH);
		m_btEdit.setText("Edit Expression...");
		gdTmp = new GridData();
		gdTmp.widthHint = 140;
		m_btEdit.setLayoutData(gdTmp);
	}
	
	private void updateDisplay () {
		m_btEdit.setEnabled(m_chkUseCF.getSelection());
		m_edExp.setEnabled(m_chkUseCF.getSelection());
	}
}
