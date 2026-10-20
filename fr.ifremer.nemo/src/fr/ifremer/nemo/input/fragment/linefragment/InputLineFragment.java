package fr.ifremer.nemo.input.fragment.linefragment;

import org.xml.sax.Attributes;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;

import fr.ifremer.nemo.input.ModelDescription;

/**
 * InputLineFragment from xml modele. Converted into CSV/Char LineFragment later
 *
 */
public class InputLineFragment extends LineFragment {

	protected int _firstCharOffset;
	protected int _lastCharOffset;
	private int _colNb = -1;

	/**
	 * @see AbstractModel.java#fromElement
	 */
	public static SAXConsumer fromElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) {
		LineFragment el = new InputLineFragment();
		el.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		return el;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildFromElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) {
		String tmp;
		if ((tmp = a_Attributes.getValue(ModelDescription.a_firstoffset)) != null) {
			setFirstCharOffset(tmp);
		}
		if ((tmp = a_Attributes.getValue(ModelDescription.a_lastoffset)) != null) {
			setLastCharOffset(tmp);
		}
		if ((tmp = a_Attributes.getValue(ModelDescription.a_colNb)) != null) {
			setColNb(tmp);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SAXConsumer consumeElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) {

		SAXConsumer superSetter = super.consumeElement(a_URI, a_LocalName, a_QName, a_Attributes);
		if (superSetter != null) {
			return superSetter;
		}
		if (a_LocalName.equals(ModelDescription.node_firstoffset)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_firstoffset);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_lastoffset)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_lastoffset);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_colNb)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_colNb);
			return setter;
		}

		return null;
	}

	public int getFirstCharOffset() {
		return _firstCharOffset;
	}

	public void setFirstCharOffset(String s) {
		try {
			_firstCharOffset = Integer.parseInt(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getLastCharOffset() {
		return _lastCharOffset;
	}

	public void setLastCharOffset(String s) {
		try {
			_lastCharOffset = Integer.parseInt(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getColNb() {
		return _colNb;
	}

	public void setColNb(String s) {
		try {
			_colNb = Integer.parseInt(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
