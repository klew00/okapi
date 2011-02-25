package net.sf.okapi.filters.archive;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.ListUtil;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.exceptions.OkapiBadFilterInputException;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.exceptions.OkapiIllegalFilterOperationException;
import net.sf.okapi.common.filters.DefaultFilters;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilter;
import net.sf.okapi.common.filters.IFilterConfigurationMapper;
import net.sf.okapi.common.filterwriter.IFilterWriter;
import net.sf.okapi.common.filterwriter.ZipFilterWriter;
import net.sf.okapi.common.lds.ResourceUtil;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.common.skeleton.ZipSkeleton;

/**
 * Implements the IFilter interface for archive-based (ZIP) content. This filter generates events for
 * certain files in the archive. Those files are processed with sub-filters that are instantiated automatically
 * based on the files' extensions. Mapping between the extensions and filter configurations to be used is
 * defined either in the filter parameters, or with the FilterConfigurationMapper object passed to the constructor.
 * The files, for which extensions no sub-filter configId is provided, will be sent as document parts with ZipSkeleton.
 * <p>
 * MIME type of the format supported by the filter is not defined by the filter, but is specified via filter parameters. 
 * It allows to use the same filter for different container formats, e.g. DOCX, ODT, etc. by only changing a set of 
 * filter parameters.
 * <p>
 * Please note that when you configure this filter and specify sub-filter classes for the files of interest inside the container,
 * you are responsible to provide visibility of class loaders of those sub-filter classes you specify, otherwise the ArchiveFilter
 * won't be able to instantiate the sub-filters, and an exception will be thrown.
 * <p>
 * There are two ways to configure this filter:
 * <li> pass to the constructor a configured FilterConfigurationMapper object, which will include sub-filter configurations for the
 * internal files to be processed. Files are detected by their extensions, and FilterConfigurationMapper creates sub-filters for 
 * those extensions. Extensions and corresponding filter configurations in filter parameters are ignored.  
 * <li> specify in Parameters the comma-separated lists of file extensions and their corresponding config Ids. In this case the filter
 * should be instantiated with its empty constructor, and then it will create a FilterConfigurationMapper object and load it with default 
 * configurations specified by the DefaultFilters class. Make sure you use one of those default configurations, provided by the 
 * DefaultFilters class. You can use this approach to map one of default configurations to a *non-standard* file extension (i.e. one 
 * not listed among the configuration's file extensions). In case you need to use a custom configuration, consider the first way.
 */

public class ArchiveFilter implements IFilter {

	private enum NextAction {
		OPENZIP, NEXTINZIP, NEXTINSUBDOC, DONE
	}

	public static final String MIME_TYPE = "application/x-archive";
	
	private final String SID = "sd";
	private final String EID = "ed";
	
	private ZipFile zipFile;
	private ZipEntry entry;
	private ZipFilterWriter filterWriter;
	private NextAction nextAction;
	private URI docURI;
	private Enumeration<? extends ZipEntry> entries;
	private int subDocId;
	private LinkedList<Event> queue;
	private LocaleId srcLoc;
	private LocaleId trgLoc;
	private List<IFilter> subFilters;
	private IFilter subFilter;
	private Parameters params;
	//private String internalMimeType;
	private EncoderManager encoderManager;
	private String mimeType = MIME_TYPE;
	private IFilterConfigurationMapper fcMapper;
	private boolean setMapping;

	public ArchiveFilter() {
		super();
		params = new Parameters();
		params.setMimeType(MIME_TYPE);
		subFilters = new ArrayList<IFilter> ();		
		setMapping = true;	
		filterWriter = new ZipFilterWriter(null);
	}
	
	public ArchiveFilter(IFilterConfigurationMapper fcMapper) {
		this();
		setFilterConfigurationMapper(fcMapper);
	}
	
	@Override
	public void cancel() {
	}

	@Override
	public void close() {
		try {
			nextAction = NextAction.DONE;
			if ( zipFile != null ) {
				zipFile.close();
				zipFile = null;
			}
		}
		catch (IOException e) {
			throw new OkapiIOException(e);
		}
	}

	@Override
	public IFilterWriter createFilterWriter() {
		return filterWriter;
	}

