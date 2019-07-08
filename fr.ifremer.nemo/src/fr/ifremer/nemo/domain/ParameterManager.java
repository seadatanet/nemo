/*
 * ============================================================================
 * Nom du fichier : ParameterManager.java
 * Auteur : sbregent
 * Date : 21 janv. 11
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.util.ArrayList;
import java.util.List;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.DataModel;
import fr.ifremer.nemo.input.Fragment;
import fr.ifremer.nemo.input.LineFragment;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.parameters.Parameter;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.input.parameters.VerticalParametersList;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_ADEPZZ01;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_AHGTZZ01;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_BATH;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_CJDY;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_DAYX;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_DEPH;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_DEPHPR01;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_DTUT;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_HEIGHT;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_LATX;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_LONX;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_MNTH;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_PRES;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_TIME;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_YEAR;
import fr.ifremer.nemo.input.parameters.addedParameters.AddedParam;
import fr.ifremer.nemo.input.parameters.addedParameters.AddedParam.IdParameter;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_Variant;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_MesureType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Intern;

/**
 * Description: classe de gestion des regles sur les parametres gere les regles
 * de detection et d'ajout automatique a partir du modele (appelees lors de la
 * conversion) cette classe ne DOIT PAS appeler l'IHM car ces methodes doivent
 * pouvoir etre appelees en batch
 * 
 * Par contre, l'IHM peut utiliser des methodes de ceette classe, car les tests
 * peuvent être utilisés pour la validation de l'onglet data
 * 
 * @author sbregent
 * 
 */
public class ParameterManager {
	/**
	 * Liste, ordonnee, des paramètres rajoutés pour ODV
	 */
	protected List<ParameterModel> _odvParameterModels;

	/**
	 * Liste, ordonnee, des paramètres rajoutés pour Medatlas
	 */
	protected List<ParameterModel> _medParameterModels;
	private DataListTypeEnum dataListType;
	private VerticalParametersList verticalParametersList;
	private E_InputType inputType;
	private E_TypeConversion conversionType;
	private E_MesureType mesureType;

	/**
	 * Informations descrivant l'onglet Data
	 */

	public ParameterManager(DataModel model) {
		_odvParameterModels = new ArrayList<ParameterModel>();
		_medParameterModels = new ArrayList<ParameterModel>();
	}

	private E_TypeConversion getConversionType() {
		conversionType = Nemo.getInstance().getDomain().getConversion().getModel().getOutputFormat();
		return conversionType;

	}

	private DataListTypeEnum getDataList() {
		if (Nemo.getInstance().isBatchMode()) {
			dataListType = Nemo.getInstance().getDomain().getData().getModel().getDataListType();
		} else {
			dataListType = RulesManager.getInstance().getMyDataListType();
		}
		return dataListType;
	}

	private VerticalParametersList getVerticalParametersList() {
		verticalParametersList = Nemo.getInstance().getDomain().getData().getModel().getVerticalParametersList();
		return verticalParametersList;
	}

	private E_InputType getInputType() {
		inputType = Nemo.getInstance().getDomain().getMedia().getModel().getInputType();
		return inputType;
	}

	private E_MesureType getMesureType() {

		mesureType = Nemo.getInstance().getDomain().getData().getModel().getMesureType();
		return mesureType;
	}

