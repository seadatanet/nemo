/* ============================================================================
 * Nom du fichier	: AddedParameter.java
 * Auteur			: dboulang
 * Date				: 9 févr. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.input.DataModel;
import fr.ifremer.nemo.input.Parameter;
import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.format.JulianDate;
import fr.ifremer.nemo.util.format.LatitudeFormat;
import fr.ifremer.nemo.util.format.LongitudeFormat;
import fr.ifremer.nemo.util.presdepth.CalcPres;

/**
 * Description: AddedParameter - paramètres ajoutés automatiquement par NEMO
 * @author dboulang
 *
 */
public class AddedParameter extends Parameter
{

    @SuppressWarnings("nls")
    public static enum IdParameter{
        DEPH, YEAR, MNTH, DAYX, TIME, CJDY, DTUT, LATX, LONX, BATH, PRES;
        
        public String getDefault(){
            switch (this){
                case YEAR : return "9999";
                case MNTH : return "99";
                case DAYX : return "99";
                case TIME : return "999999";
                case LATX : return "+99.999999";
                case LONX : return "+999.999999";
                case BATH : return "-999.9";
                case PRES : return "-999.9";
                default: return Global.ZERO;
            }
        }
        
        public String getFormat(){        
            switch (this){
                case DEPH : 
                    InputDomain iDomain = Nemo.getInstance().getDomain();
                    if (iDomain.getMedia().getModel().getInputType() == E_InputType.PROFILE){
                        ParameterModel pModelPRES = iDomain.getData().getModel().getParameterModel(Intern.paramPRES);
                        return pModelPRES.getParameter().getFormat();
                    }                    
                    for (int i = 0; i < NemoLists.getParameters().length; i++) {
                        if (NemoLists.getParameters()[i].getCode().equals(this.toString())) {
                            return NemoLists.getParameters()[i].getFormat();
                        }
                    }
                    return Global.EMPTY_STRING;                    
                case YEAR : return "%4d";
                case MNTH : return "%02d";
                case DAYX : return "%02d";
                case TIME : return "hh24mmss";
                case CJDY : return "#0.000000";
                case DTUT : return "yyyy-MM-dd'T'HH:mm:ss.SSS";
                case LATX : return "+DD.dddddd";
                case LONX : return "+DDD.dddddd";
                case BATH : return "%6.1f";
                case PRES : return "%6.1f";
                default: return Global.EMPTY_STRING;
            }
        }
        
        public String getFullName(){
            switch (this){
                case DEPH : return "DEPTH BELOW SEA SURFACE";
                case YEAR : return "YEAR"; 
                case MNTH : return "MONTH";
                case DAYX : return "DAY WITHIN MONTH";
                case TIME : return "TIME WITHIN DAY";
                case CJDY : return "Chronological Julian Date";
                case DTUT : return "time_ISO8601";
                case LATX : return "LATITUDE";
                case LONX : return "LONGITUDE";
                case BATH : return "BATHYMETRIC DEPTH";
                case PRES : return "SEA PRESSURE sea surface=0";
                default: return Global.EMPTY_STRING;
            }
        }
        
        public String getUnit(){
            switch (this){
                case DEPH : return "m";
                case YEAR : return "yyyy";
                case MNTH : return "mm";
                case DAYX : return "dd";
                case TIME : return "hhmmss";
                case CJDY : return "days";
                case DTUT : return "yyyy-mm-ddThh:mm:ss.sss";
                case LATX : return "decimal degree";
                case LONX : return "decimal degree";
                case BATH : return "meter";
                case PRES : return "decibar=10000 pascals";
                default: return Global.EMPTY_STRING;
            }
        }
            
        public Conversion getCible(){
            switch (this){
                case DEPH :
                case CJDY :
                case DTUT :
                    return Conversion.ODV;
                default :
                    return Conversion.MEDATLAS;
            }
        }
        
