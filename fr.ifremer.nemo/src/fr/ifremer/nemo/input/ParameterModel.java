package fr.ifremer.nemo.input;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.config.FlagTable;
import fr.ifremer.nemo.domain.AddedParameter;
import fr.ifremer.nemo.domain.HorodatedValue;
import fr.ifremer.nemo.domain.Parameter;
import fr.ifremer.nemo.domain.ParameterValue;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.ui.object.NemoSection;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.JulianDate;
import fr.ifremer.nemo.util.LatitudeFormat;
import fr.ifremer.nemo.util.LongitudeFormat;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.WSParameter;

/**
 * Class ParameterModel
 * 
 * Classe permettant de modeliser une parametre mesure. Le parametre peut etre
 * convertit en utilisant la formule de conversion fournie par l'attribut
 * conversionFormula
 * 
 * @author Gerard BUNEL
 */
public class ParameterModel implements Cloneable, SAXConsumer {

	/**
	 * Expression mathematique permettant de convertir la valeur du parametre.
	 * La variable d'entree est nommee "x" dans l'expression.Ex: 3*x+2. Les
	 * constantes "pi" et "e" sont reconnues ainsi que l'ensemble des fonctions
	 * mathematiques de base (cos, sin, log, etc...)
	 */
	protected String _conversionFormula;

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

	protected org.nfunk.jep.JEP _jepParser;

	/**
	 * Represente le parametre modelise par ce ParameterModel
	 */
	protected Parameter _model;

	public ParameterModel() {
		_conversionFormula = "";
		_fragment = new LineFragment();
		_flag = null;
		_model = null;
	}

	/**
	 * 
	 * @param a_Parameter
	 */
	public ParameterModel(Parameter a_Parameter) {
		this();
		setModel(a_Parameter);
		_inputDefault = a_Parameter.getDefault();
	}

	/**
	 * 
	 * @param a_WSParameter
	 */
	public ParameterModel(WSParameter a_WSParameter) {
		this();
		setModel(new Parameter(a_WSParameter));
	}

