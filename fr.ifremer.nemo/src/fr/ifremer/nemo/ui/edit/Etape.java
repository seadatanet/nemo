package fr.ifremer.nemo.ui.edit;

import java.text.ParseException;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Configuration.DataListEnum;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.conversion.EditConversionForm;
import fr.ifremer.nemo.ui.edit.cruise.EditCruiseForm;
import fr.ifremer.nemo.ui.edit.cruise.TableDataSurvey;
import fr.ifremer.nemo.ui.edit.data.EditDataForm;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.edit.file.FileSection;
import fr.ifremer.nemo.ui.edit.station.AutoLine;
import fr.ifremer.nemo.ui.edit.station.EditStationForm;
import fr.ifremer.nemo.ui.edit.station.StationSection;
import fr.ifremer.nemo.ui.edit.station.AutoLine.MetaInData;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Preformat;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.format.DateTimeFormat;
import fr.ifremer.nemo.util.format.LatitudeFormat;
import fr.ifremer.nemo.util.format.LongitudeFormat;

public class Etape
{

    /*
     * Gestion des differents affichages lors de la sélection des onglets de la
     * fenêtre principale et des différentes étapes.
     * 
     */

    public static void validationFile()
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

            // Fichier avec séparateur
            if (editFileForm.isSeparatorChecked() && !Preformat.isPreformat())
            {
                try
                {
                    Preformat.run();

                    // le préformattage a ajouté un espace au début de chaque
                    // ligne
                    // de données
                    if (Preformat.isSpaceAdded() 
                            && editFileForm.getSectionDataTerminator().radNextStationSelected())
                    {
                        editFileForm.addSpaceBeforeDataTerminator();
                    }

                }
                catch (Exception ex)
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
                }
                else
                {
                    desc.razValues();
                }
            }

            if (ok)
            {
                editFileForm.setOkApresReset(true);
                okFile();
            }
            else
            {
                Nemo.getInstance().getViewMain().tabFile.setText(Messages.onglet_file_error);
                Nemo.getInstance().getViewMain().setEtape(0);
            }

            Nemo.getInstance().getViewMain().getEditConversionForm().fillSummary();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void validationCruise()
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

        resultat =  editCruiseForm.sectionReference.checkMandatory(Messages.edit_cruise_cruiseName);
        ok = ok && resultat;

        resultat = editCruiseForm.sectionReference.checkMandatory(Messages.edit_cruise_ship);
        ok = ok && resultat;

        resultat =  editCruiseForm.sectionLocalisation.checkMandatory(Messages.edit_cruise_startDate);
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

            }
            catch (ParseException e)
            {
                Utils.messageErreurAppend(Messages.Etape_24);
                ok = false;
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

            }
            catch (ParseException e)
            {
                Utils.messageErreurAppend(Messages.Etape_27);
                ok = false;
                e.printStackTrace();
            }

        }

        ok = editCruiseForm.sectionType.chekMandatory();

        // FAE 1143 : la référence campagne doit être == 13 caractères pour
        // MEDATLAS
        editConversionForm.setWarningCampagne(Global.EMPTY_STRING);
        String value =
            editCruiseForm.sectionReference.getItem(
                Messages.edit_cruise_cruiseRef).getText().trim();
        if (value.length() != 13 && (Nemo.getInstance().getViewMain().getMyRulesManager().getMyConversionType()==E_TypeConversion.MEDATLAS))
        {
            editConversionForm.setWarningCampagne(Messages.ViewLog_5);
        }
        else
        {
            editConversionForm.setWarningStation(Global.EMPTY_STRING);
        }
        if (ok)
        {
            editCruiseForm.setOkApresReset(true);
            okCruise();
        }
        else
        {
            if (!Nemo.getInstance().getViewMain().tabCruise.isDisposed())
            {
                Nemo.getInstance().getViewMain().tabCruise.setText(Messages.onglet_cruise_error);
            }
            Nemo.getInstance().getViewMain().setEtape(1);
        }

    }

    public static void validationStation()
    {
        try
        {

            EditStationForm editStationForm = Nemo.getInstance().getViewMain().getEditStationForm();
            EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
            EditConversionForm editConversionForm= Nemo.getInstance().getViewMain().getEditConversionForm();
            RulesManager rulesManager = Nemo.getInstance().getViewMain().getMyRulesManager();

            // test des valeurs de Station

            // Remplissage du tableau de correspondance de flags
            editStationForm.qcFlags.getFlagTable().fillFlagTable(
                editStationForm.qcFlags.getTabCorrespondance());

            // retour à l'onglet message d'erreur
            ViewLog.tabFolderLog.setSelection(0);

            int i;
            boolean ok, resultat1, resultat2, resultat3, ligneOK, checkValue;
            String value;

            ligneOK = true;
            ok = true;

            int size =
                editStationForm.qcFlags.getTabFlagQC().getItemCount();

            // raz du message d'erreurs
            Utils.messageErreur(Global.EMPTY_STRING);

            // NUMERO STATION
            if (!editStationForm.stationNumber.isManual())
            {
                // position dans le fichier, verif des valeurs
                resultat1 =
                    editStationForm.stationNumber
                    .checkAutoInputValues(Messages
                        .getString(Intern.S_STATIONNUMBER));
                if (resultat1)
                {
                    String lign =
                        editStationForm.stationNumber.getLineText(0);
                    ligneOK =
                        editStationForm.stationNumber
                        .checkInDataOrHeader(lign, Messages
                            .getString(Intern.S_STATIONNUMBER));
                    ok = ok && ligneOK;
                }
                ok = ok && resultat1;

            }
            else
            {
                value = editStationForm.stationNumber.getManualValue();
                if (Nemo.getInstance().getViewMain().getMyRulesManager().isTrajectory())
                {
                    // saisie manuelle - obligatoire
                    if (value.trim().equals("") || value.equals("NaN")) { //$NON-NLS-1$ //$NON-NLS-2$
                        ok = ok && false;
                        // FAE 1673 : n'afficher le message que si erreur
                        Utils.messageErreurAppend(Messages.Etape_128);
                        Utils.messageErreurAppend(Messages.Etape_129);
                    }
                }
                else
                {
                    // autoincrementation - doit être un entier
                    try
                    {
                        Integer.parseInt(value);
                    }
                    catch (Exception e)
                    {
                        ok = ok && false;
                        Utils.messageErreurAppend(Messages.Etape_127);
                    }
                }
            }
            // FAE 1143 : dans tous les cas, le numéro de station doit être <= 5
            // caractères pour MEDATLAS
            editConversionForm.setWarningStation(Global.EMPTY_STRING);

            value = editStationForm.stationNumber.getValue();
            if (value!= null)
            {
                value = value.trim();
            }
            if (!Nemo.getInstance().getViewMain().getMyRulesManager().isProcessCollection() && value.length() > 5)
            {
                editConversionForm
                .setWarningStation(Messages.ViewLog_6);
            }
            else
            {
                editConversionForm
                .setWarningStation(Global.EMPTY_STRING);
            }

            // FAE022 : TYPE DE DONNEES
            if (Nemo.getInstance().getViewMain().getMyRulesManager().isCruiseEnabled() 
                    && !Nemo.getInstance().getViewMain().getMyRulesManager().isProcessCollection())
            {
                if (editStationForm.dataType.getDatatype()
                        .getValueCombo().getSelectionIndex() == -1)
                {
                    Utils.messageErreurAppend(Messages.Etape_32);
                    ok = ok && false;
                }
            }

            // HISTORIQUE ACQUISITION
            if (!editStationForm.historique.isManual())
            {
                // position dans fichier, verif des valeurs
                resultat1 =
                    editStationForm.historique.checkAutoInputValues(0,
                        Messages.Etape_33);
                ok = ok && resultat1;

                if (resultat1)
                {
                    int nbreLignes =
                        editStationForm.historique.calculNbreLignes(0);

                    if (nbreLignes > 1)
                    {
                        ok = ok && false;
                        Utils.messageErreurAppend(Messages.Etape_113);
                    }
                    else
                    {
                        // nbre de ligne bon mais est-ce au bon endroit ?
                        resultat3 =
                            editStationForm.historique.checkPosition(
                                0, nbreLignes);
                        ok = ok && resultat3;

                        if (!resultat3)
                            Utils.messageErreurAppend(Messages.Etape_114);
                    }
                }
                // on permet de n'utiliser qu'une ligne sur les deux!
                if (!editStationForm.historique.getLineText(1).trim()
                        .equals(Global.EMPTY_STRING))
                {
                    resultat2 =
                        editStationForm.historique
                        .checkAutoInputValues(1, Messages.Etape_34);

                    if (resultat2)
                    {
                        int nbreLignes =
                            editStationForm.historique
                            .calculNbreLignes(1);

                        if (nbreLignes > 1)
                        {
                            ok = ok && false;
                            Utils.messageErreurAppend(Messages.Etape_115);
                        }
                        else
                        {
                            // nbre de ligne bon mais est-ce au bon endroit
                            resultat3 =
                                editStationForm.historique
                                .checkPosition(1, nbreLignes);
                            ok = ok && resultat3;

                            if (!resultat3)
                                Utils.messageErreurAppend(Messages.Etape_116);
                        }
                    }
                }
                else
                {
                    resultat2 = true;
                }
                ok = ok && resultat2;
            }

            // COMMENTAIRE
            if (!editStationForm.comments.isManual())
            {
                resultat1 =
                    editStationForm.comments
                    .checkAutoInputValues(Messages
                        .getString(Intern.S_COMMENT));
                ok = ok && resultat1;

                if (resultat1)
                {
                    // VERIF si le commentaire ne depasse pas le nombre de
                    // lignes
                    // d'entete station!
                    int nbreLignes =
                        editStationForm.comments.calculNbreLignes();
                    resultat3 =
                        editStationForm.comments
                        .checkPosition(nbreLignes);
                    ok = ok && resultat3;

                    if (!resultat3)
                        Utils.messageErreurAppend(Messages.Etape_108);
                }
            }

            // ECHANTILLON SURFACE
            if (!editStationForm.surfaceSample.isManual())
            {
                resultat1 =
                    editStationForm.surfaceSample
                    .checkAutoInputValues(0, Messages.Etape_36);
                ok = ok && resultat1;
                if (resultat1)
                {
                    int nbreLignes =
                        editStationForm.surfaceSample
                        .calculNbreLignes(0);

                    if (nbreLignes > 1)
                    {
                        ok = ok && false;
                        Utils.messageErreurAppend(Messages.Etape_109);
                    }
                    else
                    {
                        // nbre de ligne bon mais est-ce au bon endroit ?
                        resultat3 =
                            editStationForm.surfaceSample
                            .checkPosition(0, nbreLignes);
                        ok = ok && resultat3;

                        if (!resultat3)
                            Utils.messageErreurAppend(Messages.Etape_110);
                    }
                }

                // permettre une seule ligne sur les deux!
                if (!editStationForm.surfaceSample.getLineText(1)
                        .trim().equals(Global.EMPTY_STRING))
                {
                    resultat2 =
                        editStationForm.surfaceSample
                        .checkAutoInputValues(1, Messages.Etape_37);
                    if (resultat2)
                    {

                        int nbreLignes =
                            editStationForm.surfaceSample
                            .calculNbreLignes(1);

                        if (nbreLignes > 1)
                        {
                            ok = ok && false;
                            Utils.messageErreurAppend(Messages.Etape_112);
                        }
                        else
                        {
                            resultat3 =
                                editStationForm.surfaceSample
                                .checkPosition(1, nbreLignes);
                            ok = ok && resultat3;

                            if (!resultat3)
                                Utils.messageErreurAppend(Messages.Etape_111);
                        }
                    }
                }
                else
                {
                    resultat2 = true;
                }
                ok = ok && resultat2;

            }

            // HEURE
            String formatSaisi =
                editStationForm.time.getConverter().getText();
            checkValue = true;
            if (!formatSaisi.equals("Unknown"))
            {
                if (!editStationForm.time.isManual())
                {
                    // position dans fichier
                    resultat1 =
                        editStationForm.time
                        .checkAutoInputValues(Messages
                            .getString(Intern.S_TIME));
                    if (resultat1)
                    {
                        String lign =
                            editStationForm.time.getLineText(0);
                        ligneOK =
                            editStationForm.time.checkInDataOrHeader(
                                lign, Messages.getString(Intern.S_TIME));
                        ok = ok && ligneOK;
                    }
                    else
                    {
                        checkValue = false;
                    }
                    ok = ok && resultat1;
                }

                // check value (creer une nouvelle heure)
                if (checkValue)
                {

                    try
                    {
                        value =
                            editStationForm.time.getValue().replace(' ',
                            '0');
                        DateTimeFormat format = new DateTimeFormat(formatSaisi);

                        if (!value.equals(StationSection.defaultValue))
                            format.parseObject(value);
                    }
                    catch (ParseException e)
                    {
                        Utils.messageErreurAppend(Messages.Etape_38
                            + formatSaisi + Messages.Etape_39 + value);
                        ok = ok && false;
                        e.printStackTrace();
                    }
                }
            }
            // DATE
            formatSaisi =
                editStationForm.date.getConverter().getText();
            checkValue = true;
            if (!editStationForm.date.isManual())
            {
                // position dans fichier
                // check valeurs ligne/debut/fin
                resultat1 =
                    editStationForm.date.checkAutoInputValues(Messages
                        .getString(Intern.S_DATE));
                if (resultat1)
                {
                    String lign = editStationForm.date.getLineText(0);
                    ligneOK =
                        editStationForm.date.checkInDataOrHeader(lign,
                            Messages.getString(Intern.S_DATE));
                    ok = ok && ligneOK;
                }
                else
                {
                    checkValue = false;
                }
                ok = ok && resultat1;
            }
            // check value (creer une nouvelle date)
            if (checkValue)
            {
                value =
                    editStationForm.date.getValue().replace(' ', '0');
                try
                {
                    DateTimeFormat format = new DateTimeFormat(formatSaisi);
                    format.parseObject(value);
                }
                catch (ParseException e)
                {
                    Utils.messageErreurAppend(Messages.Etape_43 + formatSaisi
                        + Messages.Etape_44 + value);
                    ok = ok && false;
                    e.printStackTrace();
                }
            }
            /**
             * ==============================================================================================
             * bottom depth
             * ==============================================================================================
             */
            // PROFONDEUR non-obligatoire
            // value = editStationForm.depth.getValue();
            checkValue = true;
            // if (value != null && !value.trim().equals(Global.EMPTY_STRING)){
            if (!editStationForm.depth.isManual()
                    && !rulesManager.isTrajectory())
            {
                // non-obligatoire

                resultat1 =
                    editStationForm.depth
                    .checkAutoInputValues(Messages
                        .getString(Intern.S_DEPTH));
                if (resultat1)
                {
                    String lign = editStationForm.depth.getLineText(0);
                    ligneOK =
                        editStationForm.depth.checkInDataOrHeader(
                            lign, Messages.getString(Intern.S_DEPTH));
                    ok = ok && ligneOK;
                }
                else
                {
                    checkValue = false;
                }
                ok = ok && resultat1;
            }
            if (checkValue)
            {
                value = editStationForm.depth.getValue();
                if (value != null)
                {
                    if (!value.trim().equals("") && !value.equals("NaN") && !value.equals(StationSection.defaultValue)) { //$NON-NLS-1$ //$NON-NLS-2$
                        if (!Pattern.matches(
                            "^[0-9]*[0-9][.]?[0-9]*[0-9]*$", value.trim())) { //$NON-NLS-1$
                            ok = ok && false;
                            Utils.messageErreurAppend(Messages.Etape_52);
                        }
                    }
                }
                else
                {
                    ok = ok && false;
                    Utils.messageErreurAppend(Messages.Etape_52);
                }
            }
            // }
            /**
             * ==============================================================================================
             * UT conversion
             * ==============================================================================================
             */
            // DECALAGE/TU
            checkValue = true;
            if (!editStationForm.utConvertion.isManual())
            {
                resultat1 =
                    editStationForm.utConvertion
                    .checkAutoInputValues(Messages
                        .getString(Intern.S_OFFSETTU));
                if (resultat1)
                {
                    String lign =
                        editStationForm.utConvertion.getLineText(0);
                    ligneOK =
                        editStationForm.utConvertion
                        .checkInDataOrHeader(lign, Messages
                            .getString(Intern.S_OFFSETTU));
                    ok = ok && ligneOK;
                }
                else
                {
                    checkValue = false;
                }
                ok = ok && resultat1;
            }
            if (checkValue)
            {
                value = editStationForm.utConvertion.getValue();
                if (!value.trim().equals("") && !value.equals("NaN")) { //$NON-NLS-1$ //$NON-NLS-2$
                    if (!Pattern.matches(
                        "^[+-]?[0-9]*[0-9][.]?[0-9]*$", value.trim())) { //$NON-NLS-1$
                        ok = ok && false;
                        Utils.messageErreurAppend(Messages.Etape_119);
                    }
                }
            }
            /**
             * ==============================================================================================
             * latitude
             * ==============================================================================================
             */
            formatSaisi =
                editStationForm.latitude.getConverter().getText();
            checkValue = true;
            if (!editStationForm.latitude.isManual())
            {
                // position dans fichier
                resultat1 =
                    editStationForm.latitude
                    .checkAutoInputValues(Messages.getString(Intern.S_LAT));
                if (resultat1)
                {
                    String lign =
                        editStationForm.latitude.getLineText(0);
                    ligneOK =
                        editStationForm.latitude.checkInDataOrHeader(
                            lign, Messages.getString(Intern.S_LAT));
                    ok = ok && ligneOK;
                }
                else
                {
                    checkValue = false;
                }
                ok = ok && resultat1;
            }
            // check value (creer une nouvelle latitude)
            if (checkValue)
            {
                value = editStationForm.latitude.getValue();
                try
                {
                    LatitudeFormat latitudeFormat =
                        new LatitudeFormat(formatSaisi);
                    latitudeFormat
                    .setVariableLength(editStationForm.latitude
                        .isVariablePos());
                    latitudeFormat.parseObject(value);

                }
                catch (ParseException e)
                {
                    Utils.messageErreurAppend(Messages.Etape_55 + formatSaisi
                        + Messages.Etape_56 + value);
                    ok = ok && false;
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    Utils.messageErreurAppend(Messages.Etape_53);
                    ok = ok && false;
                    e.printStackTrace();
                }
            }

            /**
             * ==============================================================================================
             * longitude
             * ==============================================================================================
             */
            formatSaisi =
                editStationForm.longitude.getConverter().getText();
            checkValue = true;
            if (!editStationForm.longitude.isManual())
            {
                // position dans fichier
                resultat1 =
                    editStationForm.longitude
                    .checkAutoInputValues(Messages.getString(Intern.S_LON));
                if (resultat1)
                {
                    String lign =
                        editStationForm.longitude.getLineText(0);
                    ligneOK =
                        editStationForm.longitude.checkInDataOrHeader(
                            lign, Messages.getString(Intern.S_LON));
                    ok = ok && ligneOK;
                }
                else
                {
                    checkValue = false;
                }
                ok = ok && resultat1;
            }
            // check value (creer une nouvelle latitude)
            if (checkValue)
            {
                value = editStationForm.longitude.getValue();
                try
                {
                    LongitudeFormat longitudeFormat =
                        new LongitudeFormat(formatSaisi);
                    longitudeFormat
                    .setVariableLength(editStationForm.longitude
                        .isVariablePos());
                    longitudeFormat.parseObject(value);

                }
                catch (ParseException e)
                {
                    Utils.messageErreurAppend(Messages.Etape_60 + formatSaisi
                        + Messages.Etape_61 + value);
                    ok = ok && false;
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    Utils.messageErreurAppend(Messages.Etape_58);
                    ok = ok && false;
                    e.printStackTrace();
                }
            }
            /**
             * ==============================================================================================
             * **********************************************************************************************
             * specifique SERIE TEMPO ou TRAJECTOIRE
             * **********************************************************************************************
             * ==============================================================================================
             */
            // specifique SERIE TEMPO ou TRAJECTOIRE
            if (rulesManager.isTimeSeries() || rulesManager.isTrajectory())
            {

                /**
                 * ==============================================================================================
                 * sensor depth
                 * ==============================================================================================
                 */
                // PROFONDEUR DU CAPTEUR
                checkValue = true;
                if (!editStationForm.sensorDepth.isManual())
                {
                    resultat1 =
                        editStationForm.sensorDepth
                        .checkAutoInputValues(Messages
                            .getString(Intern.S_SENSORDEPTH));
                    if (resultat1)
                    {
                        String lign =
                            editStationForm.sensorDepth.getLineText(0);
                        ligneOK =
                            editStationForm.sensorDepth
                            .checkInDataOrHeader(lign, Messages
                                .getString(Intern.S_SENSORDEPTH));
                        ok = ok && ligneOK;
                    }
                    else
                    {
                        checkValue = false;
                    }
                    ok = ok && resultat1;
                }
                else
                {
                    value = editStationForm.sensorDepth.getValue();

                    // FT 4466 valeur vide autorisée dans le cas trajectory
                    if (!rulesManager.isTrajectory())
                    {
                        if (value.trim().equals(Global.EMPTY_STRING))
                        {
                            checkValue = false;
                            // obligatoire que pour les series temporelles
                            // if (ViewMain.radSerieTempo.getSelection()){
                            ok = ok && false; // FAE 0001232
                            // FAE 1673 : n'afficher le message que si erreur
                            Utils.messageErreurAppend(Messages.Etape_77);
                            Utils.messageErreurAppend(Messages.Etape_79);
                            // }
                        }
                    }
                }
                if (checkValue)
                {
                    value = editStationForm.sensorDepth.getValue();

                    // FT 4466 valeur vide autorisée dans le cas trajectory
                    if (!(rulesManager.isTrajectory() && ""
                            .equals(value)))
                    {
                        if (!Pattern.matches(
                            "^[0-9]*[0-9][.]?[0-9]*[0-9]*$", value.trim())) { //$NON-NLS-1$
                            ok = ok && false;
                            Utils.messageErreurAppend(Messages.Etape_81);
                        }
                    }
                }
                /**
                 * ==============================================================================================
                 * sampling rate
                 * ==============================================================================================
                 */
                // FREQUENCE ECHANTILLONNAGE
                // ne tester que si section frequence pas grisee !
                checkValue = true;
                if (editStationForm.samplingRate.isEnabled())
                {
                    if (!editStationForm.samplingRate.isManual())
                    {
                        resultat1 =
                            editStationForm.samplingRate
                            .checkAutoInputValues(Messages
                                .getString(Intern.S_SAMPLINGRATE));
                        if (resultat1)
                        {
                            String lign =
                                editStationForm.samplingRate
                                .getLineText(0);
                            ligneOK =
                                editStationForm.samplingRate
                                .checkInDataOrHeader(lign, Messages
                                    .getString(Intern.S_SAMPLINGRATE));
                            ok = ok && ligneOK;
                        }
                        else
                        {
                            checkValue = false;
                        }
                        ok = ok && resultat1;
                    }
                    if (checkValue)
                    {
                        value =
                            editStationForm.samplingRate.getValue();

                        if (value.trim().equals(Global.EMPTY_STRING))
                        {
                            ok = ok && false;
                            Utils.messageErreurAppend(Messages.Etape_103a);
                        }
                        else
                        {
                            if (!Pattern.matches(
                                "^[0-9]*[0-9][.]?[0-9]*[0-9]*$", value.trim())) { //$NON-NLS-1$
                                ok = ok && false;
                                Utils.messageErreurAppend(Messages.Etape_103);
                            }
                            else
                            {
                                Double dblValue = Double.parseDouble(value);
                                if (dblValue.compareTo(new Double("0.0")) == 0) { //$NON-NLS-1$
                                    ok = ok && false;
                                    Utils
                                    .messageErreurAppend(Messages.Etape_103b);
                                }
                                else
                                {
                                    org.nfunk.jep.JEP parser =
                                        new org.nfunk.jep.JEP();
                                    parser.addStandardFunctions();
                                    parser.addStandardConstants();
                                    parser.addVariable(Global.STR_CONVERT,
                                        dblValue);
                                    parser
                                    .parseExpression(Global.STR_CONVERT
                                        + editStationForm.samplingRate
                                        .getConverter().getText()
                                        .trim());

                                    Double resultJEP = parser.getValue();
                                    if (resultJEP.isNaN())
                                    {
                                        ok = ok && false;
                                        Utils
                                        .messageErreurAppend(Messages.Etape_103);
                                    }
                                }
                            }
                        }
                    }
                }
            } // fin specifique serie tempo ou trajectoire
            /**
             * ==============================================================================================
             * **********************************************************************************************
             * specifique SERIE TEMPO 
             * **********************************************************************************************
             * ==============================================================================================
             */
            // specifique SERIE TEMPO
            if (rulesManager.isTimeSeries())
            {
                /**
                 * ==============================================================================================
                 * latitude end
                 * ==============================================================================================
                 */
                // LATITUDE DE FIN
                formatSaisi =
                    editStationForm.endLatitude.getConverter()
                    .getText();
                value = editStationForm.endLatitude.getValue();
                checkValue = true;
                if (value != null
                        && !value.trim().equals(Global.EMPTY_STRING)
                        && !value.equals(editStationForm.latitude
                            .getValue()))
                {
                    // vérification du format
                    if (formatSaisi == null
                            || formatSaisi.equals(Global.EMPTY_STRING))
                    {
                        formatSaisi =
                            editStationForm.latitude.getConverter()
                            .getText();
                        editStationForm.endLatitude.getConverter()
                        .setText(formatSaisi);
                    }
                    if (!editStationForm.endLatitude.isManual())
                    {
                        // position dans fichier
                        resultat1 =
                            editStationForm.endLatitude
                            .checkAutoInputValues(Messages
                                .getString(Intern.S_ELAT));
                        if (resultat1)
                        {
                            String lign =
                                editStationForm.endLatitude
                                .getLineText(0);
                            ligneOK =
                                editStationForm.endLatitude
                                .checkInDataOrHeader(lign, Messages
                                    .getString(Intern.S_ELAT));
                            ok = ok && ligneOK;
                        }
                        else
                        {
                            checkValue = false;
                        }
                        ok = ok && resultat1;
                    }
                    // check value (creer une nouvelle latitude)
                    if (checkValue)
                    {
                        try
                        {
                            LatitudeFormat latitudeFormat =
                                new LatitudeFormat(formatSaisi);
                            latitudeFormat
                            .setVariableLength(editStationForm.endLatitude
                                .isVariablePos());
                            latitudeFormat.parseObject(value);

                        }
                        catch (ParseException e)
                        {
                            Utils.messageErreurAppend(Messages.Etape_64);
                            ok = ok && false;
                            e.printStackTrace();
                        }
                    }
                }
                /**
                 * ==============================================================================================
                 * longitude end
                 * ==============================================================================================
                 */
                // LONGITUDE DE FIN
                formatSaisi =
                    editStationForm.endLongitude.getConverter()
                    .getText();
                value = editStationForm.endLongitude.getValue();
                checkValue = true;
                if (value != null
                        && !value.trim().equals(Global.EMPTY_STRING)
                        && !value.equals(editStationForm.longitude
                            .getValue()))
                {
                    // vérification du format
                    if (formatSaisi == null
                            || formatSaisi.equals(Global.EMPTY_STRING))
                    {
                        formatSaisi =
                            editStationForm.longitude.getConverter()
                            .getText();
                        editStationForm.endLongitude.getConverter()
                        .setText(formatSaisi);
                    }
                    if (!editStationForm.endLongitude.isManual())
                    {
                        // position dans fichier
                        resultat1 =
                            editStationForm.endLongitude
                            .checkAutoInputValues(Messages
                                .getString(Intern.S_ELON));
                        if (resultat1)
                        {
                            String lign =
                                editStationForm.endLongitude
                                .getLineText(0);
                            ligneOK =
                                editStationForm.endLongitude
                                .checkInDataOrHeader(lign, Messages
                                    .getString(Intern.S_ELON));
                            ok = ok && ligneOK;
                        }
                        else
                        {
                            checkValue = false;
                        }
                        ok = ok && resultat1;
                    }
                    // check value (creer une nouvelle longitude)
                    if (checkValue)
                    {
                        try
                        {
                            LongitudeFormat longitudeFormat =
                                new LongitudeFormat(formatSaisi);
                            longitudeFormat
                            .setVariableLength(editStationForm.endLongitude
                                .isVariablePos());
                            longitudeFormat.parseObject(value);

                        }
                        catch (ParseException e)
                        {
                            Utils.messageErreurAppend(Messages.Etape_67);
                            ok = ok && false;
                            e.printStackTrace();
                        }
                    }
                }
                /**
                 * ==============================================================================================
                 * depth end
                 * ==============================================================================================
                 */
                // PROFONDEUR DE FIN
                checkValue = true;
                if (!editStationForm.endDepth.isManual())
                {
                    resultat1 =
                        editStationForm.endDepth
                        .checkAutoInputValues(Messages
                            .getString(Intern.S_EDEPTH));
                    if (resultat1)
                    {
                        String lign =
                            editStationForm.endDepth.getLineText(0);
                        ligneOK =
                            editStationForm.endDepth
                            .checkInDataOrHeader(lign, Messages
                                .getString(Intern.S_EDEPTH));
                        ok = ok && ligneOK;
                    }
                    else
                    {
                        checkValue = false;
                    }
                    ok = ok && resultat1;
                }
                if (checkValue)
                {
                    value = editStationForm.endDepth.getValue();
                    if (!value.trim().equals("") && !value.equals("NaN")) { //$NON-NLS-1$ //$NON-NLS-2$
                        if (!Pattern.matches(
                            "^[0-9]*[0-9][.]?[0-9]*$", value.trim())) { //$NON-NLS-1$
                            ok = ok && false;
                            Utils.messageErreurAppend(Messages.Etape_70);
                        }
                    }
                }
                /**
                 * ==============================================================================================
                 * magnetic declin
                 * ==============================================================================================
                 */
                // DECLINAISON MAGNETIQUE
                value = editStationForm.magneticDeclin.getValue();
                checkValue = true;
                if (!editStationForm.magneticDeclin.isManual())
                {
                    resultat1 =
                        editStationForm.magneticDeclin
                        .checkAutoInputValues(Messages
                            .getString(Intern.S_MAGNETIC));
                    if (resultat1)
                    {
                        String lign =
                            editStationForm.magneticDeclin
                            .getLineText(0);
                        ligneOK =
                            editStationForm.magneticDeclin
                            .checkInDataOrHeader(lign, Messages
                                .getString(Intern.S_MAGNETIC));
                        ok = ok && ligneOK;
                    }
                    else
                    {
                        checkValue = false;
                    }
                    ok = ok && resultat1;
                }
                if (checkValue)
                {
                    if (!value.trim().equals("")) { //$NON-NLS-1$
                        if (!Pattern
                                .matches(
                                    "^[+-]{0,1}[0-9]*[0-9][.]?[0-9]*[0-9]*$", value.trim())) { //$NON-NLS-1$
                            ok = ok && false;
                            Utils.messageErreurAppend(Messages.Etape_102);
                        }
                    }
                }

            } // fin specifique serie tempo
            /**
             * ==============================================================================================
             * flag QC
             * ==============================================================================================
             */
            // Section Flag QC
            boolean flagOK = false;
            for (i = 0; i < size; i++)
            {
                String nomSection =
                    Messages.getString(Intern.S_QCFLAG)
                    + Global.SEP
                    + editStationForm.qcFlags.getTabFlagQC()
                    .getItems()[i].getText(1);

                AutoLine line =
                    new AutoLine(1, true, MetaInData.FACULTATIF, false);
                line.draw(new Shell(), false);
                line
                .setValue(editStationForm.qcFlags.getTabFlagQC()
                    .getItems()[i].getText(2),
                    editStationForm.qcFlags.getTabFlagQC()
                    .getItems()[i].getText(3),
                    editStationForm.qcFlags.getTabFlagQC()
                    .getItems()[i].getText(4));

                flagOK = line.checkValues(nomSection);
                if (flagOK)
                {
                    String zone =
                        Messages.getString(Intern.S_QCFLAG).trim()
                        + " : " //$NON-NLS-1$
                        + editStationForm.qcFlags
                        .getTabFlagQC().getItems()[i].getText(1);
                    ligneOK =
                        line.checkInDataOrHeader(
                            line.getNumberLine().getText(), zone);
                    ok = ok && ligneOK;
                }
                else
                {
                    ok = ok && false;
                }
                line.dispose();
            }

            if (ok)
            {
                editStationForm.setOkApresReset(true);
                okStation();
            }
            else
            {
                Nemo.getInstance().getViewMain().tabStation.setText(Messages.onglet_station_error);
                Nemo.getInstance().getViewMain().setEtape(2);
            }
        }
        catch (Exception e)
        {
            Utils.messageErreurAppend(Messages.Etape_132);
            Nemo.getInstance().getViewMain().setEtape(2);
            e.printStackTrace();
        }
    }

    public static void validationData()
    {
        EditConversionForm editConversionForm = Nemo.getInstance().getViewMain().getEditConversionForm();
        EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
        EditDataForm editDataForm = Nemo.getInstance().getViewMain().getEditDataForm();

        Boolean valeurNonRemplie = false;
//      boolean existDEPH = false;
        // retour e l'onglet message d'erreur
        ViewLog.tabFolderLog.setSelection(0);

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING);

        // mise e jour avant toute chose
        editDataForm.getMyTableData().majTypeData();

        // test de toutes les valeurs debut / fin des Data
        Boolean ok = true;
        try
        {
            ok = editDataForm.getMyTableData().majChampTest();
        }
        catch (Exception e)
        {
            ok = false;
            e.printStackTrace();
        }

        if (!ok)
        {
            Utils.messageErreurAppend(Messages.Etape_131);
        }

        int nbreParam = editDataForm.getMyTableData().getTypes().size();
        int[] debuts, fins;
        String[] debutsFlag, finsFlag;
        int cptDebut = 0;
        int cptFin = 0;

        String fmOut = new String();
        // Formatter formatter = new Formatter();

        if (nbreParam == 0)
        {
            ok = false;
            Utils.messageErreurAppend(Messages.Etape_117);
        }
        else
        {

            debuts = new int[nbreParam];
            fins = new int[nbreParam];
            debutsFlag = new String[nbreParam];
            finsFlag = new String[nbreParam];

            // remplissage des listes
            for (int i = 0; i < nbreParam; i++)
            {

                TypeData typeCourant = editDataForm.getMyTableData().getTypes().get(i);
                // FAE 6538
                if (typeCourant.getUnite().equals(Global.EMPTY_STRING)){
                    ok = false;
                    Utils.messageErreurAppend(Messages.Etape_118);
                    break;
                }
                debuts[i] = typeCourant.getDebut();
                fins[i] = typeCourant.getFin();
                debutsFlag[i] = typeCourant.getDebutFlag();
                finsFlag[i] = typeCourant.getFinFlag();

                // test direct sur valeur entree
                ok = ok && true;

                // test direct sur valeur sortie (valeur par defaut)
                try
                {
                    fmOut = Global.EMPTY_STRING;
                    String formatString = typeCourant.getFormat();

                    // compatiblité des tailles
                    int index = 0;
                    int indexSign = formatString.indexOf('+');
                    if (indexSign == -1)
                    {
                        index = formatString.indexOf('%') + 1;
                    }
                    else
                    {
                        index = indexSign + 1;
                    }
                    int totLength =
                        Integer.parseInt(formatString.substring(index,
                            index + 1));

                    // Float ou Decimal ?
                    String type =
                        formatString.substring(formatString.length() - 1);
                    if (type.equals("f") || type.equals("F")) { //$NON-NLS-1$ //$NON-NLS-2$
                        fmOut =
                            String.format(java.util.Locale.US, formatString,
                                Float.parseFloat(typeCourant.getSortie()));
                    }
                    else if (type.equals("d") || type.equals("D")) { //$NON-NLS-1$ //$NON-NLS-2$
                        fmOut =
                            String.format(java.util.Locale.US, formatString,
                                Integer.parseInt(typeCourant.getSortie()));
                    }

                    if ((fmOut.equals(typeCourant.getSortie()))
                            && (typeCourant.getSortie().length() == totLength))
                    {
                        // ok
                        ok = ok && true;

                    }
                    else
                    {
                        // erreur
                        ok = ok && false;
                        Utils
                        .messageErreurAppend(Messages.Etape_106
                            + " " + typeCourant.getCode() + " : " + Messages.Etape_106b); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
                catch (Exception e)
                {
                    // erreur
                    ok = ok && false;
                    Utils
                    .messageErreurAppend(Messages.Etape_106
                        + " " + typeCourant.getCode() + " : " + Messages.Etape_106b); //$NON-NLS-1$ //$NON-NLS-2$
                    System.out.print(e.getMessage());
                }

                // check des valeurs debut<fin
                if (typeCourant.getDebut() > typeCourant.getFin())
                {
                    Utils.messageErreurAppend(Messages.Etape_98 + Global.SPACE
                        + typeCourant.getCode());
                    ok = false;
                }

                // check des valeurs debut<fin flag
                if (!typeCourant.getDebutFlag().equals(Global.EMPTY_STRING)
                        && !typeCourant.getFinFlag()
                        .equals(Global.EMPTY_STRING))
                {
                    if (Integer.parseInt(typeCourant.getDebutFlag()) > Integer
                            .parseInt(typeCourant.getFinFlag()))
                    {
                        Utils.messageErreurAppend(Messages.Etape_125
                            + Global.SPACE + typeCourant.getCode());
                        ok = false;
                    }
                }
            }
                // parcours des listes et test des valeurs
                for (int i = 0; i < nbreParam; i++)
                {
                    for (int j = 0; j < nbreParam; j++)
                    {
                        if (debuts[i] == debuts[j])
                        {
                            if (debuts[i] == 0)
                            {
                                valeurNonRemplie = true;
                            }
                            cptDebut = cptDebut + 1;
                        }
                        if (fins[i] == fins[j])
                        {
                            cptFin = cptFin + 1;
                        }
                    }
                }

                if (valeurNonRemplie)
                {
                    Utils.messageErreurAppend(Messages.Etape_88);
                    ok = false;
                }

            // TEST pour savoir si PRES est bien en premier mais pas pour
            // profile ou trajectoire
            TypeData premierType = editDataForm.getMyTableData().getTypes().get(0);

            // split sur " - "
            Pattern p = Pattern.compile(Global.SEP);
            String[] mots = p.split(premierType.getCode());
            String firstParameterCode = mots[0];

            editConversionForm.setWarningData(Global.EMPTY_STRING);

            if (Nemo.getInstance().getViewMain().getMyRulesManager().isProfile())
            {

                boolean checkOk = data_checkPressureIsInFirstPosition(firstParameterCode);
                ok = ok && checkOk;

                // Si PRES n'est pas premier => Medatlas impossible
                // A moins que l'on ait les parametre DEPH, PSAL et TEMP ( FAE 3228 )
                if ((Nemo.getInstance().getViewMain().getMyRulesManager().getMyConversionType() == E_TypeConversion.MEDATLAS)
                        && !Nemo.getInstance().getViewMain().getMyRulesManager().isProcessCollection() 
                        && !firstParameterCode.equals(Intern.paramPRES))
                {
                    boolean isPSAL = false;
                    boolean isTEMP = false;
                    boolean isDEPH = false;

                    for (TypeData donnee : editDataForm.getMyTableData().getTypes())
                    {
                        if (donnee.getCode().split("-")[0].trim().equals(
                            Intern.paramDEPH))
                            isDEPH = true;
                        else if (donnee.getCode().split("-")[0].trim().equals(
                            Intern.paramPSAL))
                            isPSAL = true;
                        else if (donnee.getCode().split("-")[0].trim().equals(
                            Intern.paramTEMP))
                            isTEMP = true;
                    }

                    if (!(isPSAL && isTEMP && isDEPH))
                        editConversionForm
                        .setWarningData(Messages.ViewLog_7);
                    else
                        editConversionForm
                        .setWarningData(Global.EMPTY_STRING);
                }
                else
                {
                    editConversionForm
                    .setWarningData(Global.EMPTY_STRING);
                }
            }
        }
        if (ok)
        {
            editDataForm.setOkApresReset (true);
            okData();
        }
        else
        {
            Nemo.getInstance().getViewMain().tabData.setText(Messages.onglet_data_error);
            Nemo.getInstance().getViewMain().setEtape(3);
        }
    }
    /**
     * verification: un parametre de type pression ou equivalent doit être présent sur la première ligne
     * Note: dans certains cas on calculera la pression a partir de la profondeur ou inversement. 
     * cf. DataDomain.setAddedParameter et AddedParameter
     * @param firstParameterCode
     * @return
     */
    private static boolean data_checkPressureIsInFirstPosition(String firstParameterCode)
    {
        boolean checkOk = true;
        DataListEnum dataList = Nemo.getInstance().getConfig().getDataListToUse();

        if (dataList == DataListEnum.P011_VIA_P091 || dataList == DataListEnum.P091)
        {
            //FAE 5819 : ajout du code DPSF(P091) ou MINCDIST(P011) comme parametre possible
            if (!(firstParameterCode.equals(Intern.paramPRES) 
                    || firstParameterCode.equals(Intern.paramDEPH)
                    || firstParameterCode.equals(Intern.paramDPSF)
            ))
            {
                // PRES ou DEPH ou DPSF n'est pas en premier
                Utils.messageErreurAppend(Messages.error_data_p091_firstPRES);
                checkOk=false;
            }
        }
        else if (dataList == DataListEnum.P011_VIA_P021)
        {
            //FAE 5819 : ajout du code DPSF(P091) ou MINCDIST(P011) comme parametre possible
            if (!(firstParameterCode.equals(Intern.paramPREX) 
                    || firstParameterCode.equals(Intern.paramMPMN)
                    || firstParameterCode.equals(Intern.paramAHGT)
            ))
            {
                // PREX ou MPMN ou AHGT n'est pas en premier
                Utils.messageErreurAppend(Messages.error_data_p011_firstPRES);
                checkOk=false;
            }
        }
        return checkOk;
    }
    private static void okFile()
    {
        EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
        EditCruiseForm editCruiseForm = Nemo.getInstance().getViewMain().getEditCruiseForm();


        // envoi des valeurs dans le modele
        // attention aux boutons radio

        // puis remplissage du modele avec les valeurs de l'IHM
        editFileForm.initFromNewModel();

        Nemo.getInstance().getDomain().getMedia().buildInputFilesNames();

        // DataModel.initModele(ViewMain.nomFichier);

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
            if(! Nemo.getInstance().getViewMain().getMyRulesManager().isCruiseEnabled())
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

    private static void okCruise()
    {
        EditConversionForm editConversionForm= Nemo.getInstance().getViewMain().getEditConversionForm();
        EditCruiseForm editCruiseForm= Nemo.getInstance().getViewMain().getEditCruiseForm();

        editCruiseForm.initFromNewModel();

        if (!Nemo.getInstance().getViewMain().tabCruise.isDisposed())
        {
            Nemo.getInstance().getViewMain().tabCruise.setText(Messages.onglet_cruise_valid);
        }
        Utils.messageOK(Messages.success_etape);
        if (!editConversionForm.getWarningCampagne().equals(
            Global.EMPTY_STRING))
        {
            Utils.messageOKAppend(Messages.ViewLog_8);
            Utils.messageOKAppend(editConversionForm
                .getWarningCampagne());
        }

        // validation etape 2
        Nemo.getInstance().getViewMain().setEtape(2);
        // envoi des valeurs dans le modele

    }

    private static void okStation()
    {
        EditConversionForm editConversionForm= Nemo.getInstance().getViewMain().getEditConversionForm();
        EditStationForm editStationForm= Nemo.getInstance().getViewMain().getEditStationForm();

        // envoi des valeurs dans le modele
        Utils.messageOK(Messages.success_etape);
        if (!editConversionForm.getWarningStation().equals(
            Global.EMPTY_STRING))
        {
            Utils.messageOKAppend(Messages.ViewLog_8);
            Utils.messageOKAppend(editConversionForm
                .getWarningStation());
        }
//      DataModel.initStation();
        editStationForm.initFromNewModel();
        Nemo.getInstance().getViewMain().tabStation.setText(Messages.onglet_station_valid);
        Nemo.getInstance().getViewMain().setEtape(3);
    }

    private static void okData()
    {
        Utils.messageOK(Messages.success_etape);
        EditDataForm editDataForm= Nemo.getInstance().getViewMain().getEditDataForm();
        editDataForm.initFromNewModel();
        Nemo.getInstance().getViewMain().tabData.setText(Messages.onglet_data_valid);
        Nemo.getInstance().getViewMain().setEtape(4);
        // envoi des valeurs dans le modele

    }



}
