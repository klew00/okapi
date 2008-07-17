package net.sf.okapi.resource.serializer;

import net.sf.okapi.resource.ContentMarker;
import net.sf.okapi.resource.EndPairedContentMarker;
import net.sf.okapi.resource.IContent;
import net.sf.okapi.resource.IContainer;
import net.sf.okapi.resource.MarkupContainer;
import net.sf.okapi.resource.RootContainer;
import net.sf.okapi.resource.SegmentContainer;
import net.sf.okapi.resource.StandaloneContentMarker;
import net.sf.okapi.resource.StartPairedContentMarker;
import net.sf.okapi.resource.TextFragment;

public interface IResouceSerializer {

	public void serialize(IContent content);
	public void serialize(TextFragment fragment);
	
	public void serialize(IContainer container);
	public void serialize(RootContainer container);
	public void serialize(MarkupContainer container);
	public void serialize(SegmentContainer container);
	
	public void serialize(ContentMarker marker);
	public void serialize(StandaloneContentMarker marker);
	public void serialize(StartPairedContentMarker marker);
	public void serialize(EndPairedContentMarker marker);
	
}
