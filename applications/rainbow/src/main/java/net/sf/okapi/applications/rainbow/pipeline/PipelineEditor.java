/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.applications.rainbow.pipeline;

import java.util.ArrayList;
import java.util.Map;

import net.sf.okapi.common.BaseContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiEditorCreationException;
import net.sf.okapi.common.ui.DefaultEmbeddableEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.IEmbeddableParametersEditor;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.common.ui.genericeditor.GenericEmbeddableEditor;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PipelineEditor {

	public static final int RESULT_CANCEL = 0;
	public static final int RESULT_CLOSE = 1;
	public static final int RESULT_EXECUTE = 2;
	
	private Shell shell;
	private int result;
	private IHelp help;
	private PipelineWrapper wrapper;
	private Map<String, StepInfo> availableSteps;
	private ArrayList<StepInfo> workSteps;
	private ArrayList<IEmbeddableParametersEditor> panels;
	private Text edPath;
	private List lbSteps;
	private Button btLoad;
	private Button btSave;
	private Button btSaveAs;
	private Button btNew;
	private Button btAddStep;
	private Button btRemoveStep;
	private Button btMoveStepUp;
	private Button btMoveStepDown;
	private Text edDescription;
	private BaseContext context;
	private String predefined;
	private Composite optionsHolder;
	private Composite noParametersPanel;
	private Composite noStepsPanel;
	private Point minSize;
	
	public int edit (Shell parent,
		Map<String, StepInfo> availableSteps,
		PipelineWrapper wrapper,
		String predefined, // null if not predefined, title otherwise
		IHelp helpParam,
		String projectDir)
	{
		context = new BaseContext();
		context.setObject("shell", parent);
		this.predefined = predefined;
		
		int result = RESULT_CANCEL;
		try {
			this.availableSteps = availableSteps;
			this.wrapper = wrapper;
			this.help = helpParam;
			create(parent);
			setDataFromWrapper();
			populate(0);
			
			// Set focus on steps if possible
			if ( lbSteps.getItemCount() > 0 ) {
				lbSteps.setFocus();
			}
			// Select the first step with parameters if possible
			for ( int i=0; i<workSteps.size(); i++ ) {
				if ( workSteps.get(i).paramsData != null ) {
					lbSteps.select(i);
					updateStepDisplay();
					break;
				}
			}
			
			result = showDialog();
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getMessage(), null);
			result = RESULT_CANCEL;
		}
		finally {
			// Dispose of the shell, but not of the display
			if ( shell != null ) shell.dispose();
		}
		return result;
	}

	private void setDataFromWrapper () {
		workSteps = new ArrayList<StepInfo>();
		panels = new ArrayList<IEmbeddableParametersEditor>();
		for ( StepInfo step : wrapper.getSteps() ) {
			workSteps.add(step.clone());
			panels.add(createPanel(step));
		}
	}
	
	private int showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}

	private void create (Shell parent) {
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		
		if ( predefined == null ) {
			shell.setText("Edit / Execute Pipeline");
		}
		else { // Pre-defined pipeline
			shell.setText("Pre-defined Pipeline");
		}
		
		UIUtil.inheritIcon(shell, parent);
		shell.setLayout(new GridLayout());
		
		edPath = new Text(shell, SWT.BORDER);
		edPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edPath.setEditable(false);

		int width = 100;
		
		Composite cmpTmp = new Composite(shell, SWT.NONE);
		GridLayout layTmp = new GridLayout(2, false);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		cmpTmp.setLayout(layTmp);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		cmpTmp.setLayoutData(gdTmp);

		lbSteps = new List(cmpTmp, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gdTmp = new GridData(GridData.FILL_VERTICAL);
		gdTmp.widthHint = 175;
		gdTmp.heightHint = 300;
		lbSteps.setLayoutData(gdTmp);
		lbSteps.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateStepDisplay();
			}
		});
		
		// Second part of the sash form
		optionsHolder = new Composite(cmpTmp, SWT.NONE);
		optionsHolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		StackLayout stkTmp = new StackLayout();
		stkTmp.marginHeight = 0;
		stkTmp.marginWidth = 0;
		optionsHolder.setLayout(stkTmp);

		// Panel to use for no-parameters steps
		noParametersPanel = new Composite(optionsHolder, SWT.BORDER);
		noParametersPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		noParametersPanel.setLayout(new GridLayout());
		Label stTmp = new Label(noParametersPanel, SWT.NONE);
		stTmp.setText("This step has no parameters");

		// Panel to use for when there are no steps
		noStepsPanel = new Composite(optionsHolder, SWT.BORDER);
		noStepsPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		noStepsPanel.setLayout(new GridLayout());
		stTmp = new Label(noStepsPanel, SWT.NONE);
		stTmp.setText("Click on \"Add Step\" to start");
		
		// Bottom part
		if ( predefined == null ) {
			cmpTmp = new Composite(shell, SWT.NONE);
			cmpTmp.setLayout(new GridLayout(4, false));
			gdTmp = new GridData(GridData.FILL_HORIZONTAL);
			cmpTmp.setLayoutData(gdTmp);
		
			// Buttons
			btAddStep = new Button(cmpTmp, SWT.PUSH);
			btAddStep.setText("Add Step...");
			gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			gdTmp.widthHint = width;
			btAddStep.setLayoutData(gdTmp);
			btAddStep.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					addStep();
				}
			});
			
			btRemoveStep = new Button(cmpTmp, SWT.PUSH);
			btRemoveStep.setText("Remove Step");
			gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			gdTmp.widthHint = width;
			btRemoveStep.setLayoutData(gdTmp);
			btRemoveStep.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					removeStep();
				}
			});
			
			btMoveStepUp = new Button(cmpTmp, SWT.PUSH);
			btMoveStepUp.setText("Move Up");
			gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			gdTmp.widthHint = width;
			btMoveStepUp.setLayoutData(gdTmp);
			btMoveStepUp.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					moveStepUp();
				}
			});
			
			btMoveStepDown = new Button(cmpTmp, SWT.PUSH);
			btMoveStepDown.setText("Move Down");
			gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			gdTmp.widthHint = width;
			btMoveStepDown.setLayoutData(gdTmp);
			btMoveStepDown.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					moveStepDown();
				}
			});
			
			// Pipeline buttons
			btLoad = new Button(cmpTmp, SWT.PUSH);
			btLoad.setText("Load...");
			gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			gdTmp.widthHint = width;
			btLoad.setLayoutData(gdTmp);
			btLoad.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					load(null);
				}
			});
			
			btNew = new Button(cmpTmp, SWT.PUSH);
			btNew.setText("New");
			gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			gdTmp.widthHint = width;
			btNew.setLayoutData(gdTmp);
			btNew.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					newPipeline();
				}
			});
			
			btSave = new Button(cmpTmp, SWT.PUSH);
			btSave.setText("Save");
			gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			gdTmp.widthHint = width;
			btSave.setLayoutData(gdTmp);
			btSave.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					// Like save as if it is a predefined pipeline
					save((predefined==null) ? edPath.getText() : null);
				}
			});
			
			btSaveAs = new Button(cmpTmp, SWT.PUSH);
			btSaveAs.setText("Save As...");
			gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			gdTmp.widthHint = width;
			btSaveAs.setLayoutData(gdTmp);
			btSaveAs.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					save(null);
				}
			});
		
		} // End of "if predefined"
		
		
		edDescription = new Text(shell, SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.heightHint = 32;
		//gdTmp.horizontalSpan = 2;
		edDescription.setLayoutData(gdTmp);
		edDescription.setEditable(false);

		// Dialog-level buttons
		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = RESULT_CANCEL;
				if ( e.widget.getData().equals("h") ) { //$NON-NLS-1$
					//if ( help != null ) help.showTopic(this, "index", "inputDocProp.html"); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}
				if ( e.widget.getData().equals("o") ) { //$NON-NLS-1$
					if ( !saveData() ) return;
					result = RESULT_CLOSE;
				}
				if ( e.widget.getData().equals("x") ) { //$NON-NLS-1$
					if ( !saveData() ) return;
					result = RESULT_EXECUTE;
				}
				shell.close();
			};
		};
		
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE,
			OKCancelActions, false, "Close", "Execute"); //TODO: Add help

		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		minSize = shell.getSize();
		shell.setMinimumSize(minSize);
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 750 ) startSize.x = 750;
		if ( startSize.y < 350 ) startSize.y = 350;
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
	}

	private void updateStepDisplay () {
		int n = lbSteps.getSelectionIndex();
		Control ctrl = ((StackLayout)optionsHolder.getLayout()).topControl;
		if ( n < 0 ) {
			edDescription.setText("");
			if ( predefined == null ) {
				btAddStep.setEnabled(true);
				btRemoveStep.setEnabled(false);
				btMoveStepUp.setEnabled(false);
				btMoveStepDown.setEnabled(false);
			}
			if (( ctrl == null ) || !ctrl.equals(noStepsPanel) ) {
				((StackLayout)optionsHolder.getLayout()).topControl = noStepsPanel;
				optionsHolder.layout();
			}
			optionsHolder.layout();
			optionsHolder.getParent().layout();
			return; 
		}
		StepInfo step = workSteps.get(n);
		edDescription.setText(step.description);
		if ( predefined == null ) {
			btAddStep.setEnabled(true);
			btRemoveStep.setEnabled(true);
			btMoveStepUp.setEnabled(n>0);
			btMoveStepDown.setEnabled(n<workSteps.size()-1);
		}

		IEmbeddableParametersEditor panel = panels.get(n);
		if ( panel.getComposite() == null ) {
			if (( ctrl == null ) || !ctrl.equals(noParametersPanel) ) {
				((StackLayout)optionsHolder.getLayout()).topControl = noParametersPanel;
				optionsHolder.layout();
			}
		}
		else {
			if (( ctrl == null ) || !ctrl.equals(panel.getComposite()) ) {
				((StackLayout)optionsHolder.getLayout()).topControl = panel.getComposite();
				optionsHolder.layout();
				Point needed = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
				// For some reason pack()/layout()/computeSize() add +16 than needed
				// Maybe some scroll-bar side effect?
				needed.x -= 16; // So, for now, we correct manually
				// Get the current size (may be user-specified)
				Point size = shell.getSize();
				// Adjust as needed
				if ( size.x < needed.x ) size.x = needed.x;
				if ( size.y < needed.y ) size.y = needed.y;
				shell.setSize(size);
				// Update the minimum size required
				if ( minSize.x < needed.x ) minSize.x = needed.x;
				if ( minSize.y < needed.y ) minSize.y = needed.y;
				//shell.setMinimumSize(minSize);
			}
		}
	}
	
	private void populate (int index) {
		if ( predefined != null ) {
			edPath.setText(predefined);
		}
		else {
			edPath.setText(wrapper.getPath()==null ? "" : wrapper.getPath());
		}
		lbSteps.removeAll();
		for ( StepInfo step : workSteps ) {
			lbSteps.add(step.name);
		}
		if ( index != -1 ) {
			if (( index < 0 ) || ( index > lbSteps.getItemCount() )) {
				index = -1;
			}
		}
		if ( index == -1 ) {
			index = 0;
		}
		lbSteps.select(index);
		updateStepDisplay();
	}
	
	private void moveStepUp () {
		try {
			int n = lbSteps.getSelectionIndex();
			if ( n <= 0 ) return;
			StepInfo tmp = workSteps.get(n-1);
			IEmbeddableParametersEditor tmpPanel = panels.get(n-1);
			workSteps.set(n-1, workSteps.get(n));
			panels.set(n-1, panels.get(n));
			workSteps.set(n, tmp);
			panels.set(n, tmpPanel);
			populate(n-1); // New position
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void moveStepDown () {
		try {
			int n = lbSteps.getSelectionIndex();
			if ( n > lbSteps.getItemCount()-1 ) return;
			StepInfo tmp = workSteps.get(n+1);
			IEmbeddableParametersEditor tmpPanel = panels.get(n+1);
			workSteps.set(n+1, workSteps.get(n));
			panels.set(n+1, panels.get(n));
			workSteps.set(n, tmp);
			panels.set(n, tmpPanel);
			populate(n+1); // New position
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void addStep () {
		try {
			StepPicker dlg = new StepPicker(shell, availableSteps, help);
			String id = dlg.showDialog();
			if ( id == null ) return;
			StepInfo info = availableSteps.get(id).clone();
			workSteps.add(info);
			panels.add(createPanel(info));
			lbSteps.add(info.name);
			lbSteps.select(lbSteps.getItemCount()-1);
			updateStepDisplay();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private void removeStep () {
		try {
			int n = lbSteps.getSelectionIndex();
			if ( n < 0 ) return;
			workSteps.remove(n);
			panels.remove(n);
			if ( n >= workSteps.size() ) n = workSteps.size()-1;
			populate(n);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}
	
	private IEmbeddableParametersEditor createPanel (StepInfo step) {
		try {
			if ( step.paramsData == null ) {
				// No parameters data for this step
				return new DefaultEmbeddableEditor();
			}
			if ( step.paramsClass == null ) {
				// No parameters class defined
				return null;
			}

			// Instantiate a Parameters object for this step
			IParameters params = (IParameters)Class.forName(step.paramsClass).newInstance();
			// Set it with the data from this step
			params.fromString(step.paramsData);
			
			// Instantiate an editor object
			IParametersEditor editor = null;
			try { // Catch creation error so we can fall-back to default editor
				editor = wrapper.getEditorMapper().createParametersEditor(step.paramsClass);
			}
			catch ( OkapiEditorCreationException e ) {
				Dialogs.showError(shell, e.getMessage(), null);
			}
			if (( editor != null )
				&& IEmbeddableParametersEditor.class.isAssignableFrom(editor.getClass()) ) {
				((IEmbeddableParametersEditor)editor).initializeEmbeddableEditor(optionsHolder, params, context);
				return (IEmbeddableParametersEditor)editor;
			}
			
			// Else: Try to use the generic editor
			IEditorDescriptionProvider descProv = wrapper.getEditorMapper().getDescriptionProvider(step.paramsClass);
			if ( descProv != null ) {
				GenericEmbeddableEditor geedit = new GenericEmbeddableEditor(descProv);
				geedit.initializeEmbeddableEditor(optionsHolder, params, context);
				return geedit;
			}
			
			// Default, last fall-back option
			DefaultEmbeddableEditor defEditor = new DefaultEmbeddableEditor();
			defEditor.initializeEmbeddableEditor(optionsHolder, params, context);
			return defEditor;
			
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
		// No usable editor
		return null;
	}
	
	private boolean saveData () {
		// Validate and save data from panels to each work-steps
		StepInfo step;
		IEmbeddableParametersEditor panel;
		for ( int i=0; i<workSteps.size(); i++ ) {
			step = workSteps.get(i);
			panel = panels.get(i);
			if ( panel != null ) {
				String data = panel.validateAndSaveParameters();
				if ( data == null ) {
					// Select the panel with the problem
					lbSteps.select(i);
					updateStepDisplay();
					// Cancel save
					return false;
				}
				step.paramsData = data;
			}
		}		
		
		// Copy the work steps to the real object
		wrapper.clear();
		for ( StepInfo step2 : workSteps ) {
			wrapper.addStep(step2);
		}
		return true;
	}
	
	private void save (String path) {
		try {
			if ( Util.isEmpty(path) ) {
				path = Dialogs.browseFilenamesForSave(shell, "Save Pipeline As", null, null, null);
				if ( path == null ) return;
			}
			if ( !saveData() ) return;
			wrapper.save(path);
			
			// If it was a predefined pipeline it is not anymore
			if ( predefined != null ) {
				predefined = null;
				updateStepDisplay();
			}
			
			// Update the path display
			edPath.setText(wrapper.getPath());
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}

	private void load (String path) {
		try {
			if (( path == null ) || ( path.length() == 0 )) {
				String[] paths = Dialogs.browseFilenames(shell, "Load Pipeline", false, null, null, null);
				if ( paths == null ) return;
				else path = paths[0];
			}
			wrapper.load(path);
			setDataFromWrapper();
			populate(-1);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
	}

	private void newPipeline () {
		wrapper.setPath(null);
		wrapper.clear();
		setDataFromWrapper();
		populate(-1);
	}
}
