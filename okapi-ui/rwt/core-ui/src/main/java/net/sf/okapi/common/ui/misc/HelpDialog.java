// TODO Not complete
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

package net.sf.okapi.common.ui.misc;

import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * Default implementation of an About dialog box.
 */
public class HelpDialog {

	private Shell shell;
	private Browser browser;
//	private boolean done;

	/**
	 * Creates a new HelpDialog object displaying a help topic by opening a given url in the browser.
	 * @param parent The parent shell (also carry the icon to display).
	 * @param caption Caption text.
	 * @param url the help topic URL as String.
	 */
	public HelpDialog (Shell parent, String caption, String url)
	{
//		// Take the opportunity to do some clean up if possible
//		Runtime rt = Runtime.getRuntime();
//		rt.runFinalization();
//		rt.gc();

		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(caption);
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout());
			
		Composite cmpTmp = new Composite(shell, SWT.NONE);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout(1, false);
		cmpTmp.setLayout(layTmp);
		
		browser = new Browser(cmpTmp, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));			
		browser.setUrl(url);
		final ProgressDialog progress = new ProgressDialog(shell, "Loading " + url);
		progress.show();
		browser.addProgressListener(new ProgressListener() {
			private static final long serialVersionUID = -4465532799783954048L;

			@Override
			public void completed(ProgressEvent event) {
				//Dialogs.showWarning(shell, "Completed", "!!!");
				//done = true;
				progress.hide();
				//System.out.println(browser.getUrl());
			}
			
			@Override
			public void changed(ProgressEvent event) {
			}
		});
		
		Rectangle parentBounds = parent.getBounds();
		shell.setMinimumSize(parentBounds.width, parentBounds.height);		

		//--- Dialog-level buttons

//		SelectionAdapter CloseActions = new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				shell.close();
//			};
//		};
//		ClosePanel pnlActions = new ClosePanel(shell, SWT.NONE, CloseActions, false);
//		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		shell.setDefaultButton(pnlActions.btClose);
		
		shell.pack();
//		shell.setMinimumSize(shell.getSize());
//		Point startSize = shell.getMinimumSize();
//		if ( startSize.x < 350 ) startSize.x = 350;
//		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
	}

	public void showDialog () {
		//shell.setMaximized(true);		
		shell.open();
//		Cursor cursor = shell.getDisplay().getSystemCursor(SWT.CURSOR_WAIT);
//		shell.setCursor(cursor);
//		browser.setCursor(cursor);
//		BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
//			@Override
//			public void run() {
//				while (!done) {
//					try {
//						Thread.sleep(1);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					
//				}
//			}
//			
//		});
		
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
	}

}
