package fr.ifremer.nemo.ui.edit.station;

import java.text.ParseException;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.FormValidator;
import fr.ifremer.nemo.ui.edit.conversion.EditConversionForm;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.edit.station.AutoLine.MetaInData;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.format.DateTimeFormat;
import fr.ifremer.nemo.util.format.LatitudeFormat;
import fr.ifremer.nemo.util.format.LongitudeFormat;

/**
 * Class EtapeStation.java
 * Description: TODO
 * @author sbregent 
 */
public class StationFormValidator extends FormValidator
{

    public StationFormValidator()
    {
        super();
    }
    
    
    @Override
    public void validate()
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
            // FAE 6536 : on exige un dataType même si pas onglet Cruise
//            if ( !Nemo.getInstance().getViewMain().getMyRulesManager().isProcessCollection())
//            {
                if (editStationForm.dataType.getDatatype()
                        .getValueCombo().getSelectionIndex() == -1)
                {
                    Utils.messageErreurAppend(Messages.Etape_32);
                    ok = ok && false;
                }
//            }

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
            checkValue = true;
            if (!editStationForm.depth.isManual()
                    && !rulesManager.isTrajectory())
            {
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
                    // 4471: depth manuel possible pour les trajectoire, mais doit être vide (une valeur fixe n'a pas de sens pour une trajectoire)
                    if (rulesManager.isTrajectory() && editStationForm.depth.isManual() && !value.trim().equals(""))
                    {
                        ok = ok && false;
                        Utils.messageErreurAppend(Messages.Etape_52traj);
                    }
                    else if (!value.trim().equals("") && !value.equals("NaN") && !value.equals(StationSection.defaultValue)) { //$NON-NLS-1$ //$NON-NLS-2$
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
//                okStation();
                onSuccess();
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
    @Override
    protected void onSuccess()
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
}
