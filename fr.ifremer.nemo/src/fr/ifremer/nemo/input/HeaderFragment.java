package fr.ifremer.nemo.input;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.ui.DataModel;

/**
 * Class HeaderFragment
 * 
 * Représente un fragment du fichier d'entrée. Le fragment est définit par une
 * position de début et de fin. comme illustré ci-dessous | | | v début | | |
 * XXXXXXXXXXXXXX| | XXXXXXXXXXXXXXXXX| | XXXXXXXXXXXXXXXXX| | XXXXXXXXXXXX| | | ^
 * fin | | |
 * 
 * @author Gerard BUNEL
 */
public class HeaderFragment implements Cloneable, SAXConsumer {

	/**
	 * Offset, dans la premiere ligne, du premier caractere de la donnee
	 */
	protected int _firstCharOffset;

	/**
	 * Index de la premiere ligne dans le header ou commence la donnee
	 */
	protected int _firstLine;

	/**
	 * Offset, dans la derniere ligne, du dernier caractere de la donnee. si -1
	 * on lit jusqu'a la fin de ligne
	 */
	protected int _lastCharOffset;

	/**
	 * Index de la ligne dans le header ou finit la donnee. Par defaut egale a
	 * la valeur de firstLine
	 */
	protected int _lastLine;

	protected boolean _singleLine;
	
	/**
	 * Indique si la longueur de la derniere ligne est variable
	 */
	protected boolean _variable;

