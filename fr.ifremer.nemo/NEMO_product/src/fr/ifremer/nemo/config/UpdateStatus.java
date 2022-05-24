package fr.ifremer.nemo.config;

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * classe abstraite UpdateStatus
 * classe mere pour le stockage des Informations du processus de mise à jour
 * @author dcapitaine
 *
 */

public abstract class UpdateStatus {
	
	public static enum EtatUpdate{
	        LOCAL, CHECKED, UPDATED, FAILED, INTERRUPTED;
	        
	        @Override
			public String toString() {
				String value = Global.EMPTY_STRING;
				switch (this) {
					case CHECKED:
						value=Messages.CHECKED;
						break;
					case UPDATED:
						value=Messages.UPDATED;
						break;
					case FAILED:
						value=Messages.FAILED;
						break;
					case INTERRUPTED:
						value=Messages.INTERRUPTED;
						break;
					default:
						value=Messages.ERROR;
				}
				return value;
			}
	}
	
	private final String id;
	protected final String name;
	protected EtatUpdate etat;
	
	
	/**
	 * Constructeur
	 * @param id
	 */
	public UpdateStatus(String id){
		this.id = id;
		this.name = Messages.getString(id);
		reset();
	}
	
	/**
	 * Constructeur
	 * @param id
	 * @param name
	 */
	public UpdateStatus(String id, String name){
		this.id = id;
		this.name = name;
		reset();
	}
	
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * reset etat
	 */
	public void reset()
	{
		etat = EtatUpdate.LOCAL;
	}
	

	/**
     * Getter sur etat.
     * @return the etat
     */
    public EtatUpdate getEtat()
    {
        return etat;
    }

    /**
     * Setter sur etat.
     * @param etat the etat to set
     */
    public void setEtat(EtatUpdate etat)
    {
        this.etat = etat;
    }
    
    /** 
	 *  relate les informations du status sous forme d'une chaine
	 */
    abstract public String getStatusString();
    
}
