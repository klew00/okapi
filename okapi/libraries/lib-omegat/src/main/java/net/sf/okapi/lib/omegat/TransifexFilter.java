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

package net.sf.okapi.lib.omegat;

import net.sf.okapi.filters.transifex.Project;

import org.omegat.filters2.Instance;

public class TransifexFilter extends AbstractOkapiFilter {

	private static final String EXTENSION = Project.PROJECT_EXTENSION;
	
	public TransifexFilter () {
		initialize("Transifex project files (Okapi)",
			"net.sf.okapi.filters.transifex.TransifexFilter",
			"okf_transifex-noPrompt",
			EXTENSION);
	}

	@Override
	public Instance[] getDefaultInstances () {
        return new Instance[] {
        	new Instance("*"+EXTENSION)
        };
	}

}
