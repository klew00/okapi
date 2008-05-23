package net.sf.okapi.applications.rainbow.utilities.textrewriting;

import net.sf.okapi.applications.rainbow.utilities.IUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;
import net.sf.okapi.common.resource.IContainer;
import net.sf.okapi.common.resource.IExtractionItem;

public class Utility extends ThrougputPipeBase implements IUtility  {

	public void doProlog (String sourceLanguage,
		String targetLanguage) {
	}
	
	public void doEpilog () {
	}
	
	public IParameters getParameters () {
		return null;
	}

	public String getRoot () {
		return null;
	}

	public boolean hasParameters () {
		return false;
	}

	public boolean needRoot () {
		return false;
	}

	public boolean needOutput () {
		return true;
	}

	public void setParameters (IParameters paramsObject) {
	}

	public void setRoot (String root) {
	}

	@Override
    public void endExtractionItem(IExtractionItem sourceItem,
    	IExtractionItem targetItem)
	{
    	try {
    		if ( sourceItem.isTranslatable() ) {
    			//TODO: handle bilingual files
    			IContainer cnt = sourceItem.getContent(); 
    			String tmp = cnt.getCodedText().replaceAll("\\p{L}", "X");
    			cnt.setContent(tmp.replaceAll("\\d", "N"));
    		}
    	   	super.endExtractionItem(sourceItem, targetItem);
    	}
    	catch ( Exception e ) {
    		System.err.println(e.getLocalizedMessage());
    	}
    }
	
}
