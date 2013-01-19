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

package net.sf.okapi.lib.verification;

import net.sf.okapi.common.IHelp;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.resource.RawDocument;

public interface IQualityCheckEditor {

	/**
	 * Initializes this IQualityCheckEditor object.
	 * @param parent the object representing the parent window/shell for this editor.
	 * The type of this parameter depends on the implementation.
	 * @param asDialog true if used from another program.
	 * @param helpParam the help engine to use.
	 * @param fcMapper the IFilterConfigurationMapper object to use with the editor.
	 * @param session an optional session to use (null to use one created internally)
	 */
	public void initialize (Object parent,
		boolean asDialog,
		IHelp helpParam,
		IFilterConfigurationMapper fcMapper,
		QualityCheckSession session);

	/**
	 * Adds a raw document to the session. If this is the
	 * first document added to the session, the locales of the session are automatically
	 * set to the source and target locale of this document.
	 * This method can be called without the UI being setup yet.
	 * @param rawDoc the raw document to add (it must have an input URI and its
	 * source and target locale set).
	 */
	public void addRawDocument (RawDocument rawDoc);
	
	/**
	 * Gets the session associated with this editor. You want to call this method
	 * only after {@link #initialize(Object, boolean, IHelp, IFilterConfigurationMapper, QualityCheckSession)}
	 * has been called.
	 * @return the session associated with this editor.
	 */
	public QualityCheckSession getSession ();
	
	/**
	 * Runs an editing session with this IQualityCheckEditor object.
	 * You must have called {@link #initialize(Object, boolean, IHelp, IFilterConfigurationMapper)}
	 * once before calling this method.
	 * @param processOnStart true to trigger the verification process when the editor is opened.
	 */
	public void edit (boolean processOnStart);

}

