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

import java.util.HashMap;


/**
 * Class Station.java
 * Description: TODO
 * @author dboulang 
 */
public class Station implements Comparable 
{
    
    protected String _reference;
    protected int _nbRecords;
    
    protected Double _latitude;
    protected Double _longitude;
    protected Long _date;
    //protected Float _correctedTime;
    protected Float _offsetTU;
    protected Float _time;
    
    protected Float _sensorDepth;
    protected Float _samplingRate;

    protected HashMap<String, String> _metaFlags;
    
    protected boolean _registered;
    
    protected Float _dataSize;
    
    
    public Station(){
        _reference = null;
        _nbRecords = 0;
    }

    /**
     * Getter pour nbRecords.
     * @return int
     * @see Station.java#nbRecords
     */
    public int getNbRecords()
    {
        return _nbRecords;
    }

    /**
     * Setter pour nbRecords
     * @param nbRecords : nouvelle valeur pour nbRecords
     * @see Station.java#nbRecords
     */
    public void setNbRecords(int nbRecords)
    {
        _nbRecords = nbRecords;
    }

    /**
     * Getter pour reference.
     * @return String
     * @see Station.java#reference
     */
    public String getReference()
    {
        return _reference;
    }

    /**
     * Setter pour reference
     * @param reference : nouvelle valeur pour reference
     * @see Station.java#reference
     */
    public void setReference(String reference)
    {
        _reference = reference;
    }


    /**
     * Getter pour _date.
     * @return Long
     * @see Station.java#_date
     */
    public Long getDate()
    {
        return _date;
    }

    /**
     * Setter pour _date
     * @param date : nouvelle valeur pour date
     * @see Station.java#_date
     */
    public void setDate(Long date)
    {
        _date = date;
    }

    /**
     * Getter pour _latitude.
     * @return Double
     * @see Station.java#_latitude
     */
    public Double getLatitude()
    {
        return _latitude;
    }

    /**
     * Setter pour _latitude
     * @param latitude : nouvelle valeur pour latitude
     * @see Station.java#_latitude
     */
    public void setLatitude(Double latitude)
    {
        _latitude = latitude;
    }

    /**
     * Getter pour _longitude.
     * @return Double
     * @see Station.java#_longitude
     */
    public Double getLongitude()
    {
        return _longitude;
    }

    /**
     * Setter pour _longitude
     * @param longitude : nouvelle valeur pour longitude
     * @see Station.java#_longitude
     */
    public void setLongitude(Double longitude)
    {
        _longitude = longitude;
    }
    
    public String getMetaFlag(String key){
        return _metaFlags.get(key);
    }

    /**
     * Getter pour _metaFlags.
     * @return HashMap<String,String>
     * @see Station.java#_metaFlags
     */
    public HashMap<String, String> getMetaFlags()
    {
        return _metaFlags;
    }
    
    public void addMetaFlag(String key, String metaFlag){
        _metaFlags.put(key, metaFlag);
    }

    /**
     * Setter pour _metaFlags
     * @param metaFlags : nouvelle valeur pour metaFlags
     * @see Station.java#_metaFlags
     */
    public void setMetaFlags(HashMap<String, String> metaFlags)
    {
        _metaFlags = metaFlags;
    }

    /**
     * Getter pour _offsetTU.
     * @return Float
     * @see Station.java#_offsetTU
     */
    public Float getOffsetTU()
    {
        return _offsetTU;
    }

    /**
     * Setter pour _offsetTU
     * @param offsetTU : nouvelle valeur pour offsetTU
     * @see Station.java#_offsetTU
     */
    public void setOffsetTU(Float offsetTU)
    {
        _offsetTU = offsetTU;
    }

    /**
     * Getter pour _registered.
     * @return boolean
     * @see Station.java#_registered
     */
    public boolean isRegistered()
    {
        return _registered;
    }

    /**
     * Setter pour _registered
     * @param registered : nouvelle valeur pour registered
     * @see Station.java#_registered
     */
    public void setRegistered(boolean registered)
    {
        _registered = registered;
    }

    /**
     * Getter pour _sensorDepth.
     * @return Float
     * @see Station.java#_sensorDepth
     */
    public Float getSensorDepth()
    {
        return _sensorDepth;
    }

    /**
     * Setter pour _sensorDepth
     * @param sensorDepth : nouvelle valeur pour sensorDepth
     * @see Station.java#_sensorDepth
     */
    public void setSensorDepth(Float sensorDepth)
    {
        _sensorDepth = sensorDepth;
    }

    /**
     * Getter pour _time.
     * @return Long
     * @see Station.java#_time
     */
    public Float getTime()
    {
        return _time;
    }

    /**
     * Setter pour _time
     * @param time : nouvelle valeur pour time
     * @see Station.java#_time
     */
    public void setTime(Float time)
    {
        _time = time;
    }
    
    
    /**
     * Getter pour _samplingRate.
     * @return Float
     * @see Station.java#_samplingRate
     */
    public Float getSamplingRate()
    {
        return _samplingRate;
    }

    /**
     * Setter pour _samplingRate
     * @param samplingRate : nouvelle valeur pour samplingRate
     * @see Station.java#_samplingRate
     */
    public void setSamplingRate(Float samplingRate)
    {
        _samplingRate = samplingRate;
    }

    /**
     * Getter pour _dataSize.
     * @return int
     * @see Station.java#_dataSize
     */
    public Float getDataSize()
    {
        return _dataSize;
    }

    /**
     * Setter pour _dataSize
     * @param dataSize : nouvelle valeur pour dataSize
     * @see Station.java#_dataSize
     */
    public void setDataSize(Float dataSize)
    {
        _dataSize = dataSize;
    }

    public int compareTo(Object other) {
        // FAE1105 : tri des stations selon (Nom Fichier -) Date - Référence Station
        
        String refIni = this.getReference();
        String refOther = ((Station) other).getReference();

        return refIni.compareTo(refOther);
    }

}
