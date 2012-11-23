package fr.ifremer.nemo.util.format;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.input.ModelDescription;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;

/**
 * Class DateTimeFormat
 * 
 * 
 * @author Gerard BUNEL
 */
public class DateTimeFormat extends ExternalizableFormat implements Cloneable
{

	private static final long serialVersionUID = 7705137988380881083L;

	private static final String AM = "AM"; //$NON-NLS-1$
	private static final String PM = "PM"; //$NON-NLS-1$
	public static final String GMT = "GMT"; //$NON-NLS-1$

	protected ParseElement[] _elements;

	protected String _formatString;

	public long _refDate;

	public DateTimeFormat()
	{
		super();
		_formatString = Global.SPACE;
	}

	/**
	 * 
	 * @param a_Format
	 * @param a_RefDate
	 * @exception ParseException
	 * @throws UnsupportedEncodingException
	 */
	public DateTimeFormat(String a_Format, long a_RefDate) throws ParseException, UnsupportedEncodingException
	{
		this();
		setFormatString(a_Format);
		setRefDate(a_RefDate);
	}

	/**
	 * 
	 * @param a_Format
	 * @exception ParseException
	 * @throws UnsupportedEncodingException
	 */
	public DateTimeFormat(String a_Format) throws ParseException, UnsupportedEncodingException
	{
		this();
		setFormatString(a_Format);
		setRefDate(0);
	}

