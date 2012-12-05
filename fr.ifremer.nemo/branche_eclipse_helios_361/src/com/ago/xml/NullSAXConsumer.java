package com.ago.xml;

// Package libraries inclusion
import org.xml.sax.Attributes;

/**
 * Class NullSAXConsumer simply consumes all sub-elements. Does nothing than
 * consuming. This class can be used to ignore XML Elements
 */
public class NullSAXConsumer implements SAXConsumer {

	public NullSAXConsumer() {
		super();
	}

	/**
	 * En nouvel element a ete rencontre
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
	public SAXConsumer consumeElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) {
		return new NullSAXConsumer();
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

} /* End of Class: NullSAXConsumer */
