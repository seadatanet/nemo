package fr.ifremer.nemo.util.format;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Vector;

import com.ago.parsing.AbstractParser;

import fr.ifremer.nemo.util.Global;

/**
 * Class AngleFormatParser
 * 
 * 
 * @author Gerard BUNEL
 */
@SuppressWarnings("nls")
public class AngleFormatParser extends AbstractParser implements Cloneable
{

	static public final String CHARS = "CHARS".intern();

	static protected final String DEGREE = "DEGREE".intern();

	static protected final String DEGREES = "DEGREES".intern();

	static protected final String DEGREES_DECIMAL = "DEGREES_DECIMAL".intern();

	static public final String DEGREE_FRACTION = "DEGREE_FRACTION".intern();

	static protected final String DOT = "DOT".intern();

	static protected final String HEMISPHERE = "HEMISPHERE".intern();

	static protected final String MINUTES = "MINUTES".intern();

	static protected final String MINUTES_DECIMAL = "MINUTES_DECIMAL".intern();

	static protected final String MINUTES_DECIMAL_SANS_POINT = "MINUTES_DECIMAL_SANS_POINT".intern();

	static protected final String DEGREES_DECIMAL_SANS_POINT = "DEGREES_DECIMAL_SANS_POINT".intern();

	static protected final String MINUTES_FRACTION = "MINUTES_FRACTION".intern();

	static protected final String MINUTE_FRACTION = "MINUTES_FRACTION".intern();

	static protected final String SECONDS = "SECONDS".intern();

	static protected final String SIGN = "SIGN".intern();

	static protected final String XDEGREE = "xDegree".intern();

	static protected final String XHEMISPHERE = "xHemisphere".intern();

	static protected final String XMINUTE = "xMinute".intern();

	static protected final String XSECOND = "xSecond".intern();

	static protected final String XSIGN = "xSign".intern();

	public AngleFormatParser()
	{
		super();
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		AngleFormatParser copy = null;
		copy = (AngleFormatParser) super.clone();
		return copy;
	}

