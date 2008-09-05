package fr.ifremer.nemo.domain;

import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.Element2Field;
import com.ago.xml.PathElementConsumer;
import com.ago.xml.PathElementHandler;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.SWTProcess;
import fr.ifremer.nemo.util.Global;

/**
 * Class Station
 * 
 * Classe permettant la description de l'entête Station
 * 
 * @author Gerard BUNEL
 */
public class Station implements Cloneable, PathElementHandler, SAXConsumer {
	/**
	 * Profondeur fond. MEDATLAS: bottom depth in metres (nothing if unknown) (6
	 * char max)
	 */
	protected float _bottomDepth;

	/**
	 * Commentaire multi-ligne, chaque ligne est limitee à 79 caracteres. Pour
	 * toutes les données et méta-données complémentaire comme la date et la
	 * position à la fin du profil, les observations météorologiques. Le nombre
	 * de lignes commentaire n'est pas limité
	 */
	protected String _comment;

	/**
	 * Date de début des mesures de la station. MEDATLAS: " DDMMYYYY"
	 */
	protected long _date;

	/**
	 * Flag qualite sur la date et l'heure. MEDATLAS: "QC flag on latitude" (1
	 * char)
	 */
	protected int _dateQC;

	/**
	 * 1ere ligne des "DC History" (79 char max). MEDATLAS: "method or
	 * instrument used when the data was collected" pour les informations liées
	 * à la collecte des données en mer (instrument, calibration, ...)
	 */
	protected String _dcHistory1;

	/**
	 * 2eme ligne des "DC History" (79 char max). MEDATLAS: "method or
	 * instrument used when the data was collected" pour les informations liées
	 * à la collecte des données en mer (instrument, calibration, ...)
	 */
	protected String _dcHistory2;

	/**
	 * Flag qualite sur la profondeur. MEDATLAS: "QC flag on bottom depth" (1
	 * char)
	 */
	protected int _depthQC;

	/**
	 * 1ere ligne des "Data Management History" (79 char max). pour les
	 * informations liées à la gestion de données et à l'archivage (comme, par
	 * exemple, la latitude d'origine, si elle a été changée au cours du
	 * contrôle qualité)
	 */
	protected String _dmHistory1;

	/**
	 * 2eme ligne des "Data Management History" (79 char max). pour les
	 * informations liées à la gestion de données et à l'archivage (comme, par
	 * exemple, la latitude d'origine, si elle a été changée au cours du
	 * contrôle qualité)
	 */
	protected String _dmHistory2;

	/**
	 * global quality flags for the NP parameter (MEDATLAS: NP char max)
	 */
	protected String _globalParametersQC;

	/**
	 * global quality flag for the profile (MEDATLAS: 1 char max)
	 */
	protected String _globalProfileQC;

	/**
	 * Latitude de départ de la station. MEDATLAS : NDD MM.mm
	 */
	protected double _latitude;

	/**
	 * Flag qualite sur la latitude. MEDATLAS: "QC flag on latitude" (1 char)
	 */
	protected int _latQC;

	/**
	 * Longitude de départ de la station. MEDATLAS : NDDD MM.mm
	 */
	protected double _longitude;

	/**
	 * Flag qualite sur la longitude. MEDATLAS: "QC flag on longitude" (1 char)
	 */
	protected int _longQC;

	/**
	 * Decalage horaire de la date. Ne correspond pas à une donnée MEDATLAS. Les
	 * heures MEDATLAS sont toujours fournies en referentiel GMT.
	 */
	protected float _offsetTU;

	/**
	 * MEDATLAS: cruise+station+cast (18 char max)
	 */
	protected String _reference;

	/**
	 * 1ere ligne des "Surface Samples" (79 char max)
	 */
	protected String _surfaceSamples1;

	/**
	 * 2eme ligne des "Surface Samples" (79 char max)
	 */
	protected String _surfaceSamples2;

	/**
	 * Heure de début des mesures MEDATLAS: 'HHMN' Les heures non renseignées
	 * doivent être égales à 9999 et le commentaire 'Heure inconnue' peut être
	 * ajouté après COMMENT.
	 */
	protected float _time;

