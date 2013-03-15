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

package net.sf.okapi.steps.imagemodification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import net.sf.okapi.common.BaseParameters;
import net.sf.okapi.common.EditorFor;
import net.sf.okapi.common.ParametersDescription;
import net.sf.okapi.common.uidescription.CheckboxPart;
import net.sf.okapi.common.uidescription.EditorDescription;
import net.sf.okapi.common.uidescription.IEditorDescriptionProvider;
import net.sf.okapi.common.uidescription.ListSelectionPart;
import net.sf.okapi.common.uidescription.SpinInputPart;

@EditorFor(Parameters.class)
public class Parameters extends BaseParameters implements IEditorDescriptionProvider {
	
	private static final String SCALEWIDTH = "scaleWidth";
	private static final String SCALEHEIGHT = "scaleHeight";
	private static final String FORMAT = "format";
	private static final String MAKEGRAY = "makeGray";

	private int scaleWidth;
	private int scaleHeight;
	private String format;
	private boolean makeGray;

	public Parameters () {
		reset();
	}
	
	public int getScaleWidth () {
		return scaleWidth;
	}

	public void setScaleWidth (int scaleWidth) {
		this.scaleWidth = scaleWidth;
	}

	public int getScaleHeight() {
		return scaleHeight;
	}

	public void setScaleHeight (int scaleHeight) {
		this.scaleHeight = scaleHeight;
	}

	public String getFormat () {
		return format;
	}

	public void setFormat (String format) {
		this.format = format;
	}

	public boolean getMakeGray () {
		return makeGray;
	}

	public void setMakeGray (boolean makeGray) {
		this.makeGray = makeGray;
	}

	@Override
	public void reset () {
		scaleHeight = 50;
		scaleWidth = 50;
		format = ""; // Same as original
		makeGray = false;
	}

	@Override
	public void fromString (String data) {
		reset();
		buffer.fromString(data);
		scaleHeight = buffer.getInteger(SCALEHEIGHT, scaleHeight);
		scaleWidth = buffer.getInteger(SCALEWIDTH, scaleWidth);
		format = buffer.getString(FORMAT, format);
		makeGray = buffer.getBoolean(MAKEGRAY, makeGray);
	}

	@Override
	public String toString() {
		buffer.reset();
		buffer.setInteger(SCALEHEIGHT, scaleHeight);
		buffer.setInteger(SCALEWIDTH, scaleWidth);
		buffer.setString(FORMAT, format);
		buffer.setBoolean(MAKEGRAY, makeGray);
		return buffer.toString();
	}
	
	@Override
	public ParametersDescription getParametersDescription () {
		ParametersDescription desc = new ParametersDescription(this);
		desc.add(SCALEHEIGHT, "Percentage of the original height", "Height percentage (must be greater than 0.");		
		desc.add(SCALEWIDTH, "Percentage of the original width", "Width percentage (must be greater than 0.");
		desc.add(MAKEGRAY, "Convert to gray scale", null);
		desc.add(FORMAT, "Output format", "Format of the output files.");
		return desc;
	}
	
	@Override
	public EditorDescription createEditorDescription(ParametersDescription paramsDesc) {
		EditorDescription desc = new EditorDescription("Image Modification", false, false);	
		
		SpinInputPart sip = desc.addSpinInputPart(paramsDesc.get(SCALEWIDTH));
		sip.setRange(1, 1000);
		sip = desc.addSpinInputPart(paramsDesc.get(SCALEHEIGHT));
		sip.setRange(1, 1000);
	
		CheckboxPart cbp = desc.addCheckboxPart(paramsDesc.get(MAKEGRAY));
		cbp.setVertical(true);
		
		// List of the output formats
		List<String> available = new ArrayList<String>(Arrays.asList(ImageIO.getWriterFileSuffixes()));
		// Pre-defined set of output
		List<String> suffixes = new ArrayList<String>();
		List<String> names = new ArrayList<String>();
		names.add("<Same format as the original>");
		suffixes.add("");
		if ( available.contains("png") ) {
			names.add("PNG (Portable Network Graphics)");
			suffixes.add("png");
		}
		if ( available.contains("jpg") ) {
			names.add("JPEG (Joint Photographic Experts Group)");
			suffixes.add("jpg");
		}
		if ( available.contains("bmp") ) {
			names.add("BMP Bitmap");
			suffixes.add("bmp");
		}
		if ( available.contains("gif") ) {
			names.add("GIF (Graphics Interchange Format)");
			suffixes.add("gif");
		}
		
		ListSelectionPart lsp = desc.addListSelectionPart(paramsDesc.get(FORMAT), suffixes.toArray(new String[0]));
		lsp.setChoicesLabels(names.toArray(new String[0]));
		lsp.setVertical(true);
		lsp.setLabelFlushed(false);
		return desc;
	}
	
}
