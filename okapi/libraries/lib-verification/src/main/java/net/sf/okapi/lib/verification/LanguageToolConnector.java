/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
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

package net.sf.okapi.lib.verification;

import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Segment;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.connectors.google.GoogleMTConnector;
import net.sf.okapi.lib.translation.IQuery;

public class LanguageToolConnector {

	private ArrayList<Issue> issues;
	private String lang;
	private String serverUrl;
	private DocumentBuilder docBuilder;
	private IQuery mt;
	
	/**
	 * Creates a new LanguageToolConnector object.
	 */
	public LanguageToolConnector () {
		issues = new ArrayList<Issue>();
		DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
		Fact.setValidating(false);
		try {
			docBuilder = Fact.newDocumentBuilder();
		}
		catch ( ParserConfigurationException e ) {
			throw new RuntimeException("Error creating document builder.", e);
		}
	}

	public void initialize (LocaleId locId,
		String serverUrl,
		boolean translateLTMsg,
		String ltTranslationSource,
		String ltTranslationTarget)
	{
		//TODO: Better mapping to LT language codes
		lang = locId.getLanguage();
		// Set the server URL
		if ( !serverUrl.endsWith("/") ) serverUrl += "/";
		this.serverUrl = serverUrl;

		if ( mt != null ) {
			mt.close();
			mt = null;
		}
		if ( translateLTMsg ) {
			mt = new GoogleMTConnector();
			mt.setLanguages(LocaleId.fromBCP47(ltTranslationSource),
				LocaleId.fromBCP47(ltTranslationTarget));
			mt.open();
		}
	}
	
	public List<Issue> getIssues () {
		return issues;
	}

	public int checkSegment (String docId,
		Segment seg,
		TextUnit tu)
	{
		issues.clear();
		if ( !seg.text.hasText() ) return 0;
		String ctext = seg.text.getCodedText();

		// Create the connection and query
		URL url;
		try {
			url = new URL(serverUrl + String.format("?language=%s&text=%s", lang,
				URLEncoder.encode(ctext, "UTF-8")));
			URLConnection conn = url.openConnection();
			
			// Get and process the results
			Document doc = docBuilder.parse(conn.getInputStream(), "UTF-8");
			NodeList errors = doc.getDocumentElement().getElementsByTagName("error");
			for ( int i=0; i<errors.getLength(); i++ ) {
				Element error = (Element)errors.item(i);
				String msg = error.getAttribute("msg");
				if ( mt != null ) {
					if ( mt.query(msg) > 0 ) {
						msg = String.format("%s  (--> %s)", msg, mt.next().target.toString());
					}
				}
				int start = Integer.valueOf(error.getAttribute("fromx"));
				int end = Integer.valueOf(error.getAttribute("tox"));
				issues.add(new Issue(docId, IssueType.LANGUAGETOOL_ERROR, tu.getId(), seg.getId(), msg, 0, 0, start, end));
			}
		}
		catch ( ConnectException e ) {
			throw new RuntimeException("Connection error with the LanguageTool server.", e);
		}
		catch ( Throwable e ) {
			throw new RuntimeException("Error while verifying a segment with the LanguageTool server.", e);
		}
		
		return issues.size();
	}

}
