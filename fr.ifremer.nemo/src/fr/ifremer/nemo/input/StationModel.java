package fr.ifremer.nemo.input;

import java.beans.IntrospectionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.Element2Field;
import com.ago.xml.PathElementConsumer;
import com.ago.xml.PathElementHandler;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.config.FlagTable;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.Cdi;
import fr.ifremer.nemo.domain.HorodatedValue;
import fr.ifremer.nemo.domain.Parameter;
import fr.ifremer.nemo.domain.ParameterValue;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.TimeSeriesInformation;
import fr.ifremer.nemo.domain.Trajectory;
import fr.ifremer.nemo.ui.DataModel;
import fr.ifremer.nemo.ui.EditConversion;
import fr.ifremer.nemo.ui.SWTProcess;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.ViewFile;
import fr.ifremer.nemo.util.DateTimeFormat;
import fr.ifremer.nemo.util.FloatFormat;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.JulianDate;
import fr.ifremer.nemo.util.LatitudeFormat;
import fr.ifremer.nemo.util.LongitudeFormat;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.SizeBasedProgressNotifier;

/**
 * Class StationModel
 * 
 * Modèle de station utilisé dans la campagne
 * 
 * @author Gerard BUNEL
 */
public class StationModel implements Cloneable, PathElementHandler, SAXConsumer {

	static protected StationModel _singleton = null;

	/**
	 * Nombre de lignes entre la fin des donnees et le debut de la station
	 * suivante
	 */
	protected int _endData;

	/**
	 * Motif de fin de station
	 */
	protected String _endDataPattern;

	/**
	 * Booléen indiquant si la fin de station doit être détectée par un motif.
	 * S'il est à false, l'information de fin de station est obtenue à l'aide de
	 * l'attribut endData
	 */
	protected boolean _endOnPattern;

	protected boolean _endOnEOF;

	protected boolean _endOnEmptyLine;

	/**
	 * Definit si le numéro de référence est automatiquement incrémente
	 */
	protected boolean _incrementRef;

	/**
	 * Definit le numéro de référence a incrémenter
	 */
	protected int _incrementNumber;

	/**
	 * Indique si le fichier correspond à un fichier dont les metadata station
	 * se trouvent dans la première ligne des données
	 */
	protected boolean _metaInData;

	/**
	 * Indique si le fichier correspond à un fichier dont les metadata station
	 * se trouvent dans la première ligne des données
	 */
	protected boolean _metaInData2;

	/**
	 * Nombre de ligne du header Station
	 */
	protected int _numberOfLines;

	protected static transient int _ref = -1;

	/**
	 * Nombre de mesures réalisées sur la station
	 */
	protected int _rowNumber;

	/**
	 * Station servant de modèle pour stocker les informations de l'entête
	 * station
	 */
	protected Station _model;

	protected PropertyModel _referenceProperty;

	/**
	 * Liste des propriétés permettant de récupérer les informations de l'entête
	 * station
	 */
	protected java.util.Map<String, PropertyModel> _properties;

	protected TimeSeriesModel _timeSeriesModel;

	/**
	 * Liste, ordonnee, des paramètres mesurés sur la station
	 */
	protected java.util.List<ParameterModel> _parameterModels;

	/**
	 * Modèle pour les informations propres aux séries temporelles. Optionnel
	 */
	protected HorodatedParameterModel _timestampModel;

	/**
	 * Modèle pour les informations propres au CDI
	 */
	protected Cdi _cdi;

	/**
	 * Modèle pour les informations propres aux trajectoires
	 */
	protected Trajectory _trajectory;

	/**
	 * Dernier numero de station (mode "ODV")
	 */
	protected String _lastRef;

	/**
	 * En-tête de station plus 1 ligne de données (mode "ODV")
	 */
	protected String[] _headerPlus;

	/**
	 * Nombre de caractères du fichier
	 */
	protected int _taille;

	/**
	 * 
	 * Type de fichier en entrée : profil vertical, serie temporelle ou
	 * trajectoire
	 */
	protected String _inputType;

	/**
	 * Index du parametre PRES
	 */
	protected int _presIndex;

	/**
	 * Format du parametre DEPH
	 */
	protected String _dephFormat;

	/**
	 * Format decimal de la date Julienne
	 */
	protected DecimalFormat _df;

	/**
	 * Tableau de correspondance des flags
	 */
	protected FlagTable _flagTable;

	public StationModel() {
		_incrementNumber = 1;
		_endDataPattern = "";
		_endOnEOF = false;
		_endOnEmptyLine = false;
		_endOnPattern = false;
		_model = null;
		_parameterModels = new java.util.ArrayList<ParameterModel>();
		_properties = new java.util.LinkedHashMap<String, PropertyModel>();
		_referenceProperty = null;
		_timeSeriesModel = null;
		_timestampModel = null;
		_cdi = null;
		_trajectory = null;
		_lastRef = null;
		_headerPlus = null;
		_inputType = Global.PROFILE;
		_df = new DecimalFormat("#0.000000");
	}

	/**
	 * 
	 * @param isTimeSeries
	 */
	public StationModel(String typeFichier) {
		this();
		boolean isTimeSeries, isTrajectory;
		if (typeFichier.equals(Global.TIMESERIES)) {
			isTimeSeries = true;
			isTrajectory = false;
		} else if (typeFichier.equals(Global.TRAJECTORY)) {
			isTimeSeries = true;
			isTrajectory = true;
		} else {
			isTimeSeries = false;
			isTrajectory = false;
		}

		setModel(new Station(isTimeSeries));
		if (isTimeSeries) {
			setTimeSeriesModel(new TimeSeriesModel(this));
			setTimestampModel(new HorodatedParameterModel());
		}
		if (isTrajectory) {
			setTrajectory(new Trajectory());
		}
		setTimestampModel(new HorodatedParameterModel());
		initProperties();

		_singleton = StationModel.this;
	}

	public static StationModel getSingleton() {
		return _singleton;
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
		StationModel tmp = new StationModel();
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
		if ((tmp = a_Attributes.getValue("lineNumber")) != null) {
			setNumberOfLines(tmp);
		}
		if ((tmp = a_Attributes.getValue("endOnEOF")) != null) {
			setEndOnEOF(tmp);
		}
		if ((tmp = a_Attributes.getValue("endOnEmptyLine")) != null) {
			setEndOnEmptyLine(tmp);
		}
		if ((tmp = a_Attributes.getValue("endOnPattern")) != null) {
			setEndOnPattern(tmp);
		}
	}

