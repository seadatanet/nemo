package fr.ifremer.nemo.domain;

import java.io.OutputStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

/**
 * Class Cdi
 * 
 * Classe permettant la description des informations specifiques au resume CDI
 * 
 * @author Herve TANIOU
 */
public class Cdi implements Cloneable, SAXConsumer {
	/**
	 * Type de plate-forme
	 */
	protected String _typePlateForme;

	/**
	 * Accès aux données
	 */
	protected String _accesDonnees;

	public Cdi() {
		_typePlateForme = "";
		_accesDonnees = "";
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
		Cdi tmp = new Cdi();
		tmp.consumeElement(a_URI, a_LocalName, a_QName, a_Attributes);
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
		if ((tmp = a_Attributes.getValue("typePlateForme")) != null) {
			setTypePlateForme(tmp);
		}
		if ((tmp = a_Attributes.getValue("accesDonnees")) != null) {
			setAccesDonnees(tmp);
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
		if (a_LocalName.equals("typePlateForme")) {
			Element2Field setter = new Element2Field(this, "setTypePlateForme");
			return setter;
		}
		if (a_LocalName.equals("accesDonnees")) {
			Element2Field setter = new Element2Field(this, "setAccesDonnees");
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
		a_Writer.putTag("typePlateForme", getTypePlateForme());
		a_Writer.putTag("accesDonnees", getAccesDonnees());
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
		a_Writer.openTag("Cdi");
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
	 * Read accessor (getter) for attribute _typePlateForme
	 * 
	 * @return String
	 * @see Cdi#_typePlateForme
	 */
	public String getTypePlateForme() {
		return _typePlateForme;
	}

	/**
	 * Write accessor (setter) for attribute _typePlateForme
	 * 
	 * @param s
	 *            new value for _typePlateForme
	 * @see Dci#_typePlateForme
	 */
	public void setTypePlateForme(String s) {
		_typePlateForme = s;
	}

	/**
	 * Read accessor (getter) for attribute _accesDonnees
	 * 
	 * @return String
	 * @see Cdi#_date
	 */
	public String getAccesDonnees() {
		return _accesDonnees;
	}

	/**
	 * Write accessor (setter) for attribute _accesDonnees
	 * 
	 * @param s
	 *            new value for _accesDonnees
	 * @see Cdi#_accesDonnees
	 */
	public void setAccesDonnees(String s) {
		_accesDonnees = s;
	}

	/**
	 * 
	 * @param o
	 * @return boolean
	 */
	public boolean equals(Object o) {
		return (o == this);
	}

} /* End of class: Dci */