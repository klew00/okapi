/*===========================================================================
  Copyright (C) 2008 by the Okapi Framework contributors
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
============================================================================*/

package net.sf.okapi.applications.rainbow.utilities.ttxchecker;

import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import net.sf.okapi.applications.rainbow.utilities.BaseUtility;
import net.sf.okapi.applications.rainbow.utilities.ISimpleUtility;
import net.sf.okapi.common.IParameters;

public class Utility extends BaseUtility implements ISimpleUtility {

	protected final Logger logger = Logger.getLogger(getClass().getName());
	
	XMLStreamReader reader;
	String srcLang = "";
	String trgLang = "";
	StringBuffer srcBuffer = new StringBuffer();
	StringBuffer trgBuffer = new StringBuffer();
	StringBuffer contBuffer = new StringBuffer();
	String srcContent;
	String trgContent;
	String lastContentType="";
	int srcLine=0;
	int trgLine=0;
	int warningCounter;
	int line = 1;
	
	private Parameters params;

	public Utility () {
		params = new Parameters();
	}
	
	public String getName () {
		return "oku_ttxchecker";
	}
	
	public void preprocess () {
		// Nothing to do
	}

	public void postprocess () {
		// Nothing to do
	}

	public IParameters getParameters () {
		return params;
	}

	public boolean hasParameters () {
		return true;
	}

	public boolean isFilterDriven () {
		return false;
	}

	public boolean needsRoots () {
		return false;
	}

	public void setParameters (IParameters paramsObject) {
		params = (Parameters)paramsObject;
	}

	public int requestInputCount() {
		return 1;
	}

	public void processInput () {
	
		try{
			
		XMLInputFactory fact = (XMLInputFactory)XMLInputFactory.newInstance();
		fact.setProperty(XMLInputFactory.IS_VALIDATING,Boolean.FALSE);
		fact.setProperty(XMLInputFactory.SUPPORT_DTD,Boolean.FALSE);
		reader = (XMLStreamReader)fact.createXMLStreamReader(new FileInputStream(getInputPath(0)));
		int eventType;
		
		while (reader.hasNext()){
			eventType = reader.next();
			switch ( eventType ) {
			case XMLStreamConstants.SPACE:
			case XMLStreamConstants.CHARACTERS:
				if(reader.getText().contains("\n"))
					++line;
				break;				
 			case XMLStreamConstants.START_ELEMENT:
				if("Tuv".equals(reader.getLocalName())){
					if(srcLang.equals(reader.getAttributeValue(0))){
						srcLine = line;
						srcBuffer = parseTuv();
						srcContent = contBuffer.toString();
						contBuffer = new StringBuffer();
					}else if(trgLang.equals(reader.getAttributeValue(0))){
						trgLine = line;
						trgBuffer = parseTuv();						
						trgContent = contBuffer.toString();
						contBuffer = new StringBuffer();
					}
				}
				if("UserSettings".equals(reader.getLocalName())){
					int count = reader.getAttributeCount();
					for ( int i=0; i<count; i++ ) {
						if(reader.getAttributeLocalName(i).equals("PlugInInfo")){
							if(reader.getAttributeValue(i).contains("TRADOS Indesign Filter 1.0.0.0")){
								logger.log(Level.WARNING, "NOTE: Depending on the version of Trados the linguist used you may need to update <UserSettings:PlugInInfo> from \"TRADOS Indesign Filter 1.0.0.0\" to \"TRADOS InDesign Filter 1.0.0.0\"\n");								
							}
						}else if (reader.getAttributeLocalName(i).equals("SourceDocumentPath")){
							if(!reader.getAttributeValue(i).equals("")){
								logger.log(Level.WARNING, "NOTE: If you get the message \"Resource Tag has been inserted or modified at Control ID\" try to clear the value of <UserSettings:SourceDocumentPath>\n");
							}
						}else if (reader.getAttributeLocalName(i).equals("SourceLanguage")){
							if(!reader.getAttributeValue(i).equals("")){
								srcLang=reader.getAttributeValue(i);
							}
						}else if (reader.getAttributeLocalName(i).equals("TargetLanguage")){
							if(!reader.getAttributeValue(i).equals("")){
								trgLang=reader.getAttributeValue(i);
							}
						}
					}
				}
				
				break;
 			case XMLStreamConstants.END_ELEMENT:
				if("Tu".equals(reader.getLocalName())){
					if (!srcContent.equals(trgContent)){
						if(trgBuffer.toString().contains(("</ut><ut></cf>"))){
							warningCounter++;
							logger.log(Level.WARNING, "Missmatch "+warningCounter+" (High): \nSource (logical parts): "+ srcContent+"\nTarget (logical parts): "+trgContent +
							"\n\nSource, line "+srcLine+": "+ srcBuffer +
							"\nTarget, line "+trgLine+": "+ trgBuffer+"\n");						
						}else if(trgBuffer.toString().contains(("</ut><ut><cf"))){
							warningCounter++;
							logger.log(Level.WARNING, "Missmatch "+warningCounter+" (Low): \nSource (logical parts): "+ srcContent+"\nTarget (logical parts): "+trgContent +
							"\n\nSource, line "+srcLine+": "+ srcBuffer +
							"\nTarget, line "+trgLine+": "+ trgBuffer+"\n");						
						}
						
					}
				}				
				break;
			}				
		}
		}catch(Exception e){
			throw new RuntimeException();
		}
	}
	

	StringBuffer parseTuv(){

		StringBuffer sb = new StringBuffer();
		
		int eventType;
		try{
			while (reader.hasNext()){
				eventType = reader.next();
				switch ( eventType ) {
				case XMLStreamConstants.SPACE:
				case XMLStreamConstants.CHARACTERS:
					if(reader.getText().contains("\n")){
						++line;
					}
					sb.append(reader.getText());
					if(!lastContentType.equals("<text>")){
						contBuffer.append("<text>");
						lastContentType="<text>";
					}
					break;
	 			case XMLStreamConstants.START_ELEMENT:
	 				if(!"df".equals(reader.getLocalName())){
		 				sb.append("<"+reader.getLocalName()+">");
		 				contBuffer.append("<open_element>");
		 				lastContentType="<open_element>";
	 				}
	 				break;
	 			case XMLStreamConstants.END_ELEMENT:
					if("Tuv".equals(reader.getLocalName())){
						lastContentType="";
						return sb;
					}else{
		 				if(!"df".equals(reader.getLocalName())){
			 				sb.append("</"+reader.getLocalName()+">");
			 				contBuffer.append("<end_element>");
			 				lastContentType="<end_element>";
		 				}						
					}
					break;
				}
			}
		}catch(Exception e){
			throw new RuntimeException();
		}
		return sb;
	}
}

