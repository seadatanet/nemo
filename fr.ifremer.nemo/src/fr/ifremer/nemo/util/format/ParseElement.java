package fr.ifremer.nemo.util.format;



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
		_element = "";
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
	public Object clone() throws CloneNotSupportedException {
		ParseElement copy = null;
		copy = (ParseElement) super.clone();
		return copy;
	}

} /* End of class: ParseElement */
