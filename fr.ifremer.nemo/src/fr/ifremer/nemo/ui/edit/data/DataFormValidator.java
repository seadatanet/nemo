package fr.ifremer.nemo.ui.edit.data;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.swt.widgets.Combo;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.NetCDFLongNameParameterManager;
import fr.ifremer.nemo.domain.ParameterManager;
import fr.ifremer.nemo.domain.StandardNameParameterManager;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.FormValidator;
import fr.ifremer.nemo.ui.edit.conversion.EditConversionForm;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.BODCResult;
import fr.ifremer.nemo.util.BODCResult.BODC_RES_STATUS;
import fr.ifremer.nemo.util.BodcChecker;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_MesureType;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: validation de l'onglet data
 * 
 * @author sbregent
 */
public class DataFormValidator extends FormValidator {
	private GroupVerticalParameters verticalParamGroup;
	private ParameterManager myParameterManager;
	private Combo comboDBSBed, comboDBBSurf, comboP, comboHASLvl;
	RulesManager rm ;
	public DataFormValidator() {
		super();

	}

	@Override
	public void validate() {
		rm = RulesManager.getInstance();
		EditDataForm editDataForm =	Nemo.getInstance().getViewMain().getEditDataForm();
		editDataForm.drawingBack();// needed to display the scroll bar in the right panel
		EditConversionForm editConversionForm =	Nemo.getInstance().getViewMain().getEditConversionForm();

		verticalParamGroup = editDataForm.getMyGroupeVerticalParameters();
		comboDBSBed = verticalParamGroup.getComboDBSBed();
		comboDBBSurf = verticalParamGroup.getComboDBSSurf();
		comboHASLvl = verticalParamGroup.getComboHASLvl();
		comboP = verticalParamGroup.getComboP();

		myParameterManager = Nemo.getInstance().getDomain().getData().getMyParameterManager();
		int unitCodeColNb=Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getColNb(TableData.COL_UNIT_CODE);
		int unitColNb=Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getColNb(TableData.COL_UNIT);
		int paramCodeColNb=Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getColNb(TableData.COL_CODE);
		int instrumentCodeColNb=Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getColNb(TableData.COL_INSTRUMENT_CODE);
		int instrumentColNb=Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getColNb(TableData.COL_INSTRUMENT);

		Boolean ok = true;

		// retour e l'onglet message d'erreur
		ViewLog.tabFolderLog.setSelection(0);

		// raz du message d'erreurs
		Utils.messageErreur(Global.EMPTY_STRING);

		// mise e jour avant toute chose
		editDataForm.getMyTableData().majTypeData();

		int nbreParam = editDataForm.getMyTableData().getTypes().size();

		/**
		 * --------------------------------------------------------------------
		 * --- data05 presence d'au moins 1 paramètre dans le tableau
		 */
		ok = checkNbParams(nbreParam);
		if (!ok){
			return;
		}
		// -------------------------------------------------------------------------
		String errors =VariantsSpecificsParameters.getInstance().checkSpecificParameters(editDataForm.getMyTableData().getTypes());
		if (!errors.isEmpty()){
			Utils.messageErreurAppend(errors);
			return;
		}

		

		boolean bodcReplaced = false;
		if (ok) {
			/** data10 */
			ok = ok && updateTestValues();

			int[] debuts, fins;
			debuts = new int[nbreParam];
			fins = new int[nbreParam];

			for (int i = 0; i < nbreParam; i++) {
				TypeData typeCourant =
						editDataForm.getMyTableData().getTypes().get(i);
				debuts[i] = typeCourant.getDebut();
				fins[i] = typeCourant.getFin();

				/**
				 * data20 verif presence de l'unité pour chaque paramètre -FAE
				 * 6538
				 */
				//FAE 10755 l'unité n'est pas present pour les chaine de type text.
				if (!typeCourant.isText())
					ok = ok && checkUniteIsPresent(typeCourant);
				if (!ok){
					return;
				}
				/**
				 * data30 verif compatibilité valeur par defaut et valeur output
				 * FAE 7241: pas de test pour ODV (outputDef optionelle car ne
				 * sert pas en ODV)
				 */
				if (!(rm.isConversionToODV() || rm.isConversionToNetCDF())) {
					ok = ok && checkDefaultAndOutputCompatibility(typeCourant);
				}
				/** data40 check des valeurs debut<fin */
				ok = ok && checkStartAndEnd(typeCourant);

				/** data45 check des valeurs debut<fin flag */
				ok = ok && checkStartAndEndFlags(typeCourant);

				if (rm.isConversionToNetCDF()) {

					/** check des valeurs longNames pour NetCDF */
					ok = ok && checkLongName(typeCourant);

					/** check des valeurs standarNames pour NetCDF */
					checkStandardName(typeCourant);
				}
				/**
				 * WARNING: be sure to check parameters BEFORE units, because parameters can be replaced if deprecated
				 */
				// 35230 check parameters codes
				try{
					TypeData typeCourantOrig = new TypeData(typeCourant);
					List<BODCResult> bodcResList = BodcChecker.getInstance().checkParameterCode(typeCourant, rm.getMyDataListType());
					for (BODCResult res :bodcResList){
						if (res.getResStatus() == BODC_RES_STATUS.KO){
							Utils.messageErreurAppend(res.getErrorMsg());
							ok=false;
						}else if (res.getResStatus() == BODC_RES_STATUS.WARNING){
							if (verticalParamGroup.removeToAllCombo(typeCourantOrig)){
								verticalParamGroup.addTypeData(res.getTypeData());
							}
							typeCourant = res.getTypeData();
							Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getMyTable().getItems()[i].setText(paramCodeColNb, String.valueOf(typeCourant.getFormattedCodeAndNameToUse()));
							Utils.messageWarningAppend(res.getWarningMsg());
							bodcReplaced=true;
						}
					}
				}catch (Exception e) {
					Utils.messageErreurAppend(e.getMessage());
				}
				// 35230 check unit
				if (!typeCourant.isText() && !isUnitP09()){
					BODCResult bodcRes = BodcChecker.getInstance().checkP06(typeCourant, rm.getMyDataListType());
					if (bodcRes.getResStatus() == BODC_RES_STATUS.KO){
						Utils.messageErreurAppend(bodcRes.getErrorMsg());
						ok=false;
					}else if (bodcRes.getResStatus() == BODC_RES_STATUS.WARNING){
							typeCourant = bodcRes.getTypeData();
							Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getMyTable().getItems()[i].setText(unitCodeColNb, String.valueOf(typeCourant.getUniteCode()));
							Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getMyTable().getItems()[i].setText(unitColNb, String.valueOf(typeCourant.getUnite()));
							Utils.messageWarningAppend(bodcRes.getWarningMsg());
							bodcReplaced=true;
					}
				}
				// 35230 check instrument
				BODCResult bodcRes = BodcChecker.getInstance().checkInstrument(typeCourant);
				if (bodcRes.getResStatus() == BODC_RES_STATUS.KO){
					ok=false;
					Utils.messageErreurAppend(bodcRes.getErrorMsg());
				}else if (bodcRes.getResStatus() == BODC_RES_STATUS.WARNING){
					typeCourant = bodcRes.getTypeData();
					Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getMyTable().getItems()[i].setText(instrumentCodeColNb, String.valueOf(typeCourant.getInstrumentCode()));
					Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getMyTable().getItems()[i].setText(instrumentColNb, String.valueOf(typeCourant.getInstrument()));
					Utils.messageWarningAppend(bodcRes.getWarningMsg());
					bodcReplaced=true;
				}
			}
			
			
			// 35230 check fall rate
		
			BODCResult bodcRes = BodcChecker.getInstance().checkFallRate(	editDataForm.getFallrateCode(), editDataForm.getFallrateLabel());
			if (bodcRes.getResStatus() == BODC_RES_STATUS.KO){
				ok=false;
				Utils.messageErreurAppend(bodcRes.getErrorMsg());
			}else if (bodcRes.getResStatus() == BODC_RES_STATUS.WARNING){
				editDataForm.setFallrateCode(bodcRes.getFallrateCode());
				editDataForm.setFallrateLabel(bodcRes.getFallrateLabel());
				Utils.messageWarningAppend(bodcRes.getWarningMsg());
				bodcReplaced=true;
			}

			/**
			 * data50 verif que les valeurs de « debut » et « fin » sont
			 * différentes des valeurs affichées par défaut
			 **/
			ok = ok && checkStartAndEndAreFilled(nbreParam, debuts, fins);

			// ----------------------------------------------------------
			// PARAMETRES
			// ----------------------------------------------------------
			
			ok = ok && detectVerticalParameters();
			if (ok) {

				// enable Fall rate group
				editDataForm.setFallrateGroupEnabled(RulesManager.getInstance().isFallrateEnabled());
				/**
				 * ***************************************************** tests
				 * sur params (cf. tableau excel doc technique)
				 * *****************************************************
				 */
				editConversionForm.setWarningData(Global.EMPTY_STRING);

				if (rm.isProfile()) {
					/**
					 * data60/data65 profil parametre de reference verticale en
					 * premiere position
					 **/
					boolean verticProfilParamOk;
					verticProfilParamOk = checkProfilVerticalParameterIsInFirstPosition();
					ok = ok && verticProfilParamOk;

					if (ok
							&& RulesManager.getInstance().isConversionToMedatlas()
							&& rm.getMyMesureType() == E_MesureType.BSSURF) {
						// data 76
						// primary parameter must be DEPH or PRES
						ok = getFirstParameterCode().equals(
								rm.getMyVerticalParametersList().getP_depthBSSurf())
								||
								getFirstParameterCode().equals(
										rm.getMyVerticalParametersList().getP_pressure());
					}
				} else if (rm.isTrajectory() && (rm.isConversionToODV() || rm.isConversionToNetCDF())) {
					/**
					 * data80/data85 traj parametre de reference verticale en
					 * premiere position
					 **/
					boolean verticTrajParamOk;
					verticTrajParamOk =
							checkTrajectoryVerticalParameterIsInFirstPosition();
					ok = ok && verticTrajParamOk;
				}

				/**
				 * ***************************************************** 
				 * fintests sur params
				 * *****************************************************
				 */
			}
		}
			/**
			 * 
			 */
		if (bodcReplaced){
			ok=false;
			Utils.messageWarningAppend("Some bodc terms have been replaced. Please verify and validate. Save your model to avoid this warning next time."); // TODO 35230 message
		}
		
			if (ok) {
				// FAE 9656
				// les donnees du model doivent etre presente.
				// 10594: on a besoin que le formulaire soit valide pour lancer la
				// conversion
				editDataForm.setOkApresReset(true);
				onSuccess();
			} else {
				Nemo.getInstance().getViewMain().tabData
				.setText(Messages.onglet_data_error);
				editDataForm.initFromNewModel();
				Nemo.getInstance().getViewMain().setEtape(3);
			}

			

	}

