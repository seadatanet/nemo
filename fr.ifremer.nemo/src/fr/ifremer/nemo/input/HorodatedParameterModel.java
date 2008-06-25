package fr.ifremer.nemo.input;

import java.io.OutputStream;
import java.text.ParseException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.PathElementConsumer;
import com.ago.xml.PathElementHandler;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.util.DateTimeFormat;

/**
 * Class HorodatedParameterModel
 * 
 * Permet de modéliser un paramètre horodaté (spécifique séries temporelles)
 * 
 * @author Gerard BUNEL
 */
public class HorodatedParameterModel implements Cloneable, PathElementHandler,
		SAXConsumer {

	/**
	 * Permet de spécifier si l'horodatage est calculé à partir des informations
	 * station (Date/Heure de début, période d'échantillonage)
	 */
	protected boolean _computed;

	/**
	 * Fragment permettant de récupérer la date de mesure du paramètre dans le
	 * fichier d'entrée
	 */
	protected LineFragment _dateTimeFragment;

	protected DateTimeFormat _format;

	/**
	 * 
	 */
	public HorodatedParameterModel() {
		_computed = false;
		_dateTimeFragment = new LineFragment();
		try {
			_format = new DateTimeFormat("dd/MM/yyyy hh24:mm:ss");
		} catch (Exception e) {
		}
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
		HorodatedParameterModel tmp = new HorodatedParameterModel();
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
		String tmp;
		if ((tmp = a_Attributes.getValue("computed")) != null) {
			setComputed(tmp);
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
		if (a_LocalName.equals("dateFragment")) {
			return new PathElementConsumer("dateFragment", this);
		}
		if (a_LocalName.equals("DateTimeFormat")) {
			DateTimeFormat l = (DateTimeFormat) DateTimeFormat.fromElement(
					a_URI, a_LocalName, a_QName, a_Attributes);
			setFormat(l);
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
	 * The XML Element is a CDATA filled element
	 * 
	 * @param path
	 *            the association path name
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
	public SAXConsumer handlePathElement(String path, String a_URI,
			String a_LocalName, String a_QName, Attributes a_Attributes)
			throws SAXException {
		SAXConsumer c = null;
		if (path.equals("dateFragment")) {
			c = LineFragment.fromElement(a_URI, a_LocalName, a_QName,
					a_Attributes);
			setDateTimeFragment((LineFragment) c);
			return c;
		}
		return null;
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
		a_Writer.putAttribute("computed", isComputed());
		if (getDateTimeFragment() != null) {
			a_Writer.openTag("dateFragment");
			getDateTimeFragment().writeXMLOn(a_Writer);
			a_Writer.closeTag();
		}
		if (getFormat() != null) {
			getFormat().writeXMLOn(a_Writer);
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
		a_Writer.openTag("HorodatedParameterModel");
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
		HorodatedParameterModel copy = null;
		copy = (HorodatedParameterModel) super.clone();
		copy._dateTimeFragment = new LineFragment();
		if (getDateTimeFragment() != null) {
			copy.setDateTimeFragment((LineFragment) getDateTimeFragment()
					.clone());
		}
		copy._format = null;
		if (getFormat() != null) {
			copy.setFormat((DateTimeFormat) getFormat().clone());
		}
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
		releaseDateTimeFragment();
		releaseFormat();
	}

	/**
	 * Read accessor (getter) for attribute _computed
	 * 
	 * @return boolean
	 * @see HorodatedParameterModel#_computed
	 */
	public boolean isComputed() {
		return _computed;
	}

	/**
	 * Write accessor (setter) for attribute _computed
	 * 
	 * @param x
	 *            new value for _computed
	 * @see HorodatedParameterModel#_computed
	 */
	public void setComputed(boolean x) {
		_computed = x;
	}

	/**
	 * Write accessor (setter) for attribute _computed
	 * 
	 * @param a_String
	 *            new value for _computed as String
	 * @see HorodatedParameterModel#_computed
	 */
	public void setComputed(String a_String) {
		setComputed(new Boolean(a_String).booleanValue());
	}

	/**
	 * Remove any link for role dateTimeFragment
	 */
	public void releaseDateTimeFragment() {
		setDateTimeFragment(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public LineFragment getDateTimeFragment() {
		return _dateTimeFragment;
	}

	/**
	 * Create a new link with x for role dateTimeFragment
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setDateTimeFragment(LineFragment x) {
		if (_dateTimeFragment == x)
			return;
		_dateTimeFragment = x;
	}

	/**
	 * Remove any link for role format
	 */
	public void releaseFormat() {
		setFormat(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public DateTimeFormat getFormat() {
		return _format;
	}

	/**
	 * Create a new link with x for role format
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setFormat(DateTimeFormat x) {
		if (_format == x)
			return;
		_format = x;
	}

	/**
	 * 
	 * @param a_Line
	 * @param a_RefDate
	 * @return long
	 * @exception java.text.ParseException
	 */
	public long convertTimestamp(String a_Line, long a_RefDate)
			throws ParseException {
		String frgmnt = _dateTimeFragment.retreive(a_Line);
		_format.setRefDate(a_RefDate);
		return (Long) _format.parseObject(frgmnt);
	}

} /* End of class: HorodatedParameterModel */
