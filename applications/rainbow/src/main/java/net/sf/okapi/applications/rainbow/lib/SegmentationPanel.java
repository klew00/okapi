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

package net.sf.okapi.applications.rainbow.lib;

import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.lib.ui.segmentation.SRXEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Implements a common UI to select a filter settings string.
 */
public class SegmentationPanel extends Composite {

	private Button chkSegment;
	private Label stSourceSRX;
	private Text edSourceSRX;
	private Button btGetSourceSRX;
	private Button btEditSourceSRX;
	private Label stTargetSRX;
	private Text edTargetSRX;
	private Button btGetTargetSRX;
	private Button btEditTargetSRX;
	private IHelp help;
	private String projectDir;

	public SegmentationPanel (Composite p_Parent,
		int p_nFlags,
		String segmentCaption,
		IHelp helpParam,
		String projectDir)
	{
		super(p_Parent, SWT.NONE);
		help = helpParam;
		this.projectDir = projectDir;
		createContent(segmentCaption);
	}
	
	private void createContent (String segmentCaption) {
		GridLayout layTmp = new GridLayout(4, false);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		setLayout(layTmp);

		chkSegment = new Button(this, SWT.CHECK);
		chkSegment.setText(segmentCaption);
		GridData gdTmp = new GridData();
		gdTmp.horizontalSpan = 4;
		chkSegment.setLayoutData(gdTmp);
		chkSegment.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateDisplay();
			}
		});

		stSourceSRX = new Label(this, SWT.NONE);
		stSourceSRX.setText(Res.getString("SegmentationPanel.sourceSrx")); //$NON-NLS-1$
		
		edSourceSRX = new Text(this, SWT.BORDER);
		edSourceSRX.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		btGetSourceSRX = new Button(this, SWT.PUSH);
		btGetSourceSRX.setText("..."); //$NON-NLS-1$
		btGetSourceSRX.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getSRXFile(edSourceSRX);
			}
		});
		
		btEditSourceSRX = new Button(this, SWT.PUSH);
		btEditSourceSRX.setText(Res.getString("SegmentationPanel.edit")); //$NON-NLS-1$
		btEditSourceSRX.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editSRXFile(edSourceSRX);
			}
		});
		
		stTargetSRX = new Label(this, SWT.NONE);
		stTargetSRX.setText(Res.getString("SegmentationPanel.targetSrx")); //$NON-NLS-1$
		
		edTargetSRX = new Text(this, SWT.BORDER);
		edTargetSRX.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		btGetTargetSRX = new Button(this, SWT.PUSH);
		btGetTargetSRX.setText("..."); //$NON-NLS-1$
		btGetTargetSRX.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getSRXFile(edTargetSRX);
			}
		});
		
		btEditTargetSRX = new Button(this, SWT.PUSH);
		btEditTargetSRX.setText(Res.getString("SegmentationPanel.edit")); //$NON-NLS-1$
		btEditTargetSRX.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editSRXFile(edTargetSRX);
			}
		});

	}
	
	private void getSRXFile (Text edTextField) {
		String caption;
		if ( edTextField == edSourceSRX ) caption = Res.getString("SegmentationPanel.selectSourceSrx"); //$NON-NLS-1$
		else  caption = Res.getString("SegmentationPanel.selectTargetSrx"); //$NON-NLS-1$
		String[] paths = Dialogs.browseFilenames(getShell(), caption, false, null,
			Res.getString("SegmentationPanel.srxFilterDescriptions"), //$NON-NLS-1$
			Res.getString("SegmentationPanel.srxFilterExtensions")); //$NON-NLS-1$
		if ( paths == null ) return;
		Utils.checkProjectDirAfterPick(paths[0], edTextField, projectDir);
	}
	
	private void editSRXFile (Text edTextField) {
		try {
			SRXEditor editor = new SRXEditor(getShell(), true, help);
			String oriPath = edTextField.getText().replace("${ProjDir}", projectDir); //$NON-NLS-1$
			if ( oriPath.length() == 0 ) oriPath = null;
			editor.showDialog(oriPath);
			String newPath = editor.getPath();
			Utils.checkProjectDirAfterPick(newPath, edTextField, projectDir);
		}
		catch ( Throwable e ) {
			Dialogs.showError(getShell(), e.getMessage(), null);
		}
	}
	
	private void updateDisplay () {
		boolean enabled = chkSegment.getSelection();
		stSourceSRX.setEnabled(enabled);
		edSourceSRX.setEnabled(enabled);
		btGetSourceSRX.setEnabled(enabled);
		btEditSourceSRX.setEnabled(enabled);
		stTargetSRX.setEnabled(enabled);
		edTargetSRX.setEnabled(enabled);
		btGetTargetSRX.setEnabled(enabled);
		btEditTargetSRX.setEnabled(enabled);
	}
	
	public void setData (boolean segment,
		String sourceSRX,
		String targetSRX)
	{
		chkSegment.setSelection(segment);
		edSourceSRX.setText(sourceSRX);
		edTargetSRX.setText(targetSRX);
		updateDisplay();
	}

	public boolean validate () {
		if ( !chkSegment.getSelection() ) return true;
		if ( edSourceSRX.getText().trim().length() == 0 ) {
			Dialogs.showError(getShell(), "You must specify an SRX document for the source.", null);
			edSourceSRX.setFocus();
			return false;
		}
		return true;
	}
	
	public boolean getSegment () {
		return chkSegment.getSelection();
	}
	
	public String getSourceSRX () {
		return edSourceSRX.getText();
	}
	
	public String getTargetSRX () {
		return edTargetSRX.getText();
	}
}
