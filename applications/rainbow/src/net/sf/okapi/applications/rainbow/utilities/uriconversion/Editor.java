/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.applications.rainbow.utilities.uriconversion;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class Editor implements IParametersEditor {

	private Shell shell;
	private boolean result = false;
	private OKCancelPanel pnlActions;
	private Parameters params;
	private Table  table;
	private Button chkUpdateAll;
	private Button chkUnescape;
	private Button chkEscape;
	private Button btnFirstOption;
	private Button btnSecondOption;
	
	public boolean edit (IParameters params,
		Object object)
	{
		boolean bRes = false;
		try {
			shell = null;
			this.params = (Parameters)params;
			shell = new Shell((Shell)object, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)object);
			return showDialog();
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
			bRes = false;
		}
		finally {
			// Dispose of the shell, but not of the display
			if ( shell != null ) shell.dispose();
		}
		return bRes;
	}
	
	public IParameters createParameters () {
		return new Parameters();
	}
	
	private void create (Shell parent)
	{
		shell.setText("Search And Replace");
		if ( parent != null ) UIUtil.inheritIcon(shell, parent);

		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);
		
		TabFolder tfTmp = new TabFolder(shell, SWT.NONE);
		tfTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite cmpTmp0 = new Composite(tfTmp, SWT.NONE);
		cmpTmp0.setLayout(new GridLayout(2, false));
		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Options");
		tiTmp.setControl(cmpTmp0);		
	
		chkUnescape = new Button (cmpTmp0, SWT.RADIO);
		chkUnescape.setText ("Un-escape the URI escape sequence");
		chkUnescape.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		chkUnescape.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(chkUnescape.getSelection()){
					table.setEnabled(false);
					chkUpdateAll.setEnabled(false);
					btnFirstOption.setEnabled(false);
					btnSecondOption.setEnabled(false);
				}
			}
		});		
	
		chkEscape = new Button (cmpTmp0, SWT.RADIO);
		chkEscape.setText ("Escape content to URI escape sequence");
		chkEscape.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		chkEscape.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(chkEscape.getSelection()){
					table.setEnabled(true);
					chkUpdateAll.setEnabled(true);
					btnFirstOption.setEnabled(true);
					btnSecondOption.setEnabled(true);					
				}
			}
		});		
		
		Label lblList = new Label (cmpTmp0,SWT.LEFT);
		lblList.setText ("List of the characters to escape:");
		GridData gdTmp = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
		int indent = 16;
		gdTmp.horizontalIndent = indent;
		lblList.setLayoutData(gdTmp);

		table = new Table (cmpTmp0, SWT.CHECK | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gdTmp = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gdTmp.horizontalIndent = indent;
		table.setLayoutData(gdTmp);

		//--click updates button states--
		table.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				if ( event.detail!=SWT.CHECK ) {
					
				}
			}
		});		
		
		chkUpdateAll = new Button(cmpTmp0, SWT.CHECK);
		chkUpdateAll.setText("Escape all extended characters");
		gdTmp = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
		gdTmp.horizontalIndent = indent;
		chkUpdateAll.setLayoutData(gdTmp);

		int buttonWidth = 170;
		btnFirstOption = new Button(cmpTmp0, SWT.PUSH);
		btnFirstOption.setText("All But URI-Marks");
		gdTmp = new GridData();
		gdTmp.horizontalIndent = indent;
		gdTmp.widthHint = buttonWidth;
		btnFirstOption.setLayoutData(gdTmp);
		btnFirstOption.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectList = " `@#$^&+={}|[]\\:\";<>,?/";
				for ( int i=0; i<table.getItemCount(); i++ ) {
					TableItem ti = table.getItem(i);
					if(selectList.contains(ti.getText(0))){
						ti.setChecked(true);
					}else{
						ti.setChecked(false);
					}
				};
			}
		});		

		btnSecondOption = new Button(cmpTmp0, SWT.PUSH);
		btnSecondOption.setText("All But Marks And Reserved");
		gdTmp = new GridData();
		gdTmp.widthHint = buttonWidth;
		btnSecondOption.setLayoutData(gdTmp);
		btnSecondOption.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectList = " `#^{}|[]\\\"<>";
				for ( int i=0; i<table.getItemCount(); i++ ) {
					TableItem ti = table.getItem(i);
					if(selectList.contains(ti.getText(0))){
						ti.setChecked(true);
					}else{
						ti.setChecked(false);
					}
				};
			}
		});	

		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) {
					//TODO: Call help
					return;
				}
				if ( e.widget.getData().equals("o") ){
					if(!saveData()){
						return;
					}
				}
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		shell.setSize(shell.getSize().x,shell.getSize().y+50);
		Dialogs.centerWindow(shell, parent);
		
		setData();
	}
	
	
	private boolean showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}


	private void setData () {
		
		if(params.conversionType==0){
			chkUnescape.setSelection(true);
			chkEscape.setSelection(false);
			
			table.setEnabled(false);
			chkUpdateAll.setEnabled(false);
			btnFirstOption.setEnabled(false);
			btnSecondOption.setEnabled(false);
			
		}else{
			chkUnescape.setSelection(false);
			chkEscape.setSelection(true);
			
			table.setEnabled(true);
			chkUpdateAll.setEnabled(true);
			btnFirstOption.setEnabled(true);
			btnSecondOption.setEnabled(true);			
		}
		
		chkUpdateAll.setSelection(params.updateAll);
		
		String allItems = " ~`!@#$^&*() +-={}|[]\\:\";'<>,.?/";
		String selList = params.escapeList;
		
		int len = allItems.length();
        for (int i = 0; i < len; i++) {
        	TableItem ti = new TableItem (table, SWT.NONE);
			ti.setText (""+allItems.charAt(i));
			if(selList.contains(""+allItems.charAt(i))){
				ti.setChecked(true);
			}
        } 		
	}

	private boolean saveData () {

		params.reset();
		
		if(chkUnescape.getSelection()){
			params.conversionType=0;
		}else{
			params.conversionType=1;
		}
		
		params.updateAll = chkUpdateAll.getSelection();
		
		StringBuilder selectedItems = new StringBuilder();

		for ( int i=0; i<table.getItemCount(); i++ ) {
			TableItem ti = table.getItem(i);
			if(ti.getChecked()){
				selectedItems.append(ti.getText(0));
			}
		};
		
		// make sure the list is not empty
		if (table.getItemCount()==0){
			Dialogs.showError(shell, "You need to provide a search expression", null);
			return false;
		}
		
		result = true;
		return result;
	}
}