        public String getFlag(Station currentStation,
                              String inputLine){

            InputDomain iDomain = Nemo.getInstance().getDomain();
            
            switch (this){
                case DEPH:
                    if (iDomain.getMedia().getModel().getInputType() == E_InputType.PROFILE){
                        return iDomain.getData().getModel().getParameterModel(Intern.paramPRES).retreiveParameterFlag(inputLine);
                    }
                    return currentStation.getMetaFlags().get(Intern.S_SENSORDEPTH);
                case LATX :
                    return currentStation.getMetaFlags().get(Intern.S_LAT);
                case LONX :
                    return currentStation.getMetaFlags().get(Intern.S_LON);
                case BATH :
                    return currentStation.getMetaFlags().get(Intern.S_DEPTH);
                case CJDY :
                case DTUT :
                case YEAR :
                case MNTH :
                case DAYX :
                case TIME :
                    return currentStation.getMetaFlags().get(Intern.DATETIME);
                // si PRES calculé, QC PRES = QC DEPH ??
//                case PRES :
//                    return sDomain.getData().getModel().getParameterModel(Intern.paramDEPH).retreiveParameterFlag(inputLine);
                default:
                    return Nemo.getInstance().getConfig().getDefaultFlag();
            }
        }
        
        public String getValue(Station currentStation,
                               String inputLine,
                               long currentDate) throws ParseException{
            InputDomain iDomain = Nemo.getInstance().getDomain();
            StationDomain sDomain = iDomain.getStation();
            
            String formatString = this.getFormat();
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);

            String stringValue = null;
            switch (this){
                case DEPH :
                    if (iDomain.getMedia().getModel().getInputType() == E_InputType.PROFILE){
                        
                        // récupération de la valeur de la pression
                        ParameterModel pModelPRES = iDomain.getData().getModel().getParameterModel(Intern.paramPRES);
                        pModelPRES.computeParameterValue(inputLine);
                        ParameterValue valuePRES = pModelPRES.getLineValue();                   
                        
                        // calculDepth
                        // FAE091 : calcul de la profondeur en fonction de la pression
                        double X, Gr, P, Depth;
    
                        P = valuePRES.getValue();
                        X = Math.sin(currentStation.getLatitude() / 57.29578);
                        double X2 = X * X;
    
                        Gr = 9.780318 * (1.0 + (5.2788E-3 + 2.36E-5 * X2) * X2) + 1.092E-6 * P;
                        Depth = (((-1.82E-15 * P + 2.279E-10) * P - 2.2512E-5) * P + 9.72659) * P;
                        Depth = Depth / Gr;
                        
                        stringValue = Formatter.getSingleton().formatDouble(formatString, Depth);
                    }else{
                        // = SensorDepth
                        stringValue = String.valueOf(currentStation.getSensorDepth());
                    }
                    break;
                case CJDY :
                    // julianDate = $stationModel.julianDate($station, $i)
                    JulianDate jDate = new JulianDate(currentDate);
                    stringValue = (new DecimalFormat(formatString)).format(jDate.getJour());
                    break;
                case DTUT :
                    // time-ISO8601
                    //SimpleDateFormat fDate = new SimpleDateFormat(formatString);
                    stringValue = DateTimeUtils.getVariantDateTime(currentDate);
                    break;
                case TIME :
                    // $formatter.formatDateTime( "hh24mmss", $station.parametersAt(0).valuesAt($i).date )
                    try{
                        stringValue = Formatter.getSingleton().formatDateTime(formatString, currentDate);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case YEAR :
                    // $formatter.formatInt( "%4d", $station.parametersAt(0).valuesAt($i).year)
                    stringValue = Formatter.getSingleton().formatInt(formatString, DateTimeUtils.getYear(currentDate));
                    break;
                case MNTH :
                    // $formatter.formatInt( "%02d", $station.parametersAt(0).valuesAt($i).month)
                    stringValue = Formatter.getSingleton().formatInt(formatString, DateTimeUtils.getMonth(currentDate));
                    break;
                case DAYX :
                    // $formatter.formatInt( "%02d", $station.parametersAt(0).valuesAt($i).day)
                    stringValue = Formatter.getSingleton().formatInt(formatString, DateTimeUtils.getDay(currentDate));
                    break;
                
                // paramètres ajoutés mais mesurés !!
                case LATX :
                    LatitudeFormat formatLAT = new LatitudeFormat(formatString, true);
                    stringValue = (new DecimalFormat("#0.000000", symbols)).format(formatLAT.parseObject(inputLine));
                    stringValue = Formatter.getSingleton().padStringLeft(stringValue, formatString.length());
                    break;
                case LONX :
                    LongitudeFormat formatLON = new LongitudeFormat(formatString, true);
                    stringValue = (new DecimalFormat("#0.000000", symbols)).format(formatLON.parseObject(inputLine));
                    stringValue = Formatter.getSingleton().padStringLeft(stringValue, formatString.length());                        
                    break;
                case BATH : 
                    String type = formatString.substring(formatString.length() - 1);
                    if (type.equals("f") || type.equals("F")){ 
                        stringValue = String.format(java.util.Locale.US, formatString, Float.parseFloat(inputLine));
                    }else if (type.equals("d") || type.equals("D")){
                        stringValue = String.format(java.util.Locale.US, formatString, Integer.parseInt(inputLine));
                    }
                    break;
                case PRES :
                    DataModel dataModel = iDomain.getData().getModel();
                    // récupération de la valeur de la profondeur
                    ParameterModel pModelDEPTH = dataModel.getParameterModel(Intern.paramDEPH);
                    pModelDEPTH.computeParameterValue(inputLine);
                    ParameterValue valueDEPTH = pModelDEPTH.getLineValue();  
                    
                    // récupération de la valeur de la salinite
                    ParameterModel pModelPSAL = dataModel.getParameterModel(Intern.paramPSAL);
                    pModelPSAL.computeParameterValue(inputLine);
                    ParameterValue valuePSAL = pModelPSAL.getLineValue();  
                    
                    // récupération de la valeur de la temperature
                    ParameterModel pModelTEMP = dataModel.getParameterModel(Intern.paramTEMP);
                    pModelTEMP.computeParameterValue(inputLine);
                    ParameterValue valueTEMP = pModelTEMP.getLineValue();  
                    
                    //récupération de la valeur de la latitude
                    double valueLAT = sDomain.getCurrentStation()._latitude;  
                                       
                    double pres = CalcPres.calcPres(valueLAT, valuePSAL.getValue(), valueDEPTH.getValue(), valueTEMP.getValue());
                    stringValue = Formatter.getSingleton().formatDouble(formatString, pres);              
                    break;
                    
            }
            return stringValue;
        }
        
