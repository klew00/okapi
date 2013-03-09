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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Default panel for Help/OK/Cancel buttons
 */
public class OKCancelPanel extends Composite {

	public Button btOK;
	public Button btCancel;
	public Button btHelp;
	public Button btExtra;

	/**
	 * Creates a new panel for Help/OK/Cancel buttons.
	 * @param parent Parent control.
	 * @param flags Style flags.
	 */
	public OKCancelPanel (Composite parent,	int style)
	{
		super(parent, SWT.NONE);
		createContent(null, false, Res.getString("OKCancelPanel.btOK"), null);
	}

	/**
	 * Creates a new panel for Help/OK/Cancel buttons.
	 * @param parent Parent control.
	 * @param flags Style flags.
	 * @param action Action to execute when any of the buttons is clicked.
	 * The receiving event, the widget's data is marked: 'c' for the Cancel
	 * button, 'o' for OK, and 'h' for help.
	 * @param showHelp True to display the Help button.
	 */
	public OKCancelPanel (Composite parent,
		int flags,
		SelectionAdapter action,
		boolean showHelp)
	{
		super(parent, SWT.NONE);
		createContent(action, showHelp, Res.getString("OKCancelPanel.btOK"), null);
	}
	
	/**
	 * Creates a new panel for Help/OK/Cancel buttons.
	 * @param parent the parent control.
	 * @param flags the style flags.
	 * @param action the action to execute when any of the buttons is clicked.
	 * The receiving event, the widget's data is marked: 'c' for the Cancel
	 * button, 'o' for OK, and 'h' for help.
	 * @param showHelp true to display the Help button.
	 * @param okLabel the label for the 'o' button.
	 */
	public OKCancelPanel (Composite parent,
		int flags,
		SelectionAdapter action,
		boolean showHelp,
		String okLabel)
	{
		super(parent, SWT.NONE);
		createContent(action, showHelp, okLabel, null);
	}
	
	/**
	 * Creates a new panel for Help/OK/Cancel buttons.
	 * @param parent the parent control.
	 * @param flags the style flags.
	 * @param action the action to execute when any of the buttons is clicked.
	 * The receiving event, the widget's data is marked: 'c' for the Cancel
	 * button, 'o' for OK, and 'h' for help, and 'x' for the extra button.
	 * @param showHelp true to display the Help button.
	 * @param okLabel the label for the 'o' button.
	 * @param extraLabel the label for the 'x' button (can be null)
	 */
	public OKCancelPanel (Composite parent,
		int flags,
		SelectionAdapter action,
		boolean showHelp,
		String okLabel,
		String extraLabel)
	{
		super(parent, SWT.NONE);
		createContent(action, showHelp, okLabel, extraLabel);
	}
	
	private void createContent (SelectionAdapter action,
		boolean showHelp,
		String okLabel,
		String extraLabel)
	{
		GridLayout layTmp = new GridLayout(2, true);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);
		int nWidth = UIUtil.BUTTON_DEFAULT_WIDTH;

		btHelp = new Button(this, SWT.PUSH);
		btHelp.setText(Res.getString("OKCancelPanel.btHelp"));
		btHelp.setData("h");
		if (action != null) {
			btHelp.addSelectionListener(action);
		}
		GridData gdTmp = new GridData();
		btHelp.setLayoutData(gdTmp);
		UIUtil.ensureWidth(btHelp, nWidth);
		btHelp.setVisible(showHelp);
		
		Composite cmpTmp = new Composite(this, SWT.NONE);
		RowLayout layRow = new RowLayout(SWT.HORIZONTAL);
		layRow.marginWidth = 0;
		layRow.marginHeight = 0;
		cmpTmp.setLayout(layRow);
		gdTmp = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gdTmp.grabExcessHorizontalSpace = true;
		cmpTmp.setLayoutData(gdTmp);

		// Create the buttons in a platform-specific order
		if ( UIUtil.getPlatformType() == UIUtil.PFTYPE_WIN ) {
			if ( extraLabel != null ) btExtra = new Button(cmpTmp, SWT.PUSH);
			btOK = new Button(cmpTmp, SWT.PUSH);
			btCancel = new Button(cmpTmp, SWT.PUSH);
		}
		else { // UIUtil.PFTYPE_UNIX, UIUtil.PFTYPE_MAC
			btCancel = new Button(cmpTmp, SWT.PUSH);
			btOK = new Button(cmpTmp, SWT.PUSH);
			if ( extraLabel != null ) btExtra = new Button(cmpTmp, SWT.PUSH);
		}

		btOK.setText(okLabel);
		btOK.setData("o");
		if (action != null) {
			btOK.addSelectionListener(action);
		}
		RowData rdTmp = new RowData();
		btOK.setLayoutData(rdTmp);
		btOK.pack();
		Rectangle rect1 = btOK.getBounds();
		
		btCancel.setText(Res.getString("OKCancelPanel.btCancel"));
		btCancel.setData("c");
		if (action != null) {
			btCancel.addSelectionListener(action);
		}
		rdTmp = new RowData();
		btCancel.setLayoutData(rdTmp);
		btCancel.pack();
		Rectangle rect2 = btCancel.getBounds();
		
		Rectangle rect3 = null;
		if ( btExtra != null ) {
			btExtra.setText(extraLabel);
			btExtra.setData("x");
			if (action != null) {
				btExtra.addSelectionListener(action);
			}
			rdTmp = new RowData();
			btExtra.setLayoutData(rdTmp);
			btExtra.pack();
			rect3 = btExtra.getBounds();
		}
		
		int max = rect1.width;
		if ( max < rect2.width ) max = rect2.width;
		if ( rect3 != null ) {
			if ( max < rect3.width ) max = rect3.width;
		}
		if ( max < nWidth ) max = nWidth;
		((RowData)btOK.getLayoutData()).width = max;
		((RowData)btCancel.getLayoutData()).width = max;
		if ( btExtra != null ) {
			((RowData)btExtra.getLayoutData()).width = max;
		}
	}
	
	public void setOKText (String text) {
		btOK.setText(text);
	}

}
