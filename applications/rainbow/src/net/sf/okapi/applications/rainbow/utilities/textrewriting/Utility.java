package net.sf.okapi.applications.rainbow.utilities.textrewriting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.applications.rainbow.utilities.IFilterDrivenUtility;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.pipeline.ThrougputPipeBase;
import net.sf.okapi.common.resource.Container;
import net.sf.okapi.common.resource.IContainer;
import net.sf.okapi.common.resource.IExtractionItem;

public class Utility extends ThrougputPipeBase implements IFilterDrivenUtility  {

	private final Logger          logger = LoggerFactory.getLogger("net.sf.okapi.logging");
	private Parameters            params;
	private String                commonFolder;

	
	public Utility () {
		params = new Parameters();
	}
	
	public void resetLists () {
		// Not used for this utility
	}
	
	public String getID () {
		return "oku_textrewriting";
	}
	
	public void doProlog (String sourceLanguage,
		String targetLanguage)
	{
		commonFolder = null;
	}
	
	public void doEpilog () {
		// Not used for this utility
	}
	
	public IParameters getParameters () {
		return params;
	}

	public String getInputRoot () {
		return null;
	}
	
	public String getOutputRoot () {
		return null;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean needsRoots () {
		return false;
	}

	public boolean needsOutputFilter () {
		return true;
	}

	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}

	public void setRoots (String inputRoot,
		String outputRoot)
	{
	}

	@Override
    public void endExtractionItem(IExtractionItem item) {
		try {
			IExtractionItem currentItem = item.getFirstItem();
			do {
				// Skip non-translatable
				if ( !currentItem.isTranslatable() ) continue;
				// Skip if already translate (only if required)
				if ( currentItem.hasTarget() && !params.applyToExistingTarget ) continue;
				// Else: do the requested modifications
				// Make sure we have a target where to set data
				if ( !currentItem.hasTarget() ) {
					currentItem.setTarget(new Container());
					currentItem.getTarget().setContent(
						currentItem.getSource().getCodedText(),
						currentItem.getSource().getCodes());
				}
				switch ( params.type ) {
				case Parameters.TYPE_XNREPLACE:
					replaceWithXN(currentItem);
					break;
				}
				if ( params.addPrefix || params.addSuffix || params.addName ) {
					addText(currentItem);
				}
			} while ( (currentItem = item.getNextItem()) != null ); 
		}
		finally {
			super.endExtractionItem(item);
		}
    }
	
	private void replaceWithXN (IExtractionItem item) {
		String tmp = null;
		try {
			tmp = item.getTarget().getCodedText().replaceAll("\\p{L}", "X");
			tmp = tmp.replaceAll("\\d", "N");
			IContainer cnt = item.getTarget(); 
			cnt.setContent(tmp, item.getSource().getCodes());
		}
		catch ( Exception e ) {
			logger.warn("Error when updating content: '"+tmp+"'", e);
		}
	}
	
	/**
	 * Adds prefix and/or suffix to the target. This method assumes that
	 * the item has gone through the first transformation already.
	 * @param item The item to process.
	 */
	private void addText (IExtractionItem item) {
		String tmp = null;
		try {
			// Use the target as the text to change.
			tmp = item.getTarget().getCodedText();
			if ( params.addPrefix ) {
				tmp = params.prefix + tmp;
			}
			if ( params.addName ) {
				if ( item.getName().length() > 0 ) tmp += "."+item.getName();
				else tmp += "_"+item.getID();
			}
			if ( params.addID ) {
				tmp += "_"+item.getID();
			}
			if ( params.addSuffix ) {
				tmp += params.suffix;
			}
			IContainer cnt = item.getTarget(); 
			cnt.setContent(tmp, item.getSource().getCodes());
		}
		catch ( Exception e ) {
			logger.warn("Error when add prefix or suffix: '"+tmp+"'", e);
		}
	}
	
	public boolean isFilterDriven () {
		return true;
	}

	public void addInputData (String path,
		String encoding,
		String filterSettings)
	{
		// Not used for this utility
	}

	public void addOutputData (String path,
		String encoding)
	{
		// Compute the longest common folder
		commonFolder = Util.longestCommonDir(commonFolder,
			Util.getDirectoryName(path), !Util.isOSCaseSensitive());
	}

	public int getInputCount () {
		return 1;
	}

	public String getFolderAfterProcess () {
		return commonFolder;
	}
}
