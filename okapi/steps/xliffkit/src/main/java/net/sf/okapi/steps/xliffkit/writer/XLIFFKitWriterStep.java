/*===========================================================================
  Copyright (C) 2010-2011 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.steps.xliffkit.writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.EventType;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.MimeTypeMapper;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.encoder.EncoderManager;
import net.sf.okapi.common.filterwriter.XLIFFContent;
import net.sf.okapi.common.filterwriter.XLIFFWriter;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.Ending;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartDocument;
import net.sf.okapi.common.resource.StartGroup;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.persistence.IPersistenceSession;
import net.sf.okapi.lib.persistence.PersistenceSession;
import net.sf.okapi.lib.beans.v1.OkapiBeans;
import net.sf.okapi.persistence.json.jackson.JSONPersistenceSession;
import net.sf.okapi.lib.beans.sessions.OkapiJsonSession;
import net.sf.okapi.steps.xliffkit.opc.TKitRelationshipTypes;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.StreamHelper;
import org.apache.poi.openxml4j.opc.TargetMode;

@SuppressWarnings("unused")
@UsingParameters(Parameters.class)
public class XLIFFKitWriterStep extends BasePipelineStep {

	private XLIFFWriter writer;
	
	private LocaleId srcLoc;
	private LocaleId trgLoc;
	//private boolean inFile;
	private String docMimeType;
	private String docName;
	private String inputEncoding;
	private String configId;
	private Parameters params;
	private URI outputURI;
	
	private String resourcesFileExt = ".json";
	private String originalFileName;
	private String sourceFileName;
	private String xliffFileName;
	private String skeletonFileName;	
	private String resourcesFileName;
	
	private String originalPartName;
	private String sourcePartName;
	private String xliffPartName;
	private String skeletonPartName;	
	private String resourcesPartName;
	
	private String filterWriterClassName;

	private OPCPackage pack;
	private File tempXliff;
	private File tempResources;
	private PersistenceSession session;  // resource session
	private List<String> sources = new ArrayList<String> ();
	private List<String> originals = new ArrayList<String> ();
	
	public XLIFFKitWriterStep() {
		super();
		params = new Parameters();		
		session = new OkapiJsonSession();
		writer = new XLIFFWriter();
	}
	
	public String getDescription () {
		return "Generate an XLIFF translation kit. Expects: filter events. Sends back: filter events.";
	}

	public String getName () {
		return "XLIFF Kit Writer";
	}

	public void close() {
		if ( writer != null ) {
			writer.close();
		}
	}

	public EncoderManager getEncoderManager() {
		return null;
	}

	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALE)
	public void setTargetLocale (LocaleId targetLocale) {
		this.trgLoc = targetLocale;
	}

	@StepParameterMapping(parameterType = StepParameterType.OUTPUT_URI)
	public void setOutputURI (URI outputURI) {
		this.outputURI = outputURI;
	}
	
	public Event handleEvent (Event event) {
		//System.out.println(event.getEventType());
		switch ( event.getEventType() ) {
		case NO_OP:
			return event;
		case START_BATCH:			
			processStartBatch();
			break;
		case END_BATCH:
			processEndBatch();
			break;
		case START_DOCUMENT:			
			processStartDocument((StartDocument)event.getResource());
			break;
		case END_DOCUMENT:
			session.serialize(event);
			processEndDocument(); // Closes persistence session
			close();
			break;
		case START_SUBDOCUMENT:
			processStartSubDocument((StartSubDocument)event.getResource());
			break;
		case END_SUBDOCUMENT:
			processEndSubDocument((Ending)event.getResource());
			break;
		case START_GROUP:
			processStartGroup((StartGroup)event.getResource());
			break;
		case END_GROUP:
			processEndGroup((Ending)event.getResource());
			break;
		case TEXT_UNIT:
			ITextUnit tu = event.getTextUnit();
			processTextUnit(tu); // XLIFF
			Event ev = new Event(EventType.TEXT_UNIT, tu.clone());
			
//			TextContainer srcCont  = tu.getSource();
//			if ( !tu.getSource().contentIsOneSegment() ) {
//				srcCont.getSegments().joinAll();
//			}
//			TextFragment tf = srcCont.getFirstContent();
//			tf.setCodedText("", false);
			tu.removeTarget(trgLoc); // Also removes AltTranslationsAnnotations
						
			session.serialize(event); // JSON
			return ev;
		}
		session.serialize(event); // won't serialize END_DOCUMENT		
		return event;
	}
		
	private void processStartBatch() {		
		try {
			// If outputURI is defined explicitly in parameters, get it from there, otherwise use the one from the batch item
			if (params != null && !Util.isEmpty(params.getOutputURI())) {
				outputURI = new URI(params.getOutputURI());
			}
		} catch (URISyntaxException e) {
			// TODO Handle exception
			e.printStackTrace();
		}
		
		File outFile = new File(outputURI);
		if (outFile.exists()) 
			outFile.delete();
		
		Util.createDirectories(outFile.getAbsolutePath());
		try {
			pack = OPCPackage.openOrCreate(outFile);
		} catch (InvalidFormatException e1) {
			// TODO Handle exception
		}
		
		writer.setCopySource(params.isCopySource());
        writer.setPlaceholderMode(params.isPlaceholderMode());
		
		session.setDescription(params.getMessage());
	}
	
	private void processEndBatch() {
		sources.clear();
		originals.clear();
		try {
			pack.close();
			
		} catch (IOException e) {
			// TODO Handle exception
		}
	}
	
	private void processStartDocument (StartDocument resource) {
		close();

		srcLoc = resource.getLocale();						
		docMimeType = resource.getMimeType();
		docName = resource.getName();
		inputEncoding = resource.getEncoding();
		
		IParameters fparams = resource.getFilterParameters();
		if ( fparams == null ) configId = null;
		else configId = fparams.getPath();
		
		originalFileName = Util.getFilename(docName, true);
		sourceFileName = Util.getFilename(docName, true);
		xliffFileName = originalFileName + ".xlf";
		resourcesFileName = originalFileName + resourcesFileExt;
		skeletonFileName = String.format("resources/%s/%s", sourceFileName, resourcesFileName);
		
		filterWriterClassName = resource.getFilterWriter().getClass().getName();
		
		try {
			tempXliff = File.createTempFile(xliffFileName, null);
			tempXliff.deleteOnExit();
			
			tempResources = File.createTempFile(resourcesFileName, null);
			tempResources.deleteOnExit();
		
			writer.create(tempXliff.getAbsolutePath(), skeletonFileName, resource.getLocale(), trgLoc,
					resource.getMimeType(), sourceFileName, params.getMessage());
		} catch (IOException e) {
			// TODO Handle exception
			e.printStackTrace();
		}
		
		// Skeleton
		try {
			session.start(new FileOutputStream(tempResources));
		} catch (FileNotFoundException e) {
			// TODO Handle exception
		}
	}
	
	private PackagePart createPart(OPCPackage pack, PackagePart corePart, String name, File file, String contentType, String relationshipType) {		
		PackagePart part = null;
		try {			
			PackagePartName partName = PackagingURIHelper.createPartName("/" + name);
			if (pack.containPart(partName))	return null;
			
			part = pack.createPart(partName, contentType);
			if (corePart != null)
				corePart.addRelationship(partName, TargetMode.INTERNAL, relationshipType);
			else 
				pack.addRelationship(partName, TargetMode.INTERNAL, relationshipType);				
			
			try {
				InputStream is = new FileInputStream(file);
				OutputStream os = part.getOutputStream(); 
				StreamHelper.copyStream(is, os);
				try {
					is.close();
					os.close();
				} catch (IOException e) {
					// TODO Handle exception
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Handle exception
				e.printStackTrace();
			}
			
		} catch (InvalidFormatException e) {
			// TODO Handle exception
			e.printStackTrace();
		}
		return part;
	}
	
	private void processEndDocument () {		
		// Skeleton
		session.end();
		
		// XLIFF
		writer.close();
		
		// Save to package		
		originalPartName = String.format("content/original/%s/%s", srcLoc.toString(), originalFileName);
		sourcePartName = String.format("content/source/%s/%s", srcLoc.toString(), sourceFileName);
		xliffPartName = String.format("content/target/%s.%s/%s", srcLoc.toString(), trgLoc.toString(), xliffFileName);
		resourcesPartName = String.format("content/target/%s.%s/resources/%s/%s", srcLoc.toString(), trgLoc.toString(), sourceFileName, resourcesFileName);
		
		PackagePart corePart =
			createPart(pack, null, xliffPartName, tempXliff, MimeTypeMapper.XLIFF_MIME_TYPE, TKitRelationshipTypes.CORE_DOCUMENT);
		
		createPart(pack, corePart, resourcesPartName, tempResources, session.getMimeType(), TKitRelationshipTypes.RESOURCES);
		
		if (params.isIncludeSource())
			if (!sources.contains(docName)) {
				
				createPart(pack, corePart, sourcePartName, new File(docName), docMimeType, TKitRelationshipTypes.SOURCE);
				sources.add(docName);
			}
		
		if (params.isIncludeOriginal())
			if (!originals.contains(docName)) {
				createPart(pack, corePart, originalPartName, new File(docName), docMimeType, TKitRelationshipTypes.ORIGINAL);
				originals.add(docName);
			}
	}

	private void processStartSubDocument (StartSubDocument resource) {
		writer.writeStartFile(resource.getName(), resource.getMimeType(), skeletonFileName);
	}
	
	private void processEndSubDocument (Ending resource) {
		writer.writeEndFile();
	}
	
	private void processStartGroup (StartGroup resource) {
		writer.writeStartGroup(resource.getId(), resource.getName(), resource.getType());
	}
	
	private void processEndGroup (Ending resource) {
		writer.writeEndGroup();
	}
	
	private void processTextUnit (ITextUnit tu) {
		writer.writeTextUnit(tu);
	}
	
	@Override
	public IParameters getParameters() {
		return params;
	}

	public PersistenceSession getSession() {
		return session;
	}

	public void setSession(PersistenceSession session) {
		this.session = session;
	}

	public String getResourcesFileExt() {
		return resourcesFileExt;
	}

	public void setResourcesFileExt(String resourcesFileExt) {
		this.resourcesFileExt = resourcesFileExt;
	}
}
