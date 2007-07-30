package fr.ifremer.nemo.util;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

/**
 * Class DateTimeFormat
 *
 *
 * @author	Gerard BUNEL
 */
public  class DateTimeFormat extends ExternalizableFormat implements Cloneable {
  protected ParseElement[]  _elements;

  protected String  _formatString;

  public long  _refDate;

  public DateTimeFormat() {
    super();
    _formatString = "";
  }

    /**
     *
     * @param	a_Format 	
     * @param	a_RefDate 	
     * @exception	ParseException
     */
  public DateTimeFormat ( String a_Format, long a_RefDate) throws ParseException {
    this();
    setFormatString(a_Format);
    _refDate = a_RefDate;
  }

    /**
     *
     * @param	a_Format 	
     * @exception	ParseException
     */
  public DateTimeFormat ( String a_Format) throws ParseException {
    this();
    setFormatString(a_Format);
    _refDate = 0;
  }

  /**
   * 
   * A new XML Element as been found. Parse it.
   *
   * @param  a_URI  XML Namespace (SAX 2)
   * @param  a_LocalName  Tag name
   * @param  a_QName  Qualified tag name
   * @param  a_Attributes XML Element attributes
   * @return  SAXConsumer
   *
   */
  public SAXConsumer consumeElement(String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) throws SAXException {
    return null;
  }


  /**
   * 
   * The XML Element is a CDATA filled element
   *
   * @param  a_Characters  The CDATA content
   *
   */
  public void consumeCharacters( String a_Characters) throws SAXException {
  }

