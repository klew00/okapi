package net.sf.okapi.resource;

import java.util.ArrayList;

public class ContainerBase extends ArrayList<IContent> implements IContentContainer{

	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCodedText(String codedText) throws CodedTextException{
		throw new CodedTextException("invalid string");
	}
	
	
	public String getCodedText() {
		StringBuilder builder = new StringBuilder();
		harvestCodedTextRecursive(this, builder, 0);
		return builder.toString();
	}
	
	private void harvestCodedTextRecursive(IContentContainer container, StringBuilder builder, int level){
		for(IContent child : container){
			if(child instanceof IContentContainer){
				IContentContainer childContainer = (IContentContainer) child;
				builder.append("[");
				builder.append(level);
				builder.append("]");
				harvestCodedTextRecursive(childContainer, builder, level+1);
				builder.append("[/");
				builder.append(level);
				builder.append("]");
			}
			else{
				builder.append(child.getEquivText());
			}
		}
	}
	
}
