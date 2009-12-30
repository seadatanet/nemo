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
 * Description: TODO
 * @author Delphine BOULANGER 
 */
public class InputDomain
{
    protected MediaDomain _media;
    protected CruiseDomain _cruise;
    protected ConversionDomain _conversion;
        
    public InputDomain(){
        _media = new MediaDomain();
        _cruise = new CruiseDomain();
        _conversion = new ConversionDomain();
    }
        
    /*public InputDomain(InputModel model){
        _media = new MediaDomain(model.getMediaModel());
        _cruise = new CruiseDomain(model);
        _conversion = new ConversionDomain(model.getConversionModel());
    }*/
    
    public void update(InputModel model){
        _media.update(model);
        _cruise.update(model);
        _conversion.update(model);
    }
    
    //RELEASE
    public void release(){
        _media.release();
        _cruise.release();
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
}