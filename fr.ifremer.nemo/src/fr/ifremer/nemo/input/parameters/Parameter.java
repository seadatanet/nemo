package fr.ifremer.nemo.input.parameters;

import org.xml.sax.Attributes;

import com.ago.xml.Element2Field;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.LabelParameterManager;
import fr.ifremer.nemo.domain.StandardNameParameterManager;
import fr.ifremer.nemo.input.AbstractModel;
import fr.ifremer.nemo.input.Fragment;
import fr.ifremer.nemo.input.ModelDescription;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Intern;

/**
 * Class Parameter Description : Classe permettant la description d'un parametre
 * mesure
 * 
 * @author Gerard BUNEL
 */
public class Parameter extends AbstractModel {
	/**
	 * Code du paramètre (MEDATLAS: 4 char max)
	 */
	protected String _code;

	/**
	 * Code du paramètre P011
	 */
	protected String _codeP011;
	
	/**
	 * Index du paramètre P011 s'il n'est pas le seul dans une liste de paramètres.
	 * Cet index est à concaténer au code P011 le cas échéant (écriture NetCDF, ...)
	 */
	protected Integer _indexP011;

	/**
	 * Nom du paramètre P011
	 */
	protected String _nameP011;

	/**
	 * Booléen permettant de connaitre le type de donnée (true type P011 false
	 * type P091)
	 */
	protected boolean _P011;

	/**
	 * Valeur par défaut du paramètre (MEDATLAS: 5 char max)
	 */
	protected String _default;

	/**
	 * Format à appliquer à la valeur du paramètre
	 */
	protected String _format;

	/**
	 * Nom complet du paramètre (IOC def.) (MEDATLAS: 30 char max)
	 */
	protected String _fullName;

	/**
	 * Unité IS/IOC (MEDATLAS: 30 char max)
	 */
	protected String _unit;
	// FAE 6542
	protected String _unitCode;
	protected String _p06Label;
	
	protected String _instrument;
	protected String _instrumentCode;


	

	public String getInstrumentCode() {
		return _instrumentCode;
	}

	public void setInstrumentCode(String instrumentCode) {
		_instrumentCode = instrumentCode;
	}

	public Parameter() {
		_tagName = ModelDescription.node_parameter;
		initValues();
	}