	/**
	 * Type de donnee mesure par cette station. Il s'agit d'un d'un des
	 * dataTypes de la relation datatType sur Survey. Le dataType doit de plus
	 * correspondre à un capteur.
	 */
	protected DataType _dataType;

	/**
	 * Campagne a laquelle cette station est associee
	 */
	protected Survey _survey;

	/**
	 * Liste , ordonnee, des parametres mesures
	 */
	protected java.util.List<Parameter> _parameters;

	/**
	 * Information sur les séries temporelles. Optionnel, n'existe que si la
	 * campagne a concerne des donnees temporelles.
	 */
	protected TimeSeriesInformation _timeSeriesInformation;

	public Station() {
		_bottomDepth = Float.NaN;
		_comment = "";
		_dcHistory1 = "";
		_dcHistory2 = "";
		_dmHistory1 = "";
		_dmHistory2 = "";
		_globalParametersQC = "";
		_globalProfileQC = "0";
		_offsetTU = 0.0f;
		_reference = "";
		_surfaceSamples1 = "";
		_surfaceSamples2 = "";
		_time = Float.NaN;
		_dataType = null;
		_parameters = new ArrayList<Parameter>();
		_survey = null;
		_timeSeriesInformation = null;
	}

	/**
	 * 
	 * @param isTimeSeries
	 */
	public Station(boolean isTimeSeries) {
		this();
		if (isTimeSeries) {
			setTimeSeriesInformation(new TimeSeriesInformation());
		}
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
		Station tmp = new Station();
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
		String tmp;
		if ((tmp = a_Attributes.getValue("reference")) != null) {
			setReference(tmp);
		}
	}

