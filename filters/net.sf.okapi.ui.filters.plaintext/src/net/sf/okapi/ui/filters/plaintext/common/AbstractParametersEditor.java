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
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.IContext;
import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.IParametersEditor;
import net.sf.okapi.common.ui.Dialogs;
import net.sf.okapi.common.ui.OKCancelPanel;
import net.sf.okapi.filters.plaintext.common.CompoundParameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * Abstract base class for parameters editors 
 * 
 * @version 0.1, 12.06.2009
 * @author Sergei Vasilyev
 */

public abstract class AbstractParametersEditor implements IParametersEditor {

	private Shell shell;
//	private Shell parent;
	private boolean result = true;
	private OKCancelPanel pnlActions;
	private IParameters params;
	private IHelp help;
	private TabFolder pageContainer;
	private List<IParametersEditorPage> pages = null;
	
	public boolean edit(IParameters paramsObject, boolean readOnly, IContext context) {
	
		result = true;
		if (context == null) return false;
		if (paramsObject == null) return false;
		
		Shell parent = (Shell)context.getObject("shell"); 

		try {			
			if (pages == null) 
				pages = new ArrayList<IParametersEditorPage>();
			else
				pages.clear();
							
			help = (IHelp)context.getObject("help");
	
			shell = null;
			params = paramsObject;
													
			shell = new Shell(parent, SWT.CLOSE | SWT.TITLE | SWT.RESIZE /*| SWT.MAX */ | SWT.APPLICATION_MODAL);
//			shell.addListener(SWT.Traverse, new Listener() {
//				public void handleEvent(Event event) {
//					if (event.detail == SWT.TRAVERSE_ESCAPE) {
//						event.doit = true;
//						result = false;
//					}
//				}});
			create(parent, readOnly);			
			if (!result) return  false;
			
			showDialog();			
			if (!result) return  false;			
		}
		catch ( Exception E ) {
			Dialogs.showError(parent, E.getLocalizedMessage(), null);
			result = false;
		}
		finally {
			// Dispose of the shell, but not of the display
			if (shell != null) shell.dispose();
		}
		return result;
	}

