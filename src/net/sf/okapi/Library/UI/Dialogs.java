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

package net.sf.okapi.Library.UI;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class Dialogs {
	/**
	 * Browses for one or more files.
	 * @param p_sTitle Title of the dialog box.
	 * @param p_bMultiple True if the user can select more than one file.
	 * @param p_sRoot Directory where to start. Use null for default directory.
	 * @param p_sFilterNames List of filter names (tab-separated) to use. Can be null.
	 * @param p_sFilterExtensions List of filter extensions (tab-separated) to use. Can be null.
	 * Must be the same number of items as for p_aFilterNames.
	 * @return An array of strings, where each string is the full path for one 
	 * of the selected files, or null if the user canceled the command.
	 */
	static public String[] browseFilenames (Shell p_Parent,
		String p_sTitle,
		boolean p_bMultiple,
		String p_sRoot,
		String p_sFilterNames,
		String p_sFilterExtensions)
	{
		FileDialog Dlg = new FileDialog(p_Parent, SWT.OPEN | (p_bMultiple ? SWT.MULTI : 0));
		Dlg.setFilterPath(p_sRoot); // Can be null
		if ( p_sFilterNames != null ) {
			String[] aNames = p_sFilterNames.split("\t", -2);
			Dlg.setFilterNames(aNames);
		}
		if ( p_sFilterExtensions != null ) {
			String[] aExts = p_sFilterExtensions.split("\t", -2);
			Dlg.setFilterExtensions(aExts);
		}
		Dlg.setText(p_sTitle);
		if ( Dlg.open() == null ) return null;
		String[] aPaths = Dlg.getFileNames();
		for ( int i=0; i<aPaths.length; i++ ) {
			aPaths[i] = Dlg.getFilterPath() + File.separator + aPaths[i];
		}
		return aPaths;
	}

	/**
	 * Centers a given window on a given window.
	 * @param p_Target The window to center.
	 * @param p_CenterOn The window where to center p_Target.
	 * If p_CenterOn is null, the window is centered on the screen.
	 */
	static public void centerWindow (Shell p_Target,
		Shell p_CenterOn)
	{
		Rectangle PRect;
		Rectangle TRect = p_Target.getBounds();
		if ( p_CenterOn == null ) {
			Display Disp = p_Target.getDisplay();
			PRect = Disp.getClientArea();
		}
		else PRect = p_CenterOn.getBounds();
		
		// Compute the position and set the window
		int x = PRect.x + (PRect.width - TRect.width) / 2;
		int y = PRect.y + (PRect.height - TRect.height) / 2;
		p_Target.setLocation (x, y);
	}
}
