package fr.ifremer.nemo.input;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.ParseException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.Element2Field;
import com.ago.xml.PathElementConsumer;
import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.edit.station.AutoLine;
import fr.ifremer.nemo.ui.edit.station.StationSection;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.format.DateTimeFormat;
import fr.ifremer.nemo.util.format.ExternalizableFormat;
import fr.ifremer.nemo.util.format.FloatFormat;
import fr.ifremer.nemo.util.format.IntegerFormat;
import fr.ifremer.nemo.util.format.LatitudeFormat;
import fr.ifremer.nemo.util.format.LongitudeFormat;

/**
 * Class PropertyModel
 * Description : Modélisation d'une Propriété de Station
 * @author Gerard BUNEL
 */
public class PropertyModel extends AbstractModel {
	/**
	 * Nom de la propriete
	 */
	protected String _property;

	/**
	 * Booleen precisant si la valeur de la propriete est issue du fichier
	 * d'entree (false) ou fournie par l'utilisateur (true). La valeur saisie
	 * par l'utilisateur est stoquee dans la propriete du bean associe
	 */
	protected boolean _userProvided;

	/**
	 * Represente le fragment du fichier d'entree où l'information peut etre
	 * recuperee
	 */
	protected Fragment _fragment;

	/**
	 * valeur saisie par l'utilisateur
	 */
	protected String _userValue;

	/**
	 * Represente le fragment du fichier d'entree où le flag qualite peut etre
	 * recupere
	 */
	protected Fragment _flag;

	/**
	 * Expression mathematique permettant de convertir la valeur du parametre.
	 * La variable d'entree est nommee "x" dans l'expression.Ex: 3*x+2. Les
	 * constantes "pi" et "e" sont reconnues ainsi que l'ensemble des fonctions
	 * mathematiques de base (cos, sin, log, etc...)
	 */
	protected String _conversionFormula;

	/**
	 * format à appliquer à la valeur de cette propriété
	 */
	protected ExternalizableFormat _format;

	/**
	 * boolean précisant si cette propriété est, ou non, existante
	 */
	protected boolean _toCompute = false;

	// variable présente dans les données (et non dans l'entête)
	protected boolean _inData;

