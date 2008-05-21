package net.sf.okapi.common.filters;

import java.io.InputStream;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.pipeline.IOutputPipe;

public interface IInputFilter extends IOutputPipe {

	public static final int FEATURE_TEXTBASED    = 1;
	public static final int FEATURE_BILINGUAL    = 2;
	

	/** 
	 * Indicates if the filter supports a given feature.
	 * @param feature Code of the feature to query. The value must be one of the
	 * FEATURE_* values.
	 * @return True if the feature is supported, false otherwise.
	 */
	boolean supports (int feature);
	
	/**
	 * Gets the current parameters for the filter.
	 * @return An IParameters object of the current parameters.
	 */
	IParameters getParameters ();
	
	/**
	 * Sets the parameters for the filter. Parameters must be set before opening the input.
	 * @param params The new IParameters object to use.
	 */
	void setParameters (IParameters params);
	
	/**
	 * Initializes the input to process.
	 * @param reader The reader to use for the input.
	 * @param name The name associated with the input (e.g. file name).
	 * @param encoding The default encoding for the input.
	 * @param sourceLanguage Language code of the source.
	 * @param targeLanguage Language code of the target.
	 */
	void initialize (InputStream input,
		String name,
		String encoding,
		String sourceLanguage,
		String targetLanguage)
		throws Exception; //TODO: Specific exception?
	
	/**
	 * Closes the current input and free any associated resources.
	 */
	void close ()
		throws Exception; //TODO: Specific exception?

	/**
	 * Process the input and feed it to the output pipe.
	 */
	void process ();
}
