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

package net.sf.okapi.ui.filters.plaintext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.filters.plaintext.common.CompoundParameters;
import net.sf.okapi.ui.filters.plaintext.common.IParametersEditorPage;
import net.sf.okapi.ui.filters.plaintext.common.SWTUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * 
 * @version 0.1, 13.06.2009
 * @author Sergei Vasilyev
 */

public class GeneralTab extends Composite implements IParametersEditorPage {
	
	private Group grpExtraction;
	private Group grpSplicedLines;
	private Label lblSplicer;
	private Combo comboSplicer;
	private Button btnCreateInlineCodes;
	private Text textSplicer;
	private Group grpRegex;
	private Text edExpression;
	private Spinner edSource;
	private Label lblTextUnitExtraction;
	private Label lblSrcGroup;
	private Button btnLines;
	private Button btnUseRegex;
	private Label lblSample;
	private Composite composite_2;
	private Text edSample;
	private Text edResult;
	private Pattern fullPattern;
	private boolean busy;
	private Composite composite_3;
	private Button chkDotAll;
	private Button chkIgnoreCase;
	private Button chkMultiline;
	private Button btnPara;
	private Label label;
	private FormData formData;
	private FormData formData_2;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GeneralTab(Composite parent, int style) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(2, false));
		
		busy = true;
		
		grpExtraction = new Group(this, SWT.NONE);
		grpExtraction.setLayout(new FormLayout());
		grpExtraction.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpExtraction.setText("Extraction mode");
		
		btnPara = new Button(grpExtraction, SWT.RADIO);
		{
			formData = new FormData();
			formData.left = new FormAttachment(0, 10);
			formData.right = new FormAttachment(100, -10);
			formData.top = new FormAttachment(0, 10);
			formData.width = 189;
			btnPara.setLayoutData(formData);
		}
		btnPara.setText("Extract by paragraphs");
		
		btnLines = new Button(grpExtraction, SWT.RADIO);
		{
			formData_2 = new FormData();
			formData_2.left = new FormAttachment(0, 10);
			formData_2.right = new FormAttachment(100, -10);
			formData_2.top = new FormAttachment(btnPara, 6);
			formData_2.width = 189;
			btnLines.setLayoutData(formData_2);
		}
		btnLines.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				interop();
			}
		});
		btnLines.setText("Extract by lines");
		
		btnUseRegex = new Button(grpExtraction, SWT.RADIO);
		{
			FormData formData_1 = new FormData();
			formData_1.left = new FormAttachment(btnPara, 0, SWT.LEFT);
			formData_1.top = new FormAttachment(btnLines, 6);
			formData_1.right = new FormAttachment(100, -10);
			formData_1.width = 189;
			btnUseRegex.setLayoutData(formData_1);
		}
		btnUseRegex.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				interop();
			}
		});
		btnUseRegex.setText("Extract with a rule");
		
		grpSplicedLines = new Group(this, SWT.NONE);
		grpSplicedLines.setLayout(new FormLayout());
		grpSplicedLines.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpSplicedLines.setText("Spliced lines");
		
		lblSplicer = new Label(grpSplicedLines, SWT.NONE);
		{
			FormData formData_1 = new FormData();
			formData_1.top = new FormAttachment(0, 10);
			formData_1.left = new FormAttachment(0, 10);
			formData_1.width = 51;
			lblSplicer.setLayoutData(formData_1);
		}
		lblSplicer.setAlignment(SWT.RIGHT);
		lblSplicer.setText("Splicer:");
		
		comboSplicer = new Combo(grpSplicedLines, SWT.NONE);
		{
			FormData formData_1 = new FormData();
			formData_1.top = new FormAttachment(lblSplicer, 0, SWT.TOP);
			formData_1.left = new FormAttachment(lblSplicer, 6);
			comboSplicer.setLayoutData(formData_1);
		}
		comboSplicer.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				interop();
			}
		});
		comboSplicer.setItems(new String[] {"None", "Backslash (\\)", "Underscore (_)", "Custom"});
		//comboSplicer.setText(comboSplicer.getItem(0));
		comboSplicer.select(0);
		//		composite_1.setTabList(new Control[]{comboSplicer, btnCreateInlineCodes, textSplicer});
				
				textSplicer = new Text(grpSplicedLines, SWT.BORDER);
				{
					FormData formData_1 = new FormData();
					formData_1.right = new FormAttachment(comboSplicer, 0, SWT.RIGHT);
					formData_1.top = new FormAttachment(comboSplicer, 6);
					formData_1.left = new FormAttachment(comboSplicer, 0, SWT.LEFT);
					textSplicer.setLayoutData(formData_1);
				}
				
				btnCreateInlineCodes = new Button(grpSplicedLines, SWT.CHECK);
				{
					FormData formData_1 = new FormData();
					formData_1.right = new FormAttachment(100, -10);
					formData_1.top = new FormAttachment(textSplicer, 6);
					formData_1.left = new FormAttachment(comboSplicer, 0, SWT.LEFT);
					btnCreateInlineCodes.setLayoutData(formData_1);
				}
				btnCreateInlineCodes.setText("Create inline codes for splicers");
		
		grpRegex = new Group(this, SWT.NONE);
		grpRegex.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		grpRegex.setLayout(new FormLayout());
		grpRegex.setText("Extraction rule");
		
		composite_2 = new Composite(grpRegex, SWT.NONE);
		composite_2.setLayout(new GridLayout(5, false));
		FormData formData_4 = new FormData();
		formData_4.top = new FormAttachment(0, 4);
		formData_4.right = new FormAttachment(100);
		formData_4.left = new FormAttachment(0, 25);
		formData_4.bottom = new FormAttachment(100, -4);
		formData_4.width = 414;
		composite_2.setLayoutData(formData_4);
		
		lblTextUnitExtraction = new Label(composite_2, SWT.NONE);
		lblTextUnitExtraction.setAlignment(SWT.RIGHT);
		lblTextUnitExtraction.setText("Regular expression:");
		
		edExpression = new Text(composite_2, SWT.BORDER);
		edExpression.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateResults();
			}
		});
		edExpression.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		label = new Label(composite_2, SWT.NONE);
		label.setText("    ");
		
		lblSrcGroup = new Label(composite_2, SWT.NONE);
		lblSrcGroup.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSrcGroup.setAlignment(SWT.RIGHT);
		lblSrcGroup.setText("Source group:");
		
		edSource = new Spinner(composite_2, SWT.BORDER);
		edSource.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateResults();
			}
		});
		new Label(composite_2, SWT.NONE);
		
		lblSample = new Label(composite_2, SWT.NONE);
		lblSample.setAlignment(SWT.RIGHT);
		lblSample.setText("Sample:");
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		
		edSample = new Text(composite_2, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		edSample.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateResults();
			}
		});
		edSample.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		edSample.setText("");
		new Label(composite_2, SWT.NONE);
		
		composite_3 = new Composite(composite_2, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1));
		composite_3.setLayout(new GridLayout(1, false));
		
		chkDotAll = new Button(composite_3, SWT.CHECK);
		chkDotAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateResults();
			}
		});
		chkDotAll.setText("Dot also matches line-feed");
		chkDotAll.setText("Dot also matches line-feed");
		
		chkIgnoreCase = new Button(composite_3, SWT.CHECK);
		chkIgnoreCase.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateResults();
			}
		});
		chkIgnoreCase.setText("Ignore case difference");
		
		chkMultiline = new Button(composite_3, SWT.CHECK);
		chkMultiline.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateResults();
			}
		});
		chkMultiline.setText("Multi-line");
		new Label(composite_2, SWT.NONE);
		
		edResult = new Text(composite_2, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		edResult.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		edResult.setEditable(false);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
		new Label(composite_2, SWT.NONE);
//		composite_2.setTabList(new Control[]{edExpression, edSource, edSample, composite_3, edResult});
		
		busy = false;
				
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private int getRegexOptions () {
		int tmp = 0;
		
		if (chkDotAll == null) return 0;
		if (chkIgnoreCase == null) return 0;
		if (chkMultiline == null) return 0;
		
		if ( chkDotAll.getSelection() ) tmp |= Pattern.DOTALL;
		if ( chkIgnoreCase.getSelection() ) tmp |= Pattern.CASE_INSENSITIVE;
		if ( chkMultiline.getSelection() ) tmp |= Pattern.MULTILINE;
		return tmp;
	}
	
	private void setRegexOptions (int value) {
		
		chkDotAll.setSelection((value & Pattern.DOTALL) == Pattern.DOTALL);
		chkIgnoreCase.setSelection((value & Pattern.CASE_INSENSITIVE) == Pattern.CASE_INSENSITIVE);
		chkMultiline.setSelection((value & Pattern.MULTILINE) == Pattern.MULTILINE);		
	}
	
	
	private String getSampleText() {
		// Change different line breaks type into \n cases
		String tmp = edSample.getText();
		tmp = tmp.replaceAll("\r\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		tmp = tmp.replace("\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		return tmp.replace("\r", "\n");  //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private boolean updateResults () {
		boolean result = true;
		try {
			if (busy) return false;
			
			// Get the values
			fullPattern = Pattern.compile(edExpression.getText(), getRegexOptions());
			//int source = Integer.valueOf(edSource.getText());
			int source = edSource.getSelection();
			
			Matcher m1 = fullPattern.matcher(getSampleText());
			StringBuilder tmp = new StringBuilder();
			
			int startSearch = 0;
			
			while ( m1.find(startSearch) ) {
				
				if ( m1.start() == m1.end() ) break;
				boolean hasGroup = false;
				
				if ( tmp.length() > 0 ) tmp.append("-----\n");
				
				if (source != 0 ) {
					tmp.append("Source=[" + m1.group(source) + "]\n");
					hasGroup = true;
				}
				
				if ( !hasGroup ) tmp.append("Expression=[" + m1.group() + "]\n");
				startSearch = m1.end();
			}
			
			// If there is no match: tell it
			if ( tmp.length() == 0 ) {
				tmp.append("<No match>"); 
			}
			// Display the results
			edResult.setText(tmp.toString());
		}
		catch ( Throwable e ) {
			edResult.setText("Error: " + e.getMessage()); 
			result = false;
		}
		return result;
	}

	public void interop() {

		boolean slEnabled = btnLines.getEnabled();
		boolean slSelected = btnLines.getSelection();
		
		boolean reEnabled = btnUseRegex.getEnabled();
		boolean reSelected = btnUseRegex.getSelection();
				
		if (slSelected) {
			
			SWTUtils.setAllEnabled(grpRegex, false);
			SWTUtils.setAllEnabled(grpSplicedLines, true);

//			btnExtractByParagraphs.setSelection(false);
//			btnExtractByParagraphs.setEnabled(false);
			
			btnUseRegex.setSelection(false);
			btnUseRegex.setEnabled(false);
		} else {
					
			SWTUtils.setAllEnabled(grpSplicedLines, false);
			
			slEnabled = btnLines.getEnabled(); // Update state
//			if (slEnabled) btnExtractByParagraphs.setEnabled(true);
			if (slEnabled) btnUseRegex.setEnabled(true);
		}
		
		if (reSelected) {
			
			SWTUtils.setAllEnabled(grpRegex, true);
			SWTUtils.setAllEnabled(grpSplicedLines, false);
			
//			btnExtractByParagraphs.setSelection(false);
//			btnExtractByParagraphs.setEnabled(false);
			
			btnLines.setSelection(false);
			btnLines.setEnabled(false);			
			
			edExpression.setFocus();
		} else {
						
			SWTUtils.setAllEnabled(grpRegex, false);
			
			reEnabled = btnUseRegex.getEnabled(); // Update state
//			if (reEnabled) btnExtractByParagraphs.setEnabled(true);
			if (reEnabled) btnLines.setEnabled(true);
		}
	
		slEnabled = btnLines.getEnabled(); // Update state
		if (comboSplicer.getSelectionIndex() == 2 && slSelected) {
							
			textSplicer.setEnabled(true);
			textSplicer.setFocus();
		} else {
			textSplicer.setEnabled(false);
		}				
	}
	
	public boolean load(IParameters parameters) {
		
		if (parameters instanceof CompoundParameters) {
			
			CompoundParameters params = (CompoundParameters) parameters;
			
			Class<?> c = params.getParametersClass();
				
			if (c == net.sf.okapi.filters.plaintext.paragraphs.Parameters.class) {
				
				//btnExtractByParagraphs.setSelection(true);
				btnLines.setSelection(false);
				btnUseRegex.setSelection(false);
			}
			else if (c == net.sf.okapi.filters.plaintext.spliced.Parameters.class) {
				
				//btnExtractByParagraphs.setSelection(false);
				btnLines.setSelection(true);
				btnUseRegex.setSelection(false);
			}		
			else if (c == net.sf.okapi.filters.plaintext.regex.Parameters.class) {
				
				//btnExtractByParagraphs.setSelection(false);
				btnLines.setSelection(false);
				btnUseRegex.setSelection(true);
			}			
			else {
				
				//btnExtractByParagraphs.setSelection(false);
				btnLines.setSelection(false);
				btnUseRegex.setSelection(false);
			}
			
		} 
		else if (parameters instanceof net.sf.okapi.filters.plaintext.paragraphs.Parameters) {
			
			net.sf.okapi.filters.plaintext.paragraphs.Parameters params =
				(net.sf.okapi.filters.plaintext.paragraphs.Parameters) parameters;
			
			//btnExtractByParagraphs.setSelection(params.extractParagraphs);
		}
		else if (parameters instanceof net.sf.okapi.filters.plaintext.spliced.Parameters) {
			
			net.sf.okapi.filters.plaintext.spliced.Parameters params =
				(net.sf.okapi.filters.plaintext.spliced.Parameters) parameters;
			
			if (params.splicer.equals("\\")) {
				comboSplicer.select(0);
				textSplicer.setText("");
			}
			else if (params.splicer.equals("_")) {
				comboSplicer.select(1);
				textSplicer.setText("");
			} 
			else {
				comboSplicer.select(2);
				textSplicer.setText(params.splicer);
			}
			
			btnCreateInlineCodes.setSelection(params.createPlaceholders);
			
		}
		else if (parameters instanceof net.sf.okapi.filters.plaintext.regex.Parameters) {
		
			net.sf.okapi.filters.plaintext.regex.Parameters params =
				(net.sf.okapi.filters.plaintext.regex.Parameters) parameters;
			
			edExpression.setText(params.rule);
			edSource.setSelection(params.sourceGroup);
			setRegexOptions(params.regexOptions);
			edSample.setText(params.sample);
		}
	
		return true;
	}

	public boolean save(IParameters parameters) {
		
		if (parameters instanceof CompoundParameters) {
			
			CompoundParameters params = (CompoundParameters) parameters;
			
			if (btnLines.getSelection())
				params.setParametersClass(net.sf.okapi.filters.plaintext.spliced.Parameters.class);
			
			else if (btnUseRegex.getSelection())
				params.setParametersClass(net.sf.okapi.filters.plaintext.regex.Parameters.class);
			
//			else if (btnExtractByParagraphs.getSelection())
//				params.setParametersClass(net.sf.okapi.filters.plaintext.paragraphs.Parameters.class);
			
			else
				params.setParametersClass(net.sf.okapi.filters.plaintext.base.Parameters.class);
		} 		
		else if (parameters instanceof net.sf.okapi.filters.plaintext.paragraphs.Parameters) {
			
			net.sf.okapi.filters.plaintext.paragraphs.Parameters params =
				(net.sf.okapi.filters.plaintext.paragraphs.Parameters) parameters;
			
//			params.extractParagraphs = btnExtractByParagraphs.getSelection();
		}
		else if (parameters instanceof net.sf.okapi.filters.plaintext.spliced.Parameters) {
			
			net.sf.okapi.filters.plaintext.spliced.Parameters params =
				(net.sf.okapi.filters.plaintext.spliced.Parameters) parameters;
		
			switch (comboSplicer.getSelectionIndex()) {
			
				case 0:
					params.splicer = "\\";
					break;
					
				case 1:
					params.splicer = "_";
					break;
			
				case 2:
					params.splicer = textSplicer.getText(); 
					break;				
			}
			
			params.createPlaceholders = btnCreateInlineCodes.getSelection();
			
		}
		else if (parameters instanceof net.sf.okapi.filters.plaintext.regex.Parameters) {
		
			net.sf.okapi.filters.plaintext.regex.Parameters params =
				(net.sf.okapi.filters.plaintext.regex.Parameters) parameters;
			
			params.rule = edExpression.getText();
			params.sourceGroup = edSource.getSelection();
			params.regexOptions = getRegexOptions();
			params.sample = edSample.getText();
		}
		
		return true;
	}

	public boolean canClose(boolean isOK) {
		
		return true;
	}
}

