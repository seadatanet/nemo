/* From Jack Version V1.5 on July 11, 2000 at 10:24:22 am */

package com.ago.xml;

// Package libraries inclusion
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class abstraite d'implentation d'un SAXConsumer
 */

public abstract class SAXBaseConsumer extends Object implements SAXConsumer {

	/**
	 * Default constructor for class SAXBaseConsumer
	 */
	public SAXBaseConsumer() {
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
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
	 * @return SAXConsumer
	 * 
	 */
	public abstract SAXConsumer consumeElement(String a_URI,
			String a_LocalName, String a_QName, Attributes a_Attributes)
			throws SAXException;

	/**
	 * Un champ de type PCDATA a ete rencontre
	 * 
	 * @param a_Characters
	 *            Valeur du champ PCDATA
	 * 
	 */
	public abstract void consumeCharacters(String a_Characters)
			throws SAXException;

	/**
	 * Methode de creation d'instance a partir des informations d'un element XML
	 * 
	 * @param a_URI
	 *            Le Namespace (SAX 2)
	 * @param a_LocalName
	 *            Le nom du Tag
	 * @param a_QName
	 *            Le nom qualifie du tag. Non utilise ici
	 * @param a_Attributes
	 *            Les attributs de l'element
	 * @return SAXBaseConsumer
	 * 
	 */
	static public SAXBaseConsumer fromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
		// Not yet defined
		return (SAXBaseConsumer) null;
	}

} /* End of class: SAXBaseConsumer */
