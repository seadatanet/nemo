package fr.ifremer.nemo.util;

import com.ago.parsing.AbstractParser;
import fr.ifremer.nemo.util.ParseElement;
import java.text.ParseException;

/**
 * Class DateTimeFormatParser
 *
 *
 * @author	Gerard BUNEL
 */
public  class DateTimeFormatParser extends AbstractParser implements Cloneable {
  static protected final String  AMPM_MARKER = "AM_PM".intern();

  static protected final String  DAY = "DAY".intern();

  static protected final String  DAY_DECIMAL = "DAY_DECIMAL".intern();

  static protected final String  DOT = "DOT".intern();

  static protected final String  HOURS12 = "HOURS12".intern();

  static protected final String  HOURS24 = "HOURS24".intern();

  static protected final String  MINUTES = "MINUTES".intern();

  static protected final String  MONTH = "MONTH".intern();

  static protected final String  SECONDS = "SECONDS".intern();

  static protected final String  XAMPM = "xAmPm".intern();

  static protected final String  XDAY = "xDay".intern();

  static protected final String  XHOUR = "xHour".intern();

  static protected final String  XMINUTE = "xMinute".intern();

  static protected final String  XMONTH = "xMonth".intern();

  static protected final String  XSECOND = "xSecond".intern();

  static protected final String  XYEAR = "xYear".intern();

  static protected final String  YEAR = "YEAR".intern();

  public DateTimeFormatParser() {
    super();
  }

  /**
   * Returns a copy of this Object
   *
   * @return  a copy of this object
   */
  public Object clone() throws CloneNotSupportedException {
    DateTimeFormatParser copy = null;
    copy = (DateTimeFormatParser) super.clone();
    return copy;
  }

  /**
   *
   * @return  boolean
   * @exception  ParseException
   */
  public boolean amPmMarker ( ) throws ParseException {
    if (peekForType(AMPM_MARKER)) {
    	_stack.add( new ParseElement(ParseElementType.AMPM, (String)_stack.pop()));
    	return true;
    }
    return false;
  }

  /**
   *
   * @return  boolean
   * @exception  ParseException
   */
  public boolean characters ( ) throws ParseException {
    if (peekForType(WORD)) {
    	_stack.add( new ParseElement(ParseElementType.CHARS, (String)_stack.pop()));
    	return true;
    }
    return false;
  }

  /**
   *
   * @return  boolean
   * @exception  java.text.ParseException
   */
  public boolean date ( ) throws ParseException {
    if (year()) {
    	characters();
    	if (month()) {
    		characters();
    		if (day()) {
    			characters();
    			return (time() || nothing());
    		} else {
    			throw new ParseException ("Format du jour manquant", mark);
    		}
    	} else {
    		throw new ParseException ("Format du mois manquant", mark);
    	}
    } else if (day()) {
    	characters();
    	if (month()) {
    		characters();
    		if (year()) {
    			characters();
    			return (time() || nothing());
    		} else {
    			throw new ParseException ("Format de l'année manquant", mark);
    		}
    	} else {
    		throw new ParseException ("Format du mois manquant", mark);
    	}
    } else if (month()) {
    	characters();
    	if (day()) {
    		characters();
    		if (year()) {
    			characters();
    			return (time() || nothing());
    		} else {
    			throw new ParseException ("Format de l'année manquant", mark);
    		}
    	} else {
    		throw new ParseException ("Format du jour manquant", mark);
    	}
    }
    return false;
  }

  /**
   *
   * @return  boolean
   * @exception  ParseException
   */
  public boolean day ( ) throws ParseException {
    if (peekForType(DAY)) {
    	_stack.add( new ParseElement(ParseElementType.DAY, (String)_stack.pop()));
    	return true;
    }
    return false;
  }

  /**
   *
   * @return  boolean
   * @exception  ParseException
   */
  public boolean dayDecimal ( ) throws ParseException {
    if (peekForType(DAY_DECIMAL)) {
    	_stack.add( new ParseElement(ParseElementType.DAY_DECIMAL, (String)_stack.pop()));
    	return true;
    }
    return false;
  }

  /**
   *
   * @return  boolean
   * @exception  java.text.ParseException
   */
  public boolean format ( ) throws ParseException {
    if (date()) {
    	characters();
    	return (time() || nothing());
    } else if (time()) {
    	characters();
    	return (date() || nothing());
    } else if (dayDecimal()) {
    }
    return false;
  }

  /**
   *
   * @return  boolean
   * @exception  ParseException
   */
  public boolean hours12 ( ) throws ParseException {
    if (peekForType(HOURS12)) {
    	_stack.add( new ParseElement(ParseElementType.HOURS12, (String)_stack.pop()));
    	return true;
    }
    return false;
  }

