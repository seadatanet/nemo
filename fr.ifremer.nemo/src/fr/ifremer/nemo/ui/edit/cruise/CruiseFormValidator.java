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
import fr.ifremer.nemo.ui.view.ViewLog;
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
		resultat = editCruiseForm.sectionReference.checkMandatory(Messages.edit_cruise_cruiseRef);
		displayErrorIfNeeded(resultat, Messages.edit_cruise_cruiseRef);
		ok = ok && resultat;

		resultat = editCruiseForm.sectionReference.checkMandatory(Messages.edit_cruise_cruiseName);
		displayErrorIfNeeded(resultat, Messages.edit_cruise_cruiseName);
		ok = ok && resultat;

		resultat = editCruiseForm.sectionReference.checkMandatory(Messages.edit_cruise_ship);
		displayErrorIfNeeded(resultat, Messages.edit_cruise_ship);
		ok = ok && resultat;

		resultat = editCruiseForm.sectionLocalisation.checkMandatory(Messages.edit_cruise_startDate);
		displayErrorIfNeeded(resultat, Messages.edit_cruise_startDate);
		ok = ok && resultat;

		resultat = editCruiseForm.sectionLocalisation.checkMandatory(Messages.edit_cruise_endDate);
		displayErrorIfNeeded(resultat, Messages.edit_cruise_endDate);
		ok = ok && resultat;

		resultat = editCruiseForm.sectionLocalisation.checkMandatory(Messages.edit_cruise_region);
		displayErrorIfNeeded(resultat, Messages.edit_cruise_region);
		ok = ok && resultat;

		resultat = editCruiseForm.sectionSource.checkMandatory(Messages.edit_cruise_country);
		displayErrorIfNeeded(resultat, Messages.edit_cruise_country);
		ok = ok && resultat;

		resultat = editCruiseForm.sectionSource.checkMandatory(Messages.edit_cruise_chief);
		displayErrorIfNeeded(resultat, Messages.edit_cruise_chief);
		ok = ok && resultat;

		// CHECK DATES
		String startDate = editCruiseForm.sectionLocalisation.getItem(Messages.edit_cruise_startDate).getText();
		String endDate = editCruiseForm.sectionLocalisation.getItem(Messages.edit_cruise_endDate).getText();
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

		if (editCruiseForm.sectionType.isMandatory())
			ok = ok & editCruiseForm.sectionType.chekMandatory();

		// FAE 1143 : la référence campagne doit être == 13 caractères pour
		// MEDATLAS
		editConversionForm.setWarningCampagne(Global.EMPTY_STRING);
		String value = editCruiseForm.sectionReference.getItem(Messages.edit_cruise_cruiseRef).getText().trim();
		if (value.length() != 13 && (RulesManager.getInstance().getMyConversionType() == E_TypeConversion.MEDATLAS)) {
			editConversionForm.setWarningCampagne(Messages.ViewLog_5);
		} else {
			editConversionForm.setWarningStation(Global.EMPTY_STRING);
		}
		if (ok) {
			editCruiseForm.setOkApresReset(true);
			// okCruise();
			onSuccess();
		} else {
			editCruiseForm.initFromNewModel();
			if (!Nemo.getInstance().getViewMain().tabCruise.isDisposed()) {
				Nemo.getInstance().getViewMain().tabCruise.setText(Messages.onglet_cruise_error);
			}
			Nemo.getInstance().getViewMain().setEtape(1);
		}
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
