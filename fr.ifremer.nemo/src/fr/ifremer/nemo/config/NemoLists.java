package fr.ifremer.nemo.config;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.rpc.holders.BooleanHolder;

import org.apache.axis.client.Service;

import fr.ifremer.nemo.ws.NemoWS;
import fr.ifremer.nemo.ws.NemoWSServiceLocator;
import fr.ifremer.nemo.ws.WSCountry;
import fr.ifremer.nemo.ws.WSDataCentre;
import fr.ifremer.nemo.ws.WSDataType;
import fr.ifremer.nemo.ws.WSParameter;
import fr.ifremer.nemo.ws.WSRegion;
import fr.ifremer.nemo.ws.WSShip;
import fr.ifremer.nemo.ws.WSSurveyDescription;
import fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType;
import fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeArrayHolder;
import fr.ifremer.nemo.wsVocab.vocab_pkg.VocabLocator;
import fr.ifremer.nemo.wsVocab.vocab_pkg.VocabPortType;
import fr.ifremer.nemo.wsVocab.vocab_pkg.VocabSoapBindingStub;

/**
 * Class NemoLists
 * 
 * 
 * @author Gerard BUNEL
 */
public class NemoLists implements Cloneable {
	static protected java.util.List<WSDataType> _dataTypes = new java.util.ArrayList<WSDataType>();

	static protected java.util.List<WSCountry> _countries = new java.util.ArrayList<WSCountry>();

	static protected java.util.List<WSShip> _ships = new java.util.ArrayList<WSShip>();

	static protected java.util.List<WSDataCentre> _dataCentres = new java.util.ArrayList<WSDataCentre>();

	static protected java.util.List<WSParameter> _parameters = new java.util.ArrayList<WSParameter>();

	static protected java.util.List<WSRegion> _regions = new java.util.ArrayList<WSRegion>();

	static protected java.util.List<WSSurveyDescription> _surveys = new java.util.ArrayList<WSSurveyDescription>();

	public static Boolean okCountries, okShips, okSurveys, okDataCentres,
			okParameters, okRegions, okDataTypes, okVocabLists;
	
	public static int nbCountries, nbShips, nbSurveys, nbDataCentres,
			nbParameters, nbRegions, nbDataTypes, nbVocabLists;
	
	static protected HashMap<String, CodeTableRecordType[]> _vocabList = new HashMap<String, CodeTableRecordType[]>();
	
