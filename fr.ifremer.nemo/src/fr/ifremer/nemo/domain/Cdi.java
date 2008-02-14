package fr.ifremer.nemo.domain;

import java.io.OutputStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

/**
 * Class Cdi
 * 
 * Classe regroupant les infos CDI
 * 
 * @author Herve TANIOU
 */
public class Cdi implements Cloneable, SAXConsumer {

	/**
	 * Compteur CDI
	 */
	protected String _compteur;

	/**
	 * Type de plate-forme
	 */
	protected String _typePlateForme;

	/**
	 * Accès aux données
	 */
	protected String _accesDonnees;

	public Cdi() {
		_compteur = "";
		_typePlateForme = "";
		_accesDonnees = "";
	}

/*	*//**
	 * 
	 * @param isTimeSeries
	 *//*
	public Station(boolean isTimeSeries) {
		this();
		if (isTimeSeries) {
			setTimeSeriesInformation(new TimeSeriesInformation());
		}
	}*/

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
		Cdi tmp = new Cdi();
		tmp.consumeElement(a_URI, a_LocalName, a_QName, a_Attributes);
		// tmp.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
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
		String tmp;
		if ((tmp = a_Attributes.getValue("compteur")) != null) {
			setCompteur(tmp);
		}
		if ((tmp = a_Attributes.getValue("typePlateForme")) != null) {
			setTypePlateForme(tmp);
		}
		if ((tmp = a_Attributes.getValue("accesDonnees")) != null) {
			setAccesDonnees(tmp);
		}
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
		if (a_LocalName.equals("compteur")) {
			Element2Field setter = new Element2Field(this, "setCompteur");
			return setter;
		}
		if (a_LocalName.equals("typePlateForme")) {
			Element2Field setter = new Element2Field(this, "setTypePlateForme");
			return setter;
		}
		if (a_LocalName.equals("accesDonnees")) {
			Element2Field setter = new Element2Field(this, "setAccesDonnees");
			return setter;
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

//	/**
//	 * 
//	 * The XML Element is a CDATA filled element
//	 * 
//	 * @param path
//	 *            the association path name
//	 * @param a_URI
//	 *            XML Namespace (SAX 2)
//	 * @param a_LocalName
//	 *            Tag name
//	 * @param a_QName
//	 *            Qualified tag name
//	 * @param a_Attributes
//	 *            XML Element attributes
//	 * @return SAXConsumer
//	 * 
//	 */
//	public SAXConsumer handlePathElement(String path, String a_URI,
//			String a_LocalName, String a_QName, Attributes a_Attributes)
//			throws SAXException {
//		SAXConsumer c = null;
//		if (path.equals("parameters")) {
//			c = Parameter
//					.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
//			addParameter((Parameter) c);
//			return c;
//		}
//		return null;
//	}

	/**
	 * 
	 * Generate XML defintion on a Stream
	 * 
	 * @param a_Writer
	 *            XMLOutputWriter to which to write the XML Definition
	 * 
	 */
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		a_Writer.putTag("compteur", getCompteur());
		a_Writer.putTag("typePlateForme", getTypePlateForme());
		a_Writer.putTag("accesDonnees", getAccesDonnees());
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
		a_Writer.openTag("Cdi");
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

//	/**
//	 * Returns a copy of this Object
//	 * 
//	 * @return a copy of this object
//	 */
//	public Object clone() throws CloneNotSupportedException {
//		Cdi copy = null;
//		copy = (Cdi) super.clone();
//		
//		return copy;
//	}

//	/**
//	 * Remove any associated links
//	 */
//	public void release() {
//		releaseDataType();
//		releaseParameters();
//		releaseSurvey();
//		releaseTimeSeriesInformation();
//	}

	/**
	 * Read accessor (getter) for attribute _compteur
	 * 
	 * @return String
	 * @see Cdi#_compteur
	 */
	public String getCompteur() {
		return _compteur;
	}

	/**
	 * Write accessor (setter) for attribute _compteur
	 * 
	 * @param s
	 *            new value for _compteur
	 * @see Cdi#_compteur
	 */
	public void setCompteur(String s) {
		_compteur = s;
	}

	/**
	 * Read accessor (getter) for attribute _typePlateForme
	 * 
	 * @return String
	 * @see Cdi#_typePlateForme
	 */
	public String getTypePlateForme() {
		return _typePlateForme;
	}

	/**
	 * Write accessor (setter) for attribute _typePlateForme
	 * 
	 * @param s
	 *            new value for _typePlateForme
	 * @see Dci#_typePlateForme
	 */
	public void setTypePlateForme(String s) {
		_typePlateForme = s;
	}

	/**
	 * Read accessor (getter) for attribute _accesDonnees
	 * 
	 * @return String
	 * @see Cdi#_date
	 */
	public String getAccesDonnees() {
		return _accesDonnees;
	}

	/**
	 * Write accessor (setter) for attribute _accesDonnees
	 * 
	 * @param s
	 *            new value for _accesDonnees
	 * @see Cdi#_accesDonnees
	 */
	public void setAccesDonnees(String s) {
		_accesDonnees = s;
	}

//	/**
//	 * Remove any link for role dataType
//	 */
//	public void releaseDataType() {
//		setDataType(null);
//	}
//
//	/**
//	 * returns the linked object for role <% aRole name%>
//	 * 
//	 * @return the object linked
//	 */
//	public DataType getDataType() {
//		return _dataType;
//	}
//
//	/**
//	 * Create a new link with x for role dataType
//	 * 
//	 * @param x
//	 *            the object to link with
//	 */
//	public void setDataType(DataType x) {
//		if (_dataType == x)
//			return;
//		_dataType = x;
//	}
//
//	/**
//	 * Remove any link for role survey
//	 */
//	public void releaseSurvey() {
//		setSurvey(null);
//	}
//
//	/**
//	 * returns the linked object for role <% aRole name%>
//	 * 
//	 * @return the object linked
//	 */
//	public Survey getSurvey() {
//		return _survey;
//	}
//
//	/**
//	 * Create a new link with x for role survey
//	 * 
//	 * @param x
//	 *            the object to link with
//	 */
//	public void setSurvey(Survey x) {
//		if (_survey == x)
//			return;
//		Survey old = _survey;
//		if (_survey != null) {
//			_survey = null;
//			old.removeStation(this);
//		}
//		_survey = x;
//		if (x != null) {
//			x.addStation(this);
//		}
//	}
//
//	/**
//	 * release all links for role "parameters"
//	 */
//	public void releaseParameters() {
//		for (Parameter t : getParameters()) {
//			removeParameter(t);
//		}
//	}
//
//	/**
//	 * remove link with x for role "parameters"
//	 * 
//	 * @param x
//	 *            the linked object to unlink
//	 */
//	public void removeParameter(Parameter x) {
//		if (!parametersIncludes(x))
//			return;
//		nocheckRemoveParameter(x);
//		x.setStation(null);
//	}
//
//	/**
//	 * Add a link for role "parameters"
//	 * 
//	 * @param x
//	 *            the object to link with
//	 */
//	public void addParameter(Parameter x) {
//		if (parametersIncludes(x))
//			return;
//		nocheckAddParameter(x);
//		x.setStation(this);
//	}
//
//	/**
//	 * Returns the collection of linked objects for role "parameters"
//	 * 
//	 * @return Array of linked objects
//	 */
//	public Parameter[] getParameters() {
//		Parameter[] v = new Parameter[_parameters.size()];
//		_parameters.toArray(v);
//		return v;
//	}
//
//	/**
//	 * Returns linked object for role "parameters" at index i
//	 * 
//	 * @param i
//	 *            the index of the linked object
//	 * @return linked object at index i
//	 */
//	public Parameter parametersAt(int i) {
//		return (Parameter) _parameters.get(i);
//	}
//
//	/**
//	 * returns index of object x in "parameters"
//	 * 
//	 * @param x
//	 *            the linked object
//	 */
//	public int parametersIndexOf(Parameter x) {
//		if (!parametersIncludes(x))
//			return -1;
//		for (int i = 0; i < parametersSize(); i++) {
//			if (parametersAt(i) == x) {
//				return i;
//			}
//		}
//		return -1;
//	}
//
//	/**
//	 * move x one index before in the list for role "parameters"
//	 * 
//	 * @param x
//	 *            the object to move
//	 */
//	public void parametersMoveUp(Parameter x) {
//		int index;
//		index = parametersIndexOf(x);
//		if (index < 1)
//			return;
//		nocheckMoveParameterAt(x, index - 1);
//	}
//
//	/**
//	 * move x to the top of list for role "parameters"
//	 * 
//	 * @param x
//	 *            the object to move
//	 */
//	public void parametersMoveTop(Parameter x) {
//		int index;
//		index = parametersIndexOf(x);
//		if (index < 1)
//			return;
//		nocheckMoveParameterAt(x, 0);
//	}
//
//	/**
//	 * move x one index more in the list for role "parameters"
//	 * 
//	 * @param x
//	 *            the object to move
//	 */
//	public void parametersMoveDown(Parameter x) {
//		int index;
//		index = parametersIndexOf(x);
//		if (index < 0 || index > parametersSize() - 2)
//			return;
//		nocheckMoveParameterAt(x, index + 1);
//	}
//
//	/**
//	 * move x to the bottom of list for role "parameters"
//	 * 
//	 * @param x
//	 *            the object to move
//	 */
//	public void parametersMoveBottom(Parameter x) {
//		int index;
//		index = parametersIndexOf(x);
//		if (index < 0 || index > parametersSize() - 2)
//			return;
//		nocheckMoveParameterAt(x, parametersSize() - 1);
//	}
//
//	/**
//	 * move x one index more in the list for role "parameters"
//	 * 
//	 * @param x
//	 *            the object to move
//	 */
//	public void nocheckMoveParameterAt(Parameter x, int a_Index) {
//		int index;
//		Parameter tmp;
//		index = parametersIndexOf(x);
//		tmp = _parameters.get(index);
//		_parameters.remove(index);
//		_parameters.add(a_Index, tmp);
//	}
//
//	/**
//	 * remove x from the linked objects for role "parameters"
//	 * 
//	 * DO NOT USE. use non privileged method instead
//	 * 
//	 * @param x
//	 *            the object to link with
//	 * @see Station#removeParameter(Parameter)
//	 */
//	protected void nocheckRemoveParameter(Parameter x) {
//		_parameters.remove(x);
//	}
//
//	/**
//	 * adds x to the linked objects for role "parameters"
//	 * 
//	 * DO NOT USE. use non privileged method instead
//	 * 
//	 * @param x
//	 *            the object to link with
//	 * @see Station#addParameter(Parameter)
//	 */
//	protected void nocheckAddParameter(Parameter x) {
//		_parameters.add(x);
//	}
//
//	/**
//	 * returns the number of linked objects for role "parameters"
//	 * 
//	 * @return the number of linked objects
//	 */
//	public int parametersSize() {
//		return _parameters.size();
//	}
//
//	/**
//	 * returns true if x is linked to the receiver for role "parameters"
//	 * 
//	 * @param x
//	 *            an instance of Parameter
//	 */
//	public boolean parametersIncludes(Parameter x) {
//		return _parameters.contains(x);
//	}
//
//	/**
//	 * returns an Iterator to iterate over the elements of "parameters"
//	 * 
//	 */
//	public java.util.Iterator<Parameter> parametersIterator() {
//		return _parameters.iterator();
//
//	}
//
//	/**
//	 * Remove any link for role timeSeriesInformation
//	 */
//	public void releaseTimeSeriesInformation() {
//		setTimeSeriesInformation(null);
//	}
//
//	/**
//	 * returns the linked object for role <% aRole name%>
//	 * 
//	 * @return the object linked
//	 */
//	public TimeSeriesInformation getTimeSeriesInformation() {
//		return _timeSeriesInformation;
//	}
//
//	/**
//	 * Create a new link with x for role timeSeriesInformation
//	 * 
//	 * @param x
//	 *            the object to link with
//	 */
//	public void setTimeSeriesInformation(TimeSeriesInformation x) {
//		if (_timeSeriesInformation == x)
//			return;
//		TimeSeriesInformation old = _timeSeriesInformation;
//		if (_timeSeriesInformation != null) {
//			_timeSeriesInformation = null;
//			old.setStation(null);
//		}
//		_timeSeriesInformation = x;
//		if (x != null) {
//			x.setStation(this);
//		}
//	}

	/**
	 * 
	 * @param o
	 * @return boolean
	 */
	public boolean equals(Object o) {
		return (o == this);
	}

//	/**
//	 * 
//	 * @return String[]
//	 */
//	public String[] getCommentLines() {
//		LineNumberReader reader = new LineNumberReader(new StringReader(
//				getComment()));
//		String line = null;
//		java.util.Vector<String> lines = new java.util.Vector<String>();
//		try {
//			while ((line = reader.readLine()) != null) {
//				lines.add(line);
//			}
//		} catch (Exception e) {
//		} finally {
//			try {
//				reader.close();
//			} catch (Exception ex) {
//			}
//		}
//		String[] lineArray = new String[lines.size()];
//		return lines.toArray(lineArray);
//	}
//
//	/**
//	 * 
//	 * @return float
//	 */
//	public float getCorrectedTime() {
//		if (!Float.isNaN(_time)) {
//			TimeZone tz = TimeZone.getTimeZone("GMT");
//			Calendar cal = Calendar.getInstance(tz);
//			cal.clear();
//			Calendar cal2 = Calendar.getInstance(tz);
//			cal2.clear();
//			cal2.setTimeInMillis(new Float(_time).longValue());
//			cal.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
//			cal.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
//			cal.set(Calendar.SECOND, cal2.get(Calendar.SECOND));
//			return getGMTDate(cal.getTimeInMillis());
//		} else {
//			return _time;
//		}
//	}
//
//	/**
//	 * 
//	 * @return String
//	 */
//	public String getDefaultGlobalParametersQC() {
//		return getGlobalParametersQC().replace('0', '9');
//	}
//
//	/**
//	 * 
//	 * @return long
//	 */
//	public long getFullDate() {
//		TimeZone tz = TimeZone.getTimeZone("GMT");
//		Calendar cal = Calendar.getInstance(tz);
//		cal.clear();
//		cal.setTimeInMillis(_date);
//		if (!Float.isNaN(_time)) {
//			Calendar cal2 = Calendar.getInstance(tz);
//			cal2.clear();
//			cal2.setTimeInMillis(new Float(_time).longValue());
//			cal.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
//			cal.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
//			cal.set(Calendar.SECOND, cal2.get(Calendar.SECOND));
//		}
//		return getGMTDate(cal.getTimeInMillis());
//	}
//
//	/**
//	 * 
//	 * @return String
//	 */
//	public String getGlobalParametersQC() {
//		StringBuffer buffer = null;
//		if (_timeSeriesInformation != null) {
//			buffer = new StringBuffer("0000");
//		} else {
//			buffer = new StringBuffer();
//		}
//		for (int i = 0; i < parametersSize(); i++) {
//			buffer.append('0');
//		}
//		return buffer.toString();
//	}
//
//	/**
//	 * 
//	 * @param a_Date
//	 * @return long
//	 */
//	public long getGMTDate(long a_Date) {
//		return a_Date + ((new Float(getOffsetTU())).longValue() * 3600 * 1000);
//	}
//
//	/**
//	 * 
//	 * @return int
//	 */
//	public int getParametersNumber() {
//		if (getSurvey().isTimeSeriesSurvey()) {
//			return parametersSize() + 4;
//		} else {
//			return parametersSize();
//		}
//	}
//
//	/**
//	 * 
//	 * @return int
//	 */
//	public int getReferenceInteger() {
//		try {
//			return Integer.parseInt(_reference);
//		} catch (Exception e) {
//			return 0;
//		}
//	}
//
//	/**
//	 * 
//	 * @return String
//	 */
//	public String getSortCriteria() {
//		return Long.toString(getFullDate()) + getReference();
//	}

} /* End of class: Dci */