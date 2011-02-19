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

package net.sf.okapi.filters.openxml.ui;

import java.util.Iterator;
import java.util.TreeSet;

import net.sf.okapi.common.BaseContext;
import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.filters.openxml.ConditionalParameters;
import net.sf.okapi.filters.openxml.Excell;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

public class Editor implements IParametersEditor, SelectionListener {
	
	public final static int MSWORD=1;
	private Shell shell;
	private boolean result = false;
	private ConditionalParameters params;
	private IHelp help;
	private UIEditor ed;
	private BaseContext context;

	/**
	 * Invokes the editor for the openxml filter parameters.
	 * @param p_Options The option object of the action.
	 * @param p_Object The SWT Shell object of the parent shell in the UI.
	 */

	public void widgetSelected(SelectionEvent e) {
		result = false;
		if ( e.widget==ed.btnHelp)
		{
			if ( help != null ) help.showWiki("OpenXML Filter");
			return;
		}
		else if ( e.widget==ed.btnOk)
		{
			if ( !saveData() ) return;
			result = true;
		}
		else if ( e.widget==ed.btnCancel)
			result = false;
//		btnStylesFromDocument; // open a Word document and read the styles into the list box
//		btnColorsFromDocument; // open an Excel document and read the styles into the list box
		shell.close();
	}