	public Parameter(TypeData typeData) {
		_tagName = ModelDescription.node_parameter;
		initValues();

		// code, nom, unite
		setCode(typeData.getEntryKey());
		setFullName(typeData.getEntryTerm());
		setUnit(typeData.getUnite());
		// FAE 6542
		setUnitCode(typeData.getUniteCode());
		setP06Label(typeData.getP06Label());

		// specific P011
		setCodeP011(typeData.getEntryKeyP011());
		setNameP011(typeData.getEntryTermP011());
		setP011(typeData.isP011());

		setDefault(typeData.getSortie());
		setFormat(typeData.getFormat());
		
		setInstrument(typeData.getInstrument());
		setInstrumentCode(typeData.getInstrumentCode());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initValues() {
		_code = Global.EMPTY_STRING;
		_default = Global.EMPTY_STRING;
		_format = Global.EMPTY_STRING;
		_fullName = Global.EMPTY_STRING;
		_unit = Global.EMPTY_STRING;
		_unitCode = Global.EMPTY_STRING;
		_p06Label= Global.EMPTY_STRING;
		_codeP011 = Global.EMPTY_STRING;
		_nameP011 = Global.EMPTY_STRING;
		_P011 = false;
		_instrument = Global.EMPTY_STRING;
		_instrumentCode = Global.EMPTY_STRING;

		
	}

	/**
	 * @see AbstractModel.java#fromElement
	 */
	public static SAXConsumer fromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) {
		Parameter el = new Parameter();
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
		if ((tmp = a_Attributes.getValue(ModelDescription.a_code)) != null) {
			setCode(tmp);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SAXConsumer consumeElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) {
		if (a_LocalName.equals(ModelDescription.node_fullname)) {
			Element2Field setter = new Element2Field(this,
					ModelDescription.setter_fullname);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_unit)) {
			Element2Field setter = new Element2Field(this,
					ModelDescription.setter_unit);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_unitCode)) {
			Element2Field setter = new Element2Field(this,
					ModelDescription.setter_unitCode);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_outdefault)) {
			Element2Field setter = new Element2Field(this,
					ModelDescription.setter_outdefault);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_format)) {
			Element2Field setter = new Element2Field(this,
					ModelDescription.setter_format);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_fullname)) {
			Element2Field setter = new Element2Field(this,
					ModelDescription.setter_fullname);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_codeP011)) {
			Element2Field setter = new Element2Field(this,
					ModelDescription.setter_codeP011);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_nameP011)) {
			Element2Field setter = new Element2Field(this,
					ModelDescription.setter_nameP011);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_P011)) {
			Element2Field setter = new Element2Field(this,
					ModelDescription.setter_P011);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_instrument)) {
			Element2Field setter = new Element2Field(this,
					ModelDescription.setter_instrument);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_instrument_code)) {
			Element2Field setter = new Element2Field(this,
					ModelDescription.setter_instrument_code);
			return setter;
		}
	
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SAXConsumer handlePathElement(String path, String a_URI,
			String a_LocalName, String a_QName, Attributes a_Attributes) {
		// not used
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		a_Writer.putAttribute(ModelDescription.a_code, getCode());
		a_Writer.putTag(ModelDescription.node_fullname, getFullName());
		a_Writer.putTag(ModelDescription.node_unit, getUnit());
		a_Writer.putTag(ModelDescription.node_unitCode, getUnitCode());
		a_Writer.putTag(ModelDescription.node_outdefault, getDefault());
		a_Writer.putTag(ModelDescription.node_format, getFormat());
		a_Writer.putTag(ModelDescription.node_codeP011, getCodeP011());
		a_Writer.putTag(ModelDescription.node_nameP011, getNameP011());
		a_Writer.putTag(ModelDescription.node_P011, isP011());
		a_Writer.putTag(ModelDescription.node_instrument, getInstrument());
		a_Writer.putTag(ModelDescription.node_instrument_code, getInstrumentCode());
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Parameter copy = null;
		copy = (Parameter) super.clone();
		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return _code;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void release() {
		// aucun lien à supprimer
	}

	/**
	 * Getter pour _code.
	 * 
	 * @return String
	 * @see Parameter.java#_code
	 */
	public String getCode() {
		return _code;
	}

	/**
	 * Setter pour _code
	 * 
	 * @param x
	 *            : nouvelle valeur pour _code
	 * @see Fragment.java#_code
	 */
	public void setCode(String x) {
		_code = x;
	}

	/**
	 * Getter pour _codeP011.
	 * 
	 * @return String
	 * @see Parameter.java#_codeP011
	 */
	public String getCodeP011() {
		return _codeP011;
	}

	/**
	 * Getter for _indexP011.
	 * @return Integer
	 * @see Parameter.java#_indexP011
	 */
	public Integer getIndexP011() {
		return _indexP011;
	}

	/**
	 * Gets the P011 with the index at the end.
	 * @return String
	 */
	public String getIndexedP011() {

		if (_indexP011 == null || _indexP011 == 1) {
			return _codeP011;
		}
		
		return _codeP011 + "_" + _indexP011;
	}

	public String getLocalCode() {
		String localCode = null;
		DataListTypeEnum dataListType = Nemo.getInstance().getDomain().getData().getModel().getDataListType();
		E_TypeConversion conv = Nemo.getInstance().getDomain().getConversion().getModel().getOutputFormat();

		// **************************************
		// MEDATLAS
		// **************************************
		if (conv == E_TypeConversion.MEDATLAS) {
			if (dataListType == DataListTypeEnum.P011_VIA_P091
					|| dataListType == DataListTypeEnum.P091) {
				localCode = getCode();
			}
		}
		// **************************************
		// ODV
		// **************************************
		else {
			if (dataListType == DataListTypeEnum.P091) {
				localCode = getCode();
			} else {
				String userLabel = LabelParameterManager.getInstance().getUserLabel(getCodeP011());
				if (userLabel != null) {
					localCode = userLabel;
				} else {
					localCode = getCodeP011();
				}
			}
		}
		return localCode;
	}

	/**
	 * Setter pour _nameP011
	 * 
	 * @param x
	 *            : nouvelle valeur pour _nameP011
	 * @see Fragment.java#_nameP011
	 */
	public void setNameP011(String x) {
		_nameP011 = x;
	}

	/**
	 * Getter pour _nameP011.
	 * 
	 * @return String
	 * @see Parameter.java#_nameP011
	 */
	public String getNameP011() {
		return _nameP011;
	}

	/**
	 * Setter pour _code
	 * 
	 * @param x
	 *            : nouvelle valeur pour _code
	 * @see Fragment.java#_code
	 */
	public void setCodeP011(String x) {
		_codeP011 = x;
	}
	
	/**
	 * Setter for _indexP011.
	 * @param index an Integer representing the index.
	 * @see Parameter.java#_indexP011
	 */
	public void setIndexP011(Integer index) {
		_indexP011 = index;
	}

	/**
	 * Getter pour _P011.
	 * 
	 * @return String
	 * @see Parameter.java#_P011
	 */
	public boolean isP011() {
		return _P011;
	}

	/**
	 * Setter pour _P011
	 * 
	 * @param x
	 *            : nouvelle valeur pour _P011
	 * @see Fragment.java#_P011
	 */
	public void setP011(String x) {
		_P011 = Boolean.parseBoolean(x);
	}

	/**
	 * Setter pour _P011
	 * 
	 * @param x
	 *            : nouvelle valeur pour _P011
	 * @see Fragment.java#_P011
	 */
	public void setP011(boolean x) {
		_P011 = x;
	}

	/**
	 * Getter pour _default.
	 * 
	 * @return String
	 * @see Parameter.java#_default
	 */
	public String getDefault() {
		return _default;
	}

	/**
	 * Setter pour _default
	 * 
	 * @param x
	 *            : nouvelle valeur pour _default
	 * @see Fragment.java#_default
	 */
	public void setDefault(String x) {
		_default = x;
	}

	/**
	 * Getter pour _format.
	 * 
	 * @return String
	 * @see Parameter.java#_format
	 */
	public String getFormat() {
		return _format;
	}

	/**
	 * Setter pour _format
	 * 
	 * @param x
	 *            : nouvelle valeur pour _format
	 * @see Fragment.java#_format
	 */
	public void setFormat(String x) {
		_format = x;
	}

	/**
	 * Getter sur _standardName.
	 * 
	 * @return the standardName
	 */
	public String getStandardName() {
		String standardName = "";
		String userStandardName = StandardNameParameterManager.getInstance()
				.getUserStandardName(getCodeP011());
		if (userStandardName != null) {
			standardName = userStandardName;
		}
		return standardName;
	}

	/**
	 * Getter pour _fullName.
	 * 
	 * @return String
	 * @see Parameter.java#_fullName
	 */
	public String getFullName() {
		return _fullName;
	}

	/**
	 * Setter pour _fullName
	 * 
	 * @param x
	 *            : nouvelle valeur pour _fullName
	 * @see Fragment.java#_fullName
	 */
	public void setFullName(String x) {
		_fullName = x;
	}

	/**
	 * Getter pour _unit.
	 * 
	 * @return String
	 * @see Parameter.java#_unit
	 */
	public String getUnit() {
		return _unit;
	}

	/**
	 * Setter pour _unit
	 * 
	 * @param x
	 *            : nouvelle valeur pour _unit
	 * @see Fragment.java#_unit
	 */
	public void setUnit(String x) {
		_unit = x;
	}

	@Deprecated //24408: use INDEXED_TEXT instead of TEXT:size
	public String getOutputCode(Conversion c, boolean lastParam, Integer parameterSize, Boolean isText) {
		String code = null;
		switch (c) {
		case ODV:
			code = getOutputOdvCode(lastParam, parameterSize, isText);
			break;
		case MEDATLAS:
			code = getOutputMedatlasCode();
			break;
		}
		return code;
	}
	public String getOutputCode(Conversion c, boolean lastParam,  Boolean isText) {
		String code = null;
		switch (c) {
		case ODV:
			code = getOutputOdvCode(lastParam,  isText);
			break;
		case MEDATLAS:
			code = getOutputMedatlasCode();
			break;
		}
		return code;
	}

	private String getOutputMedatlasCode() {
		int len = _default.length() + 1;
		if (len <= _code.length()) {
			len = _code.length() + 1;
		}
		return Formatter.getSingleton().padString(_code, len);
	}
	
	@Deprecated
	private String getOutputOdvCode(boolean lastParam, Integer parameterSize,
			Boolean isText) {
		StringBuffer paramHeader = new StringBuffer();
		String code;
		// on ne fabrique le code qu'à un seul endroit comme ca on sera sur que
		// c'est le meme!!!!
		code = getLocalCode();

		if (!isText) {
			paramHeader.append(code);
			paramHeader.append(Global.SPACE);
			paramHeader.append('[');
			paramHeader.append(_unit);
			paramHeader.append(']');

		} else {
			// FAE 10755
			paramHeader.append(code);
			paramHeader.append(Global.DBL_PT);
			paramHeader.append(Intern.TEXT);
			paramHeader.append(Global.DBL_PT);
			if (parameterSize != null && parameterSize >= 2)
				paramHeader.append((parameterSize + 1));
			else
				paramHeader.append(2);
		}

		paramHeader.append(Global.TAB);
		paramHeader.append("QV:SEADATANET"); //$NON-NLS-1$

		if (!lastParam)
			paramHeader.append(Global.TAB);

		return paramHeader.toString();
	}
	private String getOutputOdvCode(boolean lastParam, Boolean isText) {
		StringBuffer paramHeader = new StringBuffer();
		String code;
		// on ne fabrique le code qu'à un seul endroit comme ca on sera sur que
		// c'est le meme!!!!
		code = getLocalCode();

		if (!isText) {
			paramHeader.append(code);
			paramHeader.append(Global.SPACE);
			paramHeader.append('[');
			paramHeader.append(_unit);
			paramHeader.append(']');

		} else {
			// FAE 10755
			paramHeader.append(code);
			paramHeader.append(Global.DBL_PT);
			paramHeader.append(Intern.INDEXED_TEXT);
		}

		paramHeader.append(Global.TAB);
		paramHeader.append("QV:SEADATANET"); //$NON-NLS-1$

		if (!lastParam)
			paramHeader.append(Global.TAB);

		return paramHeader.toString();
	}

	public String getUnitCode() {
		return _unitCode;
	}

	public void setUnitCode(String unitCode) {
		_unitCode = unitCode;
	}
	public String getInstrument() {
		return _instrument;
	}

	public void setInstrument(String instrument) {
		this._instrument = instrument;
	}



	public void setP06Label(String p06Label) {
		_p06Label = p06Label;
	}


}