	private AddedParam createAddedParam(IdParameter code) {
		DataListTypeEnum dataList = getDataList();
		AddedParam addedParam = null;
		boolean isNetCDF = getConversionType() == E_TypeConversion.NETCDF_MONOSTATION || getConversionType() == E_TypeConversion.NETCDF_MULTISTATIONS;

		if (code == IdParameter.YEAR) {
			addedParam = new AP_YEAR();
		} else if (code == IdParameter.MNTH) {
			addedParam = new AP_MNTH();
		} else if (code == IdParameter.DAYX) {
			addedParam = new AP_DAYX();
		} else if (code == IdParameter.TIME) {
			addedParam = new AP_TIME();
		} else if (code == IdParameter.DTUT) {
			addedParam = new AP_DTUT();
		} else if (code == IdParameter.CJDY) {
			addedParam = new AP_CJDY();
		} else if (code == IdParameter.LATX) {
			addedParam = new AP_LATX();
		} else if (code == IdParameter.LONX) {
			addedParam = new AP_LONX();
		} else if (code == IdParameter.BATH) {
			addedParam = new AP_BATH();
		} else if (code == IdParameter.PRES) {
			addedParam = new AP_PRES();
		}

		// profondeur

		else if (code == IdParameter.generic_depthBSSurf_FromPressure) {
			if (dataList == DataListTypeEnum.P091) {
				addedParam = new AP_DEPH();
			} else {
				addedParam = new AP_DEPHPR01();
				if (isNetCDF)
					addedParam = new AP_DEPHPR01(Conversion.NETCDF);
			}
		} else if (code == IdParameter.generic_depthBSSurf_FromSensor) {
			if (dataList == DataListTypeEnum.P091) {
				addedParam = new AP_DEPH();
			} else {
				addedParam = new AP_ADEPZZ01();
				if (isNetCDF)
					addedParam = new AP_ADEPZZ01(Conversion.NETCDF);

			}
		} else if (code == IdParameter.generic_heightASLvl_FromSensor) {
			if (dataList == DataListTypeEnum.P091) {
				addedParam = new AP_HEIGHT();
			} else {
				addedParam = new AP_AHGTZZ01();
				if (isNetCDF)
					addedParam = new AP_AHGTZZ01(Conversion.NETCDF);

			}
		}// TODO BGT below sea bed a rajouter???
		return addedParam;
	}

	private void addFixedParameterModel(IdParameter code) {
		AddedParam addedParam = createAddedParam(code);

		ParameterModel parameterModel = new ParameterModel();
		parameterModel.setParameter(addedParam);

		PropertyModel propertyModel = addedParam.getPropertyModel(Nemo.getInstance().getDomain().getStation().getModel());
		if (propertyModel != null) {
			// fragment du parametre
			Fragment headFrag = propertyModel.getFragment();
			if (headFrag != null) {
				LineFragment frag = new LineFragment();
				frag.setFirstCharOffset(headFrag.getFirstCharOffset());
				frag.setLastCharOffset(headFrag.getLastCharOffset());
				parameterModel.setFragment(frag);
			}
		}

		switch (addedParam.getCible()) {
		case ODV:
			if (!_odvParameterModels.contains(parameterModel))
				_odvParameterModels.add(parameterModel);
			break;
		case MEDATLAS:
			if (!_medParameterModels.contains(parameterModel))
				_medParameterModels.add(parameterModel);
			break;
		// case NETCDF:
		// // TODO BGT: le cas n'existe pas puisque la cible utilisée en netcdf
		// est ODV
		// à modifier pour y voir plus clair

		}

	}

