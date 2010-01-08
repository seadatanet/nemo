/* ============================================================================
 * Nom du fichier	: StationDomain.java
 * Application      : NEMO version 1.2.20
 * Auteur			: Delphine BOULANGER
 * Date	de création : 30 nov. 09
 * Client			: Ifremer
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.DataType;
import fr.ifremer.nemo.input.Fragment;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.output.medatlas.Elts;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.format.DateTimeFormat;

/**
 * Class StationDomain.java
 * Description: TODO
 * @author Delphine BOULANGER 
 */
public class StationDomain
{
    /**
     * Informations descrivant l'onglet Station
     */
    protected StationModel _model;
    final private Formatter form;
    
    protected DataDomain _data;
    protected HashMap<String, List<Station>> _listStations;
    
    protected boolean _someInData;
    
    protected int _ref = -1;
    
    protected boolean _computed;
    
    public StationDomain(){
        form = Formatter.getSingleton();
        initValues();
    }
    
    public StationDomain(InputModel model){
        form = Formatter.getSingleton();
        initValues();
        update(model);
    }
    
    public void resetCurrent(){
        _ref = -1;
        //_currentStationValues = new HashMap<String, Object>();
        //_currentStationMetaFlags = new HashMap<String, String>();
    }
    
    public void initValues(){
        _model = new StationModel();
        _data = new DataDomain();
        _listStations = new HashMap<String, List<Station>>();
        _ref = -1;
        _someInData = false;
        //_currentStationValues = new HashMap<String, Object>();
        //_currentStationMetaFlags = new HashMap<String, String>();
    }
    
    public void update(InputModel model){
        updateStation(model.getStationModel());        
        _data.update(model);
    }
    
    public void updateStation(StationModel model){
        _model = model;
        _listStations = new HashMap<String, List<Station>>();
        updateRef();
        _someInData = false;
        for (PropertyModel pModel : _model.getProperties()){
            if (pModel.isInData()){
                _someInData = true;
                break;
            }
        }
    }
    
    public void updateRef(){
        if (_model.getReferenceProperty() != null
                && _model.getReferenceProperty().isUserProvided()
                && !_model.getReferenceProperty().getUserValue().trim().equals(Global.EMPTY_STRING))
            _ref = Integer.parseInt(_model.getReferenceProperty().getUserValue());
        else
            _ref = -1;
    }
    
    @SuppressWarnings("nls")
    public StringBuffer getOdvStationHeaderLabels(){
        StringBuffer odvStationHeader = new StringBuffer(); 
        odvStationHeader.append("Cruise");
        odvStationHeader.append(Global.TAB);
        odvStationHeader.append("Station");
        odvStationHeader.append(Global.TAB);
        odvStationHeader.append("Type");
        odvStationHeader.append(Global.TAB);
        odvStationHeader.append("yyyy-mm-ddThh:mm:ss.sss");
        odvStationHeader.append(Global.TAB);
        odvStationHeader.append("Longitude [degrees_east]");
        odvStationHeader.append(Global.TAB);
        odvStationHeader.append("Latitude [degrees_north]");
        odvStationHeader.append(Global.TAB);
        odvStationHeader.append("LOCAL_CDI_ID");
        odvStationHeader.append(Global.TAB);
        odvStationHeader.append("EDMO_CODE");
        odvStationHeader.append(Global.TAB);
        odvStationHeader.append("Bot. Depth [m]");
        odvStationHeader.append(Global.TAB);
        return odvStationHeader;
    }
    
    public StringBuffer getOdvEmptyStationHeader(){
        StringBuffer odvStationHeader = new StringBuffer(); 
        for (int i = 0; i < 9; i++){
            odvStationHeader.append(Global.TAB);
        }
        return odvStationHeader;
    }
    
