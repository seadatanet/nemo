package fr.ifremer.nemo.input;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.PathElementConsumer;
import com.ago.xml.PathElementHandler;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.ui.edit.desc.EditDesc;
import fr.ifremer.nemo.ui.edit.station.AutoLine;
import fr.ifremer.nemo.ui.edit.station.StationSection;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.format.DateTimeFormat;
import fr.ifremer.nemo.util.format.ExternalizableFormat;
import fr.ifremer.nemo.util.format.FloatFormat;
import fr.ifremer.nemo.util.format.IntegerFormat;
import fr.ifremer.nemo.util.format.LatitudeFormat;
import fr.ifremer.nemo.util.format.LongitudeFormat;

/**
 * Class PropertyModel
 * 
 * Classe permettant de modeliser une information. L'information peut etre
 * obtenue: - soit par saisie utilisateur, auquel cas elle est stockee dans un
 * attribut du bean - soit par recuperation dans le fichier d'entree, auquel cas
 * la recuperation de l'information se fait par utilisation du fragment associe
 * (optionnel)
 * 
 * @author Gerard BUNEL
 */
public class PropertyModel implements Cloneable, PathElementHandler,
		SAXConsumer {
    
	/**
	 * Nom de la propriete du bean associe permettant de recuperer/definir la
	 * valeur correspondante. Le bean, doit implementer les methodes d'acces a
	 * la propriete ex: pour une propriete de type String nommee "name", le bean
	 * doit implementer: String getName(); void setName( String a_Name);
	 */
	protected String _property;

	/**
	 * Booleen precisant si la valeur de la propriete est issue du fichier
	 * d'entree (false) ou fournie par l'utilisateur (true). La valeur saisie
	 * par l'utilisateur est stoquee dans la propriete du bean associe
	 */
	protected boolean _userProvided;
    
    /**
     * boolean précisant si cette propriété est, ou non, existante
     */
    protected boolean _toCompute = false;

	protected Object _source;

	/**
	 * Represente le fragment du fichier d'entree oe l'information peut etre
	 * recuperee
	 */
	protected HeaderFragment _fragment;
	
	/**
	 * Represente le fragment du fichier d'entree oe le flag qualite peut etre
	 * recupere
	 */
	protected FlagFragment _flag;

	protected ExternalizableFormat _format;

	public PropertyModel() {
		_property = Global.EMPTY_STRING;
		_userProvided = false;
		_format = null;
		_fragment = null;
		_flag = null;
		_source = null;
	}

	/**
	 * 
	 * @param a_Property
	 * @param a_Source
	 * @param isUserProvided
	 */
	public PropertyModel(String a_Property, Object a_Source,
			boolean isUserProvided) {
		this(a_Property, a_Source);
		_userProvided = isUserProvided;
	}

	/**
	 * 
	 * @param a_Property
	 * @param a_Source
	 * @param a_Format
	 */
	public PropertyModel(String a_Property, Object a_Source,
			ExternalizableFormat a_Format) {
		this();
		_property = a_Property;
		setSource(a_Source);
		setFragment(new HeaderFragment());
		setFormat(a_Format);
	}

	/**
	 * 
	 * @param a_Property
	 * @param a_Source
	 * @param isUserProvided
	 * @param a_Format
	 */
	public PropertyModel(String a_Property, Object a_Source,
			boolean isUserProvided, ExternalizableFormat a_Format) {
		this(a_Property, a_Source);
		_userProvided = isUserProvided;
		setFormat(a_Format);
	}

	/**
	 * 
	 * @param a_Property
	 * @param a_Source
	 */
	public PropertyModel(String a_Property, Object a_Source) {
		this();
		_property = a_Property;
		setSource(a_Source);
		setFragment(new HeaderFragment());
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
		PropertyModel tmp = new PropertyModel();
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
		if ((tmp = a_Attributes.getValue("property")) != null) {
			setProperty(tmp);
		}
		if ((tmp = a_Attributes.getValue("userProvided")) != null) {
			setUserProvided(tmp);
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
		if (a_LocalName.equals("HeaderFragment")) {
			HeaderFragment l = (HeaderFragment) HeaderFragment.fromElement(
					a_URI, a_LocalName, a_QName, a_Attributes);
			setFragment(l);
			return (SAXConsumer) l;
		}
		if (a_LocalName.equals("FlagFragment")) {
			FlagFragment l = (FlagFragment) FlagFragment.fromElement(
					a_URI, a_LocalName, a_QName, a_Attributes);
			setFlag(l);
			return (SAXConsumer) l;
		}
		if (a_LocalName.equals("format")) {
			return new PathElementConsumer("format", this);
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
		if (path.equals("format")) {
			c = preferredFormatXMLElement(a_URI, a_LocalName, a_QName,
					a_Attributes);
			setFormat((ExternalizableFormat) c);
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
		a_Writer.putAttribute("property", getProperty());
		a_Writer.putAttribute("userProvided", isUserProvided());
		if (getFormat() != null) {
			a_Writer.openTag("format");
			getFormat().writeXMLOn(a_Writer);
			a_Writer.closeTag();
		}
		if (getFragment() != null) {
			getFragment().writeXMLOn(a_Writer);
		}
		if (getFlag() != null) {
			getFlag().writeXMLOn(a_Writer);
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
		a_Writer.openTag("PropertyModel");
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
		PropertyModel copy = null;
		copy = (PropertyModel) super.clone();
		copy._source = null;
		copy._fragment = null;
		if (getFragment() != null) {
			copy.setFragment((HeaderFragment) getFragment().clone());
		}
		copy._flag = null;
		if (getFlag() != null) {
			copy.setFlag((FlagFragment) getFlag().clone());
		}
		copy._format = null;
		if (getFormat() != null) {
			copy.setFormat((ExternalizableFormat) getFormat().clone());
		}
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
		releaseFormat();
		releaseFragment();
		releaseSource();
		releaseFlag();
	}

	/**
	 * Read accessor (getter) for attribute _property
	 * 
	 * @return String
	 * @see PropertyModel#_property
	 */
	public String getProperty() {
		return _property;
	}

	/**
	 * Write accessor (setter) for attribute _property
	 * 
	 * @param x
	 *            new value for _property
	 * @see PropertyModel#_property
	 */
	public void setProperty(String x) {
		_property = x;
	}

	/**
	 * Read accessor (getter) for attribute _userProvided
	 * 
	 * @return boolean
	 * @see PropertyModel#_userProvided
	 */
	public boolean isUserProvided() {
		return _userProvided;
	}

	/**
	 * Write accessor (setter) for attribute _userProvided
	 * 
	 * @param x
	 *            new value for _userProvided
	 * @see PropertyModel#_userProvided
	 */
	public void setUserProvided(boolean x) {
		_userProvided = x;
	}

	/**
	 * Write accessor (setter) for attribute _userProvided
	 * 
	 * @param a_String
	 *            new value for _userProvided as String
	 * @see PropertyModel#_userProvided
	 */
	public void setUserProvided(String a_String) {
		setUserProvided(new Boolean(a_String).booleanValue());
	}

	/**
	 * Remove any link for role source
	 */
	public void releaseSource() {
		setSource(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public Object getSource() {
		return _source;
	}

	/**
	 * Create a new link with x for role source
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setSource(Object x) {
		if (_source == x)
			return;
		_source = x;
	}

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
	public HeaderFragment getFragment() {
		return _fragment;
	}

	/**
	 * Create a new link with x for role fragment
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setFragment(HeaderFragment x) {
		if (_fragment == x)
			return;
		_fragment = x;
	}
	
	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public FlagFragment getFlag() {
		return _flag;
	}

	/**
	 * Create a new link with x for role fragment
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setFlag(FlagFragment x) {
		if (_flag == x)
			return;
		_flag = x;
	}
	
	/**
	 * Remove any link for role fragment
	 */
	public void releaseFlag() {
		setFlag(null);
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
	public ExternalizableFormat getFormat() {
		return _format;
	}

	/**
	 * Create a new link with x for role format
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setFormat(ExternalizableFormat x) {
		if (_format == x)
			return;
		_format = x;
	}

	/**
	 * 
	 * @param a_Destination
	 * @param a_Header
	 * @exception IntrospectionException
	 * @exception IllegalAccessException
	 * @exception InvocationTargetException
	 * @exception java.text.ParseException
	 */
	public void applyOn(Object a_Destination, String[] a_Header)
			throws IntrospectionException, IllegalAccessException,
			InvocationTargetException, ParseException {
		Method writer = computeWriteAccessor(a_Destination);
		if (isUserProvided()) {
			Method reader = computeReadAccessor(getSource());
			Object o = reader.invoke(getSource());
			writer.invoke(a_Destination, o);
		} else {
			String r = _fragment.retreive(a_Header);
            if (r.trim().equals(Global.EMPTY_STRING)){    
                Method reader = computeReadAccessor(getSource());
                Object o = reader.invoke(getSource());
                writer.invoke(a_Destination, o);
            }else{
                if (_format != null) {
                    try {
                        if (!(_format instanceof LatitudeFormat)
                                && !(_format instanceof LongitudeFormat)
                                && !(_format instanceof DateTimeFormat))
                        {
                            if (_fragment.isVariable())
                            // stop string at next space
                                r = _fragment.retreiveVariable(a_Header);
                        }
                        if (_format instanceof DateTimeFormat) 
                            r = r.replace(' ', '0');
                        writer.invoke(a_Destination, _format.parseObject(r));
                    }
                    catch (ParseException e)
                    {
                        if (_format instanceof LatitudeFormat)
                            throw new ParseException(Messages.PropertyModel_1 + " : "+ r, 0);
                        else if (_format instanceof LongitudeFormat)
                            throw new ParseException(Messages.PropertyModel_2 + " : "+ r, 0);
                        else if (_format instanceof DateTimeFormat)
                            throw new ParseException(Messages.PropertyModel_3 + " : "+ r, 0);
                        
                    }
                }else{
                    writer.invoke(a_Destination, r);
                }
            }
		}
	}

	/**
	 * 
	 * @param a_Source
	 * @return Method
	 * @exception IntrospectionException
	 */
	public Method computeReadAccessor(Object a_Source)
			throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(a_Source.getClass());
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		PropertyDescriptor desc = null;
		for (int i = 0; i < descriptors.length; i++) {
			if (descriptors[i].getName().equals(_property)) {
				desc = descriptors[i];
				break;
			}
		}
		if (desc != null) {
			return desc.getReadMethod();
		}
		return null;
	}

	/**
	 * 
	 * @param a_Destination
	 * @return Method
	 * @exception IntrospectionException
	 */
	public Method computeWriteAccessor(Object a_Destination)
			throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(a_Destination.getClass());
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		PropertyDescriptor desc = null;
		for (int i = 0; i < descriptors.length; i++) {
			if (descriptors[i].getName().equals(_property)) {
				desc = descriptors[i];
				break;
			}
		}
		if (desc != null) {
			return desc.getWriteMethod();
		}
		return null;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			fr.ifremer.nemo.domain.Station m1 = new fr.ifremer.nemo.domain.Station();
			fr.ifremer.nemo.domain.Station m2 = new fr.ifremer.nemo.domain.Station();
			m1.setReference("TOTO");
			m1.setLatitude(48.0);
			String[] lines = { "192W 32 28", "comment line 1", "comment line 2" };
			PropertyModel model = new PropertyModel("reference", m1, true);
			PropertyModel model2 = new PropertyModel("latitude", m1, true);
			PropertyModel model3 = new PropertyModel("longitude", m1, false);
			HeaderFragment f = model3.getFragment();
			f.setLastCharOffset(10);
			model3.setFormat(new LongitudeFormat("DDDN MM SS"));
			f.setFirstLine(0);
			PropertyModel model4 = new PropertyModel("comment", m1, false);
			f = model4.getFragment();
			f.setFirstLine(2);
			f.setLastLine(3);
			f.setLastCharOffset(-1);
			model.applyOn(m2, new String[0]);
			model2.applyOn(m2, new String[0]);
			model3.applyOn(m2, lines);
			model4.applyOn(m2, lines);
			m2.writeXMLOn(System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param a_URI
	 * @param a_LocalName
	 * @param a_QName
	 * @param a_Attributes
	 * @return SAXConsumer
	 * @exception org.xml.sax.SAXException
	 */
	public SAXConsumer preferredFormatXMLElement(String a_URI,
			String a_LocalName, String a_QName, Attributes a_Attributes)
			throws SAXException {
		try {
			if (a_LocalName.equals("DateTimeFormat")) {
				return (SAXConsumer) DateTimeFormat.fromElement(a_URI,
						a_LocalName, a_QName, a_Attributes);
			} else if (a_LocalName.equals("LatitudeFormat")) {
				return (SAXConsumer) LatitudeFormat.fromElement(a_URI,
						a_LocalName, a_QName, a_Attributes);
			} else if (a_LocalName.equals("LongitudeFormat")) {
				return (SAXConsumer) LongitudeFormat.fromElement(a_URI,
						a_LocalName, a_QName, a_Attributes);
			} else if (a_LocalName.equals("IntegerFormat")) {
				return (SAXConsumer) IntegerFormat.fromElement(a_URI,
						a_LocalName, a_QName, a_Attributes);
			} else if (a_LocalName.equals("FloatFormat")) {
				return (SAXConsumer) FloatFormat.fromElement(a_URI,
						a_LocalName, a_QName, a_Attributes);
			}
			throw new SAXException("Uknown format type");
		} catch (Exception e) {
			throw new SAXException(e);
		}
	}
    

    
    public void copyFragment(PropertyModel initiale){

        // memes valeurs de ligne/debut/fin que initial
        HeaderFragment frag = initiale.getFragment();

        _userProvided = false;
        _fragment.setFirstLine(frag.getFirstLine());
        _fragment.setLastLine(frag.getLastLine());
        _fragment.setFirstCharOffset(frag.getFirstCharOffset());
        _fragment.setLastCharOffset(frag.getLastCharOffset());
    }
    
    public HeaderFragment fillFragment(StationSection stationSection){
        return fillFragment(stationSection, null, 0, true, false);
    }
    
    public HeaderFragment fillFragment(StationSection stationSection, String ligne){
        return fillFragment(stationSection, ligne, 0, true, false);
    }
    
    public HeaderFragment fillFragment(StationSection stationSection, int i){
        return fillFragment(stationSection, null, i, true, false);
    }
    
    public HeaderFragment fillFragment(StationSection stationSection, boolean lastLine, boolean posVariable){
        return fillFragment(stationSection, null, 0, lastLine, posVariable);
    }
    
    public HeaderFragment fillFragment(StationSection stationSection, String ligne, int i, boolean lastLine, boolean posVariable){
        
        // position dans fichier
        _userProvided = false;
        AutoLine line = stationSection.getAutoInput().getLines().get(i);
        
        if (ligne == null)
            ligne = String.valueOf(((Integer.valueOf(line.getNumberLine().getText())) 
                        - Integer.valueOf(EditDesc.sectionEnteteFichier.getValue())));
        String debut = line.getStart().getText();
        String fin = line.getEnd().getText();

        _fragment.setFirstLine(ligne);
        _fragment.setFirstCharOffset(debut);
        _fragment.setLastCharOffset(fin);
        
        if (lastLine){
            _fragment.setLastLine(ligne);
        }
        if (posVariable){
            _fragment.setVariable(stationSection.isVariablePos());
        }
        
        return _fragment;
    }

    /**
     * Getter sur _toCompute.
     * @return the empty
     */
    public boolean isToCompute()
    {
        return _toCompute;
    }

    /**
     * Setter sur _toCompute.
     * @param empty the empty to set
     */
    public void setToCompute(boolean empty)
    {
        _toCompute = empty;
    }

} /* End of class: PropertyModel */
