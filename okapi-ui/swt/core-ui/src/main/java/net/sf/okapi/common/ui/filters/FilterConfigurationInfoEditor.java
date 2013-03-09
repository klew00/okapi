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

package net.sf.okapi.common.ui.filters;

import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Default implementation of the {@link IFilterConfigurationInfoEditor} interface.
 */
public class FilterConfigurationInfoEditor implements IFilterConfigurationInfoEditor {

	private Shell shell;
	private FilterConfiguration config;
	private Text edIdentifier;
	private Text edName;
	private Text edDescription;
	private Text edParametersLocation;
	private boolean result;

	/**
	 * Default constructor, needed to instantiate the object from Class.fromName().
	 */
	public FilterConfigurationInfoEditor () {
	}

	public void create (Shell parent) {
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(Res.getString("FilterConfigurationInfoEditor.caption")); //$NON-NLS-1$
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout(2, false));
		
		Label label = new Label(shell, SWT.NONE);
		label.setText(Res.getString("FilterConfigurationInfoEditor.identifier")); //$NON-NLS-1$
		edIdentifier = new Text(shell, SWT.BORDER);
		edIdentifier.setLayoutData(new GridData(GridData.FILL_HORIZONTAL)); 
		
		label = new Label(shell, SWT.NONE);
		label.setText(Res.getString("FilterConfigurationInfoEditor.paramsLocation")); //$NON-NLS-1$
		edParametersLocation = new Text(shell, SWT.BORDER);
		edParametersLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL)); 
		
		label = new Label(shell, SWT.NONE);
		label.setText(Res.getString("FilterConfigurationInfoEditor.name")); //$NON-NLS-1$
		edName = new Text(shell, SWT.BORDER);
		edName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL)); 
		
		label = new Label(shell, SWT.NONE);
		label.setText(Res.getString("FilterConfigurationInfoEditor.description")); //$NON-NLS-1$
		edDescription = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.heightHint = 40;
		edDescription.setLayoutData(gdTmp);
		
		//--- Dialog-level buttons
		SelectionAdapter actions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) { //$NON-NLS-1$
					//if ( help != null ) help.showTopic(this, "charinfo");
					return;
				}
				if ( e.widget.getData().equals("o") ) { //$NON-NLS-1$
					if ( !saveData() ) return;
					result = true;
				}
				shell.close();
			};
		};
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, actions, false);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 450 ) startSize.x = 450;
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
	}

	private void setData (FilterConfiguration config) {
		this.config = config;
		edIdentifier.setText(config.configId);
		edParametersLocation.setText(config.parametersLocation);
		edName.setText(config.name);
		edDescription.setText(config.description);
	}
	
	private boolean saveData () {
		String tmp = edIdentifier.getText();
		if ( tmp.length() == 0 ) {
			//TODO: message
			edIdentifier.setFocus();
			return false;
		}
		
		tmp = edParametersLocation.getText();
		if ( tmp.length() == 0 ) {
			//TODO: message
			edParametersLocation.setFocus();
			return false;
		}
		
		tmp = edName.getText();
		if ( tmp.length() == 0 ) {
			//TODO: message
			edName.setFocus();
			return false;
		}
		
		config.configId = edIdentifier.getText();
		config.parametersLocation = edParametersLocation.getText();
		config.name = edName.getText();
		config.description = edDescription.getText();
		return true;
	}

	public boolean showDialog (FilterConfiguration config,
		IFilterConfigurationMapper mapper)
	{
		//TODO: Perform a check that the entered identifier is not duplicated.
		setData(config);
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

}
