package fr.ifremer.nemo.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLBootstrapParser;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.AbstractModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

/**
 * Class Configuration
 * 
 * @author Gerard BUNEL
 */
public class Configuration extends AbstractModel {
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
	
	protected String _idEdmoRespCsr;

	protected String _codeEdmoDonnees;

	protected String _idEdmoRespDonnees;

	protected String _idEdmoDistDonnees;

	protected String _cdiURL;

	protected String _odvVersion;

	protected String _netCDFVersion;

	protected String _medatlasVersion;
	
	protected String _bodvVersion;
	
	protected String _modvVersion;
	
	protected String _sodvVersion;

	protected String _cdiMethod;

	protected String _typePlateForme;

	protected String _accesDonnees;

	protected String _edmoURL;

	protected int _predicate;

	protected String _fichierMapping;

	protected boolean _showRule;

	protected Locale _locale;

	protected String _defaultCode;

	// FAE 4431 Flag qualité par défault configurable
	protected String _defaultFlag;

	protected ArrayList<FlagTable> _flagTables;

	protected Proxy proxy;

	protected boolean dateISO;

	/**
	 * format d'export par défaut choisi dans les settings
	 */
	// choix du format d'export pour conversion par défaut
	private E_TypeConversion _defaultConversionType;

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
	 * only for jUnit tests (to test the NemoConfiguration_ini.xml file
	 * 
	 * @param init
	 */
	public Configuration(boolean init) {
		_tagName = ConfigDescription.node_root;
		initValues();

		XMLBootstrapParser parser = new XMLBootstrapParser(this);
		try {
			parser.parse(new FileInputStream(Intern.dir_resources +
					File.separator +
					ConfigDescription.node_root +
					"_ini" +
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
	public void initValues() {
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
		_odvVersion = Intern.defaultODVFormatVersion;
		_medatlasVersion = Intern.defaultMedatlasFormatVersion;
		_netCDFVersion = Intern.defaultNetCDFFormatVersion;
		_bodvVersion = Intern.defaultBodvFormatVersion;
		_modvVersion = Intern.defaultModvFormatVersion;
		_sodvVersion = Intern.defaultSodvFormatVersion;
		_cdiMethod = Global.EMPTY_STRING;
		_typePlateForme = Global.EMPTY_STRING;
		_accesDonnees = Global.EMPTY_STRING;
		_showRule = true;
		_locale = null;
		_edmoURL = Global.EMPTY_STRING;
		_predicate = 255;
		_fichierMapping = Global.EMPTY_STRING;
		_defaultCode = Global.EMPTY_STRING;
		_defaultConversionType = E_TypeConversion.UNKNOWN;

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
			Attributes a_Attributes) {
		Configuration el = Nemo.getInstance().getConfig();
		el.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		return el;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildFromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) {
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
			String a_QName, Attributes a_Attributes) {
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
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_DataCenter);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_showRule)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_ShowRule);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_contact)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_Contact);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_modelsDirectory)) {
			Element2Field setter =
					new Element2Field(this,
							ConfigDescription.setter_ModelsDirectory);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_cruisesDirectory)) {
			Element2Field setter =
					new Element2Field(this,
							ConfigDescription.setter_CruisesDirectory);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_outputDirectory)) {
			Element2Field setter =
					new Element2Field(this,
							ConfigDescription.setter_OutputDirectory);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_writeCdi)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_WriteCdi);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_idEdmoRespCdi)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_IdEdmoRespCdi);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_idEdmoRespCsr)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_IdEdmoRespCsr);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_codeEdmoDonnees)) {
			Element2Field setter =
					new Element2Field(this,
							ConfigDescription.setter_CodeEdmoDonnees);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_idEdmoRespDonnees)) {
			Element2Field setter =
					new Element2Field(this,
							ConfigDescription.setter_IdEdmoRespDonnees);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_idEdmoDistDonnees)) {
			Element2Field setter =
					new Element2Field(this,
							ConfigDescription.setter_IdEdmoDistDonnees);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_cdiURL)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_CdiURL);
			return setter;
		}

		if (a_LocalName.equals(ConfigDescription.node_odvVersion)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_OdvVersion);
			return setter;
		}

		if (a_LocalName.equals(ConfigDescription.node_medatlasVersion)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_MedatlasVersion);
			return setter;
		}

		if (a_LocalName.equals(ConfigDescription.node_netCDFVersion)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_NetCDFVersion);
			return setter;
		}
		
		if (a_LocalName.equals(ConfigDescription.node_bodvVersion)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_BodvVersion);
			return setter;
		}
		
		if (a_LocalName.equals(ConfigDescription.node_modvVersion)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_ModvVersion);
			return setter;
		}
		
		if (a_LocalName.equals(ConfigDescription.node_sodvVersion)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_SodvVersion);
			return setter;
		}

		if (a_LocalName.equals(ConfigDescription.node_cdiMethod)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_CdiMethod);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_accesDonnees)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_AccesDonnees);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_typePlateforme)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_TypePlateForme);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_writeMapping)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_WriteMapping);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_fichierMapping)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_FichierMapping);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_dateISO)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_DateISO);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_proxy)) {
			// return new PathElementConsumer(ConfigDescription.node_proxy,
			// this);
			// set proxy values
			getProxy().setUse(a_Attributes.getValue(Intern.proxy_use));
			getProxy().setAdress(a_Attributes.getValue(Intern.proxy_adr));
			getProxy().setPort(a_Attributes.getValue(Intern.proxy_port));
			getProxy().setLogin(a_Attributes.getValue(Intern.proxy_login));
			getProxy().setPassword(a_Attributes.getValue(Intern.proxy_pass));
			// only to avoid return null
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_Proxy);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_edmoURL)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_EdmoURL);
			return setter;
		}

		if (a_LocalName.equals(ConfigDescription.node_defaultFlagTable)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_DefaultCode);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_defaultConversionType)) {
			Element2Field setter =
					new Element2Field(this,
							ConfigDescription.setter_defaultConversionType);
			return setter;
		}
		if (a_LocalName.equals(ConfigDescription.node_FlagTable)) {
			String code = null;
			String label = null;
			FlagTable flagTable = new FlagTable();

			if ((code = a_Attributes.getValue(ConfigDescription.a_code)) != null
					&&
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

			if ((local = a_Attributes.getValue(ConfigDescription.a_local)) != null
					&&
					(sdn = a_Attributes.getValue(ConfigDescription.a_SDN)) != null) {
				flag.setLocal(local);
				flag.setSdn(sdn);
				_flagTables.get(_flagTables.size() - 1).getFlags().add(flag);
			}
			return this;
		}
		if (a_LocalName.equals(ConfigDescription.node_FlagDefault)) {
			Element2Field setter =
					new Element2Field(this, ConfigDescription.setter_FlagDefault);
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
			Attributes a_Attributes) {
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
		a_Writer.putTag(ConfigDescription.node_nemoWsURL,
				getNemoWebServiceURL());
		a_Writer.putTag(ConfigDescription.node_nautilusWsURL,
				getNautilusWebServiceURL());
		a_Writer.putTag(ConfigDescription.node_showRule, isShowRule());
		a_Writer.putTag(ConfigDescription.node_contact, getContact());
		a_Writer.putTag(ConfigDescription.node_modelsDirectory,
				getModelsDirectory());
		a_Writer.putTag(ConfigDescription.node_cruisesDirectory,
				getCruisesDirectory());
		a_Writer.putTag(ConfigDescription.node_outputDirectory,
				getOutputDirectory());
		a_Writer.putTag(ConfigDescription.node_writeCdi, isWriteCdi());
		a_Writer.putTag(ConfigDescription.node_idEdmoRespCdi,getIdEdmoRespCdi());
		a_Writer.putTag(ConfigDescription.node_idEdmoRespCsr,getIdEdmoRespCsr());
		a_Writer.putTag(ConfigDescription.node_codeEdmoDonnees,	getCodeEdmoDonnees());
		a_Writer.putTag(ConfigDescription.node_idEdmoRespDonnees,
				getIdEdmoRespDonnees());
		a_Writer.putTag(ConfigDescription.node_idEdmoDistDonnees,
				getIdEdmoDistDonnees());
		a_Writer.putTag(ConfigDescription.node_cdiURL, getCdiURL());
		a_Writer.putTag(ConfigDescription.node_odvVersion, getODVFormatVersion());
		a_Writer.putTag(ConfigDescription.node_medatlasVersion, getMedatlasVersion());
		a_Writer.putTag(ConfigDescription.node_netCDFVersion, getNetCDFVersion());
		a_Writer.putTag(ConfigDescription.node_bodvVersion, getBodvVersion());
		a_Writer.putTag(ConfigDescription.node_modvVersion, getModvVersion());
		a_Writer.putTag(ConfigDescription.node_sodvVersion, getSodvVersion());

		a_Writer.putTag(ConfigDescription.node_cdiMethod, getCdiMethod());
		a_Writer.putTag(ConfigDescription.node_accesDonnees, getAccesDonnees());
		a_Writer.putTag(ConfigDescription.node_typePlateforme,
				getTypePlateForme());
		a_Writer.putTag(ConfigDescription.node_writeMapping, isWriteMapping());
		a_Writer.putTag(ConfigDescription.node_fichierMapping,
				getFichierMapping());
		a_Writer.putTag(ConfigDescription.node_dateISO, isDateISO());
		a_Writer.putTag(ConfigDescription.node_defaultConversionType,
				_defaultConversionType.toString());
		// a_Writer.putTag("arboMapping", getArboMapping());

		// proxy:
		a_Writer.openTag(ConfigDescription.node_proxy);
		a_Writer.putAttribute(Intern.proxy_use, getProxy().isUse());
		a_Writer.putAttribute(Intern.proxy_adr, getProxy().address);
		a_Writer.putAttribute(Intern.proxy_port, getProxy().port);
		a_Writer.putAttribute(Intern.proxy_login, getProxy().login);
		a_Writer.putAttribute(Intern.proxy_pass, getProxy().password);
		a_Writer.closeTag();

		a_Writer.putTag(ConfigDescription.node_edmoURL, getEdmoURL());

		a_Writer.putTag(ConfigDescription.node_defaultFlagTable,
				getDefaultCode());
		for (int i = 0; i < getFlagTables().size(); i++) {
			a_Writer.openTag(ConfigDescription.node_FlagTable);
			a_Writer.putAttribute(ConfigDescription.a_code, getFlagTables()
					.get(i).getCode());
			a_Writer.putAttribute(ConfigDescription.a_label, getFlagTables()
					.get(i).getLibelle());

			for (int j = 0; j < getFlagTables().get(i).getFlags().size(); j++) {
				a_Writer.openTag(ConfigDescription.node_Flag);
				a_Writer.putAttribute(ConfigDescription.a_local,
						getFlagTables().get(i).getFlags().get(j).getLocal());
				a_Writer.putAttribute(ConfigDescription.a_SDN, getFlagTables()
						.get(i).getFlags().get(j).getSdn());
				a_Writer.closeTag();
			}

			a_Writer.closeTag();
		}
		a_Writer.putTag(ConfigDescription.node_FlagDefault, getDefaultFlag());
	}

	/**
	 * 
	 */
	public void restoreDefault() {
		// need to reinit flag tables, not to keep old ones:
		_flagTables = new ArrayList<>();
		
		XMLBootstrapParser parser = new XMLBootstrapParser(this);
		try {
			parser.parse(new FileInputStream(Intern.dir_resources +
					File.separator +
					Intern.fileName_configIni +
					Global.EXT_XML),
					false);
		} catch (FileNotFoundException e) {
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
			String fich2Path = Intern.dir_resources +
					File.separator +
					ConfigDescription.node_root +
					Global.EXT_XML;
			
			
			File fich2 = new java.io.File(fich2Path);
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
	 * @param a_Lang
	 */
	public void setLang(String a_Lang) {
		StringTokenizer tokenizer =
				new StringTokenizer(a_Lang, Global.UNDERSCORE);
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
	public void release() {
		_flagTables = null;
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
	 * Write accessor (setter) for attribute _netCDFVersion
	 * 
	 * @param x
	 *            new value for _netCDFVersion
	 * @see Configuration#_netCDFVersion
	 */
	public void setNetCDFVersion(String x) {
		_netCDFVersion = x;
	}

	/**
	 * Write accessor (setter) for attribute _medatlasVersion
	 * 
	 * @param x
	 *            new value for _medatlasVersion
	 * @see Configuration#_medatlasVersion
	 */
	public void setMedatlasVersion(String x) {
		_medatlasVersion = x;
	}

	/**
	 * Write accessor (setter) for attribute _odvVersion
	 * 
	 * @param x
	 *            new value for _odvVersion
	 * @see Configuration#_odvVersion
	 */
	public void setOdvVersion(String x) {
		_odvVersion = x;
	}
	
	/**
	 * Write accessor (setter) for attribute _bodvVersion
	 * 
	 * @param x
	 *            new value for _bodvVersion
	 * @see Configuration#_bodvVersion
	 */
	public void setBodvVersion(String x) {
		_bodvVersion = x;
	}
	
	/**
	 * Write accessor (setter) for attribute _modvVersion
	 * 
	 * @param x
	 *            new value for _modvVersion
	 * @see Configuration#_modvVersion
	 */
	public void setModvVersion(String x) {
		_modvVersion = x;
	}
	
	/**
	 * Write accessor (setter) for attribute _sodvVersion
	 * 
	 * @param x
	 *            new value for _sodvVersion
	 * @see Configuration#_sodvVersion
	 */
	public void setSodvVersion(String x) {
		_sodvVersion = x;
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
	 * @return String
	 */
	public String getLang() {
		return getLocale().toString();
	}

	/**
	 * @return Locale
	 */
	public Locale getLocale() {
		if (_locale == null) {
			return Locale.getDefault();
		}
		return _locale;
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

	public void setDefaultConversionType(String defaultConversionType) {
		_defaultConversionType =
				E_TypeConversion.valueOf(defaultConversionType);
	}

	public FlagTable getDefaultFlagTable() {
		FlagTable flagTable =
				FlagTable.getFlagTable(getFlagTables(), getDefaultCode());

		if (flagTable != null) {
			return flagTable;
		}
		return Utils.getBlankFlagTable();
	}

	public String getAccesDonnees() {
		return _accesDonnees;
	}

	public void setAccesDonnees(String accesDonnees) {
		_accesDonnees = accesDonnees;
	}

	public String getTypePlateForme() {
		return _typePlateForme;
	}

	public void setTypePlateForme(String typePlateForme) {
		_typePlateForme = typePlateForme;
	}

	public boolean isWriteCdi() {
		return _writeCdi;
	}

	public void setWriteCdi(boolean writeCdi) {
		_writeCdi = writeCdi;
	}

	public void setWriteCdi(String writeCdi) {
		_writeCdi = Boolean.parseBoolean(writeCdi);
	}

	/**
	 * Getter sur _writeMapping.
	 * 
	 * @return the writeMapping
	 */
	public boolean isWriteMapping() {
		return _writeMapping;
	}

	/**
	 * Setter sur _writeMapping.
	 * 
	 * @param writeMapping
	 *            the writeMapping to set
	 */
	public void setWriteMapping(boolean writeMapping) {
		_writeMapping = writeMapping;
	}

	/**
	 * Setter sur _writeMapping.
	 * 
	 * @param writeMapping
	 *            the writeMapping to set
	 */
	public void setWriteMapping(String writeMapping) {
		_writeMapping = Boolean.parseBoolean(writeMapping);
	}

	/**
	 * @return the proxy
	 */
	public Proxy getProxy() {
		return proxy;
	}

	/**
	 * @param proxy
	 *            the proxy to set
	 */
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	/**
	 * Getter sur _edmoURL.
	 * 
	 * @return the edmoURL
	 */
	public String getEdmoURL() {
		return _edmoURL;
	}

	/**
	 * Setter sur _edmoURL.
	 * 
	 * @param edmoURL
	 *            the edmoURL to set
	 */
	public void setEdmoURL(String edmoURL) {
		_edmoURL = edmoURL;
	}

	/**
	 * Getter sur dateISO.
	 * 
	 * @return the dateISO
	 */
	public boolean isDateISO() {
		return dateISO;
	}

	/**
	 * Setter sur dateISO.
	 * 
	 * @param dateISO
	 *            the dateISO to set
	 */
	public void setDateISO(boolean dateISO) {
		this.dateISO = dateISO;
	}

	/**
	 * Setter sur dateISO.
	 * 
	 * @param dateISO
	 *            the dateISO to set
	 */
	public void setDateISO(String dateISO) {
		this.dateISO = Boolean.parseBoolean(dateISO);
	}

	public E_TypeConversion getDefaultConversionType() {
		return _defaultConversionType;
	}

	public void setDefaultConversionType(E_TypeConversion defaultConversionType) {
		_defaultConversionType = defaultConversionType;
	}

	public String getODVFormatVersion() {
		return _odvVersion;
	}

	public String getNetCDFVersion() {
		return _netCDFVersion;
	}

	public String getMedatlasVersion() {
		return _medatlasVersion;
	}

	public String getBodvVersion() {
		return _bodvVersion;
	}
	
	public String getModvVersion() {
		return _modvVersion;
	}
	
	public String getSodvVersion() {
		return _sodvVersion;
	}
	public String checkConfig() {
		boolean blnOK = true;
		String message="";

		if (Nemo.getInstance().getConfig().getIdEdmoRespCdi()==null ||
				Nemo.getInstance().getConfig().getIdEdmoRespCdi().trim().equals(Global.EMPTY_STRING)) {
			message += Global.RN + Global.SEP + Messages.settings_EDMOauthor;
			blnOK = false;
		}
// IdEdmoRespCsr is optional
		//		if (Nemo.getInstance().getConfig().getIdEdmoRespCsr()==null ||
//				Nemo.getInstance().getConfig().getIdEdmoRespCsr().trim().equals(Global.EMPTY_STRING)) {
//			message += Global.RN + Global.SEP + Messages.settings_EDMOCSRauthor;
//			blnOK = false;
//		}

		if (Nemo.getInstance().getConfig().isWriteCdi()) {
			if (Nemo.getInstance().getConfig().getCodeEdmoDonnees().trim()
					.equals(Global.EMPTY_STRING)) {
				message += Global.RN + Global.SEP + Messages.settings_EDMOori;
				blnOK = false;
			}

			if (Nemo.getInstance().getConfig().getIdEdmoRespDonnees().trim()
					.equals(Global.EMPTY_STRING)) {
				message +=
						Global.RN + Global.SEP + Messages.settings_EDMOcustodian;
				blnOK = false;
			}

			if (Nemo.getInstance().getConfig().getIdEdmoDistDonnees().trim()
					.equals(Global.EMPTY_STRING)) {
				message += Global.RN + Global.SEP + Messages.settings_EDMOdist;
				blnOK = false;
			}

			if (Nemo.getInstance().getConfig().getCdiURL().trim().equals(
					Global.EMPTY_STRING)) {
				message +=
						Global.RN + Global.SEP + Messages.settings_dataDist_web;
				blnOK = false;
			}

			if (Nemo.getInstance().getConfig().getCdiMethod().trim().equals(
					Global.EMPTY_STRING)) {
				message +=
						Global.RN + Global.SEP + Messages.settings_dataDist_met;
				blnOK = false;
			}
		}

		if (!blnOK){
			message=  Messages.CDIResumeWriter_configMessage + message;
		}else{
			return message;
		}
		return message;
	}

	public String getIdEdmoRespCsr() {
		return _idEdmoRespCsr;
	}

	public void setIdEdmoRespCsr(String x) {
		_idEdmoRespCsr = x;
	}

}