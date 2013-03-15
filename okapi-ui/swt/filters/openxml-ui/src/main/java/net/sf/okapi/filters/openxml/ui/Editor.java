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

import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.filters.openxml.ConditionalParameters;
import net.sf.okapi.filters.openxml.Excell;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

@EditorFor(ConditionalParameters.class)
@SuppressWarnings({"rawtypes", "unused"})
public class Editor implements IParametersEditor {

	public final static int MSWORD=1;
	private Shell shell;
	private IContext context;
	private boolean readOnly = false;
	private boolean result = false;
	private ConditionalParameters params;
	private IHelp help;	
	private Button btnHelp;
	private Button btnOk;
	private Button btnCancel;
	private Button btnTranslateDocumentProperties;
	private Button btnTranslateComments;
	private Button btnTranslateHeadersAndFooters;
	private Button btnTranslateHiddenText;
	private Button btnStylesFromDocument;
	private Button btnColorsFromDocument;
	private Button btnExcludeExcelColumns;
	private List listExcludedWordStyles;
	private List listExcelColorsToExclude;
	private List listExcelSheet1ColumnsToExclude;
	private List listExcelSheet2ColumnsToExclude;
	private List listExcelSheet3ColumnsToExclude;
	private Button btnTranslateNotes;
	private Button btnTranslateMasters;

