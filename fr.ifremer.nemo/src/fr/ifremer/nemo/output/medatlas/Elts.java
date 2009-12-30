/* ============================================================================
 * Nom du fichier	: ParseElements.java
 * Application      : NEMO version 1.2.20
 * Auteur			: dboulang
 * Date	de création : 11 déc. 09
 * Client			: Ifremer
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.output.medatlas;

import java.text.ParseException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;

/**
 * Class ParseElements.java
 * Description: TODO
 * @author dboulang 
 */
public class Elts extends NLS
{
    private static final String BUNDLE_NAME =
        "fr.ifremer.nemo.output.medatlas.ParseElements"; //$NON-NLS-1$
    
    private static String SEP = "__"; //$NON-NLS-1$
        
    public static String kw_cruise_comment;
    public static String kw_station_addTimeSeries;
    public static String station_line;
    
    public static String cruise_ref;
    public static String cruise_name;
    public static String cruise_shipcode;
    public static String cruise_shipname;
    public static String cruise_region;
    public static String cruise_country;
    public static String cruise_labo;
    public static String cruise_chiefscientist;
    public static String cruise_project;
    public static String cruise_datacenter;
    public static String cruise_availability;
    
    public static String datatype_code;
    public static String datatype_nbprofile;
    public static String qc;
    
    public static String station_ref;
    public static String station_date;
    public static String station_time;
    public static String station_lat;
    public static String station_lon;
    public static String station_depth;
    public static String station_param;
    public static String station_records;
    public static String param_code;
    public static String param_fullname;
    public static String param_unit;
    public static String param_default;
    public static String station_gblProfile;
    public static String station_gblParam;
    public static String station_dcHistory;
    public static String station_dmHistory;
    public static String station_edmo;
    public static String station_localCdiId;
    public static String station_surface;
    
    public static String station_endDate;
    public static String station_endTime;
    public static String station_endLat;
    public static String station_endLon;
    public static String station_endDepth;
    public static String station_sensor;
    public static String station_bottomDepth;
    public static String station_duration;
    public static String station_sampling;
    public static String station_magnetic;
    
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Elts.class);
    }

    private Elts()
    {
    }

    public static String getString(String key)
    {
        ResourceBundle resource = ResourceBundle.getBundle(BUNDLE_NAME);
        return resource.getString(key);
    }
    
    public static String getKeyword(String sParseElt){
        String keyword = Global.EMPTY_STRING;
        String[] parseElt = sParseElt.split(SEP);
        if (parseElt.length > 2) keyword = parseElt[2];
        return keyword;        
    }
    
    public static int getLength(String sParseElt){
        String[] parseElt = sParseElt.split(SEP);
        return Integer.parseInt(parseElt[1]);        
    }
    
    public static String limit(String value, String sParseElt){
        int maxLen = getLength(sParseElt);
        return Formatter.getSingleton().limitString(value, maxLen);
    }
    
    @SuppressWarnings("nls")
    public static String getElt(String sParseElt, Object value) throws ParseException{
        Formatter form = Formatter.getSingleton();
        StringBuffer elt = new StringBuffer();
        
        String[] parseElt = sParseElt.split(SEP);
        // parseElt[0] = type;format
        // parseElt[1] = length
        // parseElt[2] = keyword
        // parseElt[3] = unit
        String[] format = parseElt[0].split(Global.SEMICOLON);
        
        
        if (parseElt.length > 2) elt.append(parseElt[2]);
        
        if (format.length > 1){
            // doit appliquer un format
            if (format[0].equals("DATETIME")){
                if (format.length > 2){
                    elt.append(form.formatDateTime(format[1], (Long) value, format[2]));
                }
                else{                    
                    elt.append(form.formatDateTime(format[1], (Long) value));
                }
            }
            else if(format[0].startsWith("LAT")){
                elt.append(form.formatLatitude(format[1], (Double) value));
            }
            else if(format[0].startsWith("LON")){
                elt.append(form.formatLongitude(format[1], (Double) value));
            }
            else {
                elt.append(form.formatInt(parseElt[0], (Float) value));
            }
        }
        else{
            // régularise la longeur de l'élément
            int len = Integer.parseInt(parseElt[1]);
            if (len == 0) len = ((String) value).length();
            if (format[0].equals("ZERO")){
                elt.append(form.padZero((String) value, len));
            }
            else{            
                elt.append(form.padString((String) value, len));
            }
        }
        
        if (parseElt.length > 3) elt.append(parseElt[3]);
        
        return elt.toString();
    }
    /*
    public static String getElt(String keyword, String value, String length){
        StringBuffer elt = new StringBuffer();
        if (keyword != null) elt.append(keyword);
        elt.append(Formatter.getSingleton().padString(value, Integer.parseInt(length)));
        return elt.toString();
    }
    
    public static String getNumElt(String keyword, String value, String length){
        StringBuffer elt = new StringBuffer();
        if (keyword != null) elt.append(keyword);
        elt.append(Formatter.getSingleton().padZero(value, Integer.parseInt(length)));
        return elt.toString();
    }
    */
}
