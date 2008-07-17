package net.sf.okapi.resource;

public class SegmentContainer extends ContainerBase implements IContent{

	public String getEquivText() {
		StringBuilder builder = new StringBuilder();
		for(IContent child: this){
			builder.append(child.getEquivText());
		}
		return builder.toString();
	}
}
