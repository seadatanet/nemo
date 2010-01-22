package fr.ifremer.nemo.ui.edit;

import java.text.ParseException;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.cruise.TableDataSurvey;
import fr.ifremer.nemo.ui.edit.data.TableData;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.ui.edit.file.FileSection;
import fr.ifremer.nemo.ui.edit.station.AutoLine;
import fr.ifremer.nemo.ui.edit.station.StationSection;
import fr.ifremer.nemo.ui.edit.station.AutoLine.MetaInData;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Preformat;
import fr.ifremer.nemo.util.Global.E_InputType;
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

        // retour à l'onglet message d'erreur
        ViewLog.tabFolderLog.setSelection(0);

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING);

        Boolean ok, resultat;
        ok = true;

        // Fichier avec séparateur
        if (ViewMain.chkSeparateur.getSelection() && !Preformat.isPreformat())
        {
            try
            {
                Preformat.run();

                // le préformattage a ajouté un espace au début de chaque ligne
                // de données
                if (Preformat.isSpaceAdded()
                        && ViewMain.editFileForm.sectionRuptureDonnees
                            .radNextStationSelected())
                {
                    ViewMain.editFileForm.sectionRuptureDonnees
                        .setStartStationIndicator(Global.SPACE
                                + ViewMain.editFileForm.sectionRuptureDonnees
                                    .getStartStationIndicator());
                }

            }
            catch (Exception ex)
            {
                ok = ok && false;
                Utils.messageErreurAppend(Messages.error);
                ex.printStackTrace();
            }
        }

        for (NemoSection desc : ViewMain.editFileForm.listeSections){
            if (desc.isEnabled()){
                resultat = ((FileSection) desc).checkMandatory();
                ok = ok && resultat;
            }
            else{
                desc.razValues();
            }
        }

        if (ok)
        {
            ViewMain.editFileForm.okApresReset = true;
            okFile();
        }
        else
        {
            ViewMain.tabFile.setText(Messages.onglet_file_error);
            ViewMain.etape = 0;
        }
    }

    public static void validationCruise()
    {
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
        resultat =
            ViewMain.editCruiseForm.sectionReference
                .checkMandatory(Messages.edit_cruise_cruiseRef);
        ok = ok && resultat;

        resultat =
            ViewMain.editCruiseForm.sectionReference
                .checkMandatory(Messages.edit_cruise_cruiseName);
        ok = ok && resultat;

        resultat =
            ViewMain.editCruiseForm.sectionReference
                .checkMandatory(Messages.edit_cruise_ship);
        ok = ok && resultat;

        resultat =
            ViewMain.editCruiseForm.sectionLocalisation
                .checkMandatory(Messages.edit_cruise_startDate);
        ok = ok && resultat;

        resultat =
            ViewMain.editCruiseForm.sectionLocalisation
                .checkMandatory(Messages.edit_cruise_endDate);
        ok = ok && resultat;

        resultat =
            ViewMain.editCruiseForm.sectionLocalisation
                .checkMandatory(Messages.edit_cruise_region);
        ok = ok && resultat;

        resultat =
            ViewMain.editCruiseForm.sectionSource
                .checkMandatory(Messages.edit_cruise_country);
        ok = ok && resultat;

        resultat =
            ViewMain.editCruiseForm.sectionSource
                .checkMandatory(Messages.edit_cruise_chief);
        ok = ok && resultat;

        // CHECK DATES
        String startDate =
            ViewMain.editCruiseForm.sectionLocalisation.getItem(
                Messages.edit_cruise_startDate).getText();
        String endDate =
            ViewMain.editCruiseForm.sectionLocalisation.getItem(
                Messages.edit_cruise_endDate).getText();
        if (!startDate.equals(Global.EMPTY_STRING)){
            try{
                DateTimeFormat dateFormat = new DateTimeFormat();
                dateFormat.setFormatString("dd/MM/yyyy"); //$NON-NLS-1$

                dateFormat.parseObject(startDate);

            }
            catch (ParseException e){
                Utils.messageErreurAppend(Messages.Etape_24);
                ok = false;
                e.printStackTrace();
            }
        }
        if (!endDate.equals(Global.EMPTY_STRING)){
            try{
                DateTimeFormat dateFormat = new DateTimeFormat();
                dateFormat.setFormatString("dd/MM/yyyy"); //$NON-NLS-1$

                dateFormat.parseObject(endDate);

            }
            catch (ParseException e){
                Utils.messageErreurAppend(Messages.Etape_27);
                ok = false;
                e.printStackTrace();
            }

        }

        ok = ViewMain.editCruiseForm.sectionType.chekMandatory();

        // FAE 1143 : la référence campagne doit être == 13 caractères pour
        // MEDATLAS
        ViewMain.editConversionForm.setWarningCampagne(Global.EMPTY_STRING);
        String value =
            ViewMain.editCruiseForm.sectionReference.getItem(
                Messages.edit_cruise_cruiseRef).getText().trim();
        if (value.length() != 13){
            ViewMain.editConversionForm.setWarningCampagne(Messages.ViewLog_5);
        }
        else{
            ViewMain.editConversionForm.setWarningStation(Global.EMPTY_STRING);
        }
        if (ok){
            ViewMain.editCruiseForm.okApresReset = true;
            okCruise();
        }
        else{
            if (!ViewMain.tabCruise.isDisposed())
            {
                ViewMain.tabCruise.setText(Messages.onglet_cruise_error);
            }
            ViewMain.etape = 1;
        }

    }

    public static void validationStation()
    {
        // test des valeurs de Station

        // Remplissage du tableau de correspondance de flags
        ViewMain.editStationForm.qcFlags.getFlagTable().fillFlagTable(
            ViewMain.editStationForm.qcFlags.getTabCorrespondance());

        // retour à l'onglet message d'erreur
        ViewLog.tabFolderLog.setSelection(0);

        int i;
        boolean ok, resultat1, resultat2, resultat3, ligneOK, checkValue;
        String value;

        ligneOK = true;
        ok = true;

        int size =
            ViewMain.editStationForm.qcFlags.getTabFlagQC().getItemCount();

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING);

        // NUMERO STATION
        if (!ViewMain.editStationForm.stationNumber.isManual())
        {
            // position dans le fichier, verif des valeurs
            resultat1 =
                ViewMain.editStationForm.stationNumber
                    .checkAutoInputValues(Messages
                        .getString(Intern.S_STATIONNUMBER));
            if (resultat1)
            {
                String lign =
                    ViewMain.editStationForm.stationNumber.getLineText(0);
                ligneOK =
                    ViewMain.editStationForm.stationNumber.checkInDataOrHeader(
                        lign, Messages.getString(Intern.S_STATIONNUMBER));
                ok = ok && ligneOK;
            }
            ok = ok && resultat1;

        }
        else
        {
            value = ViewMain.editStationForm.stationNumber.getManualValue();
            if (ViewMain.radTrajectoire.getSelection())
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
        ViewMain.editConversionForm.setWarningStation(Global.EMPTY_STRING);
        value = ViewMain.editStationForm.stationNumber.getValue().trim();
        if (!ViewMain.isCollection() && value.length() > 5)
        {
            ViewMain.editConversionForm.setWarningStation(Messages.ViewLog_6);
        }
        else
        {
            ViewMain.editConversionForm.setWarningStation(Global.EMPTY_STRING);
        }

        // FAE022 : TYPE DE DONNEES
        if (!ViewMain.isCollection())
        {
            if (ViewMain.editStationForm.dataType.getDatatype().getValueCombo()
                .getSelectionIndex() == -1)
            {
                Utils.messageErreurAppend(Messages.Etape_32);
                ok = ok && false;
            }
        }

        // HISTORIQUE ACQUISITION
        if (!ViewMain.editStationForm.historique.isManual())
        {
            // position dans fichier, verif des valeurs
            resultat1 =
                ViewMain.editStationForm.historique.checkAutoInputValues(0,
                    Messages.Etape_33);
            ok = ok && resultat1;

            if (resultat1)
            {
                int nbreLignes =
                    ViewMain.editStationForm.historique.calculNbreLignes(0);

                if (nbreLignes > 1)
                {
                    ok = ok && false;
                    Utils.messageErreurAppend(Messages.Etape_113);
                }
                else
                {
                    // nbre de ligne bon mais est-ce au bon endroit ?
                    resultat3 =
                        ViewMain.editStationForm.historique.checkPosition(0,
                            nbreLignes);
                    ok = ok && resultat3;

                    if (!resultat3)
                        Utils.messageErreurAppend(Messages.Etape_114);
                }
            }
            // on permet de n'utiliser qu'une ligne sur les deux!
            if (!ViewMain.editStationForm.historique.getLineText(1).trim()
                .equals(Global.EMPTY_STRING))
            {
                resultat2 =
                    ViewMain.editStationForm.historique.checkAutoInputValues(1,
                        Messages.Etape_34);

                if (resultat2)
                {
                    int nbreLignes =
                        ViewMain.editStationForm.historique.calculNbreLignes(1);

                    if (nbreLignes > 1)
                    {
                        ok = ok && false;
                        Utils.messageErreurAppend(Messages.Etape_115);
                    }
                    else
                    {
                        // nbre de ligne bon mais est-ce au bon endroit
                        resultat3 =
                            ViewMain.editStationForm.historique.checkPosition(
                                1, nbreLignes);
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
        if (!ViewMain.editStationForm.comments.isManual())
        {
            resultat1 =
                ViewMain.editStationForm.comments.checkAutoInputValues(Messages
                    .getString(Intern.S_COMMENT));
            ok = ok && resultat1;

            if (resultat1)
            {
                // VERIF si le commentaire ne depasse pas le nombre de lignes
                // d'entete station!
                int nbreLignes =
                    ViewMain.editStationForm.comments.calculNbreLignes();
                resultat3 =
                    ViewMain.editStationForm.comments.checkPosition(nbreLignes);
                ok = ok && resultat3;

                if (!resultat3)
                    Utils.messageErreurAppend(Messages.Etape_108);
            }
        }

        // ECHANTILLON SURFACE
        if (!ViewMain.editStationForm.surfaceSample.isManual())
        {
            resultat1 =
                ViewMain.editStationForm.surfaceSample.checkAutoInputValues(0,
                    Messages.Etape_36);
            ok = ok && resultat1;
            if (resultat1)
            {
                int nbreLignes =
                    ViewMain.editStationForm.surfaceSample.calculNbreLignes(0);

                if (nbreLignes > 1)
                {
                    ok = ok && false;
                    Utils.messageErreurAppend(Messages.Etape_109);
                }
                else
                {
                    // nbre de ligne bon mais est-ce au bon endroit ?
                    resultat3 =
                        ViewMain.editStationForm.surfaceSample.checkPosition(0,
                            nbreLignes);
                    ok = ok && resultat3;

                    if (!resultat3)
                        Utils.messageErreurAppend(Messages.Etape_110);
                }
            }

            // permettre une seule ligne sur les deux!
            if (!ViewMain.editStationForm.surfaceSample.getLineText(1).trim()
                .equals(Global.EMPTY_STRING))
            {
                resultat2 =
                    ViewMain.editStationForm.surfaceSample
                        .checkAutoInputValues(1, Messages.Etape_37);
                if (resultat2)
                {

                    int nbreLignes =
                        ViewMain.editStationForm.surfaceSample
                            .calculNbreLignes(1);

                    if (nbreLignes > 1)
                    {
                        ok = ok && false;
                        Utils.messageErreurAppend(Messages.Etape_112);
                    }
                    else
                    {
                        resultat3 =
                            ViewMain.editStationForm.surfaceSample
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
            ViewMain.editStationForm.time.getConverter().getText();
        checkValue = true;
        if (!ViewMain.editStationForm.time.isManual())
        {
            // position dans fichier
            resultat1 =
                ViewMain.editStationForm.time.checkAutoInputValues(Messages
                    .getString(Intern.S_TIME));
            if (resultat1)
            {
                String lign = ViewMain.editStationForm.time.getLineText(0);
                ligneOK =
                    ViewMain.editStationForm.time.checkInDataOrHeader(lign,
                        Messages.getString(Intern.S_TIME));
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
            value = ViewMain.editStationForm.time.getValue().replace(' ', '0');
            try
            {
                DateTimeFormat format = new DateTimeFormat(formatSaisi);
                format.parseObject(value);
            }
            catch (ParseException e)
            {
                Utils.messageErreurAppend(Messages.Etape_38 + formatSaisi
                        + Messages.Etape_39 + value);
                ok = ok && false;
                e.printStackTrace();
            }
        }

        // DATE
        formatSaisi = ViewMain.editStationForm.date.getConverter().getText();
        checkValue = true;
        if (!ViewMain.editStationForm.date.isManual())
        {
            // position dans fichier
            // check valeurs ligne/debut/fin
            resultat1 =
                ViewMain.editStationForm.date.checkAutoInputValues(Messages
                    .getString(Intern.S_DATE));
            if (resultat1)
            {
                String lign = ViewMain.editStationForm.date.getLineText(0);
                ligneOK =
                    ViewMain.editStationForm.date.checkInDataOrHeader(lign,
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
            value = ViewMain.editStationForm.date.getValue().replace(' ', '0');
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

        // PROFONDEUR non-obligatoire
        // value = ViewMain.editStationForm.depth.getValue();
        checkValue = true;
        // if (value != null && !value.trim().equals(Global.EMPTY_STRING)){
        if (!ViewMain.editStationForm.depth.isManual())
        {
            // non-obligatoire

            resultat1 =
                ViewMain.editStationForm.depth.checkAutoInputValues(Messages
                    .getString(Intern.S_DEPTH));
            if (resultat1)
            {
                String lign = ViewMain.editStationForm.depth.getLineText(0);
                ligneOK =
                    ViewMain.editStationForm.depth.checkInDataOrHeader(lign,
                        Messages.getString(Intern.S_DEPTH));
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
            value = ViewMain.editStationForm.depth.getValue();
            if (!value.trim().equals("") && !value.equals("NaN") && !value.equals(StationSection.defaultValue)) { //$NON-NLS-1$ //$NON-NLS-2$
                if (!Pattern.matches(
                    "^[0-9]*[0-9][.]?[0-9]*[0-9]*$", value.trim())) { //$NON-NLS-1$
                    ok = ok && false;
                    Utils.messageErreurAppend(Messages.Etape_52);
                }
            }
        }
        // }

        // DECALAGE/TU
        checkValue = true;
        if (!ViewMain.editStationForm.utConvertion.isManual())
        {
            resultat1 =
                ViewMain.editStationForm.utConvertion
                    .checkAutoInputValues(Messages
                        .getString(Intern.S_OFFSETTU));
            if (resultat1)
            {
                String lign =
                    ViewMain.editStationForm.utConvertion.getLineText(0);
                ligneOK =
                    ViewMain.editStationForm.utConvertion.checkInDataOrHeader(
                        lign, Messages.getString(Intern.S_OFFSETTU));
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
            value = ViewMain.editStationForm.utConvertion.getValue();
            if (!value.trim().equals("") && !value.equals("NaN")) { //$NON-NLS-1$ //$NON-NLS-2$
                if (!Pattern.matches(
                    "^[+-]?[0-9]*[0-9][.]?[0-9]*$", value.trim())) { //$NON-NLS-1$
                    ok = ok && false;
                    Utils.messageErreurAppend(Messages.Etape_119);
                }
            }
        }

        // LATITUDE
        formatSaisi =
            ViewMain.editStationForm.latitude.getConverter().getText();
        checkValue = true;
        if (!ViewMain.editStationForm.latitude.isManual())
        {
            // position dans fichier
            resultat1 =
                ViewMain.editStationForm.latitude.checkAutoInputValues(Messages
                    .getString(Intern.S_LAT));
            if (resultat1)
            {
                String lign = ViewMain.editStationForm.latitude.getLineText(0);
                ligneOK =
                    ViewMain.editStationForm.latitude.checkInDataOrHeader(lign,
                        Messages.getString(Intern.S_LAT));
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
            value = ViewMain.editStationForm.latitude.getValue();
            try
            {
                LatitudeFormat latitudeFormat = new LatitudeFormat(formatSaisi);
                latitudeFormat
                    .setVariableLength(ViewMain.editStationForm.latitude
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
        }

        // LONGITUDE
        formatSaisi =
            ViewMain.editStationForm.longitude.getConverter().getText();
        checkValue = true;
        if (!ViewMain.editStationForm.longitude.isManual())
        {
            // position dans fichier
            resultat1 =
                ViewMain.editStationForm.longitude
                    .checkAutoInputValues(Messages.getString(Intern.S_LON));
            if (resultat1)
            {
                String lign = ViewMain.editStationForm.longitude.getLineText(0);
                ligneOK =
                    ViewMain.editStationForm.longitude.checkInDataOrHeader(
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
            value = ViewMain.editStationForm.longitude.getValue();
            try
            {
                LongitudeFormat longitudeFormat =
                    new LongitudeFormat(formatSaisi);
                longitudeFormat
                    .setVariableLength(ViewMain.editStationForm.longitude
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
        }

        // specifique SERIE TEMPO ou TRAJECTOIRE
        if (ViewMain.radSerieTempo.getSelection()
                || ViewMain.radTrajectoire.getSelection())
        {

            // PROFONDEUR DU CAPTEUR
            checkValue = true;
            if (!ViewMain.editStationForm.sensorDepth.isManual())
            {
                resultat1 =
                    ViewMain.editStationForm.sensorDepth
                        .checkAutoInputValues(Messages
                            .getString(Intern.S_SENSORDEPTH));
                if (resultat1)
                {
                    String lign =
                        ViewMain.editStationForm.sensorDepth.getLineText(0);
                    ligneOK =
                        ViewMain.editStationForm.sensorDepth
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
                value = ViewMain.editStationForm.sensorDepth.getValue();
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
            if (checkValue)
            {
                value = ViewMain.editStationForm.sensorDepth.getValue();
                if (!Pattern.matches(
                    "^[0-9]*[0-9][.]?[0-9]*[0-9]*$", value.trim())) { //$NON-NLS-1$
                    ok = ok && false;
                    Utils.messageErreurAppend(Messages.Etape_81);
                }
            }

            // FREQUENCE ECHANTILLONNAGE
            // ne tester que si section frequence pas grisee !
            checkValue = true;
            if (ViewMain.editStationForm.samplingRate.isEnabled())
            {
                if (!ViewMain.editStationForm.samplingRate.isManual())
                {
                    resultat1 =
                        ViewMain.editStationForm.samplingRate
                            .checkAutoInputValues(Messages
                                .getString(Intern.S_SAMPLINGRATE));
                    if (resultat1)
                    {
                        String lign =
                            ViewMain.editStationForm.samplingRate
                                .getLineText(0);
                        ligneOK =
                            ViewMain.editStationForm.samplingRate
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
                    value = ViewMain.editStationForm.samplingRate.getValue();

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
                                Utils.messageErreurAppend(Messages.Etape_103b);
                            }
                            else
                            {
                                org.nfunk.jep.JEP parser =
                                    new org.nfunk.jep.JEP();
                                parser.addStandardFunctions();
                                parser.addStandardConstants();
                                parser
                                    .addVariable(Global.STR_CONVERT, dblValue);
                                parser.parseExpression(Global.STR_CONVERT
                                        + ViewMain.editStationForm.samplingRate
                                            .getConverter().getText().trim());

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

        // specifique SERIE TEMPO
        if (ViewMain.radSerieTempo.getSelection())
        {

            // LATITUDE DE FIN
            formatSaisi =
                ViewMain.editStationForm.endLatitude.getConverter().getText();
            value = ViewMain.editStationForm.endLatitude.getValue();
            checkValue = true;
            if (value != null
                    && !value.trim().equals(Global.EMPTY_STRING)
                    && !value.equals(ViewMain.editStationForm.latitude
                        .getValue()))
            {
                // vérification du format
                if (formatSaisi == null
                        || formatSaisi.equals(Global.EMPTY_STRING))
                {
                    formatSaisi =
                        ViewMain.editStationForm.latitude.getConverter()
                            .getText();
                    ViewMain.editStationForm.endLatitude.getConverter()
                        .setText(formatSaisi);
                }
                if (!ViewMain.editStationForm.endLatitude.isManual())
                {
                    // position dans fichier
                    resultat1 =
                        ViewMain.editStationForm.endLatitude
                            .checkAutoInputValues(Messages
                                .getString(Intern.S_ELAT));
                    if (resultat1)
                    {
                        String lign =
                            ViewMain.editStationForm.endLatitude.getLineText(0);
                        ligneOK =
                            ViewMain.editStationForm.endLatitude
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
                            .setVariableLength(ViewMain.editStationForm.endLatitude
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

            // LONGITUDE DE FIN
            formatSaisi =
                ViewMain.editStationForm.endLongitude.getConverter().getText();
            value = ViewMain.editStationForm.endLongitude.getValue();
            checkValue = true;
            if (value != null
                    && !value.trim().equals(Global.EMPTY_STRING)
                    && !value.equals(ViewMain.editStationForm.longitude
                        .getValue()))
            {
                // vérification du format
                if (formatSaisi == null
                        || formatSaisi.equals(Global.EMPTY_STRING))
                {
                    formatSaisi =
                        ViewMain.editStationForm.longitude.getConverter()
                            .getText();
                    ViewMain.editStationForm.endLongitude.getConverter()
                        .setText(formatSaisi);
                }
                if (!ViewMain.editStationForm.endLongitude.isManual())
                {
                    // position dans fichier
                    resultat1 =
                        ViewMain.editStationForm.endLongitude
                            .checkAutoInputValues(Messages
                                .getString(Intern.S_ELON));
                    if (resultat1)
                    {
                        String lign =
                            ViewMain.editStationForm.endLongitude
                                .getLineText(0);
                        ligneOK =
                            ViewMain.editStationForm.endLongitude
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
                            .setVariableLength(ViewMain.editStationForm.endLongitude
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

            // PROFONDEUR DE FIN
            checkValue = true;
            if (!ViewMain.editStationForm.endDepth.isManual())
            {
                resultat1 =
                    ViewMain.editStationForm.endDepth
                        .checkAutoInputValues(Messages
                            .getString(Intern.S_EDEPTH));
                if (resultat1)
                {
                    String lign =
                        ViewMain.editStationForm.endDepth.getLineText(0);
                    ligneOK =
                        ViewMain.editStationForm.endDepth.checkInDataOrHeader(
                            lign, Messages.getString(Intern.S_EDEPTH));
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
                value = ViewMain.editStationForm.endDepth.getValue();
                if (!value.trim().equals("") && !value.equals("NaN")) { //$NON-NLS-1$ //$NON-NLS-2$
                    if (!Pattern.matches(
                        "^[0-9]*[0-9][.]?[0-9]*$", value.trim())) { //$NON-NLS-1$
                        ok = ok && false;
                        Utils.messageErreurAppend(Messages.Etape_70);
                    }
                }
            }

            // DECLINAISON MAGNETIQUE
            value = ViewMain.editStationForm.magneticDeclin.getValue();
            checkValue = true;
            if (!ViewMain.editStationForm.magneticDeclin.isManual())
            {
                resultat1 =
                    ViewMain.editStationForm.magneticDeclin
                        .checkAutoInputValues(Messages
                            .getString(Intern.S_MAGNETIC));
                if (resultat1)
                {
                    String lign =
                        ViewMain.editStationForm.magneticDeclin.getLineText(0);
                    ligneOK =
                        ViewMain.editStationForm.magneticDeclin
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
                    if (!Pattern.matches(
                        "^[+-]{0,1}[0-9]*[0-9][.]?[0-9]*[0-9]*$", value.trim())) { //$NON-NLS-1$
                        ok = ok && false;
                        Utils.messageErreurAppend(Messages.Etape_102);
                    }
                }
            }

        } // fin specifique serie tempo

        // Section Flag QC
        boolean flagOK = false;
        for (i = 0; i < size; i++)
        {
            String nomSection =
                Messages.getString(Intern.S_QCFLAG)
                        + Global.SEP
                        + ViewMain.editStationForm.qcFlags.getTabFlagQC()
                            .getItems()[i].getText(1);

            AutoLine line = new AutoLine(1, true, MetaInData.FACULTATIF, false);
            line.draw(new Shell(), false);
            line.setValue(ViewMain.editStationForm.qcFlags.getTabFlagQC()
                .getItems()[i].getText(2), ViewMain.editStationForm.qcFlags
                .getTabFlagQC().getItems()[i].getText(3),
                ViewMain.editStationForm.qcFlags.getTabFlagQC().getItems()[i]
                    .getText(4));

            flagOK = line.checkValues(nomSection);
            if (flagOK)
            {
                String zone =
                    Messages.getString(Intern.S_QCFLAG).trim()
                            + " : " //$NON-NLS-1$
                            + ViewMain.editStationForm.qcFlags.getTabFlagQC()
                                .getItems()[i].getText(1);
                ligneOK =
                    line.checkInDataOrHeader(line.getNumberLine().getText(),
                        zone);
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
            ViewMain.editStationForm.okApresReset = true;
            okStation();
        }
        else
        {
            ViewMain.tabStation.setText(Messages.onglet_station_error);
            ViewMain.etape = 2;
        }
    }

    public static void validationData()
    {
        // test de toutes les valeurs debut / fin des Data
        Boolean ok = true;
        Boolean valeurNonRemplie = false;
        boolean existDEPH = false;
        // retour e l'onglet message d'erreur
        ViewLog.tabFolderLog.setSelection(0);

        // mise e jour avant toute chose
        TableData.majTypeData();

        int nbreParam = TableData.types.size();
        int[] debuts, fins;
        String[] debutsFlag, finsFlag;
        int cptDebut = 0;
        int cptFin = 0;

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING);

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
                TypeData typeCourant = TableData.types.get(i);
                debuts[i] = typeCourant.getDebut();
                fins[i] = typeCourant.getFin();
                debutsFlag[i] = typeCourant.getDebutFlag();
                finsFlag[i] = typeCourant.getFinFlag();

                // recherche de l'existance du paramètre DEPH
                if (typeCourant.getCode().startsWith(Intern.paramDEPH))
                    existDEPH = true;

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
            TypeData premierType = TableData.types.get(0);

            // split sur " - "
            Pattern p = Pattern.compile(Global.SEP);
            String[] mots = p.split(premierType.getCode());
            String code = mots[0];

            ViewMain.editConversionForm.setWarningData(Global.EMPTY_STRING);
            if (ViewMain.getTypeFichierEntree() == E_InputType.PROFILE)
            {
                if (!(code.equals(Intern.paramPRES) || code
                    .equals(Intern.paramDEPH)))
                {
                    // PRES ou DEPH n'est pas en premier
                    Utils.messageErreurAppend(Messages.error_data_firstPRES);
                    ok = ok && false;
                }
                else
                {
                    ok = ok && true;
                }

                // Si PRES n'est pas premier => Medatlas impossible
                // A moins que l'on est les parametre DEPH, PSAL et TEMP ( FAE 3228 )
                if (!ViewMain.isCollection() && !code.equals(Intern.paramPRES))
                {
                    boolean isPSAL = false;
                    boolean isTEMP = false;
                    boolean isDEPH = false;
                    
                   for (TypeData donnee : TableData.types)
                   {
                      if (donnee.getCode().split("-")[0].trim().equals(Intern.paramDEPH))
                          isDEPH = true;
                      else if (donnee.getCode().split("-")[0].trim().equals(Intern.paramPSAL))
                          isPSAL = true;
                      else if (donnee.getCode().split("-")[0].trim().equals(Intern.paramTEMP))
                          isTEMP = true;                      
                   }
                   
                   if (!(isPSAL && isTEMP && isDEPH))
                       ViewMain.editConversionForm
                            .setWarningData(Messages.ViewLog_7);
                   else
                       ViewMain.editConversionForm
                       .setWarningData(Global.EMPTY_STRING);
                }
                else
                {
                    ViewMain.editConversionForm
                        .setWarningData(Global.EMPTY_STRING);
                }
            }
            else if (ViewMain.getTypeFichierEntree() == E_InputType.PROFILE)
            {
                if (!code.equals(Intern.paramDEPH) && existDEPH)
                {
                    // DEPH existe et n'est pas en premier
                    Utils
                        .messageErreurAppend(Messages.error_data_firstDEPH_TRAJ);
                    ok = ok && false;
                }
            }
        }
        if (ok)
        {
            ViewMain.editDataForm.okApresReset = true;
            okData();
        }
        else
        {
            ViewMain.tabData.setText(Messages.onglet_data_error);
            ViewMain.etape = 3;
        }
    }

    private static void okFile()
    {
        // FAE1386 : desactiver "One File per station" si Trajectory
        if (ViewMain.radTrajectoire.getSelection())
        {
            ViewMain.radMonoStation.setSelection(false);
            ViewMain.radMonoStation.setEnabled(false);
            ViewMain.radMonoFichier.setEnabled(true);
            ViewMain.radMonoFichier.setSelection(true);
        }

        // envoi des valeurs dans le modele
        // attention aux boutons radio

        // puis remplissage du modele avec les valeurs de l'IHM
        if (ViewMain.radioFichier.getSelection())
            DataModel.initDesc(ViewMain.nomFichier);
        else
            DataModel.initDesc(ViewMain.nomRepertoire);
        //DataModel.initModele(ViewMain.nomFichier);

        Utils.messageOK(Messages.success_etape);

        // on rajoute "ok" dans l'onglet description
        ViewMain.tabFile.setText(Messages.onglet_file_valid);

        // on valide l'etape 1
        ViewMain.editFileForm.validee = true;

        // verrouillage de l'option serieTemporelle pour eviter les changements
        ViewMain.enableChoice(false);

        try
        {
            // FAE 022
            if (ViewMain.isCollection())
            {
                ViewMain.editCruiseForm.okApresReset = true;
                ViewMain.editCruiseForm.validee = true;

                DataModel.initCruise();
                ViewMain.etape = 2;
            }
            else
            {
                ViewMain.etape = 1;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private static void okCruise()
    {

        DataModel.initCruise();
        if (!ViewMain.tabCruise.isDisposed())
        {
            ViewMain.tabCruise.setText(Messages.onglet_cruise_valid);
        }
        Utils.messageOK(Messages.success_etape);
        if (!ViewMain.editConversionForm.getWarningCampagne().equals(
            Global.EMPTY_STRING))
        {
            Utils.messageOKAppend(Messages.ViewLog_8);
            Utils.messageOKAppend(ViewMain.editConversionForm
                .getWarningCampagne());
        }

        // validation etape 2
        ViewMain.etape = 2;
        // envoi des valeurs dans le modele

    }

    private static void okStation()
    {

        // envoi des valeurs dans le modele
        Utils.messageOK(Messages.success_etape);
        if (!ViewMain.editConversionForm.getWarningStation().equals(
            Global.EMPTY_STRING))
        {
            Utils.messageOKAppend(Messages.ViewLog_8);
            Utils.messageOKAppend(ViewMain.editConversionForm
                .getWarningStation());
        }
        DataModel.initStation();
        ViewMain.tabStation.setText(Messages.onglet_station_valid);
        ViewMain.etape = 3;
    }

    private static void okData()
    {
        Utils.messageOK(Messages.success_etape);
        DataModel.initData();
        ViewMain.tabData.setText(Messages.onglet_data_valid);
        ViewMain.etape = 4;
        // envoi des valeurs dans le modele

    }

}
