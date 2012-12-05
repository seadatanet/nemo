/* From Jack Version V1.5 on July 11, 2000 at 10:24:21 am */

package com.ago.xml;

// Package libraries inclusion
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Classe abstraite de parsing de fichier XML utilisant l'API SAX
 */

public abstract class XmlSAXParser extends DefaultHandler {
	private XMLReader _xmlReader;

	/**
	 * Pile permettant d'empiler les differents elements parses. L'empilement se
	 * fait par niveau d'imbrication.
	 */
	protected Stack<SAXConsumer> _stack = new Stack<SAXConsumer>();

	public Stack<StringBuffer> _characterStack = new Stack<StringBuffer>();

	public StringBuffer _buffer;

	/**
	 * Objet SAXConsumer qui sera le recepteur des evennements SAX
	 */
	protected SAXConsumer _consumer;

	/**
	 * Default constructor for class XmlSAXParser
	 */
	public XmlSAXParser() {
		super();
		_stack = new Stack<SAXConsumer>();
		_characterStack = new Stack<StringBuffer>();
		_consumer = null;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {

		releaseConsumer();
	}

	/**
	 * Read accessor (getter) for attribute _stack
	 * 
	 * @return Stack
	 * @see XmlSAXParser#_stack
	 */
	public Stack<SAXConsumer> getStack() {
		return _stack;
	}

	/**
	 * Write accessor (setter) for attribute _stack
	 * 
	 * @param x
	 *            new value for _stack
	 * @see XmlSAXParser#_stack
	 */
	public void setStack(Stack<SAXConsumer> x) {
		_stack = x;
	}

	/**
	 * Read accessor (getter) for attribute _characterStack
	 * 
	 * @return Stack
	 * @see XmlSAXParser#_characterStack
	 */
	public Stack<StringBuffer> getCharacterStack() {
		return _characterStack;
	}

	/**
	 * Write accessor (setter) for attribute _characterStack
	 * 
	 * @param x
	 *            new value for _characterStack
	 * @see XmlSAXParser#_characterStack
	 */
	public void setCharacterStack(Stack<StringBuffer> x) {
		_characterStack = x;
	}

	/**
	 * Read accessor (getter) for attribute _buffer
	 * 
	 * @return StringBuffer
	 * @see XmlSAXParser#_buffer
	 */
	public StringBuffer getBuffer() {
		return _buffer;
	}

	/**
	 * Write accessor (setter) for attribute _buffer
	 * 
	 * @param x
	 *            new value for _buffer
	 * @see XmlSAXParser#_buffer
	 */
	public void setBuffer(StringBuffer x) {
		_buffer = x;
	}

	/**
	 * Methode permettant de declencher le parsing
	 * 
	 * @param a_URL
	 *            L'URL du fichier a parser
	 * @return SAXConsumer
	 * 
	 */
	public SAXConsumer parse(URL a_URL) throws SAXException {
		InputStream stream = null;
		setStack(new Stack<SAXConsumer>());
		setCharacterStack(new Stack<StringBuffer>());
		setConsumer(null);
		setBuffer(new StringBuffer());

		try {
			XMLReader parser = getXmlReader();
			// pas de grammaire utilis�e pour v�rifier la syntaxe du fichier XML
			parser.setFeature("http://xml.org/sax/features/validation", false);
			parser.setContentHandler(this);
			stream = a_URL.openStream();
			parser.parse(new InputSource(new InputStreamReader(stream)));
			stream.close();
			stream = null;
		} catch (Exception e) {
			throw new SAXException(e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
                    e.printStackTrace();
				}
			}
		}
		return getConsumer();
	}

	public synchronized SAXConsumer parse(InputStream a_Stream)
			throws SAXException {
		setStack(new Stack<SAXConsumer>());
		setCharacterStack(new Stack<StringBuffer>());
		setConsumer(null);
		setBuffer(new StringBuffer());

		try {
			XMLReader parser = getXmlReader();
			// pas de grammaire utilis�e pour v�rifier la syntaxe du fichier XML
			parser.setFeature("http://xml.org/sax/features/validation", false);
			parser.setContentHandler(this);
			parser.parse(new InputSource(new InputStreamReader(a_Stream)));
		} catch (Exception e) {
			throw new SAXException(e);
		}
		return getConsumer();

	}

