package fr.ifremer.nemo.input;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.nfunk.jep.JEP;
import org.xml.sax.Attributes;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.AddedParameter;
import fr.ifremer.nemo.domain.InputDomain;
import fr.ifremer.nemo.domain.ParameterValue;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.format.LatitudeFormat;
import fr.ifremer.nemo.util.format.LongitudeFormat;

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

        if (value.equals(Global.EMPTY_STRING) && !_inputDefault.trim().equals(Global.EMPTY_STRING)) {
            pValue.setValue(Double.NaN);
            pValue.setQc(Global.DEFAULT_FLAG);
        } else {
            if (_inputDefault.trim().equals(value)) {
                pValue.setValue(Double.parseDouble(_parameter.getDefault()));
                pValue.setQc(Global.DEFAULT_FLAG);
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
                
                // Flag QC
                if (_flag != null) {
                    String flag = _flag.retreive(inputLine);
                    
                    // Correspondance SDN
                    flag = Nemo.getInstance().getDomain().getCruise().getStation().getModel().getFlagTable().getFlagSDN(flag);
                    pValue.setQc(flag);
                }
            }
        }
        _lineValue = pValue;
    }
    
    public void computeAddedParameterValue(Station currentStation,
                                           String inputLine) throws Exception{
        ParameterValue pValue = new ParameterValue();
        AddedParameter p = (AddedParameter) _parameter;
        
        InputDomain iDomain = Nemo.getInstance().getDomain();
        StationDomain sDomain = iDomain.getCruise().getStation();
        
        String stringValue = null;
        String flag = Global.ZERO;
        switch (p.getIdParam()){
            case DEPH :
                if (iDomain.getMedia().getModel().getInputType() == E_InputType.PROFILE){
                    
                    // récupération de la valeur de la pression
                    ParameterModel pModelPRES = sDomain.getData().getModel().getParameterModel(Intern.paramPRES);
                    if (pModelPRES.getLineValue() == null){
                        pModelPRES.computeParameterValue(inputLine);
                    }
                    ParameterValue valuePRES = pModelPRES.getLineValue();                   
                    
                    // calculDepth
                    // FAE091 : calcul de la profondeur en fonction de la pression
                    double X, Gr, P, Depth;

                    P = valuePRES.getValue();
                    X = Math.sin((Double)currentStation.getLatitude() / 57.29578);
                    double X2 = X * X;

                    Gr = 9.780318 * (1.0 + (5.2788E-3 + 2.36E-5 * X2) * X2) + 1.092E-6 * P;
                    Depth = (((-1.82E-15 * P + 2.279E-10) * P - 2.2512E-5) * P + 9.72659) * P;
                    Depth = Depth / Gr;
                    
                    stringValue = Formatter.getSingleton().formatDouble(p.getFormat(), Depth);

                    flag = valuePRES.getQc();
                    if (flag.equals(Global.EMPTY_STRING)) {
                        flag = Global.ZERO;
                    }
                }else{
                    // = SensorDepth
                    stringValue = String.valueOf(currentStation.getSensorDepth());
                    flag = currentStation.getMetaFlags().get(Intern.S_SENSORDEPTH);
                }
                break;
            case CJDY :
                // julianDate = $stationModel.julianDate($station, $i)
                // Long date = horodate.getDate();
                //JulianDate jDate = new JulianDate(date);
                stringValue = "TODO"; //TODO (new DecimalFormat(p.getFormat())).format(jDate.getJour());
                flag = currentStation.getMetaFlags().get(Intern.DATETIME);
                break;
            case DTUT :
                // time-ISO8601
                SimpleDateFormat fDate = new SimpleDateFormat(p.getFormat());
                //stringValue = fDate.format(new Date(horodate.getDate()));
                stringValue = "TODO"; //TODO station.getVariantDateTime(index);
                flag = currentStation.getMetaFlags().get(Intern.DATETIME);
                break;
            case YEAR :
                // $formatter.formatInt( "%4d", $station.parametersAt(0).valuesAt($i).year)
                stringValue = "TODO"; //TODO Formatter.getSingleton().formatInt(p.getFormat(), horodate.getYear());
                flag = currentStation.getMetaFlags().get(Intern.DATETIME);
                break;
            case MNTH :
                // $formatter.formatInt( "%02d", $station.parametersAt(0).valuesAt($i).month)
                stringValue = "TODO"; //TODO Formatter.getSingleton().formatInt(p.getFormat(), horodate.getMonth());
                flag = currentStation.getMetaFlags().get(Intern.DATETIME);
                break;
            case DAYX :
                // $formatter.formatInt( "%02d", $station.parametersAt(0).valuesAt($i).day)
                stringValue = "TODO"; //TODO Formatter.getSingleton().formatInt(p.getFormat(), horodate.getDay());
                flag = currentStation.getMetaFlags().get(Intern.DATETIME);
                break;
            case TIME :
                // $formatter.formatDateTime( "hh24mmss", $station.parametersAt(0).valuesAt($i).date )
                try{
                    stringValue = "TODO"; //TODO Formatter.getSingleton().formatDateTime(p.getFormat(), horodate.getDate());
                    flag = currentStation.getMetaFlags().get(Intern.DATETIME);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case LATX :
            case LONX :
            case BATH :
                // mesuré - flag = celui de la métadonnée
                pValue = convertAdded(currentStation, inputLine);
                break;
        }
        if (stringValue != null){
            pValue.setStringValue(stringValue);
            pValue.setQc(flag);
        }

        _lineValue = pValue;
    }

    @SuppressWarnings("nls")
    private ParameterValue convertAdded(Station station, String line) 
        throws StringIndexOutOfBoundsException, ParseException {

        AddedParameter p = (AddedParameter) _parameter;
        ParameterValue paramValue = new ParameterValue();
        String fragmnt = _fragment.fullRetreive(line);
        
        if (fragmnt.equals(Global.EMPTY_STRING)) {
            paramValue.setStringValue(Global.EMPTY_STRING);
            paramValue.setQc(Global.DEFAULT_FLAG);
        } else {
            // VALUE
            if (p.getDefault().equals(fragmnt)) {
                paramValue.setStringValue(p.getDefault());
            }else {
                String value = Global.EMPTY_STRING;
                String formatString = p.getFormat();
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                switch (p.getIdParam()){
                    case LATX :
                        LatitudeFormat formatLAT = new LatitudeFormat(formatString, true);
                        value = (new DecimalFormat("#0.000000", symbols)).format(formatLAT.parseObject(fragmnt));
                        value = Formatter.getSingleton().padStringLeft(value, formatString.length());
                        break;
                    case LONX :
                        LongitudeFormat formatLON = new LongitudeFormat(formatString, true);
                        value = (new DecimalFormat("#0.000000", symbols)).format(formatLON.parseObject(fragmnt));
                        value = Formatter.getSingleton().padStringLeft(value, formatString.length());                        
                        break;
                    case BATH : 
                        String type = formatString.substring(formatString.length() - 1);
                        if (type.equals("f") || type.equals("F")){ 
                            value = String.format(java.util.Locale.US, formatString, Float.parseFloat(fragmnt));
                        }else if (type.equals("d") || type.equals("D")){
                            value = String.format(java.util.Locale.US, formatString, Integer.parseInt(fragmnt));
                        }
                        break;
                    default :
                        break;
                }
                paramValue.setStringValue(value);
            }
            
            // Flag QC
            String metaFlagCode;
            switch (p.getIdParam()){
                case LATX :
                    metaFlagCode = Intern.S_LAT;
                    break;
                case LONX :
                    metaFlagCode = Intern.S_LON;
                    break;
                case BATH :
                    metaFlagCode = Intern.S_DEPTH;
                    break;
                default :
                    metaFlagCode = Global.EMPTY_STRING;
                    break;
            }
            paramValue.setQc(station.getMetaFlags().get(metaFlagCode));
        }
        return paramValue; 
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