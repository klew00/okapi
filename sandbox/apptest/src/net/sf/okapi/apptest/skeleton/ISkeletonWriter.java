package net.sf.okapi.apptest.skeleton;

import net.sf.okapi.apptest.resource.DocumentPart;
import net.sf.okapi.apptest.resource.Ending;
import net.sf.okapi.apptest.resource.StartDocument;
import net.sf.okapi.apptest.resource.StartGroup;
import net.sf.okapi.apptest.resource.StartSubDocument;
import net.sf.okapi.apptest.resource.TextUnit;

public interface ISkeletonWriter {

	public void setOptions (String language,
		String encoding);

	public void processStart ();
	
	public void processFinished ();
	
	public String processStartDocument (StartDocument resource);
	
	public String processEndDocument (Ending resource);
	
	public String processStartSubDocument (StartSubDocument resource);
	
	public String processEndSubDocument (Ending resource);
	
	public String processStartGroup (StartGroup resource);
	
	public String processEndGroup (Ending resource);
	
	public String processTextUnit (TextUnit resource);
	
	public String processDocumentPart (DocumentPart resource);
	
}
