package fr.ifremer.nemo.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.Element2Field;
import com.ago.xml.RootSAXConsumer;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLBootstrapParser;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.config.FlagTable;
import fr.ifremer.nemo.ui.Utils;

/**
 * Class Configuration
 * 
 * 
 * @author Gerard BUNEL
 */
public class Configuration implements Cloneable, RootSAXConsumer {

	/**
	 * Adresse de contact IFREMER en cas de problème WebService
	 */
	protected String _contact;

	/**
	 * Répertoire de base des fichiers modèles
	 */
	protected String _modelsDirectory;
	
	/**
	 * Répertoire de base des fichier de sortie
	 */
	protected String _outputDirectory;
	
	protected String _cruisesDirectory;
	
	protected String _dataCenter;

	protected String _nautilusWebServiceURL;

	protected String _nemoWebServiceURL;
	
	protected String _idEdmoRespCdi;
	
	protected String _codeEdmoDonnees;
	
	protected String _idEdmoRespDonnees;
	
	protected String _idEdmoDistDonnees;
	
	protected String _cdiURL;
	
	protected String _cdiMethod;
	
	protected String _vocabURL;
	
	protected int _predicate;
	
	protected String _fichierMapping;
	
	protected String _arboMapping;
		
	protected HashMap<String, String> _vocabLists = new HashMap<String, String>();

	protected boolean _showRule;

	static protected Configuration _singleton = null;

	protected Locale _locale;
	
	protected String _defaultFlagTable;
	
	protected ArrayList<FlagTable> _flagTables;

