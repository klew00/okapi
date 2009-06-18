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

package net.sf.okapi.ui.common.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import net.sf.okapi.common.BaseContext;
import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.ui.Dialogs;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Okapi GUI tester
 * 
 * @version 0.1, 15.06.2009
 * @author Sergei Vasilyev
 */

public class Okapi_GUI_Tester extends ApplicationWindow {

	String[] GUI_CLASSES = new String[] {
			
			net.sf.okapi.ui.filters.plaintext.Editor.class.getName(),
			net.sf.okapi.steps.ui.textmodification.ParametersEditor.class.getName(),
			net.sf.okapi.ui.filters.openxml.Editor.class.getName()
			
//			, net.sf.okapi.ui.filters.table.Editor.class.getName()  // not yet
						
// Commented, because package names differ from those of projects, and java cannot locate the classes			
//			, net.sf.okapi.ui.filters.openoffice.Editor.class.getName()
//			, net.sf.okapi.ui.filters.po.Editor.class.getName()
//			, net.sf.okapi.ui.filters.properties.Editor.class.getName()
//			, net.sf.okapi.ui.filters.regex.Editor.class.getName()			
		};
	
	
	private Group grpParameters;
	private FormData formData_1;
	private Text text;
	private Button button_1;
	private FormData formData_3;
	private FormData formData_5;
	private Group grpParameterEditors;
	private List list;
	private Text text_1;
	private Button button_2;
	private IParameters params = null;
	private Button btnClear;
	private Button button;
	private FormData formData_4;
	
