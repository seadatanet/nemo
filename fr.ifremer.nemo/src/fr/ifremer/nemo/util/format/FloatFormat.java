package fr.ifremer.nemo.util.format;

import java.io.OutputStream;
import java.text.FieldPosition;
import java.text.ParsePosition;

import org.xml.sax.Attributes;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.input.ModelDescription;
import fr.ifremer.nemo.util.Global;


/**
 * Class FloatFormat
 * 
 * 
 * @author Gerard BUNEL
 */
public class FloatFormat extends ExternalizableFormat implements Cloneable {

    private static final long serialVersionUID = 2589900117568212494L;
    public String _formatString;

	public FloatFormat() {
		super();
		_formatString = Global.EMPTY_STRING;
	}

	/**
	 * 
	 * @param a_Format
	 */
	public FloatFormat(String a_Format) {
		this();
		_formatString = a_Format;
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
	public static SAXConsumer fromElement(String a_URI, 
                                          String a_LocalName,
                                          String a_QName, 
                                          Attributes a_Attributes){
		FloatFormat tmp = new FloatFormat();
		tmp.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		return tmp;
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
	public void buildFromElement(String a_URI, 
                                 String a_LocalName,
                                 String a_QName, 
                                 Attributes a_Attributes) {
		String tmp;
		if ((tmp = a_Attributes.getValue(ModelDescription.a_string)) != null) {
			setFormatString(tmp);
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
			String a_QName, Attributes a_Attributes){
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
	public void consumeCharacters(String a_Characters){
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
		a_Writer.putAttribute(ModelDescription.a_string, getFormatString());
	}

	/**
	 * 
	 * Generate XML defintion on a XMLOutputWriter
	 * 
	 * @param a_Writer
	 *            the XMLOutputWriter to which to write the XML Definition
	 * 
	 */
	@Override
    public void writeXMLOn(XMLOutputWriter a_Writer) {
		a_Writer.openTag(ModelDescription.node_fFloat);
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
	 * Read accessor (getter) for attribute _formatString
	 * 
	 * @return String
	 * @see FloatFormat#_formatString
	 */
	public String getFormatString() {
		return _formatString;
	}

	/**
	 * Write accessor (setter) for attribute _formatString
	 * 
	 * @param x
	 *            new value for _formatString
	 * @see FloatFormat#_formatString
	 */
	public void setFormatString(String x) {
		_formatString = x;
	}

	/**
	 * 
	 * @return Object
	 */
	@Override
    public Object clone() {
		return new FloatFormat(new String(_formatString));
	}

	/**
	 * 
	 * @param obj
	 * @param toAppendTo
	 * @param pos
	 * @return StringBuffer
	 */
	@Override
    public StringBuffer format(Object obj, 
                               StringBuffer toAppendTo,
                               FieldPosition pos) {
		toAppendTo.append(String
				.format(java.util.Locale.US, _formatString, obj));
		return toAppendTo;
	}

	/**
	 * 
	 * @param source
	 * @param pos
	 * @return Float
	 */
	@Override
    public Float parseObject(String source, 
                             ParsePosition pos) {
		pos.setIndex(source.length());
		return Float.valueOf(source);
	}

	/**
	 * 
	 * @return String
	 */
	@Override
    public String toString() {
		return _formatString;
	}

}