/* From Jack Version V1.5 on July 11, 2000 at 10:24:22 am */

package com.ago.xml;

// Package libraries inclusion
import java.lang.reflect.Method;

import org.xml.sax.Attributes;

/**
 * Classe pouvant etre utilisee lors du parsing XML. Elle sert lorsqu'un element
 * XML est mappe vers un attribut d'un objet. Cette classe est a utiliser avec
 * un parser de type XmlSAXParser.
 */

public class Element2Field extends Object implements SAXConsumer {

	/**
	 * Nom de la methode de l'objet <code>target</code> a utiliser pour fixer
	 * la valeur de l'attribut correspondant
	 */
	protected String _setterName;

	/**
	 * Objet possedant l'attribut dont la valeur sera affectee par la valeur du
	 * champ PCDATA de l'element
	 */
	protected Object _target;

	/**
	 * Default constructor for class Element2Field
	 */
	public Element2Field() {
		_setterName = "";
		_target = null;

	}

	/**
	 * Constructeur de Element2Field
	 * 
	 * @param a_Target
	 * @param a_Setter
	 * 
	 */
	public Element2Field(Object a_Target, String a_Setter) {
		setTarget(a_Target);
		setSetterName(a_Setter);
	}

	/**
	 * Remove any associated links
	 */
	public void release() {

		releaseTarget();
	}

	/**
	 * Read accessor (getter) for attribute _setterName
	 * 
	 * @return String
	 * @see Element2Field#_setterName
	 */
	public String getSetterName() {
		return _setterName;
	}

	/**
	 * Write accessor (setter) for attribute _setterName
	 * 
	 * @param x
	 *            new value for _setterName
	 * @see Element2Field#_setterName
	 */
	public void setSetterName(String x) {
		_setterName = x;
	}

	/**
	 * Cette methode ne doit pas etre appelee. Un element de ce type n'est pas
	 * suppose avoir des sous-elements. Genere une erreur
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
		System.out.println("Attribute setter cannot consume element");
		System.out.println("Class: " + getTarget().getClass());
		System.out.println("setter: " + getSetterName());
		throw new Error("Should not be called");
	}

	/**
	 * Methode invoquee lors de la lecture d'un champ PCDATA. C'est cette valeur
	 * qui va etre utilisee et affectee a l'attribut correspondant de l'objet
	 * <code>target</code> en utilisant la methode de nom
	 * <code>setterName</code> et prenant en argument une chaine de
	 * caracteres.
	 * 
	 * @param a_Characters
	 *            Valeur du champ PCDATA
	 * 
	 */
	public void consumeCharacters(String a_Characters) {
		try {
			Method m = getTarget().getClass().getMethod(getSetterName(),
					new Class[] { java.lang.String.class });
			m.invoke(getTarget(), new Object[] { a_Characters });
		} catch (Exception e) {
			System.out.println("Unable to consume characters");
			System.out.println("Class: " + getTarget().getClass());
			System.out.println("setter: " + getSetterName());
			e.printStackTrace();
		}
	}

	/**
	 * Remove any link for role target
	 */
	public void releaseTarget() {
		setTarget(null);
	}

	/**
	 * returns the linked object for role target
	 * 
	 * @return the object linked
	 */
	public Object getTarget() {
		return _target;
	}

	/**
	 * Create a new link with x for role target
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setTarget(Object x) {
		_target = x;
	}

} /* End of class: Element2Field */
