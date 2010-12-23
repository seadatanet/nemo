/* ============================================================================
 * Nom du fichier	: DataDomain.java
 * Application      : NEMO version 1.2.20
 * Auteur			: Delphine BOULANGER
 * Date	de création : 30 nov. 09
 * Client			: Ifremer
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.AddedParameter.IdParameter;
import fr.ifremer.nemo.input.DataModel;
import fr.ifremer.nemo.input.Fragment;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.input.LineFragment;
import fr.ifremer.nemo.input.Parameter;
import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.output.medatlas.Elts;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Global.E_InputType;

/**
 * Class DataDomain.java Description: Classe pour la description des données de l'onglet Data
 * 
 * @author Delphine BOULANGER
 */
public class DataDomain
{

    private enum URNtype
    {
        subject,
        object,
        units;
    }

    /**
     * Informations descrivant l'onglet Data
     */
    protected DataModel _model;

    /**
     * Liste, ordonnee, des paramètres rajoutés pour ODV
     */
    protected List<ParameterModel> _odvParameterModels;

    /**
     * Liste, ordonnee, des paramètres rajoutés pour Medatlas
     */
    protected List<ParameterModel> _medParameterModels;

    /**
     * Index du parametre PRES
     */
    // protected int _presIndex;
    public DataDomain()
    {
        initValues();
    }

    /*
     * public DataDomain(InputModel model){ //form = Formatter.getSingleton();
     * initValues(); update(model); }
     */

    public void initValues()
    {
        _model = new DataModel();
        _odvParameterModels = new ArrayList<ParameterModel>();
        _medParameterModels = new ArrayList<ParameterModel>();
        // _presIndex = -1;
    }

    public void update(InputModel model)
    {
        updateData(model.getDataModel());
    }

    public void updateData(DataModel model)
    {
        _model = model;
        setAddedParameterModel();
    }

    private ParameterModel[] getAddedParameterModels(Conversion c)
    {
        ParameterModel[] addedParameters = null;
        // DataDomain data =
        // Nemo.getInstance().getDomain().getCruise().getStation().getData();
        switch (c)
        {
            case MEDATLAS:
                addedParameters = getMedParameterModels();
            break;
            case ODV:
                addedParameters = getOdvParameterModels();
            break;
            default:
            break;
        }
        return addedParameters;
    }

    public StringBuffer getDataHeader(Conversion c)
    {
        StringBuffer dataHeader = new StringBuffer();

        if (c == Conversion.MEDATLAS)
            dataHeader.append(c.getDebut());

        int nbParamTot =
            getAddedParameterModels(c).length
                    + _model.getParameterModels().length;
        int cptParam = 0;

        // paramètres ajoutés
        for (ParameterModel pModel : getAddedParameterModels(c))
        {
            cptParam++;
            Parameter param = pModel.getParameter();
            dataHeader.append(param.getOutputCode(c, (cptParam == nbParamTot)));
        }

        // paramètres mesurés
        for (ParameterModel pModel : _model.getParameterModels())
        {
            cptParam++;
            Parameter param = pModel.getParameter();
            dataHeader.append(param.getOutputCode(c, (cptParam == nbParamTot)));
        }
        return dataHeader;
    }

    public StringBuffer getDataLine(Conversion c, Station currentStation,
            String inputLine, int indexRecord) throws Exception
    {
        StringBuffer dataLine = new StringBuffer();
        switch (c)
        {
            case ODV:
                dataLine.append(getOdvDataLine(currentStation, inputLine,
                    indexRecord));
            break;
            case MEDATLAS:
                dataLine.append(getMedatlasDataLine(currentStation, inputLine,
                    indexRecord));
            break;
        }
        return dataLine;
    }

