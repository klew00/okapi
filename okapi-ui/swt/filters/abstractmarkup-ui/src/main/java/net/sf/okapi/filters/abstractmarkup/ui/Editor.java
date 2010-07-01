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

package net.sf.okapi.filters.abstractmarkup.ui;

import java.util.ArrayList;
import java.util.Map;

import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.InputDialog;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
import net.sf.okapi.common.ui.filters.InlineCodeFinderPanel;
import net.sf.okapi.filters.abstractmarkup.AbstractMarkupParameters;
import net.sf.okapi.filters.yaml.TaggedFilterConfiguration;
import net.sf.okapi.filters.yaml.TaggedFilterConfiguration.RULE_TYPE;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

@EditorFor(AbstractMarkupParameters.class)
public class Editor implements IParametersEditor {

	private static final int ATTRULES_TRANS = 0;
	private static final int ATTRULES_WRITABLE = 1;
	private static final int ATTRULES_READONLY = 2;
	private static final int ATTRULES_ID = 3;
	private static final int ATTRULES_PRESERVE_WHITESPACE = 4;
	
	private static final int TAB_ATTRIBUTES = 1;
	
	private Shell shell;
	private boolean result = false;
	private AbstractMarkupParameters params;
	private TabFolder tabs;
	private IHelp help;
	private Button chkWellformed;
	private Button chkGlobalPreserveWS;
	private List lbAtt;
	private Table tblAttRules;
	private Button rdAttrAllElements;
	private Button rdAttOnlyThese;
	private Button rdAttExceptThese;
	private Text edAttScopeElements;
	private Attribute currentAtt;
	private Button btRemoveAtt;
	private Group grpWS;
	private Group grpAttCond;
	private Text edAttConditions;
	private Text edPreserveWS;
	private Text edDefaultWS;
	private Button chkUseCodeFinder;
	private InlineCodeFinderPanel pnlCodeFinder;

	public boolean edit (IParameters options,
		boolean readOnly,
		IContext context)
	{
		help = (IHelp)context.getObject("help");
		boolean bRes = false;
		shell = null;
		params = (AbstractMarkupParameters)options;
		try {
			shell = new Shell((Shell)context.getObject("shell"), SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
			create((Shell)context.getObject("shell"), readOnly);
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
		return new AbstractMarkupParameters();
	}
	
	private void create (Shell parent,
		boolean readOnly)
	{
		shell.setText(params.getEditorTitle());
		if ( parent != null ) UIUtil.inheritIcon(shell, parent);
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		tabs = new TabFolder(shell, SWT.NONE);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		tabs.setLayoutData(gdTmp);

		//=== Elements tab
		
		Composite cmpTmp = new Composite(tabs, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		

		TabItem tiTmp = new TabItem(tabs, SWT.NONE);
		tiTmp.setText("Elements");
		tiTmp.setControl(cmpTmp);
		
		
		//=== Attributes tab
		
		cmpTmp = new Composite(tabs, SWT.NONE);
		layTmp = new GridLayout(2, false);
		cmpTmp.setLayout(layTmp);
		
		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText("Attributes:");
		
		label = new Label(cmpTmp, SWT.NONE);
		label.setText("Rules for the selected attribute:");
		
		lbAtt = new List(cmpTmp, SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.verticalSpan = 4;
		lbAtt.setLayoutData(gdTmp);
		lbAtt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateAttribute();
			};
		});

		tblAttRules = new Table(cmpTmp, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		tblAttRules.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		TableColumn col1 = new TableColumn(tblAttRules, SWT.NONE);
		TableColumn col2 = new TableColumn(tblAttRules, SWT.NONE);
		TableItem item = new TableItem(tblAttRules, SWT.NONE);
		item.setData(ATTRULES_TRANS);
		item.setText(0, "Translatable text"); item.setText(1, RULE_TYPE.ATTRIBUTE_TRANS.toString());
		item = new TableItem(tblAttRules, SWT.NONE);
		item.setData(ATTRULES_WRITABLE);
		item.setText(0, "Modifiable property"); item.setText(1, RULE_TYPE.ATTRIBUTE_WRITABLE.toString());
		item = new TableItem(tblAttRules, SWT.NONE);
		item.setData(ATTRULES_READONLY);
		item.setText(0, "Read-only property"); item.setText(1, RULE_TYPE.ATTRIBUTE_READONLY.toString());
		item = new TableItem(tblAttRules, SWT.NONE);
		item.setData(ATTRULES_ID);
		item.setText(0, "Identifier"); item.setText(1, RULE_TYPE.ATTRIBUTE_ID.toString());
		item = new TableItem(tblAttRules, SWT.NONE);
		item.setData(ATTRULES_PRESERVE_WHITESPACE);
		item.setText(0, "Preserve element's whitespaces"); item.setText(1, RULE_TYPE.ATTRIBUTE_PRESERVE_WHITESPACE.toString());
		col1.pack();
		col2.pack();
		tblAttRules.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if ( event.detail == SWT.CHECK ) {
					updateAttributeRules((TableItem)event.item);
				}
            }
		});
		
