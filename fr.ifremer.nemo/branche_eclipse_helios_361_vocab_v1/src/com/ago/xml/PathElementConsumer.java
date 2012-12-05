/* From Jack Version V1.5 on July 11, 2000 at 10:24:22 am */

package com.ago.xml;

// Package libraries inclusion
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

// Description of interface: PathElementHandler

/**
 * description: none
 */
public class PathElementConsumer implements SAXConsumer {

	protected PathElementHandler _handler = null;

	protected String _path;

	public PathElementConsumer(String a_Path, PathElementHandler a_Handler) {
		_path = a_Path;
		_handler = a_Handler;
	}

	/**
	 * Un nouvel element a ete rencontre
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
	public SAXConsumer consumeElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
		return _handler.handlePathElement(_path, a_URI, a_LocalName, a_QName,
				a_Attributes);
	}

	/**
	 * Un champ de type PCDATA a ete rencontre
	 * 
	 * @param a_Characters
	 *            Valeur du champ PCDATA
	 * 
	 */
	public void consumeCharacters(String a_Characters) {
	}

} /* End of Interface: PathElementHandler */