	/**
	 * 
	 * @return boolean
	 * @exception ParseException
	 */
	public boolean degrees() throws ParseException
	{
		if (peekForType(DEGREES))
		{
			_stack.add(new ParseElement(ParseElementType.DEGREES, (String) _stack.pop()));
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return boolean
	 * @exception ParseException
	 */
	public boolean degreesDecimal() throws ParseException
	{
		if (peekForType(DEGREES_DECIMAL))
		{
			_stack.add(new ParseElement(ParseElementType.DEGREES_DECIMAL, (String) _stack.pop()));
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return boolean
	 * @exception ParseException
	 */
	public boolean degreesDecimalSansPoint() throws ParseException
	{
		if (peekForType(DEGREES_DECIMAL_SANS_POINT))
		{
			_stack.add(new ParseElement(ParseElementType.DEGREES_DECIMAL_SANS_POINT, (String) _stack.pop()));
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return boolean
	 * @exception java.text.ParseException
	 */
	public boolean format() throws ParseException
	{

		boolean res = true;

		// En fait il faut pouvoir accepter beaucoup plus de formats
		// Par contre moins de cas pouvant poser probleme sont detectes

		spaces();
		hemisphere();
		sign();
		spaces();
		degrees();
		degreesDecimal();
		degreesDecimalSansPoint();
		spaces();
		minutes();
		minutesDecimal();
		minutesDecimalSansPoint();
		spaces();
		seconds();
		spaces();
		hemisphere(); // FAE 54 DEBUT
		spaces(); // FAE 54 DEBUT

		return res;

		/*
		 * spaces(); if (sign()) { spaces(); return valuePart(); } else if
		 * (hemisphere()) { spaces(); return valuePart(); } else if (degrees())
		 * {
		 * spaces(); if (hemisphere()) { spaces(); return valueSubPart(); } }
		 * else if (valuePart()) { spaces(); return hemisphere(); } return
		 * false;
		 */

	}

	/**
	 * 
	 * @return boolean
	 * @exception ParseException
	 */
	public boolean hemisphere() throws ParseException
	{
		if (peekForType(HEMISPHERE))
		{
			_stack.add(new ParseElement(ParseElementType.HEMISPHERE, (String) _stack.pop()));
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	@Override
	protected void initTypeTable()
	{

		PrintStream ps = null;
		byte[] c = null;

		try
		{

			String separators = "\r\n\t";
			c = separators.getBytes(Global.NEMO_ENCODING);
			for (int i = 0; i < separators.length(); i++)
			{
				typeTable[c[i]] = XDELIMITER;
			}
			String digits = "0123456789";
			c = digits.getBytes(Global.NEMO_ENCODING);
			for (int i = 0; i < digits.length(); i++)
			{
				typeTable[c[i]] = XLETTER;
			}
			String letters = "abcdefghijklmnopqrstuvwxytzABCDEFGHIJKLMNOPQRSTUVWXYTZ_";
			c = letters.getBytes(Global.NEMO_ENCODING);
			for (int i = 0; i < letters.length(); i++)
			{
				typeTable[c[i]] = XLETTER;
			}
			String binaries = "!%&*-/<=>?@\\~";
			c = binaries.getBytes(Global.NEMO_ENCODING);
			for (int i = 0; i < binaries.length(); i++)
			{
				typeTable[c[i]] = XLETTER;
			}

			// Other multi-character tokens
			typeTable['"'] = XLETTER;
			typeTable['\''] = XLETTER;
			typeTable['#'] = XLETTER;
			typeTable['('] = XLETTER;
			typeTable[')'] = XLETTER;
			typeTable['$'] = XLETTER;
			typeTable[','] = XLETTER;
			typeTable['.'] = XLETTER;
			typeTable[':'] = XLETTER;
			typeTable[';'] = XLETTER;
			typeTable['['] = XLETTER;
			typeTable[']'] = XLETTER;
			typeTable['{'] = XLETTER;
			typeTable['}'] = XLETTER;
			typeTable['^'] = XLETTER;
			typeTable['|'] = XLETTER;
			typeTable['°'] = XLETTER;

			typeTable['N'] = XHEMISPHERE;
			typeTable['D'] = XDEGREE;
			typeTable['d'] = DEGREE_FRACTION;
			typeTable['M'] = XMINUTE;
			typeTable['m'] = MINUTE_FRACTION;
			typeTable['S'] = XSECOND;
			typeTable['.'] = DOT;
			typeTable[' '] = XLETTER;
			typeTable['+'] = XSIGN;

		} catch (UnsupportedEncodingException error)
		{
			System.err.println(error);
			System.exit(0);
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		AngleFormatParser parser = new AngleFormatParser();
		try
		{
			Vector<String> v = parser.breakIntoTokens(args[0]);
			for (int i = 0; i < v.size(); i++)
			{
				System.out.println("i=" + i + " \"" + v.get(i) + "\"");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return boolean
	 * @exception ParseException
	 */
	public boolean minutes() throws ParseException
	{
		if (peekForType(MINUTES))
		{
			_stack.add(new ParseElement(ParseElementType.MINUTES, (String) _stack.pop()));
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return boolean
	 * @exception ParseException
	 */
	public boolean minutesDecimal() throws ParseException
	{
		if (peekForType(MINUTES_DECIMAL))
		{
			_stack.add(new ParseElement(ParseElementType.MINUTES_DECIMAL, (String) _stack.pop()));
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @return boolean
	 * @exception ParseException
	 */
	public boolean minutesDecimalSansPoint() throws ParseException
	{

		if (peekForType(MINUTES_DECIMAL_SANS_POINT))
		{
			_stack.add(new ParseElement(ParseElementType.MINUTES_DECIMAL_SANS_POINT, (String) _stack.pop()));
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param a_Format
	 * @return ParseElement[]
	 * @exception ParseException
	 * @throws UnsupportedEncodingException
	 */
	public ParseElement[] parse(String a_Format) throws ParseException, UnsupportedEncodingException
	{
		java.util.Stack<Object> stack = parse(a_Format, "format");
		ParseElement[] elements = new ParseElement[stack.size()];
		stack.copyInto(elements);
		return elements;
	}

	/**
	 * 
	 * @param a_Format
	 * @return ParseElement[]
	 * @exception ParseException
	 * @throws UnsupportedEncodingException
	 */
	public static ParseElement[] parseFormat(String a_Format) throws ParseException, UnsupportedEncodingException
	{
		AngleFormatParser parser = new AngleFormatParser();
		return parser.parse(a_Format);
	}

	/**
	 * 
	 * @return boolean
	 * @exception ParseException
	 */
	public boolean seconds() throws ParseException
	{
		if (peekForType(SECONDS))
		{
			_stack.add(new ParseElement(ParseElementType.SECONDS, (String) _stack.pop()));
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return boolean
	 * @exception ParseException
	 */
	public boolean sign() throws ParseException
	{
		if (peekForType(SIGN))
		{
			_stack.add(new ParseElement(ParseElementType.SIGN, (String) _stack.pop()));
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return boolean
	 * @exception ParseException
	 */
	public boolean spaces() throws ParseException
	{
		if (peekForType(WORD))
		{
			_stack.add(new ParseElement(ParseElementType.CHARS, (String) _stack.pop()));
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return boolean
	 * @exception ParseException
	 */
	public boolean valuePart() throws ParseException
	{
		if (degrees())
		{
			spaces();
			if (minutes())
			{
				spaces();
				if (!seconds())
				{
					throw new ParseException("Secondes manquantes", mark);
				}
			} else if (!minutesDecimal())
			{
				if (!minutesDecimalSansPoint())
					throw new ParseException("Minutes manquantes", mark);
			}
		} else if (!degreesDecimal())
		{
			if (!degreesDecimalSansPoint())
				throw new ParseException("Valeur manquantes", mark);
		}
		return true;
	}

	/**
	 * 
	 * @return boolean
	 * @exception ParseException
	 */
	public boolean valueSubPart() throws ParseException
	{
		if (minutes())
		{
			spaces();
			if (!seconds())
			{
				throw new ParseException("Secondes manquantes", mark);
			}
		} else if (!minutesDecimal())
		{
			if (!minutesDecimalSansPoint())
				throw new ParseException("Minutes manquantes", mark);
		}
		return true;
	}

	/**
	 * 
	 * @exception java.text.ParseException
	 */
	public void xDegree() throws ParseException
	{
		StringBuffer buff = new StringBuffer();
		tokenType = DEGREES;
		buff.append('#');
		step();
		while (hereChar > 0 && (typeTable[hereChar] == XDEGREE))
		{
			buff.append('#');
			step();
		}
		if (hereChar > 0 && (typeTable[hereChar] == DOT))
		{
			String entier = buff.toString();
			buff = new StringBuffer();
			buff.append(entier.substring(0, entier.length() - 1));
			buff.append('0');
			tokenType = DEGREES_DECIMAL;
			buff.append((char) hereChar);
			step();
			while (hereChar > 0 && (typeTable[hereChar] == DEGREE_FRACTION))
			{
				buff.append('0');
				step();
			}
			token = buff.toString();
			token = ((String) token).replace('#', '0');

			// nouveau cas pas de point entre DD et dd !!!
		} else
		{
			if (hereChar > 0 && (typeTable[hereChar] == DEGREE_FRACTION))
			{

				String entier = buff.toString();
				buff = new StringBuffer();
				buff.append(entier.substring(0, entier.length() - 1));
				buff.append('0');
				tokenType = DEGREES_DECIMAL_SANS_POINT;
				buff.append(".");
				while (hereChar > 0 && (typeTable[hereChar] == DEGREE_FRACTION))
				{
					buff.append('0');
					step();
				}
				token = buff.toString();
				token = ((String) token).replace('#', '0');

			} else
			{
				token = buff.toString();
			}
		}
	}

	/**
	 * 
	 * @exception ParseException
	 */
	public void xHemisphere() throws ParseException
	{
		tokenType = HEMISPHERE;
		step();
		token = "N";
	}

	/**
	 * 
	 * @exception java.text.ParseException
	 */
	public void xMinute() throws ParseException
	{
		StringBuffer buff = new StringBuffer();
		tokenType = MINUTES;
		buff.append('#');
		step();
		while (hereChar > 0 && (typeTable[hereChar] == XMINUTE))
		{
			buff.append('#');
			step();
		}
		if (hereChar > 0 && (typeTable[hereChar] == DOT))
		{
			String entier = buff.toString();
			buff = new StringBuffer();
			buff.append(entier.substring(0, entier.length() - 1));
			buff.append('0');
			tokenType = MINUTES_DECIMAL;
			buff.append((char) hereChar);
			step();
			while (hereChar > 0 && (typeTable[hereChar] == MINUTE_FRACTION))
			{
				buff.append('0');
				step();
			}
			token = buff.toString();
			token = ((String) token).replace('#', '0');

			// nouveau cas pas de point entre MM et mm !!!
		} else
		{
			if (hereChar > 0 && (typeTable[hereChar] == MINUTE_FRACTION))
			{

				String entier = buff.toString();
				buff = new StringBuffer();
				buff.append(entier.substring(0, entier.length() - 1));
				buff.append('0');
				tokenType = MINUTES_DECIMAL_SANS_POINT;
				buff.append(".");
				while (hereChar > 0 && (typeTable[hereChar] == MINUTE_FRACTION))
				{
					buff.append('0');
					step();
				}
				token = buff.toString();
				token = ((String) token).replace('#', '0');
			} else
			{
				token = buff.toString();
			}
		}
	}

	/**
	 * 
	 * @exception java.text.ParseException
	 */
	public void xSecond() throws ParseException
	{
		StringBuffer buff = new StringBuffer();
		tokenType = SECONDS;
		buff.append('#');
		step();
		while (hereChar > 0 && (typeTable[hereChar] == XSECOND))
		{
			buff.append('#');
			step();
		}
		token = buff.toString();
	}

	/**
	 * 
	 * @exception ParseException
	 */
	public void xSign() throws ParseException
	{
		tokenType = SIGN;
		step();
		token = "+";
	}

} /* End of class: AngleFormatParser */
