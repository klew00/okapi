/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.qualitycheck.ui;

import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.ISWTEmbeddableParametersEditor;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.TextAndBrowsePanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.steps.qualitycheck.Parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

@EditorFor(Parameters.class)
public class ParametersEditor implements IParametersEditor, ISWTEmbeddableParametersEditor {
	
	private Shell shell;
	private boolean result = false;
	private OKCancelPanel pnlActions;
	private Parameters params;
	private IHelp help;
	private Button chkAutoOpen;
	private Button chkLeadingWS;
	private Button chkTrailingWS;
	private Button chkEmptyTarget;
	private Button chkTargetSameAsSource;
	private Button chkTargetSameAsSourceWithCodes;
	private Composite mainComposite;
	private TextAndBrowsePanel pnlOutputPath;
	private Button chkCodeDifference;
	private Button chkPatterns;
	private Table table;
	private Button btAdd;
	private Button btEdit;
	private Button btRemove;
	private Button btMoveUp;
	private Button btMoveDown;
	private Button btImport;
	private Button btExport;
	
	public boolean edit (IParameters params,
		boolean readOnly,
		IContext context)
	{
		boolean bRes = false;
		try {
			shell = null;
			help = (IHelp)context.getObject("help");
			this.params = (Parameters)params;
			shell = new Shell((Shell)context.getObject("shell"), SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)context.getObject("shell"), readOnly);
			return showDialog();
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
			bRes = false;
		}
		finally {
			// Dispose of the shell, but not of the display
			if ( shell != null ) shell.dispose();
		}
		return bRes;
	}
	
	public IParameters createParameters () {
		return new Parameters();
	}
	
	@Override
	public Composite getComposite () {
		return mainComposite;
	}

	@Override
	public void initializeEmbeddableEditor (Composite parent,
		IParameters paramsObject,
		IContext context)
	{
		params = (Parameters)paramsObject; 
		shell = (Shell)context.getObject("shell");
		
		createComposite(parent);
		
		setData();
	}

	@Override
	public String validateAndSaveParameters () {
		if ( !saveData() ) return null;
		return params.toString();
	}
	
	private void create (Shell parent,
		boolean readOnly)
	{
		shell.setText("Quality Check");
		if ( parent != null ) UIUtil.inheritIcon(shell, parent);
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		createComposite(shell);

		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) {
					if ( help != null ) help.showTopic(this, "qualitycheckstep");
					return;
				}
				if ( e.widget.getData().equals("o") ) saveData();
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		pnlActions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pnlActions.btOK.setEnabled(!readOnly);
		if ( !readOnly ) {
			shell.setDefaultButton(pnlActions.btOK);
		}

		setData();
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Dialogs.centerWindow(shell, parent);
	}
	
	private void createComposite (Composite parent) {
		mainComposite = new Composite(parent, SWT.BORDER);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabFolder tfTmp = new TabFolder(mainComposite, SWT.NONE);
		tfTmp.setLayout(new GridLayout());
		tfTmp.setLayoutData(new GridData(GridData.FILL_BOTH));

		//--- General tab
		
		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout());
		
		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText("Flag the following potential issues:");
		
		chkTargetSameAsSource = new Button(cmpTmp, SWT.CHECK);
		chkTargetSameAsSource.setText("Target is the same as the source (when it has text)");
		chkTargetSameAsSource.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateTargetSameAsSourceWithCodes();
			};
		});
		
		chkTargetSameAsSourceWithCodes = new Button(cmpTmp, SWT.CHECK);
		chkTargetSameAsSourceWithCodes.setText("Include the codes in the comparison");
		GridData gdTmp = new GridData();
		gdTmp.horizontalIndent = 16;
		chkTargetSameAsSourceWithCodes.setLayoutData(gdTmp);
		
		chkCodeDifference = new Button(cmpTmp, SWT.CHECK);
		chkCodeDifference.setText("Code differences between source and target");

		chkEmptyTarget = new Button(cmpTmp, SWT.CHECK);
		chkEmptyTarget.setText("Empty translation");

		chkLeadingWS = new Button(cmpTmp, SWT.CHECK);
		chkLeadingWS.setText("Leading white spaces");
		
		chkTrailingWS = new Button(cmpTmp, SWT.CHECK);
		chkTrailingWS.setText("Trailing white spaces");

		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("General");
		tiTmp.setControl(cmpTmp);


		//--- Patterns tab
		
		cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout());
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));

		chkPatterns = new Button(cmpTmp, SWT.CHECK);
		chkPatterns.setText("Verify that the following source patterns are translated as expected:");
		chkPatterns.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updatePatterns();
			};
		});
		
		table = new Table(cmpTmp, SWT.CHECK | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		// Update buttons when moving cursor
		table.addListener(SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				if ( event.detail != SWT.CHECK ) {
					updateMoveButtons();
				}
			}
		});		
		// Double-click is like edit
		table.addListener(SWT.MouseDoubleClick, new Listener () {
			public void handleEvent (Event event) {
				if ( table.getSelectionIndex() != -1 ) {
					editPattern(false);
				}				
			}
		});		
		// Resizing the columns
		table.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				int tableWidth = table.getClientArea().width;
				int remaining = tableWidth - table.getColumn(0).getWidth();
				table.getColumn(1).setWidth(remaining/2);
				table.getColumn(2).setWidth(remaining/2);
			}
		});

		String[] titles = {"Use", "Source Pattern", "Expected Target Pattern"};
		for ( int i=0; i<titles.length; i++ ) {
			TableColumn column = new TableColumn(table, SWT.LEFT);
			column.setText(titles [i]);
			column.pack();
		}
		
		// Buttons
		Composite cmpTmp2 = new Composite(cmpTmp, SWT.NONE);
		GridLayout layTmp = new GridLayout(7, true);
		layTmp.marginHeight = layTmp.marginWidth = 0;
		cmpTmp2.setLayout(layTmp);

