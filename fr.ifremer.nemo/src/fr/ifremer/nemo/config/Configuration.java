package fr.ifremer.nemo.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLBootstrapParser;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.input.AbstractModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;

/**
 * Class Configuration
 * 
 * 
 * @author Gerard BUNEL
 */
public class Configuration extends AbstractModel
{
	/**
	 * Adresse de contact IFREMER en cas de probleme WebService
	 */
	protected String _contact;

	/**
	 * Repertoire de base des fichiers modeles
	 */
	protected String _modelsDirectory;
	
	/**
	 * Repertoire de base des fichier de sortie
	 */
	protected String _outputDirectory;
	
	protected String _cruisesDirectory;
	
	protected String _dataCenter;

	protected String _nautilusWebServiceURL;

	protected String _nemoWebServiceURL;
	
    protected boolean _writeCdi;
    
    protected boolean _writeMapping;
    
	protected String _idEdmoRespCdi;
	
	protected String _codeEdmoDonnees;
	
	protected String _idEdmoRespDonnees;
	
	protected String _idEdmoDistDonnees;
	
	protected String _cdiURL;
	
	protected String _cdiMethod;
    
    protected String _typePlateForme;

    protected String _accesDonnees;
	
	protected String _vocabURL;
    
    protected String _edmoURL;
	
	protected int _predicate;
	
	protected String _fichierMapping;
		
	protected HashMap<String, BODCList> _vocabLists = new HashMap<String, BODCList>();

	protected boolean _showRule;

	protected Locale _locale;
	
	protected String _defaultCode;
    
    //FAE 4431 Flag qualité par défault configurable
    protected String _defaultFlag;
	
	protected ArrayList<FlagTable> _flagTables;
    
    protected Proxy proxy;
    
    protected boolean dateISO;

    public enum DataListEnum {P011_VIA_P091, P091, P011_VIA_P021};
    
    private DataListEnum _dataList;
    