	private boolean isUnitP09() {
		return RulesManager.getInstance().isDataListTypeP091()||RulesManager.getInstance().isDataListTypeP011viaP091();
	}
	@Override
	protected void onSuccess() {
		super.onSuccess();

		EditDataForm editDataForm =	Nemo.getInstance().getViewMain().getEditDataForm();

		editDataForm.initFromNewModel();

		Nemo.getInstance().getViewMain().tabData.setText(Messages.onglet_data_valid);
		Nemo.getInstance().getViewMain().setEtape(4);

		Nemo.getInstance().getDomain().getData().getModel().setMesureType(rm.getMyMesureType());


		Nemo.getInstance().getDomain().getData().getModel().setFallrateCode(editDataForm.getFallrateCode());
		Nemo.getInstance().getDomain().getData().getModel().setFallrateLabel(editDataForm.getFallrateLabel());

		Nemo.getInstance().getViewMain().getEditConversionForm().fillSummary();

	}

	private boolean checkStartAndEndAreFilled(int nbreParam, int[] debuts,
			int[] fins) {
		boolean ok = true;
		int cptDebut = 0;
		int cptFin = 0;
		Boolean valeurNonRemplie = false;
		// parcours des listes et test des valeurs
		for (int i = 0; i < nbreParam; i++) {
			for (int j = 0; j < nbreParam; j++) {
				if (debuts[i] == debuts[j]) {
					if (debuts[i] == 0) {
						valeurNonRemplie = true;
					}
					cptDebut = cptDebut + 1;
				}
				if (fins[i] == fins[j]) {
					cptFin = cptFin + 1;
				}
			}
		}
		if (valeurNonRemplie) {
			Utils.messageErreurAppend(Messages.Etape_88);
			ok = false;
		}

		return ok;
	}

