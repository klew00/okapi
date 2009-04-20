/*===========================================================================*/
/* Copyright (C) 2008 By the Okapi Framework contributors                    */
/*---------------------------------------------------------------------------*/
/* This library is free software; you can redistribute it and/or modify it   */
/* under the terms of the GNU Lesser General Public License as published by  */
/* the Free Software Foundation; either version 2.1 of the License, or (at   */
/* your option) any later version.                                           */
/*                                                                           */
/* This library is distributed in the hope that it will be useful, but       */
/* WITHOUT ANY WARRANTY; without even the implied warranty of                */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser   */
/* General Public License for more details.                                  */
/*                                                                           */
/* You should have received a copy of the GNU Lesser General Public License  */
/* along with this library; if not, write to the Free Software Foundation,   */
/* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA               */
/*                                                                           */
/* See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html */
/*===========================================================================*/

package net.sf.okapi.tm.opentran;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.ITMQuery;
import net.sf.okapi.lib.translation.QueryResult;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class OpenTranTMConnector implements ITMQuery {

	private XmlRpcClient client;
	private String srcLang;
	private String trgLang;
	private List<QueryResult> results;
	private int current = -1;
	private int maxHits = 25;
	
	public String getName () {
		return "OpenTran-Repository";
	}

	public void close () {
		if ( client != null ) {
			client = null; // Free to garbage collect
		}
	}

	public void export (String outputPath) {
		throw new UnsupportedOperationException();
	}

	public String getSourceLanguage () {
		return srcLang;
	}

	public String getTargetLanguage () {
		return trgLang;
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
			//TODO: use the connection string
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL("http://open-tran.eu/RPC2"));
			client = new XmlRpcClient();
			client.setConfig(config);
		}
		catch ( MalformedURLException e ) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public int query (String plainText) {
		try {
			current = -1;
			results = new ArrayList<QueryResult>();
			// Prepare the parameters
			Object[] params = new Object[] {
				new String(plainText),
				new String(srcLang),
				new String(trgLang),
				new Integer(maxHits)};
			
			// Do the query
			Object[] array = (Object[])client.execute("suggest3", params);
			if (( array == null ) || ( array.length == 0 )) return 0;
			
			// Convert the results
			QueryResult qr;
			int count = 0;
			mainLoop:
			for ( Object obj1 : array ) {
				Map<String, Object> map1 = (Map<String, Object>)obj1;
				String trgText = (String)map1.get("text");
				//int value = (Integer)map1.get("value");
				//int count = (Integer)map1.get("count");
				Object[] projects = (Object[])map1.get("projects");
				for ( Object obj2 : projects ) {
					Map<String, Object> map2 = (Map<String, Object>)obj2;
					qr = new QueryResult();
					qr.target = new TextContainer();
					qr.target.append(trgText);
					String srcText = (String)map2.get("orig_phrase");
					qr.source = new TextContainer();
					qr.source.append(srcText);
					results.add(qr);
					// suggest3 maximum parameters limits the number of
					// level-1 object returned, not the total number
					if ( ++count == maxHits ) break mainLoop;
				}
			}

			current = 0;
			return results.size();
		}
		catch ( XmlRpcException e ) {
			throw new RuntimeException(e);
		}
	}

	public int query (TextFragment text) {
		String tmp = text.getCodedText();
		return query(tmp);
	}

	public void removeAttribute (String name) {
		// Not used with this connector
	}

	public void setAttribute (String name,
		String value)
	{
		// Not used with this connector
	}

	public void setLanguages (String sourceLang,
		String targetLang)
	{
		srcLang = toInternalCode(sourceLang);
		trgLang = toInternalCode(targetLang);
	}

	public void setMaximumHits (int max) {
		maxHits = max;
	}

	public void setThreshold (int threshold) {
		// Not used with this connector
	}

	public int getMaximunHits () {
		return maxHits;
	}

	public int getThreshold () {
		// Not used with this connector
		return 0;
	}

	private String toInternalCode (String standardCode) {
		String code = standardCode.toLowerCase().replace('-', '_');
		if ( !code.startsWith("zh") && ( code.length() > 2 )) {
			code = code.substring(0, 2);
		}
		return code;
	}

	public IParameters getParameters () {
		// Not used with this connector
		return null;
	}

	public void setParameters (IParameters params) {
		// Not used with this connector
	}

}