    public String getReference(String inputFileName, String[] inputFile) throws ParseException{
        // STATION NUMBER
        String ref = Global.EMPTY_STRING;
        if ((_model.getReferenceProperty().isUserProvided()) && 
                (Nemo.getInstance().getDomain().getMedia().getModel().getInputType() != E_InputType.TRAJECTORY)){
            // incrementation du numéro
            ref = Integer.toString(_ref);
            _ref++;
        }
        else if (_model.getReferenceProperty().getFragment().getFirstLine() == 0){
            // dans le nom du fichier
            ref = (String) _model.getReferenceProperty().applyOn(new String[] {inputFileName});
        }
        else{
            // dans le Header
            ref = (String) _model.getReferenceProperty().applyOn(inputFile);
        }
        return ref;
    }
    
    public StringBuffer getHeader(CruiseDomain cDomain, Conversion c, Station currentStation, String[] inputFile) throws ParseException{
        StringBuffer stationHeader = new StringBuffer();        
        switch (c){
            case ODV:
                stationHeader = getOdvHeader(cDomain, currentStation, inputFile);
                break;
            case MEDATLAS:
                stationHeader = getMedatlasHeader(cDomain, currentStation, inputFile);
                break;
        }
        return stationHeader;
    }
    
    @SuppressWarnings("nls")
    private StringBuffer getOdvHeader(CruiseDomain cDomain, Station currentStation, String[] inputFile) throws ParseException{
        StringBuffer odvStationHeader = new StringBuffer(); 
        String stationRef = currentStation.getReference();
        // Cruise
        String cruiseName = Nemo.getInstance().getDomain().getCruise().getModel().getName();
        odvStationHeader.append(form.limitString(cruiseName, 20));
        odvStationHeader.append(Global.TAB);
        
        //Station
        odvStationHeader.append(form.limitString(stationRef, 20));
        odvStationHeader.append(Global.TAB);
        
        //Type
        odvStationHeader.append(getTypeBC(stationRef));
        odvStationHeader.append(Global.TAB);
        
        //yyyy-mm-ddThh:mm:ss.sss
        odvStationHeader.append(getDateTime(currentStation));
        odvStationHeader.append(Global.TAB);
        
        //Longitude [degrees_east]
        //Double longitude = (Double) _model.getProperty(Intern.S_LON).applyOn(inputFile);
        odvStationHeader.append(form.formatLongitude("+DDD.dddddd", currentStation.getLongitude()));
        odvStationHeader.append(Global.TAB);
        
        //Latitude [degrees_north]
        //Double latitude = (Double) _model.getProperty(Intern.S_LAT).applyOn(inputFile);
        //_currentStationValues.put(Intern.S_LAT, latitude);
        odvStationHeader.append(form.formatLatitude("+DD.dddddd ", currentStation.getLatitude()));
        odvStationHeader.append(Global.TAB);
        
        //LOCAL_CDI_ID
        odvStationHeader.append(getLocalCdiId(cDomain, Conversion.ODV, stationRef));
        odvStationHeader.append(Global.TAB);
        
        //EDMO_CODE
        odvStationHeader.append(Nemo.getInstance().getConfig().getIdEdmoRespCdi());
        odvStationHeader.append(Global.TAB);
        
        //Bot. Depth [m]
        Float botDepth = Float.valueOf((String)_model.getProperty(Intern.S_DEPTH).applyOn(inputFile));
        if (!botDepth.isNaN()){
            odvStationHeader.append(botDepth);
        }
        else{
            odvStationHeader.append(Global.ZERO);
        }
        odvStationHeader.append(Global.TAB);
        
        
        /*
        // TODO cas TRAJECTORY
## ---- initialisation de des valeurs 
##
#set( $limit = $station.parametersAt(0).getModel().valuesSize() - 1 )
#set ( $range = [0 .. $limit] )
##
#foreach( $i in  $range )
## ---- determination de valeurs d'entête variables pour Trajectory
#if ($stationModel.isTrajectory()) 
#set ($stationRef = $i + 1)
#set ($dateTime   = $station.getVariantDateTime($i))
#set ($longitude  = $station.getVariant($i, "LONX"))
#set ($latitude   = $station.getVariant($i, "LATX"))
#if ($station.bottomDepth.toString() != "NaN")#set ($botDepth = $station.getVariant($i, "BATH"))#end
#end
         */
        return odvStationHeader;
    }