	private boolean checkNbParams(int nbParams) {
		boolean ok = (nbParams > 0);
		if (!ok) {
			Utils.messageErreurAppend(Messages.Etape_117);
		}
		return ok;
	}

	private boolean checkUniteIsPresent(TypeData typeCourant) {
		boolean ok;
		ok = !(typeCourant.getUnite().equals(Global.EMPTY_STRING));
		if (!ok) {
			Utils.messageErreurAppend(Messages.Etape_118);
		}
		return ok;
	}

	private boolean checkStartAndEnd(TypeData typeCourant) {
		boolean ok = true;
		if (typeCourant.getDebut() > typeCourant.getFin()) {
			Utils.messageErreurAppend(Messages.Etape_98 + Global.SPACE
					+ typeCourant.getCodeToUse());
			ok = false;
		}
		return ok;
	}

	private boolean checkStartAndEndFlags(TypeData typeCourant) {
		boolean ok = true;
		if (!typeCourant.getDebutFlag().equals(Global.EMPTY_STRING)
				&& !typeCourant.getFinFlag().equals(Global.EMPTY_STRING)) {
			if (Integer.parseInt(typeCourant.getDebutFlag()) > Integer
					.parseInt(typeCourant.getFinFlag())) {
				Utils.messageErreurAppend(Messages.Etape_125 + Global.SPACE
						+ typeCourant.getCodeToUse());
				ok = false;
			}
		}
		return ok;
	}

