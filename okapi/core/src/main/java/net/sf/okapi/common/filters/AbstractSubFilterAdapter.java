package net.sf.okapi.common.filters;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.encoder.EncoderManager;
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
public abstract class AbstractSubFilterAdapter implements IFilter {
	IFilter filter;
	FilterState state;
	SubFilterEventConverter converter;
	
	public AbstractSubFilterAdapter(IFilter filter, FilterState state) {
		this.state = state;
		this.filter = filter;
		this.converter = new SubFilterEventConverter(state.getParentId(),
				state.getStartSkeleton(), state.getEndSkeleton());
	}
	
	protected void setFilter(IFilter filter) {
		this.filter = filter;
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
		filter.open(input);
	}

	@Override
	public void open(RawDocument input, boolean generateSkeleton) {
		filter.open(input, generateSkeleton);
	}

	@Override
	public void close() {
		filter.close();
	}

	@Override
	public boolean hasNext() { 
		return filter.hasNext();
	}

	@Override
	public Event next() {
		Event e = filter.next();		
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

	@Override
	public boolean isSubfilter() {
		return true;
	}
}
