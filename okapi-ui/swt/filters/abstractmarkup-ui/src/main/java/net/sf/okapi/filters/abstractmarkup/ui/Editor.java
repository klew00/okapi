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

import java.util.Map;

import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.common.ui.UIUtil;
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
	
	private Shell shell;
	private boolean result = false;
	private AbstractMarkupParameters params;
	private IHelp help;
	private List lbAtt;
	private Table tblAttRules;
	private Button rdAttrAllElements;
	private Button rdAttOnlyThese;
	private Button rdAttExceptThese;
	private Text edAttScopeElements;
	private Attribute currentAtt;

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
		shell.setText("AbstractMarkup Filter Parameters");
		if ( parent != null ) UIUtil.inheritIcon(shell, parent);
		GridLayout layTmp = new GridLayout();
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		TabFolder tfTmp = new TabFolder(shell, SWT.NONE);
		GridData gdTmp = new GridData(GridData.FILL_BOTH);
		tfTmp.setLayoutData(gdTmp);

		//--- General tab
		
		Composite cmpTmp = new Composite(tfTmp, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		

		TabItem tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("General");
		tiTmp.setControl(cmpTmp);
		
		
		//--- Elements tab
		
		cmpTmp = new Composite(tfTmp, SWT.NONE);
		layTmp = new GridLayout();
		cmpTmp.setLayout(layTmp);
		

		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Elements");
		tiTmp.setControl(cmpTmp);
		
		
		//--- Attributes tab
		
		cmpTmp = new Composite(tfTmp, SWT.NONE);
		layTmp = new GridLayout(2, false);
		cmpTmp.setLayout(layTmp);
		
		Label label = new Label(cmpTmp, SWT.NONE);
		label.setText("Attributes:");
		
		label = new Label(cmpTmp, SWT.NONE);
		label.setText("Rules for the selected attribute:");
		
		lbAtt = new List(cmpTmp, SWT.BORDER | SWT.V_SCROLL);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.verticalSpan = 2;
		gdTmp.widthHint = 130;
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
		item.setText(0, "Translatable text"); item.setText(1, RULE_TYPE.ATTRIBUTE_TRANS.toString());
		item = new TableItem(tblAttRules, SWT.NONE);
		item.setText(0, "Modifiable property"); item.setText(1, RULE_TYPE.ATTRIBUTE_WRITABLE.toString());
		item = new TableItem(tblAttRules, SWT.NONE);
		item.setText(0, "Read-only property"); item.setText(1, RULE_TYPE.ATTRIBUTE_READONLY.toString());
		item = new TableItem(tblAttRules, SWT.NONE);
		item.setText(0, "Identifier"); item.setText(1, RULE_TYPE.ATTRIBUTE_ID.toString());
		item = new TableItem(tblAttRules, SWT.NONE);
		item.setText(0, "Preserve element's whitespaces"); item.setText(1, RULE_TYPE.ATTRIBUTE_PRESERVE_WHITESPACE.toString());
		col1.pack();
		col2.pack();

		Group grpScope = new Group(cmpTmp, SWT.NONE);
		grpScope.setLayout(new GridLayout());
		grpScope.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));
		grpScope.setText("Scope:");
		
		rdAttrAllElements = new Button(grpScope, SWT.RADIO); 
		rdAttrAllElements.setText("Applies to all elements");
		rdAttrAllElements.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateScopeElements();
			};
		});
		
		rdAttOnlyThese = new Button(grpScope, SWT.RADIO);
		rdAttOnlyThese.setText("Apply only for the following elements:");
		rdAttOnlyThese.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateScopeElements();
			};
		});
		
		rdAttExceptThese = new Button(grpScope, SWT.RADIO);
		rdAttExceptThese.setText("Apply to all elements excepted for the following ones:");
		rdAttExceptThese.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateScopeElements();
			};
		});
		
		edAttScopeElements = new Text(grpScope, SWT.BORDER);
		edAttScopeElements.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		tiTmp = new TabItem(tfTmp, SWT.NONE);
		tiTmp.setText("Attributes");
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
		currentAtt.scopeElements = edAttScopeElements.getText();
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
			updateScopeElements();
			currentAtt = att;
		}
		return true;
	}
	
	private boolean showDialog () {
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}
		return result;
	}
	
	private void setData () {
		TaggedFilterConfiguration tfg = params.getTaggedConfig();

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
					att.scopeElements = items.get(itemName).toString();
					if ( att.scopeElements.length() > 2 ) {
						att.scopeElements = att.scopeElements.substring(1, att.scopeElements.length()-1);
					}
				}
				else if ( itemName.equals(TaggedFilterConfiguration.ONLY_THESE_ELEMENTS) ) {
					att.scope = Attribute.SCOPE_ONLY;
					att.scopeElements = items.get(itemName).toString();
					if ( att.scopeElements.length() > 2 ) {
						att.scopeElements = att.scopeElements.substring(1, att.scopeElements.length()-1);
					}
				}
			}
		
			lbAtt.add(attName);
			lbAtt.setData(attName, att);

		}
	}
	
	private boolean saveData () {
		return true;
	}
	
}
