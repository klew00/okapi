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

package net.sf.okapi.lib.ui.verification;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
import net.sf.okapi.lib.verification.Parameters;
import net.sf.okapi.lib.verification.PatternItem;
import net.sf.okapi.lib.verification.QualityCheckSession;

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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

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
	private Button chkDoubledWord;
	private Table table;
	private Button btAdd;
	private Button btEdit;
	private Button btRemove;
	private Button btMoveUp;
	private Button btMoveDown;
	private Button btImport;
	private Button btExport;
	private Text edSource;
	private Text edTarget;
	private Shell dialog;
	private TableItem editItem;
	private boolean addMode;
	private Button chkCheckWithLT;
	private Text edServerURL;
	private Button chkTranslateLTMsg;
	private Text edLTTranslationSource;
	private Text edLTTranslationTarget;
	// Flag to indicate the editor is use for step parameters
	// We default to true because the step cannot set this option
	private boolean stepMode = false; // Disabled for now
	// step-mode fields:
	private TextAndBrowsePanel pnlSessionPath;
	private Button chkSaveSession;
	
	@Override
	public boolean edit (IParameters params,
		boolean readOnly,
		IContext context)
	{
		boolean bRes = false;
		try {
			shell = null;
			help = (IHelp)context.getObject("help");
			stepMode = context.getBoolean("stepMode");
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
		shell.setText("Quality Check Options");
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
					if ( help != null ) help.showTopic(this, "parameterseditor");
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

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Dialogs.centerWindow(shell, parent);
		setData();
	}
	
	private void createComposite (Composite parent) {
		mainComposite = new Composite(parent, SWT.BORDER);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabFolder tfTmp = new TabFolder(mainComposite, SWT.NONE);
		tfTmp.setLayout(new GridLayout());
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		// Auto-size is too high, we need to fix it manually
		gdTmp.heightHint = 430;
		tfTmp.setLayoutData(gdTmp);

		//--- General tab
		
		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayout(new GridLayout());

		Group grpTU = new Group(cmpTmp, SWT.NONE);
		grpTU.setText("Text unit verifications");
		grpTU.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		grpTU.setLayout(new GridLayout());
		
		Button chkTmp = new Button(grpTU, SWT.CHECK);
		chkTmp.setText("[Always On] Warn if an entry does not have a translation");
		chkTmp.setSelection(true);
		chkTmp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				((Button)e.getSource()).setSelection(true);
			};
		});
		
		chkLeadingWS = new Button(grpTU, SWT.CHECK);
		chkLeadingWS.setText("Warn if a target entry has a difference in leading white spaces");
		
		chkTrailingWS = new Button(grpTU, SWT.CHECK);
		chkTrailingWS.setText("Warn if a target entry has a difference in trailing white spaces");

		Group grpSeg = new Group(cmpTmp, SWT.NONE);
		grpSeg.setText("Segment verifications");
		grpSeg.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		grpSeg.setLayout(new GridLayout());
		
		chkTmp = new Button(grpSeg, SWT.CHECK);
		chkTmp.setText("[Always On] Warn if a source segment does not have a corresponding target");
		chkTmp.setSelection(true);
		chkTmp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				((Button)e.getSource()).setSelection(true);
			};
		});
		
		chkEmptyTarget = new Button(grpSeg, SWT.CHECK);
		chkEmptyTarget.setText("Warn if a target segment is empty (and its source is not)");

		chkTargetSameAsSource = new Button(grpSeg, SWT.CHECK);
		chkTargetSameAsSource.setText("Warn if a target segment is the same as its source (for segments with text)");
		chkTargetSameAsSource.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateTargetSameAsSourceWithCodes();
			};
		});
		chkTargetSameAsSource.setLayoutData(new GridData());

		chkTargetSameAsSourceWithCodes = new Button(grpSeg, SWT.CHECK);
		chkTargetSameAsSourceWithCodes.setText("Include the codes in the comparison");
		gdTmp = new GridData();
		gdTmp.horizontalIndent = 16;
		chkTargetSameAsSourceWithCodes.setLayoutData(gdTmp);
		
		chkCodeDifference = new Button(grpSeg, SWT.CHECK);
		chkCodeDifference.setText("Warn if there is a code difference between source and target segments");

		chkDoubledWord = new Button(grpSeg, SWT.CHECK);
		chkDoubledWord.setText("Warn on doubled words (e.g. \"This [is is an example\")");
		
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
		gdTmp = new GridData(GridData.FILL_BOTH);