	/**
	 * 
	 * A new XML Element as been found. Parse it.
	 * 
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
	public SAXConsumer consumeElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
		if (a_LocalName.equals("date")) {
			Element2Field setter = new Element2Field(this, "setDate");
			return setter;
		}
		if (a_LocalName.equals("time")) {
			Element2Field setter = new Element2Field(this, "setTime");
			return setter;
		}
		if (a_LocalName.equals("offsetTU")) {
			Element2Field setter = new Element2Field(this, "setOffsetTU");
			return setter;
		}
		if (a_LocalName.equals("latitude")) {
			Element2Field setter = new Element2Field(this, "setLatitude");
			return setter;
		}
		if (a_LocalName.equals("longitude")) {
			Element2Field setter = new Element2Field(this, "setLongitude");
			return setter;
		}
		if (a_LocalName.equals("bottomDepth")) {
			Element2Field setter = new Element2Field(this, "setBottomDepth");
			return setter;
		}
		if (a_LocalName.equals("dateQC")) {
			Element2Field setter = new Element2Field(this, "setDateQC");
			return setter;
		}
		if (a_LocalName.equals("latitudeQC")) {
			Element2Field setter = new Element2Field(this, "setLatQC");
			return setter;
		}
		if (a_LocalName.equals("longitudeQC")) {
			Element2Field setter = new Element2Field(this, "setLongQC");
			return setter;
		}
		if (a_LocalName.equals("depthQC")) {
			Element2Field setter = new Element2Field(this, "setDepthQC");
			return setter;
		}
		if (a_LocalName.equals("globalProfileQC")) {
			Element2Field setter = new Element2Field(this, "setGlobalProfileQC");
			return setter;
		}
		if (a_LocalName.equals("globalParametersQC")) {
			Element2Field setter = new Element2Field(this,
					"setGlobalParametersQC");
			return setter;
		}
		if (a_LocalName.equals("dcHistory1")) {
			Element2Field setter = new Element2Field(this, "setDcHistory1");
			return setter;
		}
		if (a_LocalName.equals("dcHistory2")) {
			Element2Field setter = new Element2Field(this, "setDcHistory2");
			return setter;
		}
		if (a_LocalName.equals("dmHistory1")) {
			Element2Field setter = new Element2Field(this, "setDmHistory1");
			return setter;
		}
		if (a_LocalName.equals("dmHistory2")) {
			Element2Field setter = new Element2Field(this, "setDmHistory2");
			return setter;
		}
		if (a_LocalName.equals("surfaceSample1")) {
			Element2Field setter = new Element2Field(this, "setSurfaceSamples1");
			return setter;
		}
		if (a_LocalName.equals("surfaceSample2")) {
			Element2Field setter = new Element2Field(this, "setSurfaceSamples2");
			return setter;
		}
		if (a_LocalName.equals("comment")) {
			Element2Field setter = new Element2Field(this, "setComment");
			return setter;
		}
		if (a_LocalName.equals("DataType")) {
			DataType l = (DataType) DataType.fromElement(a_URI, a_LocalName,
					a_QName, a_Attributes);
			setDataType(l);
			return (SAXConsumer) l;
		}
		if (a_LocalName.equals("parameters")) {
			return new PathElementConsumer("parameters", this);
		}
		if (a_LocalName.equals("TimeSeriesInformation")) {
			TimeSeriesInformation l = (TimeSeriesInformation) TimeSeriesInformation
					.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			setTimeSeriesInformation(l);
			return (SAXConsumer) l;
		}
		return null;
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
		if (path.equals("parameters")) {
			c = Parameter
					.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			addParameter((Parameter) c);
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
		a_Writer.putAttribute("reference", getReference());
		// a_Writer.putAttribute( "", getSortCriteria());
		a_Writer.putTag("date", getDate());
		a_Writer.putTag("time", getTime());
		a_Writer.putTag("offsetTU", getOffsetTU());
		a_Writer.putTag("latitude", getLatitude());
		a_Writer.putTag("longitude", getLongitude());
		a_Writer.putTag("bottomDepth", getBottomDepth());
		a_Writer.putTag("dateQC", getDateQC());
		a_Writer.putTag("latitudeQC", getLatQC());
		a_Writer.putTag("longitudeQC", getLongQC());
		a_Writer.putTag("depthQC", getDepthQC());
		a_Writer.putTag("globalProfileQC", getGlobalProfileQC());
		a_Writer.putTag("globalParametersQC", getGlobalParametersQC());
		a_Writer.putTag("dcHistory1", getDcHistory1());
		a_Writer.putTag("dcHistory2", getDcHistory2());
		a_Writer.putTag("dmHistory1", getDmHistory1());
		a_Writer.putTag("dmHistory2", getDmHistory2());
		a_Writer.putTag("surfaceSample1", getSurfaceSamples1());
		a_Writer.putTag("surfaceSample2", getSurfaceSamples2());
		a_Writer.putCDATATag("comment", getComment());
		if (getDataType() != null) {
			getDataType().writeXMLOn(a_Writer);
		}
		a_Writer.openTag("parameters");
		for (Parameter t : getParameters()) {
			t.writeXMLOn(a_Writer);
		}
		a_Writer.closeTag();
		if (getTimeSeriesInformation() != null) {
			getTimeSeriesInformation().writeXMLOn(a_Writer);
		}
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
		a_Writer.openTag("Station");
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
	public Object clone() throws CloneNotSupportedException {
		Station copy = null;
		copy = (Station) super.clone();
		copy._dataType = null;
		if (getDataType() != null) {
			copy.setDataType((DataType) getDataType().clone());
		}
		copy._survey = null;
		copy._parameters = new java.util.ArrayList<Parameter>();
		for (Parameter t0 : getParameters()) {
			copy.addParameter((Parameter) t0.clone());
		}
		copy._timeSeriesInformation = null;
		if (getTimeSeriesInformation() != null) {
			copy
					.setTimeSeriesInformation((TimeSeriesInformation) getTimeSeriesInformation()
							.clone());
		}
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
		releaseDataType();
		releaseParameters();
		releaseSurvey();
		releaseTimeSeriesInformation();
	}

	/**
	 * Read accessor (getter) for attribute _bottomDepth
	 * 
	 * @return float
	 * @see Station#_bottomDepth
	 */
	public float getBottomDepth() {
		return _bottomDepth;
	}

