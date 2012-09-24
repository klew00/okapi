/*===========================================================================
  Copyright (C) 2008-2012 by the Okapi Framework contributors
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

package net.sf.okapi.common.filters;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.encoder.EncoderContext;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.encoder.IEncoder;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.filterwriter.ILayerProvider;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.EndSubfilter;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.StartSubfilter;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

public class SubFilterSkeletonWriter implements ISkeletonWriter {

	public static final Ending GET_OUTPUT = new Ending("#$GET_SFSW_OUTPUT$#");
	public static final StartDocument SET_OPTIONS = new StartDocument("#$SET_OPTIONS$#");
	private ISkeletonWriter skelWriter; // Skeleton writer of the subfilter's internal filter
	private IEncoder parentEncoder;
	private StringBuilder sb;	
	
	public SubFilterSkeletonWriter(StartSubfilter resource) { //,
//			LocaleId locale, String encoding) {
		sb = new StringBuilder();
		IFilterWriter sfFilterWriter = resource.getFilterWriter();
//		sfFilterWriter.setOptions(locale, encoding);
		this.skelWriter = sfFilterWriter.getSkeletonWriter();
//		this.skelWriter.processStartDocument(locale, encoding, null, 
//				resource.getFilterWriter().getEncoderManager(), resource.getStartDoc());
		this.parentEncoder = resource.getParentEncoder();		
	}
	
	@Override
	public void close() {
		skelWriter.close();		
	}

	@Override
	public String processStartDocument(LocaleId outputLocale,
			String outputEncoding, ILayerProvider layer,
			EncoderManager encoderManager, StartDocument resource) {
//		if (resource == SET_OPTIONS) {
//			return "";
//		}
//		else {
			sb.append(skelWriter.processStartDocument(outputLocale, outputEncoding, layer, 
					encoderManager, resource));
			return "";
//		}		
	}
	
	/**
	 * Get output created by this skeleton writer from a sequence of events.
	 * This method is useful when only an ISkeletonWriter reference is available.
	 * @param resource can be the SubFilterSkeletonWriter.GET_OUTPUT token (to return the overall output
	 * of this skeleton writer), or any other Ending resource.
	 * @return output of this skeleton writer if parameter is the SubFilterSkeletonWriter.GET_OUTPUT token
	 * or an empty string otherwise.  
	 */
	@Override
	public String processEndDocument(Ending resource) {
		if (resource == GET_OUTPUT) {
			return parentEncoder == null ? sb.toString() : parentEncoder.encode(sb.toString(), EncoderContext.TEXT);
		}
		else {
			sb.append(skelWriter.processEndDocument(resource));
			return "";
		}
	}

	@Override
	public String processStartSubDocument(StartSubDocument resource) {
		sb.append(skelWriter.processStartSubDocument(resource));
		return "";
	}

	@Override
	public String processEndSubDocument(Ending resource) {
		sb.append(skelWriter.processEndSubDocument(resource));
		return "";
	}

	@Override
	public String processStartGroup(StartGroup resource) {
		sb.append(skelWriter.processStartGroup(resource));
		return "";
	}

	@Override
	public String processEndGroup(Ending resource) {
		sb.append(skelWriter.processEndGroup(resource));
		return "";
	}

	@Override
	public String processTextUnit(ITextUnit resource) {
		sb.append(skelWriter.processTextUnit(resource));
		return "";
	}

	@Override
	public String processDocumentPart(DocumentPart resource) {
		sb.append(skelWriter.processDocumentPart(resource));
		return "";
	}

	@Override
	public String processStartSubfilter(StartSubfilter resource) {
		sb.append(skelWriter.processStartSubfilter(resource));
		return "";
	}

	@Override
	public String processEndSubfilter(EndSubfilter resource) {
		sb.append(skelWriter.processEndSubfilter(resource));
		return "";
	}

	public String getEncodedOutput() {
		return processEndDocument(GET_OUTPUT);
	}

	public ISkeletonWriter setOptions(LocaleId outputLocale, String outputEncoding, 
			StartSubfilter startSubfilter) {
		StartDocument sfStartDoc = startSubfilter.getStartDoc();
		IFilterWriter sfFilterWriter = sfStartDoc.getFilterWriter();
		EncoderManager sfEncoderManager = sfFilterWriter.getEncoderManager();
		
		processStartDocument(outputLocale, outputEncoding, null, 
				sfEncoderManager,
				startSubfilter.getStartDoc());
		return this;
	}

}