	public void widgetDefaultSelected(SelectionEvent e) { // DWH 6-17-09 because it has to be implemented
		widgetSelected(e);		
	}	
	public boolean edit(IParameters paramsObject, IContext context) {
		// TODO Auto-generated method stub
		return edit(paramsObject,false,context);
	}
	public boolean edit (IParameters p_Options,
		boolean readOnly,
		IContext context)
	{
		help = (IHelp)context.getObject("help");
		boolean bRes = false;
		shell = null;
		params = (net.sf.okapi.filters.openxml.ConditionalParameters)p_Options;
		try {
			shell = new Shell((Shell)context.getObject("shell"), SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
//			create((Shell)context.getObject("shell"));
			ed = new UIEditor(shell,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			ed.open(this);
//			return true;
			bRes = result; // DWH 6-26-09 result is set in widgetSelected above
		}
		catch ( Exception E ) {
			Dialogs.showError(shell, E.getLocalizedMessage(), null);
			bRes = false;
		}
		finally {
			// Dispose of the shell, but not of the display
			if ( shell != null ) shell.dispose();
		}
		return bRes;
	}
	
	public IParameters createParameters () {
		return new ConditionalParameters();
	}
		
	protected void setData ()
	{
		Iterator it;
		String sYmphony;
		String sRGB;
		Excell eggshell;
		String sDuraCell;
		String sMulti[];
		TreeSet<String> tsColors;
		Object o[];
		int ndx;
		int siz;
		ed.btnTranslateDocumentProperties.setSelection(params.bPreferenceTranslateDocProperties) ;
		ed.btnTranslateComments.setSelection(params.bPreferenceTranslateComments);
		ed.btnTranslateHeadersAndFooters.setSelection(params.bPreferenceTranslateWordHeadersFooters);
		ed.btnTranslateHiddenText.setSelection(params.bPreferenceTranslateWordHidden);
		ed.btnTranslateNotes.setSelection(params.bPreferenceTranslatePowerpointNotes);
		ed.btnTranslateMasters.setSelection(params.bPreferenceTranslatePowerpointMasters);
		if (!params.bPreferenceTranslateWordAllStyles &&
			params.tsExcludeWordStyles!=null && !params.tsExcludeWordStyles.isEmpty())
		{
			it = params.tsExcludeWordStyles.iterator();
			siz = params.tsExcludeWordStyles.size();
			if (siz>0)
			{
				sMulti = new String[siz];
				ndx = 0;
				while(it.hasNext())
				{
					sMulti[ndx++] = (String)it.next();
				}
				ed.listExcludedWordStyles.setSelection(sMulti);
			}
		}	
		if (params.bPreferenceTranslateExcelExcludeColors &&
			params.tsExcelExcludedColors!=null && !params.tsExcelExcludedColors.isEmpty())
		{
			tsColors = new TreeSet<String>();
			it = params.tsExcelExcludedColors.iterator();
			while(it.hasNext())
			{
				sRGB = (String)it.next();
				if (sRGB.equals("FF0070C0"))
					sRGB = "blue";
				else if (sRGB.equals("FF00B0F0"))
					sRGB = "light blue";
				else if (sRGB.equals("FF00B050"))
					sRGB = "green";
				else if (sRGB.equals("FF7030A0"))
					sRGB = "purple";
				else if (sRGB.equals("FFFF0000"))
					sRGB = "red";
				else if (sRGB.equals("FFFFFF00"))
					sRGB = "yellow";
				else if (sRGB.equals("FFC00000"))
					sRGB = "dark red";
				else if (sRGB.equals("FF92D050"))
					sRGB = "light green";
				else if (sRGB.equals("FFFFC000"))
					sRGB = "orange";
				else if (sRGB.equals("FF002060"))
					sRGB = "dark blue";
				tsColors.add(sRGB);
			}
			siz = tsColors.size();
			if (siz>0)
			{
				sMulti = new String[siz];
				it = tsColors.iterator();
				ndx = 0;
				while(it.hasNext())
					sMulti[ndx++] = (String)it.next();
				ed.listExcelColorsToExclude.setSelection(sMulti);
			}
		}
		ed.btnExcludeExcelColumns.setSelection(params.bPreferenceTranslateExcelExcludeColumns);
		if (params.bPreferenceTranslateExcelExcludeColumns &&
			params.tsExcelExcludedColumns!=null && !params.tsExcelExcludedColumns.isEmpty())
		{
			siz = 0;
			for(it=params.tsExcelExcludedColumns.iterator();it.hasNext();)
			{
				sYmphony = (String)it.next();			
				eggshell = new Excell(sYmphony);
				sDuraCell = eggshell.getColumn();
				if (eggshell.getSheet().equals("1"))
					siz++;
			}
			if (siz>0)
			{
				ndx = 0;
				sMulti = new String[siz];
				for(it=params.tsExcelExcludedColumns.iterator();it.hasNext();)
				{
					sYmphony = (String)it.next();			
					eggshell = new Excell(sYmphony);
					sDuraCell = eggshell.getColumn();
					if (eggshell.getSheet().equals("1"))
						sMulti[ndx++] = sDuraCell;
				}
				ed.listExcelSheet1ColumnsToExclude.setSelection(sMulti);
			}

			siz = 0;
			for(it=params.tsExcelExcludedColumns.iterator();it.hasNext();)
			{
				sYmphony = (String)it.next();			
				eggshell = new Excell(sYmphony);
				sDuraCell = eggshell.getColumn();
				if (eggshell.getSheet().equals("2"))
					siz++;
			}
			if (siz>0)
			{
				ndx = 0;
				sMulti = new String[siz];
				for(it=params.tsExcelExcludedColumns.iterator();it.hasNext();)
				{
					sYmphony = (String)it.next();			
					eggshell = new Excell(sYmphony);
					sDuraCell = eggshell.getColumn();
					if (eggshell.getSheet().equals("2"))
						sMulti[ndx++] = sDuraCell;
				}
				ed.listExcelSheet2ColumnsToExclude.setSelection(sMulti);
			}

			siz = 0;
			for(it=params.tsExcelExcludedColumns.iterator();it.hasNext();)
			{
				sYmphony = (String)it.next();			
				eggshell = new Excell(sYmphony);
				sDuraCell = eggshell.getColumn();
				if (eggshell.getSheet().equals("3"))
					siz++;
			}
			if (siz>0)
			{
				ndx = 0;
				sMulti = new String[siz];
				for(it=params.tsExcelExcludedColumns.iterator();it.hasNext();)
				{
					sYmphony = (String)it.next();			
					eggshell = new Excell(sYmphony);
					sDuraCell = eggshell.getColumn();
					if (eggshell.getSheet().equals("3"))
						sMulti[ndx++] = sDuraCell;
				}
				ed.listExcelSheet3ColumnsToExclude.setSelection(sMulti);
			}
		}
	}
	
	private boolean saveData () {
		String sColor;
		String sArray[];
		String sRGB;
		int len;
		params.bPreferenceTranslateDocProperties = ed.btnTranslateDocumentProperties.getSelection() ;
		params.bPreferenceTranslateComments = ed.btnTranslateComments.getSelection();
		params.bPreferenceTranslateWordHeadersFooters = ed.btnTranslateHeadersAndFooters.getSelection();
		params.bPreferenceTranslateWordHidden = ed.btnTranslateHiddenText.getSelection();
		params.bPreferenceTranslatePowerpointNotes = ed.btnTranslateNotes.getSelection();
		params.bPreferenceTranslatePowerpointMasters = ed.btnTranslateMasters.getSelection();

		// Exclude text in certain styles from translation in Word
		sArray = ed.listExcludedWordStyles.getSelection(); // selected items
		if (params.tsExcludeWordStyles==null)
			params.tsExcludeWordStyles = new TreeSet<String>();
		else
			params.tsExcludeWordStyles.clear();
		len = sArray.length;
		if (len>0)
		{
			params.bPreferenceTranslateWordAllStyles = false;
			for(int i=0;i<len;i++)
				params.tsExcludeWordStyles.add(sArray[i]);
		}
		else
			params.bPreferenceTranslateWordAllStyles = true;
		
		// Exclude text in certain colors from translation in Excel
		sArray = ed.listExcelColorsToExclude.getSelection(); // selected items
		if (params.tsExcelExcludedColors==null)
			params.tsExcelExcludedColors = new TreeSet<String>();
		else
			params.tsExcelExcludedColors.clear();
		len = sArray.length;
		if (len>0)
		{
			params.bPreferenceTranslateExcelExcludeColors = true;
			for(int i=0;i<len;i++)
			{
				sColor = sArray[i];
				sRGB = null;
/*
				if (sColor.equals("black"))
					sRGB = "000000FF";
				else if (sColor.equals("blue"))
				{
					sRGB = "FFFF000";
					params.tsExcelExcludedColors.add("FF0070C0");
				}
				else if (sColor.equals("cyan"))
					sRGB = "FF000000";
				else if (sColor.equals("green"))
					sRGB = "FF00FF00";
				else if (sColor.equals("magenta"))
					sRGB = "00FF0000";
				else if (sColor.equals("red"))
					sRGB = "00FFFF00";
				else if (sColor.equals("white"))
					sRGB = "00000000";
				else if (sColor.equals("yellow"))
					sRGB = "0000FF00";
*/
				if (sColor.equals("blue")) // FF002060
					sRGB = "FF0070C0";
				else if (sColor.equals("light blue"))
					sRGB = "FF00B0F0";
				else if (sColor.equals("green"))
					sRGB = "FF00B050";
				else if (sColor.equals("purple"))
					sRGB = "FF7030A0";
				else if (sColor.equals("red"))
					sRGB = "FFFF0000";
				else if (sColor.equals("yellow"))
					sRGB = "FFFFFF00";
				else if (sColor.equals("dark red"))
					sRGB = "FFC00000";
				else if (sColor.equals("light green"))
					sRGB = "FF92D050";
				else if (sColor.equals("orange"))
					sRGB = "FFFFC000";
				else if (sColor.equals("dark blue"))
					sRGB = "FF002060";
				if (sRGB!=null)
					params.tsExcelExcludedColors.add(sRGB);
			}
		}
		else
			params.bPreferenceTranslateExcelExcludeColors = false;
		
		// Exclude text in certain columns in Excel in sheets 1, 2, or 3
		params.bPreferenceTranslateExcelExcludeColumns = ed.btnExcludeExcelColumns.getSelection();
		if (params.tsExcelExcludedColumns==null)
			params.tsExcelExcludedColumns = new TreeSet<String>();
		else
			params.tsExcelExcludedColumns.clear();
		params.bPreferenceTranslateExcelExcludeColumns = ed.btnExcludeExcelColumns.getSelection();
		if (params.bPreferenceTranslateExcelExcludeColumns)
		{
			sArray = ed.listExcelSheet1ColumnsToExclude.getSelection(); // selected items
			len = sArray.length;
			if (len>0)
			{
				for(int i=0;i<len;i++)
					params.tsExcelExcludedColumns.add("1"+sArray[i]);
			}
			sArray = ed.listExcelSheet2ColumnsToExclude.getSelection(); // selected items
			len = sArray.length;
			if (len>0)
			{
				for(int i=0;i<len;i++)
					params.tsExcelExcludedColumns.add("2"+sArray[i]);
			}
			sArray = ed.listExcelSheet3ColumnsToExclude.getSelection(); // selected items
			len = sArray.length;
			if (len>0)
			{
				for(int i=0;i<len;i++)
					params.tsExcelExcludedColumns.add("3"+sArray[i]);
			}
		}
		params.nFileType = MSWORD; // DWH 6-27-09
		return true;
	}
	public ConditionalParameters getParametersFromUI(ConditionalParameters cparams)
	{
		context = new BaseContext();
		edit(cparams,context);
		return cparams;
	}
}
