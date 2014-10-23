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

import java.io.File;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.RulesManager;
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
            EditFileForm editFileForm =
                Nemo.getInstance().getViewMain().getEditFileForm();

            // retour à l'onglet message d'erreur
            ViewLog.tabFolderLog.setSelection(0);

            // raz du message d'erreurs
            Utils.messageErreur(Global.EMPTY_STRING);

            Boolean ok, resultat;
            ok = true;

            // first check if an output format has been set
            if (RulesManager.getInstance()
                .getMyConversionType() == E_TypeConversion.UNKNOWN)
            {
                Utils.messageErreur(Messages.error_file_convertFormatUnknown);
                return;
            }
            if (RulesManager.getInstance()
                .getMyFileType() == E_InputType.UNKNOWN)
            {
                Utils.messageErreur(Messages.error_file_inputTypeUnknown);
                return;
            }
            // Fichier avec séparateur
            if (editFileForm.isSeparatorChecked())
            {
                // si on a deja preformate une fois, on repart sur les vrais
                // noms d'origine (et pas les PF_...)
                // if (Preformat.isPreformat())
                // {
                editFileForm.nomRepertoire = editFileForm.nomRepertoireOrigin;
                editFileForm.nomFichier = editFileForm.nomFichierOrigin;
                // }
                try
                {
                    // on va avoir besoin de connaitre les modele saisi pour
                    // pouvoir formatter (8847)
                    editFileForm.initFromNewModel();
                    // formatage
                    Preformat.runExGUI();

                    // le préformattage a ajouté un espace au début de chaque
                    // ligne
                    // de données
                    // 8847 BGT modifié si next en si NON next
                    if (Preformat.isSpaceAdded()
                            && !editFileForm.getSectionDataTerminator()
                                .radNextStationSelected())
                    {
                        editFileForm.addSpaceBeforeDataTerminator();
                    }
                    
                    // 13196
                    if (!Preformat.isPreformat()) {
                        ok = ok && false;
                        Utils.messageErreur(Messages.error_preformat_impossible + " " + editFileForm.getNomFichier().replace(editFileForm.getNomRepertoire(), "").replace(File.separator, ""));
                    }

                }
                catch (Exception ex)
                {
                    ok = ok && false;
                    Utils.messageErreurAppend(Messages.error);
                    ex.printStackTrace();
                }
            }

            for (NemoSection desc : editFileForm.getListeSections())
            {
                if (desc.isEnabled())
                {
                    resultat = ((FileSection) desc).checkMandatory();
                    ok = ok && resultat;
                }
                else
                {
                    desc.razValues();
                }
            }

            if (ok)
            {
                editFileForm.setOkApresReset(true);
                onSuccess();
            }
            else
            {
                Nemo.getInstance().getViewMain().tabFile
                    .setText(Messages.onglet_file_error);
                Nemo.getInstance().getViewMain().setEtape(0);

                editFileForm.initFromNewModel();
                Nemo.getInstance().getDomain().getMedia()
                    .buildInputFilesNames();
            }
            RulesManager.getInstance().defineSectionMandatory();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSuccess()
    {
        EditFileForm editFileForm =
            Nemo.getInstance().getViewMain().getEditFileForm();
        EditCruiseForm editCruiseForm =
            Nemo.getInstance().getViewMain().getEditCruiseForm();

        // envoi des valeurs dans le modele
        // attention aux boutons radio

        // puis remplissage du modele avec les valeurs de l'IHM
        editFileForm.initFromNewModel();

        Nemo.getInstance().getDomain().getMedia().buildInputFilesNames();

        Utils.messageOK(Messages.success_etape);

        // on rajoute "ok" dans l'onglet description
        Nemo.getInstance().getViewMain().tabFile
            .setText(Messages.onglet_file_valid);

        // on valide l'etape 1
        editFileForm.setValidated(true);

        // verrouillage de l'option serieTemporelle pour eviter les changements
        editFileForm.enableSeparatorChoice(false);
        editFileForm.enableFileTypeChoice(false);

        try
        {
            // FAE 022
            if (!RulesManager.getInstance()
                .isCruiseEnabled())
            {
                editCruiseForm.setOkApresReset(true);
                editCruiseForm.setValidated(true);

                editCruiseForm.initFromNewModel();
                Nemo.getInstance().getViewMain().setEtape(2);
            }
            else
            {
                Nemo.getInstance().getViewMain().setEtape(1);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
