/* ============================================================================
 * Nom du fichier	: Station.java
 * Application      : NEMO version 1.2.20
 * Auteur			: dboulang
 * Date	de création : 17 déc. 09
 * Client			: Ifremer
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.text.ParseException;
import java.util.HashMap;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Global.E_InputType;

/**
 * Class Station.java Description: description d'une station
 * 
 * @author dboulang
 */
public class Station implements Comparable
{

    protected String _reference;
    protected int _nbRecords;

    protected double _latitude;
    protected double _longitude;
    protected long _date;
    protected long _time;
    protected float _offsetTU;

    // pour Timeseries et Trajectory
    protected float _sensorDepth;
    protected float _samplingRate;
    
    // pour Trajectory
    protected double _endlatitude;
    protected double _endlongitude;
    protected float _enddepth;
    

    protected HashMap<String, String> _metaFlags;
    protected HashMap<String, String> _globalParamQc;
    protected HashMap<String, Boolean> _missingValueParams;
    
    protected String _fileName;

    public Station(String fileName, String[] inputStationHeader) throws ParseException
    {
        _fileName = fileName;
        _reference = null;
        _nbRecords = 0;
        _metaFlags = new HashMap<String, String>();
        
        _endlatitude = Double.NaN;
        _endlongitude = Double.NaN;
        _enddepth = Float.NaN;

        StationDomain stationDomain = Nemo.getInstance().getDomain().getStation();

        _latitude = ((Double) stationDomain.getModel().getProperty(Intern.S_LAT).applyOn(inputStationHeader)).doubleValue();
        _longitude = ((Double) stationDomain.getModel().getProperty(Intern.S_LON).applyOn(inputStationHeader)).doubleValue();
        _date = ((Long) stationDomain.getModel().getProperty(Intern.S_DATE).applyOn(inputStationHeader)).longValue();
        
        Object time = stationDomain.getModel().getProperty(Intern.S_TIME).applyOn(inputStationHeader);
        
        if (time.toString().equals("Default"))
            _time = -1;
        else
            _time = (Long) time;
        
        _offsetTU = ((Float) stationDomain.getModel().getProperty(Intern.S_OFFSETTU).applyOn(inputStationHeader)).floatValue();
        //--float depth = ((Float) stationDomain.getModel().getProperty(Intern.S_DEPTH).applyOn(inputStationHeader)).floatValue();
        //-- recup valeur par defaut
        
        addMetaFlag(Intern.DATETIME, stationDomain.getMetaFlag(Intern.DATETIME, inputStationHeader));
        addMetaFlag(Intern.S_LAT, stationDomain.getMetaFlag(Intern.S_LAT, inputStationHeader));
        addMetaFlag(Intern.S_LON, stationDomain.getMetaFlag(Intern.S_LON, inputStationHeader));
        // TODO FAE Depth par défaut (flags) si "defaut" = > Global.DEFAULT_FLAG sinon getMetaFlag
        addMetaFlag(Intern.S_DEPTH, stationDomain.getMetaFlag(Intern.S_DEPTH, inputStationHeader));

        if (Nemo.getInstance().getDomain().getMedia().getModel().getInputType() != E_InputType.PROFILE)
        {
            _sensorDepth = ((Float) stationDomain.getModel().getProperty(Intern.S_SENSORDEPTH).applyOn(inputStationHeader)).floatValue();
            _samplingRate = ((Float) stationDomain.getModel().getProperty(Intern.S_SAMPLINGRATE).applyOn(inputStationHeader)).floatValue();
            
            String conversionFormula = stationDomain.getModel().getProperty(Intern.S_SAMPLINGRATE).getConversionFormula();

            if (conversionFormula != null && conversionFormula != "")
            {
            org.nfunk.jep.JEP parser =
                new org.nfunk.jep.JEP();
            parser.addStandardFunctions();
            parser.addStandardConstants();
            parser.addVariable(Global.STR_CONVERT,
                _samplingRate);
            parser
            .parseExpression(Global.STR_CONVERT
                + conversionFormula);
            
            _samplingRate = (float) parser.getValue();
            }
            
            addMetaFlag(Intern.S_SENSORDEPTH, stationDomain.getMetaFlag(Intern.S_SENSORDEPTH, inputStationHeader));
            addMetaFlag(Intern.ENDDATETIME, stationDomain.getMetaFlag(Intern.ENDDATETIME, inputStationHeader));
            addMetaFlag(Intern.S_ELAT, stationDomain.getMetaFlag(Intern.S_ELAT, inputStationHeader));
            addMetaFlag(Intern.S_ELON, stationDomain.getMetaFlag(Intern.S_ELON, inputStationHeader));
            addMetaFlag(Intern.S_EDEPTH, stationDomain.getMetaFlag(Intern.S_EDEPTH, inputStationHeader));
            addMetaFlag(Intern.S_SENSORDEPTH, stationDomain.getMetaFlag(Intern.S_SENSORDEPTH, inputStationHeader));
            addMetaFlag(Intern.DISTANCETOBOTTOM, calculateMetaFlag(Intern.S_DEPTH, Intern.S_SENSORDEPTH));
            addMetaFlag(Intern.DURATION, calculateMetaFlag(Intern.DATETIME, Intern.ENDDATETIME));
        }

        _globalParamQc = new HashMap<String, String>();
        _missingValueParams = new HashMap<String, Boolean>();
        DataDomain dataDomain = Nemo.getInstance().getDomain().getData();
        for (ParameterModel paramModel : dataDomain.getMedParameterModels()){
            _globalParamQc.put(paramModel.getParameter().getCode(), ((AddedParameter)paramModel.getParameter()).getIdParam().getFlag(this, null));
            _missingValueParams.put(paramModel.getParameter().getCode(), true);
        }
        for (ParameterModel paramModel : dataDomain.getModel().getParameterModels()){
            _globalParamQc.put(paramModel.getParameter().getCode(),Global.WORSE_FLAG);
            _missingValueParams.put(paramModel.getParameter().getCode(), true);
        }
    }

