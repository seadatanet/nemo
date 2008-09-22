package fr.ifremer.nemo.input;

import java.io.OutputStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

/**
 * Class LineFragment
 * 
 * Représente un fragment d'une ligne du fichier d'entrée. Le fragment est
 * définit par un offset de début et de fin. L'offset de fin est optionnel, dans
 * ce cas sa valeur vaut -1 et la ligne est lue jusqu'à la fin comme illustré
 * ci-dessous
 * 
 * v début du fragment | | XXXXXXXXX| | ^ fin du fragment
 * 
 * @author Gerard BUNEL
 */
public class LineFragment implements Cloneable, SAXConsumer {

	/**
	 * Offset dans la ligne du premier caractere de la chaine de caracteres
	 */
	protected int _firstCharOffset;

	/**
	 * Offset du dernier caractere de la chaine a lire dans le fichier d'entree.
	 * si -1 on lit jusqu'a la fin de ligne
	 */
	protected int _lastCharOffset;

	public LineFragment() {
		_firstCharOffset = 1;
		_lastCharOffset = 1;
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
		LineFragment tmp = new LineFragment();
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
		if ((tmp = a_Attributes.getValue("firstCharOffset")) != null) {
			setFirstCharOffset(tmp);
		}
		if ((tmp = a_Attributes.getValue("lastCharOffset")) != null) {
			setLastCharOffset(tmp);
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
		a_Writer.putAttribute("firstCharOffset", getFirstCharOffset());
		a_Writer.putAttribute("lastCharOffset", getLastCharOffset());
	}

	/**
	 * 
	 * Generate XML defintion on a XMLOutputWriter
	 * 
	 * @param a_Writer
	 *            the XMLOutputWriter to which to write the XML Definition
	 * 
	 */
	public void writeXMLOn(XMLOutputWriter a_Writer, String tag) {
		a_Writer.openTag(tag);
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
	public void writeXMLOn(OutputStream a_Stream, String tag) {
		XMLOutputWriter writer = new XMLOutputWriter(a_Stream);
		writeXMLOn(writer, tag);
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	public Object clone() throws CloneNotSupportedException {
		LineFragment copy = null;
		copy = (LineFragment) super.clone();
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
	}

	/**
	 * Read accessor (getter) for attribute _firstCharOffset
	 * 
	 * @return int
	 * @see LineFragment#_firstCharOffset
	 */
	public int getFirstCharOffset() {
		return _firstCharOffset;
	}

	/**
	 * Write accessor (setter) for attribute _firstCharOffset
	 * 
	 * @param x
	 *            new value for _firstCharOffset
	 * @see LineFragment#_firstCharOffset
	 */
	public void setFirstCharOffset(int x) {
		_firstCharOffset = x;
	}

	/**
	 * Write accessor (setter) for attribute _firstCharOffset
	 * 
	 * @param a_String
	 *            new value for _firstCharOffset as String
	 * @see LineFragment#_firstCharOffset
	 */
	public void setFirstCharOffset(String a_String) {
		try {
			setFirstCharOffset(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _lastCharOffset
	 * 
	 * @return int
	 * @see LineFragment#_lastCharOffset
	 */
	public int getLastCharOffset() {
		return _lastCharOffset;
	}

	/**
	 * Write accessor (setter) for attribute _lastCharOffset
	 * 
	 * @param x
	 *            new value for _lastCharOffset
	 * @see LineFragment#_lastCharOffset
	 */
	public void setLastCharOffset(int x) {
		_lastCharOffset = x;
	}

	/**
	 * Write accessor (setter) for attribute _lastCharOffset
	 * 
	 * @param a_String
	 *            new value for _lastCharOffset as String
	 * @see LineFragment#_lastCharOffset
	 */
	public void setLastCharOffset(String a_String) {
		try {
			setLastCharOffset(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 * @param a_Line
	 * @return String
	 */
	public String retreive(String a_Line) {
		if (_lastCharOffset == -1) {
			return a_Line.substring(_firstCharOffset - 1).trim();
		} else {
			return a_Line.substring(_firstCharOffset - 1, _lastCharOffset)
					.trim();
		}
	}

} /* End of class: LineFragment */
