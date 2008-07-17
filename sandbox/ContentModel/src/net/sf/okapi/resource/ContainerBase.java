package net.sf.okapi.resource;

import java.util.ArrayList;

public class ContainerBase extends ArrayList<IContent> implements IContainer, CharSequence{

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
	
	public String getCodedText(int startLevel) {
		StringBuilder builder = new StringBuilder();
		harvestCodedTextRecursive(this, builder, startLevel);
		return builder.toString();
	}	
	public String getCodedText() {
		StringBuilder builder = new StringBuilder();
		harvestCodedTextRecursive(this, builder, 0);
		return builder.toString();
	}
	
	private void harvestCodedTextRecursive(IContainer container, StringBuilder builder, int level){
		for(IContent child : container){
			if(child instanceof IContainer){
				IContainer childContainer = (IContainer) child;
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

	public char charAt(int index) {
		int currentLength = 0;
		for(IContent child: this){
			if(child instanceof IContainer){
				
			}
		}
		
		return ' ';
	}

	public int length() {
		// TODO this is a very expensive operation unless cached somehow
		int length = 0;
		for(IContent child: this){
			length += ((CharSequence)child).length();
		}
		return length;
	}

	public CharSequence subSequence(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
