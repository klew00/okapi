/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.lib.ui.segmentation;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.ui.CharacterInfoDialog;
import net.sf.okapi.common.ui.ClosePanel;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.InputDialog;
import net.sf.okapi.common.ui.ResourceManager;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.common.ui.UserConfiguration;
import net.sf.okapi.common.writer.GenericInlines;
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

	private static final String APPNAME = "Ratel"; //$NON-NLS-1$
	
	private Shell shell;
	private boolean asDialog;
	private Text edSampleText;
	private Text edResults;
	private Table tblRules;
	private RulesTableModel rulesTableMod;
	private Combo cbGroup;
	private SRXDocument srxDoc;
	private Segmenter segmenter;
	private String srxPath;
	private TextContainer sampleText;
	private Button btAddRule;
	private Button btEditRule;
	private Button btRemoveRule;
	private Button btMoveUpRule;
	private Button btMoveDownRule;
	private Button rdTestOnLanguage;
	private Button rdTestOnSelectedGroup;
	private Text edSampleLanguage;
	private GenericInlines sampleOutput;
	private Font sampleFont;
	private ResourceManager rm;
	private String helpPath;
	private FileProcessor fileProc;
	private String testInputPath;
	private String testOutputPath;
	private boolean htmlOutput;
	private UserConfiguration config;

	@Override
	protected void finalize () {
		dispose();
	}

	public SRXEditor (Shell parent,
		boolean asDialog,
		String helpPath)
	{
		config = new UserConfiguration();
		config.load(APPNAME);
		testInputPath = config.getProperty("testInputPath"); //$NON-NLS-1$
		testOutputPath = config.getProperty("testOutputPath"); //$NON-NLS-1$
		htmlOutput = config.getBoolean("htmlOutput"); //$NON-NLS-1$

		this.asDialog = asDialog;
		this.helpPath = helpPath;
		srxDoc = new SRXDocument();
		srxPath = null;
		sampleText = new TextContainer(null);
		sampleOutput = new GenericInlines();
		fileProc = new FileProcessor();
		
		if ( asDialog ) {
			shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.MAX | SWT.MIN | SWT.APPLICATION_MODAL);
			shell.setImage(parent.getImage());
		}
		else {
			shell = parent;
		}

		rm = new ResourceManager(SRXEditor.class, shell.getDisplay());
		rm.loadCommands("commands.xml"); //$NON-NLS-1$
		
		GridLayout layout = new GridLayout();
		shell.setLayout(layout);
		
		createMenus();
		
		SashForm sashForm = new SashForm(shell, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		sashForm.setSashWidth(3);
		sashForm.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		
		Composite cmpTmp = new Composite(sashForm, SWT.BORDER);
		cmpTmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layTmp = new GridLayout(6, false);
		cmpTmp.setLayout(layTmp);
		
		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText(Res.getString("edit.currentLangRules")); //$NON-NLS-1$
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 4;
		label.setLayoutData(gdTmp);
		
		cbGroup = new Combo(cmpTmp, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 5;
		cbGroup.setLayoutData(gdTmp);
		cbGroup.setVisibleItemCount(15);
		cbGroup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateRules(0, false);
			};
		});
		
		int topButtonsWidth = 180;
		Button btTmp = new Button(cmpTmp, SWT.PUSH);
		btTmp.setText(Res.getString("edit.groupAndOptions")); //$NON-NLS-1$
		gdTmp = new GridData();
		gdTmp.widthHint = topButtonsWidth;
		btTmp.setLayoutData(gdTmp);
		btTmp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editGroupsAndOptions();
			}
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
					if ( n < 0 ) return;
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
		layTmp = new GridLayout(7, true);
		layTmp.marginHeight = 0;
		layTmp.marginWidth = 0;
		cmpGroup.setLayout(layTmp);
		gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		gdTmp.horizontalSpan = 6;
		cmpGroup.setLayoutData(gdTmp);

		int ruleButtonsWidth = 95;
		btAddRule = new Button(cmpGroup, SWT.PUSH);
		btAddRule.setText(Res.getString("edit.btAddRule")); //$NON-NLS-1$
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
		btEditRule.setText(Res.getString("edit.btEditRule")); //$NON-NLS-1$
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
		btRemoveRule.setText(Res.getString("edit.btRemoveRule")); //$NON-NLS-1$
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
		btMoveUpRule.setText(Res.getString("edit.moveUp")); //$NON-NLS-1$
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
		btMoveDownRule.setText(Res.getString("edit.moveDown")); //$NON-NLS-1$
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btMoveDownRule.setLayoutData(gdTmp);
		btMoveDownRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveDownRule();
			}
		});

		Button btMaskRule = new Button(cmpGroup, SWT.PUSH);
		btMaskRule.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btMaskRule.setText(Res.getString("edit.maskRule")); //$NON-NLS-1$
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btMaskRule.setLayoutData(gdTmp);
		btMaskRule.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editMaskRule();
			}
		});

		Button btCharInfo = new Button(cmpGroup, SWT.PUSH);
		btCharInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btCharInfo.setText(Res.getString("edit.charInfo")); //$NON-NLS-1$
		gdTmp = new GridData();
		gdTmp.widthHint = ruleButtonsWidth;
		btCharInfo.setLayoutData(gdTmp);
		btCharInfo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showCharInfo();
			}
		});

		
		//--- Sample block

		Composite cmpSample = new Composite(sashForm, SWT.BORDER);
		cmpSample.setLayoutData(new GridData(GridData.FILL_BOTH));
		cmpSample.setLayout(new GridLayout(3, false));
		
		label = new Label(cmpSample, SWT.None);
		label.setText(Res.getString("edit.sampleNote")); //$NON-NLS-1$
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
		
		rdTestOnSelectedGroup = new Button(cmpSample, SWT.RADIO);
		rdTestOnSelectedGroup.setText(Res.getString("edit.testOnlyGroup")); //$NON-NLS-1$
		rdTestOnSelectedGroup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				edSampleLanguage.setEnabled(rdTestOnLanguage.getSelection());
				updateRules(tblRules.getSelectionIndex(), true);
			};
		});

		rdTestOnLanguage = new Button(cmpSample, SWT.RADIO);
		rdTestOnLanguage.setText(Res.getString("edit.testLanguage")); //$NON-NLS-1$
		rdTestOnLanguage.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				edSampleLanguage.setEnabled(rdTestOnLanguage.getSelection());
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
		if ( asDialog ) {
			SelectionAdapter closeActions = new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if ( e.widget.getData().equals("h") ) { //$NON-NLS-1$
						callHelp();
						return;
					}
					if ( e.widget.getData().equals("c") ) { //$NON-NLS-1$
						shell.close();
					}
				};
			};
			ClosePanel pnlActions = new ClosePanel(shell, SWT.NONE, closeActions, true);
			gdTmp = new GridData(GridData.FILL_HORIZONTAL);
			gdTmp.horizontalSpan = 2;
			pnlActions.setLayoutData(gdTmp);
			shell.setDefaultButton(pnlActions.btClose);
		}

		// Size
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		Point startSize = shell.getMinimumSize();
		if ( startSize.x < 700 ) startSize.x = 700; 
		if ( startSize.y < 600 ) startSize.y = 600; 
		shell.setSize(startSize);
		if ( asDialog ) {
			Dialogs.centerWindow(shell, parent);
		}
		
		updateCaption();
		updateAll();
	}
	
	private void createMenus () {
		// Menus
	    Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);

		//=== File menu
		
		MenuItem topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText(rm.getCommandLabel("file")); //$NON-NLS-1$
		Menu dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);
		
		MenuItem menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "file.new"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				newSRXDocument();
            }
		});
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "file.open"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				loadSRXDocument(null);
            }
		});

		new MenuItem(dropMenu, SWT.SEPARATOR);

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "file.save"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveSRXDocument(srxPath);
            }
		});
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "file.saveas"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveSRXDocument(null);
            }
		});

		new MenuItem(dropMenu, SWT.SEPARATOR);

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "file.exit"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
            }
		});

		//=== Tools menu

		topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText(rm.getCommandLabel("tools")); //$NON-NLS-1$
		dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "tools.segfile"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				segmentTextFile();
            }
		});
		
		//=== Help menu

		topItem = new MenuItem(menuBar, SWT.CASCADE);
		topItem.setText(rm.getCommandLabel("help")); //$NON-NLS-1$
		dropMenu = new Menu(shell, SWT.DROP_DOWN);
		topItem.setMenu(dropMenu);
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "help.topics"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				callHelp();
            }
		});
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "help.feedback"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				UIUtil.start("mailto:okapitools@opentag.com&subject=Feedback (Ratel: SRX Editor)"); //$NON-NLS-1$
            }
		});
		
		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "help.users"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				UIUtil.start("http://groups.yahoo.com/group/okapitools/"); //$NON-NLS-1$
            }
		});

		menuItem = new MenuItem(dropMenu, SWT.SEPARATOR);

		menuItem = new MenuItem(dropMenu, SWT.PUSH);
		rm.setCommand(menuItem, "help.srx20"); //$NON-NLS-1$
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				UIUtil.start("http://www.lisa.org/fileadmin/standards/srx20.html"); //$NON-NLS-1$
            }
		});

	}
	
	public void dispose () {
		if ( sampleFont != null ) {
			sampleFont.dispose();
			sampleFont = null;
		}
		if ( rm != null ) {
			rm.dispose();
		}
	}

	private void showCharInfo () {
		try {
			CharacterInfoDialog charInfoDlg = new CharacterInfoDialog(shell,
				Res.getString("edit.charInfoCaption"), null); //$NON-NLS-1$
			int codePoint = 225;
			String tmp = edSampleText.getSelectionText();
			if ( tmp.length() > 0 ) {
				codePoint = tmp.codePointAt(0);
			}
			charInfoDlg.showDialog(codePoint);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getMessage(), null);
		}
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
				if ( rdTestOnSelectedGroup.getSelection() ) {
					segmenter = srxDoc.applySingleLanguageRule(cbGroup.getText(),
						(forceReset ? null : segmenter));
				}
				else { // Applies all the matching rules
					// Make sure we have a language code
					if ( edSampleLanguage.getText().length() == 0 ) {
						edSampleLanguage.setText("en"); //$NON-NLS-1$
					}
					segmenter = srxDoc.applyLanguageRules(edSampleLanguage.getText(),
						(forceReset ? null : segmenter));
				}
			}
			else { // No selection
				segmenter = null;
			}
			
			if (( segmenter != null ) && ( segmenter.getLanguage() != null )) {
				// Converts the <x>/</x>/etc. into real inline codes
				fileProc.populateTextContainer(
					edSampleText.getText().replace("\r", ""), sampleText);  //$NON-NLS-1$  //$NON-NLS-2$
				// Segment
				segmenter.computeSegments(sampleText);
				sampleText.createSegments(segmenter.getSegmentRanges());
				// Create the output in generic format
				edResults.setText(sampleOutput.printSegmentedContent(sampleText, true, true));
			}
			else {
				edResults.setText(""); //$NON-NLS-1$
			}
		}
		catch ( Throwable e ) {
			edResults.setText(Res.getString("edit.error")+ e.getMessage()); //$NON-NLS-1$
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
			GroupsAndOptionsDialog dlg = new GroupsAndOptionsDialog(shell, srxDoc, helpPath);
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
		rdTestOnSelectedGroup.setSelection(srxDoc.testOnSelectedGroup());
		rdTestOnLanguage.setSelection(!srxDoc.testOnSelectedGroup());
		edSampleLanguage.setEnabled(rdTestOnLanguage.getSelection());
	}

	private void getSurfaceData () {
		srxDoc.setSampleText(edSampleText.getText());
		srxDoc.setSampleLanguage(edSampleLanguage.getText());
		srxDoc.setTestOnSelectedGroup(rdTestOnSelectedGroup.getSelection());
	}
	
	private void updateCaption () {
		String text = Res.getString(asDialog ? "edit.captionDlg" : "edit.captionApp");  //$NON-NLS-1$  //$NON-NLS-2$
		if ( srxPath != null ) {
			text += " - "; //$NON-NLS-1$
			text += Util.getFilename(srxPath, true);	
		}
		shell.setText(text);
	}
	
	private void updateAll () {
		cbGroup.removeAll();
		setSurfaceData();
		updateLanguageRuleList();
	}
	
	private boolean newSRXDocument () {
		if ( !checkIfRulesNeedSaving() ) return false;
		srxDoc = new SRXDocument();
		srxPath = null;
		updateCaption();
		updateAll();
		return true;
	}
	
	private void loadSRXDocument (String path) {
		try {
			getSurfaceData(); // To get back the original data in case of escape: 
			if ( path == null ) {
				String[] paths = Dialogs.browseFilenames(shell,
					Res.getString("edit.loadDocCaption"), //$NON-NLS-1$
					false, null, Res.getString("edit.loadDocFileTypes"), //$NON-NLS-1$
					Res.getString("edit.loadDocFilters")); //$NON-NLS-1$
				if ( paths == null ) return; // Cancel
				else path = paths[0];
			}
			srxPath = null; // In case an error occurs
			srxDoc.loadRules(path);
			if ( srxDoc.hasWarning() ) {
				MessageBox dlg = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.CANCEL);
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
			if ( !srxDoc.getVersion().equals("2.0") ) { //$NON-NLS-1$
				MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
				dlg.setText(shell.getText());
				dlg.setMessage(Res.getString("edit.saveDocVersionWarning")); //$NON-NLS-1$
				if ( dlg.open() != SWT.YES ) return false;
			}
			if ( path == null ) {
				path = Dialogs.browseFilenamesForSave(shell, Res.getString("edit.saveDocCaption"), null, //$NON-NLS-1$
					Res.getString("edit.saveDocFileTypes"), //$NON-NLS-1$
					Res.getString("edit.saveDocFilters")); //$NON-NLS-1$
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
			rule = new Rule("", "", true);  //$NON-NLS-1$  //$NON-NLS-2$
		}
		else {
			n = tblRules.getSelectionIndex();
			if ( n == -1 ) return;
			rule = srxDoc.getLanguageRules(ruleName).get(n);
		}
		
		RuleDialog dlg = new RuleDialog(shell, rule, helpPath);
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
	 * Edits the range rule of the document.
	 */
	private void editMaskRule () {
		try {
			InputDialog dlg = new InputDialog(shell, Res.getString("edit.maskRuleCaption"), //$NON-NLS-1$
				Res.getString("edit.maskRuleDesc"), "", null, 0);  //$NON-NLS-1$  //$NON-NLS-2$
			dlg.setInputValue(srxDoc.getMaskRule());
			dlg.setAllowEmptyValue(true);
			String result = dlg.showDialog();
			if ( result == null ) return; // Canceled
			// Else: Set the new expression
			srxDoc.setMaskRule(result);
			updateResults(true);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
	}
	
	/**
	 * Checks if the rules need saving, and save them after prompting
	 * the user if needed.
	 * @return False if the user cancel, true if a decision is made. 
	 */
	private boolean checkIfRulesNeedSaving () {
		config.setProperty("testInputPath", testInputPath); //$NON-NLS-1$
		config.setProperty("testOutputPath", testOutputPath); //$NON-NLS-1$
		config.setProperty("htmlOutput", htmlOutput); //$NON-NLS-1$
		config.save(APPNAME, "Beta");
		
		getSurfaceData();
		if ( srxDoc.isModified() ) {
			MessageBox dlg = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
			dlg.setText(shell.getText());
			dlg.setMessage(Res.getString("edit.confirmSave")); //$NON-NLS-1$
			switch ( dlg.open() ) {
			case SWT.CANCEL:
				return false;
			case SWT.YES:
				return saveSRXDocument(srxPath);
			}
		}
		return true;
	}

	public void callHelp () {
		if ( helpPath != null ) UIUtil.start(helpPath);
	}

	private void segmentTextFile () {
		try {
			// Get the input file
			FileProcessingDialog dlg = new FileProcessingDialog(shell, helpPath);
			String[] result = dlg.showDialog(testInputPath, testOutputPath, htmlOutput);
			if ( result == null ) return; // Canceled
			testInputPath = result[0];
			testOutputPath = result[1];
			htmlOutput = (result[2]!=null);

			// Process
			fileProc.process(testInputPath, testOutputPath, htmlOutput, segmenter);

			// Show the result
			UIUtil.start(testOutputPath);
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, e.getLocalizedMessage(), null);
		}
	}
	
}
