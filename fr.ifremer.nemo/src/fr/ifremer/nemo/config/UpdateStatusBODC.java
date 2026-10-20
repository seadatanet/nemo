package fr.ifremer.nemo.config;

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class UpdateStatusBODC extends UpdateStatus {
	
	public static int INVALID = -1;
	
	/**
	 * version de la liste
	 */
	private int version;
	
	/**
	 * constructeur
	 * @param id
	 */
	public UpdateStatusBODC(String id) {
		super(id);
		version =INVALID;
	}
	/**
	 * constructeur
	 * @param id
	 * @param name
	 */
	public UpdateStatusBODC(String id, String name) {
		super(id, name);
		version =INVALID;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/** 
	 *  relate les informations du status sous forme d'une chaine
	 */
	@Override
	public String getStatusString() {
		StringBuffer object = new StringBuffer();
		
		object.append(etat.toString());
		if(version != INVALID){
			object.append(Global.SEP);
			object.append(Messages.version);
			object.append(Global.SPACE);
			object.append(version);
		}
		
		return object.toString();
	}

}