	private boolean checkStandardName(TypeData typeCourant) {
		boolean ok = true;
		RulesManager rm = RulesManager.getInstance();
		String standardName = StandardNameParameterManager.getInstance().getUserStandardName(typeCourant.getEntryKeyP011());
		if (standardName == null || standardName.equals(Global.EMPTY_STRING) && rm.isConversionToNetCDF()) {
			Utils.messageWarningAppend(Messages.Etape_126 + Global.SPACE
					+ typeCourant.getCodeToUse());
			ok = false;
		}
		return ok;
	}

	private boolean checkLongName(TypeData typeCourant) {
		boolean ok = true;
		RulesManager rm = RulesManager.getInstance();
		String longName = NetCDFLongNameParameterManager.getInstance().getUserLongName(typeCourant.getEntryKeyP011());
		if (longName == null || longName.equals(Global.EMPTY_STRING) && rm.isConversionToNetCDF()) {
			Utils.messageErreurAppend(Messages.Etape_134 + Global.SPACE
					+ typeCourant.getCodeToUse());
			ok = false;
		}
		return ok;
	}

	private String getFirstParameterCode() {
		// recup du premier parametre (on reliut a chaque fois, car le tableau
		// peut être modifie par le user entre 2 validations)
		TypeData premierType =
				Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData()
						.getTypes().get(0);
		return premierType.getCodeToUse();
	}


	/**
	 * verification: un parametre de type pression ou equivalent doit être
	 * présent sur la première ligne Note: dans certains cas on calculera la
	 * pression a partir de la profondeur ou inversement. cf.
	 * DataDomain.setAddedParameter et AddedParameter
	 * 
	 * @param firstParameterCode
	 * @return
	 */
	private boolean checkProfilVerticalParameterIsInFirstPosition() {
		boolean checkOk = true;

		E_MesureType mesureType = rm.getMyMesureType();

		// cas below sea bed (data65)
		if (mesureType == E_MesureType.BSBED) {
			checkOk =
					getFirstParameterCode().equals(
							rm.getMyVerticalParametersList().getP_depthBSBed());
			if (!checkOk) {
				Utils
						.messageErreurAppend(Messages.error_data_firstParam_depthBSBed
								+ " (data65)"); //$NON-NLS-1$
			}
		}
		// cas below sea surface (data60)
		else if (mesureType == E_MesureType.BSSURF) {
			checkOk =
					getFirstParameterCode().equals(
							rm.getMyVerticalParametersList().getP_depthBSSurf())
							|| getFirstParameterCode().equals(
									rm.getMyVerticalParametersList().getP_pressure());
			if (!checkOk) {
				Utils
						.messageErreurAppend(Messages.error_data_firstParam_depthBSSurfOrPres
								+ " (data60)"); //$NON-NLS-1$
			}
		}

		return checkOk;
	}