	public void setAddedParameterModel() {
		if (Nemo.getInstance().getDomain().getMedia().getModel().getVariant() != E_Variant.PHYSICOCHEMICAL) {
			// no parameter to add
			return;
		}
		/**
		 * ====================================================================
		 * reset systematique des parametres stockes dans le dataModel
		 */
		_odvParameterModels = new ArrayList<ParameterModel>();
		_medParameterModels = new ArrayList<ParameterModel>();

		/**
		 * ====================================================================
		 * recup des infos utiles pour la suite
		 */
		boolean isProfile = getInputType() == E_InputType.PROFILE;
		boolean isTrajectory = getInputType() == E_InputType.TRAJECTORY;
		boolean isTimeSeries = getInputType() == E_InputType.TIMESERIES;

		boolean isMedatlas = getConversionType() == E_TypeConversion.MEDATLAS;
		boolean isODV = getConversionType() == E_TypeConversion.ODV_MONOSTATION || getConversionType() == E_TypeConversion.ODV_MULTISTATIONS;
		boolean isNetCDF = getConversionType() == E_TypeConversion.NETCDF_MONOSTATION || getConversionType() == E_TypeConversion.NETCDF_MULTISTATIONS;

		DataModel dataModel = Nemo.getInstance().getDomain().getData().getModel();
		// recuperation des parametres verticaux
		String p_depthBSBed = getVerticalParametersList().getP_depthBSBed();
		String p_depthBSSurf = getVerticalParametersList().getP_depthBSSurf();
		String p_pressure = getVerticalParametersList().getP_pressure();
		String p_heightASLvl = getVerticalParametersList().getP_heightASLvl();
		boolean exist_p_depthBSSurf = !p_depthBSSurf.equals(Global.EMPTY_STRING);
		boolean exist_p_pressure = !p_pressure.equals(Global.EMPTY_STRING);
		boolean exist_p_heightASLvl = !p_heightASLvl.equals(Global.EMPTY_STRING);

		// verif PSAL et TEMP (code P091 uniquement car test pour MEDATLAS, donc
		// inutilisé en P011viaP021)
		boolean existTEMP = false;
		boolean existPSAL = false;
		for (ParameterModel param : dataModel.getParameterModels()) {
			if (param.getParameter().getCode().equals(getP_salinityCode()))
				existPSAL = true;
			else if (param.getParameter().getCode().equals(getP_temperatureCode()))
				existTEMP = true;
		}

		boolean isBathPresentInDataTab = false;
		for (ParameterModel param : dataModel.getParameterModels()) {
			if (param.getParameter().getCode().equals(IdParameter.BATH.name())) {
				isBathPresentInDataTab = true;
			}
		}
		/**
		 * ====================================================================
		 * gestion parametres ajoutes ATTENTION, pour chaque type de conversion,
		 * l'ordre d'ajout des parametres est important (car ordre des colonnes
		 * dans le fichier en dépend)
		 */

		if (isMedatlas) {
			// ordre: PRES, YMDT, LAT LON, nemo parameters, BATH
// 20755: DEPTH is allowed instead of pres -> remove P3 rule
//			if (isProfile) {
//				/** aap_p3 */
//				if (!exist_p_pressure && exist_p_depthBSSurf && existPSAL && existTEMP) {
//					// ajout de DEPH calculé à partir de PRES
//					addFixedParameterModel(IdParameter.PRES);
//				}
//			}
			if (isTrajectory || isTimeSeries) {
				/** aap_p5 */
				// Ajout YEAR, MNTH, DAYX et TIME pour Medatlas
				addFixedParameterModel(IdParameter.YEAR);
				addFixedParameterModel(IdParameter.MNTH);
				addFixedParameterModel(IdParameter.DAYX);
				addFixedParameterModel(IdParameter.TIME);
			}

			if (isTrajectory) {
				/** aap_p8 */
				// Ajout LATX et LONX pour Medatlas
				addFixedParameterModel(IdParameter.LATX);
				addFixedParameterModel(IdParameter.LONX);

				/** aap_p4 */
				// info:
				// Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DEPTH).isInData()=true
				// <=> automatique
				boolean isBottomDepthAuto = Nemo.getInstance().getDomain()
						.getStation().getModel().getProperty(Intern.S_DEPTH)
						.isInData();

				if (isBottomDepthAuto) {
					// FAE 4471 on regarde si le parametre BATH est dans les
					// parametres de l'onlget données
					// -> Ajout BATH pour Medatlas
					// - si "Profondeur" saisie dans les données
					// -et si il n'est pas déjà présent dans l'onglet data
					if (!isBathPresentInDataTab) {
						addFixedParameterModel(IdParameter.BATH);
					}
				}

			}
		}
		if (isODV) {
			// ordre: DTUT/CJDY, DEPH
			if (isTimeSeries) {
				/** aap_p6 */
				// Ajout de "Chronological Julian Date" ou "time-ISO8601
				if (Nemo.getInstance().getConfig().isDateISO()) {
					addFixedParameterModel(IdParameter.DTUT);
				}
				/** aap_p7 */
				else {
					addFixedParameterModel(IdParameter.CJDY);
				}
			}
		}
		if (isODV) {
			/** aap_p 1 2 */
			// Ajout du paramètre DEPH ( = Sensor Depth) pour ODV
			if (!exist_p_depthBSSurf && getMesureType() == E_MesureType.BSSURF) {
				if (isProfile) {
					addFixedParameterModel(IdParameter.generic_depthBSSurf_FromPressure);
				} else {
					addFixedParameterModel(IdParameter.generic_depthBSSurf_FromSensor);
				}
			}
			/** aap_p 9 */
			if (isTimeSeries && getMesureType() == E_MesureType.ASLVL) {
				addFixedParameterModel(IdParameter.generic_heightASLvl_FromSensor);
			}
		}
		if (isNetCDF) {
			/** aap_p 12 */
			// Ajout du paramètre DEPH ( = Sensor Depth) por TIMESERIES
			boolean generic_depthBSSurf_FromSensor=false;
			if (isTimeSeries && getMesureType() != E_MesureType.ASLVL) {
				addFixedParameterModel(IdParameter.generic_depthBSSurf_FromSensor);
				generic_depthBSSurf_FromSensor=true;
			}
			/** aap_p 13 */
			if (((isTimeSeries || !exist_p_heightASLvl) && getMesureType() == E_MesureType.ASLVL)) {
				addFixedParameterModel(IdParameter.generic_heightASLvl_FromSensor);
			}

			if (!exist_p_depthBSSurf && !exist_p_pressure && getMesureType() == E_MesureType.BSSURF) {
				if (isProfile) {
					/** aap_p 10 */
					addFixedParameterModel(IdParameter.generic_depthBSSurf_FromPressure);
				} else if (! generic_depthBSSurf_FromSensor){
					/** aap_p 11 */
					addFixedParameterModel(IdParameter.generic_depthBSSurf_FromSensor);
				}
			}
		}

	}

