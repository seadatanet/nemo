package fr.ifremer.nemo.input;

import com.ago.xml.*;
import fr.ifremer.nemo.domain.Parameter;
import fr.ifremer.nemo.domain.ParameterValue;
import fr.ifremer.nemo.util.*;
import fr.ifremer.nemo.ws.WSParameter;
import java.io.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class ParameterModel
 * 
 * Classe permettant de modéliser une paramètre mesuré. Le paramètre peut être
 * convertit en utilisant la formule de conversion fournie par l'attribut
 * conversionFormula
 * 
 * @author Gerard BUNEL
 */
public class ParameterModel implements Cloneable, SAXConsumer {

	/**
	 * Expression mathématique permettant de convertir la valeur du paramètre.
	 * La variable d'entrée est nommée "x" dans l'expression.Ex: 3*x+2. Les
	 * constantes "pi" et "e" sont reconnues ainsi que l'ensemble des fonctions
	 * mathématiques de base (cos, sin, log, etc...)
	 */
	protected String _conversionFormula;

	public String _inputDefault;

	/**
	 * Représente le fragment de ligne du fichier d'entrée où la valeur du
	 * paramètre peut être lue
	 */
	protected LineFragment _fragment;

	protected org.nfunk.jep.JEP _jepParser;

	/**
	 * Représente le paramètre modélisé par ce ParameterModel
	 */
	protected Parameter _model;

	public ParameterModel() {
		_conversionFormula = "";
		_fragment = new LineFragment();
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
			getFragment().writeXMLOn(a_Writer);
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
	public Object clone() throws CloneNotSupportedException {
		ParameterModel copy = null;
		copy = (ParameterModel) super.clone();
		copy._fragment = new LineFragment();
		if (getFragment() != null) {
			copy.setFragment((LineFragment) getFragment().clone());
		}
		copy._model = null;
		if (getModel() != null) {
			copy.setModel((Parameter) getModel().clone());
		}
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
		releaseFragment();
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
	/*public void setInputDefault(String a_String) {
		try {
			setInputDefault(Float.parseFloat(a_String));
		} catch (Exception e) {
		}		
	}
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
	public void convert(String a_Line, ParameterValue a_ParameterValue) throws StringIndexOutOfBoundsException {

		org.nfunk.jep.JEP parser = null;
		
		String fragmnt = _fragment.retreive(a_Line);

		if (_inputDefault.trim().equals(fragmnt)){
			a_ParameterValue.setValue(Float.parseFloat(_model.getDefault()));
		}
			

		
	/*	if ((float) val == _inputDefault) {
			a_ParameterValue.setValue(Float.parseFloat(_model.getDefault()));
		}*/ else {
			double val;
			try {
			val = Double.parseDouble(fragmnt);
			}
			catch (NumberFormatException e)
			{
				throw new NumberFormatException("Erreur lors du traitement de la donnée\r\nProblème de conversion de la chaine " + e.getMessage().substring(e.getMessage().indexOf("\""),e.getMessage().lastIndexOf("\"")) + "\" en valeur décimale" + "\r\n");
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
					a_ParameterValue.setValue((float) result);
				} else {
					parser = getJepParser();
					parser.addVariable("x", val);
					double result = parser.getValue();
					a_ParameterValue.setValue((float) result);
				}
			} else {
				a_ParameterValue.setValue((float) val);
			}
		}
	}

	/**
	 * 
	 * @return String
	 */
	public String toString() {
		return getModel().toString();
	}

} /* End of class: ParameterModel */
