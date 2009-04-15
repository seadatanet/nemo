package fr.ifremer.nemo.config;

import java.util.ArrayList;

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;


/**
 * update process informations
 * @author IFREMER
 */
public class UpdateBean{
    
    public static enum EtatUpdate{
        LOCAL, CHECKED, UPDATED, FAILED, INTERRUPTED;
    }

	private String id;
	private String name;
	private EtatUpdate etat;
	private ArrayList<UpdateBean> subUpdates;
	private int nbRetrieve;
	private int nbTot;
	
	public UpdateBean(String id){
		this.id = id;
		this.name = Messages.getString(id);
		reset();
	}
	
	public UpdateBean(String id, String name){
		this.id = id;
		this.name = name;
		reset();
	}
	
	/**
	 * reset object
	 */
	public void reset(){
        etat = EtatUpdate.LOCAL;
		subUpdates = new ArrayList<UpdateBean>();
		nbRetrieve = 0;
		this.nbTot = 0;
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
	 * @return the nbRetrieve
	 */
	public int getNbRetrieve() {
		return nbRetrieve;
	}
    
    public UpdateBean getSubUpdate(String id){
        for (UpdateBean update : subUpdates){
            if (update.getId().equals(id)){
                return update;
            }
        }
        return null;
    }

	/**
	 * @param nbRetrieve the nbRetrieve to set
	 */
	public void setNbRetrieve(int nbRetrieve) {
		this.nbRetrieve = nbRetrieve;
	}

    /**
     * 
     */
    public void setNbRetrieve() {
        this.nbRetrieve = subUpdates.size();
    }

	/**
	 * @return the nbTot
	 */
	public int getNbTot() {
		return nbTot;
	}

	/**
	 * @param nbTot the nbTot to set
	 */
	public void setNbTot(int nbTot) {
		this.nbTot = nbTot;
	}

	/**
	 * @return the subUpdates
	 */
	public ArrayList<UpdateBean> getSubUpdates() {
		return subUpdates;
	}
	
	/**
	 * @param subUpdate  the updated to add
	 */
	public void addSubUpdate(UpdateBean subUpdate){
		subUpdates.add(subUpdate);
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
	 * relate main object informations into a string
	 */
	@Override
    public String toString()
	{
		StringBuffer object = new StringBuffer();
		object.append(name);
		object.append(Global.SPACE);
		object.append(Global.SLASH);
		object.append(" etat = "); //$NON-NLS-1$
		object.append(etat.toString());
		object.append(Global.SPACE);
		object.append(Global.SLASH);
		object.append(" nbUpdated = "); //$NON-NLS-1$
		object.append(nbRetrieve);
		object.append(Global.SPACE);
		object.append(Global.SLASH);
		object.append(" nb subUpdates = "); //$NON-NLS-1$
		object.append(subUpdates.size());
		
		return object.toString();
	}
	
}