  /**
   *
   * @return  boolean
   * @exception  ParseException
   */
  public boolean hours24 ( ) throws ParseException {
    if (peekForType(HOURS24)) {
    	_stack.add( new ParseElement(ParseElementType.HOURS24, (String)_stack.pop()));
    	return true;
    }
    return false;
  }

  /**
   *
   */
  protected void initTypeTable ( ) {
    for (int i=0; i<typeTable.length; i++) {
    	typeTable[i] = XDEFAULT;
    }
    
    String separators = "\r\n\t";
    for (int i=0; i<separators.length(); i++) {
    	typeTable[(int)separators.charAt(i)] = XDELIMITER;
    }
    String digits = "0123456789";
    for (int i=0; i<digits.length(); i++) {
    	typeTable[(int)digits.charAt(i)] = XLETTER;
    }
    String letters = "abcdefghijklmnopqrstuvwxytzABCDEFGHIJKLMNOPQRSTUVWXYTZ_";
    for (int i=0; i<letters.length(); i++) {
    	typeTable[(int)letters.charAt(i)] = XLETTER;
    }
    String binaries = "!%&*-/<=>?@\\~";
    for (int i=0; i<binaries.length(); i++) {
    	typeTable[(int)binaries.charAt(i)] = XLETTER;
    }
    
    // Other multi-character tokens
    typeTable[(int)'"'] = XLETTER;
    typeTable[(int)'\''] = XLETTER;
    typeTable[(int)'#'] = XLETTER;
    typeTable[(int)'('] = XLETTER;
    typeTable[(int)')'] = XLETTER;
    typeTable[(int)'$'] = XLETTER;
    typeTable[(int)','] = XLETTER;
    typeTable[(int)':'] = XLETTER;
    typeTable[(int)';'] = XLETTER;
    typeTable[(int)'['] = XLETTER;
    typeTable[(int)']'] = XLETTER;
    typeTable[(int)'{'] = XLETTER;
    typeTable[(int)'}'] = XLETTER;
    typeTable[(int)'^'] = XLETTER;
    typeTable[(int)'|'] = XLETTER;
    typeTable[(int)'/'] = XLETTER;
    
    typeTable[(int)'.'] = DOT;
    typeTable[(int) 'y'] = XYEAR;
    typeTable[(int) 'M'] = XMONTH;
    typeTable[(int) 'd'] = XDAY;
    typeTable[(int) 'h'] = XHOUR;
    typeTable[(int) 'm'] = XMINUTE;
    typeTable[(int) 's'] = XSECOND;
    typeTable[(int) ' '] = XLETTER;
    typeTable[(int) 'A'] = XAMPM;
  }

  /**
   *
   * @param  args  
   */
  public static void main ( String[] args) {
	  ParseElement[] elements = null;
	try {
	  elements = DateTimeFormatParser.parseFormat("dd/MM/yyyy");
    } catch (Exception e) {
    	e.printStackTrace();
    }
/*
    DateTimeFormatParser parser = new DateTimeFormatParser();
    try {
    	java.util.Vector v = parser.breakIntoTokens(args[0]);
    	for (int i = 0; i < v.size(); i++) {
    		System.out.println("i=" + i + " \"" + v.get(i) + "\"");
    	}
    } catch (Exception e) {
    	e.printStackTrace();
    }
*/
  }

  /**
   *
   * @return  boolean
   * @exception  ParseException
   */
  public boolean minutes ( ) throws ParseException {
    if (peekForType(MINUTES)) {
    	_stack.add( new ParseElement(ParseElementType.MINUTES, (String)_stack.pop()));
    	return true;
    }
    return false;
  }

  /**
   *
   * @return  boolean
   * @exception  ParseException
   */
  public boolean month ( ) throws ParseException {
    if (peekForType(MONTH)) {
    	_stack.add( new ParseElement(ParseElementType.MONTH, (String)_stack.pop()));
    	return true;
    }
    return false;
  }

  /**
   *
   * @return  boolean
   */
  protected boolean nothing ( ) {
    return true;
  }

  /**
   *
   * @param  a_Format  
   * @return  ParseElement[]
   * @exception  ParseException
   */
  public ParseElement[] parse ( String a_Format) throws ParseException {
    java.util.Stack<Object> stack = parse( a_Format, "format");
    ParseElement[] elements = new ParseElement[stack.size()];
    stack.copyInto( elements );
    return elements;
  }

  /**
   *
   * @param  a_Format  
   * @return  ParseElement[]
   * @exception  ParseException
   */
  public static ParseElement[] parseFormat ( String a_Format) throws ParseException {
    DateTimeFormatParser parser = new DateTimeFormatParser();
    return parser.parse(a_Format);
  }

  /**
   *
   * @return  boolean
   * @exception  ParseException
   */
  public boolean seconds ( ) throws ParseException {
    if (peekForType(SECONDS)) {
    	_stack.add( new ParseElement(ParseElementType.SECONDS, (String)_stack.pop()));
    	return true;
    }
    return false;
  }

