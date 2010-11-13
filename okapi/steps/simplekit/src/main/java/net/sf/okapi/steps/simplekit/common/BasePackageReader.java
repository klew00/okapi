package net.sf.okapi.steps.simplekit.common;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.steps.simplekit.common.IPackageReader;
import net.sf.okapi.steps.simplekit.common.Manifest;

/**
 * Implements the {@link IPackageReader} interface for translation kits based
 * on the "simple-kit" mechanism.
 * <p>This base class implements the handing of the manifest. derived class must
 * override the {@link #postProcessPackageItem(String, LocaleId, LocaleId)} method
 * to implement the handling of their own type of files. 
 */
public abstract class BasePackageReader implements IPackageReader {

	@Override
	public void postProcessPackage (String path,
		LocaleId sourceLocale,
		LocaleId targetLocale)
	{
		String filename = Util.getFilename(path, true);
		if ( filename.equals(Manifest.MANIFEST_FILENAME) ) {
			postProcessManifest(path, sourceLocale, targetLocale);
		}
		else {
			postProcessPackageItem(path, sourceLocale, targetLocale);
		}
	}

	protected void postProcessManifest (String path,
		LocaleId sourceLocale,
		LocaleId targetLocale)
	{
		// Do nothing by default
	}

	protected abstract void postProcessPackageItem (String path,
		LocaleId sourceLocale,
		LocaleId targetLocale);

}
