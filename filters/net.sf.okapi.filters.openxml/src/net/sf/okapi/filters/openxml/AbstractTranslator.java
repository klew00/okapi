package net.sf.okapi.filters.openxml;

import java.util.logging.Logger;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.skeleton.GenericSkeletonWriter;

/**
 * Implements ITranslator and extends GenericSkeletonWriter so that those that 
 * extend AbstractTranslator can see GenericSkeletonWriter methods.
 */

public class AbstractTranslator extends GenericSkeletonWriter implements ITranslator {

	public AbstractTranslator()
	{
		super();
	}
	
	public String translate(TextFragment tf, Logger lgr, int nFileType) {
		// TODO Auto-generated method stub
		return null;
	}	
}
