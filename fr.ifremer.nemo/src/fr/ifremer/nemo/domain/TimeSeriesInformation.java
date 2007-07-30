package fr.ifremer.nemo.domain;

import com.ago.xml.*;
import java.io.*;
import java.sql.Date;
import java.sql.Time;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class TimeSeriesInformation
 * 
 * Classe permettant la description des informations spécifiques aux séries
 * temporelles.
 * 
 * @author Gerard BUNEL
 */
public class TimeSeriesInformation implements Cloneable, SAXConsumer {

	/**
	 * End date/time of the Time series (MEDATLAS: 8 char DDMMYYYY)
	 */
	protected float _endDate;

	/**
	 * bottom depth in metres at the end of the Time series (MEDATLAS: 6 char)
	 */
	protected float _endDepth;

	/**
	 * Latitude at the end of the Time series (MEDATLAS: 9 char = NDD.MM.99 or
	 * SDD.MM.99)
	 */
	protected double _endLatitude;

	/**
	 * Longitude at the end of the Time series (MEDATLAS: 10 char = EDDD.MM.99
	 * or WDDD.MM.99)
	 */
	protected double _endLongitude;

	protected float _endTime;

	/**
	 * Magnetic declination in degrees (MEDATLAS: 4 char max)
	 */
	protected float _magneticDeclination;

	/**
	 * Quality flag for endDate, endTime, endLatitude, endLongitude and endDepth
	 * (MEDATLAS: 4 char max)
	 */
	protected String _qc1;

	/**
	 * Quality flag for sensorDepth and distToBottom (MEDATLAS: 2 char max)
	 */
	protected String _qc2;

	/**
	 * Quality flag for duration (MEDATLAS: 1 char max)
	 */
	protected String _qc3;

	/**
	 * Periode d'echantillonage en secondes. Sampling rate in seconds (MEDATLAS:
	 * 5 char)
	 */
	protected float _samplingRate;

	/**
	 * Sensor depth in metres (MEDATLAS: 5 char)
	 */
	protected float _sensorDepth;

	/**
	 * Station pour laquelle des mesures de series temporelles ont ete
	 * realisees.
	 */
	protected Station _station;

