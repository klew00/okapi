package net.sf.okapi.applications.rainbow.utilities.textrewriting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.utilities.IUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;
import net.sf.okapi.common.resource.ExtractionItem;
import net.sf.okapi.common.resource.IContainer;
import net.sf.okapi.common.resource.IExtractionItem;

public class Utility extends ThrougputPipeBase implements IUtility  {

	private final Logger          logger = LoggerFactory.getLogger(Utility.class);

	
	public void doProlog (String sourceLanguage,
		String targetLanguage) {
	}
	
	public void doEpilog () {
	}
	
	public IParameters getParameters () {
		return null;
	}

	public String getInputRoot () {
		return null;
	}
	
	public String getOutputRoot () {
		return null;
	}

	public boolean hasParameters () {
		return false;
	}

	public boolean needsRoots () {
		return false;
	}

	public boolean needsOutputFilter () {
		return true;
	}

	public void setParameters (IParameters paramsObject) {
	}

	public void setRoots (String inputRoot,
		String outputRoot) {
	}

	@Override
    public void endExtractionItem(IExtractionItem sourceItem,
    	IExtractionItem targetItem)
	{
		String tmp = "";
		try {
			if ( sourceItem.isTranslatable() ) {
				if ( !sourceItem.hasTarget() ) {
					targetItem = new ExtractionItem();
					sourceItem.setHasTarget(true);
				}
				tmp = sourceItem.getContent().getCodedText().replaceAll("\\p{L}", "X");
				tmp = tmp.replaceAll("\\d", "N");
				IContainer cnt = targetItem.getContent(); 
				cnt.setContent(tmp, sourceItem.getContent().getCodes());
				super.endExtractionItem(sourceItem, targetItem);
			}
		}
		catch ( Exception e ) {
			logger.warn("Error when setting new content: '"+tmp+"'", e);
			super.endExtractionItem(sourceItem, targetItem);
		}
    }
	
	public void processInput () {
		// Do nothing: this utility is filter-driven.
	}

	public boolean isFilterDriven () {
		return true;
	}

	public void setInputData (String path,
		String encoding,
		String filterSettings)
	{
	}

	public void setOutputData (String path,
		String encoding)
	{
	}
}
