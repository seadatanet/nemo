package fr.ifremer.nemo.util;

import java.text.ParseException;

/**
 * Class Formatter
 *
 * Classe utilitaire utilisable notament par les generateurs pour formater les valeurs de 
 * sortie
 *
 * @author	Gerard BUNEL
 */
public  class Formatter implements Cloneable {
  static protected Formatter _singleton = null;

  static {
    _singleton = new Formatter();
  }

    /**
     *
     */
  public Formatter ( ) {
    
  }

  /**
   * Returns a copy of this Object
   *
   * @return  a copy of this object
   */
  public Object clone() throws CloneNotSupportedException {
    Formatter copy = null;
    copy = (Formatter) super.clone();
    return copy;
  }

  /**
   * Remove any link for role singleton
   */
  public static void releaseSingleton() {
    setSingleton( null );
  }

  /**
   * returns the linked object for role <% aRole name%>
   *
   * @return  the object linked
   */
   public static Formatter getSingleton() {
    return _singleton;
  }

  /**
   * Create a new link with x for role singleton
   *
   * @param  x  the object to link with
   */
  public static void setSingleton(Formatter x) {
    if (_singleton == x) return;
    _singleton = x;
  }

  /**
   *
   * @param  format  Format de sortie de la date/heure
   * @param  date  Date a formater. Nombre de millisecondes depuis 0h00 GMT 01/01/1970
   * @return  String
   * @exception  ParseException
   */
  public String formatDateTime ( String format, float date) throws ParseException {
    return formatDateTime( format, new Float(date).longValue());
  }

  /**
   *
   * @param  format  Format de sortie de la date/heure
   * @param  date  Date a formater. Nombre de millisecondes depuis 0h00 GMT 01/01/1970
   * @param  sdefault  
   * @return  date/heure formatee
   * @exception  ParseException
   */
  public String formatDateTime ( String format, float date, String sdefault) throws ParseException {
    if (Float.isNaN(date)) {
    	return sdefault;
    } else {
    	return formatDateTime ( format, date);
    }
  }

  /**
   *
   * @param  format  Format de sortie de la date/heure
   * @param  date  Date a formater. Nombre de millisecondes depuis 0h00 GMT 01/01/1970
   * @return  String
   * @exception  ParseException
   */
  public String formatDateTime ( String format, long date) throws ParseException {
    DateTimeFormat fmt = new DateTimeFormat(format);
    return fmt.format(date);
  }

  /**
   *
   * @param  format  Le format de sortie
   * @param  a_Float  la valeur flottante a formater
   * @return  String
   */
  public String formatFloat ( String format, float a_Float) {
    if (Float.isNaN(a_Float)) {
    	return String.format( java.util.Locale.US, format, a_Float).replace("NaN", "   ");
    } else {
    	return String.format( java.util.Locale.US, format, a_Float);
    }
  }

  /**
   *
   * @param  format  Le format de sortie
   * @param  a_Float  la valeur flottante a formater
   * @return  valeur flotante formatee
   */
  public String formatInt ( String format, float a_Float) {
    if (Float.isNaN(a_Float)) {
    	return formatInt( format, 0).replace('0', ' ');
    } else {
    	return formatInt( format, (int)a_Float);
    }
  }

  /**
   *
   * @param  format  Le format de sortie
   * @param  a_Int  la valeur flottante a formater
   * @return  String
   */
  public String formatInt ( String format, int a_Int) {
    return String.format( java.util.Locale.US, format, a_Int);
  }

  /**
   *
   * @param  format  Format de sortie de la latitude
   * @param  a_Latitude  latitude a formater
   * @return  latitude formatee
   * @exception  ParseException
   */
  public String formatLatitude ( String format, double a_Latitude) throws ParseException {
    LatitudeFormat fmt = new LatitudeFormat(format);
    return fmt.format(a_Latitude);
  }

  /**
   *
   * @param  format  Format de sortie de la longitude
   * @param  a_Longitude  Longitude a formater
   * @return  longitude formatee
   * @exception  ParseException
   */
  public String formatLongitude ( String format, double a_Longitude) throws ParseException {
    LongitudeFormat fmt = new LongitudeFormat(format);
    return fmt.format(a_Longitude);
  }

  /**
   *
   * @param  a_String  
   * @param  length  
   * @return  String
   */
  public String limitString ( String a_String, int length) {
    if (a_String.length() > length) {
    	return a_String.substring( 0, length);
    }
    return a_String.trim();
  }

  /**
   *
   * @param  a_Int  
   * @param  length  
   * @return  String
   */
  public String padInt ( int a_Int, int length) {
    String a_String = Integer.toString(a_Int);
    return padString( a_String, length);
  }

  /**
   *
   * @param  a_String  
   * @param  length  
   * @return  String
   */
  public String padString ( String a_String, int length) {
    if (a_String.length() > length) {
    	return a_String.substring( 0, length);
    } else {
    	StringBuffer buff = new StringBuffer(a_String);
    	for (int i=a_String.length(); i<length; i++) {
    		buff.append(' ');
    	}
    	return buff.toString();
    }
  }

} /* End of class: Formatter */
