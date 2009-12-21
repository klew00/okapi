package net.sf.okapi.common.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.okapi.common.IResource;
import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.annotation.Annotations;
import net.sf.okapi.common.annotation.IAnnotation;
import net.sf.okapi.common.exceptions.OkapiNotImplementedException;

public class Document implements IResource, Iterable<IResource> {

	private Annotations annotations;
	private String id;
	private List<IResource> documentResources;
	
	public Document() {
		documentResources = new ArrayList<IResource>(100);
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
	 * @return never returns.
	 * @throws OkapiNotImplementedException
	 */
	@Override
	public ISkeleton getSkeleton() {
		throw new OkapiNotImplementedException("The Document resource does not have skeketon");
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
	 * @throws OkapiNotImplementedException
	 */
	@Override	
	public void setSkeleton(ISkeleton skeleton) {
		throw new OkapiNotImplementedException("Dcoument has no skeleton");
	}

	@Override
	public Iterator<IResource> iterator() {
		return documentResources.iterator();
	}	
}
