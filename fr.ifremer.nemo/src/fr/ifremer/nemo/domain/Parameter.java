package fr.ifremer.nemo.domain;

import com.ago.xml.*;
import fr.ifremer.nemo.ws.WSParameter;
import java.io.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class Parameter
 *
 * Classe permettant la description d'un paramètre mesuré
 *
 * @author	Gerard BUNEL
 */
public  class Parameter
    implements
       Cloneable,
       PathElementHandler,
       SAXConsumer {

  /**
   * Code for parameter (MEDATLAS: 4 char max)
   */
  protected String  _code;

  /**
   * default val. (MEDATLAS: 5 char max)
   */
  protected String  _default;

  protected String  _format;

  /**
   * parameter full name (IOC def.) (MEDATLAS: 30 char max)
   */
  protected String  _fullName;

  /**
   * IS/IOC unit (MEDATLAS: 30 char max)
   */
  protected String  _unit;

  /**
   * Liste ordonnee des valeurs mesurees de ce parametre
   */
  protected java.util.List<ParameterValue> _values;

  /**
   * Station pour laquelle des mesures de ce parametre ont ete realisees.
   */
  protected Station _station;

  public Parameter() {
    _code = "";
    _default = "";
    _format = "";
    _fullName = "";
    _unit = "";
    _station = null;
    _values = new java.util.ArrayList<ParameterValue>();
  }

    /**
     *
     * @param	a_WSParameter 	
     */
  public Parameter ( WSParameter a_WSParameter) {
    this();
    _code = a_WSParameter.getCode();
    _fullName = a_WSParameter.getName();
    _unit = a_WSParameter.getUnit();
    _format = a_WSParameter.getFormat();
    _default = a_WSParameter.getDefaultValue();
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
    Parameter tmp = new Parameter();
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
    if ((tmp = a_Attributes.getValue("code")) != null) {
      setCode(tmp);
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
    if (a_LocalName.equals("fullName")) {
      Element2Field setter = new Element2Field( this, "setFullName");
      return setter;
    }
    if (a_LocalName.equals("unit")) {
      Element2Field setter = new Element2Field( this, "setUnit");
      return setter;
    }
    if (a_LocalName.equals("default")) {
      Element2Field setter = new Element2Field( this, "setDefault");
      return setter;
    }
    if (a_LocalName.equals("format")) {
      Element2Field setter = new Element2Field( this, "setFormat");
      return setter;
    }
    if (a_LocalName.equals("values")) {
      return new PathElementConsumer( "values", this);
    }
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
   * The XML Element is a CDATA filled element
   *
   * @param  path  the association path name
   * @param  a_URI  XML Namespace (SAX 2)
   * @param  a_LocalName  Tag name
   * @param  a_QName  Qualified tag name
   * @param  a_Attributes XML Element attributes
   * @return  SAXConsumer
   *
   */
  public SAXConsumer handlePathElement ( String path, String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) throws SAXException {
    SAXConsumer c = null;
    if (path.equals("values")) {
      c =  ParameterValue.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
      addValue( (ParameterValue)c );
      return c;
    }
    return null;
  }

  /**
   * 
   * Generate XML defintion on a Stream
   *
   * @param  a_Writer  XMLOutputWriter to which to write the XML Definition
   *
   */
  public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
    a_Writer.putAttribute( "code", getCode());
    a_Writer.putTag( "fullName", getFullName());
    a_Writer.putTag( "unit", getUnit());
    a_Writer.putTag( "default", getDefault());
    a_Writer.putTag( "format", getFormat());
    a_Writer.openTag( "values");
    for (ParameterValue t : getValues()) {
      t.writeXMLOn(a_Writer);
    }
    a_Writer.closeTag();
  }

  /**
   * 
   * Generate XML defintion on a XMLOutputWriter
   *
   * @param  a_Writer  the XMLOutputWriter to which to write the XML Definition
   *
   */
  public void writeXMLOn(XMLOutputWriter a_Writer) {
    a_Writer.openTag("Parameter");
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
   * Returns a copy of this Object
   *
   * @return  a copy of this object
   */
  public Object clone() throws CloneNotSupportedException {
    Parameter copy = null;
    copy = (Parameter) super.clone();
    copy._values = new java.util.ArrayList<ParameterValue>();
    copy._station = null;
    return copy;
  }

  /**
   * Read accessor (getter) for attribute _code
   *
   * @return  String
   * @see Parameter#_code
   */
  public  String getCode() {
    return _code;
  }

  /**
   * Write accessor (setter) for attribute _code
   *
   * @param  x  new value for _code
   * @see Parameter#_code
   */
  public void setCode(String x) {
    _code = x;
  }

  /**
   * Read accessor (getter) for attribute _default
   *
   * @return  String
   * @see Parameter#_default
   */
  public  String getDefault() {
    return _default;
  }

  /**
   * Write accessor (setter) for attribute _default
   *
   * @param  x  new value for _default
   * @see Parameter#_default
   */
  public void setDefault(String x) {
    _default = x;
  }

  /**
   * Read accessor (getter) for attribute _format
   *
   * @return  String
   * @see Parameter#_format
   */
  public  String getFormat() {
    return _format;
  }

  /**
   * Write accessor (setter) for attribute _format
   *
   * @param  x  new value for _format
   * @see Parameter#_format
   */
  public void setFormat(String x) {
    _format = x;
  }

  /**
   * Read accessor (getter) for attribute _fullName
   *
   * @return  String
   * @see Parameter#_fullName
   */
  public  String getFullName() {
    return _fullName;
  }

  /**
   * Write accessor (setter) for attribute _fullName
   *
   * @param  x  new value for _fullName
   * @see Parameter#_fullName
   */
  public void setFullName(String x) {
    _fullName = x;
  }

  /**
   * Read accessor (getter) for attribute _unit
   *
   * @return  String
   * @see Parameter#_unit
   */
  public  String getUnit() {
    return _unit;
  }

  /**
   * Write accessor (setter) for attribute _unit
   *
   * @param  x  new value for _unit
   * @see Parameter#_unit
   */
  public void setUnit(String x) {
    _unit = x;
  }

  /**
   * release all links for role "values"
   */
  public void releaseValues() {
    for (ParameterValue t : getValues()) {
      removeValue(t);
    }
  }

  /**
   * remove link with x for role "values"
   *
   * @param  x  the linked object to unlink
   */
  public void removeValue (ParameterValue x) {
    if (!valuesIncludes(x)) return;
    nocheckRemoveValue(x);
    x.setDefinition(null);
  }

  /**
   * Add a link for role "values" 
   * @param  x  the object to link with
   */
  public void addValue(ParameterValue x) {
    if (valuesIncludes(x)) return;
    nocheckAddValue(x);
    x.setDefinition(this);
  }

  /**
   * Returns the collection of linked objects for role "values"
   *
   * @return  Array of linked objects
   */
  public ParameterValue[] getValues() {
    ParameterValue[] v = new ParameterValue[_values.size()];
    _values.toArray(v);
    return v;
  }

  /**
   * Returns linked object for role "values" at index i
   *
   * @param  i  the index of the linked object
   * @return  linked object at index i
   */
  public ParameterValue valuesAt(int i) {
    return (ParameterValue)_values.get(i);
  }

  /**
   * returns index of object x in "values"
   *
   * @param  x  the linked object
   */
  public int valuesIndexOf(ParameterValue x) {
    if (!valuesIncludes(x)) return -1;
      for (int i=0 ; i < valuesSize () ; i++) {
        if (valuesAt(i) == x) {
          return i;
        }
      }
      return -1;
  }

  /**
   * move x one index before in the list for role "values"
   *
   * @param  x  the object to move
   */
  public void valuesMoveUp(ParameterValue x) {
    int index;
    index = valuesIndexOf(x);
    if (index < 1) return;
    nocheckMoveValueAt( x, index-1);
  }

  /**
   * move x to the top of list for role "values"
   *
   * @param  x  the object to move
   */
  public void valuesMoveTop(ParameterValue x) {
    int index;
    index = valuesIndexOf(x);
    if (index < 1) return;
    nocheckMoveValueAt( x, 0);
  }

  /**
   * move x one index more in the list for role "values"
   *
   * @param  x  the object to move
   */
  public void valuesMoveDown(ParameterValue x) {
    int index;
    index = valuesIndexOf(x);
    if (index < 0 || index > valuesSize()-2) return;
    nocheckMoveValueAt( x, index+1);
  }

  /**
   * move x to the bottom of list for role "values"
   *
   * @param  x  the object to move
   */
  public void valuesMoveBottom(ParameterValue x) {
    int index;
    index = valuesIndexOf(x);
    if (index < 0 || index > valuesSize()-2) return;
    nocheckMoveValueAt( x, valuesSize()-1);
  }

  /**
   * move x one index more in the list for role "values"
   *
   * @param  x  the object to move
   */
  public void nocheckMoveValueAt(ParameterValue x, int a_Index) {
    int index;
    ParameterValue tmp;
    index = valuesIndexOf(x);
    tmp = _values.get(index);
    _values.remove(index);
    _values.add( a_Index,  tmp);
  }

  /**
   * remove x from the linked objects for role "values"
   *
   * DO NOT USE. use non privileged method instead
   * @param  x  the object to link with
   * @see  Parameter#removeValue(ParameterValue)
   */
  protected void nocheckRemoveValue(ParameterValue x) {
    _values.remove(x);
  }

  /**
   * adds x to the linked objects for role "values"
   *
   * DO NOT USE. use non privileged method instead
   * @param  x  the object to link with
   * @see    Parameter#addValue(ParameterValue)
   */
  protected void nocheckAddValue(ParameterValue x) {
    _values.add(x);
  }

  /**
   * returns the number of linked objects for role "values"
   *
   * @return  the number of linked objects
   */
  public int valuesSize() {
    return _values.size();
  }

  /**
   * returns true if x is linked to the receiver for role "values"
   *
   * @param x an instance of ParameterValue
   */
  public boolean valuesIncludes( ParameterValue x ) {
    return _values.contains(x);
  }

  /**
   * returns an Iterator to iterate over the elements of "values"
   *
   */
  public java.util.Iterator<ParameterValue> valuesIterator() {
    return _values.iterator();

  }

  /**
   * Remove any link for role station
   */
  public void releaseStation() {
    setStation( null );
  }

  /**
   * returns the linked object for role <% aRole name%>
   *
   * @return  the object linked
   */
   public Station getStation() {
    return _station;
  }

  /**
   * Create a new link with x for role station
   *
   * @param  x  the object to link with
   */
  public void setStation(Station x) {
    if (_station == x) return;
    Station old = _station;
    if (_station != null) {
      _station = null;
      old.removeParameter(this);
    }
    _station = x;
    if (x != null) {
      x.addParameter(this);
    }
  }

  /**
   *
   * @param  a_Value  Chaine de caracteres lue dans le fichier d'entree
   * @param  a_Formula  Formule de conversion de la valeur. Peut etre a null
   */
  public void addValue ( String a_Value, String a_Formula) {
    
  }

  /**
   *
   * @param  a_Value  Chaine de caracteres lue dans le fichier d'entree
   * @param  a_Formula  Formule de conversion de la valeur. Peut etre a null
   * @param  a_Date  
   */
  public void addValue ( String a_Value, String a_Formula, long a_Date) {
    
  }

  /**
   *
   * @return  String
   */
  public String toString ( ) {
    return _code;
  }

} /* End of class: Parameter */
