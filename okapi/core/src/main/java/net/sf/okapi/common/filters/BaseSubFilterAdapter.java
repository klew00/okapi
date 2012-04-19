package net.sf.okapi.common.filters;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.skeleton.ISkeletonWriter;

/**
 * Adapter that converts any {@link IFilter} into a subfilter (a filter called from another {@link IFilter}). 
 * Specific implementations can implement this class and override any needed methods to transform {@link Event}s
 * as they are produced 
 * 
 * @author Jim Hargrave
 *
 */
public class BaseSubFilterAdapter implements IFilter {
	private IFilter filter;
	private FilterState state;
	private SubFilterEventConverter converter;
	private int tuChildCount;
	
	public BaseSubFilterAdapter(IFilter filter, FilterState state) {		
		this.filter = filter;
		tuChildCount = 0;
		setState(state);
	}
	
	public BaseSubFilterAdapter(IFilter filter) {
		this.filter = filter;
		tuChildCount = 0;
	}
		
	public void setFilter(IFilter filter) {
		this.filter = filter;
	}

	public IFilter getFilter() {
		return filter;
	}

	public void setState(FilterState state) {
		this.state = state;		
		// reset subfilter converter with new state info
		intializeSubfilterConverter();
	}

	public FilterState getState() {
		return state;
	}

	public SubFilterEventConverter getConverter() {
		return converter;
	}

	public void setConverter(SubFilterEventConverter converter) {
		this.converter = converter;
	}

	@Override
	public String getName() {
		return filter.getName();
	}

	@Override
	public String getDisplayName() {
		return filter.getDisplayName();
	}

	@Override
	public void open(RawDocument input) {
		//SubFilterEventConverter.connect();
		filter.open(input);
		//intializeSubfilterConverter();
		tuChildCount = 0;
	}

	@Override
	public void open(RawDocument input, boolean generateSkeleton) {
		//SubFilterEventConverter.connect();
		filter.open(input, generateSkeleton);
		//intializeSubfilterConverter();
		tuChildCount = 0;
	}

	@Override
	public void close() {
		filter.close();
		tuChildCount = 0;
		//SubFilterEventConverter.disconnect();
	}

	@Override
	public boolean hasNext() { 
		return filter.hasNext();
	}

	@Override
	public Event next() {
		Event e = filter.next();
		String parentName = getState().getParentTextUnitName();
		if (parentName != null) {
			// subfiltered textunits inherit any name from a parent TU
			if (e.isTextUnit()) {
//				if (e.getTextUnit().getName() == null) {
//					String parentName = getState().getParentTextUnitName();
//					// we need to add a child id so each tu name is unique for this subfiltered content
//					if (parentName != null) {
//						parentName = parentName + "-" + Integer.toString(++tuChildCount); 
//					}
//					e.getTextUnit().setName(parentName);
//				}
				// Always prefix tu name with the parent name								
				String name = e.getTextUnit().getName(); 
				
				if (name == null) {
					name = Integer.toString(++tuChildCount);
				}
				
				parentName = parentName + "-" + name;				
				e.getTextUnit().setName(parentName);
			}
		}
		
		return converter.convertEvent(e);
	}

	@Override
	public void cancel() {
		filter.cancel();
	}

	@Override
	public IParameters getParameters() {
		return filter.getParameters();
	}

	@Override
	public void setParameters(IParameters params) {
		filter.setParameters(params);
	}

	@Override
	public void setFilterConfigurationMapper(IFilterConfigurationMapper fcMapper) {
		filter.setFilterConfigurationMapper(fcMapper);
	}

	@Override
	public ISkeletonWriter createSkeletonWriter() {
		return filter.createSkeletonWriter();
	}

	@Override
	public IFilterWriter createFilterWriter() {
		return filter.createFilterWriter();
	}

	@Override
	public EncoderManager getEncoderManager() {
		return filter.getEncoderManager();
	}

	@Override
	public String getMimeType() {
		return filter.getMimeType();
	}

	@Override
	public List<FilterConfiguration> getConfigurations() {
		return filter.getConfigurations();
	}

	private void intializeSubfilterConverter() {
		try {
			this.converter = new SubFilterEventConverter(state.getParentId(),
				state.getStartSkeleton(), state.getEndSkeleton(), state.getIdGenerator());
		} catch (NullPointerException e) {
			throw new OkapiBadFilterInputException("FilterState not set. Cannot intialize subfilter converter");
		}
	}
}
