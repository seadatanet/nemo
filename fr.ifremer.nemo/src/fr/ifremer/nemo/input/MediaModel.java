package fr.ifremer.nemo.input;

import org.xml.sax.Attributes;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_EndData;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Global.E_Variant;

/**
 * Class MediaModel Description: Informations descrivant l'onglet File
 * 
 * @author Delphine BOULANGER
 */
public class MediaModel extends AbstractModel
{
	/**
	 * Chemin du fichier source
	 */
	protected String _sourcePath;

	/**
	 * Type du traitement
	 */
	protected E_ProcessType _processType;

	/**
	 * indicateur de multiplicité des stations pour chaque fichier
	 */
	protected boolean _multiStations;

	/**
	 * Séparateur des données en entrée
	 */
	protected String _separator;

	/**
	 * Type de fichier en entrée
	 */
	protected E_InputType _inputType;
	/**
	 * Variante
	 */
	protected E_Variant _variant;
	/**
	 * nombre de lignes de l'entête fichier
	 */
	protected int _fileHeader;

	/**
	 * nombre de lignes de l'entête station ou séparant _stationHeaderPattern du
	 * début des données
	 */
	private int _stationHeader;

	/**
	 * indicateur de fin d'entête station
	 */
	protected String _stationHeaderPattern;
	
	/**
	 * indique si l'utilisateur souhaite utiliser le séparateur  _separator dans l'en-tete station
	 */
	protected boolean _useSeparatorInStationHeader;
	


	/**
	 * indique si l'utilisateur souhaite utiliser un tableau pour afficher les donnees (csv uniquement)
	 */
	protected boolean _useTableForCsvData;
	/**
	 * indique si le titre des colonnes est dispo dans le fichier (pour input avec separateur uniquement)
	 */
	protected boolean _colTitlesInInputFiles;
	protected int _colTitlesInInputFilesLineNb;

	/**
	 * nombre de lignes séparant la fin d'une station et le début de la suivante
	 */
	protected int _endStation;

	/**
	 * indicateur de fin des données
	 */
	protected E_EndData _endData;

	/**
	 * complément de l'indicateur de fin des données
	 */
	protected String _endDataPattern;
	protected boolean _endDataPatternIsRegexp;

