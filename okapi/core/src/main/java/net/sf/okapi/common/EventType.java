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

package net.sf.okapi.common;

/**
 * The type of events used when working with the pipeline and its associated
 * interfaces such as {@link net.sf.okapi.common.filters.IFilter} or 
 * {@link net.sf.okapi.common.filterwriter.IFilterWriter}.
 */
public enum EventType {

	/**
	 * Indicates the start of an input document. A {@link net.sf.okapi.common.resource.StartDocument}
	 * resource should be associated with this event.
	 */
	START_DOCUMENT,

	/**
	 * Indicates the end of an input document. An {@link net.sf.okapi.common.resource.Ending}
	 * resource should be associated with this event.
	 */
	END_DOCUMENT,

	/**
	 * Indicates the start of a sub-document. A {@link net.sf.okapi.common.resource.StartSubDocument}
	 * resource should be associated with this event.
	 */
	START_SUBDOCUMENT,

	/**
	 * Indicates the end of a sub-document. An {@link net.sf.okapi.common.resource.Ending}
	 * resource should be associated with this event.
	 */
	END_SUBDOCUMENT,

	/**
	 * Indicates the start of a group. For example, the start tag of the
	 * &lt;table> element in HTML. A {@link net.sf.okapi.common.resource.StartGroup} resource
	 * should be associated with this event.
	 */
	START_GROUP,

	/**
	 * Indicates the end of a group. An {@link net.sf.okapi.common.resource.Ending} resource
	 * should be associated with this event.
	 */
	END_GROUP,

	/**
	 * Indicates a text unit. For example, a paragraph in an HTML document. A
	 * {@link net.sf.okapi.common.resource.TextUnit} resource should be associated 
	 * with this event.
	 */
	TEXT_UNIT,

	/**
	 * Indicates a document part. Document parts are used to carry chunks of the
	 * input document that have no translatable data, but may have properties. A
	 * {@link net.sf.okapi.common.resource.DocumentPart} resource should be associated 
	 * with this event.
	 */
	DOCUMENT_PART,

	/**
	 * Indicates that the user has canceled the process. No resource are
	 * associated with this event.
	 */
	CANCELED,
	
	/**
	 * Used to notify pipeline steps that the current batch operation is starting.
	 */
	START_BATCH,

	/**
	 * Used to notify pipeline steps that the current batch operation is finished.
	 */
	END_BATCH,
	
	/**
	 * Used to notify pipeline steps that a new batch item is about to come.
	 */
	START_BATCH_ITEM,

	/**
	 * Used to notify the pipeline steps that athe current batch item is done.
	 */
	END_BATCH_ITEM,
	
	/**
	 * Document-level event. A {@link net.sf.okapi.common.resource.RawDocument} resource
	 * should be associated with this event.
	 */
	RAW_DOCUMENT,
	
	/**
	 * An Event which holds multiple related Events, possibly of different types.
	 */
	MULTI_EVENT,
	
	/**
	 * A special event which holds updated runtime parameters for the pipeline.
	 */
	PIPELINE_PARAMETERS,

	/**
	 * A custom event type used when steps need to exchange non-resource based
	 * information.
	 */
	CUSTOM,

	/**
	 * No operation event that is ignored by all steps. Used as a placeholder
	 * event when steps need to stay alive without triggering any actions.
	 */
	NO_OP

}
