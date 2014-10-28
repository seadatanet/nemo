package fr.ifremer.nemo.input;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.rpc.ServiceException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import sdn.vocabulary.interfaces.ICollection;
import sdn.vocabulary.interfaces.IElement;

import com.ago.xml.Element2Field;
import com.ago.xml.PathElementConsumer;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLBootstrapParser;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nautilus.metadata.NautilusSurveyWS;
import fr.ifremer.nautilus.metadata.NautilusSurveyWSServiceLocator;
import fr.ifremer.nautilus.metadata.WSCollectedData;
import fr.ifremer.nautilus.metadata.WSSurvey;
import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.VocabLists;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_Availability;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.sismer_tools.seadatanet.SDNXLinkException;
import fr.ifremer.sismer_tools.seadatanet.SdnXLinkReference;

/**
 * Class CruiseModel.java
 * Description: Informations descrivant l'onglet Cuise
 * @author Delphine BOULANGER
 */
public class CruiseModel extends AbstractModel {
	private static String CSR_DATE_FORMAT = "yyyy-MM-dd"; //$NON-NLS-1$
	public static String MED_DATE_FORMAT = "dd/MM/yyyy"; //$NON-NLS-1$

	/**
	 * xml actuellement autorise pour initialisation xml
	 */
	public static enum XmlVersion {ISO_19139, ISO_19115};

	/**
	 * Code d'accessibilité des données (MEDATLAS: 1 char = P/L/C)
	 */
	protected E_Availability _availability;

	/**
	 * Nom du chef scientifique (MEDATLAS: 40 char max)
	 */
	protected String _chiefScientist;

	/**
	 * Commentaires sur la campagne (multi-lines)
	 */
	protected String _comment;

	/**
	 * Corresponding URL for Cruise SDN reference 
	 */
	protected String _correspondingURL;
	// default: auto compute the CSR
	protected Boolean _correspondingURLAutoCheck = false;

	/**
	 * Code pays du laboratoire source (MEDATLAS: 2 char max)
	 */
	protected String _countryCode;

	/**
	 * Nom du pays du laboratoire source
	 */
	protected String _countryName;

	/**
	 * Code du centre d'archive régional (MEDATLAS: 2 char max)
	 */
	protected String _dataCentre;

	/**
	 * Date de fin de la campagne (MEDATLAS: DD/MM/YYYY)
	 */
	protected long _endDate;

	/**
	 * Laboratoire, institution, ville (MEDATLAS: 75 char max)
	 */
	protected String _laboratories;

	/**
	 * Nom / référence de l'organisateur de la campagne (MEDATLAS: 32 char max)
	 */
	protected String _name;

	/**
	 * Nom du projet (MEDATLAS: 28 char max)
	 */
	protected String _projectName;

	/**
	 * Référence campagne (MEDATLAS: 13 char max)
	 */
	protected String _reference;

	/**
	 * Nom de la région [table GF3] (MEDATLAS: 35 char max)
	 */
	protected String _regionName;

	/**
	 * Code de la région [table C19]
	 */
	protected String _regionCode;

	/**
	 * Code du navire - standardisé WDCA/ICES/IOC (MEDATLAS: 4 char max)
	 */
	protected String _shipCode;

	/**
	 * Nom complet du navire (MEDATLAS: 25 char max)
	 */
	protected String _shipName;

	/**
	 * Date de début de campagne (MEDATLAS: DD/MM/YYYY)
	 */
	protected long _startDate;


	/**
	 * version xml du fichier charger depuis xml initialisation
	 */
	private XmlVersion xmlVersion;

	/**
	 * Liste , ordonnee, des types de donnees
	 */
	protected List<DataType> _dataTypes;

