/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
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

package net.sf.okapi.applications.olifant;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

class LogPanel extends Composite {

	private Text edLog;
	private Button button;
	private CLabel info;
	
	LogPanel (Composite p_Parent,
		int p_nFlags)
	{
		super(p_Parent, p_nFlags);
		createContent();
	}
	
	private void createContent () {
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);

		button = new Button(this, SWT.PUSH);
		button.setText("Cancel");
		button.setEnabled(false);
		
		info = new CLabel(this, SWT.BORDER);
		GridData gdTmp = new GridData(GridData.FILL_HORIZONTAL);
		info.setLayoutData(gdTmp);
		
		edLog = new Text(this, SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		gdTmp = new GridData(GridData.FILL_BOTH);
		gdTmp.horizontalSpan = 2;
		edLog.setLayoutData(gdTmp);
	}

	public void setInfo (String text) {
		info.setText((text == null) ? "" : text); //$NON-NLS-1$
	}
	
	public void log (String text) {
		if ( text == null ) return;
		edLog.append(text+"\n");
	}
	

}
