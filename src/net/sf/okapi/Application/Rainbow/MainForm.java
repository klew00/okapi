/*===========================================================================*/
/* Copyright (C) 2008 ENLASO Corporation, Okapi Development Team             */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.Application.Rainbow;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.Library.Base.LogForm;

public class MainForm
{
	private Shell       shell;
	private ILog        log;
	
	public MainForm (Shell p_Shell) {
		shell = p_Shell;
		log = new LogForm(shell);
		log.setTitle("Rainbow Log");
		createContent();
	}

	public void createContent () {
		GridLayout layTmp = new GridLayout();
		shell.setLayout(layTmp);
		shell.setText("Rainbow [v6 ALPHA]");
	}

	public void run () {
		try {
			Display Disp = shell.getDisplay();
			while ( !shell.isDisposed() ) {
				if (!Disp.readAndDispatch())
					Disp.sleep();
			}
		}
		finally {
			// Dispose of any global resources 
		}
	}

}
