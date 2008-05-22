/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel (at ENLASO Corporation)                  */
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

package net.sf.okapi.common.ui;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class Dialogs {
	/**
	 * Browses for one or more files.
	 * @param parent Parent of the dialog.
	 * @param title Title of the dialog box.
	 * @param allowMultiple True if the user can select more than one file.
	 * @param root Directory where to start. Use null for default directory.
	 * @param filterNames List of filter names (tab-separated) to use. Can be null.
	 * @param filterExtensions List of filter extensions (tab-separated) to use.
	 * Can be null.
	 * Must be the same number of items as for p_aFilterNames.
	 * @return An array of strings, where each string is the full path for one 
	 * of the selected files. Returns null if the user canceled the command or
	 * an error occurred.
	 */
	static public String[] browseFilenames (Shell parent,
		String title,
		boolean allowMultiple,
		String root,
		String filterNames,
		String filterExtensions)
	{
		FileDialog dlg = new FileDialog(parent, SWT.OPEN | (allowMultiple ? SWT.MULTI : 0));
		dlg.setFilterPath(root); // Can be null
		if ( filterNames != null ) {
			String[] aNames = filterNames.split("\t", -2);
			dlg.setFilterNames(aNames);
		}
		if ( filterExtensions != null ) {
			String[] aExts = filterExtensions.split("\t", -2);
			dlg.setFilterExtensions(aExts);
		}
		dlg.setText(title);
		if ( dlg.open() == null ) return null;
		String[] aPaths = dlg.getFileNames();
		for ( int i=0; i<aPaths.length; i++ ) {
			aPaths[i] = dlg.getFilterPath() + File.separator + aPaths[i];
		}
		return aPaths;
	}

	/**
	 * Browses for one or more files.
	 * @param parent Parent of the dialog.
	 * @param title Title of the dialog box.
	 * @param root Directory where to start. Use null for default directory.
	 * @param filterNames List of filter names (tab-separated) to use. Can be null.
	 * @param filterExtensions List of filter extensions (tab-separated) to use.
	 * Can be null.
	 * Must be the same number of items as for p_aFilterNames.
	 * @return The full path of the selected file or null if the user canceled
	 * the command or an error occurred.
	 */
	static public String browseFilenamesForSave (Shell parent,
			String title,
			String root,
			String filterNames,
			String filterExtensions)
		{
			FileDialog dlg = new FileDialog(parent, SWT.SAVE);
			dlg.setFilterPath(root); // Can be null
			if ( filterNames != null ) {
				String[] aNames = filterNames.split("\t", -2);
				dlg.setFilterNames(aNames);
			}
			if ( filterExtensions != null ) {
				String[] aExts = filterExtensions.split("\t", -2);
				dlg.setFilterExtensions(aExts);
			}
			dlg.setText(title);
			if ( dlg.open() == null ) return null;
			return dlg.getFilterPath() + File.separator + dlg.getFileName();
		}
	
	/**
	 * Centers a given window on a given window.
	 * @param target The window to center.
	 * @param centerOn The window where to center target.
	 * If p_CenterOn is null, the window is centered on the screen.
	 */
	static public void centerWindow (Shell target,
		Shell centerOn)
	{
		Rectangle PRect;
		Rectangle TRect = target.getBounds();
		if ( centerOn == null ) {
			Display Disp = target.getDisplay();
			PRect = Disp.getClientArea();
		}
		else PRect = centerOn.getBounds();
		
		// Compute the position and set the window
		int x = PRect.x + (PRect.width - TRect.width) / 2;
		int y = PRect.y + (PRect.height - TRect.height) / 2;
		target.setLocation (x, y);
	}

	static public void showError (Shell shell,
		String message,
		String details)
	{
		try {
			MessageBox dlg = new MessageBox(shell, SWT.ICON_ERROR);
			dlg.setMessage(message);
			dlg.setText("Error");
			dlg.open();
		}
		catch ( Exception E ) {
			System.err.println(message);
		}
	}
		
}
