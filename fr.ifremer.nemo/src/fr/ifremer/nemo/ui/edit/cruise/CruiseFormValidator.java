package fr.ifremer.nemo.ui.edit.cruise;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import fr.ifremer.nemo.Nemo;
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
public class CruiseFormValidator extends FormValidator
{

	public CruiseFormValidator()
	{
		super();
	}

	@Override
	public void validate()
	{
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
		ok = ok && resultat;

		resultat = editCruiseForm.sectionReference.checkMandatory(Messages.edit_cruise_cruiseName);
		ok = ok && resultat;

		resultat = editCruiseForm.sectionReference.checkMandatory(Messages.edit_cruise_ship);
		ok = ok && resultat;

		resultat = editCruiseForm.sectionLocalisation.checkMandatory(Messages.edit_cruise_startDate);
		ok = ok && resultat;

		resultat = editCruiseForm.sectionLocalisation.checkMandatory(Messages.edit_cruise_endDate);
		ok = ok && resultat;

		resultat = editCruiseForm.sectionLocalisation.checkMandatory(Messages.edit_cruise_region);
		ok = ok && resultat;

		resultat = editCruiseForm.sectionSource.checkMandatory(Messages.edit_cruise_country);
		ok = ok && resultat;

		resultat = editCruiseForm.sectionSource.checkMandatory(Messages.edit_cruise_chief);
		ok = ok && resultat;

		// CHECK DATES
		String startDate = editCruiseForm.sectionLocalisation.getItem(Messages.edit_cruise_startDate).getText();
		String endDate = editCruiseForm.sectionLocalisation.getItem(Messages.edit_cruise_endDate).getText();

		if (!startDate.equals(Global.EMPTY_STRING))
		{
			try
			{
				DateTimeFormat dateFormat = new DateTimeFormat();
				dateFormat.setFormatString("dd/MM/yyyy"); //$NON-NLS-1$

				dateFormat.parseObject(startDate);

			} catch (ParseException e)
			{
				Utils.messageErreurAppend(Messages.Etape_24);
				ok = false;
				e.printStackTrace();
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!endDate.equals(Global.EMPTY_STRING))
		{
			try
			{
				DateTimeFormat dateFormat = new DateTimeFormat();
				dateFormat.setFormatString("dd/MM/yyyy"); //$NON-NLS-1$

				dateFormat.parseObject(endDate);

			} catch (ParseException e)
			{
				Utils.messageErreurAppend(Messages.Etape_27);
				ok = false;
				e.printStackTrace();
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		ok = editCruiseForm.sectionType.chekMandatory();

		// FAE 1143 : la référence campagne doit être == 13 caractères pour
		// MEDATLAS
		editConversionForm.setWarningCampagne(Global.EMPTY_STRING);
		String value = editCruiseForm.sectionReference.getItem(Messages.edit_cruise_cruiseRef).getText().trim();
		if (value.length() != 13 && (Nemo.getInstance().getViewMain().getMyRulesManager().getMyConversionType() == E_TypeConversion.MEDATLAS))
		{
			editConversionForm.setWarningCampagne(Messages.ViewLog_5);
		} else
		{
			editConversionForm.setWarningStation(Global.EMPTY_STRING);
		}
		if (ok)
		{
			editCruiseForm.setOkApresReset(true);
			// okCruise();
			onSuccess();
		} else
		{
			if (!Nemo.getInstance().getViewMain().tabCruise.isDisposed())
			{
				Nemo.getInstance().getViewMain().tabCruise.setText(Messages.onglet_cruise_error);
			}
			Nemo.getInstance().getViewMain().setEtape(1);
		}
	}

	@Override
	protected void onSuccess()
	{
		EditConversionForm editConversionForm = Nemo.getInstance().getViewMain().getEditConversionForm();
		EditCruiseForm editCruiseForm = Nemo.getInstance().getViewMain().getEditCruiseForm();

		editCruiseForm.initFromNewModel();

		if (!Nemo.getInstance().getViewMain().tabCruise.isDisposed())
		{
			Nemo.getInstance().getViewMain().tabCruise.setText(Messages.onglet_cruise_valid);
		}
		Utils.messageOK(Messages.success_etape);
		if (!editConversionForm.getWarningCampagne().equals(Global.EMPTY_STRING))
		{
			Utils.messageOKAppend(Messages.ViewLog_8);
			Utils.messageOKAppend(editConversionForm.getWarningCampagne());
		}

		// validation etape 2
		Nemo.getInstance().getViewMain().setEtape(2);
		// envoi des valeurs dans le modele

	}
}