	public MediaModel()
	{
		_tagName = ModelDescription.node_mediaModel;
		initValues();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initValues()
	{
		_sourcePath = Global.EMPTY_STRING;
		_processType = E_ProcessType.CRUISE_FILE;
		_multiStations = false;
		_separator = Global.EMPTY_STRING;
		_inputType = E_InputType.UNKNOWN;
		_variant= E_Variant.UNKNOWN;
		_fileHeader = 0;
		_stationHeader = 0;
		_stationHeaderPattern = Global.EMPTY_STRING;
		_useSeparatorInStationHeader = false;
		_endStation = 0;
		_endData = E_EndData.EOF;
		_endDataPattern = Global.EMPTY_STRING;
		_endDataPatternIsRegexp = false;
		_colTitlesInInputFiles=false;
		_colTitlesInInputFilesLineNb = 0;
		_useTableForCsvData=false;
	}

	/**
	 * @see AbstractModel.java#fromElement
	 */
	public static SAXConsumer fromElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes)
	{
		MediaModel el = new MediaModel();
		el.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		return el;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildFromElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes)
	{
		// String tmp;
		// if ((tmp = a_Attributes.getValue(ModelDescription.a_sourcepath)) !=
		// null){
		// setSourcePath(tmp);
		// }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SAXConsumer consumeElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes)
	{
		if (a_LocalName.equals(ModelDescription.node_process))
		{
			String attribute = a_Attributes.getValue(ModelDescription.a_multistations);
			setMultiStations(attribute);

			Element2Field setter = new Element2Field(this, ModelDescription.setter_process);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_separator))
		{
			Element2Field setter = new Element2Field(this, ModelDescription.setter_separator);
			return setter;
		}
		/**
		 * column titles for CSV files
		 */
		if (a_LocalName.equals(ModelDescription.node_colTitleInInputFile)){
			try{
				String attributeColTitleCheck = a_Attributes.getValue(ModelDescription.a_colTitleInInputFileCheck);
				setColTitlesInInputFile(Boolean.parseBoolean(attributeColTitleCheck));
			}catch (Exception e) {
				setColTitlesInInputFile(false);
			}
			try{
				String attributeColTitleLineNb = a_Attributes.getValue(ModelDescription.a_colTitleInInputFileLineNb);
				setColTitlesInInputFilesLineNb(Integer.parseInt(attributeColTitleLineNb));
			}catch (Exception e) {
				setColTitlesInInputFilesLineNb(0);
			}
			try{
				String useTableForCsv = a_Attributes.getValue(ModelDescription.a_useTableForCsv);
				setUseTableForCsvData(Boolean.parseBoolean(useTableForCsv));
			}catch (Exception e) {
				setUseTableForCsvData(false);
			}

		}
		if (a_LocalName.equals(ModelDescription.node_type))
		{
			Element2Field setter = new Element2Field(this, ModelDescription.setter_type);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_variant))
		{
			Element2Field setter = new Element2Field(this, ModelDescription.setter_variant);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_fileHeader))
		{
			Element2Field setter = new Element2Field(this, ModelDescription.setter_fileHeader);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_stationHeader))
		{
			String attribute = a_Attributes.getValue(ModelDescription.a_pattern);
			setStationHeaderPattern(attribute);
			
			attribute = a_Attributes.getValue(ModelDescription.a_useSeparatorInStationHeader);
			setUseSeparatorInStationHeader(Boolean.parseBoolean(attribute));
			
			Element2Field setter = new Element2Field(this, ModelDescription.setter_stationHeader);
			return setter;
		}
		
		if (a_LocalName.equals(ModelDescription.node_endStation))
		{
			Element2Field setter = new Element2Field(this, ModelDescription.setter_endStation);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_endData))
		{
			String attribute = a_Attributes.getValue(ModelDescription.a_pattern);
			setEndDataPattern(attribute);
			attribute = a_Attributes.getValue(ModelDescription.a_patternIsRegexp);
			setEndDataPatternIsRegexp(Boolean.parseBoolean(attribute));

			Element2Field setter = new Element2Field(this, ModelDescription.setter_endData);
			return setter;
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SAXConsumer handlePathElement(String path, String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes)
	{
		// non utilisé
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeXMLBodyOn(XMLOutputWriter a_Writer)
	{

		a_Writer.putAttribute(ModelDescription.a_sourcepath, getSourcePath());
		
		// 	PROCESS TYPE
		a_Writer.openTag(ModelDescription.node_process);
		a_Writer.putAttribute(ModelDescription.a_multistations, isMultiStations());
		a_Writer.closeTag(getProcessType().toString());
		
		// SEPARATOR
		a_Writer.putTag(ModelDescription.node_separator, getSeparator());
		
		// INPUT TYPE
		a_Writer.putTag(ModelDescription.node_type, getInputType().toString());
		
		// INPUT VARIANT
		a_Writer.putTag(ModelDescription.node_variant, getVariant().toString());

		// FILE HEADER
		a_Writer.putTag(ModelDescription.node_fileHeader, getFileHeader());
		
		// STATION HEADER
		a_Writer.openTag(ModelDescription.node_stationHeader);
		a_Writer.putAttribute(ModelDescription.a_pattern, getStationHeaderPattern());
		a_Writer.putAttribute(ModelDescription.a_useSeparatorInStationHeader, isUseSeparatorInStationHeader());
		a_Writer.closeTag(getStationHeader());

		// COLUMN TITLES
		a_Writer.openTag(ModelDescription.node_colTitleInInputFile);
		a_Writer.putAttribute(ModelDescription.a_colTitleInInputFileCheck, isColTitlesInInputFile());
		a_Writer.putAttribute(ModelDescription.a_colTitleInInputFileLineNb, getColTitlesInInputFilesLineNb());
		a_Writer.putAttribute(ModelDescription.a_useTableForCsv, getUseTableForCsvData());
		a_Writer.closeTag();
		
		// END STATION
		a_Writer.putTag(ModelDescription.node_endStation, getEndStation());
		a_Writer.openTag(ModelDescription.node_endData);
		a_Writer.putAttribute(ModelDescription.a_pattern, getEndDataPattern());
		a_Writer.putAttribute(ModelDescription.a_patternIsRegexp, getEndDataPatternIsRegexp());
		a_Writer.closeTag(getEndData().toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		MediaModel copy = null;
		copy = (MediaModel) super.clone();
		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void release()
	{
		// aucun lien à supprimer
	}

	/**
	 * Getter pour _endData.
	 * 
	 * @return E_EndData
	 * @see MediaModel.java#_endData
	 */
	public E_EndData getEndData()
	{
		return _endData;
	}

	/**
	 * Setter pour _endData.
	 * 
	 * @param x
	 *            : nouvelle valeur pour _endData
	 * @see MediaModel.java#_endData
	 */
	public void setEndData(E_EndData x)
	{
		_endData = x;
	}

	/**
	 * Setter pour _endData.
	 * 
	 * @param s
	 *            : nouvelle valeur pour _endData sous forme de String
	 * @see MediaModel.java#_endData
	 */
	public void setEndData(String s)
	{
		try
		{
			_endData = Enum.valueOf(E_EndData.class, s);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Getter pour _endDataPattern.
	 * 
	 * @return String
	 * @see MediaModel.java#_endDataPattern
	 */
	public String getEndDataPattern()
	{
		return _endDataPattern;
	}

	/**
	 * Getter pour _endDataPatternIsRegexp.
	 * 
	 * @return Boolean
	 * @see MediaModel.java#_endDataPatternIsRegexp
	 */
	public Boolean getEndDataPatternIsRegexp()
	{
		return _endDataPatternIsRegexp;
	}

	/**
	 * Setter pour _endDataPattern.
	 * 
	 * @param x
	 *            : nouvelle valeur pour _endDataPattern
	 * @see MediaModel.java#_endDataPattern
	 */
	public void setEndDataPattern(String x)
	{
		_endDataPattern = x;
	}

	public void setEndDataPatternIsRegexp(Boolean x)
	{
		_endDataPatternIsRegexp = x;
	}

	/**
	 * Getter pour _endStation.
	 * 
	 * @return int
	 * @see MediaModel.java#_endStation
	 */
	public int getEndStation()
	{
		return _endStation;
	}

	/**
	 * Setter pour _endStation.
	 * 
	 * @param x
	 *            : nouvelle valeur pour _endStation
	 * @see MediaModel.java#_endStation
	 */
	public void setEndStation(int x)
	{
		_endStation = x;
	}

	/**
	 * Setter pour _endStation.
	 * 
	 * @param s
	 *            : nouvelle valeur pour _endStation sous forme de String
	 * @see MediaModel.java#_endStation
	 */
	public void setEndStation(String s)
	{
		try
		{
			_endStation = Integer.parseInt(s);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Getter pour _fileHeader.
	 * 
	 * @return int
	 * @see MediaModel.java#_fileHeader
	 */
	public int getFileHeader()
	{
		return _fileHeader;
	}

	/**
	 * Setter pour _fileHeader.
	 * 
	 * @param x
	 *            : nouvelle valeur pour _fileHeader
	 * @see MediaModel.java#_fileHeader
	 */
	public void setFileHeader(int x)
	{
		_fileHeader = x;
	}

	/**
	 * Setter pour _fileHeader.
	 * 
	 * @param s
	 *            : nouvelle valeur pour _fileHeader sous forme de String
	 * @see MediaModel.java#_fileHeader
	 */
	public void setFileHeader(String s)
	{
		try
		{
			_fileHeader = Integer.parseInt(s);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Getter pour _inputType.
	 * 
	 * @return E_InputType
	 * @see MediaModel.java#_inputType
	 */
	public E_InputType getInputType()
	{
		return _inputType;
	}

	/**
	 * Setter pour _inputType.
	 * 
	 * @param x
	 *            : nouvelle valeur pour _inputType
	 * @see MediaModel.java#_inputType
	 */
	public void setInputType(E_InputType x)
	{
		_inputType = x;
	}

	/**
	 * Setter pour _inputType.
	 * 
	 * @param s
	 *            : nouvelle valeur pour _inputType sous forme de String
	 * @see MediaModel.java#_inputType
	 */
	public void setInputType(String s)
	{
		try
		{
			_inputType = Enum.valueOf(E_InputType.class, s.toUpperCase());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public E_Variant getVariant() {
		return _variant;
	}

	public void setVariant(E_Variant variant) {
		_variant = variant;
	}
	
	
	
	public void setVariant(String  variant) {
		try
		{
			_variant = Enum.valueOf(E_Variant.class, variant.toUpperCase());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * Getter pour _multiStations.
	 * 
	 * @return boolean
	 * @see MediaModel.java#_multiStations
	 */
	public boolean isMultiStations()
	{
		return _multiStations;
	}

	/**
	 * Setter pour _multiStations.
	 * 
	 * @param x
	 *            : nouvelle valeur pour _multiStations
	 * @see MediaModel.java#_multiStations
	 */
	public void setMultiStations(boolean x)
	{
		_multiStations = x;
	}

	/**
	 * Setter pour _multiStations.
	 * 
	 * @param s
	 *            : nouvelle valeur pour _multiStations sous forme de String
	 * @see MediaModel.java#_multiStations
	 */
	public void setMultiStations(String s)
	{
		try
		{
			_multiStations = new Boolean(s).booleanValue();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Getter pour _processType.
	 * 
	 * @return E_ProcessType
	 * @see MediaModel.java#_processType
	 */
	public E_ProcessType getProcessType()
	{
		return _processType;
	}

	/**
	 * Setter pour _processType.
	 * 
	 * @param x
	 *            : nouvelle valeur pour _processType
	 * @see MediaModel.java#_processType
	 */
	public void setProcessType(E_ProcessType x)
	{
		_processType = x;
	}

	/**
	 * Setter pour _processType.
	 * 
	 * @param s
	 *            : nouvelle valeur pour _processType sous forme de String
	 * @see MediaModel.java#_processType
	 */
	public void setProcessType(String s)
	{
		try
		{
			_processType = Enum.valueOf(E_ProcessType.class, s);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Getter pour _separator.
	 * 
	 * @return String
	 * @see MediaModel.java#_separator
	 */
	public String getSeparator()
	{
		return _separator;
	}

	/**
	 * Setter pour _separator.
	 * 
	 * @param x
	 *            : nouvelle valeur pour _separator
	 * @see MediaModel.java#_separator
	 */
	public void setSeparator(String x)
	{
		_separator = x;
	}

	/**
	 * Getter pour _sourcePath.
	 * 
	 * @return String
	 * @see MediaModel.java#_sourcePath
	 */
	public String getSourcePath()
	{
		return _sourcePath;
	}

	/**
	 * Setter pour _sourcePath.
	 * 
	 * @param x
	 *            : nouvelle valeur pour _sourcePath
	 * @see MediaModel.java#_sourcePath
	 */
	public void setSourcePath(String x)
	{
		_sourcePath = x;
	}

	/**
	 * Getter pour _stationHeader.
	 * 
	 * @return int
	 * @see MediaModel.java#_stationHeader
	 */
	public int getStationHeader()
	{
		return _stationHeader;
	}

	/**
	 * Setter pour _stationHeader.
	 * 
	 * @param x
	 *            : nouvelle valeur pour _stationHeader
	 * @see MediaModel.java#_stationHeader
	 */
	public void setStationHeader(int x)
	{
		_stationHeader = x;
	}

	/**
	 * Setter pour _stationHeader.
	 * 
	 * @param s
	 *            : nouvelle valeur pour _stationHeader sous forme de String
	 * @see MediaModel.java#_stationHeader
	 */
	public void setStationHeader(String s)
	{
		try
		{
			_stationHeader = Integer.parseInt(s);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Getter pour _stationHeaderPattern.
	 * 
	 * @return String
	 * @see MediaModel.java#_stationHeaderPattern
	 */
	public String getStationHeaderPattern()
	{
		return _stationHeaderPattern;
	}

	/**
	 * Setter pour _stationHeaderPattern.
	 * 
	 * @param x
	 *            : nouvelle valeur pour _stationHeaderPattern
	 * @see MediaModel.java#_stationHeaderPattern
	 */
	public void setStationHeaderPattern(String x)
	{
		_stationHeaderPattern = x;
	}

	public boolean isColTitlesInInputFile() {
		return _colTitlesInInputFiles;
	}

	public void setColTitlesInInputFile(boolean colTitlesInInputFile) {
		_colTitlesInInputFiles = colTitlesInInputFile;
	}

	public int getColTitlesInInputFilesLineNb() {
		return _colTitlesInInputFilesLineNb;
	}

	public void setColTitlesInInputFilesLineNb(int colTitlesInInputFilesLineNb) {
		_colTitlesInInputFilesLineNb = colTitlesInInputFilesLineNb;
	}
	public boolean getUseTableForCsvData() {
		return _useTableForCsvData;
	}

	public void setUseTableForCsvData(boolean useTableForCsvData) {
		_useTableForCsvData = useTableForCsvData;
	}
	public boolean isUseSeparatorInStationHeader() {
		return _useSeparatorInStationHeader;
	}

	public void setUseSeparatorInStationHeader(boolean useSeparatorInStationHeader) {
		_useSeparatorInStationHeader = useSeparatorInStationHeader;
	}
}