	/**
	 * 
	 * A new XML Element has been found. Parse it.
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
		if (a_LocalName.equals("inputType")) {
			Element2Field setter = new Element2Field(this, "setInputType");
			return setter;
		}
		if (a_LocalName.equals("incrementRef")) {
			Element2Field setter = new Element2Field(this, "setIncrementRef");
			return setter;
		}
		if (a_LocalName.equals("incrementNumber")) {
			Element2Field setter = new Element2Field(this, "setIncrementNumber");
			return setter;
		}
		if (a_LocalName.equals("endDataPattern")) {
			Element2Field setter = new Element2Field(this, "setEndDataPattern");
			return setter;
		}
		if (a_LocalName.equals("rowNumber")) {
			Element2Field setter = new Element2Field(this, "setRowNumber");
			return setter;
		}
		if (a_LocalName.equals("endData")) {
			Element2Field setter = new Element2Field(this, "setEndData");
			return setter;
		}
		if (a_LocalName.equals("metaInData")) {
			Element2Field setter = new Element2Field(this, "setMetaInData");
			return setter;
		}
		if (a_LocalName.equals("metaInData2")) {
			Element2Field setter = new Element2Field(this, "setMetaInData2");
			return setter;
		}
		if (a_LocalName.equals("Cdi")) {
			Cdi l = (Cdi) Cdi.fromElement(a_URI, a_LocalName, a_QName,
					a_Attributes);
			setCdi(l);
			return (SAXConsumer) l;
		}
		if (a_LocalName.equals("Station")) {
			Station l = (Station) Station.fromElement(a_URI, a_LocalName,
					a_QName, a_Attributes);
			setModel(l);
			return (SAXConsumer) l;
		}
		if (a_LocalName.equals("referenceProperty")) {
			return new PathElementConsumer("referenceProperty", this);
		}
		if (a_LocalName.equals("properties")) {
			return new PathElementConsumer("properties", this);
		}
		if (a_LocalName.equals("TimeSeriesModel")) {
			TimeSeriesModel l = (TimeSeriesModel) TimeSeriesModel.fromElement(
					a_URI, a_LocalName, a_QName, a_Attributes);
			setTimeSeriesModel(l);
			return (SAXConsumer) l;
		}
		if (a_LocalName.equals("Trajectory")) {
			Trajectory l = (Trajectory) Trajectory.fromElement(a_URI,
					a_LocalName, a_QName, a_Attributes);
			setTrajectory(l);
			return (SAXConsumer) l;
		}
		if (a_LocalName.equals("parameters")) {
			return new PathElementConsumer("parameters", this);
		}
		if (a_LocalName.equals("HorodatedParameterModel")) {
			HorodatedParameterModel l = (HorodatedParameterModel) HorodatedParameterModel
					.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			setTimestampModel(l);
			return (SAXConsumer) l;
		}
		if (a_LocalName.equals("FlagTable")) {
			String code = null;
			String label = null;
			FlagTable flagTable = new FlagTable();

			if ((code = a_Attributes.getValue("code")) != null
					&& (label = a_Attributes.getValue("label")) != null) {
				flagTable.setCode(code);
				flagTable.setLibelle(label);
				setFlagTable(flagTable);
			}
			return this;
		}
		if (a_LocalName.equals("Flag")) {
			String local = null;
			String sdn = null;
			Flag flag = new Flag();

			if ((local = a_Attributes.getValue("local")) != null
					&& (sdn = a_Attributes.getValue("SDN")) != null) {
				flag.setLocal(local);
				flag.setSdn(sdn);
				getFlagTable().getFlags().add(flag);
			}
			return this;
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
	 * Generate XML defintion on a Stream
	 * 
	 * @param a_Writer
	 *            XMLOutputWriter to which to write the XML Definition
	 * 
	 */
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		a_Writer.putAttribute("lineNumber", getNumberOfLines());
		a_Writer.putAttribute("endOnEOF", isEndOnEOF());
		a_Writer.putAttribute("endOnEmptyLine", isEndOnEmptyLine());
		a_Writer.putAttribute("endOnPattern", isEndOnPattern());
		a_Writer.putTag("inputType", getInputType());
		a_Writer.putTag("incrementRef", isIncrementRef());
		a_Writer.putTag("incrementNumber", getIncrementNumber());
		a_Writer.putTag("endDataPattern", getEndDataPattern());
		a_Writer.putTag("rowNumber", getRowNumber());
		a_Writer.putTag("endData", getEndData());
		a_Writer.putTag("metaInData", isMetaInData());
		a_Writer.putTag("metaInData2", isMetaInData2());
		if (ViewFile.checkCDI.getSelection()) {
			if (getCdi() != null) {
				getCdi().writeXMLOn(a_Writer);
			} else {
				Cdi cdi = new Cdi();
				cdi.setTypePlateForme(ViewFile.comboTypePlateForme.getText());
				cdi.setAccesDonnees(ViewFile.comboAccesDonnees.getText());
				setCdi(cdi);
				getCdi().writeXMLOn(a_Writer);
			}
		}
		if (getModel() != null) {
			getModel().writeXMLOn(a_Writer);
		}
		a_Writer.openTag("parameters");
		for (ParameterModel t : getParameterModels()) {
			t.writeXMLOn(a_Writer);
		}
		a_Writer.closeTag();
		a_Writer.openTag("properties");
		for (PropertyModel t : getProperties()) {
			t.writeXMLOn(a_Writer);
		}
		a_Writer.closeTag();
		if (getReferenceProperty() != null) {
			a_Writer.openTag("referenceProperty");
			getReferenceProperty().writeXMLOn(a_Writer);
			a_Writer.closeTag();
		}
		if (getTrajectory() != null) {
			getTrajectory().writeXMLOn(a_Writer);
		}
		if (getTimeSeriesModel() != null) {
			getTimeSeriesModel().writeXMLOn(a_Writer);
		}
		if (getTimestampModel() != null) {
			getTimestampModel().writeXMLOn(a_Writer);
		}
		if (getFlagTable() != null) {
			a_Writer.openTag("FlagTable");
			a_Writer.putAttribute("code", getFlagTable().getCode());
			a_Writer.putAttribute("label", getFlagTable().getLibelle());
			for (int i = 0; i < getFlagTable().getFlags().size(); i++) {
				a_Writer.openTag("Flag");
				a_Writer.putAttribute("local", getFlagTable().getFlags().get(i)
						.getLocal());
				a_Writer.putAttribute("SDN", getFlagTable().getFlags().get(i)
						.getSdn());
				a_Writer.closeTag();
			}
			a_Writer.closeTag();
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
		a_Writer.openTag("StationModel");
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
		StationModel copy = null;
		copy = (StationModel) super.clone();
		copy._model = null;
		if (getModel() != null) {
			copy.setModel((Station) getModel().clone());
		}
		copy._referenceProperty = null;
		if (getReferenceProperty() != null) {
			copy.setReferenceProperty((PropertyModel) getReferenceProperty()
					.clone());
		}
		copy._properties = new java.util.LinkedHashMap<String, PropertyModel>();
		for (PropertyModel t0 : getProperties()) {
			copy.addProperty((PropertyModel) t0.clone());
		}
		copy._timeSeriesModel = null;
		if (getTimeSeriesModel() != null) {
			copy.setTimeSeriesModel((TimeSeriesModel) getTimeSeriesModel()
					.clone());
		}
		copy._parameterModels = new java.util.ArrayList<ParameterModel>();
		for (ParameterModel t1 : getParameterModels()) {
			copy.addParameterModel((ParameterModel) t1.clone());
		}
		copy._timestampModel = null;
		if (getTimestampModel() != null) {
			copy
					.setTimestampModel((HorodatedParameterModel) getTimestampModel()
							.clone());
		}
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
		releaseModel();
		releaseCdi();
		releaseParameterModels();
		releaseProperties();
		releaseReferenceProperty();
		releaseTimeSeriesModel();
		releaseTrajectory();
		releaseTimestampModel();
	}

