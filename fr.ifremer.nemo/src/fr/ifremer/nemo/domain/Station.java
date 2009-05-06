package fr.ifremer.nemo.domain;

import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.Element2Field;
import com.ago.xml.PathElementHandler;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.domain.AddedParameter.IdParameter;
import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.ui.edit.conversion.EditConversion;
import fr.ifremer.nemo.ui.view.ViewFile;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.format.DateTimeFormat;
import fr.ifremer.nemo.util.format.ExternalizableFormat;
import fr.ifremer.nemo.util.format.LatitudeFormat;
import fr.ifremer.nemo.util.format.LongitudeFormat;

/**
 * Class Station
 * 
 * Classe permettant la description de l'entete Station
 * 
 * @author Gerard BUNEL
 */
public class Station implements Cloneable, PathElementHandler, SAXConsumer {
    /**
     * Taille du fichier correspondant en Mo
     * Utilise pour le resume CDI dans le cas des fichiers monostation
     */
    protected float _dataSize;
    
	/**
	 * Profondeur fond. MEDATLAS: bottom depth in metres (nothing if unknown) (6
	 * char max)
	 */
	protected float _bottomDepth;

	/**
	 * Commentaire multi-ligne, chaque ligne est limitee e 79 caracteres. Pour
	 * toutes les donnees et meta-donnees complementaire comme la date et la
	 * position e la fin du profil, les observations meteorologiques. Le nombre
	 * de lignes commentaire n'est pas limite
	 */
	protected String _comment;

	/**
	 * Date de debut des mesures de la station. MEDATLAS: " DDMMYYYY"
	 */
	protected long _date;

	/**
	 * Flag qualite sur la date et l'heure. MEDATLAS: "QC flag on latitude" (1
	 * char)
	 */
	protected int _dateQC;

	/**
	 * 1ere ligne des "DC History" (79 char max). MEDATLAS: "method or
	 * instrument used when the data was collected" pour les informations liees
	 * e la collecte des donnees en mer (instrument, calibration, ...)
	 */
	protected String _dcHistory1;

	/**
	 * 2eme ligne des "DC History" (79 char max). MEDATLAS: "method or
	 * instrument used when the data was collected" pour les informations liees
	 * e la collecte des donnees en mer (instrument, calibration, ...)
	 */
	protected String _dcHistory2;

	/**
	 * Flag qualite sur la profondeur. MEDATLAS: "QC flag on bottom depth" (1
	 * char)
	 */
	protected int _depthQC;

	/**
	 * 1ere ligne des "Data Management History" (79 char max). pour les
	 * informations liees e la gestion de donnees et e l'archivage (comme, par
	 * exemple, la latitude d'origine, si elle a ete changee au cours du
	 * contrele qualite)
	 */
	protected String _dmHistory1;

	/**
	 * 2eme ligne des "Data Management History" (79 char max). pour les
	 * informations liees e la gestion de donnees et e l'archivage (comme, par
	 * exemple, la latitude d'origine, si elle a ete changee au cours du
	 * contrele qualite)
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
	 * Latitude de depart de la station. MEDATLAS : NDD MM.mm
	 */
	protected double _latitude;

	/**
	 * Flag qualite sur la latitude. MEDATLAS: "QC flag on latitude" (1 char)
	 */
	protected int _latQC;

	/**
	 * Longitude de depart de la station. MEDATLAS : NDDD MM.mm
	 */
	protected double _longitude;

	/**
	 * Flag qualite sur la longitude. MEDATLAS: "QC flag on longitude" (1 char)
	 */
	protected int _longQC;

	/**
	 * Decalage horaire de la date. Ne correspond pas e une donnee MEDATLAS. Les
	 * heures MEDATLAS sont toujours fournies en referentiel GMT.
	 */
	protected float _offsetTU;

	/**
	 * MEDATLAS: numero de station (5 char max)
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
	 * Heure de debut des mesures MEDATLAS: 'HHMN' Les heures non renseignees
	 * doivent etre egales e 9999 et le commentaire 'Heure inconnue' peut etre
	 * ajoute apres COMMENT.
	 */
	protected float _time;

