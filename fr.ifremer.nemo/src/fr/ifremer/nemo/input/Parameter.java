package fr.ifremer.nemo.input;

import org.xml.sax.Attributes;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.domain.AddedParameter.IdParameter;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;

/**
 * Class Parameter
 * Description : Classe permettant la description d'un parametre mesure
 * @author Gerard BUNEL
 */
public class Parameter extends AbstractModel
{
	/**
	 * Code du paramètre (MEDATLAS: 4 char max)
	 */
	protected String _code;

	/**
	 * Valeur par défaut du paramètre (MEDATLAS: 5 char max)
	 */
	protected String _default;

    /**
     * Format à appliquer à la valeur du paramètre
     */
	protected String _format;

	/**
	 * Nom complet du paramètre (IOC def.) (MEDATLAS: 30 char max)
	 */
	protected String _fullName;

	/**
	 * Unité IS/IOC (MEDATLAS: 30 char max)
	 */
	protected String _unit;

	public Parameter() {
        _tagName = ModelDescription.node_parameter;
        initValues();
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initValues(){
        _code = Global.EMPTY_STRING;
        _default = Global.EMPTY_STRING;
        _format = Global.EMPTY_STRING;
        _fullName = Global.EMPTY_STRING;
        _unit = Global.EMPTY_STRING;
    }


    /**
     * @see AbstractModel.java#fromElement
     */
	public static SAXConsumer fromElement(String a_URI, 
                                          String a_LocalName,
                                          String a_QName, 
                                          Attributes a_Attributes){
		Parameter el = new Parameter();
		el.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		return el;
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void buildFromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes){
		String tmp;
		if ((tmp = a_Attributes.getValue(ModelDescription.a_code)) != null) {
			setCode(tmp);
		}
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
	public SAXConsumer consumeElement(String a_URI, 
                                      String a_LocalName,
                                      String a_QName, 
                                      Attributes a_Attributes){
		if (a_LocalName.equals(ModelDescription.node_fullname)) { 
			Element2Field setter = new Element2Field(this, ModelDescription.setter_fullname); 
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_unit)) { 
			Element2Field setter = new Element2Field(this, ModelDescription.setter_unit); 
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_outdefault)) { 
			Element2Field setter = new Element2Field(this, ModelDescription.setter_outdefault); 
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_format)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_format); 
			return setter;
		}
		return null;
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
	public SAXConsumer handlePathElement(String path, 
                                         String a_URI,
                                         String a_LocalName, 
                                         String a_QName, 
                                         Attributes a_Attributes){
	    // not used
		return null;
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		a_Writer.putAttribute(ModelDescription.a_code, getCode());
		a_Writer.putTag(ModelDescription.node_fullname, getFullName());
		a_Writer.putTag(ModelDescription.node_unit, getUnit()); 
		a_Writer.putTag(ModelDescription.node_outdefault, getDefault());
		a_Writer.putTag(ModelDescription.node_format, getFormat());
	}

    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
		Parameter copy = null;
		copy = (Parameter) super.clone();
		return copy;
	}

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return _code;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void release(){
        // aucun lien à supprimer
    }

    /**
     * Getter pour _code.
     * @return String
     * @see Parameter.java#_code
     */
	public String getCode() {
		return _code;
	}

    /**
     * Setter pour _code
     * @param x : nouvelle valeur pour _code
     * @see Fragment.java#_code
     */
	public void setCode(String x) {
		_code = x;
	}

    /**
     * Getter pour _default.
     * @return String
     * @see Parameter.java#_default
     */
	public String getDefault() {
		return _default;
	}

    /**
     * Setter pour _default
     * @param x : nouvelle valeur pour _default
     * @see Fragment.java#_default
     */
	public void setDefault(String x) {
		_default = x;
	}

    /**
     * Getter pour _format.
     * @return String
     * @see Parameter.java#_format
     */
	public String getFormat() {
		return _format;
	}

    /**
     * Setter pour _format
     * @param x : nouvelle valeur pour _format
     * @see Fragment.java#_format
     */
	public void setFormat(String x) {
		_format = x;
	}

    /**
     * Getter pour _fullName.
     * @return String
     * @see Parameter.java#_fullName
     */
	public String getFullName() {
		return _fullName;
	}

    /**
     * Setter pour _fullName
     * @param x : nouvelle valeur pour _fullName
     * @see Fragment.java#_fullName
     */
	public void setFullName(String x) {
		_fullName = x;
	}

    /**
     * Getter pour _unit.
     * @return String
     * @see Parameter.java#_unit
     */
	public String getUnit() {
		return _unit;
	}

    /**
     * Setter pour _unit
     * @param x : nouvelle valeur pour _unit
     * @see Fragment.java#_unit
     */
	public void setUnit(String x) {
		_unit = x;
	}
    
    
    public String getOutputCode(Conversion c){
        String code = null;
        switch (c){
            case ODV:
                code = getOutputOdvCode();
                break;
            case MEDATLAS:
                code = getOutputMedatlasCode();
                break;
        }
        return code;
    }
    
    private String getOutputMedatlasCode(){
        int len = _default.length() + 1;
        if (len <= _code.length()){
            len = _code.length() + 1;
        }
        return Formatter.getSingleton().padString(_code, len);
    }
    
    private String getOutputOdvCode(){
        StringBuffer paramHeader = new StringBuffer();
        String code = _code;
        if (code.equals(IdParameter.CJDY.toString()) || code.equals(IdParameter.DTUT.toString())){
            code = _fullName;
        }
        paramHeader.append(code);
        paramHeader.append(Global.SPACE);
        paramHeader.append('[');
        paramHeader.append(_unit);
        paramHeader.append(']');
        paramHeader.append(Global.TAB);
        
        paramHeader.append("QV:SEADATANET"); //$NON-NLS-1$
        paramHeader.append(Global.TAB);
        
        return paramHeader.toString();
    }
}