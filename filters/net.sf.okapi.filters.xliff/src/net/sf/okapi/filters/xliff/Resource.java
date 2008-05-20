package net.sf.okapi.filters.xliff;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.okapi.common.resource.IExtractionItem;

public class Resource extends net.sf.okapi.common.resource.ResourceBase {

	public Document          doc;
	public int               status;
	public IExtractionItem   trgItem;
	public Element           srcElem;
	public Element           trgElem;

	public Resource () {
	}
}
