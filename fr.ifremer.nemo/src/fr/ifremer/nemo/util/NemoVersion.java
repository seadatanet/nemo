package fr.ifremer.nemo.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.osgi.framework.Version;

import software.version.SoftwareState;
import software.version.SoftwareState.STATE;
import software.version.SoftwareVersionClient;
import fr.ifremer.nemo.Nemo;

public class NemoVersion {



	public static SoftwareState check (Version version) {
		SoftwareVersionClient client;
		SoftwareState state = null;
		try
		{                      
			client = new SoftwareVersionClient(new URL("http://www.ifremer.fr/SoftwareVersionSoap/SoftwareVersionWebService?wsdl"));                                


			state = client.getSoftwareState("NEMO", version.toString());
			if(state.getState() == STATE.LAST_VERSION){
				Nemo.getInstance().getLogger().info ( state.getDescription());
			}else{
				Nemo.getInstance().getLogger().warn("You are currently using Nemo version " + state.getVersion()  
						+ ". A new version is available since "+state.getLastVersionDay()+". Please udate to " + state.getLastVersion());  
			}


		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		return state;
	}


}