	/**
	 * Ajoute les paramètres fixes en fonction du type de fichier
	 */
	// public void setAddedParameterModelOrig() {
	//
	// // reset systematique des parametres stockes dans le dataModel
	// // releaseAddedParameterModels();
	// _odvParameterModels = new ArrayList<ParameterModel>();
	// _medParameterModels = new ArrayList<ParameterModel>();
	//
	// // *** Ajouts automatiques de certains paramètres
	// boolean existDEPH = false;
	// boolean existPRES = false;
	// boolean existTEMP = false;
	// boolean existPSAL = false;
	// DataModel dataModel =
	// Nemo.getInstance().getDomain().getData().getModel();
	// for (ParameterModel param : dataModel.getParameterModels()) {
	// if (param.getParameter().getCode().equals(IdParameter.DEPH.toString()))
	// existDEPH = true;
	// else if (param.getParameter().getCode().equals(Intern.paramPRES))
	// existPRES = true;
	// else if (param.getParameter().getCode().equals(Intern.paramPSAL))
	// existPSAL = true;
	// else if (param.getParameter().getCode().equals(Intern.paramTEMP))
	// existTEMP = true;
	// }
	//
	// if (getInputType() == E_InputType.PROFILE) {
	// /** aap_p1 */
	// // Profile vertical
	// if (!existDEPH && existPRES) {
	// // ajout de DEPH calculé à partir de PRES
	// //
	// parameterModel.setFlag(stationModel.parameterModelsAt(indexPRES).getFlag());
	// addFixedParameterModel(IdParameter.DEPH);
	// }
	// /**
	// * *****************************************************************
	// * ******************
	// */
	// /** aap_p3 */
	// if (!existPRES && existDEPH && existPSAL && existTEMP) {
	// // ajout de DEPH calculé à partir de PRES
	// //
	// parameterModel.setFlag(stationModel.parameterModelsAt(indexPRES).getFlag());
	// addFixedParameterModel(IdParameter.PRES);
	// }
	// /**
	// * *****************************************************************
	// * ******************
	// */
	// } else {
	// // Serie temporelle ou Trajectoire
	//
	// /** aap_p5 */
	// // Ajout YEAR, MNTH, DAYX et TIME pour Medatlas
	// addFixedParameterModel(IdParameter.YEAR);
	// addFixedParameterModel(IdParameter.MNTH);
	// addFixedParameterModel(IdParameter.DAYX);
	// addFixedParameterModel(IdParameter.TIME);
	// /**
	// * *****************************************************************
	// * ******************
	// */
	//
	// if (getInputType() == E_InputType.TIMESERIES) {
	// /** aap_p6 */
	// // Ajout de "Chronological Julian Date" ou "time-ISO8601 pour
	// // ODV
	// if (Nemo.getInstance().getConfig().isDateISO()) {
	// addFixedParameterModel(IdParameter.DTUT);
	// }
	// /**
	// * *************************************************************
	// * **********************
	// */
	// /** aap_p7 */
	// else {
	// addFixedParameterModel(IdParameter.CJDY);
	// }
	// /**
	// * *************************************************************
	// * **********************
	// */
	//
	// /** aap_p2 */
	// // Serie temporelle
	// // Ajout du paramètre DEPH ( = Sensor Depth) pour ODV
	// if (!existDEPH)
	// addFixedParameterModel(IdParameter.DEPH);
	// /**
	// * *************************************************************
	// * **********************
	// */
	// } else {
	// /** aap_p8 */
	// // Trajectoire
	// // Ajout LATX et LONX pour Medatlas
	// addFixedParameterModel(IdParameter.LATX);
	// addFixedParameterModel(IdParameter.LONX);
	// /**
	// * *************************************************************
	// * **********************
	// */
	//
	// /** aap_p4 */
	// // si bottomDepth est en manuel
	// // info:
	// //
	// Nemo.getInstance().getDomain().getCruise().getStation().getModel().getProperty(Intern.S_DEPTH).isInData()=true
	// // <=> automatique
	// if
	// (Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DEPTH).isInData())
	// {
	// // FAE 4471 on regarde si le parametre BATH est dans les
	// // parametres de l'onlget données
	// boolean isBathPresentInDataTab = false;
	// for (ParameterModel param : dataModel.getParameterModels()) {
	// if (param.getParameter().getCode().equals(IdParameter.BATH.name())) {
	// isBathPresentInDataTab = true;
	// }
	// }
	// // -> Ajout BATH pour Medatlas
	// // - si "Profondeur" saisie dans les données
	// // -et si il n'est pas déjà présent dans l'onglet data
	// if (!isBathPresentInDataTab) {
	// addFixedParameterModel(IdParameter.BATH);
	// }
	// }
	// /**
	// * *************************************************************
	// * **********************
	// */
	//
	// /** aap_p2 */
	// // Ajout du paramètre DEPH ( = Sensor Depth) pour ODV
	// if (!existDEPH)
	// addFixedParameterModel(IdParameter.DEPH);
	// /**
	// * *************************************************************
	// * **********************
	// */
	// }
	// }
	// }