    @SuppressWarnings("nls")
    private String getTypeBC(String ref){
        String typeBC = "*";
        if (Nemo.getInstance().getDomain().getMedia().getModel().getInputType() == E_InputType.PROFILE){
            DataType dataType = _model.getDataType();
            if (dataType != null 
                    && dataType.getCode() != null
                    && dataType.getCode().equals("H09")) {
                typeBC = "B";
            }
            else if (dataType != null
                     && dataType.getCode() != null
                     && (dataType.getCode().equals("H10") || dataType.getCode().equals("H13"))) {
                typeBC = "C";
            }
            else{
                int nbRecords = getStationByRef(ref).getNbRecords();
                if (nbRecords < 250){
                    typeBC = "B";
                }
                else{
                    typeBC = "C";
                }
            }
        }
        return typeBC;
    }

    @SuppressWarnings("nls")
    private String getDateTime(Station currentStation) throws ParseException
    {        
        StringBuffer value = new StringBuffer();
        String s_date =
            Formatter.getSingleton()
                .formatDateTime("YYYY-MM-DD", getFullDate(currentStation));
        value.append(s_date);
        value.append("T");
        String s_time =
            Formatter.getSingleton().formatDateTime(
                "hh24:mm:ss.sss", getCorrectedTime(currentStation.getTime(), currentStation.getOffsetTU()));
        value.append(s_time);
        return value.toString();
    }

    private long getFullDate(Station currentStation){
        
        long date = currentStation.getDate();
        long time = currentStation.getTime();
        
        TimeZone tz = TimeZone.getTimeZone(DateTimeFormat.GMT);
        Calendar cal = Calendar.getInstance(tz);
        cal.clear();
        cal.setTimeInMillis(date);
        if (!Float.isNaN(time)){
            cal.setTimeInMillis(date + (time));
        }
        else
        {
            cal.setTimeInMillis(date);
        }
        return getGMTDate(cal.getTimeInMillis(), currentStation.getOffsetTU());
    }
    
    private long getGMTDate(long a_Date, float offsetTU){
        return a_Date + (Float.valueOf(offsetTU).longValue() * 3600 * 1000);
    }
    
    public long getCorrectedTime(long time, float offsetTU) {
        if (!Float.isNaN(time)){
            TimeZone tz = TimeZone.getTimeZone(DateTimeFormat.GMT);
            Calendar cal = Calendar.getInstance(tz);
            cal.clear();
            Calendar cal2 = Calendar.getInstance(tz);
            cal2.clear();
            cal2.setTimeInMillis(new Float(time).longValue());
            cal.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
            cal.set(Calendar.SECOND, cal2.get(Calendar.SECOND));
            cal.set(Calendar.MILLISECOND, cal2.get(Calendar.MILLISECOND));
            return getGMTDate(cal.getTimeInMillis(), offsetTU);
        }
        return time;
    }


