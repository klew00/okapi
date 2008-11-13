package net.sf.okapi.tm.opentran;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.*;

import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.translation.IQuery;
import net.sf.okapi.lib.translation.QueryResult;

public class OpenTranTMConnector implements IQuery {

	private XmlRpcClient client;
	private String srcLang;
	private String trgLang;
	private List<QueryResult> results;
	private int current = -1;
	
	public void close () {
		if ( client != null ) {
			client = null; // Free
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

	public boolean hasOption (int option) {
		switch ( option ) {
		case HAS_SERVER:
			return false; // TODO: allow choice of server
		default:
			return false;
		}
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

	public void open (String connectionString) {
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

	public int query (String plainText) {
		try {
			current = -1;
			results = new ArrayList<QueryResult>();

			Object[] params = new Object[] {
				new String(plainText),
				new String(srcLang),
				new String(trgLang)};
			Object[] array = (Object[])client.execute("suggest2", params);
			if (( array == null ) || ( array.length == 0 )) return 0;

			QueryResult qr;
			for ( Object obj1 : array ) {
				Map<String, Object> map1 = (Map<String, Object>)obj1;
				String trgText = (String)map1.get("text");
				int value = (Integer)map1.get("value");
				int count = (Integer)map1.get("count");
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

	public void removeAttribute (String anme) {
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
		if ( !code.startsWith("zh") && ( code.length() > 2 )) {
			code = code.substring(0, 2);
		}
		return code;
	}

	
}
