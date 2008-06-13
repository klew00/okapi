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

package net.sf.okapi.applications.rainbow.utilities.extraction;

import java.io.File;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class Editor implements IParametersEditor {
	
	private Shell                 m_Shell;
	private boolean               m_bResult = false;
	private OKCancelPanel         m_pnlActions;
	private Parameters            m_Opt;
	private List                  m_lbTypes;
	private Text                  edDescription;
	private Text                  m_edName;
	private Text                  m_edOutputFolder;
	private Button                m_chkCreateZip;
	private Button                m_chkIncludeMergeData;
	private Text                  m_edSample;
	private boolean               m_bInInit = true;
	
	/**
	 * Invokes the editor for the options of the ExportPackage action.
	 * @param p_Options The option object of the action.
	 * @param p_Object The SWT Shell object of the parent shell in the UI.
	 */
	public boolean edit (IParameters p_Options,
		Object p_Object)
	{
		boolean bRes = false;
		try {
			m_Shell = null;
			m_Opt = (Parameters)p_Options;
			m_Shell = new Shell((Shell)p_Object, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)p_Object);
			return showDialog();
		}
		catch ( Exception e ) {
			Dialogs.showError(m_Shell, e.getLocalizedMessage(), null);
			bRes = false;
		}
		finally {
			// Dispose of the shell, but not of the display
			if ( m_Shell != null ) m_Shell.dispose();
		}
		return bRes;
	}
	
	public IParameters createParameters () {
		return new Parameters();
	}
	
	private void create (Shell p_Parent)
	{
		m_Shell.setText("Export Translation Package");
		if ( p_Parent != null ) m_Shell.setImage(p_Parent.getImage());
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		m_Shell.setLayout(layTmp);

		TabFolder tfTmp = new TabFolder(m_Shell, SWT.NONE);
		tfTmp.setLayoutData(new GridData(GridData.FILL_BOTH));

		//--- Format tab

		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout());
		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Package Format");
		tiTmp.setControl(cmpTmp);

		Label stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Type of package to create:");

		m_lbTypes = new List(cmpTmp, SWT.BORDER);
		m_lbTypes.add("Generic XLIFF");
		m_lbTypes.add("OmegaT");
		m_lbTypes.add("TagEditor TTX");
		m_lbTypes.add("Test: Okapi-XML-Test");
		// To use neutral access to the list
		m_lbTypes.setData("xliff\tomegat\tttx\ttest");
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.heightHint = 70;
		m_lbTypes.setLayoutData(gdTmp);
		
		edDescription = new Text(cmpTmp, SWT.BORDER | SWT.MULTI);
		edDescription.setEditable(false);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.heightHint = 60;
		edDescription.setLayoutData(gdTmp);

		//--- Name tab
		
		cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout());
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Package Location");
		tiTmp.setControl(cmpTmp);

		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Root of the output folder:");
		
		m_edOutputFolder = new Text(cmpTmp, SWT.BORDER);
		m_edOutputFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_edOutputFolder.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateSample();
			}
		});
		
		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Package name:");
		
		m_edName = new Text(cmpTmp, SWT.BORDER);
		m_edName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		m_edName.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateSample();
			}
		});
		
		stTmp = new Label(cmpTmp, SWT.NONE);
		stTmp.setText("Full path of the directory name:");
		
		m_edSample = new Text(cmpTmp, SWT.BORDER);
		m_edSample.setEditable(false);
		m_edSample.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		//--- Options tab
		
		cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout());
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Options");
		tiTmp.setControl(cmpTmp);

		m_chkCreateZip = new Button(cmpTmp, SWT.CHECK);
		m_chkCreateZip.setText("Compress the package into a ZIP file");

		m_chkIncludeMergeData = new Button(cmpTmp, SWT.CHECK);
		m_chkIncludeMergeData.setText("Includes the data to merge back the files");


		//--- References tab
		
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("References");
		//TODO
		
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
		m_Shell.setDefaultButton(m_pnlActions.btOK);

		setData();
		m_bInInit = false;
		m_Shell.pack();
		m_Shell.setMinimumSize(m_Shell.getSize());
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
		int n = -1;
		String[] aItems = ((String)m_lbTypes.getData()).split("\t", -2);
		for ( int i=0; i<aItems.length; i++ ) {
			if ( aItems[i].equals(m_Opt.pkgType) ) {
				n = i;
				break;
			}
		}
		if ( n < 0 ) n = 0;
		m_lbTypes.setSelection(n);
		m_edOutputFolder.setText(m_Opt.outputFolder);
		m_edName.setText(m_Opt.pkgName);
		m_chkCreateZip.setSelection(m_Opt.createZip);
		updateSample();
	}

	private boolean saveData () {
		if ( m_bInInit ) return true;
		//m_Opt.setName("TODO:Name"); //TODO "m_edName.getText());
		String[] aItems = ((String)m_lbTypes.getData()).split("\t", -2);
		m_Opt.pkgType = aItems[m_lbTypes.getSelectionIndex()];
		m_Opt.createZip = m_chkCreateZip.getSelection();
		m_Opt.pkgName = m_edName.getText();
		m_Opt.outputFolder = m_edOutputFolder.getText();
		m_bResult = true;
		return true;
	}
	
	private void updateSample () {
		saveData();
//TODO		String[] aRes = m_Opt.makePackageName("<ProjectID>", "<Lang>");
		m_edSample.setText(m_edOutputFolder.getText() + File.separator + m_edName.getText()); //TODO aRes[1]);
	}
}
