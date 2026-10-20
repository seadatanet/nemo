package fr.ifremer.nemo.input.fragment.stationfragment;

import org.xml.sax.Attributes;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.AbstractModel;
import fr.ifremer.nemo.input.ModelDescription;
import fr.ifremer.nemo.util.Utils;

/**
 * Class Fragment Description : Represente un fragment du fichier d'entree. Le
 * fragment est definit par une position de debut et de fin. comme illustre
 * ci-dessous | | | v debut | | | XXXXXXXXXXXXXX| | XXXXXXXXXXXXXXXXX| |
 * XXXXXXXXXXXXXXXXX| | XXXXXXXXXXXX| | | ^ fin | | |
 * 
 * @author Gerard BUNEL
 */
//public abstract class Fragment extends AbstractModel {
public abstract class Fragment extends AbstractModel {

	/**
	 * Index de la premiere ligne dans le header ou commence la donnee
	 */
	protected int _firstLine;

	/**
	 * Index de la ligne dans le header ou finit la donnee. Par defaut egale a
	 * la valeur de firstLine
	 */
	protected int _lastLine;

	/**
	 * Indique si le fragment est sur une ligne ou plusieurs
	 */
	protected boolean _singleLine;

	/**
	 * Indique si il y a une valeur par défault ou non associée à ce paramètre
	 */
	protected boolean _hasDefaultValue;

	/**
	 * Indique si valeur par défault est sélectionné
	 */
	protected boolean _defaultValueSelected;

	/**
	 * Valeur par défault
	 */
	protected String _defaultValue;

	public Fragment() {
		_tagName = ModelDescription.nodetitle_default;
		initValues();
	}

