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

package net.sf.okapi.common.ui.abstracteditor;

import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.ui.OKCancelPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * 
 * @version 0.1, 23.06.2009
 */

public class InputQueryDialog extends AbstractBaseDialog  {
	
	//	private OKCancelPanel pnlActions;
//	private String caption;
	private String prompt;	
//	private Object data = null;
//	private IHelp help;
//	private Class<?> pageClass;
//	private IInputQueryPage page; 

	public InputQueryDialog() {
		super(false);
	}
	
	public InputQueryDialog(boolean sizeable) {
		super(sizeable);		
	}
	
	public boolean run(Shell parent, Class<? extends Composite> pageClass, String caption, String prompt, Object initialData, IHelp help) {
		
//		if (!IDialogPage.class.isAssignableFrom(pageClass)) return false;
//		if (!Composite.class.isAssignableFrom(pageClass)) return false;
		
		this.prompt = prompt;
//		this.data = initialData;

		return super.run(parent, pageClass, caption, initialData, help);
		
//		try {
////			this.pageClass = pageClass;
////			this.caption = caption;
////			this.prompt = prompt;
////			this.data = initialData;
////			this.help = help;
//			
//			create(parent);			
//			if (!result) return  false;
//			
//			showDialog();			
//			if (!result) return  false;
//		}
//		finally {
//			
//			if (shell != null) shell.dispose();
//		}
//		
//		return result;				
	}
	
	
	
//	private void showDialog () {
//		
//		if (!result) return;
//		
//		result = false; // To react to OK only
//		shell.open();
//		while ( !shell.isDisposed() ) {
//			
//			try {
//				if ( !shell.getDisplay().readAndDispatch() )
//					shell.getDisplay().sleep();
//			}
//			catch ( Exception E ) {
//				Dialogs.showError(shell, E.getLocalizedMessage(), null);
//			}
//		}				
//	}
	

//	@Override
//	protected void setActionButtonsPanel(Shell shell, SelectionAdapter listener) {
//		
//		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, listener, true);
//		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
//		pnlActions.setLayoutData(gdTmp);
//		shell.setDefaultButton(pnlActions.btOK);
//		
//	}
	
	@Override
	protected void setActionButtonsPanel(Shell shell, SelectionAdapter listener, boolean showHelp) {
		
		OKCancelPanel pnlActions = new OKCancelPanel(shell, SWT.NONE, listener, showHelp);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		pnlActions.setLayoutData(gdTmp);
		shell.setDefaultButton(pnlActions.btOK);
		
	}
	
	@Override
	protected void done() {
		
	}



	@Override
	protected void init() {
		
		if (page instanceof IInputQueryPage)
			((IInputQueryPage) page).setPrompt(prompt);
	}
	
}

