/* ============================================================================
 * Nom du fichier	: vocabList.java
 * Auteur			: dcapitai
 * Date				: 13 avr. 2012
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import sdn.vocabulary.implementations.CollectionFactory;
import sdn.vocabulary.interfaces.ICollection;
import sdn.vocabulary.interfaces.ICollectionFactory;
import sdn.vocabulary.interfaces.ICollectionMapping;
import sdn.vocabulary.interfaces.IElement;
import sdn.vocabulary.interfaces.VocabularyException;
import fr.ifremer.nemo.config.UpdateBean.EtatUpdate;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: Class permettant de gerer le list BODDC via la librartie SDNVocabulary
 * @author dcapitai
 *
 */
public class VocabLists {
	static private ICollectionFactory cf;

	static protected HashMap<String, ICollection> _vocabList = new HashMap<String, ICollection>();
	static protected HashMap<String, ICollectionMapping> _vocabMap = new HashMap<String, ICollectionMapping>();

	static {
		String vocabDir = Intern.dir_resources + File.separator + Intern.dir_vocabLists;
		File aRep = new File(vocabDir);

		try {

			CollectionFactory.newInstance(aRep);
			cf = CollectionFactory.getInstance();
		} catch (VocabularyException ve) {
			System.out.println("ERROR : " + ve.getMessage()); //$NON-NLS-1$
		}
	}

	public static void initVocabs() {
		initVocabList();
		initVocabMaps();
	}

