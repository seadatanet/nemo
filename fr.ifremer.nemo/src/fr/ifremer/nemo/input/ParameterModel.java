package fr.ifremer.nemo.input;

import org.nfunk.jep.JEP;
import org.xml.sax.Attributes;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.FlagTable;
import fr.ifremer.nemo.domain.AddedParameter;
import fr.ifremer.nemo.domain.ParameterValue;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ui.actions.OptionsAction;

/**
 * Class ParameterModel
 * Description : Modélisation d'un paramètre mesuré. 
 * @author Gerard BUNEL
 */
public class ParameterModel extends AbstractModel
{
	/**
	 * Expression mathematique permettant de convertir la valeur du parametre.
	 * La variable d'entree est nommee "x" dans l'expression.Ex: 3*x+2. Les
	 * constantes "pi" et "e" sont reconnues ainsi que l'ensemble des fonctions
	 * mathematiques de base (cos, sin, log, etc...)
	 */
	protected String _conversionFormula;

    /**
     * Valeur par défaut dans le fichier
     */
	public String _inputDefault;

	/**
	 * Represente le fragment de ligne du fichier d'entree où la valeur du
	 * parametre peut etre lue
	 */
	protected LineFragment _fragment;

	/**
	 * Represente le fragment de ligne du fichier d'entree où la valeur du flag
	 * peut etre lue
	 */
	protected LineFragment _flag;
    
	/**
	 * Represente le parametre modelise par ce ParameterModel
	 */
	protected Parameter _parameter;
    
    protected ParameterValue _lineValue;
    
    protected JEP _jepParser;
    