    public String getLocalCdiId(CruiseDomain cDomain, Conversion c, String ref) throws ParseException{
        // FAE 1143 : nommage du LOCAL_CDI_ID

        String cruiseRef = Global.EMPTY_STRING;
        String stationReference = ref.trim();

        switch (c){
            case ODV:
                // ODV -> [Référence du jeu de données]
                if (Nemo.getInstance().getDomain().getMedia().getModel().getProcessType() == E_ProcessType.COLLECTION_FILES)
                    cruiseRef = cDomain.getModel().getName();
                else
                    cruiseRef = cDomain.getDataSetRef();
                // [Numéro station]si < 5 : codé sur 5 caractères il est complété à
                // gauche par des '0'.
                if (stationReference.length() < 5){
                    stationReference = form.padZero(stationReference, 5);
                }
                break;
                
            case MEDATLAS:
                // MEDATLAS ->
                // [Référence campagne]
                cruiseRef = Elts.getElt(Elts.cruise_ref, cDomain.getModel().getReference());
                // [Numéro station] codé sur 5 caractères il est complété à gauche
                // par des '0'.
                stationReference = form.padZero(stationReference, 5);
                break;            
        }

        // [Reference]
        String temp = Global.EMPTY_STRING;
        temp += cruiseRef;

        // [Numéro station]
        if (!temp.equals(Global.EMPTY_STRING))
            temp += Global.UNDERSCORE;
        temp += stationReference;

        // [Type de données] s'il existe
        DataType dataType = _model.getDataType();
        if (dataType != null){
            if (!dataType.getCode().equals(Global.EMPTY_STRING)){
                if (!temp.equals(Global.EMPTY_STRING)){
                    temp += Global.UNDERSCORE;
                }
                temp += dataType.getCode();
            }
        }
        return Utils.validFileName(temp);
    }
    