	/**
	 * Type de donnee mesure par cette station. Il s'agit d'un d'un des
	 * dataTypes de la relation datatType sur Survey. Le dataType doit de plus
	 * correspondre e un capteur.
	 */
	protected DataType _dataType;

	/**
	 * Campagne a laquelle cette station est associee
	 */
	protected Survey _survey;

//	/**
//	 * Liste , ordonnee, des parametres mesures
//	 */
//	protected java.util.List<Parameter> _parameters;
//
//    /** 
//     * Liste, ordonnee, des paramètres rajoutés pour ODV
//     */
//    protected java.util.List<AddedParameter> _odvparameters;
//
//    /** 
//     * Liste, ordonnee, des paramètres rajoutés pour Medatlas
//     */
//    protected java.util.List<AddedParameter> _medparameters;

    
    

	/**
	 * Liste , ordonnee, des parametres mesures
	 */
	protected List<ParameterModel> _parameters;

    /** 
     * Liste, ordonnee, des paramètres rajoutés pour ODV
     */
    protected List<ParameterModel> _odvparameters;

    /** 
     * Liste, ordonnee, des paramètres rajoutés pour Medatlas
     */
    protected List<ParameterModel> _medparameters;
    
    
    
    
    
	/**
	 * Information sur les series temporelles. Optionnel, n'existe que si la
	 * campagne a concerne des donnees temporelles.
	 */
	protected TimeSeriesInformation _timeSeriesInformation;

	/**
	 * Liste des flags QC des meta-donnees
	 * 
	 */
	protected HashMap<String, String> _metaFlags;
    
    /**
     * Nom du fichier source, sert notemment pour trier les stations
     */
    protected String _fichierSource;

	public Station() {
        _dataSize = 0;
		_bottomDepth = Float.NaN;
		_comment = Global.EMPTY_STRING;
		_dcHistory1 = Global.EMPTY_STRING;
		_dcHistory2 = Global.EMPTY_STRING;
		_dmHistory1 = Global.EMPTY_STRING;
		_dmHistory2 = Global.EMPTY_STRING;
		_globalParametersQC = Global.EMPTY_STRING;
		_globalProfileQC = Global.WORSE_FLAG;
		_offsetTU = 0.0f;
		_reference = Global.EMPTY_STRING;
		_surfaceSamples1 = Global.EMPTY_STRING;
		_surfaceSamples2 = Global.EMPTY_STRING;
		_time = Float.NaN;
		_dataType = null;
		_parameters = new ArrayList<ParameterModel>();
        _odvparameters = new ArrayList<ParameterModel>();
        _medparameters = new ArrayList<ParameterModel>();
		_survey = null;
		_timeSeriesInformation = null;
		_metaFlags = new HashMap<String, String>();
        _fichierSource = Global.EMPTY_STRING;
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
//		if (a_LocalName.equals("parameters")) {
//			return new PathElementConsumer("parameters", this);
//		}
//        if (a_LocalName.equals("odvparameters")) {
//            return new PathElementConsumer("odvparameters", this);
//        }
//        if (a_LocalName.equals("medparameters")) {
//            return new PathElementConsumer("medparameters", this);
//        }
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
//		SAXConsumer c = null;
//		if (path.equals("parameters")) {
//			c = Parameter
//					.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
//			addParameter((Parameter) c);
//			return c;
//		}
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
//		a_Writer.openTag("parameters");
////		for (Parameter t : getParameters()) {
////			t.writeXMLOn(a_Writer);
////		}
//        a_Writer.closeTag();
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
		copy._parameters = new java.util.ArrayList<ParameterModel>();
        copy._odvparameters = new java.util.ArrayList<ParameterModel>();
        copy._medparameters = new java.util.ArrayList<ParameterModel>();
		for (ParameterModel t0 : getParameters()) {
			copy.addParameter((ParameterModel) t0.clone());
		}
		copy._timeSeriesInformation = null;
		if (getTimeSeriesInformation() != null) {
			copy
					.setTimeSeriesInformation((TimeSeriesInformation) getTimeSeriesInformation()
							.clone());
		}
		return copy;
	}
    