	public TimeSeriesInformation() {
		_endDate = Float.NaN;
		_endDepth = Float.NaN;
		_endLatitude = Double.NaN;
		_endLongitude = Double.NaN;
		_endTime = Float.NaN;
		_magneticDeclination = -999.0f;
		_qc1 = "00000";
		_qc2 = "00";
		_qc3 = "0";
		_sensorDepth = Float.NaN;
		_station = null;
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
		TimeSeriesInformation tmp = new TimeSeriesInformation();
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
		if (a_LocalName.equals("endDate")) {
			Element2Field setter = new Element2Field(this, "setEndDate");
			return setter;
		}
		if (a_LocalName.equals("endTime")) {
			Element2Field setter = new Element2Field(this, "setEndTime");
			return setter;
		}
		if (a_LocalName.equals("endLatitude")) {
			Element2Field setter = new Element2Field(this, "setEndLatitude");
			return setter;
		}
		if (a_LocalName.equals("endLongitude")) {
			Element2Field setter = new Element2Field(this, "setEndLongitude");
			return setter;
		}
		if (a_LocalName.equals("endDepth")) {
			Element2Field setter = new Element2Field(this, "setEndDepth");
			return setter;
		}
		if (a_LocalName.equals("qc1")) {
			Element2Field setter = new Element2Field(this, "setQc1");
			return setter;
		}
		if (a_LocalName.equals("sensorDepth")) {
			Element2Field setter = new Element2Field(this, "setSensorDepth");
			return setter;
		}
		if (a_LocalName.equals("qc2")) {
			Element2Field setter = new Element2Field(this, "setQc2");
			return setter;
		}
		if (a_LocalName.equals("qc3")) {
			Element2Field setter = new Element2Field(this, "setQc3");
			return setter;
		}
		if (a_LocalName.equals("samplingRate")) {
			Element2Field setter = new Element2Field(this, "setSamplingRate");
			return setter;
		}
		if (a_LocalName.equals("magneticDecination")) {
			Element2Field setter = new Element2Field(this,
					"setMagneticDeclination");
			return setter;
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
		a_Writer.putTag("endDate", getEndDate());
		a_Writer.putTag("endTime", getEndTime());
		a_Writer.putTag("endLatitude", getEndLatitude());
		a_Writer.putTag("endLongitude", getEndLongitude());
		a_Writer.putTag("endDepth", getEndDepth());
		a_Writer.putTag("qc1", getQc1());
		a_Writer.putTag("sensorDepth", getSensorDepth());
		a_Writer.putTag("qc2", getQc2());
		a_Writer.putTag("qc3", getQc3());
		a_Writer.putTag("samplingRate", getSamplingRate());
		a_Writer.putTag("magneticDecination", getMagneticDeclination());
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
		a_Writer.openTag("TimeSeriesInformation");
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
		TimeSeriesInformation copy = null;
		copy = (TimeSeriesInformation) super.clone();
		copy._station = null;
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
		releaseStation();
	}

	/**
	 * Read accessor (getter) for attribute _endDate
	 * 
	 * @return float
	 * @see TimeSeriesInformation#_endDate
	 */
	public float getEndDate() {
		return _endDate;
	}

	/**
	 * Write accessor (setter) for attribute _endDate
	 * 
	 * @param x
	 *            new value for _endDate
	 * @see TimeSeriesInformation#_endDate
	 */
	public void setEndDate(float x) {
		_endDate = x;
	}

	/**
	 * Write accessor (setter) for attribute _endDate
	 * 
	 * @param a_String
	 *            new value for _endDate as String
	 * @see TimeSeriesInformation#_endDate
	 */
	public void setEndDate(String a_String) {
		try {
			setEndDate(Float.parseFloat(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _endDepth
	 * 
	 * @return float
	 * @see TimeSeriesInformation#_endDepth
	 */
	public float getEndDepth() {
		return _endDepth;
	}

	/**
	 * Write accessor (setter) for attribute _endDepth
	 * 
	 * @param x
	 *            new value for _endDepth
	 * @see TimeSeriesInformation#_endDepth
	 */
	public void setEndDepth(float x) {
		_endDepth = x;
	}

	/**
	 * Write accessor (setter) for attribute _endDepth
	 * 
	 * @param a_String
	 *            new value for _endDepth as String
	 * @see TimeSeriesInformation#_endDepth
	 */
	public void setEndDepth(String a_String) {
		try {
			setEndDepth(Float.parseFloat(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Write accessor (setter) for attribute _endLatitude
	 * 
	 * @param x
	 *            new value for _endLatitude
	 * @see TimeSeriesInformation#_endLatitude
	 */
	public void setEndLatitude(double x) {
		_endLatitude = x;
	}

	/**
	 * Write accessor (setter) for attribute _endLatitude
	 * 
	 * @param a_String
	 *            new value for _endLatitude as String
	 * @see TimeSeriesInformation#_endLatitude
	 */
	public void setEndLatitude(String a_String) {
		try {
			setEndLatitude(Double.parseDouble(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Write accessor (setter) for attribute _endLongitude
	 * 
	 * @param x
	 *            new value for _endLongitude
	 * @see TimeSeriesInformation#_endLongitude
	 */
	public void setEndLongitude(double x) {
		_endLongitude = x;
	}

	/**
	 * Write accessor (setter) for attribute _endLongitude
	 * 
	 * @param a_String
	 *            new value for _endLongitude as String
	 * @see TimeSeriesInformation#_endLongitude
	 */
	public void setEndLongitude(String a_String) {
		try {
			setEndLongitude(Double.parseDouble(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _endTime
	 * 
	 * @return float
	 * @see TimeSeriesInformation#_endTime
	 */
	public float getEndTime() {
		return _endTime;
	}

	/**
	 * Write accessor (setter) for attribute _endTime
	 * 
	 * @param x
	 *            new value for _endTime
	 * @see TimeSeriesInformation#_endTime
	 */
	public void setEndTime(float x) {
		_endTime = x;
	}

	/**
	 * Write accessor (setter) for attribute _endTime
	 * 
	 * @param a_String
	 *            new value for _endTime as String
	 * @see TimeSeriesInformation#_endTime
	 */
	public void setEndTime(String a_String) {
		try {
			setEndTime(Float.parseFloat(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _magneticDeclination
	 * 
	 * @return float
	 * @see TimeSeriesInformation#_magneticDeclination
	 */
	public float getMagneticDeclination() {
		return _magneticDeclination;
	}

	/**
	 * Write accessor (setter) for attribute _magneticDeclination
	 * 
	 * @param x
	 *            new value for _magneticDeclination
	 * @see TimeSeriesInformation#_magneticDeclination
	 */
	public void setMagneticDeclination(float x) {
		_magneticDeclination = x;
	}

	/**
	 * Write accessor (setter) for attribute _magneticDeclination
	 * 
	 * @param a_String
	 *            new value for _magneticDeclination as String
	 * @see TimeSeriesInformation#_magneticDeclination
	 */
	public void setMagneticDeclination(String a_String) {
		try {
			setMagneticDeclination(Float.parseFloat(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _qc1
	 * 
	 * @return String
	 * @see TimeSeriesInformation#_qc1
	 */
	public String getQc1() {
		return _qc1;
	}

	/**
	 * Write accessor (setter) for attribute _qc1
	 * 
	 * @param x
	 *            new value for _qc1
	 * @see TimeSeriesInformation#_qc1
	 */
	public void setQc1(String x) {
		_qc1 = x;
	}

	/**
	 * Read accessor (getter) for attribute _qc2
	 * 
	 * @return String
	 * @see TimeSeriesInformation#_qc2
	 */
	public String getQc2() {
		return _qc2;
	}

	/**
	 * Write accessor (setter) for attribute _qc2
	 * 
	 * @param x
	 *            new value for _qc2
	 * @see TimeSeriesInformation#_qc2
	 */
	public void setQc2(String x) {
		_qc2 = x;
	}

	/**
	 * Read accessor (getter) for attribute _qc3
	 * 
	 * @return String
	 * @see TimeSeriesInformation#_qc3
	 */
	public String getQc3() {
		return _qc3;
	}

	/**
	 * Write accessor (setter) for attribute _qc3
	 * 
	 * @param x
	 *            new value for _qc3
	 * @see TimeSeriesInformation#_qc3
	 */
	public void setQc3(String x) {
		_qc3 = x;
	}

	/**
	 * Read accessor (getter) for attribute _samplingRate
	 * 
	 * @return float
	 * @see TimeSeriesInformation#_samplingRate
	 */
	public float getSamplingRate() {
		return _samplingRate;
	}

	/**
	 * Write accessor (setter) for attribute _samplingRate
	 * 
	 * @param x
	 *            new value for _samplingRate
	 * @see TimeSeriesInformation#_samplingRate
	 */
	public void setSamplingRate(float x) {
		_samplingRate = x;
	}

	/**
	 * Write accessor (setter) for attribute _samplingRate
	 * 
	 * @param a_String
	 *            new value for _samplingRate as String
	 * @see TimeSeriesInformation#_samplingRate
	 */
	public void setSamplingRate(String a_String) {
		try {
			setSamplingRate(Float.parseFloat(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _sensorDepth
	 * 
	 * @return float
	 * @see TimeSeriesInformation#_sensorDepth
	 */
	public float getSensorDepth() {
		return _sensorDepth;
	}

	/**
	 * Write accessor (setter) for attribute _sensorDepth
	 * 
	 * @param x
	 *            new value for _sensorDepth
	 * @see TimeSeriesInformation#_sensorDepth
	 */
	public void setSensorDepth(float x) {
		_sensorDepth = x;
	}

	/**
	 * Write accessor (setter) for attribute _sensorDepth
	 * 
	 * @param a_String
	 *            new value for _sensorDepth as String
	 * @see TimeSeriesInformation#_sensorDepth
	 */
	public void setSensorDepth(String a_String) {
		try {
			setSensorDepth(Float.parseFloat(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Remove any link for role station
	 */
	public void releaseStation() {
		setStation(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public Station getStation() {
		return _station;
	}

	/**
	 * Create a new link with x for role station
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setStation(Station x) {
		if (_station == x)
			return;
		Station old = _station;
		if (_station != null) {
			_station = null;
			old.setTimeSeriesInformation(null);
		}
		_station = x;
		if (x != null) {
			x.setTimeSeriesInformation(this);
		}
	}

	/**
	 * 
	 * @return float
	 */
	public float getDistToBottom() {
		if (Float.isNaN(_endDepth) || Float.isNaN(_sensorDepth)) {
			return Float.NaN;
		} else {
			return _endDepth - _sensorDepth;
		}
	}

	/**
	 * 
	 * @return float
	 */
	public float getDuration() {
		long startDate = getStation().getFullDate();
		float duration = getEndDate() + getEndTime()
				- new Long(startDate).floatValue(); // duree en millisecondes
		return duration / (1000.0f * 60.0f * 60.0f * 24.0f);
	}

	/**
	 * 
	 * @return double
	 */
	public double getEndLatitude() {
		if (Double.isNaN(_endLatitude)) {
			return getStation().getLatitude();
		} else {
			return _endLatitude;
		}
	}

	/**
	 * 
	 * @return double
	 */
	public double getEndLongitude() {
		if (Double.isNaN(_endLongitude)) {
			return getStation().getLongitude();
		} else {
			return _endLongitude;
		}
	}

	/**
	 * 
	 * @param a_Long
	 */
	public void setEndDate(long a_Long) {
		setEndDate(new Float(a_Long).floatValue());
	}

	/**
	 * 
	 * @param a_Long
	 */
	public void setEndTime(long a_Long) {
		setEndTime(new Float(a_Long).floatValue());
	}

} /* End of class: TimeSeriesInformation */
