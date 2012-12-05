package fr.ifremer.nemo.util.format;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.input.ModelDescription;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;

/**
 * Class LongitudeFormat
 * 
 * 
 * @author Gerard BUNEL
 */
public class GeoCoordinateFormat extends ExternalizableFormat implements Cloneable
{

	private static final long serialVersionUID = -8382333200124639431L;

	protected ParseElement[] _elements;

	protected String _formatString;

	protected boolean _variableLength = false;

	protected String hemispherePositive;
	protected String hemisphereNegative;

	public GeoCoordinateFormat()
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
	public GeoCoordinateFormat(String a_Format) throws ParseException, UnsupportedEncodingException
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
	public GeoCoordinateFormat(String a_Format, boolean a_VariableLength) throws ParseException, UnsupportedEncodingException
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
		// To be overwritten
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
		a_Writer.openTag(ModelDescription.node_fLongitude);
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
	 * @see GeoCoordinateFormat#_formatString
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
		// to be overwritten
		try
		{
			return new GeoCoordinateFormat(new String(_formatString), _variableLength);
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
				if (value >= 0.0)
				{
					toAppendTo.append(hemispherePositive);
				} else
				{
					toAppendTo.append(hemisphereNegative);
				}
				break;
			case SIGN:
				if (value >= 0.0)
				{
					toAppendTo.append('+');
				} else
				{
					toAppendTo.append('-');
				}
				break;
			case CHARS:
				toAppendTo.append(_elements[i]._element);
				break;
			case DEGREES:
				double val = Math.abs(value);
				double degrees = Math.floor(val);
				DecimalFormat format = new DecimalFormat(_elements[i]._element, symbols);
				toAppendTo.append(paddedZeros(format.format(degrees), size));
				break;
			case DEGREES_DECIMAL:
				val = Math.abs(value);
				format = new DecimalFormat(_elements[i]._element, symbols);
				toAppendTo.append(padded(format.format(val), size));
				break;
			case MINUTES:
				val = Math.abs(value);
				degrees = Math.floor(val);
				double minutes = (val - degrees) * 60.0D;
				double mins = Math.floor(minutes);
				format = new DecimalFormat(_elements[i]._element, symbols);
				toAppendTo.append(padded(format.format(mins), size));
				break;
			case MINUTES_DECIMAL:
				val = Math.abs(value);
				degrees = Math.floor(val);
				minutes = (val - degrees) * 60.0D;
				format = new DecimalFormat(_elements[i]._element, symbols);
				toAppendTo.append(padded(format.format(minutes), size));
				break;
			case SECONDS:
				val = Math.abs(value);
				degrees = Math.floor(val);
				minutes = (val - degrees) * 60.0D;
				mins = Math.floor(minutes);
				double seconds = (minutes - mins) * 60.0D;
				format = new DecimalFormat(_elements[i]._element, symbols);
				toAppendTo.append(padded(format.format(seconds), size));
				break;
			default:
				break;
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
		// to be overwritten
		// try {
		// GeoCoordinateFormat tmp = new GeoCoordinateFormat();
		// tmp.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		// return tmp;
		// } catch (Exception e) {
		throw new SAXException();
		// }
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
			GeoCoordinateFormat format = new GeoCoordinateFormat(args[0]);
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
		if (_variableLength)
		{
			return parseObjectVariableLength(source, pos);
		}
		return parseObjectFixedLength(source, pos);
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
		source = replaceSpacesByZeros(source);

		String tmp;
		try
		{
			for (int i = 0; i < _elements.length && index < source.length(); i++)
			{
				switch (_elements[i]._type) {
				case HEMISPHERE:
				{
					tmp = source.substring(index, index + 1);
					if (tmp.equals(Global.EST))
					{
						sign = 1.0;
					} else if (tmp.equals(Global.OUEST_EN) || tmp.equals(Global.OUEST))
					{
						sign = -1.0;
					} else if (tmp.equals(Global.NORD))
					{
						sign = 1.0;
					} else if (tmp.equals(Global.SUD))
					{
						sign = -1.0;
					} else
					{
						generateHemisphereException(index);
					}
					index += 1;
					break;
				}
				case SIGN:
				{
					tmp = source.substring(index, index + 1);
					if (tmp.equals(Global.PLUS) || tmp.equals(Global.SPACE) || tmp.equals(Global.EMPTY_STRING))
					{
						sign = 1.0;
						// index += 1;
					} else if (tmp.equals(Global.MOINS))
					{
						sign = -1.0;
						// index += 1;
					} else
					{
						// sign = 1.0;
						throw new ParseException(Intern.errorParseSign, index);
					}
					index += 1;
					break;
				}
				case CHARS:
				{
					tmp = source.substring(index, index + _elements[i]._element.length());

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
					tmp = source.replace(' ', '0');
					DecimalFormat format = new DecimalFormat(_elements[i]._element);
					value += ((Number) format.parseObject(tmp, pos)).doubleValue();
					index = pos.getIndex();
					break;
				}
				case DEGREES_DECIMAL:
				{
					tmp = source.replace(' ', '0');
					DecimalFormat format = new DecimalFormat(_elements[i]._element, symbols);
					value += ((Number) format.parseObject(tmp, pos)).doubleValue();
					index = pos.getIndex();
					break;
				}
				case MINUTES:
				{
					tmp = source.replace(' ', '0');
					DecimalFormat format = new DecimalFormat(_elements[i]._element);
					value += ((Number) format.parseObject(tmp, pos)).doubleValue() / 60.0;
					index = pos.getIndex();
					break;
				}
				case MINUTES_DECIMAL:
				{
					tmp = source.replace(' ', '0');
					DecimalFormat format = new DecimalFormat(_elements[i]._element, symbols);
					value += ((Number) format.parseObject(tmp, pos)).doubleValue() / 60.0;
					index = pos.getIndex();
					break;
				}
				case SECONDS:
				{
					tmp = source.replace(' ', '0');
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
			e.printStackTrace();
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
				tmp1 = tmp1.replaceFirst("+", "0");
				tmp1 = tmp1.replaceFirst("0", "+");
				source = tmp1;
			}
			if (tmp1.contains("-"))
			{
				tmp1 = tmp1.replaceFirst("-", "0");
				tmp1 = tmp1.replaceFirst("0", "-");
				source = tmp1;
			}
		}
		return source;
	}

	protected void generateHemisphereException(int index) throws ParseException
	{
		throw new ParseException(Intern.errorParseCoordW, index);
	}

	/**
	 * 
	 * @param source
	 * @param pos
	 * @return Object
	 */
	public Object parseObjectFixedLength(String source, ParsePosition pos)
	{
		double value = 0.0;
		double sign = 1.0;
		int index = pos.getIndex();
		java.util.Locale locale = java.util.Locale.US;
		java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(locale);
		source = replaceSpacesByZeros(source);

		try
		{
			for (int i = 0; i < _elements.length && index < source.length(); i++)
			{
				switch (_elements[i]._type) {
				case HEMISPHERE:
				{
					String tmp = source.substring(index, index + 1);
					if (tmp.equals(Global.EST))
					{
						sign = 1.0;
					} else if (tmp.equals(Global.OUEST_EN) || tmp.equals(Global.OUEST))
					{
						sign = -1.0;
					} else if (tmp.equals(Global.NORD))
					{
						sign = 1.0;
					} else if (tmp.equals(Global.SUD))
					{
						sign = -1.0;
					} else
					{
						generateHemisphereException(index);
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
							throw new ParseException(String.format(Intern.errorParseChar, _elements[i]._element, tmp), index);
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
						throw new ParseException(String.format(Intern.errorParseDegres, _elements[i]._element), index);
					}
					index += _elements[i]._element.length();
					break;
				}
				case DEGREES_DECIMAL:
				{
					int endIndex;
					if (source.length() < (index + _elements[i]._element.length()))
					{ // 2967 endIndex can be less than the format length if
						// leading zeros are not present (ex: 2.5000 is smaller
						// that the format 00.0000)
						endIndex = source.length();
					} else
					{
						endIndex = index + _elements[i]._element.length();
					}
					String tmp = source.substring(index, endIndex);
					if (tmp.matches("\\s*\\d+\\.\\d+\\s*")) { //$NON-NLS-1$
						DecimalFormat format = new DecimalFormat(_elements[i]._element, symbols);
						value += ((Number) format.parseObject(tmp.trim())).doubleValue();
					} else
					{
						throw new ParseException(String.format(Intern.errorParseDegres, _elements[i]._element), index);
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
						throw new ParseException(String.format(Intern.errorParseDegres, _elements[i]._element), index);
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
						throw new ParseException(String.format(Intern.errorParseMinutes, _elements[i]._element), index);
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
						throw new ParseException(String.format(Intern.errorParseMinutes, _elements[i]._element), index);
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
						throw new ParseException(String.format(Intern.errorParseMinutes, _elements[i]._element), index);
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
						throw new ParseException(String.format(Intern.errorParseSeconds, _elements[i]._element), index);
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
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
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

} /* End of class: LongitudeFormat */
