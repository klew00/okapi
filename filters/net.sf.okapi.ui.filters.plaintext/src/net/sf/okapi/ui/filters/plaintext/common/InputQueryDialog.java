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

package net.sf.okapi.ui.filters.plaintext.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * 
 * @version 0.1, 23.06.2009
 * @author Sergei Vasilyev
 */

public class InputQueryDialog {

	private Shell shell;
	private boolean result = true;
	private OKCancelPanel pnlActions;
	private String caption;
	private String prompt;
	private Object data = null;
	private IHelp help;
	private Class<?> pageClass;
	private IInputQueryPage page; 
	
	public boolean run(Shell parent, Class<?> pageClass, String caption, String prompt, Object initialData, IHelp help) {
		
		if (!IInputQueryPage.class.isAssignableFrom(pageClass)) return false;
		if (!Composite.class.isAssignableFrom(pageClass)) return false;
		
		try {
			this.pageClass = pageClass;
			this.caption = caption;
			this.prompt = prompt;
			this.data = initialData;
			this.help = help;
			
			create(parent);			
			if (!result) return  false;
			
			showDialog();			
			if (!result) return  false;
		}
		finally {
			
			if (shell != null) shell.dispose();
		}
		
		return result;				
	}
	
	@SuppressWarnings("unchecked")
	private void create (Shell p_Parent) {
		
		
		String[] ss = null;
		run(p_Parent, InputQueryDialog.class, null, null, ss, null);
		
		try {			
			shell = new Shell(p_Parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			shell.setText(caption);
			
			//if ( p_Parent != null ) shell.setImage(p_Parent.getImage());
			
			GridLayout layTmp = new GridLayout();		
			layTmp.marginBottom = 0;
			layTmp.verticalSpacing = 0;
			shell.setLayout(layTmp);
			
			Constructor<Composite> cc = (Constructor<Composite>) pageClass.getConstructor(new Class[] {Composite.class, int.class});
			if (cc == null) return;
			
			page = (IInputQueryPage) cc.newInstance(new Object[] {shell, SWT.BORDER});
			if (page == null) return;
			
		} catch (InstantiationException e) {
			
			result = false;
			return;
			
		} catch (IllegalAccessException e) {
			
			result = false;
			return;
			
		} catch (SecurityException e) {
			
			result = false;
			return;
			
		} catch (NoSuchMethodException e) {
			
			result = false;
			return;
			
		} catch (IllegalArgumentException e) {
			
			result = false;
			return;
			
		} catch (InvocationTargetException e) {
			
			result = false;
			return;
		}		

		((Composite) page).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		page.setPrompt(prompt);
		
		if (!result) return;
		
		page.load(data);
		page.interop();
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				if ( e.widget.getData().equals("h") ) {  // Help
					if ( help != null ) help.showTopic(this, "index");
					return;
				} 
				else if ( e.widget.getData().equals("o") ) { // OK
					
					result = page.save(data);
				}
				else {  // Cancel
					result = false;
				}
				
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btOK);

		shell.pack();
		Rectangle Rect = shell.getBounds();
		shell.setMinimumSize(Rect.width, Rect.height);
		Dialogs.centerWindow(shell, p_Parent);
	}
	
	private void showDialog () {
		
		if (!result) return;
		
		result = false; // To react to OK only
		shell.open();
		while ( !shell.isDisposed() ) {
			
			try {
				if ( !shell.getDisplay().readAndDispatch() )
					shell.getDisplay().sleep();
			}
			catch ( Exception E ) {
				Dialogs.showError(shell, E.getLocalizedMessage(), null);
			}
		}				
	}
	
	public Object getResult() {
		
		return data;
	}
}

