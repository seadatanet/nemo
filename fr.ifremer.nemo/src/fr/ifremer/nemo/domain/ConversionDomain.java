/* ============================================================================
 * Nom du fichier	: ConversionDomain.java
 * Application      : NEMO version 1.2.20
 * Auteur			: Delphine BOULANGER
 * Date	de création : 30 nov. 09
 * Client			: Ifremer
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import fr.ifremer.nemo.input.ConversionModel;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.util.Global.E_TypeConversion;

/**
 * Class ConversionDomain.java
 * Description: TODO
 * @author Delphine BOULANGER 
 */
public class ConversionDomain
{
    /**
     * Informations descrivant l'onglet Conversion
     */
    protected ConversionModel _model;
    
    /**
     * Conversion courrante
     */
    protected E_TypeConversion _current;

    /**
     * indicateur de tri des données en sortie
     */
    protected boolean _sortData;
    
    /**
     * pour mapping
     */
    protected String _nomMappingHorsPrefix;
    
    public ConversionDomain(){
        initValues();
    }
    
    public void initValues(){
        _model = new ConversionModel();
        _current = E_TypeConversion.MEDALAS;
        _nomMappingHorsPrefix = null;
    }
    
    public void update(InputModel model){
        updateConversion(model.getConversionModel());
    }
    
    public void updateConversion(ConversionModel model){
        _model = model;
    }
    
    //RELEASE
    public void release(){
        _model.release();
    }

    /**
     * Getter pour model.
     * @return ConversionModel
     * @see ConversionDomain.java#model
     */
    public ConversionModel getModel(){
        return _model;
    }

    /**
     * Setter pour model
     * @param model : nouvelle valeur pour model
     * @see ConversionDomain.java#model
     */
    public void setModel(ConversionModel model){
        _model = model;
    }

    /**
     * Getter pour _currentConversion.
     * @return E_TypeConversion
     * @see ConversionDomain.java#_currentConversion
     */
    public E_TypeConversion getCurrent()
    {
        return _current;
    }

    /**
     * Setter pour _currentConversion
     * @param currentConversion : nouvelle valeur pour currentConversion
     * @see ConversionDomain.java#_currentConversion
     */
    public void setCurrentConversion(E_TypeConversion currentConversion)
    {
        _current = currentConversion;
    }

    /**
     * Getter pour _nomMappingHorsPrefix.
     * @return String
     * @see ConversionDomain.java#_nomMappingHorsPrefix
     */
    public String getNomMappingHorsPrefix()
    {
        return _nomMappingHorsPrefix;
    }

    /**
     * Setter pour _nomMappingHorsPrefix
     * @param nomMappingHorsPrefix : nouvelle valeur pour nomMappingHorsPrefix
     * @see ConversionDomain.java#_nomMappingHorsPrefix
     */
    public void setNomMappingHorsPrefix(String nomMappingHorsPrefix)
    {
        _nomMappingHorsPrefix = nomMappingHorsPrefix;
    }

    /**
     * Getter pour _sortData.
     * @return boolean
     * @see ConversionDomain.java#_sortData
     */
    public boolean isSortData()
    {
        return _sortData;
    }

    /**
     * Setter pour _sortData
     * @param sortData : nouvelle valeur pour sortData
     * @see ConversionDomain.java#_sortData
     */
    public void setSortData(boolean sortData)
    {
        _sortData = sortData;
    } 
}
