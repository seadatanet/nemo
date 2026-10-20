package fr.ifremer.nemo.config;

import java.util.Date;

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * CSR status handling.
 */
public class UpdateStatusCSR extends UpdateStatus {

	public static String INVALID_VERSION = null;
	public static Date INVALID_DATE = null;

	/**
	 * CSR version.
	 */
	private String version;

	/**
	 * CSR date.
	 */
	private Date date;

	/**
	 * constructeur
	 * 
	 * @param id
	 */
	public UpdateStatusCSR(String id) {
		super(id);
		version = INVALID_VERSION;
		date = INVALID_DATE;
	}

	/**
	 * constructeur
	 * 
	 * @param id
	 * @param name
	 */
	public UpdateStatusCSR(String id, String name) {
		super(id, name);
		version = INVALID_VERSION;
		date = INVALID_DATE;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}
	
	/**
	 * relate les informations du status sous forme d'une chaine
	 */
	@Override
	public String getStatusString() {
		StringBuffer object = new StringBuffer();

		object.append(etat.toString());
		if (version != INVALID_VERSION) {
			object.append(Global.SEP);
			object.append(Messages.version);
			object.append(Global.SPACE);
			object.append(version);
			object.append(Global.SEP);
			object.append(date);
		}

		return object.toString();
	}


}
