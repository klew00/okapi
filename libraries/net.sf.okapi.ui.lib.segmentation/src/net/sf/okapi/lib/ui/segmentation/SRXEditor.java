/*===========================================================================*/
/* Copyright (C) 2008 Yves Savourel and the Okapi Framework contributors     */
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

package net.sf.okapi.lib.ui.segmentation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.ui.ClosePanel;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.filters.GenericContent;
import net.sf.okapi.lib.segmentation.Rule;
import net.sf.okapi.lib.segmentation.SRXDocument;
import net.sf.okapi.lib.segmentation.Segmenter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class SRXEditor {

	private Shell            shell;
	private Text             edSampleText;
	private Text             edResults;
	private Table            tblRules;
	private RulesTableModel  rulesTableMod;
	private Combo            cbGroup;
	private SRXDocument      srxDoc;
	private Segmenter        segmenter;
	private String           srxPath;
	private TextContainer    sampleText;
	private Button           btSRXDocs;
	private Menu             popupSRXDocs;
	private Button           btAddRule;
	private Button           btEditRule;
	private Button           btRemoveRule;
	private Button           btMoveUpRule;
	private Button           btMoveDownRule;
	private ClosePanel       pnlActions;
	private Button           rdApplySampleForMappedRules;
	private Button           rdApplySampleForCurrentSet;
	private Text             edSampleLanguage;
	private GenericContent   sampleOutput;
	private Pattern          patternOpening;
	private Pattern          patternClosing;
	private Pattern          patternPlaceholder;
	private Font             sampleFont; 
	

	@Override
	protected void finalize () {
		dispose();
	}

	public SRXEditor (Shell parent) {
		srxDoc = new SRXDocument();
		segmenter = new Segmenter();
		srxPath = null;
		sampleText = new TextContainer(null);
		sampleOutput = new GenericContent();
		
		patternOpening = Pattern.compile("\\<(\\w+[^\\>]*)\\>");
		patternClosing = Pattern.compile("\\</(\\w+[^\\>]*)\\>");
		patternPlaceholder = Pattern.compile("\\<(\\w+[^\\>]*)/\\>");
		
		shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.MAX | SWT.MIN | SWT.APPLICATION_MODAL);
		shell.setImage(parent.getImage());
		GridLayout layout = new GridLayout();
		shell.setLayout(layout);
		
		SashForm sashForm = new SashForm(shell, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		sashForm.setSashWidth(layout.verticalSpacing);
		
		Composite cmpTmp = new Composite(sashForm, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout(6, false);
		cmpTmp.setLayout(layTmp);
		
		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText("Language rules currently displayed:");
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		label.setLayoutData(gdTmp);
		
		cbGroup = new Combo(cmpTmp, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		cbGroup.setLayoutData(gdTmp);
		cbGroup.setVisibleItemCount(15);
		cbGroup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateRules(0, false);
			};
		});
		
		int topButtonsWidth = 140;
		Button btTmp = new Button(cmpTmp, SWT.PUSH);
		btTmp.setText("Groups and Options...");
		gdTmp = new GridData();
		gdTmp.widthHint = topButtonsWidth;
		btTmp.setLayoutData(gdTmp);
		btTmp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editGroupsAndOptions();
			}
		});
		
		btSRXDocs = new Button(cmpTmp, SWT.PUSH);
		btSRXDocs.setText("SRX Document...");
		gdTmp = new GridData();
		gdTmp.widthHint = topButtonsWidth;
		btSRXDocs.setLayoutData(gdTmp);
		setFileMenu(shell, btSRXDocs);
		btSRXDocs.addSelectionListener( new SelectionAdapter () {
			public void widgetSelected(SelectionEvent evt) {
				if (( popupSRXDocs != null ) && ( !popupSRXDocs.isVisible() )) {
					Rectangle bounds = btSRXDocs.getBounds(); 
					Point menuLoc = btSRXDocs.getParent().toDisplay(
						bounds.x, bounds.y + bounds.height);
					popupSRXDocs.setLocation(menuLoc.x, menuLoc.y);
					popupSRXDocs.setVisible(true);
			}}
		});
		
		tblRules = new Table(cmpTmp, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.CHECK);
		tblRules.setHeaderVisible(true);
		tblRules.setLinesVisible(true);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 6;
		gdTmp.minimumHeight = 130;
		tblRules.setLayoutData(gdTmp);
		tblRules.addControlListener(new ControlAdapter() {
		    public void controlResized(ControlEvent e) {
		    	Rectangle rect = tblRules.getClientArea();
				//TODO: Check behavior when manual resize a column width out of client area
		    	int typeColWidth = 75;
				int nHalf = (int)((rect.width-typeColWidth) / 2);
				tblRules.getColumn(0).setWidth(typeColWidth);
				tblRules.getColumn(1).setWidth(nHalf);
				tblRules.getColumn(2).setWidth(nHalf);
		    }
		});
		tblRules.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				editRule(false);
			}
			public void mouseDown(MouseEvent e) {}
			public void mouseUp(MouseEvent e) {}
		});
		tblRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.CHECK) {
					int n = tblRules.getSelectionIndex();
					if ( n < 1 ) return;
					String ruleName = cbGroup.getItem(cbGroup.getSelectionIndex());
					srxDoc.getLanguageRules(ruleName).get(n).setIsActive(((TableItem)e.item).getChecked());
					srxDoc.setIsModified(true);
					updateResults(true);
				}
				updateRulesButtons();
			};
		});
		
		rulesTableMod = new RulesTableModel();
		rulesTableMod.linkTable(tblRules);
		
		Composite cmpGroup = new Composite(cmpTmp, SWT.NONE);
		layTmp = new GridLayout(5, true);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		cmpGroup.setLayout(layTmp);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 5;
		cmpGroup.setLayoutData(gdTmp);

		int ruleButtonsWidth = 80;
		btAddRule = new Button(cmpGroup, SWT.PUSH);
		btAddRule.setText("&Add...");
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btAddRule.setLayoutData(gdTmp);
		btAddRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editRule(true);
			}
		});
		
		btEditRule = new Button(cmpGroup, SWT.PUSH);
		btEditRule.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btEditRule.setText("&Edit...");
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btEditRule.setLayoutData(gdTmp);
		btEditRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editRule(false);
			}
		});
		
		btRemoveRule = new Button(cmpGroup, SWT.PUSH);
		btRemoveRule.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btRemoveRule.setText("&Remove");
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btRemoveRule.setLayoutData(gdTmp);
		btRemoveRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeRule();
			}
		});

		btMoveUpRule = new Button(cmpGroup, SWT.PUSH);
		btMoveUpRule.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btMoveUpRule.setText("Move &Up");
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btMoveUpRule.setLayoutData(gdTmp);
		btMoveUpRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveUpRule();
			}
		});

		btMoveDownRule = new Button(cmpGroup, SWT.PUSH);
		btMoveDownRule.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btMoveDownRule.setText("Move &Down");
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btMoveDownRule.setLayoutData(gdTmp);
		btMoveDownRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveDownRule();
			}
		});

		
		//--- Sample block

		Composite cmpSample = new Composite(sashForm, SWT.BORDER);
		cmpSample.setLayoutData(new GridData(GridData.FILL_BOTH));
		cmpSample.setLayout(new GridLayout(3, false));
		
		label = new Label(cmpSample, SWT.None);
		label.setText("Sample text (use <x>...</x> and <x/> for inline codes):");
		gdTmp = new GridData();
		gdTmp.horizontalSpan = 3;
		label.setLayoutData(gdTmp);
		
		int sampleMinHeight = 40;
		edSampleText = new Text(cmpSample, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.minimumHeight = sampleMinHeight;
		gdTmp.horizontalSpan = 3;
		edSampleText.setLayoutData(gdTmp);

		Font font = edSampleText.getFont();
		FontData[] fontData = font.getFontData();
		fontData[0].setHeight(10);
		sampleFont = new Font(font.getDevice(), fontData[0]);
		edSampleText.setFont(sampleFont);
		
		rdApplySampleForCurrentSet = new Button(cmpSample, SWT.RADIO);
		rdApplySampleForCurrentSet.setText("Test on the current set of rules only");
		rdApplySampleForCurrentSet.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				edSampleLanguage.setEnabled(rdApplySampleForMappedRules.getSelection());
				updateRules(tblRules.getSelectionIndex(), true);
			};
		});

		rdApplySampleForMappedRules = new Button(cmpSample, SWT.RADIO);
		rdApplySampleForMappedRules.setText("Test on the rules for this language code:");
		rdApplySampleForMappedRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				edSampleLanguage.setEnabled(rdApplySampleForMappedRules.getSelection());
				updateRules(tblRules.getSelectionIndex(), true);
			};
		});
		
		edSampleLanguage = new Text(cmpSample, SWT.BORDER | SWT.SINGLE);
		gdTmp = new GridData();
		edSampleLanguage.setLayoutData(gdTmp);
		edSampleLanguage.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateResults(false);
			}
		});


		edResults = new Text(cmpSample, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		edResults.setLayoutData(gdTmp);
		gdTmp.minimumHeight = sampleMinHeight;
		gdTmp.horizontalSpan = 3;
		edResults.setEditable(false);
		edResults.setFont(sampleFont);

		edSampleText.addModifyListener(new ModifyListener () {
			public void modifyText(ModifyEvent e) {
				updateResults(false);
			}
		});

		// Handling of the closing event
		shell.addShellListener(new ShellListener() {
			public void shellActivated(ShellEvent event) {}
			public void shellClosed(ShellEvent event) {
				if ( !checkIfRulesNeedSaving() ) event.doit = false;
			}
			public void shellDeactivated(ShellEvent event) {}
			public void shellDeiconified(ShellEvent event) {}
			public void shellIconified(ShellEvent event) {}
		});

		
		//--- Dialog-level buttons
		
		SelectionAdapter CloseActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( e.widget.getData().equals("h") ) {
					//TODO: UIUtil.start(help);
					return;
				}
				if ( e.widget.getData().equals("c") ) {
					shell.close();
				}
			};
		};
		pnlActions = new ClosePanel(shell, SWT.NONE, CloseActions, true);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 2;
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btClose);
		
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 700 ) startSize.x = 700; 
		if ( startSize.y < 600 ) startSize.y = 600; 
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
		
		updateCaption();
		updateAll();
	}
	
	public void dispose () {
		if ( sampleFont != null ) {
			sampleFont.dispose();
			sampleFont = null;
		}
	}
	
	private void setFileMenu (Shell shell,
		Button fileButton)
	{
		popupSRXDocs = new Menu(shell, SWT.POP_UP);
		fileButton.setMenu(popupSRXDocs);

		MenuItem menuItem = new MenuItem(popupSRXDocs, SWT.PUSH);
		menuItem.setText("New Document");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				newSRXDocument();
            }
		});

		menuItem = new MenuItem(popupSRXDocs, SWT.PUSH);
		menuItem.setText("Open Document...");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				loadSRXDocument(null);
            }
		});

		new MenuItem(popupSRXDocs, SWT.SEPARATOR);

		menuItem = new MenuItem(popupSRXDocs, SWT.PUSH);
		menuItem.setText("Save Current Document");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveSRXDocument(srxPath);
            }
		});

		menuItem = new MenuItem(popupSRXDocs, SWT.PUSH);
		menuItem.setText("Save Current Document As...");
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveSRXDocument(null);
            }
		});
	}

	/**
	 * Opens the dialog box, loads an SRX document if one is specified.
	 * @param path Optional SRX document to load. Use null to load nothing.
	 */
	public void showDialog (String path) {
		shell.open();
		if ( path != null ) loadSRXDocument(path);
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
	}
	
	/**
	 * Gets the full path of the last SRX document loaded.
	 * @return The full path of the last SRX document loaded, or null
	 * if not has been loaded.
	 */
	public String getPath () {
		return srxPath;
	}
	
	private void updateResults (boolean forceReset) {
		try {
			// Check if we need to re-build the list of applicable rules
			if ( cbGroup.getSelectionIndex() != -1 ) {
				// Both methods applies new rules only if the 
				// parameter passed is different from the current identifier
				// or if forceReset is true.
				if ( rdApplySampleForCurrentSet.getSelection() ) {
					segmenter = srxDoc.applySingleLanguageRule(cbGroup.getText(),
						(forceReset ? null : segmenter));
				}
				else { // Applies all the matching rules
					// Make sure we have a language code
					if ( edSampleLanguage.getText().length() == 0 ) {
						edSampleLanguage.setText("en");
					}
					segmenter = srxDoc.applyLanguageRules(edSampleLanguage.getText(),
						(forceReset ? null : segmenter));
				}
			}
			
			if ( segmenter.getLanguage() != null ) {
				// Converts the <x>/</x>/etc. into real inline codes
				processInlineCodes();
				// Segment
				segmenter.computeSegments(sampleText);
				List<java.awt.Point> ranges = segmenter.getSegmentRanges();
				sampleText.createSegments(ranges);
				// Create the output in generic format
				edResults.setText(sampleOutput.printSegmentedContent(sampleText, true));
			}
			else {
				edResults.setText("");
			}
		}
		catch ( Throwable e ) {
			edResults.setText("Error: "+ e.getMessage());
		}
	}

	/**
	 * Converts the sample edit field content to sampleText.
	 */
	private void processInlineCodes () {
		sampleText.clear();
		String text = edSampleText.getText().replace("\r", "");
		sampleText.clear();
		sampleText.setCodedText(text);
		int n;
		int start = 0;
		int diff = 0;
		
		Matcher m = patternOpening.matcher(text);
		while ( m.find(start) ) {
			n = m.start();
			diff += sampleText.changeToCode(n+diff, (n+diff)+m.group().length(),
				TagType.OPENING, m.group(1));
			start = (n+m.group().length());
		}
		
		text = sampleText.getCodedText();
		start = diff = 0;
		m = patternClosing.matcher(text);
		while ( m.find(start) ) {
			n = m.start();
			diff += sampleText.changeToCode(n+diff, (n+diff)+m.group().length(),
				TagType.CLOSING, m.group(1));
			start = (n+m.group().length());
		}
		
		text = sampleText.getCodedText();
		start = diff = 0;
		m = patternPlaceholder.matcher(text);
		while ( m.find(start) ) {
			n = m.start();
			diff += sampleText.changeToCode(n+diff, (n+diff)+m.group().length(),
				TagType.PLACEHOLDER, null);
			start = (n+m.group().length());
		}
	}
	
	private void updateLanguageRuleList () {
		cbGroup.removeAll();
		LinkedHashMap<String, ArrayList<Rule>> langRules = srxDoc.getAllLanguageRules();
		for ( String ruleName : langRules.keySet() ) {
			cbGroup.add(ruleName);
		}
		if ( cbGroup.getItemCount() > 0 ) {
			cbGroup.select(0);
		}
		updateRules(0, true);
	}
	
	private void updateRules (int selection,
		boolean forceReset) {
		rulesTableMod.setLanguageRules(
			srxDoc.getLanguageRules(cbGroup.getText()));
		rulesTableMod.updateTable(selection);
		updateResults(forceReset);
		updateRulesButtons();
	}
	
	private void updateRulesButtons () {
		int n = tblRules.getSelectionIndex();
		btAddRule.setEnabled(cbGroup.getSelectionIndex()>-1);
		btEditRule.setEnabled(n != -1);
		btRemoveRule.setEnabled(n != -1);
		btMoveUpRule.setEnabled(n > 0);
		btMoveDownRule.setEnabled(n < tblRules.getItemCount()-1);
	}
	
	private void editGroupsAndOptions () {
		try {
			getSurfaceData();
			GroupsAndOptionsDialog dlg = new GroupsAndOptionsDialog(shell, srxDoc);
			dlg.showDialog();
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
		finally {
			updateAll();
		}
	}

	private void setSurfaceData () {
		edSampleText.setText(srxDoc.getSampleText());
		edSampleLanguage.setText(srxDoc.getSampleLanguage());
		rdApplySampleForMappedRules.setSelection(srxDoc.sampleOnMappedRules());
		rdApplySampleForCurrentSet.setSelection(!srxDoc.sampleOnMappedRules());
		edSampleLanguage.setEnabled(rdApplySampleForMappedRules.getSelection());
	}

	private void getSurfaceData () {
		srxDoc.setSampleText(edSampleText.getText());
		srxDoc.setSampleLanguage(edSampleLanguage.getText());
		srxDoc.setSampleOnMappedRules(rdApplySampleForMappedRules.getSelection());
	}
	
	private void updateCaption () {
		if ( srxPath == null ) {
			shell.setText("Segmentation Rules Editor");
		}
		else {
			shell.setText("Segmentation Rules Editor - "
				+ Util.getFilename(srxPath, true));
		}
	}
	
	private void updateAll () {
		cbGroup.removeAll();
		setSurfaceData();
		updateLanguageRuleList();
	}
	
	private void newSRXDocument () {
		if ( !checkIfRulesNeedSaving() ) return;
		srxDoc = new SRXDocument();
		srxPath = null;
		updateCaption();
		updateAll();
	}
	
	private void loadSRXDocument (String path) {
		try {
			getSurfaceData(); // To get back the original data in case of escape: 
			if ( path == null ) {
				String[] paths = Dialogs.browseFilenames(shell, "Open SRX Document",
					false, null, "SRX Documents (*.srx)\tAll Files (*.*)",
					"*.srx\t*.*");
				if ( paths == null ) return; // Cancel
				else path = paths[0];
			}
			srxPath = null; // In case an error occurs
			srxDoc.loadRules(path);
			if ( srxDoc.hasWarning() ) {
				MessageBox dlg = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES| SWT.CANCEL);
				dlg.setText(shell.getText());
				dlg.setMessage(srxDoc.getWarning());
				dlg.open();
			}
			srxPath = path; // Set the path only after the load is fine
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
		finally {
			updateCaption();
			updateAll();
		}
	}
	
	private boolean saveSRXDocument (String path) {
		try {
			if ( !srxDoc.getVersion().equals("2.0") ) {
				MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
				dlg.setText(shell.getText());
				dlg.setMessage("The file will be saved as SRX 2.0.\n"
					+"Do you want to proceed?");
				if ( dlg.open() != SWT.YES ) return false;
			}
			if ( path == null ) {
				path = Dialogs.browseFilenamesForSave(shell, "Save SRX Document", null,
					"SRX Documents (*.srx)", "*.srx");
				if ( path == null ) return false;
			}
			getSurfaceData();
			// Save, but not the rules extra info: active/non-active (not standard) 
			srxDoc.saveRules(path, false);
			srxPath = path;
			updateCaption();
		}
		catch ( Exception e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
		return true;
	}
	
	private void editRule (boolean createNewRule) {
		if ( cbGroup.getSelectionIndex() < 0 ) return;
		Rule rule;
		String caption;
		String ruleName = cbGroup.getItem(cbGroup.getSelectionIndex());
		int n = -1;
		if ( createNewRule ) {
			caption = "New Rule";
			rule = new Rule("", "", true);
		}
		else {
			n = tblRules.getSelectionIndex();
			if ( n == -1 ) return;
			rule = srxDoc.getLanguageRules(ruleName).get(n);
			caption = "Edit Rule";
		}
		
		RuleDialog dlg = new RuleDialog(shell, caption, rule);
		if ( (rule = dlg.showDialog()) == null ) return; // Cancel
		
		if ( createNewRule ) {
			srxDoc.getLanguageRules(ruleName).add(rule);
			n = srxDoc.getLanguageRules(ruleName).size()-1;
		}
		else {
			srxDoc.getLanguageRules(ruleName).set(n, rule);
		}
		srxDoc.setIsModified(true);
		updateRules(n, true);
	}
	
	private void removeRule () {
		int n = tblRules.getSelectionIndex();
		if ( n == -1 ) return;
		String ruleName = cbGroup.getItem(cbGroup.getSelectionIndex());
		srxDoc.getLanguageRules(ruleName).remove(n);
		srxDoc.setIsModified(true);
		tblRules.remove(n);
		if ( n > tblRules.getItemCount()-1 )
			n = tblRules.getItemCount()-1;
		if ( tblRules.getItemCount() > 0 )
			tblRules.select(n);
		updateRulesButtons();
		updateResults(true);
	}
	
	private void moveUpRule () {
		int n = tblRules.getSelectionIndex();
		if ( n < 1 ) return;
		// Move in the segmenter
		String ruleName = cbGroup.getItem(cbGroup.getSelectionIndex());
		Rule tmp = srxDoc.getLanguageRules(ruleName).get(n-1);
		srxDoc.getLanguageRules(ruleName).set(n-1,
			srxDoc.getLanguageRules(ruleName).get(n));
		srxDoc.getLanguageRules(ruleName).set(n, tmp);
		srxDoc.setIsModified(true);
		// Update
		updateRules(n-1, true);
	}
	
	private void moveDownRule () {
		int n = tblRules.getSelectionIndex();
		if ( n > tblRules.getItemCount()-2 ) return;
		// Move in the segmenter
		String ruleName = cbGroup.getItem(cbGroup.getSelectionIndex());
		Rule tmp = srxDoc.getLanguageRules(ruleName).get(n+1);
		srxDoc.getLanguageRules(ruleName).set(n+1,
			srxDoc.getLanguageRules(ruleName).get(n));
		srxDoc.getLanguageRules(ruleName).set(n, tmp);
		srxDoc.setIsModified(true);
		// Update
		updateRules(n+1, true);
	}
	
	/**
	 * Checks if the rules need saving, and save them after prompting
	 * the user if needed.
	 * @return False if the user cancel, true if a decision is made. 
	 */
	private boolean checkIfRulesNeedSaving () {
		getSurfaceData();
		if ( srxDoc.isModified() ) {
			MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
			dlg.setText(shell.getText());
			dlg.setMessage("The segmentation rules have been modified but not saved.\n"
				+"Do you want to save them?");
			switch ( dlg.open() ) {
			case SWT.CANCEL:
				return false;
			case SWT.YES:
				return saveSRXDocument(srxPath);
			}
		}
		return true;
	}
}
