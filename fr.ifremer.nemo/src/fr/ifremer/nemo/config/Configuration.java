package fr.ifremer.nemo.config;

import com.ago.xml.*;
import fr.ifremer.nemo.config.Configuration;
import java.io.*;
import java.net.URL;
import java.util.Locale;
import java.util.StringTokenizer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class Configuration
 *
 *
 * @author	Gerard BUNEL
 */
public  class Configuration
    implements
       Cloneable,
       RootSAXConsumer {

  /**
   * Adresse de contact IFREMER en cas de problème WebService
   */
  protected String  _contact;

  /**
   * Répertoire de base des fichiers modèles
   */
  protected String  _modelsDirectory;

  protected String  _nautilusWebServiceURL;

  protected String  _nemoWebServiceURL;

  /**
   * Répertoire de base des fichier de sortie
   */
  protected String  _outputDirectory;

  protected boolean  _showRule;

  static protected Configuration _singleton = null;

  protected Locale _locale;

  public Configuration() {
    _contact = "";
    _modelsDirectory = "";
    _nautilusWebServiceURL = "";
    _nemoWebServiceURL = "";
    _outputDirectory = "";
    _showRule = true;
    _locale = null;
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
    Configuration tmp = new Configuration();
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
    if ((tmp = a_Attributes.getValue("lang")) != null) {
      setLang(tmp);
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
    if (a_LocalName.equals("nemoWsURL")) {
      Element2Field setter = new Element2Field( this, "setNemoWebServiceURL");
      return setter;
    }
    if (a_LocalName.equals("nautilusWsURL")) {
      Element2Field setter = new Element2Field( this, "setNautilusWebServiceURL");
      return setter;
    }
    if (a_LocalName.equals("showRule")) {
      Element2Field setter = new Element2Field( this, "setShowRule");
      return setter;
    }
    if (a_LocalName.equals("contact")) {
      Element2Field setter = new Element2Field( this, "setContact");
      return setter;
    }
    if (a_LocalName.equals("modelsDirectory")) {
      Element2Field setter = new Element2Field( this, "setModelsDirectory");
      return setter;
    }
    if (a_LocalName.equals("outputDirectory")) {
      Element2Field setter = new Element2Field( this, "setOutputDirectory");
      return setter;
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
   * Generate XML defintion on a Stream
   *
   * @param  a_Writer  XMLOutputWriter to which to write the XML Definition
   *
   */
  public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
    a_Writer.putAttribute( "lang", getLang());
    a_Writer.putTag( "nemoWsURL", getNemoWebServiceURL());
    a_Writer.putTag( "nautilusWsURL", getNautilusWebServiceURL());
    a_Writer.putTag( "showRule", isShowRule());
    a_Writer.putTag( "contact", getContact());
    a_Writer.putTag( "modelsDirectory", getModelsDirectory());
    a_Writer.putTag( "outputDirectory", getOutputDirectory());
  }

  /**
   * 
   * Generate XML defintion on a XMLOutputWriter
   *
   * @param  a_Writer  the XMLOutputWriter to which to write the XML Definition
   *
   */
  public void writeXMLOn(XMLOutputWriter a_Writer) {
    a_Writer.openTag("NemoConfiguration");
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
    Configuration copy = null;
    copy = (Configuration) super.clone();
    copy._locale = null;
    return copy;
  }

  /**
   * Read accessor (getter) for attribute _contact
   *
   * @return  String
   * @see Configuration#_contact
   */
  public  String getContact() {
    return _contact;
  }

  /**
   * Write accessor (setter) for attribute _contact
   *
   * @param  x  new value for _contact
   * @see Configuration#_contact
   */
  public void setContact(String x) {
    _contact = x;
  }

  /**
   * Read accessor (getter) for attribute _modelsDirectory
   *
   * @return  String
   * @see Configuration#_modelsDirectory
   */
  public  String getModelsDirectory() {
    return _modelsDirectory;
  }

  /**
   * Write accessor (setter) for attribute _modelsDirectory
   *
   * @param  x  new value for _modelsDirectory
   * @see Configuration#_modelsDirectory
   */
  public void setModelsDirectory(String x) {
    _modelsDirectory = x;
  }

  /**
   * Read accessor (getter) for attribute _nautilusWebServiceURL
   *
   * @return  String
   * @see Configuration#_nautilusWebServiceURL
   */
  public  String getNautilusWebServiceURL() {
    return _nautilusWebServiceURL;
  }

  /**
   * Write accessor (setter) for attribute _nautilusWebServiceURL
   *
   * @param  x  new value for _nautilusWebServiceURL
   * @see Configuration#_nautilusWebServiceURL
   */
  public void setNautilusWebServiceURL(String x) {
    _nautilusWebServiceURL = x;
  }

  /**
   * Read accessor (getter) for attribute _nemoWebServiceURL
   *
   * @return  String
   * @see Configuration#_nemoWebServiceURL
   */
  public  String getNemoWebServiceURL() {
    return _nemoWebServiceURL;
  }

  /**
   * Write accessor (setter) for attribute _nemoWebServiceURL
   *
   * @param  x  new value for _nemoWebServiceURL
   * @see Configuration#_nemoWebServiceURL
   */
  public void setNemoWebServiceURL(String x) {
    _nemoWebServiceURL = x;
  }

  /**
   * Read accessor (getter) for attribute _outputDirectory
   *
   * @return  String
   * @see Configuration#_outputDirectory
   */
  public  String getOutputDirectory() {
    return _outputDirectory;
  }

  /**
   * Write accessor (setter) for attribute _outputDirectory
   *
   * @param  x  new value for _outputDirectory
   * @see Configuration#_outputDirectory
   */
  public void setOutputDirectory(String x) {
    _outputDirectory = x;
  }

  /**
   * Read accessor (getter) for attribute _showRule
   *
   * @return  boolean
   * @see Configuration#_showRule
   */
  public boolean isShowRule() {
    return _showRule;
  }

  /**
   * Write accessor (setter) for attribute _showRule
   *
   * @param  x  new value for _showRule
   * @see Configuration#_showRule
   */
  public void setShowRule(boolean x) {
    _showRule = x;
  }

  /**
   * Write accessor (setter) for attribute _showRule
   *
   * @param  a_String  new value for _showRule as String
   * @see Configuration#_showRule
   */
  public void setShowRule( String a_String) {
    setShowRule(new Boolean(a_String).booleanValue());
  }

  /**
   * Remove any link for role singleton
   */
  public static void releaseSingleton() {
    setSingleton( null );
  }

  /**
   * Create a new link with x for role singleton
   *
   * @param  x  the object to link with
   */
  public static void setSingleton(Configuration x) {
    if (_singleton == x) return;
    _singleton = x;
  }

  /**
   * Remove any link for role locale
   */
  public void releaseLocale() {
    setLocale( null );
  }

  /**
   * Create a new link with x for role locale
   *
   * @param  x  the object to link with
   */
  public void setLocale(Locale x) {
    if (_locale == x) return;
    _locale = x;
  }

  /**
   *
   * @return  String
   */
  public String getLang ( ) {
    return getLocale().toString();
  }

  /**
   *
   * @return  Locale
   */
  public Locale getLocale ( ) {
    if (_locale == null) {
    	return Locale.getDefault();
    } else {
    	return _locale;
    }
  }

  /**
   *
   * @return  Configuration
   */
  public static Configuration getSingleton ( ) {
    if (_singleton == null) {
    	_singleton = new Configuration();
    	XMLBootstrapParser parser = new XMLBootstrapParser(_singleton);
    	parser.parse( Configuration.class.getResourceAsStream("/resources/NemoConfiguration.xml"), false);
    }
    return _singleton;
  }

  /**
   *
   * @param  args  
   */
  public static void main ( String[] args) {
    getSingleton().save();
  }

  /**
   *
   */
  public void save ( ) {
    FileOutputStream stream = null;
    try {
    	URL url = Configuration.class.getResource("/resources/NemoConfiguration.xml");
    	String file = url.getFile();
    	stream = new FileOutputStream(file);
    	writeXMLOn(stream);
    } catch (Exception e) {
    } finally {
    	if (stream != null) {
    		try { stream.close(); } catch (Exception ex) {}
    	}
    }
  }

  /**
   *
   * @param  a_Lang  
   */
  public void setLang ( String a_Lang) {
    StringTokenizer tokenizer = new StringTokenizer( a_Lang, "_");
    String[] tokens = new String[3];
    int i = 0;
    while (tokenizer.hasMoreTokens()) {
    	tokens[i++] = tokenizer.nextToken();
    }
    if (tokens[2] != null ) {
    	setLocale( new Locale( tokens[0], tokens[1], tokens[2]));  // language, country, variant
    } else if (tokens[1] != null) {
    	setLocale( new Locale( tokens[0], tokens[1]));  // language, country
    } else {
    	setLocale( new Locale( tokens[0]));  // language
    }
  }

} /* End of class: Configuration */
