package fr.ifremer.nemo.input;

import org.xml.sax.Attributes;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.util.Global;

/**
 * Class DataType
 * Description : Classe permettant la description d'un type de donnees
 * @author Gerard BUNEL
 */
public class DataType extends AbstractModel
{
	/**
	 * Code ROSCOP (MEDATLAS: 3 char max)
	 */
	protected String _code;

	/**
	 * Nombre de profiles de ce type (MEDATLAS: 4 char max)
	 */
	protected int _numberOfProfiles;

    /**
     * indicateur de qualité
     */
	protected boolean _qc;

	/**
	 * Indique si le type de donnees correspond est un capteur
	 */
	protected boolean _sensor;

    
	public DataType() {
        _tagName = ModelDescription.node_datatypeModel;
        initValues();
	}
    
    public DataType(String code, boolean qc, boolean sensor) {
        this();
        _code = code;
        _qc = qc;
        _sensor = sensor;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initValues(){
        _code = Global.EMPTY_STRING;
        _qc = false;
        _sensor = false;
        _numberOfProfiles = 0;
    }

    
    /**
     * @see AbstractModel.java#fromElement
     */
	public static SAXConsumer fromElement(String a_URI, 
                                          String a_LocalName,
                                          String a_QName, 
                                          Attributes a_Attributes){
		DataType el = new DataType();
		el.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		return el;
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void buildFromElement(String a_URI, 
                                 String a_LocalName,
                                 String a_QName, 
                                 Attributes a_Attributes){
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
		if (a_LocalName.equals(ModelDescription.node_nbprofiles)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_nbprofiles);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_qc)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_qc);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_sensor)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_sensor);
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
        // non utilisé
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		a_Writer.putAttribute(ModelDescription.a_code, getCode());
		a_Writer.putTag(ModelDescription.node_nbprofiles, getNumberOfProfiles());
		a_Writer.putTag(ModelDescription.node_qc, isQc());
		a_Writer.putTag(ModelDescription.node_sensor, isSensor());
	}

    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
		DataType copy = null;
		copy = (DataType) super.clone();
		return copy;
	}
    
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void release() {
        // aucun lien à supprimer
	}


    /**
     * Getter pour _code.
     * @return String
     * @see DataType.java#_code
     */
	public String getCode() {
		return _code;
	}

    /**
     * Setter pour _code
     * @param x : nouvelle valeur pour _code
     * @see DataType.java#_code
     */
	public void setCode(String x) {
		_code = x;
	}
    
    /**
     * Getter pour _numberOfProfiles.
     * @return int
     * @see DataType.java#_numberOfProfiles
     */
	public int getNumberOfProfiles() {
		return _numberOfProfiles;
	}

    /**
     * Setter pour _numberOfProfiles
     * @param x : nouvelle valeur pour _numberOfProfiles
     * @see DataType.java#_numberOfProfiles
     */
	public void setNumberOfProfiles(int x) {
		_numberOfProfiles = x;
	}

    /**
     * Setter pour _numberOfProfiles
     * @param s : nouvelle valeur pour _numberOfProfiles sous forme de String
     * @see DataType.java#_numberOfProfiles
     */
	public void setNumberOfProfiles(String s) {
		try {
			setNumberOfProfiles(Integer.parseInt(s));
		} catch (Exception e) {
		}
	}
    
    /**
     * Getter pour _qc.
     * @return boolean
     * @see DataType.java#_qc
     */
	public boolean isQc() {
		return _qc;
	}

    /**
     * Setter pour _qc
     * @param x : nouvelle valeur pour _qc
     * @see DataType.java#_qc
     */
	public void setQc(boolean x) {
		_qc = x;
	}

    /**
     * Setter pour _qc
     * @param s : nouvelle valeur pour _qc sous forme de String
     * @see DataType.java#_qc
     */
	public void setQc(String s) {
        try{
            _qc = new Boolean(s).booleanValue();
        } catch (Exception e) {
        }
	}
    
    /**
     * Getter pour _sensor.
     * @return boolean
     * @see DataType.java#_sensor
     */
	public boolean isSensor() {
		return _sensor;
	}

    /**
     * Setter pour _sensor
     * @param x : nouvelle valeur pour _sensor
     * @see DataType.java#_sensor
     */
	public void setSensor(boolean x) {
		_sensor = x;
	}

    /**
     * Setter pour _sensor
     * @param s : nouvelle valeur pour _sensor sous forme de String
     * @see DataType.java#_sensor
     */
	public void setSensor(String s) {
        try{
            _sensor = new Boolean(s).booleanValue();
        } catch (Exception e) {
        }
	}
}