    public void reset()
    {
        _bottomDepth = Float.NaN;
        _comment = Global.EMPTY_STRING;
        _dcHistory1 = Global.EMPTY_STRING;
        _dcHistory2 = Global.EMPTY_STRING;
        _dmHistory1 = Global.EMPTY_STRING;
        _dmHistory2 = Global.EMPTY_STRING;
        _globalParametersQC = Global.EMPTY_STRING;
        _globalProfileQC = Global.WORSE_FLAG;
        _offsetTU = 0.0f;
        _reference = Global.EMPTY_STRING;
        _surfaceSamples1 = Global.EMPTY_STRING;
        _surfaceSamples2 = Global.EMPTY_STRING;
        _time = Float.NaN;
        
        _date = 0;
        _dateQC = 0;
        _depthQC = 0;
        _latitude = Double.NaN;
        _latQC = 0;
        _longitude = Double.NaN;
        _longQC = 0;
        
        _metaFlags.clear();
        release();
    }

	/**
	 * Remove any associated links
	 */
	public void release() 
    {
        releaseSurvey();
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
			if (a_String.trim().equals(Global.EMPTY_STRING)) {
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
     * 
     */
    public void setDate(String a_String, DateTimeFormat a_Format) {
        try {
            Long value = (Long) a_Format.parseObject(a_String);
            setDate(value);
        } catch (Exception e) {
            e.printStackTrace();
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
        
        String globalParameter = getGlobalParametersQC();
        boolean missingValue = true;
        
        for (int i = 0; i < globalParameter.length(); i++){
            String qc = globalParameter.substring(i, i+1);
            
            if (!qc.equals(Global.ZERO) 
                    && !qc.equals(Global.DEFAULT_FLAG) 
                    && qc.compareTo(_globalProfileQC) < 0)
                _globalProfileQC = qc;
            
            if (!qc.equals(Global.DEFAULT_FLAG))
                missingValue = false;
        }
        
        if (_globalProfileQC.equals(Global.WORSE_FLAG)) _globalProfileQC = Global.ZERO;
        if (missingValue) _globalProfileQC = Global.DEFAULT_FLAG;
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
     * 
     */
    public void setLatitude(String a_String, LatitudeFormat a_Format) {
        try {
            double value = ((Number) a_Format.parseObject(a_String)).doubleValue();
            setLatitude(value);
        } catch (Exception e) {
            e.printStackTrace();
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
     * 
     */
    public void setLongitude(String a_String, LongitudeFormat a_Format) {
        try {
            double value = ((Number) a_Format.parseObject(a_String)).doubleValue();
            setLongitude(value);
        } catch (Exception e) {
            e.printStackTrace();
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
     * 
     */
    public void setTime(String a_String, DateTimeFormat a_Format) {
        try {
            Long value = (Long) a_Format.parseObject(a_String);
            setTime(value);
        } catch (Exception e) {
            e.printStackTrace();
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

    
    public void addMedParameter(ParameterModel x) {
        if (_medparameters.contains(x))
            return;
        _medparameters.add(x);
        //x.setStation(this, Conversion.MEDATLAS);
    }
    public ParameterModel[] getMedParameters() {
        ParameterModel[] v = new ParameterModel[_medparameters.size()];
        _medparameters.toArray(v);
        return v;
    }
    public void addOdvParameter(ParameterModel x) {
        if (_odvparameters.contains(x))
            return;
        _odvparameters.add(x);
        //x.setStation(this, Conversion.ODV);
    }
    public ParameterModel[] getOdvParameters() {
        ParameterModel[] v = new ParameterModel[_odvparameters.size()];
        _odvparameters.toArray(v);
        return v;
    }

    /**
     * Getter sur _medparameters.
     * @return the medparameters
     */
    public java.util.List<ParameterModel> getMedparameters()
    {
        return _medparameters;
    }

    /**
     * Getter sur _odvparameters.
     * @return the odvparameters
     */
    public java.util.List<ParameterModel> getOdvparameters()
    {
        return _odvparameters;
    }
    
    public ParameterModel getAddedParameter(IdParameter idParam){
        ParameterModel param = null;
        for (ParameterModel current : _medparameters){
            if (current.getModel().getCode().equals(idParam.toString())){                
                param = current;
                break;
            }
        }
        if (param == null){
            for (ParameterModel current : _odvparameters){
                if (current.getModel().getCode().equals(idParam.toString())){                    
                    param = current;
                    break;
                }
            }
        }
        return param;
    }

	/**
	 * release all links for role "parameters"
	 */
	public void releaseParameters() {
		for (ParameterModel t : getParameters()) {
            t.release();
			removeParameter(t);
		}
        for (ParameterModel t : getOdvParameters()) {
            t.release();
            if (_odvparameters.contains(t)) _odvparameters.remove(t);;
        }
        for (ParameterModel t : getMedParameters()) {
            t.release();
            if (_medparameters.contains(t)) _medparameters.remove(t);;
        }
	}

	/**
	 * remove link with x for role "parameters"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public void removeParameter(ParameterModel x) {
		if (!parametersIncludes(x))
			return;
		nocheckRemoveParameter(x);
		//x.setStation(null);
	}

	/**
	 * Add a link for role "parameters"
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void addParameter(ParameterModel x) {
		if (parametersIncludes(x))
			return;
		nocheckAddParameter(x);
		//x.setStation(this);
	}

	/**
	 * Returns the collection of linked objects for role "parameters"
	 * 
	 * @return Array of linked objects
	 */
	public ParameterModel[] getParameters() {
        ParameterModel[] v = new ParameterModel[_parameters.size()];
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
	public ParameterModel parametersAt(int i) {
		return _parameters.get(i);
	}

	/**
	 * returns index of object x in "parameters"
	 * 
	 * @param x
	 *            the linked object
	 */
	public int parametersIndexOf(ParameterModel x) {
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
			if (parametersAt(i).getModel().getCode().equals(paramCode)) {
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
	public void parametersMoveUp(ParameterModel x) {
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
	public void parametersMoveTop(ParameterModel x) {
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
	public void parametersMoveDown(ParameterModel x) {
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
	public void parametersMoveBottom(ParameterModel x) {
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
	public void nocheckMoveParameterAt(ParameterModel x, int a_Index) {
		int index;
        ParameterModel tmp;
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
	protected void nocheckRemoveParameter(ParameterModel x) {
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
	protected void nocheckAddParameter(ParameterModel x) {
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
	public boolean parametersIncludes(ParameterModel x) {
		return _parameters.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "parameters"
	 * 
	 */
	public java.util.Iterator<ParameterModel> parametersIterator() {
		return _parameters.iterator();

	}

	/**
	 * Remove any link for role timeSeriesInformation
	 */
	public void releaseTimeSeriesInformation() {
        if (_timeSeriesInformation != null) _timeSeriesInformation.release();
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
	@Override
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
			TimeZone tz = TimeZone.getTimeZone("GMT"); //$NON-NLS-1$
			Calendar cal = Calendar.getInstance(tz);
			cal.clear();
			Calendar cal2 = Calendar.getInstance(tz);
			cal2.clear();
			cal2.setTimeInMillis(new Float(_time).longValue());
			cal.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, cal2.get(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, cal2.get(Calendar.MILLISECOND));
			return getGMTDate(cal.getTimeInMillis());
		} //else {
		return _time;
		//}
	}

	/**
	 * 
	 * @return String
	 */
	public String getDefaultGlobalParametersQC() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < _medparameters.size(); i++) {
            buffer.append(Global.DEFAULT_FLAG);
        }
        for (int i = 0; i < parametersSize(); i++) {
            buffer.append(Global.DEFAULT_FLAG);
        }
        return buffer.toString();
	}

	/**
	 * 
	 * @return long
	 */
	public long getFullDate() {
		TimeZone tz = TimeZone.getTimeZone("GMT"); //$NON-NLS-1$
		Calendar cal = Calendar.getInstance(tz);
		cal.clear();
		cal.setTimeInMillis(_date);
		if (!Float.isNaN(_time)) {
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
		StringBuffer buffer = new StringBuffer();
        for (ParameterModel medPara : _medparameters){
            String qc = medPara.getModel().getGlobalQC();
            if (qc.equals(Global.WORSE_FLAG)) qc = Global.ZERO;
            buffer.append(qc);
        }
        for (ParameterModel para : _parameters){
            String qc = para.getModel().getGlobalQC();
            if (qc.equals(Global.WORSE_FLAG)) qc = Global.ZERO;
            buffer.append(qc);
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
        // uniquement pour MEDATLAS
        int parametersNumber = parametersSize();
        if (_medparameters != null) parametersNumber += _medparameters.size();
		return parametersNumber;
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
     * Trie pour l'ordre de sortie des fichiers.
	 */
	public String getSortCriteria() {
		return getFichierSource() + Long.toString(getFullDate()) + getReference();
	}

	/**
	 * Trie les donnees de la station par ordre croissant/decroissant en
	 * supprimant les doublons
	 * 
	 * @param ascendant
	 */
	public void triSansDoublon(boolean ascendant) {
		if (ViewFile.blnForcerTri) {
			ArrayList<Integer> arlIndexes = new ArrayList<Integer>();
			TreeMap<Double, Integer> tmTri = new TreeMap<Double, Integer>();

			String paramCode = Global.EMPTY_STRING;
			int paramIndex = -1;

			int i;

			// Choix du parametre e trier en fonction du type de donnees en
			// entree et en sortie
			if (StationModel.getSingleton().getInputType().equals(Global.PROFILE)) {
			    paramCode = Intern.paramPRES;
			} 
			else {
			    paramCode = Global.EMPTY_STRING;
			}
            
			// Ajout des binomes valeur/index dans un TreeMap pour tri sur la
			// valeur
			if (paramCode.equals(Global.EMPTY_STRING)) {
				// Cas du tri par date
				// on utilise la date du 1er parametre
				for (i = 0; i < parametersAt(0).getModel().valuesSize(); i++) {
					Long date = ((HorodatedValue) parametersAt(0).getModel().valuesAt(i))
							.getDate();
					tmTri.put(date.doubleValue(), i);
				}
			} 
            else {
				// Recherche de l'index du parametre en fonction du code
				paramIndex = parametersIndexOf(paramCode);

				if (paramIndex >= 0) {
					for (i = 0; i < parametersAt(paramIndex).getModel().valuesSize(); i++) {
						tmTri.put(parametersAt(paramIndex).getModel().valuesAt(i).getValue(), i);
					}
				} 
                else {
					return;
				}
			}

			// Ajout des indexes tries dans une liste dans l'ordre croissant ou
			// decroissant
			if (ascendant) {
				for (Entry<Double, Integer> currentEntry : tmTri.entrySet()) {
					arlIndexes.add(currentEntry.getValue());
				}
			} 
            else {
				ArrayList<Integer> arrayTri = new ArrayList<Integer>(
						parametersAt(paramIndex).getModel().valuesSize());

				for (Entry<Double, Integer> currentEntry : tmTri.entrySet()) {
					arrayTri.add(currentEntry.getValue());
				}

				for (i = arrayTri.size() - 1; i >= 0; i++) {
					arlIndexes.add(arrayTri.get(i));
				}
			}

			// Tri de tous les parametres de la station
			for (ParameterModel param : getParameters()) {
				param.getModel().triIndexes(arlIndexes);
			}
		}
	}

	/**
	 * Trie les donnees de la station par ordre croissant/decroissant en
	 * conservant les doublons
	 * 
	 * @param ascendant
	 */
	public void tri(boolean ascendant) {
		if (ViewFile.blnForcerTri) {
			ArrayList<Integer> arlIndexes = new ArrayList<Integer>();
			ArrayList<StationData> dataList = new ArrayList<StationData>(
					parametersAt(0).getModel().valuesSize());
			StationData dataTemp;
			String paramCode = Global.EMPTY_STRING;
			int paramIndex = -1;
			int nb = 0;
			int i;

			// Choix du parametre e trier en fonction du type de donnees en
			// entree et en sortie
            if (StationModel.getSingleton().getInputType().equals(Global.PROFILE)) {
                paramCode = Intern.paramPRES;
            } 
            else {
                paramCode = Global.EMPTY_STRING;
            }

			// Ajout des binomes valeur/index dans un TreeMap pour tri sur la
			// valeur
			if (paramCode.equals(Global.EMPTY_STRING)) {
				// Cas du tri par date
				// on utilise la date du 1er parametre
				for (i = 0; i < parametersAt(0).getModel().valuesSize(); i++) {
					Long date = ((HorodatedValue) parametersAt(0).getModel().valuesAt(i))
							.getDate();
					dataTemp = new StationData(date.doubleValue(), i,
							getReference());
					dataList.add(dataTemp);
				}
			} else {
				// Recherche de l'index du parametre en fonction du code
				paramIndex = parametersIndexOf(paramCode);

				if (paramIndex >= 0) {
					for (i = 0; i < parametersAt(paramIndex).getModel().valuesSize(); i++) {
						dataTemp = new StationData(parametersAt(paramIndex).getModel()
								.valuesAt(i).getValue(), i, getReference());
						dataList.add(dataTemp);
					}
				} else {
					return;
				}
			}

			// Ajout des indexes tries dans une liste dans l'ordre croissant ou
			// decroissant
			if (ascendant) {
				Collections.sort(dataList);
			} else {
				Collections.sort(dataList, Collections.reverseOrder());
			}

			for (i = 0; i < dataList.size(); i++) {
				arlIndexes.add(dataList.get(i).getIndex());
				if (dataList.get(i).getIndex() != i) {
					nb++;
				}
			}

			if (nb > 0) {
				// Ajout de la station e la liste des stations triees
				EditConversion.getSortedStations().put(getReference().trim(),
						nb);

				// Tri de tous les parametres de la station
				for (ParameterModel param : getParameters()) {
					param.getModel().triIndexes(arlIndexes);
				}
			}
		}
	}
    


    public String getLocalCdiId(String surveyReference, String formatConversion) {
        // FAE 1143 : nommage du LOCAL_CDI_ID
        
        String ref = Global.EMPTY_STRING;
        String stationReference = _reference.trim();
        
        
        
        if (formatConversion.equals(Conversion.ODV.toString())) {
            // ODV      -> [Référence du jeu de données]
            ref = EditConversion.getDataSetRef();
            // [Numéro station]si < 5 : codé sur 5 caractères il est complété à gauche par des '0'. 
            if (stationReference.length() < 5){
                stationReference = Formatter.getSingleton().padZero(stationReference, 5);
            }
        }else{
            // MEDATLAS ->  
            // [Référence campagne]
            ref = Formatter.getSingleton().padString(surveyReference,13);
            // [Numéro station] codé sur 5 caractères il est complété à gauche par des '0'. 
            stationReference = Formatter.getSingleton().padZero(stationReference, 5);
        }

        // [Reference]
        String temp = Global.EMPTY_STRING;
        temp += ref;
        
        // [Numéro station]
        if (!temp.equals(Global.EMPTY_STRING)) temp += Global.UNDERSCORE; 
        temp += stationReference;
        
        // [Type de données] s'il existe
        if (_dataType != null) {
            if (!_dataType.getCode().equals(Global.EMPTY_STRING)) {
                if (!temp.equals(Global.EMPTY_STRING)) {
                    temp += Global.UNDERSCORE;
                }
                temp += _dataType.getCode();
            }
        }

        return Utils.validFileName(temp);
    }

	public HashMap<String, String> getMetaFlags() {
		return _metaFlags;
	}

	public void setMetaFlags(HashMap<String, String> flags) {
		_metaFlags = flags;
	}

	public String getMetaFlag(String property) {
		String flag = Global.ZERO;

		if (getMetaFlags() != null) {
			if ((getMetaFlags().containsKey(property)) && (getMetaFlags().get(property) != null)) {
				flag = getMetaFlags().get(property);
			}
		}

		return flag;
	}
    
    public String calculTypeBC(){
        String typeBC = "*"; //$NON-NLS-1$

        if (_dataType != null && _dataType.getCode() != null 
                && _dataType.getCode().equals("H09")){ //$NON-NLS-1$
            typeBC = "B"; //$NON-NLS-1$
        }else if (_dataType != null && _dataType.getCode() != null 
                && (_dataType.getCode().equals("H10") || _dataType.getCode().equals("H13"))){ //$NON-NLS-1$ //$NON-NLS-2$
            typeBC = "C";      //$NON-NLS-1$      
        }else{
            int nbRecords = parametersAt(0).getModel().valuesSize();
            if (nbRecords < 250){
                typeBC = "B"; //$NON-NLS-1$
            }else{
                typeBC = "C"; //$NON-NLS-1$
            }
        }
        return typeBC;
    }
    
    public String getDateTime() throws ParseException{
        StringBuffer value = new StringBuffer();
        //$formatter.formatDateTime("YYYY-MM-DD", $station.fullDate)T$formatter.formatDateTime("hh24:mm:ss.sss", $station.correctedTime, "9999"))
        String date = Formatter.getSingleton().formatDateTime("YYYY-MM-DD", getFullDate()); //$NON-NLS-1$
        value.append(date);
        value.append("T"); //$NON-NLS-1$
        String time = Formatter.getSingleton().formatDateTime("hh24:mm:ss.sss", getCorrectedTime()); //$NON-NLS-1$
        value.append(time);
        return value.toString();
    }
    
    public String getVariantDateTime(int i) throws ParseException{
        StringBuffer value = new StringBuffer();
        HorodatedValue horodate = null;
        if (parametersAt(0).getModel().valuesAt(i) instanceof HorodatedValue){
            horodate = (HorodatedValue) parametersAt(0).getModel().valuesAt(i);
            String date = Formatter.getSingleton().formatDateTime("YYYY-MM-DD", horodate.getDate()); //$NON-NLS-1$
            value.append(date);
            value.append("T"); //$NON-NLS-1$
            String time = Formatter.getSingleton().formatDateTime("hh24:mm:ss.sss", horodate.getDate()); //$NON-NLS-1$
            value.append(time);
        }
        return value.toString();
    }
    
    public String getVariant(int i, String code){
        String value = Global.EMPTY_STRING;
        //Parameter param = null;
        for (ParameterModel current : _medparameters){
            if (current.getModel().getCode().equals(code)){                
                value = current.getModel().valuesAt(i).getStringValue();
                break;
            }
        }
        if (value.equals(Global.EMPTY_STRING)){
            for (ParameterModel current : _odvparameters){
                if (current.getModel().getCode().equals(code)){
                    value = current.getModel().valuesAt(i).getStringValue();
                    break;
                }
            }
        }
        return value;
    }

	public String getFlag(ParameterModel param, int index) {
		String flag = Global.ZERO;
		
		if (param.getModel().valuesAt(index).getValue().isNaN()) {
			flag = Global.DEFAULT_FLAG; 
		} else if (param.getModel().valuesAt(index).getQc().equals(Global.EMPTY_STRING)) {
			flag = Global.ZERO;
		} else {
			flag = param.getModel().valuesAt(index).getQc();
		}
		
		return flag;
	}

    public String getAddedFlag(ParameterModel param, int index) {
        String flag = Global.ZERO;
        String stringValue = param.getModel().valuesAt(index).getStringValue(); 
        if (stringValue == null ||stringValue.equals(Global.EMPTY_STRING)) {
            flag = Global.DEFAULT_FLAG; 
        } else if (param.getModel().valuesAt(index).getQc().equals(Global.EMPTY_STRING)) {
            flag = Global.ZERO;
        } else {
            flag = param.getModel().valuesAt(index).getQc();
        }
        
        return flag;
    }
	public String getFlags(int index) {
		StringBuffer buffer = new StringBuffer();

        // Ajout des flags des parametres ajoutés
        for (ParameterModel param : getMedParameters()) {
            buffer.append(getAddedFlag(param, index));
        }
        
        // Ajout des flags des parametres
        for (ParameterModel param : getParameters()) {
            buffer.append(getFlag(param, index));
        }
		return buffer.toString();
	}
    
    
    /**
     * setter généralisé, sur le même modèle que la méthode consumeElement
     * @param a_ChampDest
     * @param a_String
     * @param a_Format
     * @return true si le a_ChampDest existe, false sinon
     */
    public boolean set(String a_ChampDest, String a_String, ExternalizableFormat a_Format){
        boolean exist = true;
        if (a_ChampDest.equals(NemoSection.S_DCHIST + '1')){
            setDcHistory1(a_String);
        }else if (a_ChampDest.equals(NemoSection.S_DCHIST + '2')){
            setDcHistory2(a_String);
        }else if (a_ChampDest.equals(NemoSection.S_COMMENT)){
            setComment(a_String);
        }else if (a_ChampDest.equals(NemoSection.S_SURFACE + '1')){
            setSurfaceSamples1(a_String);
        }else if (a_ChampDest.equals(NemoSection.S_SURFACE + '2')){
            setSurfaceSamples2(a_String);
        }else if (a_ChampDest.equals(NemoSection.S_OFFSETTU)){
            setOffsetTU(a_String);
        }else if (a_ChampDest.equals(NemoSection.S_TIME)){
            setTime(a_String, (DateTimeFormat) a_Format);
        }else if (a_ChampDest.equals(NemoSection.S_DATE)){
            setDate(a_String, (DateTimeFormat) a_Format);
        }else if (a_ChampDest.equals(NemoSection.S_LAT)){
            setLatitude(a_String, (LatitudeFormat) a_Format);
        }else if (a_ChampDest.equals(NemoSection.S_LON)){
            setLongitude(a_String, (LongitudeFormat) a_Format);
        }else if (a_ChampDest.equals(NemoSection.S_DEPTH)){
            setBottomDepth(a_String);
        }else{
            exist = false;
        }
        return exist;
    }
    
    
    /**
     * getter généralisé, sur le même modèle que la méthode consumeElement
     * @param a_ChampDest
     * @param a_String
     * @param a_Format
     * @return true si le a_ChampDest existe, false sinon
     */
    public String get(String a_ChampDest, ExternalizableFormat a_Format){
        String value = Global.EMPTY_STRING;
        Formatter formatter = Formatter.getSingleton();
        try
        {
            if (a_ChampDest.equals(NemoSection.S_DCHIST + '1')){
                value = getDcHistory1();
            }else if (a_ChampDest.equals(NemoSection.S_DCHIST + '2')){
                value = getDcHistory2();
            }else if (a_ChampDest.equals(NemoSection.S_COMMENT)){
                value = getComment();
            }else if (a_ChampDest.equals(NemoSection.S_SURFACE + '1')){
                value = getSurfaceSamples1();
            }else if (a_ChampDest.equals(NemoSection.S_SURFACE + '2')){
                value = getSurfaceSamples2();
            }else if (a_ChampDest.equals(NemoSection.S_OFFSETTU)){
                value = String.valueOf(getOffsetTU());
            }else if (a_ChampDest.equals(NemoSection.S_TIME)){
                value = formatter.formatDateTime(((DateTimeFormat) a_Format).getFormatString(), getTime());
            }else if (a_ChampDest.equals(NemoSection.S_DATE)){
                value = formatter.formatDateTime(((DateTimeFormat) a_Format).getFormatString(), getDate());
            }else if (a_ChampDest.equals(NemoSection.S_LAT)){
                value = formatter.formatLatitude(((LatitudeFormat) a_Format).getFormatString(), getLatitude());
            }else if (a_ChampDest.equals(NemoSection.S_LON)){
                value = formatter.formatLongitude(((LongitudeFormat) a_Format).getFormatString(), getLongitude());
            }else if (a_ChampDest.equals(NemoSection.S_DEPTH)){
                if (!Float.isNaN(getBottomDepth()))
                    value = String.valueOf(getBottomDepth());
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Getter sur _fichierSource.
     * @return the fichierSource
     */
    public String getFichierSource()
    {
        return _fichierSource;
    }

    /**
     * Setter sur _fichierSource.
     * @param fichierSource the fichierSource to set
     */
    public void setFichierSource(String fichierSource)
    {
        _fichierSource = fichierSource;
    }


    public float getDataSize() {
        return _dataSize;
    }

    public void setDataSize(float size) {
        _dataSize = size;
    }

} /* End of class: Station */
