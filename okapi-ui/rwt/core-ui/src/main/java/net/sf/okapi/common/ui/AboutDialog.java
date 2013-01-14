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

import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Default implementation of an About dialog box.
 */
public class AboutDialog {

	private Shell shell;

	/**
	 * Creates a new AboutDialog object. The icon displayed in this dialog box
	 * is provided by the parent shell. It should be either a unique image 
	 * (Shell.getImage()), or the second image of a list of (Shell.getImages()[1]).
	 * @param parent The parent shell (also carry the icon to display).
	 * @param caption Caption text.
	 * @param description Text for the application description line.
	 * @param version Text for the application version line.
	 */
	public AboutDialog (Shell parent,
		String caption,
		String description,
		String version)
	{
		// Take the opportunity to do some clean up if possible
		Runtime rt = Runtime.getRuntime();
		rt.runFinalization();
		rt.gc();

		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(caption);
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout());
			
		Composite cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout(2, false);
		cmpTmp.setLayout(layTmp);

		// Application icon
		Label appIcon = new Label(cmpTmp, SWT.NONE);
		GridData gdTmp = new GridData();
		gdTmp.verticalSpan = 2;
		appIcon.setLayoutData(gdTmp);
		Image[] list = parent.getImages();
		// Gets the single icon
		if (( list == null ) || ( list.length < 2 )) {
			appIcon.setImage(parent.getImage());
		}
		else { // Or the second one if there are more than one.
			appIcon.setImage(list[1]);
		}

		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText(description==null ? "Web" : description); //$NON-NLS-1$
		gdTmp = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL
			| GridData.VERTICAL_ALIGN_CENTER | GridData.GRAB_VERTICAL);
		label.setLayoutData(gdTmp);
		
		label = new Label(cmpTmp, SWT.NONE);
		label.setText("Web version (BETA)"); //$NON-NLS-1$
		gdTmp = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL
			| GridData.VERTICAL_ALIGN_CENTER | GridData.GRAB_VERTICAL);
		label.setLayoutData(gdTmp);		

		// Info
		
		cmpTmp = new Composite(shell, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		layTmp = new GridLayout(2, false);
		cmpTmp.setLayout(layTmp);
		
		label = new Label(cmpTmp, SWT.NONE);
		label.setText("Browser:"); //$NON-NLS-1$
		label = new Label(cmpTmp, SWT.NONE);
		label.setText((String) RWT.getSessionStore().getAttribute("userAgent")); //$NON-NLS-1$
//		label = new Label(cmpTmp, SWT.NONE);
//		label.setText("");
		
		//--- Dialog-level buttons

		SelectionAdapter CloseActions = new SelectionAdapter() {
			private static final long serialVersionUID = 4631437271255187216L;

			public void widgetSelected(SelectionEvent e) {
				shell.close();
			};
		};
		ClosePanel pnlActions = new ClosePanel(shell, SWT.NONE, CloseActions, false);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shell.setDefaultButton(pnlActions.btClose);
		
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 400 ) startSize.x = 400;
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
	}

	public void showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
	}

}