	public Configuration() {
        _tagName = ConfigDescription.node_root;
        initValues();

        XMLBootstrapParser parser = new XMLBootstrapParser(this);
        try {
            parser.parse(new FileInputStream(Intern.dir_resources + 
                                             File.separator + 
                                             ConfigDescription.node_root + 
                                             Global.EXT_XML), 
                         false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initValues(){        
        _contact = Global.EMPTY_STRING;
        _modelsDirectory = Global.EMPTY_STRING;
        _cruisesDirectory = Global.EMPTY_STRING;
        _dataCenter = Global.EMPTY_STRING;
        _nautilusWebServiceURL = Global.EMPTY_STRING;
        _nemoWebServiceURL = Global.EMPTY_STRING;
        _outputDirectory = Global.EMPTY_STRING;
        _writeCdi = false;
        _writeMapping = false;
        _idEdmoRespCdi = Global.EMPTY_STRING;
        _codeEdmoDonnees = Global.EMPTY_STRING;
        _idEdmoRespDonnees = Global.EMPTY_STRING;
        _idEdmoDistDonnees = Global.EMPTY_STRING;
        _cdiURL = Global.EMPTY_STRING;
        _cdiMethod = Global.EMPTY_STRING;
        _typePlateForme = Global.EMPTY_STRING;
        _accesDonnees = Global.EMPTY_STRING;
        _showRule = true;
        _locale = null;
        _vocabURL = Global.EMPTY_STRING;
        _edmoURL = Global.EMPTY_STRING;
        _predicate = 255;
        _fichierMapping = Global.EMPTY_STRING;
        _defaultCode = Global.EMPTY_STRING;
        _dataList = DataListEnum.P091;
        
        _vocabLists = new HashMap<String, BODCList>();
        
        _flagTables = new ArrayList<FlagTable>();
        proxy = new Proxy();
        
        dateISO = true;
    }


    /**
     * @see AbstractModel.java#fromElement
     */
	public static SAXConsumer fromElement(String a_URI, 
                                          String a_LocalName,
                                          String a_QName, 
                                          Attributes a_Attributes){
		Configuration el = new Configuration();
        el.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		return el;
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void buildFromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes){
		String tmp;
		if ((tmp = a_Attributes.getValue(ConfigDescription.a_lang)) != null) {  
			setLang(tmp);
		}
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
	public SAXConsumer consumeElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes){
		if (a_LocalName.equals(ConfigDescription.node_nemoWsURL)) {
			Element2Field setter = new Element2Field(this,
					ConfigDescription.setter_NemoWsURL);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_nautilusWsURL)) {
			Element2Field setter = new Element2Field(this,
					ConfigDescription.setter_NautilusWsURL); 
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_dataCenter)) {
			Element2Field setter = new Element2Field(this, ConfigDescription.setter_DataCenter);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_showRule)) {
			Element2Field setter = new Element2Field(this, ConfigDescription.setter_ShowRule);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_contact)) {
			Element2Field setter = new Element2Field(this, ConfigDescription.setter_Contact);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_modelsDirectory)) {
			Element2Field setter = new Element2Field(this, ConfigDescription.setter_ModelsDirectory);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_cruisesDirectory)) {
			Element2Field setter = new Element2Field(this, ConfigDescription.setter_CruisesDirectory);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_outputDirectory)) {
			Element2Field setter = new Element2Field(this, ConfigDescription.setter_OutputDirectory);
			return setter;
		}
        if (a_LocalName.equals(ConfigDescription.node_writeCdi)) {
            Element2Field setter = new Element2Field(this, ConfigDescription.setter_WriteCdi);
            return setter;
        }
		if (a_LocalName.equals(ConfigDescription.node_idEdmoRespCdi)) {
			Element2Field setter = new Element2Field(this, ConfigDescription.setter_IdEdmoRespCdi);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_codeEdmoDonnees)) {
			Element2Field setter = new Element2Field(this, ConfigDescription.setter_CodeEdmoDonnees);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_idEdmoRespDonnees)) {
			Element2Field setter = new Element2Field(this, ConfigDescription.setter_IdEdmoRespDonnees);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_idEdmoDistDonnees)) {
			Element2Field setter = new Element2Field(this, ConfigDescription.setter_IdEdmoDistDonnees);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_cdiURL)) {
			Element2Field setter = new Element2Field(this, ConfigDescription.setter_CdiURL);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_cdiMethod)) {
			Element2Field setter = new Element2Field(this, ConfigDescription.setter_CdiMethod);
			return setter;
		}
        if (a_LocalName.equals(ConfigDescription.node_accesDonnees)) {
            Element2Field setter = new Element2Field(this, ConfigDescription.setter_AccesDonnees);
            return setter;
        }
        if (a_LocalName.equals(ConfigDescription.node_typePlateforme)) {
            Element2Field setter = new Element2Field(this, ConfigDescription.setter_TypePlateForme);
            return setter;
        }      
        if (a_LocalName.equals(ConfigDescription.node_writeMapping)) {
            Element2Field setter = new Element2Field(this, ConfigDescription.setter_WriteMapping);
            return setter;
        }  
		if (a_LocalName.equals(ConfigDescription.node_fichierMapping)) {
			Element2Field setter = new Element2Field(this, ConfigDescription.setter_FichierMapping);
			return setter;
		}  
        if (a_LocalName.equals(ConfigDescription.node_dateISO)) {
            Element2Field setter = new Element2Field(this, ConfigDescription.setter_DateISO);
            return setter;
        }
        if (a_LocalName.equals(ConfigDescription.node_proxy)) { 
            //return new PathElementConsumer(ConfigDescription.node_proxy, this);
            // set proxy values
            getProxy().setUse(a_Attributes.getValue(Intern.proxy_use));
            getProxy().setAdress(a_Attributes.getValue(Intern.proxy_adr));
            getProxy().setPort(a_Attributes.getValue(Intern.proxy_port));
            getProxy().setLogin(a_Attributes.getValue(Intern.proxy_login));
            getProxy().setPassword(a_Attributes.getValue(Intern.proxy_pass));
            // only to avoid return null
            Element2Field setter = new Element2Field(this, ConfigDescription.setter_Proxy);
            return setter;
        }
        if (a_LocalName.equals(ConfigDescription.node_edmoURL)) {
            Element2Field setter = new Element2Field(this, ConfigDescription.setter_EdmoURL); 
            return setter;
        }
		if (a_LocalName.equals(ConfigDescription.node_vocabURL)) {
			Element2Field setter = new Element2Field(this, ConfigDescription.setter_VocabURL);  
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_VocabListURL)) {
			String name = null;
			String value = null;
            String version = null;
			if ((name = a_Attributes.getValue(ConfigDescription.a_name)) != null   
					&& (value = a_Attributes.getValue(ConfigDescription.a_value)) != null  
                    && (version = a_Attributes.getValue(ConfigDescription.a_version)) != null) {  
                BODCList list = new BODCList(name, version, value);
                _vocabLists.put(name, list);
			}
			return this;
		}
		if (a_LocalName.equals(ConfigDescription.node_defaultFlagTable)) {
			Element2Field setter = new Element2Field(this, ConfigDescription.setter_DefaultCode);  
			return setter;
		}
        if (a_LocalName.equals("dataList")) {
            Element2Field setter = new Element2Field(this, "setDataList");  
            return setter;
        }
        
		if (a_LocalName.equals(ConfigDescription.node_FlagTable)) {
			String code = null;
			String label = null;
			FlagTable flagTable = new FlagTable();
			
			if ((code = a_Attributes.getValue(ConfigDescription.a_code)) != null &&  
					(label = a_Attributes.getValue(ConfigDescription.a_label)) != null) {  
				flagTable.setCode(code);
				flagTable.setLibelle(label);
				_flagTables.add(flagTable);
			}
			return this;
		}
		if (a_LocalName.equals(ConfigDescription.node_Flag)) {
			String local = null;
			String sdn = null;
			Flag flag = new Flag();
			
			if ((local = a_Attributes.getValue(ConfigDescription.a_local)) != null &&  
					(sdn = a_Attributes.getValue(ConfigDescription.a_SDN)) != null) {  
				flag.setLocal(local);
				flag.setSdn(sdn);
				_flagTables.get(_flagTables.size() - 1).getFlags().add(flag);
			}
			return this;
		}
        if (a_LocalName.equals(ConfigDescription.node_FlagDefault)) {
            Element2Field setter = new Element2Field(this, ConfigDescription.setter_FlagDefault);  
            return setter;
        }
		
		return null;
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SAXConsumer handlePathElement(String path, 
                                         String a_URI,
                                         String a_LocalName, 
                                         String a_QName, 
                                         Attributes a_Attributes){
        SAXConsumer el = null;
        if (path.equals(ConfigDescription.node_proxy)) {  
            el = Proxy.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
            setProxy((Proxy) el);
            return el;
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		a_Writer.putAttribute(ConfigDescription.a_lang, getLang());  
		a_Writer.putTag(ConfigDescription.node_dataCenter, getDataCenter());
		a_Writer.putTag(ConfigDescription.node_nemoWsURL, getNemoWebServiceURL());
		a_Writer.putTag(ConfigDescription.node_nautilusWsURL, getNautilusWebServiceURL());
		a_Writer.putTag(ConfigDescription.node_showRule, isShowRule());
		a_Writer.putTag(ConfigDescription.node_contact, getContact());
		a_Writer.putTag(ConfigDescription.node_modelsDirectory, getModelsDirectory());
		a_Writer.putTag(ConfigDescription.node_cruisesDirectory, getCruisesDirectory());
		a_Writer.putTag(ConfigDescription.node_outputDirectory, getOutputDirectory());
        a_Writer.putTag(ConfigDescription.node_writeCdi, isWriteCdi());
		a_Writer.putTag(ConfigDescription.node_idEdmoRespCdi, getIdEdmoRespCdi());
		a_Writer.putTag(ConfigDescription.node_codeEdmoDonnees, getCodeEdmoDonnees());
		a_Writer.putTag(ConfigDescription.node_idEdmoRespDonnees, getIdEdmoRespDonnees());
		a_Writer.putTag(ConfigDescription.node_idEdmoDistDonnees, getIdEdmoDistDonnees());
		a_Writer.putTag(ConfigDescription.node_cdiURL, getCdiURL());
		a_Writer.putTag(ConfigDescription.node_cdiMethod, getCdiMethod());
        a_Writer.putTag(ConfigDescription.node_accesDonnees, getAccesDonnees());
        a_Writer.putTag(ConfigDescription.node_typePlateforme, getTypePlateForme());
        a_Writer.putTag(ConfigDescription.node_writeMapping, isWriteMapping());
		a_Writer.putTag(ConfigDescription.node_fichierMapping, getFichierMapping());
        a_Writer.putTag(ConfigDescription.node_dateISO, isDateISO());
        a_Writer.putTag("dataList", _dataList.toString());
		//a_Writer.putTag("arboMapping", getArboMapping());
        
        //proxy:
        a_Writer.openTag(ConfigDescription.node_proxy);
        a_Writer.putAttribute(Intern.proxy_use, getProxy().isUse());  
        a_Writer.putAttribute(Intern.proxy_adr, getProxy().address);  
        a_Writer.putAttribute(Intern.proxy_port, getProxy().port);  
        a_Writer.putAttribute(Intern.proxy_login, getProxy().login);  
        a_Writer.putAttribute(Intern.proxy_pass, getProxy().password);  
        a_Writer.closeTag();
        
        
        
        a_Writer.putTag(ConfigDescription.node_edmoURL, getEdmoURL());
		a_Writer.putTag(ConfigDescription.node_vocabURL, getVocabURL());
		Iterator<String> keys = getVocabLists().keySet().iterator();
		while (keys.hasNext()) {
			String listName = keys.next();
            BODCList list = _vocabLists.get(listName);
			a_Writer.openTag(ConfigDescription.node_VocabListURL);
			a_Writer.putAttribute(ConfigDescription.a_name, listName);  
			a_Writer.putAttribute(ConfigDescription.a_value, list.getUrl());  
            a_Writer.putAttribute(ConfigDescription.a_version, list.getLocalVersion());  
			a_Writer.closeTag();
		}
		a_Writer.putTag(ConfigDescription.node_defaultFlagTable, getDefaultCode());
		for (int i = 0; i < getFlagTables().size(); i++) {
			a_Writer.openTag(ConfigDescription.node_FlagTable);
			a_Writer.putAttribute(ConfigDescription.a_code, getFlagTables().get(i).getCode());  
			a_Writer.putAttribute(ConfigDescription.a_label, getFlagTables().get(i).getLibelle());  
			
			for (int j = 0; j < getFlagTables().get(i).getFlags().size(); j++) {
				a_Writer.openTag(ConfigDescription.node_Flag);
				a_Writer.putAttribute(ConfigDescription.a_local, getFlagTables().get(i).getFlags().get(j).getLocal());  
				a_Writer.putAttribute(ConfigDescription.a_SDN, getFlagTables().get(i).getFlags().get(j).getSdn());  
				a_Writer.closeTag();
			}
			
			a_Writer.closeTag();
		}
        a_Writer.putTag(ConfigDescription.node_FlagDefault ,getDefaultFlag());
	}
    

    /**
     * 
     */
    public void restoreDefault(){
        // sauvegarde des listes BODC
        HashMap<String, BODCList> bkVocabLists = new HashMap<String, BODCList>();
        bkVocabLists.putAll(_vocabLists);
        
        XMLBootstrapParser parser = new XMLBootstrapParser(this);
        try {
            parser.parse(new FileInputStream(Intern.dir_resources +  
                                             File.separator + 
                                             Intern.fileName_configIni +  
                                             Global.EXT_XML), 
                         false);

            // récupération des listes BODC
            _vocabLists.clear();
            _vocabLists.putAll(bkVocabLists);
            setVocabLists(_vocabLists);
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    public void save() {
        FileOutputStream stream = null;
        try {
            // creation nouveau dossier puis creation fichier config
            File fich2 = new java.io.File(Intern.dir_resources +  
                                           File.separator + 
                                           ConfigDescription.node_root + 
                                           Global.EXT_XML); 
            stream = new FileOutputStream(fich2.toString());
            writeXMLOn(stream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 
     * @param a_Lang
     */
    public void setLang(String a_Lang) {
        StringTokenizer tokenizer = new StringTokenizer(a_Lang, Global.UNDERSCORE);
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
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
		Configuration copy = null;
		copy = (Configuration) super.clone();
		copy._locale = null;
		return copy;
	}
    


    /**
     * {@inheritDoc}
     */
    @Override
    public void release(){
        _flagTables = null;
        _vocabLists = null;
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
		}
		return _locale;
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

	public HashMap<String, BODCList> getVocabLists() {
		return _vocabLists;
	}

    public BODCList getVocabList(String listName) {
        return _vocabLists.get(listName);
    }

	public void setVocabLists(HashMap<String, BODCList> lists) {
		_vocabLists = lists;
	}

	public int getPredicate() {
		return _predicate;
	}

	public String getFichierMapping() {
		if (_fichierMapping.trim().equals(Global.EMPTY_STRING)) {
			return (Intern.file_mapping + Global.EXT_TXT);  
		}
		return _fichierMapping;
	}
	
	public String getFichierMappingTxt() {
		return _fichierMapping + Global.EXT_TXT;
	}

	public void setFichierMapping(String mapping) {
		_fichierMapping = mapping;
	}

	public ArrayList<FlagTable> getFlagTables() {
		return _flagTables;
	}

	public void setFlagTables(ArrayList<FlagTable> tables) {
		_flagTables = tables;
	}

	public String getDefaultCode() {
		return _defaultCode;
	}

	public void setDefaultCode(String flagTable) {
		_defaultCode = flagTable;
	}
    
    public String getDefaultFlag() {
        return _defaultFlag;
    }

    public void setDefaultFlag(String defaultFlag) {
        _defaultFlag = defaultFlag;
    }

    public DataListEnum getDataList() {
        return _dataList;
    }

    public void setDataList(String flagTable) {
        _dataList = DataListEnum.valueOf(flagTable);
    }
    
	public FlagTable getDefaultFlagTable() {
		FlagTable flagTable = FlagTable.getFlagTable(getFlagTables(), getDefaultCode());
		
		if (flagTable != null) {
			return flagTable;
		}
		return Utils.getBlankFlagTable();
	}

    public String getAccesDonnees()
    {
        return _accesDonnees;
    }

    public void setAccesDonnees(String accesDonnees)
    {
        _accesDonnees = accesDonnees;
    }

    public String getTypePlateForme()
    {
        return _typePlateForme;
    }

    public void setTypePlateForme(String typePlateForme)
    {
        _typePlateForme = typePlateForme;
    }

    public boolean isWriteCdi()
    {
        return _writeCdi;
    }

    public void setWriteCdi(boolean writeCdi)
    {
        _writeCdi = writeCdi;
    }
    
    public void setWriteCdi(String writeCdi)
    {
        _writeCdi = Boolean.parseBoolean(writeCdi);
    }

    /**
     * Getter sur _writeMapping.
     * @return the writeMapping
     */
    public boolean isWriteMapping()
    {
        return _writeMapping;
    }

    /**
     * Setter sur _writeMapping.
     * @param writeMapping the writeMapping to set
     */
    public void setWriteMapping(boolean writeMapping)
    {
        _writeMapping = writeMapping;
    }

    /**
     * Setter sur _writeMapping.
     * @param writeMapping the writeMapping to set
     */
    public void setWriteMapping(String writeMapping)
    {
        _writeMapping = Boolean.parseBoolean(writeMapping);
    }

    /**
     * @return the proxy
     */
    public Proxy getProxy() {
        return proxy;
    }

    /**
     * @param proxy the proxy to set
     */
    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Getter sur _edmoURL.
     * @return the edmoURL
     */
    public String getEdmoURL()
    {
        return _edmoURL;
    }

    /**
     * Setter sur _edmoURL.
     * @param edmoURL the edmoURL to set
     */
    public void setEdmoURL(String edmoURL)
    {
        _edmoURL = edmoURL;
    }

    /**
     * Getter sur dateISO.
     * @return the dateISO
     */
    public boolean isDateISO()
    {
        return dateISO;
    }

    /**
     * Setter sur dateISO.
     * @param dateISO the dateISO to set
     */
    public void setDateISO(boolean dateISO)
    {
        this.dateISO = dateISO;
    }

    /**
     * Setter sur dateISO.
     * @param dateISO the dateISO to set
     */
    public void setDateISO(String dateISO)
    {
        this.dateISO = Boolean.parseBoolean(dateISO);
    }
	
}