	private boolean checkTrajectoryVerticalParameterIsInFirstPosition() {
		boolean checkOk = true;

		// recup du premier parametre
		TypeData premierType =
				Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData()
						.getTypes().get(0);
		String firstParameterCode = premierType.getCodeToUse();
		E_MesureType mesureType = rm.getMyMesureType();

		// cas below sea bed (data85)
		if (mesureType == E_MesureType.BSBED
				&& !rm.getMyVerticalParametersList().getP_depthBSBed()
						.equals(Global.EMPTY_STRING)) {
			checkOk =
					firstParameterCode.equals(rm.getMyVerticalParametersList()
							.getP_depthBSBed());
			if (!checkOk) {
				Utils
						.messageErreurAppend(Messages.error_data_firstParam_depthBSBed
								+ "( data85)"); //$NON-NLS-1$
			}
		}
		// cas below sea surface (data80)
		else if (mesureType == E_MesureType.BSSURF
				&& !rm.getMyVerticalParametersList().getP_depthBSSurf()
						.equals(Global.EMPTY_STRING)) {
			checkOk =
					firstParameterCode.equals(rm.getMyVerticalParametersList()
							.getP_depthBSSurf())
							|| firstParameterCode.equals(rm
									.getMyVerticalParametersList().getP_pressure());
			if (!checkOk) {
				Utils
						.messageErreurAppend(Messages.error_data_firstParam_depthBSSurf
								+ "( data80)"); //$NON-NLS-1$
			}
		}

		return checkOk;
	}

	private boolean updateTestValues() {
		boolean res;
		EditDataForm editDataForm =
				Nemo.getInstance().getViewMain().getEditDataForm();
		// test de toutes les valeurs debut / fin des Data

		res = editDataForm.getMyTableData().majChampTest();

		if (!res) {
			Utils.messageErreurAppend(Messages.Etape_131);
		}

		return res;
	}

	private boolean checkDefaultAndOutputCompatibility(TypeData typeCourant) {
		boolean okRes = true;
		String fmOut = new String();

		// test direct sur valeur sortie (valeur par defaut)
		try {
			fmOut = Global.EMPTY_STRING;
			String formatString = typeCourant.getFormat();

			// compatiblité des tailles
			int index = 0;
			int indexSign = formatString.indexOf('+');
			if (indexSign == -1) {
				index = formatString.indexOf('%') + 1;
			} else {
				index = indexSign + 1;
			}
//			int totLength =
//					Integer.parseInt(formatString.substring(index, index + 1));
			int totLength;
			if (formatString.indexOf(".")==-1){
			 totLength =
					Integer.parseInt(formatString.substring(index, formatString.length() - 1));
			}else{
				 totLength =
						Integer.parseInt(formatString.substring(index, formatString.lastIndexOf(".")));
			}

			// Float ou Decimal ?
			String type = formatString.substring(formatString.length() - 1);
			if (type.equals("f") || type.equals("F")) { //$NON-NLS-1$ //$NON-NLS-2$
//				fmOut =
//						String.format(java.util.Locale.US, formatString,
//								Float.parseFloat(typeCourant.getSortie()));
				// 33045 use bigInt to avoid round value
				fmOut =
						String.format(java.util.Locale.US, formatString,
								new BigDecimal(typeCourant.getSortie()));
			} else if (type.equals("d") || type.equals("D")) { //$NON-NLS-1$ //$NON-NLS-2$
				try{
					int resInt = Integer.parseInt(typeCourant.getSortie());
					 fmOut =
								String.format(java.util.Locale.US, formatString,
										resInt);
				}catch (Exception e) {
					long resLong= Long.parseLong(typeCourant.getSortie());
					fmOut =
							String.format(java.util.Locale.US, formatString,
									resLong);
				}
				
			}

			if ((fmOut.equals(typeCourant.getSortie())) && (typeCourant.getSortie().length() == totLength)) {
				// ok
				okRes = true;

			} else {
				// erreur
				okRes = false;
				Utils.messageErreurAppend(Messages.Etape_106
								+ " " + typeCourant.getCodeToUse() + " : " + Messages.Etape_106b); //$NON-NLS-1$ //$NON-NLS-2$
			}

		} catch (Exception e) {
			// erreur
			okRes = false;
			Utils.messageErreurAppend(Messages.Etape_106
							+ " " + typeCourant.getCodeToUse() + " : " + Messages.Etape_106b); //$NON-NLS-1$ //$NON-NLS-2$
			System.out.print(e.getMessage());
		}

		return okRes;
	}

