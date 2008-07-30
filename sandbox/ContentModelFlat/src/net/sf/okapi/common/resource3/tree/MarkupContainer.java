package net.sf.okapi.common.resource3.tree;

public class MarkupContainer extends ContainerBase implements IContent{

	private String startCode;
	private String endCode;

	public MarkupContainer (String startCode) {
		this.startCode = startCode;
	}
	
	public void setEndCode (String value) {
		endCode = value;
	}
	
	public String getEquivText() {
		StringBuilder builder = new StringBuilder();
		for(IContent child: this){
			builder.append(child.getEquivText());
		}
		return builder.toString();
	}
	
}
