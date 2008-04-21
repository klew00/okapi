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

package net.sf.okapi.Borneo.Actions;

import java.util.Enumeration;

import net.sf.okapi.Library.Base.Utils;
import net.sf.okapi.Library.UI.Dialogs;
import net.sf.okapi.Library.UI.OKCancelPanel;
import net.sf.okapi.Package.Manifest;
import net.sf.okapi.Package.ManifestItem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ImportPackageForm {
	
	private Shell                 m_Shell;
	private boolean               m_bResult = false;
	private OKCancelPanel         m_pnlActions;
	private Manifest              m_Mnf;
	private Table                 m_Docs;
	private Text                  m_edProjectID;
	private Text                  m_edPackageType;
	
	/**
	 * Invokes the editor for the options of the ExportPackage action.
	 * @param p_Options The option object of the action.
	 * @param p_Object The SWT Shell object of the parent shell in the UI.
	 */
	public boolean edit (Manifest p_Manifest,
		Object p_Object)
	{
		boolean bRes = false;
		m_Shell = null;
		m_Mnf = p_Manifest;
		try {
			m_Shell = new Shell((Shell)p_Object, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)p_Object);
			return showDialog();
		}
		catch ( Exception E ) {
			Utils.showError(E.getLocalizedMessage(), null);
			bRes = false;
		}
		finally {
			// Dispose of the shell, but not of the display
			if ( m_Shell != null ) m_Shell.dispose();
		}
		return bRes;
	}
	
	private void create (Shell p_Parent)
	{
		m_Shell.setText("Import Translation Package");
		m_Shell.setImage(p_Parent.getImage());
		GridLayout layTmp = new GridLayout();
		m_Shell.setLayout(layTmp);

		Composite cmpTmp = new Composite(m_Shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		cmpTmp.setLayout(new GridLayout(2, false));

		Label stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Please, select the documents to import:");
		GridData gdTmp = new GridData();
		gdTmp.horizontalSpan = 2;
		stTmp.setLayoutData(gdTmp);
		
		m_Docs = new Table(cmpTmp, SWT.BORDER | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		gdTmp.widthHint = 400;
		gdTmp.heightHint = 200;
		m_Docs.setLayoutData(gdTmp);

		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Project ID:");
		m_edProjectID = new Text(cmpTmp, SWT.BORDER);
		m_edProjectID.setEditable(false);
		m_edProjectID.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Package type:");
		m_edPackageType = new Text(cmpTmp, SWT.BORDER);
		m_edPackageType.setEditable(false);
		m_edPackageType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_bResult = false;
				if ( e.widget.getData().equals("h") ) {
					//TODO: Call help
					return;
				}
				if ( e.widget.getData().equals("o") ) saveData();
				m_Shell.close();
			};
		};
		m_pnlActions = new OKCancelPanel(m_Shell, SWT.NONE, OKCancelActions, true);
		m_pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_Shell.setDefaultButton(m_pnlActions.m_btOK);

		setData();
		m_Shell.pack();
		Rectangle Rect = m_Shell.getBounds();
		m_Shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(m_Shell, p_Parent);
	}
	
	private boolean showDialog () {
		m_Shell.open();
		while ( !m_Shell.isDisposed() ) {
			if ( !m_Shell.getDisplay().readAndDispatch() )
				m_Shell.getDisplay().sleep();
		}
		return m_bResult;
	}

	private void setData () {
		Enumeration<ManifestItem> E = m_Mnf.getItems().elements();
		ManifestItem MI;
		TableItem TI;
		while ( E.hasMoreElements() ) {
			MI = E.nextElement();
			TI = new TableItem(m_Docs, SWT.NONE);
			TI.setText((MI.exists() ? "" : "<NOT FOUND> : ") + MI.getRelativePath());
			TI.setChecked(MI.isSelected());
		}
		if ( m_Docs.getItemCount() > 0 )
			m_Docs.setSelection(0);
		m_edProjectID.setText(m_Mnf.getProjectID());
		m_edPackageType.setText(m_Mnf.getPackageType());
	}

	private boolean saveData () {
		Enumeration<ManifestItem> E = m_Mnf.getItems().elements();
		ManifestItem MI;
		int i = 0;
		while ( E.hasMoreElements() ) {
			MI = E.nextElement();
			MI.setSelected(m_Docs.getItem(i++).getChecked());
		}
		m_bResult = true;
		return true;
	}
}
