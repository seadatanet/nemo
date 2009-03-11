package fr.ifremer.nemo.config;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.holders.BooleanHolder;

import org.apache.axis.client.Service;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Table;

import fr.ifremer.nemo.ns_ws_edmo.Edmo_webservice;
import fr.ifremer.nemo.ns_ws_edmo.Edmo_webserviceLocator;
import fr.ifremer.nemo.ns_ws_edmo.Edmo_webserviceSoap;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.NemoWS;
import fr.ifremer.nemo.ws.NemoWSServiceLocator;
import fr.ifremer.nemo.ws.WSDataType;
import fr.ifremer.nemo.ws.WSParameter;
import fr.ifremer.nemo.ws.WSSurveyDescription;
import fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType;
import fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeArrayHolder;
import fr.ifremer.nemo.wsVocab.vocab_pkg.VocabSoapBindingStub;

/**
 * Class NemoLists
 * 
 * 
 * @author Gerard BUNEL
 */
public class NemoLists implements Cloneable {
	static protected java.util.List<WSDataType> _dataTypes = new java.util.ArrayList<WSDataType>();

	static protected java.util.List<WSParameter> _parameters = new java.util.ArrayList<WSParameter>();

	static protected java.util.List<WSSurveyDescription> _surveys = new java.util.ArrayList<WSSurveyDescription>();

	static protected java.util.HashMap<Integer, String> _edmo = new java.util.HashMap<Integer, String>();

	public static Boolean okSurveys = false;
    public static Boolean okParameters = false;
    public static Boolean okDataTypes = false;
    public static Boolean okVocabLists = false;
    public static Boolean okVocabMaps = false;
    public static Boolean okEdmo = false;

	public static int nbSurveys = 0;
    public static int nbParameters = 0;
    public static int nbDataTypes = 0;
    public static int nbVocabLists = 0;
    public static int nbVocabMaps = 0;
    public static int nbEdmo = 0;

	static protected HashMap<String, CodeTableRecordType[]> _vocabList = new HashMap<String, CodeTableRecordType[]>();

	static protected HashMap<String, Integer> _vocabListStatus = new HashMap<String, Integer>();

	static protected HashMap<String, CodeTableRecordType[]> _vocabMap = new HashMap<String, CodeTableRecordType[]>();

	static protected String _sdnParameters;

	public static Table filtreTable;

	public static ArrayList<String> listeNavire, listeCentreDonnees, listeRegion;

	public static ArrayList<String> listeFiltree;

	static {
		initLists();
	}

