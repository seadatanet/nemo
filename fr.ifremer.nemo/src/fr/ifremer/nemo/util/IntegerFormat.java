package fr.ifremer.nemo.util;

import com.ago.xml.*;
import java.io.*;
import java.text.FieldPosition;
import java.text.ParsePosition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class IntegerFormat
 *
 *
 * @author	Gerard BUNEL
 */
public  class IntegerFormat extends ExternalizableFormat implements Cloneable {
  protected String  _formatString;

  public IntegerFormat() {
    super();
    _formatString = "";
  }

    /**
     *
     * @param	a_Format 	
     */
  public IntegerFormat ( String a_Format) {
    this();
    _formatString = a_Format;
  }

  /**
   * 
   * Build object from its XML defintion
   *
   * @param  a_URI  Element Namespace (SAX 2)
   * @param  a_LocalName  Tag name
   * @param  a_QName  qualified tag name
   * @param  a_Attributes   XML Element attributes
   * @return  SAXConsumer
   *
   */
  public static SAXConsumer fromElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) throws SAXException {
    IntegerFormat tmp = new IntegerFormat();
    tmp.buildFromElement( a_URI,  a_LocalName,  a_QName,  a_Attributes);
    return (SAXConsumer)tmp;
  }

  /**
   * 
   * Retrieve attributes values from their XML defintion
   *
   * @param  a_URI  Element Namespace (SAX 2)
   * @param  a_LocalName  Tag name
   * @param  a_QName  qualified tag name
   * @param  a_Attributes   XML Element attributes
   *
   */
  public void buildFromElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) throws SAXException {
    String tmp;
    if ((tmp = a_Attributes.getValue("formatString")) != null) {
      setFormatString(tmp);
    }
  }

  /**
   * 
   * A new XML Element as been found. Parse it.
   *
   * @param  a_URI  XML Namespace (SAX 2)
   * @param  a_LocalName  Tag name
   * @param  a_QName  Qualified tag name
   * @param  a_Attributes XML Element attributes
   * @return  SAXConsumer
   *
   */
  public SAXConsumer consumeElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) throws SAXException {
    return null;
  }


  /**
   * 
   * The XML Element is a CDATA filled element
   *
   * @param  a_Characters  The CDATA content
   *
   */
  public void consumeCharacters( String a_Characters) throws SAXException {
  }

  /**
   * 
   * Generate XML defintion on a Stream
   *
   * @param  a_Writer  XMLOutputWriter to which to write the XML Definition
   *
   */
  public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
    a_Writer.putAttribute( "formatString", getFormatString());
  }

  /**
   * 
   * Generate XML defintion on a XMLOutputWriter
   *
   * @param  a_Writer  the XMLOutputWriter to which to write the XML Definition
   *
   */
  public void writeXMLOn(XMLOutputWriter a_Writer) {
    a_Writer.openTag("IntegerFormat");
    writeXMLBodyOn(a_Writer);
    a_Writer.closeTag();
  }

  /**
   * 
   * Generate XML defintion on a Stream
   *
   * @param  a_Stream  the OutptStream to which to write the XML Definition
   *
   */
  public void writeXMLOn(OutputStream a_Stream) {
    XMLOutputWriter writer = new XMLOutputWriter(a_Stream);
    writeXMLOn(writer);
  }

  /**
   * Read accessor (getter) for attribute _formatString
   *
   * @return  String
   * @see IntegerFormat#_formatString
   */
  public  String getFormatString() {
    return _formatString;
  }

  /**
   * Write accessor (setter) for attribute _formatString
   *
   * @param  x  new value for _formatString
   * @see IntegerFormat#_formatString
   */
  public void setFormatString(String x) {
    _formatString = x;
  }

  /**
   *
   * @return  Object
   */
  public Object clone ( ) {
    return new IntegerFormat( new String(_formatString));
  }

  /**
   *
   * @param  obj  
   * @param  toAppendTo  
   * @param  pos  
   * @return  StringBuffer
   */
  public StringBuffer format ( Object obj, StringBuffer toAppendTo, FieldPosition pos) {
    toAppendTo.append(String.format( java.util.Locale.US, _formatString, obj));
    return toAppendTo;
  }

  /**
   *
   * @param  source  
   * @param  pos  
   * @return  Integer
   */
  public Integer parseObject ( String source, ParsePosition pos) {
    pos.setIndex(source.length());
    return Integer.valueOf(source);
  }

  /**
   *
   * @return  String
   */
  public String toString ( ) {
    return _formatString;
  }

} /* End of class: IntegerFormat */