  /**
   *
   * @return  boolean
   * @exception  java.text.ParseException
   */
  public boolean time ( ) throws ParseException {
    if (hours12()) {
    	characters();
    	if (minutes()) {
    		characters();
    		if (seconds()) {
    			characters();
    			if (amPmMarker()) {
    				return true;
    			} else {
    				throw new ParseException ("Marker AM/PM manquant", mark);
    			}
    		} else {
    			characters();
    			if (amPmMarker()) {
    				return true;
    			} else {
    				throw new ParseException ("Marker AM/PM manquant", mark);
    			}
    		}
    	} else {
    		characters();
    		if (amPmMarker()) {
    			return true;
    		} else {
    			throw new ParseException ("Marker AM/PM manquant", mark);
    		}
    	}
    } else if (hours24()) {
    	characters();
    	if (minutes()) {
    		characters();
    		return (seconds() || nothing());
    	} else {
    		return characters();
    	}
    }
    return false;
  }

  /**
   *
   * @exception  ParseException
   */
  public void xAmPm ( ) throws ParseException {
    StringBuffer buff = new StringBuffer();
    tokenType = WORD;
    buff.append('A');
    long sourcePos = getSourcePosition();
    step();
    if (hereChar > 0 && (typeTable[hereChar] == XMONTH)) {
    	step();
    	token = "AM";
    	tokenType = AMPM_MARKER;
    } else {
    	setSourcePosition(sourcePos);
    	xLetter();
    }
  }

  /**
   *
   * @exception  java.text.ParseException
   */
  public void xDay ( ) throws ParseException {
    StringBuffer buff = new StringBuffer();
    tokenType = DAY;
    buff.append('d');
    step();
    while (hereChar > 0 && (typeTable[hereChar] == XDAY)) {
    	buff.append('d');
    	step();
    }
    if (hereChar > 0 && (typeTable[hereChar] == DOT)) {
    	buff.append('.');
    	step();
    	while (hereChar > 0 && (typeTable[hereChar] == XDAY)) {
    		buff.append('d');
    		step();
    	}
    	tokenType = DAY_DECIMAL;
    }
    token = buff.toString();
  }

  /**
   *
   * @exception  java.text.ParseException
   */
  public void xHour ( ) throws ParseException {
    StringBuffer buff = new StringBuffer();
    tokenType = HOURS12;
    buff.append('h');
    step();
    while (hereChar > 0 && (typeTable[hereChar] == XHOUR)) {
    	buff.append('h');
    	step();
    }
    long sourcePos = getSourcePosition();
    if (hereChar > 0 && (typeTable[hereChar] == XLETTER) && hereChar == (int)'2') {
    	step();
    	if (hereChar > 0 && (typeTable[hereChar] == XLETTER) && hereChar == (int)'4') {
    		tokenType = HOURS24;
    		step();
    	} else {
    		setSourcePosition( sourcePos);
    	}
    }
    token = buff.toString();
  }

  /**
   *
   * @exception  java.text.ParseException
   */
  public void xMinute ( ) throws ParseException {
    StringBuffer buff = new StringBuffer();
    tokenType = MINUTES;
    buff.append('m');
    step();
    while (hereChar > 0 && (typeTable[hereChar] == XMINUTE)) {
    	buff.append('m');
    	step();
    }
    token = buff.toString();
  }

  /**
   *
   * @exception  java.text.ParseException
   */
  public void xMonth ( ) throws ParseException {
    StringBuffer buff = new StringBuffer();
    tokenType = MONTH;
    buff.append('M');
    step();
    while (hereChar > 0 && (typeTable[hereChar] == XMONTH)) {
    	buff.append('M');
    	step();
    }
    token = buff.toString();
  }

  /**
   *
   * @exception  java.text.ParseException
   */
  public void xSecond ( ) throws ParseException {
    StringBuffer buff = new StringBuffer();
    tokenType = SECONDS;
    buff.append('s');
    step();
    while (hereChar > 0 && (typeTable[hereChar] == XSECOND)) {
    	buff.append('s');
    	step();
    }
    token = buff.toString();
  }

  /**
   *
   * @exception  java.text.ParseException
   */
  public void xYear ( ) throws ParseException {
    StringBuffer buff = new StringBuffer();
    tokenType = YEAR;
    buff.append('y');
    step();
    while (hereChar > 0 && (typeTable[hereChar] == XYEAR)) {
    	buff.append('y');
    	step();
    }
    token = buff.toString();
  }

  /**
   *
   * @return  boolean
   * @exception  ParseException
   */
  public boolean year ( ) throws ParseException {
    if (peekForType(YEAR)) {
    	_stack.add( new ParseElement(ParseElementType.YEAR, (String)_stack.pop()));
    	return true;
    }
    return false;
  }

} /* End of class: DateTimeFormatParser */