		//--- Conditions group
		
		grpAttCond = new Group(cmpTmp, SWT.NONE);
		grpAttCond.setLayout(new GridLayout(3, false));
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		grpAttCond.setLayoutData(gdTmp);
		grpAttCond.setText("Conditions");
		
		edAttConditions = new Text(grpAttCond, SWT.BORDER);
		edAttConditions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edAttConditions.setEditable(false);
		
		Button btTmp = new Button(grpAttCond, SWT.PUSH);
		btTmp.setText("Edit...");
		btTmp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if ( currentAtt == null ) return;
				if ( currentAtt.conditions == null ) {
					currentAtt.conditions = new ArrayList<Condition>();
				}
				editConditions(edAttConditions, currentAtt.conditions);
            }
		});

		//--- Scope group
		
		Group grpTmp = new Group(cmpTmp, SWT.NONE);
		grpTmp.setLayout(new GridLayout());
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		grpTmp.setLayoutData(gdTmp);
		grpTmp.setText("Scope");
		
		rdAttrAllElements = new Button(grpTmp, SWT.RADIO); 
		rdAttrAllElements.setText("Applies to all elements");
		rdAttrAllElements.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateScopeElements();
			};
		});
		
		rdAttOnlyThese = new Button(grpTmp, SWT.RADIO);
		rdAttOnlyThese.setText("Apply only for the following elements:");
		rdAttOnlyThese.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateScopeElements();
			};
		});
		
		rdAttExceptThese = new Button(grpTmp, SWT.RADIO);
		rdAttExceptThese.setText("Apply to all elements excepted for the following ones:");
		rdAttExceptThese.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateScopeElements();
			};
		});
		
		edAttScopeElements = new Text(grpTmp, SWT.BORDER);
		edAttScopeElements.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//--- White spaces group
		
		grpWS = new Group(cmpTmp, SWT.NONE);
		grpWS.setLayout(new GridLayout(3, false));
		gdTmp = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		gdTmp.verticalSpan = 2;
		grpWS.setLayoutData(gdTmp);
		grpWS.setText("White spaces");
		
		Label stTmp = new Label(grpWS, SWT.NONE);
		stTmp.setText("Preserve:");
		
		edPreserveWS = new Text(grpWS, SWT.BORDER);
		edPreserveWS.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edPreserveWS.setEditable(false);
		
		btTmp = new Button(grpWS, SWT.PUSH);
		btTmp.setText("Edit...");
		btTmp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if ( currentAtt == null ) return;
				if ( currentAtt.conditions == null ) {
					currentAtt.wsPreserve = new ArrayList<Condition>();
				}
				editConditions(edPreserveWS, currentAtt.wsPreserve);
            }
		});
		
		stTmp = new Label(grpWS, SWT.NONE);
		stTmp.setText("Default:");
		
		edDefaultWS = new Text(grpWS, SWT.BORDER);
		edDefaultWS.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edDefaultWS.setEditable(false);
		
		btTmp = new Button(grpWS, SWT.PUSH);
		btTmp.setText("Edit...");
		btTmp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if ( currentAtt == null ) return;
				if ( currentAtt.conditions == null ) {
					currentAtt.wsDefault = new ArrayList<Condition>();
				}
				editConditions(edDefaultWS, currentAtt.wsDefault);
            }
		});
		
		//--- Add/Remove buttons for the list of attributes
		
		Composite cmpButtons = new Composite(cmpTmp, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = layout.marginWidth = 0;
		cmpButtons.setLayout(layout);
		cmpButtons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END));
		
		Button btAdd = UIUtil.createGridButton(cmpButtons, SWT.PUSH, "Add...", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				addAttribute();
            }
		});
		
		btRemoveAtt = UIUtil.createGridButton(cmpButtons, SWT.PUSH, "Remove", UIUtil.BUTTON_DEFAULT_WIDTH, 1);
		btRemoveAtt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				removeAttribute();
            }
		});
		
		tiTmp = new TabItem(tabs, SWT.NONE);
		tiTmp.setText("Attributes");
		tiTmp.setControl(cmpTmp);
		

		//=== Inline codes tab
		
		cmpTmp = new Composite(tabs, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		
		chkUseCodeFinder = new Button(cmpTmp, SWT.CHECK);
		chkUseCodeFinder.setText("Has inline codes as defined below:");
		chkUseCodeFinder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateInlineCodes();
			};
		});
		
		pnlCodeFinder = new InlineCodeFinderPanel(cmpTmp, SWT.NONE);
		pnlCodeFinder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		tiTmp = new TabItem(tabs, SWT.NONE);
		tiTmp.setText("Inline Codes");
		tiTmp.setControl(cmpTmp);
		
		
		//=== General tab
		
		cmpTmp = new Composite(tabs, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);

		chkWellformed = new Button(cmpTmp, SWT.CHECK);
		chkWellformed.setText("Assumes the documents are well-formed");
		gdTmp = new GridData();
		gdTmp.verticalIndent = 16;
		chkWellformed.setLayoutData(gdTmp);

		chkGlobalPreserveWS = new Button(cmpTmp, SWT.CHECK);
		chkGlobalPreserveWS.setText("Preserve white-spaces unless otherwise specified");
		
		tiTmp = new TabItem(tabs, SWT.NONE);
		tiTmp.setText("General");
		tiTmp.setControl(cmpTmp);
		
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = false;
				if ( e.widget.getData().equals("h") ) {
					if ( help != null ) help.showTopic(this, "index");
					return;
				}
				if ( e.widget.getData().equals("o") ) {
					if ( !saveData() ) return;
					result = true;
				}
				shell.close();
			};
		};
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, (help!=null));
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
		if ( startSize.x < 400 ) startSize.x = 400; 
		if ( startSize.y < 300 ) startSize.y = 300; 
		shell.setSize(startSize);
		Dialogs.centerWindow(shell, parent);
		setData();
	}
	
	private void removeAttribute () {
		int n = lbAtt.getSelectionIndex();
		if ( n < 0 ) return;
		lbAtt.remove(n);
		if ( n >= lbAtt.getItemCount() ) n = lbAtt.getItemCount()-1;
		if ( n > -1 ) lbAtt.setSelection(n);
		updateAttributesButtons();
		updateAttribute();
	}
	
	private void addAttribute () {
		try {
			InputDialog dlg = new InputDialog(shell, "Add Attribute", "Name of the attribute to add:", null, null, 0, -1, -1);
			String name = dlg.showDialog();
			if ( name == null ) return;
			name = ensureValidName(name);
			if ( name.isEmpty() ) return;
			
			for ( String tmp : lbAtt.getItems() ) {
				if ( tmp.equals(name) ) {
					Dialogs.showError(shell,
						String.format("The attribute \"%s\" is already listed.", name), null);
					return;
				}
			}
			// Else: add the attribute
			Attribute att = new Attribute();
			att.name = name;
			lbAtt.add(name);
			lbAtt.setData(name, att);
			lbAtt.select(lbAtt.getItemCount()-1);
			updateAttributesButtons();
			updateAttribute();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error when adding an attribute.\n"+e.getMessage(), null);
		}
	}
	
	private void updateInlineCodes () {
		pnlCodeFinder.setEnabled(chkUseCodeFinder.getSelection());
	}

	private void updateAttributesButtons () {
		btRemoveAtt.setEnabled(lbAtt.getItemCount()>0);
	}
	
	// Actions to do when a rule type is being checked or un-checked
	private void updateAttributeRules (TableItem item) {
		// Treat case for preserve white-spaces first
		if ( (Integer)item.getData() == ATTRULES_PRESERVE_WHITESPACE ) {
			updateWhiteSpaces();
			return;
		}
		// Other cases are affected only when checking the option
		if ( item.getChecked() ) {
			switch ( (Integer)item.getData() ) {
			case ATTRULES_TRANS:
				tblAttRules.getItem(ATTRULES_WRITABLE).setChecked(false);
				tblAttRules.getItem(ATTRULES_READONLY).setChecked(false);
				break;
			case ATTRULES_WRITABLE:
				tblAttRules.getItem(ATTRULES_TRANS).setChecked(false);
				tblAttRules.getItem(ATTRULES_READONLY).setChecked(false);
				break;
			case ATTRULES_READONLY:
				tblAttRules.getItem(ATTRULES_WRITABLE).setChecked(false);
				tblAttRules.getItem(ATTRULES_TRANS).setChecked(false);
				break;
			}
		}
	}
	
	private void updateWhiteSpaces () {
		boolean enabled = tblAttRules.getItem(ATTRULES_PRESERVE_WHITESPACE).getChecked();
		grpWS.setEnabled(enabled);
		for ( Control ctrl : grpWS.getChildren() ) {
			ctrl.setEnabled(enabled);
		}
	}
	
	private void updateScopeElements () {
		edAttScopeElements.setEnabled(!rdAttrAllElements.getSelection());
	}

	private boolean saveAttribute () {
		if ( currentAtt == null ) return true;
		currentAtt.rules.clear();
		if ( tblAttRules.getItem(ATTRULES_TRANS).getChecked() ) currentAtt.rules.add(RULE_TYPE.ATTRIBUTE_TRANS);
		if ( tblAttRules.getItem(ATTRULES_WRITABLE).getChecked() ) currentAtt.rules.add(RULE_TYPE.ATTRIBUTE_WRITABLE);
		if ( tblAttRules.getItem(ATTRULES_READONLY).getChecked() ) currentAtt.rules.add(RULE_TYPE.ATTRIBUTE_READONLY);
		if ( tblAttRules.getItem(ATTRULES_ID).getChecked() ) currentAtt.rules.add(RULE_TYPE.ATTRIBUTE_ID);
		if ( tblAttRules.getItem(ATTRULES_PRESERVE_WHITESPACE).getChecked() ) currentAtt.rules.add(RULE_TYPE.ATTRIBUTE_PRESERVE_WHITESPACE);
		// Scope
		if ( rdAttExceptThese.getSelection() ) currentAtt.scope = Attribute.SCOPE_ALLEXCEPT;
		else if ( rdAttOnlyThese.getSelection() ) currentAtt.scope = Attribute.SCOPE_ONLY;
		else currentAtt.scope = Attribute.SCOPE_ALL;
		currentAtt.scopeElements = edAttScopeElements.getText().trim();
		return true;
	}
	
	private boolean updateAttribute () {
		if ( !saveAttribute() ) {
			
			return false;
		}
		int n = lbAtt.getSelectionIndex();
		if ( n < 0 ) {
			for ( TableItem item : tblAttRules.getItems() ) {
				item.setChecked(false);
			}
			rdAttrAllElements.setSelection(true);
			rdAttExceptThese.setSelection(false);
			rdAttOnlyThese.setSelection(false);
			edAttScopeElements.setText("");
			edAttConditions.setText("");
			edPreserveWS.setText("");
			edDefaultWS.setText("");
			currentAtt = null;
		}
		else {
			Attribute att = (Attribute)lbAtt.getData(lbAtt.getItem(n));
			tblAttRules.getItem(ATTRULES_TRANS).setChecked(att.rules.contains(RULE_TYPE.ATTRIBUTE_TRANS));
			tblAttRules.getItem(ATTRULES_WRITABLE).setChecked(att.rules.contains(RULE_TYPE.ATTRIBUTE_WRITABLE));
			tblAttRules.getItem(ATTRULES_READONLY).setChecked(att.rules.contains(RULE_TYPE.ATTRIBUTE_READONLY));
			tblAttRules.getItem(ATTRULES_ID).setChecked(att.rules.contains(RULE_TYPE.ATTRIBUTE_ID));
			tblAttRules.getItem(ATTRULES_PRESERVE_WHITESPACE).setChecked(att.rules.contains(RULE_TYPE.ATTRIBUTE_PRESERVE_WHITESPACE));
			rdAttrAllElements.setSelection(att.scope==Attribute.SCOPE_ALL);
			rdAttExceptThese.setSelection(att.scope==Attribute.SCOPE_ALLEXCEPT);
			rdAttOnlyThese.setSelection(att.scope==Attribute.SCOPE_ONLY);
			edAttScopeElements.setText(att.scopeElements);
			edAttConditions.setText(formatConditions(att.conditions));
			edPreserveWS.setText(formatConditions(att.wsPreserve));
			edDefaultWS.setText(formatConditions(att.wsDefault));
			updateWhiteSpaces();
			currentAtt = att;
		}
		updateScopeElements();
		updateWhiteSpaces();
		return true;
	}
	
	// Formats a list of conditions for display
	private String formatConditions (java.util.List<Condition> list) {
		if ( Util.isEmpty(list) ) {
			return "";
		}
		return list.toString();
	}

	private boolean showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}
	
	private boolean validate () {
		saveAttribute();
		for ( int i=0; i<lbAtt.getItemCount(); i++ ) {
			String name = lbAtt.getItem(i);
			if ( !name.equals(ensureValidName(name)) ) {
				Dialogs.showError(shell,
					String.format("The attribute name \"%s\" is invalid.", lbAtt.getItem(i)), null);
				tabs.setSelection(TAB_ATTRIBUTES);
				return false;
			}
			Attribute att = (Attribute)lbAtt.getData(name);
			// We need at least one rule
			if ( att.rules.isEmpty() ) {
				Dialogs.showError(shell,
					String.format("The attribute \"%s\" has no rule defined.", name), null);
				tabs.setSelection(TAB_ATTRIBUTES);
				return false;
			}
			// Check the scope
			if (( att.scope != Attribute.SCOPE_ALL ) && att.scopeElements.isEmpty() ) {
				Dialogs.showError(shell,
					String.format("The attribute \"%s\" has no elements defined for its scope.", name), null);
				tabs.setSelection(TAB_ATTRIBUTES);
				return false;
			}
		}
		
		// check inline codes
		String tmp = pnlCodeFinder.getRules();
		if ( tmp == null ) return false;

		return true;
	}
	
	private void setData () {
		try {
			TaggedFilterConfiguration tfg = params.getTaggedConfig();

			chkWellformed.setSelection(tfg.isWellformed());
			chkGlobalPreserveWS.setSelection(tfg.isGlobalPreserveWhitespace());
			
			chkUseCodeFinder.setSelection(tfg.isUseCodeFinder());
			pnlCodeFinder.setRules(tfg.getCodeFinderRules());
			
			//--- Read the attributes
			
			Map<String, Object> map = tfg.getAttributeRules();
			for ( String attName : map.keySet() ) {
				Attribute att = new Attribute();
				att.name = attName;
				@SuppressWarnings("unchecked")
				Map<String, Object> items = (Map<String, Object>)map.get(attName);
				for ( String itemName : items.keySet() ) {
					// Get the list of ruleTypes
					if ( itemName.equals(TaggedFilterConfiguration.RULETYPES) ) {
						@SuppressWarnings("unchecked")
						java.util.List<String> list = (java.util.List<String>)items.get(itemName);
						for ( String tmp : list ) {
							att.rules.add(tfg.convertRuleAsStringToRuleType(tmp));
						}
					}
					else if ( itemName.equals(TaggedFilterConfiguration.ALL_ELEMENTS_EXCEPT) ) {
						att.scope = Attribute.SCOPE_ALLEXCEPT;
						att.scopeElements = makeStringList(items.get(itemName).toString());
					}
					else if ( itemName.equals(TaggedFilterConfiguration.ONLY_THESE_ELEMENTS) ) {
						att.scope = Attribute.SCOPE_ONLY;
						att.scopeElements = makeStringList(items.get(itemName).toString());
					}
					else if ( itemName.equals(TaggedFilterConfiguration.PRESERVE_CONDITION) ) {
						att.wsPreserve = parseConditions(items.get(itemName));
					}
					else if ( itemName.equals(TaggedFilterConfiguration.DEFAULT_CONDITION) ) {
						att.wsDefault = parseConditions(items.get(itemName));
					}
					else if ( itemName.equals(TaggedFilterConfiguration.CONDITIONS) ) {
						att.conditions = parseConditions(items.get(itemName));
					}
				}
				// Attribute is read, add it
				lbAtt.add(attName);
				lbAtt.setData(attName, att);
			}
			// Select default and update all
			if ( lbAtt.getItemCount() > 0 ) lbAtt.setSelection(0);
			updateAttribute();
			updateAttributesButtons();
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error when loading the configuration.\n"+e.getMessage(), null);
		}
	}
	
	private java.util.List<Condition> parseConditions (Object rawObject) {
		java.util.List<Condition> list = new ArrayList<Condition>(1);
		// The conditions entry is either a single condition statement or a list of conditions
		@SuppressWarnings("unchecked")
		java.util.List<Object> objs = (java.util.List<Object>)rawObject;
		if ( objs.get(0) instanceof String ) { // This is a condition statement
			parseCondition(list, objs);
		}
		else { // Otherwise it has to be a list of conditions
			for ( Object obj : objs ) {
				parseCondition(list, obj);
			}
		}
		return list;
	}
	
	private void parseCondition (java.util.List<Condition> conditions,
		Object rawObject)
	{
		// We should have three objects: name, operator, value(s)
		@SuppressWarnings("unchecked")
		java.util.List<Object> objs = (java.util.List<Object>)rawObject;
		Condition condition = new Condition();
		condition.part1 = (String)objs.get(0);
		condition.operator = (String)objs.get(1);
		// The value(s) can be one string or a list of strings
		if ( objs.get(2) instanceof String ) {
			condition.part2 = (String)objs.get(2);
		}
		else { // List of values
			condition.part2 = makeStringList(objs.get(2).toString());
		}
		// Add the condition
		conditions.add(condition);
	}

	// Converts a YAML representation of a list, or a string into a simple list
	private String makeStringList (String yamlList) {
		String res = yamlList.trim();
		// If it's a list: remove the brackets
		if (( res.length() > 2 ) && ( res.charAt(0) == '[' )) {
			res = res.substring(1, res.length()-1);
		}
		return res.trim();
	}
	
	private String ensureValidName (String name) {
		name = name.toLowerCase().trim();
		name = name.replaceAll("\\s", "");
		return name;
	}
	
	private boolean saveData () {
		if ( !validate() ) return false;
		
		StringBuilder tmp = new StringBuilder();
		
		//--- General
		tmp.append(String.format("%s: %s\n",
			TaggedFilterConfiguration.WELLFORMED,
			chkWellformed.getSelection()));
		tmp.append(String.format("%s: %s\n",
			TaggedFilterConfiguration.GLOBAL_PRESERVE_WHITESPACE,
			chkGlobalPreserveWS.getSelection()));
		
		//--- Inline codes
		tmp.append(String.format("\n%s: %s\n",
			TaggedFilterConfiguration.USECODEFINDER,
			chkUseCodeFinder.getSelection()));
		String rules = pnlCodeFinder.getRules().replace("\\", "\\\\");
		rules = rules.replace("\n", "\\n");
		tmp.append(String.format("%s: %s\n",
			TaggedFilterConfiguration.CODEFINDERRULES,
			"\""+rules+"\""));
		
		//--- Attribute
		tmp.append("\nattributes:\n");
		for ( int i=0; i<lbAtt.getItemCount(); i++ ) {
			Attribute att = (Attribute)lbAtt.getData(lbAtt.getItem(i));
			tmp.append("  '"+att.name+"':\n    ruleTypes: ");
			tmp.append(att.rules.toString());
			// White-spaces
			if ( att.rules.contains(RULE_TYPE.ATTRIBUTE_PRESERVE_WHITESPACE) ) {
				if ( !Util.isEmpty(att.wsPreserve) ) {
					tmp.append(String.format("\n    %s: ", TaggedFilterConfiguration.PRESERVE_CONDITION));
					tmp.append(att.wsPreserve.toString());
				}
				if ( !Util.isEmpty(att.wsDefault) ) {
					tmp.append(String.format("\n    %s: ", TaggedFilterConfiguration.DEFAULT_CONDITION));
					tmp.append(att.wsDefault.toString());
				}
			}
			// Conditions
			if ( !Util.isEmpty(att.conditions) ) {
				tmp.append(String.format("\n    %s: ", TaggedFilterConfiguration.CONDITIONS));
				tmp.append(att.conditions.toString());
			}
			tmp.append("\n");
		}
		
System.out.print(tmp.toString());		
System.out.print("\n---\n");		
		params.fromString(tmp.toString());
System.out.print(params.toString());		
		return true;
	}

	private void editConditions (Text ctrlDisplay,
		java.util.List<Condition> conditions)
	{
		try {
			ConditionsDialog dlg = new ConditionsDialog(shell, null, conditions);
			if ( !dlg.showDialog() ) return;
			// Else: update the display
			ctrlDisplay.setText(formatConditions(conditions));
		}
		catch ( Throwable e ) {
			Dialogs.showError(shell, "Error when editing conditions.\n"+e.getMessage(), null);
		}
	}

}