	public CruiseModel() {
		_tagName = ModelDescription.node_cruiseModel;
		initValues();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initValues() {
		_availability = E_Availability.publicly;
		_chiefScientist = Intern.unknown;
		_comment = Global.EMPTY_STRING;
		_correspondingURL = Global.EMPTY_STRING;
		_correspondingURLAutoCheck = false;
		_countryCode = Global.EMPTY_STRING;
		_countryName = null;
		_dataCentre = Global.EMPTY_STRING;
		_endDate = System.currentTimeMillis();
		_laboratories = Global.EMPTY_STRING;
		_name = Global.EMPTY_STRING;
		_projectName = Global.EMPTY_STRING;
		_reference = Global.EMPTY_STRING;
		_regionName = Global.EMPTY_STRING;
		_regionCode = Global.EMPTY_STRING;
		_shipCode = "XXXX"; //$NON-NLS-1$
		_shipName = Intern.unknown;
		_startDate = System.currentTimeMillis();
		_dataTypes = new ArrayList<DataType>();
	}

	/**
	 * @see AbstractModel.java#fromElement
	 */
	public static SAXConsumer fromElement(String a_URI,
			String a_LocalName,
			String a_QName,
			Attributes a_Attributes) {
		CruiseModel el = new CruiseModel();
		el.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		return el;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildFromElement(String a_URI,
			String a_LocalName,
			String a_QName,
			Attributes a_Attributes) {
		// l'élément n'a pas d'attributs
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SAXConsumer consumeElement(String a_URI,
			String a_LocalName,
			String a_QName,
			Attributes a_Attributes) {

		if (a_LocalName.equals(ModelDescription.node_cref)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_cref);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_cname)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_cname);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_shipcode)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_shipcode);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_shipname)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_shipname);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_startdate)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_startdate);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_formattedStartDate)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_formattedStartDate);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_enddate)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_enddate);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_formattedEndDate)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_formattedEndDate);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_region)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_region);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_country)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_country);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_countryName)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_countryName);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_laboratories)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_laboratories);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_chief)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_chief);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_project)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_project);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_datacenter)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_datacenter);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_availability)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_availability);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_comment)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_comment);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_correspondingURL)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_correspondingURL);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_correspondingURLAutoCheck)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_correspondingURLAutoCheck);
			return setter;
		}

		if (a_LocalName.equals(ModelDescription.node_listDatatypes)) {
			return new PathElementConsumer(ModelDescription.node_listDatatypes, this);
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
		if (path.equals(ModelDescription.node_listDatatypes)) {
			el = DataType.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			addDataType((DataType) el);
			return el;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		a_Writer.putTag(ModelDescription.node_cref, getReference());
		a_Writer.putTag(ModelDescription.node_cname, getName());
		a_Writer.putTag(ModelDescription.node_shipcode, getShipCode());
		a_Writer.putTag(ModelDescription.node_shipname, getShipName());
		a_Writer.putTag(ModelDescription.node_startdate, getStartDate());
		a_Writer.putTag(ModelDescription.node_enddate, getEndDate());
		a_Writer.putTag(ModelDescription.node_region, getRegionName());
		a_Writer.putTag(ModelDescription.node_regionCode, getRegionCode());
		a_Writer.putTag(ModelDescription.node_country, getCountryCode());
		a_Writer.putTag(ModelDescription.node_laboratories, getLaboratories());
		a_Writer.putTag(ModelDescription.node_chief, getChiefScientist());
		a_Writer.putTag(ModelDescription.node_project, getProjectName());
		a_Writer.putTag(ModelDescription.node_datacenter, getDataCentre());
		a_Writer.putTag(ModelDescription.node_availability, getAvailability().toString());
		a_Writer.putCDATATag(ModelDescription.node_comment, getComment());
		a_Writer.putCDATATag(ModelDescription.node_correspondingURL, getCorrespondingURL());
		a_Writer.putTag(ModelDescription.node_correspondingURLAutoCheck, getCorrespondingURLAutoCheck());
		a_Writer.openTag(ModelDescription.node_listDatatypes);
		for (DataType t : getDataTypes()) {
			t.writeXMLOn(a_Writer);
		}
		a_Writer.closeTag();
	}

	/**
	 * Methode de creation d'instance a partir du WebService Nautilus
	 * @param a_Reference : reference de campagne
	 * @exception ServiceException
	 * @exception MalformedURLException
	 * @exception java.rmi.RemoteException
	 */
	public void fillFromSISMER(String a_Reference) throws ServiceException, MalformedURLException, RemoteException {

		NautilusSurveyWS service = new NautilusSurveyWSServiceLocator()
		.getNAUTILUS(new java.net.URL(Nemo.getInstance().getConfig()
				.getNautilusWebServiceURL()));
		WSSurvey wssurvey = service.getSurvey(a_Reference);

		// initialisation avec les valeurs par défaut
		initValues();

		if (wssurvey.getReference() != null) {
			// force à 13 caractères
			setReference(Formatter.getSingleton().padString(wssurvey.getReference(), 13));
		}
		if (wssurvey.getName() != null) {
			setName(wssurvey.getName());
		}
		if (wssurvey.getShipCode() != null) {
			setShipCode(wssurvey.getShipCode());
		}
		if (wssurvey.getShipName() != null) {
			setShipName(wssurvey.getShipName());
		}
		setStartDate(wssurvey.getStartDate());
		setEndDate(wssurvey.getEndDate());

		if (wssurvey.getRegionName() != null) {
			setRegionName(wssurvey.getRegionName());
		}

		if (wssurvey.getCountryCode() != null) {
			setCountryCode(wssurvey.getCountryCode());
		}

		if (wssurvey.getLaboratories() != null) {
			setLaboratories(wssurvey.getLaboratories());
		}
		if (wssurvey.getChiefScientist() != null) {
			setChiefScientist(wssurvey.getChiefScientist());
		}
		if (wssurvey.getProjectName() != null) {
			setProjectName(wssurvey.getProjectName());
		}
		if (wssurvey.getDataCentre() != null) {
			setDataCentre(wssurvey.getDataCentre());
		}

		WSCollectedData[] datas = wssurvey.getDataTypes();
		//releaseDataTypes();
		if (datas != null) {
			for (int i = 0; i < datas.length; i++) {
				WSCollectedData data = datas[i];
				DataType dataType = new DataType();
				dataType.setCode(data.getCode());
				dataType.setNumberOfProfiles(data.getObservationNumber());
				dataType.setSensor(data.isSensor());
				addDataType(dataType);
			}
		}
	}

	/**
	 * transcode C16 label to C19 Label
	 * @param region Name C16
	 * @return region Name C19
	 */

	//FAE 16868
	public static String getTranscodeRegionName(String regionNameC16) {
		ICollection regionsRectangles = VocabLists.getVocabList(Intern.list_regionsRectangles);
		String regionsKey = null;
		String RegionName = Global.EMPTY_STRING;
		for (IElement elem : regionsRectangles.getElements()) {
			if (elem.getLabel().equals(regionNameC16)) {
				regionsKey = elem.getKey();
				break;
			}
		}
		if (regionsKey != null) {
			List<IElement> c19List = VocabLists.getVocabMatchs(Intern.list_regionsRectangles,
					Intern.list_regionsPolygones, regionsKey);
			if (!c19List.isEmpty()) {
				RegionName = c19List.get(0).getLabel();
			}
		}
		return RegionName;
	}




	/**
	 * Methode de creation d'instance a partir d'un fichier XML
	 * il peut charger des fichier ISO 19139 et 19115
	 * @param xmlFile : Schéma XML
	 * @exception SAXException
	 * @exception TransformerException
	 * @exception java.io.FileNotFoundException
	 */
	public void importFromXML(File xmlFile) throws SAXException, TransformerException {

		//verifie si input xml est SDN2_CSR_ISO19139_3.0.0.xsd complieant
		if(checkSchema( new StreamSource(xmlFile), Intern.CruiseShemaLocationISO19139))
		{
			importFromXML(xmlFile, new File(Intern.dir_resources + File.separator + Intern.CruiseXSLISO19139));
			xmlVersion = XmlVersion.ISO_19139;
		}
		//verifie si input xml est CSR_sdn_V1.3.xsd complieant
		else if (checkSchema( new StreamSource(xmlFile), Intern.CruiseShemaLocationISO19115))
		{
			importFromXML(xmlFile,  new File(Intern.dir_resources + File.separator + Intern.CruiseXSLISO19115));
			xmlVersion = XmlVersion.ISO_19115;
		}
		else
		{
			//erreur si fichier n'est pas en ISO_19139 ou ISO_19115
			Utils.messageErreurAppend(Messages.error_cruise_invalidXML);
		}



	}


	/**
	 * Methode de creation d'instance a partir d'un fichier XML
	 * @param xmlFile : Schéma XML
	 * @param xslSource : Schéma XSL
	 * @param schemaFile : Schéma XSD
	 * @exception SAXException
	 * @exception TransformerException
	 * @exception java.io.FileNotFoundException
	 */
	private void importFromXML(File xmlFile, File xslFile) throws SAXException, TransformerException {

		StreamSource xmlSource = new StreamSource(xmlFile);
		StreamSource xslSource = new StreamSource(xslFile);

		XMLBootstrapParser parser = new XMLBootstrapParser(this);
		Result result = new SAXResult(parser);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(xslSource);
		transformer.transform(xmlSource, result);

	}

	/**
	 * Vérifie la validité du Schéma XML passé
	 * @param xmlFile : Schéma XML
	 * @return Boolean : true si valide, false sinon
	 * @throws SAXException
	 */
	private Boolean checkSchema(Source xmlFile, String fileName) throws SAXException {

		// 1. Lookup a factory for the W3C XML Schema language
		SchemaFactory factory = SchemaFactory.newInstance(Intern.CruiseShemaFactory);

		// 2. Compile the schema.
		// Here the schema is loaded from a java.io.File, but you could use
		// a java.net.URL or a javax.xml.transform.Source instead.
		File schemaFile = new File(Intern.dir_resources + File.separator + fileName);
		Schema schema = factory.newSchema(schemaFile);

		// 3. Get a validator from the schema.
		Validator validator = schema.newValidator();

		// 4. Check the document
		try {
			validator.validate(xmlFile);
			return true;

		} catch (SAXException ex) {
			return false;
		} catch (IOException ex) {
			return false;
		}

	}

	/**
	 * Returns a copy of this Object
	 * @return a copy of this object
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		CruiseModel copy = null;
		copy = (CruiseModel) super.clone();
		copy._dataTypes = new ArrayList<DataType>();
		for (DataType el : getDataTypes()) {
			copy.addDataType((DataType) el.clone());
		}
		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void release() {
		releaseDataTypes();
	}

	/**
	 * Supprime _datatypes
	 */
	public void releaseDataTypes() {
		for (DataType el : getDataTypes()) {
			el.release();
			removeDataType(el);
		}
		_dataTypes = null;
	}

	/**
	 * Getter pour _availability.
	 * @return E_Availability
	 * @see CruiseModel.java#_availability
	 */
	public E_Availability getAvailability() {
		return _availability;
	}

	/**
	 * Setter pour _availability
	 * @param x : nouvelle valeur pour _availability
	 * @see CruiseModel.java#_availability
	 */
	public void setAvailability(E_Availability x) {
		_availability = x;
	}

	/**
	 * Setter pour _availability
	 * @param s : nouvelle valeur pour _availability sous forme de String
	 * @see CruiseModel.java#_availability
	 */
	public void setAvailability(String s) {
		try {
			_availability = Enum.valueOf(E_Availability.class, s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter pour _chiefScientist.
	 * @return String
	 * @see CruiseModel.java#_chiefScientist
	 */
	public String getChiefScientist() {
		return _chiefScientist;
	}

	/**
	 * Setter pour _chiefScientist
	 * @param x : nouvelle valeur pour _chiefScientist
	 * @see CruiseModel.java#_chiefScientist
	 */
	public void setChiefScientist(String x) {
		_chiefScientist = x;
	}

	/**
	 * Getter pour _comment.
	 * @return String
	 * @see CruiseModel.java#_comment
	 */
	public String getComment() {
		return _comment;
	}

	/**
	 * Getter pour _correspondingURL.
	 * @return String
	 * @see CruiseModel.java#_correspondingURL
	 */
	public String getCorrespondingURL() {
		if (_correspondingURLAutoCheck){
			return getCorrespondingURLDefault();
		}
		return _correspondingURL;

	}

	public String  getCorrespondingURLDefault()
	{

		SdnXLinkReference csr = new SdnXLinkReference();
		try {
			// TODO 20755 check if edmo is the good one
			csr.setAsCsr(Integer.valueOf(Nemo.getInstance().getConfig().getIdEdmoRespCdi()), 
					_reference);
		} catch (NumberFormatException e) {
			// TODO 20755 Auto-generated catch block
			e.printStackTrace();
		} catch (SDNXLinkException e) {
			// TODO 20755 Auto-generated catch block
			e.printStackTrace();
		}
		return csr.toString();
	}

	public Boolean getCorrespondingURLAutoCheck() {
		return _correspondingURLAutoCheck;
	}

	public void setCorrespondingURLAutoCheck(String x) {
		_correspondingURLAutoCheck = Boolean.valueOf(x);
	}
	public void setCorrespondingURLAutoCheck(Boolean  correspondingURLAutoCheck) {
		_correspondingURLAutoCheck = correspondingURLAutoCheck;
	}
	/**
	 * Setter pour _comment
	 * @param x : nouvelle valeur pour _comment
	 * @see CruiseModel.java#_comment
	 */
	public void setComment(String x) {
		_comment = x;
	}

	/**
	 * Setter pour _correspondingURL
	 * @param x : nouvelle valeur pour _correspondingURL
	 * @see CruiseModel.java#_correspondingURL
	 */
	public void setCorrespondingURL(String x) {
		_correspondingURL = x;
	}

	/**
	 * Getter pour _countryCode.
	 * @return String
	 * @see CruiseModel.java#_countryCode
	 */
	public String getCountryCode() {
		return _countryCode;
	}

	/**
	 * Setter pour _countryCode
	 * @param x : nouvelle valeur pour _countryCode
	 * @see CruiseModel.java#_countryCode
	 */
	public void setCountryCode(String x) {
		_countryCode = x;
	}

	/**
	 * Getter pour _countryName.
	 * @return String
	 * @see CruiseModel.java#_countryName
	 */
	public String getCountryName() {
		return _countryName;
	}

	/**
	 * Setter pour _countryName
	 * @param countryName : nouvelle valeur pour countryName
	 * @see CruiseModel.java#_countryName
	 */
	public void setCountryName(String countryName) {
		_countryName = countryName;
	}

	/**
	 * Getter pour _dataCentre.
	 * @return String
	 * @see CruiseModel.java#_dataCentre
	 */
	public String getDataCentre() {
		return _dataCentre;
	}

	/**
	 * Setter pour _dataCentre
	 * @param x : nouvelle valeur pour _dataCentre
	 * @see CruiseModel.java#_dataCentre
	 */
	public void setDataCentre(String x) {
		_dataCentre = x;
	}

	/**
	 * Getter pour _endDate.
	 * @return long
	 * @see CruiseModel.java#_endDate
	 */
	public long getEndDate() {
		return _endDate;
	}

	/**
	 * Setter pour _endDate
	 * @param x : nouvelle valeur pour _endDate
	 * @see CruiseModel.java#_endDate
	 */
	public void setEndDate(long x) {
		_endDate = x;
	}

	/**
	 * Setter pour _endDate
	 * @param s : nouvelle valeur pour _endDate sous forme de String
	 * @see CruiseModel.java#_endDate
	 */
	public void setEndDate(String s) throws ParseException {
		try {
			_endDate = Long.parseLong(s);
		} catch (Exception e) {
			SimpleDateFormat fmt = new SimpleDateFormat(MED_DATE_FORMAT);
			Date value = fmt.parse(s);
			_endDate = value.getTime();
		}
	}

	/**
	 * Setter pour _endDate
	 * @param a_EndDate : nouvelle valeur pour _endDate sous forme de String formattée
	 * @exception ParseException
	 * @see CruiseModel.java#_endDate
	 */
	public void setFormattedEndDate(String a_EndDate) throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat(CSR_DATE_FORMAT);
		Date value = fmt.parse(a_EndDate);
		setEndDate(value.getTime());
	}

	/**
	 * Getter pour _laboratories.
	 * @return String
	 * @see CruiseModel.java#_laboratories
	 */
	public String getLaboratories() {
		return _laboratories;
	}

	/**
	 * Setter pour _laboratories
	 * @param x : nouvelle valeur pour _laboratories
	 * @see CruiseModel.java#_laboratories
	 */
	public void setLaboratories(String x) {
		if (x.length() > 75)
			_laboratories = x.substring(0, 75);
		else
			_laboratories = x;
	}

	/**
	 * Getter pour _name.
	 * @return String
	 * @see CruiseModel.java#_name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Setter pour _name
	 * @param x : nouvelle valeur pour _name
	 * @see CruiseModel.java#_name
	 */
	public void setName(String x) {
		_name = x;
	}

	/**
	 * Getter pour _projectName.
	 * @return String
	 * @see CruiseModel.java#_projectName
	 */
	public String getProjectName() {
		return _projectName;
	}

	/**
	 * Setter pour _projectName
	 * @param x : nouvelle valeur pour _projectName
	 * @see CruiseModel.java#_projectName
	 */
	public void setProjectName(String x) {
		_projectName = x;
	}

	/**
	 * Getter pour _reference.
	 * @return String
	 * @see CruiseModel.java#_reference
	 */
	public String getReference() {
		return _reference;
	}

	/**
	 * Setter pour _reference
	 * @param x : nouvelle valeur pour _reference
	 * @see CruiseModel.java#_reference
	 */
	public void setReference(String x) {
		_reference = x;
	}

	/**
	 * Getter pour _regionName.
	 * @return String
	 * @see CruiseModel.java#_regionName
	 */
	public String getRegionName() {
		return _regionName;
	}

	/**
	 * Setter pour _regionName
	 * @param x : nouvelle valeur pour _regionName
	 * @see CruiseModel.java#_regionName
	 */
	public void setRegionName(String x) {
		_regionName = x;
	}

	/**
	 * @return the regionCode
	 */
	public String getRegionCode() {
		return _regionCode;
	}

	/**
	 * @param regionCode the regionCode to set
	 */
	public void setRegionCode(String regionCode) {
		_regionCode = regionCode;
	}

	/**
	 * Getter pour _shipCode.
	 * @return String
	 * @see CruiseModel.java#_shipCode
	 */
	public String getShipCode() {
		return _shipCode;
	}

	/**
	 * Setter pour _shipCode
	 * @param x : nouvelle valeur pour _shipCode
	 * @see CruiseModel.java#_shipCode
	 */
	public void setShipCode(String x) {
		_shipCode = x;
	}

	/**
	 * Getter pour _shipName.
	 * @return String
	 * @see CruiseModel.java#_shipName
	 */
	public String getShipName() {
		return _shipName;
	}

	/**
	 * Setter pour _shipName
	 * @param x : nouvelle valeur pour _shipName
	 * @see CruiseModel.java#_shipName
	 */
	public void setShipName(String x) {
		_shipName = x;
	}

	/**
	 * Getter pour _startDate.
	 * @return long
	 * @see CruiseModel.java#_startDate
	 */
	public long getStartDate() {
		return _startDate;
	}

	/**
	 * Getter pour xmlVersion.
	 * @return XmlVersion
	 * @see CruiseModel.java#xmlVersion
	 */
	public XmlVersion getXmlVersion()
	{
		return xmlVersion;
	}

	/**
	 * Setter pour _startDate
	 * @param x : nouvelle valeur pour _startDate
	 * @see CruiseModel.java#_startDate
	 */
	public void setStartDate(long x) {
		_startDate = x;
	}

	/**
	 * Setter pour _startDate
	 * @param s : nouvelle valeur pour _startDate sous forme de String
	 * @throws ParseException 
	 * @see CruiseModel.java#_startDate
	 */
	public void setStartDate(String s) throws ParseException {
		try {
			_startDate = Long.parseLong(s);
		} catch (Exception e) {
			SimpleDateFormat fmt = new SimpleDateFormat(MED_DATE_FORMAT);
			Date value = fmt.parse(s);
			_startDate = value.getTime();
		}
	}

	/**
	 * Setter pour _startDate
	 * @param a_StartDate : nouvelle valeur pour _startDate sous forme de String formattée
	 * @exception ParseException
	 * @see CruiseModel.java#_startDate
	 */
	public void setFormattedStartDate(String a_StartDate) throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat(CSR_DATE_FORMAT);
		Date value = fmt.parse(a_StartDate);
		setStartDate(value.getTime());
	}

	/**
	 * Getter pour _dataTypes
	 * @return DataType[] : le tableau des DataType
	 * @see CruiseModel.java#_dataTypes
	 */
	public DataType[] getDataTypes() {
		DataType[] v = new DataType[_dataTypes.size()];
		_dataTypes.toArray(v);
		return v;
	}

	public int getDataTypesLength() {
		return _dataTypes.size();
	}

	public DataType getDataType(String code) {
		DataType dataType = null;
		for (DataType d : getDataTypes()) {
			if (d.getCode().equals(code)) {
				dataType = d;
				break;
			}
		}
		return dataType;
	}

	/**
	 * Renvoit le DataType trouvée à l'index i de _dataTypes
	 * @param i : index
	 * @return DataType
	 * @see CruiseModel.java#_dataTypes
	 */
	public DataType dataTypesAt(int i) {
		return _dataTypes.get(i);
	}

	/**
	 * Supprime x de _dataTypes
	 * @param x : DataType à supprimer
	 * @see CruiseModel.java#_dataTypes
	 */
	public void removeDataType(DataType x) {
		if (_dataTypes.contains(x)) {
			_dataTypes.remove(x);
		}
	}

	/**
	 * Ajoute x à _dataTypes
	 * @param x : DataType à ajouter
	 * @see CruiseModel.java#_dataTypes
	 */
	public void addDataType(DataType x) {
		if (!_dataTypes.contains(x)) {
			for (DataType data : _dataTypes) {
				if (data.getCode().equals(x.getCode()))
					return;
			}
			_dataTypes.add(x);
		}
	}
}