        public boolean isMesured(){
            switch (this){
                case LATX :
                case LONX :
                case BATH :
                    return true;
                default :
                    return false;
            }
        }

        public String getPropertyCode(){  
            switch (this){
                case LATX : return Intern.S_LAT;
                case LONX : return Intern.S_LON;
                case BATH : return Intern.S_DEPTH;
                default : return null;
            }
        }
    }
    
    private IdParameter _idParam;

    public AddedParameter(IdParameter idParam)
    {
        _idParam = idParam;
        _code = idParam.toString();
        _default = idParam.getDefault();
        _format = idParam.getFormat();
        _fullName = idParam.getFullName();
        _unit = idParam.getUnit();
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        AddedParameter copy = null;
        copy = (AddedParameter) super.clone();
        return copy;
    }

    
    public PropertyModel getPropertyModel(StationModel stationModel){  
        PropertyModel propertyModel = null;
        String propertyCode = _idParam.getPropertyCode();
        if (propertyCode != null){
            propertyModel = stationModel.propertiesAt(propertyCode);
        }
        return propertyModel;
    }
    

    @SuppressWarnings("nls")
    public String compute(String fragmnt) 
        throws StringIndexOutOfBoundsException, ParseException {

        String paramValue = null;
        
        if (fragmnt.equals(Global.EMPTY_STRING)) {
            paramValue = Global.EMPTY_STRING;
        } else {
            if (_default.equals(fragmnt)) {
                paramValue = _default;
            }else {
                paramValue = _idParam.getValue(null, fragmnt, 0);
            }
        }
        return paramValue; 
    }

    /**
     * Getter sur idParam.
     * @return the idParam
     */
    public IdParameter getIdParam()
    {
        return _idParam;
    }


    /**
     * Setter sur idParam.
     * @param idParam the idParam to set
     */
    public void setIdParam(IdParameter idParam)
    {
        _idParam = idParam;
    }
}