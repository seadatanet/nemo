package fr.ifremer.nemo.domain;

import fr.ifremer.nemo.util.Global;

/**
 * Class ParameterValue
 * 
 * Classe permettant de stocker la valeur d'une mesure
 * 
 * @author Gerard BUNEL
 */
public class ParameterValue {

	public String _qc;

	/**
	 * Valeur de la mesure
	 */
	protected Double _value;
    
    /**
     *  
     */
    private String _stringValue;

	public ParameterValue() {
        initValues();
	}
    
    private void initValues(){
        _qc = Global.EMPTY_STRING;
    }

    
    public void reset(){
        release();
        initValues();
    }
    
    public void release(){
        _qc = null;
        _value = null;
        _stringValue = null;
    }

	/**
	 * Read accessor (getter) for attribute _qc
	 * 
	 * @return String
	 * @see ParameterValue#_qc
	 */
	public String getQc() {
		return _qc;
	}

	/**
	 * Write accessor (setter) for attribute _qc
	 * 
	 * @param x
	 *            new value for _qc
	 * @see ParameterValue#_qc
	 */
	public void setQc(String x) {
		_qc = x;
	}

	/**
	 * Read accessor (getter) for attribute _value
	 * 
	 * @return float
	 * @see ParameterValue#_value
	 */
	public Double getValue() {
		return _value;
	}

	/**
	 * Write accessor (setter) for attribute _value
	 * 
	 * @param x
	 *            new value for _value
	 * @see ParameterValue#_value
	 */
	public void setValue(Double x) {
		_value = x;
	}

	/**
	 * Write accessor (setter) for attribute _value
	 * 
	 * @param a_String
	 *            new value for _value as String
	 * @see ParameterValue#_value
	 */
	public void setValue(String a_String) {
		try {
			setValue(Double.parseDouble(a_String));
		} catch (Exception e) {
		}
	}

    /**
     * Getter sur _stringValue.
     * @return the stringValue
     */
    public String getStringValue()
    {
        return _stringValue;
    }

    /**
     * Setter sur _stringValue.
     * @param stringValue the stringValue to set
     */
    public void setStringValue(String stringValue)
    {
        _stringValue = stringValue;
    }

}