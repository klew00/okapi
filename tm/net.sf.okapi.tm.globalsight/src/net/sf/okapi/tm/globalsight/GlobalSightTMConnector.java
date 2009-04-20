/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

package net.sf.okapi.tm.globalsight;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.globalsight.webservices.WebServiceException;
import com.globalsight.www.webservices.Ambassador;
import com.globalsight.www.webservices.AmbassadorWebServiceSoapBindingStub;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiNotImplementedException;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.ITMQuery;
import net.sf.okapi.lib.translation.QueryResult;

public class GlobalSightTMConnector implements ITMQuery {

	private String srcLang;
	private String trgLang;
	private List<QueryResult> results;
	private int current = -1;
	private int maxHits = 25;
	private Ambassador gsWS;
	private String gsToken;
	private String gsTmProfile;
	private Parameters params;
	private DocumentBuilder docBuilder;

	public GlobalSightTMConnector () {
		params = new Parameters();
		DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
		Fact.setValidating(false);
		try {
			docBuilder = Fact.newDocumentBuilder();
		}
		catch ( ParserConfigurationException e ) {
			throw new RuntimeException("Error creating document builder.", e);
		}
	}

	public String getName () {
		return "GlobalSight-TM";
	}

	public void close () {
	}

	public void export (String outputPath) {
		throw new OkapiNotImplementedException("The export() method is not supported.");
	}

	public String getSourceLanguage () {
		return toISOCode(srcLang);
	}

	public String getTargetLanguage () {
		return toISOCode(trgLang);
	}

	public boolean hasNext () {
		if ( results == null ) return false;
		if ( current >= results.size() ) {
			current = -1;
		}
		return (current > -1);
	}

	public QueryResult next () {
		if ( results == null ) return null;
		if (( current > -1 ) && ( current < results.size() )) {
			current++;
			return results.get(current-1);
		}
		current = -1;
		return null;
	}

	public void open () {
		try {
			URL url = new URL(params.serverURL);
			gsWS = new AmbassadorWebServiceSoapBindingStub(url, null);
			gsToken = gsWS.login(params.username, params.password);
			// Remove the end part
			int n = gsToken.lastIndexOf("+_+");
			gsToken = gsToken.substring(0, n);
			gsTmProfile = params.tmProfile;
			results = new ArrayList<QueryResult>();
		}
		catch ( AxisFault e ) {
			throw new RuntimeException("Error creating the GS Web services.", e);
		}
		catch ( RemoteException e ) {
			throw new RuntimeException("Error when login.", e);
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException("Invalid server URL.", e);
		}
	}

	public int query (String plainText) {
		try {
			results.clear();
			String xmlRes = gsWS.searchEntries(gsToken, gsTmProfile, plainText, srcLang);
			Document doc = docBuilder.parse(new InputSource(new StringReader(xmlRes)));
			NodeList list1 = doc.getElementsByTagName("entry");
			Element elem;
			NodeList list2;
			NodeList list3;
			QueryResult res;
			for ( int i=0; i<list1.getLength(); i++ ) {
				elem = (Element)list1.item(i);
				list2 = elem.getElementsByTagName("percentage");
				res = new QueryResult();
				res.score = Integer.valueOf(Util.getTextContent(list2.item(0)).replace("%", ""));
				list2 = elem.getElementsByTagName("source");
				list3 = ((Element)list2.item(0)).getElementsByTagName("segment");
				res.source = new TextFragment(Util.getTextContent(list3.item(0)));
				list2 = elem.getElementsByTagName("target");
				list3 = ((Element)list2.item(0)).getElementsByTagName("segment");
				res.target = new TextFragment(Util.getTextContent(list3.item(0)));
				results.add(res);
			}
		}
		catch ( WebServiceException e ) {
			throw new RuntimeException("Error querying TM.", e);
		}
		catch ( RemoteException e ) {
			throw new RuntimeException("Error querying TM.", e);
		}
		catch ( SAXException e ) {
			throw new RuntimeException("Error with query resuls.", e);
		}
		catch ( IOException e ) {
			throw new RuntimeException("Error with query resuls.", e);
		}
		if ( results.size() > 0 ) current = 0;
		return results.size();
	}

	public int query (TextFragment text) {
		String qtext = text.getCodedText();
		StringBuilder tmpCodes = new StringBuilder();
		if ( text.hasCode() ) {
			StringBuilder tmpText = new StringBuilder();
			for ( int i=0; i<qtext.length(); i++ ) {
				switch ( qtext.charAt(i) ) {
				case TextFragment.MARKER_OPENING:
				case TextFragment.MARKER_CLOSING:
				case TextFragment.MARKER_ISOLATED:
				case TextFragment.MARKER_SEGMENT:
					tmpCodes.append(qtext.charAt(i));
					tmpCodes.append(qtext.charAt(++i));
					break;
				default:
					tmpText.append(qtext.charAt(i));
				}
			}
			qtext = tmpText.toString();
		}
		return query(qtext);
	}

	public void removeAttribute (String name) {
	}

	public void setAttribute (String name,
		String value)
	{
	}

	public void setLanguages (String sourceLang,
		String targetLang)
	{
		srcLang = toInternalCode(sourceLang);
		trgLang = toInternalCode(targetLang);
	}

	private String toInternalCode (String standardCode) {
		String code = standardCode.toLowerCase().replace('-', '_');
		return code;
	}

	private String toISOCode (String internalCode) {
		String code = internalCode.toLowerCase().replace('_', '-');
		return code;
	}

	public void setMaximumHits (int max) {
		maxHits = max;
	}

	public void setThreshold (int threshold) {
		// Not supported currently
	}

	public int getMaximunHits () {
		return maxHits;
	}

	public int getThreshold () {
		// Not supported currently
		return 0;
	}

	public IParameters getParameters() {
		return params;
	}

	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

}
