/**
 * 
 * 
 * @class	XMLBootstrapParser
 * @project	CTI
 * @package	com.ftrd.util
 * @author 	Gerard BUNEL - Atlantide (2001 - 2002)
 * @version	1.0 - January 21, 2002
 */

package com.ago.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XMLBootstrapParser extends XmlSAXParser {

	protected RootSAXConsumer _saxConsumer;

	/**
	 * Default constructor for class XMLBootstrapParser
	 */
	public XMLBootstrapParser() {
		super();
		_saxConsumer = null;
	}

	/**
	 * Constructor initialized with the Root element
	 * 
	 * @param a_Consumer
	 * 
	 */
	public XMLBootstrapParser(RootSAXConsumer a_Consumer) {
		this();
		_saxConsumer = a_Consumer;
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
	static public SAXConsumer fromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {

		XMLBootstrapParser tmp = new XMLBootstrapParser();
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

		return null;
	}

	public void consumeCharacters(String a_Characters) {

	}

	/**
	 * 
	 * Generate XML defintion on a Stream
	 * 
	 * @param a_Writer
	 *            XMLOutputWriter to which to write the XML Definition
	 * 
	 */
	public void writeXMLOn(XMLOutputWriter a_Writer) {

	}

	/**
	 * Creates the initial SAXConsumer which Corresponds to the root XML Element
	 * 
	 * @param a_URI
	 *            SAX2 Namespace
	 * @param a_LocalName
	 *            Tag element name
	 * @param a_QName
	 *            Qualified tag name
	 * @param a_Attributes
	 *            Element attributes
	 * @return SAXConsumer
	 * 
	 */
	public SAXConsumer createConsumer(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
		_saxConsumer
				.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		return _saxConsumer;
	}

	/**
	 * No Description
	 * 
	 * @param a_Stream
	 * 
	 */
	public synchronized SAXConsumer parse(InputStream a_Stream) {
		return parse(a_Stream, true);
	}

	/**
	 * No Description
	 * 
	 * @param a_Stream
	 * 
	 */
	public synchronized SAXConsumer parse(InputStream a_Stream,
			boolean a_Validation) {
		setStack(new Stack<SAXConsumer>());
		setCharacterStack(new Stack<StringBuffer>());
		setConsumer(null);
		setBuffer(new StringBuffer());

		try {
			XMLReader parser = getXmlReader();
			// pas de grammaire utilisée pour vérifier la syntaxe du fichier XML
			parser.setFeature("http://xml.org/sax/features/validation",
					a_Validation);
			parser.setContentHandler(this);
			parser.parse(new InputSource(new InputStreamReader(a_Stream)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _saxConsumer;
	}

	/**
	 * No Description
	 * 
	 * @param a_Filename
	 * 
	 */
	public synchronized SAXConsumer parse(String a_Filename) {
		FileInputStream stream = null;
		try {
			File file = new File(a_Filename);
			stream = new FileInputStream(file);
			parse(stream);
			stream.close();
			stream = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
				}
			}
		}
		return _saxConsumer;
	}

} /* End of class: XMLBootstrapParser */
