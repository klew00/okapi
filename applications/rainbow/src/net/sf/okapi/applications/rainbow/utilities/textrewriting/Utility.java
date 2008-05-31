package net.sf.okapi.applications.rainbow.utilities.textrewriting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.utilities.IUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;
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

	public String getRoot () {
		return null;
	}

	public boolean hasParameters () {
		return false;
	}

	public boolean needsRoot () {
		return false;
	}

	public boolean needsOutput () {
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
		String tmp = "";
		try {
			if ( sourceItem.isTranslatable() ) {
				//TODO: handle bilingual files
				IContainer cnt = sourceItem.getContent(); 
				tmp = cnt.getCodedText().replaceAll("\\p{L}", "X");
				cnt.setContent(tmp.replaceAll("\\d", "N"));
				super.endExtractionItem(sourceItem, targetItem);
			}
		}
		catch ( Exception e ) {
			logger.warn("Error when setting new content: '"+tmp+"'", e);
			super.endExtractionItem(sourceItem, targetItem);
		}
    }
	
	public void execute (String inputPath) {
		// Do nothing: this utility is filter-driven.
	}

	public boolean isFilterDriven () {
		return true;
	}
}
