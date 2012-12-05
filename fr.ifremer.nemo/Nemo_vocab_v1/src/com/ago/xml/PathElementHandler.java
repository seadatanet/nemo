/* From Jack Version V1.5 on July 11, 2000 at 10:24:22 am */

package com.ago.xml;

// Package libraries inclusion
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

// Description of interface: PathElementHandler

/**
 * description: none
 */
public interface PathElementHandler extends SAXConsumer {

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
	SAXConsumer handlePathElement(String path, String a_URI,
			String a_LocalName, String a_QName, Attributes a_Attributes)
			throws SAXException;

} /* End of Interface: PathElementHandler */