    private StringBuffer getOdvDataLine(Station currentStation,
            String inputLine, int indexRecord) throws Exception
    {

        StringBuffer odvDataLine = new StringBuffer();

        int nbParamTot =
            getOdvParameterModels().length + _model.getParameterModels().length;
        int cptParam = 0;

        // paramètres ajoutés
        long currentDate =
            DateTimeUtils.getCurrentDate(currentStation, indexRecord);
        for (ParameterModel pModel : getOdvParameterModels())
        {
            cptParam++;
            pModel.computeAddedParameterValue(currentStation, inputLine,
                currentDate);

            String defaultValue = pModel.getParameter().getDefault();
            String stringValue = pModel.getLineValue().getStringValue();

            // odvDataLine.append(Global.TAB);
            if (!stringValue.equals(defaultValue))
                odvDataLine.append(stringValue);
            odvDataLine.append(Global.TAB);
            odvDataLine.append(pModel.getLineValue().getQc());

            if (cptParam < nbParamTot)
                odvDataLine.append(Global.TAB);
        }

        // paramètres mesurés
        for (ParameterModel pModel : _model.getParameterModels())
        {
            cptParam++;
            pModel.computeParameterValue(inputLine);

            String defaultValue = pModel.getParameter().getDefault();
            String stringValue =
                Formatter.getSingleton().formatParameter(
                    pModel.getParameter().getFormat(),
                    pModel.getLineValue().getValue());

            // odvDataLine.append(Global.TAB);
            if (!stringValue.equals(defaultValue))
                odvDataLine.append(stringValue);
            odvDataLine.append(Global.TAB);
            odvDataLine.append(pModel.getLineValue().getQc());

            if (cptParam < nbParamTot)
                odvDataLine.append(Global.TAB);
        }
        // odvDataLine.append(Global.RN);
        return odvDataLine;
    }

    private StringBuffer getMedatlasDataLine(Station currentStation,
            String inputLine, int indexRecord) throws Exception
    {

        StringBuffer medatlasDataLine = new StringBuffer();

        // -- VALEURS
        // paramètres ajoutés
        long currentDate = 0;
        if (Nemo.getInstance().getDomain().getMedia().getModel().getInputType() != E_InputType.PROFILE)
            currentDate =
                DateTimeUtils.getCurrentDate(currentStation, indexRecord);
        for (ParameterModel pModel : getMedParameterModels())
        {
            pModel.computeAddedParameterValue(currentStation, inputLine,
                currentDate);

            medatlasDataLine.append(pModel.getLineValue().getStringValue());
            medatlasDataLine.append(Global.SPACE);
        }

        // paramètres mesurés
        for (ParameterModel pModel : _model.getParameterModels())
        {
            pModel.computeParameterValue(inputLine);

            medatlasDataLine.append(Formatter.getSingleton().formatParameter(
                pModel.getParameter().getFormat(),
                pModel.getLineValue().getValue()));
            medatlasDataLine.append(Global.SPACE);
        }

        // -- FLAGS QUALITE
        // paramètres ajoutés
        for (ParameterModel pModel : getMedParameterModels())
        {
            medatlasDataLine.append(pModel.getLineValue().getQc());
        }

        // paramètres mesurés
        for (ParameterModel pModel : _model.getParameterModels())
        {
            medatlasDataLine.append(pModel.getLineValue().getQc());
        }

        return medatlasDataLine;
    }

    public StringBuffer getMedatlasLastDataLine()
    {
        StringBuffer medLastDataLine = new StringBuffer();

        int cptParam = 0;

        // paramètres ajoutés
        for (ParameterModel pModel : getMedParameterModels())
        {
            Parameter param = pModel.getParameter();
            medLastDataLine.append(param.getDefault());
            medLastDataLine.append(Global.SPACE);
            cptParam++;
        }

        // paramètres mesurés
        for (ParameterModel pModel : _model.getParameterModels())
        {
            Parameter param = pModel.getParameter();
            medLastDataLine.append(param.getDefault());
            medLastDataLine.append(Global.SPACE);
            cptParam++;
        }

        // ajouts des qcFlags default
        for (int i = 0; i < cptParam; i++)
        {
            medLastDataLine.append(Global.DEFAULT_FLAG);
        }

        return medLastDataLine;
    }

    public StringBuffer getMedParametersDesc() throws ParseException
    {
        StringBuffer paramsDesc = new StringBuffer();

        for (ParameterModel pModel : getMedParameterModels())
        {
            paramsDesc.append(Conversion.MEDATLAS.getDebut());
            paramsDesc.append(getMedParameterDesc(pModel));
        }

        for (ParameterModel pModel : _model.getParameterModels())
        {
            paramsDesc.append(Conversion.MEDATLAS.getDebut());
            paramsDesc.append(getMedParameterDesc(pModel));
        }
        return paramsDesc;
    }

    private StringBuffer getMedParameterDesc(ParameterModel pModel)
            throws ParseException
    {
        // *PRES SEA PRESSURE sea surface=0 (decibar=10000 pascals ) def.=-999.9
        Parameter param = pModel.getParameter();
        StringBuffer paramDesc = new StringBuffer();

        paramDesc.append(Elts.getElt(Elts.param_code, param.getCode()));
        paramDesc.append(Global.SPACE);
        paramDesc.append(Elts.getElt(Elts.param_fullname, param.getFullName()));
        paramDesc.append(Elts.getElt(Elts.param_unit, param.getUnit()));
        paramDesc.append(Elts.getElt(Elts.param_default, param.getDefault()));
        paramDesc.append(Global.RN);

        return paramDesc;
    }

