package fr.ifremer.nemo.input;

import java.io.OutputStream;
import java.text.ParseException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.PathElementConsumer;
import com.ago.xml.PathElementHandler;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.ui.object.NemoSection;
import fr.ifremer.nemo.util.DateTimeFormat;
import fr.ifremer.nemo.util.Global;

/**
 * Class HorodatedParameterModel
 * 
 * Permet de modeliser un parametre horodate (specifique series temporelles)
 * 
 * @author Gerard BUNEL
 */
public class HorodatedParameterModel implements Cloneable, PathElementHandler,
		SAXConsumer {

	/**
	 * Permet de specifier si l'horodatage est calcule e partir des informations
	 * station (Date/Heure de debut, periode d'echantillonage)
	 */
	protected boolean _computed;

	/**
	 * Fragment permettant de recuperer la date de mesure du parametre dans le
	 * fichier d'entree
	 */
	protected LineFragment _dateFragment;
	
	/**
	 * Fragment permettant de recuperer la date de mesure du parametre dans le
	 * fichier d'entree
	 */
	protected LineFragment _timeFragment;

	protected DateTimeFormat _dateFormat;
	
	protected DateTimeFormat _timeFormat;

	/**
	 * 
	 */
	public HorodatedParameterModel() {
		_computed = false;
		_dateFragment = new LineFragment();
		_timeFragment = new LineFragment();
		
		try {
			_dateFormat = new DateTimeFormat("dd/MM/yyyy");
			_timeFormat = new DateTimeFormat("hh24:mm:ss");
		} catch (Exception e) {
			e.printStackTrace();
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
		if (a_LocalName.equals("timeFragment")) {
			return new PathElementConsumer("timeFragment", this);
		}
		if (a_LocalName.equals("dateFormat")) {
			DateTimeFormat l = (DateTimeFormat) DateTimeFormat.fromElement(
					a_URI, a_LocalName, a_QName, a_Attributes);
			setDateFormat(l);
			return (SAXConsumer) l;
		}
		if (a_LocalName.equals("timeFormat")) {
			DateTimeFormat l = (DateTimeFormat) DateTimeFormat.fromElement(
					a_URI, a_LocalName, a_QName, a_Attributes);
			setTimeFormat(l);
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
			setDateFragment((LineFragment) c);
			return c;
		}
		if (path.equals("timeFragment")) {
			c = LineFragment.fromElement(a_URI, a_LocalName, a_QName,
					a_Attributes);
			setTimeFragment((LineFragment) c);
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
		if (!isComputed()) {
			if (getDateFragment() != null) {
				a_Writer.openTag("dateFragment");
				getDateFragment().writeXMLOn(a_Writer, "LineFragment");
				a_Writer.closeTag();
			}
			if (getDateFragment() != null) {
				a_Writer.openTag("timeFragment");
				getTimeFragment().writeXMLOn(a_Writer, "LineFragment");
				a_Writer.closeTag();
			}
			if (getDateFormat() != null) {
//				getDateFormat().writeXMLOn(a_Writer);
				a_Writer.openTag("dateFormat");
				getDateFormat().writeXMLBodyOn(a_Writer);
				a_Writer.closeTag();
			}
			if (getTimeFormat() != null) {
//				getTimeFormat().writeXMLOn(a_Writer);
				a_Writer.openTag("timeFormat");
				getTimeFormat().writeXMLBodyOn(a_Writer);
				a_Writer.closeTag();
			}
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
		
		copy._dateFragment = new LineFragment();
		if (getDateFragment() != null) {
			copy.setDateFragment((LineFragment) getDateFragment()
					.clone());
		}
		
		copy._timeFragment = new LineFragment();
		if (getTimeFragment() != null) {
			copy.setTimeFragment((LineFragment) getTimeFragment()
					.clone());
		}
		
		copy._dateFormat = null;
		if (getDateFormat() != null) {
			copy.setDateFormat((DateTimeFormat) getDateFormat().clone());
		}
		copy._timeFormat = null;
		if (getTimeFormat() != null) {
			copy.setTimeFormat((DateTimeFormat) getTimeFormat().clone());
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
		setDateFragment(null);
		setTimeFragment(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public LineFragment getDateFragment() {
		return _dateFragment;
	}

	/**
	 * Create a new link with x for role dateTimeFragment
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setDateFragment(LineFragment x) {
		if (_dateFragment == x)
			return;
		_dateFragment = x;
	}
	
	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public LineFragment getTimeFragment() {
		return _timeFragment;
	}
	
	/**
	 * Create a new link with x for role dateTimeFragment
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setTimeFragment(LineFragment x) {
		if (_timeFragment == x)
			return;
		_timeFragment = x;
	}

	/**
	 * Remove any link for role format
	 */
	public void releaseFormat() {
		setDateFormat(null);
		setTimeFormat(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public DateTimeFormat getDateFormat() {
		return _dateFormat;
	}

	/**
	 * Create a new link with x for role format
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setDateFormat(DateTimeFormat x) {
		if (_dateFormat == x)
			return;
		_dateFormat = x;
	}
	
	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public DateTimeFormat getTimeFormat() {
		return _timeFormat;
	}

	/**
	 * Create a new link with x for role format
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setTimeFormat(DateTimeFormat x) {
		if (_timeFormat == x)
			return;
		_timeFormat = x;
	}

	/**
	 * 
	 * @param a_Line
	 * @param a_RefDate
	 * @return long
	 * @exception java.text.ParseException
	 */
	public long convertTimestamp(String a_Line/*, long a_RefDate*/)
			throws ParseException {
		
        String frgmntFullDate = null;
        String formatFullDate = null;
        DateTimeFormat format = new DateTimeFormat();
        
        if (getDateFormat().getFormatString().contains("D.d") //$NON-NLS-1$
                && getTimeFormat().getFormatString().contains("D.d")){ //$NON-NLS-1$
            // convert Chronological Julian Date
            LineFragment fullDate = new LineFragment(); 
            fullDate.setFirstCharOffset(getDateFragment().getFirstCharOffset());
            fullDate.setLastCharOffset(getTimeFragment().getLastCharOffset());
            frgmntFullDate = fullDate.fullRetreive(a_Line);
            
            String dateTmp = getDateFormat().getFormatString();
            String timeTmp = getTimeFormat().getFormatString();
            String dateFormat = dateTmp.substring(0, dateTmp.indexOf(".")); //$NON-NLS-1$
            String timeFormat = timeTmp.substring(timeTmp.indexOf(".")); //$NON-NLS-1$
            formatFullDate = dateFormat + timeFormat;
        }else{       
    		String frgmntDate = getDateFragment().fullRetreive(a_Line);
    		String frgmntTime = getTimeFragment().fullRetreive(a_Line);
    
    		// FAE 0001229 : Conversion date et heure : s'il manque des Zeros il faut que NEMO les mettent 
            frgmntDate = frgmntDate.replace(' ', '0');
            frgmntTime = frgmntTime.replace(' ', '0');
            frgmntFullDate = frgmntDate + Global.SPACE + frgmntTime;

            formatFullDate = getDateFormat().getFormatString() + Global.SPACE + getTimeFormat().getFormatString();
        }
        format.setFormatString(formatFullDate);
        format.setRefDate(0);         
        
        return (Long) format.parseObject(frgmntFullDate);
	}
    
    public DateTimeFormat getFormat(String champDest){
        DateTimeFormat result = null;
        if (champDest.equals(NemoSection.S_DATE)){
            result = getDateFormat();
        }else if (champDest.equals(NemoSection.S_TIME)){
            result = getTimeFormat();
        }
        return result;
    }
    
    public LineFragment getFragment (String champDest){
        LineFragment result = null;
        if (champDest.equals(NemoSection.S_DATE)){
            result = getDateFragment();
        }else if (champDest.equals(NemoSection.S_TIME)){
            result = getTimeFragment();
        }
        return result;
    }

} /* End of class: HorodatedParameterModel */
