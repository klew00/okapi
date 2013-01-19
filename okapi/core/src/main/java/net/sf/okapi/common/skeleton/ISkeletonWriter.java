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
import net.sf.okapi.common.filterwriter.ILayerProvider;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.EndSubfilter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.StartSubfilter;

/**
 * Provides the methods common to all skeleton writers.
 */
public interface ISkeletonWriter {

	/**
	 * Closes this skeleton writer.
	 */
	public void close ();
	
	/**
	 * Processes the START_DOCUMENT event.
	 * @param outputLocale the output locale. 
	 * @param outputEncoding the name of the output charset encoding.
	 * @param layer the layer provider to use.
	 * @param encoderManager the encoder manager to use.
	 * @param resource the StartDocument resource associated with the event.
	 * @return the string output corresponding to this event.
	 */
	public String processStartDocument (LocaleId outputLocale,
		String outputEncoding,
		ILayerProvider layer,
		EncoderManager encoderManager,
		StartDocument resource);
	
	/**
	 * Processes the END_DOCUMENT event.
	 * @param resource the Ending resource associated with the event.
	 * @return the string output corresponding to this event.
	 */
	public String processEndDocument (Ending resource);
	
	/**
	 * Processes a START_SUBDOCUMENT event.
	 * @param resource the StartSubDocument resource associated with the event.
	 * @return the string output corresponding to this event.
	 */
	public String processStartSubDocument (StartSubDocument resource);
	
	/**
	 * Processes the END_SUBDOCUMENT event.
	 * @param resource the Ending resource associated with the event.
	 * @return the string output corresponding to this event.
	 */
	public String processEndSubDocument (Ending resource);
	
	/**
	 * Processes the START_GROUP event.
	 * @param resource the StartGroup resource associated with the event.
	 * @return the string output corresponding to this event.
	 */
	public String processStartGroup (StartGroup resource);
	
	/**
	 * Processes the END_GROUP event.
	 * @param resource the Ending resource associated with the event.
	 * @return the string output corresponding to this event.
	 */
	public String processEndGroup (Ending resource);
	
	/**
	 * Processes the TEXT_UNIT event.
	 * @param resource the TextUnit resource associated with the event.
	 * @return the string output corresponding to this event.
	 */
	public String processTextUnit (ITextUnit resource);
	
	/**
	 * Processes the DOCUMENT_PART event.
	 * @param resource the DocumentPart resource associated with the event.
	 * @return the string output corresponding to this event.
	 */
	public String processDocumentPart (DocumentPart resource);
	
	public String processStartSubfilter (StartSubfilter resource);
	
	public String processEndSubfilter (EndSubfilter resource);	
}