	private boolean detectVerticalParameters() {

		if (rm.isDataListTypeP091()) {
			detectVerticalParametersForP091();
		} else if (rm.isDataListTypeP011viaP021()) {
			detectVerticalParametersForP011();
		} else if (rm.isDataListTypeP011viaP091()) {
			detectVerticalParametersForP011viaP091();

		}

		return checkVerticalParametersWellDetected();
	}

	private void detectVerticalParametersForP011() {

		int index;
		// depth below sea surface
		if (rm.isP_depthBSSurf_enabled()) {
			index = comboDBBSurf.getSelectionIndex();
			if (index != -1) {
				rm.getMyVerticalParametersList().setP_depthBSSurf(
						comboDBBSurf.getItem(index));
			}
		}
		// Depth below sea bed
		if (rm.isP_depthBSBed_enabled()) {
			index = comboDBSBed.getSelectionIndex();
			if (index != -1) {
				rm.getMyVerticalParametersList().setP_depthBSBed(
						comboDBSBed.getItem(index));
			}
		}
		// pressure
		if (rm.isP_pressure_enabled()) {
			index = comboP.getSelectionIndex();
			if (index != -1) {
				rm.getMyVerticalParametersList().setP_pressure(
						comboP.getItem(index));
			}
		}

		// Hauteur au dessus de la colonne d'eau
		if (rm.isP_heightASLvl_enabled()) {
			index = comboHASLvl.getSelectionIndex();
			if (index != -1) {
				rm.getMyVerticalParametersList().setP_heightASLvl(
						comboHASLvl.getItem(index));
			}
		}

	}

	private boolean checkVerticalParametersWellDetected() {

		boolean ok = true;
		if (rm.isP_depthBSBed_enabled()
				&& rm.getMyVerticalParametersList().getP_depthBSBed()
						.equals(Global.EMPTY_STRING)) {
			
			Utils.messageErreurAppend(MessageFormat.format(Messages.edit_data_verticalParameters_info_chooseDepthBSBed, myParameterManager.getP_depthBSBedCode()));

			ok = false;
		}

		if ((rm.isP_depthBSSurf_enabled() && rm.isP_pressure_enabled())
				&& (rm.getMyVerticalParametersList().getP_depthBSSurf()
						.equals(Global.EMPTY_STRING) && rm
						.getMyVerticalParametersList().getP_pressure()
						.equals(Global.EMPTY_STRING))) {
			
			Utils.messageErreurAppend(MessageFormat.format(Messages.edit_data_verticalParameters_info_chooseDepthBSurfOrPressure, myParameterManager.getP_depthBSSurfCode(), myParameterManager.getP_pressureCode()));

			ok = false;
		}

		if (rm.isP_heightASLvl_enabled() && rm.getMyVerticalParametersList().getP_heightASLvl().equals(Global.EMPTY_STRING) && !rm.isTrajectory()) {

			Utils.messageErreurAppend(MessageFormat.format(Messages.edit_data_verticalParameters_info_chooseHeightASLvl, myParameterManager.getP_heightASLvlCode()));

			ok = false;
		}

		return ok;
	}