    public void updateGlobalParamQc(String inputLine){
        DataDomain dataDomain = Nemo.getInstance().getDomain().getData();
        // les qcFlags des paramètres ajoutés pour MEDATLAS ne varient pas (= MetaFlags)
        String qcFlag = null;
        for (ParameterModel paramModel : dataDomain.getModel().getParameterModels()){
            qcFlag = paramModel.retreiveParameterFlag(inputLine);

            if (!qcFlag.equals(Global.DEFAULT_FLAG) 
                    && !qcFlag.equals(Global.ZERO) 
                    && qcFlag.compareTo(_globalParamQc.get(paramModel.getParameter().getCode())) < 0){
                _globalParamQc.put(paramModel.getParameter().getCode(), qcFlag);
            }
            

            // missingValue
            if (!qcFlag.equals(Global.DEFAULT_FLAG)){ 
                _missingValueParams.put(paramModel.getParameter().getCode(), false);
            }
            
        }
    }

    public String getGlobalParamQc(){
        StringBuffer globalParamQc = new StringBuffer();

        DataDomain dataDomain = Nemo.getInstance().getDomain().getData();
        String globalQc = null;
        for (ParameterModel paramModel : dataDomain.getMedParameterModels()){
            globalQc = _globalParamQc.get(paramModel.getParameter().getCode());
            if (globalQc.equals(Global.WORSE_FLAG)) 
                globalQc = Global.ZERO;
            globalParamQc.append(globalQc);
        }
        for (ParameterModel paramModel : dataDomain.getModel().getParameterModels()){
            globalQc = _globalParamQc.get(paramModel.getParameter().getCode());
            if (_missingValueParams.get(paramModel.getParameter().getCode()))
                globalQc = Global.DEFAULT_FLAG;
            else if (globalQc.equals(Global.WORSE_FLAG)) 
                globalQc = Global.ZERO;
            globalParamQc.append(globalQc);
        }

        return globalParamQc.toString();
    }

