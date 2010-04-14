package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.exceptions.OkapiNotImplementedException;
import net.sf.okapi.common.pipeline.Pipeline;

public class MultiEvent implements IResource, Iterable<Event> {
	private Annotations annotations;
	private String id;
	private boolean propagateAsSingleEvent = false;
	private List<Event> events;

	public MultiEvent() {
		propagateAsSingleEvent = false;
		events = new ArrayList<Event>(100);
	}
	
	public MultiEvent(List<Event> events) {
		propagateAsSingleEvent = false;
		this.events = events;;
	}

	public void addEvent(Event event) {
		events.add(event);
	}

	@Override
	public <A extends IAnnotation> A getAnnotation(Class<A> annotationType) {
		if (annotations == null)
			return null;
		return annotationType.cast(annotations.get(annotationType));
	}

	@Override
	public String getId() {
		return id;
	}

	/**
	 * Always throws an exception as there is never a skeleton associated to a RawDocument.
	 * 
	 * @return never returns.
	 * @throws OkapiNotImplementedException
	 */
	@Override
	public ISkeleton getSkeleton() {
		throw new OkapiNotImplementedException("MultiResource does not have a skeketon");
	}

	@Override
	public void setAnnotation(IAnnotation annotation) {
		if (annotations == null) {
			annotations = new Annotations();
		}
		annotations.set(annotation);
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * This method has no effect as there is never a skeleton for a Document.
	 * 
	 * @throws OkapiNotImplementedException
	 */
	@Override
	public void setSkeleton(ISkeleton skeleton) {
		throw new OkapiNotImplementedException("MultiResource does not have a skeketon");
	}

	@Override
	public Iterator<Event> iterator() {
		return events.iterator();
	}

	/**
	 * Set Propagate As Single Event flag.
	 * @param propagateAsSingleEvent
	 */
	public void setPropagateAsSingleEvent(boolean propagateAsSingleEvent) {
		this.propagateAsSingleEvent = propagateAsSingleEvent;
	}

	/**
	 * Do we send this {@link Event} by itself or does the {@link Pipeline} break the individual Events and end them
	 * singley. Default is false - we send each Event singley.
	 * 
	 * @return true if we send the Event as-is, false to send the invidual Events contined here.
	 */
	public boolean isPropagateAsSingleEvent() {
		return propagateAsSingleEvent;
	}

	public Annotations getAnnotations() {
		return (annotations == null) ? new Annotations() : annotations;
	}
}