	public Fragment(InputFragment inputFragment) {
		this();
		this.setFirstLine(inputFragment.getFirstLine());
		this.setLastLine(inputFragment.getLastLine());
		this.setSingleLine(inputFragment.isSingleLine());
		this.setHasDefaultValue(inputFragment.isDefaultValue());
		this.setDefaultValueSelected(inputFragment.isDefaultValueSelected());
		this.setDefaultValue(inputFragment.getDefaultValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initValues() {
		// _firstCharOffset = 1;
		// _firstLine = 1;
		// _lastCharOffset = 1;
		// _lastLine = 1;
		_singleLine = true;
		_hasDefaultValue = false;
		_defaultValueSelected = false;
		_defaultValue = "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildFromElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SAXConsumer consumeElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SAXConsumer handlePathElement(String path, String a_URI, String a_LocalName, String a_QName,
			Attributes a_Attributes) {
		// non utilisé
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		writeXMLAttributes(a_Writer);
		writeXMLTags(a_Writer);

	}

	public void writeXMLAttributes(XMLOutputWriter a_Writer) {
		a_Writer.putAttribute(ModelDescription.a_singleline, isSingleLine());
	}

	public void writeXMLTags(XMLOutputWriter a_Writer) {
		a_Writer.putTag(ModelDescription.node_firstline, getFirstLine());
		a_Writer.putTag(ModelDescription.node_lastline, getLastLine());
		if (_hasDefaultValue) {
			a_Writer.putTag(ModelDescription.node_defaultselected, isDefaultValueSelected());
			a_Writer.putTag(ModelDescription.node_defaultvalue, getDefaultValue());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Fragment copy = null;
		copy = (Fragment) super.clone();
		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void release() {
		// aucun lien à supprimer
	}


	public String retreiveInLine(String a_Line) {
		// TODO Auto-generated method stub
		return null;
	}

	public String fullRetreiveInLine(String a_Line) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Getter pour _firstLine.
	 * 
	 * @return int
	 * @see Fragment.java#_firstLine
	 */
	public int getFirstLine() {
		return _firstLine;
	}

	/**
	 * Setter pour _firstLine
	 * 
	 * @param x : nouvelle valeur pour _firstLine
	 * @see Fragment.java#_firstLine
	 */
	public void setFirstLine(int x) {
		_firstLine = x;
	}

	/**
	 * Setter pour _firstLine
	 * 
	 * @param s : nouvelle valeur pour _firstLine sous forme de String
	 * @see Fragment.java#_firstLine
	 */
	public void setFirstLine(String s) {
		try {
			_firstLine = Integer.parseInt(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter pour _lastLine.
	 * 
	 * @return int
	 * @see Fragment.java#_lastLine
	 */
	public int getLastLine() {
		return _lastLine;
	}

	/**
	 * Setter pour _lastLine
	 * 
	 * @param x : nouvelle valeur pour _lastLine
	 * @see Fragment.java#_lastLine
	 */
	public void setLastLine(int x) {
		_lastLine = x;
	}

	/**
	 * Setter pour _lastLine
	 * 
	 * @param s : nouvelle valeur pour _lastLine sous forme de String
	 * @see Fragment.java#_lastLine
	 */
	public void setLastLine(String s) {
		try {
			_lastLine = Integer.parseInt(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter pour _singleLine.
	 * 
	 * @return boolean
	 * @see Fragment.java#_singleLine
	 */
	public boolean isSingleLine() {
		return _singleLine;
	}

	/**
	 * Setter pour _singleLine
	 * 
	 * @param x : nouvelle valeur pour _singleLine
	 * @see Fragment.java#_singleLine
	 */
	public void setSingleLine(boolean x) {
		_singleLine = x;
	}

	/**
	 * Setter pour _singleLine
	 * 
	 * @param s : nouvelle valeur pour _singleLine sous forme de String
	 * @see Fragment.java#_singleLine
	 */
	public void setSingleLine(String s) {
		try {
			_singleLine = new Boolean(s).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter pour _defaultValue
	 * 
	 * @return String
	 * @see Fragment.java#_defaultValue
	 */
	public String getDefaultValue() {
		return _defaultValue;
	}

	/**
	 * Setter pour _defaultValue
	 * 
	 * @param s : nouvelle valeur pour _defaultValue sous forme de String
	 * @see Fragment.java#_defaultValue
	 */
	public void setDefaultValue(String s) {
		_defaultValue = s;
	}

	/**
	 * Getter pour _hasDefaultValue.
	 * 
	 * @return boolean
	 * @see Fragment.java#_hasDefaultValue
	 */
	public boolean isDefaultValue() {
		return _hasDefaultValue;
	}

	/**
	 * Setter pour _variable
	 * 
	 * @param x : nouvelle valeur pour _variable
	 * @see Fragment.java#_variable
	 */
	public void setHasDefaultValue(boolean x) {
		_hasDefaultValue = x;
	}

	/**
	 * Getter pour _defaultValueSelected.
	 * 
	 * @return boolean
	 * @see Fragment.java#_defaultValueSelected
	 */
	public boolean isDefaultValueSelected() {
		return _defaultValueSelected;
	}

	/**
	 * Setter pour _defaultValueSelected
	 * 
	 * @param x : nouvelle valeur pour _defaultValueSelected
	 * @see Fragment.java#_defaultValueSelected
	 */
	public void setDefaultValueSelected(boolean x) {
		_defaultValueSelected = x;
	}

	/**
	 * Setter pour _defaultValueSelected
	 * 
	 * @param s : nouvelle valeur pour _defaultValueSelected sous forme de
	 * String
	 * @see Fragment.java#_defaultValueSelected
	 */
	public void setDefaultValueSelected(String s) {
		try {
			_defaultValueSelected = new Boolean(s).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Fragment convertFragment(InputFragment inputFragment) {
		Fragment frag = null;
		if (inputFragment != null) {
			if (!Utils.isInData(inputFragment.getFirstLine())) { // in header
				if (!Nemo.getInstance().getDomain().getMedia().getModel().isUseSeparatorInStationHeader()) {
					frag = new CharFragment(inputFragment);
				} else {
					frag = new CsvFragment(inputFragment);
				}	
			} else {
				if (Nemo.getInstance().getDomain().getMedia().getModel().getSeparator().isEmpty() || !Nemo.getInstance().getDomain().getMedia().getModel().getUseTableForCsvData()) {
					frag = new CharFragment(inputFragment);
				} else {
					frag = new CsvFragment(inputFragment);
				}	
			}
		}
		return frag;
	}

}