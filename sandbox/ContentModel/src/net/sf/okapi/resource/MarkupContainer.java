package net.sf.okapi.resource;

public class MarkupContainer extends ContainerBase implements IContent{

	private String startCode;

	private String endCode;
	
	public String getEquivText() {
		StringBuilder builder = new StringBuilder();
		for(IContent child: this){
			builder.append(child.getEquivText());
		}
		return builder.toString();
	}
	
}
