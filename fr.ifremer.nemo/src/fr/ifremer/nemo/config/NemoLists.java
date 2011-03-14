package fr.ifremer.nemo.config;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.holders.BooleanHolder;

import org.apache.axis.client.Service;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Table;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.UpdateBean.EtatUpdate;
import fr.ifremer.nemo.ns_ws_edmo.Edmo_webservice;
import fr.ifremer.nemo.ns_ws_edmo.Edmo_webserviceLocator;
import fr.ifremer.nemo.ns_ws_edmo.Edmo_webserviceSoap;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.NemoWS;
import fr.ifremer.nemo.ws.NemoWSServiceLocator;
import fr.ifremer.nemo.ws.WSDataType;
import fr.ifremer.nemo.ws.WSParameter;
import fr.ifremer.nemo.ws.WSSurveyDescription;
import fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType;
import fr.ifremer.nemo.wsVocab.vocab.CodeTableTypeType;
import fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeArrayHolder;
import fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableTypeTypeArrayHolder;
import fr.ifremer.nemo.wsVocab.vocab_pkg.VocabSoapBindingStub;

/**
 * Class NemoLists
 * 
 * 
 * @author Gerard BUNEL
 */
public class NemoLists implements Cloneable
{

	static protected List<WSDataType> _dataTypes = new java.util.ArrayList<WSDataType>();

	static protected List<WSParameter> _parameters = new java.util.ArrayList<WSParameter>();

	static protected List<WSSurveyDescription> _surveys = new java.util.ArrayList<WSSurveyDescription>();

	static protected HashMap<Integer, String> _edmo = new java.util.HashMap<Integer, String>();

	/**
	 * list of updates
	 */
	static protected ArrayList<UpdateBean> updatedLists;

	static protected HashMap<String, CodeTableRecordType[]> _vocabList = new HashMap<String, CodeTableRecordType[]>();

	static protected HashMap<String, CodeTableRecordType[]> _vocabMap = new HashMap<String, CodeTableRecordType[]>();

	// static protected String _sdnParameters;

	public static Table filtreTable;

	public static ArrayList<String> listeNavire, listeCentreDonnees, listeRegion;

	public static ArrayList<String> listeFiltree;

	static
	{
		initLists();
	}

