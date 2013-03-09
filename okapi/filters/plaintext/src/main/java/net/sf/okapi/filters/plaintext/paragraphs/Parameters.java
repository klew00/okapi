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

package net.sf.okapi.filters.plaintext.paragraphs;

import net.sf.okapi.common.ParametersString;

/**
 * Paragraph Plain Text Filter parameters 
 * 
 * @version 0.1, 09.06.2009
 */

public class Parameters extends net.sf.okapi.filters.plaintext.base.Parameters {
	
	/**
	 * This parameter specifies whether input plain text contains paragraphs (groups of lines separated by one or more empty lines).<p>
	 * When false, each line will be extracted separately. When true, adjacent lines will be extracted together as a paragraph.<p> 
	 * Default: false (extract single lines)
	 */
	public boolean extractParagraphs;
		
//----------------------------------------------------------------------------------------------------------------------------	
	
	@Override
	protected void parameters_load(ParametersString buffer) {

		super.parameters_load(buffer);
		
		extractParagraphs = buffer.getBoolean("extractParagraphs", true);
	}

	@Override
	protected void parameters_reset() {

		super.parameters_reset();
		
		extractParagraphs = true;
	}

	@Override
	protected void parameters_save(ParametersString buffer) {

		super.parameters_save(buffer);
		
		buffer.setBoolean("extractParagraphs", extractParagraphs);
	}
	
}