	public SAXConsumer consumeElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes)
	{
		return null;
	}

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
		a_Writer.openTag(ModelDescription.node_fDate);
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
	 * @see DateTimeFormat#_formatString
	 */
	public String getFormatString()
	{
		return _formatString;
	}

	/**
	 * Read accessor (getter) for attribute _refDate
	 * 
	 * @return long
	 * @see DateTimeFormat#_refDate
	 */
	public long getRefDate()
	{
		return _refDate;
	}

	/**
	 * Write accessor (setter) for attribute _refDate
	 * 
	 * @param x
	 *            new value for _refDate
	 * @see DateTimeFormat#_refDate
	 */
	public void setRefDate(long x)
	{
		_refDate = x;
	}

	/**
	 * Write accessor (setter) for attribute _refDate
	 * 
	 * @param a_String
	 *            new value for _refDate as String
	 * @see DateTimeFormat#_refDate
	 */
	public void setRefDate(String a_String)
	{
		try
		{
			setRefDate(Long.parseLong(a_String));
		} catch (Exception e)
		{
		}
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
		try
		{
			return new DateTimeFormat(new String(_formatString));
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
		Long dateTime = (Long) obj;
		Date value = new Date(dateTime.longValue());

		DecimalFormat format = null;
		TimeZone tz = TimeZone.getTimeZone(GMT);
		java.util.Locale locale = java.util.Locale.US;
		java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(locale);
		Calendar cal = Calendar.getInstance(tz);
		cal.setTime(value);
		for (int i = 0; i < _elements.length; i++)
		{

			switch (_elements[i]._type) {
			case YEAR:
			{
				format = new DecimalFormat(_elements[i]._element.replace('y', '0'));
				int year = cal.get(Calendar.YEAR);
				if (_elements[i]._element.length() <= 2)
				{
					if (year >= 2000)
					{
						year -= 2000;
					} else
					{
						year -= 1900;
					}
				}
				toAppendTo.append(format.format(year));
				break;
			}

			case YEAR19:
			{
				format = new DecimalFormat(_elements[i]._element.replace('r', '0'));
				int year = cal.get(Calendar.YEAR);
				if (_elements[i]._element.length() <= 2)
				{
					year -= 1900;
				}
				toAppendTo.append(format.format(year));
				break;
			}

			case MONTH:
			{
				format = new DecimalFormat(_elements[i]._element.replace('M', '0'));
				toAppendTo.append(format.format(cal.get(Calendar.MONTH) + 1));
				break;
			}
			case DAY:
			{
				format = new DecimalFormat(_elements[i]._element.replace('d', '0'));
				toAppendTo.append(format.format(cal.get(Calendar.DAY_OF_MONTH)));
				break;
			}
			case DAY_DECIMAL:
			{ // cas des dates/heures donnees en jours
				// decimaux par rapport a une date de reference
				format = new DecimalFormat(_elements[i]._element.replace('d', '0'), symbols);
				int day = dateTime.intValue();
				float dayDecimal = (float) (day / (60.0 * 60.0 * 24.0));
				toAppendTo.append(format.format(dayDecimal));
				break;
			}
			case CHARS:
			{
				toAppendTo.append(_elements[i]._element);
				break;
			}
			case HOURS12:
			{
				format = new DecimalFormat(_elements[i]._element.replace('h', '0'));
				toAppendTo.append(format.format(cal.get(Calendar.HOUR)));
				break;
			}
			case HOURS24:
			{
				format = new DecimalFormat(_elements[i]._element.replace('h', '0'));
				toAppendTo.append(format.format(cal.get(Calendar.HOUR_OF_DAY)));
				break;
			}
			case AMPM:
			{
				int amPm = cal.get(Calendar.AM_PM);
				if (amPm == Calendar.AM)
				{
					toAppendTo.append(AM);
				} else
				{
					toAppendTo.append(PM);
				}
				break;
			}
			case MINUTES:
			{
				format = new DecimalFormat(_elements[i]._element.replace('m', '0'));
				toAppendTo.append(format.format(cal.get(Calendar.MINUTE)));
				break;
			}
			case SECONDS:
			{
				format = new DecimalFormat(_elements[i]._element.replace('s', '0'));
				toAppendTo.append(format.format(cal.get(Calendar.SECOND)));
				break;
			}
			case MILLISECONDS:
			{
				format = new DecimalFormat(_elements[i]._element.replace('s', '0'), symbols);
				String tmp = String.valueOf(cal.get(Calendar.SECOND)) + Global.POINT + String.valueOf(cal.get(Calendar.MILLISECOND));

				float milliseconds = Float.parseFloat(tmp);
				toAppendTo.append(format.format(milliseconds));
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
			DateTimeFormat tmp = new DateTimeFormat();
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
			Formatter formatter = new Formatter();
			System.out.println("latitude ===>" + formatter.formatLatitude("NDD MM.mm", -66.111));
			System.out.println("longitude ===>" + formatter.formatLongitude("NDDD MM.mm", 126.111));
		} catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(">>>>" + e.getMessage());
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
	 * @param source
	 * @param pos
	 * @return Long
	 */
	@Override
	public Long parseObject(String source, ParsePosition pos)
	{
		int index = pos.getIndex();

		TimeZone tz = TimeZone.getTimeZone(GMT);
		Calendar cal = Calendar.getInstance(tz);
		cal.clear();
		cal.setLenient(false);

		try
		{
			for (int i = 0; i < _elements.length && index < source.length(); i++)
			{
				switch (_elements[i]._type) {
				case YEAR:
				{
					String tmp = source.substring(index, index + _elements[i]._element.length());
					int year = Integer.parseInt(tmp);
					if (_elements[i]._element.length() <= 2)
					{
						if (year < 50)
						{
							year = 2000 + year;
						} else
						{
							year = 1900 + year;
						}
					}
					cal.set(Calendar.YEAR, year);
					index += _elements[i]._element.length();
					break;
				}
				case YEAR19:
				{
					String tmp = source.substring(index, index + _elements[i]._element.length());
					int year = Integer.parseInt(tmp);
					if (_elements[i]._element.length() <= 2)
					{
						year = 1900 + year;
					}
					cal.set(Calendar.YEAR, year);
					index += _elements[i]._element.length();
					break;
				}
				case MONTH:
				{
					String tmp = source.substring(index, index + _elements[i]._element.length());
					if (tmp.length() > 2)
					{
						java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat(_elements[i]._element, new java.text.DateFormatSymbols(java.util.Locale.US));
						java.util.Date date = fmt.parse(tmp);
						Calendar c = Calendar.getInstance();
						c.setTime(date);
						cal.set(Calendar.MONTH, c.get(Calendar.MONTH));
					} else
					{
						int month = Integer.parseInt(tmp);
						cal.set(Calendar.MONTH, month - 1);
					}

					index += _elements[i]._element.length();
					break;
				}
				case DAY:
				{
					String tmp = source.substring(index, index + _elements[i]._element.length());
					int day = Integer.parseInt(tmp);
					cal.set(Calendar.DAY_OF_MONTH, day);

					index += _elements[i]._element.length();
					break;
				}
				case DAY_DECIMAL:
				{
					String tmp = source.substring(index, index + _elements[i]._element.length());
					cal.setTimeInMillis(getRefDate());
					double days = Double.parseDouble(tmp.replace(',', '.'));
					JulianDate jd = new JulianDate(days, true);
					cal = jd.toCalendarDate(cal);

					index += _elements[i]._element.length();
					break;
				}
				case CHARS:
				{
					String tmp = source.substring(index, index + _elements[i]._element.length());
					if (!tmp.equals(_elements[i]._element))
					{
						if (!_elements[i]._element.trim().equals(Global.EMPTY_STRING))
						{
							throw new ParseException(String.format(Intern.errorParseChar, _elements[i]._element, tmp), index);
						}
					}
					index += _elements[i]._element.length();
					break;
				}
				case HOURS12:
				{
					String tmp = source.substring(index, index + _elements[i]._element.length());
					int hours = 0;
					if (tmp.trim().length() > 0)
					{
						hours = Integer.parseInt(tmp.trim());
					}
					cal.set(Calendar.HOUR, hours);
					index += _elements[i]._element.length();
					break;
				}
				case HOURS24:
				{
					String tmp = source.substring(index, index + _elements[i]._element.length());
					int hours = 0;
					if (tmp.trim().length() > 0)
					{
						hours = Integer.parseInt(tmp.trim());
					}
					cal.set(Calendar.HOUR_OF_DAY, hours);
					index += _elements[i]._element.length();
					break;
				}
				case AMPM:
				{
					String tmp = source.substring(index, index + _elements[i]._element.length());
					if (tmp.toUpperCase().equals(AM))
					{
						cal.set(Calendar.AM_PM, Calendar.AM);
					} else
					{
						cal.set(Calendar.AM_PM, Calendar.PM);
					}
					index += _elements[i]._element.length();
					break;
				}
				case MINUTES:
				{
					String tmp = source.substring(index, index + _elements[i]._element.length());
					int minutes = 0;
					if (tmp.trim().length() > 0)
					{
						minutes = Integer.parseInt(tmp.trim());
					}
					cal.set(Calendar.MINUTE, minutes);
					index += _elements[i]._element.length();
					break;
				}
				case SECONDS:
				{
					String tmp = source.substring(index, index + _elements[i]._element.length());
					int seconds = Integer.parseInt(tmp.trim());
					cal.set(Calendar.SECOND, seconds);
					index += _elements[i]._element.length();
					break;
				}
				case MILLISECONDS:
				{
					String tmp = source.substring(index, index + _elements[i]._element.length());
					Pattern p = Pattern.compile("[.]"); //$NON-NLS-1$
					String parties[] = p.split(tmp);

					cal.add(Calendar.SECOND, Integer.parseInt(parties[0]));
					cal.add(Calendar.MILLISECOND, Integer.parseInt(parties[1]));

					index += _elements[i]._element.length();
					break;
				}
				case HOURSDEC:
				{
					String tmp = source.substring(index, index + _elements[i]._element.length());

					// maximum 7 éléments
					tmp = Formatter.getSingleton().genericPadString(tmp, 7, '0', false);

					// les 2 premières décimales correspondent aux minutes [*60
					// /100]
					String sMinutes = tmp.substring(0, 2);
					int minutes = Integer.parseInt(sMinutes) * 60 / 100;
					cal.set(Calendar.MINUTE, minutes);

					// les 2 décimales suivantes correspondent aux secondes [*60
					// /100]
					String sSecondes = tmp.substring(2, 4);
					int secondes = Integer.parseInt(sSecondes) * 60 / 100;
					cal.set(Calendar.SECOND, secondes);

					// les 3 dernières décimales correspondent aux millisecondes
					// [direct]
					String sMillisecondes = tmp.substring(4);
					int millisecondes = Integer.parseInt(sMillisecondes);
					cal.set(Calendar.MILLISECOND, millisecondes);

					index += _elements[i]._element.length();
					break;
				}
				case SECONDSD:
				{
					Calendar calTemp = Calendar.getInstance();
					calTemp.clear();

					String tmp = source.substring(index, index + _elements[i]._element.length());

					calTemp.add(Calendar.SECOND, Integer.parseInt(tmp.trim()));

					cal.set(Calendar.HOUR_OF_DAY, calTemp.get(Calendar.HOUR_OF_DAY));
					cal.set(Calendar.MINUTE, calTemp.get(Calendar.MINUTE));
					cal.set(Calendar.SECOND, calTemp.get(Calendar.SECOND));

					index += _elements[i]._element.length();
					break;
				}
				default:
				{
					break;
				}
				}
			}
			cal.getTime();
		} catch (ParseException e) {
            return null;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		pos.setIndex(index);
		return new Long(cal.getTime().getTime());
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
		_elements = DateTimeFormatParser.parseFormat(a_Format);
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

	public int getFormatSize()
	{
		int size = 0;
		for (int i = 0; i < _elements.length; i++)
		{
			size += _elements[i]._element.length();
		}
		return size;
	}

}