    public String getGlobalProfileQc(){
        String globalProfileQC = Global.WORSE_FLAG;
        boolean missingValue = true;
        
        for (String qc : _globalParamQc.values()){
            if (!qc.equals(Global.ZERO) && !qc.equals(Global.DEFAULT_FLAG)
                    && qc.compareTo(globalProfileQC) < 0)
                globalProfileQC = qc;

            if (!qc.equals(Global.DEFAULT_FLAG))
                missingValue = false;
        }

        if (globalProfileQC.equals(Global.WORSE_FLAG))
            globalProfileQC = Global.ZERO;
        
        if (missingValue)
            globalProfileQC = Global.DEFAULT_FLAG;
        
        return globalProfileQC;
    }
    
    public void completeMedatlasTrajectory(String inputLine) throws NumberFormatException, StringIndexOutOfBoundsException, ParseException{
        StationDomain stationDomain= Nemo.getInstance().getDomain().getStation();
        
        // ENDLAT => LATX
        _endlatitude = (Double)stationDomain.getModel().getProperty(Intern.S_LAT).applyOnLine(inputLine);
        
        // ENDLON => LONX
        _endlongitude = (Double)stationDomain.getModel().getProperty(Intern.S_LON).applyOnLine(inputLine);
        
        // ENDDEPTH => BATH si inData
        if (stationDomain.getModel().getProperty(Intern.S_DEPTH).isInData()){
            _enddepth = (Float)stationDomain.getModel().getProperty(Intern.S_DEPTH).applyOnLine(inputLine);
        }
    }

    /**
     * Getter pour nbRecords.
     * 
     * @return int
     * @see Station.java#nbRecords
     */
    public int getNbRecords(){
        return _nbRecords;
    }

    /**
     * Setter pour nbRecords
     * 
     * @param nbRecords :
     *            nouvelle valeur pour nbRecords
     * @see Station.java#nbRecords
     */
    public void setNbRecords(int nbRecords){
        _nbRecords = nbRecords;
    }

    /**
     * Getter pour reference.
     * 
     * @return String
     * @see Station.java#reference
     */
    public String getReference(){
        return _reference;
    }

    /**
     * Setter pour reference
     * 
     * @param reference :
     *            nouvelle valeur pour reference
     * @see Station.java#reference
     */
    public void setReference(String reference){
    //    System.out.println(reference);
        _reference = reference;
    }

    /**
     * Getter pour _date.
     * 
     * @return long
     * @see Station.java#_date
     */
    public long getDate(){
        return _date;
    }

    /**
     * Setter pour _date
     * 
     * @param date :
     *            nouvelle valeur pour date
     * @see Station.java#_date
     */
    public void setDate(long date){
        _date = date;
    }

    /**
     * Getter pour _latitude.
     * 
     * @return double
     * @see Station.java#_latitude
     */
    public double getLatitude(){
        return _latitude;
    }

    /**
     * Setter pour _latitude
     * 
     * @param latitude :
     *            nouvelle valeur pour latitude
     * @see Station.java#_latitude
     */
    public void setLatitude(double latitude){
        _latitude = latitude;
    }

    /**
     * Getter pour _longitude.
     * 
     * @return double
     * @see Station.java#_longitude
     */
    public double getLongitude(){
        return _longitude;
    }

    /**
     * Setter pour _longitude
     * 
     * @param longitude :
     *            nouvelle valeur pour longitude
     * @see Station.java#_longitude
     */
    public void setLongitude(double longitude){
        _longitude = longitude;
    }
    
    private String calculateMetaFlag(String initial1, String initial2){

        String flag1 = getMetaFlag(initial1);
        String flag2 = getMetaFlag(initial2);
        String value;

        if (flag1.equals(Global.ZERO))
        {
            value = flag1;
        }
        else if (flag2.equals(Global.ZERO))
        {
            value = flag2;
        }
        else if (flag1.compareTo(flag2) > 0)
        {
            value = flag2;
        }
        else
        {
            value = flag1;
        }
        
        return value;
    }

    public String getMetaFlag(String key){
        String flag = Global.ZERO;

        if (getMetaFlags() != null)
        {
            if ((getMetaFlags().containsKey(key))
                    && (getMetaFlags().get(key) != null))
            {
                flag = getMetaFlags().get(key);
            }
        }

        return flag;
    }