	private void create (Shell p_Parent,
		boolean readOnly)
	{
		shell.setText(getCaption());
		
		if ( p_Parent != null ) shell.setImage(p_Parent.getImage());
		
		GridLayout layTmp = new GridLayout();		
		layTmp.marginBottom = 0;
		layTmp.verticalSpacing = 0;
		shell.setLayout(layTmp);

		pageContainer = new TabFolder(shell, SWT.NONE);
		pageContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		createPages(pageContainer);
		if (!result) return;
		
		loadParameters();
		// result = loadParameters();
//		if (!result) return;
		
		//--- Dialog-level buttons

		SelectionAdapter OKCancelActions = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				if ( e.widget.getData().equals("h") ) {  // Help
					if ( help != null ) help.showTopic(this, "index");
					return;
				} 
				else if ( e.widget.getData().equals("o") ) { // OK
					
					if (!checkCanClose(true)) return;
					result = saveParameters();
				}
				else {  // Cancel
					result = false;
					if (!checkCanClose(false)) return;
				}
				
				shell.close();
			};
		};
		pnlActions = new OKCancelPanel(shell, SWT.NONE, OKCancelActions, true);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		pnlActions.setLayoutData(gdTmp);
		pnlActions.btOK.setEnabled(!readOnly);
		if ( !readOnly ) {
			shell.setDefaultButton(pnlActions.btOK);
		}

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
			if ( !shell.getDisplay().readAndDispatch() )
				shell.getDisplay().sleep();
		}				
	}

	private boolean loadParameters() {
		
		// Iterate through parameters of sub-filters and pages to load default values into the pages
		
		if (params instanceof CompoundParameters) {
			
			List<IParameters> list = ((CompoundParameters) params).getParameters();
			
			for (IParameters parameters : list)
				for (IParametersEditorPage page : pages)					
					page.load(parameters);
		}
		 
		
		// Iterate through pages, load parameters		
		
		for (IParametersEditorPage page : pages) {			
			
			if (page == null) return false;
			
			if (!page.load(params)) {
								
				Dialogs.showError(shell, String.format("Error loading parameters to the %s page", getCaption(page)), null);
				return false; // The page unable to load params is invalid
			}
		}
		
		if (params instanceof CompoundParameters) {
			
			IParameters activeParams = ((CompoundParameters) params).getActiveParameters(); 
			
			for (IParametersEditorPage page : pages) {			
				
				if (page == null) return false;
				
				if (!page.load(activeParams)) {
									
					Dialogs.showError(shell, String.format("Error loading parameters to the %s page", getCaption(page)), null);
					return false; // The page unable to load params is invalid
				}
			}
		}
						
		for (IParametersEditorPage page : pages) {			
			
			if (page == null) return false;
			
			page.interop();
		}
		
		return true;		
	}
	
	private boolean saveParameters() {
		// Iterate through pages, store parameters
		
		for (IParametersEditorPage page : pages) {			
			
			if (page == null) return false;
			
			page.interop();
		}
		
		for (IParametersEditorPage page : pages) {			
			
			if (page == null) return false;
			if (!page.save(params)) { // Fills in parametersClass
				
				Dialogs.showError(shell, String.format("Error saving parameters from the %s page", getCaption(page)), null);
				return false;
			}
		}
		
		if (params instanceof CompoundParameters) {
			
			IParameters activeParams = ((CompoundParameters) params).getActiveParameters(); 
			
			for (IParametersEditorPage page : pages) {			
				
				if (page == null) return false;
				
				if (!page.save(activeParams)) {
			
					Dialogs.showError(shell, String.format("Error saving parameters from the %s page", getCaption(page)), null);
					return false; 
				}
			}
		}
		
		return true;
	}
	
	private boolean checkCanClose(boolean isOK) {		
		// Iterate through pages, ask if the editor can be closed
		
		for (IParametersEditorPage page : pages) {			
			
			if (page == null) return false;
			if (!page.canClose(isOK)) {
				
				pageContainer.setSelection(findTab(page));
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	protected Composite addPage(String caption, Class<?> pageClass) {
			
		if (!Composite.class.isAssignableFrom(pageClass)) return null;
		
		try {
			Constructor<Composite> cc = (Constructor<Composite>) pageClass.getConstructor(new Class[] {Composite.class, int.class});
			if (cc == null) return null;
			
			Composite page = cc.newInstance(new Object[] {pageContainer, SWT.NONE});
			return addPage(caption, page);
			
		} catch (InstantiationException e) {
			
			result = false;
			return null;
			
		} catch (IllegalAccessException e) {
			
			result = false;
			return null;
			
		} catch (SecurityException e) {
			
			result = false;
			return null;
			
		} catch (NoSuchMethodException e) {
			
			result = false;
			return null;
			
		} catch (IllegalArgumentException e) {
			
			result = false;
			return null;
			
		} catch (InvocationTargetException e) {
			
			result = false;
			return null;
		}
	}
	
	protected Composite addPage(String caption, Composite page) {
		
		TabItem tabItem = new TabItem(pageContainer, SWT.NONE);
		tabItem.setText(caption);
		
		tabItem.setControl(page);
		
		if (page instanceof IParametersEditorPage) {

			IParametersEditorPage ppg = (IParametersEditorPage) page;  
			if (pages != null) pages.add(ppg);			
		}
		
		return page;
	}
	
	protected TabFolder getPageContainer() {return pageContainer;}
	
	protected Composite getPage(Class<?> pageClass) {
		
		for (TabItem tabItem : pageContainer.getItems()) {
			
			Composite page = (Composite) tabItem.getControl();
			if (page.getClass() == pageClass) return page;
		}
		
		return null;		
	}
	
	protected Composite getPage(String caption) {
		
		for (TabItem tabItem : pageContainer.getItems()) {
			
			if (tabItem.getText().equalsIgnoreCase(caption)) {
				
				Composite page = (Composite) tabItem.getControl();
				return page;
			}			
		}
		
		return null;		
	}
	
	protected TabItem findTab(IParametersEditorPage page) {
		
		if (page instanceof Composite) 
			return findTab((Composite) page);
		
		return null;
	}
	
	protected TabItem findTab(Composite page) {
		
		for (TabItem tabItem : pageContainer.getItems()) {
			
			Composite p = (Composite) tabItem.getControl();
			
			if (p == page) return tabItem;
		}			

		return null;		
	}
	
	protected String getCaption(IParametersEditorPage page) {
		
		if (!(page instanceof Composite)) return "";
		
		TabItem tab = findTab(page);
		if (tab == null) return "";
		
		return tab.getText();
	}
	
	abstract public IParameters createParameters();
	abstract protected String getCaption();
	abstract protected void createPages(TabFolder pageContainer);
}
