/* ============================================================================
 * Nom du fichier	: vocabList.java
 * Auteur			: dcapitai
 * Date				: 13 avr. 2012
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.UpdateStatus.EtatUpdate;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.sismer_tools.externalresources.ExternalResourcesManager;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;
import sdn.vocabulary.interfaces.ICollection;
import sdn.vocabulary.interfaces.ICollectionMapping;
import sdn.vocabulary.interfaces.VocabularyException;

/**
 * 
 * 
 * 
 */
public class NemoExternalResourcesManager {

	public static void init() {
		try {
			FileInputStream extResourcesConf = new FileInputStream(new File("resources/externalResourcesConfiguration.yaml"));
			ExternalResourcesManager.getInstance(extResourcesConf, "resources/externalResources");
			try {
				ExternalResourcesManager.getInstance().getSdnVocabularyManager().init(false);
			}catch (Exception e) {
				Nemo.getInstance().getLogger().warn(e.getMessage());
			}
			ExternalResourcesManager.getInstance().getCsrListManager().getInstance().checkAndDownloadIfNeeded();
		} catch (Exception e) {
			Nemo.getInstance().getLogger().error(e.getMessage());
		}
	}

	/**
	 * update vocabulary map from vocabulary file
	 * 
	 * @param monitor
	 */
	public static void updateVocabLists(IProgressMonitor monitor) {

		UpdateStatusList updateLists = (UpdateStatusList) ManageLists.getUpdate(Intern.id_lists);
		UpdateStatusList updateMaps = (UpdateStatusList) ManageLists.getUpdate(Intern.id_maps);
		int idxCurrent = 1;
		SdnVocabularyManager sdnMgr = ExternalResourcesManager.getInstance().getSdnVocabularyManager();
		HashMap<String,Integer>currentVersions = new HashMap<>();
		HashMap<String,UpdateStatusBODC>updateStatusBODCs = new HashMap<>();
		HashMap<String, ICollection> lists = ExternalResourcesManager.getInstance().getSdnVocabularyManager().get_vocabList();
		HashMap<String, ICollectionMapping> maps = ExternalResourcesManager.getInstance().getSdnVocabularyManager().get_vocabMap();
		
		//---------------------------------------------------------------------
		// read existing lists and init
		if (monitor == null || !monitor.isCanceled()) {
			if (monitor != null) {
				monitor.subTask(Messages.getString(Intern.id_lists));
			}
		}
		
		ICollection oneList=null;
		for (Entry<String, ICollection> list : lists.entrySet()){
			oneList= list.getValue();
			StringBuffer msg = new StringBuffer();
			if (oneList==null) {
				currentVersions.put(list.getKey() , -1);
				msg.append(list.getKey() );
			}else {
				currentVersions.put(list.getKey() , list.getValue().getDescription().getVersion());
				msg.append(list.getKey() + Global.SEP+list.getValue().getDescription().getLabel());
			}

			UpdateStatusBODC updateStatusBODC = new UpdateStatusBODC(list.getKey(), msg.toString());
			updateLists.addUpdateStatus(updateStatusBODC);
			updateStatusBODCs.put(list.getKey(),updateStatusBODC);

			idxCurrent++;
		}

		//---------------------------------------------------------------------
		// call global reload
		try {
			Nemo.getInstance().getLogger().info("launch Vocabularies reload");
			sdnMgr.reload();
			// update lists in _vocabbList and_vocabMaps
			sdnMgr.init(true);
		} catch (VocabularyException e) {
			Nemo.getInstance().getLogger().error("Vocabularies reload error: " +e.getMessage());
			updateLists.setEtat(EtatUpdate.FAILED);
		}
		//---------------------------------------------------------------------
		// compare versions
		int oldNumVersion;int newNumVersion;boolean updated=false;
		oneList=null;
		String label="";
		int keySetSize=lists.size();
		idxCurrent=1;
		for (Entry<String, ICollection> list : lists.entrySet()){
			try {
				if (monitor == null || !monitor.isCanceled()) {
					String toMonitor="";
					toMonitor= Messages.getString(Intern.id_lists)+Global.SPACE +"[" + idxCurrent+"/"+keySetSize+"] "+ list.getKey();
					if (monitor != null) {
						monitor.subTask(toMonitor);
					}
				}
				
				oneList=list.getValue();
				oldNumVersion=currentVersions.get(list.getKey());
				if (oneList==null) {
					newNumVersion=-1;
				}else {
					newNumVersion=list.getValue().getDescription().getVersion();
					label=list.getValue().getDescription().getLabel();
				}
				Nemo.getInstance().getLogger().info("BODC list "+list.getKey() +  " : "+oldNumVersion+" -> "+newNumVersion);
				// set new version
				if (updateStatusBODCs.get(list.getKey())==null) {
					UpdateStatusBODC updateStatusBODC = new UpdateStatusBODC(list.getKey(), list.getKey()+Global.SEP+label);
					updateStatusBODCs.put(list.getKey(),updateStatusBODC);
				}
				updateStatusBODCs.get(list.getKey()).setVersion(newNumVersion);
				// set status
				if (oldNumVersion == newNumVersion) {
					updateStatusBODCs.get(list.getKey()).setEtat(EtatUpdate.CHECKED);
				}else if(oldNumVersion < newNumVersion){
					updateStatusBODCs.get(list.getKey()).setEtat(EtatUpdate.UPDATED);
					updated=true;
				}
			}catch (Exception e) {
				updateStatusBODCs.get(list.getKey()).setEtat(EtatUpdate.FAILED);
			}
			idxCurrent++;
		}
		if (updated) {
			updateLists.setEtat(EtatUpdate.UPDATED);
		}else {
			updateLists.setEtat(EtatUpdate.CHECKED);
		}

		//---------------------------------------------------------------------
		// read existing maps and init
		if (monitor == null || !monitor.isCanceled()) {
			if (monitor != null) {
				monitor.subTask(Messages.getString(Intern.id_maps));
			}
		}
		idxCurrent=1;
		keySetSize=maps.size();
		String to,from;
		ICollectionMapping oneMap=null;
		for (Entry<String, ICollectionMapping> map : maps.entrySet()){
			from=map.getKey().split("_")[0];
			to=map.getKey().split("_")[1];
			StringBuffer msg = new StringBuffer();
			msg.append(map.getKey());

			UpdateStatusBODC updateStatusBODC = new UpdateStatusBODC(map.getKey(), msg.toString());
			updateMaps.addUpdateStatus(updateStatusBODC);
			updateStatusBODCs.put(map.getKey(),updateStatusBODC);
			boolean ok=true;
			try {
				ICollection vocab = sdnMgr.getCf().getCollection(false, to);
				oneMap = sdnMgr.getCf().getMapping(false, vocab, vocab.getMappedDescriptionFromKey(from));
				if (oneMap!=null) {
					updateStatusBODC.setEtat(EtatUpdate.UPDATED);
				}else {
					updateStatusBODC.setEtat(EtatUpdate.FAILED);
				}
			} catch (VocabularyException e) {
				updateStatusBODC.setEtat(EtatUpdate.FAILED);
				ok=false;
			}
			if(ok) {
				updateMaps.setEtat(EtatUpdate.UPDATED);
			}else {
				updateMaps.setEtat(EtatUpdate.FAILED);
			}
			if (monitor == null || !monitor.isCanceled()) {
				msg = new StringBuffer();
				msg.append(Messages.getString(Intern.id_maps));
				msg.append(Global.SPACE +"[" + idxCurrent+"/"+keySetSize+"] "+ map.getKey());
				if (monitor != null) {
					monitor.subTask(msg.toString());
				}
			}
			idxCurrent++;
		}

	}


}
