package net.sf.okapi.filters.xliff;

import java.io.InputStream;
import java.util.regex.Pattern;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.filters.IInputFilter;
import net.sf.okapi.common.pipeline.IResourceBuilder;

public class InputFilter implements IInputFilter {

	private InputStream      input;
	private IResourceBuilder output;
	private XLIFFReader      reader;
	private Parameters       params;
	private Pattern          pattern;
	

	public InputFilter () {
		reader = new XLIFFReader();
		params = new Parameters();
	}
	
	public void close ()
	{
	}

	public IParameters getParameters () {
		return params;
	}

	public void initialize (InputStream input,
		String name,
		String encoding,
		String sourceLanguage,
		String targetLanguage)
	{
		close();
		this.input = input;
		reader.resource.setName(name);
		// Not used: encoding;
		// Not used: sourceLanguage
		// Not used: targetLanguage
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	public boolean supports (int feature) {
		switch ( feature ) {
		case FEATURE_TEXTBASED:
		case FEATURE_BILINGUAL:
			return true;
		default:
			return false;
		}
	}

	public void process () {
		try {
			close();
			reader.open(input, params.fallbackToID);
			
			if ( params.useStateValues ) {
				pattern = Pattern.compile(params.stateValues);
			}
			// Get started
			output.startResource(reader.resource);
			
			// Process
			int n;
			do {
				//TODO: groups
				switch ( (n = reader.readItem()) ) {
				case XLIFFReader.RESULT_STARTTRANSUNIT:
					// Do nothing: Both events to be sent when end trans-unit comes
					// We do this because of the condition based on state attribute.
					break;
				case XLIFFReader.RESULT_ENDTRANSUNIT:
					if ( isExtractable() ) {
						output.startExtractionItem(reader.sourceItem, reader.targetItem);
						output.endExtractionItem(reader.sourceItem, reader.targetItem);
					}
					break;
				case XLIFFReader.RESULT_STARTFILE:
					output.startContainer(reader.fileRes);
					break;
				case XLIFFReader.RESULT_ENDFILE:
					output.endContainer(reader.fileRes);
					break;
				}
			}
			while ( n > XLIFFReader.RESULT_ENDINPUT );
			
			output.endResource(reader.resource);
		}
		finally {
			close();
		}
	}

	public void setOutput (IResourceBuilder builder) {
		this.output = builder;
	}

	private boolean isExtractable () {
		if ( !params.useStateValues ) return true;
		if ( reader.targetItem == null ) return true;
		String state = (String)reader.targetItem.getProperty("state");
		if (( state == null ) || ( state.length() == 0 )) {
			return params.extractNoState;
		}
		return pattern.matcher(state).find();
	}
}