	public NemoLists()
	{
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		NemoLists copy = null;
		copy = (NemoLists) super.clone();
		return copy;
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
	 * release all links for role "parameters"
	 */
	public static void releaseParameters()
	{
		for (WSParameter t : getParameters())
		{
			removeParameter(t);
		}
	}

	/**
	 * remove link with x for role "parameters"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public static void removeParameter(WSParameter x)
	{
		if (!parametersIncludes(x))
			return;
		nocheckRemoveParameter(x);
	}

	/**
	 * Add a link for role "parameters"
	 * 
	 * @param x
	 *            the object to link with
	 */
	public static void addParameter(WSParameter x)
	{
		if (parametersIncludes(x))
			return;
		nocheckAddParameter(x);
	}

	/**
	 * Returns the collection of linked objects for role "parameters"
	 * 
	 * @return Array of linked objects
	 */
	public static WSParameter[] getParameters()
	{
		WSParameter[] v = new WSParameter[_parameters.size()];
		_parameters.toArray(v);
		return v;
	}

	/**
	 * Returns linked object for role "parameters" at index i
	 * 
	 * @param i
	 *            the index of the linked object
	 * @return linked object at index i
	 */
	public static WSParameter parametersAt(int i)
	{
		return _parameters.get(i);
	}

	/**
	 * returns index of object x in "parameters"
	 * 
	 * @param x
	 *            the linked object
	 */
	public static int parametersIndexOf(WSParameter x)
	{
		if (!parametersIncludes(x))
			return -1;
		for (int i = 0; i < parametersSize(); i++)
		{
			if (parametersAt(i) == x)
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * move x one index before in the list for role "parameters"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void parametersMoveUp(WSParameter x)
	{
		int index;
		index = parametersIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveParameterAt(x, index - 1);
	}

	/**
	 * move x to the top of list for role "parameters"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void parametersMoveTop(WSParameter x)
	{
		int index;
		index = parametersIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveParameterAt(x, 0);
	}

	/**
	 * move x one index more in the list for role "parameters"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void parametersMoveDown(WSParameter x)
	{
		int index;
		index = parametersIndexOf(x);
		if (index < 0 || index > parametersSize() - 2)
			return;
		nocheckMoveParameterAt(x, index + 1);
	}

	/**
	 * move x to the bottom of list for role "parameters"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void parametersMoveBottom(WSParameter x)
	{
		int index;
		index = parametersIndexOf(x);
		if (index < 0 || index > parametersSize() - 2)
			return;
		nocheckMoveParameterAt(x, parametersSize() - 1);
	}

	/**
	 * move x one index more in the list for role "parameters"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void nocheckMoveParameterAt(WSParameter x, int a_Index)
	{
		int index;
		WSParameter tmp;
		index = parametersIndexOf(x);
		tmp = _parameters.get(index);
		_parameters.remove(index);
		_parameters.add(a_Index, tmp);
	}

	/**
	 * remove x from the linked objects for role "parameters"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see NemoLists#removeParameter(WSParameter)
	 */
	protected static void nocheckRemoveParameter(WSParameter x)
	{
		_parameters.remove(x);
	}

	/**
	 * adds x to the linked objects for role "parameters"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see NemoLists#addParameter(WSParameter)
	 */
	protected static void nocheckAddParameter(WSParameter x)
	{
		_parameters.add(x);
	}

	/**
	 * returns the number of linked objects for role "parameters"
	 * 
	 * @return the number of linked objects
	 */
	public static int parametersSize()
	{
		return _parameters.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "parameters"
	 * 
	 * @param x
	 *            an instance of WSParameter
	 */
	public static boolean parametersIncludes(WSParameter x)
	{
		return _parameters.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "parameters"
	 * 
	 */
	public static java.util.Iterator<WSParameter> parametersIterator()
	{
		return _parameters.iterator();

	}

	/**
	 * release all links for role "surveys"
	 */
	public static void releaseSurveys()
	{
		for (WSSurveyDescription t : getSurveys())
		{
			removeSurvey(t);
		}
	}

	/**
	 * remove link with x for role "surveys"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public static void removeSurvey(WSSurveyDescription x)
	{
		if (!surveysIncludes(x))
			return;
		nocheckRemoveSurvey(x);
	}

	/**
	 * Add a link for role "surveys"
	 * 
	 * @param x
	 *            the object to link with
	 */
	public static void addSurvey(WSSurveyDescription x)
	{
		if (surveysIncludes(x))
			return;
		nocheckAddSurvey(x);
	}

	/**
	 * Returns the collection of linked objects for role "surveys"
	 * 
	 * @return Array of linked objects
	 */
	public static WSSurveyDescription[] getSurveys()
	{
		WSSurveyDescription[] v = new WSSurveyDescription[_surveys.size()];
		_surveys.toArray(v);
		return v;
	}

	/**
	 * Returns linked object for role "surveys" at index i
	 * 
	 * @param i
	 *            the index of the linked object
	 * @return linked object at index i
	 */
	public static WSSurveyDescription surveysAt(int i)
	{
		return _surveys.get(i);
	}

	/**
	 * returns index of object x in "surveys"
	 * 
	 * @param x
	 *            the linked object
	 */
	public static int surveysIndexOf(WSSurveyDescription x)
	{
		if (!surveysIncludes(x))
			return -1;
		for (int i = 0; i < surveysSize(); i++)
		{
			if (surveysAt(i) == x)
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * move x one index before in the list for role "surveys"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void surveysMoveUp(WSSurveyDescription x)
	{
		int index;
		index = surveysIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveSurveyAt(x, index - 1);
	}

	/**
	 * move x to the top of list for role "surveys"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void surveysMoveTop(WSSurveyDescription x)
	{
		int index;
		index = surveysIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveSurveyAt(x, 0);
	}

	/**
	 * move x one index more in the list for role "surveys"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void surveysMoveDown(WSSurveyDescription x)
	{
		int index;
		index = surveysIndexOf(x);
		if (index < 0 || index > surveysSize() - 2)
			return;
		nocheckMoveSurveyAt(x, index + 1);
	}

	/**
	 * move x to the bottom of list for role "surveys"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void surveysMoveBottom(WSSurveyDescription x)
	{
		int index;
		index = surveysIndexOf(x);
		if (index < 0 || index > surveysSize() - 2)
			return;
		nocheckMoveSurveyAt(x, surveysSize() - 1);
	}

	/**
	 * move x one index more in the list for role "surveys"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void nocheckMoveSurveyAt(WSSurveyDescription x, int a_Index)
	{
		int index;
		WSSurveyDescription tmp;
		index = surveysIndexOf(x);
		tmp = _surveys.get(index);
		_surveys.remove(index);
		_surveys.add(a_Index, tmp);
	}

	/**
	 * remove x from the linked objects for role "surveys"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see NemoLists#removeSurvey(WSSurveyDescription)
	 */
	protected static void nocheckRemoveSurvey(WSSurveyDescription x)
	{
		_surveys.remove(x);
	}

	/**
	 * adds x to the linked objects for role "surveys"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see NemoLists#addSurvey(WSSurveyDescription)
	 */
	protected static void nocheckAddSurvey(WSSurveyDescription x)
	{
		_surveys.add(x);
	}

	/**
	 * returns the number of linked objects for role "surveys"
	 * 
	 * @return the number of linked objects
	 */
	public static int surveysSize()
	{
		return _surveys.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "surveys"
	 * 
	 * @param x
	 *            an instance of WSSurveyDescription
	 */
	public static boolean surveysIncludes(WSSurveyDescription x)
	{
		return _surveys.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "surveys"
	 * 
	 */
	public static java.util.Iterator<WSSurveyDescription> surveysIterator()
	{
		return _surveys.iterator();

	}

	/**
	 * Getter sur _vocabList.
	 * 
	 * @return the vocabList
	 */
	public static HashMap<String, CodeTableRecordType[]> getVocabList()
	{
		return _vocabList;
	}

	public static CodeTableRecordType[] getVocabList(String listName)
	{
		return _vocabList.get(listName);
	}

	/**
	 * Getter sur _vocabMap.
	 * 
	 * @return the vocabMap
	 */
	public static HashMap<String, CodeTableRecordType[]> getVocabMap()
	{
		return _vocabMap;
	}

	public static CodeTableRecordType[] getVocabMap(String list1, String list2)
	{
		return _vocabMap.get(fr.ifremer.nemo.util.Utils.concat(list1, list2, Global.UNDERSCORE));
	}

	public static void setVocabMap(String list1, String list2,
			CodeTableRecordType[] map)
	{
		_vocabMap.put(fr.ifremer.nemo.util.Utils.concat(list1, list2, Global.UNDERSCORE), map);
	}

	public static String getVocabMatch(String list1, String list2,
			String value, String errorMessage)
	{
		String strReturn = errorMessage;

		try
		{
			CodeTableRecordType[] tableMap = NemoLists
					.getVocabMap(list1, list2);
			int intLength = tableMap.length;

			for (int i = 0; i < intLength; i++)
			{
				if (tableMap[i].getEntryTermAbbr().equals(value))
				{
					if (tableMap[i].getExactMatch() != null)
					{
						strReturn = tableMap[i].getExactMatch()[0]
								.getEntryKey();
					} else if (tableMap[i].getNarrowMatch() != null)
					{
						strReturn = tableMap[i].getNarrowMatch()[0]
								.getEntryKey();
					} else if (tableMap[i].getBroadMatch() != null)
					{
						strReturn = tableMap[i].getBroadMatch()[0]
								.getEntryKey();
					} else if (tableMap[i].getMajorMatch() != null)
					{
						strReturn = tableMap[i].getMajorMatch()[0]
								.getEntryKey();
					} else if (tableMap[i].getMinorMatch() != null)
					{
						strReturn = tableMap[i].getMinorMatch()[0]
								.getEntryKey();
					}
					break;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
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
	 *            valeur du entryTermAbbr
	 * @return liste des paramètres d'arrivée qui correspondent à la valeur
	 *         value dans le liste de départ
	 * 
	 *         ex: list1=P091 list2=P021, value=BATH renvoie les parametres P021
	 *         correspondant au parametre BATH en P091
	 */
	public static List<CodeTableRecordType> getVocabMatchs(String list1, String list2,
			String value)
	{

		List<CodeTableRecordType> listReturn = new ArrayList<CodeTableRecordType>();

		try
		{

			CodeTableRecordType[] tableMap = NemoLists
					.getVocabMap(list1, list2);
			int intLength = tableMap.length;

			for (int i = 0; i < intLength; i++)
			{
				if (tableMap[i].getEntryTermAbbr().equals(value))
				{

					CodeTableRecordType[] tmpTab = tableMap[i].getExactMatch();
					if (tmpTab != null)
						listReturn.addAll(Arrays.asList(tmpTab));

					tmpTab = tableMap[i].getNarrowMatch();
					if (tmpTab != null)
						listReturn.addAll(Arrays.asList(tmpTab));

					tmpTab = tableMap[i].getBroadMatch();
					if (tmpTab != null)
						listReturn.addAll(Arrays.asList(tmpTab));

					tmpTab = tableMap[i].getMajorMatch();
					if (tmpTab != null)
						listReturn.addAll(Arrays.asList(tmpTab));

					tmpTab = tableMap[i].getMinorMatch();
					if (tmpTab != null)
						listReturn.addAll(Arrays.asList(tmpTab));

					break;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return listReturn;
	}

	public static java.util.HashMap<Integer, String> getEdmo()
	{
		return _edmo;
	}

	/**
	 * Getter sur updatedLists.
	 * 
	 * @return the updatedLists
	 */
	public static ArrayList<UpdateBean> getUpdatedLists()
	{
		return updatedLists;
	}

	/**
	 * @param id
	 * @return the updatedList selected
	 */
	public static UpdateBean getUpdate(String id)
	{
		for (UpdateBean value : updatedLists)
		{
			if (value.getId().equals(id))
			{
				return value;
			}
		}
		return null;
	}

	/*
	 * Initialisation des listes
	 */

	/**
	 * 
	 */
	public static void initLists()
	{

		initParameters();
		initSurveys();
		initVocabLists();
		initVocabMaps();
		initDataTypes();
		initEdmo();
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static void initParameters()
	{
		XMLDecoder decoder = null;
		try
		{
			decoder = new XMLDecoder(new FileInputStream(Intern.dir_resources + File.separator + Intern.list_nemo_parameters));
			_parameters = (List<WSParameter>) decoder.readObject();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (decoder != null)
			{
				try
				{
					decoder.close();
				} catch (Exception ex)
				{
				}
			}
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static void initSurveys()
	{
		XMLDecoder decoder = null;
		try
		{
			decoder = new XMLDecoder(new FileInputStream(Intern.dir_resources + File.separator + Intern.list_nemo_survey));
			_surveys = (List<WSSurveyDescription>) decoder.readObject();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (decoder != null)
			{
				try
				{
					decoder.close();
				} catch (Exception ex)
				{
				}
			}
		}
	}

	/**
     * 
     */
	public static void initVocabLists()
	{
		XMLDecoder decoder = null;
		String[] listNames = Intern.vocabLists.split(Global.COMMA);
		for (String listName : listNames)
		{
			try
			{
				String listVocab = Intern.dir_resources + File.separator + Intern.dir_vocabLists + File.separator + Global.POURCENT_S + Global.EXT_XML;
				String fileName = String.format(listVocab, listName);
				File file = new File(fileName);
				if (file.exists())
				{
					decoder = new XMLDecoder(new FileInputStream(file));
					_vocabList.put(listName, (CodeTableRecordType[]) decoder
							.readObject());
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
				if (decoder != null)
				{
					try
					{
						decoder.close();
					} catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
	}

	/**
     * 
     */
	public static void initVocabMaps()
	{
		XMLDecoder decoder = null;
		String[] mapNames = Intern.vocabMaps.split(Global.COMMA);
		for (String mapName : mapNames)
		{
			try
			{
				String listVocab = Intern.dir_resources + File.separator + Intern.dir_vocabLists + File.separator + Global.POURCENT_S + Global.EXT_XML;
				String fileName = String.format(listVocab, mapName);
				File file = new File(fileName);
				if (file.exists())
				{
					decoder = new XMLDecoder(new FileInputStream(file));
					_vocabMap.put(mapName, (CodeTableRecordType[]) decoder.readObject());
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
				if (decoder != null)
				{
					try
					{
						decoder.close();
					} catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
	}

	/**
     * 
     */
	public static void initDataTypes()
	{
		// récupération de la liste des datatypes
		CodeTableRecordType[] liste = getVocabList(Intern.list_ROSCOPparameters);

		for (CodeTableRecordType item : liste)
		{
			WSDataType dataType = new WSDataType();
			dataType.setCode(fr.ifremer.nemo.util.Utils.extraitCodeURN(item.getEntryKey()));
			dataType.setLibelle(item.getEntryTerm());
			dataType.setSensor(false);

			// is sensor ??
			for (CodeTableRecordType sensor : getVocabMap(Intern.list_ROSCOPparameters, Intern.list_instruments))
			{
				if (item.getEntryKey().equals(sensor.getEntryKey()))
				{

					if ((sensor.getExactMatch() != null && sensor.getExactMatch().length > 0)
							||
							sensor.getNarrowMatch() != null && sensor.getNarrowMatch().length > 0
							||
							sensor.getMajorMatch() != null && sensor.getMajorMatch().length > 0
							||
							sensor.getMinorMatch() != null && sensor.getMinorMatch().length > 0
							||
							sensor.getBroadMatch() != null && sensor.getBroadMatch().length > 0)
					{
						dataType.setSensor(true);
					}
				}
			}
			_dataTypes.add(dataType);
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

	public static int initEdmo()
	{
		int edmoSize = 0;
		try
		{

			EdmoHandler edmoH = new EdmoHandler();

			SAXParserFactory.newInstance().newSAXParser().parse(
					new FileInputStream(Intern.dir_resources + File.separator + Intern.list_edmo), edmoH);

			_edmo.putAll(edmoH.get_valeurs());
			edmoSize = _edmo.size();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return edmoSize;
	}

	/*
	 * Mise à jour des listes
	 */
	private static void initUpdatedLists()
	{
		updatedLists = new ArrayList<UpdateBean>();
		updatedLists.add(new UpdateBean(Intern.id_nemo));
		updatedLists.add(new UpdateBean(Intern.id_lists));
		updatedLists.add(new UpdateBean(Intern.id_maps));
		updatedLists.add(new UpdateBean(Intern.id_edmo));
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
			for (UpdateBean value : updatedLists)
			{
				value.reset();
			}
		}
	}

	public static NemoWS callNemoWS()
	{

		NemoWS service = null;
		try
		{
			service = new NemoWSServiceLocator()
					.getNEMO(new java.net.URL(Nemo.getInstance().getConfig()
							.getNemoWebServiceURL()));
		} catch (ServiceException e)
		{
			e.printStackTrace();
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		return service;
	}

	/**
	 * 
	 */
	public static void updateParameters(IProgressMonitor monitor, NemoWS service, UpdateBean updateNemo)
	{

		String listName = Intern.list_nemo_parameters;
		int nbParameters = 0;
		UpdateBean updateParameters = new UpdateBean(listName, listName);
		updateParameters.setNbTot(_parameters.size());

		if (service != null)
		{
			// BackUp de l'objet
			List<WSParameter> bkParameters = new ArrayList<WSParameter>();
			for (WSParameter bkParam : _parameters)
			{
				bkParameters.add(bkParam);
			}

			FileOutputStream stream = null;
			XMLEncoder encoder = null;
			try
			{
				if (!monitor.isCanceled())
				{
					WSParameter[] parameters = service.getParameters();
					releaseParameters();
					for (int i = 0; i < parameters.length; i++)
					{
						addParameter(parameters[i]);
					}
					nbParameters = parameters.length;
				}

				if (!monitor.isCanceled())
				{
					File file = new java.io.File(Intern.dir_resources + File.separator + listName);
					stream = new FileOutputStream(file.toString());
					encoder = new XMLEncoder(stream);
					encoder.writeObject(_parameters);

					updateParameters.setEtat(EtatUpdate.UPDATED);
					updateParameters.setNbRetrieve(nbParameters);

				} else
				{
					updateParameters.setEtat(EtatUpdate.INTERRUPTED);
					if (nbParameters != 0)
					{
						// rollback
						releaseParameters();
						for (WSParameter bkParam : bkParameters)
						{
							addParameter(bkParam);
						}
						nbParameters = 0;
					}
				}
			} catch (Exception e)
			{
				updateParameters.setEtat(EtatUpdate.FAILED);
				e.printStackTrace();
			} finally
			{
				if (encoder != null)
				{
					try
					{
						encoder.close();
					} catch (Exception ex)
					{
					}
				}
				if (stream != null)
				{
					try
					{
						stream.close();
					} catch (Exception ex)
					{
					}
				}
			}
		} else
		{
			updateParameters.setEtat(EtatUpdate.FAILED);
		}

		updateNemo.addSubUpdate(updateParameters);
		if (updateNemo.getEtat().compareTo(updateParameters.getEtat()) < 0)
		{
			updateNemo.setEtat(updateParameters.getEtat());
		}
	}

	/**
	 * 
	 */
	public static void updateSurveys(IProgressMonitor monitor, NemoWS service, UpdateBean updateNemo)
	{

		String listName = Intern.list_nemo_survey;
		int nbSurveys = 0;
		UpdateBean updateSurveys = new UpdateBean(listName, listName);
		updateSurveys.setNbTot(_surveys.size());

		if (service != null)
		{

			// BackUp de l'objet
			List<WSSurveyDescription> bkSurveys = new ArrayList<WSSurveyDescription>();
			for (WSSurveyDescription bkSurvey : _surveys)
			{
				bkSurveys.add(bkSurvey);
			}

			FileOutputStream stream = null;
			XMLEncoder encoder = null;
			try
			{
				if (!monitor.isCanceled())
				{
					WSSurveyDescription[] surveys = service.getSurveyList();
					releaseSurveys();
					for (int i = 0; i < surveys.length; i++)
					{
						addSurvey(surveys[i]);
					}
					nbSurveys = surveys.length;
				}

				if (!monitor.isCanceled())
				{
					File file = new java.io.File(Intern.dir_resources + File.separator + listName);
					stream = new FileOutputStream(file.toString());

					encoder = new XMLEncoder(stream);
					encoder.writeObject(_surveys);

					updateSurveys.setEtat(EtatUpdate.UPDATED);
					updateSurveys.setNbRetrieve(nbSurveys);
				} else
				{
					updateSurveys.setEtat(EtatUpdate.INTERRUPTED);
					if (nbSurveys != 0)
					{
						// rollback
						releaseSurveys();
						for (WSSurveyDescription bkSurvey : bkSurveys)
						{
							addSurvey(bkSurvey);
						}
						nbSurveys = 0;
					}
				}

			} catch (Exception e)
			{
				updateSurveys.setEtat(EtatUpdate.FAILED);
				e.printStackTrace();
			} finally
			{
				if (encoder != null)
				{
					try
					{
						encoder.close();
					} catch (Exception ex)
					{
					}
				}
				if (stream != null)
				{
					try
					{
						stream.close();
					} catch (Exception ex)
					{
					}
				}
			}
		} else
		{
			updateSurveys.setEtat(EtatUpdate.FAILED);
		}

		updateNemo.addSubUpdate(updateSurveys);
		if (updateNemo.getEtat().compareTo(updateSurveys.getEtat()) < 0)
		{
			updateNemo.setEtat(updateSurveys.getEtat());
		}
	}

	public static VocabSoapBindingStub connectVocab()
	{

		VocabSoapBindingStub vocab = null;
		try
		{

			vocab = new VocabSoapBindingStub(
						new java.net.URL(Nemo.getInstance().getConfig().getVocabURL()),
						new Service());

			// récupération des versions serveur des listes
			CodeTableTypeTypeArrayHolder table = new CodeTableTypeTypeArrayHolder();
			javax.xml.rpc.holders.BooleanHolder error = new BooleanHolder();
			vocab.whatLists(Global.EMPTY_STRING, table, error);
			if (!error.value && table.value != null)
			{
				for (CodeTableTypeType item : table.value)
				{
					String[] itemValue = item.getListKey().split(Global.SLASH);
					String listId = itemValue[itemValue.length - 2];
					if (Nemo.getInstance().getConfig().getVocabLists().containsKey(listId))
					{
						BODCList list = Nemo.getInstance().getConfig().getVocabLists().get(listId);
						list.setServerVersion(String.valueOf(item.getListVersion()));
						list.setName(item.getListShortName());
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			vocab = null;
		}
		return vocab;
	}

	/**
     * 
     */
	public static void updateVocabLists(VocabSoapBindingStub vocabService, IProgressMonitor monitor)
	{

		UpdateBean updateLists = getUpdate(Intern.id_lists);

		if (vocabService != null)
		{
			int nbRetrieve = 0;
			int idxCurrent = 0;

			try
			{
				String[] listNames = Intern.vocabLists.split(Global.COMMA);
				int keySetSize = listNames.length;
				updateLists.setNbTot(keySetSize);

				for (String listName : listNames)
				{
					idxCurrent++;
					if (!monitor.isCanceled())
					{

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

						UpdateBean updateList = updateVocabList(vocabService, listName);
						updateLists.addSubUpdate(updateList);
						if (updateList.getEtat().compareTo(EtatUpdate.UPDATED) == 0
								|| updateList.getEtat().compareTo(EtatUpdate.CHECKED) == 0)
						{
							nbRetrieve++;
						}
						if (updateLists.getEtat().compareTo(updateList.getEtat()) < 0)
						{
							updateLists.setEtat(updateList.getEtat());
						}
					} else
					{
						updateLists.setEtat(EtatUpdate.INTERRUPTED);
					}
				}

				updateLists.setNbRetrieve(nbRetrieve);

			} catch (Exception e)
			{
				updateLists.setEtat(EtatUpdate.FAILED);
				e.printStackTrace();
			}
		} else
		{
			updateLists.setEtat(EtatUpdate.FAILED);
		}
	}

	/**
	 * 
	 */
	private static UpdateBean updateVocabList(VocabSoapBindingStub vocab, String listId)
	{
		BODCList list = Nemo.getInstance().getConfig().getVocabList(listId);

		StringBuffer msg = new StringBuffer();
		msg.append(listId);
		msg.append(Global.SEP);
		msg.append(list.getName());

		UpdateBean updateList = new UpdateBean(listId, msg.toString());
		updateList.setNbTot(_vocabList.get(listId).length);

		if (list.getLocalVersion().compareTo(list.getServerVersion()) < 0)
		{
			FileOutputStream stream = null;
			XMLEncoder encoder = null;
			CodeTableRecordTypeArrayHolder table = new CodeTableRecordTypeArrayHolder();
			javax.xml.rpc.holders.BooleanHolder error = new BooleanHolder();
			try
			{
				vocab.getList(new String[]
				{ list.getUrl() }, null, table, error);
				if (!error.value && table.value != null)
				{
					_vocabList.put(list.getId(), table.value);

					File dir = new java.io.File(Intern.dir_resources + File.separator + Intern.dir_vocabLists);
					if (!dir.exists())
					{
						dir.mkdirs();
					}
					File file = new java.io.File(dir, list.getId() + Global.EXT_XML);
					stream = new FileOutputStream(file.toString());

					encoder = new XMLEncoder(stream);
					encoder.writeObject(table.value);

					list.setLocalVersion(list.getServerVersion());
					Nemo.getInstance().getConfig().getVocabLists().put(list.getId(), list);

					updateList.setEtat(EtatUpdate.UPDATED);
					updateList.setNbRetrieve(_vocabList.get(listId).length);
				} else
				{
					updateList.setEtat(EtatUpdate.FAILED);
				}
			} catch (Exception e)
			{
				updateList.setEtat(EtatUpdate.FAILED);
				e.printStackTrace();
			} finally
			{
				if (encoder != null)
				{
					try
					{
						encoder.close();
					} catch (Exception ex)
					{
					}
				}
				if (stream != null)
				{
					try
					{
						stream.close();
					} catch (Exception ex)
					{
					}
				}
			}
		} else
		{
			updateList.setEtat(EtatUpdate.CHECKED);
		}
		return updateList;
	}

	public static void updateVocabMaps(VocabSoapBindingStub vocabService, IProgressMonitor monitor)
	{
		UpdateBean updateMaps = getUpdate(Intern.id_maps);

		if (vocabService != null)
		{
			int nbRetrieve = 0;
			int idxCurrent = 0;

			try
			{
				String[] listNames = Intern.vocabMaps.split(Global.COMMA);
				int keySetSize = listNames.length;
				updateMaps.setNbTot(keySetSize);

				for (String listName : listNames)
				{
					idxCurrent++;
					if (!monitor.isCanceled())
					{
						StringBuffer msg = new StringBuffer();
						msg.append(Messages.getString(Intern.id_maps));
						msg.append(Global.SPACE);
						msg.append(listName);
						msg.append(" [");//$NON-NLS-1$
						msg.append((idxCurrent));
						msg.append(" / ");//$NON-NLS-1$
						msg.append(keySetSize);
						msg.append("]");//$NON-NLS-1$
						monitor.subTask(msg.toString());

						UpdateBean updateMap = updateVocabMap(vocabService, listName);
						updateMaps.addSubUpdate(updateMap);
						if (updateMap.getEtat().compareTo(EtatUpdate.UPDATED) == 0
								|| updateMap.getEtat().compareTo(EtatUpdate.CHECKED) == 0)
						{
							nbRetrieve++;
						}
						if (updateMaps.getEtat().compareTo(updateMap.getEtat()) < 0)
						{
							updateMaps.setEtat(updateMap.getEtat());
						}
					} else
					{
						updateMaps.setEtat(EtatUpdate.INTERRUPTED);
					}
				}
				updateMaps.setNbRetrieve(nbRetrieve);
			} catch (Exception e)
			{
				updateMaps.setEtat(EtatUpdate.FAILED);
				e.printStackTrace();
			}
		} else
		{
			updateMaps.setEtat(EtatUpdate.FAILED);
		}
	}

	/**
	 * 
	 */
	private static UpdateBean updateVocabMap(VocabSoapBindingStub vocab, String listName)
	{

		String[] listIds = listName.split(Global.UNDERSCORE);

		BODCList list1 = Nemo.getInstance().getConfig().getVocabList(listIds[0]);
		BODCList list2 = Nemo.getInstance().getConfig().getVocabList(listIds[1]);

		UpdateBean updateMap = new UpdateBean(listName, listName);

		if (_vocabMap.get(listName) != null)
			updateMap.setNbTot(_vocabMap.get(listName).length);
		else
			// Cas ou le fichier xml n'est pas présent sur le disque
			updateMap.setNbTot(0);

		// check if update is needed
		// !! cette façon de faire nécessite le chargement et la mise à jour de
		// chacune des listes en individuel :
		// !! si les listes individuelles sont supprimées, cette vérification ne
		// fonctionnera plus
		UpdateBean updateList1 = getUpdate(Intern.id_lists).getSubUpdate(list1.getId());
		UpdateBean updateList2 = getUpdate(Intern.id_lists).getSubUpdate(list2.getId());

		if (updateList1.getEtat() != EtatUpdate.CHECKED
				|| updateList2.getEtat() != EtatUpdate.CHECKED)
		{

			FileOutputStream stream = null;
			XMLEncoder encoder = null;
			CodeTableRecordTypeArrayHolder table = new CodeTableRecordTypeArrayHolder();
			javax.xml.rpc.holders.BooleanHolder error = new BooleanHolder();
			try
			{
				vocab.getMap(new String[]
				{ list1.getUrl() },
								Nemo.getInstance().getConfig().getPredicate(),
								new String[]
								{ list2.getUrl() }, "false", table, error); //$NON-NLS-1$

				if (!error.value)
				{
					if (table.value != null)
					{
						_vocabMap.put(listName, table.value);

						File dir = new java.io.File(Intern.dir_resources + File.separator + Intern.dir_vocabLists);
						if (!dir.exists())
						{
							dir.mkdirs();
						}
						File file = new java.io.File(dir, listName + Global.EXT_XML);
						stream = new FileOutputStream(file.toString());

						encoder = new XMLEncoder(stream);
						encoder.writeObject(table.value);

						updateMap.setEtat(EtatUpdate.UPDATED);
						updateMap.setNbRetrieve(_vocabMap.get(listName).length);
					}
				} else
				{
					updateMap.setEtat(EtatUpdate.FAILED);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
				updateMap.setEtat(EtatUpdate.FAILED);
			} finally
			{
				if (encoder != null)
				{
					try
					{
						encoder.close();
					} catch (Exception ex)
					{
					}
				}
				if (stream != null)
				{
					try
					{
						stream.close();
					} catch (Exception ex)
					{
					}
				}
			}
		} else
		{
			updateMap.setEtat(EtatUpdate.CHECKED);
		}
		return updateMap;
	}

	/**
     * 
     */
	public static void updateDataTypes()
	{
		// les listes sont-elles mises à jour ?
		UpdateBean parameters = getUpdate(Intern.id_nemo).getSubUpdate(Intern.list_ROSCOPparameters);
		UpdateBean instruments = getUpdate(Intern.id_nemo).getSubUpdate(Intern.list_instruments);

		if ((parameters != null && parameters.getEtat().equals(EtatUpdate.UPDATED))
				|| (instruments != null && instruments.getEtat().equals(EtatUpdate.UPDATED)))
		{
			_dataTypes.clear();
			initDataTypes();
		}
	}

	public static void updateEdmo(IProgressMonitor monitor)
	{

		UpdateBean updateEdmo = getUpdate(Intern.id_edmo);
		updateEdmo.setNbTot(_edmo.size());
		int nbEdmo = 0;
		FileOutputStream stream = null;
		String result = Global.EMPTY_STRING;

		try
		{
			if (!monitor.isCanceled())
			{
				// Appel au WebService
				Edmo_webservice service = new Edmo_webserviceLocator();
				((Edmo_webserviceLocator) service).setedmo_webserviceSoap12EndpointAddress(Nemo.getInstance().getConfig().getEdmoURL());
				Edmo_webserviceSoap port = service.getedmo_webserviceSoap12();
				result = port.ws_edmo_get_list();
			}
			if (!monitor.isCanceled())
			{
				if (!result.equals(Global.EMPTY_STRING))
				{
					monitor.subTask(Messages.getString(Intern.id_edmo));

					// écriture du nouveau fichier
					File file = new java.io.File(Intern.dir_resources + File.separator + Intern.list_edmo);

					stream = new FileOutputStream(file.toString());
					OutputStreamWriter osw = new OutputStreamWriter(stream, "UTF-8"); //$NON-NLS-1$
					osw.write(result);
					osw.flush();
					osw.close();

					// stockage des valeurs dans l'objet
					nbEdmo = initEdmo();
					updateEdmo.setEtat(EtatUpdate.UPDATED);
					updateEdmo.setNbRetrieve(nbEdmo);
				} else
				{
					updateEdmo.setEtat(EtatUpdate.FAILED);
				}
			} else
			{
				updateEdmo.setEtat(EtatUpdate.INTERRUPTED);
			}

		} catch (Exception exG)
		{
			updateEdmo.setEtat(EtatUpdate.FAILED);
			exG.printStackTrace();
		}
	}

	/*
	 * mapping Seadatanet
	 */
	/*
	 * public static void writeSdnParameters(Conversion convert)
	 * {
	 * setSdnParameters(convert.getDebut());
	 * 
	 * setSdnParameters(convert.getDebut() + Intern.file_mapping);
	 * 
	 * for (fr.ifremer.nemo.input.ParameterModel param :
	 * convert.getAddedParameterModels()) {
	 * _sdnParameters += Global.RN + convert.getDebut() +
	 * parameterMap(param.getParameter());
	 * }
	 * 
	 * // FAE 0001131 : remplacer "\n" par "\r\n" pour retour chariot sous
	 * bloc-note
	 * DataModel data =
	 * Nemo.getInstance().getDomain().getCruise().getStation().getData
	 * ().getModel();
	 * for (fr.ifremer.nemo.input.ParameterModel param :
	 * data.getParameterModels()) {
	 * _sdnParameters += Global.RN + convert.getDebut() +
	 * parameterMap(param.getParameter());
	 * }
	 * }
	 * 
	 * private static String parameterMap(Parameter param) {
	 * String code = param.getCode();
	 * StringBuffer sdnMapping = new StringBuffer();
	 * try{
	 * sdnMapping.append(Intern.getString(code));
	 * }
	 * catch (MissingResourceException e){
	 * String subjectURN = getURNSyntaxe(URNtype.subject, code, null, null);
	 * String objectURN = getURNSyntaxe(URNtype.object, code,
	 * Intern.mapping_subject, Intern.mapping_objet);
	 * String unitURN = getURNSyntaxe(URNtype.units, code,
	 * Intern.mapping_subject, Intern.mapping_unit);
	 * 
	 * sdnMapping.append(subjectURN);
	 * sdnMapping.append(objectURN);
	 * sdnMapping.append(unitURN);
	 * }
	 * return sdnMapping.toString();
	 * }
	 * 
	 * private static String getURNSyntaxe(URNtype type, String code, String
	 * list1, String list2)
	 * {
	 * StringBuffer urnSyntaxe = new StringBuffer();
	 * 
	 * urnSyntaxe.append(Intern.xml_tag_start);
	 * urnSyntaxe.append(type.toString());
	 * urnSyntaxe.append(Intern.xml_tag_end);
	 * 
	 * urnSyntaxe.append(Intern.file_mapping_sdn);
	 * urnSyntaxe.append(Global.DBL_PT);
	 * 
	 * switch (type){
	 * case subject :
	 * urnSyntaxe.append(Intern.file_mapping_subject);
	 * urnSyntaxe.append(Global.DBL_PT);
	 * urnSyntaxe.append(code);
	 * break;
	 * case object:
	 * case units:
	 * urnSyntaxe.append(list2);
	 * urnSyntaxe.append(Global.DBL_PT);
	 * urnSyntaxe.append(Global.DBL_PT);
	 * String pdvCode = Utils.extraitCodeURN(getVocabMatch(list1, list2, code,
	 * Intern.NemoLists_unknown));
	 * urnSyntaxe.append(pdvCode);
	 * break;
	 * }
	 * 
	 * urnSyntaxe.append(Intern.xml_tag_start);
	 * urnSyntaxe.append(Intern.xml_tag_close);
	 * urnSyntaxe.append(type.toString());
	 * urnSyntaxe.append(Intern.xml_tag_end);
	 * 
	 * return urnSyntaxe.toString();
	 * }
	 * 
	 * public static String getSdnParameters() {
	 * return _sdnParameters;
	 * }
	 * 
	 * public static void setSdnParameters(String parameters) {
	 * _sdnParameters = parameters;
	 * }
	 */
} /* End of class: NemoLists */
