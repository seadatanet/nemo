package fr.ifremer.nemo.ui.edit.cruise;


import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.FormValidator;
import fr.ifremer.nemo.ui.edit.conversion.EditConversionForm;
import fr.ifremer.nemo.ui.edit.cruise.CruiseSection.itemID;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.BODCResult;
import fr.ifremer.nemo.util.BODCResult.BODC_RES_STATUS;
import fr.ifremer.nemo.util.BodcChecker;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.format.DateTimeFormat;

/**
 * Description: validation de l'onget cruise
 * 
 * @author sbregent
 */
public class CruiseFormValidator extends FormValidator {
	

	private String warningCruise = "";

	public CruiseFormValidator() {
		super();
	}

	private void displayErrorIfNeeded(boolean checkResult, String fieldName) {
		
		if (!checkResult) {
			Utils.messageErreurAppend(Messages.Regexp_3 + fieldName + Messages.Regexp_5);
		}
	}

	@Override
	public void validate() {
		guiLogger.info("validate cruise tab");
		warningCruise = "";
		EditCruiseForm editCruiseForm = Nemo.getInstance().getViewMain().getEditCruiseForm();
		EditConversionForm editConversionForm = Nemo.getInstance().getViewMain().getEditConversionForm();

		// test de toutes les valeurs de Survey

		// retour à l'onglet message d'erreur
		ViewLog.tabFolderLog.setSelection(0);

		// mise à jour de la table
		TableDataSurvey.majTypeData();

		Boolean ok, resultat;
		ok = true;

		// raz du message d'erreurs
		Utils.messageErreur(Global.EMPTY_STRING);

		// check valeurs obligatoires
		CruiseSection section = editCruiseForm.getSectionReference();
		if (section != null){
			resultat = section.checkMandatory(itemID.REFERENCE);
			displayErrorIfNeeded(resultat, Messages.edit_cruise_cruiseRef);
			ok = ok && resultat;
			resultat = section.checkMandatory(itemID.LOCAL_CSR_ID);
			displayErrorIfNeeded(resultat, Messages.edit_cruise_cruiseLocalCsrId);
			ok = ok && resultat;
			resultat = section.checkMandatory(itemID.NAME);
			displayErrorIfNeeded(resultat, Messages.edit_cruise_cruiseName);
			ok = ok && resultat;

			resultat = section.checkMandatory(itemID.SHIP);
			displayErrorIfNeeded(resultat, Messages.edit_cruise_ship);
			ok = ok && resultat;
			
			// 35230 SHIP
			String shipCode = fr.ifremer.nemo.util.Utils.extraitCode(section.getItem(itemID.SHIP).getText());
			String shipName = fr.ifremer.nemo.util.Utils.extraitValeur(section.getItem(itemID.SHIP).getText());
			
		
			BODCResult bodcRes = BodcChecker.getInstance().checkShip(shipCode, shipName);
			if (bodcRes.getResStatus() == BODC_RES_STATUS.KO){
				ok=false;
				Utils.messageErreurAppend(bodcRes.getErrorMsg());
			}else if (bodcRes.getResStatus() == BODC_RES_STATUS.WARNING){
				Utils.selectComboCodePlus(section.getItem(itemID.SHIP).getValueCombo(),bodcRes.getShipCode(), bodcRes.getShipLabel());
				Utils.messageWarningAppend(bodcRes.getWarningMsg());
			}
		
			// FAE 1143 : la référence campagne doit être == 13 caractères pour
			// MEDATLAS
			editConversionForm.setWarningCampagne(Global.EMPTY_STRING);
			String value = section.getItem(itemID.REFERENCE).getText().trim();
			if (value.length() != 13 && (RulesManager.getInstance().getMyConversionType() == E_TypeConversion.MEDATLAS)) {
				editConversionForm.setWarningCampagne(Messages.ViewLog_5);
			} else {
				editConversionForm.setWarningStation(Global.EMPTY_STRING);
			}
		}

		section = editCruiseForm.getSectionLocalisation();
		if (section != null && section.isEnabled()){
			resultat = section.checkMandatory(itemID.START_DATE);
			displayErrorIfNeeded(resultat, Messages.edit_cruise_startDate);
			ok = ok && resultat;

			resultat = section.checkMandatory(itemID.END_DATE);
			displayErrorIfNeeded(resultat, Messages.edit_cruise_endDate);
			ok = ok && resultat;

			resultat = section.checkMandatory(itemID.REGION);
			displayErrorIfNeeded(resultat, Messages.edit_cruise_region);
			ok = ok && resultat;

			ok = checkDates(section, ok);
			// 35230 region
			String regionCode = fr.ifremer.nemo.util.Utils.extraitCode(section.getItem(itemID.REGION).getText());
			String regionName = fr.ifremer.nemo.util.Utils.extraitValeur(section.getItem(itemID.REGION).getText());
			
			
			BODCResult bodcRes = BodcChecker.getInstance().checkRegion(regionCode, regionName);
			if (bodcRes.getResStatus() == BODC_RES_STATUS.KO){
				ok=false;
				Utils.messageErreurAppend(bodcRes.getErrorMsg());
			}else if (bodcRes.getResStatus() == BODC_RES_STATUS.WARNING){
				Utils.selectComboCodePlus(section.getItem(itemID.REGION).getValueCombo(),bodcRes.getRegionCode(), bodcRes.getRegionLabel());
				Utils.messageWarningAppend(bodcRes.getWarningMsg());
			}
		}

		section = editCruiseForm.getSectionSource();
		if (section != null){
			resultat = section.checkMandatory(itemID.COUNTRY);
			displayErrorIfNeeded(resultat, Messages.edit_cruise_country);
			ok = ok && resultat;

			resultat = section.checkMandatory(itemID.CHIEF);
			displayErrorIfNeeded(resultat, Messages.edit_cruise_chief);
			ok = ok && resultat;
			
			// 35230 country
			String countryCode = fr.ifremer.nemo.util.Utils.extraitCode(section.getItem(itemID.COUNTRY).getText());
			String countryName = fr.ifremer.nemo.util.Utils.extraitValeur(section.getItem(itemID.COUNTRY).getText());

			BODCResult bodcRes = BodcChecker.getInstance().checkCountry(countryCode, countryName);
			if (bodcRes.getResStatus() == BODC_RES_STATUS.KO){
				ok=false;
				Utils.messageErreurAppend(bodcRes.getErrorMsg());
			}else if (bodcRes.getResStatus() == BODC_RES_STATUS.WARNING){
				Utils.selectComboCodePlus(section.getItem(itemID.COUNTRY).getValueCombo(),bodcRes.getCountryCode(), bodcRes.getCountryLabel());
				Utils.messageWarningAppend(bodcRes.getWarningMsg());
			}
						
		}

		CruiseDatatypeSection sectionType = editCruiseForm.getSectionType();
		if(sectionType !=null){
			if (sectionType.isMandatory())
				ok = ok & sectionType.chekMandatory();
		}
		


		if (ok) {
			guiLogger.info("Cruise validation result: OK");
			editCruiseForm.setOkApresReset(true);
			// okCruise();
			onSuccess();
		} else {
			guiLogger.info("Cruise validation result: KO");
			editCruiseForm.initFromNewModel();
			if (!Nemo.getInstance().getViewMain().tabCruise.isDisposed()) {
				Nemo.getInstance().getViewMain().tabCruise.setText(Messages.onglet_cruise_error);
			}
			Nemo.getInstance().getViewMain().setEtape(1);
		}
	}
	/**
	 * @param editCruiseForm
	 * @param ok
	 * @return
	 */
	private Boolean checkDates(CruiseSection section, Boolean ok) {
		// CHECK DATES
		String startDate = section.getItem(itemID.START_DATE).getText();
		String endDate = section.getItem(itemID.END_DATE).getText();
		Date curDate = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		if (!startDate.equals(Global.EMPTY_STRING)) {
			try {
				DateTimeFormat dateFormat = new DateTimeFormat();
				dateFormat.setFormatString("dd/MM/yyyy"); //$NON-NLS-1$

				dateFormat.parseObject(startDate);

				if (formatter.format(curDate).equals(startDate)) {
					warningCruise += Messages.Regexp_3 +
							Messages.edit_cruise_startDate +
							" (" + startDate + ") " +
							Messages.Regexp_6 + Global.RN;
				}

			} catch (ParseException e) {
				Utils.messageErreurAppend(Messages.Etape_24);
				ok = false;
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!endDate.equals(Global.EMPTY_STRING)) {
			try {
				DateTimeFormat dateFormat = new DateTimeFormat();
				dateFormat.setFormatString("dd/MM/yyyy"); //$NON-NLS-1$

				dateFormat.parseObject(endDate);
				if (formatter.format(curDate).equals(endDate)) {
					warningCruise += Messages.Regexp_3 +
							Messages.edit_cruise_endDate +
							" (" + endDate + ") " +
							Messages.Regexp_6 + Global.RN;
				}
			} catch (ParseException e) {
				Utils.messageErreurAppend(Messages.Etape_27);
				ok = false;
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return ok;
	}
	@Override
	protected void onSuccess() {
		EditConversionForm editConversionForm = Nemo.getInstance().getViewMain().getEditConversionForm();
		EditCruiseForm editCruiseForm = Nemo.getInstance().getViewMain().getEditCruiseForm();

		editCruiseForm.initFromNewModel();

		if (!Nemo.getInstance().getViewMain().tabCruise.isDisposed()) {
			Nemo.getInstance().getViewMain().tabCruise.setText(Messages.onglet_cruise_valid);
		}

		Utils.messageOK(Messages.success_etape);

		if (!warningCruise.equals("")) {
			Utils.messageWarningAppend(warningCruise);
		}
		if (!editConversionForm.getWarningCampagne().equals(Global.EMPTY_STRING)) {

			Utils.messageWarningAppend(editConversionForm.getWarningCampagne());
		}

		// validation etape 2
		Nemo.getInstance().getViewMain().setEtape(2);
		// envoi des valeurs dans le modele

	}
}
