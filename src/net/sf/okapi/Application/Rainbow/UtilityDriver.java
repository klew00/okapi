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

package net.sf.okapi.Application.Rainbow;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import net.sf.okapi.Filter.FilterAccess;
import net.sf.okapi.Filter.FilterItemType;
import net.sf.okapi.Library.Base.ILog;
import net.sf.okapi.utilities.PseudoTranslation;
import net.sf.okapi.utility.IUtility;

public class UtilityDriver {

	ILog                log;
	Project             prj;
	FilterAccess        fa;
	IUtility            util;
	
	public UtilityDriver (ILog newLog,
		Project newPrj,
		FilterAccess newFA)
	{
		log = newLog;
		prj = newPrj;
		fa = newFA;
		util = new PseudoTranslation();
	}
	
	public void execute (Shell shell) {
		try {
			// If there are no options to ask for,
			// ask confirmation to launch the utility
			if ( util.hasParameters() ) {
				//TODO
			}
			else {
				MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
				dlg.setMessage(String.format("You are about to execute the utility: %s\nDo you want to proceed?",
					"TODO"));
				dlg.setText("Rainbow");
				if ( dlg.open() != SWT.YES ) return;
			}
			
			log.beginTask("Pseudo Translation");
			
			for ( Input item : prj.inputList ) {
				if ( item.filterSettings.length() == 0 ) continue;
				log.message("Input: "+item.relativePath);
				
				// Load the filter
				fa.loadFilterFromFilterSettingsType1(prj.paramsFolder, item.filterSettings);
				
				// Open the input file
				fa.getFilter().openInputFile(prj.inputRoot + File.separator + item.relativePath,
					prj.sourceLanguage,
					prj.buildSourceEncoding(item));
				
				util.processStartDocument(fa.getFilter(),
					prj.buildTargetPath(item.relativePath),
					prj.targetLanguage,
					prj.buildTargetEncoding(item));
				
				while ( fa.getFilter().readItem() >FilterItemType.ENDINPUT ) {
					util.processItem(fa.getFilter().getItem());
				}
				
				util.processEndDocument();
				fa.getFilter().closeInput();
			}
		}
		catch ( Exception E ) {
			log.error(E.getLocalizedMessage());
		}
		finally {
			log.endTask(null);
		}
	}
}