	private void detectVerticalParametersForP091() {
		autoFillVerticalParametersForP091();
	}

	private void detectVerticalParametersForP011viaP091() {

		// on regarde si un choix a été fait dans les combos
		// sinon et si un seul choix possible, on le fait
		// sinon et si plusieurs choix possibles, on prend celui qui est en 1er
		// param si il correspond au type de combo?

		// reinit des valeurs
		rm.reinitVerticalParams();

		// Depth below sea bed
		if (rm.isP_depthBSBed_enabled()) {
			rm.getMyVerticalParametersList().setP_depthBSBed(
					fillComboAsP011viaP091(comboDBSBed));
		}
		// depth below sea surface
		if (rm.isP_depthBSSurf_enabled()) {
			rm.getMyVerticalParametersList().setP_depthBSSurf(
					fillComboAsP011viaP091(comboDBBSurf));
		}
		// pressure
		if (rm.isP_pressure_enabled()) {
			rm.getMyVerticalParametersList().setP_pressure(
					fillComboAsP011viaP091(comboP));
		}

		// Hauteur au dessus de la colonne d'eau
		if (rm.isP_heightASLvl_enabled()) {
			rm.getMyVerticalParametersList().setP_heightASLvl(
					fillComboAsP011viaP091(comboHASLvl));

		}
	}

	private String fillComboAsP091(Combo combo) {
		String entryKey = Global.EMPTY_STRING;
		int waitedNbItems = 2; // la valeur vide "" + la valeur attendue pour ce
		// param en P091
		int indexToSelect = 1; // index de la valeur à selectionner (index de la
		// valeur vide = 0, index de la valeur attendue est 1)
		if (combo.getItemCount() == waitedNbItems) {
			entryKey = combo.getItem(indexToSelect);
			combo.select(indexToSelect);
		}
		return entryKey;
	}

	private String fillComboAsP011viaP091(Combo combo) {
		String entryKey = Global.EMPTY_STRING;
		int waitedNbItems = 2; // la valeur vide "" + la valeur attendue pour ce
		// param en P091
		int indexToSelect = 1; // index de la valeur à selectionner (index de la
		// valeur vide = 0, index de la valeur attendue est 1)
		TypeData premierType =
				Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData()
						.getTypes().get(0);
		String firstParameterCode = premierType.getCodeToUse();

		// cas ideal: on n'a qu'une possibilite -> on la selectionne
		if (combo.getItemCount() == waitedNbItems) {
			entryKey = combo.getItem(indexToSelect);
			combo.select(indexToSelect);
		}
		// cas ambigu: on a plusieurs choix possibles -> on selectionne celui
		// qui correspond au premier parametre du tableau
		else if (combo.getItemCount() > waitedNbItems) {
			String selected = Global.EMPTY_STRING;
			int selectedIndex = combo.getSelectionIndex();
			if (selectedIndex != -1) {
				selected = combo.getItem(combo.getSelectionIndex());
			}
			if (selected.equals(Global.EMPTY_STRING)) {
				for (int i = 0; i < combo.getItems().length; i++) {
					if (combo.getItem(i).equals(firstParameterCode)) {
						combo.select(i);
						entryKey = combo.getItem(i);
					}
				}
			} else {
				entryKey = selected;
			}
		}
		return entryKey;
	}

	private void autoFillVerticalParametersForP091() {

		// Depth below sea bed
		if (rm.isP_depthBSBed_enabled()) {
			rm.getMyVerticalParametersList().setP_depthBSBed(
					fillComboAsP091(comboDBSBed));
		}
		// depth below sea surface
		if (rm.isP_depthBSSurf_enabled()) {
			rm.getMyVerticalParametersList().setP_depthBSSurf(
					fillComboAsP091(comboDBBSurf));
		}
		// pressure
		if (rm.isP_pressure_enabled()) {
			rm.getMyVerticalParametersList().setP_pressure(
					fillComboAsP091(comboP));
		}
		if (rm.isP_heightASLvl_enabled()) {
			rm.getMyVerticalParametersList().setP_heightASLvl(
					fillComboAsP091(comboHASLvl));
		}
	}
}
