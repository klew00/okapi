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

package net.sf.okapi.lib.verification;

import java.net.URI;
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
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.connectors.google.GoogleMTv2Connector;
import net.sf.okapi.connectors.google.GoogleMTv2Parameters;
import net.sf.okapi.common.query.IQuery;

public class LanguageToolConnector {

	private ArrayList<Issue> issues;
	private String lang;
	private String motherTongue;
	private String serverUrl;
	private DocumentBuilder docBuilder;
	private IQuery mt;
	private boolean bilingualMode;
	
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
		LocaleId motherLocId, 
		String serverUrl,
		boolean translateLTMsg,
		boolean bilingualMode,
		String ltTranslationSource,
		String ltTranslationTarget,
		String ltTranslationServiceKey)
	{
		//TODO: Better mapping to LT language codes
		lang = locId.getLanguage();
		motherTongue = motherLocId.getLanguage();
		// Set the server URL
		if ( !serverUrl.endsWith("/") ) serverUrl += "/";
		this.serverUrl = serverUrl;

		if ( mt != null ) {
			mt.close();
			mt = null;
		}
		this.bilingualMode = bilingualMode;
		if ( translateLTMsg ) {
			mt = new GoogleMTv2Connector();
			GoogleMTv2Parameters prm = (GoogleMTv2Parameters)mt.getParameters();
			prm.setApiKey(ltTranslationServiceKey);
			mt.setLanguages(LocaleId.fromBCP47(ltTranslationSource),
				LocaleId.fromBCP47(ltTranslationTarget));
			mt.open();
		}
	}
	
	public List<Issue> getIssues () {
		return issues;
	}

	public int checkSegment (URI docId,
		Segment srcSeg,
		Segment trgSeg,
		ITextUnit tu)
	{
		issues.clear();
		if ( !trgSeg.text.hasText() ) return 0;
		String ctext = trgSeg.text.getCodedText();

		// Create the connection and query
		URL url;
		try {
			if ( bilingualMode ) {
				url = new URL(serverUrl + String.format("?language=%s&text=%s&srctext=%s&motherTongue=%s", lang,
					URLEncoder.encode(ctext, "UTF-8"),
					URLEncoder.encode(srcSeg.text.getCodedText(), "UTF-8"),
					motherTongue));
			}
			else {
				url = new URL(serverUrl + String.format("?language=%s&text=%s", lang,
					URLEncoder.encode(ctext, "UTF-8")));
			}
			URLConnection conn = url.openConnection();
			
			// Get and process the results
			Document doc = docBuilder.parse(conn.getInputStream(), "UTF-8");
			NodeList errors = doc.getDocumentElement().getElementsByTagName("error");
			for ( int i=0; i<errors.getLength(); i++ ) {
				Element error = (Element)errors.item(i);
				String msg = error.getAttribute("msg");
				if ( mt != null ) {
					if ( mt.query(msg) > 0 ) {
						msg = String.format("%s  (--> %s)", msg, mt.next().target.toText());
					}
				}
				int start = Integer.valueOf(error.getAttribute("fromx"));
				int end = start+Integer.valueOf(error.getAttribute("errorlength"));
				issues.add(new Issue(docId, IssueType.LANGUAGETOOL_ERROR, tu.getId(), trgSeg.getId(), msg, 0, 0,
					QualityChecker.fromFragmentToString(trgSeg.text, start),
					QualityChecker.fromFragmentToString(trgSeg.text, end),
					Issue.SEVERITY_MEDIUM, tu.getName()));
			}
		}
		catch ( Throwable e ) {
			// -99 for srcEnd special marker
			issues.add(new Issue(docId, IssueType.LANGUAGETOOL_ERROR, tu.getId(), trgSeg.getId(),
				"Error with LanguageTool server. All LT checks are skipped from this text unit on. "+e.getMessage(),
				0, -99, 0, -1, Issue.SEVERITY_HIGH, tu.getName()));
		}
		
		return issues.size();
	}

}