	/**
	 * Create the application window.
	 */
	public Okapi_GUI_Tester() {
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FormLayout());
		{
			grpParameters = new Group(container, SWT.NONE);
			grpParameters.setLayout(new FormLayout());
			grpParameters.setText("Parameters");
			{
				formData_1 = new FormData();
				formData_1.bottom = new FormAttachment(100, -4);
				formData_1.right = new FormAttachment(100, -17);
				formData_1.left = new FormAttachment(0, 14);
				formData_1.height = 94;
				formData_1.width = 449;
				grpParameters.setLayoutData(formData_1);
			}
			{
				text = new Text(grpParameters, SWT.BORDER);
				{
					formData_3 = new FormData();
					formData_3.left = new FormAttachment(0, 10);
					formData_3.right = new FormAttachment(100, -4);
					formData_3.top = new FormAttachment(0, 4);
					text.setLayoutData(formData_3);
				}
				text.setEditable(false);
			}
			{
				button_1 = new Button(grpParameters, SWT.NONE);
				button_1.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						
						String[] selected = Dialogs.browseFilenames(getShell(), "Open", false, null, 
								"Filter Parameters (*.fprm)\tAll Files (*.*)",
								"*.fprm\t*.*"); 
						
				        if (selected != null && selected.length > 0 && !Util.isEmpty(selected[0])) {
				        	text.setText(selected[0]);
				        	text_1.setText(fileAsString(selected[0]));
				        	text_1.setFocus();
				        }
					}
				});
				{
					formData_5 = new FormData();
					formData_5.right = new FormAttachment(text, 0, SWT.RIGHT);
					formData_5.width = 84;
					button_1.setLayoutData(formData_5);
				}
				button_1.setText("Open...");
			}
			{
				button = new Button(grpParameters, SWT.NONE);
				button.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						
						String selected = Dialogs.browseFilenamesForSave(getShell(), "Save As", text.getText(), 
								"Filter Parameters (*.fprm)\tAll Files (*.*)",
								"*.fprm\t*.*");
						
				        if (!Util.isEmpty(selected)) {
				        	
				        	text.setText(selected);
				        	
				        	if (params == null) {
				        		if (!Util.isEmpty(text_1.getText()))
				        			writeToFile(selected, text_1.getText());
				        	}
				        	else
				        		params.save(selected);
				        }
				        	
					}
				});
				{
					formData_4 = new FormData();
					formData_4.top = new FormAttachment(button_1, 4);
					formData_4.right = new FormAttachment(text, 0, SWT.RIGHT);
					formData_4.width = 84;
					button.setLayoutData(formData_4);
				}
				button.setText("Save As...");
			}
		}
		
		grpParameterEditors = new Group(container, SWT.NONE);
		formData_1.top = new FormAttachment(grpParameterEditors, 4);
		
		text_1 = new Text(grpParameters, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		FormData formData_7 = new FormData();
		formData_7.left = new FormAttachment(0, 10);
		formData_7.right = new FormAttachment(button_1, -4);
		formData_7.top = new FormAttachment(text, 4);
		formData_7.bottom = new FormAttachment(100, -4);
		text_1.setLayoutData(formData_7);
		grpParameterEditors.setLayout(new FormLayout());
		grpParameterEditors.setText("Registered Classes");
		FormData formData = new FormData();
		formData.right = new FormAttachment(grpParameters, 0, SWT.RIGHT);
		
		btnClear = new Button(grpParameters, SWT.NONE);
		formData_5.top = new FormAttachment(btnClear, 4);
		btnClear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				text.setText("");
				text_1.setText("");
			}
		});
		FormData formData_2 = new FormData();
		formData_2.top = new FormAttachment(text, 4);
		formData_2.right = new FormAttachment(text, 0, SWT.RIGHT);
		formData_2.width = 84;
		btnClear.setLayoutData(formData_2);
		btnClear.setText("Clear");
		formData.left = new FormAttachment(0, 14);
		formData.top = new FormAttachment(0, 4);
		formData.height = 207;
		formData.width = 531;
		grpParameterEditors.setLayoutData(formData);
		
		list = new List(grpParameterEditors, SWT.BORDER | SWT.V_SCROLL);
		list.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				if (list.getSelectionCount() > 0)
						displayEditor(list.getSelection()[0]);
			}
		});
		FormData formData_6 = new FormData();
		formData_6.bottom = new FormAttachment(100, -4);
		formData_6.top = new FormAttachment(0);
		formData_6.right = new FormAttachment(100, -91);
		formData_6.left = new FormAttachment(0, 4);
		list.setLayoutData(formData_6);
		
		list.setItems(GUI_CLASSES);
		list.select(0);
		
		button_2 = new Button(grpParameterEditors, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (list.getSelectionCount() > 0)
					displayEditor(list.getSelection()[0]);
			}
		});
		FormData formData_8 = new FormData();
		formData_8.left = new FormAttachment(list, 4);
		formData_8.right = new FormAttachment(100, -4);
		formData_8.top = new FormAttachment(0, 4);
		button_2.setLayoutData(formData_8);
		button_2.setText("Show...");

		getShell().pack();
		Rectangle Rect = getShell().getBounds();
		if (Rect.height < 600) Rect.height = 600; 
		getShell().setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(getShell(), null);

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		return menuManager;
	}

	/**
	 * Create the toolbar manager.
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		statusLineManager.setMessage(null, "");
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Okapi_GUI_Tester window = new Okapi_GUI_Tester();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
				
		URL url = Okapi_GUI_Tester.class.getResource("Rainbow.png");
		if (url == null) return;
		
		String root = Util.getDirectoryName(url.getPath());		
		newShell.setImage(new Image(Display.getCurrent(), root + "/Rainbow.png"));
		
		newShell.setText("Okapi GUI Tester");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(647, 596);
	}

	@SuppressWarnings("unchecked")
	private void displayEditor(String editorClass) {
		
		if (Util.isEmpty(editorClass)) return;
		
		Class c = null;
		try {
			c = Class.forName(editorClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		IParametersEditor editor = null;
		try {
			editor = (IParametersEditor) c.newInstance();
			
		} catch (InstantiationException e) {
			
			e.printStackTrace();
			
		} catch (IllegalAccessException e) {
			
			e.printStackTrace();
		}		
		IContext context = new BaseContext();
		context.setObject("shell", getShell());
		
		params = editor.createParameters();
		if (!Util.isEmpty(text.getText()))
			params.load(Util.toURI(text.getText()), true);
		
		if (editor.edit(params, context)) {
			
			text_1.setText(params.toString());
		}
		else
			params = null;
	}
	
	private String fileAsString(String fileName) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		StringBuilder tmp = new StringBuilder();
		char[] buf = new char[2048];
		int count = 0;
		try {
			while (( count = reader.read(buf)) != -1 ) {
				tmp.append(buf, 0, count);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        return tmp.toString();
    }
	
	private boolean writeToFile(String fileName, String st) {
		
		try {
	        BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
	        out.write(st);
	        out.close();
	        
	    } catch (IOException e) {
	    	
	    	return false;
	    }

		return true;
		
	}
}

