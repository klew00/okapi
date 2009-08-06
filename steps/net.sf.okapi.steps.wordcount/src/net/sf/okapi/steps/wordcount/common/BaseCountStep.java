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

package net.sf.okapi.steps.wordcount.common;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.steps.tokenization.common.AbstractPipelineStep;

/**
 * Base abstract class for different counter steps (word count step, character count step, etc.).
 * 
 * @version 0.1 08.07.2009
 */

public abstract class BaseCountStep extends AbstractPipelineStep {

	private Parameters params;	
	
	private long batchCount;
	private long batchItemCount;
	private long documentCount;
	private long subDocumentCount;
	private long groupCount;
	private long textUnitCount;
	
	public BaseCountStep() {
		
		super();
		setParameters(new Parameters());
	}
	
	@Override
	protected void component_init() {
		
		params = getParameters(Parameters.class);
		
		// Reset counters
		batchCount = 0;
		batchItemCount = 0;
		documentCount = 0;
		subDocumentCount = 0;
		groupCount = 0;
		textUnitCount = 0;
	}

	//-------------------------
//	abstract protected long getCount(TextUnit textUnit);
//	abstract protected void saveCount(Metrics metrics, long count);
	
//	abstract protected String getToken();
	abstract protected String getMetric();
	abstract protected long getCount(TextUnit textUnit);

	protected void saveCount(Metrics metrics, long count) {
		
		if (metrics == null) return;
		
		metrics.setMetric(getMetric(), count);
	}

	public long getBatchCount() {
		
		return batchCount;
	}

	public long getBatchItemCount() {
		
		return batchItemCount;
	}

	public long getDocumentCount() {
		
		return documentCount;
	}

	public long getSubDocumentCount() {
		
		return subDocumentCount;
	}

	public long getGroupCount() {
		
		return groupCount;
	}

	public long getTextUnitCount() {
		
		return textUnitCount;
	}
	
	protected void saveToMetrics(Event event, long count) {
		
		if (event == null) return;
		if (count == 0) return;
		
		IResource res = event.getResource();
		if (res == null) return;
		
		MetricsAnnotation ma = res.getAnnotation(MetricsAnnotation.class);
		
		if (ma == null) {
			
			ma = new MetricsAnnotation();
			res.setAnnotation(ma);
		}
		
		if (ma == null) return;
		
		Metrics m = ma.getMetrics();		
		if (m == null) return;
					
		saveCount(m, count);
	}
	
	//-------------------------	
	@Override
	protected void handleStartBatch(Event event) {
			
		batchCount = 0;
	}
	
	@Override
	protected void handleEndBatch(Event event) {
		
		if (!params.countInBatch) return;
		if (batchCount == 0) return;
		
		saveToMetrics(event, batchCount);
	}

	//-------------------------	
	@Override
	protected void handleStartBatchItem(Event event) {
		
		batchItemCount = 0;
	}
	
	@Override
	protected void handleEndBatchItem(Event event) {
		
		if (!params.countInBatchItems) return;
		if (batchItemCount == 0) return;
		
		saveToMetrics(event, batchItemCount);
	}

	//-------------------------	
	@Override
	protected void handleStartDocument(Event event) {
		
		documentCount = 0;
		super.handleStartDocument(event); // Sets language						
	}
	
	@Override
	protected void handleEndDocument(Event event) {
		
		if (!params.countInDocuments) return;
		if (documentCount == 0) return;
		
		saveToMetrics(event, documentCount);
	}

	//-------------------------
	@Override
	protected void handleStartSubDocument(Event event) {
		
		subDocumentCount = 0;
	}
	
	@Override
	protected void handleEndSubDocument(Event event) {
		
		if (!params.countInSubDocuments) return;
		if (subDocumentCount == 0) return;
		
		saveToMetrics(event, subDocumentCount);
	}
	
	//-------------------------
	@Override
	protected void handleStartGroup(Event event) {
		
		groupCount = 0;
	}
	
	@Override
	protected void handleEndGroup(Event event) {
		
		if (!params.countInGroups) return;
		if (groupCount == 0) return;
		
		saveToMetrics(event, groupCount);
	}

	//-------------------------
	@Override
	protected void handleTextUnit(Event event) {
		
		TextUnit tu = (TextUnit) event.getResource();
		
		if (tu.isEmpty()) return;
		if (!tu.isTranslatable()) return;
		
		textUnitCount = getCount(tu);
		if (textUnitCount == 0) return;
		
		saveToMetrics(event, textUnitCount);
				
		if (params.countInBatch) batchCount += textUnitCount;
		if (params.countInBatchItems) batchItemCount += textUnitCount;
		if (params.countInDocuments) documentCount += textUnitCount;
		if (params.countInSubDocuments) subDocumentCount += textUnitCount;
		if (params.countInGroups) groupCount += textUnitCount;		
	}

}
