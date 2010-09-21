/* ============================================================================
 * Nom du fichier	: CruiseDomain.java
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
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.ifremer.nemo.input.CruiseModel;
import fr.ifremer.nemo.input.DataType;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.output.medatlas.Elts;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;

/**
 * Class CruiseDomain.java
 * Description: TODO
 * @author Delphine BOULANGER 
 */
public class CruiseDomain
{
    /**
     * Informations descrivant l'onglet Cuise
     */
    protected CruiseModel _model;
    protected StationDomain _station;
    final private Formatter form;
    
    private String _dataSetRef;
    
    public CruiseDomain(){
        form = Formatter.getSingleton();
        initValues();
    }
    
    /*public CruiseDomain(InputModel model){
        this();
        update(model);
    }*/
    
    public void initValues(){
        _model = new CruiseModel();
        _station = new StationDomain();
        _dataSetRef = Global.EMPTY_STRING;
    }
    
    public void update(InputModel model){
        updateCruise(model.getCruiseModel());
        _station.update(model);
    }
    
    public void updateCruise(CruiseModel cruiseModel){
        _model = cruiseModel;
        // TODO update Domain
    }
    
    //RELEASE
    public void release(){
        _station.release();
        _model.release();
    }
    
    public StringBuffer getHeader(Conversion c) throws ParseException{
        StringBuffer cruiseHeader = new StringBuffer();
        switch (c){
            case ODV:
                cruiseHeader = getOdvHeader();
                break;
            case MEDATLAS:
                cruiseHeader = getMedatlasHeader();
                break;
        }
        return cruiseHeader;
    }
    
    private StringBuffer getOdvHeader(){
        String start = Conversion.ODV.getDebut();
        StringBuffer odvCruiseHeader = new StringBuffer();

        if (!_model.getComment().equals(Global.EMPTY_STRING)){    
            for (String line : getCommentLines()){
                odvCruiseHeader.append(start);
                odvCruiseHeader.append(line);
                odvCruiseHeader.append(Global.RN);
            }
        }
        
        odvCruiseHeader.append(start);
        odvCruiseHeader.append(Global.RN);
        
        odvCruiseHeader.append(_station.getData().getSDNparameters(Conversion.ODV));
        odvCruiseHeader.append(start);
        odvCruiseHeader.append(Global.RN);
        
        odvCruiseHeader.append(_station.getOdvStationHeaderLabels());
        odvCruiseHeader.append(_station.getData().getDataHeader(Conversion.ODV));
        
        return odvCruiseHeader;
    }
    
    private StringBuffer getMedatlasHeader() throws ParseException{
        String start = Conversion.MEDATLAS.getDebut();
        SimpleDateFormat fmt = new SimpleDateFormat(CruiseModel.MED_DATE_FORMAT);
        
        StringBuffer medCruiseHeader = new StringBuffer();  

        medCruiseHeader.append(start);
        medCruiseHeader.append(Elts.getElt(Elts.cruise_ref, _model.getReference()));
        medCruiseHeader.append(Global.SPACE);
        medCruiseHeader.append(Elts.getElt(Elts.cruise_name, _model.getName()));
        medCruiseHeader.append(Global.SPACE);
        medCruiseHeader.append(Elts.getElt(Elts.cruise_shipcode, _model.getShipCode()));
        medCruiseHeader.append(Global.SPACE);
        medCruiseHeader.append(Elts.getElt(Elts.cruise_shipname, _model.getShipName()));
        medCruiseHeader.append(Global.RN);

        medCruiseHeader.append(fmt.format(new Date(_model.getStartDate())));
        medCruiseHeader.append(Global.MOINS);
        medCruiseHeader.append(fmt.format(new Date(_model.getEndDate())));
        medCruiseHeader.append(Global.SPACE);
        medCruiseHeader.append(Elts.getElt(Elts.cruise_region, _model.getRegionName()));
        medCruiseHeader.append(Global.RN);

        medCruiseHeader.append(Elts.getElt(Elts.cruise_country, _model.getCountryCode()));
        medCruiseHeader.append(Global.SPACE);
        medCruiseHeader.append(Elts.getElt(Elts.cruise_labo, _model.getLaboratories()));
        medCruiseHeader.append(Global.RN);

        medCruiseHeader.append(Elts.getElt(Elts.cruise_chiefscientist, _model.getChiefScientist()));
        medCruiseHeader.append(Elts.getElt(Elts.cruise_project, _model.getProjectName()));
        medCruiseHeader.append(Global.RN);

        medCruiseHeader.append(Elts.getElt(Elts.cruise_datacenter, _model.getDataCentre()));
        medCruiseHeader.append(form.padString(Global.EMPTY_STRING, 18));
        medCruiseHeader.append(Elts.getElt(Elts.cruise_availability, _model.getAvailability().getCode()));
        medCruiseHeader.append(Global.RN);

        int end = _model.getDataTypesLength();
        int length = 0;
        for (DataType dType : _model.getDataTypes()){
            medCruiseHeader.append(Elts.getElt(Elts.datatype_code, dType.getCode()));
            medCruiseHeader.append(Elts.getElt(Elts.datatype_nbprofile, Integer.toString(dType.getNumberOfProfiles())));
            medCruiseHeader.append(Elts.getElt(Elts.qc, form.formatBoolean(dType.isQc())));
            length++;
            if(length != end)
                medCruiseHeader.append(Global.RN);
        }

        if (!_model.getComment().equals(Global.EMPTY_STRING)){            
            medCruiseHeader.append(Global.RN);
            medCruiseHeader.append(Elts.kw_cruise_comment);
            //medCruiseHeader.append(Global.RN);
            
            for (String line : getCommentLines()){
                medCruiseHeader.append(Global.RN);
                medCruiseHeader.append(line);
            }
        }
        
        return medCruiseHeader;
    }

    /**
     * 
     * @return String[]
     */
    private String[] getCommentLines() {
        LineNumberReader reader = new LineNumberReader(new StringReader( _model.getComment()));
        String line = null;
        java.util.Vector<String> lines = new java.util.Vector<String>();
        try {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
            }
        }
        String[] lineArray = new String[lines.size()];
        return lines.toArray(lineArray);
    }

    /**
     * Getter pour model.
     * @return CruiseModel
     * @see CruiseDomain.java#model
     */
    public CruiseModel getModel(){
        return _model;
    }

    /**
     * Setter pour model
     * @param model : nouvelle valeur pour model
     * @see CruiseDomain.java#model
     */
    public void setModel(CruiseModel model){
        _model = model;
    }    

    /**
     * Getter pour station.
     * @return StationDomain
     * @see InputDomain.java#station
     */
    public StationDomain getStation(){
        return _station;
    }

    /**
     * Setter pour station
     * @param station : nouvelle valeur pour station
     * @see InputDomain.java#station
     */
    public void setStation(StationDomain station){
        _station = station;
    }

    /**
     * Getter pour _dataSetRef.
     * @return String
     * @see CruiseDomain.java#_dataSetRef
     */
    public String getDataSetRef()
    {
        return _dataSetRef;
    }

    /**
     * Setter pour _dataSetRef
     * @param dataSetRef : nouvelle valeur pour dataSetRef
     * @see CruiseDomain.java#_dataSetRef
     */
    public void setDataSetRef(String dataSetRef)
    {
        _dataSetRef = dataSetRef;
    }
}