	/**
	 * 
	 * Build object from its XML defintion
	 * 
	 * @param a_URI
	 *            Element Namespace (SAX 2)
	 * @param a_LocalName
	 *            Tag name
	 * @param a_QName
	 *            qualified tag name
	 * @param a_Attributes
	 *            XML Element attributes
	 * @return SAXConsumer
	 * 
	 */
	public static SAXConsumer fromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
		ParameterModel tmp = new ParameterModel();
		tmp.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		return (SAXConsumer) tmp;
	}

	/**
	 * 
	 * Retrieve attributes values from their XML defintion
	 * 
	 * @param a_URI
	 *            Element Namespace (SAX 2)
	 * @param a_LocalName
	 *            Tag name
	 * @param a_QName
	 *            qualified tag name
	 * @param a_Attributes
	 *            XML Element attributes
	 * 
	 */
	public void buildFromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
	}

	/**
	 * 
	 * A new XML Element as been found. Parse it.
	 * 
	 * @param a_URI
	 *            XML Namespace (SAX 2)
	 * @param a_LocalName
	 *            Tag name
	 * @param a_QName
	 *            Qualified tag name
	 * @param a_Attributes
	 *            XML Element attributes
	 * @return SAXConsumer
	 * 
	 */
	public SAXConsumer consumeElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
		if (a_LocalName.equals("conversionFormula")) {
			Element2Field setter = new Element2Field(this,
					"setConversionFormula");
			return setter;
		}
		if (a_LocalName.equals("inputDefault")) {
			Element2Field setter = new Element2Field(this, "setInputDefault");
			return setter;
		}
		if (a_LocalName.equals("LineFragment")) {
			LineFragment l = (LineFragment) LineFragment.fromElement(a_URI,
					a_LocalName, a_QName, a_Attributes);
			setFragment(l);
			return (SAXConsumer) l;
		}
		if (a_LocalName.equals("Parameter")) {
			Parameter l = (Parameter) Parameter.fromElement(a_URI, a_LocalName,
					a_QName, a_Attributes);
			setModel(l);
			return (SAXConsumer) l;
		}
		if (a_LocalName.equals("Flag")) {
			LineFragment l = (LineFragment) LineFragment.fromElement(a_URI,
					a_LocalName, a_QName, a_Attributes);
			setFlag(l);
			return (SAXConsumer) l;
		}
		return null;
	}

	/**
	 * 
	 * The XML Element is a CDATA filled element
	 * 
	 * @param a_Characters
	 *            The CDATA content
	 * 
	 */
	public void consumeCharacters(String a_Characters) throws SAXException {
	}

	/**
	 * 
	 * Generate XML defintion on a Stream
	 * 
	 * @param a_Writer
	 *            XMLOutputWriter to which to write the XML Definition
	 * 
	 */
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		a_Writer.putTag("conversionFormula", getConversionFormula());
		a_Writer.putTag("inputDefault", getInputDefault());
		if (getFragment() != null) {
			getFragment().writeXMLOn(a_Writer, "LineFragment");
		}
		if (getFlag() != null) {
			getFlag().writeXMLOn(a_Writer, "Flag");
		}
		if (getModel() != null) {
			getModel().writeXMLOn(a_Writer);
		}
	}

	/**
	 * 
	 * Generate XML defintion on a XMLOutputWriter
	 * 
	 * @param a_Writer
	 *            the XMLOutputWriter to which to write the XML Definition
	 * 
	 */
	public void writeXMLOn(XMLOutputWriter a_Writer) {
		a_Writer.openTag("ParameterModel");
		writeXMLBodyOn(a_Writer);
		a_Writer.closeTag();
	}

	/**
	 * 
	 * Generate XML defintion on a Stream
	 * 
	 * @param a_Stream
	 *            the OutptStream to which to write the XML Definition
	 * 
	 */
	public void writeXMLOn(OutputStream a_Stream) {
		XMLOutputWriter writer = new XMLOutputWriter(a_Stream);
		writeXMLOn(writer);
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	@Override
    public Object clone() throws CloneNotSupportedException {
		ParameterModel copy = null;
		copy = (ParameterModel) super.clone();
		//copy._fragment = new LineFragment();
		if (getFragment() != null) {
			copy.setFragment((LineFragment) getFragment().clone());
		}
		copy._model = null;
		if (getModel() != null) {
            if (getModel() instanceof AddedParameter){
                copy.setModel((AddedParameter)((AddedParameter) getModel()).clone());
            }else{
                copy.setModel((Parameter) getModel().clone());
            }
		}
		//copy._flag = new LineFragment();
		if (getFlag() != null) {
			copy.setFlag((LineFragment) getFlag().clone());
		}
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
		releaseFragment();
		releaseFlag();
		releaseModel();
	}

	/**
	 * Read accessor (getter) for attribute _conversionFormula
	 * 
	 * @return String
	 * @see ParameterModel#_conversionFormula
	 */
	public String getConversionFormula() {
		return _conversionFormula;
	}

	/**
	 * Write accessor (setter) for attribute _conversionFormula
	 * 
	 * @param x
	 *            new value for _conversionFormula
	 * @see ParameterModel#_conversionFormula
	 */
	public void setConversionFormula(String x) {
		_conversionFormula = x;
	}

	/**
	 * Read accessor (getter) for attribute _inputDefault
	 * 
	 * @return float
	 * @see ParameterModel#_inputDefault
	 */
	public String getInputDefault() {
		return _inputDefault;
	}

	/**
	 * Write accessor (setter) for attribute _inputDefault
	 * 
	 * @param x
	 *            new value for _inputDefault
	 * @see ParameterModel#_inputDefault
	 */
	public void setInputDefault(String x) {
		_inputDefault = x;
	}

	/**
	 * Write accessor (setter) for attribute _inputDefault
	 * 
	 * @param a_String
	 *            new value for _inputDefault as String
	 * @see ParameterModel#_inputDefault
	 */
	/*
	 * public void setInputDefault(String a_String) { try {
	 * setInputDefault(Float.parseFloat(a_String)); } catch (Exception e) { } }
	 */
	/**
	 * Remove any link for role fragment
	 */
	public void releaseFragment() {
		setFragment(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public LineFragment getFragment() {
		return _fragment;
	}

	/**
	 * Create a new link with x for role fragment
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setFragment(LineFragment x) {
		if (_fragment == x)
			return;
		_fragment = x;
	}

	/**
	 * Remove any link for role fragment
	 */
	public void releaseFlag() {
		setFlag(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public LineFragment getFlag() {
		return _flag;
	}

	/**
	 * Create a new link with x for role fragment
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setFlag(LineFragment x) {
		if (_flag == x)
			return;
		_flag = x;
	}

	/**
	 * Remove any link for role model
	 */
	public void releaseModel() {
		setModel(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public Parameter getModel() {
		return _model;
	}

	/**
	 * Create a new link with x for role model
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setModel(Parameter x) {
		if (_model == x)
			return;
		_model = x;
	}

	protected org.nfunk.jep.JEP getJepParser() {
		return _jepParser;
	}

	protected void setJepParser(org.nfunk.jep.JEP parser) {
		_jepParser = parser;
	}

	/**
	 * 
	 * @param a_Line
	 * @param a_ParameterValue
	 */
	public ParameterValue convert(String a_Line, ParameterValue a_ParameterValue,
			FlagTable flagTable) throws StringIndexOutOfBoundsException {

		org.nfunk.jep.JEP parser = null;

		String fragmnt = _fragment.retreive(a_Line);

		if (fragmnt.equals(Global.EMPTY_STRING)) {
			a_ParameterValue.setValue(Double.NaN);
			a_ParameterValue.setQc(Global.DEFAULT_FLAG);
		} else {
			if (_inputDefault.trim().equals(fragmnt)) {
				a_ParameterValue.setValue(Double.parseDouble(_model.getDefault()));
                a_ParameterValue.setQc(Global.DEFAULT_FLAG);
			}else {
				double val;
				try {
					val = Double.parseDouble(fragmnt);
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
						a_ParameterValue.setValue((Double) result);
					} else {
						parser = getJepParser();
						parser.addVariable("x", val);
						double result = parser.getValue();
						a_ParameterValue.setValue((Double) result);
					}
				} else {
					a_ParameterValue.setValue((Double) val);
				}
                
				// Flag QC
				if (_flag != null) {
				    String flag = _flag.retreive(a_Line);
				    
				    // Correspondance SDN
				    flag = flagTable.getFlagSDN(flag);
				    a_ParameterValue.setQc(flag);
				}
			}

		}
        return a_ParameterValue;
	}

	/**
	 * 
	 * @return String
	 */
	public String toString() {
		return getModel().toString();
	}
    
    
    
    
    
    public ParameterValue convertAdded(ParameterValue paramValue,
                        Station station, 
                        String inputType, 
                        int index, 
                        int indexPRES, 
                        String line)
    throws StringIndexOutOfBoundsException, ParseException{
        
        AddedParameter p = (AddedParameter) _model;
        
        HorodatedValue horodate = null;
        if (station.parametersAt(0).getModel().valuesAt(index) instanceof HorodatedValue){
            horodate = (HorodatedValue) station.parametersAt(0).getModel().valuesAt(index);
        }
        String stringValue = null;
        String flag = Global.ZERO;
        switch (p.getIdParam()){
            case DEPH :
                if (inputType.equals(Global.PROFILE)){
                    // calculDepth
                    // FAE091 : calcul de la profondeur en fonction de la pression
                    double X, Gr, P, Depth;

                    P = station.getParameters()[indexPRES].getModel().getValues()[index].getValue();
                    X = Math.sin(station.getLatitude() / 57.29578);
                    X = X * X;

                    Gr = 9.780318 * (1.0 + (5.2788E-3 + 2.36E-5 * X) * X) + 1.092E-6 * P;
                    Depth = (((-1.82E-15 * P + 2.279E-10) * P - 2.2512E-5) * P + 9.72659) * P;
                    Depth = Depth / Gr;
                    
                    stringValue = Formatter.getSingleton().formatDouble(p.getFormat(), Depth);

                    flag = station.getParameters()[indexPRES].getModel().getValues()[index].getQc();
                    if (flag.equals(Global.EMPTY_STRING)) {
                        flag = Global.ZERO;
                    }
                }else{
                    // = SensorDepth
                    stringValue = String.valueOf(station.getTimeSeriesInformation().getSensorDepth());
                    flag = station.getMetaFlag(NemoSection.S_SENSORDEPTH);
                }
                break;
            case CJDY :
                // julianDate = $stationModel.julianDate($station, $i)
                Long date = horodate.getDate();
                JulianDate jDate = new JulianDate(date);
                stringValue = (new DecimalFormat(p.getFormat())).format(jDate.getJour());
                flag = station.getMetaFlag(Global.DATETIME);
                break;
            case DTUT :
                // time-ISO8601
                SimpleDateFormat fDate = new SimpleDateFormat(p.getFormat());
                //stringValue = fDate.format(new Date(horodate.getDate()));
                stringValue = station.getVariantDateTime(index);
                flag = station.getMetaFlag(Global.DATETIME);
                break;
            case YEAR :
                // $formatter.formatInt( "%4d", $station.parametersAt(0).valuesAt($i).year)
                stringValue = Formatter.getSingleton().formatInt(p.getFormat(), horodate.getYear());
                flag = station.getMetaFlag(Global.DATETIME);
                break;
            case MNTH :
                // $formatter.formatInt( "%02d", $station.parametersAt(0).valuesAt($i).month)
                stringValue = Formatter.getSingleton().formatInt(p.getFormat(), horodate.getMonth());
                flag = station.getMetaFlag(Global.DATETIME);
                break;
            case DAYX :
                // $formatter.formatInt( "%02d", $station.parametersAt(0).valuesAt($i).day)
                stringValue = Formatter.getSingleton().formatInt(p.getFormat(), horodate.getDay());
                flag = station.getMetaFlag(Global.DATETIME);
                break;
            case TIME :
                // $formatter.formatDateTime( "hh24mmss", $station.parametersAt(0).valuesAt($i).date )
                try{
                    stringValue = Formatter.getSingleton().formatDateTime(p.getFormat(), horodate.getDate());
                    flag = station.getMetaFlag(Global.DATETIME);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case LATX :
            case LONX :
                // mesuré - flag = celui de la métadonnée
                paramValue = convertCoordinates(station, line);
                break;
            case BATH :
                // mesuré - flag = celui de la métadonnée
                paramValue = convertAddedInData(station, line);
                break;
        }
        if (stringValue != null){
            paramValue.setStringValue(stringValue);
            paramValue.setQc(flag);
        }
        return paramValue;
    }
    

    public ParameterValue convertCoordinates(Station station,
                                        String line) 
        throws StringIndexOutOfBoundsException, ParseException {

        AddedParameter p = (AddedParameter) _model;
        ParameterValue paramValue = new ParameterValue();
        String fragmnt = getFragment().fullRetreive(line);
        
        if (fragmnt.equals(Global.EMPTY_STRING)) {
            paramValue.setStringValue(Global.EMPTY_STRING);
            paramValue.setQc(Global.DEFAULT_FLAG);
        } else {
            // VALUE
            if (getModel().getDefault().equals(fragmnt)) {
                paramValue.setStringValue(getModel().getDefault());
            }else {
                String value = Global.EMPTY_STRING;
                String formatString = getModel().getFormat();
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                switch (p.getIdParam()){
                    case LATX :
                        LatitudeFormat formatLAT = new LatitudeFormat(formatString, true);
                        //value = String.valueOf(formatLAT.parseObject(fragmnt));
                        //fragmnt = 
                        //value = String.format(java.util.Locale.US, formatString, Float.parseFloat(formatLAT.parseObject(fragmnt)));
                        value = (new DecimalFormat("#0.000000", symbols)).format(formatLAT.parseObject(fragmnt)); //$NON-NLS-1$
                        value = Formatter.getSingleton().padStringLeft(value, formatString.length());
                        break;
                    case LONX :
                        LongitudeFormat formatLON = new LongitudeFormat(formatString, true);
                        //value = String.valueOf(formatLON.parseObject(fragmnt));
                        value = (new DecimalFormat("#0.000000", symbols)).format(formatLON.parseObject(fragmnt)); //$NON-NLS-1$
                        value = Formatter.getSingleton().padStringLeft(value, formatString.length());                        
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
                    metaFlagCode = NemoSection.S_LAT;
                    break;
                case LONX :
                    metaFlagCode = NemoSection.S_LON;
                    break;
                default :
                    metaFlagCode = Global.EMPTY_STRING;
                    break;
            }
            paramValue.setQc(station.getMetaFlag(metaFlagCode));
        }
        return paramValue; 
    }
    

    public ParameterValue convertAddedInData(Station station,
                                        String line) 
        throws StringIndexOutOfBoundsException {

        AddedParameter p = (AddedParameter) _model;
        ParameterValue paramValue = new ParameterValue();
        String fragmnt = getFragment().fullRetreive(line);
        
        if (fragmnt.equals(Global.EMPTY_STRING)) {
            paramValue.setStringValue(Global.EMPTY_STRING);
            paramValue.setQc(Global.DEFAULT_FLAG);
        } else {
            // VALUE
            if (getModel().getDefault().equals(fragmnt)) {
                paramValue.setStringValue(getModel().getDefault());
            }else {
                String value = Global.EMPTY_STRING;
                String formatString = getModel().getFormat();
                String type = formatString.substring(formatString.length() - 1);
                if (type.equals("f") || type.equals("F")){       //$NON-NLS-1$//$NON-NLS-2$
                    value = String.format(java.util.Locale.US, formatString, Float.parseFloat(fragmnt));
                }else if (type.equals("d") || type.equals("D")){ //$NON-NLS-1$ //$NON-NLS-2$
                    value = String.format(java.util.Locale.US, formatString, Integer.parseInt(fragmnt));
                }
                paramValue.setStringValue(value);
            }
            
            // Flag QC
            String metaFlagCode;
            switch (p.getIdParam()){
                case BATH :
                    metaFlagCode = NemoSection.S_DEPTH;
                    break;
                default :
                    metaFlagCode = Global.EMPTY_STRING;
                    break;
            }
            paramValue.setQc(station.getMetaFlag(metaFlagCode));
        }
        return paramValue;        
    }

} /* End of class: ParameterModel */