    @SuppressWarnings("nls")
    private StringBuffer getMedatlasHeader(CruiseDomain cDomain, Station currentStation, String[] inputFile) throws ParseException{
        StringBuffer medStationHeader = new StringBuffer();
        String stationRef = currentStation.getReference();
        
        String start = Conversion.MEDATLAS.getDebut();
        String cruiseRef = Nemo.getInstance().getDomain().getCruise().getModel().getReference();
        
        //*FI3519981000700001 Data Type=D09
        medStationHeader.append(start);
        medStationHeader.append(Elts.getElt(Elts.cruise_ref, cruiseRef));
        medStationHeader.append(Elts.getElt(Elts.station_ref, stationRef.trim()));
        medStationHeader.append(Global.SPACE);
        medStationHeader.append(Elts.getElt(Elts.datatype_code, _model.getDataType().getCode()));
        medStationHeader.append(Global.RN);
        

        //*DATE=21071998 TIME=0930 LAT=S18 08.55 LON=E178 27.21 DEPTH=       QC=0000
        medStationHeader.append(start);
        medStationHeader.append(Elts.getElt(Elts.station_date, getFullDate(currentStation)));
        medStationHeader.append(Elts.getElt(Elts.station_time, getCorrectedTime(currentStation.getTime(), currentStation.getOffsetTU())));
        medStationHeader.append(Elts.getElt(Elts.station_lat,  currentStation.getLatitude()));
        medStationHeader.append(Elts.getElt(Elts.station_lon, currentStation.getLongitude()));
        medStationHeader.append(Elts.getElt(Elts.station_depth, Float.parseFloat((String) _model.getProperty(Intern.S_DEPTH).applyOn(inputFile))));
        medStationHeader.append(Global.SPACE);
        medStationHeader.append(Elts.getKeyword(Elts.qc));
        medStationHeader.append(currentStation.getMetaFlag(Intern.DATETIME));
        medStationHeader.append(currentStation.getMetaFlag(Intern.S_LAT));
        medStationHeader.append(currentStation.getMetaFlag(Intern.S_LON));
        medStationHeader.append(currentStation.getMetaFlag(Intern.S_DEPTH));
        medStationHeader.append(Global.RN);
        
        //*NB PARAMETERS=07 RECORD LINES=08651
        int paramNumbers = _data.getMedParameterModels().length + _data.getModel().getParameterModels().length;
        medStationHeader.append(start);
        medStationHeader.append(Elts.getElt(Elts.station_param, Integer.toString(paramNumbers)));
        medStationHeader.append(Elts.getElt(Elts.station_records, currentStation.getNbRecords()));
        medStationHeader.append(Global.RN);
        
        medStationHeader.append(_data.getMedParametersDesc());
        
        //*GLOBAL PROFILE QUALITY FLAG=0 GLOBAL PARAMETERS QC FLAGS=0000000
        medStationHeader.append(start);
        medStationHeader.append(Elts.getElt(Elts.station_gblProfile, currentStation.getGlobalProfileQc()));
        medStationHeader.append(Elts.getElt(Elts.station_gblParam, currentStation.getGlobalParamQc()));
        medStationHeader.append(Global.RN);
        
        //*DC HISTORY=
        medStationHeader.append(start);
        medStationHeader.append(Elts.getElt(Elts.station_dcHistory, _model.getProperty(Intern.S_DCHIST + "1").applyOn(inputFile))); 
        medStationHeader.append(Global.RN);

        medStationHeader.append(start);
        medStationHeader.append(Elts.limit((String) _model.getProperty(Intern.S_DCHIST + "2").applyOn(inputFile), Elts.station_line));
        medStationHeader.append(Global.RN);

        //*DM HISTORY=
        medStationHeader.append(start);
        medStationHeader.append(Elts.getElt(Elts.station_dmHistory, Global.EMPTY_STRING)); 
        medStationHeader.append(Global.RN);

        medStationHeader.append(start);
        medStationHeader.append(Global.RN);

        //*COMMENT
        medStationHeader.append(start);      
        medStationHeader.append(Elts.kw_cruise_comment);
        medStationHeader.append(Global.RN);
        
        // pour chaque ligne de commentaires
        for (String line : getCommentLines(inputFile)){
            medStationHeader.append(start);
            medStationHeader.append(line);
            medStationHeader.append(Global.RN);
        }
        
        if (Nemo.getInstance().getDomain().getMedia().getModel().getInputType() != E_InputType.PROFILE)
            medStationHeader.append(getMedTimeSeriesHeader(currentStation, inputFile));
        
        //--SDN_parameter_mapping--
        medStationHeader.append(_data.getSDNparameters(Conversion.MEDATLAS));
        
        //*EDMO_CODE=1
        medStationHeader.append(start);
        medStationHeader.append(Elts.getElt(Elts.station_edmo, Nemo.getInstance().getConfig().getIdEdmoRespCdi()));
        medStationHeader.append(Global.RN);

        //*LOCAL_CDI_ID=FI35199810007_00001_D09
        medStationHeader.append(start);
        medStationHeader.append(Elts.getElt(Elts.station_localCdiId, getLocalCdiId(cDomain, Conversion.MEDATLAS, stationRef)));
        medStationHeader.append(Global.RN);

        //*SURFACE SAMPLES=
        medStationHeader.append(start);
        medStationHeader.append(Elts.getElt(Elts.station_surface, _model.getProperty(Intern.S_SURFACE+ "1").applyOn(inputFile))); 
        medStationHeader.append(Global.RN);
        
        medStationHeader.append(start);
        medStationHeader.append(Elts.limit((String) _model.getProperty(Intern.S_SURFACE + "2").applyOn(inputFile), Elts.station_line));

        
        /*
#foreach( $station in $survey.stations )
*$formatter.padString( $survey.reference, 13)$formatter.padZero($station.reference.trim(), 5) Data Type=$formatter.padString($station.dataType.code, 3)
*DATE=$formatter.formatDateTime("ddMMyyyy", $station.fullDate) TIME=$formatter.formatDateTime("hh24mm", $station.correctedTime, "9999") LAT=$formatter.formatLatitude("NDD MM.mm", $station.latitude) LON=$formatter.formatLongitude("NDDD MM.mm", $station.longitude) DEPTH=#if($station.bottomDepth == 0) #else$formatter.formatInt("%-5d", $station.bottomDepth)#end  QC=$station.getMetaFlag("dateTime")$station.getMetaFlag("latitude")$station.getMetaFlag("longitude")$station.getMetaFlag("bottomDepth")
*NB PARAMETERS=$formatter.formatInt("%02d", $station.parametersNumber) RECORD LINES=$formatter.formatInt("%05d", $station.parametersAt(0).getModel().valuesSize())
## -- PARAMETRES
## -- -- paramètres ajoutés
#foreach( $medparamModel in $station.medparameters )
#set ($medparam = $medparamModel.getModel())
*$formatter.padString($medparam.code, 4) $formatter.padString($medparam.fullName, 29) ($formatter.padString($medparam.unit, 28)) def.=$medparam.default
#end
## -- -- paramètres mesurés
#foreach( $paramModel in $station.parameters )
#set ($param = $paramModel.getModel())
*$formatter.padString($param.code, 4) $formatter.padString($param.fullName, 29) ($formatter.padString($param.unit, 28)) def.=$param.default
#end
*GLOBAL PROFILE QUALITY FLAG=$station.getGlobalProfileQC() GLOBAL PARAMETERS QC FLAGS=$station.getGlobalParametersQC()
*DC HISTORY=$formatter.limitString( $station.dcHistory1, 68)
*$formatter.limitString( $station.dcHistory2, 80)
*DM HISTORY=$formatter.limitString( $station.dmHistory1, 68)
*$formatter.limitString( $station.dmHistory2, 80)
*COMMENT
#foreach ($line in $station.commentLines)
*$formatter.limitString( $line, 80)
#end
* 
#if ($survey.isTimeSeriesSurvey())
*ADDITIONAL INFORMATION - TIME SERIES
*EDATE=$formatter.formatDateTime("ddMMyyyy", $station.timeSeriesInformation.endDate) ETIME=$formatter.formatDateTime("hh24mm", $station.timeSeriesInformation.endTime) ELAT=$formatter.formatLatitude("NDD MM.mm", $station.timeSeriesInformation.endLatitude) ELON=$formatter.formatLongitude("NDDD MM.mm", $station.timeSeriesInformation.endLongitude) EDEPTH=#if($station.timeSeriesInformation.endDepth==0) #else$formatter.formatInt("%-5d", $station.timeSeriesInformation.endDepth)#end  QC=$station.getMetaFlag("endDateTime")$station.getMetaFlag("endLatitude")$station.getMetaFlag("endLongitude")$station.getMetaFlag("endDepth")
*SENSOR DEPTH=$formatter.formatInt("%-5d", $station.timeSeriesInformation.sensorDepth) (metre)            DISTANCE TO BOTTOM=$formatter.formatInt("%-5d", $station.timeSeriesInformation.distToBottom) (metre) QC=$station.getMetaFlag("sensorDepth")$station.getMetaFlag("distanceToBottom")
*DURATION=$formatter.formatInt("%-5d", $station.timeSeriesInformation.duration) (day)                                                   QC=$station.getMetaFlag("duration")
*SAMPLING RATE=$formatter.formatInt("%-5d", $station.timeSeriesInformation.samplingRate) (second)          MAGNETIC DECLINATION=$formatter.formatFloat("%-4.0f", $station.timeSeriesInformation.getMagneticDeclination()) (degree)
#end
## -- SDN_parameters_mapping
$nemoLists.getSdnParameters()
*EDMO_CODE=$configuration.getIdEdmoRespCdi()
*LOCAL_CDI_ID=$station.getLocalCdiId($survey, "MEDATLAS")
*SURFACE SAMPLES=$formatter.limitString( $station.surfaceSamples1, 63)
*$formatter.limitString( $station.surfaceSamples2, 80)
##
         */
        // TODO
        return medStationHeader;
    }

