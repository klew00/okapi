/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit.opc;


public interface TKitRelationshipTypes {

	/**
	 * Core document relationship type.
	 */
	String CORE_DOCUMENT = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument";

	/**
	 * Skeleton relationship type.
	 */
	String SKELETON = "http://schemas.okapi.org/2010/relationships/skeleton";
	
	/**
	 * Resources relationship type.
	 */
	String RESOURCES = "http://schemas.okapi.org/2010/relationships/resources";
	
	/**
	 * Source document relationship type.
	 */
	String SOURCE = "http://schemas.okapi.org/2010/relationships/source";
	
	/**
	 * Document original relationship type.
	 */
	String ORIGINAL = "http://schemas.okapi.org/2010/relationships/original";
	
	/**
	 * Core properties relationship type.
	 */
	String CORE_PROPERTIES = "http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties";

	/**
	 * Extended properties relationship type.
	 */
	String EXTENDED_PROPERTIES = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties";
	
	/**
	 * Custom properties relationship type.
	 */
	String CUSTOM_PROPERTIES = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/custom-properties";
	
	/**
	 * Custom XML relationship type.
	 */
	String CUSTOM_XML = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/customXml";

	/**
	 * Thumbnail relationship type.
	 */
	String THUMBNAIL = "http://schemas.openxmlformats.org/package/2006/relationships/metadata/thumbnail";
	
	/**
	 * Image part relationship type.
	 */
	String IMAGE_PART = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image";

	/**
	 * Style part relationship type.
	 */
	String STYLE_PART = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles";
	
	/**
	 * Audio part relationship type. 
	 */
	String AUDIO_PART = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/audio";
	
	/**
	 * Video part relationship type.
	 */
	String VIDEO_PART = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/video";
	
	/**
	 * Embedded package relationship type.
	 */
	String EMBEDDED_PACKAGE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/package";
	
	/**
	 * Embedded font relationship type.
	 */
	String EMBEDDED_FONT = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/font";
	
	/**
	 * Digital signature relationship type.
	 */
	String DIGITAL_SIGNATURE = "http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/signature";

	/**
	 * Digital signature certificate relationship type.
	 */
	String DIGITAL_SIGNATURE_CERTIFICATE = "http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/certificate";

	/**
	 * Digital signature origin relationship type.
	 */
	String DIGITAL_SIGNATURE_ORIGIN = "http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/origin";
	
	
}
