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

package net.sf.okapi.connectors.microsoft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.XMLWriter;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.BaseConnector;
import net.sf.okapi.lib.translation.ITMQuery;
import net.sf.okapi.common.query.QueryResult;
import net.sf.okapi.lib.translation.QueryUtil;

@UsingParameters(Parameters.class)
public class MicrosoftMTConnector extends BaseConnector implements ITMQuery {

	private final String OPTIONS = "<TranslateOptions xmlns=\"http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2\">" +
		"<Category>general</Category>" +
		"<ContentType>text/html</ContentType>" +
		"<ReservedFlags />" +
		"<State />" +
		"<Uri></Uri>" +
		"<User>defaultUser</User>" +
		"</TranslateOptions>";

	private final String PLACEHOLDER = "[$#@list@#$]";
	
	private QueryUtil util;
	Parameters params;
	int maximumHits = 1;
	int threshold = 95;
	private List<QueryResult> results;
	String template;

	public MicrosoftMTConnector () {
		util = new QueryUtil();
		params = new Parameters();
	}
	
	@Override
	public void close () {
		// Nothing to do
	}

	@Override
	public String getName () {
		return "Microsoft-Translator";
	}

	@Override
	public String getSettingsDisplay () {
		return "Service: http://api.microsofttranslator.com/V2/Http.svc" ;
	}

	@Override
	public void open () {
		results = new ArrayList<QueryResult>();		
	}

	@Override
	public int query (String plainText) {
		return query(new TextFragment(plainText));
	}
	
