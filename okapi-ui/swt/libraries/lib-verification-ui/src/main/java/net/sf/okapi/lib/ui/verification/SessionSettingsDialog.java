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

import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.lib.verification.QualityCheckSession;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
	
	public SessionSettingsDialog (Shell parent, IHelp paramHelp) {

		help = paramHelp;
		dialog = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		dialog.setText("Session Settings");
		dialog.setLayout(new GridLayout());
		dialog.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Documents
		
		Group grpDocs = new Group(dialog, SWT.NONE);
		grpDocs.setText("Documents");
		grpDocs.setLayout(new GridLayout(2, false));
		grpDocs.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		lbDocs = new List(grpDocs, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		lbDocs.setLayoutData(gdTmp);
		
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
		dialog.setMinimumSize(dialog.getSize());
		Dialogs.centerWindow(dialog, parent);
	}

	public void setData (QualityCheckSession session) {
		this.session = session;
		
		for ( RawDocument rd : session.getDocuments() ) {
			lbDocs.add(rd.getInputURI().toString());
		}
		
		edSourceLocale.setText(session.getSourceLocale().toBCP47());
		edTargetLocale.setText(session.getTargetLocale().toBCP47());
	}
	
	private boolean saveData () {
		// Check source locale
		String tmp = edSourceLocale.getText();
		try {
			LocaleId.fromBCP47(tmp);
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
			LocaleId.fromBCP47(tmp);
		}
		catch ( Throwable e ) {
			// Invalid BCP-47 tag
			Dialogs.showError(dialog,
				String.format("The target locale '%s' is not a valid locale.", tmp), null);
			edTargetLocale.setFocus();
			return false;
		}
		
		// Save data
		session.setSourceLocale(LocaleId.fromBCP47(edSourceLocale.getText()));
		session.setTargetLocale(LocaleId.fromBCP47(edTargetLocale.getText()));
		
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