//TODO: Fix resizing of buttons!!!		
		btAdd = UIUtil.createGridButton(cmpTmp2, SWT.PUSH, "Add...", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editPattern(true);
			}
		});

		btEdit = UIUtil.createGridButton(cmpTmp2, SWT.PUSH, "Edit...", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editPattern(false);
			}
		});
		
		btRemove = UIUtil.createGridButton(cmpTmp2, SWT.PUSH, "Remove", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removePattern();
			}
		});

		btMoveUp = UIUtil.createGridButton(cmpTmp2, SWT.PUSH, "Move Up", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btMoveUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			}
		});

		btMoveDown = UIUtil.createGridButton(cmpTmp2, SWT.PUSH, "Move Down", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btMoveDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			}
		});

		btImport = UIUtil.createGridButton(cmpTmp2, SWT.PUSH, "Import...", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btImport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			}
		});

		btExport = UIUtil.createGridButton(cmpTmp2, SWT.PUSH, "Export...", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btExport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			}
		});

		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Patterns");
		tiTmp.setControl(cmpTmp);

		
		//--- Output tab
		
		cmpTmp = new Composite(tfTmp, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);

		label = new Label(cmpTmp, SWT.NONE);
		label.setText("Path of the report file:");
		pnlOutputPath = new TextAndBrowsePanel(cmpTmp, SWT.NONE, false);
		pnlOutputPath.setSaveAs(true);
		pnlOutputPath.setTitle("Quality Check Report");
		pnlOutputPath.setBrowseFilters("HTML Files (*.html;*.htm)\tAll Files (*.*)", "*.html;**.htm\t*.*");
		gdTmp = new GridData();
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		pnlOutputPath.setLayoutData(gdTmp);

		chkAutoOpen = new Button(cmpTmp, SWT.CHECK);
		chkAutoOpen.setText("Open the report after completion");
		
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Output");
		tiTmp.setControl(cmpTmp);
	}
	
	private boolean showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}
	
	private void editPattern (boolean add) {
		TableItem item = new TableItem(table, SWT.NONE);
		String[] data = {"", "search", "target"};
		item.setText(data);
		item.setChecked(true);
		table.setSelection(table.getItemCount()-1);
		
		updatePatternsButtons();
	}

	private void removePattern () {
		int index = table.getSelectionIndex();
		if ( index < 0 ) return;
		table.remove(index);
		int count = table.getItemCount();
		if ( index > count-1 ) table.setSelection(table.getItemCount()-1);
		else table.setSelection(index);
		updatePatternsButtons();
	}
	
	private void updateTargetSameAsSourceWithCodes () {
		chkTargetSameAsSourceWithCodes.setEnabled(chkTargetSameAsSource.getSelection());
	}

	private void updatePatterns () {
		boolean enabled = chkPatterns.getSelection();
		table.setEnabled(enabled);
		btAdd.setEnabled(enabled);
		if ( enabled ) {
			updatePatternsButtons();
		}
		else {
			btEdit.setEnabled(false);
			btRemove.setEnabled(false);
			btMoveUp.setEnabled(false);
			btMoveDown.setEnabled(false);
			btImport.setEnabled(false);
			btExport.setEnabled(false);
		}
	}
	
	private void updatePatternsButtons () {
		int index = table.getSelectionIndex();
		int count = table.getItemCount();
		btEdit.setEnabled(index!=-1);
		btRemove.setEnabled(index!=-1);
		updateMoveButtons();
		btImport.setEnabled(count>0);
		btExport.setEnabled(count>0);
	}
	
	private void updateMoveButtons () {
		int index = table.getSelectionIndex();
		int count = table.getItemCount();
		btMoveUp.setEnabled(index>0);
		btMoveDown.setEnabled(index<count-1);
	}

	private void setData () {
		pnlOutputPath.setText(params.getOutputPath());
		chkAutoOpen.setSelection(params.getAutoOpen());
		chkCodeDifference.setSelection(params.getCodeDifference());
		chkLeadingWS.setSelection(params.getLeadingWS());
		chkTrailingWS.setSelection(params.getTrailingWS());
		chkEmptyTarget.setSelection(params.getEmptyTarget());
		chkTargetSameAsSource.setSelection(params.getTargetSameAsSource());
		chkTargetSameAsSourceWithCodes.setSelection(params.getTargetSameAsSourceWithCodes());
		chkPatterns.setSelection(params.getPatterns());
		updateTargetSameAsSourceWithCodes();
		updatePatterns();
	}

	private boolean saveData () {
		if ( pnlOutputPath.getText().length() == 0 ) {
			Dialogs.showError(shell, "Please, enter a path for the report.", null);
			pnlOutputPath.setFocus();
			return false;
		}
		params.setOutputPath(pnlOutputPath.getText());
		params.setCodeDifference(chkCodeDifference.getSelection());
		params.setAutoOpen(chkAutoOpen.getSelection());
		params.setLeadingWS(chkLeadingWS.getSelection());
		params.setTrailingWS(chkTrailingWS.getSelection());
		params.setEmptyTarget(chkEmptyTarget.getSelection());
		params.setTargetSameAsSource(chkTargetSameAsSource.getSelection());
		if ( chkTargetSameAsSourceWithCodes.isEnabled() ) {
			params.setTargetSameAsSourceWithCodes(chkTargetSameAsSourceWithCodes.getSelection());
		}
		params.setPatterns(chkPatterns.getSelection());
		result = true;
		return result;
	}
	
}