	/**
	 * Methode abstraite de creation du consumer. Cette methode devra etre
	 * implementee par les sous-classes
	 * 
	 * @param a_URI
	 *            Le Namespace (SAX 2)
	 * @param a_LocalName
	 *            Le nom du Tag
	 * @param a_QName
	 *            Le nom qualifie du tag. Non utilise ici
	 * @param a_Attributes
	 *            Les attributs de l'element
	 * @return SAXConsumer
	 * 
	 */
	public abstract SAXConsumer createConsumer(String a_URI,
			String a_LocalName, String a_QName, Attributes a_Attributes)
			throws SAXException;

	/**
	 * route l'evennement SAX vers le consumer
	 * 
	 * @param a_ch
	 *            tableau contenant la chaine de caracteres
	 * @param a_start
	 *            index dans le tableau du debut de la chaine de caracteres
	 * @param a_length
	 *            longueur de la chaine de caracteres
	 * 
	 */
	public void characters(char a_ch[], int a_start, int a_length) {
		// Filtrage des caracteres. Les chaines vides sont ignorees
		String str = new String(a_ch, a_start, a_length);
		// if (str.trim().length() > 0) {
		if (str.length() > 0) {
			// Les caracteres ne peuvent etre utilises que par le consumer
			if (getConsumer() != null) {
				_buffer.append(str);
			}
		}
	}

	/**
	 * Fin d'un element, on depile le consumer
	 * 
	 * @param a_URI
	 *            Le Namespace (SAX 2)
	 * @param a_LocalName
	 *            Le nom du Tag
	 * @param a_QName
	 *            Le nom qualifie du tag. Non utilise ici
	 * 
	 */
	public void endElement(String a_URI, String a_LocalName, String a_QName)
			throws SAXException {
		if (getConsumer() != null) {
			// String characters = _buffer.toString().trim();
			String characters = _buffer.toString();
			if (characters.length() > 0) {
				getConsumer().consumeCharacters(characters);
			}
		}
		if (getStack().size() != 0) {
			_buffer = (StringBuffer) _characterStack.pop();
			setConsumer((SAXConsumer) getStack().pop());
		}
	}

	/**
	 * Un nouvel element a ete rencontre. Si un consumer existe, il devra le
	 * gerer, sinon on cree le consumer initial
	 * 
	 * @param a_URI
	 *            Le Namespace (SAX 2)
	 * @param a_LocalName
	 *            Le nom du Tag
	 * @param a_QName
	 *            Le nom qualifie du tag. Non utilise ici
	 * @param a_Attributes
	 *            Les attributs de l'element
	 * 
	 */
	public void startElement(String a_URI, String a_LocalName, String a_QName,
			Attributes a_Attributes) throws SAXException {
		// S'il existe un consumer pour cet element,
		// C'est a lui de consommer
		if (getConsumer() != null) {
			SAXConsumer cons;
			cons = getConsumer().consumeElement(a_URI, a_LocalName, a_QName,
					a_Attributes);
			getStack().push(getConsumer());
			setConsumer(cons);
			if (_buffer != null) {
				_characterStack.push(_buffer);
			}
			_buffer = new StringBuffer();
			return;
		}
		// Sinon, on se trouve dans le cas de la creation d'un ouvrage
		// Alors, creons le
		SAXConsumer c = createConsumer(a_URI, a_LocalName, a_QName,
				a_Attributes);
		setConsumer(c);
		_buffer = new StringBuffer();
	}

	/**
	 * Remove any link for role consumer
	 */
	public void releaseConsumer() {
		setConsumer(null);
	}

	/**
	 * returns the linked object for role consumer
	 * 
	 * @return the object linked
	 */
	public SAXConsumer getConsumer() {
		return _consumer;
	}

	/**
	 * Create a new link with x for role consumer
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setConsumer(SAXConsumer x) {
		_consumer = x;
	}

	/**
	 * No Description
	 * 
	 * @return XMLReader
	 * 
	 */
	public XMLReader getXmlReader() {
		if (_xmlReader == null) {
			try {
				// String tmp = System.getProperty( "org.xml.sax.driver");
				// if (tmp != null) {
				_xmlReader = XMLReaderFactory.createXMLReader();
				/*
				 * } else { _xmlReader =
				 * XMLReaderFactory.createXMLReader("org.apache.crimson.parser.XMLReaderImpl"); }
				 */
			} catch (Exception e) {
				e.printStackTrace();
				throw new Error("Unable to crate XMLReader");
			}
		}
		return _xmlReader;
	}

} /* End of class: XmlSAXParser */
