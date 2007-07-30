package fr.ifremer.nemo.domain;

import com.ago.xml.*;
import fr.ifremer.nemo.ws.*;
import java.io.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class DataType
 * 
 * Classe permettant la description d'un type de données
 * 
 * @author Gerard BUNEL
 */
public class DataType implements Cloneable, SAXConsumer {

	/**
	 * ROSCOP code (MEDATLAS: 3 char max)
	 */
	protected String _code;

	/**
	 * number of profiles for the type (MEDATLAS: 4 char max)
	 */
	protected int _numberOfProfiles;

	protected boolean _qc;

	/**
	 * Indique si le type de données correspond à un capteur
	 */
	protected boolean _sensor;

	public DataType() {
		_code = "";
		_qc = false;
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
		DataType tmp = new DataType();
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
		if ((tmp = a_Attributes.getValue("code")) != null) {
			setCode(tmp);
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
		if (a_LocalName.equals("profiles")) {
			Element2Field setter = new Element2Field(this,
					"setNumberOfProfiles");
			return setter;
		}
		if (a_LocalName.equals("qc")) {
			Element2Field setter = new Element2Field(this, "setQc");
			return setter;
		}
		if (a_LocalName.equals("sensor")) {
			Element2Field setter = new Element2Field(this, "setSensor");
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
		a_Writer.putAttribute("code", getCode());
		a_Writer.putTag("profiles", getNumberOfProfiles());
		a_Writer.putTag("qc", isQc());
		a_Writer.putTag("sensor", isSensor());
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
		a_Writer.openTag("DataType");
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
		DataType copy = null;
		copy = (DataType) super.clone();
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
	}

	/**
	 * Read accessor (getter) for attribute _code
	 * 
	 * @return String
	 * @see DataType#_code
	 */
	public String getCode() {
		return _code;
	}

	/**
	 * Write accessor (setter) for attribute _code
	 * 
	 * @param x
	 *            new value for _code
	 * @see DataType#_code
	 */
	public void setCode(String x) {
		_code = x;
	}

	/**
	 * Read accessor (getter) for attribute _numberOfProfiles
	 * 
	 * @return int
	 * @see DataType#_numberOfProfiles
	 */
	public int getNumberOfProfiles() {
		return _numberOfProfiles;
	}

	/**
	 * Write accessor (setter) for attribute _numberOfProfiles
	 * 
	 * @param x
	 *            new value for _numberOfProfiles
	 * @see DataType#_numberOfProfiles
	 */
	public void setNumberOfProfiles(int x) {
		_numberOfProfiles = x;
	}

	/**
	 * Write accessor (setter) for attribute _numberOfProfiles
	 * 
	 * @param a_String
	 *            new value for _numberOfProfiles as String
	 * @see DataType#_numberOfProfiles
	 */
	public void setNumberOfProfiles(String a_String) {
		try {
			setNumberOfProfiles(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _qc
	 * 
	 * @return boolean
	 * @see DataType#_qc
	 */
	public boolean isQc() {
		return _qc;
	}

	/**
	 * Write accessor (setter) for attribute _qc
	 * 
	 * @param x
	 *            new value for _qc
	 * @see DataType#_qc
	 */
	public void setQc(boolean x) {
		_qc = x;
	}

	/**
	 * Write accessor (setter) for attribute _qc
	 * 
	 * @param a_String
	 *            new value for _qc as String
	 * @see DataType#_qc
	 */
	public void setQc(String a_String) {
		setQc(new Boolean(a_String).booleanValue());
	}

	/**
	 * Read accessor (getter) for attribute _sensor
	 * 
	 * @return boolean
	 * @see DataType#_sensor
	 */
	public boolean isSensor() {
		return _sensor;
	}

	/**
	 * Write accessor (setter) for attribute _sensor
	 * 
	 * @param x
	 *            new value for _sensor
	 * @see DataType#_sensor
	 */
	public void setSensor(boolean x) {
		_sensor = x;
	}

	/**
	 * Write accessor (setter) for attribute _sensor
	 * 
	 * @param a_String
	 *            new value for _sensor as String
	 * @see DataType#_sensor
	 */
	public void setSensor(String a_String) {
		setSensor(new Boolean(a_String).booleanValue());
	}

} /* End of class: DataType */