    /**
     * Getter pour _metaFlags.
     * 
     * @return HashMap<String,String>
     * @see Station.java#_metaFlags
     */
    public HashMap<String, String> getMetaFlags(){
        return _metaFlags;
    }

    public void addMetaFlag(String key, String metaFlag){
        _metaFlags.put(key, metaFlag);
    }

    /**
     * Setter pour _metaFlags
     * 
     * @param metaFlags :
     *            nouvelle valeur pour metaFlags
     * @see Station.java#_metaFlags
     */
    public void setMetaFlags(HashMap<String, String> metaFlags){
        _metaFlags = metaFlags;
    }

    /**
     * Getter pour _offsetTU.
     * 
     * @return float
     * @see Station.java#_offsetTU
     */
    public float getOffsetTU(){
        return _offsetTU;
    }

    /**
     * Setter pour _offsetTU
     * 
     * @param offsetTU :
     *            nouvelle valeur pour offsetTU
     * @see Station.java#_offsetTU
     */
    public void setOffsetTU(float offsetTU){
        _offsetTU = offsetTU;
    }

    /**
     * Getter pour _sensorDepth.
     * 
     * @return float
     * @see Station.java#_sensorDepth
     */
    public float getSensorDepth(){
        return _sensorDepth;
    }

    /**
     * Setter pour _sensorDepth
     * 
     * @param sensorDepth :
     *            nouvelle valeur pour sensorDepth
     * @see Station.java#_sensorDepth
     */
    public void setSensorDepth(float sensorDepth){
        _sensorDepth = sensorDepth;
    }

    /**
     * Getter pour _time.
     * 
     * @return long
     * @see Station.java#_time
     */
    public long getTime(){
        return _time;
    }

    /**
     * Setter pour _time
     * 
     * @param time :
     *            nouvelle valeur pour time
     * @see Station.java#_time
     */
    public void setTime(long time){
        _time = time;
    }

    /**
     * Getter pour _samplingRate.
     * 
     * @return float
     * @see Station.java#_samplingRate
     */
    public float getSamplingRate(){
        return _samplingRate;
    }
    
    /**
     * Setter pour _samplingRate
     * 
     * @param samplingRate :
     *            nouvelle valeur pour samplingRate
     * @see Station.java#_samplingRate
     */
    public void setSamplingRate(float samplingRate){
        _samplingRate = samplingRate;
    }
    
    public void calculSamplingRate(String inputLine) throws ParseException{        
        long ecart = (DateTimeUtils.convertTimestamp(inputLine) - DateTimeUtils.getFullDate(this)) / 1000;
        if (ecart > 0){
            _samplingRate = ecart;
        }
    }

    public int compareTo(Object other){
        Integer refIniInteger;
        Integer refOtherInteger;
        
        try
        {
        refIniInteger = Integer.valueOf(this.getReference());
        refOtherInteger = Integer.valueOf(((Station)other).getReference());
        }
        catch(NumberFormatException e)
        {
            String refIni = this.getReference();
            String refOther = ((Station)other).getReference();
            
            return refIni.compareTo(refOther);
        }

        return refIniInteger.compareTo(refOtherInteger);
    }

    /**
     * Getter pour _fileName.
     * @return String
     * @see Station.java#_fileName
     */
    public String getFileName()
    {
        return _fileName;
    }

    /**
     * Setter pour _fileName
     * @param fileName : nouvelle valeur pour fileName
     * @see Station.java#_fileName
     */
    public void setFileName(String fileName)
    {
        _fileName = fileName;
    }

    /**
     * Getter pour _enddepth.
     * @return float
     * @see Station.java#_enddepth
     */
    public float getEnddepth()
    {
        return _enddepth;
    }

    /**
     * Getter pour _endlatitude.
     * @return double
     * @see Station.java#_endlatitude
     */
    public double getEndlatitude()
    {
        return _endlatitude;
    }

    /**
     * Getter pour _endlongitude.
     * @return double
     * @see Station.java#_endlongitude
     */
    public double getEndlongitude()
    {
        return _endlongitude;
    }

}
