/* ============================================================================
 * Nom du fichier	: AddedParameter.java
 * Auteur			: dboulang
 * Date				: 9 févr. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.io.OutputStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;

/**
 * Description: AddedParameter - paramètres ajoutés automatiquement par NEMO
 * @author dboulang
 *
 */
public class AddedParameter extends Parameter
{
    
    public static enum IdParameter{
        DEPH, YEAR, MNTH, DAYX, TIME, CJDY, DTUT, LATX, LONX, BATH;
    }
    
    private IdParameter idParam;
    private Conversion cible;

    
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
        AddedParameter tmp = new AddedParameter();
        tmp.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
        return tmp;
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
    @Override
    public void buildFromElement(String a_URI, String a_LocalName,
            String a_QName, Attributes a_Attributes) throws SAXException {
        super.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
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
    @Override
    public SAXConsumer consumeElement(String a_URI, String a_LocalName,
            String a_QName, Attributes a_Attributes) throws SAXException {
        return super.consumeElement(a_URI, a_LocalName, a_QName, a_Attributes);
    }

    /**
     * 
     * The XML Element is a CDATA filled element
     * 
     * @param a_Characters
     *            The CDATA content
     * 
     */
    @Override
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
    @Override
    public SAXConsumer handlePathElement(String path, String a_URI,
            String a_LocalName, String a_QName, Attributes a_Attributes)
            throws SAXException {
//        SAXConsumer c = null;
//        if (path.equals("values")) { //$NON-NLS-1$
//            c = ParameterValue.fromElement(a_URI, a_LocalName, a_QName,
//                    a_Attributes);
//            addValue((ParameterValue) c);
//            return c;
//        }
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
    @Override
    public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
        super.writeXMLBodyOn(a_Writer);
    }

    /**
     * 
     * Generate XML defintion on a XMLOutputWriter
     * 
     * @param a_Writer
     *            the XMLOutputWriter to which to write the XML Definition
     * 
     */
    @Override
    public void writeXMLOn(XMLOutputWriter a_Writer) {
//        a_Writer.openTag("AddedParameter"); //$NON-NLS-1$
//        writeXMLBodyOn(a_Writer);
//        a_Writer.closeTag();
    }

    /**
     * 
     * Generate XML defintion on a Stream
     * 
     * @param a_Stream
     *            the OutptStream to which to write the XML Definition
     * 
     */
    @Override
    public void writeXMLOn(OutputStream a_Stream) {
        super.writeXMLOn(a_Stream);
    }
    

    /**
     * Returns a copy of this Object
     * 
     * @return a copy of this object
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        AddedParameter copy = null;
        copy = (AddedParameter) super.clone();
        copy.idParam = this.idParam;
        copy.cible = this.cible;
        return copy;
    }
    
    @Override
    public void reset(){
        idParam = null;
        cible = null;
        super.reset();
    }
    
    public AddedParameter(){
        super();
    }

    /**
     * 
     */
    public AddedParameter(IdParameter idParam, String formatPRES, String inputType)
    {
        this.idParam = idParam;
        _code = idParam.toString();
        
        switch (idParam){
            case DEPH :
                _default = Global.ZERO;
                _format = Global.EMPTY_STRING;   
                if (inputType.equals(Global.PROFILE)){
                    _format = formatPRES;
                }else{                    
                    for (int i = 0; i < NemoLists.getParameters().length; i++) {
                        if (NemoLists.getParameters()[i].getCode().equals(this.getCode())) {
                            _format = NemoLists.getParameters()[i].getFormat();
                            break;
                        }
                    }                   
                }
                _fullName = "DEPTH BELOW SEA SURFACE";       //$NON-NLS-1$
                _unit = "m";                             //$NON-NLS-1$
                cible = Conversion.ODV;
                break;
            case YEAR :
                _default = "9999";                  //$NON-NLS-1$
                _format = "%4d";                       //$NON-NLS-1$
                _fullName = "YEAR";                          //$NON-NLS-1$
                _unit = "yyyy";                          //$NON-NLS-1$
                cible = Conversion.MEDATLAS;
                break;
            case MNTH :
                _default = "99";                    //$NON-NLS-1$
                _format = "%02d";                       //$NON-NLS-1$
                _fullName = "MONTH";                         //$NON-NLS-1$
                _unit = "mm";                            //$NON-NLS-1$
                cible = Conversion.MEDATLAS;
                break;
            case DAYX :
                _default = "99";                    //$NON-NLS-1$
                _format = "%02d";                       //$NON-NLS-1$
                _fullName = "DAY WITHIN MONTH";              //$NON-NLS-1$
                _unit = "dd";                            //$NON-NLS-1$
                cible = Conversion.MEDATLAS;
                break;
            case TIME :
                _default = "999999";                //$NON-NLS-1$
                _format = "hh24mmss";                    //$NON-NLS-1$
                _fullName = "TIME WITHIN DAY";               //$NON-NLS-1$
                _unit = "hhmmss";                        //$NON-NLS-1$
                cible = Conversion.MEDATLAS;
                break;
            case CJDY :
                _default = Global.ZERO;
                _format = "#0.000000";                   //$NON-NLS-1$
                _fullName = "Chronological Julian Date";     //$NON-NLS-1$
                _unit = "days";                          //$NON-NLS-1$
                cible = Conversion.ODV;
                break;
            case DTUT :
                _default = Global.ZERO;
                _format = "yyyy-MM-dd'T'HH:mm:ss.SSS";      //$NON-NLS-1$
                _fullName = "time_ISO8601";                 //$NON-NLS-1$
                _unit = "yyyy-mm-ddThh:mm:ss.sss";          //$NON-NLS-1$
                cible = Conversion.ODV;
                break;
            case LATX :
                _default = "+99.999999";            //$NON-NLS-1$
                _format = "+DD.dddddd";                     //$NON-NLS-1$
                _fullName = "LATITUDE";                      //$NON-NLS-1$
                _unit = "decimal degree";                //$NON-NLS-1$
                cible = Conversion.MEDATLAS;
                break;
            case LONX :
                _default = "+999.999999";           //$NON-NLS-1$
                _format = "+DDD.dddddd";                     //$NON-NLS-1$
                _fullName = "LONGITUDE";                     //$NON-NLS-1$
                _unit = "decimal degree";                //$NON-NLS-1$
                cible = Conversion.MEDATLAS;
                break;
            case BATH :
                _default = "-999.9";                //$NON-NLS-1$
                _format = "%6.1f";                       //$NON-NLS-1$
                _fullName = "BATHYMETRIC DEPTH";             //$NON-NLS-1$
                _unit = "meter";                         //$NON-NLS-1$
                cible = Conversion.MEDATLAS;
                break;
        }
    }

    
    public PropertyModel getPropertyModel(StationModel stationModel){  
        PropertyModel propertyModel = null;
        String propertyCode;
        switch (this.idParam){
            case LATX :
                propertyCode = NemoSection.S_LAT;
                break;
            case LONX :
                propertyCode = NemoSection.S_LON;
                break;
            case BATH :
                propertyCode = NemoSection.S_DEPTH;
                break;
            default :
                propertyCode = null;
                break;
        }
        if (propertyCode != null){
            propertyModel = stationModel.propertiesAt(propertyCode);
        }
        return propertyModel;
    }
    
//    public void setStation(Station x, Conversion cible) {
//        if (_station == x)
//            return;
//        Station old = _station;
//        if (_station != null) {
//            _station = null;
//            old.removeParameter(this);
//        }
//        _station = x;
//        if (x != null) {
//            switch (cible){
//                case ODV :
//                    x.addOdvParameter(this);
//                    break;
//                case MEDATLAS :
//                    x.addMedParameter(this);
//                    break;
//            }
//            
//        }
//    }


    /**
     * Getter sur cible.
     * @return the cible
     */
    public Conversion getCible()
    {
        return cible;
    }


    /**
     * Setter sur cible.
     * @param cible the cible to set
     */
    public void setCible(Conversion cible)
    {
        this.cible = cible;
    }


    /**
     * Getter sur idParam.
     * @return the idParam
     */
    public IdParameter getIdParam()
    {
        return idParam;
    }


    /**
     * Setter sur idParam.
     * @param idParam the idParam to set
     */
    public void setIdParam(IdParameter idParam)
    {
        this.idParam = idParam;
    }
}
