package fr.ifremer.nemo.input;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

/**
 * Class Flag
 * 
 * @author Herve Taniou
 */
public class FlagFragment implements Cloneable, SAXConsumer {

	/**
	 * Index de la ligne de la donnee
	 */
	protected int _line;

	/**
	 * Offset, dans la premiere ligne, du premier caractere de la donnee
	 */
	protected int _firstCharOffset;

	/**
	 * Offset, dans la derniere ligne, du dernier caractere de la donnee. si -1
	 * on lit jusqu'a la fin de ligne
	 */
	protected int _lastCharOffset;

	public FlagFragment() {
		_line = 1;
		_firstCharOffset = 1;
		_lastCharOffset = 1;
	}

	public FlagFragment(int line, int first, int last) {
		_line = line;
		_firstCharOffset = first;
		_lastCharOffset = last;
	}

	public int getFirstCharOffset() {
		return _firstCharOffset;
	}

	public void setFirstCharOffset(int charOffset) {
		_firstCharOffset = charOffset;
	}
	
	public static SAXConsumer fromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
		FlagFragment tmp = new FlagFragment();
		tmp.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		return (SAXConsumer) tmp;
	}
	
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
	 * Write accessor (setter) for attribute _firstCharOffset
	 * 
	 * @param a_String
	 *            new value for _firstCharOffset as String
	 * @see HeaderFragment#_firstCharOffset
	 */
	public void setFirstCharOffset(String a_String) {
		try {
			setFirstCharOffset(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}

	public int getLastCharOffset() {
		return _lastCharOffset;
	}

	public void setLastCharOffset(int charOffset) {
		_lastCharOffset = charOffset;
	}

	/**
	 * Write accessor (setter) for attribute _lastCharOffset
	 * 
	 * @param a_String
	 *            new value for _lastCharOffset as String
	 * @see HeaderFragment#_lastCharOffset
	 */
	public void setLastCharOffset(String a_String) {
		try {
			setLastCharOffset(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}

	public int getLine() {
		return _line;
	}

	public void setLine(int _line) {
		this._line = _line;
	}
	
	/**
	 * Write accessor (setter) for attribute _line
	 * 
	 * @param a_String
	 *            new value for _line as String
	 * @see HeaderFragment#_line
	 */
	public void setLine(String a_String) {
		try {
			setLine(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}
	
	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	public Object clone() throws CloneNotSupportedException {
		FlagFragment copy = null;
		copy = (FlagFragment) super.clone();
		return copy;
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
		if (a_LocalName.equals("line")) {
			Element2Field setter = new Element2Field(this, "setLine");
			return setter;
		}
		if (a_LocalName.equals("firstCharOffset")) {
			Element2Field setter = new Element2Field(this, "setFirstCharOffset");
			return setter;
		}
		if (a_LocalName.equals("lastCharOffset")) {
			Element2Field setter = new Element2Field(this, "setLastCharOffset");
			return setter;
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
		a_Writer.putTag("line", getLine());
		a_Writer.putTag("firstCharOffset", getFirstCharOffset());
		a_Writer.putTag("lastCharOffset", getLastCharOffset());
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
		a_Writer.openTag("FlagFragment");
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
	 * 
	 * @param a_Header
	 * @return String
	 */
	public String retreive(String[] a_Header) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter, true);

		writer.println(a_Header[_line - 1].substring(_firstCharOffset - 1, _lastCharOffset));
		
		return stringWriter.toString();
	}
}
