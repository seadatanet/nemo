/* ============================================================================
 * Nom du fichier	: ManageLists.java
 * Auteur			: dcapitai
 * Date				: 13 avr. 2012
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.config;

import java.util.ArrayList;
import java.util.List;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.UpdateStatus.EtatUpdate;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.ws.model.DataType;
import fr.ifremer.sismer_tools.externalresources.ExternalResourcesManager;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;
import sdn.vocabulary.interfaces.ICollection;
import sdn.vocabulary.interfaces.ICollectionMapping;
import sdn.vocabulary.interfaces.IElement;
import sdn.vocabulary.interfaces.VocabularyException;

/**
 * Description: classe gerant les web service
 * 
 * @author dcapitai
 *
 */
public class ManageLists {
	static protected ArrayList<UpdateStatus> updatedLists;

	static protected List<DataType> _dataTypes = new java.util.ArrayList<DataType>();

	/**
	 * Getter sur updatedLists.
	 * 
	 * @return the updatedLists
	 */
	public static void initLists() {
		NemoLists.initLists();
		initDataTypes();

	}

	public static ArrayList<UpdateStatus> getUpdatedLists() {
		return updatedLists;
	}

	/**
	 * @param id
	 * @return the updatedList selected
	 */
	public static UpdateStatus getUpdate(String id) {
		for (UpdateStatus value : updatedLists) {
			if (value.getId().equals(id)) {
				return value;
			}
		}
		return null;
	}

	/*
	 * Mise à jour des listes
	 */
	private static void initUpdatedLists() {
		updatedLists = new ArrayList<UpdateStatus>();
		updatedLists.add(new UpdateStatusCSR(Intern.id_csr));
		updatedLists.add(new UpdateStatusList(Intern.id_nemo));
		updatedLists.add(new UpdateStatusList(Intern.id_lists));
		updatedLists.add(new UpdateStatusList(Intern.id_maps));
		updatedLists.add(new UpdateStatusNemo(Intern.id_edmo));
		updatedLists.add(new UpdateStatusNemo(Intern.id_edmerp));
	}

	/**
	 * reset updatedLists
	 */
	public static void resetUpdatedLists() {
		if (updatedLists == null) {
			initUpdatedLists();
		} else {
			for (UpdateStatus value : updatedLists) {
				value.reset();
			}
		}
	}

	// TODO : datetype est un hybrid entre Vocab et nemoWs
	/**
	 * Returns the collection of linked objects for role "dataTypes"
	 * 
	 * @return Array of linked objects
	 */
	public static DataType[] getDataTypes() {
		DataType[] v = new DataType[_dataTypes.size()];
		_dataTypes.toArray(v);
		return v;
	}

	public static DataType getDataType(String code) throws Exception {
		for (DataType dt : _dataTypes) {
			if (dt.getCode().equals(code)) {
				return dt;
			}
		}

		throw new Exception(String.format("datatype %s not found in Nemo list", code)); //$NON-NLS-1$
																						// TODO
																						// 35230
																						// msg
	}

	/**
	 * Returns the collection of linked objects for role "dataTypes"
	 * 
	 * @return Array of linked objects
	 */
	public static DataType[] getDataTypesIsSensor() {

		java.util.List<DataType> dataTypesIsSensor = new java.util.ArrayList<DataType>();

		for (DataType object : _dataTypes) {
			if (object.isSensor())
				dataTypesIsSensor.add(object);
		}

		DataType[] v = new DataType[dataTypesIsSensor.size()];
		dataTypesIsSensor.toArray(v);
		return v;
	}

	/**
	 * Returns linked object for role "dataTypes" at index i
	 * 
	 * @param i the index of the linked object
	 * @return linked object at index i
	 */
	public static DataType dataTypesAt(int i) {
		return _dataTypes.get(i);
	}

