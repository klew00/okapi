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

package net.sf.okapi.steps.linebreakconversion.ui;

import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.steps.linebreakconversion.Parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

@EditorFor(Parameters.class)
public class ParametersEditor implements IParametersEditor {
	
	private Shell shell;
	private boolean result = false;
	private OKCancelPanel pnlActions;
	private Parameters params;
	private Button rdDos;
	private Button rdUnix;
	private Button rdMac;
	private IHelp help;

	public boolean edit (IParameters params,
		boolean readOnly,
		IContext context)
	{
		boolean bRes = false;
		try {
			shell = null;
			help = (IHelp)context.getObject("help");
			this.params = (Parameters)params;
			shell = new Shell((Shell)context.getObject("shell"), SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)context.getObject("shell"), readOnly);
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
	
	private void create (Shell parent,
		boolean readOnly)
	{
		shell.setText("Line-Break Conversion");
		if ( parent != null ) UIUtil.inheritIcon(shell, parent);
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		TabFolder tfTmp = new TabFolder(shell, SWT.NONE);
		tfTmp.setLayoutData(new GridData(GridData.FILL_BOTH));

		//--- Options tab

		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout());
		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Options");
		tiTmp.setControl(cmpTmp);

		Group group = new Group(cmpTmp, SWT.NONE);
		group.setLayout(new RowLayout(SWT.VERTICAL));
		group.setText("New type of line-break");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		rdDos = new Button(group, SWT.RADIO);
		rdDos.setText("DOS/Windows (Carriage-Return + Line-Feed, \\r\\n, 0x0D+0x0A)");
		rdUnix = new Button(group, SWT.RADIO);
		rdUnix.setText("Unix/Linux (Line-Feed, \\n, 0x0A)");
		rdMac = new Button(group, SWT.RADIO);
		rdMac.setText("Macintosh (Carriage-Return, \\r, 0x0D)");

		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) {
					if ( help != null ) help.showTopic(this, "index");
					return;
				}
				if ( e.widget.getData().equals("o") ) saveData();
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pnlActions.btOK.setEnabled(!readOnly);
		if ( !readOnly ) {
			shell.setDefaultButton(pnlActions.btOK);
		}

		setData();
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Dialogs.centerWindow(shell, parent);
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
		if ( params.getLineBreak().equals(Util.LINEBREAK_MAC) )
			rdMac.setSelection(true);
		else if ( params.getLineBreak().equals(Util.LINEBREAK_UNIX) )
			rdUnix.setSelection(true);
		else
			rdDos.setSelection(true);
	}

	private boolean saveData () {
		if ( rdDos.getSelection() )
			params.setLineBreak(Util.LINEBREAK_DOS);
		else if ( rdUnix.getSelection() )
			params.setLineBreak(Util.LINEBREAK_UNIX);
		else
			params.setLineBreak(Util.LINEBREAK_MAC);
		result = true;
		return true;
	}
	
}