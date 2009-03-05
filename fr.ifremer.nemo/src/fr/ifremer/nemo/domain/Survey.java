package fr.ifremer.nemo.domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import javax.xml.rpc.ServiceException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.Element2Field;
import com.ago.xml.PathElementConsumer;
import com.ago.xml.PathElementHandler;
import com.ago.xml.RootSAXConsumer;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLBootstrapParser;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nautilus.metadata.NautilusSurveyWS;
import fr.ifremer.nautilus.metadata.NautilusSurveyWSServiceLocator;
import fr.ifremer.nautilus.metadata.WSCollectedData;
import fr.ifremer.nautilus.metadata.WSSurvey;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.WSShip;

/**
 * Class Survey
 * 
 * Classe permettant la description de l'entete Campagne
 * 
 * @author Gerard BUNEL
 */
public class Survey implements Cloneable, PathElementHandler, RootSAXConsumer,
		Serializable {

	/**
	 * Taille du fichier de la campagne correspondant en Mo
	 * Utilise pour le resume CDI dans les cas de fichier multistations
	 */
	protected float _dataSize;
	
	/**
	 * Data Availability code (MEDATLAS: 1 char = P/L/C)
	 */
	protected Availability _availability;

	/**
	 * chief scientist full name (MEDATLAS: 40 char max)
	 */
	protected String _chiefScientist;

	/**
	 * Comment on the survey (multi-lines)
	 */
	protected String _comment;

	/**
	 * Source laboratory country code (MEDATLAS: 2 char max)
	 */
	protected String _countryCode;
	
	/**
	 * Source laboratory country name
	 */
	protected String _countryName;

	/**
	 * regional archiving centre code (MEDATLAS: 2 char max)
	 */
	protected String _dataCentre;

	/**
	 * MEDATLAS: DD/MM/YYYY
	 */
	protected long _endDate;

	/**
	 * Laboratory, institution, town (MEDATLAS: 75 char max)
	 */
	protected String _laboratories;

	/**
	 * originator cruise name/reference (MEDATLAS: 32 char max)
	 */
	protected String _name;

	/**
	 * name of the project (MEDATLAS: 28 char max)
	 */
	protected String _projectName;

	/**
	 * MEDATLAS cruise reference (MEDATLAS: 13 char max)
	 */
	protected String _reference;

	/**
	 * GF3 table (MEDATLAS: 35 char max)
	 */
	protected String _regionName;

	/**
	 * standardised WDCA/ICES/IOC code (MEDATLAS: 4 char max)
	 */
	protected String _shipCode;

	/**
	 * full ship name (MEDATLAS: 25 char max)
	 */
	protected String _shipName;

	/**
	 * MEDATLAS: DD/MM/YYYY
	 */
	protected long _startDate;

	/**
	 * Liste , ordonnee, des types de donnees
	 */
	protected java.util.List<DataType> _dataTypes;

	protected class StationsComparator implements Comparator<Station> {
		public int compare(Station o1, Station o2) {
			return o1.getSortCriteria().compareTo(o2.getSortCriteria());
		}
	}

	/**
	 * Liste , triees par date, des stations de la campagne
	 */
	protected java.util.Set<Station> _stations;

	public enum Availability {
		publicly, limited, confidential
	}

	public Survey() {
		initValues();
		_dataTypes = new java.util.ArrayList<DataType>();
		_stations = new java.util.TreeSet<Station>(new StationsComparator());
	}

	/**
	 * 
	 * Build object from its XML defintion
	 * 
	 * @param a_URI
	 *            Element Namespace (SAX 2)
	 * @param a_LocalName
	 *            Tag name
	 * @param a_QName
	 *            qualified tag name
	 * @param a_Attributes
	 *            XML Element attributes
	 * @return SAXConsumer
	 * 
	 */
	public static SAXConsumer fromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
		Survey tmp = new Survey();
		tmp.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		return (SAXConsumer) tmp;
	}

	/**
	 * 
	 * Retrieve attributes values from their XML defintion
	 * 
	 * @param a_URI
	 *            Element Namespace (SAX 2)
	 * @param a_LocalName
	 *            Tag name
	 * @param a_QName
	 *            qualified tag name
	 * @param a_Attributes
	 *            XML Element attributes
	 * 
	 */
	public void buildFromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
	}

	/**
	 * 
	 * The XML Element is a CDATA filled element
	 * 
	 * @param a_Characters
	 *            The CDATA content
	 * 
	 */
	public void consumeCharacters(String a_Characters) throws SAXException {
	}

	/**
	 * 
	 * The XML Element is a CDATA filled element
	 * 
	 * @param path
	 *            the association path name
	 * @param a_URI
	 *            XML Namespace (SAX 2)
	 * @param a_LocalName
	 *            Tag name
	 * @param a_QName
	 *            Qualified tag name
	 * @param a_Attributes
	 *            XML Element attributes
	 * @return SAXConsumer
	 * 
	 */
	public SAXConsumer handlePathElement(String path, String a_URI,
			String a_LocalName, String a_QName, Attributes a_Attributes)
			throws SAXException {
		SAXConsumer c = null;
		if (path.equals("dataTypes")) {
			c = DataType.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			addDataType((DataType) c);
			return c;
		}
		if (path.equals("stations")) {
			c = Station.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			addStation((Station) c);
			return c;
		}
		return null;
	}

	/**
	 * 
	 * Generate XML defintion on a Stream
	 * 
	 * @param a_Writer
	 *            XMLOutputWriter to which to write the XML Definition
	 * 
	 */
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		a_Writer.putTag("reference", getReference());
		a_Writer.putTag("name", getName());
		a_Writer.putTag("shipCode", getShipCode());
		a_Writer.putTag("shipName", getShipName());
		a_Writer.putTag("startDate", getStartDate());
		a_Writer.putTag("endDate", getEndDate());
		a_Writer.putTag("regionName", getRegionName());
		a_Writer.putTag("country", getCountryCode());
		a_Writer.putTag("countryName", getCountryName());
		a_Writer.putTag("laboratories", getLaboratories());
		a_Writer.putTag("chiefScientist", getChiefScientist());
		a_Writer.putTag("projectName", getProjectName());
		a_Writer.putTag("dataCentre", getDataCentre());
		a_Writer.putTag("availability", getAvailability().toString());
		a_Writer.putCDATATag("comment", getComment());
		a_Writer.openTag("dataTypes");
		for (DataType t : getDataTypes()) {
			t.writeXMLOn(a_Writer);
		}
		a_Writer.closeTag();
		a_Writer.openTag("stations");
