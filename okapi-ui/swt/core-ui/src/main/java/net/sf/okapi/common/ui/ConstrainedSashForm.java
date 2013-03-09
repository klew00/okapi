/*===========================================================================
  Copyright (C) 2008-2013 by the Okapi Framework contributors
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

package net.sf.okapi.common.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;

public class ConstrainedSashForm extends SashForm {
	
	private Sash sash;
	private Control upPart;
	private Control dnPart;
	private int wDelta;
	private int hDelta;
	private int min1; 
	private int min2;
	private Rectangle sr;
	
	public ConstrainedSashForm(Composite parent, final boolean verticalSash) {
		super(parent, verticalSash ? SWT.HORIZONTAL : SWT.VERTICAL);
		Display.getCurrent().asyncExec(new Runnable() {			
			@Override
			public void run() {
				// Determine the size difference from the parent (workaround for RWT shrinking the sash form at times) 
				Rectangle r0 = ConstrainedSashForm.this.getParent().getBounds();									
				Rectangle r1 = ConstrainedSashForm.this.getBounds();
				wDelta = r0.width - r1.width;
				hDelta = r0.height - r1.height;
				
				for (Control child : getChildren()) {
					if (child instanceof Sash) {
						sash = (Sash) child;
						sash.addListener(SWT.Selection, new Listener() {
							public void handleEvent(Event event) {
								//sash = (Sash)event.widget;
								Rectangle rect = sash.getParent().getClientArea();
								if (verticalSash) {							
									event.x = event.x < rect.width / 2 ? Math.max(min1, event.x) : Math.min(rect.width - min2, event.x);
								}
								else {
									event.y = event.y < rect.height / 2 ? Math.max(min1, event.y) : Math.min(rect.height - min2, event.y);
								}
								
								if (event.detail != SWT.DRAG) {
					            	//sash.setBounds(event.x, event.y, event.width, event.height);
									Rectangle r0 = sash.getParent().getClientArea();									
									Rectangle r1 = upPart.getBounds();
									Rectangle r2 = sash.getBounds();
									Rectangle r3 = dnPart.getBounds();
									
									if (verticalSash) {
										int ltWidth = event.x;
										int rtWidth = r0.width - (ltWidth + r2.width);
										
										upPart.setBounds(r1.x, r1.y, ltWidth, r1.height);																											
										sash.setBounds(ltWidth, r2.y, r2.width, r2.height);
										dnPart.setBounds(ltWidth + r2.width, r3.y, rtWidth, r3.height);
									}
									else {
										int upHeight = event.y;
										int dnHeight = r0.height - (upHeight + r2.height);
										
										upPart.setBounds(r1.x, r1.y, r1.width, upHeight);																											
										sash.setBounds(r2.x, upHeight, r2.width, r2.height);
										dnPart.setBounds(r3.x, upHeight + r2.height, r3.width, dnHeight);
									}									
									
//					            	Display.getCurrent().asyncExec(new Runnable() {
//										@Override
//										public void run() {
////											if (upPart instanceof Composite) {
////												((Composite)upPart).layout();
////											}
////											
////											if (dnPart instanceof Composite) {
////												((Composite)dnPart).layout();
////											}
//											//System.out.println(sash.getBounds().y + "  h: " + sash.getBounds().height);
//											ConstrainedSashForm.this.layout();											
////											Rectangle rect = sash.getBounds();
//											//ConstrainedSashForm.this.layout(true, true);
//										}	            		
//					            	});	            	
				            	}
							}
						});
						
						Control[] children = getChildren();
						if (children.length > 1) {
							upPart = children[0];
							dnPart = children[1];
							Point p1 = upPart.computeSize(SWT.DEFAULT, SWT.DEFAULT);
							Point p2 = dnPart.computeSize(SWT.DEFAULT, SWT.DEFAULT);
							
							if (verticalSash) {
								min1 = p1.x;
								min2 = p2.x;
								setWeights(new int[] {min1, min2});
							}
							else {
								min1 = p1.y;
								min2 = p2.y;
								setWeights(new int[] {min1, min2});
							}							
						}
						break;
					}
				}
			}					
		});
				
		addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (sash == null) return;
				Rectangle rect = sash.getParent().getClientArea();
				//Point p = sash.getLocation();
				sr = sash.getBounds();
				if (verticalSash) {							
					sr.x = sr.x < rect.width / 2 ? Math.max(min1, sr.x) : Math.min(rect.width - min2, sr.x);
					setWeights(new int[] {sr.x, rect.width - sr.x});
				}
				else {
					sr.y = sr.y < rect.height / 2 ? Math.max(min1, sr.y) : Math.min(rect.height - min2, sr.y);
					setWeights(new int[] {sr.y, rect.height - sr.y});
				}
				
				// Workaround for RWT shrinking the sash form at times
//				ConstrainedSashForm.this.getParent().layout();
				Rectangle r0 = ConstrainedSashForm.this.getParent().getBounds();
				Rectangle r1 = ConstrainedSashForm.this.getBounds();
				if (r0.width - r1.width != wDelta ||
						r0.height - r1.height != hDelta) {
					ConstrainedSashForm.this.setBounds(r1.x, r1.y, 
							r0.width - wDelta, r0.height - hDelta);
				}				
			}
		});
	}				
}
