package fr.ifremer.nemo.util.format;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.Fragment;
import fr.ifremer.nemo.input.ModelDescription;
import fr.ifremer.nemo.ui.edit.station.EditStationForm;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

/**
 * Class LatitudeFormat
 * 
 * 
 * @author Gerard BUNEL
 */
public class LatitudeFormat extends ExternalizableFormat implements Cloneable
{

	private static final long serialVersionUID = 442022117102694776L;

	protected ParseElement[] _elements;

	protected String _formatString;

	protected boolean _variableLength = false;

	public LatitudeFormat()
	{
		super();
		_formatString = Global.EMPTY_STRING;
	}

	/**
	 * 
	 * @param a_Format
	 * @exception ParseException
	 * @throws UnsupportedEncodingException
	 */
	public LatitudeFormat(String a_Format) throws ParseException, UnsupportedEncodingException
	{
		this();
		setFormatString(a_Format);
	}

	/**
	 * 
	 * @param a_Format
	 * @exception ParseException
	 * @throws UnsupportedEncodingException
	 */
	public LatitudeFormat(String a_Format, boolean a_VariableLength) throws ParseException, UnsupportedEncodingException
	{
		this();
		setFormatString(a_Format);
		setVariableLength(a_VariableLength);
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
	@Override
	public SAXConsumer consumeElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes)
	{
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
	@Override
	public void consumeCharacters(String a_Characters)
	{
	}

	/**
	 * 
	 * Generate XML defintion on a Stream
	 * 
	 * @param a_Writer
	 *            XMLOutputWriter to which to write the XML Definition
	 * 
	 */
	public void writeXMLBodyOn(XMLOutputWriter a_Writer)
	{
		a_Writer.putAttribute(ModelDescription.a_string, getFormatString());
		if (_variableLength)
		{
			a_Writer.putAttribute(ModelDescription.a_vari, _variableLength);
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
	@Override
	public void writeXMLOn(XMLOutputWriter a_Writer)
	{
		a_Writer.openTag(ModelDescription.node_fLatitude);
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
	public void writeXMLOn(FileOutputStream a_Stream)
	{
		XMLOutputWriter writer = new XMLOutputWriter(a_Stream);
		writeXMLOn(writer);
	}

	/**
	 * Read accessor (getter) for attribute _formatString
	 * 
	 * @return String
	 * @see LatitudeFormat#_formatString
	 */
	public String getFormatString()
	{
		return _formatString;
	}

	/**
	 * 
	 * @param a_URI
	 * @param a_LocalName
	 * @param a_QName
	 * @param a_Attributes
	 * @exception SAXException
	 */
	public void buildFromElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) throws SAXException
	{
		try
		{
			String tmp;
			if ((tmp = a_Attributes.getValue(ModelDescription.a_string)) != null)
			{
				setFormatString(tmp);
			}
			if ((tmp = a_Attributes.getValue(ModelDescription.a_vari)) != null)
			{
				setVariableLength(tmp);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new SAXException(e);
		}
	}

	/**
	 * 
	 * @return Object
	 */
	@Override
	public Object clone()
	{
		try
		{
			return new LatitudeFormat(new String(_formatString), _variableLength);
		} catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * 
	 * @param obj
	 * @param toAppendTo
	 * @param pos
	 * @return StringBuffer
	 */
	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
	{
		double value = ((Double) obj).doubleValue();
		java.util.Locale locale = java.util.Locale.US;
		java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(locale);
		int size = 0;
		for (int i = 0; i < _elements.length; i++)
		{
			size = _elements[i]._element.length();
			switch (_elements[i]._type) {
			case HEMISPHERE:
			{
				if (value >= 0.0)
				{
					toAppendTo.append('N');
				} else
				{
					toAppendTo.append('S');
				}
				break;
			}
			case SIGN:
			{
				if (value >= 0.0)
				{
					toAppendTo.append('+');
				} else
				{
					toAppendTo.append('-');
				}
				break;
			}
			case CHARS:
			{
				toAppendTo.append(_elements[i]._element);
				break;
			}
			case DEGREES:
			{
				double val = Math.abs(value);
				double degrees = Math.floor(val);
				DecimalFormat format = new DecimalFormat(_elements[i]._element, symbols);
				toAppendTo.append(paddedZeros(format.format(degrees), size));
				break;
			}
			case DEGREES_DECIMAL:
			{
				double val = Math.abs(value);
				DecimalFormat format = new DecimalFormat(_elements[i]._element, symbols);
				toAppendTo.append(padded(format.format(val), size));
				break;
			}
			case MINUTES:
			{
				double val = Math.abs(value);
				double degrees = Math.floor(val);
				double minutes = (val - degrees) * 60.0D;
				double mins = Math.floor(minutes);
				DecimalFormat format = new DecimalFormat(_elements[i]._element, symbols);
				toAppendTo.append(padded(format.format(mins), size));
				break;
			}
			case MINUTES_DECIMAL:
			{
				double val = Math.abs(value);
				double degrees = Math.floor(val);
				double minutes = (val - degrees) * 60.0D;
				DecimalFormat format = new DecimalFormat(_elements[i]._element, symbols);
				toAppendTo.append(padded(format.format(minutes), size));
				break;
			}
			case SECONDS:
			{
				double val = Math.abs(value);
				double degrees = Math.floor(val);
				double minutes = (val - degrees) * 60.0D;
				double mins = Math.floor(minutes);
				double seconds = (minutes - mins) * 60.0D;
				DecimalFormat format = new DecimalFormat(_elements[i]._element, symbols);
				toAppendTo.append(padded(format.format(seconds), size));
				break;
			}
			default:
			{
				break;
			}
			}
		}
		return toAppendTo;
	}

	/**
	 * 
	 * @param a_URI
	 * @param a_LocalName
	 * @param a_QName
	 * @param a_Attributes
	 * @return SAXConsumer
	 * @exception SAXException
	 */
	public static SAXConsumer fromElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) throws SAXException
	{
		try
		{
			LatitudeFormat tmp = new LatitudeFormat();
			tmp.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			return tmp;
		} catch (Exception e)
		{
			throw new SAXException(e);
		}
	}

	/**
	 * 
	 * @param args
	 */
	@SuppressWarnings("nls")
	public static void main(String[] args)
	{
		try
		{
			LatitudeFormat format = new LatitudeFormat(args[0]);
			double value = ((Number) format.parseObject(args[1])).doubleValue();
			System.out.println(value);
			System.out.println("out: " + format.format(value));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param a_String
	 * @param a_Size
	 * @return String
	 */
	public String padded(String a_String, int a_Size)
	{
		StringBuffer buf = new StringBuffer();
		int index = a_String.length();
		while (index < a_Size)
		{
			buf.append(' ');
			index++;
		}
		buf.append(a_String);
		return buf.toString();
	}

	/**
	 * 
	 * @param a_String
	 * @param a_Size
	 * @return String
	 */
	public String paddedZeros(String a_String, int a_Size)
	{
		StringBuffer buf = new StringBuffer();
		int index = a_String.length();
		while (index < a_Size)
		{
			buf.append('0');
			index++;
		}
		buf.append(a_String);
		return buf.toString();
	}

	/**
	 * 
	 * @param source
	 * @param pos
	 * @return Object
	 */
	@Override
	public Object parseObject(String source, ParsePosition pos)
	{

		Object obj = null;
		try
		{
			if (isFreeDecimalDegrees()){//40216
				pos.setIndex( source.length());
				return Double.valueOf(source);
			}
			else if (_variableLength)
			{
				obj = parseObjectVariableLength(source, pos);
			} else
			{
				obj = parseObjectFixedLength(source, pos);
			}
		} catch (ParseException e)
		{
			errorMessage = e.getMessage();
			return obj;
		}
		return obj;
	}

	public boolean isFreeDecimalDegrees(){//40216
		return _formatString.equals(EditStationForm.LATLON_CHOICE_DEC_DEGREES_FOR_FILES_WITH_SEP);
	}
	/**
	 * 
	 * @param source
	 * @param pos
	 * @return Object
	 */
	public Object parseObjectVariableLength(String source, ParsePosition pos)
	{
		double value = 0.0;
		double sign = 1.0;
		int index = pos.getIndex();
		java.util.Locale locale = java.util.Locale.US;
		java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(locale);
		try
		{
			for (int i = 0; i < _elements.length && index < source.length(); i++)
			{
				switch (_elements[i]._type) {
				case HEMISPHERE:
				{
					String tmp = source.substring(index, index + 1);
					if (tmp.equals(Global.NORD))
					{
						sign = 1.0;
					} else if (tmp.equals(Global.SUD))
					{
						sign = -1.0;
					} else
					{
						throw new ParseException(Intern.errorParseCoordN, index);
					}
					index += 1;
					break;
				}
				case SIGN:
				{
					String tmp = source.substring(index, index + 1);
					if (tmp.equals(Global.PLUS) || tmp.equals(Global.SPACE) || tmp.equals(Global.EMPTY_STRING))
					{
						sign = 1.0;
					} else if (tmp.equals(Global.MOINS))
					{
						sign = -1.0;
					} else
					{
						throw new ParseException(Intern.errorParseSign, index);
					}
					index += 1;
					break;
				}
				case CHARS:
				{
					String tmp = source.substring(index, index + _elements[i]._element.length());
//					if (_elements[i]._element.equals("#") && RulesManager.getInstance().isSeparatorChecked()){
//						try{
//						String separator=RulesManager.getInstance().getSeparator();
//						if (source.startsWith(separator)){
//							source = source.substring(source.indexOf(separator)+1, source.length());
//						}
//						pos.setIndex(index + source.split(separator)[0].length());
//						return Double.valueOf(source.split(separator)[0]);
//						}catch (Exception e) {
//							// TODO
//							throw new ParseException(String.format(Intern.errorParseChar, _elements[i]._element, tmp), index);
//						}
//					}
					if (_elements[i]._element.length() == 1 && _elements[i]._element.equals(Global.SPACE))
					{
						// cas particulier si on rencontre un espace, n'importe
						// quel caractere est possible
					} else
					{
						if (!tmp.equals(_elements[i]._element))
						{
							throw new ParseException(String.format(Intern.errorParseChar, _elements[i]._element, tmp), index);
						}
					}
					index += _elements[i]._element.length();
					break;
				}
				case DEGREES:
				{
					// 7369 ajout trim
					String tmp = source.trim();// .replace(' ', '0');
					DecimalFormat format = new DecimalFormat(_elements[i]._element);
					value += ((Number) format.parseObject(tmp, pos)).doubleValue();
					index = pos.getIndex();
					break;
				}
				case DEGREES_DECIMAL:
				{
					String tmp = source;// .replace(' ', '0');
					DecimalFormat format = new DecimalFormat(_elements[i]._element, symbols);
					value += ((Number) format.parseObject(tmp, pos)).doubleValue();
					index = pos.getIndex();
					break;
				}
				case MINUTES:
				{
					String tmp = source;// .replace(' ', '0');
					DecimalFormat format = new DecimalFormat(_elements[i]._element);
					value += ((Number) format.parseObject(tmp, pos)).doubleValue() / 60.0;
					index = pos.getIndex();
					break;
				}
				case MINUTES_DECIMAL:
				{
					String tmp = source;// .replace(' ', '0');
					DecimalFormat format = new DecimalFormat(_elements[i]._element, symbols);
					value += ((Number) format.parseObject(tmp, pos)).doubleValue() / 60.0;
					index = pos.getIndex();
					break;
				}
				case SECONDS:
				{
					String tmp = source;// .replace(' ', '0');
					DecimalFormat format = new DecimalFormat(_elements[i]._element);
					value += ((Number) format.parseObject(tmp, pos)).doubleValue() / 3600.0;
					index = pos.getIndex();
					break;
				}
				default:
				{
					break;
				}
				}
				pos.setIndex(index);
			}
		} catch (Exception e)
		{
			// e.printStackTrace();
			System.out.println("exception " + e.getMessage());
			return null;
		}
		value *= sign;
		return new Double(value);
	}

	protected String replaceSpacesByZeros(String source)
	{
		String tmp1 = source.replace(' ', '0');
		if (tmp1.startsWith("0"))
		{
			if (tmp1.contains("+"))
			{
				tmp1 = tmp1.replaceFirst("\\+", "0");
				tmp1 = tmp1.replaceFirst("0", "\\+");
				source = tmp1;
			}
			if (tmp1.contains("-"))
			{
				tmp1 = tmp1.replaceFirst("\\-", "0");
				tmp1 = tmp1.replaceFirst("0", "\\-");
				source = tmp1;
			}
		}
		return source;
	}

	/**
	 * 
	 * @param source
	 * @param pos
	 * @return Object
	 * @throws ParseException
	 */
	public Object parseObjectFixedLength(String source, ParsePosition pos) throws ParseException
	{
		double value = 0.0;
		double sign = 1.0;
		int index = pos.getIndex();
		java.util.Locale locale = java.util.Locale.US;
		java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(locale);


		source = replaceSpacesByZeros(source);
		for (int i = 0; i < _elements.length && index < source.length(); i++)
		{
			switch (_elements[i]._type) {
			case HEMISPHERE:
			{
				String tmp = source.substring(index, index + 1);
				if (tmp.equals(Global.NORD))
				{
					sign = 1.0;
				} else if (tmp.equals(Global.SUD))
				{
					sign = -1.0;
				} else
				{
					throw new ParseException(Intern.errorParseCoordN, index);
				}
				index += 1;
				break;
			}
			case SIGN:
			{
				String tmp = source.substring(index, index + 1);
				if (tmp.equals(Global.PLUS) || tmp.equals(Global.SPACE) || tmp.equals(Global.EMPTY_STRING))
				{
					sign = 1.0;
				} else if (tmp.equals(Global.MOINS))
				{
					sign = -1.0;
				} else
				{
					throw new ParseException(Intern.errorParseSign, index);
				}
				index += 1;
				break;
			}
			case CHARS:
			{
				String tmp = source.substring(index, index + _elements[i]._element.length());
				if (_elements[i]._element.length() == 1 && _elements[i]._element.equals(Global.SPACE))
				{
					// cas particulier si on rencontre un espace, n'importe
					// quel caractere est possible
				} else
				{
					if (!tmp.equals(_elements[i]._element))
					{
						throw new ParseException(MessageFormat.format(Messages.errorParseChar, _elements[i]._element, tmp), index);
					}
				}
				index += _elements[i]._element.length();
				break;
			}
			case DEGREES:
			{
				String tmp = source.substring(index, index + _elements[i]._element.length());
				if (tmp.matches("\\s*\\d+\\s*")) { //$NON-NLS-1$
					DecimalFormat format = new DecimalFormat(_elements[i]._element);
					value += ((Number) format.parseObject(tmp.trim())).doubleValue();
				} else
				{
					throw new ParseException(MessageFormat.format(Intern.errorParseDegres, _elements[i]._element), index);
				}
				index += _elements[i]._element.length();
				break;
			}
			case DEGREES_DECIMAL:
			{
				String tmp = source.substring(index, index + _elements[i]._element.length());
				if (tmp.matches("\\s*\\d+\\.\\d+\\s*")) { //$NON-NLS-1$
					DecimalFormat format = new DecimalFormat(_elements[i]._element, symbols);
					value += ((Number) format.parseObject(tmp.trim())).doubleValue();
				} else
				{
					throw new ParseException(MessageFormat.format(Intern.errorParseDegres, _elements[i]._element), index);
				}
				index += _elements[i]._element.length();
				break;
			}
			case DEGREES_DECIMAL_SANS_POINT:
			{
				int indexOfPoint = _elements[i]._element.indexOf(Global.POINT);
				String tmp = source.substring(index, index + _elements[i]._element.length() - 1);
				String tmpPlusPoint = tmp.substring(0, indexOfPoint) + Global.POINT + tmp.substring(indexOfPoint);
				if (tmpPlusPoint.matches("\\s*\\d+\\.\\d+\\s*")) { //$NON-NLS-1$
					DecimalFormat format = new DecimalFormat(_elements[i]._element, symbols);
					value += ((Number) format.parseObject(tmpPlusPoint.trim())).doubleValue();
				} else
				{
					throw new ParseException(MessageFormat.format(Intern.errorParseDegres, _elements[i]._element), index);
				}
				index += _elements[i]._element.length();
				break;
			}
			case MINUTES:
			{
				String tmp = source.substring(index, index + _elements[i]._element.length());
				if (tmp.matches("\\s*\\d+\\s*")) { //$NON-NLS-1$
					DecimalFormat format = new DecimalFormat(_elements[i]._element);
					value += ((Number) format.parseObject(tmp.trim())).doubleValue() / 60.0;
				} else
				{
					throw new ParseException(MessageFormat.format(Intern.errorParseMinutes, _elements[i]._element), index);
				}
				index += _elements[i]._element.length();
				break;
			}
			case MINUTES_DECIMAL:
			{
				String tmp = source.substring(index, index + _elements[i]._element.length());
				if (tmp.matches("\\s*\\d+\\.\\d+\\s*")) { //$NON-NLS-1$
					DecimalFormat format = new DecimalFormat(_elements[i]._element, symbols);
					value += ((Number) format.parseObject(tmp.trim())).doubleValue() / 60.0;
				} else
				{
					throw new ParseException(MessageFormat.format(Intern.errorParseMinutes, _elements[i]._element), index);
				}
				index += _elements[i]._element.length();
				break;
			}
			case MINUTES_DECIMAL_SANS_POINT:
			{
				int indexOfPoint = _elements[i]._element.indexOf(Global.POINT);
				String tmp = source.substring(index, index + _elements[i]._element.length() - 1);
				String tmpPlusPoint = tmp.substring(0, indexOfPoint) + Global.POINT + tmp.substring(indexOfPoint);
				if (tmpPlusPoint.matches("\\s*\\d+\\.\\d+\\s*")) { //$NON-NLS-1$
					DecimalFormat format = new DecimalFormat(_elements[i]._element, symbols);
					value += ((Number) format.parseObject(tmpPlusPoint.trim())).doubleValue() / 60.0;
				} else
				{
					throw new ParseException(MessageFormat.format(Intern.errorParseMinutes, _elements[i]._element), index);
				}
				index += _elements[i]._element.length() - 1;
				break;
			}
			case SECONDS:
			{
				String tmp = source.substring(index, index + _elements[i]._element.length());
				if (tmp.matches("\\s*\\d+\\s*")) { //$NON-NLS-1$
					DecimalFormat format = new DecimalFormat(_elements[i]._element);
					value += ((Number) format.parseObject(tmp.trim())).doubleValue() / 3600.0;
				} else
				{
					throw new ParseException(MessageFormat.format(Intern.errorParseMinutes, _elements[i]._element), index);
				}
				index += _elements[i]._element.length();
				break;
			}
			default:
			{
				break;
			}
			}
		}
		value *= sign;
		pos.setIndex(index);
		return new Double(value);
	}

	/**
	 * 
	 * @param a_Format
	 * @exception ParseException
	 * @throws UnsupportedEncodingException
	 */
	public void setFormatString(String a_Format) throws ParseException, UnsupportedEncodingException
	{
		_formatString = a_Format;
		_elements = AngleFormatParser.parseFormat(a_Format);
	}

	/**
	 * 
	 * @return the format string
	 */
	@Override
	public String toString()
	{
		return _formatString;
	}

	public boolean isVariableLength()
	{
		return _variableLength;
	}

	public void setVariableLength(boolean length)
	{
		_variableLength = length;
	}

	public void setVariableLength(String a_String)
	{
		setVariableLength(new Boolean(a_String).booleanValue());
	}

	
	/**
	 * //40216
	 */
	public Object parseFreeDecimalDegrees(Fragment fragment, String source) throws ParseException {
		String separator=Nemo.getInstance().getDomain().getMedia().getModel().getSeparator();
		
		if (separator!=null){
			try{
			if (source.startsWith(separator)){
				source = source.substring(source.indexOf(separator)+1, source.length());
			}
			return Double.valueOf(source.split(separator)[fragment.getColNb()]);
			}catch (Exception e) {
				// TODO
				throw new ParseException("error", 0);
			}
		}else{
			//TODO error
			throw new ParseException("error: can not use FreeDecimalDegrees format on files which are not using a separator", 0);
		}
	}

}