package fr.ifremer.nemo.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

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
				try{
				String[] lastDate = state.getLastVersionDay().split("-");
				Nemo.getInstance().getLogger().warn(MessageFormat.format(Messages.NemoVersionUpdateAvailable, state.getVersion()  , state.getLastVersion(), lastDate[0], lastDate[1], lastDate[2]));
				}catch (Exception e) {
					Nemo.getInstance().getLogger().warn(Messages.NemoVersionUnreachable);
				}
			}


		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		return state;
	}


}
