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
import fr.ifremer.nemo.ws.WSDataType;
import fr.ifremer.sismer_tools.externalresources.ExternalResourcesManager;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;
import sdn.vocabulary.interfaces.ICollection;
import sdn.vocabulary.interfaces.ICollectionMapping;
import sdn.vocabulary.interfaces.IElement;
import sdn.vocabulary.interfaces.VocabularyException;

/**
 * Description: classe gerant les web service
 * @author dcapitai
 *
 */
public class ManageLists
{
	static protected ArrayList<UpdateStatus> updatedLists;

	static protected List<WSDataType> _dataTypes = new java.util.ArrayList<WSDataType>();


	/**
	 * Getter sur updatedLists.
	 * 
	 * @return the updatedLists
	 */
	public static void initLists()
	{
		NemoLists.initLists();
//		VocabLists.initVocabs();
		initDataTypes();

	}


	public static ArrayList<UpdateStatus> getUpdatedLists()
	{
		return updatedLists;
	}

	/**
	 * @param id
	 * @return the updatedList selected
	 */
	public static UpdateStatus getUpdate(String id)
	{
		for (UpdateStatus value : updatedLists)
		{
			if (value.getId().equals(id))
			{
				return value;
			}
		}
		return null;
	}


	/*
	 * Mise à jour des listes
	 */
	private static void initUpdatedLists()
	{
		updatedLists = new ArrayList<UpdateStatus>();
		updatedLists.add(new UpdateStatusList(Intern.id_nemo));
		updatedLists.add(new UpdateStatusList(Intern.id_lists));
		updatedLists.add(new UpdateStatusList(Intern.id_maps));
		updatedLists.add(new UpdateStatusNemo(Intern.id_edmo));
	}




	/**
	 * reset updatedLists
	 */
	public static void resetUpdatedLists()
	{
		if (updatedLists == null)
		{
			initUpdatedLists();
		} else
		{
			for (UpdateStatus value : updatedLists)
			{
				value.reset();
			}
		}
	}




	//TODO : datetype est un hybrid entre Vocab et nemoWs
	/**
	 * Returns the collection of linked objects for role "dataTypes"
	 * 
	 * @return Array of linked objects
	 */
	public static WSDataType[] getDataTypes()
	{
		WSDataType[] v = new WSDataType[_dataTypes.size()];
		_dataTypes.toArray(v);
		return v;
	}

	public static WSDataType getDataType(String code) throws Exception
	{
		for (WSDataType dt : _dataTypes){
			if (dt.getCode().equals(code)){
				return dt;
			}
		}
		throw new Exception("datatype " + code + "not found in Nemo list"); // TODO 35230 msg
	}

	/**
	 * Returns the collection of linked objects for role "dataTypes"
	 * 
	 * @return Array of linked objects
	 */
	public static WSDataType[] getDataTypesIsSensor()
	{

		java.util.List<WSDataType> dataTypesIsSensor = new java.util.ArrayList<WSDataType>();

		for (WSDataType object : _dataTypes)
		{
			if (object.isSensor())
				dataTypesIsSensor.add(object);
		}

		WSDataType[] v = new WSDataType[dataTypesIsSensor.size()];
		dataTypesIsSensor.toArray(v);
		return v;
	}

	/**
	 * Returns linked object for role "dataTypes" at index i
	 * 
	 * @param i
	 *            the index of the linked object
	 * @return linked object at index i
	 */
	public static WSDataType dataTypesAt(int i)
	{
		return _dataTypes.get(i);
	}

