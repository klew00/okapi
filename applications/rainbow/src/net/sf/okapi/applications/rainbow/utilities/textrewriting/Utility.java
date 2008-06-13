package net.sf.okapi.applications.rainbow.utilities.textrewriting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.utilities.IFilterDrivenUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;
import net.sf.okapi.common.resource.ExtractionItem;
import net.sf.okapi.common.resource.IContainer;
import net.sf.okapi.common.resource.IExtractionItem;

public class Utility extends ThrougputPipeBase implements IFilterDrivenUtility  {

	private final Logger          logger = LoggerFactory.getLogger("net.sf.okapi.logging");

	
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
		String outputRoot)
	{
	}

	@Override
    public void endExtractionItem(IExtractionItem item) {
		try {
			String tmp = "";
			IExtractionItem currentItem = item.getFirstItem();
			do {
				try {
					if ( currentItem.isTranslatable() ) {
						if ( !currentItem.hasTarget() ) {
							currentItem.setTarget(new ExtractionItem());
						}
						tmp = currentItem.getContent().getCodedText().replaceAll("\\p{L}", "X");
						tmp = tmp.replaceAll("\\d", "N");
						IContainer cnt = currentItem.getTarget().getContent(); 
						cnt.setContent(tmp, currentItem.getContent().getCodes());
					}
				}
				catch ( Exception e ) {
					logger.warn("Error when updating content: '"+tmp+"'", e);
				}
			} while ( (currentItem = item.getNextItem()) != null ); 
		}
		finally {
			super.endExtractionItem(item);
		}
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
