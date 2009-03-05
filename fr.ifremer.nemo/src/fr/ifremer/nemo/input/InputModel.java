package fr.ifremer.nemo.input;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.PathElementConsumer;
import com.ago.xml.PathElementHandler;
import com.ago.xml.RootSAXConsumer;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.output.ProgressNotification;

/**
 * Class InputModel
 * 
 * 
 * @author Gerard BUNEL
 */
public class InputModel implements Cloneable, PathElementHandler,
		RootSAXConsumer {

	/**
	 * Date de reference par rapport e laquelle toutes les autres dates sont
	 * fournies
	 */
	protected long _refDate;

	/**
	 * Indique si les dates fournies dans les fichiers d'entree sont donnes en
	 * temps epuise depuis une date de reference
	 */
	protected boolean _refDateBased;

	/**
	 * heure de reference par rapport e laquelle toutes les autres dates sont
	 * fournies
	 */
	protected long _refTime;

	/**
	 * Modele de media contenant la campagne
	 */
	protected SurveyMedia _media;

    
    private boolean converted = false;

	public InputModel() {
		_media = null;
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
		InputModel tmp = new InputModel();
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
		if ((tmp = a_Attributes.getValue("refDateBased")) != null) { //$NON-NLS-1$
			setRefDateBased(tmp);
		}
		if ((tmp = a_Attributes.getValue("refDate")) != null) { //$NON-NLS-1$
			setRefDate(tmp);
		}
		if ((tmp = a_Attributes.getValue("refTime")) != null) { //$NON-NLS-1$
			setRefTime(tmp);
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
		if (a_LocalName.equals("Media")) { //$NON-NLS-1$
			return new PathElementConsumer("Media", this); //$NON-NLS-1$
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
		if (path.equals("Media")) { //$NON-NLS-1$
			c = preferredMediaXMLElement(a_URI, a_LocalName, a_QName,
					a_Attributes);
			setMedia((SurveyMedia) c);
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
		a_Writer.putAttribute("refDateBased", isRefDateBased()); //$NON-NLS-1$
		a_Writer.putAttribute("refDate", getRefDate()); //$NON-NLS-1$
		a_Writer.putAttribute("refTime", getRefTime()); //$NON-NLS-1$
		if (getMedia() != null) {
			a_Writer.openTag("Media"); //$NON-NLS-1$
			getMedia().writeXMLOn(a_Writer);
			a_Writer.closeTag();
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
		a_Writer.openTag("InputModel"); //$NON-NLS-1$
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
		InputModel copy = null;
		copy = (InputModel) super.clone();
		copy._media = null;
		if (getMedia() != null) {
			copy.setMedia((SurveyMedia) getMedia().clone());
		}
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
        releaseMedia();
        _refDate = 0;
        _refDateBased = false;
        _refTime = 0;
        converted = false;
	}

	/**
	 * Read accessor (getter) for attribute _refDate
	 * 
	 * @return long
	 * @see InputModel#_refDate
	 */
	public long getRefDate() {
		return _refDate;
	}

	/**
	 * Write accessor (setter) for attribute _refDate
	 * 
	 * @param x
	 *            new value for _refDate
	 * @see InputModel#_refDate
	 */
	public void setRefDate(long x) {
		_refDate = x;
	}

	/**
	 * Write accessor (setter) for attribute _refDate
	 * 
	 * @param a_String
	 *            new value for _refDate as String
	 * @see InputModel#_refDate
	 */
	public void setRefDate(String a_String) {
		try {
			setRefDate(Long.parseLong(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _refDateBased
	 * 
	 * @return boolean
	 * @see InputModel#_refDateBased
	 */
	public boolean isRefDateBased() {
		return _refDateBased;
	}

	/**
	 * Write accessor (setter) for attribute _refDateBased
	 * 
	 * @param x
	 *            new value for _refDateBased
	 * @see InputModel#_refDateBased
	 */
	public void setRefDateBased(boolean x) {
		_refDateBased = x;
	}

	/**
	 * Write accessor (setter) for attribute _refDateBased
	 * 
	 * @param a_String
	 *            new value for _refDateBased as String
	 * @see InputModel#_refDateBased
	 */
	public void setRefDateBased(String a_String) {
		setRefDateBased(new Boolean(a_String).booleanValue());
	}

	/**
	 * Read accessor (getter) for attribute _refTime
	 * 
	 * @return long
	 * @see InputModel#_refTime
	 */
	public long getRefTime() {
		return _refTime;
	}

	/**
	 * Write accessor (setter) for attribute _refTime
	 * 
	 * @param x
	 *            new value for _refTime
	 * @see InputModel#_refTime
	 */
	public void setRefTime(long x) {
		_refTime = x;
	}

	/**
	 * Write accessor (setter) for attribute _refTime
	 * 
	 * @param a_String
	 *            new value for _refTime as String
	 * @see InputModel#_refTime
	 */
	public void setRefTime(String a_String) {
		try {
			setRefTime(Long.parseLong(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Remove any link for role media
	 */
	private void releaseMedia() {
        if (_media != null) _media.release();
		setMedia(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public SurveyMedia getMedia() {
		return _media;
	}

	/**
	 * Create a new link with x for role media
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setMedia(SurveyMedia x) {
		if (_media == x)
			return;
		SurveyMedia old = _media;
		if (_media != null) {
			_media = null;
			old.setInputModel(null);
		}
		_media = x;
		if (x != null) {
			x.setInputModel(this);
		}
	}

	/**
	 * 
	 * @param a_ProgressNotifier
	 * @return
	 * @exception FileNotFoundException
	 * @exception IOException
	 * @exception ParseException
	 */
	public Survey convert(ProgressNotification a_ProgressNotifier)
			throws NumberFormatException, IOException, ParseException, StringIndexOutOfBoundsException, InterruptedException {

        Survey survey = null;
        
        try{
            if (!converted)
            {   
                long refDate = 0;
                if (isRefDateBased()) {
                    TimeZone tz = TimeZone.getTimeZone("GMT"); //$NON-NLS-1$
                    Calendar cal = Calendar.getInstance(tz);
                    cal.clear();
                    cal.setTimeInMillis(getRefDate());
                    Calendar cal2 = Calendar.getInstance(tz);
                    cal2.clear();
                    cal2.setTimeInMillis(_refTime);
                    cal.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
                    cal.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
                    cal.set(Calendar.SECOND, cal2.get(Calendar.SECOND));
                    
                    refDate = cal.getTimeInMillis();
                    
                }
                survey = _media.convert(_refDateBased, refDate, a_ProgressNotifier);
                _media.setModel(survey);
                //converted = true;
            }
            else
            {
                // la conversion en objet a déjà été réalisée
                survey = _media.getModel();
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        catch (InterruptedException e)
        {
            throw e;
        }
        catch (ParseException e)
        {
            throw e;
        }
        catch (NumberFormatException e)
        {
            throw e;
        }
        catch (StringIndexOutOfBoundsException e)
        {
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("interrompu !"); //$NON-NLS-1$
        }
        
        
        return survey;
	}

	/**
	 * 
	 * @param isTimeSeries
	 */
	public void initFoDirectoryBased(String typeFichier) {
		setMedia(new DirectoryBasedSurvey(typeFichier));
	}	

	/**
	 * 
	 * @param isTimeSeries
	 */
	public void initForSingleFile(String typeFichier) {
		setMedia(new SingleFileSurvey(typeFichier));
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		InputModel model = new InputModel();
		java.io.FileInputStream stream = null;
		try {
			com.ago.xml.XMLBootstrapParser parser = new com.ago.xml.XMLBootstrapParser(
					model);
			stream = new java.io.FileInputStream(args[0]);
			parser.parse(stream, false);
			Survey survey = model.convert(null);
			// survey.writeXMLOn( System.out );
			fr.ifremer.nemo.medatlas.MedatlasSurveyWriter writer = new fr.ifremer.nemo.medatlas.MedatlasSurveyWriter();
			writer.writeSurvey(survey, new java.io.File(args[1]), null);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stream != null) stream.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 
	 * @param a_URI
	 * @param a_LocalName
	 * @param a_QName
	 * @param a_Attributes
	 * @return SurveyMedia
	 * @exception org.xml.sax.SAXException
	 */
	public SurveyMedia preferredMediaXMLElement(String a_URI,
			String a_LocalName, String a_QName, Attributes a_Attributes)
			throws SAXException {
		if (a_LocalName.equals("DirectoryBasedSurvey")) { //$NON-NLS-1$
			return (SurveyMedia) DirectoryBasedSurvey.fromElement(a_URI,
					a_LocalName, a_QName, a_Attributes);
		} //else {
			return (SurveyMedia) SingleFileSurvey.fromElement(a_URI,
					a_LocalName, a_QName, a_Attributes);
		//}
	}

    /**
     * Getter sur converted.
     * @return the converted
     */
    public boolean isConverted()
    {
        return converted;
    }

    /**
     * Setter sur converted.
     * @param converted the converted to set
     */
    public void setConverted(boolean converted)
    {
        this.converted = converted;
    }

} /* End of class: InputModel */