	public PropertyModel() {
		_tagName = ModelDescription.node_propertyModel;
		initValues();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initValues() {
		_property = Global.EMPTY_STRING;
		_userProvided = false;
		_userValue = Global.EMPTY_STRING;
		_conversionFormula = null;
		_format = null;
		_fragment = null;
		_flag = null;
		_inData = false;
	}

	/**
	 * @see AbstractModel.java#fromElement
	 */
	public static SAXConsumer fromElement(String a_URI,
											String a_LocalName,
											String a_QName,
											Attributes a_Attributes) {
		PropertyModel el = new PropertyModel();
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
		String tmp;
		if ((tmp = a_Attributes.getValue(ModelDescription.a_property)) != null) {
			setProperty(tmp);
		}
		if ((tmp = a_Attributes.getValue(ModelDescription.a_userprovided)) != null) {
			setUserProvided(tmp);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SAXConsumer consumeElement(String a_URI,
										String a_LocalName,
										String a_QName,
										Attributes a_Attributes) {

		SAXConsumer el = null;
		if (a_LocalName.equals(ModelDescription.node_formula)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_formula);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_uservalue)) {
			Element2Field setter = new Element2Field(this, ModelDescription.setter_uservalue);
			return setter;
		}
		if (a_LocalName.equals(ModelDescription.node_fragment)) {
			el = Fragment.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			setFragment((Fragment) el);
			return el;
		}
		if (a_LocalName.equals(ModelDescription.node_flag)) {
			el = Fragment.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			setFlag((Fragment) el);
			return el;
		}
		if (a_LocalName.equals(ModelDescription.node_format)) {
			return new PathElementConsumer(ModelDescription.node_format, this);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SAXConsumer handlePathElement(String path, String a_URI,
			String a_LocalName, String a_QName, Attributes a_Attributes)
			throws SAXException {
		SAXConsumer el = null;
		if (path.equals(ModelDescription.node_format)) {
			el = preferredFormatXMLElement(a_URI, a_LocalName, a_QName, a_Attributes);
			setFormat((ExternalizableFormat) el);
			return el;
		}
		return null;
	}

	public SAXConsumer preferredFormatXMLElement(String a_URI,
													String a_LocalName,
													String a_QName,
													Attributes a_Attributes) throws SAXException {
		try {
			if (a_LocalName.equals(ModelDescription.node_fDate)) {
				return DateTimeFormat.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			} else if (a_LocalName.equals(ModelDescription.node_fLatitude)) {
				return LatitudeFormat.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			} else if (a_LocalName.equals(ModelDescription.node_fLongitude)) {
				return LongitudeFormat.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			} else if (a_LocalName.equals(ModelDescription.node_fInteger)) {
				return IntegerFormat.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			} else if (a_LocalName.equals(ModelDescription.node_fFloat)) {
				return FloatFormat.fromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			}
			throw new SAXException(Messages.error_unknown_format);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SAXException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		a_Writer.putAttribute(ModelDescription.a_property, getProperty());
		a_Writer.putAttribute(ModelDescription.a_userprovided, isUserProvided());
		if (getFormat() != null) {
			a_Writer.openTag(ModelDescription.node_format);
			getFormat().writeXMLOn(a_Writer);
			a_Writer.closeTag();
		}
		if (getConversionFormula() != null) {
			a_Writer.putTag(ModelDescription.node_formula, getConversionFormula());
		}
		if (isUserProvided()) {
			a_Writer.putTag(ModelDescription.node_uservalue, getUserValue());
		} else if (getFragment() != null) {
			getFragment().setTagName(ModelDescription.node_fragment);
			getFragment().writeXMLOn(a_Writer);
		}
		if (getFlag() != null) {
			getFlag().setTagName(ModelDescription.node_flag);
			getFlag().writeXMLOn(a_Writer);
		}
	}

	/**
	 * Valide la saisie
	 */
	public boolean validate() {
		boolean ok = false;
		// TODO
		return ok;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		PropertyModel copy = null;
		copy = (PropertyModel) super.clone();
		copy._fragment = null;
		if (getFragment() != null) {
			copy.setFragment((Fragment) getFragment().clone());
		}
		copy._flag = null;
		if (getFlag() != null) {
			copy.setFlag((Fragment) getFlag().clone());
		}
		copy._format = null;
		if (getFormat() != null) {
			copy.setFormat((ExternalizableFormat) getFormat().clone());
		}
		return copy;
	}

	public void copyFragment(PropertyModel initiale) {
		// memes valeurs de ligne/debut/fin que initial
		Fragment frag = initiale.getFragment();

		_userProvided = false;
		_fragment.setFirstLine(frag.getFirstLine());
		_fragment.setLastLine(frag.getLastLine());
		_fragment.setFirstCharOffset(frag.getFirstCharOffset());
		_fragment.setLastCharOffset(frag.getLastCharOffset());
		_fragment.setHasDefaultValue(frag.isDefaultValue());
		_fragment.setDefaultValueSelected(frag.isDefaultValueSelected());
		_fragment.setDefaultValue(frag.getDefaultValue());
	}

	/**
	 * @param a_Destination
	 * @param a_Header
	 * @exception java.text.ParseException
	 */
	public Object applyOn(String[] a_Header) throws ParseException {

		Object result = null;
		String sResult = Global.EMPTY_STRING;

		if (isUserProvided()) {
			sResult = _userValue;
			if (_format != null) {
				if (!sResult.equals(Global.EMPTY_STRING))
					result = _format.parseObject(sResult);
				else
					result = Float.NaN;
			} else {
				result = sResult;
			}
		} else {
			// FAE 7239
			if (isInData()) {
				sResult = _fragment.retreiveInLine(a_Header[a_Header.length - 1]);
			} else {
				sResult = _fragment.retreive(a_Header);
			}
			if (_fragment.isDefaultValue())
				if (_fragment.isDefaultValueSelected())
					if (sResult.trim().equals(_fragment.getDefaultValue().trim()))
						return "Default";

			if (!sResult.trim().equals(Global.EMPTY_STRING) && _format != null) {
				try {
					if (!(_format instanceof LatitudeFormat)
							&& !(_format instanceof LongitudeFormat)
							&& !(_format instanceof DateTimeFormat)) {
						if (_fragment.isVariable())
							// stop string at next space
							sResult = _fragment.retreiveVariable(a_Header);
					}
					if (_format instanceof DateTimeFormat) {
						sResult = sResult.replace(' ', '0');
					}
					result = _format.parseObject(sResult);
				} catch (ParseException e) {
					if (_format instanceof LatitudeFormat)
						throw new ParseException(Messages.PropertyModel_1 + sResult + " " + _format.getErrorMessage(), 0);
					else if (_format instanceof LongitudeFormat)
						throw new ParseException(Messages.PropertyModel_2 + sResult + " " + _format.getErrorMessage(), 0);
					else if (_format instanceof DateTimeFormat)
						throw new ParseException(Messages.PropertyModel_3 + sResult + " " + _format.getErrorMessage(), 0);
				}
			} else {
				result = sResult;
			}
		}

		return result;
	}

	/**
	 * Utilisé dans le cas trajectory pour long lat et prof
	 * @param a_Destination
	 * @param a_Header
	 * @exception java.text.ParseException
	 */
	public Object applyOnLine(String a_Line) throws ParseException {

		Object result = null;
		String sResult = Global.EMPTY_STRING;

		sResult = _fragment.retreiveInLine(a_Line);

		if (_fragment.isDefaultValue())
			if (sResult.trim().equals(_fragment.getDefaultValue().trim()))
				return "Default";

		if (!sResult.trim().equals(Global.EMPTY_STRING) && _format != null) {
			try {
				if (!(_format instanceof LatitudeFormat) && !(_format instanceof LongitudeFormat) && !(_format instanceof DateTimeFormat)) {
					if (_fragment.isVariable())
						// stop string at next space
						sResult = _fragment.retreiveVariableInLine(a_Line);
				}
				if (_format instanceof DateTimeFormat)
					sResult = sResult.replace(' ', '0');
				result = _format.parseObject(sResult);
			} catch (ParseException e) {
				if (_format instanceof LatitudeFormat)
					throw new ParseException(Messages.PropertyModel_1 + sResult, 0);
				else if (_format instanceof LongitudeFormat)
					throw new ParseException(Messages.PropertyModel_2 + sResult, 0);
				else if (_format instanceof DateTimeFormat)
					throw new ParseException(Messages.PropertyModel_3 + sResult, 0);
			}
		} else {
			result = sResult;
		}
		// }
		return result;
	}

	/**
	 * @param a_Source
	 * @return Method
	 * @exception IntrospectionException
	 */
	public Method computeReadAccessor(Object a_Source) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(a_Source.getClass());
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		PropertyDescriptor desc = null;
		for (int i = 0; i < descriptors.length; i++) {
			if (descriptors[i].getName().equals(_property)) {
				desc = descriptors[i];
				break;
			}
		}
		if (desc != null) {
			return desc.getReadMethod();
		}
		return null;
	}

	/**
	 * @param a_Destination
	 * @return Method
	 * @exception IntrospectionException
	 */
	public Method computeWriteAccessor(Object a_Destination) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(a_Destination.getClass());
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		PropertyDescriptor desc = null;
		for (int i = 0; i < descriptors.length; i++) {
			if (descriptors[i].getName().equals(_property)) {
				desc = descriptors[i];
				break;
			}
		}
		if (desc != null) {
			return desc.getWriteMethod();
		}
		return null;
	}

	public Fragment fillFragment(StationSection stationSection) {
		return fillFragment(stationSection, null, 0, true, false);
	}

	public Fragment fillFragment(StationSection stationSection, String ligne) {
		return fillFragment(stationSection, ligne, 0, true, false);
	}

	public Fragment fillFragment(StationSection stationSection, int i) {
		return fillFragment(stationSection, null, i, true, false);
	}

	public Fragment fillFragment(StationSection stationSection, boolean lastLine, boolean posVariable) {
		return fillFragment(stationSection, null, 0, lastLine, posVariable);
	}

	public Fragment fillFragment(StationSection stationSection, String ligne, int i, boolean lastLine, boolean posVariable, boolean isDefaultValueEnable, boolean isDefaultValueSelected, String defaultValue) {

		Fragment frag = fillFragment(stationSection, ligne, i, lastLine, posVariable);

		frag.setHasDefaultValue(isDefaultValueEnable);
		frag.setDefaultValue(defaultValue);
		frag.setDefaultValueSelected(isDefaultValueSelected);

		return frag;
	}

	public Fragment fillFragment(StationSection stationSection, String ligne, int i, boolean lastLine, boolean posVariable) {

		// position dans fichier
		_userProvided = false;
		AutoLine line = stationSection.getAutoInput().getLines().get(i);

		if (ligne == null)
			ligne = String.valueOf(((Integer.valueOf(line.getNumberLine().getText())) - Nemo.getInstance().getDomain().getMedia().getModel().getFileHeader()));

		/*
		 * if (ligne == null)
		 * ligne =
		 * String.valueOf(((Integer
		 * .valueOf(line.getNumberLine().getText())) - Integer
		 * .valueOf(ViewMain.editFileForm.sectionEnteteFichier
		 * .getValue())));
		 */
		String debut = line.getStart().getText();
		String fin = line.getEnd().getText();

		if (_fragment == null)
			_fragment = new Fragment();

		_fragment.setFirstLine(ligne);
		_fragment.setFirstCharOffset(debut);
		_fragment.setLastCharOffset(fin);

		if (lastLine) {
			_fragment.setLastLine(ligne);
		}
		if (posVariable) {
			_fragment.setVariable(stationSection.isVariablePos());
		}

		return _fragment;
	}

	public void fillUserValue(String value) {
		_userProvided = true;
		_userValue = value;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void release() {
		_format = null;
		releaseFragment();
		releaseFlag();
	}

	/**
	 * Supprime _fragment
	 */
	public void releaseFragment() {
		if (_fragment != null)
			_fragment.release();
		_fragment = null;
	}

	/**
	 * Supprime _flag
	 */
	public void releaseFlag() {
		if (_flag != null)
			_flag.release();
		_flag = null;
	}

	/**
	 * Getter pour _property.
	 * 
	 * @return String
	 * @see PropertyModel.java#_property
	 */
	public String getProperty() {
		return _property;
	}

	/**
	 * Setter pour _property
	 * 
	 * @param x
	 *            : nouvelle valeur pour _property
	 * @see PropertyModel.java#_property
	 */
	public void setProperty(String x) {
		_property = x;
		if (x.equals(Intern.S_OFFSETTU) || x.equals(Intern.S_MAGNETIC)) {
			_format = new FloatFormat("%4.1f"); //$NON-NLS-1$
		} else if (x.equals(Intern.S_DEPTH) || x.equals(Intern.S_EDEPTH) || x.equals(Intern.S_SENSORDEPTH) || x.equals(Intern.S_SAMPLINGRATE)) {
			_format = new FloatFormat("%4.0f"); //$NON-NLS-1$
		}

	}

	/**
	 * Getter pour _userProvided.
	 * 
	 * @return boolean
	 * @see PropertyModel.java#_userProvided
	 */
	public boolean isUserProvided() {
		return _userProvided;
	}

	/**
	 * Setter pour _userProvided
	 * 
	 * @param x
	 *            : nouvelle valeur pour _userProvided
	 * @see PropertyModel.java#_userProvided
	 */
	public void setUserProvided(boolean x) {
		_userProvided = x;
	}

	/**
	 * Setter pour _userProvided
	 * 
	 * @param s
	 *            : nouvelle valeur pour _userProvided sous forme de String
	 * @see PropertyModel.java#_userProvided
	 */
	public void setUserProvided(String s) {
		setUserProvided(new Boolean(s).booleanValue());
	}

	/**
	 * Getter pour _fragment.
	 * 
	 * @return Fragment
	 * @see PropertyModel.java#_fragment
	 */
	public Fragment getFragment() {
		return _fragment;
	}

	/**
	 * Setter pour _fragment
	 * 
	 * @param x
	 *            : nouvelle valeur pour _fragment
	 * @see PropertyModel.java#_fragment
	 */
	public void setFragment(Fragment x) {
		if (_fragment == x)
			return;
		_fragment = x;
	}

	/**
	 * Getter pour _flag.
	 * 
	 * @return Fragment
	 * @see PropertyModel.java#_flag
	 */
	public Fragment getFlag() {
		return _flag;
	}

	/**
	 * Setter pour _flag
	 * 
	 * @param x
	 *            : nouvelle valeur pour _flag
	 * @see PropertyModel.java#_flag
	 */
	public void setFlag(Fragment x) {
		if (_flag == x)
			return;
		_flag = x;
	}

	/**
	 * Getter pour _format.
	 * 
	 * @return ExternalizableFormat
	 * @see PropertyModel.java#_format
	 */
	public ExternalizableFormat getFormat() {
		return _format;
	}

	/**
	 * Setter pour _format
	 * 
	 * @param x
	 *            : nouvelle valeur pour _format
	 * @see PropertyModel.java#_format
	 */
	public void setFormat(ExternalizableFormat x) {
		if (_format == x)
			return;
		_format = x;
	}

	/**
	 * Getter pour _userValue.
	 * 
	 * @return String
	 * @see PropertyModel.java#_userValue
	 */
	public String getUserValue() {
		return _userValue;
	}

	/**
	 * Setter pour _userValue
	 * 
	 * @param x
	 *            : nouvelle valeur pour _userValue
	 * @see PropertyModel.java#_userValue
	 */
	public void setUserValue(String x) {
		_userValue = x;
	}

	/**
	 * Getter pour _conversionFormula.
	 * 
	 * @return String
	 * @see PropertyModel.java#_conversionFormula
	 */
	public String getConversionFormula() {
		return _conversionFormula;
	}

	/**
	 * Setter pour _conversionFormula
	 * 
	 * @param x
	 *            : nouvelle valeur pour _conversionFormula
	 * @see PropertyModel.java#_conversionFormula
	 */
	public void setConversionFormula(String x) {
		_conversionFormula = x;
	}

	/**
	 * Getter pour _inData.
	 * 
	 * @return boolean
	 * @see PropertyModel.java#_inData
	 */
	public boolean isInData() {
		return _inData;
	}

	/**
	 * Setter pour _inData
	 * 
	 * @param inData
	 *            : nouvelle valeur pour inData
	 * @see PropertyModel.java#_inData
	 */
	public void setInData(boolean inData) {
		_inData = inData;
	}

	/**
	 * Getter pour _toCompute.
	 * 
	 * @return boolean
	 * @see PropertyModel.java#_toCompute
	 */
	public boolean isToCompute() {
		return _toCompute;
	}

	/**
	 * Setter pour _toCompute
	 * 
	 * @param toCompute
	 *            : nouvelle valeur pour toCompute
	 * @see PropertyModel.java#_toCompute
	 */
	public void setToCompute(boolean toCompute) {
		_toCompute = toCompute;
	}
}