//		gdTmp.heightHint = 5;
		table.setLayoutData(gdTmp);
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
				moveItem(-1);
			}
		});

		btMoveDown = UIUtil.createGridButton(cmpTmp2, SWT.PUSH, "Move Down", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btMoveDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveItem(+1);
			}
		});

		btImport = UIUtil.createGridButton(cmpTmp2, SWT.PUSH, "Import...", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btImport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				importPatterns();
			}
		});

		btExport = UIUtil.createGridButton(cmpTmp2, SWT.PUSH, "Export...", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btExport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				exportPatterns();
			}
		});

		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Patterns");
		tiTmp.setControl(cmpTmp);

		
		//--- Language Tool tab
		
		cmpTmp = new Composite(tfTmp, SWT.NONE);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cmpTmp.setLayout(new GridLayout());

		chkCheckWithLT = new Button(cmpTmp, SWT.CHECK);
		chkCheckWithLT.setText("Perform the verifications provided by the LanguageTool server");
		chkCheckWithLT.setLayoutData(new GridData());
		chkCheckWithLT.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateLTOptions();
			};
		});
		
		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText("Server URL (e.g. http://localhost:8081/):");
		edServerURL = new Text(cmpTmp, SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		edServerURL.setLayoutData(gdTmp);
		
		chkTranslateLTMsg = new Button(cmpTmp, SWT.CHECK);
		chkTranslateLTMsg.setText("Auto-translate the messages from the LanguageTool checker");
		gdTmp = new GridData();
		gdTmp.verticalIndent = 16;
		chkTranslateLTMsg.setLayoutData(gdTmp);
		chkTranslateLTMsg.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateTranslateLTMsg();
			};
		});
		
		Composite grpTmp = new Composite(cmpTmp, SWT.NONE);
		grpTmp.setLayout(new GridLayout(2, false));
		
		label = new Label(grpTmp, SWT.NONE);
		label.setText("From:");
		gdTmp = new GridData();
		label.setLayoutData(gdTmp);
		
		edLTTranslationSource = new Text(grpTmp, SWT.BORDER);
		gdTmp = new GridData();
		gdTmp.widthHint = 80;
		edLTTranslationSource.setLayoutData(gdTmp);
		
		label = new Label(grpTmp, SWT.NONE);
		label.setText("Into:");
		gdTmp = new GridData();
		label.setLayoutData(gdTmp);

		edLTTranslationTarget = new Text(grpTmp, SWT.BORDER);
		gdTmp = new GridData();
		gdTmp.widthHint = 80;
		edLTTranslationTarget.setLayoutData(gdTmp);

		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("LanguageTool");
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
		pnlOutputPath.setBrowseFilters("HTML Files (*.html;*.htm)\tAll Files (*.*)", "*.html;*.htm\t*.*");
		gdTmp = new GridData();
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		pnlOutputPath.setLayoutData(gdTmp);

		chkAutoOpen = new Button(cmpTmp, SWT.CHECK);
		chkAutoOpen.setText("Open the report after completion");

		if ( stepMode ) {
			chkSaveSession = new Button(cmpTmp, SWT.CHECK);
			chkSaveSession.setText("Save the session using the following path:");
			chkSaveSession.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					pnlSessionPath.setEnabled(chkSaveSession.getSelection());
				}
			});

			pnlSessionPath = new TextAndBrowsePanel(cmpTmp, SWT.NONE, false);
			pnlSessionPath.setSaveAs(true);
			pnlSessionPath.setTitle("Quality Check Session");
			pnlSessionPath.setBrowseFilters(
				String.format("Quality Check Sessions (*%s)\tAll Files (*.*)", QualityCheckSession.FILE_EXTENSION),
				String.format("*%s\t*.*", QualityCheckSession.FILE_EXTENSION));
			gdTmp = new GridData();
			gdTmp = new GridData(GridData.FILL_HORIZONTAL);
			pnlSessionPath.setLayoutData(gdTmp);
		}
		
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Output");
		tiTmp.setControl(cmpTmp);
		
		mainComposite.pack();
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
		addMode = add;
		dialog = new Shell(shell, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		dialog.setText(add ? "Add Pattern Entry" : "Edit Pattern Entry");
		dialog.setMinimumSize(400, 200);
		dialog.setSize(dialog.getMinimumSize());
		dialog.setLayout(new GridLayout());
		dialog.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Source pattern
		Label label = new Label(dialog, SWT.NONE | SWT.WRAP);
		label.setText("Pattern for the source:");
		
		edSource = new Text(dialog, SWT.BORDER);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		edSource.setLayoutData(gdTmp);
		
		// Target correspondence
		label = new Label(dialog, SWT.NONE);
		label.setText(String.format("Expected corresponding target pattern (use '%s' if same as source):", PatternItem.SAME));

		edTarget = new Text(dialog, SWT.BORDER | SWT.WRAP);
		gdTmp = new GridData(GridData.FILL_BOTH);
		edTarget.setLayoutData(gdTmp);

		// Set the text in the edit fields
		if ( add ) {
			edTarget.setText(PatternItem.SAME);
		}
		else {
			int index = table.getSelectionIndex();
			if ( index < 0 ) return;
			editItem = table.getItem(index);
			edSource.setText(editItem.getText(1));
			edTarget.setText(editItem.getText(2));
		}

		//  Dialog buttons
		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if ( event.widget.getData().equals("o") ) {
					// Validate entries
					if ( edSource.getText().trim().length() < 1 ) {
						Dialogs.showError(shell, "You must enter a pattern for the source.", null);
						edSource.setFocus();
						return;
					}
					if ( edTarget.getText().trim().length() < 1 ) {
						Dialogs.showError(shell, "You must enter a corresponding part for the target.", null);
						edTarget.setFocus();
						return;
					}
					// Try patterns
					try {
						Pattern.compile(edSource.getText());
						if ( !edTarget.getText().equals(PatternItem.SAME) ) {
							Pattern.compile(edTarget.getText());
						}
					}
					catch ( Exception e ) {
						Dialogs.showError(shell, "Pattern error:\n" + e.getLocalizedMessage(), null);
						return;
					}

					// Update the table
					if ( addMode ) { // Add a new item if needed
						editItem = new TableItem(table, SWT.NONE);
						editItem.setText(2, PatternItem.SAME);
						editItem.setChecked(true);
						table.setSelection(table.getItemCount()-1);
					}
					editItem.setText(1, edSource.getText());
					editItem.setText(2, edTarget.getText());
					updatePatternsButtons();
				}
				// Close
				dialog.close();
			};
		};

		OKCancelPanel pnlActionsDialog = new OKCancelPanel(dialog, SWT.NONE, OKCancelActions, false);
		pnlActionsDialog.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dialog.setDefaultButton(pnlActionsDialog.btOK);
		
		dialog.pack();
		Dialogs.centerWindow(dialog, shell);
		dialog.open ();
		while ( !dialog.isDisposed() ) {
			if ( !dialog.getDisplay().readAndDispatch() )
				dialog.getDisplay().sleep();
		}
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
	
	private void moveItem (int offset) {
		int index = table.getSelectionIndex();
		int count = table.getItemCount();
		if ( offset < 0 ) {
			if ( index < 1 ) return;
		}
		else {
			if ( index >= count-1 ) return;
		}
		
		// Get the selected entry
        TableItem ti = table.getItem(index);
        boolean isChecked = ti.getChecked();
        String[] data = {ti.getText(0), ti.getText(1), ti.getText(2)};
        ti.dispose();
        
        // Add the new item at the new place
		ti = new TableItem (table, SWT.NONE, index+offset);
		ti.setChecked(isChecked);
		ti.setText(data);
		
		// Update cursor and buttons
		table.select(index+offset);
		updateMoveButtons();
	}

	private void importPatterns () {
		try {
			String[] paths = Dialogs.browseFilenames(shell, "Import Patterns", false, null,
				"Patterns Files (*.txt)\tAll Files (*.*)", "*.txt\t*.*");
			if ( paths == null ) return;
			setPatternsData(PatternItem.loadFile(paths[0]));
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}

	private void exportPatterns () {
		try {
			String path = Dialogs.browseFilenamesForSave(shell, "Export Patterns", null,
				"Patterns Files (*.txt)\tAll Files (*.*)", "*.txt\t*.*");
			if ( path == null ) return;
			PatternItem.saveFile(path, savePatternsData());
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}

	private void updateLTOptions () {
		boolean enabled = chkCheckWithLT.getSelection();
		edServerURL.setEnabled(enabled);
		chkTranslateLTMsg.setEnabled(enabled);
		if ( enabled ) {
			updateTranslateLTMsg();
		}
		else {
			edLTTranslationSource.setEnabled(false);
			edLTTranslationTarget.setEnabled(false);
		}
	}
	
	private void updateTranslateLTMsg () {
		edLTTranslationSource.setEnabled(chkTranslateLTMsg.getSelection());
		edLTTranslationTarget.setEnabled(chkTranslateLTMsg.getSelection());
	}
	
	private void updateTargetSameAsSourceWithCodes () {
		chkTargetSameAsSourceWithCodes.setEnabled(chkTargetSameAsSource.getSelection());
	}

	private void updatePatterns () {
		boolean enabled = chkPatterns.getSelection();
		table.setEnabled(enabled);
		btAdd.setEnabled(enabled);
		btImport.setEnabled(enabled);
		if ( enabled ) {
			updatePatternsButtons();
		}
		else {
			btEdit.setEnabled(false);
			btRemove.setEnabled(false);
			btMoveUp.setEnabled(false);
			btMoveDown.setEnabled(false);
			btExport.setEnabled(false);
		}
	}
	
	private void updatePatternsButtons () {
		int index = table.getSelectionIndex();
		int count = table.getItemCount();
		btEdit.setEnabled(index!=-1);
		btRemove.setEnabled(index!=-1);
		updateMoveButtons();
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
		chkCheckWithLT.setSelection(params.getCheckWithLT());
		edServerURL.setText(params.getServerURL());
		chkTranslateLTMsg.setSelection(params.getTranslateLTMsg());
		edLTTranslationSource.setText(params.getLtTranslationSource());
		edLTTranslationTarget.setText(params.getLtTranslationTarget());
		chkPatterns.setSelection(params.getCheckPatterns());
		chkDoubledWord.setSelection(params.getDoubledWord());
		setPatternsData(params.getPatterns());
		updateTargetSameAsSourceWithCodes();
		updatePatterns();
		updateLTOptions();
		// Step-mode fields
		if ( stepMode ) {
			chkSaveSession.setSelection(params.getSaveSession());
			pnlSessionPath.setText(params.getSessionPath());
			pnlSessionPath.setEnabled(chkSaveSession.getSelection());
		}
	}
	
	private void setPatternsData (List<PatternItem> list) {
		table.removeAll();
		for ( PatternItem item : list ) {
			TableItem row = new TableItem(table, SWT.NONE);
			row.setChecked(item.enabled);
			row.setText(1, item.source);
			row.setText(2, item.target);
		}
		if ( table.getItemCount() > 0 ) {
			table.setSelection(0);
		}
	}

	private boolean saveData () {
		if ( pnlOutputPath.getText().trim().length() == 0 ) {
			Dialogs.showError(shell, "Please, enter a path for the report.", null);
			pnlOutputPath.setFocus();
			return false;
		}
		if ( chkCheckWithLT.getSelection() ) {
			if ( edServerURL.getText().trim().length() == 0 ) {
				Dialogs.showError(shell, "Please, enter a server URL.", null);
				edServerURL.setFocus();
				return false;
			}
			if ( chkTranslateLTMsg.getSelection() ) {
				if ( edLTTranslationSource.getText().trim().length() == 0 ) {
					Dialogs.showError(shell, "Please, enter the language code of the messages returned by LanguageTool (e.g. fr).", null);
					edLTTranslationSource.setFocus();
					return false;
					
				}
				if ( edLTTranslationTarget.getText().trim().length() == 0 ) {
					Dialogs.showError(shell, "Please, enter the language to translate the LanguageTool messages into (e.g. en).", null);
					edLTTranslationTarget.setFocus();
					return false;
				}
			}
		}
		if ( stepMode ) {
			if ( chkSaveSession.getSelection() ) {
				if ( pnlSessionPath.getText().trim().length() == 0 ) {
					Dialogs.showError(shell, "Please, enter a path for the session.", null);
					pnlSessionPath.setFocus();
					return false;
				}
			}
		}

		params.setOutputPath(pnlOutputPath.getText());
		params.setCodeDifference(chkCodeDifference.getSelection());
		params.setAutoOpen(chkAutoOpen.getSelection());
		params.setLeadingWS(chkLeadingWS.getSelection());
		params.setTrailingWS(chkTrailingWS.getSelection());
		params.setEmptyTarget(chkEmptyTarget.getSelection());
		params.setTargetSameAsSource(chkTargetSameAsSource.getSelection());
		params.setDoubledWord(chkDoubledWord.getSelection());
		if ( chkTargetSameAsSourceWithCodes.isEnabled() ) {
			params.setTargetSameAsSourceWithCodes(chkTargetSameAsSourceWithCodes.getSelection());
		}
		params.setCheckWithLT(chkCheckWithLT.getSelection());
		if ( chkCheckWithLT.getSelection() ) {
			params.setServerURL(edServerURL.getText());
			params.setTranslateLTMsg(chkTranslateLTMsg.getSelection());
			if ( chkTranslateLTMsg.getSelection() ) {
				params.setLtTranslationSource(edLTTranslationSource.getText());
				params.setLtTranslationTarget(edLTTranslationTarget.getText());
			}
		}
		if ( stepMode ) {
			params.setSaveSession(chkSaveSession.getSelection());
			if ( chkSaveSession.getSelection() ) {
				params.setSessionPath(pnlSessionPath.getText());
			}
		}
		params.setCheckPatterns(chkPatterns.getSelection());
		params.setPatterns(savePatternsData());
		result = true;
		return result;
	}
	
	private List<PatternItem> savePatternsData () {
		List<PatternItem> list = new ArrayList<PatternItem>();
		for ( int i=0; i<table.getItemCount(); i++ ) {
			list.add(new PatternItem(table.getItem(i).getText(1), table.getItem(i).getText(2),
				table.getItem(i).getChecked()));
		}
		return list;
	}
	
}