	/**
	 * returns index of object x in "dataTypes"
	 * 
	 * @param x the linked object
	 */
	public static int dataTypesIndexOf(DataType x) {
		if (!dataTypesIncludes(x))
			return -1;
		for (int i = 0; i < dataTypesSize(); i++) {
			if (dataTypesAt(i) == x) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * move x one index before in the list for role "dataTypes"
	 * 
	 * @param x the object to move
	 */
	public static void dataTypesMoveUp(DataType x) {
		int index;
		index = dataTypesIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveDataTypeAt(x, index - 1);
	}

	/**
	 * move x to the top of list for role "dataTypes"
	 * 
	 * @param x the object to move
	 */
	public static void dataTypesMoveTop(DataType x) {
		int index;
		index = dataTypesIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveDataTypeAt(x, 0);
	}

	/**
	 * move x one index more in the list for role "dataTypes"
	 * 
	 * @param x the object to move
	 */
	public static void dataTypesMoveDown(DataType x) {
		int index;
		index = dataTypesIndexOf(x);
		if (index < 0 || index > dataTypesSize() - 2)
			return;
		nocheckMoveDataTypeAt(x, index + 1);
	}

	/**
	 * move x to the bottom of list for role "dataTypes"
	 * 
	 * @param x the object to move
	 */
	public static void dataTypesMoveBottom(DataType x) {
		int index;
		index = dataTypesIndexOf(x);
		if (index < 0 || index > dataTypesSize() - 2)
			return;
		nocheckMoveDataTypeAt(x, dataTypesSize() - 1);
	}

	/**
	 * move x one index more in the list for role "dataTypes"
	 * 
	 * @param x the object to move
	 */
	public static void nocheckMoveDataTypeAt(DataType x, int a_Index) {
		int index;
		DataType tmp;
		index = dataTypesIndexOf(x);
		tmp = _dataTypes.get(index);
		_dataTypes.remove(index);
		_dataTypes.add(a_Index, tmp);
	}

	/**
	 * remove x from the linked objects for role "dataTypes"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x the object to link with
	 * @see NemoLists#removeDataType(DataType)
	 */
	protected static void nocheckRemoveDataType(DataType x) {
		_dataTypes.remove(x);
	}

	/**
	 * adds x to the linked objects for role "dataTypes"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x the object to link with
	 * @see NemoLists#addDataType(DataType)
	 */
	protected static void nocheckAddDataType(DataType x) {
		_dataTypes.add(x);
	}

	/**
	 * returns the number of linked objects for role "dataTypes"
	 * 
	 * @return the number of linked objects
	 */
	public static int dataTypesSize() {
		return _dataTypes.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "dataTypes"
	 * 
	 * @param x an instance of DataType
	 */
	public static boolean dataTypesIncludes(DataType x) {
		return _dataTypes.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "dataTypes"
	 * 
	 */
	public static java.util.Iterator<DataType> dataTypesIterator() {
		return _dataTypes.iterator();

	}

	/**
	 * release all links for role "dataTypes"
	 */
	public static void releaseDataTypes() {
		for (DataType t : getDataTypes()) {
			removeDataType(t);
		}
	}

	/**
	 * remove link with x for role "dataTypes"
	 * 
	 * @param x the linked object to unlink
	 */
	public static void removeDataType(DataType x) {
		if (!dataTypesIncludes(x))
			return;
		nocheckRemoveDataType(x);
	}

	/**
	 * Add a link for role "dataTypes"
	 * 
	 * @param x the object to link with
	 */
	public static void addDataType(DataType x) {
		if (dataTypesIncludes(x))
			return;
		nocheckAddDataType(x);
	}

	/**
	 * 
	 */
	public static void initDataTypes() {
		Nemo.getInstance().getLogger().info("init datatypes");
		ICollection liste;
		ICollectionMapping sensors;
		try {
			SdnVocabularyManager SdnVocabularyManager = ExternalResourcesManager.getInstance()
					.getSdnVocabularyManager();
			liste = SdnVocabularyManager.getVocabList(Intern.list_ROSCOPparameters);
			sensors = SdnVocabularyManager.getMapping(Intern.list_ROSCOPparameters, Intern.list_instruments);

			for (IElement item : liste.getElements()) {
				DataType dataType = new DataType();
				dataType.setCode(item.getKey());
				dataType.setLibelle(item.getLabel());
				dataType.setSensor(false);

				// is sensor ??
//				List<IElement> listMap = new ArrayList<IElement>();
//				listMap.addAll(sensors.getNarrowElements(item.getKey()));
//				listMap.addAll(sensors.getSameAsElements(item.getKey()));
//				listMap.addAll(sensors.getBroadElements(item.getKey()));
//				listMap.addAll(sensors.getRelatedElements(item.getKey()));
//
//				if (listMap.size()>0)
//				{
//					dataType.setSensor(true);
//				}
				try {
					String match = ExternalResourcesManager.getInstance().getSdnVocabularyManager()
							.getBestVocabMatchCode(SdnVocabularyManager.LIST_L05, SdnVocabularyManager.LIST_C77,
									item.getKey());
//					Nemo.getInstance().getLogger().info(item.getKey()+" match=" + match);
//					if match sensor=true:
					dataType.setSensor(true);
				} catch (Exception e) {
//					Nemo.getInstance().getLogger().error("Invalid datatype : "+item.getKey()+" was not found in C77-L05 mapping");
				}

				_dataTypes.add(dataType);
			}
		} catch (Throwable e) {
			// Catch Throwable (not just VocabularyException) to handle NoClassDefFoundError
			// thrown when JAX-WS internal classes are absent on Java 11+ Alpine CI builds.
			// In that case the vocabulary manager was already initialised from local cache
			// by AbstractTest.setupBeforeClass(), so the cached data is available.
			Nemo.getInstance().getLogger().warn("initDataTypes: vocabulary access failed, datatypes may be incomplete: " + e.getMessage());
		}
		// ordonnancement de la liste des datatypes - tri bulle
		int iteration = _dataTypes.size() - 1;
		boolean permut;
		do {
			permut = false;
			for (int i = 0; i < iteration; i++) {
				DataType item1 = _dataTypes.get(i);
				DataType item2 = _dataTypes.get(i + 1);
				if (item2.getCode().compareTo(item1.getCode()) <= 0) {
					permut = true;
					_dataTypes.set(i, item2);
					_dataTypes.set(i + 1, item1);
				}
			}
			iteration--;
		} while (permut);
	}

	/**
	 * 
	 */
	public static void updateDataTypes() {
		// les listes sont-elles mises à jour ?
		UpdateStatus parameters = ((UpdateStatusList) ManageLists.getUpdate(Intern.id_nemo))
				.getUpdateStatus(Intern.list_ROSCOPparameters);
		UpdateStatus instruments = ((UpdateStatusList) ManageLists.getUpdate(Intern.id_nemo))
				.getUpdateStatus(Intern.list_instruments);

		if ((parameters != null && parameters.getEtat().equals(EtatUpdate.UPDATED))
				|| (instruments != null && instruments.getEtat().equals(EtatUpdate.UPDATED))) {
			_dataTypes.clear();
			initDataTypes();
		}
	}
}
