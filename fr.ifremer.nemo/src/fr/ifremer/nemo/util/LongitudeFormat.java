package fr.ifremer.nemo.util;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

/**
 * Class LongitudeFormat
 * 
 * 
 * @author Gerard BUNEL
 */
public class LongitudeFormat extends ExternalizableFormat implements Cloneable {
	protected ParseElement[] _elements;

	protected String _formatString;

	protected boolean _variableLength = false;

	public LongitudeFormat() {
		super();
		_formatString = "";
	}

	/**
	 * 
	 * @param a_Format
	 * @exception ParseException
	 */
	public LongitudeFormat(String a_Format) throws ParseException {
		this();
		setFormatString(a_Format);
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
		a_Writer.putAttribute("formatString", getFormatString());
		if (_variableLength) {
			a_Writer.putAttribute("variable", _variableLength);
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
		a_Writer.openTag("LongitudeFormat");
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
	 * Read accessor (getter) for attribute _formatString
	 * 
	 * @return String
	 * @see LongitudeFormat#_formatString
	 */
	public String getFormatString() {
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
	public void buildFromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
		try {
			String tmp;
			if ((tmp = a_Attributes.getValue("formatString")) != null) {
				setFormatString(tmp);
			}
			if ((tmp = a_Attributes.getValue("variable")) != null) {
				setVariableLength(tmp);
			}
		} catch (Exception e) {
			throw new SAXException(e);
		}
	}

	/**
	 * 
	 * @return Object
	 */
	public Object clone() {
		try {
			return new LongitudeFormat(new String(_formatString));
		} catch (Exception e) {
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
	public StringBuffer format(Object obj, StringBuffer toAppendTo,
			FieldPosition pos) {
		double value = ((Double) obj).doubleValue();
		java.util.Locale locale = java.util.Locale.US;
		java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(
				locale);
		int size = 0;
		for (int i = 0; i < _elements.length; i++) {
			size = _elements[i]._element.length();
			switch (_elements[i]._type) {
			case HEMISPHERE: {
				if (value >= 0.0) {
					toAppendTo.append('E');
				} else {
					toAppendTo.append('W');
				}
				break;
			}
			case SIGN: {
				if (value >= 0.0) {
					toAppendTo.append('+');
				} else {
					toAppendTo.append('-');
				}
				break;
			}
			case CHARS: {
				toAppendTo.append(_elements[i]._element);
				break;
			}
			case DEGREES: {
				double val = Math.abs(value);
				double degrees = Math.floor(val);
				DecimalFormat format = new DecimalFormat(_elements[i]._element,
						symbols);
				toAppendTo.append(paddedZeros(format.format(degrees), size));
				break;
			}
			case DEGREES_DECIMAL: {
				double val = Math.abs(value);
				DecimalFormat format = new DecimalFormat(_elements[i]._element,
						symbols);
				toAppendTo.append(padded(format.format(val), size));
				break;
			}
			case MINUTES: {
				double val = Math.abs(value);
				double degrees = Math.floor(val);
				double minutes = (val - degrees) * 60.0D;
				double mins = Math.floor(minutes);
				DecimalFormat format = new DecimalFormat(_elements[i]._element,
						symbols);
				toAppendTo.append(padded(format.format(mins), size));
				break;
			}
			case MINUTES_DECIMAL: {
				double val = Math.abs(value);
				double degrees = Math.floor(val);
				double minutes = (val - degrees) * 60.0D;
				DecimalFormat format = new DecimalFormat(_elements[i]._element,
						symbols);
				toAppendTo.append(padded(format.format(minutes), size));
				break;
			}
			case SECONDS: {
				double val = Math.abs(value);
				double degrees = Math.floor(val);
				double minutes = (val - degrees) * 60.0D;
				double mins = Math.floor(minutes);
				double seconds = (minutes - mins) * 60.0D;
				DecimalFormat format = new DecimalFormat(_elements[i]._element,
						symbols);
				toAppendTo.append(padded(format.format(seconds), size));
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
	public static SAXConsumer fromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
		try {
			LongitudeFormat tmp = new LongitudeFormat();
			tmp.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
			return (SAXConsumer) tmp;
		} catch (Exception e) {
			throw new SAXException(e);
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			LongitudeFormat format = new LongitudeFormat(args[0]);
			double value = ((Number) format.parseObject(args[1])).doubleValue();
			System.out.println(value);
			System.out.println("out: " + format.format(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param a_String
	 * @param a_Size
	 * @return String
	 */
	public String padded(String a_String, int a_Size) {
		StringBuffer buf = new StringBuffer();
		int index = a_String.length();
		while (index < a_Size) {
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
	public String paddedZeros(String a_String, int a_Size) {
		StringBuffer buf = new StringBuffer();
		int index = a_String.length();
		while (index < a_Size) {
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
	public Object parseObject(String source, ParsePosition pos) {
		if (_variableLength) {
			return parseObjectVariableLength(source, pos);
		} else {
			return parseObjectFixedLength(source, pos);
		}
	}

/**
	 * 
	 * @param source
	 * @param pos
	 * @return Object
	 */
	public Object parseObjectVariableLength(String source, ParsePosition pos) {
		double value = 0.0;
		double sign = 1.0;
		int index = pos.getIndex();
		java.util.Locale locale = java.util.Locale.US;
		java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(
				locale);
		try {
			for (int i = 0; i < _elements.length && index < source.length(); i++) {
				switch (_elements[i]._type) {
				case HEMISPHERE: {
					String tmp = source.substring(index, index + 1);
					if (tmp.equals("E")) {
						sign = 1.0;
					} else if (tmp.equals("W") || tmp.equals("O")) {
						sign = -1.0;
					} else {
						throw new ParseException(
								"Charactere invalide, 'E', 'O' ou 'W' attendu",
								index);
					}
					index += 1;
					break;
				}
				case SIGN: {
					String tmp = source.substring(index, index + 1);
					if (tmp.equals("+") || tmp.equals(" ") || tmp.equals("")) {
						sign = 1.0;
					} else if (tmp.equals("-")) {
						sign = -1.0;
					} else {
						throw new ParseException(
								"Charactere invalide, signe (+/-) attendu",
								index);
					}
					index += 1;
					break;
				}
				case CHARS: {
					String tmp = source.substring(index, index
							+ _elements[i]._element.length());

					if (_elements[i]._element.length() == 1
							&& _elements[i]._element.equals(" ")) {
						// cas particulier si on rencontre un espace, n'importe
						// quel caractère est possible
					} else {
						if (!tmp.equals(_elements[i]._element)) {
							throw new ParseException(
									"Charactere invalide, chaine attendue: \""
											+ _elements[i]._element + "\""
											+ " lue: \"" + tmp + "\"", index);
						}
					}
					index += _elements[i]._element.length();
					break;
				}
				case DEGREES: {
					DecimalFormat format = new DecimalFormat(
							_elements[i]._element);
					value += ((Number) format.parseObject( source, pos)).doubleValue();
					index = pos.getIndex();
					break;
				}
				case DEGREES_DECIMAL: {
						DecimalFormat format = new DecimalFormat(
								_elements[i]._element, symbols);
						value += ((Number) format.parseObject(source, pos)).doubleValue();
						index = pos.getIndex();
					break;
				}
				case MINUTES: {
						DecimalFormat format = new DecimalFormat(
								_elements[i]._element);
						value += ((Number) format.parseObject(source, pos)).doubleValue() / 60.0;
						index = pos.getIndex();
					break;
				}
				case MINUTES_DECIMAL: {
					DecimalFormat format = new DecimalFormat(
							_elements[i]._element, symbols);
					value += ((Number) format.parseObject(source, pos)).doubleValue() / 60.0;
					index = pos.getIndex();
					break;
				}
				case SECONDS: {
						DecimalFormat format = new DecimalFormat(
								_elements[i]._element);
						value += ((Number) format.parseObject(source, pos)).doubleValue() / 3600.0;
						index = pos.getIndex();
					break;
				}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		value *= sign;
		pos.setIndex(index);
		return new Double(value);
	}

	/**
	 * 
	 * @param source
	 * @param pos
	 * @return Object
	 */
	public Object parseObjectFixedLength(String source, ParsePosition pos) {
		double value = 0.0;
		double sign = 1.0;
		int index = pos.getIndex();
		java.util.Locale locale = java.util.Locale.US;
		java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(
				locale);
		try {
			for (int i = 0; i < _elements.length && index < source.length(); i++) {
				switch (_elements[i]._type) {
				case HEMISPHERE: {
					String tmp = source.substring(index, index + 1);
					if (tmp.equals("E")) {
						sign = 1.0;
					} else if (tmp.equals("W") || tmp.equals("O")) {
						sign = -1.0;
					} else {
						throw new ParseException(
								"Charactere invalide, 'E', 'O' ou 'W' attendu",
								index);
					}
					index += 1;
					break;
				}
				case SIGN: {
					String tmp = source.substring(index, index + 1);
					if (tmp.equals("+") || tmp.equals(" ") || tmp.equals("")) {
						sign = 1.0;
					} else if (tmp.equals("-")) {
						sign = -1.0;
					} else {
						throw new ParseException(
								"Charactere invalide, signe (+/-) attendu",
								index);
					}
					index += 1;
					break;
				}
				case CHARS: {
					String tmp = source.substring(index, index
							+ _elements[i]._element.length());

					if (_elements[i]._element.length() == 1
							&& _elements[i]._element.equals(" ")) {
						// cas particulier si on rencontre un espace, n'importe
						// quel caractère est possible
					} else {
						if (!tmp.equals(_elements[i]._element)) {
							throw new ParseException(
									"Charactere invalide, chaine attendue: \""
											+ _elements[i]._element + "\""
											+ " lue: \"" + tmp + "\"", index);
						}
					}
					index += _elements[i]._element.length();
					break;
				}
				case DEGREES: {
					String tmp = source.substring(index, index
							+ _elements[i]._element.length());
					if (tmp.matches("\\s*\\d+\\s*")) {
						DecimalFormat format = new DecimalFormat(
								_elements[i]._element);
						value += ((Number) format.parseObject(tmp.trim()))
								.doubleValue();
					} else {
						throw new ParseException("Degres attendus au format: "
								+ _elements[i]._element, index);
					}
					index += _elements[i]._element.length();
					break;
				}
				case DEGREES_DECIMAL: {
					String tmp = source.substring(index, index
							+ _elements[i]._element.length());
					if (tmp.matches("\\s*\\d+\\.\\d+\\s*")) {
						DecimalFormat format = new DecimalFormat(
								_elements[i]._element, symbols);
						value += ((Number) format.parseObject(tmp.trim()))
								.doubleValue();
					} else {
						throw new ParseException("Degres attendus au format: "
								+ _elements[i]._element, index);
					}
					index += _elements[i]._element.length();
					break;
				}
				case DEGREES_DECIMAL_SANS_POINT: {
					
					int indexOfPoint = _elements[i]._element.indexOf(".");
					
					String tmp = source.substring(index, index
							+ _elements[i]._element.length() - 1);
					
					String tmpPlusPoint = tmp.substring(0,indexOfPoint) + "." + tmp.substring(indexOfPoint);							
					
					if (tmpPlusPoint.matches("\\s*\\d+\\.\\d+\\s*")) {
						DecimalFormat format = new DecimalFormat(
								_elements[i]._element, symbols);
						value += ((Number) format.parseObject(tmpPlusPoint.trim()))
								.doubleValue();
					} else {
						throw new ParseException("Degres attendus au format: "
								+ _elements[i]._element, index);
					}
					index += _elements[i]._element.length();
					break;
				}
				case MINUTES: {
					String tmp = source.substring(index, index
							+ _elements[i]._element.length());
					if (tmp.matches("\\s*\\d+\\s*")) {
						DecimalFormat format = new DecimalFormat(
								_elements[i]._element);
						value += ((Number) format.parseObject(tmp.trim()))
								.doubleValue() / 60.0;
					} else {
						throw new ParseException(
								"Minutes attendues au format: "
										+ _elements[i]._element, index);
					}
					index += _elements[i]._element.length();
					break;
				}
				case MINUTES_DECIMAL: {
					String tmp = source.substring(index, index
							+ _elements[i]._element.length());
					if (tmp.matches("\\s*\\d+\\.\\d+\\s*")) {
						DecimalFormat format = new DecimalFormat(
								_elements[i]._element, symbols);
						value += ((Number) format.parseObject(tmp.trim()))
								.doubleValue() / 60.0;
					} else {
						throw new ParseException("Minutes attendus au format: "
								+ _elements[i]._element, index);
					}
					index += _elements[i]._element.length();
					break;
				}
				case MINUTES_DECIMAL_SANS_POINT: {
					
					int indexOfPoint = _elements[i]._element.indexOf(".");
										
					String tmp = source.substring(index, index
							+ _elements[i]._element.length() - 1);
					
					String tmpPlusPoint = tmp.substring(0,indexOfPoint) + "." + tmp.substring(indexOfPoint);							
					
					if (tmpPlusPoint.matches("\\s*\\d+\\.\\d+\\s*")) {
						DecimalFormat format = new DecimalFormat(
								_elements[i]._element, symbols);
						value += ((Number) format.parseObject(tmpPlusPoint.trim()))
								.doubleValue() / 60.0;
					} else {
						throw new ParseException("Minutes attendus au format: "
								+ _elements[i]._element, index);
					}
					index += _elements[i]._element.length() - 1;
					break;
				}
				case SECONDS: {
					String tmp = source.substring(index, index
							+ _elements[i]._element.length());
					if (tmp.matches("\\s*\\d+\\s*")) {
						DecimalFormat format = new DecimalFormat(
								_elements[i]._element);
						value += ((Number) format.parseObject(tmp.trim()))
								.doubleValue() / 3600.0;
					} else {
						throw new ParseException(
								"Secondes attendues au format: "
										+ _elements[i]._element, index);
					}
					index += _elements[i]._element.length();
					break;
				}
				}
			}
		} catch (Exception e) {
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
	 */
	public void setFormatString(String a_Format) throws ParseException {
		_formatString = a_Format;
		_elements = AngleFormatParser.parseFormat(a_Format);
	}

	/**
	 * 
	 * @return the format string
	 */
	public String toString() {
		return _formatString;
	}

	public boolean isVariableLength() {
		return _variableLength;
	}

	public void setVariableLength(boolean length) {
		_variableLength = length;
	}

	public void setVariableLength(String a_String) {
		setVariableLength(new Boolean(a_String).booleanValue());
	}

} /* End of class: LongitudeFormat */
