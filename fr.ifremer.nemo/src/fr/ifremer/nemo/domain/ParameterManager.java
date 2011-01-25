/* ============================================================================
 * Nom du fichier	: ParameterManager.java
 * Auteur			: sbregent
 * Date				: 21 janv. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.internal.ole.win32.ISpecifyPropertyPages;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.DataModel;
import fr.ifremer.nemo.input.Fragment;
import fr.ifremer.nemo.input.LineFragment;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.parameters.AddedParameter;
import fr.ifremer.nemo.input.parameters.Parameter;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.input.parameters.AddedParameter.IdParameter;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_MesureType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;

/**
 * Description: classe de gestion des regles sur les parametres
 * gere les regles de detection et d'ajout automatique a partir du modele (appelees lors de la conversion)
 * cette classe ne DOIT PAS appeler l'IHM car ces methodes doivent pouvoir etre appelees en batch
 * 
 * Par contre, l'IHM peut utiliser des methodes de ceette classe, car les tests peuvent être utilisés pour la validation de l'onglet data
 * @author sbregent
 *
 */
public class ParameterManager
{
    /**
     * Liste, ordonnee, des paramètres rajoutés pour ODV
     */
    protected List<ParameterModel> _odvParameterModels;

    /**
     * Liste, ordonnee, des paramètres rajoutés pour Medatlas
     */
    protected List<ParameterModel> _medParameterModels;
    /**
     * Informations descrivant l'onglet Data
     */
    protected DataModel _model;
    
    
    /**
     * codes des parametres verticaux de reference
     */
    private String _p_depthBSSurf_entryKey;
    private String _p_depthBBed_entryKey;
    private String _p_pressure_entryKey;

    
    public ParameterManager(DataModel model)
    {
        _odvParameterModels = new ArrayList<ParameterModel>();
        _medParameterModels = new ArrayList<ParameterModel>();
        _model = model;
    }
    private void addFixedParameterModel(IdParameter code)
    {
        
        System.out.println("ajout du parametre: "+ code.toString());
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
            para.getPropertyModel(Nemo.getInstance().getDomain().getStation().getModel());
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
        // TODO BGT ko en P021 ne trouve pas PRES
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
                // info: Nemo.getInstance().getDomain().getCruise().getStation().getModel().getProperty(Intern.S_DEPTH).isInData()=true <=> automatique
                if (Nemo.getInstance().getDomain().getStation()
                    .getModel().getProperty(Intern.S_DEPTH).isInData())
                {
                    // FAE 4471 on regarde si le parametre BATH est dans les parametres de l'onlget données
                    boolean isBathPresentInDataTab=false;
                    for (ParameterModel param : _model.getParameterModels())
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
    public ParameterModel[] getAddedParameterModels(Conversion c)
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
    /**
     * release all links for role "parameterModels"
     */
    public void releaseAddedParameterModels()
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
    
    
    public boolean isPossiblePressureParameter(TypeData typeData)
    {
        return  isPossiblePressureParameter(new Parameter(typeData));
    }
    public boolean isPossiblePressureParameter(Parameter param)
    {
        boolean res = false;
        DataListTypeEnum dataListType = Nemo.getInstance().getDomain().getData().getModel().getDataListType();
        if (dataListType == DataListTypeEnum.P011_VIA_P021)
        {
            if(param.getCode().equals(Intern.paramAHGT)){
                res=true;
            }
        }else if(dataListType == DataListTypeEnum.P091 ||dataListType == DataListTypeEnum.P011_VIA_P091 ){
            if (param.getCode().equals(Intern.paramPRES))
            {
                res=true;
            }
        }
        return res;
    }
    
    public boolean isPossibleDepthBSSurfParameter(TypeData typeData)
    {
        return  isPossibleDepthBSSurfParameter(new Parameter(typeData));
    }
    public boolean isPossibleDepthBSSurfParameter(Parameter param)
    {
        boolean res = false;
        DataListTypeEnum dataListType = Nemo.getInstance().getDomain().getData().getModel().getDataListType();
        if (dataListType == DataListTypeEnum.P011_VIA_P021)
        {
            if(param.getCode().equals(Intern.paramAHGT)){
                res=true;
            }
        }else if(dataListType == DataListTypeEnum.P091 ||dataListType == DataListTypeEnum.P011_VIA_P091 ){
            if (param.getCode().equals(Intern.paramDEPH))
            {
                res=true;
            }
        }
        return res;
    }
    public boolean isPossibleDepthBSBedParameter(TypeData typeData)
    {
        return  isPossibleDepthBSBedParameter(new Parameter(typeData));
    }
    public boolean isPossibleDepthBSBedParameter(Parameter param)
    {
        boolean res = false;
        DataListTypeEnum dataListType = Nemo.getInstance().getDomain().getData().getModel().getDataListType();
        if (dataListType == DataListTypeEnum.P011_VIA_P021)
        {
            if(param.getCode().equals(Intern.paramAHGT)){
                res=true;
            }
        }else if(dataListType == DataListTypeEnum.P091 ||dataListType == DataListTypeEnum.P011_VIA_P091 ){
            if (param.getCode().equals(Intern.paramDPSF))
            {
                res=true;
            }
        }
        return res;
    }
    
    
//    public boolean isDepthBSBedParameterNeeded()
//    {
//        boolean res = false;
//        res = 
//            (
//                    Nemo.getInstance().getDomain().getMedia().getModel().getInputType() == E_InputType.PROFILE
//                    &&
//                    (
//                            Nemo.getInstance().getDomain().getConversion().getModel().getOutputFormat()==E_TypeConversion.ODV_MONOSTATION
//                            ||
//                            Nemo.getInstance().getDomain().getConversion().getModel().getOutputFormat()==E_TypeConversion.ODV_MULTISTATIONS
//                    )
//                    &&
//                    Nemo.getInstance().getDomain().getData().getModel().getMesureType()==E_MesureType.BSBED
//            );
//        return res;
//    }
    
    public String getP_depthBBed_entryKey()
    {
        return _p_depthBBed_entryKey;
    }
    public void setP_depthBBed_entryKey(String bed_entryKey)
    {
        _p_depthBBed_entryKey = bed_entryKey;
    }
    public String getP_depthBSSurf_entryKey()
    {
        return _p_depthBSSurf_entryKey;
    }
    public void setP_depthBSSurf_entryKey(String surf_entryKey)
    {
        _p_depthBSSurf_entryKey = surf_entryKey;
    }
    public String getP_pressure_entryKey()
    {
        return _p_pressure_entryKey;
    }
    public void setP_pressure_entryKey(String key)
    {
        _p_pressure_entryKey = key;
    }
}