	public HeaderFragment() {
		_firstCharOffset = 1;
		_firstLine = 1;
		_lastCharOffset = 1;
		_lastLine = 1;
		_singleLine = false;
		_variable = false;
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
		HeaderFragment tmp = new HeaderFragment();
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
		if ((tmp = a_Attributes.getValue("isSingleLine")) != null) {
			setSingleLine(tmp);
		}
		if ((tmp = a_Attributes.getValue("isVariable")) != null) {
			setVariable(tmp);
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
		if (a_LocalName.equals("firstLine")) {
			Element2Field setter = new Element2Field(this, "setFirstLine");
			return setter;
		}
		if (a_LocalName.equals("lastLine")) {
			Element2Field setter = new Element2Field(this, "setLastLine");
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
		if (a_LocalName.equals("variable")) {
			Element2Field setter = new Element2Field(this, "setVariable");
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
		a_Writer.putAttribute("isSingleLine", isSingleLine());
		a_Writer.putAttribute("isVariable", isVariable());
		a_Writer.putTag("firstLine", getFirstLine());
		a_Writer.putTag("lastLine", getLastLine());
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
		a_Writer.openTag("HeaderFragment");
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
		HeaderFragment copy = null;
		copy = (HeaderFragment) super.clone();
		return copy;
	}

	/**
	 * Read accessor (getter) for attribute _firstCharOffset
	 * 
	 * @return int
	 * @see HeaderFragment#_firstCharOffset
	 */
	public int getFirstCharOffset() {
		return _firstCharOffset;
	}

	/**
	 * Write accessor (setter) for attribute _firstCharOffset
	 * 
	 * @param x
	 *            new value for _firstCharOffset
	 * @see HeaderFragment#_firstCharOffset
	 */
	public void setFirstCharOffset(int x) {
		_firstCharOffset = x;
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

	/**
	 * Read accessor (getter) for attribute _firstLine
	 * 
	 * @return int
	 * @see HeaderFragment#_firstLine
	 */
	public int getFirstLine() {
		return _firstLine;
	}

	/**
	 * Write accessor (setter) for attribute _firstLine
	 * 
	 * @param x
	 *            new value for _firstLine
	 * @see HeaderFragment#_firstLine
	 */
	public void setFirstLine(int x) {
		_firstLine = x;
	}

	/**
	 * Write accessor (setter) for attribute _firstLine
	 * 
	 * @param a_String
	 *            new value for _firstLine as String
	 * @see HeaderFragment#_firstLine
	 */
	public void setFirstLine(String a_String) {
		try {
			setFirstLine(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _lastCharOffset
	 * 
	 * @return int
	 * @see HeaderFragment#_lastCharOffset
	 */
	public int getLastCharOffset() {
		return _lastCharOffset;
	}

	/**
	 * Write accessor (setter) for attribute _lastCharOffset
	 * 
	 * @param x
	 *            new value for _lastCharOffset
	 * @see HeaderFragment#_lastCharOffset
	 */
	public void setLastCharOffset(int x) {
		_lastCharOffset = x;
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

	/**
	 * Read accessor (getter) for attribute _lastLine
	 * 
	 * @return int
	 * @see HeaderFragment#_lastLine
	 */
	public int getLastLine() {
		return _lastLine;
	}

	/**
	 * Write accessor (setter) for attribute _lastLine
	 * 
	 * @param x
	 *            new value for _lastLine
	 * @see HeaderFragment#_lastLine
	 */
	public void setLastLine(int x) {
		_lastLine = x;
	}

	/**
	 * Write accessor (setter) for attribute _lastLine
	 * 
	 * @param a_String
	 *            new value for _lastLine as String
	 * @see HeaderFragment#_lastLine
	 */
	public void setLastLine(String a_String) {
		try {
			setLastLine(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _singleLine
	 * 
	 * @return boolean
	 * @see HeaderFragment#_singleLine
	 */
	public boolean isSingleLine() {
		return _singleLine;
	}

	/**
	 * Write accessor (setter) for attribute _singleLine
	 * 
	 * @param x
	 *            new value for _singleLine
	 * @see HeaderFragment#_singleLine
	 */
	public void setSingleLine(boolean x) {
		_singleLine = x;
	}

	/**
	 * Write accessor (setter) for attribute _singleLine
	 * 
	 * @param a_String
	 *            new value for _singleLine as String
	 * @see HeaderFragment#_singleLine
	 */
	public void setSingleLine(String a_String) {
		setSingleLine(new Boolean(a_String).booleanValue());
	}
	
	/**
	 * Read accessor (getter) for attribute _variable
	 * 
	 * @return boolean
	 * @see HeaderFragment#_variable
	 */
	public boolean isVariable() {
		return _variable;
	}
	
	/**
	 * Write accessor (setter) for attribute _variable
	 * 
	 * @param x
	 *            new value for _variable
	 * @see HeaderFragment#_variable
	 */
	public void setVariable(boolean x) {
		_variable = x;
	}
	
	/**
	 * Write accessor (setter) for attribute _variable
	 * 
	 * @param a_String
	 *            new value for _variable as String
	 * @see HeaderFragment#_variable
	 */
	public void setVariable(String a_String) {
		setVariable(new Boolean(a_String).booleanValue());
	}

	/**
	 * 
	 * @param a_Header
	 * @return String
	 */
	public String retreive(String[] a_Header) {
		if (_firstLine == 0) {
			if (_lastCharOffset == -1 || isVariable()) {
				// return a_Header[0].substring( _firstCharOffset-1 ).trim();
				return a_Header[0].substring(_firstCharOffset - 1);
			} else {
				// return a_Header[0].substring( _firstCharOffset-1,
				// _lastCharOffset-_firstCharOffset+1 ).trim();
				return a_Header[0].substring(_firstCharOffset - 1,
						_lastCharOffset);
			}
		} else {
			StringWriter stringWriter = new StringWriter();
			PrintWriter writer = new PrintWriter(stringWriter, true);
			// System.out.println("firstline ==>" + _firstLine);
			// System.out.println("lastline ==>" + _lastLine);
			// System.out.println("lenght ==>" + a_Header.length);

			for (int i = _firstLine - 1; i < _lastLine; i++) {
				int start = 0;
				int end = -1;
				
				if (i == _firstLine - 1) {
					start = _firstCharOffset - 1;
				}
				if (i == _lastLine - 1) {
					if (_lastCharOffset != -1 && !isVariable()) {
						end = _lastCharOffset - 1;
					}
				}
				
				if (DataModel.stationModel.isMetaInData()) {
					i = 1;
				}
				
				if (end == -1) {
					// writer.println( a_Header[i].substring( start ).trim());
					writer.println(a_Header[i].substring(start));
				} else {
					// writer.println( a_Header[i].substring( start, end+1
					// ).trim());
					writer.println(a_Header[i].substring(start, end + 1));
				}
				
			}
			
			// return stringWriter.toString().trim();
			return stringWriter.toString();
		}
	}

} /* End of class: HeaderFragment */