	static private String fromInputStreamToString (InputStream stream,
		String encoding)
		throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(stream, encoding));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ( (line = br.readLine()) != null ) {
			sb.append(line + "\n");
		}
		br.close();
		return sb.toString();
	}
	
	@Override
	public int query (TextFragment frag) {
		current = -1;
		results.clear();
		if ( !frag.hasText(false) ) return 0;
		try {
			// Convert the fragment to coded HTML
			String stext = util.toCodedHTML(frag);
			URL url = new URL(String.format("http://api.microsofttranslator.com/v2/Http.svc/GetTranslations"
				+ "?appId=%s&text=%s&from=%s&to=%s&maxTranslations=%d",
				params.getAppId(),
				URLEncoder.encode(stext, "UTF-8"),
				srcCode,
				trgCode,
				maximumHits));
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.addRequestProperty("Content-Type", "text/xml");
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
		    conn.setDoInput(true);
		    
			OutputStreamWriter osw = null;
			try {
				osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
				osw.write(OPTIONS);
			}
			finally {
				osw.flush();
				osw.close();
			}
			String resp = fromInputStreamToString(conn.getInputStream(), "UTF-8");

			results = parseBlock(resp, frag);
			
//			// Get the results (they are simple enough to avoid the overhead of using an XML parser)
//			int n1, n2, from = 0;
//			while ( true ) {
//				// Isolate the next match result
//				n1 = resp.indexOf("<TranslationMatch>", from);
//				if ( n1 < 0 ) break; // Done
//				n2 = resp.indexOf("</TranslationMatch>", n1);
//				String res = resp.substring(n1, n2);
//				from = n2+1; // For next iteration
//				// Parse the found match
//				n1 = res.indexOf("<MatchDegree>");
//				n2 = res.indexOf("</MatchDegree>", n1+1);
//				int score = Integer.parseInt(res.substring(n1+13, n2));
//				if ( score < threshold ) continue;
//				
//				// Get the rating
//				int rating = 5;
//				n1 = res.indexOf("<Rating", 0); // No > to handle /> cases
//				n2 = res.indexOf("</Rating>", n1);
//				if ( n2 > -1 ) rating = Integer.parseInt(res.substring(n1+8, n2));
//				// Get the source (when available)
//				n1 = res.indexOf("<MatchedOriginalText", 0); // No > to handle /> cases
//				n2 = res.indexOf("</MatchedOriginalText", n1);
//				if ( n2 > -1 ) stext = unescapeXML(res.substring(n1+21, n2));
//				else stext = null; // No source (same as original
//				// Translation
//				String ttext = "";
//				n1 = res.indexOf("<TranslatedText", n2); // No > to handle /> cases
//				n2 = res.indexOf("</TranslatedText", n1);
//				if ( n2 > -1 ) ttext = unescapeXML(res.substring(n1+16, n2));
//				result = new QueryResult();
//				result.score = score; // Score from the system
//				if ( score > 90 ) {
//					result.score += (rating-10); // Try to adjust high scores
//					// Ideally we would want a composite value for the score
//				}
//				// Weed out the scores lower than the threshold after adjustment
//				if ( result.score < threshold ) continue;
//				// Else: continue with that result
//				result.weight = getWeight();
//				if ( frag.hasCode() ) {
//					if ( stext == null ) result.source = frag;
//					else result.source = new TextFragment(util.fromCodedHTML(stext, frag, false),
//						frag.getClonedCodes());
//					result.target = new TextFragment(util.fromCodedHTML(ttext, frag, false),
//						frag.getClonedCodes());
//				}
//				else {
//					if ( stext == null ) result.source = frag;
//					else result.source = new TextFragment(util.fromCodedHTML(stext, frag, false));
//					result.target = new TextFragment(util.fromCodedHTML(ttext, frag, false));
//				}
//				result.origin = getName();
//				result.matchType = MatchType.MT;
//				results.add(result);
//			}
		}
		catch ( Throwable e) {
			throw new RuntimeException("Error querying the MT server." + e.getMessage(), e);
		}
		if ( results.size() > 0 ) current = 0;
		return results.size();
	}
	
	private String unescapeXML (String text) {
		text = text.replace("&apos;", "'");
		text = text.replace("&lt;", "<");
		text = text.replace("&gt;", ">");
		text = text.replace("&quot;", "\"");
		return text.replace("&amp;", "&"); // Ampersand must be done last
	}

	private List<QueryResult> parseBlock (String block,
		TextFragment frag)
	{
		List<QueryResult> list = new ArrayList<QueryResult>(maximumHits); // No more that maximumHits
		int n1, n2, from = 0;
		// Get the results for the given entry
		while ( true ) {
			// Isolate the next match result
			n1 = block.indexOf("<TranslationMatch>", from);
			if ( n1 < 0 ) break; // Done
			n2 = block.indexOf("</TranslationMatch>", n1);
			String res = block.substring(n1, n2);
			from = n2+1; // For next iteration
			
			// Parse the found match
			n1 = res.indexOf("<MatchDegree>");
			n2 = res.indexOf("</MatchDegree>", n1+1);
			int score = Integer.parseInt(res.substring(n1+13, n2));
			if ( score < threshold ) continue;
			
			// Get the rating
			int rating = 5;
			n1 = res.indexOf("<Rating", 0); // No > to handle /> cases
			n2 = res.indexOf("</Rating>", n1);
			if ( n2 > -1 ) rating = Integer.parseInt(res.substring(n1+8, n2));
			// Get the source (when available)
			n1 = res.indexOf("<MatchedOriginalText", 0); // No > to handle /> cases
			n2 = res.indexOf("</MatchedOriginalText", n1);
			String stext = null; // No source (same as original
			if ( n2 > -1 ) stext = unescapeXML(res.substring(n1+21, n2));
			// Translation
			String ttext = "";
			n1 = res.indexOf("<TranslatedText", n2); // No > to handle /> cases
			n2 = res.indexOf("</TranslatedText", n1);
			if ( n2 > -1 ) ttext = unescapeXML(res.substring(n1+16, n2));
			
			QueryResult qr = new QueryResult();
			qr.score = score; // Score from the system
			if ( score > 90 ) {
				qr.score += (rating-10); // Try to adjust high scores
				// Ideally we would want a composite value for the score
			}
			// Weed out the scores lower than the threshold after adjustment
			if ( qr.score < threshold ) continue;
			// Else: continue with that result
			qr.weight = getWeight();
			if ( frag.hasCode() ) {
				if ( stext == null ) qr.source = frag;
				else qr.source = new TextFragment(util.fromCodedHTML(stext, frag, false),
					frag.getClonedCodes());
				qr.target = new TextFragment(util.fromCodedHTML(ttext, frag, false),
					frag.getClonedCodes());
			}
			else {
				if ( stext == null ) qr.source = frag;
				else qr.source = new TextFragment(util.fromCodedHTML(stext, frag, false));
				qr.target = new TextFragment(util.fromCodedHTML(ttext, frag, false));
			}
			qr.origin = getName();
			qr.matchType = MatchType.MT;
			list.add(qr);
		}
		return list;
	}
	
	private List<List<QueryResult>> parseAllBlocks (String resp,
		List<TextFragment> fragments)
	{
		List<List<QueryResult>> list = new ArrayList<List<QueryResult>>();
		int from = 0;

		// Look for the results of each query:
		for ( TextFragment frag : fragments ) {
			// Move the start at the proper position
			from = resp.indexOf("<Translations>", from);
			if ( from < 0 ) break; // Nothing more
			int n = resp.indexOf("</Translations>", from);
			String block = resp.substring(from, n);
			from = n+1; // For next iteration
			// Parse the block and store the results
			list.add(parseBlock(block, frag));
		}
		
		return list;
	}
	
	/**
	 * Adds or overwrites a translation.
	 * @param source the text of the source.
	 * @param target the new text of the translation.
	 * @param rating the rating to use for this translation.
	 * @return the HTTP response code (200 is success)
	 */
	public int addTranslation (TextFragment source,
		TextFragment target,
		int rating)
	{
		try {
			// Convert the fragment to coded HTML
			String stext = util.toCodedHTML(source);
			String ttext = util.toCodedHTML(target);
			URL url = new URL(String.format("http://api.microsofttranslator.com/v2/Http.svc/AddTranslation"
				+ "?appId=%s&originaltext=%s&translatedtext=%s&from=%s&to=%s&user=defaultUser&rating=%d",
				params.getAppId(),
				URLEncoder.encode(stext, "UTF-8"),
				URLEncoder.encode(ttext, "UTF-8"),
				srcCode,
				trgCode,
				rating));
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.addRequestProperty("Content-Type", "text/xml");
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
		    conn.setDoInput(true);
		    
		    return conn.getResponseCode();
		}
		catch ( Throwable e) {
			throw new RuntimeException("Error adding translation to the server.\n" + e.getMessage(), e);
		}
	}
	
	public List<List<QueryResult>> queryList (List<TextFragment> fragments) {
		List<List<QueryResult>> list = new ArrayList<List<QueryResult>>();

		// Create the query template if needed
		StringWriter strWriter = null;
		try {
			if ( template == null ) {
				strWriter = new StringWriter();
				XMLWriter xmlWriter = new XMLWriter(strWriter);
				xmlWriter.writeStartDocument();
				xmlWriter.writeStartElement("GetTranslationsArrayRequest");
				xmlWriter.writeElementString("AppId", params.getAppId());
				xmlWriter.writeElementString("From", srcCode);
				xmlWriter.writeStartElement("Options");
				xmlWriter.writeAttributeString("xmlns:o", "http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2");
				xmlWriter.writeElementString("o:Category", "");
				xmlWriter.writeElementString("o:ContentType", "text/html");
				xmlWriter.writeElementString("o:ReservedFlags", "");
				xmlWriter.writeElementString("o:State", "");
				xmlWriter.writeElementString("o:Uri", "");
				xmlWriter.writeElementString("o:User", "");
				xmlWriter.writeEndElement(); // Options
				xmlWriter.writeStartElement("Texts");
				xmlWriter.writeAttributeString("xmlns:s", "http://schemas.microsoft.com/2003/10/Serialization/Arrays");
				xmlWriter.writeRawXML(PLACEHOLDER); // Place-holder for the text array
				xmlWriter.writeEndElement(); // Texts
				xmlWriter.writeElementString("To", trgCode);
				xmlWriter.writeElementString("MaxTranslations", String.valueOf(maximumHits));
				xmlWriter.writeEndElement(); // GetTranslationsArrayRequest
				xmlWriter.writeEndDocument();
				xmlWriter.close();
				strWriter.close();
				template = strWriter.toString();
			}
			
			// Fill the template
			StringBuilder sb = new StringBuilder();
			for ( TextFragment tf : fragments ) {
				sb.append("<s:string>");
				String stext = util.toCodedHTML(tf);
				sb.append(Util.escapeToXML(stext, 0, false, null));
				sb.append("</s:string>");
			}

			URL url = new URL(String.format("http://api.microsofttranslator.com/v2/Http.svc/GetTranslationsArray"
				+ "?appId=%s", params.getAppId()));
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.addRequestProperty("Content-Type", "text/xml");
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
		    conn.setDoInput(true);
			OutputStreamWriter osw = null;
			try {
				osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
				String query = template.replace(PLACEHOLDER, sb.toString());
				osw.write(query);
			}
			finally {
				osw.flush();
				osw.close();
			}

			int code = conn.getResponseCode();
			if ( code == 200 ) {
				String resp = fromInputStreamToString(conn.getInputStream(), "UTF-8");
System.out.println(resp);
				list = parseAllBlocks(resp, fragments);
			}
		}
		catch ( MalformedURLException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	
	@Override
	protected String toInternalCode (LocaleId locale) {
		String code = locale.toBCP47();
		if ( code.equals("zh-tw") || code.equals("zh-hant") || code.equals("zh-cht") ) {
			code = "zh-CHT";
		}
		else if ( code.startsWith("zh") ) { // zh-cn, zh-hans, zh-..
			code = "zh-CHS";
		}
		else { // Use just the language otherwise
			code = locale.getLanguage(); 
		}
		return code;
	}

	@Override
	public IParameters getParameters () {
		return params;
	}

	@Override
	public void setParameters (IParameters params) {
		this.params = (Parameters)params;
	}

	@Override
	public boolean hasNext () {
		if ( results == null ) return false;
		if ( current >= results.size() ) {
			current = -1;
		}
		return (current > -1);
	}

	@Override
	public QueryResult next () {
		if ( results == null ) return null;
		if (( current > -1 ) && ( current < results.size() )) {
			current++;
			return results.get(current-1);
		}
		current = -1;
		return null;
	}

	@Override
	public int getMaximumHits () {
		return maximumHits;
	}

	@Override
	public void setMaximumHits (int maximumHits) {
		this.maximumHits = maximumHits;
		template = null;
	}

	@Override
	public int getThreshold () {
		return threshold;
	}

	@Override
	public void setThreshold (int threshold) {
		this.threshold = threshold;
	}

	@Override
	public void setLanguages (LocaleId sourceLocale,
		LocaleId targetLocale)
	{
		super.setLanguages(sourceLocale, targetLocale);
		template = null;
	}

}
