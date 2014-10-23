package fr.ifremer.nemo.util;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.format.DateTimeFormat;
import fr.ifremer.nemo.util.format.LatitudeFormat;
import fr.ifremer.nemo.util.format.LongitudeFormat;

/**
 * Class Formatter
 * 
 * Classe utilitaire utilisable notament par les generateurs pour formater les
 * valeurs de sortie
 * 
 * @author Gerard BUNEL
 */
public class Formatter implements Cloneable
{

	static protected Formatter _singleton = null;

	private static final String SPACES = "   "; //$NON-NLS-1$
	private static final String NaN = "NaN"; //$NON-NLS-1$

	public static final int StationNumberLength_MEDATLAS = 5;
	public static final int StationNumberLength_ODV = 55;

	static
	{
		_singleton = new Formatter();
	}

	/**
	 * 
	 */
	public Formatter()
	{

	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		Formatter copy = null;
		copy = (Formatter) super.clone();
		return copy;
	}

	/**
	 * Remove any link for role singleton
	 */
	public static void releaseSingleton()
	{
		setSingleton(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public static Formatter getSingleton()
	{
		return _singleton;
	}

	/**
	 * Create a new link with x for role singleton
	 * 
	 * @param x
	 *            the object to link with
	 */
	public static void setSingleton(Formatter x)
	{
		if (_singleton == x)
			return;
		_singleton = x;
	}

	/**
	 * 
	 * @param format
	 *            Format de sortie de la date/heure
	 * @param date
	 *            Date a formater. Nombre de millisecondes depuis 0h00 GMT
	 *            01/01/1970
	 * @return String
	 * @exception ParseException
	 * @throws UnsupportedEncodingException
	 */
	public String formatDateTime(String format, float date) throws ParseException, UnsupportedEncodingException
	{
		return formatDateTime(format, new Float(date).longValue());
	}

	/**
	 * 
	 * @param format
	 *            Format de sortie de la date/heure
	 * @param date
	 *            Date a formater. Nombre de millisecondes depuis 0h00 GMT
	 *            01/01/1970
	 * @param sdefault
	 * @return date/heure formatee
	 * @exception ParseException
	 * @throws UnsupportedEncodingException
	 */
	public String formatDateTime(String format, float date, String sdefault) throws ParseException, UnsupportedEncodingException
	{
		if (Float.isNaN(date))
		{
			return sdefault;
		}
		return formatDateTime(format, date);
	}

	/**
	 * 
	 * @param format
	 *            Format de sortie de la date/heure
	 * @param date
	 *            Date a formater. Nombre de millisecondes depuis 0h00 GMT
	 *            01/01/1970
	 * @return String
	 * @exception ParseException
	 * @throws UnsupportedEncodingException
	 */
	public String formatDateTime(String format, long date) throws ParseException, UnsupportedEncodingException
	{
		DateTimeFormat fmt = new DateTimeFormat(format);
		return fmt.format(date);
	}

	/**
	 * 
	 * @param format
	 *            Le format de sortie
	 * @param a_Float
	 *            la valeur flottante a formater
	 * @return String
	 */
	public String formatFloat(String format, float a_Float)
	{
		if (Float.isNaN(a_Float))
		{
			return String.format(java.util.Locale.US, format, a_Float).replace(NaN, SPACES);
		}
		return String.format(java.util.Locale.US, format, a_Float);
	}

	/**
	 * 
	 * @param format
	 *            Le format de sortie
	 * @param a_Float
	 *            la valeur double a formater
	 * @return String
	 */
	public String formatDouble(String format, Double a_Double)
	{
		if (Double.isNaN(a_Double))
		{
			return String.format(java.util.Locale.US, format, a_Double).replace(NaN, SPACES);
		}
		return String.format(java.util.Locale.US, format, a_Double);
	}

	public String formatParameter(String format, String a_Double)
	{
	    
	    Double value = Double.NaN;
	  
	        if(format.length()!=0)
            {
    	        value = Double.parseDouble(a_Double);
    	        if (Double.isNaN(value))
    	        {
    	            return String.format(java.util.Locale.US, format, value).replace(NaN, SPACES);
    	        }
	        
    	        String type = format.substring(format.length() - 1);
    	        if (type.equals("d") || type.equals("D")) { //$NON-NLS-1$ //$NON-NLS-2$
    	            return String.format(java.util.Locale.US, format, value.intValue());
    	        }
    	        return String.format(java.util.Locale.US, format, value);
	        }
	        //FAE 10755 - retourne la chaine texte si impossible de la caster en Double
	        else
	            return a_Double;
	   
	    
		
	}

	/**
	 * 
	 * @param format
	 *            Le format de sortie
	 * @param a_Float
	 *            la valeur flottante a formater
	 * @return valeur flotante formatee
	 */
	public String formatInt(String format, float a_Float)
	{
		if (Float.isNaN(a_Float))
		{
			return formatInt(format, 0).replace('0', ' ');
		}
		return formatInt(format, Math.round(a_Float));
	}

	/**
	 * 
	 * @param format
	 *            Le format de sortie
	 * @param a_Int
	 *            la valeur flottante a formater
	 * @return String
	 */
	public String formatInt(String format, int a_Int)
	{
		return String.format(java.util.Locale.US, format, a_Int);
	}

	public String formatBoolean(boolean value)
	{
		String result = Global.EMPTY_STRING;
		if (value)
			result = Global.YES;
		else
			result = Global.NO;
		return result;
	}

	public String formatParamValue(String format, Double a_Double)
	{
		String result = Global.EMPTY_STRING;
		if (Double.isNaN(a_Double))
		{
			result = formatInt(format, 0).replace('0', ' ');
		} else
		{
			result = formatInt(format, a_Double.intValue());
		}
		return result;
	}

	/**
	 * 
	 * @param format
	 *            Format de sortie de la latitude
	 * @param a_Latitude
	 *            latitude a formater
	 * @return latitude formatee
	 * @exception ParseException
	 * @throws UnsupportedEncodingException
	 */
	public String formatLatitude(String format, double a_Latitude) throws ParseException, UnsupportedEncodingException
	{
		LatitudeFormat fmt = new LatitudeFormat(format);
		return fmt.format(a_Latitude);
	}

	/**
	 * 
	 * @param format
	 *            Format de sortie de la longitude
	 * @param a_Longitude
	 *            Longitude a formater
	 * @return longitude formatee
	 * @exception ParseException
	 * @throws UnsupportedEncodingException
	 */
	public String formatLongitude(String format, double a_Longitude) throws ParseException, UnsupportedEncodingException
	{
		LongitudeFormat fmt = new LongitudeFormat(format);
		return fmt.format(a_Longitude);
	}

	/**
	 * 
	 * @param a_String
	 * @param length
	 * @return String
	 */
	public String limitString(String a_String, int length)
	{
		if (a_String.length() > length)
		{
			return a_String.substring(0, length);
		}
		return a_String.trim();
	}

	/**
	 * 
	 * @param a_Int
	 * @param length
	 * @return String
	 */
	public String padInt(int a_Int, int length)
	{
		String a_String = Integer.toString(a_Int);
		return padString(a_String, length);
	}

	/**
	 * 
	 * @param a_String
	 * @param length
	 * @return String
	 */
	public String padString(String a_String, int length)
	{
		return genericPadString(a_String, length, ' ', false);
	}

	/**
	 * 
	 * @param a_String
	 * @param length
	 * @return String
	 */
	public String padStringLeft(String a_String, int length)
	{
		return genericPadString(a_String, length, ' ', true);
	}

	/**
	 * 
	 * @param a_String
	 * @param length
	 * @return String
	 */
	public String padZero(String a_String, int length)
	{
		return genericPadString(a_String, length, '0', true);
	}

	public String genericPadString(String a_String, int maxLength, char padChar, boolean left)
	{
		if (a_String.length() > maxLength)
		{
			return a_String.substring(0, maxLength);
		}
		StringBuffer buff = new StringBuffer(a_String);
		for (int i = a_String.length(); i < maxLength; i++)
		{
			if (left)
				buff.insert(0, padChar);
			else
				buff.append(padChar);
		}
		return buff.toString();
	}

	public String formatStationNumber(String stNumber)
	{
		// 6406
		String res = stNumber;
		E_TypeConversion conv = RulesManager.getInstance().getMyConversionType();
		if (conv == E_TypeConversion.MEDATLAS)
		{
			res = padZero(stNumber, StationNumberLength_MEDATLAS);
		} else if (conv == E_TypeConversion.ODV_MONOSTATION || conv == E_TypeConversion.ODV_MULTISTATIONS
		        || conv == E_TypeConversion.NETCDF_MONOSTATION || conv == E_TypeConversion.NETCDF_MULTISTATIONS)
		{
			if (res.length() > StationNumberLength_ODV)
			{
				res = res.substring(0, StationNumberLength_ODV);
			}
		}
		return res;
	}
}