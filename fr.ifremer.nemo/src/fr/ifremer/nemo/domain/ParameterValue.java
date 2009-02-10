package fr.ifremer.nemo.domain;

import java.io.OutputStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.util.Global;

/**
 * Class ParameterValue
 * 
 * Classe permettant de stocker la valeur d'une mesure
 * 
 * @author Gerard BUNEL
 */
public class ParameterValue implements Cloneable, SAXConsumer {

	public String _qc;

	/**
	 * Valeur de la mesure
	 */
	protected Double _value;
    
    /**
     *  
     */
    private String _stringValue;

	/**
	 * Parametre pour lequel la mesure a ete effectuee
	 */
	protected Parameter _definition;

	public ParameterValue() {
		_qc = Global.EMPTY_STRING;
		_definition = null;
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
		ParameterValue tmp = new ParameterValue();
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
		if ((tmp = a_Attributes.getValue("value")) != null) {
			setValue(tmp);
		}
        if ((tmp = a_Attributes.getValue("stringValue")) != null) {
            setStringValue(tmp);
        }
		if ((tmp = a_Attributes.getValue(Global.EMPTY_STRING)) != null) {
			setQc(tmp);
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
		a_Writer.putAttribute("value", getValue());
        a_Writer.putAttribute("stringValue", getStringValue());
		a_Writer.putAttribute(Global.EMPTY_STRING, getQc());
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
		a_Writer.openTag("ParameterValue");
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
		ParameterValue copy = null;
		copy = (ParameterValue) super.clone();
		copy._definition = null;
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
		releaseDefinition();
	}

	/**
	 * Read accessor (getter) for attribute _qc
	 * 
	 * @return String
	 * @see ParameterValue#_qc
	 */
	public String getQc() {
		return _qc;
	}

	/**
	 * Write accessor (setter) for attribute _qc
	 * 
	 * @param x
	 *            new value for _qc
	 * @see ParameterValue#_qc
	 */
	public void setQc(String x) {
		_qc = x;
	}

	/**
	 * Read accessor (getter) for attribute _value
	 * 
	 * @return float
	 * @see ParameterValue#_value
	 */
	public Double getValue() {
		return _value;
	}

	/**
	 * Write accessor (setter) for attribute _value
	 * 
	 * @param x
	 *            new value for _value
	 * @see ParameterValue#_value
	 */
	public void setValue(Double x) {
		_value = x;
	}

	/**
	 * Write accessor (setter) for attribute _value
	 * 
	 * @param a_String
	 *            new value for _value as String
	 * @see ParameterValue#_value
	 */
	public void setValue(String a_String) {
		try {
			setValue(Double.parseDouble(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Remove any link for role definition
	 */
	public void releaseDefinition() {
		setDefinition(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public Parameter getDefinition() {
		return _definition;
	}

	/**
	 * Create a new link with x for role definition
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setDefinition(Parameter x) {
		if (_definition == x)
			return;
		Parameter old = _definition;
		if (_definition != null) {
			_definition = null;
			old.removeValue(this);
		}
		_definition = x;
		if (x != null) {
			x.addValue(this);
		}
	}

    /**
     * Getter sur _stringValue.
     * @return the stringValue
     */
    public String getStringValue()
    {
        return _stringValue;
    }

    /**
     * Setter sur _stringValue.
     * @param stringValue the stringValue to set
     */
    public void setStringValue(String stringValue)
    {
        _stringValue = stringValue;
    }

} /* End of class: ParameterValue */