	/**
	 * Read accessor (getter) for attribute _endData
	 * 
	 * @return int
	 * @see StationModel#_endData
	 */
	public int getEndData() {
		return _endData;
	}

	/**
	 * Write accessor (setter) for attribute _endData
	 * 
	 * @param x
	 *            new value for _endData
	 * @see StationModel#_endData
	 */
	public void setEndData(int x) {
		_endData = x;
	}

	/**
	 * Write accessor (setter) for attribute _endData
	 * 
	 * @param a_String
	 *            new value for _endData as String
	 * @see StationModel#_endData
	 */
	public void setEndData(String a_String) {
		try {
			setEndData(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _endDataPattern
	 * 
	 * @return String
	 * @see StationModel#_endDataPattern
	 */
	public String getEndDataPattern() {
		return _endDataPattern;
	}

	/**
	 * Write accessor (setter) for attribute _endDataPattern
	 * 
	 * @param x
	 *            new value for _endDataPattern
	 * @see StationModel#_endDataPattern
	 */
	public void setEndDataPattern(String x) {
		_endDataPattern = x;
	}

	/**
	 * Read accessor (getter) for attribute _endOnPattern
	 * 
	 * @return boolean
	 * @see StationModel#_endOnPattern
	 */
	public boolean isEndOnPattern() {
		return _endOnPattern;
	}

	/**
	 * Write accessor (setter) for attribute _endOnPattern
	 * 
	 * @param x
	 *            new value for _endOnPattern
	 * @see StationModel#_endOnPattern
	 */
	public void setEndOnPattern(boolean x) {
		_endOnPattern = x;
	}

	/**
	 * Write accessor (setter) for attribute _endOnPattern
	 * 
	 * @param a_String
	 *            new value for _endOnPattern as String
	 * @see StationModel#_endOnPattern
	 */
	public void setEndOnPattern(String a_String) {
		setEndOnPattern(new Boolean(a_String).booleanValue());
	}

	/**
	 * Read accessor (getter) for attribute _endOnEOF
	 * 
	 * @return boolean
	 * @see StationModel#_endOnEOF
	 */
	public boolean isEndOnEOF() {
		return _endOnEOF;
	}

	/**
	 * Write accessor (setter) for attribute _endOnEOF
	 * 
	 * @param x
	 *            new value for _endOnEOF
	 * @see StationModel#_endOnEOF
	 */
	public void setEndOnEOF(boolean x) {
		_endOnEOF = x;
	}

	/**
	 * Write accessor (setter) for attribute _endOnEOF
	 * 
	 * @param a_String
	 *            new value for _endOnEOF as String
	 * @see StationModel#_endOnEOF
	 */
	public void setEndOnEOF(String a_String) {
		setEndOnEOF(new Boolean(a_String).booleanValue());
	}

	/**
	 * Read accessor (getter) for attribute _endOnEmptyLine
	 * 
	 * @return boolean
	 * @see StationModel#_endOnEmptyLine
	 */
	public boolean isEndOnEmptyLine() {
		return _endOnEmptyLine;
	}

	/**
	 * Write accessor (setter) for attribute _endOnEmptyLine
	 * 
	 * @param x
	 *            new value for _endOnEmptyLine
	 * @see StationModel#_endOnEmptyLine
	 */
	public void setEndOnEmptyLine(boolean x) {
		_endOnEmptyLine = x;
	}

	/**
	 * Write accessor (setter) for attribute _endOnEmptyLine
	 * 
	 * @param a_String
	 *            new value for _endOnEmptyLine as String
	 * @see StationModel#_endOnEmptyLine
	 */
	public void setEndOnEmptyLine(String a_String) {
		setEndOnEmptyLine(new Boolean(a_String).booleanValue());
	}

	/**
	 * Read accessor (getter) for attribute _incrementRef
	 * 
	 * @return boolean
	 * @see StationModel#_incrementRef
	 */
	public boolean isIncrementRef() {
		return _incrementRef;
	}

	/**
	 * Write accessor (setter) for attribute _incrementRef
	 * 
	 * @param x
	 *            new value for _incrementRef
	 * @see StationModel#_incrementRef
	 */
	public void setIncrementRef(boolean x) {
		_incrementRef = x;
	}

	/**
	 * Write accessor (setter) for attribute _incrementRef
	 * 
	 * @param a_String
	 *            new value for _incrementRef as String
	 * @see StationModel#_incrementRef
	 */
	public void setIncrementRef(String a_String) {
		setIncrementRef(new Boolean(a_String).booleanValue());
	}

	/**
	 * 
	 */
	public int getIncrementNumber() {
		return _incrementNumber;
	}

	public void setIncrementNumber(String number) {
		_incrementNumber = Integer.parseInt(number);
	}

	/**
	 * Read accessor (getter) for attribute _metaInData
	 * 
	 * @return boolean
	 * @see StationModel#_metaInData
	 */
	public boolean isMetaInData() {
		return _metaInData;
	}

	/**
	 * Write accessor (setter) for attribute _metaInData
	 * 
	 * @param x
	 *            new value for _metaInData
	 * @see StationModel#_metaInData
	 */
	public void setMetaInData(boolean x) {
		_metaInData = x;
	}

	/**
	 * Write accessor (setter) for attribute _metaInData
	 * 
	 * @param a_String
	 *            new value for _metaInData as String
	 * @see StationModel#_metaInData
	 */
	public void setMetaInData(String a_String) {
		setMetaInData(new Boolean(a_String).booleanValue());
	}

	/**
	 * Read accessor (getter) for attribute _numberOfLines
	 * 
	 * @return int
	 * @see StationModel#_numberOfLines
	 */
	public int getNumberOfLines() {
		return _numberOfLines;
	}

	/**
	 * Write accessor (setter) for attribute _numberOfLines
	 * 
	 * @param x
	 *            new value for _numberOfLines
	 * @see StationModel#_numberOfLines
	 */
	public void setNumberOfLines(int x) {
		_numberOfLines = x;
	}

	/**
	 * Write accessor (setter) for attribute _numberOfLines
	 * 
	 * @param a_String
	 *            new value for _numberOfLines as String
	 * @see StationModel#_numberOfLines
	 */
	public void setNumberOfLines(String a_String) {
		try {
			setNumberOfLines(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _ref
	 * 
	 * @return int
	 * @see StationModel#_ref
	 */
	public int getRef() {
		return _ref;
	}

	/**
	 * Write accessor (setter) for attribute _ref
	 * 
	 * @param x
	 *            new value for _ref
	 * @see StationModel#_ref
	 */
	public void setRef(int x) {
		_ref = x;
	}

	/**
	 * Read accessor (getter) for attribute _rowNumber
	 * 
	 * @return int
	 * @see StationModel#_rowNumber
	 */
	public int getRowNumber() {
		return _rowNumber;
	}

	/**
	 * Write accessor (setter) for attribute _rowNumber
	 * 
	 * @param x
	 *            new value for _rowNumber
	 * @see StationModel#_rowNumber
	 */
	public void setRowNumber(int x) {
		_rowNumber = x;
	}

	/**
	 * Write accessor (setter) for attribute _rowNumber
	 * 
	 * @param a_String
	 *            new value for _rowNumber as String
	 * @see StationModel#_rowNumber
	 */
	public void setRowNumber(String a_String) {
		try {
			setRowNumber(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Remove any link for role model
	 */
	public void releaseModel() {
		setModel(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public Station getModel() {
		return _model;
	}

	/**
	 * Create a new link with x for role model
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setModel(Station x) {
		if (_model == x)
			return;
		_model = x;
	}

	/**
	 * Remove any link for role CDI
	 */
	public void releaseCdi() {
		setCdi(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public Cdi getCdi() {
		return _cdi;
	}

	/**
	 * Create a new link with x for role model
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setCdi(Cdi x) {
		if (_cdi == x)
			return;
		_cdi = x;
	}

	/**
	 * Remove any link for role Trajectory
	 */
	public void releaseTrajectory() {
		setTrajectory(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public Trajectory getTrajectory() {
		return _trajectory;
	}

	/**
	 * Create a new link with x for role model
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setTrajectory(Trajectory x) {
		if (_trajectory == x)
			return;
		_trajectory = x;
	}

	/**
	 * Remove any link for role referenceProperty
	 */
	public void releaseReferenceProperty() {
		setReferenceProperty(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public PropertyModel getReferenceProperty() {
		return _referenceProperty;
	}

	/**
	 * Create a new link with x for role referenceProperty
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setReferenceProperty(PropertyModel x) {
		if (_referenceProperty == x)
			return;
		_referenceProperty = x;
	}

	/**
	 * release all links for role "properties"
	 */
	public void releaseProperties() {
		for (PropertyModel t : getProperties()) {
			removeProperty(t);
		}
	}

	/**
	 * remove link with x for role "properties"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public void removeProperty(PropertyModel x) {
		if (!propertiesIncludes(x))
			return;
		nocheckRemoveProperty(x);
	}

	/**
	 * Add a link for role "properties"
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void addProperty(PropertyModel x) {
		if (propertiesIncludes(x))
			return;
		nocheckAddProperty(x);
	}

	/**
	 * Returns the collection of linked objects for role "properties"
	 * 
	 * @return Array of linked objects
	 */
	public PropertyModel[] getProperties() {
		PropertyModel[] v = new PropertyModel[_properties.size()];
		_properties.values().toArray(v);
		return v;
	}

	/**
	 * Returns linked object for role "properties" at index i
	 * 
	 * @param i
	 *            the index of the linked object
	 * @return linked object at index i
	 */
	public PropertyModel propertiesAt(String i) {
		return _properties.get(i);
	}

	/**
	 * remove x from the linked objects for role "properties"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see StationModel#removeProperty(PropertyModel)
	 */
	protected void nocheckRemoveProperty(PropertyModel x) {
		_properties.remove(x.getProperty());
	}

	/**
	 * adds x to the linked objects for role "properties"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see StationModel#addProperty(PropertyModel)
	 */
	protected void nocheckAddProperty(PropertyModel x) {
		_properties.put(x.getProperty(), x);
	}

	/**
	 * returns the number of linked objects for role "properties"
	 * 
	 * @return the number of linked objects
	 */
	public int propertiesSize() {
		return _properties.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "properties"
	 * 
	 * @param x
	 *            an instance of PropertyModel
	 */
	public boolean propertiesIncludes(PropertyModel x) {
		return _properties.containsKey(x.getProperty());
	}

	/**
	 * returns an Iterator to iterate over the elements of "properties"
	 * 
	 */
	public java.util.Iterator<PropertyModel> propertiesIterator() {
		return _properties.values().iterator();

	}

	/**
	 * Remove any link for role timeSeriesModel
	 */
	public void releaseTimeSeriesModel() {
		setTimeSeriesModel(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public TimeSeriesModel getTimeSeriesModel() {
		return _timeSeriesModel;
	}

	/**
	 * Create a new link with x for role timeSeriesModel
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setTimeSeriesModel(TimeSeriesModel x) {
		if (_timeSeriesModel == x)
			return;
		TimeSeriesModel old = _timeSeriesModel;
		if (_timeSeriesModel != null) {
			_timeSeriesModel = null;
			old.setStationModel(null);
		}
		_timeSeriesModel = x;
		if (x != null) {
			x.setStationModel(this);
		}
	}

	/**
	 * release all links for role "parameterModels"
	 */
	public void releaseParameterModels() {
		for (ParameterModel t : getParameterModels()) {
			removeParameterModel(t);
		}
	}

	/**
	 * remove link with x for role "parameterModels"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public void removeParameterModel(ParameterModel x) {
		if (!parameterModelsIncludes(x))
			return;
		nocheckRemoveParameterModel(x);
	}

	/**
	 * Add a link for role "parameterModels"
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void addParameterModel(ParameterModel x) {
		if (parameterModelsIncludes(x))
			return;
		nocheckAddParameterModel(x);
	}

	/**
	 * Returns the collection of linked objects for role "parameterModels"
	 * 
	 * @return Array of linked objects
	 */
	public ParameterModel[] getParameterModels() {
		ParameterModel[] v = new ParameterModel[_parameterModels.size()];
		_parameterModels.toArray(v);
		return v;
	}

	/**
	 * Returns linked object for role "parameterModels" at index i
	 * 
	 * @param i
	 *            the index of the linked object
	 * @return linked object at index i
	 */
	public ParameterModel parameterModelsAt(int i) {
		return (ParameterModel) _parameterModels.get(i);
	}

	/**
	 * returns index of object x in "parameterModels"
	 * 
	 * @param x
	 *            the linked object
	 */
	public int parameterModelsIndexOf(ParameterModel x) {
		if (!parameterModelsIncludes(x))
			return -1;
		for (int i = 0; i < parameterModelsSize(); i++) {
			if (parameterModelsAt(i) == x) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * move x one index before in the list for role "parameterModels"
	 * 
	 * @param x
	 *            the object to move
	 */
	public void parameterModelsMoveUp(ParameterModel x) {
		int index;
		index = parameterModelsIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveParameterModelAt(x, index - 1);
	}

	/**
	 * move x to the top of list for role "parameterModels"
	 * 
	 * @param x
	 *            the object to move
	 */
	public void parameterModelsMoveTop(ParameterModel x) {
		int index;
		index = parameterModelsIndexOf(x);
		if (index < 1)
			return;
		nocheckMoveParameterModelAt(x, 0);
	}

	/**
	 * move x one index more in the list for role "parameterModels"
	 * 
	 * @param x
	 *            the object to move
	 */
	public void parameterModelsMoveDown(ParameterModel x) {
		int index;
		index = parameterModelsIndexOf(x);
		if (index < 0 || index > parameterModelsSize() - 2)
			return;
		nocheckMoveParameterModelAt(x, index + 1);
	}

	/**
	 * move x to the bottom of list for role "parameterModels"
	 * 
	 * @param x
	 *            the object to move
	 */
	public void parameterModelsMoveBottom(ParameterModel x) {
		int index;
		index = parameterModelsIndexOf(x);
		if (index < 0 || index > parameterModelsSize() - 2)
			return;
		nocheckMoveParameterModelAt(x, parameterModelsSize() - 1);
	}

	/**
	 * move x one index more in the list for role "parameterModels"
	 * 
	 * @param x
	 *            the object to move
	 */
	public void nocheckMoveParameterModelAt(ParameterModel x, int a_Index) {
		int index;
		ParameterModel tmp;
		index = parameterModelsIndexOf(x);
		tmp = _parameterModels.get(index);
		_parameterModels.remove(index);
		_parameterModels.add(a_Index, tmp);
	}

	/**
	 * remove x from the linked objects for role "parameterModels"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see StationModel#removeParameterModel(ParameterModel)
	 */
	protected void nocheckRemoveParameterModel(ParameterModel x) {
		_parameterModels.remove(x);
	}

	/**
	 * adds x to the linked objects for role "parameterModels"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see StationModel#addParameterModel(ParameterModel)
	 */
	protected void nocheckAddParameterModel(ParameterModel x) {
		_parameterModels.add(x);
	}

	/**
	 * returns the number of linked objects for role "parameterModels"
	 * 
	 * @return the number of linked objects
	 */
	public int parameterModelsSize() {
		return _parameterModels.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "parameterModels"
	 * 
	 * @param x
	 *            an instance of ParameterModel
	 */
	public boolean parameterModelsIncludes(ParameterModel x) {
		return _parameterModels.contains(x);
	}

	/**
	 * returns an Iterator to iterate over the elements of "parameterModels"
	 * 
	 */
	public java.util.Iterator<ParameterModel> parameterModelsIterator() {
		return _parameterModels.iterator();

	}

	/**
	 * Remove any link for role timestampModel
	 */
	public void releaseTimestampModel() {
		setTimestampModel(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public HorodatedParameterModel getTimestampModel() {
		return _timestampModel;
	}

	/**
	 * Create a new link with x for role timestampModel
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setTimestampModel(HorodatedParameterModel x) {
		if (_timestampModel == x)
			return;
		_timestampModel = x;
	}

	/**
	 * 
	 * @param a_Station
	 */
	public void computeEndDate(Station a_Station) {
		TimeSeriesInformation timeSeries = a_Station.getTimeSeriesInformation();
		if (Float.isNaN(timeSeries.getEndDate())) {
			long rate = new Float(timeSeries.getSamplingRate() * 1000.0f)
					.longValue();
			int size = a_Station.parametersAt(0).valuesSize() - 1;
			long date = a_Station.getFullDate() + (size * rate);
			TimeZone tz = TimeZone.getTimeZone("GMT");
			Calendar cal = Calendar.getInstance(tz);
			cal.clear();
			cal.setTimeInMillis(date);
			/*
			 * Calendar cal2 = Calendar.getInstance(tz); cal2.clear();
			 * cal2.setTimeZone(tz); cal2.set(Calendar.HOUR_OF_DAY,
			 * cal.get(Calendar.HOUR_OF_DAY)); cal2.set(Calendar.MINUTE,
			 * cal.get(Calendar.MINUTE)); cal2.set(Calendar.SECOND,
			 * cal.get(Calendar.SECOND));
			 * timeSeries.setEndTime(cal2.getTimeInMillis());
			 */
			timeSeries.setEndTime(cal.getTimeInMillis());
			/*
			 * cal2.clear(); cal2.setTimeZone(tz); cal2.set(Calendar.YEAR,
			 * cal.get(Calendar.YEAR)); cal2.set(Calendar.MONTH,
			 * cal.get(Calendar.MONTH)); cal2.set(Calendar.DAY_OF_MONTH,
			 * cal.get(Calendar.DAY_OF_MONTH));
			 * timeSeries.setEndDate(cal2.getTimeInMillis());
			 */
			timeSeries.setEndDate(cal.getTimeInMillis());
		}
	}

	/**
	 * 
	 * @param a_Reader
	 * @return String[]
	 * @exception java.io.IOException
	 */
	public String[] consumeFooter(BufferedReader a_Reader) throws IOException {
		EditConversion.incrementErreurLigne();
		Vector<String> v = new Vector<String>();
		for (int i = 0; i < _endData; i++) {
			String line = a_Reader.readLine();
			EditConversion.incrementErreurLigne();
			EditConversion.setErreurChaine(line);
			if (line != null) {
				v.add(line);
			}
		}
		String[] lines = new String[v.size()];
		return v.toArray(lines);
	}

	/**
	 * 
	 * @param a_Reader
	 * @return String[]
	 * @exception java.io.IOException
	 */
	public String[] consumeHeader(BufferedReader a_Reader) throws IOException {
		Vector<String> v = new Vector<String>();
		for (int i = 0; i < _numberOfLines; i++) {

			String line = a_Reader.readLine();
			EditConversion.incrementErreurLigne();
			EditConversion.setErreurChaine(line);
			if (line != null) {
				v.add(line);
			} else {
				return null;
			}
		}
		String[] lines = new String[v.size()];
		return v.toArray(lines);
	}

	/**
	 * 
	 * @param a_Reader
	 * @return String[]
	 * @exception java.io.IOException
	 */
	public String[] consumeHeaderPlusOne(BufferedReader a_Reader)
			throws IOException {
		EditConversion.setErreurLigne(0);
		Vector<String> v = new Vector<String>();
		String line = null;
		for (int i = 0; i < _numberOfLines; i++) {
			line = a_Reader.readLine();
			EditConversion.incrementErreurLigne();
			EditConversion.setErreurChaine(line);
			if (line != null) {
				v.add(line);
			} else {
				return null;
			}
		}
		line = a_Reader.readLine();
		EditConversion.incrementErreurLigne();
		EditConversion.setErreurChaine(line);
		v.add(line);

		String[] lines = new String[v.size()];
		return v.toArray(lines);
	}

	/**
	 * 
	 * @param a_Reader
	 * @return String[]
	 * @exception java.io.IOException
	 */
	public void consumeHeaderPlus(BufferedReader a_Reader) throws IOException {
		Vector<String> v = new Vector<String>();
		for (int i = 0; i < _numberOfLines; i++) {
			String line = a_Reader.readLine();
			EditConversion.incrementErreurLigne();
			EditConversion.setErreurChaine(line);
			if (line != null) {
				v.add(line);
			} else {
				_headerPlus = null;
			}
		}

		v.add("");

		String[] lines = new String[v.size()];
		_headerPlus = v.toArray(lines);
	}

	/**
	 * 
	 * @param a_Reader
	 * @param a_Filename
	 * @param isRefDateBased
	 * @param a_RefDate
	 * @param a_Notifier
	 * @return Station
	 * @exception IOException
	 * @exception java.text.ParseException
	 */
	public Station convert(BufferedReader a_Reader, String a_Filename,
			boolean isRefDateBased, long a_RefDate,
			SizeBasedProgressNotifier a_Notifier) throws IOException,
			ParseException, StringIndexOutOfBoundsException {
		Station station = null;
		String[] header = null;

		try {
			station = (Station) _model.clone();
			station.setMetaFlags(new HashMap<String, String>());

			if ((_timeSeriesModel != null /* && _timestampModel.isComputed() */&& _trajectory == null)
					|| (_trajectory != null && _trajectory.getOutputType()
							.equals(Global.MEDATLAS)) && !isMetaInData2()) {
				// Marquage du fichier au debut
				a_Reader.mark(getTaille());
				if ((header = consumeHeaderPlusOne(a_Reader)) != null) {
					// a_Notifier.progress(a_Reader.getFilePointer());
					if ((getReferenceProperty().getFragment().getFirstLine() == 0
							&& (a_Filename != null) && !(isIncrementRef()))) {
						if (ViewFile.collectionCasN) {
							String[] filename = DataModel.singleSurvey
									.getFile().split("/");
							getReferenceProperty()
									.applyOn(
											station,
											new String[] { filename[filename.length - 1] });
						} else
							getReferenceProperty().applyOn(station,
									new String[] { a_Filename });
					} else {
						if (isIncrementRef()) {
							station.setReference(Integer.toString(_ref++));
						} else {
							getReferenceProperty().applyOn(station, header);
						}
					}
					for (PropertyModel m : getProperties()) {
						// System.out.println("@DEBUG propertyModel
						// courant
						// :"+m.getProperty());
						m.applyOn(station, header);
						addFlag(m, station, header);
					}
					if (_timeSeriesModel != null) {
						_timeSeriesModel.convert(station, header,
								isRefDateBased, a_RefDate);
					}
					// a_Notifier.progress(a_Reader.getFilePointer());

					// Retour au debut du fichier et depassement du header
					a_Reader.reset();
					EditConversion.setErreurLigne(0);
					consumeHeader(a_Reader);

					convertParameters(a_Reader, station, isRefDateBased,
							a_RefDate, a_Notifier);
					if (_timeSeriesModel != null
							&& Float.isNaN(station.getTimeSeriesInformation()
									.getEndDate())) {
						computeEndDate(station);
					}
					consumeFooter(a_Reader);
					// a_Notifier.progress(a_Reader.getFilePointer());

					return station;
				}

			} else if (isMetaInData()) {
				consumeHeader(a_Reader);

				convertParameters(a_Reader, station, isRefDateBased, a_RefDate,
						a_Notifier);
				if (_timeSeriesModel != null
						&& Float.isNaN(station.getTimeSeriesInformation()
								.getEndDate())) {
					computeEndDate(station);
				}
				// a_Notifier.progress(a_Reader.getFilePointer());

				consumeFooter(a_Reader);
				// a_Notifier.progress(a_Reader.getFilePointer());
				return station;

			} else if (isMetaInData2()
					|| (_trajectory != null && _trajectory.getOutputType()
							.equals(Global.ODV))) {
				int index;
				String line = null;
				boolean initStation = false;

				for (index = 0; (line = a_Reader.readLine()) != null
						&& (!(_endOnEmptyLine && line.trim().matches("^$")))
						&& (!(_endOnPattern && line.matches("^"
								+ _endDataPattern + "$")))
						|| (index < _rowNumber); index++) {

					_headerPlus[_headerPlus.length - 1] = line;
					EditConversion.incrementErreurLigne();
					EditConversion.setErreurChaine(line);

					a_Notifier.progressLine(EditConversion.getErreurLigne(),
							EditConversion.getTotalLigne());

					// Recuperation du numero de station
					if (isMetaInData2()) {
						getReferenceProperty().applyOn(station, _headerPlus);
					} else {
						if ((getReferenceProperty().getFragment()
								.getFirstLine() == 0
								&& (a_Filename != null) && !(isIncrementRef()))) {
							if (ViewFile.collectionCasN) {
								String[] filename = DataModel.singleSurvey
										.getFile().split("/");
								getReferenceProperty()
										.applyOn(
												station,
												new String[] { filename[filename.length - 1] });
							} else
								getReferenceProperty().applyOn(station,
										new String[] { a_Filename });
						} else {
							if (isIncrementRef()) {
								station.setReference(Integer.toString(_ref++));
							} else {
								getReferenceProperty().applyOn(station, header);
							}
						}
					}

					String refTemp = station.getReference();

					// Comparaison du numero de station en cours avec celui de
					// la derniere station
					if (!initStation) {

						for (PropertyModel m : getProperties()) {
							m.applyOn(station, _headerPlus);
							addFlag(m, station, header);
						}

						for (ParameterModel pm : _parameterModels) {
							station.addParameter((Parameter) pm.getModel()
									.clone());
						}

						if (_timeSeriesModel != null) {
							_timeSeriesModel.convert(station, _headerPlus,
									isRefDateBased, a_RefDate);
						}

						initStation = true;
					}

					if (getLastRef() != null) {
						// FAE 0001135 : reformatage des fichiers ODV 
						//      (si la reference de la station est vide, considerer la ligne comme appartenant e la derniere station referencee)
						if (!refTemp.equals(getLastRef()) && !(refTemp.replace("\r\n", "")).trim().equals("")) {
							station.setReference(getLastRef());
							setLastRef(refTemp);

							a_Reader.reset();
							EditConversion.setErreurLigne(EditConversion
									.getErreurLigne() - 1);
							_ref--;
							return station;
						}
					} else {
						setLastRef(station.getReference());
					}

					a_Reader.mark(getTaille());

					long rate = 0;
					if (station.getTimeSeriesInformation() != null) {
						rate = new Float(station.getTimeSeriesInformation()
								.getSamplingRate() * 1000.0f).longValue();
						org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
						parser.addStandardFunctions();
						parser.addStandardConstants();
						parser.addVariable("x", rate); //$NON-NLS-1$
						parser.parseExpression("x"
								+ getTimeSeriesModel()
										.getSampleRateConversion());

						Double resultJEP = parser.getValue();
						rate = resultJEP.longValue();

						station.getTimeSeriesInformation().setSamplingRate(
								rate / 1000);
					}

					int paramIndex = 0;

					for (ParameterModel pm : _parameterModels) {
						Parameter p = station.parametersAt(paramIndex);
						ParameterValue value = null;
						if (_timeSeriesModel != null) {
							if (_timestampModel != null) {
								HorodatedValue hValue = new HorodatedValue();
								if (_timestampModel.isComputed()) {
									hValue.setDate(station.getFullDate()
											+ (index * rate));
								} else {
									long time = _timestampModel
											.convertTimestamp(
													_headerPlus[_headerPlus.length - 1],
													a_RefDate);
									hValue.setDate(station.getGMTDate(time));

									// FAE086 START
									long ecart = (time - station.getFullDate()) / 1000;

									if (ecart > 0
											&& station
													.getTimeSeriesInformation()
													.getSamplingRate() == 0) {
										station.getTimeSeriesInformation()
												.setSamplingRate(ecart);
									}
									// FAE086 END
								}
								value = hValue;
							} else {
								value = new ParameterValue();
							}
						} else {
							value = new ParameterValue();
						}

						pm.convert(_headerPlus[_headerPlus.length - 1], value,
								getFlagTable());
						p.addValue(value);
						paramIndex++;
					}

					if (_timeSeriesModel != null
							&& Float.isNaN(station.getTimeSeriesInformation()
									.getEndDate())) {
						computeEndDate(station);
					}
				}

				if (line == null && !station.getReference().equals("")) {
					// consumeFooter(a_Reader);

					return station;
				}

			} else {
				if ((header = consumeHeader(a_Reader)) != null) {
					// a_Notifier.progress(a_Reader.getFilePointer());
					if ((getReferenceProperty().getFragment().getFirstLine() == 0
							&& (a_Filename != null) && !(isIncrementRef()))) {
						if (ViewFile.collectionCasN) {
							String[] filename = DataModel.singleSurvey
									.getFile().split("/");
							getReferenceProperty()
									.applyOn(
											station,
											new String[] { filename[filename.length - 1] });
						} else
							getReferenceProperty().applyOn(station,
									new String[] { a_Filename });
					} else {
						if (isIncrementRef()) {
							station.setReference(Integer.toString(_ref++));
						} else {
							getReferenceProperty().applyOn(station, header);
						}
					}

					for (PropertyModel m : getProperties()) {
						m.applyOn(station, header);
						addFlag(m, station, header);
					}

					if (_timeSeriesModel != null) {
						_timeSeriesModel.convert(station, header,
								isRefDateBased, a_RefDate);
					}
					// a_Notifier.progress(a_Reader.getFilePointer());
					convertParameters(a_Reader, station, isRefDateBased,
							a_RefDate, a_Notifier);
					if (_timeSeriesModel != null
							&& Float.isNaN(station.getTimeSeriesInformation()
									.getEndDate())) {
						computeEndDate(station);
					}
					consumeFooter(a_Reader);
					// a_Notifier.progress(a_Reader.getFilePointer());

					return station;
				}
			}
		} catch (StringIndexOutOfBoundsException e) {
			throw e;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param a_Reader
	 * @param a_Station
	 * @param isRefDateBased
	 * @param a_RefDate
	 * @param a_Notifier
	 * @exception IOException
	 * @exception ParseException
	 */
	public void convertParameters(BufferedReader a_Reader, Station a_Station,
			boolean isRefDateBased, long a_RefDate,
			SizeBasedProgressNotifier a_Notifier) throws IOException,
			ParseException {
		long rate = 0;
		if (a_Station.getTimeSeriesInformation() != null) {
			rate = new Float(a_Station.getTimeSeriesInformation()
					.getSamplingRate() * 1000.0f).longValue();
			org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
			parser.addStandardFunctions();
			parser.addStandardConstants();
			parser.addVariable("x", rate); //$NON-NLS-1$
			parser.parseExpression("x"
					+ getTimeSeriesModel().getSampleRateConversion());

			Double resultJEP = parser.getValue();
			rate = resultJEP.longValue();

			a_Station.getTimeSeriesInformation().setSamplingRate(rate / 1000);
		}
		int index = 0;
		try {
			for (ParameterModel pm : _parameterModels) {
				a_Station.addParameter((Parameter) pm.getModel().clone());

			}
			String line = null;
			long size = 0;
			for (index = 0; (line = a_Reader.readLine()) != null
					&& (!(_endOnEmptyLine && line.trim().matches("^$")))
					&& (!(_endOnPattern && line.matches("^" + _endDataPattern
							+ "$"))) || (index < _rowNumber); index++) {

				size += line.length();
				a_Notifier.progress(size);

				EditConversion.incrementErreurLigne();
				EditConversion.setErreurChaine(line);

				if (index == 0 && isMetaInData()) {
					String[] header = new String[] { line };
					if (isIncrementRef()) {
						a_Station.setReference(Long.toString(_ref++));
					} else {
						getReferenceProperty().applyOn(a_Station, header);
					}
					for (PropertyModel m : getProperties()) {
						m.applyOn(a_Station, header);
						addFlag(m, a_Station, header);
					}
					if (_timeSeriesModel != null) {
						_timeSeriesModel.convert(a_Station, header,
								isRefDateBased, a_RefDate);
					}
				}
				int paramIndex = 0;
				for (ParameterModel pm : _parameterModels) {
					Parameter p = a_Station.parametersAt(paramIndex);
					ParameterValue value = null;
					if (_timeSeriesModel != null) {
						if (_timestampModel != null) {
							HorodatedValue hValue = new HorodatedValue();
							if (_timestampModel.isComputed()) {
								hValue.setDate(a_Station.getFullDate()
										+ (index * rate));
							} else {
								long time = _timestampModel.convertTimestamp(
										line, a_RefDate);
								hValue.setDate(a_Station.getGMTDate(time));

								// FAE086 START
								long ecart = (time - a_Station.getFullDate()) / 1000;

								if (ecart > 0
										&& a_Station.getTimeSeriesInformation()
												.getSamplingRate() == 0) {
									a_Station.getTimeSeriesInformation()
											.setSamplingRate(ecart);
								}
								// FAE086 END
							}
							value = hValue;
						} else {
							value = new ParameterValue();
						}
					} else {
						value = new ParameterValue();
					}

					if (EditConversion.getErreurLigne() == 4
							&& pm.getModel().getCode().equals("NTRI")) {
						System.out.println();
					}
					pm.convert(line, value, getFlagTable());
					p.addValue(value);
					paramIndex++;
				}
				// a_Notifier.progress(a_Reader.getFilePointer());
			}
		} catch (NumberFormatException e) {
			throw new NumberFormatException(e.getMessage()
					+ Messages.getString("StationModel.1")
					+ a_Station.getReference().trim()
					+ Messages.getString("StationModel.2") + (index + 1)
					+ "\r\n");
		} catch (ParseException e) {
			throw new ParseException(e.getMessage()
					+ Messages.getString("StationModel.1")
					+ a_Station.getReference().trim()
					+ Messages.getString("StationModel.2") + (index + 1)
					+ "\r\n", 0);
		} catch (StringIndexOutOfBoundsException e) {
			throw new StringIndexOutOfBoundsException(Messages
					.getString("StationModel.1")
					+ a_Station.getReference().trim()
					+ Messages.getString("StationModel.2")
					+ (index + 1)
					+ Messages.getString("StationModel.3"));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return TimeSeriesInformation
	 */
	public TimeSeriesInformation getTimeSeriesInformation() {
		return getModel().getTimeSeriesInformation();
	}

	/**
	 * 
	 * @param path
	 *            the association path name
	 * @param a_URI
	 *            a_URI XML Namespace (SAX 2)
	 * @param a_LocalName
	 *            Tag name
	 * @param a_QName
	 *            Qualified tag name
	 * @param a_Attributes
	 *            XML Element attributes
	 * @return SAXConsumer
	 * @exception SAXException
	 */
	public SAXConsumer handlePathElement(String path, String a_URI,
			String a_LocalName, String a_QName, Attributes a_Attributes)
			throws SAXException {
		SAXConsumer c = null;
		if (path.equals("parameters")) {
			c = ParameterModel.fromElement(a_URI, a_LocalName, a_QName,
					a_Attributes);
			addParameterModel((ParameterModel) c);
			return c;
		}
		if (path.equals("properties")) {
			c = PropertyModel.fromElement(a_URI, a_LocalName, a_QName,
					a_Attributes);
			((PropertyModel) c).setSource(_model);
			addProperty((PropertyModel) c);
			return c;
		}
		if (path.equals("referenceProperty")) {
			c = PropertyModel.fromElement(a_URI, a_LocalName, a_QName,
					a_Attributes);
			((PropertyModel) c).setSource(_model);
			setReferenceProperty((PropertyModel) c);
			return c;
		}
		return null;
	}

	/**
	 * 
	 */
	public void initProperties() {
		try {
			setReferenceProperty(new PropertyModel("reference", _model));
			addProperty(new PropertyModel("date", _model, new DateTimeFormat(
					"dd/MM/yyyy")));
			addProperty(new PropertyModel("time", _model, new DateTimeFormat(
					"hh:mm AM")));
			addProperty(new PropertyModel("offsetTU", _model, new FloatFormat(
					"%4.1f")));
			addProperty(new PropertyModel("latitude", _model,
					new LatitudeFormat("NDD MM SS")));
			addProperty(new PropertyModel("longitude", _model,
					new LongitudeFormat("NDDD MM SS")));
			addProperty(new PropertyModel("bottomDepth", _model,
					new FloatFormat("%4.0f")));
			addProperty(new PropertyModel("dcHistory1", _model));
			addProperty(new PropertyModel("dcHistory2", _model));
			// addProperty( new PropertyModel( "dmHistory1", _model));
			// addProperty( new PropertyModel( "dmHistory2", _model));
			addProperty(new PropertyModel("surfaceSamples1", _model));
			addProperty(new PropertyModel("surfaceSamples2", _model));
			addProperty(new PropertyModel("comment", _model));
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 */
	public void initRef() {
		if (_ref == -1)
			_ref = getIncrementNumber();
	}

	public void resetRef() {
		_ref = -1;
	}

	public boolean isMetaInData2() {
		return _metaInData2;
	}

	public void setMetaInData2(boolean inData2) {
		_metaInData2 = inData2;
	}

	/**
	 * Write accessor (setter) for attribute _metaInData2
	 * 
	 * @param a_String
	 *            new value for _metaInData2 as String
	 * @see StationModel#_metaInData2
	 */
	public void setMetaInData2(String a_String) {
		setMetaInData2(new Boolean(a_String).booleanValue());
	}

	public String getLastRef() {
		return _lastRef;
	}

	public void setLastRef(String ref) {
		_lastRef = ref;
	}

	public int getTaille() {
		return _taille;
	}

	public void setTaille(int _taille) {
		this._taille = _taille;
	}

	public String getInputType() {
		return _inputType;
	}

	public void setInputType(String type) {
		_inputType = type;
	}

	public boolean isProfile() {
		return _inputType.equals(Global.PROFILE);
	}

	public boolean isTimeSeries() {
		return _inputType.equals(Global.TIMESERIES);
	}

	public boolean isTrajectory() {
		return _inputType.equals(Global.TRAJECTORY);
	}

	public boolean testDepth() {
		//TODO Stocker le resultat dans une variable a reinitialiser a chaque conversion
		if (SWTProcess.odv) {
			if (_parameterModels.get(0).getModel().getCode().equals("DEPH")) {
				return false;
			} else {
				for (int i = 0; i < _parameterModels.size(); i++) {
					if (_parameterModels.get(i).getModel().getCode().equals(
							"PRES")) {
						return true;
					}
				}
				return false;
			}
		} else {
			return false;
		}
	}

	public String headerDepth() {
		if (testDepth()) {
			for (int i = 0; i < NemoLists.getParameters().length; i++) {
				if (NemoLists.getParameters()[i].getCode().equals("DEPH")) {
					setDephFormat(NemoLists.getParameters()[i].getFormat());
					break;
				}
			}
			searchPresIndex();

			return Utils.configuration.getSep() + "DEPH [m]"
					+ Utils.configuration.getSep()
					+ Utils.configuration.getSdnHeader();
		} else {
			setPresIndex(-1);
			return "";
		}
	}

	// FAE091 : calcul de la profondeur en fonction de la pression
	public String calculDepth(Station station, int index) {
		if (testDepth()) {
			if (getPresIndex() > -1) {
				double X, Gr, P, Depth;

				String QC = station.getParameters()[getPresIndex()].getValues()[index]
						.getQc();
				if (QC.equals("")) {
					QC = "0";
				}

				P = station.getParameters()[getPresIndex()].getValues()[index]
						.getValue();
				X = Math.sin(station.getLatitude() / 57.29578);
				X = X * X;

				Gr = 9.780318 * (1.0 + (5.2788E-3 + 2.36E-5 * X) * X)
						+ 1.092E-6 * P;
				Depth = (((-1.82E-15 * P + 2.279E-10) * P - 2.2512E-5) * P + 9.72659)
						* P;
				Depth = Depth / Gr;
				
				return Utils.configuration.getSep()
						+ Formatter.getSingleton().formatDouble(
								getDephFormat(), Depth)
						+ Utils.configuration.getSep() + QC;
			} else {
				return Utils.configuration.getSep()
						+ Utils.configuration.getSep();
			}
		} else {
			return "";
		}
	}

	private void searchPresIndex() {
		int i;
		int size = _parameterModels.size();

		for (i = 0; i < size; i++) {
			if (_parameterModels.get(i).getModel().getCode().equals("PRES")) {
				setPresIndex(i);
				return;
			}
		}

		setPresIndex(-1);
	}

	public String julianDate(Station station, int index) {
		if (station.parametersAt(0).valuesAt(index) instanceof HorodatedValue) {
			Long date = ((HorodatedValue) station.parametersAt(0).valuesAt(
					index)).getDate();

			JulianDate jDate = new JulianDate(date);

			return getDecimalFormat().format(jDate.getJour());
		}
		return "";
	}

	public int getPresIndex() {
		return _presIndex;
	}

	public void setPresIndex(int index) {
		_presIndex = index;
	}

	public String getDephFormat() {
		return _dephFormat;
	}

	public void setDephFormat(String format) {
		_dephFormat = format;
	}

	public DecimalFormat getDecimalFormat() {
		return _df;
	}

	public void setDecimalFormat(DecimalFormat _df) {
		this._df = _df;
	}

	public FlagTable getFlagTable() {
		if (_flagTable != null) {
			return _flagTable;
		} else {
			return Utils.getBlankFlagTable();
		}
	}

	public void setFlagTable(FlagTable table) {
		_flagTable = table;
	}

	public void addFlag(PropertyModel pm, Station station, String[] a_Header) {
		if (pm.getFlag() != null) {
			if (station.getMetaFlags() == null) {
				station.setMetaFlags(new HashMap<String, String>());
			}
			// Lecture du flag dans le fichier en entree
			String value = pm.getFlag().retreive(a_Header).trim();

			// Correspondance SDN
			if (getFlagTable() == null) {
				setFlagTable(new FlagTable(Utils.getBlankFlagTable()));
			}
			value = getFlagTable().getFlagSDN(value);

			// AJout dans la liste des flags de metadonnees
			String property = pm.getProperty();
			if (property.equals(Global.DATE) || property.equals(Global.TIME)) {
				property = Global.DATETIME;
			}
			station.getMetaFlags().put(property, value);
		}
	}
} /* End of class: StationModel */
