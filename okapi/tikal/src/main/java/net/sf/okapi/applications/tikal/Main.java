/*===========================================================================
  Copyright (C) 2009-2012 by the Okapi Framework contributors
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

package net.sf.okapi.applications.tikal;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.okapi.common.FileUtil;
import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UserConfiguration;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiIOException;
import net.sf.okapi.common.filters.DefaultFilters;
import net.sf.okapi.common.filters.FilterConfiguration;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.filters.IFilterConfigurationEditor;
import net.sf.okapi.common.filters.IFilterConfigurationListEditor;
import net.sf.okapi.common.filterwriter.XLIFFWriter;
import net.sf.okapi.common.pipeline.IPipelineStep;
import net.sf.okapi.common.pipelinedriver.BatchItemContext;
import net.sf.okapi.common.pipelinedriver.PipelineDriver;
import net.sf.okapi.common.plugins.PluginsManager;
import net.sf.okapi.common.query.IQuery;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.connectors.apertium.ApertiumMTConnector;
import net.sf.okapi.connectors.globalsight.GlobalSightTMConnector;
import net.sf.okapi.connectors.google.GoogleMTv2Connector;
import net.sf.okapi.connectors.microsoft.MicrosoftMTConnector;
import net.sf.okapi.connectors.mymemory.MyMemoryTMConnector;
import net.sf.okapi.connectors.opentran.OpenTranTMConnector;
import net.sf.okapi.connectors.pensieve.PensieveTMConnector;
import net.sf.okapi.connectors.tda.TDASearchConnector;
import net.sf.okapi.connectors.translatetoolkit.TranslateToolkitTMConnector;
import net.sf.okapi.filters.mosestext.FilterWriterParameters;
import net.sf.okapi.lib.translation.ITMQuery;
import net.sf.okapi.steps.common.FilterEventsToRawDocumentStep;
import net.sf.okapi.steps.common.FilterEventsWriterStep;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;
import net.sf.okapi.steps.formatconversion.FormatConversionStep;
import net.sf.okapi.steps.formatconversion.Parameters;
import net.sf.okapi.steps.formatconversion.TableFilterWriterParameters;
import net.sf.okapi.steps.leveraging.LeveragingStep;
import net.sf.okapi.steps.moses.ExtractionStep;
import net.sf.okapi.steps.moses.MergingParameters;
import net.sf.okapi.steps.moses.MergingStep;
import net.sf.okapi.steps.segmentation.SegmentationStep;

public class Main {
	
	protected final static int CMD_EXTRACT = 0;
	protected final static int CMD_MERGE = 1;
	protected final static int CMD_EDITCONFIG = 2;
	protected final static int CMD_QUERYTRANS = 3;
	protected final static int CMD_CONV2PO = 4;
	protected final static int CMD_CONV2TMX = 5;
	protected final static int CMD_CONV2TABLE = 6;
	protected final static int CMD_CONV2PEN = 7;
	protected final static int CMD_TRANSLATE = 8;
	protected final static int CMD_EXTRACTTOMOSES = 9;
	protected final static int CMD_LEVERAGEMOSES = 10;
	protected final static int CMD_SEGMENTATION = 11;
	protected final static int CMD_SHOWCONFIGS = 12;
	protected final static int CMD_ADDTRANS = 13;
	
	private static final String DEFAULT_SEGRULES = "-";
	private static final String MSG_ONLYWITHUICOMP = "UI-based commands are available only in the distributions with UI components.";

	private static PrintStream ps;
	
	protected ArrayList<String> inputs;
	protected String skeleton;
	protected String output;
	protected String specifiedConfigId;
	protected String specifiedConfigIdPath;
	protected String configId;
	protected String inputEncoding;
	protected String outputEncoding;
	protected LocaleId srcLoc;
	protected LocaleId trgLoc;
	protected int command = -1;
	protected String query;
	protected String addTransTrans;
	protected int addTransRating = 4;
	protected boolean useGoogleV2;
	protected String googleV2Params;
	protected boolean useOpenTran;
	protected boolean useTransToolkit;
	protected String transToolkitParams;
	protected boolean useGlobalSight;
	protected String globalSightParams;
	protected boolean useTDA;
	protected String tdaParams;
	protected boolean useMyMemory;
	protected String myMemoryParams;
	protected boolean useApertium;
	protected String apertiumParams;
	protected boolean usePensieve;
	protected String pensieveData;
	protected boolean useMicrosoft;
	protected String microsoftParams;
//	protected boolean useProMT;
//	protected String proMTParams;
	protected boolean genericOutput = false;
	protected String tableConvFormat;
	protected String tableConvCodes;
	protected int convTargetStyle = net.sf.okapi.steps.formatconversion.Parameters.TRG_TARGETOREMPTY;
	protected boolean convSkipEntriesWithoutText = true;
	protected boolean convOverwrite = false;
	protected String segRules;
	protected boolean showTraceHint = true;
	protected String tmOptions;
	protected boolean levOptFillTarget = true;
	protected String levOptTMXPath;
	protected boolean extOptCopy = true; // Copy source in empty target by default
	protected boolean extOptAltTrans = true; // Output alt-trans by default
	protected boolean mosesCopyToTarget = false;
	protected boolean mosesOverwriteTarget = false;
	protected boolean moses2Outputs = false;
	protected boolean mosesUseGModeInAltTrans = true;
	protected String mosesFromPath;
	protected String mosesToPath;
	protected String skeletonDir;
	protected String outputDir;
	protected String rootDir = System.getProperty("user.dir");
	
	private FilterConfigurationMapper fcMapper;
	private Hashtable<String, String> extensionsMap;
	private Hashtable<String, String> filtersMap;

	/**
	 * Try the guess the encoding of the console.
	 * @return the guessed name of the console's encoding.
	 */
	private static String getConsoleEncodingName () {
		String osName = System.getProperty("os.name");
		String enc = null;
		if ( osName.startsWith("Mac OS")) {
			enc = "UTF-8"; // Apparently the default for bash on Mac
		}
		else if ( osName.startsWith("Windows") ) {
			enc = "cp850"; // Not perfect, but covers many languages
		}
		else {
			// Default: Assumes unique encoding overall
			enc = Charset.defaultCharset().name();
		}
		// Now check if we have a user setting
		UserConfiguration uc = new UserConfiguration();
		uc.load("Tikal");
		return uc.getProperty("displayEncoding", enc);
	}
	
	public static void main (String[] originalArgs) {
		Main prog = new Main();
	
		boolean showTrace = false;
		try {
			// Create an encoding-aware output for the console
			// System.out uses the default system encoding that
			// may not be the right one (e.g. windows-1252 vs cp850)
			ps = new PrintStream(System.out, true, getConsoleEncodingName());

			// Remove all empty arguments
			// This is to work around the "$1" issue in bash
			ArrayList<String> args = new ArrayList<String>();
			for ( String tmp : originalArgs ) {
				if ( tmp.length() > 0 ) args.add(tmp);
			}
			
			prog.printBanner();
			if ( args.size() == 0 ) {
				prog.printUsage();
				return;
			}
			if ( args.contains("-?") ) {
				prog.printUsage();
				return; // Overrides all arguments 
			}
			if ( args.contains("-h") || args.contains("--help") || args.contains("-help") ) {
				prog.showHelp();
				return; // Overrides all arguments
			}
			if ( args.contains("-i") || args.contains("--info")  || args.contains("-info") ) {
				prog.showInfo();
				return; // Overrides all arguments 
			}
			if ( args.contains("-trace") ) {
				// Check early so the option does not get 'eaten' by a bad syntax
				showTrace = true;
			}
			
			for ( int i=0; i<args.size(); i++ ) {
				String arg = args.get(i);
				if ( arg.equals("-fc") ) {
					prog.specifiedConfigId = prog.getArgument(args, ++i);
				}
				else if ( arg.equals("-sl") ) {
					prog.srcLoc = new LocaleId(prog.getArgument(args, ++i), true);
				}
				else if ( arg.equals("-tl") ) {
					prog.trgLoc = new LocaleId(prog.getArgument(args, ++i), true);
				}
				else if ( arg.equals("-ie") ) {
					prog.inputEncoding = prog.getArgument(args, ++i);
				}
				else if ( arg.equals("-oe") ) {
					prog.outputEncoding = prog.getArgument(args, ++i);
				}
				else if ( arg.equals("-od") ) {
					prog.outputDir = prog.getArgument(args, ++i);
				}
				else if ( arg.equals("-sd") ) {
					prog.skeletonDir = prog.getArgument(args, ++i);
				}
				else if ( arg.equals("-rd") ) {
					prog.rootDir = prog.getArgument(args, ++i);
				}
				else if ( arg.equals("-x") ) {
					prog.command = CMD_EXTRACT;
				}
				else if ( arg.equals("-xm") ) {
					prog.command = CMD_EXTRACTTOMOSES;
				}
				else if ( arg.equals("-2") ) {
					prog.moses2Outputs = true;
				}
				else if ( arg.equals("-t") ) {
					prog.command = CMD_TRANSLATE;
				}
				else if ( arg.equals("-m") ) {
					prog.command = CMD_MERGE;
				}
				else if ( arg.equals("-lm") ) {
					prog.command = CMD_LEVERAGEMOSES;
				}
				else if ( arg.equals("-totrg") ) {
					prog.mosesCopyToTarget = true;
					prog.mosesOverwriteTarget = false;
				}
				else if ( arg.equals("-overtrg") ) {
					prog.mosesCopyToTarget = true;
					prog.mosesOverwriteTarget = true;
				}
				else if ( arg.equals("-bpt") ) {
					prog.mosesUseGModeInAltTrans = false;
				}
				else if ( arg.equals("-over") ) {
					prog.convOverwrite = true;
				}
				else if ( arg.equals("-from")) {
					prog.mosesFromPath = prog.getArgument(args, ++i);
				}
				else if ( arg.equals("-to") ) {
					prog.mosesToPath = prog.getArgument(args, ++i);
				}
				else if ( arg.equals("-2po") ) {
					prog.command = CMD_CONV2PO;
				}
				else if ( arg.equals("-2tmx") ) {
					prog.command = CMD_CONV2TMX;
				}
				else if ( arg.equals("-2tbl") ) {
					prog.command = CMD_CONV2TABLE;
				}
				else if ( arg.equals("-csv") ) {
					prog.tableConvFormat = "csv";
				}
				else if ( arg.equals("-tab") ) {
					prog.tableConvFormat = "tab";
				}
				else if ( arg.equals("-xliff") ) {
					prog.tableConvCodes = TableFilterWriterParameters.INLINE_XLIFF;
				}
				else if ( arg.equals("-xliffgx") ) {
					prog.tableConvCodes = TableFilterWriterParameters.INLINE_XLIFFGX;
				}
				else if ( arg.equals("-tmx") ) {
					prog.tableConvCodes = TableFilterWriterParameters.INLINE_TMX;
				}
				else if ( arg.equals("-all") ) {
					prog.convSkipEntriesWithoutText = false;
				}
				else if ( arg.equals("-nofill") ) {
					prog.levOptFillTarget = false;
				}
				else if ( arg.equals("-nocopy") ) {
					prog.extOptCopy = false;
				}
				else if ( arg.equals("-noalttrans") ) {
					prog.extOptAltTrans = false;
				}
				else if ( arg.equals("-maketmx") ) {
					prog.levOptTMXPath = "pretrans.tmx";
					if ( args.size() > i+1 ) {
						if ( !args.get(i+1).startsWith("-") ) {
							prog.levOptTMXPath = args.get(++i);
						}
					}
				}
				else if ( arg.equals("-trgsource") ) {
					prog.convTargetStyle = net.sf.okapi.steps.formatconversion.Parameters.TRG_FORCESOURCE;
				}
				else if ( arg.equals("-trgempty") ) {
					prog.convTargetStyle = net.sf.okapi.steps.formatconversion.Parameters.TRG_FORCEEMPTY;
				}
				else if ( arg.equals("-imp") ) {
					prog.command = CMD_CONV2PEN;
					prog.pensieveData = prog.getArgument(args, ++i);
				}
				else if ( arg.equals("-exp") ) {
					prog.command = CMD_CONV2TMX;
					prog.specifiedConfigId = "okf_pensieve";
				}
				else if ( arg.equals("-e") ) {
					prog.command = CMD_EDITCONFIG;
					if ( args.size() > i+1 ) {
						if ( !args.get(i+1).startsWith("-") ) {
							prog.specifiedConfigId = args.get(++i);
						}
					}
				}
				else if ( arg.equals("-generic") ) {
					prog.genericOutput = true;
					prog.tableConvCodes = TableFilterWriterParameters.INLINE_GENERIC;
				}
				else if ( arg.equals("-q") ) {
					prog.command = CMD_QUERYTRANS;
					prog.query = prog.getArgument(args, ++i);
				}
				else if ( arg.equals("-a") ) {
					prog.command = CMD_ADDTRANS;
					prog.query = prog.getArgument(args, ++i);
					prog.addTransTrans = prog.getArgument(args, ++i);
					if ( args.size() > i+1 ) {
						if ( !args.get(i+1).startsWith("-") ) {
							// Optional rating
							try {
								prog.addTransRating = Integer.parseInt(args.get(++i));
							}
							catch ( NumberFormatException e ) {
								throw new RuntimeException(String.format("Invalid rating option: '%s'.", args.get(i)));
							}
							if (( prog.addTransRating < -10 ) || ( prog.addTransRating > 10 )) {
								throw new RuntimeException("Rating must be between -10 and 10.");
							}
						}
					}
				}
				else if ( arg.equals("-opt") ) {
					prog.tmOptions = prog.getArgument(args, ++i);
				}
				else if ( arg.equals("-gg") || arg.equals("-google") ) {
					prog.useGoogleV2 = true;
					if ( args.size() > i+1 ) {
						if ( !args.get(i+1).startsWith("-") ) {
							prog.googleV2Params = args.get(++i);
						}
					}
				}
				else if ( arg.equals("-opentran") ) {
					prog.useOpenTran = true;
				}
				else if ( arg.equals("-tt") ) {
					prog.useTransToolkit = true;
					prog.transToolkitParams = "amagama.locamotion.org:80";
					if ( args.size() > i+1 ) {
						if ( !args.get(i+1).startsWith("-") ) {
							prog.transToolkitParams = args.get(++i);
						}
					}
				}
				else if ( arg.equals("-gs") ) {
					prog.useGlobalSight = true;
					prog.globalSightParams = prog.getArgument(args, ++i);
				}
				else if ( arg.equals("-tda") ) {
					prog.useTDA = true;
					prog.tdaParams = prog.getArgument(args, ++i);
				}
				else if ( arg.equals("-ms") ) {
					prog.useMicrosoft = true;
					if ( args.size() > i+1 ) {
						if ( !args.get(i+1).startsWith("-") ) {
							prog.microsoftParams = args.get(++i);
						}
					}
				}
//				else if ( arg.equals("-promt") ) {
//					prog.useProMT = true;
//					if ( args.size() > i+1 ) {
//						if ( !args.get(i+1).startsWith("-") ) {
//							prog.proMTParams = args.get(++i);
//						}
//					}
//				}
				else if ( arg.equals("-apertium") ) {
					prog.useApertium = true;
					if ( args.size() > i+1 ) {
						if ( !args.get(i+1).startsWith("-") ) {
							prog.apertiumParams = args.get(++i);
						}
					}
				}
				else if ( arg.equals("-mm") ) {
					prog.useMyMemory = true;
					// Key is optional (left for backward compatibility)
					if ( args.size() > i+1 ) {
						if ( !args.get(i+1).startsWith("-") ) {
							prog.myMemoryParams = prog.getArgument(args, ++i);
						}
					}
				}
				else if ( arg.equals("-pen") ) {
					prog.usePensieve = true;
					prog.pensieveData = "http://localhost:8080";
					if ( args.size() > i+1 ) {
						if ( !args.get(i+1).startsWith("-") ) {
							prog.pensieveData = args.get(++i);
						}
					}
				}
				else if ( arg.endsWith("-listconf") || arg.equals("-lfc") ) {
					prog.command = CMD_SHOWCONFIGS;
				}
				else if ( arg.equals("-s") ) {
					prog.command = CMD_SEGMENTATION;
					prog.segRules = DEFAULT_SEGRULES;
				}
				else if ( arg.equals("-seg") ) {
					prog.segRules = DEFAULT_SEGRULES; // Default
					if ( args.size() > i+1 ) {
						if ( !args.get(i+1).startsWith("-") ) {
							prog.segRules = args.get(++i);
						}
					}
				}
				else if ( arg.equals("-trace") ) {
					// Trace aAlready set. this is just to avoid
					// seeing -trace as invalid parameter
				}
				//=== Input file or error
				else if ( !arg.startsWith("-") ) {
					prog.inputs.add(args.get(i));
				}
				else {
					prog.showTraceHint = false; // Using trace is not helpful to the user for this error
					throw new InvalidParameterException(
						String.format("Invalid command-line argument '%s'.", args.get(i)));
				}
			}

			// Forgive having the extension .fprm from configuration ID if there is one
			if ( prog.specifiedConfigId != null ) {
				String cfgPath = Util.getDirectoryName(prog.specifiedConfigId);
				if ( !cfgPath.isEmpty() ) {
					prog.specifiedConfigIdPath = cfgPath;
					prog.specifiedConfigId = Util.getFilename(prog.specifiedConfigId, true);
				}
				if ( prog.specifiedConfigId.endsWith(FilterConfigurationMapper.CONFIGFILE_EXT) ) {
					prog.specifiedConfigId = Util.getFilename(prog.specifiedConfigId, false);
				}
			}
			
			// Check inputs and command
			if ( prog.command == -1 ) {
				ps.println("No command specified. Please use one of the command described below:");
				prog.printUsage();
				return;
			}
			if ( prog.command == CMD_EDITCONFIG ) {
				if ( prog.specifiedConfigId == null ) {
					prog.editAllConfigurations();
				}
				else {
					prog.editConfiguration();
				}
				return;
			}
			if ( prog.command == CMD_SHOWCONFIGS ) {
				prog.showAllConfigurations();
				return;
			}
			if ( prog.command == CMD_QUERYTRANS ) {
				prog.processQuery();
				return;
			}
			if ( prog.command == CMD_ADDTRANS ) {
				prog.processAddTranslation();
				return;
			}
			if ( prog.inputs.size() == 0 ) {
				throw new RuntimeException("No input document specified.");
			}
			
			// Process all input files
			for ( int i=0; i<prog.inputs.size(); i++ ) {
				if ( i > 0 ) {
					ps.println("------------------------------------------------------------"); //$NON-NLS-1$
				}
				prog.process(prog.inputs.get(i));
			}
		}
		catch ( Throwable e ) {
			if ( showTrace ) e.printStackTrace();
			else {
				ps.println("ERROR: "+e.getMessage());
				Throwable e2 = e.getCause();
				if ( e2 != null ) ps.println(e2.getMessage());
				if ( prog.showTraceHint ) ps.println("You can use the -trace option for more details.");
			}
			System.exit(1); // Error
		}
	}

	public Main () {
		inputs = new ArrayList<String>();
	}
	
	protected String getArgument (ArrayList<String> args, int index) {
		if ( index >= args.size() ) {
			showTraceHint = false; // Using trace is not helpful to the user for this error
			throw new RuntimeException(String.format(
				"Missing parameter after '%s'", args.get(index-1)));
		}
		return args.get(index);
	}
	
	private void initialize () {
		// Create the mapper and load it with all parameters editor info
		// Do not load the filter configurations yet (time consuming)
		fcMapper = new FilterConfigurationMapper();
		DefaultFilters.setMappings(fcMapper, false, false);
		
		// Instead create a map with extensions -> filter
		extensionsMap = new Hashtable<String, String>();
		filtersMap = new Hashtable<String, String>();
		
		extensionsMap.put(".docx", "okf_openxml");
		extensionsMap.put(".pptx", "okf_openxml");
		extensionsMap.put(".xlsx", "okf_openxml");
		filtersMap.put("okf_openxml", "net.sf.okapi.filters.openxml.OpenXMLFilter");

		extensionsMap.put(".odt", "okf_openoffice");
		extensionsMap.put(".swx", "okf_openoffice");
		extensionsMap.put(".ods", "okf_openoffice");
		extensionsMap.put(".swc", "okf_openoffice");
		extensionsMap.put(".odp", "okf_openoffice");
		extensionsMap.put(".sxi", "okf_openoffice");
		extensionsMap.put(".odg", "okf_openoffice");
		extensionsMap.put(".sxd", "okf_openoffice");
		filtersMap.put("okf_openoffice", "net.sf.okapi.filters.openoffice.OpenOfficeFilter");

		extensionsMap.put(".htm", "okf_html");
		extensionsMap.put(".html", "okf_html");
		filtersMap.put("okf_html", "net.sf.okapi.filters.html.HtmlFilter");
		
		extensionsMap.put(".xlf", "okf_xliff");
		extensionsMap.put(".xlif", "okf_xliff");
		extensionsMap.put(".xliff", "okf_xliff");
		filtersMap.put("okf_xliff", "net.sf.okapi.filters.xliff.XLIFFFilter");
		
		extensionsMap.put(".tmx", "okf_tmx");
		filtersMap.put("okf_tmx", "net.sf.okapi.filters.tmx.TmxFilter");
		
		extensionsMap.put(".properties", "okf_properties");
		extensionsMap.put(".lang", "okf_properties-skypeLang");
		filtersMap.put("okf_properties", "net.sf.okapi.filters.properties.PropertiesFilter");
		
		extensionsMap.put(".po", "okf_po");
		filtersMap.put("okf_po", "net.sf.okapi.filters.po.POFilter");
		
		extensionsMap.put(".xml", "okf_xml");
		extensionsMap.put(".resx", "okf_xml-resx");
		filtersMap.put("okf_xml", "net.sf.okapi.filters.xml.XMLFilter");
		
		extensionsMap.put(".srt", "okf_regex-srt");
		filtersMap.put("okf_regex", "net.sf.okapi.filters.regex.RegexFilter");
		
		extensionsMap.put(".dtd", "okf_dtd");
		extensionsMap.put(".ent", "okf_dtd");
		filtersMap.put("okf_dtd", "net.sf.okapi.filters.dtd.DTDFilter");
		
		extensionsMap.put(".ts", "okf_ts");
		filtersMap.put("okf_ts", "net.sf.okapi.filters.ts.TsFilter");
		
		extensionsMap.put(".txt", "okf_plaintext");
		filtersMap.put("okf_plaintext", "net.sf.okapi.filters.plaintext.PlainTextFilter");

		extensionsMap.put(".csv", "okf_table_csv");
		filtersMap.put("okf_table", "net.sf.okapi.filters.table.TableFilter");

		extensionsMap.put(".ttx", "okf_ttx");
		filtersMap.put("okf_ttx", "net.sf.okapi.filters.ttx.TTXFilter");

		extensionsMap.put(".json", "okf_json");
		filtersMap.put("okf_json", "net.sf.okapi.filters.json.JSONFilter");

		filtersMap.put("okf_phpcontent", "net.sf.okapi.filters.php.PHPContentFilter");

		extensionsMap.put(".pentm", "okf_pensieve");
		filtersMap.put("okf_pensieve", "net.sf.okapi.filters.pensieve.PensieveFilter");

		filtersMap.put("okf_vignette", "net.sf.okapi.filters.vignette.VignetteFilter");

		extensionsMap.put(".yml", "okf_railsyaml");
		filtersMap.put("okf_railsyaml", "net.sf.okapi.filters.railsyaml.RailsYamlFilter");

		extensionsMap.put(".idml", "okf_idml");
		filtersMap.put("okf_idml", "net.sf.okapi.filters.idml.IDMLFilter");

		extensionsMap.put(".mif", "okf_mif");
		filtersMap.put("okf_mif", "net.sf.okapi.filters.mif.MIFFilter");

		extensionsMap.put(".txp", "okf_transifex");
		filtersMap.put("okf_transifex", "net.sf.okapi.filters.transifex.TransifexFilter");

		extensionsMap.put(".rtf", "okf_tradosrtf");
		filtersMap.put("okf_tradosrtf", "net.sf.okapi.filters.rtf.RTFFilter");

		extensionsMap.put(".zip", "okf_archive");
		filtersMap.put("okf_archive", "net.sf.okapi.filters.archive.ArchiveFilter");

		extensionsMap.put(".txml", "okf_txml");
		filtersMap.put("okf_txml", "net.sf.okapi.filters.txml.TXMLFilter");

		filtersMap.put("okf_versifiedtxt", "net.sf.okapi.filters.versifiedtxt.VersifiedTextFilter");
		filtersMap.put("okf_xmlstream", "net.sf.okapi.filters.xmlstream.XmlStreamFilter");
		filtersMap.put("okf_mosestext", "net.sf.okapi.filters.mosestext.MosesTextFilter");
		filtersMap.put("okf_itshtml5", "net.sf.okapi.filters.its.html5.HTML5Filter");

		if ( specifiedConfigIdPath != null ) {
			fcMapper.setCustomConfigurationsDirectory(specifiedConfigIdPath);
		}
	}
	
	private String getConfigurationId (String ext) {
		// Get the configuration for the extension
		String id = extensionsMap.get(ext);
		if ( id == null ) {
			throw new RuntimeException(String.format(
				"Could not guess the configuration for the extension '%s'", ext));
		}
		return id;
	}
	
	private void editAllConfigurations () {
		initialize();
		guessMissingLocales(null);
		// Add all the pre-defined configurations
		DefaultFilters.setMappings(fcMapper, false, true);
		loadFromPluginsAndUpdate();
		// Add the custom configurations
		fcMapper.updateCustomConfigurations();

		// Edit
		try {
			// Invoke the editor using dynamic instantiation so we can compile non-UI distributions 
			IFilterConfigurationListEditor editor =
				(IFilterConfigurationListEditor)Class.forName("net.sf.okapi.common.ui.filters.FilterConfigurationEditor").newInstance();
			// Call the editor
			editor.editConfigurations(fcMapper);
		}
		catch ( InstantiationException e ) {
			throw new RuntimeException(MSG_ONLYWITHUICOMP);
		}
		catch ( IllegalAccessException e ) {
			throw new RuntimeException(MSG_ONLYWITHUICOMP);
		}
		catch ( ClassNotFoundException e ) {
			throw new RuntimeException(MSG_ONLYWITHUICOMP);
		}
	}
	
	private void editConfiguration () {
		initialize();
		guessMissingLocales(null);
		
		if ( specifiedConfigId == null ) {
			throw new RuntimeException("You must specified the configuration to edit.");
		}
		configId = specifiedConfigId;
		if ( !prepareFilter(configId) ) return; // Next input
		
		try {
			// Invoke the editor using dynamic instantiation so we can compile non-UI distributions 
			IFilterConfigurationEditor editor =
				(IFilterConfigurationEditor)Class.forName("net.sf.okapi.common.ui.filters.FilterConfigurationEditor").newInstance();
			// Call the editor
			editor.editConfiguration(configId, fcMapper);
		}
		catch ( InstantiationException e ) {
			throw new RuntimeException(MSG_ONLYWITHUICOMP);
		}
		catch ( IllegalAccessException e ) {
			throw new RuntimeException(MSG_ONLYWITHUICOMP);
		}
		catch ( ClassNotFoundException e ) {
			throw new RuntimeException(MSG_ONLYWITHUICOMP);
		}
	}
	
	private void showAllConfigurations () {
		initialize();
		DefaultFilters.setMappings(fcMapper, true, true);
		loadFromPluginsAndUpdate();
		// Add the custom configurations
		fcMapper.updateCustomConfigurations();

		ps.println("List of all filter configurations available:");
		Iterator<FilterConfiguration> iter = fcMapper.getAllConfigurations();
		FilterConfiguration config;
		while ( iter.hasNext() ) {
			config = iter.next();
			ps.println(String.format(" - %s = %s",
				config.configId, config.description));
		}
	}
	
	private boolean prepareFilter (String configId) {
		boolean pluginsDone = false;
		// Always add okf_html because it's used as sub-filter by several filters
		//TODO: Find a better way to handle sub-filter cases
		fcMapper.addConfigurations(filtersMap.get("okf_html"));
		while ( true ) {
			// Is it a default configuration?
			if ( filtersMap.containsKey(configId) ) {
				// Configuration ID is a default one:
				// Add its filter to the configuration mapper
				fcMapper.addConfigurations(filtersMap.get(configId));
				return true;
			}
			// Else: Try to find the filter for that configuration
			for ( String tmp : filtersMap.keySet() ) {
				if ( configId.startsWith(tmp) ) {
					fcMapper.addConfigurations(filtersMap.get(tmp));
					// If the given configuration is not one of the pre-defined
					if ( fcMapper.getConfiguration(configId) == null ) {
						// Assume it is a custom one
						fcMapper.addCustomConfiguration(configId);
					}
					return true;
				}
			}
			// No success yet?
			if ( pluginsDone ) break;
			// Try to load the plug-ins if it was not done yet
			loadFromPluginsAndUpdate();
			pluginsDone = true;
		}
		
		// Could not guess
		ps.println(String.format(
			"ERROR: Could not guess the filter for the configuration '%s'", configId));
		return false;
	}

	private void loadFromPluginsAndUpdate () {
		// Discover and add plug-ins
		PluginsManager mgt = new PluginsManager();
		mgt.discover(new File(getAppRootDirectory()+File.separator+"dropins"), true);
		fcMapper.addFromPlugins(mgt);
		// Now update the filtersMap with new configurations
		Iterator<FilterConfiguration> iter = fcMapper.getAllConfigurations();
		while ( iter.hasNext() ) {
			FilterConfiguration cfg = iter.next();
			if ( !filtersMap.containsKey(cfg.configId) ) {
				filtersMap.put(cfg.configId, cfg.filterClass);
			}
		}
	}
	
	private void guessMissingLocales (String inputPath) {
		// If both locales are already set: just use those
		if (( srcLoc != null ) && ( trgLoc != null )) return;
		
		// Try to see if we can get one or both from the input file
		if ( inputPath != null ) {
			List<String> guessed = FileUtil.guessLanguages(inputPath);
			if ( guessed.size() > 0 ) {
				if ( srcLoc == null ) {
					srcLoc = LocaleId.fromString(guessed.get(0));
				}
				if ( guessed.size() > 1 ) {
					if ( trgLoc == null ) {
						trgLoc = LocaleId.fromString(guessed.get(1));
					}
				}
			}
		}

		// Make sure we do have a source
		if ( srcLoc == null ) {
			srcLoc = new LocaleId("en", false);
		}
		// Make sure we do have a target
		if ( trgLoc == null ) {
			trgLoc = new LocaleId(Locale.getDefault());
			if ( trgLoc.sameLanguageAs(srcLoc) ) {
				trgLoc = new LocaleId("fr", false);
			}
		}
	}
	
	private void guessMissingParameters (String inputOfConfig) {
		if ( specifiedConfigId == null ) {
			String ext = Util.getExtension(inputOfConfig);
			if ( Util.isEmpty(ext) ) {
				throw new RuntimeException(String.format(
					"The input file '%s' has no extension to guess the filter from.", inputOfConfig));
			}
			configId = getConfigurationId(ext.toLowerCase());
		}
		else {
			configId = specifiedConfigId;
		}
		
		if ( outputEncoding == null ) {
			if ( inputEncoding != null ) outputEncoding = inputEncoding;
			else outputEncoding = Charset.defaultCharset().name();
		}
		if ( inputEncoding == null ) {
			inputEncoding = Charset.defaultCharset().name();
		}
	}
	
	String pathChangeFolder (String newFolder,
		String oldPath)
	{
		String result;
		if ( newFolder == null ) {
			result = oldPath;
		}
		else {
			File file = new File(newFolder, Util.getFilename(oldPath, true));
			result = file.toString();
		}
		return result;
	}

	String pathInsertOutBeforeExt(String oldPath) {
		String ext = Util.getExtension(oldPath);
		int n = oldPath.lastIndexOf('.');
		return oldPath.substring(0, n) + ".out" + ext; //$NON-NLS-1$
	}

	private void guessMergingArguments (String input) {
		String ext = Util.getExtension(input);
		if ( !ext.equals(".xlf") ) {
			throw new RuntimeException(String.format(
				"The input file '%s' does not have the expected .xlf extension.", input));
		}
		
		int n = input.lastIndexOf('.');
		skeleton = input.substring(0, n);

		if ( outputDir == null ) {
			output = pathInsertOutBeforeExt(skeleton);
		}
		else {
			output = pathChangeFolder(outputDir, skeleton);
		}

		skeleton = pathChangeFolder(skeletonDir, skeleton);
	}
	
	private void guessMergingMosesArguments (String input) {
		// Main input is the original file, not the Moses file
		// The Moses file is specified with -from or null
		if ( Util.isEmpty(mosesFromPath) ) {
			// We guess the Moses filename:
			mosesFromPath = input + "."+trgLoc.toString();
		}
		if ( !Util.isEmpty(mosesToPath) ) {
			output = mosesToPath;
		}
		else {
			output = pathInsertOutBeforeExt(input);
		}
	}
	
	protected void process (String input) throws URISyntaxException {
		initialize();
		RawDocument rd;
		File file;
		
		switch ( command ) {
		case CMD_TRANSLATE:
			ps.println("Translation");
			guessMissingParameters(input);
			if ( !prepareFilter(configId) ) return; // Next input
			guessMissingLocales(input);
			file = new File(input);
			rd = new RawDocument(file.toURI(), inputEncoding, srcLoc, trgLoc);
			rd.setFilterConfigId(configId);
			translateFile(rd);
			break;
			
		case CMD_SEGMENTATION:
			ps.println("Segmentation");
			guessMissingParameters(input);
			if ( !prepareFilter(configId) ) return; // Next input
			guessMissingLocales(input);
			file = new File(input);
			rd = new RawDocument(file.toURI(), inputEncoding, srcLoc, trgLoc);
			rd.setFilterConfigId(configId);
			segmentFile(rd);
			break;
			
		case CMD_EXTRACT:
			ps.println("Extraction");
			guessMissingParameters(input);
			if ( !prepareFilter(configId) ) return; // Next input
			guessMissingLocales(input);
			file = new File(input);
			rd = new RawDocument(file.toURI(), inputEncoding, srcLoc, trgLoc);
			rd.setFilterConfigId(configId);
			extractFile(rd);
			break;
			
		case CMD_EXTRACTTOMOSES:
			ps.println("Extraction to Moses InlineText");
			guessMissingParameters(input);
			if ( !prepareFilter(configId) ) return; // Next input
			guessMissingLocales(input);
			file = new File(input);
			rd = new RawDocument(file.toURI(), inputEncoding, srcLoc, trgLoc);
			rd.setFilterConfigId(configId);
			extractFileToMoses(rd);
			break;
			
		case CMD_MERGE:
			ps.println("Merging");
			guessMergingArguments(input);
			guessMissingParameters(skeleton);
			if ( !prepareFilter(configId) ) return; // Next input
			guessMissingLocales(input);
			XLIFFMergingStep stepMrg = new XLIFFMergingStep(fcMapper);
			file = new File(skeleton);
			RawDocument skelRawDoc = new RawDocument(file.toURI(), inputEncoding,
				srcLoc, trgLoc);
			skelRawDoc.setFilterConfigId(configId);
			stepMrg.setXliffPath(input);
			stepMrg.setOutputPath(output);
			stepMrg.setOutputEncoding(outputEncoding);
			ps.println("Source language: "+srcLoc);
			ps.println("Target language: "+trgLoc);
			ps.println("Default input encoding: "+inputEncoding);
			ps.println("Output encoding: "+outputEncoding);
			ps.println("Filter configuration: "+configId);
			ps.println("XLIFF: "+input);
			ps.println(String.format("Output: %s", (output==null) ? "<auto-defined>" : output));
			stepMrg.handleRawDocument(skelRawDoc);
			break;

		case CMD_LEVERAGEMOSES:
			ps.println("Merging Moses InlineText");
			guessMissingLocales(input);
			guessMergingMosesArguments(input);
			guessMissingParameters(input);
			if ( !prepareFilter(configId) ) return; // Next input
			file = new File(input);
			rd = new RawDocument(file.toURI(), inputEncoding, srcLoc, trgLoc, configId);
			leverageFileWithMoses(rd);
			break;
			
		case CMD_CONV2PO:
		case CMD_CONV2TMX:
		case CMD_CONV2PEN:
		case CMD_CONV2TABLE:
			if ( command == CMD_CONV2PO ) {
				ps.println("Conversion to PO");
			}
			else if ( command == CMD_CONV2TMX ) {
				ps.println("Conversion to TMX");
			}
			else if ( command == CMD_CONV2TABLE ) {
				ps.println("Conversion to Table");
			}
			else {
				ps.println("Importing to Pensieve TM");
			}
			guessMissingParameters(input);
			if ( !prepareFilter(configId) ) return; // Next input
			guessMissingLocales(input);
			
			file = new File(input);
			String output = input;
			if ( command == CMD_CONV2PO ) {
				output += ".po";
			}
			else if ( command == CMD_CONV2TMX ) {
				output += ".tmx";
			}
			else if ( command == CMD_CONV2TABLE) {
				output += ".txt";
			}
			else { // Pensieve
				output = checkPensieveDirExtension();
			}
			URI outputURI = new File(output).toURI();
			rd = new RawDocument(file.toURI(), inputEncoding, srcLoc, trgLoc);
			rd.setFilterConfigId(configId);
			
			ps.println("Source language: "+srcLoc);
			ps.println("Target language: "+trgLoc);
			ps.println("Default input encoding: "+inputEncoding);
			ps.println("Filter configuration: "+configId);
			ps.println("Output: "+output);
			
			convertFile(rd, outputURI);
			break;
		}
		ps.println("Done");
		
	}
	
	private void printBanner () {
		ps.println("-------------------------------------------------------------------------------"); //$NON-NLS-1$
		ps.println("Okapi Tikal - Localization Toolset");
		// The version will show as 'null' until the code is build as a JAR.
		ps.println(String.format("Version: %s", getClass().getPackage().getImplementationVersion()));
		ps.println("-------------------------------------------------------------------------------"); //$NON-NLS-1$
	}

	private void showInfo () {
		Runtime rt = Runtime.getRuntime();
		rt.runFinalization();
		rt.gc();
		ps.println("Java version: " + System.getProperty("java.version")); //$NON-NLS-1$
		ps.println(String.format("Platform: %s, %s, %s",
			System.getProperty("os.name"), //$NON-NLS-1$ 
			System.getProperty("os.arch"), //$NON-NLS-1$
			System.getProperty("os.version"))); //$NON-NLS-1$
		NumberFormat nf = NumberFormat.getInstance();
		ps.println(String.format("Java VM memory: free=%s KB, total=%s KB", //$NON-NLS-1$
			nf.format(rt.freeMemory()/1024),
			nf.format(rt.totalMemory()/1024)));
		ps.println(String.format("Tikal display encoding: %s", getConsoleEncodingName()));
		ps.println("-------------------------------------------------------------------------------"); //$NON-NLS-1$
	}
	
	private String getAppRootDirectory () {
		try {
			URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
			String path = new File(url.toURI()).getCanonicalPath();
			return Util.getDirectoryName(Util.getDirectoryName(path));
		}
		catch ( IOException e ) {
			throw new OkapiIOException(e);
		}
		catch ( URISyntaxException e ) {
			throw new OkapiIOException("Bad URI syntax.", e);
		}
	}
	
	private void showHelp () throws MalformedURLException {
		Util.openWikiTopic("Tikal");
	}
	
	private void printUsage () {
		ps.println("Shows this screen: -?");
		ps.println("Shows version and other information: -i or --info");
		ps.println("Opens the user guide page: -h or --help");
		ps.println("Lists all available filter configurations: -lfc or --listconf");
		ps.println("Edits or view filter configurations (UI-dependent command):");
		ps.println("   -e [[-fc] configId]");
		ps.println("Extracts a file to XLIFF (and optionally segment and pre-translate):");
		ps.println("   -x inputFile [inputFile2...] [-fc configId] [-ie encoding] [-sl srcLang]");
		ps.println("      [-tl trgLang] [-seg [srxFile]] [-tt [hostname[:port]]|-mm [key]");
		ps.println("      |-pen tmDirectory|-gs configFile|-apertium [configFile]");
		ps.println("      |-ms configFile|-tda configFile|-gg configFile]");
		ps.println("      [-maketmx [tmxFile]] [-opt threshold] [-od outputDirectory]");
		ps.println("      [-rd rootDirectory] [-nocopy] [-noalttrans]");
		ps.println("Merges an XLIFF document back to its original format:");
		ps.println("   -m xliffFile [xliffFile2...] [-fc configId] [-ie encoding] [-oe encoding]");
		ps.println("      [-sd sourceDirectory] [-od outputDirectory]");
		ps.println("      [-sl srcLang] [-tl trgLang]");
		ps.println("Translates a file:");
		ps.println("   -t inputFile [inputFile2...] [-fc configId] [-ie encoding] [-oe encoding]");
		ps.println("      [-sl srcLang] [-tl trgLang] [-seg [srxFile]] [-tt [hostname[:port]]");
		ps.println("      |-mm [key]|-pen tmDirectory|-gs configFile|-apertium [configFile]");
		ps.println("      |-ms configFile|-tda configFile|-gg configFile] [-rd rootDirectory]");
		ps.println("      [-maketmx [tmxFile]] [-opt threshold]");
		ps.println("Extracts a file to Moses InlineText:");
		ps.println("   -xm inputFile [-fc configId] [-ie encoding] [-seg [srxFile]] [-2]");
		ps.println("      [-sl srcLang] [-tl trgLang] [-to srcOutputFile] [-rd rootDirectory]");
		ps.println("Leverages a file with Moses InlineText:");
		ps.println("   -lm inputFile [-fc configId] [-ie encoding] [-oe encoding] [-sl srcLang]");
		ps.println("      [-tl trgLang] [-seg [srxFile]] [-totrg|-overtrg] [-bpt]");
		ps.println("      [-from mosesFile] [-to outputFile] [-rd rootDirectory] [-noalttrans]");
		ps.println("Segments a file:");
		ps.println("   -s inputFile [-fc configId] [-ie encoding] [-rd rootDirectory]");
		ps.println("      [-sl srcLang] [-tl trgLang] [-seg [srxFile]]");
		ps.println("Queries translation resources:");
		ps.println("   -q \"source text\" [-sl srcLang] [-tl trgLang] [-opentran]");
		ps.println("      [-tt [hostname[:port]]] [-mm [key]] [-pen tmDirectory] [-gs configFile]");
		ps.println("      [-apertium [configFile]] [-ms configFile] [-tda configFile]");
		ps.println("      [-gg configFile] [-opt threshold[:maxhits]]");
		ps.println("Adds translation to a resources:");
		ps.println("   -a \"source text\" \"target text\" [rating] [-sl srcLang] [-tl trgLang]");
		ps.println("      -ms configFile");
		ps.println("Converts to PO format:");
		ps.println("   -2po inputFile [inputFile2...] [-fc configId] [-ie encoding] [-all] [-generic]");
		ps.println("      [-sl srcLang] [-tl trgLang] [-trgsource|-trgempty] [-rd rootDirectory]");
		ps.println("Converts to TMX format:");
		ps.println("   -2tmx inputFile [inputFile2...] [-fc configId] [-ie encoding] [-all]");
		ps.println("      [-sl srcLang] [-tl trgLang] [-trgsource|-trgempty] [-rd rootDirectory]");
		ps.println("Converts to table format:");
		ps.println("   -2tbl inputFile [inputFile2...] [-fc configId] [-ie encoding]");
		ps.println("      [-sl srcLang] [-tl trgLang] [-trgsource|-trgempty] [-csv|-tab]");
		ps.println("      [-xliff|-xliffgx|-tmx|-generic] [-all] [-rd rootDirectory]");
		ps.println("Imports to Pensieve TM:");
		ps.println("   -imp tmDirectory inputFile [inputFile2...] [-fc configId] [-ie encoding]");
		ps.println("      [-sl srcLang] [-tl trgLang] [-trgsource|-trgempty] [-all] [-over]");
		ps.println("      [-rd rootDirectory]");
		ps.println("Exports Pensieve TM as TMX:");
		ps.println("   -exp tmDirectory1 [tmDirectory2...] [-sl srcLang] [-tl trgLang]");
		ps.println("      [-trgsource|-trgempty] [-all]");
	}

	private void displayQuery (IQuery conn,
		boolean isTM)
	{
		int count;
		if ( conn.getClass().getName().endsWith("PensieveTMConnector")
			|| conn.getClass().getName().endsWith("GoogleMTConnector")
			|| conn.getClass().getName().endsWith("GoogleMTv2Connector")
			|| conn.getClass().getName().endsWith("MyMemoryTMConnector")
			|| conn.getClass().getName().endsWith("MicrosoftMTConnector")
//			|| conn.getClass().getName().endsWith("ProMTConnector")
			|| conn.getClass().getName().endsWith("GlobalSightTMConnector") ) {
			count = conn.query(parseToTextFragment(query));
		}
		else { // Raw text otherwise
			count = conn.query(query);
		}

		ps.println(String.format("\n= From %s (%s->%s)", conn.getName(),
			conn.getSourceLanguage(), conn.getTargetLanguage()));
		if ( isTM ) {
			ITMQuery tmConn = (ITMQuery)conn;
			ps.println(String.format("  Threshold=%d, Maximum hits=%d",
				tmConn.getThreshold(), tmConn.getMaximumHits()));
		}
		
		if ( count > 0 ) {
			QueryResult qr;
			while ( conn.hasNext() ) {
				qr = conn.next();
				ps.println(String.format("score: %d, origin: '%s'%s",
					qr.getCombinedScore(),
					(qr.origin==null ? "" : qr.origin),
					(qr.fromMT() ? " (from MT)" : "")));
				ps.println(String.format("  Source: \"%s\"", qr.source.toText()));
				ps.println(String.format("  Target: \"%s\"", qr.target.toText()));
			}
		}
		else {
			ps.println(String.format("  Source: \"%s\"", query));
			ps.println("  <No translation has been found>");
		}	
	}
	
	private void processAddTranslation () {
		guessMissingLocales(null);
		if ( Util.isEmpty(query) ) {
			throw new RuntimeException(String.format("Cannot add empty source text."));
		}
		if ( Util.isEmpty(addTransTrans) ) {
			throw new RuntimeException(String.format("Cannot add empty target text."));
		}
		if ( useMicrosoft ) {
			MicrosoftMTConnector conn = new MicrosoftMTConnector();
			conn.setParameters(prepareConnectorParameters(conn.getClass().getName()));
			conn.setLanguages(srcLoc, trgLoc);
			conn.open();
			int res = conn.addTranslation(parseToTextFragment(query), parseToTextFragment(addTransTrans), addTransRating);
			if ( res == 200 ) {
				ps.println("Done");
			}
			else {
				ps.println(String.format("Error code %d.", res));
			}
			conn.close();
		}
		else {
			throw new RuntimeException(String.format("No valid connector specified to add a translation."));
		}
	}
	
	private void processQuery () {
		guessMissingLocales(null);
		if ( !useGoogleV2 && !useOpenTran && !useTransToolkit && !useMyMemory
			&& !usePensieve && !useGlobalSight && !useApertium && !useMicrosoft && !useTDA ) {
			useOpenTran = true; // Default if none is specified
		}
		// Query options
		int[] opt = parseTMOptions();
		int threshold = opt[0];
		int maxhits = opt[1];
		
		IQuery conn;
		if ( useGoogleV2 ) {
			conn = new GoogleMTv2Connector();
			conn.setParameters(prepareConnectorParameters(conn.getClass().getName()));
			conn.setLanguages(srcLoc, trgLoc);
			conn.open();
			displayQuery(conn, false);
			conn.close();
		}
		if ( usePensieve ) {
			conn = new PensieveTMConnector();
			conn.setParameters(prepareConnectorParameters(conn.getClass().getName()));
			conn.setLanguages(srcLoc, trgLoc);
			setTMOptionsIfPossible(conn, threshold, maxhits);
			conn.open();
			displayQuery(conn, true);
			conn.close();
		}
		if ( useTransToolkit ) {
			conn = new TranslateToolkitTMConnector();
			conn.setParameters(prepareConnectorParameters(conn.getClass().getName()));
			conn.setLanguages(srcLoc, trgLoc);
			setTMOptionsIfPossible(conn, threshold, maxhits);
			conn.open();
			displayQuery(conn, true);
			conn.close();
		}
		if ( useGlobalSight ) {
			conn = new GlobalSightTMConnector();
			conn.setParameters(prepareConnectorParameters(conn.getClass().getName()));
			conn.setLanguages(srcLoc, trgLoc);
			setTMOptionsIfPossible(conn, threshold, maxhits);
			conn.open();
			displayQuery(conn, true);
			conn.close();
		}
		if ( useTDA ) {
			conn = new TDASearchConnector();
			conn.setParameters(prepareConnectorParameters(conn.getClass().getName()));
			conn.setLanguages(srcLoc, trgLoc);
			setTMOptionsIfPossible(conn, threshold, maxhits);
			conn.open();
			displayQuery(conn, true);
			conn.close();
		}
		if ( useMicrosoft ) {
			conn = new MicrosoftMTConnector();
			conn.setParameters(prepareConnectorParameters(conn.getClass().getName()));
			conn.setLanguages(srcLoc, trgLoc);
			setTMOptionsIfPossible(conn, threshold, maxhits);
			conn.open();
			displayQuery(conn, true);
			conn.close();
		}
//		if ( useProMT ) {
//			conn = new ProMTConnector();
//			conn.setParameters(prepareConnectorParameters(conn.getClass().getName()));
//			conn.setLanguages(srcLoc, trgLoc);
//			conn.open();
//			displayQuery(conn, false);
//			conn.close();
//		}
		if ( useMyMemory ) {
			conn = new MyMemoryTMConnector();
			conn.setParameters(prepareConnectorParameters(conn.getClass().getName()));
			conn.setLanguages(srcLoc, trgLoc);
			setTMOptionsIfPossible(conn, threshold, maxhits);
			conn.open();
			displayQuery(conn, true);
			conn.close();
		}
		if ( useApertium ) {
			conn = new ApertiumMTConnector();
			conn.setParameters(prepareConnectorParameters(conn.getClass().getName()));
			conn.setLanguages(srcLoc, trgLoc);
			conn.open();
			displayQuery(conn, false);
			conn.close();
		}
		if ( useOpenTran ) {
			conn = new OpenTranTMConnector();
			conn.setLanguages(srcLoc, trgLoc);
			setTMOptionsIfPossible(conn, threshold, maxhits);
			conn.open();
			displayQuery(conn, true);
			conn.close();
		}
	}

	private int[] parseTMOptions () {
		int[] opt = new int[2];
		opt[0] = -1;
		opt[1] = -1;
		if ( !Util.isEmpty(tmOptions) ) {
			try {
				// Expected format: "threshold[:maxhits]"
				int n = tmOptions.indexOf(':');
				if ( n == -1 ) { // Threshold only
					opt[0] = Integer.parseInt(tmOptions);
				}
				else {
					opt[0] = Integer.parseInt(tmOptions.substring(0, n));
					opt[1] = Integer.parseInt(tmOptions.substring(n+1));
					if ( opt[1] < 0 ) {
						throw new RuntimeException(String.format("Invalid TM options: '%s' Maximum hits must be more than 0.", tmOptions));
					}
				}
				if (( opt[0] < 0 ) || ( opt[0] > 100 )) {
					throw new RuntimeException(String.format("Invalid TM options: '%s' Thresold must be between 0 and 100.", tmOptions));
				}
			}
			catch ( NumberFormatException e ) {
				throw new RuntimeException(String.format("Invalid TM options: '%s'", tmOptions));
			}
		}
		return opt;
	}
	
	private void setTMOptionsIfPossible (IQuery conn,
		int threshold,
		int maxhits)
	{
		ITMQuery tmConn = (ITMQuery)conn;
		if ( threshold > -1 ) tmConn.setThreshold(threshold);
		if ( maxhits > -1 ) tmConn.setMaximumHits(maxhits);
	}
	
	private void convertFile (RawDocument rd, URI outputURI) {
		// Create the driver
		PipelineDriver driver = new PipelineDriver();
		driver.setFilterConfigurationMapper(fcMapper);
		driver.setRootDirectories(rootDir, Util.getDirectoryName(rd.getInputURI().getPath()));

		RawDocumentToFilterEventsStep rd2feStep = new RawDocumentToFilterEventsStep();
		driver.addStep(rd2feStep);
		
		FormatConversionStep fcStep = new FormatConversionStep();
		net.sf.okapi.steps.formatconversion.Parameters params = fcStep.getParameters();
		if ( command == CMD_CONV2PO ) {
			params.setOutputFormat(Parameters.FORMAT_PO);
			params.setOutputPath("output.po");
		}
		else if ( command == CMD_CONV2TMX ) {
			params.setOutputFormat(Parameters.FORMAT_TMX);
			params.setOutputPath("output.tmx");
		}
		else if ( command == CMD_CONV2TABLE ) {
			params.setOutputFormat(Parameters.FORMAT_TABLE);
			TableFilterWriterParameters opt = new TableFilterWriterParameters();
			opt.fromArguments(tableConvFormat, tableConvCodes);
			params.setFormatOptions(opt.toString());
			params.setOutputPath("output.txt");
		}
		else if ( command == CMD_CONV2PEN ) {
			params.setOutputFormat(Parameters.FORMAT_PENSIEVE);
			params.setOutputPath(checkPensieveDirExtension());
		}
		
		params.setSingleOutput(command==CMD_CONV2PEN);
		
		// These options may or may not be used depending on the output format
		params.setUseGenericCodes(genericOutput);
		params.setTargetStyle(convTargetStyle);
		params.setSkipEntriesWithoutText(convSkipEntriesWithoutText);
		params.setOverwriteSameSource(convOverwrite);
		
		driver.addStep(fcStep);
		driver.addBatchItem(rd, outputURI, outputEncoding);
		driver.processBatch();
	}

	private IPipelineStep addSegmentationStep () {
		if ( segRules.equals(DEFAULT_SEGRULES) ) { // Defaults
			segRules = getAppRootDirectory();
			segRules += File.separator + "config" + File.separator + "defaultSegmentation.srx";
		}
		else {
			if ( Util.isEmpty(Util.getExtension(segRules)) ) {
				segRules += ".srx";
			}
		}
		SegmentationStep segStep = new SegmentationStep();
		net.sf.okapi.steps.segmentation.Parameters segParams
			= (net.sf.okapi.steps.segmentation.Parameters)segStep.getParameters();
		segParams.segmentSource = true;
		segParams.segmentTarget = true;
		File f = new File(segRules);
		segParams.setSourceSrxPath(f.getAbsolutePath());
		segParams.setTargetSrxPath(f.getAbsolutePath());
		ps.println("Segmentation: " + f.getAbsolutePath());
		return segStep;
	}

	private IPipelineStep addLeveragingStep () {
		LeveragingStep levStep = new LeveragingStep();
		net.sf.okapi.steps.leveraging.Parameters levParams
			= (net.sf.okapi.steps.leveraging.Parameters)levStep.getParameters();
		if ( usePensieve ) {
			levParams.setResourceClassName(PensieveTMConnector.class.getName());
		}
		else if ( useTransToolkit ) {
			levParams.setResourceClassName(TranslateToolkitTMConnector.class.getName());
		}
		else if ( useMyMemory ) {
			levParams.setResourceClassName(MyMemoryTMConnector.class.getName());
		}
		else if ( useGoogleV2 ) {
			levParams.setResourceClassName(GoogleMTv2Connector.class.getName());
		}
		else if ( useGlobalSight ) {
			levParams.setResourceClassName(GlobalSightTMConnector.class.getName());
		}
		else if ( useTDA ) {
			levParams.setResourceClassName(TDASearchConnector.class.getName());
		}
		else if ( useMicrosoft ) {
			levParams.setResourceClassName(MicrosoftMTConnector.class.getName());
		}
//		else if ( useProMT ) {
//			levParams.setResourceClassName(ProMTConnector.class.getName());
//		}
		else if ( useApertium ) {
			levParams.setResourceClassName(ApertiumMTConnector.class.getName());
		}
		IParameters p = prepareConnectorParameters(levParams.getResourceClassName());
		if ( p != null ) levParams.setResourceParameters(p.toString());
		levParams.setFillTarget(levOptFillTarget);
		// Query options
		int[] opt = parseTMOptions();
		if ( opt[0] > -1 ) levParams.setThreshold(opt[0]);
		if ( levOptTMXPath != null ) {
			levParams.setMakeTMX(true);
			levParams.setTMXPath(levOptTMXPath);
		}
		return levStep;
	}

	private void extractFile (RawDocument rd) throws URISyntaxException {
		// Create the driver
		PipelineDriver driver = new PipelineDriver();
		driver.setFilterConfigurationMapper(fcMapper);
		driver.setRootDirectories(rootDir, Util.getDirectoryName(rd.getInputURI().getPath()));

		// Raw document to filter events step 
		RawDocumentToFilterEventsStep rd2feStep = new RawDocumentToFilterEventsStep();
		driver.addStep(rd2feStep);
		
		// Add segmentation step if requested
		if ( segRules != null ) {
			driver.addStep(addSegmentationStep());
		}
		
		// Add leveraging step if requested
		if ( useGoogleV2 || useTransToolkit || useMyMemory || usePensieve
			|| useGlobalSight || useApertium || useMicrosoft || useTDA ) {
			driver.addStep(addLeveragingStep());
		}
		
		// Filter events to raw document final step (using the XLIFF writer)
		FilterEventsWriterStep fewStep = new FilterEventsWriterStep();
		XLIFFWriter writer = new XLIFFWriter();
		writer.setPlaceholderMode(true);
		writer.setCopySource(extOptCopy);
		writer.setIncludeAltTrans(extOptAltTrans);
		fewStep.setFilterWriter(writer);
		fewStep.setDocumentRoots(rootDir);
		driver.addStep(fewStep);

		// Create the raw document and set the output
		String tmp = rd.getInputURI().getPath();
		// If the input is a directory, it ends with a separator, then we remove it
		if ( tmp.endsWith("/") || tmp.endsWith("\\") ) {
			tmp = tmp.substring(0, tmp.length()-1);
		}
		tmp += ".xlf";

		tmp = pathChangeFolder(outputDir, tmp);
		driver.addBatchItem(rd, new File(tmp).toURI(), outputEncoding);

		ps.println("Source language: "+srcLoc);
		ps.println("Target language: "+trgLoc);
		ps.println("Default input encoding: "+inputEncoding);
		ps.println("Filter configuration: "+configId);
		ps.println("Output: "+tmp);

		// Process
		driver.processBatch();
	}

	private void segmentFile (RawDocument rd) throws URISyntaxException {
		// Create the driver
		PipelineDriver driver = new PipelineDriver();
		driver.setFilterConfigurationMapper(fcMapper);
		driver.setRootDirectories(rootDir, Util.getDirectoryName(rd.getInputURI().getPath()));

		// Raw document to filter events step 
		RawDocumentToFilterEventsStep rd2feStep = new RawDocumentToFilterEventsStep();
		driver.addStep(rd2feStep);

		driver.addStep(addSegmentationStep());
		
		// Filter events to raw document final step
		FilterEventsToRawDocumentStep ferdStep = new FilterEventsToRawDocumentStep();
		driver.addStep(ferdStep);

		// Create the raw document and set the output
		String tmp = rd.getInputURI().getPath();

		output = pathInsertOutBeforeExt(tmp);

		ps.println("Source language: "+srcLoc);
		ps.println("Target language: "+trgLoc);
		ps.println("Default input encoding: "+inputEncoding);
		ps.println("Output encoding: "+outputEncoding);
		ps.println("Filter configuration: "+configId);
		ps.println("Output: "+output);
		
		driver.addBatchItem(rd, new File(output).toURI(), outputEncoding);

		// Process
		driver.processBatch();
	}

	private void leverageFileWithMoses (RawDocument rd) {
		// Create the driver
		PipelineDriver driver = new PipelineDriver();
		driver.setFilterConfigurationMapper(fcMapper);
		driver.setRootDirectories(rootDir, Util.getDirectoryName(rd.getInputURI().getPath()));
		driver.addStep(new RawDocumentToFilterEventsStep());

		// Add segmentation step if requested
		if ( segRules != null ) {
			driver.addStep(addSegmentationStep());
		}

		MergingStep mrgStep = new MergingStep();
		MergingParameters params = (MergingParameters)mrgStep.getParameters();
		params.setCopyToTarget(mosesCopyToTarget);
		params.setOverwriteExistingTarget(mosesOverwriteTarget);
		params.setForceAltTransOutput(extOptAltTrans);
		params.setUseGModeInAltTrans(mosesUseGModeInAltTrans);
		driver.addStep(mrgStep);
		
		driver.addStep(new FilterEventsToRawDocumentStep());
		
		// Two parallel inputs: 1=the original file, 2=the Moses translated file
		RawDocument rdMoses = new RawDocument(new File(mosesFromPath).toURI(), "UTF-8", trgLoc);
		driver.addBatchItem(new BatchItemContext(rd, new File(output).toURI(), outputEncoding, rdMoses));
		// Execute
		driver.processBatch();
	}
	
	private void extractFileToMoses (RawDocument rd) throws URISyntaxException {
		// Create the driver
		PipelineDriver driver = new PipelineDriver();
		driver.setFilterConfigurationMapper(fcMapper);
		driver.setRootDirectories(rootDir, Util.getDirectoryName(rd.getInputURI().getPath()));

		// Raw document to filter events step 
		RawDocumentToFilterEventsStep rd2feStep = new RawDocumentToFilterEventsStep();
		driver.addStep(rd2feStep);
		
		// Add segmentation step if requested
		if ( segRules != null ) {
			driver.addStep(addSegmentationStep());
		}
		
		// Filter events to raw document final step (using the XLIFF writer)
		ExtractionStep extStep = new ExtractionStep();
		if ( moses2Outputs ) {
			FilterWriterParameters p = (FilterWriterParameters)extStep.getParameters();
			p.setSourceAndTarget(true);
		}
		driver.addStep(extStep);

		// Create the raw document and set the output
		if ( Util.isEmpty(mosesToPath) ) {
			mosesToPath = rd.getInputURI().getPath();
		}
		if ( !mosesToPath.endsWith("."+srcLoc.toString()) ) {
			mosesToPath = mosesToPath + ("."+srcLoc.toString());
		}
		driver.addBatchItem(rd, new File(mosesToPath).toURI(), "UTF-8");

		ps.println("Source language: "+srcLoc);
		if ( moses2Outputs ) {
			ps.println("Target language: "+trgLoc);
		}
		ps.println("Default input encoding: "+inputEncoding);
		ps.println("Filter configuration: "+configId);

		// Process
		driver.processBatch();
	}

	private void translateFile (RawDocument rd) throws URISyntaxException {
		// Create the driver
		PipelineDriver driver = new PipelineDriver();
		driver.setFilterConfigurationMapper(fcMapper);
		driver.setRootDirectories(rootDir, Util.getDirectoryName(rd.getInputURI().getPath()));

		// Raw document to filter events step 
		RawDocumentToFilterEventsStep rd2feStep = new RawDocumentToFilterEventsStep();
		driver.addStep(rd2feStep);
		
		// Add segmentation step if requested
		if ( segRules != null ) {
			driver.addStep(addSegmentationStep());
		}
		
		// Add leveraging step
		if ( useGoogleV2 || useTransToolkit || useMyMemory || usePensieve
			|| useGlobalSight || useApertium || useMicrosoft || useTDA ) {
			driver.addStep(addLeveragingStep());
		}
		else { // Or indicate that we won't translate
			ps.println("No valid translation resource has been specified: The text will not be modified.");
		}
		
		// Filter events to raw document final step
		FilterEventsToRawDocumentStep ferdStep = new FilterEventsToRawDocumentStep();
		driver.addStep(ferdStep);

		// Create the raw document and set the output
		String tmp = rd.getInputURI().getPath();

		output = pathInsertOutBeforeExt(tmp);

		ps.println("Source language: "+srcLoc);
		ps.println("Target language: "+trgLoc);
		ps.println("Default input encoding: "+inputEncoding);
		ps.println("Output encoding: "+outputEncoding);
		ps.println("Filter configuration: "+configId);
		ps.println("Output: "+output);
		
		driver.addBatchItem(rd, new File(output).toURI(), outputEncoding);

		// Process
		driver.processBatch();
	}

	private String checkPensieveDirExtension () {
		String ext = Util.getExtension(pensieveData);
		if ( Util.isEmpty(ext) ) pensieveData += ".pentm";
		return pensieveData;
	}
	
	private IParameters prepareConnectorParameters (String connectorClassName) {
		if ( connectorClassName.equals(PensieveTMConnector.class.getName()) ) {
			net.sf.okapi.connectors.pensieve.Parameters params
				= new net.sf.okapi.connectors.pensieve.Parameters();
			if ( pensieveData.startsWith("http:") ) {
				params.setHost(pensieveData);
				params.setUseServer(true);
			}
			else {
				params.setDbDirectory(checkPensieveDirExtension());
			}
			return params;
		}

		if ( connectorClassName.equals(TranslateToolkitTMConnector.class.getName()) ) {
			net.sf.okapi.connectors.translatetoolkit.Parameters params
				= new net.sf.okapi.connectors.translatetoolkit.Parameters();
			// Parse the parameters hostname:port
			int n = transToolkitParams.lastIndexOf(':');
			if ( n == -1 ) {
				params.setHost(transToolkitParams);
			}
			else {
				params.setPort(Integer.valueOf(transToolkitParams.substring(n+1)));
				params.setHost(transToolkitParams.substring(0, n));
			}
			return params;
		}

		if ( connectorClassName.equals(MyMemoryTMConnector.class.getName()) ) {
			net.sf.okapi.connectors.mymemory.Parameters params
				= new net.sf.okapi.connectors.mymemory.Parameters();
			params.setKey(myMemoryParams);
			return params;
		}
		
		if ( connectorClassName.equals(GlobalSightTMConnector.class.getName()) ) {
			net.sf.okapi.connectors.globalsight.Parameters params
				= new net.sf.okapi.connectors.globalsight.Parameters();
			URI paramURI = (new File(globalSightParams).toURI());
			params.load(paramURI, false);
			return params;
		}

		if ( connectorClassName.equals(TDASearchConnector.class.getName()) ) {
			net.sf.okapi.connectors.tda.Parameters params
				= new net.sf.okapi.connectors.tda.Parameters();
			URI paramURI = (new File(tdaParams).toURI());
			params.load(paramURI, false);
			return params;
		}

		if ( connectorClassName.equals(MicrosoftMTConnector.class.getName()) ) {
			net.sf.okapi.connectors.microsoft.Parameters params
				= new net.sf.okapi.connectors.microsoft.Parameters();
			// Use the specified parameters if available, otherwise use the default
			if ( microsoftParams != null ) {
				URI paramURI = (new File(microsoftParams).toURI());
				params.load(paramURI, false);
			}
			return params;
		}

		if ( connectorClassName.equals(GoogleMTv2Connector.class.getName()) ) {
			net.sf.okapi.connectors.google.GoogleMTv2Parameters params
				= new net.sf.okapi.connectors.google.GoogleMTv2Parameters();
			// Use the specified parameters if available, otherwise use the default
			if ( googleV2Params != null ) {
				URI paramURI = (new File(googleV2Params).toURI());
				params.load(paramURI, false);
			}
			return params;
		}

//		if ( connectorClassName.equals(ProMTConnector.class.getName()) ) {
//			net.sf.okapi.connectors.promt.Parameters params
//				= new net.sf.okapi.connectors.promt.Parameters();
//			// Use the specified parameters if available, otherwise use the default
//			if ( proMTParams != null ) {
//				URI paramURI = (new File(proMTParams).toURI());
//				params.load(paramURI, false);
//			}
//			return params;
//		}
		
		if ( connectorClassName.equals(ApertiumMTConnector.class.getName()) ) {
			net.sf.okapi.connectors.apertium.Parameters params
				= new net.sf.okapi.connectors.apertium.Parameters();
			if ( apertiumParams != null ) {
				URI paramURI = (new File(apertiumParams).toURI());
				params.load(paramURI, false);
			} // Use default otherwise
			return params;
		}
		
		// Other connector: no parameters
		return null;
	}

	/**
	 * Converts the plain text string into a TextFragment, using HTML-like patterns are inline codes.
	 * @param text the plain text to convert to TextFragment
	 * @return a new TextFragment (with possibly inline codes).
	 */	
	public TextFragment parseToTextFragment (String text) {
		// Parses any thing within <...> into opening codes
		// Parses any thing within </...> into closing codes
		// Parses any thing within <.../> into placeholder codes
		Pattern patternOpening = Pattern.compile("\\<(\\w+)[ ]*[^\\>/]*\\>");
		Pattern patternClosing = Pattern.compile("\\</(\\w+)[ ]*[^\\>]*\\>");
		Pattern patternPlaceholder = Pattern.compile("\\<(\\w+)[ ]*[^\\>]*/\\>");
		
		TextFragment tf = new TextFragment();
		tf.setCodedText(text);

		int n;
		int start = 0;
		int diff = 0;
		Matcher m = patternOpening.matcher(text);
		while ( m.find(start) ) {
			n = m.start();
			diff += tf.changeToCode(n+diff, (n+diff)+m.group().length(),
				TagType.OPENING, m.group(1));
			start = (n+m.group().length());
		}
		
		text = tf.getCodedText();
		start = diff = 0;
		m = patternClosing.matcher(text);
		while ( m.find(start) ) {
			n = m.start();
			diff += tf.changeToCode(n+diff, (n+diff)+m.group().length(),
				TagType.CLOSING, m.group(1));
			start = (n+m.group().length());
		}
		
		text = tf.getCodedText();
		start = diff = 0;
		m = patternPlaceholder.matcher(text);
		while ( m.find(start) ) {
			n = m.start();
			diff += tf.changeToCode(n+diff, (n+diff)+m.group().length(),
				TagType.PLACEHOLDER, null);
			start = (n+m.group().length());
		}
		return tf;
	}

}
