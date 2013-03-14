/*===========================================================================
  Copyright (C) 2013 by the Okapi Framework contributors
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

import net.sf.okapi.common.IUserPrompt;
import net.sf.okapi.common.exceptions.OkapiUserCanceledException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * A GUI implementation of {@link IUserPrompt}.
 */
public class UserPrompt implements IUserPrompt {

	private Shell shell;
	private String title;

	@Override
	public void initialize(Object uiParent, String title) {
		if (uiParent != null && uiParent instanceof Shell) {
			shell = (Shell)uiParent;
		} else {
			shell = new Shell();
		}
		this.title = title == null ? "Okapi" : title;
	}

	public boolean promptYesNoCancel(String message) throws OkapiUserCanceledException {
		
		MessageBox dlg = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
		dlg.setMessage(message);
		dlg.setText(title);
		switch  ( dlg.open() ) {
		case SWT.YES:
			return true;
		case SWT.NO:
			return false;
		}
		
		throw new OkapiUserCanceledException("Operation was canceled by user.");
	}

	public boolean promptOKCancel(String message) throws OkapiUserCanceledException {
		
		MessageBox dlg = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		dlg.setMessage(message); 
		dlg.setText(title);
		if  ( dlg.open() == SWT.OK ) {
			return true;
		}
		
		throw new OkapiUserCanceledException("Operation was canceled by user.");
	}
}