	/**
	 * returns index of object x in "dataTypes"
	 * 
	 * @param x
	 *            the linked object
	 */
	public static int dataTypesIndexOf(WSDataType x)
	{
		if (!dataTypesIncludes(x))
			return -1;
		for (int i = 0; i < dataTypesSize(); i++)
		{
			if (dataTypesAt(i) == x)
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * move x one index before in the list for role "dataTypes"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void dataTypesMoveUp(WSDataType x)
	{
		int index;
		index = dataTypesIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveDataTypeAt(x, index - 1);
	}

	/**
	 * move x to the top of list for role "dataTypes"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void dataTypesMoveTop(WSDataType x)
	{
		int index;
		index = dataTypesIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveDataTypeAt(x, 0);
	}

	/**
	 * move x one index more in the list for role "dataTypes"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void dataTypesMoveDown(WSDataType x)
	{
		int index;
		index = dataTypesIndexOf(x);
		if (index < 0 || index > dataTypesSize() - 2)
			return;
		nocheckMoveDataTypeAt(x, index + 1);
	}

	/**
	 * move x to the bottom of list for role "dataTypes"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void dataTypesMoveBottom(WSDataType x)
	{
		int index;
		index = dataTypesIndexOf(x);
		if (index < 0 || index > dataTypesSize() - 2)
			return;
		nocheckMoveDataTypeAt(x, dataTypesSize() - 1);
	}

	/**
	 * move x one index more in the list for role "dataTypes"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void nocheckMoveDataTypeAt(WSDataType x, int a_Index)
	{
		int index;
		WSDataType tmp;
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
	 * @param x
	 *            the object to link with
	 * @see NemoLists#removeDataType(WSDataType)
	 */
	protected static void nocheckRemoveDataType(WSDataType x)
	{
		_dataTypes.remove(x);
	}

	/**
	 * adds x to the linked objects for role "dataTypes"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see NemoLists#addDataType(WSDataType)
	 */
	protected static void nocheckAddDataType(WSDataType x)
	{
		_dataTypes.add(x);
	}

	/**
	 * returns the number of linked objects for role "dataTypes"
	 * 
	 * @return the number of linked objects
	 */
	public static int dataTypesSize()
	{
		return _dataTypes.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "dataTypes"
	 * 
	 * @param x
	 *            an instance of WSDataType
	 */
	public static boolean dataTypesIncludes(WSDataType x)
	{
		return _dataTypes.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "dataTypes"
	 * 
	 */
	public static java.util.Iterator<WSDataType> dataTypesIterator()
	{
		return _dataTypes.iterator();

	}

	/**
	 * release all links for role "dataTypes"
	 */
	public static void releaseDataTypes()
	{
		for (WSDataType t : getDataTypes())
		{
			removeDataType(t);
		}
	}

	/**
	 * remove link with x for role "dataTypes"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public static void removeDataType(WSDataType x)
	{
		if (!dataTypesIncludes(x))
			return;
		nocheckRemoveDataType(x);
	}

	/**
	 * Add a link for role "dataTypes"
	 * 
	 * @param x
	 *            the object to link with
	 */
	public static void addDataType(WSDataType x)
	{
		if (dataTypesIncludes(x))
			return;
		nocheckAddDataType(x);
	}

	/**
	 * 
	 */
	public static void initDataTypes()
	{
		Nemo.getInstance().getLogger().info("init datatypes");
		// récupération de la liste des datatypes
		//        ICollection liste = VocabLists.getVocabList(Intern.list_ROSCOPparameters);
		//        ICollectionMapping sensors =  VocabLists.getVocabMap(Intern.list_ROSCOPparameters, Intern.list_instruments);
		ICollection liste;
		ICollectionMapping sensors;
		try {
			SdnVocabularyManager SdnVocabularyManager = ExternalResourcesManager.getInstance().getSdnVocabularyManager();
			liste = SdnVocabularyManager.getVocabList(Intern.list_ROSCOPparameters);
			sensors = SdnVocabularyManager.getMapping(Intern.list_ROSCOPparameters, Intern.list_instruments);

			for (IElement item : liste.getElements())
			{
				WSDataType dataType = new WSDataType();
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
							.getBestVocabMatchCode(SdnVocabularyManager.LIST_L05, SdnVocabularyManager.LIST_C77, item.getKey());
//					Nemo.getInstance().getLogger().info(item.getKey()+" match=" + match);
//					if match sensor=true:
					dataType.setSensor(true);
				} catch (Exception e) {
//					Nemo.getInstance().getLogger().error("Invalid datatype : "+item.getKey()+" was not found in C77-L05 mapping");
				} 
				
				_dataTypes.add(dataType);
			}
		} catch (VocabularyException e) {
			Nemo.getInstance().getLogger().error(e.getMessage());
		}
		// ordonnancement de la liste des datatypes - tri bulle
		int iteration = _dataTypes.size() - 1;
		boolean permut;
		do
		{
			permut = false;
			for (int i = 0; i < iteration; i++)
			{
				WSDataType item1 = _dataTypes.get(i);
				WSDataType item2 = _dataTypes.get(i + 1);
				if (item2.getCode().compareTo(item1.getCode()) <= 0)
				{
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
	public static void updateDataTypes()
	{
		// les listes sont-elles mises à jour ?
		UpdateStatus parameters =((UpdateStatusList) ManageLists.getUpdate(Intern.id_nemo)).getUpdateStatus(Intern.list_ROSCOPparameters);
		UpdateStatus instruments =((UpdateStatusList)  ManageLists.getUpdate(Intern.id_nemo)).getUpdateStatus(Intern.list_instruments);

		if ((parameters != null && parameters.getEtat().equals(EtatUpdate.UPDATED))
				|| (instruments != null && instruments.getEtat().equals(EtatUpdate.UPDATED)))
		{
			_dataTypes.clear();
			initDataTypes();
		}
	}
}