    // RELEASE
    public void release()
    {
        _model.release();
        releaseAddedParameterModels();
    }

    /**
     * release all links for role "parameterModels"
     */
    private void releaseAddedParameterModels()
    {
        for (ParameterModel t : getOdvParameterModels())
        {
            t.release();
            if (_odvParameterModels.contains(t))
                _odvParameterModels.remove(t);
        }
        _odvParameterModels = null;
        for (ParameterModel t : getMedParameterModels())
        {
            t.release();
            if (_medParameterModels.contains(t))
                _medParameterModels.remove(t);
        }
        _medParameterModels = null;
    }

    public void addOdvParameterModel(ParameterModel x)
    {
        if (_odvParameterModels.contains(x))
            return;
        _odvParameterModels.add(x);
    }

    public ParameterModel[] getOdvParameterModels()
    {
        ParameterModel[] v = new ParameterModel[_odvParameterModels.size()];
        _odvParameterModels.toArray(v);
        return v;
    }

    public void addMedParameterModel(ParameterModel x)
    {
        if (_medParameterModels.contains(x))
            return;
        _medParameterModels.add(x);
    }

    public ParameterModel[] getMedParameterModels()
    {
        ParameterModel[] v = new ParameterModel[_medParameterModels.size()];
        _medParameterModels.toArray(v);
        return v;
    }

    public ParameterModel getAddedParameterModel(IdParameter idParam)
    {
        ParameterModel param = null;
        for (ParameterModel current : _medParameterModels)
        {
            if (current.getParameter().getCode().equals(idParam.toString()))
            {
                param = current;
                break;
            }
        }
        if (param == null)
        {
            for (ParameterModel current : _odvParameterModels)
            {
                if (current.getParameter().getCode().equals(idParam.toString()))
                {
                    param = current;
                    break;
                }
            }
        }
        return param;
    }

    /**
     * Ajoute les paramètres fixes en fonction du type de fichier
     */
    public void setAddedParameterModel()
    {
        E_InputType inputType =
            Nemo.getInstance().getDomain().getMedia().getModel().getInputType();
        // reset systematique des parametres stockes dans le dataModel
        releaseAddedParameterModels();
        _odvParameterModels = new ArrayList<ParameterModel>();
        _medParameterModels = new ArrayList<ParameterModel>();

        // *** Ajouts automatiques de certains paramètres
        boolean existDEPH = false;
        boolean existPRES = false;
        boolean existTEMP = false;
        boolean existPSAL = false;
        for (ParameterModel param : _model.getParameterModels())
        {
            if (param.getParameter().getCode().equals(
                IdParameter.DEPH.toString()))
                existDEPH = true;
            else if (param.getParameter().getCode().equals(Intern.paramPRES))
                existPRES = true;
            else if (param.getParameter().getCode().equals(Intern.paramPSAL))
                existPSAL = true;
            else if (param.getParameter().getCode().equals(Intern.paramTEMP))
                existTEMP = true;
        }

        if (inputType == E_InputType.PROFILE)
        {
            // Profile vertical
            if (!existDEPH && existPRES)
            {
                // ajout de DEPH calculé à partir de PRES
                // parameterModel.setFlag(stationModel.parameterModelsAt(indexPRES).getFlag());
                addFixedParameterModel(IdParameter.DEPH);
            }
            if (!existPRES && existDEPH && existPSAL && existTEMP)
            {
                // ajout de DEPH calculé à partir de PRES
                // parameterModel.setFlag(stationModel.parameterModelsAt(indexPRES).getFlag());
                addFixedParameterModel(IdParameter.PRES);
            }
        }
        else
        {
            // Serie temporelle ou Trajectoire
            // Ajout YEAR, MNTH, DAYX et TIME pour Medatlas
            addFixedParameterModel(IdParameter.YEAR);
            addFixedParameterModel(IdParameter.MNTH);
            addFixedParameterModel(IdParameter.DAYX);
            addFixedParameterModel(IdParameter.TIME);

            if (inputType == E_InputType.TIMESERIES)
            {
                // Ajout de "Chronological Julian Date" ou "time-ISO8601 pour
                // ODV
                if (Nemo.getInstance().getConfig().isDateISO())
                {
                    addFixedParameterModel(IdParameter.DTUT);
                }
                else
                {
                    addFixedParameterModel(IdParameter.CJDY);
                }

                // Serie temporelle
                // Ajout du paramètre DEPH ( = Sensor Depth) pour ODV
                if (!existDEPH)
                    addFixedParameterModel(IdParameter.DEPH);
            }
            else
            {
                // Trajectoire
                // Ajout LATX et LONX pour Medatlas
                addFixedParameterModel(IdParameter.LATX);
                addFixedParameterModel(IdParameter.LONX);

                
                // si bottomDepth est en manuel
                if (Nemo.getInstance().getDomain().getCruise().getStation()
                    .getModel().getProperty(Intern.S_DEPTH).isInData())
                {
                    // FAE 4471 on regarde si le parametre BATH est dans les parametres de l'onlget données
                    boolean isBathPresentInDataTab=false;
                    for (ParameterModel param : Nemo.getInstance().getDomain().getCruise().getStation().getData().getModel().getParameterModels())
                    {
                        if(param.getParameter().getCode().equals(IdParameter.BATH.name()))
                        {
                            isBathPresentInDataTab=true;
                        }
                    }
                    // -> Ajout BATH pour Medatlas 
                    // - si "Profondeur" saisie dans les données
                    // -et si il n'est pas déjà présent dans l'onglet data
                    if (!isBathPresentInDataTab)
                    {
                        addFixedParameterModel(IdParameter.BATH);
                    }
                }

                // Ajout du paramètre DEPH ( = Sensor Depth) pour ODV
                if (!existDEPH)
                    addFixedParameterModel(IdParameter.DEPH);
            }
        }
    }