	@Override
	public ISkeletonWriter createSkeletonWriter() {
		return null; // There is no corresponding skeleton writer
	}

	@Override
	public List<FilterConfiguration> getConfigurations() {
		List<FilterConfiguration> list = new ArrayList<FilterConfiguration>();
		list.add(new FilterConfiguration(getName(),
			MIME_TYPE,
			getClass().getName(),
			"Archive files",
			"Archive files",
			null,
			".archive;")); 
		// TODO Add mapping for these extensions to FormatManager in
		// okapi-application-rainbow/src/main/java/net/sf/okapi/applications/rainbow/lib/ 
		return list;
	}

	@Override
	public String getDisplayName() {
		return "Archive Filter (BETA)";
	}

	@Override
	public EncoderManager getEncoderManager() {
		return encoderManager; // There is no corresponding encoder manager, encoding is handled by sub-filters
	}

	@Override
	public String getMimeType() {
		return params == null ? MIME_TYPE : params.getMimeType();
	}

	@Override
	public String getName() {
		return "okf_archive";
	}

	@Override
	public IParameters getParameters() {
		return params;
	}

	@Override
	public boolean hasNext() {
		return ((( queue != null ) && ( !queue.isEmpty() ))
			|| ( nextAction != NextAction.DONE ));
	}

	@Override
	public Event next() {
		// Send remaining event from the queue first
		if ( queue.size() > 0 ) {
			return queue.poll();
		}
		
		// When the queue is empty: process next action
		switch ( nextAction ) {
		case OPENZIP:
			return openZipFile();
		case NEXTINZIP:
			return nextInZipFile();
		case NEXTINSUBDOC:
			return nextInSubDocument();
		default:
			throw new OkapiIllegalFilterOperationException("Invalid next() call.");
		}
	}

	@Override
	public void open(RawDocument input) {
		open(input, true);
	}

	@Override
	public void open(RawDocument input, boolean generateSkeleton) {
		close();
		docURI = input.getInputURI();
		if ( docURI == null ) {
			throw new OkapiBadFilterInputException("This filter supports only URI input.");
		}
		nextAction = NextAction.OPENZIP;
		queue = new LinkedList<Event>();
		srcLoc = input.getSourceLocale();
		trgLoc = input.getTargetLocale();
		
		if (params != null) {
			mimeType = params.getMimeType();
		}
		// Configure fcMapper for given parameters
		if (setMapping) {
			this.fcMapper = new FilterConfigurationMapper();
			DefaultFilters.setMappings(fcMapper, true, true);
			
			List<FilterConfiguration> usedConfigs = new ArrayList<FilterConfiguration> ();
			String[] extensions = ListUtil.stringAsArray(params.getFileExtensions());
			String[] configIds = ListUtil.stringAsArray(params.getConfigIds());
			if ((configIds.length > 0) && (extensions.length != configIds.length)) {
				throw new RuntimeException("Different number of configIds and extensions in parameters");
			}
			for (int i = 0; i < configIds.length; i++) {
				String configId = configIds[i];
				
				FilterConfiguration fc = fcMapper.getConfiguration(configId);
				if (fc == null) 
					fc = new FilterConfiguration();
				else
					usedConfigs.add(fc);
				
				String ext = extensions[i];
				if (!ext.startsWith(".")) ext = "." + ext;
					
				fc.configId = configId;
				if (fc.extensions == null)
					fc.extensions = "";
				if (fc.extensions.indexOf(ext + ";") < 0) {
					fc.extensions += ext + ";";
				}
				
			}
			
			fcMapper.clearConfigurations(false);
			for (FilterConfiguration config : usedConfigs) {
				fcMapper.addConfiguration(config);
			}
		}		
	}

	@Override
	public void setFilterConfigurationMapper(IFilterConfigurationMapper fcMapper) {
		this.fcMapper = fcMapper;
		setMapping = fcMapper == null;
	}

	@Override
	public void setParameters(IParameters params) {
		this.params = (Parameters)params;
		//if ( subFilter != null ) subFilter.setParameters(params);
		for (IFilter subFilter : subFilters) {
			subFilter.setParameters(params);
		}
	}

