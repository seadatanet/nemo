package fr.ifremer.nemo.config;

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;


/**
 * classe UpdateStatusNemo
 * classe pour le stockage des Informations 
 * du processus de mise à jour des webservice Nemo
 * @author dcapitaine
 *
 */
public class UpdateStatusNemo extends UpdateStatus{
    
	/**
	 * nombre d'entrée recupere
	 */
	private int nbRetrieve;
	
	/**
	 * constructeur
	 * @param id
	 */
	public UpdateStatusNemo(String id){
		super(id);
		reset();
	}
	
	/**
	 * constructeur
	 * @param id
	 * @param name
	 */
	public UpdateStatusNemo(String id, String name){
		super(id, name);
		reset();
	}
	

	/**
	 * @return the nbRetrieve
	 */
	public int getNbRetrieve() {
		return nbRetrieve;
	}
    

	/**
	 * @param nbRetrieve the nbRetrieve to set
	 */
	public void setNbRetrieve(int nbRetrieve) {
		this.nbRetrieve = nbRetrieve;
	}



    /** 
	 *  relate les informations du status sous forme d'une chaine
	 */
    @Override
	public String getStatusString()
	{
		StringBuffer object = new StringBuffer();
		object.append(etat.toString());
		object.append(Global.SEP);
		object.append(nbRetrieve);
		object.append(Global.SPACE);
		object.append(Messages.retrieved);

		return object.toString();
	}
	
}