    private void addFixedParameterModel(IdParameter code)
    {
        AddedParameter para = new AddedParameter(code);

        // FAE 4045
        if (_model.getParameterModels().length > 1)
        {
            para.setP011(_model.getParameterModels()[_model
                .getParameterModels().length - 1].getParameter().isP011());
        }

        ParameterModel parameterModel = new ParameterModel();
        parameterModel.setParameter(para);

        PropertyModel propertyModel =
            para.getPropertyModel(Nemo.getInstance().getDomain().getCruise()
                .getStation().getModel());
        if (propertyModel != null)
        {
            // fragment du parametre
            Fragment headFrag = propertyModel.getFragment();
            if (headFrag != null)
            {
                LineFragment frag = new LineFragment();
                frag.setFirstCharOffset(headFrag.getFirstCharOffset());
                frag.setLastCharOffset(headFrag.getLastCharOffset());
                parameterModel.setFragment(frag);
            }
        }

        switch (para.getIdParam().getCible())
        {
            case ODV:
                if (!_odvParameterModels.contains(parameterModel))
                    _odvParameterModels.add(parameterModel);
            break;
            case MEDATLAS:
                if (!_medParameterModels.contains(parameterModel))
                    _medParameterModels.add(parameterModel);
            break;
        }
    }

    public StringBuffer getSDNparameters(Conversion c)
    {
        StringBuffer sdnParams = new StringBuffer();

        sdnParams.append(c.getDebut());
        sdnParams.append(Intern.file_mapping);
        sdnParams.append(Global.RN);

        /** 
         * added parameters
         */
        if (c.equals(Conversion.ODV))
        {
            for (ParameterModel param : getAddedParameterModels(c))
            {
                param.getParameter().setP011( _model.getParameterModels()[0].getParameter().isP011());
            }
        } 
        else
        {
            for (ParameterModel param : getAddedParameterModels(c))
            {
                param.getParameter().setP011(false);
            }
        }
        
        
        for (ParameterModel param : getAddedParameterModels(c))
        {
            sdnParams.append(parameterMap(c.getDebut(), param.getParameter(), c));
        }

        /**
         * parameters
         */
        for (ParameterModel param : _model.getParameterModels())
        {
            sdnParams.append(parameterMap(c.getDebut(), param.getParameter(), c));
        }

        return sdnParams;
    }