	/**
	 * Write accessor (setter) for attribute _bottomDepth
	 * 
	 * @param x
	 *            new value for _bottomDepth
	 * @see Station#_bottomDepth
	 */
	public void setBottomDepth(float x) {
		_bottomDepth = x;
	}

	/**
	 * Write accessor (setter) for attribute _bottomDepth
	 * 
	 * @param a_String
	 *            new value for _bottomDepth as String
	 * @see Station#_bottomDepth
	 */
	public void setBottomDepth(String a_String) {
		try {
			if (a_String.trim().equals("")) {
				setBottomDepth(Float.NaN);
			} else {
				setBottomDepth(Float.parseFloat(a_String));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read accessor (getter) for attribute _comment
	 * 
	 * @return String
	 * @see Station#_comment
	 */
	public String getComment() {
		return _comment;
	}

	/**
	 * Write accessor (setter) for attribute _comment
	 * 
	 * @param x
	 *            new value for _comment
	 * @see Station#_comment
	 */
	public void setComment(String x) {
		_comment = x;
	}

	/**
	 * Read accessor (getter) for attribute _date
	 * 
	 * @return long
	 * @see Station#_date
	 */
	public long getDate() {
		return _date;
	}

	/**
	 * Write accessor (setter) for attribute _date
	 * 
	 * @param x
	 *            new value for _date
	 * @see Station#_date
	 */
	public void setDate(long x) {
		_date = x;
	}

	/**
	 * Write accessor (setter) for attribute _date
	 * 
	 * @param a_String
	 *            new value for _date as String
	 * @see Station#_date
	 */
	public void setDate(String a_String) {
		try {
			setDate(Long.parseLong(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _dateQC
	 * 
	 * @return int
	 * @see Station#_dateQC
	 */
	public int getDateQC() {
		return _dateQC;
	}

	/**
	 * Write accessor (setter) for attribute _dateQC
	 * 
	 * @param x
	 *            new value for _dateQC
	 * @see Station#_dateQC
	 */
	public void setDateQC(int x) {
		_dateQC = x;
	}

	/**
	 * Write accessor (setter) for attribute _dateQC
	 * 
	 * @param a_String
	 *            new value for _dateQC as String
	 * @see Station#_dateQC
	 */
	public void setDateQC(String a_String) {
		try {
			setDateQC(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _dcHistory1
	 * 
	 * @return String
	 * @see Station#_dcHistory1
	 */
	public String getDcHistory1() {
		return _dcHistory1;
	}

	/**
	 * Write accessor (setter) for attribute _dcHistory1
	 * 
	 * @param x
	 *            new value for _dcHistory1
	 * @see Station#_dcHistory1
	 */
	public void setDcHistory1(String x) {
		_dcHistory1 = x;
	}

	/**
	 * Read accessor (getter) for attribute _dcHistory2
	 * 
	 * @return String
	 * @see Station#_dcHistory2
	 */
	public String getDcHistory2() {
		return _dcHistory2;
	}

	/**
	 * Write accessor (setter) for attribute _dcHistory2
	 * 
	 * @param x
	 *            new value for _dcHistory2
	 * @see Station#_dcHistory2
	 */
	public void setDcHistory2(String x) {
		_dcHistory2 = x;
	}

	/**
	 * Read accessor (getter) for attribute _depthQC
	 * 
	 * @return int
	 * @see Station#_depthQC
	 */
	public int getDepthQC() {
		return _depthQC;
	}

	/**
	 * Write accessor (setter) for attribute _depthQC
	 * 
	 * @param x
	 *            new value for _depthQC
	 * @see Station#_depthQC
	 */
	public void setDepthQC(int x) {
		_depthQC = x;
	}

	/**
	 * Write accessor (setter) for attribute _depthQC
	 * 
	 * @param a_String
	 *            new value for _depthQC as String
	 * @see Station#_depthQC
	 */
	public void setDepthQC(String a_String) {
		try {
			setDepthQC(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _dmHistory1
	 * 
	 * @return String
	 * @see Station#_dmHistory1
	 */
	public String getDmHistory1() {
		return _dmHistory1;
	}

	/**
	 * Write accessor (setter) for attribute _dmHistory1
	 * 
	 * @param x
	 *            new value for _dmHistory1
	 * @see Station#_dmHistory1
	 */
	public void setDmHistory1(String x) {
		_dmHistory1 = x;
	}

	/**
	 * Read accessor (getter) for attribute _dmHistory2
	 * 
	 * @return String
	 * @see Station#_dmHistory2
	 */
	public String getDmHistory2() {
		return _dmHistory2;
	}

	/**
	 * Write accessor (setter) for attribute _dmHistory2
	 * 
	 * @param x
	 *            new value for _dmHistory2
	 * @see Station#_dmHistory2
	 */
	public void setDmHistory2(String x) {
		_dmHistory2 = x;
	}

	/**
	 * Write accessor (setter) for attribute _globalParametersQC
	 * 
	 * @param x
	 *            new value for _globalParametersQC
	 * @see Station#_globalParametersQC
	 */
	public void setGlobalParametersQC(String x) {
		_globalParametersQC = x;
	}

	/**
	 * Read accessor (getter) for attribute _globalProfileQC
	 * 
	 * @return String
	 * @see Station#_globalProfileQC
	 */
	public String getGlobalProfileQC() {
		return _globalProfileQC;
	}

	/**
	 * Write accessor (setter) for attribute _globalProfileQC
	 * 
	 * @param x
	 *            new value for _globalProfileQC
	 * @see Station#_globalProfileQC
	 */
	public void setGlobalProfileQC(String x) {
		_globalProfileQC = x;
	}

	/**
	 * Read accessor (getter) for attribute _latitude
	 * 
	 * @return double
	 * @see Station#_latitude
	 */
	public double getLatitude() {
		return _latitude;
	}

	/**
	 * Write accessor (setter) for attribute _latitude
	 * 
	 * @param x
	 *            new value for _latitude
	 * @see Station#_latitude
	 */
	public void setLatitude(double x) {
		_latitude = x;
	}

	/**
	 * Write accessor (setter) for attribute _latitude
	 * 
	 * @param a_String
	 *            new value for _latitude as String
	 * @see Station#_latitude
	 */
	public void setLatitude(String a_String) {
		try {
			setLatitude(Double.parseDouble(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _latQC
	 * 
	 * @return int
	 * @see Station#_latQC
	 */
	public int getLatQC() {
		return _latQC;
	}

	/**
	 * Write accessor (setter) for attribute _latQC
	 * 
	 * @param x
	 *            new value for _latQC
	 * @see Station#_latQC
	 */
	public void setLatQC(int x) {
		_latQC = x;
	}

	/**
	 * Write accessor (setter) for attribute _latQC
	 * 
	 * @param a_String
	 *            new value for _latQC as String
	 * @see Station#_latQC
	 */
	public void setLatQC(String a_String) {
		try {
			setLatQC(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _longitude
	 * 
	 * @return double
	 * @see Station#_longitude
	 */
	public double getLongitude() {
		return _longitude;
	}

	/**
	 * Write accessor (setter) for attribute _longitude
	 * 
	 * @param x
	 *            new value for _longitude
	 * @see Station#_longitude
	 */
	public void setLongitude(double x) {
		_longitude = x;
	}

	/**
	 * Write accessor (setter) for attribute _longitude
	 * 
	 * @param a_String
	 *            new value for _longitude as String
	 * @see Station#_longitude
	 */
	public void setLongitude(String a_String) {
		try {
			setLongitude(Double.parseDouble(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _longQC
	 * 
	 * @return int
	 * @see Station#_longQC
	 */
	public int getLongQC() {
		return _longQC;
	}

	/**
	 * Write accessor (setter) for attribute _longQC
	 * 
	 * @param x
	 *            new value for _longQC
	 * @see Station#_longQC
	 */
	public void setLongQC(int x) {
		_longQC = x;
	}

	/**
	 * Write accessor (setter) for attribute _longQC
	 * 
	 * @param a_String
	 *            new value for _longQC as String
	 * @see Station#_longQC
	 */
	public void setLongQC(String a_String) {
		try {
			setLongQC(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _offsetTU
	 * 
	 * @return float
	 * @see Station#_offsetTU
	 */
	public float getOffsetTU() {
		return _offsetTU;
	}

	/**
	 * Write accessor (setter) for attribute _offsetTU
	 * 
	 * @param x
	 *            new value for _offsetTU
	 * @see Station#_offsetTU
	 */
	public void setOffsetTU(float x) {
		_offsetTU = x;
	}

	/**
	 * Write accessor (setter) for attribute _offsetTU
	 * 
	 * @param a_String
	 *            new value for _offsetTU as String
	 * @see Station#_offsetTU
	 */
	public void setOffsetTU(String a_String) {
		try {
			setOffsetTU(Float.parseFloat(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _reference
	 * 
	 * @return String
	 * @see Station#_reference
	 */
	public String getReference() {
		return _reference;
	}

	/**
	 * Write accessor (setter) for attribute _reference
	 * 
	 * @param x
	 *            new value for _reference
	 * @see Station#_reference
	 */
	public void setReference(String x) {
		_reference = x;
	}

	/**
	 * Read accessor (getter) for attribute _surfaceSamples1
	 * 
	 * @return String
	 * @see Station#_surfaceSamples1
	 */
	public String getSurfaceSamples1() {
		return _surfaceSamples1;
	}

	/**
	 * Write accessor (setter) for attribute _surfaceSamples1
	 * 
	 * @param x
	 *            new value for _surfaceSamples1
	 * @see Station#_surfaceSamples1
	 */
	public void setSurfaceSamples1(String x) {
		_surfaceSamples1 = x;
	}

	/**
	 * Read accessor (getter) for attribute _surfaceSamples2
	 * 
	 * @return String
	 * @see Station#_surfaceSamples2
	 */
	public String getSurfaceSamples2() {
		return _surfaceSamples2;
	}

	/**
	 * Write accessor (setter) for attribute _surfaceSamples2
	 * 
	 * @param x
	 *            new value for _surfaceSamples2
	 * @see Station#_surfaceSamples2
	 */
	public void setSurfaceSamples2(String x) {
		_surfaceSamples2 = x;
	}

	/**
	 * Read accessor (getter) for attribute _time
	 * 
	 * @return float
	 * @see Station#_time
	 */
	public float getTime() {
		return _time;
	}

	/**
	 * Write accessor (setter) for attribute _time
	 * 
	 * @param x
	 *            new value for _time
	 * @see Station#_time
	 */
	public void setTime(float x) {
		_time = x;
	}

	/**
	 * Write accessor (setter) for attribute _time
	 * 
	 * @param a_String
	 *            new value for _time as String
	 * @see Station#_time
	 */
	public void setTime(String a_String) {
		try {
			setTime(Float.parseFloat(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Remove any link for role dataType
	 */
	public void releaseDataType() {
		setDataType(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public DataType getDataType() {
		return _dataType;
	}

	/**
	 * Create a new link with x for role dataType
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setDataType(DataType x) {
		if (_dataType == x)
			return;
		_dataType = x;
	}

	/**
	 * Remove any link for role survey
	 */
	public void releaseSurvey() {
		setSurvey(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public Survey getSurvey() {
		return _survey;
	}

	/**
	 * Create a new link with x for role survey
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setSurvey(Survey x) {
		if (_survey == x)
			return;
		Survey old = _survey;
		if (_survey != null) {
			_survey = null;
			old.removeStation(this);
		}
		_survey = x;
		if (x != null) {
			x.addStation(this);
		}
	}

	/**
	 * release all links for role "parameters"
	 */
	public void releaseParameters() {
		for (Parameter t : getParameters()) {
			removeParameter(t);
		}
	}

	/**
	 * remove link with x for role "parameters"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public void removeParameter(Parameter x) {
		if (!parametersIncludes(x))
			return;
		nocheckRemoveParameter(x);
		x.setStation(null);
	}

	/**
	 * Add a link for role "parameters"
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void addParameter(Parameter x) {
		if (parametersIncludes(x))
			return;
		nocheckAddParameter(x);
		x.setStation(this);
	}

	/**
	 * Returns the collection of linked objects for role "parameters"
	 * 
	 * @return Array of linked objects
	 */
	public Parameter[] getParameters() {
		Parameter[] v = new Parameter[_parameters.size()];
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
	public Parameter parametersAt(int i) {
		return (Parameter) _parameters.get(i);
	}

	/**
	 * returns index of object x in "parameters"
	 * 
	 * @param x
	 *            the linked object
	 */
	public int parametersIndexOf(Parameter x) {
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
	 * returns index of parameter according to code
	 * 
	 * @param paramCode
	 *            the linked object
	 */
	public int parametersIndexOf(String paramCode) {
		int i;
		for (i = 0; i < parametersSize(); i++) {
			if (parametersAt(i).getCode().equals(paramCode)) {
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
	public void parametersMoveUp(Parameter x) {
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
	public void parametersMoveTop(Parameter x) {
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
	public void parametersMoveDown(Parameter x) {
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
	public void parametersMoveBottom(Parameter x) {
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
	public void nocheckMoveParameterAt(Parameter x, int a_Index) {
		int index;
		Parameter tmp;
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
	 * @see Station#removeParameter(Parameter)
	 */
	protected void nocheckRemoveParameter(Parameter x) {
		_parameters.remove(x);
	}

	/**
	 * adds x to the linked objects for role "parameters"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see Station#addParameter(Parameter)
	 */
	protected void nocheckAddParameter(Parameter x) {
		_parameters.add(x);
	}

	/**
	 * returns the number of linked objects for role "parameters"
	 * 
	 * @return the number of linked objects
	 */
	public int parametersSize() {
		return _parameters.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "parameters"
	 * 
	 * @param x
	 *            an instance of Parameter
	 */
	public boolean parametersIncludes(Parameter x) {
		return _parameters.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "parameters"
	 * 
	 */
	public java.util.Iterator<Parameter> parametersIterator() {
		return _parameters.iterator();

	}

	/**
	 * Remove any link for role timeSeriesInformation
	 */
	public void releaseTimeSeriesInformation() {
		setTimeSeriesInformation(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public TimeSeriesInformation getTimeSeriesInformation() {
		return _timeSeriesInformation;
	}

	/**
	 * Create a new link with x for role timeSeriesInformation
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setTimeSeriesInformation(TimeSeriesInformation x) {
		if (_timeSeriesInformation == x)
			return;
		TimeSeriesInformation old = _timeSeriesInformation;
		if (_timeSeriesInformation != null) {
			_timeSeriesInformation = null;
			old.setStation(null);
		}
		_timeSeriesInformation = x;
		if (x != null) {
			x.setStation(this);
		}
	}

	/**
	 * 
	 * @param o
	 * @return boolean
	 */
	public boolean equals(Object o) {
		return (o == this);
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
	 * @return float
	 */
	public float getCorrectedTime() {
		if (!Float.isNaN(_time)) {
			TimeZone tz = TimeZone.getTimeZone("GMT");
			Calendar cal = Calendar.getInstance(tz);
			cal.clear();
			Calendar cal2 = Calendar.getInstance(tz);
			cal2.clear();
			cal2.setTimeInMillis(new Float(_time).longValue());
			cal.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, cal2.get(Calendar.SECOND));
			return getGMTDate(cal.getTimeInMillis());
		} else {
			return _time;
		}
	}

	/**
	 * 
	 * @return String
	 */
	public String getDefaultGlobalParametersQC() {
		return getGlobalParametersQC().replace('0', '9');
	}

	/**
	 * 
	 * @return long
	 */
	public long getFullDate() {
		TimeZone tz = TimeZone.getTimeZone("GMT");
		Calendar cal = Calendar.getInstance(tz);
		cal.clear();
		cal.setTimeInMillis(_date);
		if (!Float.isNaN(_time)) {
			/*
			 * Calendar cal2 = Calendar.getInstance(tz); cal2.clear();
			 * cal2.setTimeInMillis(new Float(_time).longValue());
			 * cal.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
			 * cal.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
			 * cal.set(Calendar.SECOND, cal2.get(Calendar.SECOND));
			 */
			cal.setTimeInMillis(_date + (new Float(_time).longValue()));
		} else {
			cal.setTimeInMillis(_date);
		}
		return getGMTDate(cal.getTimeInMillis());
	}

	/**
	 * 
	 * @return String
	 */
	public String getGlobalParametersQC() {
		StringBuffer buffer = null;
		if (_timeSeriesInformation != null) {
			buffer = new StringBuffer("0000");
		} else {
			buffer = new StringBuffer();
		}
		for (int i = 0; i < parametersSize(); i++) {
			buffer.append('0');
		}
		return buffer.toString();
	}

	/**
	 * 
	 * @param a_Date
	 * @return long
	 */
	public long getGMTDate(long a_Date) {
		return a_Date + ((new Float(getOffsetTU())).longValue() * 3600 * 1000);
	}

	/**
	 * 
	 * @return int
	 */
	public int getParametersNumber() {
		if (getSurvey().isTimeSeriesSurvey()) {
			return parametersSize() + 4;
		} else {
			return parametersSize();
		}
	}

	/**
	 * 
	 * @return int
	 */
	public int getReferenceInteger() {
		try {
			return Integer.parseInt(_reference);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 
	 * @return String
	 */
	public String getSortCriteria() {
		return Long.toString(getFullDate()) + getReference();
	}

	/**
	 * Trie les données de la station par ordre croissant/décroissant
	 * 
	 * @param ascendant
	 */
	public void tri(boolean ascendant) {
		ArrayList<Integer> arlIndexes = new ArrayList<Integer>();
		TreeMap<Double, Integer> tmTri = new TreeMap<Double, Integer>();
		
		String paramCode = "";
		int paramIndex = -1;
		
		int i;

		// Choix du paramètre à trier en fonction du type de données en entrée et en sortie
		if (SWTProcess.odv) {
			if (StationModel.getSingleton().getInputType().equals(
					Global.TIMESERIES)) {
				paramCode = "";
			} else if (StationModel.getSingleton().getInputType().equals(
					Global.TRAJECTORY)) {
				paramCode = "";
			} else {
				paramCode = "PRES";
			}
		} else {
			if (StationModel.getSingleton().getInputType().equals(
					Global.TIMESERIES)) {
				paramCode = "";
			} else if (StationModel.getSingleton().getInputType().equals(
					Global.TRAJECTORY)) {
				paramCode = "";
			} else {
				paramCode = "PRES";
			}
		}

		// Ajout des binomes valeur/index dans un TreeMap pour tri sur la valeur
		if (paramCode.equals("")) {
			// Cas du tri par date
			// on utilise la date du 1er paramètre
			for (i = 0; i < parametersAt(0).valuesSize(); i++) {
				Long date = ((HorodatedValue) parametersAt(0).valuesAt(i)).getDate();
				tmTri.put(date.doubleValue(), i);
			}
		} else {
			// Recherche de l'index du paramètre en fonction du code
			paramIndex = parametersIndexOf(paramCode);
			
			if (paramIndex >= 0) {
				for (i = 0; i < parametersAt(paramIndex).valuesSize(); i++) {
					tmTri.put(parametersAt(paramIndex).valuesAt(i).getValue(), i);
				}
			} else {
				return;
			}
		}
		
		// Ajout des indexes triés dans une liste dans l'ordre croissant ou décroissant
		if (ascendant) {
			for (Entry<Double, Integer> currentEntry : tmTri.entrySet()) {
				arlIndexes.add(currentEntry.getValue());
			}
		} else {
			ArrayList<Integer> arrayTri = new ArrayList<Integer>(parametersAt(
					paramIndex).valuesSize());

			for (Entry<Double, Integer> currentEntry : tmTri.entrySet()) {
				arrayTri.add(currentEntry.getValue());
			}

			for (i = arrayTri.size() - 1; i >= 0; i++) {
				arlIndexes.add(arrayTri.get(i));
			}
		}
		
		// Tri de tous les paramètres de la station
		for (Parameter param : getParameters()) {
			param.triIndexes(arlIndexes);
		}
	}

} /* End of class: Station */