	public Configuration() {
		_contact = "";
		_modelsDirectory = "";
		_cruisesDirectory = "";
		_dataCenter = "";
		_nautilusWebServiceURL = "";
		_nemoWebServiceURL = "";
		_outputDirectory = "";
		_idEdmoRespCdi = "";
		_codeEdmoDonnees = "";
		_idEdmoRespDonnees = "";
		_idEdmoDistDonnees = "";
		_cdiURL = "";
		_cdiMethod = "";
		_showRule = true;
		_locale = null;
		_vocabURL = "";
		_predicate = 255;
		_fichierMapping = "";
		_arboMapping = "";
		_defaultFlagTable = "";
		
		_vocabLists = new HashMap<String, String>();
		
		_flagTables = new ArrayList<FlagTable>();
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
		Configuration tmp = new Configuration();
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
		if ((tmp = a_Attributes.getValue("lang")) != null) {
			setLang(tmp);
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
		if (a_LocalName.equals("nemoWsURL")) {
			Element2Field setter = new Element2Field(this,
					"setNemoWebServiceURL");
			return setter;
		}
		if (a_LocalName.equals("nautilusWsURL")) {
			Element2Field setter = new Element2Field(this,
					"setNautilusWebServiceURL");
			return setter;
		}
		if (a_LocalName.equals("dataCenter")) {
			Element2Field setter = new Element2Field(this, "setDataCenter");
			return setter;
		}
		if (a_LocalName.equals("showRule")) {
			Element2Field setter = new Element2Field(this, "setShowRule");
			return setter;
		}
		if (a_LocalName.equals("contact")) {
			Element2Field setter = new Element2Field(this, "setContact");
			return setter;
		}
		if (a_LocalName.equals("modelsDirectory")) {
			Element2Field setter = new Element2Field(this, "setModelsDirectory");
			return setter;
		}
		if (a_LocalName.equals("cruisesDirectory")) {
			Element2Field setter = new Element2Field(this, "setCruisesDirectory");
			return setter;
		}
		if (a_LocalName.equals("outputDirectory")) {
			Element2Field setter = new Element2Field(this, "setOutputDirectory");
			return setter;
		}
		if (a_LocalName.equals("idEdmoRespCdi")) {
			Element2Field setter = new Element2Field(this, "setIdEdmoRespCdi");
			return setter;
		}
		if (a_LocalName.equals("codeEdmoDonnees")) {
			Element2Field setter = new Element2Field(this, "setCodeEdmoDonnees");
			return setter;
		}
		if (a_LocalName.equals("idEdmoRespDonnees")) {
			Element2Field setter = new Element2Field(this, "setIdEdmoRespDonnees");
			return setter;
		}
		if (a_LocalName.equals("idEdmoDistDonnees")) {
			Element2Field setter = new Element2Field(this, "setIdEdmoDistDonnees");
			return setter;
		}
		if (a_LocalName.equals("cdiURL")) {
			Element2Field setter = new Element2Field(this, "setCdiURL");
			return setter;
		}
		if (a_LocalName.equals("cdiMethod")) {
			Element2Field setter = new Element2Field(this, "setCdiMethod");
			return setter;
		}
		if (a_LocalName.equals("fichierMapping")) {
			Element2Field setter = new Element2Field(this, "setFichierMapping");
			return setter;
		}
		if (a_LocalName.equals("arboMapping")) {
			Element2Field setter = new Element2Field(this, "setArboMapping");
			return setter;
		}
		if (a_LocalName.equals("vocabURL")) {
			Element2Field setter = new Element2Field(this, "setVocabURL");
			return setter;
		}
		if (a_LocalName.equals("VocabListURL")) {
			String name = null;
			String value = null;
			if ((name = a_Attributes.getValue("name")) != null &&
					(value = a_Attributes.getValue("value")) != null) {
				_vocabLists.put(name, value);
			}
			return this;
		}
		if (a_LocalName.equals("defaultFlagTable")) {
			Element2Field setter = new Element2Field(this, "setDefaultFlagTable");
			return setter;
		}
		if (a_LocalName.equals("FlagTable")) {
			String code = null;
			String label = null;
			FlagTable flagTable = new FlagTable();
			
			if ((code = a_Attributes.getValue("code")) != null &&
					(label = a_Attributes.getValue("label")) != null) {
				flagTable.setCode(code);
				flagTable.setLibelle(label);
				_flagTables.add(flagTable);
			}
			return this;
		}
		if (a_LocalName.equals("Flag")) {
			String local = null;
			String sdn = null;
			Flag flag = new Flag();
			
			if ((local = a_Attributes.getValue("local")) != null &&
					(sdn = a_Attributes.getValue("SDN")) != null) {
				flag.setLocal(local);
				flag.setSdn(sdn);
				_flagTables.get(_flagTables.size() - 1).getFlags().add(flag);
			}
			return this;
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
		a_Writer.putAttribute("lang", getLang());
		a_Writer.putTag("dataCenter", getDataCenter());
		a_Writer.putTag("nemoWsURL", getNemoWebServiceURL());
		a_Writer.putTag("nautilusWsURL", getNautilusWebServiceURL());
		a_Writer.putTag("showRule", isShowRule());
		a_Writer.putTag("contact", getContact());
		a_Writer.putTag("modelsDirectory", getModelsDirectory());
		a_Writer.putTag("cruisesDirectory", getCruisesDirectory());
		a_Writer.putTag("outputDirectory", getOutputDirectory());
		a_Writer.putTag("idEdmoRespCdi", getIdEdmoRespCdi());
		a_Writer.putTag("codeEdmoDonnees", getCodeEdmoDonnees());
		a_Writer.putTag("idEdmoRespDonnees", getIdEdmoRespDonnees());
		a_Writer.putTag("idEdmoDistDonnees", getIdEdmoDistDonnees());
		a_Writer.putTag("cdiURL", getCdiURL());
		a_Writer.putTag("cdiMethod", getCdiMethod());
		a_Writer.putTag("fichierMapping", getFichierMapping());
		a_Writer.putTag("arboMapping", getArboMapping());
		a_Writer.putTag("vocabURL", getVocabURL());
		Iterator<String> keys = getVocabLists().keySet().iterator();
		while (keys.hasNext()) {
			String listName = keys.next();
			a_Writer.openTag("VocabListURL");
			a_Writer.putAttribute("name", listName);
			a_Writer.putAttribute("value", _vocabLists.get(listName));
			a_Writer.closeTag();
		}
		a_Writer.putTag("defaultFlagTable", getDefaultFlagTable());
		for (int i = 0; i < getFlagTables().size(); i++) {
			a_Writer.openTag("FlagTable");
			a_Writer.putAttribute("code", getFlagTables().get(i).getCode());
			a_Writer.putAttribute("label", getFlagTables().get(i).getLibelle());
			
			for (int j = 0; j < getFlagTables().get(i).getFlags().size(); j++) {
				a_Writer.openTag("Flag");
				a_Writer.putAttribute("local", getFlagTables().get(i).getFlags().get(j).getLocal());
				a_Writer.putAttribute("SDN", getFlagTables().get(i).getFlags().get(j).getSdn());
				a_Writer.closeTag();
			}
			
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
		a_Writer.openTag("NemoConfiguration");
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
		Configuration copy = null;
		copy = (Configuration) super.clone();
		copy._locale = null;
		return copy;
	}

	/**
	 * Read accessor (getter) for attribute _contact
	 * 
	 * @return String
	 * @see Configuration#_contact
	 */
	public String getContact() {
		return _contact;
	}

	/**
	 * Write accessor (setter) for attribute _contact
	 * 
	 * @param x
	 *            new value for _contact
	 * @see Configuration#_contact
	 */
	public void setContact(String x) {
		_contact = x;
	}

	/**
	 * Read accessor (getter) for attribute _modelsDirectory
	 * 
	 * @return String
	 * @see Configuration#_modelsDirectory
	 */
	public String getModelsDirectory() {
		return _modelsDirectory;
	}
	
	/**
	 * Write accessor (setter) for attribute _modelsDirectory
	 * 
	 * @param x
	 *            new value for _modelsDirectory
	 * @see Configuration#_modelsDirectory
	 */
	public void setModelsDirectory(String x) {
		_modelsDirectory = x;
	}
	
	/**
	 * Read accessor (getter) for attribute _cruisesDirectory
	 * 
	 * @return String
	 * @see Configuration#_cruisesDirectory
	 */
	public String getCruisesDirectory() {
		return _cruisesDirectory;
	}

	/**
	 * Write accessor (setter) for attribute _cruisesDirectory
	 * 
	 * @param x
	 *            new value for _cruisesDirectory
	 * @see Configuration#_cruisesDirectory
	 */
	public void setCruisesDirectory(String x) {
		_cruisesDirectory = x;
	}
	
	/**
	 * Read accessor (getter) for attribute _dataCenter
	 * 
	 * @return String
	 * @see Configuration#_dataCenter
	 */
	public String getDataCenter() {
		return _dataCenter;
	}

	/**
	 * Write accessor (setter) for attribute _dataCenter
	 * 
	 * @param x
	 *            new value for _dataCenter
	 * @see Configuration#_dataCenter
	 */
	public void setDataCenter(String x) {
		_dataCenter = x;
	}

	/**
	 * Read accessor (getter) for attribute _nautilusWebServiceURL
	 * 
	 * @return String
	 * @see Configuration#_nautilusWebServiceURL
	 */
	public String getNautilusWebServiceURL() {
		return _nautilusWebServiceURL;
	}

	/**
	 * Write accessor (setter) for attribute _nautilusWebServiceURL
	 * 
	 * @param x
	 *            new value for _nautilusWebServiceURL
	 * @see Configuration#_nautilusWebServiceURL
	 */
	public void setNautilusWebServiceURL(String x) {
		_nautilusWebServiceURL = x;
	}

	/**
	 * Read accessor (getter) for attribute _nemoWebServiceURL
	 * 
	 * @return String
	 * @see Configuration#_nemoWebServiceURL
	 */
	public String getNemoWebServiceURL() {
		return _nemoWebServiceURL;
	}

	/**
	 * Write accessor (setter) for attribute _nemoWebServiceURL
	 * 
	 * @param x
	 *            new value for _nemoWebServiceURL
	 * @see Configuration#_nemoWebServiceURL
	 */
	public void setNemoWebServiceURL(String x) {
		_nemoWebServiceURL = x;
	}

	/**
	 * Read accessor (getter) for attribute _outputDirectory
	 * 
	 * @return String
	 * @see Configuration#_outputDirectory
	 */
	public String getOutputDirectory() {
		return _outputDirectory;
	}

	/**
	 * Write accessor (setter) for attribute _outputDirectory
	 * 
	 * @param x
	 *            new value for _outputDirectory
	 * @see Configuration#_outputDirectory
	 */
	public void setOutputDirectory(String x) {
		_outputDirectory = x;
	}
	
	/**
	 * Read accessor (getter) for attribute _idEdmoRespCdi
	 * 
	 * @return String
	 * @see Configuration#_idEdmoRespCdi
	 */
	public String getIdEdmoRespCdi() {
		return _idEdmoRespCdi;
	}
	
	/**
	 * Write accessor (setter) for attribute _idEdmoRespCdi
	 * 
	 * @param x
	 *            new value for _idEdmoRespCdi
	 * @see Configuration#_idEdmoRespCdi
	 */
	public void setIdEdmoRespCdi(String x) {
		_idEdmoRespCdi = x;
	}
	
	/**
	 * Read accessor (getter) for attribute _codeEdmoDonnees
	 * 
	 * @return String
	 * @see Configuration#_codeEdmoDonnees
	 */
	public String getCodeEdmoDonnees() {
		return _codeEdmoDonnees;
	}
	
	/**
	 * Write accessor (setter) for attribute _codeEdmoDonnees
	 * 
	 * @param x
	 *            new value for _codeEdmoDonnees
	 * @see Configuration#_codeEdmoDonnees
	 */
	public void setCodeEdmoDonnees(String x) {
		_codeEdmoDonnees = x;
	}
	
	/**
	 * Read accessor (getter) for attribute _idEdmoRespDonnees
	 * 
	 * @return String
	 * @see Configuration#_idEdmoRespDonnees
	 */
	public String getIdEdmoRespDonnees() {
		return _idEdmoRespDonnees;
	}
	
	/**
	 * Write accessor (setter) for attribute _idEdmoRespDonnees
	 * 
	 * @param x
	 *            new value for _idEdmoRespDonnees
	 * @see Configuration#_idEdmoRespDonnees
	 */
	public void setIdEdmoRespDonnees(String x) {
		_idEdmoRespDonnees = x;
	}
	
	/**
	 * Read accessor (getter) for attribute _idEdmoDistDonnees
	 * 
	 * @return String
	 * @see Configuration#_idEdmoDistDonnees
	 */
	public String getIdEdmoDistDonnees() {
		return _idEdmoDistDonnees;
	}
	
	/**
	 * Write accessor (setter) for attribute _idEdmoDistDonnees
	 * 
	 * @param x
	 *            new value for _idEdmoDistDonnees
	 * @see Configuration#_idEdmoDistDonnees
	 */
	public void setIdEdmoDistDonnees(String x) {
		_idEdmoDistDonnees = x;
	}
	
	/**
	 * Read accessor (getter) for attribute _cdiURL
	 * 
	 * @return String
	 * @see Configuration#_cdiURL
	 */
	public String getCdiURL() {
		return _cdiURL;
	}
	
	/**
	 * Write accessor (setter) for attribute _cdiURL
	 * 
	 * @param x
	 *            new value for _cdiURL
	 * @see Configuration#_cdiURL
	 */
	public void setCdiURL(String x) {
		_cdiURL = x;
	}
	
	/**
	 * Read accessor (getter) for attribute _cdiMethod
	 * 
	 * @return String
	 * @see Configuration#_cdiMethod
	 */
	public String getCdiMethod() {
		return _cdiMethod;
	}
	
	/**
	 * Write accessor (setter) for attribute _cdiMethod
	 * 
	 * @param x
	 *            new value for _cdiMethod
	 * @see Configuration#_cdiMethod
	 */
	public void setCdiMethod(String x) {
		_cdiMethod = x;
	}
	
	/**
	 * Read accessor (getter) for attribute _showRule
	 * 
	 * @return boolean
	 * @see Configuration#_showRule
	 */
	public boolean isShowRule() {
		return _showRule;
	}

	/**
	 * Write accessor (setter) for attribute _showRule
	 * 
	 * @param x
	 *            new value for _showRule
	 * @see Configuration#_showRule
	 */
	public void setShowRule(boolean x) {
		_showRule = x;
	}

	/**
	 * Write accessor (setter) for attribute _showRule
	 * 
	 * @param a_String
	 *            new value for _showRule as String
	 * @see Configuration#_showRule
	 */
	public void setShowRule(String a_String) {
		setShowRule(new Boolean(a_String).booleanValue());
	}

	/**
	 * Remove any link for role singleton
	 */
	public static void releaseSingleton() {
		setSingleton(null);
	}

	/**
	 * Create a new link with x for role singleton
	 * 
	 * @param x
	 *            the object to link with
	 */
	public static void setSingleton(Configuration x) {
		if (_singleton == x)
			return;
		_singleton = x;
	}

	/**
	 * Remove any link for role locale
	 */
	public void releaseLocale() {
		setLocale(null);
	}

	/**
	 * Create a new link with x for role locale
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setLocale(Locale x) {
		if (_locale == x)
			return;
		_locale = x;
	}

	/**
	 * 
	 * @return String
	 */
	public String getLang() {
		return getLocale().toString();
	}

	/**
	 * 
	 * @return Locale
	 */
	public Locale getLocale() {
		if (_locale == null) {
			return Locale.getDefault();
		} else {
			return _locale;
		}
	}

	/**
	 * 
	 * @return Configuration
	 */
	public static Configuration getSingleton() {
		if (_singleton == null) {
			_singleton = new Configuration();
			XMLBootstrapParser parser = new XMLBootstrapParser(_singleton);
			try {
				parser.parse(new FileInputStream(
						"resources/NemoConfiguration.xml"), false);
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}
			// parser.parse(
			// Configuration.class.getResourceAsStream("resources/NemoConfiguration.xml"),
			// false);

		}
		return _singleton;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		getSingleton().save();
	}

	/**
	 * 
	 */
	public void save() {
		FileOutputStream stream = null;
		try {
			// FileOutputStream fich2 = new FileOutputStream(new
			// File("D:/NemoConfiguration.xml"));

			// creation nouveau dossier puis creation fichier config
			File fich2 = new java.io.File("resources/NemoConfiguration.xml");
			// URL url =
			// Configuration.class.getResource("/NemoConfiguration.xml");
			// URL url =
			// Configuration.class.getResource("/resources/NemoConfiguration.xml");
			// String file = url.getFile();
			stream = new FileOutputStream(fich2.toString());
			// stream = new FileOutputStream(file);
			writeXMLOn(stream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	/**
	 * 
	 * @param a_Lang
	 */
	public void setLang(String a_Lang) {
		StringTokenizer tokenizer = new StringTokenizer(a_Lang, "_");
		String[] tokens = new String[3];
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			tokens[i++] = tokenizer.nextToken();
		}
		if (tokens[2] != null) {
			setLocale(new Locale(tokens[0], tokens[1], tokens[2])); // language,
																	// country,
																	// variant
		} else if (tokens[1] != null) {
			setLocale(new Locale(tokens[0], tokens[1])); // language, country
		} else {
			setLocale(new Locale(tokens[0])); // language
		}
	}

	/**
	 * Read accessor (getter) for attribute _vocabURL
	 * 
	 * @return String
	 * @see Configuration#_vocabURL
	 */
	public String getVocabURL() {
		return _vocabURL;
	}

	/**
	 * Write accessor (setter) for attribute _vocabURL
	 * 
	 * @param x  new value for _vocabURL
	 * @see Configuration#_vocabURL
	 */
	public void setVocabURL(String x) {
		_vocabURL = x;
	}

	public HashMap<String, String> getVocabLists() {
		return _vocabLists;
	}

	public void setVocabLists(HashMap<String, String> lists) {
		_vocabLists = lists;
	}

	public String getVocabListURL(String listName) {
		return _vocabLists.get(listName);
	}

	public void setVocabListURL(String listName) {
		String VocabUrl = Utils.configuration.getVocabURL();
		String ListUrl = VocabUrl.substring(0, VocabUrl.indexOf('/',7) + 1) + "list/" + listName + "/current";
		
		_vocabLists.put(listName, ListUrl);
	}

	public int getPredicate() {
		return _predicate;
	}

	public String getArboMapping() {
		return _arboMapping;
	}

	public void setArboMapping(String mapping) {
		_arboMapping = mapping;
	}

	public String getFichierMapping() {
		return _fichierMapping;
	}
	
	public String getFichierMappingTxt() {
		return _fichierMapping + ".txt";
	}

	public void setFichierMapping(String mapping) {
		_fichierMapping = mapping;
	}

//	public void setPredicate(String _predicate) {
//		this._predicate = _predicate;
//	}
	
	public String getSep() {
		return "\t";
	}

	public ArrayList<FlagTable> getFlagTables() {
		return _flagTables;
	}

	public void setFlagTables(ArrayList<FlagTable> tables) {
		_flagTables = tables;
	}

	public String getDefaultFlagTable() {
		return _defaultFlagTable;
	}

	public void setDefaultFlagTable(String flagTable) {
		_defaultFlagTable = flagTable;
	}
	
} /* End of class: Configuration */
