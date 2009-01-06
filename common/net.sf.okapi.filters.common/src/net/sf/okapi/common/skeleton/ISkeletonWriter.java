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

package net.sf.okapi.common.skeleton;

import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.common.writer.ILayerProvider;

/**
 * Provides the methods common to all skeleton writers.
 */
public interface ISkeletonWriter {

	/**
	 * Processes the START event.
	 * @param language Code of the output language. 
	 * @param encoding Name of the output charset encoding.
	 * @param layer Layer provider to use.
	 * @param encoderManager Encoder manager to use.
	 */
	public void processStart (String language,
		String encoding,
		ILayerProvider layer,
		EncoderManager encoderManager);
	
	/**
	 * Processes the FINISHED event.
	 */
	public void processFinished ();
	
	/**
	 * Processes the START_DOCUMENT event.
	 * @param resource The StartDocument resource associated with the event.
	 * @return The string output corresponding to this event.
	 */
	public String processStartDocument (StartDocument resource);
	
	/**
	 * Processes the END_DOCUMENT event.
	 * @param resource The Ending resource associated with the event.
	 * @return The string output corresponding to this event.
	 */
	public String processEndDocument (Ending resource);
	
	/**
	 * Processes a START_SUBDOCUMENT event.
	 * @param resource The StartSubDocument resource associated with the event.
	 * @return The string output corresponding to this event.
	 */
	public String processStartSubDocument (StartSubDocument resource);
	
	/**
	 * Processes the END_SUBDOCUMENT event.
	 * @param resource The Ending resource associated with the event.
	 * @return The string output corresponding to this event.
	 */
	public String processEndSubDocument (Ending resource);
	
	/**
	 * Processes the START_GROUP event.
	 * @param resource The StartGroup resource associated with the event.
	 * @return The string output corresponding to this event.
	 */
	public String processStartGroup (StartGroup resource);
	
	/**
	 * Processes the END_GROUP event.
	 * @param resource The Ending resource associated with the event.
	 * @return The string output corresponding to this event.
	 */
	public String processEndGroup (Ending resource);
	
	/**
	 * Processes the TEXT_UNIT event.
	 * @param resource The TextUnit resource associated with the event.
	 * @return The string output corresponding to this event.
	 */
	public String processTextUnit (TextUnit resource);
	
	/**
	 * Processes the DOCUMENT_PART event.
	 * @param resource The DocumentPart resource associated with the event.
	 * @return The string output corresponding to this event.
	 */
	public String processDocumentPart (DocumentPart resource);
	
}
