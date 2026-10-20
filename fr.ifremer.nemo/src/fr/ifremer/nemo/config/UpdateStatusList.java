package fr.ifremer.nemo.config;

import java.util.ArrayList;

/**
 * classe UpdateStatusList
 * classe permettant le stockage de chaque type de liste
 *
 */
public class UpdateStatusList extends UpdateStatus {

	/**
	 * ArrayList updateList
	 */
	private ArrayList<UpdateStatus> updateList;
	
	/**
	 * constructeur
	 * @param id
	 */
	public UpdateStatusList(String id) {
		super(id);
		updateList = new ArrayList<UpdateStatus>();
	}
	
	/**
	 * constructeur
	 * @param id
	 * @param name
	 */
	public UpdateStatusList(String id, String name) {
		super(id, name);
		updateList = new ArrayList<UpdateStatus>();
	}
	
	
	@Override
	public void reset(){
		super.reset();
		updateList = new ArrayList<UpdateStatus>();
	}

	@Override
	public String getStatusString() {
		StringBuffer object = new StringBuffer();
		
		object.append(etat.toString());
		return object.toString();
	}
	
	/**
	 * get specifique sub update
	 * @param updateId
	 * @return
	 */
	public UpdateStatus getUpdateStatus(String updateId){
        for (UpdateStatus update : updateList){
            if (update.getId().equals(updateId)){
                return update;
            }
        }
        return null;
    }
	
	/**
	 * @return the subUpdates
	 */
	public ArrayList<UpdateStatus> getUpdateList() {
		return updateList;
	}
	
	/**
	 * @param subUpdate  the updated to add
	 */
	public void addUpdateStatus(UpdateStatus updateSatus){
		updateList.add(updateSatus);
	}
	

}
