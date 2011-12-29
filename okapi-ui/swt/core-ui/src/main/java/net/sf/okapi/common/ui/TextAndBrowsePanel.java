/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.common.ui;

import net.sf.okapi.common.ui.Dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;

/**
 * Helper panel for a text field and a browse button.
 */
public class TextAndBrowsePanel extends Composite {

	private Text edText;
	private Button btBrowse;
	private boolean isFolder = false;
	private boolean saveAs = false;
	private String title;
	private String filterNames;
	private String filterExtensions;

	/**
	 * Creates a new TextAndBrowsePanel object with given arguments.
	 * @param parent the parent of the panel.
	 * @param flags the style of the panel.
	 * @param isFolder true for a directory/folder browse button, false
	 * for a file browse button.
	 * @param buttonLabel label of the browse button, if null the text "..." is used. 
	 */
	public TextAndBrowsePanel (Composite parent,
		int flags,
		boolean isFolder,
		String buttonLabel)
	{
		super(parent, flags);
		createContent(isFolder, buttonLabel);
	}
	
	/**
	 * Creates a new TextAndBrowsePanel object with given arguments and a button
	 * with a label set to "...".
	 * @param parent the parent of the panel.
	 * @param flags the style of the panel.
	 * @param isFolder true for a directory/folder browse button, false
	 * for a file browse button.
	 */
	public TextAndBrowsePanel (Composite parent,
		int flags,
		boolean isFolder)
	{
		super(parent, flags);
		createContent(isFolder, null);
	}
	
	private void createContent (boolean browseFolder,
		String buttonLabel)
	{
		isFolder = browseFolder;
		title = "Select a File";
		
		GridLayout layTmp = new GridLayout(2, false);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);

		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		setLayoutData(gdTmp);

		edText = new Text(this, SWT.BORDER);
		edText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		btBrowse = new Button(this, SWT.PUSH);
		if ( buttonLabel == null ) btBrowse.setText("...");
		else btBrowse.setText(buttonLabel);
		btBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( isFolder ) browseFolder();
				else browsePath();
			};
		});
		
	}

	public String getText () {
		return edText.getText();
	}

	public void setText(String text) {
		edText.setText(text);
	}

	@Override
	public void setEnabled (boolean enabled) {
		super.setEnabled(enabled);
		edText.setEnabled(enabled);
		btBrowse.setEnabled(enabled);
	}

	public void setEditable (boolean editable) {
		edText.setEditable(editable);
		btBrowse.setEnabled(editable);
	}

	public boolean isSaveAs () {
		return saveAs;
	}

	public void setSaveAs (boolean saveAs) {
		this.saveAs = saveAs;
	}
	
	public String getTitle () {
		return title;
	}
	
	public void setTitle (String title) {
		this.title = title;
	}
	
	public void setBrowseFilters (String filterNames,
		String filterExtensions)
	{
		this.filterNames = filterNames;
		this.filterExtensions = filterExtensions;
	}

	private void browseFolder () {
		try {
			DirectoryDialog dlg = new DirectoryDialog(getShell());
			dlg.setMessage("Please, select a folder.");
			dlg.setFilterPath(edText.getText());
			dlg.setText(title);
			String dir = dlg.open();
			if (  dir == null ) return;
			edText.setText(dir);
			edText.selectAll();
			edText.setFocus();
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), e.getLocalizedMessage(), null);
		}
	}
	
	private void browsePath () {
		try {
			if ( saveAs ) {
				String path = Dialogs.browseFilenamesForSave(getShell(), title,
					null, null, filterNames, filterExtensions);
				if ( path == null ) return;
				edText.setText(path);
			}
			else {
				String[] paths = Dialogs.browseFilenames(getShell(), title, false,
					null, filterNames, filterExtensions);
				if ( paths == null ) return;
				edText.setText(paths[0]);
			}
			edText.selectAll();
			edText.setFocus();
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), e.getLocalizedMessage(), null);
		}
	}
	
}
