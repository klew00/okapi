/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.applications.olifant;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.lib.tmdb.IRepository;
import net.sf.okapi.lib.tmdb.ITm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

class RepositoryPanel extends Composite {

	private MainForm mainForm;
	private List tmList;
	private Button btNewTM;
	private IRepository repo;

	public RepositoryPanel (MainForm mainForm,
		Composite parent,
		int flags)
	{
		super(parent, flags);
		this.mainForm = mainForm;
		
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);
	
		int minButtonWidth = 130;
		Button btSelectRepo = UIUtil.createGridButton(this, SWT.PUSH, "Select Repository...", minButtonWidth, 1);
		//btSelectRepo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		btSelectRepo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectRepository();
			}
		});
		
		btNewTM = UIUtil.createGridButton(this, SWT.PUSH, "Create New TM...", minButtonWidth, 1);
		//btNewTM.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		btNewTM.setEnabled(false); // No repository is open yet
		btNewTM.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO: createTM();
			}
		});
		
		UIUtil.setSameWidth(minButtonWidth, btSelectRepo, btNewTM);

		Label stTmp = new Label(this, SWT.NONE);
		stTmp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		stTmp.setText("TMs in this repository:");
		
		tmList = new List(this, SWT.BORDER);
		tmList.setLayoutData(new GridData(GridData.FILL_BOTH));
		tmList.addMouseListener(new MouseListener() {
			public void mouseDoubleClick (MouseEvent e) {
				int n = tmList.getSelectionIndex();
				if ( n < 0 ) return;
				openTmTab(tmList.getItem(n));
			}
			public void mouseDown (MouseEvent e) {}
			public void mouseUp (MouseEvent e) {}
		});
		
	}

	private void openTmTab (String tmName) {
		try {
			// Check if we have already a tab for that TM
			TmPanel tp = mainForm.findTmTab(tmName, true);
			// We are done if the tab exists
			if ( tp != null ) return;
			
			// Else: create a new TmPanel
			ITm tm = repo.getTm(tmName);
			tp = mainForm.addTmTabEmpty(tm);
			if ( tp == null ) return;

			// Now the tab should exist
			mainForm.findTmTab(tmName, true);
			tp.resetTmDisplay();
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), "Error selecting repository.\n"+e.getMessage(), null);
		}
	}
	
	@Override
	public void dispose () {
		closeRepository();
		super.dispose();
	}

	List getTmList () {
		return tmList;
	}

	IRepository getRepository () {
		return repo;
	}

	String getRepositoryName () {
		if ( repo == null ) return null;
		else return repo.getName();
	}

	boolean isRepositoryOpen () {
		return (repo != null);
	}
	
	TmPanel createTmAndTmTab (String name,
		String description,
		LocaleId locId)
	{
		try {
			// Make sure we have a repository open
			if ( !isRepositoryOpen() ) {
				selectRepository();
				// Check that we do have a repository open now
				if ( !isRepositoryOpen() ) return null;
			}
		
			// Create the empty TM
			ITm tm = repo.createTm(name, description, locId);
			if ( tm == null ) return null;
			
			tmList.add(tm.getName());
			return mainForm.addTmTabEmpty(tm);
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), "Error creating a new TM.\n"+e.getMessage(), null);
			return null;
		}
	}
	
	void selectRepository () {
		try {
			RepositoryForm dlg = new RepositoryForm(getShell());
			String[] res = dlg.showDialog();
			if ( res == null ) return; // No repository selected
			openRepository(res[0], res[1]);
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), "Error selecting repository.\n"+e.getMessage(), null);
		}
	}

	private void openRepository (String type,
		String name)
	{
		try {
//			// Make sure we close the previous repository
//			closeRepository();

			// Instantiate the new repository
			if ( type.equals("m") ) {
				closeRepository();
				repo = new net.sf.okapi.lib.tmdb.h2.Repository(null);
			}
			else if ( type.equals("d") ) {
				closeRepository();
				repo = new net.sf.okapi.lib.tmdb.h2.Repository(name);
			}
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), "Error opening repository.\n"+e.getMessage(), null);
		}
		finally {
			// Update the display
			updateRepositoryUI();
			updateRepositoryStatus();
		}
	}
	
	void updateRepositoryUI () {
		tmList.removeAll();
		if ( repo == null ) return;
		for ( String name : repo.getTmNames() ) {
			tmList.add(name);
		}
		if ( tmList.getItemCount() > 0 ) {
			tmList.setSelection(0);
		}
	}

	void closeRepository () {
		if ( repo != null ) {
			repo.close();
			repo = null;
		}
		updateRepositoryUI();
		updateRepositoryStatus();
	}
	
	void updateRepositoryStatus () {
		btNewTM.setEnabled(isRepositoryOpen());
		mainForm.updateCommands();
		mainForm.updateTitle();
	}
}
