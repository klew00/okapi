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

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

class EditorPanel extends SashForm {

	private SegmentEditor edSource;
	private SegmentEditor edTarget;
	private ExtraFieldPanel extraPanel;

	public EditorPanel (Composite parent,
		int flags)
	{
		super(parent, flags);
		// Default layout
		setLayout(new GridLayout(1, false));
		setLayoutData(new GridData(GridData.FILL_BOTH));
		
		edSource = new SegmentEditor(this, -1);
		edTarget = new SegmentEditor(this, -1);
		extraPanel = new ExtraFieldPanel(this, 0);
		
		setWeights(new int[]{1, 1, 0});
		setSashWidth(0);
		
		edTarget.setFocus();
	}

	public void setFields (String source,
		String target)
	{
		edSource.setText(source);
		edTarget.setText(target);
	}
	
	public void setFullCodesMode (boolean fullCodesMode) {
		edSource.setFullCodesMode(fullCodesMode);
		edTarget.setFullCodesMode(fullCodesMode);
	}
	
	public boolean getFullCodesMode () {
		return edSource.getFullcodesMode();
		// Source and target are always in the same mode
	}
	
	public void setExtraText (String text) {
		extraPanel.setText(text);
	}
	
	public String getSourceText () {
		return edSource.getText();
	}
		
	public String getTargetText () {
		return edTarget.getText();
	}
	
	public String getExtraText () {
		return extraPanel.getText();
	}
		
	public void clear () {
		edSource.clear();
		edTarget.clear();
		extraPanel.clear();
	}

	public boolean isModified () {
		return (edTarget.isModified()
			|| edSource.isModified()
			|| extraPanel.isModified());
	}
	
	public boolean isSourceModified () {
		return edSource.isModified();
	}
	
	public boolean isTargetModified () {
		return edTarget.isModified();
	}
	
	public boolean isExtraModified () {
		return extraPanel.isModified();
	}
	
	@Override
	public void setEnabled (boolean enabled) {
		super.setEnabled(enabled);
		edSource.setEnabled(enabled);
		edTarget.setEnabled(enabled);
		extraPanel.setEnabled(enabled);
	}
	
	public void toggleExtra () {
		if ( getWeights()[2] > 0 ) {
			setWeights(new int[]{1, 1, 0});
			setSashWidth(0);
		}
		else {
			setWeights(new int[]{1, 1, 1});
			setSashWidth(4);
		}
	}
	
	public boolean isExtraVisible () {
		return (getWeights()[2] > 0);
	}
	
	public ExtraFieldPanel getExtraFieldPanel () {
		return extraPanel;
	}

}