    private static StringBuffer parameterMap(String start, Parameter param,
            Conversion c)
    {
//        String code = param.getCode();
        StringBuffer sdnMapping = new StringBuffer();
        sdnMapping.append(start);
//        try
//        {
//            sdnMapping.append(Intern.getString(code));
//        }
//        catch (MissingResourceException e)
//        {
            /**
             * -----------------------------------------------------
             * subject
             */
            // FAE 4045
//            if (c.equals(Conversion.ODV) && param.isP011())
//
//            {
//                if (param.getCodeP011() == null
//                        || param.getCodeP011().equals(""))
//                {
//                    param.setCodeP011(Utils.extraitCodeURN(NemoLists.getVocabMatch(
//                        Intern.mapping_subject, Intern.mapping_objet, code,
//                        Intern.NemoLists_unknown)));
//                }
//                sdnMapping.append(getURNSyntaxe(URNtype.subject, 
//                    param.getCodeP011(),
//                    null, 
//                    null));
//            }
//            else{
//                sdnMapping.append(getURNSyntaxe(URNtype.subject, 
//                    code, 
//                    null,
//                    null));
//            }
        sdnMapping.append(getURNSyntaxe(URNtype.subject, 
            param.getCodeToUse(), 
          null,
          null));
            /** -----------------------------------------------------
             * object 
             * 
             * si param P011, on met le code P011
             * si param P091, on met le premier P011 de la liste de P011 correspondants au P091
             */
            if (param.isP011())
            {
                sdnMapping.append(getURNSyntaxe(
                    URNtype.object, 
                    param.getCodeP011(), 
                    null, 
                    Intern.mapping_objet));
            }
            else
            {
                sdnMapping.append(getURNSyntaxe(
                    URNtype.object, 
                    param.getCode(),
                    Intern.mapping_subject, 
                    Intern.mapping_objet));
            }

            /** -----------------------------------------------------
             * units 
             * 
             * unitCode utilisé seulement pour les parametres P011 choisis depuis P021 
             * (qui n'ont pas d'unité, et dont l'unité est donc choisie manuellement 
             * par l'utilisateur depuis la liste P061)
             * 
             * 
             * s'il est vide, on va chercher l'unité correspondant au P091 choisi ou ayant servi à choisir le P021
             * sinon, on prend sa valeur qui a été renseignée quen l'utilisateur a choisi l'unité dans la liste des P061
             */
            if (param.getUnitCode()==null  || param.getUnitCode().equals(Global.EMPTY_STRING)){
                sdnMapping.append(getURNSyntaxe(URNtype.units, 
                    param.getCode(),
                    Intern.mapping_subject, 
                    Intern.mapping_unit));
            }else{
                // FAE 6542
                sdnMapping.append(getURNSyntaxe(URNtype.units, 
                    param.getUnitCode(),
                    null, 
                    Intern.mapping_unit));
            }
//        }
        sdnMapping.append(Global.RN);
        return sdnMapping;
    }

    private static StringBuffer getURNSyntaxe(URNtype type, String code,
            String list1, String list2)
    {
        StringBuffer urnSyntaxe = new StringBuffer();

        urnSyntaxe.append(Intern.xml_tag_start);
        urnSyntaxe.append(type.toString());
        urnSyntaxe.append(Intern.xml_tag_end);

        urnSyntaxe.append(Intern.file_mapping_sdn);
        urnSyntaxe.append(Global.DBL_PT);

        switch (type)
        {
            case subject:
                urnSyntaxe.append(Intern.file_mapping_subject);
                urnSyntaxe.append(Global.DBL_PT);
                urnSyntaxe.append(code);
            break;
            case object:
            case units:
                urnSyntaxe.append(list2);
                urnSyntaxe.append(Global.DBL_PT);
                urnSyntaxe.append(Global.DBL_PT);
                String pdvCode;
                if (list1 != null)
                    pdvCode =
                        Utils.extraitCodeURN(NemoLists.getVocabMatch(list1,
                            list2, code, Intern.NemoLists_unknown));
                else
                    pdvCode = code;
                urnSyntaxe.append(pdvCode);
            break;
        }

        urnSyntaxe.append(Intern.xml_tag_start);
        urnSyntaxe.append(Intern.xml_tag_close);
        urnSyntaxe.append(type.toString());
        urnSyntaxe.append(Intern.xml_tag_end);

        return urnSyntaxe;
    }

    /**
     * Getter pour model.
     * 
     * @return DataModel
     * @see DataDomain.java#model
     */
    public DataModel getModel()
    {
        return _model;
    }

    /**
     * Setter pour model
     * 
     * @param model :
     *            nouvelle valeur pour model
     * @see DataDomain.java#model
     */
    public void setModel(DataModel model)
    {
        _model = model;
    }

    // public int getPresIndex()
    // {
    // return _presIndex;
    // }

    // public void setPresIndex(int index)
    // {
    // _presIndex = index;
    // }
}