	public boolean edit (IParameters options,
		boolean readOnly,
		IContext context)
	{
		this.context = context;
		this.readOnly = readOnly;
		help = (IHelp)context.getObject("help");
		boolean bRes = false;
		shell = null;
		params = (ConditionalParameters)options;
		try {
			createContents();			
			return showDialog();
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
	
	/**
	 * @wbp.parser.entryPoint
	 * Create contents of the dialog.
	 */	
	protected void createContents() { // DWH 6-17-09 was private
		Shell parent = (Shell)context.getObject("shell");
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Office 2007 Filter Parameters");
		if ( parent != null ) UIUtil.inheritIcon(shell, parent);
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		//tabFolder.setLayoutData(BorderLayout.CENTER);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		tabFolder.setLayoutData(gdTmp);
		{
			TabItem tbtmGeneralOptions_1 = new TabItem(tabFolder, SWT.NONE);
			tbtmGeneralOptions_1.setText("General Options");
			{
				Composite composite = new Composite(tabFolder, SWT.NONE);
				tbtmGeneralOptions_1.setControl(composite);
				composite.setLayout(new GridLayout(1, false));
				{
					btnTranslateDocumentProperties = new Button(composite, SWT.CHECK);
					btnTranslateDocumentProperties.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
					btnTranslateDocumentProperties.setSelection(true);
					btnTranslateDocumentProperties.setText("Translate Document Properties");
				}
				{
					btnTranslateComments = new Button(composite, SWT.CHECK);
					btnTranslateComments.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
					btnTranslateComments.setSelection(true);
					btnTranslateComments.setText("Translate Comments");
				}
			}
		}
		{
			TabItem tbtmWordOptions = new TabItem(tabFolder, SWT.NONE);
			tbtmWordOptions.setText("Word Options");
			{
				Composite composite = new Composite(tabFolder, SWT.NONE);
				tbtmWordOptions.setControl(composite);
				composite.setLayout(new GridLayout(2, false));
				{
					btnTranslateHeadersAndFooters = new Button(composite, SWT.CHECK);
					btnTranslateHeadersAndFooters.setSelection(true);
					btnTranslateHeadersAndFooters.setText("Translate Headers and Footers");
				}
				//compositeWordStyles.setLayoutData(new RowData(292, 67));
//						{
//							btnStylesFromDocument = new Button(compositeWordStyles, SWT.NONE);
//							btnStylesFromDocument.setBounds(269, 8, 146, 23);
//							btnStylesFromDocument.setText("Styles from Document ...");
//						}
				{
					Label lblStyles = new Label(composite, SWT.NONE);
					lblStyles.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
					lblStyles.setText("Styles to Exclude:");
				}
				{
					btnTranslateHiddenText = new Button(composite, SWT.CHECK);
					btnTranslateHiddenText.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
					btnTranslateHiddenText.setSelection(true);
					btnTranslateHiddenText.setText("Translate Hidden Text");
				}
				{
					listExcludedWordStyles = new List(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
					GridData gd_listExcludedWordStyles = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
					gd_listExcludedWordStyles.heightHint = 40;
					listExcludedWordStyles.setLayoutData(gd_listExcludedWordStyles);
					listExcludedWordStyles.setItems(new String[] {"Emphasis", "ExcludeCharacterStyle", "ExcludeParagraphStyle", "Heading1", "Heading2", "Normal", "Title", "Strong", "Subtitle", "tw4winExternal"});
				}
			}
		}
		{
			TabItem tbtmExcelOptions = new TabItem(tabFolder, SWT.NONE);
			tbtmExcelOptions.setText("Excel Options");
			{
				Composite composite = new Composite(tabFolder, SWT.NONE);
				tbtmExcelOptions.setControl(composite);
				composite.setLayout(new GridLayout(2, false));
				{
					btnExcludeExcelColumns = new Button(composite, SWT.CHECK);
					btnExcludeExcelColumns.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
					btnExcludeExcelColumns.setSelection(true);
					btnExcludeExcelColumns.setText("Exclude Marked Columns in Each Sheet");
				}
				new Label(composite, SWT.NONE);
				{
					Label lblColorsToExclude = new Label(composite, SWT.NONE);
					lblColorsToExclude.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
					lblColorsToExclude.setText("Colors to Exclude:");
				}
				{
					Label lblSheetColumns = new Label(composite, SWT.NONE);
					lblSheetColumns.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
					lblSheetColumns.setText("Sheet 1 Columns to Exlude:");
				}
				//						{
				//							btnColorsFromDocument = new Button(compositeExcelColors, SWT.NONE);
				//							btnColorsFromDocument.setBounds(289, 10, 146, 23);
				//							btnColorsFromDocument.setText("Colors from Document ...");
				//						}
									{
										listExcelColorsToExclude = new List(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
										GridData gd_listExcelColorsToExclude = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
										gd_listExcelColorsToExclude.heightHint = 40;
										listExcelColorsToExclude.setLayoutData(gd_listExcelColorsToExclude);
										listExcelColorsToExclude.setItems(new String[] {"blue", "dark blue", "dark red", "green", "light blue", "light green", "orange", "purple", "red", "yellow"});
									}
				{
					listExcelSheet1ColumnsToExclude = new List(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
					GridData gd_listExcelSheet1ColumnsToExclude = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
					gd_listExcelSheet1ColumnsToExclude.heightHint = 40;
					listExcelSheet1ColumnsToExclude.setLayoutData(gd_listExcelSheet1ColumnsToExclude);
					listExcelSheet1ColumnsToExclude.setItems(new String[] {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "X", "Y", "Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL", "AM", "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AX", "AY", "AZ", "BA", "BB", "BC", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BK", "BL", "BM", "BN", "BO", "BP", "BQ", "BR", "BS", "BT", "BU", "BV", "BX", "BY", "BZ", "CA", "CB", "CC", "CD", "CE", "CF", "CG", "CH", "CI", "CJ", "CK", "CL", "CM", "CN", "CO", "CP", "CQ", "CR", "CS", "CT", "CU", "CV", "CX", "CY", "CZ", "DA", "DB", "DC", "DD", "DE", "DF", "DG", "DH", "DI", "DJ", "DK", "DL", "DM", "DN", "DO", "DP", "DQ", "DR", "DS", "DT", "DU", "DV", "DX", "DY", "DZ", "EA", "EB", "EC", "ED", "EE", "EF", "EG", "EH", "EI", "EJ", "EK", "EL", "EM", "EN", "EO", "EP", "EQ", "ER", "ES", "ET", "EU", "EV", "EX", "EY", "EZ"});
				}
				{
					Label lblSheetColumns_1 = new Label(composite, SWT.NONE);
					lblSheetColumns_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
					lblSheetColumns_1.setText("Sheet 2 Columns to Exlude:");
				}
				{
					Label lblSheetColumns_2 = new Label(composite, SWT.NONE);
					lblSheetColumns_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
					lblSheetColumns_2.setText("Sheets 3 (and higher) Columns to Exlude:");
				}
				{
					listExcelSheet2ColumnsToExclude = new List(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
					GridData gd_listExcelSheet2ColumnsToExclude = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
					gd_listExcelSheet2ColumnsToExclude.heightHint = 40;
					listExcelSheet2ColumnsToExclude.setLayoutData(gd_listExcelSheet2ColumnsToExclude);
					listExcelSheet2ColumnsToExclude.setItems(new String[] {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "X", "Y", "Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL", "AM", "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AX", "AY", "AZ", "BA", "BB", "BC", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BK", "BL", "BM", "BN", "BO", "BP", "BQ", "BR", "BS", "BT", "BU", "BV", "BX", "BY", "BZ", "CA", "CB", "CC", "CD", "CE", "CF", "CG", "CH", "CI", "CJ", "CK", "CL", "CM", "CN", "CO", "CP", "CQ", "CR", "CS", "CT", "CU", "CV", "CX", "CY", "CZ", "DA", "DB", "DC", "DD", "DE", "DF", "DG", "DH", "DI", "DJ", "DK", "DL", "DM", "DN", "DO", "DP", "DQ", "DR", "DS", "DT", "DU", "DV", "DX", "DY", "DZ", "EA", "EB", "EC", "ED", "EE", "EF", "EG", "EH", "EI", "EJ", "EK", "EL", "EM", "EN", "EO", "EP", "EQ", "ER", "ES", "ET", "EU", "EV", "EX", "EY", "EZ"});
				}
				{
					listExcelSheet3ColumnsToExclude = new List(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
					GridData gd_listExcelSheet3ColumnsToExclude = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
					gd_listExcelSheet3ColumnsToExclude.heightHint = 40;
					listExcelSheet3ColumnsToExclude.setLayoutData(gd_listExcelSheet3ColumnsToExclude);
					listExcelSheet3ColumnsToExclude.setItems(new String[] {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "X", "Y", "Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL", "AM", "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AX", "AY", "AZ", "BA", "BB", "BC", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BK", "BL", "BM", "BN", "BO", "BP", "BQ", "BR", "BS", "BT", "BU", "BV", "BX", "BY", "BZ", "CA", "CB", "CC", "CD", "CE", "CF", "CG", "CH", "CI", "CJ", "CK", "CL", "CM", "CN", "CO", "CP", "CQ", "CR", "CS", "CT", "CU", "CV", "CX", "CY", "CZ", "DA", "DB", "DC", "DD", "DE", "DF", "DG", "DH", "DI", "DJ", "DK", "DL", "DM", "DN", "DO", "DP", "DQ", "DR", "DS", "DT", "DU", "DV", "DX", "DY", "DZ", "EA", "EB", "EC", "ED", "EE", "EF", "EG", "EH", "EI", "EJ", "EK", "EL", "EM", "EN", "EO", "EP", "EQ", "ER", "ES", "ET", "EU", "EV", "EX", "EY", "EZ"});
				}
			}
		}
		{
			TabItem tbtmPowerpointOptions = new TabItem(tabFolder, SWT.NONE);
			tbtmPowerpointOptions.setText("Powerpoint Options");
			{
				Composite composite = new Composite(tabFolder, SWT.NONE);
				tbtmPowerpointOptions.setControl(composite);
				composite.setLayout(new GridLayout(1, false));
				{
					btnTranslateNotes = new Button(composite, SWT.CHECK);
					btnTranslateNotes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
					btnTranslateNotes.setSelection(true);
					btnTranslateNotes.setText("Translate Notes");
				}
				{
					btnTranslateMasters = new Button(composite, SWT.CHECK);
					btnTranslateMasters.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
					btnTranslateMasters.setSelection(true);
					btnTranslateMasters.setText("Translate Masters");
				}
			}
		}
				
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) {
					if ( help != null ) help.showWiki("OpenOffice Filter");
					return;
				}
				if ( e.widget.getData().equals("o") ) {
					if ( !saveData() ) return;
					result = true;
				}
				shell.close();
			};
		};
		
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		pnlActions.setLayoutData(gdTmp);
		pnlActions.btOK.setEnabled(!readOnly);
		if ( !readOnly ) {
			shell.setDefaultButton(pnlActions.btOK);
		}

		shell.pack();
		Rectangle Rect = shell.getBounds();
		shell.setMinimumSize(Rect.width, Rect.height);
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 300 ) startSize.x = 300; 
		if ( startSize.y < 200 ) startSize.y = 200; 
		shell.setSize(new Point(541, 367));
		Dialogs.centerWindow(shell, parent);
		setData();
	}
	
	private boolean showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
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
		btnTranslateDocumentProperties.setSelection(params.bPreferenceTranslateDocProperties) ;
		btnTranslateComments.setSelection(params.bPreferenceTranslateComments);
		btnTranslateHeadersAndFooters.setSelection(params.bPreferenceTranslateWordHeadersFooters);
		btnTranslateHiddenText.setSelection(params.bPreferenceTranslateWordHidden);
		btnTranslateNotes.setSelection(params.bPreferenceTranslatePowerpointNotes);
		btnTranslateMasters.setSelection(params.bPreferenceTranslatePowerpointMasters);
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
				listExcludedWordStyles.setSelection(sMulti);
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
				listExcelColorsToExclude.setSelection(sMulti);
			}
		}
		btnExcludeExcelColumns.setSelection(params.bPreferenceTranslateExcelExcludeColumns);
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
				listExcelSheet1ColumnsToExclude.setSelection(sMulti);
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
				listExcelSheet2ColumnsToExclude.setSelection(sMulti);
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
				listExcelSheet3ColumnsToExclude.setSelection(sMulti);
			}
		}
	}
	
	private boolean saveData () {
		String sColor;
		String sArray[];
		String sRGB;
		int len;
		params.bPreferenceTranslateDocProperties = btnTranslateDocumentProperties.getSelection() ;
		params.bPreferenceTranslateComments = btnTranslateComments.getSelection();
		params.bPreferenceTranslateWordHeadersFooters = btnTranslateHeadersAndFooters.getSelection();
		params.bPreferenceTranslateWordHidden = btnTranslateHiddenText.getSelection();
		params.bPreferenceTranslatePowerpointNotes = btnTranslateNotes.getSelection();
		params.bPreferenceTranslatePowerpointMasters = btnTranslateMasters.getSelection();

		// Exclude text in certain styles from translation in Word
		sArray = listExcludedWordStyles.getSelection(); // selected items
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
		sArray = listExcelColorsToExclude.getSelection(); // selected items
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
		params.bPreferenceTranslateExcelExcludeColumns = btnExcludeExcelColumns.getSelection();
		if (params.tsExcelExcludedColumns==null)
			params.tsExcelExcludedColumns = new TreeSet<String>();
		else
			params.tsExcelExcludedColumns.clear();
		params.bPreferenceTranslateExcelExcludeColumns = btnExcludeExcelColumns.getSelection();
		if (params.bPreferenceTranslateExcelExcludeColumns)
		{
			sArray = listExcelSheet1ColumnsToExclude.getSelection(); // selected items
			len = sArray.length;
			if (len>0)
			{
				for(int i=0;i<len;i++)
					params.tsExcelExcludedColumns.add("1"+sArray[i]);
			}
			sArray = listExcelSheet2ColumnsToExclude.getSelection(); // selected items
			len = sArray.length;
			if (len>0)
			{
				for(int i=0;i<len;i++)
					params.tsExcelExcludedColumns.add("2"+sArray[i]);
			}
			sArray = listExcelSheet3ColumnsToExclude.getSelection(); // selected items
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
}

