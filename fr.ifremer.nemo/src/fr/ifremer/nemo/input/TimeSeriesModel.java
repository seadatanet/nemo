package fr.ifremer.nemo.input;

import java.beans.IntrospectionException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.Element2Field;
import com.ago.xml.PathElementConsumer;
import com.ago.xml.PathElementHandler;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.TimeSeriesInformation;
import fr.ifremer.nemo.ui.object.NemoSection;
import fr.ifremer.nemo.util.DateTimeFormat;
import fr.ifremer.nemo.util.FloatFormat;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.LatitudeFormat;
import fr.ifremer.nemo.util.LongitudeFormat;

/**
 * Class TimeSeriesModel
 * 
 * 
 * @author Gerard BUNEL
 */
public class TimeSeriesModel implements Cloneable, PathElementHandler,
		SAXConsumer {

	protected String _sampleRateConversion;

	protected StationModel _stationModel;

	protected org.nfunk.jep.JEP _jepParser;

	protected java.util.Map<String, PropertyModel> _properties;

	public TimeSeriesModel() {
		_sampleRateConversion = Global.EMPTY_STRING;
		_properties = new java.util.LinkedHashMap<String, PropertyModel>();
		_stationModel = null;
	}

	/**
	 * 
	 * @param a_StationModel
	 */
	public TimeSeriesModel(StationModel a_StationModel) {
		this();
		setStationModel(a_StationModel);
		initProperties();
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
		TimeSeriesModel tmp = new TimeSeriesModel();
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
		if (a_LocalName.equals("sampeRateConversion")) {
			Element2Field setter = new Element2Field(this,
					"setSampleRateConversion");
			return setter;
		}
		if (a_LocalName.equals("properties")) {
			return new PathElementConsumer("properties", this);
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
		a_Writer.putTag("sampeRateConversion", getSampleRateConversion());
		a_Writer.openTag("properties");
		for (PropertyModel t : getProperties()) {
			t.writeXMLOn(a_Writer);
		}
		a_Writer.closeTag();
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
		a_Writer.openTag("TimeSeriesModel");
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
		TimeSeriesModel copy = null;
		copy = (TimeSeriesModel) super.clone();
		copy._stationModel = null;
		copy._properties = new java.util.LinkedHashMap<String, PropertyModel>();
		for (PropertyModel t0 : getProperties()) {
			copy.addProperty((PropertyModel) t0.clone());
		}
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
		releaseProperties();
		releaseStationModel();
	}

	/**
	 * Read accessor (getter) for attribute _sampleRateConversion
	 * 
	 * @return String
	 * @see TimeSeriesModel#_sampleRateConversion
	 */
	public String getSampleRateConversion() {
		return _sampleRateConversion;
	}

	/**
	 * Write accessor (setter) for attribute _sampleRateConversion
	 * 
	 * @param x
	 *            new value for _sampleRateConversion
	 * @see TimeSeriesModel#_sampleRateConversion
	 */
	public void setSampleRateConversion(String x) {
		_sampleRateConversion = x;
	}

	/**
	 * Remove any link for role stationModel
	 */
	public void releaseStationModel() {
		setStationModel(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public StationModel getStationModel() {
		return _stationModel;
	}

	/**
	 * Create a new link with x for role stationModel
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setStationModel(StationModel x) {
		if (_stationModel == x)
			return;
		StationModel old = _stationModel;
		if (_stationModel != null) {
			_stationModel = null;
			old.setTimeSeriesModel(null);
		}
		_stationModel = x;
		if (x != null) {
			x.setTimeSeriesModel(this);
		}
	}

	/**
	 * release all links for role "properties"
	 */
	public void releaseProperties() {
		for (PropertyModel t : getProperties()) {
            t.release();
			removeProperty(t);
		}
	}

	/**
	 * remove link with x for role "properties"
	 * 
	 * @param x
	 *            the linked object to unlink
	 */
	public void removeProperty(PropertyModel x) {
		if (!propertiesIncludes(x))
			return;
		nocheckRemoveProperty(x);
	}

	/**
	 * Add a link for role "properties"
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void addProperty(PropertyModel x) {
		if (propertiesIncludes(x))
			return;
		nocheckAddProperty(x);
	}

	/**
	 * Returns the collection of linked objects for role "properties"
	 * 
	 * @return Array of linked objects
	 */
	public PropertyModel[] getProperties() {
		PropertyModel[] v = new PropertyModel[_properties.size()];
		_properties.values().toArray(v);
		return v;
	}

	/**
	 * Returns linked object for role "properties" at index i
	 * 
	 * @param i
	 *            the index of the linked object
	 * @return linked object at index i
	 */
	public PropertyModel propertiesAt(String i) {
		return _properties.get(i);
	}

	/**
	 * remove x from the linked objects for role "properties"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see TimeSeriesModel#removeProperty(PropertyModel)
	 */
	protected void nocheckRemoveProperty(PropertyModel x) {
		_properties.remove(x.getProperty());
	}

	/**
	 * adds x to the linked objects for role "properties"
	 * 
	 * DO NOT USE. use non privileged method instead
	 * 
	 * @param x
	 *            the object to link with
	 * @see TimeSeriesModel#addProperty(PropertyModel)
	 */
	protected void nocheckAddProperty(PropertyModel x) {
		_properties.put(x.getProperty(), x);
	}

	/**
	 * returns the number of linked objects for role "properties"
	 * 
	 * @return the number of linked objects
	 */
	public int propertiesSize() {
		return _properties.size();
	}

	/**
	 * returns true if x is linked to the receiver for role "properties"
	 * 
	 * @param x
	 *            an instance of PropertyModel
	 */
	public boolean propertiesIncludes(PropertyModel x) {
		return _properties.containsKey(x.getProperty());
	}

	/**
	 * returns an Iterator to iterate over the elements of "properties"
	 * 
	 */
	public java.util.Iterator<PropertyModel> propertiesIterator() {
		return _properties.values().iterator();

	}

	/**
	 * 
	 * @param a_Station
	 * @param a_Header
	 * @param isRefDateBased
	 * @param a_RefDate
	 * @return TimeSeriesInformation
	 * @exception IntrospectionException
	 * @exception IllegalAccessException
	 * @exception InvocationTargetException
	 * @exception ParseException
	 */
	public TimeSeriesInformation convert(Station a_Station, 
                                         String inputType,
                                         String[] a_Header)
   {
		TimeSeriesInformation timeSeries = a_Station.getTimeSeriesInformation();
		for (PropertyModel m : getProperties()) {             
            try {
                if (!m.isToCompute()){    
                	m.applyOn(timeSeries, a_Header);
                	_stationModel.addFlag(m, a_Station, a_Header);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
		}
        
        
        _stationModel.addCalculateFlag(a_Station, Global.DURATION, Global.DATETIME, Global.ENDDATETIME);
        _stationModel.addCalculateFlag(a_Station, Global.DISTANCETOBOTTOM, NemoSection.S_DEPTH, NemoSection.S_SENSORDEPTH);        

        // SAMPLING RATE
        convertSampleRate(timeSeries);
        
		return timeSeries;
	}

	/**
	 * 
	 * @param a_TimeSeriesInformation
	 */
    private void convertSampleRate(TimeSeriesInformation a_TimeSeriesInformation) {

        PropertyModel samplingRate = propertiesAt(NemoSection.S_SAMPLINGRATE);
		org.nfunk.jep.JEP parser = null;

		if (_sampleRateConversion.trim().length() > 0
				&& !samplingRate.isUserProvided()) {

			if (getJepParser() == null) {

				parser = new org.nfunk.jep.JEP();
				parser.addStandardFunctions();
				parser.addStandardConstants();
				parser.addVariable("x", a_TimeSeriesInformation.getSamplingRate());
				parser.parseExpression("x" + _sampleRateConversion.trim());
				setJepParser(parser);
				double result = parser.getValue();
				a_TimeSeriesInformation.setSamplingRate((float) result);
			} else {
				parser = getJepParser();
				parser.addVariable("x", a_TimeSeriesInformation.getSamplingRate());
				double result = parser.getValue();
				a_TimeSeriesInformation.setSamplingRate((float) result);

			}

		}
	}
    public long calculSamplingRate(TimeSeriesInformation a_TimeSeriesInformation){
        long rate = 0;
        if (a_TimeSeriesInformation != null){            
            rate = new Float(a_TimeSeriesInformation.getSamplingRate() * 1000.0f).longValue();
            org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
            parser.addStandardFunctions();
            parser.addStandardConstants();
            parser.addVariable("x", rate);      //$NON-NLS-1$
            parser.parseExpression("x");        //$NON-NLS-1$
            
            Double resultJEP = parser.getValue();
            rate = resultJEP.longValue();
        }
        return rate;
    }

    /**
     * 
     * @param a_Station
     */
    public void computeEndDate(Station a_Station) {
        TimeSeriesInformation timeSeries = a_Station.getTimeSeriesInformation();
        if (Float.isNaN(timeSeries.getEndDate())) 
        {
            long rate = new Float(timeSeries.getSamplingRate() * 1000.0f).longValue();
            //long rate = new Float(timeSeries.getSamplingRate()).longValue();
            int size = a_Station.parametersAt(0).getModel().valuesSize() - 1;
            long date = a_Station.getFullDate() + (size * rate);
            TimeZone tz = TimeZone.getTimeZone("GMT"); //$NON-NLS-1$
            Calendar cal = Calendar.getInstance(tz);
            cal.clear();
            cal.setTimeInMillis(date);
            
            timeSeries.setEndTime(cal.getTimeInMillis());            
            timeSeries.setEndDate(cal.getTimeInMillis());
        }
    }

	/**
	 * 
	 * @return TimeSeriesInformation
	 */
	public TimeSeriesInformation getTimeSeriesInformation() {
		return getStationModel().getTimeSeriesInformation();
	}

	/**
	 * 
	 * @param path
	 *            the association path name
	 * @param a_URI
	 *            a_URI XML Namespace (SAX 2)
	 * @param a_LocalName
	 *            Tag name
	 * @param a_QName
	 *            Qualified tag name
	 * @param a_Attributes
	 *            XML Element attributes
	 * @return SAXConsumer
	 * @exception SAXException
	 */
	public SAXConsumer handlePathElement(String path, String a_URI,
			String a_LocalName, String a_QName, Attributes a_Attributes)
			throws SAXException {
		SAXConsumer c = null;
		if (path.equals("properties")) {
			c = PropertyModel.fromElement(a_URI, a_LocalName, a_QName,
					a_Attributes);
			((PropertyModel) c).setSource(getTimeSeriesInformation());
			addProperty((PropertyModel) c);
			return c;
		}
		return null;
	}

	protected org.nfunk.jep.JEP getJepParser() {
		return _jepParser;
	}

	protected void setJepParser(org.nfunk.jep.JEP parser) {
		_jepParser = parser;
	}

	/**
	 * 
	 */
	public void initProperties() {
		try {
			addProperty(new PropertyModel("endDate",
					getTimeSeriesInformation(),	new DateTimeFormat("dd/MM/yyyy")));
			addProperty(new PropertyModel("endTime",
					getTimeSeriesInformation(), new DateTimeFormat("hh24:mm")));
			addProperty(new PropertyModel("endLatitude",
					getTimeSeriesInformation(), new LatitudeFormat("NDD MM SS")));
			addProperty(new PropertyModel("endLongitude",
					getTimeSeriesInformation(), new LongitudeFormat("NDDD MM SS")));
			addProperty(new PropertyModel("endDepth",
					getTimeSeriesInformation(), new FloatFormat("%4.0f")));
			addProperty(new PropertyModel("sensorDepth",
					getTimeSeriesInformation(), new FloatFormat("%4.0f")));
			addProperty(new PropertyModel("samplingRate",
					getTimeSeriesInformation(), new FloatFormat("%4.0f")));
			addProperty(new PropertyModel("magneticDeclination",
					getTimeSeriesInformation(), new FloatFormat("%4.1f")));
		} catch (Exception e) {
		}
	}

} /* End of class: TimeSeriesModel */