	private Event openZipFile () {
		try {
			zipFile = new ZipFile(new File(docURI));
			entries = zipFile.entries();
			subDocId = 0;
			nextAction = NextAction.NEXTINZIP;
//			// Initialize the internalMime type
//			getInternalMimeType();
			
			StartDocument startDoc = new StartDocument(SID);
			startDoc.setName(docURI.getPath());
			startDoc.setLocale(srcLoc);
			startDoc.setMimeType(mimeType);
			startDoc.setFilterParameters(params);
			startDoc.setFilterWriter(filterWriter);
			startDoc.setLineBreak("\n"); // forced
			startDoc.setEncoding("UTF-8", false); // Forced
			return new Event(EventType.START_DOCUMENT, startDoc);
		}
		catch ( ZipException e ) {
			throw new OkapiIOException(e);
		}
		catch ( IOException e ) {
			throw new OkapiIOException(e);
		}
	}
	
	private Event nextInZipFile () {
		while( entries.hasMoreElements() ) {
			entry = entries.nextElement();
			subFilter = getSubFilter(entry.getName());
			if (subFilter != null) {	
				filterWriter.setSubDocWriter(subFilter.createFilterWriter());
				return openSubDocument();
			}
			else {
				DocumentPart dp = new DocumentPart(entry.getName(), false);
				ZipSkeleton skel = new ZipSkeleton(zipFile, entry);
				return new Event(EventType.DOCUMENT_PART, dp, skel);
			}
		}

		// No more sub-documents: end of the ZIP document
		close();
		Ending ending = new Ending(EID);
		return new Event(EventType.END_DOCUMENT, ending);
	}
	
	private IFilter getSubFilter(String name) {
		// Get extension (with no dot)
		String ext = Util.getExtension(name);
		if (Util.isEmpty(ext)) return null;
		//if (ext.startsWith(".")) ext = ext.substring(1); // remove the leading dot
		
		// Lookup config for this extension
		FilterConfiguration config = fcMapper.getDefaultConfigurationFromExtension(ext);
		if (config == null) return null;
		
		// Create sub-filter		
		return fcMapper.createFilter(config.configId);
	}

	private Event openSubDocument () {
		if (subFilter != null) subFilter.close();
		Event event;
		try {
			subFilter.open(new RawDocument(zipFile.getInputStream(entry), "UTF-8", srcLoc, trgLoc));
			event = subFilter.next(); // START_DOCUMENT
			//filter.setContainerMimeType(internalMimeType);
		}
		catch (IOException e) {
			throw new OkapiIOException("Error opening internal file.", e);
		}
		
		// Change the START_DOCUMENT event from sub-filter to START_SUBDOCUMENT
		StartDocument sd = null;
		StartSubDocument ssd = null;
		if (event.getEventType() == EventType.START_DOCUMENT) {
			sd = (StartDocument) event.getResource();
			ssd = new StartSubDocument(SID, sd.getId());
		}
		else
			ssd = new StartSubDocument(SID, String.valueOf(++subDocId));
		
		ResourceUtil.copyProperties(sd, ssd);
		ssd.setName(docURI.getPath() + "/" + entry.getName()); // Use '/'
		nextAction = NextAction.NEXTINSUBDOC;
		ZipSkeleton skel = new ZipSkeleton(
			(GenericSkeleton)event.getResource().getSkeleton(), zipFile, entry);
		return new Event(EventType.START_SUBDOCUMENT, ssd, skel);
	}
	
	private Event nextInSubDocument () {
		Event event;
		while ( subFilter.hasNext() ) {
			event = subFilter.next();
			switch ( event.getEventType() ) {
			case END_DOCUMENT:
				// Change the END_DOCUMENT to END_SUBDOCUMENT
				//Ending ending = new Ending(String.valueOf(++subDocId));
				Ending ending = (Ending) event.getResource();
				nextAction = NextAction.NEXTINZIP;
				ZipSkeleton skel = new ZipSkeleton(
					(GenericSkeleton)event.getResource().getSkeleton(), zipFile, entry);
				return new Event(EventType.END_SUBDOCUMENT, ending, skel);
			
			default: // Else: just pass the event through
				return event;
			}
		}
		return null; // Should not get here
	}
}