    /**
     * 
     * @return String[]
     * @throws ParseException 
     */
    private String[] getCommentLines(String[] inputFile) throws ParseException {
        LineNumberReader reader = new LineNumberReader(new StringReader((String) _model.getProperty(Intern.S_COMMENT).applyOn(inputFile)));
        String line = null;
        java.util.Vector<String> lines = new java.util.Vector<String>();
        try {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
            }
        }
        String[] lineArray = new String[lines.size()];
        return lines.toArray(lineArray);
    }
    
    public String getMetaFlag(String key, String[] inputFile){
        String metaFlag = Global.ZERO;
        if (key.equals(Intern.DATETIME)){
            key = Intern.S_DATE;
        }else if (key.equals(Intern.ENDDATETIME)){
            key = Intern.S_EDATE;
        }
        Fragment flag = _model.getProperty(key).getFlag();
        if (flag != null){
            metaFlag = _model.getFlagTable().getFlagSDN(flag.retreive(inputFile));
        }
        return metaFlag;
    }
    
    private StringBuffer getMedTimeSeriesHeader(Station currentStation, String[] inputFile) throws ParseException{
        StringBuffer timeseriesHeader = new StringBuffer(); 
        String start = Conversion.MEDATLAS.getDebut();
        
        float endDepth = Float.parseFloat((String)_model.getProperty(Intern.S_EDEPTH).applyOn(inputFile));
        float sensorDepth = currentStation.getSensorDepth();
        long endTime;
        try{
            endTime = Long.parseLong(_model.getProperty(Intern.S_ETIME).getUserValue());
        }
        catch (NumberFormatException e){
            long rate = new Float(currentStation.getSamplingRate() * 1000.0f).longValue();
            int size = currentStation.getNbRecords() - 1;
            long date = getFullDate(currentStation) + (size * rate);
            TimeZone tz = TimeZone.getTimeZone(DateTimeFormat.GMT); 
            Calendar cal = Calendar.getInstance(tz);
            cal.clear();
            cal.setTimeInMillis(date);
            
            endTime = cal.getTimeInMillis();
            
            _model.getProperty(Intern.S_ETIME).setUserProvided(true);
            _model.getProperty(Intern.S_ETIME).setUserValue(String.valueOf(endTime));
  
            _model.getProperty(Intern.S_EDATE).setUserProvided(true);
            _model.getProperty(Intern.S_EDATE).setUserValue(String.valueOf(endTime));     
        }

        //*ADDITIONAL INFORMATION - TIME SERIES
        timeseriesHeader.append(start);
        timeseriesHeader.append(Elts.kw_station_addTimeSeries);
        timeseriesHeader.append(Global.RN);
        
        //*EDATE=19091998 ETIME=1110 ELAT=S18 08.55 ELON=E178 27.21 EDEPTH=       QC=0000
        timeseriesHeader.append(start);
        timeseriesHeader.append(Elts.getElt(Elts.station_endDate, Long.parseLong((String)_model.getProperty(Intern.S_EDATE).applyOn(inputFile))));
        timeseriesHeader.append(Elts.getElt(Elts.station_endTime, endTime));
        timeseriesHeader.append(Elts.getElt(Elts.station_endLat, _model.getProperty(Intern.S_ELAT).applyOn(inputFile)));
        timeseriesHeader.append(Elts.getElt(Elts.station_endLon, _model.getProperty(Intern.S_ELON).applyOn(inputFile)));
        timeseriesHeader.append(Elts.getElt(Elts.station_endDepth, endDepth));
        timeseriesHeader.append(Elts.getKeyword(Elts.qc));
        timeseriesHeader.append(currentStation.getMetaFlag(Intern.ENDDATETIME));
        timeseriesHeader.append(currentStation.getMetaFlag(Intern.S_ELAT));
        timeseriesHeader.append(currentStation.getMetaFlag(Intern.S_ELON));
        timeseriesHeader.append(currentStation.getMetaFlag(Intern.S_EDEPTH));
        timeseriesHeader.append(Global.RN);
        
        //*SENSOR DEPTH=17    (metre)            DISTANCE TO BOTTOM=      (metre) QC=00
        Float bottomDepth = Float.NaN;
        if (!Float.isNaN(endDepth) && !Float.isNaN(sensorDepth)){
            bottomDepth = endDepth - sensorDepth;
        }
        timeseriesHeader.append(start);
        timeseriesHeader.append(Elts.getElt(Elts.station_sensor, sensorDepth));
        timeseriesHeader.append(Elts.getElt(Elts.station_bottomDepth, bottomDepth));
        timeseriesHeader.append(Elts.getKeyword(Elts.qc));
        timeseriesHeader.append(currentStation.getMetaFlag(Intern.S_SENSORDEPTH));
        timeseriesHeader.append(currentStation.getMetaFlag(Intern.DISTANCETOBOTTOM));
        timeseriesHeader.append(Global.RN);
        
        
        
        //*DURATION=60    (day)                                                   QC=0
        float duration = endTime - getFullDate(currentStation); // duree en millisecondes
        duration = duration / (1000.0f * 60.0f * 60.0f * 24.0f);
        timeseriesHeader.append(start);
        timeseriesHeader.append(Elts.getElt(Elts.station_duration, duration));
        for (int i = 0; i < 50; i++){
            timeseriesHeader.append(Global.SPACE);
        }
        timeseriesHeader.append(Elts.getElt(Elts.qc, currentStation.getMetaFlag(Intern.DURATION)));
        timeseriesHeader.append(Global.RN);
        
        //*SAMPLING RATE=600   (second)          MAGNETIC DECLINATION=-999 (degree)
        timeseriesHeader.append(start);
        
        timeseriesHeader.append(Elts.getElt(Elts.station_sampling, currentStation.getSamplingRate()));
        timeseriesHeader.append(Elts.getElt(Elts.station_magnetic, _model.getProperty(Intern.S_MAGNETIC).applyOn(inputFile)));
        timeseriesHeader.append(Global.RN);
        
        return timeseriesHeader;
    }
    
    //RELEASE
    public void release(){
        _model.release();
        _data.release();
    }

    /**
     * Getter pour model.
     * @return StationModel
     * @see StationDomain.java#model
     */
    public StationModel getModel(){
        return _model;
    }

    /**
     * Setter pour model
     * @param model : nouvelle valeur pour model
     * @see StationDomain.java#model
     */
    public void setModel(StationModel model){
        _model = model;
    } 

    /**
     * Getter pour data.
     * @return DataDomain
     * @see InputDomain.java#data
     */
    public DataDomain getData(){
        return _data;
    }

    /**
     * Setter pour data
     * @param data : nouvelle valeur pour data
     * @see InputDomain.java#data
     */
    public void setData(DataDomain data){
        _data = data;
    }

    /**
     * Getter pour _someInData.
     * @return boolean
     * @see StationDomain.java#_someInData
     */
    public boolean isSomeInData()
    {
        return _someInData;
    }

    /**
     * Setter pour _someInData
     * @param someInData : nouvelle valeur pour someInData
     * @see StationDomain.java#_someInData
     */
    public void setSomeInData(boolean someInData)
    {
        _someInData = someInData;
    }
    
    public void addStation(String inputFilePath, Station st){
        if (_listStations.containsKey(inputFilePath)){
            _listStations.get(inputFilePath).add(st);
        }
        else{
            List<Station> stations = new ArrayList<Station>();
            stations.add(st);
            _listStations.put(inputFilePath, stations);
        }
    }
    
    public boolean stationsRegistered(String inputFilePath, String inputFileName){
        if (_listStations.containsKey(inputFilePath)){
            for (Station st : _listStations.get(inputFilePath)){
                if (st.getFileName().equals(inputFileName))
                    return true;
            }
        }
        return false;
    }
    
    public List<Station> getStations(String inputFilePath){
        return _listStations.get(inputFilePath);
    }
    
    public Station getStationByRef(String ref){
        Station selected = null;
        for (String inputFilePath : _listStations.keySet()){
            for (Station st : _listStations.get(inputFilePath)){
                if (ref.equals(st.getReference())){
                    selected = st;
                    break;
                }  
            }
        }
        return selected;
    }
    
    public void resetStations(){
        _listStations.clear();
    }

    /**
     * Getter pour _computed.
     * @return boolean
     * @see StationDomain.java#_computed
     */
    public boolean isComputed()
    {
        return _computed;
    }

    /**
     * Setter pour _computed
     * @param computed : nouvelle valeur pour computed
     * @see StationDomain.java#_computed
     */
    public void setComputed(boolean computed)
    {
        _computed = computed;
    }
}