//		for (Station t : getStations()) {
//			t.writeXMLOn(a_Writer);
//		}
		a_Writer.closeTag();
	}

	/**
	 * 
	 * Generate XML defintion on a XMLOutputWriter
	 * 
	 * @param a_Writer
	 *            the XMLOutputWriter to which to write the XML Definition
	 * 
	 */
	public void writeXMLOn(XMLOutputWriter a_Writer) {
		a_Writer.openTag("Survey");
		writeXMLBodyOn(a_Writer);
		a_Writer.closeTag();
	}

	/**
	 * 
	 * Generate XML defintion on a Stream
	 * 
	 * @param a_Stream
	 *            the OutptStream to which to write the XML Definition
	 * 
	 */
	public void writeXMLOn(OutputStream a_Stream) {
		XMLOutputWriter writer = new XMLOutputWriter(a_Stream);
		writeXMLOn(writer);
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	@Override
    public Object clone() throws CloneNotSupportedException {
		Survey copy = null;
		copy = (Survey) super.clone();
		copy._dataTypes = new java.util.ArrayList<DataType>();
		for (DataType t0 : getDataTypes()) {
			copy.addDataType((DataType) t0.clone());
		}
		copy._stations = new java.util.TreeSet<Station>(new StationsComparator());
		for (Station t1 : getStations()) {
			copy.addStation((Station) t1.clone());
		}
		return copy;
	}
    
    public void initValues(){
        _dataSize = 0;
        _availability = Availability.publicly;
        _chiefScientist = "UNKNOWN"; //$NON-NLS-1$
        _comment = Global.EMPTY_STRING;
        _countryCode = Global.EMPTY_STRING;
        _countryName = Global.EMPTY_STRING;
        _dataCentre = Global.EMPTY_STRING;
        _endDate = System.currentTimeMillis();
        _laboratories = Global.EMPTY_STRING;
        _name = Global.EMPTY_STRING;
        _projectName = Global.EMPTY_STRING;
        _reference = Global.EMPTY_STRING;
        _regionName = Global.EMPTY_STRING;
        _shipCode = "XXXX"; //$NON-NLS-1$
        _shipName = "UNKNOWN"; //$NON-NLS-1$
        _startDate = System.currentTimeMillis();
    }
    
    public void reset(){
        initValues();
        release();
    }

	/**
	 * Remove any associated links
	 */
	public void release() {
		releaseDataTypes();
		releaseStations();
	}

	/**
	 * Read accessor (getter) for attribute _availability
	 * 
	 * @return Availability
	 * @see Survey#_availability
	 */
	public Availability getAvailability() {
		return _availability;
	}

	/**
	 * Write accessor (setter) for attribute _availability
	 * 
	 * @param x
	 *            new value for _availability
	 * @see Survey#_availability
	 */
	public void setAvailability(Availability x) {
		_availability = x;
	}

	/**
	 * Write accessor (setter) for attribute _availability
	 * 
	 * @param a_String
	 *            new value for _availability as String
	 * @see Survey#_availability
	 */
	public void setAvailability(String a_String) {
		try {
			setAvailability(Enum.valueOf(Availability.class, a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _chiefScientist
	 * 
	 * @return String
	 * @see Survey#_chiefScientist
	 */
	public String getChiefScientist() {
		return _chiefScientist;
	}

	/**
	 * Write accessor (setter) for attribute _chiefScientist
	 * 
	 * @param x
	 *            new value for _chiefScientist
	 * @see Survey#_chiefScientist
	 */
	public void setChiefScientist(String x) {
		_chiefScientist = x;
	}

	/**
	 * Read accessor (getter) for attribute _comment
	 * 
	 * @return String
	 * @see Survey#_comment
	 */
	public String getComment() {
		return _comment;
	}

	/**
	 * Write accessor (setter) for attribute _comment
	 * 
	 * @param x
	 *            new value for _comment
	 * @see Survey#_comment
	 */
	public void setComment(String x) {
		_comment = x;
	}

	/**
	 * Read accessor (getter) for attribute _countryCode
	 * 
	 * @return String
	 * @see Survey#_countryCode
	 */
	public String getCountryCode() {
		return _countryCode;
	}

	/**
	 * Write accessor (setter) for attribute _countryCode
	 * 
	 * @param x
	 *            new value for _countryCode
	 * @see Survey#_countryCode
	 */
	public void setCountryCode(String x) {
		_countryCode = x;
	}
	
	/**
	 * Read accessor (getter) for attribute _countryName
	 * 
	 * @return String
	 * @see Survey#_countryName
	 */
	public String getCountryName() {
		return _countryName;
	}

	/**
	 * Read accessor (getter) for attribute _dataCentre
	 * 
	 * @return String
	 * @see Survey#_dataCentre
	 */
	public String getDataCentre() {
		return _dataCentre;
	}

	/**
	 * Write accessor (setter) for attribute _dataCentre
	 * 
	 * @param x
	 *            new value for _dataCentre
	 * @see Survey#_dataCentre
	 */
	public void setDataCentre(String x) {
		_dataCentre = x;
	}

	/**
	 * Read accessor (getter) for attribute _endDate
	 * 
	 * @return long
	 * @see Survey#_endDate
	 */
	public long getEndDate() {
		return _endDate;
	}

	/**
	 * Write accessor (setter) for attribute _endDate
	 * 
	 * @param x
	 *            new value for _endDate
	 * @see Survey#_endDate
	 */
	public void setEndDate(long x) {
		_endDate = x;
	}

	/**
	 * Write accessor (setter) for attribute _endDate
	 * 
	 * @param a_String
	 *            new value for _endDate as String
	 * @see Survey#_endDate
	 */
	public void setEndDate(String a_String) {
		try {
			setEndDate(Long.parseLong(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _laboratories
	 * 
	 * @return String
	 * @see Survey#_laboratories
	 */
	public String getLaboratories() {
		return _laboratories;
	}

	/**
	 * Write accessor (setter) for attribute _laboratories
	 * 
	 * @param x
	 *            new value for _laboratories
	 * @see Survey#_laboratories
	 */
	public void setLaboratories(String x) {
		if (x.length() > 75)
			_laboratories = x.substring(0, 75);
		else
			_laboratories = x;

	}

	/**
	 * Read accessor (getter) for attribute _name
	 * 
	 * @return String
	 * @see Survey#_name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Write accessor (setter) for attribute _name
	 * 
	 * @param x
	 *            new value for _name
	 * @see Survey#_name
	 */
	public void setName(String x) {
		_name = x;
	}

	/**
	 * Read accessor (getter) for attribute _projectName
	 * 
	 * @return String
	 * @see Survey#_projectName
	 */
	public String getProjectName() {
		return _projectName;
	}

	/**
	 * Write accessor (setter) for attribute _projectName
	 * 
	 * @param x
	 *            new value for _projectName
	 * @see Survey#_projectName
	 */
	public void setProjectName(String x) {
		_projectName = x;
	}

	/**
	 * Read accessor (getter) for attribute _reference
	 * 
	 * @return String
	 * @see Survey#_reference
	 */
	public String getReference() {
		return _reference;
	}

	/**
	 * Write accessor (setter) for attribute _reference
	 * 
	 * @param x
	 *            new value for _reference
	 * @see Survey#_reference
	 */
	public void setReference(String x) {
		_reference = x;
	}

	/**
	 * Read accessor (getter) for attribute _regionName
	 * 
	 * @return String
	 * @see Survey#_regionName
	 */
	public String getRegionName() {
		return _regionName;
	}

	/**
	 * Write accessor (setter) for attribute _regionName
	 * 
	 * @param x
	 *            new value for _regionName
	 * @see Survey#_regionName
	 */
	public void setRegionName(String x) {
		_regionName = x;
	}

	/**
	 * Read accessor (getter) for attribute _shipCode
	 * 
	 * @return String
	 * @see Survey#_shipCode
	 */
	public String getShipCode() {
		return _shipCode;
	}

	/**
	 * Write accessor (setter) for attribute _shipCode
	 * 
	 * @param x
	 *            new value for _shipCode
	 * @see Survey#_shipCode
	 */
	public void setShipCode(String x) {
		_shipCode = x;
	}

	/**
	 * Read accessor (getter) for attribute _shipName
	 * 
	 * @return String
	 * @see Survey#_shipName
	 */
	public String getShipName() {
		return _shipName;
	}

	/**
	 * Write accessor (setter) for attribute _shipName
	 * 
	 * @param x
	 *            new value for _shipName
	 * @see Survey#_shipName
	 */
	public void setShipName(String x) {
		_shipName = x;
	}

	/**
	 * Read accessor (getter) for attribute _startDate
	 * 
	 * @return long
	 * @see Survey#_startDate
	 */
	public long getStartDate() {
		return _startDate;
	}

	/**
	 * Write accessor (setter) for attribute _startDate
	 * 
	 * @param x
	 *            new value for _startDate
	 * @see Survey#_startDate
	 */
	public void setStartDate(long x) {
		_startDate = x;
	}

	/**
	 * Write accessor (setter) for attribute _startDate
	 * 
	 * @param a_String
	 *            new value for _startDate as String
	 * @see Survey#_startDate
	 */
	public void setStartDate(String a_String) {
		try {
			setStartDate(Long.parseLong(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * release all links for role "dataTypes"
	 */
	public void releaseDataTypes() {
		for (DataType t : getDataTypes()) {
			removeDataType(t);
		}
	}

	/**
	 * remove link with x for role "dataTypes"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public void removeDataType(DataType x) {
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
	public void addDataType(DataType x) {
		if (dataTypesIncludes(x))
			return;
		nocheckAddDataType(x);
	}

	/**
	 * Returns the collection of linked objects for role "dataTypes"
	 * 
	 * @return Array of linked objects
	 */
	public DataType[] getDataTypes() {
		DataType[] v = new DataType[_dataTypes.size()];
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
	public DataType dataTypesAt(int i) {
		return (DataType) _dataTypes.get(i);
	}

	/**
	 * returns index of object x in "dataTypes"
	 * 
	 * @param x
	 *            the linked object
	 */
	public int dataTypesIndexOf(DataType x) {
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
	public void dataTypesMoveUp(DataType x) {
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
	public void dataTypesMoveTop(DataType x) {
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
	public void dataTypesMoveDown(DataType x) {
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
	public void dataTypesMoveBottom(DataType x) {
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
	public void nocheckMoveDataTypeAt(DataType x, int a_Index) {
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
	 * @param x
	 *            the object to link with
	 * @see Survey#removeDataType(DataType)
	 */
	protected void nocheckRemoveDataType(DataType x) {
		_dataTypes.remove(x);
	}

	/**
	 * adds x to the linked objects for role "dataTypes"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see Survey#addDataType(DataType)
	 */
	protected void nocheckAddDataType(DataType x) {
		_dataTypes.add(x);
	}

	/**
	 * returns the number of linked objects for role "dataTypes"
	 * 
	 * @return the number of linked objects
	 */
	public int dataTypesSize() {
		return _dataTypes.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "dataTypes"
	 * 
	 * @param x
	 *            an instance of DataType
	 */
	public boolean dataTypesIncludes(DataType x) {
		return _dataTypes.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "dataTypes"
	 * 
	 */
	public java.util.Iterator<DataType> dataTypesIterator() {
		return _dataTypes.iterator();

	}

	/**
	 * release all links for role "stations"
	 */
	public void releaseStations() {
		for (Station t : getStations()) {
			removeStation(t);
		}
	}

	/**
	 * remove link with x for role "stations"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public void removeStation(Station x) {
		if (!stationsIncludes(x))
			return;
		nocheckRemoveStation(x);
		x.setSurvey(null);
	}

	/**
	 * Add a link for role "stations"
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void addStation(Station x) {
		if (stationsIncludes(x))
			return;
		nocheckAddStation(x);
		x.setSurvey(this);
	}

	/**
	 * Returns the collection of linked objects for role "stations"
	 * 
	 * @return Array of linked objects
	 */
	public Station[] getStations() {
		Station[] v = new Station[_stations.size()];
		_stations.toArray(v);
		return v;
	}

	/**
	 * remove x from the linked objects for role "stations"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see Survey#removeStation(Station)
	 */
	protected void nocheckRemoveStation(Station x) {
		_stations.remove(x);
	}

	/**
	 * adds x to the linked objects for role "stations"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see Survey#addStation(Station)
	 */
	protected void nocheckAddStation(Station x) {
		_stations.add(x);
	}

	/**
	 * returns the number of linked objects for role "stations"
	 * 
	 * @return the number of linked objects
	 */
	public int stationsSize() {
		return _stations.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "stations"
	 * 
	 * @param x
	 *            an instance of Station
	 */
	public boolean stationsIncludes(Station x) {
		return _stations.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "stations"
	 * 
	 */
	public java.util.Iterator<Station> stationsIterator() {
		return _stations.iterator();

	}

	/**
	 * 
	 * @param a_URI
	 * @param a_LocalName
	 * @param a_QName
	 * @param a_Attributes
	 * @return SAXConsumer
	 */
	public SAXConsumer consumeElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) {
		if (a_LocalName.equals("reference")) {
			Element2Field setter = new Element2Field(this, "setReference");
			return setter;
		}
		if (a_LocalName.equals("name")) {
			Element2Field setter = new Element2Field(this, "setName");
			return setter;
		}
		if (a_LocalName.equals("shipCode")) {
			Element2Field setter = new Element2Field(this, "setShipCode");
			return setter;
		}
		if (a_LocalName.equals("shipName")) {
			Element2Field setter = new Element2Field(this, "setShipName");
			return setter;
		}
		if (a_LocalName.equals("startDate")) {
			Element2Field setter = new Element2Field(this, "setStartDate");
			return setter;
		}
		if (a_LocalName.equals("endDate")) {
			Element2Field setter = new Element2Field(this, "setEndDate");
			return setter;
		}
		if (a_LocalName.equals("formattedStartDate")) {
			Element2Field setter = new Element2Field(this,
					"setFormattedStartDate");
			return setter;
		}
		if (a_LocalName.equals("formattedEndDate")) {
			Element2Field setter = new Element2Field(this,
					"setFormattedEndDate");
			return setter;
		}
		if (a_LocalName.equals("regionName")) {
			Element2Field setter = new Element2Field(this, "setRegionName");
			return setter;
		}
		if (a_LocalName.equals("country")) {
			Element2Field setter = new Element2Field(this, "setCountryCode");
			return setter;
		}
		if (a_LocalName.equals("countryName")) {
			Element2Field setter = new Element2Field(this, "setCountryName");
			return setter;
		}
		if (a_LocalName.equals("laboratories")) {
			Element2Field setter = new Element2Field(this, "setLaboratories");
			return setter;
		}
		if (a_LocalName.equals("chiefScientist")) {
			Element2Field setter = new Element2Field(this, "setChiefScientist");
			return setter;
		}
		if (a_LocalName.equals("projectName")) {
			Element2Field setter = new Element2Field(this, "setProjectName");
			return setter;
		}
		if (a_LocalName.equals("dataCentre")) {
			Element2Field setter = new Element2Field(this, "setDataCentre");
			return setter;
		}
		if (a_LocalName.equals("availability")) {
			Element2Field setter = new Element2Field(this, "setAvailability");
			return setter;
		}
		if (a_LocalName.equals("comment")) {
			Element2Field setter = new Element2Field(this, "setComment");
			return setter;
		}
		if (a_LocalName.equals("dataTypes")) {
			return new PathElementConsumer("dataTypes", this);
		}
		return null;
	}

	/**
	 * 
	 * @param a_Reference
	 *            reference de campagne
	 * @exception ServiceException
	 * @exception MalformedURLException
	 * @exception java.rmi.RemoteException
	 */
	public void fillFromSISMER(String a_Reference) throws ServiceException,
			MalformedURLException, RemoteException {
		NautilusSurveyWS service = new NautilusSurveyWSServiceLocator()
				.getNAUTILUS(new java.net.URL(Utils.configuration
						.getNautilusWebServiceURL()));
		WSSurvey wssurvey = service.getSurvey(a_Reference);
		if (wssurvey.getReference() != null) {

			setReference(wssurvey.getReference());
		} else {
			setReference("");
		}
		if (wssurvey.getName() != null) {
			setName(wssurvey.getName());
		} else {
			setName("");
		}
		if (wssurvey.getShipCode() != null) {
			setShipCode(wssurvey.getShipCode());
		} else {
			setShipCode("");
		}
		if (wssurvey.getShipName() != null) {
			setShipName(wssurvey.getShipName());
		} else {
			setShipName("");
		}
		setStartDate(wssurvey.getStartDate());
		setEndDate(wssurvey.getEndDate());
		if (wssurvey.getRegionName() != null) {
			setRegionName(wssurvey.getRegionName());
		} else {
			setRegionName("");
		}
		if (wssurvey.getCountryCode() != null) {
			setCountryCode(wssurvey.getCountryCode());
		} else {
			setCountryCode("");
		}
//		if (wssurvey.getCountryName() != null) {
//			setCountryName(wssurvey.getCountryName());
//		} else {
//			setCountryName("");
//		}
		if (wssurvey.getLaboratories() != null) {
			setLaboratories(wssurvey.getLaboratories());
		} else {
			setLaboratories("");
		}
		if (wssurvey.getChiefScientist() != null) {
			setChiefScientist(wssurvey.getChiefScientist());
		} else {
			setChiefScientist("");
		}
		if (wssurvey.getProjectName() != null) {
			setProjectName(wssurvey.getProjectName());
		} else {
			setProjectName("");
		}
		if (wssurvey.getDataCentre() != null) {
			setDataCentre(wssurvey.getDataCentre());
		} else {
			setDataCentre("");
		}
		setAvailability(Availability.publicly);
		setComment("");

		WSCollectedData[] datas = wssurvey.getDataTypes();
		releaseDataTypes();
		if (datas != null) {
			for (int i = 0; i < datas.length; i++) {
				WSCollectedData data = datas[i];
				DataType dataType = new DataType();
				dataType.setCode(data.getCode());
				dataType.setNumberOfProfiles(data.getObservationNumber());
				dataType.setSensor(data.isSensor());
				addDataType(dataType);
			}
		}
	}

	/**
	 * 
	 * @return String
	 */
	public String getAvailabilityCode() {
		switch (_availability) {
		case publicly: {
			return "P";
		}
		case limited: {
			return "L";
		}
		case confidential: {
			return "C";
		}
		}
		return "";
	}

	/**
	 * 
	 * @return String[]
	 */
	public String[] getCommentLines() {
		LineNumberReader reader = new LineNumberReader(new StringReader(
				getComment()));
		String line = null;
		java.util.Vector<String> lines = new java.util.Vector<String>();
		try {
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (Exception e) {
		} finally {
			try {
				reader.close();
			} catch (Exception ex) {
			}
		}
		String[] lineArray = new String[lines.size()];
		return lines.toArray(lineArray);
	}

	/**
	 * 
	 * @return String
	 * @exception ParseException
	 */
	public String getFormattedEndDate() throws ParseException {
		Formatter fmt = Formatter.getSingleton();
		return fmt.formatDateTime("yyyy-MM-dd", getEndDate());
	}

	/**
	 * 
	 * @return String
	 * @exception ParseException
	 */
	public String getFormattedStartDate() throws ParseException {
		Formatter fmt = Formatter.getSingleton();
		return fmt.formatDateTime("yyyy-MM-dd", getStartDate());
	}

	/**
	 * 
	 * @return int
	 */
	public int getValuesCount() {
		int count = 0;
		for (Station s : _stations) {
			count += s.parametersAt(0).valuesSize();
		}
		return count;
	}

	/**
	 * 
	 * @param xmlFile
	 * @exception SAXException
	 * @exception TransformerException
	 * @exception java.io.FileNotFoundException
	 */
	public void importFromXML(File xmlFile) throws SAXException,
			TransformerException, FileNotFoundException {
		/*
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setFeature("http://xml.org/sax/features/validation", false);
		reader.setFeature("http://xml.org/sax/features/namespaces", true);
		reader.setFeature("http://xml.org/sax/features/namespace-prefixes",
				false);

		Source source = new SAXSource(reader, new InputSource(new FileReader(
				xmlFile)));
		
		XMLBootstrapParser parser = new XMLBootstrapParser(this);
		Result result = new SAXResult(parser);
		java.io.InputStream inputStream = new FileInputStream(
				"resources/survey.xsl");
		getClass().getResourceAsStream("/resources/survey.xsl");
		Source style = new StreamSource(new InputStreamReader(inputStream));

		if (checkSchema(source)) {
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer trans = transFactory.newTransformer(style);
			source = new SAXSource(reader, new InputSource(new FileReader(xmlFile)));
			trans.transform(source, result);
		}
		*/
		
		StreamSource xmlSource = new StreamSource(xmlFile);

		StreamSource xslSource = new StreamSource(new File("resources/survey.xsl"));
		
		XMLBootstrapParser parser = new XMLBootstrapParser(this);
		Result result = new SAXResult(parser);
		
		if (checkSchema(xmlSource))	{
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(xslSource);
			transformer.transform(xmlSource,result);
		}
		
		
	}

	private Boolean checkSchema(Source xmlFile) throws SAXException {

		// 1. Lookup a factory for the W3C XML Schema language
		SchemaFactory factory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");

		// 2. Compile the schema.
		// Here the schema is loaded from a java.io.File, but you could use
		// a java.net.URL or a javax.xml.transform.Source instead.
		File schemaLocation = new File("resources/CSR_sdn_V1.3.xsd");
		Schema schema = factory.newSchema(schemaLocation);

		// 3. Get a validator from the schema.
		Validator validator = schema.newValidator();

		// 4. Check the document
		try {
			validator.validate(xmlFile);
			return true;

		} catch (SAXException ex) {
			Utils.messageErreurAppend(Messages.getString("EditSurvey.72"));
			Utils.messageErreurAppend(ex.getMessage());
			throw ex;
		} catch (IOException ex) {
			return false;
		}

	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean isTimeSeriesSurvey() {
		if (stationsSize() > 0) {
			return (getStations()[0].getTimeSeriesInformation() != null);
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Survey survey = new Survey();
			survey.importFromXML(new File(args[0]));
			survey.writeXMLOn(System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param a_EndDate
	 * @exception ParseException
	 */
	public void setFormattedEndDate(String a_EndDate) throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date value = fmt.parse(a_EndDate);
		setEndDate(value.getTime());
	}

	/**
	 * 
	 * @param a_StartDate
	 * @exception ParseException
	 */
	public void setFormattedStartDate(String a_StartDate) throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date value = fmt.parse(a_StartDate);
		setStartDate(value.getTime());
	}

	/**
	 * 
	 * @param a_WSShip
	 */
	public void setShip(WSShip a_WSShip) {
		setShipCode(a_WSShip.getCode());
		setShipName(a_WSShip.getName());
	}

	public float getDataSize() {
		return _dataSize;
	}

	public void setDataSize(float size) {
		_dataSize = size;
	}

    /**
     * Setter sur _countryName.
     * @param countryName the countryName to set
     */
    public void setCountryName(String countryName)
    {
        _countryName = countryName;
    }
} /* End of class: Survey */
