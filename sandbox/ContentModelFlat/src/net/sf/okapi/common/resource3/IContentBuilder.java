package net.sf.okapi.common.resource3;

public interface IContentBuilder {

	public void append (char ch);

	public void append (CharSequence sequence);

	public void append (int type, String label, String data);
	
	public boolean isEmpty ();
	
	public char charAt (int index);

	public int length ();

	public CharSequence subSequence (int start, int end);

}