	public ParameterModel() {
        _tagName = ModelDescription.node_parameterModel;
        initValues();
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initValues(){
        _conversionFormula = Global.EMPTY_STRING;
        _inputDefault = Global.EMPTY_STRING;
        _fragment = new LineFragment();
        _flag = null;
        _parameter = new Parameter();
        _lineValue = null;
    }


    /**
     * @see AbstractModel.java#fromElement
     */
	public static SAXConsumer fromElement(String a_URI, 
                                          String a_LocalName,
                                          String a_QName, 
                                          Attributes a_Attributes){
		ParameterModel el = new ParameterModel();
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
        // l'élément n'a pas d'attribut
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
	public SAXConsumer consumeElement(String a_URI, 
                                      String a_LocalName,
                                      String a_QName, 
                                      Attributes a_Attributes){

        SAXConsumer el = null;
		if (a_LocalName.equals(ModelDescription.node_formula)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_formula);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_indefault)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_indefault);
			return setter;
		}        
		if (a_LocalName.equals(ModelDescription.node_dataFragment)) {
            el = LineFragment.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
            setFragment((LineFragment) el);
            return el;
            //return new PathElementConsumer(ModelDescription.node_dataFragment, this);
		}
		if (a_LocalName.equals(ModelDescription.node_parameter)) {
            el = Parameter.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
            setParameter((Parameter) el);
            return el;
            //return new PathElementConsumer(ModelDescription.node_parameter, this);
		}
		if (a_LocalName.equals(ModelDescription.node_flag)) {
            el = LineFragment.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
            setFlag((LineFragment) el);
            return el;
            //return new PathElementConsumer(ModelDescription.node_flag, this);
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
		a_Writer.putTag(ModelDescription.node_formula, getConversionFormula());
		a_Writer.putTag(ModelDescription.node_indefault, getInputDefault());
		if (getFragment() != null) {
            getFragment().setTagName(ModelDescription.node_dataFragment);
            getFragment().writeXMLOn(a_Writer);
		}
		if (getFlag() != null) {
            getFlag().setTagName(ModelDescription.node_flag);
			getFlag().writeXMLOn(a_Writer);
		}
		if (getParameter() != null) {
			getParameter().writeXMLOn(a_Writer);
		}
	}

    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
		ParameterModel copy = null;
		copy = (ParameterModel) super.clone();
		copy._fragment = null;
		if (getFragment() != null) {
			copy.setFragment((LineFragment) getFragment().clone());
		}
		copy._parameter = null;
		if (getParameter() != null) {
            copy.setParameter((Parameter) getParameter().clone());
		}
		copy._flag = null;
		if (getFlag() != null) {
			copy.setFlag((LineFragment) getFlag().clone());
		}
		return copy;
	}
    
    @SuppressWarnings("nls")
    public void computeParameterValue(String inputLine){
        ParameterValue pValue = new ParameterValue();
        JEP parser = null;
        String value = _fragment.retreive(inputLine);

        // calcul de la valeur du paramètre
        if (value.equals(Global.EMPTY_STRING) && !_inputDefault.trim().equals(Global.EMPTY_STRING)) {
            pValue.setValue(Double.NaN);
        } else {
            if (_inputDefault.trim().equals(value)) {
                pValue.setValue(Double.parseDouble(_parameter.getDefault()));
            }else {
                double val;
                try {
                    val = Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException(Messages
                            .ParameterModel_1
                            + e.getMessage().substring(
                                    e.getMessage().indexOf("\""),
                                    e.getMessage().lastIndexOf("\""))
                            + Messages.ParameterModel_2 + Global.RN);
                }
                if (_conversionFormula.trim().length() > 0) {
                    if (getJepParser() == null) {
                        parser = new org.nfunk.jep.JEP();
                        parser.addStandardFunctions();
                        parser.addStandardConstants();
                        parser.addVariable("x", val);
                        parser.parseExpression(_conversionFormula.trim());
                        setJepParser(parser);
                        double result = parser.getValue();
                        pValue.setValue(result);
                    } else {
                        parser = getJepParser();
                        parser.addVariable("x", val);
                        double result = parser.getValue();
                        pValue.setValue(result);
                    }
                } else {
                    pValue.setValue(val);
                }
            }
        }
        
        // détermination du flag du paramètre
        pValue.setQc(retreiveParameterFlag(inputLine));
        
        _lineValue = pValue;
    }
    
    public String retreiveParameterFlag(String inputLine)
    {
        String value = _fragment.retreive(inputLine);
        
        //FAE 4431 Flag qualité par défault configurable
        String flagConfig = Nemo.getInstance().getConfig().getDefaultFlag();
        String flagQc = flagConfig==null?Global.ZERO:flagConfig;
     
        if (_inputDefault.trim().equals(value)  || value.equals(Global.EMPTY_STRING)){
            // la valeur est celle par défaut ou la valeur est manquante
            flagQc = Global.DEFAULT_FLAG;
        }
        else{
            if (_flag != null) {
                // le flag est a récupérer dans fichier
                // table de correspondance SDN
                FlagTable flagTable = Nemo.getInstance().getDomain().getCruise().getStation().getModel().getFlagTable();
                flagQc = flagTable.getFlagSDN(_flag.retreive(inputLine));
            }
                
            // sinon, on laisse le flag à 0
        }
        return flagQc;
    }
    
    public void computeAddedParameterValue(Station currentStation,
                                           String inputLine,
                                           long currentDate) throws Exception{
        ParameterValue pValue = new ParameterValue();
        AddedParameter p = (AddedParameter) _parameter;
        
        String stringValue = null;
        
        if (p.getIdParam().isMesured()){
            stringValue = p.compute(_fragment.fullRetreive(inputLine));
        }
        else{
            stringValue = p.getIdParam().getValue(currentStation, inputLine, currentDate);
        }
        
        if (stringValue != null){
            pValue.setStringValue(stringValue);
            pValue.setQc(((AddedParameter)_parameter).getIdParam().getFlag(currentStation, inputLine));
        }

        _lineValue = pValue;
    }    

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getParameter().toString();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
	public void release() {
		releaseFragment();
		releaseFlag();
        releaseParameter();
        releaseLineValue();
	}

    /**
     * Supprime _fragment
     */
    public void releaseFragment() {
        if (_fragment != null)
            _fragment.release();
        _fragment = null;
    }

    /**
     * Supprime _flag
     */
    public void releaseFlag() {
        if (_flag != null)
            _flag.release();
        _flag = null;
    }

    /**
     * Supprime _parameter
     */
    public void releaseParameter() {
        if (_parameter != null)
            _parameter.release();
        _parameter = null;
    }

    /**
     * Supprime _lineValue
     */
    public void releaseLineValue() {
        if (_lineValue != null)
            _lineValue.release();
        _lineValue = null;
    }

    protected JEP getJepParser() {
        return _jepParser;
    }

    protected void setJepParser(JEP parser) {
        _jepParser = parser;
    }


    /**
     * Getter pour _conversionFormula.
     * @return String
     * @see ParameterModel.java#_conversionFormula
     */
	public String getConversionFormula() {
		return _conversionFormula;
	}

    /**
     * Setter pour _conversionFormula
     * @param x : nouvelle valeur pour _conversionFormula
     * @see ParameterModel.java#_conversionFormula
     */
	public void setConversionFormula(String x) {
		_conversionFormula = x;
	}

    /**
     * Getter pour _inputDefault.
     * @return String
     * @see ParameterModel.java#_inputDefault
     */
	public String getInputDefault() {
		return _inputDefault;
	}

    /**
     * Setter pour _inputDefault
     * @param x : nouvelle valeur pour _inputDefault
     * @see ParameterModel.java#_inputDefault
     */
	public void setInputDefault(String x) {
		_inputDefault = x;
	}

    /**
     * Getter pour _fragment.
     * @return LineFragment
     * @see ParameterModel.java#_fragment
     */
	public LineFragment getFragment() {
		return _fragment;
	}

    /**
     * Setter pour _fragment
     * @param x : nouvelle valeur pour _fragment
     * @see ParameterModel.java#_fragment
     */
	public void setFragment(LineFragment x) {
		if (_fragment == x)
			return;
		_fragment = x;
	}

    /**
     * Getter pour _flag.
     * @return LineFragment
     * @see ParameterModel.java#_flag
     */
	public LineFragment getFlag() {
		return _flag;
	}

    /**
     * Setter pour _flag
     * @param x : nouvelle valeur pour _flag
     * @see ParameterModel.java#_flag
     */
	public void setFlag(LineFragment x) {
		if (_flag == x)
			return;
		_flag = x;
	}

    /**
     * Getter pour _parameter.
     * @return Parameter
     * @see ParameterModel.java#_parameter
     */
	public Parameter getParameter() {
		return _parameter;
	}

    /**
     * Setter pour _parameter
     * @param x : nouvelle valeur pour _parameter
     * @see ParameterModel.java#_parameter
     */
	public void setParameter(Parameter x) {
		if (_parameter == x)
			return;
        _parameter = x;
	}

    /**
     * Getter pour _lineValue.
     * @return ParameterValue
     * @see ParameterModel.java#_lineValue
     */
    public ParameterValue getLineValue()
    {
        return _lineValue;
    }

    /**
     * Setter pour _lineValue
     * @param lineValue : nouvelle valeur pour lineValue
     * @see ParameterModel.java#_lineValue
     */
    public void setLineValue(ParameterValue lineValue)
    {
        _lineValue = lineValue;
    }
  
}