	public ParameterModel[] getAddedParameterModels(Conversion c) {
		ParameterModel[] addedParameters = null;
		// DataDomain data =
		// Nemo.getInstance().getDomain().getCruise().getStation().getData();
		switch (c) {
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
	public void releaseAddedParameterModels() {
		for (ParameterModel t : getOdvParameterModels()) {
			t.release();
			if (_odvParameterModels.contains(t))
				_odvParameterModels.remove(t);
		}
		_odvParameterModels = null;
		for (ParameterModel t : getMedParameterModels()) {
			t.release();
			if (_medParameterModels.contains(t))
				_medParameterModels.remove(t);
		}
		_medParameterModels = null;
	}

	public void addOdvParameterModel(ParameterModel x) {
		if (_odvParameterModels.contains(x))
			return;
		_odvParameterModels.add(x);
	}

	public ParameterModel[] getOdvParameterModels() {
		ParameterModel[] v = new ParameterModel[_odvParameterModels.size()];
		_odvParameterModels.toArray(v);
		return v;
	}

	public ParameterModel[] getNetCDFParameterModels() {
		ParameterModel[] v = new ParameterModel[_odvParameterModels.size()];
		_odvParameterModels.toArray(v);
		return v;
	}

	public void addMedParameterModel(ParameterModel x) {
		if (_medParameterModels.contains(x))
			return;
		_medParameterModels.add(x);
	}

	public ParameterModel[] getMedParameterModels() {
		ParameterModel[] v = new ParameterModel[_medParameterModels.size()];
		_medParameterModels.toArray(v);
		return v;
	}

	public ParameterModel getAddedParameterModel(IdParameter idParam) {
		ParameterModel param = null;
		for (ParameterModel current : _medParameterModels) {
			if (current.getParameter().getCode().equals(idParam.toString())) {
				param = current;
				break;
			}
		}
		if (param == null) {
			for (ParameterModel current : _odvParameterModels) {
				if (current.getParameter().getCode().equals(idParam.toString())) {
					param = current;
					break;
				}
			}
		}
		return param;
	}

	public boolean isPossible_PressureParameter(TypeData typeData) {
		return isPossible_PressureParameter(new Parameter(typeData));
	}

	public boolean isPossible_PressureParameter(Parameter param) {
		return param.getCode().equals(getP_pressureCode());
	}

	public boolean isPossible_DepthBSSurfParameter(TypeData typeData) {
		return isPossible_DepthBSSurfParameter(new Parameter(typeData));
	}

	public boolean isPossible_DepthBSSurfParameter(Parameter param) {
		return param.getCode().equals(getP_depthBSSurfCode());
	}

	public boolean isPossible_DepthBSBedParameter(TypeData typeData) {
		return isPossible_DepthBSBedParameter(new Parameter(typeData));
	}

	public boolean isPossible_HeightASlvlParameter(Parameter param) {
		return param.getCode().equals(getP_depthBSBedCode());
	}

	public boolean isPossible_HeightASlvlParameter(TypeData typeData) {
		return isPossible_HeightASlvlParameter(new Parameter(typeData));
	}

	public boolean isPossible_DepthBSBedParameter(Parameter param) {
		return param.getCode().equals(getP_heightASLvlCode());
	}

	public String getP_depthBSSurfCode() {
		String code = Global.EMPTY_STRING;
		DataListTypeEnum theDataListType = getDataList();
		if (theDataListType == DataListTypeEnum.P011_VIA_P021) {
			code = Intern.paramAHGT;
		} else if (theDataListType == DataListTypeEnum.P091 || theDataListType == DataListTypeEnum.P011_VIA_P091) {
			code = Intern.paramDEPH;
		}
		return code;
	}

	public String getP_depthBSBedCode() {
		String code = Global.EMPTY_STRING;
		DataListTypeEnum theDataListType = getDataList();
		if (theDataListType == DataListTypeEnum.P011_VIA_P021) {
			code = Intern.paramAHGT;
		} else if (theDataListType == DataListTypeEnum.P091 || theDataListType == DataListTypeEnum.P011_VIA_P091) {
			code = Intern.paramDPSF;
		}
		return code;
	}

	public String getP_heightASLvlCode() {
		String code = Global.EMPTY_STRING;
		DataListTypeEnum theDataListType = getDataList();
		if (theDataListType == DataListTypeEnum.P011_VIA_P021) {
			code = Intern.paramAHGT;
		} else if (theDataListType == DataListTypeEnum.P091 || theDataListType == DataListTypeEnum.P011_VIA_P091) {
			code = Intern.paramDPSF;
		}
		return code;
	}

	public String getP_pressureCode() {
		String code = Global.EMPTY_STRING;
		DataListTypeEnum theDataListType = getDataList();
		if (theDataListType == DataListTypeEnum.P011_VIA_P021) {
			code = Intern.paramAHGT;
		} else if (theDataListType == DataListTypeEnum.P091 || theDataListType == DataListTypeEnum.P011_VIA_P091) {
			code = Intern.paramPRES;
		}
		return code;
	}

	public String getP_salinityCode() {
		String code = Global.EMPTY_STRING;
		DataListTypeEnum theDataListType = getDataList();
		if (theDataListType == DataListTypeEnum.P011_VIA_P021) {
			code = null;
		} else if (theDataListType == DataListTypeEnum.P091 || theDataListType == DataListTypeEnum.P011_VIA_P091) {
			code = Intern.paramPSAL;
		}
		return code;
	}

	public String getP_temperatureCode() {
		String code = Global.EMPTY_STRING;
		DataListTypeEnum theDataListType = getDataList();
		if (theDataListType == DataListTypeEnum.P011_VIA_P021) {
			code = null;
		} else if (theDataListType == DataListTypeEnum.P091 || theDataListType == DataListTypeEnum.P011_VIA_P091) {
			code = Intern.paramTEMP;
		}
		return code;
	}

}
