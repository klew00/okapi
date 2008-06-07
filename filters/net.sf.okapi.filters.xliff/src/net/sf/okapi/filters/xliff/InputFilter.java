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
	private Pattern          pattern;
	

	public InputFilter () {
		reader = new XLIFFReader();
	}
	
	public void close () {
	}

	public IParameters getParameters () {
		return reader.resource.getParameters();
	}

	public void initialize (InputStream input,
		String name,
		String filterSettings,
		String encoding,
		String sourceLanguage,
		String targetLanguage)
	{
		close();
		this.input = input;
		reader.resource.setName(name);
		reader.resource.setName(name);
		reader.resource.setFilterSettings(filterSettings);
		reader.resource.setSourceEncoding(encoding);
		//TODO: Get the real target/output encoding from parameters
		reader.resource.setTargetEncoding(encoding);
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
			Resource res = (Resource)reader.resource;
			reader.open(input, res.params.fallbackToID);
			
			if ( res.params.useStateValues ) {
				pattern = Pattern.compile(res.params.stateValues);
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
						output.startExtractionItem(reader.item);
						output.endExtractionItem(reader.item);
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
		Resource res = (Resource)reader.resource;
		
		if ( !res.params.useStateValues ) return true;
		if ( !reader.item.hasTarget() ) return true;
		
		String state = (String)reader.item.getTarget().getProperty("state");
		if (( state == null ) || ( state.length() == 0 )) {
			return res.params.extractNoState;
		}
		return pattern.matcher(state).find();
	}
}
