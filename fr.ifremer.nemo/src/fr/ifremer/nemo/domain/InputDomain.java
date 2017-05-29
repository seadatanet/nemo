/* ============================================================================
 * Nom du fichier	: InputDomain.java
 * Application      : NEMO version 1.2.20
 * Auteur			: Delphine BOULANGER
 * Date	de création : 30 nov. 09
 * Client           : Ifremer
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import fr.ifremer.nemo.input.InputModel;

/**
 * Class InputDomain
 * Description: description des données
 * 
 * @author Delphine BOULANGER 
 */
public class InputDomain
{
    protected MediaDomain _media; //onglet File
    protected CruiseDomain _cruise; //onglet Cruise 
    protected StationDomain _station; //onglet Station
    protected DataDomain _data; // onglet Data
    protected ConversionDomain _conversion; // onglet Conversion
    private boolean valid;
        
    public InputDomain(){
    	valid=false;
        _media = new MediaDomain();
        _cruise = new CruiseDomain();
        _station = new StationDomain();
        _data = new DataDomain();
        _conversion = new ConversionDomain();
    }
        
    /*public InputDomain(InputModel model){
        _media = new MediaDomain(model.getMediaModel());
        _cruise = new CruiseDomain(model);
        _conversion = new ConversionDomain(model.getConversionModel());
    }*/
    
    public void update(InputModel model){
    	valid=model.isValid();
        _media.update(model);
        _cruise.update(model);
        _station.update(model);
        // mettre a jour la conversion AVANT le data, car l'ajout des parametres dans le dataModel depend du forma de conversion -> doit être connu avant!
        _conversion.update(model);
        _data.update(model);
    }
    
    //RELEASE
    public void release(){
    	valid=false;
        _media.release();
        _cruise.release();
        _station.release();
        _data.release();
        _conversion.release();
    }

    /**
     * Getter pour conversion.
     * @return ConversionDomain
     * @see InputDomain.java#conversion
     */
    public ConversionDomain getConversion(){
        return _conversion;
    }

    /**
     * Setter pour conversion
     * @param conversion : nouvelle valeur pour conversion
     * @see InputDomain.java#conversion
     */
    public void setConversion(ConversionDomain conversion){
        _conversion = conversion;
    }

    /**
     * Getter pour cruise.
     * @return CruiseDomain
     * @see InputDomain.java#cruise
     */
    public CruiseDomain getCruise(){
        return _cruise;
    }

    /**
     * Setter pour cruise
     * @param cruise : nouvelle valeur pour cruise
     * @see InputDomain.java#cruise
     */
    public void setCruise(CruiseDomain cruise){
        _cruise = cruise;
    }
    /**
     * Getter pour ststion.
     * @return StationDomain
     * @see InputDomain.java#station
     */
    public StationDomain getStation(){
        return _station;
    }

    /**
     * Setter pour cruise
     * @param station : nouvelle valeur pour station
     * @see InputDomain.java#station
     */
    public void setStation(StationDomain station){
        _station = station;
    }
    /**
     * Getter pour _media.
     * @return MediaDomain
     * @see InputDomain.java#_media
     */
    public MediaDomain getMedia(){
        return _media;
    }

    /**
     * Setter pour _media
     * @param file : nouvelle valeur pour _media
     * @see InputDomain.java#_media
     */
    public void setMedia(MediaDomain file){
        _media = file;
    }
    /**
     * Getter pour data.
     * 
     * @return DataDomain
     * @see InputDomain.java#data
     */
    public DataDomain getData()
    {
        return _data;
    }

    /**
     * Setter pour data
     * 
     * @param data :
     *            nouvelle valeur pour data
     * @see InputDomain.java#data
     */
    public void setData(DataDomain data)
    {
        _data = data;
    }

	public boolean isValid() {
		return valid;
	}
}