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

package net.sf.okapi.common.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Default panel for Help/Close buttons
 */
public class ClosePanel extends Composite {

	public Button btClose;
	public Button btHelp;

	/**
	 * Creates a new panel for Help/OK/Cancel buttons.
	 * @param parent Parent control.
	 * @param flags Style flags.
	 * @param action Action to execute when any of the buttons is clicked.
	 * The receiving event, the widget's data is marked: 'c' for the Close
	 * button, and 'h' for help.
	 * @param showHelp True to display the Help button.
	 */
	public ClosePanel (Composite parent,
		int flags,
		SelectionAdapter action,
		boolean showHelp)
	{
		super(parent, SWT.NONE);
		createContent(action, showHelp);
	}
	
	private void createContent (SelectionAdapter action,
		boolean showHelp)
	{
		GridLayout layTmp = new GridLayout(2, true);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);
		int nWidth = UIUtil.BUTTON_DEFAULT_WIDTH;

		btHelp = new Button(this, SWT.PUSH);
		btHelp.setText(Res.getString("ClosePanel.btHelp")); //$NON-NLS-1$
		btHelp.setData("h"); //$NON-NLS-1$
		btHelp.addSelectionListener(action);
		btHelp.setLayoutData(new GridData());
		UIUtil.ensureWidth(btHelp, nWidth);
		btHelp.setVisible(showHelp);
		
		Composite cmpTmp = new Composite(this, SWT.NONE);
		layTmp = new GridLayout();
		layTmp.marginWidth = 0;
		layTmp.marginHeight = 0;
		cmpTmp.setLayout(layTmp);
		GridData gdTmp = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gdTmp.grabExcessHorizontalSpace = true;
		cmpTmp.setLayoutData(gdTmp);

		btClose = new Button(cmpTmp, SWT.PUSH);
		btClose.setText(Res.getString("ClosePanel.btClose")); //$NON-NLS-1$
		btClose.setData("c"); //$NON-NLS-1$
		btClose.addSelectionListener(action);
		btClose.setLayoutData(new GridData());
		UIUtil.ensureWidth(btClose, nWidth);
	}
	
}
