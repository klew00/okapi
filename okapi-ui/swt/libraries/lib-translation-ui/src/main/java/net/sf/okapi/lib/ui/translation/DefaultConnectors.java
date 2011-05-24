package net.sf.okapi.lib.ui.translation;

import java.util.ArrayList;
import java.util.List;

public class DefaultConnectors implements IConnectorList {

	private ArrayList<ConnectorInfo> list;
	
	public DefaultConnectors () {
		list = new ArrayList<ConnectorInfo>();
		setDefaultMapping();
	}
	
	// Temporary hard-coded mapping
	private void setDefaultMapping () {
		ConnectorInfo trc = new ConnectorInfo();
		trc.description = "Pensieve TM Engine";
		trc.connectorClass = "net.sf.okapi.connectors.pensieve.PensieveTMConnector";
		trc.descriptionProviderClass = "net.sf.okapi.connectors.pensieve.Parameters";
		list.add(trc);
		
		trc = new ConnectorInfo();
		trc.description = "SimpleTM TM Engine";
		trc.connectorClass = "net.sf.okapi.connectors.simpletm.SimpleTMConnector";
		trc.descriptionProviderClass = "net.sf.okapi.connectors.simpletm.ParametersUI";
		list.add(trc);
		
		trc = new ConnectorInfo();
		trc.description = "GlobalSight TM Web Services";
		trc.connectorClass = "net.sf.okapi.connectors.globalsight.GlobalSightTMConnector";
		trc.descriptionProviderClass = "net.sf.okapi.connectors.globalsight.Parameters";
		list.add(trc);
		
		trc = new ConnectorInfo();
		trc.description = "Google Translate Services";
		trc.connectorClass = "net.sf.okapi.connectors.google.GoogleMTConnector";
		list.add(trc);
		
		trc = new ConnectorInfo();
		trc.description = "MyMemory TM Web Services";
		trc.connectorClass = "net.sf.okapi.connectors.mymemory.MyMemoryTMConnector";
		trc.descriptionProviderClass = "net.sf.okapi.connectors.mymemory.Parameters";
		list.add(trc);

		trc = new ConnectorInfo();
		trc.description = "OpenTran Web Repository";
		trc.connectorClass = "net.sf.okapi.connectors.opentran.OpenTranTMConnector";
		list.add(trc);

		trc = new ConnectorInfo();
		trc.description = "Translate Toolkit TM Server";
		trc.connectorClass = "net.sf.okapi.connectors.translatetoolkit.TranslateToolkitTMConnector";
		trc.descriptionProviderClass = "net.sf.okapi.connectors.translatetoolkit.Parameters";
		list.add(trc);

		trc = new ConnectorInfo();
		trc.description = "Apertium MT Web Server";
		trc.connectorClass = "net.sf.okapi.connectors.apertium.ApertiumMTConnector";
		trc.descriptionProviderClass = "net.sf.okapi.connectors.apertium.Parameters";
		list.add(trc);

		trc = new ConnectorInfo();
		trc.description = "Microsoft MT Server";
		trc.connectorClass = "net.sf.okapi.connectors.microsoft.MicrosoftMTConnector";
		trc.descriptionProviderClass = "net.sf.okapi.connectors.microsoft.Parameters";
		list.add(trc);

//Not ready yet
//		trc = new ConnectorInfo();
//		trc.description = "ProMT MT Server (Beta connector)";
//		trc.connectorClass = "net.sf.okapi.connectors.promt.ProMTConnector";
//		trc.descriptionProviderClass = "net.sf.okapi.connectors.promt.Parameters";
//		list.add(trc);
//

// Not ready		
//		trc = new ConnectorInfo();
//		trc.description = "CrossLanguage MT Server (Beta connector)";
//		trc.connectorClass = "net.sf.okapi.connectors.crosslanguage.CrossLanguageMTConnector";
//		trc.descriptionProviderClass = "net.sf.okapi.connectors.crosslanguage.Parameters";
//		list.add(trc);

//Implement concordance search only for now
//		trc = new ConnectorInfo();
//		trc.description = "TAUS Data Association Search Repository (Beta connector)";
//		trc.connectorClass = "net.sf.okapi.connectors.tda.TDASearchConnector";
//		trc.descriptionProviderClass = "net.sf.okapi.connectors.tda.Parameters";
//		list.add(trc);
	}

	public List<ConnectorInfo> getList () {
		return list;
	}

}