	public NemoLists() {
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	@Override
    public Object clone() throws CloneNotSupportedException {
		NemoLists copy = null;
		copy = (NemoLists) super.clone();
		return copy;
	}

	/**
	 * release all links for role "dataTypes"
	 */
	public static void releaseDataTypes() {
		for (WSDataType t : getDataTypes()) {
			removeDataType(t);
		}
	}

	/**
	 * remove link with x for role "dataTypes"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public static void removeDataType(WSDataType x) {
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
	public static void addDataType(WSDataType x) {
		if (dataTypesIncludes(x))
			return;
		nocheckAddDataType(x);
	}

	/**
	 * Returns the collection of linked objects for role "dataTypes"
	 * 
	 * @return Array of linked objects
	 */
	public static WSDataType[] getDataTypes() {
		WSDataType[] v = new WSDataType[_dataTypes.size()];
		_dataTypes.toArray(v);
		return v;
	}
    
    /**
     * Returns the collection of linked objects for role "dataTypes"
     * 
     * @return Array of linked objects
     */
    public static WSDataType[] getDataTypesIsSensor() {
        java.util.List<WSDataType> dataTypesIsSensor = new java.util.ArrayList<WSDataType>();
        
        for (WSDataType object : _dataTypes)
        {
            if(object.isSensor())
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
	public static WSDataType dataTypesAt(int i) {
		return  _dataTypes.get(i);
	}

	/**
	 * returns index of object x in "dataTypes"
	 * 
	 * @param x
	 *            the linked object
	 */
	public static int dataTypesIndexOf(WSDataType x) {
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
	 * @param x
	 *            the object to move
	 */
	public static void dataTypesMoveUp(WSDataType x) {
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
	public static void dataTypesMoveTop(WSDataType x) {
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
	public static void dataTypesMoveDown(WSDataType x) {
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
	public static void dataTypesMoveBottom(WSDataType x) {
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
	public static void nocheckMoveDataTypeAt(WSDataType x, int a_Index) {
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
	protected static void nocheckRemoveDataType(WSDataType x) {
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
	protected static void nocheckAddDataType(WSDataType x) {
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
	 * @param x
	 *            an instance of WSDataType
	 */
	public static boolean dataTypesIncludes(WSDataType x) {
		return _dataTypes.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "dataTypes"
	 * 
	 */
	public static java.util.Iterator<WSDataType> dataTypesIterator() {
		return _dataTypes.iterator();

	}

	/**
	 * release all links for role "parameters"
	 */
	public static void releaseParameters() {
		for (WSParameter t : getParameters()) {
			removeParameter(t);
		}
	}

	/**
	 * remove link with x for role "parameters"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public static void removeParameter(WSParameter x) {
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
	public static void addParameter(WSParameter x) {
		if (parametersIncludes(x))
			return;
		nocheckAddParameter(x);
	}

	/**
	 * Returns the collection of linked objects for role "parameters"
	 * 
	 * @return Array of linked objects
	 */
	public static WSParameter[] getParameters() {
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
	public static WSParameter parametersAt(int i) {
		return  _parameters.get(i);
	}

	/**
	 * returns index of object x in "parameters"
	 * 
	 * @param x
	 *            the linked object
	 */
	public static int parametersIndexOf(WSParameter x) {
		if (!parametersIncludes(x))
			return -1;
		for (int i = 0; i < parametersSize(); i++) {
			if (parametersAt(i) == x) {
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
	public static void parametersMoveUp(WSParameter x) {
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
	public static void parametersMoveTop(WSParameter x) {
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
	public static void parametersMoveDown(WSParameter x) {
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
	public static void parametersMoveBottom(WSParameter x) {
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
	public static void nocheckMoveParameterAt(WSParameter x, int a_Index) {
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
	protected static void nocheckRemoveParameter(WSParameter x) {
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
	protected static void nocheckAddParameter(WSParameter x) {
		_parameters.add(x);
	}

	/**
	 * returns the number of linked objects for role "parameters"
	 * 
	 * @return the number of linked objects
	 */
	public static int parametersSize() {
		return _parameters.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "parameters"
	 * 
	 * @param x
	 *            an instance of WSParameter
	 */
	public static boolean parametersIncludes(WSParameter x) {
		return _parameters.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "parameters"
	 * 
	 */
	public static java.util.Iterator<WSParameter> parametersIterator() {
		return _parameters.iterator();

	}

	/**
	 * release all links for role "surveys"
	 */
	public static void releaseSurveys() {
		for (WSSurveyDescription t : getSurveys()) {
			removeSurvey(t);
		}
	}

	/**
	 * remove link with x for role "surveys"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public static void removeSurvey(WSSurveyDescription x) {
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
	public static void addSurvey(WSSurveyDescription x) {
		if (surveysIncludes(x))
			return;
		nocheckAddSurvey(x);
	}

	/**
	 * Returns the collection of linked objects for role "surveys"
	 * 
	 * @return Array of linked objects
	 */
	public static WSSurveyDescription[] getSurveys() {
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
	public static WSSurveyDescription surveysAt(int i) {
		return  _surveys.get(i);
	}

	/**
	 * returns index of object x in "surveys"
	 * 
	 * @param x
	 *            the linked object
	 */
	public static int surveysIndexOf(WSSurveyDescription x) {
		if (!surveysIncludes(x))
			return -1;
		for (int i = 0; i < surveysSize(); i++) {
			if (surveysAt(i) == x) {
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
	public static void surveysMoveUp(WSSurveyDescription x) {
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
	public static void surveysMoveTop(WSSurveyDescription x) {
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
	public static void surveysMoveDown(WSSurveyDescription x) {
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
	public static void surveysMoveBottom(WSSurveyDescription x) {
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
	public static void nocheckMoveSurveyAt(WSSurveyDescription x, int a_Index) {
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
	protected static void nocheckRemoveSurvey(WSSurveyDescription x) {
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
	protected static void nocheckAddSurvey(WSSurveyDescription x) {
		_surveys.add(x);
	}

	/**
	 * returns the number of linked objects for role "surveys"
	 * 
	 * @return the number of linked objects
	 */
	public static int surveysSize() {
		return _surveys.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "surveys"
	 * 
	 * @param x
	 *            an instance of WSSurveyDescription
	 */
	public static boolean surveysIncludes(WSSurveyDescription x) {
		return _surveys.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "surveys"
	 * 
	 */
	public static java.util.Iterator<WSSurveyDescription> surveysIterator() {
		return _surveys.iterator();

	}

	/**
	 * 
	 */
	public static void initDataTypes() {
		XMLDecoder decoder = null;
		try {
			decoder = new XMLDecoder(new FileInputStream(
					"resources/DataTypes.xml")); //$NON-NLS-1$
			_dataTypes = (java.util.List<WSDataType>) decoder.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (decoder != null) {
				try {
					decoder.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	/**
	 * 
	 */
	public static void initLists() {
		initDataTypes();
		initParameters();
		initSurveys();
		initVocabLists();
		initVocabMap("P091", "P011"); //$NON-NLS-1$ //$NON-NLS-2$
		initVocabMap("P091", "P061"); //$NON-NLS-1$ //$NON-NLS-2$
        initVocabMap("P091", "P021"); //$NON-NLS-1$ //$NON-NLS-2$
//		initVocabMap("L05", "C77");
		initEdmo();
	}

	/**
	 * 
	 */
	public static void initParameters() {
		XMLDecoder decoder = null;
		try {
			decoder = new XMLDecoder(new FileInputStream(
					"resources/Parameters.xml")); //$NON-NLS-1$
			_parameters = (java.util.List<WSParameter>) decoder.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (decoder != null) {
				try {
					decoder.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	/**
	 * 
	 */
	public static void initSurveys() {
		XMLDecoder decoder = null;
		try {
			decoder = new XMLDecoder(new FileInputStream(
					"resources/Surveys.xml")); //$NON-NLS-1$
			_surveys = (java.util.List<WSSurveyDescription>) decoder.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (decoder != null) {
				try {
					decoder.close();
				} catch (Exception ex) {
				}
			}
		}
	}

//	/**
//	 * 
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		updateDataTypes();
//		updateParameters();
//		updateSurveys();
//		updateVocabLists(null);
//		updateEdmo();
//	}
    
    public static NemoWS callNemoWS(){
        NemoWS service = null;
        try 
        {
            service = new NemoWSServiceLocator()
                .getNEMO(new java.net.URL(Utils.configuration
                .getNemoWebServiceURL()));
        } 
        catch (ServiceException e) 
        {
            e.printStackTrace();
        }  
        catch (MalformedURLException e) 
        {
            e.printStackTrace();
        } 
        return service;
    }

	/**
	 * 
	 */
	public static void updateDataTypes(IProgressMonitor monitor, NemoWS service) {

        nbDataTypes = 0;
        okDataTypes = false;
        
        if (service != null){
            
            // BackUp de l'objet
            List<WSDataType> bkDataTypes = new ArrayList<WSDataType>();
            for(WSDataType bkType : _dataTypes){
                bkDataTypes.add(bkType);     
            }
            
            FileOutputStream stream = null;
            XMLEncoder encoder = null;
            try 
            {
                if (!monitor.isCanceled()){
                    WSDataType[] types = service.getDataTypes();
                    releaseDataTypes();
                    for (WSDataType type : service.getDataTypes()){
                        addDataType(type);
                    }
                    nbDataTypes = types.length;
                }
                
                if (!monitor.isCanceled()){
                    File file = new java.io.File("resources/DataTypes.xml"); //$NON-NLS-1$
                    stream = new FileOutputStream(file.toString());
                    
                    encoder = new XMLEncoder(stream);
                    encoder.writeObject(_dataTypes);

                    okDataTypes = true;
                    
                }else{
                    if (nbDataTypes != 0){
                        // rollback sur la liste des Data Types
                        releaseDataTypes();
                        for(WSDataType type : bkDataTypes){
                            addDataType(type);
                        }
                        nbDataTypes = 0;
                    }
                }
            } catch (Exception e) {
                okDataTypes = false;
                e.printStackTrace();
            } finally {
                if (encoder != null) {
                    try {
                        encoder.close();
                    } catch (Exception ex) {
                    }
                }
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception ex) {
                    }
                }
            }
        }
	}

	/**
	 * 
	 */
	public static void updateParameters(IProgressMonitor monitor, NemoWS service) {
        
		okParameters = false;
        nbParameters = 0;

        
        if (service != null)
        {
            // BackUp de l'objet
            List<WSParameter> bkParameters = new ArrayList<WSParameter>();
            for(WSParameter bkParam : _parameters){
                bkParameters.add(bkParam);     
            }
            
            FileOutputStream stream = null;
            XMLEncoder encoder = null;
            try {
                if (!monitor.isCanceled())
                {
                    WSParameter[] parameters = service.getParameters();
                    releaseParameters();
                    for (int i = 0; i < parameters.length; i++) {
                        addParameter(parameters[i]);
                    }
                    nbParameters = parameters.length;
                }

                if (!monitor.isCanceled())
                {
                    File file = new java.io.File("resources/Parameters.xml"); //$NON-NLS-1$
                    stream = new FileOutputStream(file.toString());
                    encoder = new XMLEncoder(stream);
                    encoder.writeObject(_parameters);
                    okParameters = true;
                }else{
                    if (nbParameters != 0){
                        // rollback
                        releaseParameters();
                        for(WSParameter bkParam : bkParameters){
                            addParameter(bkParam);   
                        }
                        nbParameters = 0;
                    }
                }
            } catch (Exception e) {
                okParameters = false;
                e.printStackTrace();
            } finally {
                if (encoder != null) {
                    try {
                        encoder.close();
                    } catch (Exception ex) {
                    }
                }
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception ex) {
                    }
                }
            }
        }
	}

	/**
	 * 
	 */
	public static void updateSurveys(IProgressMonitor monitor, NemoWS service) {
		okSurveys = false;
        nbSurveys = 0;
        
        if (service != null){
            
            // BackUp de l'objet
            List<WSSurveyDescription> bkSurveys = new ArrayList<WSSurveyDescription>();
            for(WSSurveyDescription bkSurvey : _surveys){
                bkSurveys.add(bkSurvey);     
            }
        
    		FileOutputStream stream = null;
    		XMLEncoder encoder = null;
    		try {
                if (!monitor.isCanceled())
                {
        			WSSurveyDescription[] surveys = service.getSurveyList();
        			releaseSurveys();
        			for (int i = 0; i < surveys.length; i++) {
        				addSurvey(surveys[i]);
        			}
                    nbSurveys = surveys.length;
                }

                if (!monitor.isCanceled())
                {
        			File file = new java.io.File("resources/Surveys.xml"); //$NON-NLS-1$
        			stream = new FileOutputStream(file.toString());
                    
        			encoder = new XMLEncoder(stream);
        			encoder.writeObject(_surveys);
                    
        			okSurveys = true;
                }
                else
                {
                    if (nbSurveys != 0){
                        // rollback 
                        releaseSurveys();
                        for(WSSurveyDescription bkSurvey : bkSurveys){
                            addSurvey(bkSurvey); 
                        }
                        nbSurveys = 0;
                    }
                }
    		} catch (Exception e) {
    			okSurveys = false;
    			e.printStackTrace();
    		} finally {
    			if (encoder != null) {
    				try {
    					encoder.close();
    				} catch (Exception ex) {
    				}
    			}
    			if (stream != null) {
    				try {
    					stream.close();
    				} catch (Exception ex) {
    				}
    			}
    		}
        }
	}

	public static int initEdmo() {
        int edmoSize = 0;
		XMLDecoder decoder = null;
		try {

			EdmoHandler edmoH = new EdmoHandler();

			SAXParserFactory.newInstance().newSAXParser().parse(
					new FileInputStream("resources/Edmo.xml"), edmoH); //$NON-NLS-1$

			_edmo.putAll(edmoH.get_valeurs());
            edmoSize = _edmo.size();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (decoder != null) {
				try {
					decoder.close();
				} catch (Exception ex) {
				}
			}
		}
        return edmoSize;
	}

	public static void updateEdmo(IProgressMonitor monitor) {
		okEdmo = false;
        nbEdmo = 0;
		FileOutputStream stream = null;
        String result = Global.EMPTY_STRING;

		try {
            if (!monitor.isCanceled())
            {
                // Appel au WebService
    			Edmo_webservice service = new Edmo_webserviceLocator();
    			Edmo_webserviceSoap port = service.getedmo_webserviceSoap12();
    			result = port.ws_edmo_get_list();
            }
            if (!monitor.isCanceled() && !result.equals(Global.EMPTY_STRING))
            {
                // écriture du nouveau fichier
				File file = new java.io.File("resources/Edmo.xml"); //$NON-NLS-1$

				stream = new FileOutputStream(file.toString());
				OutputStreamWriter osw = new OutputStreamWriter(stream, "UTF-8"); //$NON-NLS-1$
				osw.write(result);
				osw.flush();
				osw.close();
                
                // stockage des valeurs dans l'objet
                nbEdmo = initEdmo();
                okEdmo = true;
			}

		} catch (ServiceException ex1) {
			ex1.printStackTrace();
		} catch (java.rmi.RemoteException ex2) {
			ex2.printStackTrace();
		} catch (Exception exG) {
			exG.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private static void updateVocabList(String listName) 
    {
		okVocabLists = true;
        
		FileOutputStream stream = null;
		XMLEncoder encoder = null;
		CodeTableRecordTypeArrayHolder table = new CodeTableRecordTypeArrayHolder();
		javax.xml.rpc.holders.BooleanHolder error = new BooleanHolder();
		try {
			VocabSoapBindingStub vocab = new VocabSoapBindingStub(
					new java.net.URL(Utils.configuration.getVocabURL()),
					new Service());
			vocab.getList(new String[] { Utils.configuration
					.getVocabListURL(listName) }, null, table, error);
			if (!error.value) {
				if (table.value != null) {
					_vocabList.put(listName, table.value);

					File dir = new java.io.File("resources/Vocab"); //$NON-NLS-1$
					if (!dir.exists()) {
						dir.mkdirs();
					}
					File file = new java.io.File(dir, listName + ".xml"); //$NON-NLS-1$
					stream = new FileOutputStream(file.toString());

					encoder = new XMLEncoder(stream);
					encoder.writeObject(table.value);
					_vocabListStatus.put(listName, table.value.length);
				} else {
					_vocabListStatus.put(listName, 0);
				}
			} else {
				okVocabLists = false;
				_vocabListStatus.put(listName, -1);
			}
		} catch (Exception e) {
			_vocabListStatus.put(listName, -1);
			okVocabLists = false;
			e.printStackTrace();
		} finally {
			if (encoder != null) {
				try {
					encoder.close();
				} catch (Exception ex) {
				}
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	public static CodeTableRecordType[] getVocabList(String listName) {
		return _vocabList.get(listName);
	}

	/**
	 * 
	 */
	public static void updateVocabLists(IProgressMonitor monitor) {
		Iterator<String> keys = Utils.configuration.getVocabLists().keySet().iterator();
        int keySetSize = Utils.configuration.getVocabLists().size();
        int idxCurrent = 0;

        nbVocabLists = 0;
        okVocabLists = false;
        
        
      while (keys.hasNext()) {
          idxCurrent ++ ;
          String listName = keys.next();
          if (!monitor.isCanceled()){
              StringBuffer msg = new StringBuffer();
              msg.append(Messages.getString("SWTProcessWS.9")); //$NON-NLS-1$
              msg.append(Global.SPACE);
              msg.append(listName);
              msg.append(" [");//$NON-NLS-1$
              msg.append((idxCurrent + 1));
              msg.append(" / ");//$NON-NLS-1$
              msg.append(keySetSize);
              msg.append("]");//$NON-NLS-1$
              monitor.subTask(msg.toString());
              updateVocabList(listName);
              if (okVocabLists)  nbVocabLists++;
          }else{
              okVocabLists = false;
          }
      }
	}

	/**
	 * 
	 */
	public static void initVocabLists() {
		XMLDecoder decoder = null;
		Iterator<String> keys = Utils.configuration.getVocabLists().keySet()
				.iterator();
		while (keys.hasNext()) {
			String listName = keys.next();
			try {
				String fileName = String.format("resources/Vocab/%s.xml", //$NON-NLS-1$
						listName);
				File file = new File(fileName);
				if (file.exists()) {
					decoder = new XMLDecoder(new FileInputStream(file));
					_vocabList.put(listName, (CodeTableRecordType[]) decoder
							.readObject());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (decoder != null) {
					try {
						decoder.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	public static void initVocabMap(String list1, String list2) {
		XMLDecoder decoder = null;
		try {
			String fileName = "resources/Vocab/" + Utils.concat(list1, list2, Global.UNDERSCORE) //$NON-NLS-1$
					+ ".xml"; //$NON-NLS-1$
			File file = new File(fileName);
			if (file.exists()) {
				decoder = new XMLDecoder(new FileInputStream(file));

				_vocabMap.put(Utils.concat(list1, list2, Global.UNDERSCORE),
						(CodeTableRecordType[]) decoder.readObject());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (decoder != null) {
				try {
					decoder.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
    
    public static void updateVocabMaps(IProgressMonitor monitor)
    {
        okVocabMaps = false;
        nbVocabMaps = 0;
        for (String key : _vocabMap.keySet()){
            if (!monitor.isCanceled()){
                monitor.subTask(Messages.getString("SWTProcessWS.9") + " Mapping " + key); //$NON-NLS-1$ //$NON-NLS-2$
                String[] listName = key.split(Global.UNDERSCORE);
                NemoLists.updateVocabMap(listName[0], listName[1]);
                if (okVocabMaps)  nbVocabMaps++;
            }else{
                okVocabMaps = false;
            }
        }
    }

	/**
	 * 
	 */
	private static void updateVocabMap(String list1, String list2) {
		okVocabMaps = true;
		FileOutputStream stream = null;
		XMLEncoder encoder = null;
		CodeTableRecordTypeArrayHolder table = new CodeTableRecordTypeArrayHolder();
		javax.xml.rpc.holders.BooleanHolder error = new BooleanHolder();
		try {
			VocabSoapBindingStub vocab = new VocabSoapBindingStub(
					new java.net.URL(Utils.configuration.getVocabURL()),
					new Service());
			vocab.getMap(new String[] { Utils.configuration
					.getVocabListURL(list1) }, Utils.configuration
					.getPredicate(), new String[] { Utils.configuration
					.getVocabListURL(list2) }, "false", table, error); //$NON-NLS-1$
			if (!error.value) {
				if (table.value != null) {
					_vocabMap.put(Utils.concat(list1, list2, Global.UNDERSCORE), table.value);

					File dir = new java.io.File("resources/Vocab"); //$NON-NLS-1$
					if (!dir.exists()) {
						dir.mkdirs();
					}
					File file = new java.io.File(dir, Utils
							.concat(list1, list2, Global.UNDERSCORE)
							+ ".xml"); //$NON-NLS-1$
					stream = new FileOutputStream(file.toString());

					encoder = new XMLEncoder(stream);
					encoder.writeObject(table.value);
				}
			}else{
               okVocabMaps = false; 
            }
		} catch (Exception e) {
			e.printStackTrace();
            okVocabMaps = false;
		} finally {
			if (encoder != null) {
				try {
					encoder.close();
				} catch (Exception ex) {
				}
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	public static CodeTableRecordType[] getVocabMap(String list1, String list2) {
		return _vocabMap.get(Utils.concat(list1, list2, Global.UNDERSCORE));
	}

	public static void setVocabMap(String list1, String list2,
			CodeTableRecordType[] map) {
		_vocabMap.put(Utils.concat(list1, list2, Global.UNDERSCORE), map);
	}

	public static java.util.HashMap<Integer, String> getEdmo() {
		return _edmo;
	}

	public static void writeSdnParameters(fr.ifremer.nemo.util.Conversion convert) {
		setSdnParameters(convert.getDebut());
		
		setSdnParameters(convert.getDebut() + "SDN_parameter_mapping"); //$NON-NLS-1$
        
        for (fr.ifremer.nemo.input.ParameterModel param : convert.getAddedParameterModels()) {
            _sdnParameters += Global.RN + convert.getDebut() + parameterMap(param.getModel());
        }

		// FAE 0001131 : remplacer "\n" par "\r\n" pour retour chariot sous bloc-note
		for (fr.ifremer.nemo.input.ParameterModel param : fr.ifremer.nemo.ui.DataModel.stationModel.getParameterModels()) {
			_sdnParameters += Global.RN + convert.getDebut() + parameterMap(param.getModel());
		}
	}

	public static String getVocabMatch(String list1, String list2,
			String value, String errorMessage) {
		String strReturn = errorMessage;

		try {
			CodeTableRecordType[] tableMap = NemoLists
					.getVocabMap(list1, list2);
			int intLength = tableMap.length;

			for (int i = 0; i < intLength; i++) {
				if (tableMap[i].getEntryTermAbbr().equals(value)) {
					if (tableMap[i].getExactMatch() != null) {
						strReturn = tableMap[i].getExactMatch()[0]
								.getEntryKey();
					} else if (tableMap[i].getNarrowMatch() != null) {
						strReturn = tableMap[i].getNarrowMatch()[0]
								.getEntryKey();
					} else if (tableMap[i].getBroadMatch() != null) {
						strReturn = tableMap[i].getBroadMatch()[0]
								.getEntryKey();
					} else if (tableMap[i].getMajorMatch() != null) {
						strReturn = tableMap[i].getMajorMatch()[0]
								.getEntryKey();
					} else if (tableMap[i].getMinorMatch() != null) {
						strReturn = tableMap[i].getMinorMatch()[0]
								.getEntryKey();
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return strReturn;
	}

	public static String parameterMap(fr.ifremer.nemo.domain.Parameter param) {
        String name = param.getCode();
        if (param.getCode().equals(fr.ifremer.nemo.domain.AddedParameter.IdParameter.CJDY.toString())) name = ((fr.ifremer.nemo.domain.AddedParameter) param).getFullName();
		String pdvCode = Utils.extraitCodeURN(getVocabMatch("P091", "P011", //$NON-NLS-1$ //$NON-NLS-2$
            param.getCode(), Messages.getString("NemoLists.unknown"))); //$NON-NLS-1$
		String unitCode = Utils.extraitCodeURN(getVocabMatch("P091", "P061", param.getCode(), Messages //$NON-NLS-1$ //$NON-NLS-2$
				.getString("NemoLists.unknown"))); //$NON-NLS-1$
		return "<subject>SDN:LOCAL:" + name //$NON-NLS-1$
				+ "</subject><object>SDN:P011::" + pdvCode //$NON-NLS-1$
				+ "</object><unit>SDN:P061::" + unitCode + "</unit>";  //$NON-NLS-1$//$NON-NLS-2$
	}

	public static String getSdnParameters() {
		return _sdnParameters;
	}

	public static void setSdnParameters(String parameters) {
		_sdnParameters = parameters;
	}

    /**
     * Getter sur _vocabList.
     * @return the vocabList
     */
    public static HashMap<String, CodeTableRecordType[]> getVocabList()
    {
        return _vocabList;
    }

    /**
     * Getter sur _vocabMap.
     * @return the vocabMap
     */
    public static HashMap<String, CodeTableRecordType[]> getVocabMap()
    {
        return _vocabMap;
    }

} /* End of class: NemoLists */