  /**
   * 
   * Generate XML defintion on a Stream
   *
   * @param  a_Writer  XMLOutputWriter to which to write the XML Definition
   *
   */
  public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
    a_Writer.putAttribute( "formatString", getFormatString());
    a_Writer.putAttribute( "refDate", getRefDate());
  }

  /**
   * 
   * Generate XML defintion on a XMLOutputWriter
   *
   * @param  a_Writer  the XMLOutputWriter to which to write the XML Definition
   *
   */
  public void writeXMLOn(XMLOutputWriter a_Writer) {
    a_Writer.openTag("DateTimeFormat");
    writeXMLBodyOn(a_Writer);
    a_Writer.closeTag();
  }

  /**
   * 
   * Generate XML defintion on a Stream
   *
   * @param  a_Stream  the OutptStream to which to write the XML Definition
   *
   */
  public void writeXMLOn(OutputStream a_Stream) {
    XMLOutputWriter writer = new XMLOutputWriter(a_Stream);
    writeXMLOn(writer);
  }

  /**
   * Read accessor (getter) for attribute _formatString
   *
   * @return  String
   * @see DateTimeFormat#_formatString
   */
  public  String getFormatString() {
    return _formatString;
  }

  /**
   * Read accessor (getter) for attribute _refDate
   *
   * @return  long
   * @see DateTimeFormat#_refDate
   */
  public  long getRefDate() {
    return _refDate;
  }

  /**
   * Write accessor (setter) for attribute _refDate
   *
   * @param  x  new value for _refDate
   * @see DateTimeFormat#_refDate
   */
  public void setRefDate(long x) {
    _refDate = x;
  }

  /**
   * Write accessor (setter) for attribute _refDate
   *
   * @param  a_String  new value for _refDate as String
   * @see DateTimeFormat#_refDate
   */
  public void setRefDate( String a_String) {
    try { setRefDate(Long.parseLong(a_String)); } catch (Exception e) {}
  }

  /**
   *
   * @param  a_URI  
   * @param  a_LocalName  
   * @param  a_QName  
   * @param  a_Attributes  
   * @exception  SAXException
   */
  public void buildFromElement ( String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) throws SAXException {
    try {
    	String tmp;
    	if ((tmp = a_Attributes.getValue("formatString")) != null) {
    		setFormatString(tmp);
    	}
    	if ((tmp = a_Attributes.getValue("refDate")) != null) {
    		setRefDate(tmp);
    	}
    } catch (Exception e) {
    	throw new SAXException(e);
    }
  }

  /**
   *
   * @return  Object
   */
  public Object clone ( ) {
    try {
    	return new DateTimeFormat( new String(_formatString));
    } catch (Exception e) {
    	return null;
    }
  }

  /**
   *
   * @param  obj  
   * @param  toAppendTo  
   * @param  pos  
   * @return  StringBuffer
   */
  public StringBuffer format ( Object obj, StringBuffer toAppendTo, FieldPosition pos) {
    Long dateTime = (Long)obj;
    Date value = new Date( dateTime.longValue());
    int size = 0;
    DecimalFormat format = null;
    TimeZone tz = TimeZone.getTimeZone("GMT");
    java.util.Locale locale = java.util.Locale.US;
    java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols(locale);
    Calendar cal = Calendar.getInstance(tz);
    cal.setTime(value);
    for (int i=0; i<_elements.length; i++) {
    	size = _elements[i]._element.length();
    	switch (_elements[i]._type) {
    			case YEAR: {
    				format = new DecimalFormat(_elements[i]._element.replace('y', '0'));
    				int year = cal.get( Calendar.YEAR);
    				if (_elements[i]._element.length() <= 2) {
    					if (year >= 2000) {
    						year -= 2000;
    					} else {
    						year -= 1900;
    					}
    				}
    				toAppendTo.append(format.format(year));
    				break;
    			}
    			case MONTH: {
    				format = new DecimalFormat(_elements[i]._element.replace('M', '0'));
    				toAppendTo.append(format.format(cal.get( Calendar.MONTH)+1));
    				break;
    			}
    			case DAY: {
    				format = new DecimalFormat(_elements[i]._element.replace('d', '0'));
    				toAppendTo.append(format.format(cal.get( Calendar.DAY_OF_MONTH)));
    				break;
    			}
    			case DAY_DECIMAL: {  // cas des dates/heures données en jours decimaux par rapport a une date de reference
    				format = new DecimalFormat(_elements[i]._element.replace('d', '0'), symbols);
    				int day = dateTime.intValue();
    				float dayDecimal = (float)(day/(60.0*60.0*24.0));
    				toAppendTo.append(format.format(dayDecimal));
    				break;
    			}
    			case CHARS: {
    				toAppendTo.append(_elements[i]._element);
    				break;
    			}
    			case HOURS12: {
    				format = new DecimalFormat(_elements[i]._element.replace('h', '0'));
    				toAppendTo.append(format.format(cal.get( Calendar.HOUR)));
    				break;
    			}
    			case HOURS24: {
    				format = new DecimalFormat(_elements[i]._element.replace('h', '0'));
    				toAppendTo.append(format.format(cal.get( Calendar.HOUR_OF_DAY)));
    				break;
    			}
    			case AMPM: {
    				int amPm = cal.get( Calendar.AM_PM);
    				if (amPm == Calendar.AM) {
    					toAppendTo.append("AM");
    				} else {
    					toAppendTo.append("PM");
    				}
    				break;
    			}
    			case MINUTES: {
    				format = new DecimalFormat(_elements[i]._element.replace('m', '0'));
    				toAppendTo.append(format.format(cal.get( Calendar.MINUTE)));
    				break;
    			}
    			case SECONDS: {
    				format = new DecimalFormat(_elements[i]._element.replace('s', '0'));
    				toAppendTo.append(format.format(cal.get( Calendar.SECOND)));
    				break;
    			}
    	}
    }
    return toAppendTo;
  }

  /**
   *
   * @param  a_URI  
   * @param  a_LocalName  
   * @param  a_QName  
   * @param  a_Attributes  
   * @return  SAXConsumer
   * @exception  SAXException
   */
  public static SAXConsumer fromElement ( String a_URI, String a_LocalName, String a_QName, Attributes a_Attributes) throws SAXException {
    try {
    	DateTimeFormat tmp = new DateTimeFormat();
    	tmp.buildFromElement( a_URI,  a_LocalName,  a_QName,  a_Attributes);
    	return (SAXConsumer)tmp;
    } catch (Exception e) {
    	throw new SAXException(e);
    }
  }

  /**
   *
   * @param  args  
   */
  public static void main ( String[] args) {
    try {
/*
    	//	long baseTime = System.currentTimeMillis();
    	long baseTime = 0;
    	System.out.println("TimeZone: "+TimeZone.getDefault().getID());
    	DateTimeFormat format = new DateTimeFormat( args[0], baseTime );
    	Date value = new Date(((Long)format.parseObject(args[2])).longValue());
    	System.out.println(value.getTime());
    	DateTimeFormat outFormat = new DateTimeFormat( args[1] );
    	System.out.println("base: "+outFormat.format(baseTime));
    	System.out.println("out: "+outFormat.format(value.getTime()));
*/
    //	DateTimeFormat format = new DateTimeFormat("MMM dd yyyy");
    //	format.toString();
    	
//    	DateTimeFormat format2 = new DateTimeFormat("hh24:mm:ss");
//    	String timeConvertie = format2.format("23:15");
//    	System.out.println("Heure 23:15 convertie en hh24:mm:ss = "+timeConvertie);
    	
    	//N125 06.66
    	
//    	LongitudeFormat longitudeFormat = (LongitudeFormat)propertyModel.getFormat();
//		Formatter formatter = new Formatter();
    	
    	//Long dateLong = (Long)format.parseObject("JUN 02 2007");
    	
    	Formatter formatter = new Formatter();
    	System.out.println("latitude ===>"+formatter.formatLatitude("NDD MM.mm", -66.111)); 
    	System.out.println("longitude ===>"+formatter.formatLongitude("NDDD MM.mm", 126.111)); 
    	
    	
    	//System.out.println(">>>>>>"+formatter.formatDateTime("MM/dd/yyyy", dateLong));
   
    	
    	
    	//System.out.println(new Date( dateLong.longValue()));
    } catch (Exception e) {
    	e.printStackTrace();
    	System.out.println(">>>>"+e.getMessage());
    }
  }

  /**
   *
   * @param  a_String  
   * @param  a_Size  
   * @return  String
   */
  public String padded ( String a_String, int a_Size) {
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
   * @param  source  
   * @param  pos  
   * @return  Long
   */
  public Long parseObject ( String source, ParsePosition pos) {
    int index = pos.getIndex();
    
    //System.out.println("DateTimeFormat/parseObject(): Source parsée ==>"+source);

    
    TimeZone tz = TimeZone.getTimeZone("GMT");
    Calendar cal = Calendar.getInstance(tz);
    cal.clear();
    try {
    	for (int i=0; i<_elements.length && index < source.length(); i++) {
    		switch (_elements[i]._type) {
    				case YEAR: {
    					String tmp = source.substring( index, index+_elements[i]._element.length());
    					int year = Integer.parseInt(tmp);
    					if (_elements[i]._element.length() <= 2) {
    						if (year < 50) {
    							year = 2000+year;
    						} else {
    							year = 1900+year;
    						}
    					}
    					cal.set( Calendar.YEAR, year);
    					index += _elements[i]._element.length();
    					break;
    				}
    				case MONTH: {
    					String tmp = source.substring( index, index+_elements[i]._element.length());
    					if (tmp.length() > 2) {
    						java.text.SimpleDateFormat fmt =
    							new java.text.SimpleDateFormat(
    									_elements[i]._element,
    									new java.text.DateFormatSymbols(java.util.Locale.US));
    						java.util.Date date = fmt.parse(tmp);
    						Calendar c = Calendar.getInstance();
    						c.setTime(date);
    						cal.set( Calendar.MONTH, c.get( Calendar.MONTH));
    					} else {
    						int month = Integer.parseInt(tmp);
    						cal.set( Calendar.MONTH, month-1);
    					}
    					index += _elements[i]._element.length();
    					break;
    				}
    				case DAY: {
    					String tmp = source.substring( index, index+_elements[i]._element.length());
    					
    					
    					
    					int day = Integer.parseInt(tmp);
    					cal.set( Calendar.DAY_OF_MONTH, day);
    					index += _elements[i]._element.length();
    					break;
    				}
    				case DAY_DECIMAL: {
    					String tmp = source.substring( index, index+_elements[i]._element.length());
    					cal.setTimeInMillis(_refDate);
    					double day = Double.parseDouble(tmp);
    					int dayInt = (int)Math.floor(day);
    					double hours = (day-dayInt)*24.0D;
    					int hour = (int)Math.floor(hours);
    					double minutes = (hours-hour)*60.0D;
    					int mins = (int)Math.floor(minutes);
    					int seconds = (int)Math.floor((minutes-mins)*60.0D);
    					cal.add( Calendar.DAY_OF_MONTH, dayInt);
    					cal.add( Calendar.HOUR_OF_DAY, hour);
    					cal.add( Calendar.MINUTE, mins);
    					cal.add( Calendar.SECOND, seconds);
    					index += _elements[i]._element.length();
    					break;
    				}
    				case CHARS: {
    					String tmp = source.substring( index, index+_elements[i]._element.length());
    					if (!tmp.equals(_elements[i]._element)) {
    						throw new ParseException( "Charactere invalide, chaine attendue: \""+_elements[i]._element+"\""+" lue: \""+tmp+"\"", index);
    					}
    					index += _elements[i]._element.length();
    					break;
    				}
    				case HOURS12: {
    					String tmp = source.substring( index, index+_elements[i]._element.length());
    					int hours = 0;
    					if (tmp.trim().length() > 0) {
    						hours = Integer.parseInt(tmp.trim());
    					}
    					cal.set( Calendar.HOUR, hours);
    					index += _elements[i]._element.length();
    					break;
    				}
    				case HOURS24: {
    					String tmp = source.substring( index, index+_elements[i]._element.length());
    					int hours = 0;
    					if (tmp.trim().length() > 0) {
    						hours = Integer.parseInt(tmp.trim());
    					}
    					cal.set( Calendar.HOUR_OF_DAY, hours);
    					index += _elements[i]._element.length();
    					break;
    				}
    				case AMPM: {
    					String tmp = source.substring( index, index+_elements[i]._element.length());
    					if (tmp.toUpperCase().equals("AM")) {
    						cal.set( Calendar.AM_PM, Calendar.AM);
    					} else {
    						cal.set( Calendar.AM_PM, Calendar.PM);
    					}
    					index += _elements[i]._element.length();
    					break;
    				}
    				case MINUTES: {
    					String tmp = source.substring( index, index+_elements[i]._element.length());
    					int minutes = 0;
    					if (tmp.trim().length() > 0) {
    						minutes = Integer.parseInt(tmp.trim());
    					}
    					cal.set( Calendar.MINUTE, minutes);
    					index += _elements[i]._element.length();
    					break;
    				}
    				case SECONDS: {
    					String tmp = source.substring( index, index+_elements[i]._element.length());
    					int seconds = Integer.parseInt(tmp.trim());
    					cal.set( Calendar.SECOND, seconds);
    					index += _elements[i]._element.length();
    					break;
    				}
    		}
    	}
    } catch (Exception e) {
    	e.printStackTrace();
    	return null;
    }
    pos.setIndex(index);
    return new Long(cal.getTime().getTime());
  }

  /**
   *
   * @param  a_Format  
   * @exception  ParseException
   */
  public void setFormatString ( String a_Format) throws ParseException {
    _formatString = a_Format;
    _elements = DateTimeFormatParser.parseFormat(a_Format);
  }

  /**
   *
   * @return  the format string
   */
  public String toString ( ) {
    return _formatString;
  }

} /* End of class: DateTimeFormat */
