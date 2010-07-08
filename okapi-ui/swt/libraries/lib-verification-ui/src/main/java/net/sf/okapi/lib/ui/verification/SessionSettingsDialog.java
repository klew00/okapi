/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.lib.ui.verification;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.lib.verification.QualityCheckSession;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class SessionSettingsDialog {
	
	private Shell dialog;
	private boolean result = false;
	private OKCancelPanel pnlActions;
	private IHelp help;
	private QualityCheckSession session;
	private Text edSourceLocale;
	private Text edTargetLocale;
	private List lbDocs;
	private Button btRemove;
	
	public SessionSettingsDialog (Shell parent, IHelp paramHelp) {

		help = paramHelp;
		dialog = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		dialog.setText("Session Settings");
		dialog.setLayout(new GridLayout());
		dialog.setLayoutData(new GridData(GridData.FILL_BOTH));
		UIUtil.inheritIcon(dialog, parent);

		// Documents
		
		Group grpDocs = new Group(dialog, SWT.NONE);
		grpDocs.setText("Documents");
		grpDocs.setLayout(new GridLayout(2, false));
		grpDocs.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		lbDocs = new List(grpDocs, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		lbDocs.setLayoutData(gdTmp);
		
		Button btAdd = UIUtil.createGridButton(grpDocs, SWT.PUSH, "Add...", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addDocument();
			}
		});
		
		btRemove = UIUtil.createGridButton(grpDocs, SWT.PUSH, "Remove...", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeDocument();
			}
		});
		
		// Locales
		
		Group grpLocales = new Group(dialog, SWT.NONE);
		grpLocales.setText("Locales");
		grpLocales.setLayout(new GridLayout(2, false));
		grpLocales.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label label = new Label(grpLocales, SWT.NONE);
		label.setText("Source locale:");
		
		edSourceLocale = new Text(grpLocales, SWT.BORDER);
		
		label = new Label(grpLocales, SWT.NONE);
		label.setText("Target locale:");
		
		edTargetLocale = new Text(grpLocales, SWT.BORDER);
		
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) {
					if ( help != null ) help.showTopic(this, "sessionsettings");
					return;
				}
				if ( e.widget.getData().equals("o") ) saveData();
				dialog.close();
			};
		};
		pnlActions = new OKCancelPanel(dialog, SWT.NONE, OKCancelActions, true);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		pnlActions.setLayoutData(gdTmp);

		dialog.pack();
		Point size = dialog.getSize();
		dialog.setMinimumSize(size);
		if ( size.x < 630 ) size.x = 630;
		if ( size.y < 400 ) size.y = 400;
		dialog.setSize(size);
		Dialogs.centerWindow(dialog, parent);
	}

	private void removeDocument () {
		int n = lbDocs.getSelectionIndex();
		if ( n < 0 ) return;
		lbDocs.remove(n);
		if ( n >= lbDocs.getItemCount() ) n = lbDocs.getItemCount()-1;
		lbDocs.setSelection(n);
		updateFileButtons();
	}
	
	private void addDocument () {
		try {
			InputDocumentDialog dlg = new InputDocumentDialog(dialog, "Add document",
				session.getFilterConfigurationMapper());
			// Set default data
			dlg.setData(null, null, "UTF-8", session.getSourceLocale(), session.getTargetLocale());
			// Lock the locales if we have already documents in the session
			dlg.setLocalesEditable(lbDocs.getItemCount()==0);
			// Edit
			Object[] data = dlg.showDialog();
			if ( data == null ) return;
			
			// Create the raw document to add to the session
			URI uri = (new File((String)data[0])).toURI();
			RawDocument rd = new RawDocument(uri, (String)data[2], (LocaleId)data[3], (LocaleId)data[4]);
			rd.setFilterConfigId((String)data[1]);
			// Add to list
			lbDocs.add(formatDocument(rd));
			lbDocs.setSelection(lbDocs.getItemCount()-1);
			
			// If it is the first document: its locales become the default
			if ( lbDocs.getItemCount() == 1 ) {
				edSourceLocale.setText(((LocaleId)data[3]).toBCP47());
				edTargetLocale.setText(((LocaleId)data[4]).toBCP47());
			}
		}
		catch ( Throwable e ) {
			Dialogs.showError(dialog, "Error adding document.\n"+e.getMessage(), null);
		}
		finally {
			updateFileButtons();
		}
	}
	
	private String formatDocument (RawDocument rd) {
		return String.format("%s    \t(%s,  %s)", rd.getInputURI().getPath(), rd.getFilterConfigId(), rd.getEncoding());
	}
	
	private String[] explodeDocument (String doc) {
		String[] res = new String[3]; 
		String tmp[] = doc.split("\t");
		res[0] = tmp[0].trim(); // the URI
		tmp = tmp[1].split(",");
		res[1] = tmp[0].substring(1); // The filter configuration
		res[2] = tmp[1].trim(); // The encoding
		res[2] = res[2].substring(0, res[2].length()-1);
		return res;
	}
	
	private void updateFileButtons () {
		btRemove.setEnabled(lbDocs.getSelectionIndex()>-1);
	}
	
	public void setData (QualityCheckSession session) {
		this.session = session;
		for ( RawDocument rd : session.getDocuments() ) {
			lbDocs.add(formatDocument(rd));
		}
		if ( lbDocs.getItemCount() > 0 ) {
			lbDocs.setSelection(0);
		}
		edSourceLocale.setText(session.getSourceLocale().toBCP47());
		edTargetLocale.setText(session.getTargetLocale().toBCP47());
		updateFileButtons();
	}
	
	private boolean saveData () {
		// Check source locale
		LocaleId srcLoc;
		LocaleId trgLoc;
		String tmp = edSourceLocale.getText();
		try {
			srcLoc = LocaleId.fromBCP47(tmp);
		}
		catch ( Throwable e ) {
			// Invalid BCP-47 tag
			Dialogs.showError(dialog,
				String.format("The source locale '%s' is not a valid locale.", tmp), null);
			edSourceLocale.setFocus();
			return false;
		}
		
		// Check target locale
		tmp = edTargetLocale.getText();
		try {
			trgLoc = LocaleId.fromBCP47(tmp);
		}
		catch ( Throwable e ) {
			// Invalid BCP-47 tag
			Dialogs.showError(dialog,
				String.format("The target locale '%s' is not a valid locale.", tmp), null);
			edTargetLocale.setFocus();
			return false;
		}
		
		// Save locales
		session.setSourceLocale(srcLoc);
		session.setTargetLocale(trgLoc);
		
		// Save documents
		Map<URI, RawDocument> docs = session.getDocumentsMap();
		docs.clear(); // Clear existing list
		// Add all documents
		for ( String item : lbDocs.getItems() ) {
			String[] res = explodeDocument(item);
			try {
				URI uri = (new File(res[0])).toURI();
				RawDocument rd = new RawDocument(uri, res[2], srcLoc, trgLoc);
				rd.setFilterConfigId(res[1]);
				session.addRawDocument(rd);
			}
			catch ( Throwable e ) {
				Dialogs.showError(dialog,
					String.format("Error with: %s", res[0]), null);
				return false;
			}
		}

		return true;
	}

	public boolean showDialog () {
		dialog.open();
		while ( !dialog.isDisposed() ) {
			if ( !dialog.getDisplay().readAndDispatch() )
				dialog.getDisplay().sleep();
		}
		return result;
	}

}