	static protected HashMap<String, Integer> _vocabListStatus = new HashMap<String, Integer>();

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
	 * Returns linked object for role "dataTypes" at index i
	 * 
	 * @param i
	 *            the index of the linked object
	 * @return linked object at index i
	 */
	public static WSDataType dataTypesAt(int i) {
		return (WSDataType) _dataTypes.get(i);
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
	 * release all links for role "countries"
	 */
	public static void releaseCountries() {
		for (WSCountry t : getCountries()) {
			removeCountry(t);
		}
	}

	/**
	 * remove link with x for role "countries"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public static void removeCountry(WSCountry x) {
		if (!countriesIncludes(x))
			return;
		nocheckRemoveCountry(x);
	}

	/**
	 * Add a link for role "countries"
	 * 
	 * @param x
	 *            the object to link with
	 */
	public static void addCountry(WSCountry x) {
		if (countriesIncludes(x))
			return;
		nocheckAddCountry(x);
	}

	/**
	 * Returns the collection of linked objects for role "countries"
	 * 
	 * @return Array of linked objects
	 */
	public static WSCountry[] getCountries() {
		WSCountry[] v = new WSCountry[_countries.size()];
		_countries.toArray(v);
		return v;
	}

	/**
	 * Returns linked object for role "countries" at index i
	 * 
	 * @param i
	 *            the index of the linked object
	 * @return linked object at index i
	 */
	public static WSCountry countriesAt(int i) {
		return (WSCountry) _countries.get(i);
	}

	/**
	 * returns index of object x in "countries"
	 * 
	 * @param x
	 *            the linked object
	 */
	public static int countriesIndexOf(WSCountry x) {
		if (!countriesIncludes(x))
			return -1;
		for (int i = 0; i < countriesSize(); i++) {
			if (countriesAt(i) == x) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * move x one index before in the list for role "countries"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void countriesMoveUp(WSCountry x) {
		int index;
		index = countriesIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveCountryAt(x, index - 1);
	}

	/**
	 * move x to the top of list for role "countries"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void countriesMoveTop(WSCountry x) {
		int index;
		index = countriesIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveCountryAt(x, 0);
	}

	/**
	 * move x one index more in the list for role "countries"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void countriesMoveDown(WSCountry x) {
		int index;
		index = countriesIndexOf(x);
		if (index < 0 || index > countriesSize() - 2)
			return;
		nocheckMoveCountryAt(x, index + 1);
	}

	/**
	 * move x to the bottom of list for role "countries"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void countriesMoveBottom(WSCountry x) {
		int index;
		index = countriesIndexOf(x);
		if (index < 0 || index > countriesSize() - 2)
			return;
		nocheckMoveCountryAt(x, countriesSize() - 1);
	}

	/**
	 * move x one index more in the list for role "countries"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void nocheckMoveCountryAt(WSCountry x, int a_Index) {
		int index;
		WSCountry tmp;
		index = countriesIndexOf(x);
		tmp = _countries.get(index);
		_countries.remove(index);
		_countries.add(a_Index, tmp);
	}

	/**
	 * remove x from the linked objects for role "countries"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see NemoLists#removeCountry(WSCountry)
	 */
	protected static void nocheckRemoveCountry(WSCountry x) {
		_countries.remove(x);
	}

	/**
	 * adds x to the linked objects for role "countries"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see NemoLists#addCountry(WSCountry)
	 */
	protected static void nocheckAddCountry(WSCountry x) {
		_countries.add(x);
	}

	/**
	 * returns the number of linked objects for role "countries"
	 * 
	 * @return the number of linked objects
	 */
	public static int countriesSize() {
		return _countries.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "countries"
	 * 
	 * @param x
	 *            an instance of WSCountry
	 */
	public static boolean countriesIncludes(WSCountry x) {
		return _countries.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "countries"
	 * 
	 */
	public static java.util.Iterator<WSCountry> countriesIterator() {
		return _countries.iterator();

	}

	/**
	 * release all links for role "ships"
	 */
	public static void releaseShips() {
		for (WSShip t : getShips()) {
			removeShip(t);
		}
	}

	/**
	 * remove link with x for role "ships"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public static void removeShip(WSShip x) {
		if (!shipsIncludes(x))
			return;
		nocheckRemoveShip(x);
	}

	/**
	 * Add a link for role "ships"
	 * 
	 * @param x
	 *            the object to link with
	 */
	public static void addShip(WSShip x) {
		if (shipsIncludes(x))
			return;
		nocheckAddShip(x);
	}

	/**
	 * Returns the collection of linked objects for role "ships"
	 * 
	 * @return Array of linked objects
	 */
	public static WSShip[] getShips() {
		WSShip[] v = new WSShip[_ships.size()];
		_ships.toArray(v);
		return v;
	}

	/**
	 * Returns linked object for role "ships" at index i
	 * 
	 * @param i
	 *            the index of the linked object
	 * @return linked object at index i
	 */
	public static WSShip shipsAt(int i) {
		return (WSShip) _ships.get(i);
	}

	/**
	 * returns index of object x in "ships"
	 * 
	 * @param x
	 *            the linked object
	 */
	public static int shipsIndexOf(WSShip x) {
		if (!shipsIncludes(x))
			return -1;
		for (int i = 0; i < shipsSize(); i++) {
			if (shipsAt(i) == x) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * move x one index before in the list for role "ships"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void shipsMoveUp(WSShip x) {
		int index;
		index = shipsIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveShipAt(x, index - 1);
	}

	/**
	 * move x to the top of list for role "ships"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void shipsMoveTop(WSShip x) {
		int index;
		index = shipsIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveShipAt(x, 0);
	}

	/**
	 * move x one index more in the list for role "ships"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void shipsMoveDown(WSShip x) {
		int index;
		index = shipsIndexOf(x);
		if (index < 0 || index > shipsSize() - 2)
			return;
		nocheckMoveShipAt(x, index + 1);
	}

	/**
	 * move x to the bottom of list for role "ships"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void shipsMoveBottom(WSShip x) {
		int index;
		index = shipsIndexOf(x);
		if (index < 0 || index > shipsSize() - 2)
			return;
		nocheckMoveShipAt(x, shipsSize() - 1);
	}

	/**
	 * move x one index more in the list for role "ships"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void nocheckMoveShipAt(WSShip x, int a_Index) {
		int index;
		WSShip tmp;
		index = shipsIndexOf(x);
		tmp = _ships.get(index);
		_ships.remove(index);
		_ships.add(a_Index, tmp);
	}

	/**
	 * remove x from the linked objects for role "ships"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see NemoLists#removeShip(WSShip)
	 */
	protected static void nocheckRemoveShip(WSShip x) {
		_ships.remove(x);
	}

	/**
	 * adds x to the linked objects for role "ships"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see NemoLists#addShip(WSShip)
	 */
	protected static void nocheckAddShip(WSShip x) {
		_ships.add(x);
	}

	/**
	 * returns the number of linked objects for role "ships"
	 * 
	 * @return the number of linked objects
	 */
	public static int shipsSize() {
		return _ships.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "ships"
	 * 
	 * @param x
	 *            an instance of WSShip
	 */
	public static boolean shipsIncludes(WSShip x) {
		return _ships.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "ships"
	 * 
	 */
	public static java.util.Iterator<WSShip> shipsIterator() {
		return _ships.iterator();

	}

	/**
	 * release all links for role "dataCentres"
	 */
	public static void releaseDataCentres() {
		for (WSDataCentre t : getDataCentres()) {
			removeDataCentre(t);
		}
	}

	/**
	 * remove link with x for role "dataCentres"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public static void removeDataCentre(WSDataCentre x) {
		if (!dataCentresIncludes(x))
			return;
		nocheckRemoveDataCentre(x);
	}

	/**
	 * Add a link for role "dataCentres"
	 * 
	 * @param x
	 *            the object to link with
	 */
	public static void addDataCentre(WSDataCentre x) {
		if (dataCentresIncludes(x))
			return;
		nocheckAddDataCentre(x);
	}

	/**
	 * Returns the collection of linked objects for role "dataCentres"
	 * 
	 * @return Array of linked objects
	 */
	public static WSDataCentre[] getDataCentres() {
		WSDataCentre[] v = new WSDataCentre[_dataCentres.size()];
		_dataCentres.toArray(v);
		return v;
	}

	/**
	 * Returns linked object for role "dataCentres" at index i
	 * 
	 * @param i
	 *            the index of the linked object
	 * @return linked object at index i
	 */
	public static WSDataCentre dataCentresAt(int i) {
		return (WSDataCentre) _dataCentres.get(i);
	}

	/**
	 * returns index of object x in "dataCentres"
	 * 
	 * @param x
	 *            the linked object
	 */
	public static int dataCentresIndexOf(WSDataCentre x) {
		if (!dataCentresIncludes(x))
			return -1;
		for (int i = 0; i < dataCentresSize(); i++) {
			if (dataCentresAt(i) == x) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * move x one index before in the list for role "dataCentres"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void dataCentresMoveUp(WSDataCentre x) {
		int index;
		index = dataCentresIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveDataCentreAt(x, index - 1);
	}

	/**
	 * move x to the top of list for role "dataCentres"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void dataCentresMoveTop(WSDataCentre x) {
		int index;
		index = dataCentresIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveDataCentreAt(x, 0);
	}

	/**
	 * move x one index more in the list for role "dataCentres"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void dataCentresMoveDown(WSDataCentre x) {
		int index;
		index = dataCentresIndexOf(x);
		if (index < 0 || index > dataCentresSize() - 2)
			return;
		nocheckMoveDataCentreAt(x, index + 1);
	}

	/**
	 * move x to the bottom of list for role "dataCentres"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void dataCentresMoveBottom(WSDataCentre x) {
		int index;
		index = dataCentresIndexOf(x);
		if (index < 0 || index > dataCentresSize() - 2)
			return;
		nocheckMoveDataCentreAt(x, dataCentresSize() - 1);
	}

	/**
	 * move x one index more in the list for role "dataCentres"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void nocheckMoveDataCentreAt(WSDataCentre x, int a_Index) {
		int index;
		WSDataCentre tmp;
		index = dataCentresIndexOf(x);
		tmp = _dataCentres.get(index);
		_dataCentres.remove(index);
		_dataCentres.add(a_Index, tmp);
	}

	/**
	 * remove x from the linked objects for role "dataCentres"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see NemoLists#removeDataCentre(WSDataCentre)
	 */
	protected static void nocheckRemoveDataCentre(WSDataCentre x) {
		_dataCentres.remove(x);
	}

	/**
	 * adds x to the linked objects for role "dataCentres"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see NemoLists#addDataCentre(WSDataCentre)
	 */
	protected static void nocheckAddDataCentre(WSDataCentre x) {
		_dataCentres.add(x);
	}

	/**
	 * returns the number of linked objects for role "dataCentres"
	 * 
	 * @return the number of linked objects
	 */
	public static int dataCentresSize() {
		return _dataCentres.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "dataCentres"
	 * 
	 * @param x
	 *            an instance of WSDataCentre
	 */
	public static boolean dataCentresIncludes(WSDataCentre x) {
		return _dataCentres.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "dataCentres"
	 * 
	 */
	public static java.util.Iterator<WSDataCentre> dataCentresIterator() {
		return _dataCentres.iterator();

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
		return (WSParameter) _parameters.get(i);
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
	 * release all links for role "regions"
	 */
	public static void releaseRegions() {
		for (WSRegion t : getRegions()) {
			removeRegion(t);
		}
	}

	/**
	 * remove link with x for role "regions"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public static void removeRegion(WSRegion x) {
		if (!regionsIncludes(x))
			return;
		nocheckRemoveRegion(x);
	}

	/**
	 * Add a link for role "regions"
	 * 
	 * @param x
	 *            the object to link with
	 */
	public static void addRegion(WSRegion x) {
		if (regionsIncludes(x))
			return;
		nocheckAddRegion(x);
	}

	/**
	 * Returns the collection of linked objects for role "regions"
	 * 
	 * @return Array of linked objects
	 */
	public static WSRegion[] getRegions() {
		WSRegion[] v = new WSRegion[_regions.size()];
		_regions.toArray(v);
		return v;
	}

	/**
	 * Returns linked object for role "regions" at index i
	 * 
	 * @param i
	 *            the index of the linked object
	 * @return linked object at index i
	 */
	public static WSRegion regionsAt(int i) {
		return (WSRegion) _regions.get(i);
	}

	/**
	 * returns index of object x in "regions"
	 * 
	 * @param x
	 *            the linked object
	 */
	public static int regionsIndexOf(WSRegion x) {
		if (!regionsIncludes(x))
			return -1;
		for (int i = 0; i < regionsSize(); i++) {
			if (regionsAt(i) == x) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * move x one index before in the list for role "regions"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void regionsMoveUp(WSRegion x) {
		int index;
		index = regionsIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveRegionAt(x, index - 1);
	}

	/**
	 * move x to the top of list for role "regions"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void regionsMoveTop(WSRegion x) {
		int index;
		index = regionsIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveRegionAt(x, 0);
	}

	/**
	 * move x one index more in the list for role "regions"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void regionsMoveDown(WSRegion x) {
		int index;
		index = regionsIndexOf(x);
		if (index < 0 || index > regionsSize() - 2)
			return;
		nocheckMoveRegionAt(x, index + 1);
	}

	/**
	 * move x to the bottom of list for role "regions"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void regionsMoveBottom(WSRegion x) {
		int index;
		index = regionsIndexOf(x);
		if (index < 0 || index > regionsSize() - 2)
			return;
		nocheckMoveRegionAt(x, regionsSize() - 1);
	}

	/**
	 * move x one index more in the list for role "regions"
	 * 
	 * @param x
	 *            the object to move
	 */
	public static void nocheckMoveRegionAt(WSRegion x, int a_Index) {
		int index;
		WSRegion tmp;
		index = regionsIndexOf(x);
		tmp = _regions.get(index);
		_regions.remove(index);
		_regions.add(a_Index, tmp);
	}

	/**
	 * remove x from the linked objects for role "regions"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see NemoLists#removeRegion(WSRegion)
	 */
	protected static void nocheckRemoveRegion(WSRegion x) {
		_regions.remove(x);
	}

	/**
	 * adds x to the linked objects for role "regions"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see NemoLists#addRegion(WSRegion)
	 */
	protected static void nocheckAddRegion(WSRegion x) {
		_regions.add(x);
	}

	/**
	 * returns the number of linked objects for role "regions"
	 * 
	 * @return the number of linked objects
	 */
	public static int regionsSize() {
		return _regions.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "regions"
	 * 
	 * @param x
	 *            an instance of WSRegion
	 */
	public static boolean regionsIncludes(WSRegion x) {
		return _regions.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "regions"
	 * 
	 */
	public static java.util.Iterator<WSRegion> regionsIterator() {
		return _regions.iterator();

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
		return (WSSurveyDescription) _surveys.get(i);
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
	public static void initCountries() {
		XMLDecoder decoder = null;
		try {
			decoder = new XMLDecoder(new FileInputStream(
					"resources/Countries.xml"));
			// decoder = new
			// XMLDecoder(NemoLists.class.getResourceAsStream("/resources/Countries.xml"));
			_countries = (java.util.List<WSCountry>) decoder.readObject();
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
	public static void initDataTypes() {
		XMLDecoder decoder = null;
		try {
			decoder = new XMLDecoder(new FileInputStream(
					"resources/DataTypes.xml"));
			// decoder = new
			// XMLDecoder(NemoLists.class.getResourceAsStream("/resources/DataTypes.xml"));
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
		initRegions();
		initCountries();
		initShips();
		intDataCentres();
		initParameters();
		initSurveys();
		initVocabLists();
	}

	/**
	 * 
	 */
	public static void initParameters() {
		XMLDecoder decoder = null;
		try {
			decoder = new XMLDecoder(new FileInputStream(
					"resources/Parameters.xml"));
			// decoder = new
			// XMLDecoder(NemoLists.class.getResourceAsStream("/resources/Parameters.xml"));
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
	public static void initRegions() {
		XMLDecoder decoder = null;
		try {
			decoder = new XMLDecoder(new FileInputStream(
					"resources/Regions.xml"));

			// decoder = new
			// XMLDecoder(NemoLists.class.getResourceAsStream("/resources/Regions.xml"));
			_regions = (java.util.List<WSRegion>) decoder.readObject();
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
	public static void initShips() {
		XMLDecoder decoder = null;
		try {
			decoder = new XMLDecoder(new FileInputStream("resources/Ships.xml"));
			// decoder = new
			// XMLDecoder(NemoLists.class.getResourceAsStream("/resources/Ships.xml"));
			_ships = (java.util.List<WSShip>) decoder.readObject();
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
			// decoder = new
			// XMLDecoder(NemoLists.class.getResourceAsStream("/resources/Surveys.xml"));
			decoder = new XMLDecoder(new FileInputStream(
					"resources/Surveys.xml"));
			_surveys = (java.util.List<WSSurveyDescription>) decoder
					.readObject();
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
	public static void intDataCentres() {
		XMLDecoder decoder = null;
		try {
			// decoder = new
			// XMLDecoder(NemoLists.class.getResourceAsStream("/resources/DataCentres.xml"));
			decoder = new XMLDecoder(new FileInputStream(
					"resources/DataCentres.xml"));
			_dataCentres = (java.util.List<WSDataCentre>) decoder.readObject();
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
	 * @param args
	 */
	public static void main(String[] args) {
		updateDataTypes();
		updateRegions();
		updateCountries();
		updateShips();
		updateDataCentres();
		updateParameters();
		updateSurveys();
		updateVocabLists();
	}

	/**
	 * 
	 */
	public static void updateCountries() {
		FileOutputStream stream = null;
		XMLEncoder encoder = null;
		okCountries = false;
		try {
			java.net.URL toto = new java.net.URL(Configuration.getSingleton()
					.getNemoWebServiceURL());

			NemoWSServiceLocator servicews = new NemoWSServiceLocator();
			NemoWS service = servicews.getNEMO(toto);
			// new
			// java.net.URL(Configuration.getSingleton().getNemoWebServiceURL()));
			WSCountry[] countries = service.getCountries();
			releaseCountries();
			for (int i = 0; i < countries.length; i++) {
				addCountry(countries[i]);
			}

			File file = new java.io.File("resources/Countries.xml");
			stream = new FileOutputStream(file.toString());

			// URL url =
			// NemoLists.class.getResource("/resources/Countries.xml");
			// String file = url.getFile();
			// stream = new FileOutputStream(file);
			encoder = new XMLEncoder(stream);
			encoder.writeObject(_countries);
			okCountries = true;
			nbCountries = countries.length;
		} catch (Exception e) {
			okCountries = false;
			System.out.println("WS countries failed");
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

	/**
	 * 
	 */
	public static void updateDataCentres() {
		FileOutputStream stream = null;
		XMLEncoder encoder = null;
		okDataCentres = false;
		try {
			NemoWS service = new NemoWSServiceLocator()
					.getNEMO(new java.net.URL(Configuration.getSingleton()
							.getNemoWebServiceURL()));
			WSDataCentre[] types = service.getDataCentres();
			releaseDataCentres();
			for (int i = 0; i < types.length; i++) {
				addDataCentre(types[i]);
			}

			File file = new java.io.File("resources/DataCentres.xml");
			stream = new FileOutputStream(file.toString());

			// URL url =
			// NemoLists.class.getResource("/resources/DataCentres.xml");
			// String file = url.getFile();
			// stream = new FileOutputStream(file);
			encoder = new XMLEncoder(stream);
			encoder.writeObject(_dataCentres);
			okDataCentres = true;
			nbDataCentres = types.length;
		} catch (Exception e) {
			okDataCentres = false;
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

	/**
	 * 
	 */
	public static void updateDataTypes() {
		FileOutputStream stream = null;
		XMLEncoder encoder = null;
		okDataTypes = false;
		try {
			NemoWS service = new NemoWSServiceLocator()
					.getNEMO(new java.net.URL(Configuration.getSingleton()
							.getNemoWebServiceURL()));
			WSDataType[] types = service.getDataTypes();
			releaseDataTypes();
			for (int i = 0; i < types.length; i++) {
				addDataType(types[i]);
			}

			File file = new java.io.File("resources/DataTypes.xml");
			stream = new FileOutputStream(file.toString());

			// URL url =
			// NemoLists.class.getResource("/resources/DataTypes.xml");
			// String file = url.getFile();
			// stream = new FileOutputStream(file);
			encoder = new XMLEncoder(stream);
			encoder.writeObject(_dataTypes);
			okDataTypes = true;
			nbDataTypes = types.length;
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

	/**
	 * 
	 */
	public static void updateParameters() {
		okParameters = false;
		FileOutputStream stream = null;
		XMLEncoder encoder = null;
		try {
			NemoWS service = new NemoWSServiceLocator()
					.getNEMO(new java.net.URL(Configuration.getSingleton()
							.getNemoWebServiceURL()));
			WSParameter[] parameters = service.getParameters();
			releaseParameters();
			for (int i = 0; i < parameters.length; i++) {
				addParameter(parameters[i]);
			}

			File file = new java.io.File("resources/Parameters.xml");
			stream = new FileOutputStream(file.toString());

			// URL url =
			// NemoLists.class.getResource("/resources/Parameters.xml");
			// String file = url.getFile();
			// stream = new FileOutputStream(file);
			encoder = new XMLEncoder(stream);
			encoder.writeObject(_parameters);
			okParameters = true;
			nbParameters = parameters.length;
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

	/**
	 * 
	 */
	public static void updateRegions() {
		okRegions = false;
		FileOutputStream stream = null;
		XMLEncoder encoder = null;
		try { 
			int j;
			NemoWS service = new NemoWSServiceLocator()
					.getNEMO(new java.net.URL(Configuration.getSingleton()
							.getNemoWebServiceURL()));
			WSRegion[] regions = service.getRegions();
			releaseRegions();
			for (int i = 0; i < regions.length; i++) {
				addRegion(regions[i]);
			}

			File file = new java.io.File("resources/Regions.xml");
			stream = new FileOutputStream(file.toString());

			// URL url = NemoLists.class.getResource("/resources/Regions.xml");
			// String file = url.getFile();
			// stream = new FileOutputStream(file);
			encoder = new XMLEncoder(stream);
			encoder.writeObject(_regions);
			okRegions = true;
			nbRegions = regions.length;
		} catch (Exception e) {
			okRegions = false;
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

	/**
	 * 
	 */
	public static void updateShips() {
		okShips = false;
		FileOutputStream stream = null;
		XMLEncoder encoder = null;
		try {
			NemoWS service = new NemoWSServiceLocator()
					.getNEMO(new java.net.URL(Configuration.getSingleton()
							.getNemoWebServiceURL()));
			WSShip[] ships = service.getShips();
			releaseShips();
			for (int i = 0; i < ships.length; i++) {
				addShip(ships[i]);
			}

			File file = new java.io.File("resources/Ships.xml");
			stream = new FileOutputStream(file.toString());

			// URL url = NemoLists.class.getResource("/resources/Ships.xml");
			// String file = url.getFile();
			// stream = new FileOutputStream(file);
			encoder = new XMLEncoder(stream);
			encoder.writeObject(_ships);
			okShips = true;
			nbShips = ships.length;
		} catch (Exception e) {
			okShips = false;
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

	/**
	 * 
	 */
	public static void updateSurveys() {
		okSurveys = false;
		FileOutputStream stream = null;
		XMLEncoder encoder = null;
		try {
			NemoWS service = new NemoWSServiceLocator()
					.getNEMO(new java.net.URL(Configuration.getSingleton()
							.getNemoWebServiceURL()));
			WSSurveyDescription[] surveys = service.getSurveyList();
			releaseSurveys();
			for (int i = 0; i < surveys.length; i++) {
				addSurvey(surveys[i]);
			}

			File file = new java.io.File("resources/Surveys.xml");
			stream = new FileOutputStream(file.toString());

			// URL url = NemoLists.class.getResource("/resources/Surveys.xml");
			// String file = url.getFile();
			// stream = new FileOutputStream(file);
			encoder = new XMLEncoder(stream);
			encoder.writeObject(_surveys);
			okSurveys = true;
			nbSurveys = surveys.length;
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

	/**
	 * 
	 */
	public static void updateVocabList(String listName) {
		//okSurveys = false;
		okVocabLists = true;
		FileOutputStream stream = null;
		XMLEncoder encoder = null;
		CodeTableRecordTypeArrayHolder table = new CodeTableRecordTypeArrayHolder();
		javax.xml.rpc.holders.BooleanHolder error = new BooleanHolder();
		try {
			VocabSoapBindingStub vocab = new VocabSoapBindingStub(
					new java.net.URL(Configuration.getSingleton().getVocabURL()),
					new Service());
			vocab.getList(new String[]{Configuration.getSingleton().getVocabListURL(listName)}, null, table, error );
			if (!error.value) {
				if (table.value != null) {
					_vocabList.put(listName, table.value);
					
					
					File dir = new java.io.File("resources/Vocab");
					if (!dir.exists()) {
						dir.mkdirs();
					}
					File file = new java.io.File(dir, listName+".xml");
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
			if (okVocabLists) {
				nbVocabLists++;
			}
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
	public static void updateVocabLists() {
		Iterator<String> keys = Configuration.getSingleton().getVocabLists().keySet().iterator();
		nbVocabLists = 0;
		while (keys.hasNext()) {
			String listName = keys.next();
			updateVocabList(listName);
		}
	}
		
	/**
	 * 
	 */
	public static void initVocabLists() {
		XMLDecoder decoder = null;
		Iterator<String> keys = Configuration.getSingleton().getVocabLists().keySet().iterator();
		while (keys.hasNext()) {
			String listName = keys.next();
			try {
				String fileName = String.format("resources/Vocab/%s.xml", listName);
				File file = new File(fileName);
				if (file.exists()) {
					decoder = new XMLDecoder(new FileInputStream(
							file));
					_vocabList.put( listName, (CodeTableRecordType[]) decoder.readObject());
				}
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
	}


} /* End of class: NemoLists */