	/*
	 * Loading all the vocab list of Parameter vocabLists
	 */
	public static void initVocabList() {
		String[] listNames = Intern.vocabLists.split(Global.COMMA);
		for (String listName : listNames) {
			try {
				_vocabList.put(listName, cf.getCollection(false, listName));
			} catch (VocabularyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * Loading all the vocab mapping of Parameter vocabMaps
	 */
	public static void initVocabMaps() {
		String[] mapNames = Intern.vocabMaps.split(Global.COMMA);
		for (String mapName : mapNames) {
			String[] listNames = mapName.split(Global.UNDERSCORE);

			ICollection vocab = _vocabList.get(listNames[1]);

			try {
				_vocabMap.put(mapName, cf.getMapping(false, vocab, vocab.getMappedDescriptionFromKey(listNames[0])));
			} catch (VocabularyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Getter sur _vocabList.
	 * 
	 * @return the vocabList
	 */
	public static HashMap<String, ICollection> getVocabList() {
		return _vocabList;
	}

	public static ICollection getVocabList(String listName) {
		return _vocabList.get(listName);
	}

	/**
	 * Getter sur _vocabMap.
	 * 
	 * @return the vocabMap
	 */
	public static HashMap<String, ICollectionMapping> getVocabMap() {
		return _vocabMap;
	}

	public static ICollectionMapping getVocabMap(String list1, String list2) {
		return _vocabMap.get(fr.ifremer.nemo.util.Utils.concat(list1, list2, Global.UNDERSCORE));
	}

	public static void setVocabMap(String list1, String list2,
			ICollectionMapping map) {
		_vocabMap.put(fr.ifremer.nemo.util.Utils.concat(list1, list2, Global.UNDERSCORE), map);
	}

	public static String getVocabMatch(String list1, String list2,
			String value, String errorMessage) {
		String strReturn = errorMessage;

		ICollectionMapping tableMap = getVocabMap(list1, list2);

		if (!tableMap.getSameAsElements(value).isEmpty()) {
			strReturn = tableMap.getSameAsElements(value).get(0).getKey();
		} else if (!tableMap.getNarrowElements(value).isEmpty()) {
			strReturn = tableMap.getNarrowElements(value).get(0).getKey();
		} else if (!tableMap.getBroadElements(value).isEmpty()) {
			strReturn = tableMap.getBroadElements(value).get(0).getKey();
		} else if (!tableMap.getRelatedElements(value).isEmpty()) {
			strReturn = tableMap.getRelatedElements(value).get(0).getKey();
		}
		return strReturn;
	}

	/**
	 * 
	 * @param list1
	 *            nom de la liste de parametres de depart
	 * @param list2
	 *            nom de la liste des parametres d'arrivée
	 * @param value
	 *            valeur du entryKey
	 * @return liste des paramètres d'arrivée qui correspondent à la valeur
	 *         value dans le liste de départ
	 * 
	 *         ex: list1=P091 list2=P021, value=BATH renvoie les parametres P021
	 *         correspondant au parametre BATH en P091
	 */
	public static List<IElement> getVocabMatchs(String list1, String list2,
			String value) {
		ICollectionMapping tableMap = getVocabMap(list1, list2);
		List<IElement> listReturn = new ArrayList<IElement>();

		if (!tableMap.getSameAsElements(value).isEmpty()) {
			listReturn.addAll(tableMap.getSameAsElements(value));
		}

		if (!tableMap.getNarrowElements(value).isEmpty()) {
			listReturn.addAll(tableMap.getNarrowElements(value));
		}

		if (!tableMap.getBroadElements(value).isEmpty()) {
			listReturn.addAll(tableMap.getBroadElements(value));
		}

		if (!tableMap.getRelatedElements(value).isEmpty()) {
			listReturn.addAll(tableMap.getRelatedElements(value));
		}

		return listReturn;
	}

	public static void updateVocabLists(IProgressMonitor monitor) {

		UpdateBean updateLists = ManageLists.getUpdate(Intern.id_lists);
		String[] listNames = Intern.vocabLists.split(Global.COMMA);
		int keySetSize = listNames.length;

		int idxCurrent = 0;
		int nbRetrieve = 0;
		try {
			cf.reload();
		} catch (VocabularyException e1) {

			e1.printStackTrace();
		}
		for (String listName : listNames) {
			try {
				if (!monitor.isCanceled()) {
					StringBuffer msg = new StringBuffer();
					msg.append(Messages.getString(Intern.id_lists));
					msg.append(Global.SPACE);
					msg.append(listName);
					msg.append(" [");//$NON-NLS-1$
					msg.append((idxCurrent));
					msg.append(" / ");//$NON-NLS-1$
					msg.append(keySetSize);
					msg.append("]");//$NON-NLS-1$
					monitor.subTask(msg.toString());

					UpdateBean updateMap = updateVocabList(listName);
					updateLists.addSubUpdate(updateMap);

					if (updateLists.getEtat().compareTo(updateMap.getEtat()) < 0) {
						updateLists.setEtat(updateMap.getEtat());
					}
				} else {
					updateLists.setEtat(EtatUpdate.INTERRUPTED);
				}

				updateLists.setNbRetrieve(nbRetrieve);
			} catch (Exception e) {
				updateLists.setEtat(EtatUpdate.FAILED);
				e.printStackTrace();
			}
		}
	}

	public static UpdateBean updateVocabList(String listId) {

		String label = _vocabList.get(listId).getDescription().getLabel();

		StringBuffer msg = new StringBuffer();
		msg.append(listId);
		msg.append(Global.SEP);
		msg.append(label);
		UpdateBean updateList = new UpdateBean(listId, msg.toString());
		int nbTot = _vocabList.get(listId).getElements().size();
		updateList.setNbTot(nbTot);
		int numVersion = _vocabList.get(listId).getDescription().getVersion();

		try {
			cf.loadCollections(true, listId);
			_vocabList.put(listId, cf.getCollection(true, listId));
			updateList.setEtat(EtatUpdate.UPDATED);
		} catch (VocabularyException e) {
			updateList.setEtat(EtatUpdate.FAILED);
			e.printStackTrace();
		}

		if (numVersion == _vocabList.get(listId).getDescription().getVersion())
			updateList.setEtat(EtatUpdate.CHECKED);
		updateList.setNbRetrieve(_vocabList.get(listId).getElements().size() - nbTot);
		return updateList;
	}

	public static void updateMapLists(IProgressMonitor monitor) {

		UpdateBean updateMaps = ManageLists.getUpdate(Intern.id_maps);

		String[] listMaps = Intern.vocabMaps.split(Global.COMMA);
		int keySetSize = listMaps.length;

		int idxCurrent = 0;
		int nbRetrieve = 0;

		for (String mapName : listMaps) {
			try {
				if (!monitor.isCanceled()) {
					StringBuffer msg = new StringBuffer();
					msg.append(Messages.getString(Intern.id_lists));
					msg.append(Global.SPACE);
					msg.append(mapName);
					msg.append(" [");//$NON-NLS-1$
					msg.append((idxCurrent));
					msg.append(" / ");//$NON-NLS-1$
					msg.append(keySetSize);
					msg.append("]");//$NON-NLS-1$
					monitor.subTask(msg.toString());

					UpdateBean updateMap = updateMapLists(mapName);
					updateMaps.addSubUpdate(updateMap);
					if (updateMap.getEtat().compareTo(EtatUpdate.UPDATED) == 0
							|| updateMap.getEtat().compareTo(EtatUpdate.CHECKED) == 0) {
						nbRetrieve++;
					}
					if (updateMaps.getEtat().compareTo(updateMap.getEtat()) < 0) {
						updateMaps.setEtat(updateMap.getEtat());
					}

				} else {
					updateMaps.setEtat(EtatUpdate.INTERRUPTED);
				}
				updateMaps.setNbRetrieve(nbRetrieve);

			} catch (Exception e) {
				updateMaps.setEtat(EtatUpdate.FAILED);
				e.printStackTrace();
			}
		}
	}

	public static UpdateBean updateMapLists(String mapName) {

		UpdateBean updateMap = new UpdateBean(mapName, mapName);
		try {
			String[] listNames = mapName.split(Global.UNDERSCORE);
			ICollection vocab = _vocabList.get(listNames[1]);
			cf.loadMappings(true, vocab, vocab.getMappedDescriptionFromKey(listNames[0]));
			_vocabMap.put(mapName, cf.getMapping(false, vocab, vocab.getMappedDescriptionFromKey(listNames[0])));
			updateMap.setEtat(EtatUpdate.UPDATED);
		} catch (VocabularyException e) {
			updateMap.setEtat(EtatUpdate.FAILED);
			e.printStackTrace();
		}

		return updateMap;
	}

}
