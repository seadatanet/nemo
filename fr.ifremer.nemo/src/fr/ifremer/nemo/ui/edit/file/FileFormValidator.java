/*
 * ============================================================================
 * Nom du fichier : EtapeFile.java
 * Application : NEMO version 1.2.20
 * Auteur : dboulang
 * Date de création : 31 déc. 09
 * Client : Ifremer
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.file;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.FormValidator;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.ui.edit.cruise.EditCruiseForm;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Preformat;

/**
 * Description: validation de l'onglet File
 * 
 * @author sbregent
 */
public class FileFormValidator extends FormValidator
{
	public FileFormValidator()
	{
		super();
	}

	@Override
	public void validate()
	{
		try
		{
			EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();

			// retour à l'onglet message d'erreur
			ViewLog.tabFolderLog.setSelection(0);

			// raz du message d'erreurs
			Utils.messageErreur(Global.EMPTY_STRING);

			Boolean ok, resultat;
			ok = true;

			// first check if an output format has been set
			if (Nemo.getInstance().getViewMain().getMyRulesManager().getMyConversionType() == E_TypeConversion.UNKNOWN)
			{
				Utils.messageErreur(Messages.error_file_convertFormatUnknown);
				return;
			}
			if (Nemo.getInstance().getViewMain().getMyRulesManager().getMyFileType() == E_InputType.UNKNOWN)
			{
				Utils.messageErreur(Messages.error_file_inputTypeUnknown);
				return;
			}
			// Fichier avec séparateur
			if (editFileForm.isSeparatorChecked() && !Preformat.isPreformat())
			{
				try
				{
					Preformat.run();

					// le préformattage a ajouté un espace au début de chaque
					// ligne
					// de données
					if (Preformat.isSpaceAdded() && editFileForm.getSectionDataTerminator().radNextStationSelected())
					{
						editFileForm.addSpaceBeforeDataTerminator();
					}

				} catch (Exception ex)
				{
					ok = ok && false;
					Utils.messageErreurAppend(Messages.error);
					ex.printStackTrace();
				}
			}

			for (NemoSection desc : editFileForm.listeSections)
			{
				if (desc.isEnabled())
				{
					resultat = ((FileSection) desc).checkMandatory();
					ok = ok && resultat;
				} else
				{
					desc.razValues();
				}
			}

			if (ok)
			{
				editFileForm.setOkApresReset(true);
				onSuccess();
			} else
			{
				Nemo.getInstance().getViewMain().tabFile.setText(Messages.onglet_file_error);
				Nemo.getInstance().getViewMain().setEtape(0);
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void onSuccess()
	{
		EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
		EditCruiseForm editCruiseForm = Nemo.getInstance().getViewMain().getEditCruiseForm();

		// envoi des valeurs dans le modele
		// attention aux boutons radio

		// puis remplissage du modele avec les valeurs de l'IHM
		editFileForm.initFromNewModel();

		Nemo.getInstance().getDomain().getMedia().buildInputFilesNames();

		Utils.messageOK(Messages.success_etape);

		// on rajoute "ok" dans l'onglet description
		Nemo.getInstance().getViewMain().tabFile.setText(Messages.onglet_file_valid);

		// on valide l'etape 1
		editFileForm.setValidated(true);

		// verrouillage de l'option serieTemporelle pour eviter les changements
		editFileForm.enableSeparatorChoice(false);
		editFileForm.enableFileTypeChoice(false);

		try
		{
			// FAE 022
			if (!Nemo.getInstance().getViewMain().getMyRulesManager().isCruiseEnabled())
			{
				editCruiseForm.setOkApresReset(true);
				editCruiseForm.setValidated(true);

				editCruiseForm.initFromNewModel();
				Nemo.getInstance().getViewMain().setEtape(2);
			} else
			{
				Nemo.getInstance().getViewMain().setEtape(1);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
