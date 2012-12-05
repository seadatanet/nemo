package fr.ifremer.nemo.util.format;

import fr.ifremer.nemo.util.Global;



/**
 * Class ParseElement
 * 
 * 
 * @author Gerard BUNEL
 */
public class ParseElement implements Cloneable {
	public String _element;

	public ParseElementType _type;

	public ParseElement() {
		_element = Global.EMPTY_STRING;
	}

	/**
	 * 
	 * @param a_Type
	 * @param a_Element
	 */
	public ParseElement(ParseElementType a_Type, String a_Element) {
		this();
		_type = a_Type;
		_element = a_Element;
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	@Override
    public Object clone() throws CloneNotSupportedException {
		ParseElement copy = null;
		copy = (ParseElement) super.clone();
		return copy;
	}

}