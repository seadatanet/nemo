package fr.ifremer.nemo.ui;

import java.text.ParseException;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.ui.object.AutoLine;
import fr.ifremer.nemo.ui.object.FileSection;
import fr.ifremer.nemo.ui.object.NemoSection;
import fr.ifremer.nemo.ui.object.AutoLine.MetaInData;
import fr.ifremer.nemo.util.DateTimeFormat;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.LatitudeFormat;
import fr.ifremer.nemo.util.LongitudeFormat;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Preformat;

public class Etape {

	/*
	 * Gestion des differents affichages lors de la sélection des onglets de la
	 * fenêtre principale et des différentes étapes.
	 * 
	 */

	public static void validationDesc() {


        // retour à l'onglet message d'erreur
        ViewLog.tabFolderLog.setSelection(0);

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING);

        Boolean ok, resultat;
        ok = true;
        
        
		// Fichier avec séparateur
		if (ViewFile.chkSeparateur.getSelection() && !Preformat.isPreformat()) {
			try {
				Preformat.run();
			} catch (Exception ex) {
                ok = ok && false;
				Utils.messageErreurAppend(Messages.error);
				ex.printStackTrace();
			}
		}
        
        
        for (NemoSection desc : EditDesc.listeSectionsDesc){
            
            if (desc.isEnabled()){
                resultat = ((FileSection) desc).checkMandatory();
                ok = ok && resultat;
            }else{
                desc.razValues();
            }
        }

		if (ok) {
			EditDesc.okApresReset = true;
			okDesc();
		} else {
			ViewFile.tabItem.setText(Messages.onglet_file_error);
			ViewFile.etape = 0;
		}
	}

	public static void validationSurvey() {
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
		resultat = Regexp.checkPresenceValeurObligatoire(
				            EditSurvey.textCampagne.getText(), 
                            Messages.edit_cruise_sectionReference + " / " + Messages.edit_cruise_cruiseRef); //$NON-NLS-1$
		ok = ok && resultat;
		resultat = Regexp.checkPresenceValeurObligatoire(
                            EditSurvey.textNom.getText(), 
                            Messages.edit_cruise_sectionReference + " / " + Messages.edit_cruise_cruiseName); //$NON-NLS-1$
		ok = ok && resultat;
		resultat = Regexp.checkPresenceValeurObligatoire(
                            EditSurvey.comboNavire.getText(), 
                            Messages.edit_cruise_sectionReference + " / " + Messages.edit_cruise_ship); //$NON-NLS-1$
		ok = ok && resultat;
		resultat = Regexp.checkPresenceValeurObligatoire(
				            EditSurvey.textDateDebut.getText(), 
                            Messages.edit_cruise_sectionLocation + " / " + Messages.edit_cruise_startDate); //$NON-NLS-1$
		ok = ok && resultat;
		resultat = Regexp.checkPresenceValeurObligatoire(
                            EditSurvey.textDateFin.getText(), 
                            Messages.edit_cruise_sectionLocation + " / " + Messages.edit_cruise_endDate); //$NON-NLS-1$
		ok = ok && resultat;
		resultat = Regexp.checkPresenceValeurObligatoire(
                            EditSurvey.comboRegion.getText(), 
                            Messages.edit_cruise_sectionLocation + " / " + Messages.edit_cruise_region); //$NON-NLS-1$
		ok = ok && resultat;
		resultat = Regexp.checkPresenceValeurObligatoire(
				            EditSurvey.comboCountries.getText(), 
                            Messages.edit_cruise_sectionDataSource + " / " + Messages.edit_cruise_country); //$NON-NLS-1$
		ok = ok && resultat;
		resultat = Regexp.checkPresenceValeurObligatoire(
                            EditSurvey.textChef.getText(), 
                            Messages.edit_cruise_sectionDataSource + " / " + Messages.edit_cruise_chief); //$NON-NLS-1$
		ok = ok && resultat;

		// CHECK DATES
		if (!EditSurvey.textDateDebut.getText().equals(Global.EMPTY_STRING)) { 
			try {
				DateTimeFormat dateFormat = new DateTimeFormat();
				dateFormat.setFormatString("dd/MM/yyyy"); //$NON-NLS-1$

				dateFormat.parseObject(EditSurvey.textDateDebut.getText());

			} catch (ParseException e) {
				Utils.messageErreurAppend(Messages.Etape_24); 
				ok = false;
				e.printStackTrace();
			}
		}
		if (!EditSurvey.textDateFin.getText().equals(Global.EMPTY_STRING)) {
			try {
				DateTimeFormat dateFormat = new DateTimeFormat();
				dateFormat.setFormatString("dd/MM/yyyy"); //$NON-NLS-1$

				dateFormat.parseObject(EditSurvey.textDateFin.getText());

			} catch (ParseException e) {
				Utils.messageErreurAppend(Messages.Etape_27);
				ok = false;
				e.printStackTrace();
			}

		}

		if (TableDataSurvey.types.size() == 0) {
			Utils.messageErreurAppend(Messages.Etape_28);
			ok = false;
		} else {
			// vérif si pas de "nombre=0"
			int taille = TableDataSurvey.types.size();
			for (int i = 0; i < taille; i++) {
				int nbre = TableDataSurvey.types.get(i).getNombre();
				if (nbre == 0) {
					Utils.messageErreurAppend(Messages.Etape_99);
					ok = false;
                    break;
				}
			}
            
            // TODO ? vérif au moins 1 "sensor"
//            boolean atLeastOne = false;
//            for (int i = 0; i < taille; i++) {
//                if (TableDataSurvey.types.get(i).getSensor()){
//                    atLeastOne = true;
//                }
//            }
//            if (!atLeastOne){
//                Utils.messageErreur(Messages.Etape_130);
//                ok = false;
//            }
		}

        // FAE 1143 : la référence campagne doit être == 13 caractères pour MEDATLAS
        EditConversion.setWarningCampagne(Global.EMPTY_STRING);
        String value = EditSurvey.textCampagne.getText().trim();
        if (value.length() != 13){
            EditConversion.setWarningCampagne(Messages.ViewLog_5);
        }else{
            EditConversion.setWarningStation(Global.EMPTY_STRING);
        }
		if (ok) {
			EditSurvey.okApresReset = true;
			okSurvey();
		} else {
			if (!ViewFile.tabItem1.isDisposed()) {
				ViewFile.tabItem1.setText(Messages.onglet_cruise_error);
			}
			ViewFile.etape = 1;
		}

	}

	public static void validationStation() {        
		// test des valeurs de Station

		// Remplissage du tableau de correspondance de flags
		EditStation.qcFlags.getFlagTable().fillFlagTable(EditStation.qcFlags.getTabCorrespondance());
		
		// retour à l'onglet message d'erreur
		ViewLog.tabFolderLog.setSelection(0);

		int i;
		boolean ok, resultat1, resultat2, resultat3, ligneOK, checkValue;
        String value;
        
        ligneOK = true;
		ok = true;
        
        int size = EditStation.qcFlags.getTabFlagQC().getItemCount();

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING);                                                                     

		// NUMERO STATION
		if (!EditStation.stationNumber.isManual()) {
			// position dans le fichier, verif des valeurs
			resultat1 = EditStation.stationNumber.checkAutoInputValues(Messages.getString(NemoSection.S_STATIONNUMBER));
            if (resultat1){
                String lign = EditStation.stationNumber.getLineText(0);
                ligneOK = EditStation.stationNumber.checkInDataOrHeader(lign, Messages.getString(NemoSection.S_STATIONNUMBER));
                ok = ok && ligneOK;
            }
			ok = ok && resultat1;
            
		} else {
            value = EditStation.stationNumber.getManualValue();
            if (ViewFile.radTrajectoire.getSelection()){
                // saisie manuelle - obligatoire
                if (value.trim().equals("") || value.equals("NaN")){                                    //$NON-NLS-1$ //$NON-NLS-2$
                    ok = ok && false;
                    // FAE 1673 : n'afficher le message que si erreur
                    Utils.messageErreurAppend(Messages.Etape_128);
                    Utils.messageErreurAppend(Messages.Etape_129);
                }
            }else{
                // autoincrementation - doit être un entier
                try {
                    Integer.parseInt(value);
                } catch (Exception e) {
                    ok = ok && false;
                    Utils.messageErreurAppend(Messages.Etape_127);
                }
            }
		}
        // FAE 1143 : dans tous les cas, le numéro de station doit être <= 5 caractères pour MEDATLAS
        EditConversion.setWarningStation(Global.EMPTY_STRING);
        value = EditStation.stationNumber.getValue().trim();
        if (!ViewFile.isCollection() && value.length() > 5){
            EditConversion.setWarningStation(Messages.ViewLog_6);
        }else{
            EditConversion.setWarningStation(Global.EMPTY_STRING);
        }
        
		// FAE022 : TYPE DE DONNEES
		if (!ViewFile.isCollection()) {
			if (EditStation.dataType.getDatatype().getValueCombo().getSelectionIndex() == -1) {
				Utils.messageErreurAppend(Messages.Etape_32); 
				ok = ok && false;
			}
		}
        
		// HISTORIQUE ACQUISITION
		if (!EditStation.historique.isManual()) {
			// position dans fichier, verif des valeurs
			resultat1 = EditStation.historique.checkAutoInputValues(0, Messages.Etape_33);
			ok = ok && resultat1;

			if (resultat1) {
                int nbreLignes = EditStation.historique.calculNbreLignes(0);

				if (nbreLignes > 1) {
					ok = ok && false;
					Utils.messageErreurAppend(Messages.Etape_113);
				} else {
					// nbre de ligne bon mais est-ce au bon endroit ?
                    resultat3 = EditStation.historique.checkPosition(0, nbreLignes);
                    ok = ok && resultat3;
                    
					if (!resultat3)
						Utils.messageErreurAppend(Messages.Etape_114);
				}
			}
			// on permet de n'utiliser qu'une ligne sur les deux!
			if (!EditStation.historique.getLineText(1).trim().equals(Global.EMPTY_STRING)) { 
                resultat2 = EditStation.historique.checkAutoInputValues(1, Messages.Etape_34);

				if (resultat2) {
					int nbreLignes = EditStation.historique.calculNbreLignes(1);

					if (nbreLignes > 1) {
						ok = ok && false;
						Utils.messageErreurAppend(Messages.Etape_115);
					} else {
						// nbre de ligne bon mais est-ce au bon endroit
                        resultat3 = EditStation.historique.checkPosition(1, nbreLignes);
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
		if (!EditStation.comments.isManual()) {
			resultat1 = EditStation.comments.checkAutoInputValues(Messages.getString(NemoSection.S_COMMENT));
			ok = ok && resultat1;

			if (resultat1) {
				// VERIF si le commentaire ne depasse pas le nombre de lignes d'entete station!
				int nbreLignes = EditStation.comments.calculNbreLignes();
                resultat3 = EditStation.comments.checkPosition(nbreLignes);
                ok = ok && resultat3;
                
                if (!resultat3)
                    Utils.messageErreurAppend(Messages.Etape_108);
			}
		}

        
		// ECHANTILLON SURFACE
		if (!EditStation.surfaceSample.isManual()) {
            resultat1 = EditStation.surfaceSample.checkAutoInputValues(0, Messages.Etape_36);
			ok = ok && resultat1;
			if (resultat1) {
				int nbreLignes = EditStation.surfaceSample.calculNbreLignes(0);

				if (nbreLignes > 1) {
					ok = ok && false;
					Utils.messageErreurAppend(Messages.Etape_109);
				} else {
					// nbre de ligne bon mais est-ce au bon endroit ?
                    resultat3 = EditStation.surfaceSample.checkPosition(0, nbreLignes);
                    ok = ok && resultat3;
                    
                    if (!resultat3)
                        Utils.messageErreurAppend(Messages.Etape_110);
				}
			}

			// permettre une seule ligne sur les deux!
			if (!EditStation.surfaceSample.getLineText(1).trim().equals(Global.EMPTY_STRING)) {
                resultat2 = EditStation.surfaceSample.checkAutoInputValues(1, Messages.Etape_37);
				if (resultat2) {

					int nbreLignes = EditStation.surfaceSample.calculNbreLignes(1);

					if (nbreLignes > 1) {
						ok = ok && false;
						Utils.messageErreurAppend(Messages.Etape_112);
					} else {
                        resultat3 = EditStation.surfaceSample.checkPosition(1, nbreLignes);
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
        String formatSaisi = EditStation.time.getConverter().getText();
        checkValue = true;
		if (!EditStation.time.isManual()) {
			// position dans fichier
            resultat1 = EditStation.time.checkAutoInputValues(Messages.getString(NemoSection.S_TIME));
            if (resultat1){
                String lign = EditStation.time.getLineText(0);
                ligneOK = EditStation.time.checkInDataOrHeader(lign, Messages.getString(NemoSection.S_TIME));
                ok = ok && ligneOK;
            }else{
                checkValue = false;
            }
			ok = ok && resultat1;
		}
        // check value (creer une nouvelle  heure)
        if (checkValue){  
            value = EditStation.time.getValue().replace(' ', '0');        
            try {
                DateTimeFormat format = new DateTimeFormat(formatSaisi);
                format.parseObject(value);
            } catch (ParseException e) {
                Utils.messageErreurAppend(Messages.Etape_38
                    + formatSaisi 
                    + Messages.Etape_39
                    + value); 
                ok = ok && false;
                e.printStackTrace();
            }
        }
        

		// DATE
        formatSaisi = EditStation.date.getConverter().getText();
        checkValue = true;
		if (!EditStation.date.isManual()) {
			// position dans fichier
			// check valeurs ligne/debut/fin
            resultat1 = EditStation.date.checkAutoInputValues(Messages.getString(NemoSection.S_DATE));
            if (resultat1){
                String lign = EditStation.date.getLineText(0);
                ligneOK = EditStation.date.checkInDataOrHeader(lign, Messages.getString(NemoSection.S_DATE));
                ok = ok && ligneOK;
            }else{
                checkValue = false;
            }
			ok = ok && resultat1;
        }
        // check value (creer une nouvelle  date)
        if (checkValue){ 
            value = EditStation.date.getValue().replace(' ', '0');
            try {
                DateTimeFormat format = new DateTimeFormat(formatSaisi);
                format.parseObject(value);
            } catch (ParseException e) {
                Utils.messageErreurAppend(Messages.Etape_43
                    + formatSaisi 
                    + Messages.Etape_44
                    + value); 
                ok = ok && false;
                e.printStackTrace();
            }
        }

        
		// PROFONDEUR non-obligatoire
        //value = EditStation.depth.getValue();
        checkValue = true;
        //if (value != null && !value.trim().equals(Global.EMPTY_STRING)){ 
    		if (!EditStation.depth.isManual()) {
                // non-obligatoire
                
                resultat1 = EditStation.depth.checkAutoInputValues(Messages.getString(NemoSection.S_DEPTH)); 
                if (resultat1){
                    String lign = EditStation.depth.getLineText(0);
                    ligneOK = EditStation.depth.checkInDataOrHeader(lign, Messages.getString(NemoSection.S_DEPTH));
                    ok = ok && ligneOK;
                }else{
                    checkValue = false;
                }
    			ok = ok && resultat1;
    		} 
            if (checkValue){
                value = EditStation.depth.getValue();
                if (!value.trim().equals("") && !value.equals("NaN")){                                  //$NON-NLS-1$ //$NON-NLS-2$
                    if (!Pattern.matches("^[0-9]*[0-9][.]?[0-9]*[0-9]*$", value.trim())) {                     //$NON-NLS-1$
                        ok = ok && false;
                        Utils.messageErreurAppend(Messages.Etape_52);
                    }
                }
            }
       // }
        
		// DECALAGE/TU
        checkValue = true;
        if (!EditStation.utConvertion.isManual()) {
            resultat1 = EditStation.utConvertion.checkAutoInputValues(Messages.getString(NemoSection.S_OFFSETTU)); 
            if (resultat1){
                String lign = EditStation.utConvertion.getLineText(0);
                ligneOK = EditStation.utConvertion.checkInDataOrHeader(lign, Messages.getString(NemoSection.S_OFFSETTU));
                ok = ok && ligneOK;
            }else{
                checkValue = false;
            }
            ok = ok && resultat1;
		}
        if (checkValue){
            value = EditStation.utConvertion.getValue();
            if (!value.trim().equals("") && !value.equals("NaN")) {                                     //$NON-NLS-1$ //$NON-NLS-2$
                if (!Pattern.matches("^[+-]?[0-9]*[0-9][.]?[0-9]*$", value.trim())) {                          //$NON-NLS-1$
                    ok = ok && false;
                    Utils.messageErreurAppend(Messages.Etape_119);
                }
            }
        }

        
		// LATITUDE
        formatSaisi = EditStation.latitude.getConverter().getText();
        checkValue = true;
		if (!EditStation.latitude.isManual()) {
			// position dans fichier
            resultat1 = EditStation.latitude.checkAutoInputValues(Messages.getString(NemoSection.S_LAT));
            if (resultat1){
                String lign = EditStation.latitude.getLineText(0);
                ligneOK = EditStation.latitude.checkInDataOrHeader(lign, Messages.getString(NemoSection.S_LAT));
                ok = ok && ligneOK;
            }else{
                checkValue = false;
            }
            ok = ok && resultat1;
		}
        // check value (creer une nouvelle latitude)
        if (checkValue){
            value = EditStation.latitude.getValue(); 
            try {
                LatitudeFormat latitudeFormat = new LatitudeFormat(formatSaisi);
                latitudeFormat.setVariableLength(EditStation.latitude.isVariablePos());
                latitudeFormat.parseObject(value);
                
            } catch (ParseException e) {
                Utils.messageErreurAppend(Messages.Etape_55
                    + formatSaisi 
                    + Messages.Etape_56
                    + value); 
                ok = ok && false;
                e.printStackTrace();
            }
        }

        
		// LONGITUDE
        formatSaisi = EditStation.longitude.getConverter().getText();
        checkValue = true;
        if (!EditStation.longitude.isManual()) {
            // position dans fichier
            resultat1 = EditStation.longitude.checkAutoInputValues(Messages.getString(NemoSection.S_LON));
            if (resultat1){
                String lign = EditStation.longitude.getLineText(0);
                ligneOK = EditStation.longitude.checkInDataOrHeader(lign, Messages.getString(NemoSection.S_LON));
                ok = ok && ligneOK;
            }else{
                checkValue = false;
            }
            ok = ok && resultat1;
        }
        // check value (creer une nouvelle latitude)
        if (checkValue){       
            value = EditStation.longitude.getValue();     
            try {
                LongitudeFormat longitudeFormat = new LongitudeFormat(formatSaisi);
                longitudeFormat.setVariableLength(EditStation.longitude.isVariablePos());
                longitudeFormat.parseObject(value);
                
            } catch (ParseException e) {
                Utils.messageErreurAppend(Messages.Etape_60
                    + formatSaisi 
                    + Messages.Etape_61
                    + value); 
                ok = ok && false;
                e.printStackTrace();
            }
        }

        
		// specifique SERIE TEMPO ou TRAJECTOIRE
		if (ViewFile.radSerieTempo.getSelection()
				|| ViewFile.radTrajectoire.getSelection()) {

			// PROFONDEUR DU CAPTEUR
            checkValue = true;        
            if (!EditStation.sensorDepth.isManual()){
                resultat1 = EditStation.sensorDepth.checkAutoInputValues(Messages.getString(NemoSection.S_SENSORDEPTH)); 
                if (resultat1){
                    String lign = EditStation.sensorDepth.getLineText(0);
                    ligneOK = EditStation.sensorDepth.checkInDataOrHeader(lign, Messages.getString(NemoSection.S_SENSORDEPTH)); 
                    ok = ok && ligneOK;
                }else{
                    checkValue = false;
                }
                ok = ok && resultat1;
            }else{
                value = EditStation.sensorDepth.getValue();
                if (value.trim().equals(Global.EMPTY_STRING)) {
                    checkValue = false;
                    // obligatoire que pour les series temporelles
                    //if (ViewFile.radSerieTempo.getSelection()){
                        ok = ok && false; // FAE 0001232
                        // FAE 1673 : n'afficher le message que si erreur
                        Utils.messageErreurAppend(Messages.Etape_77);
                        Utils.messageErreurAppend(Messages.Etape_79);
                   // }
                }
            }
            if (checkValue){
                value = EditStation.sensorDepth.getValue();
                if (!Pattern.matches("^[0-9]*[0-9][.]?[0-9]*[0-9]*$", value.trim())) {                         //$NON-NLS-1$
                    ok = ok && false;
                    Utils.messageErreurAppend(Messages.Etape_81);
                }
            }

			// FREQUENCE ECHANTILLONNAGE
			// ne tester que si section frequence pas grisee ! 
            checkValue = true;
            if (EditStation.samplingRate.isEnabled()){ 
                if (!EditStation.samplingRate.isManual()){
                    resultat1 = EditStation.samplingRate.checkAutoInputValues(Messages.getString(NemoSection.S_SAMPLINGRATE));
                    if (resultat1){
                        String lign = EditStation.samplingRate.getLineText(0);
                        ligneOK = EditStation.samplingRate.checkInDataOrHeader(lign, Messages.getString(NemoSection.S_SAMPLINGRATE));
                        ok = ok && ligneOK;
                    }else{
                        checkValue = false;
                    }
                    ok = ok && resultat1;
                }
                if (checkValue){        
                    value = EditStation.samplingRate.getValue();
                    
                    if (value.trim().equals(Global.EMPTY_STRING)) {    
                        ok = ok && false; 
                        Utils.messageErreurAppend(Messages.Etape_103a);
                    } else {
                        if (!Pattern.matches("^[0-9]*[0-9][.]?[0-9]*[0-9]*$", value.trim())) {   //$NON-NLS-1$
                            ok = ok && false;
                            Utils.messageErreurAppend(Messages.Etape_103); 
                        }else{
                            Double dblValue = Double.parseDouble(value);
                            if (dblValue.compareTo(new Double("0.0")) == 0){                     //$NON-NLS-1$
                                ok = ok && false;
                                Utils.messageErreurAppend(Messages.Etape_103b);
                            }else{
                                org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
                                parser.addStandardFunctions();
                                parser.addStandardConstants();
                                parser.addVariable(Global.STR_CONVERT, dblValue);
                                parser.parseExpression(Global.STR_CONVERT + EditStation.samplingRate.getConverter().getText().trim());
                        
                                Double resultJEP = parser.getValue();
                                if (resultJEP.isNaN()){
                                    ok = ok && false;
                                    Utils.messageErreurAppend(Messages.Etape_103); 
                                }
                            }
                        }
                    }
                }
            }
		} // fin specifique serie tempo ou trajectoire
        

        
        // specifique SERIE TEMPO
        if (ViewFile.radSerieTempo.getSelection()) {
            
            // LATITUDE DE FIN
            formatSaisi = EditStation.endLatitude.getConverter().getText();
            value = EditStation.endLatitude.getValue();
            checkValue = true;
            if (value != null && !value.trim().equals(Global.EMPTY_STRING) && !value.equals(EditStation.latitude.getValue())){ 
                // vérification du format
                if (formatSaisi == null || formatSaisi.equals(Global.EMPTY_STRING)){
                    formatSaisi = EditStation.latitude.getConverter().getText();
                    EditStation.endLatitude.getConverter().setText(formatSaisi);
                }
                if (!EditStation.endLatitude.isManual()) {
                    // position dans fichier
                    resultat1 = EditStation.endLatitude.checkAutoInputValues(Messages.getString(NemoSection.S_ELAT)); 
                    if (resultat1){
                        String lign = EditStation.endLatitude.getLineText(0);
                        ligneOK = EditStation.endLatitude.checkInDataOrHeader(lign, Messages.getString(NemoSection.S_ELAT));
                        ok = ok && ligneOK;
                    }else{
                        checkValue = false;
                    }
                    ok = ok && resultat1;
                }
                // check value (creer une nouvelle latitude)
                if (checkValue){                    
                    try {
                        LatitudeFormat latitudeFormat = new LatitudeFormat(formatSaisi);
                        latitudeFormat.setVariableLength(EditStation.endLatitude.isVariablePos());
                        latitudeFormat.parseObject(value);
                        
                    } catch (ParseException e) {
                        Utils.messageErreurAppend(Messages.Etape_64);
                        ok = ok && false;
                        e.printStackTrace();
                    }
                }
            }
            

            // LONGITUDE DE FIN
            formatSaisi = EditStation.endLongitude.getConverter().getText();
            value = EditStation.endLongitude.getValue();
            checkValue = true;
            if (value != null && !value.trim().equals(Global.EMPTY_STRING) && !value.equals(EditStation.longitude.getValue())){     
                // vérification du format
                if (formatSaisi == null || formatSaisi.equals(Global.EMPTY_STRING)){
                    formatSaisi = EditStation.longitude.getConverter().getText();
                    EditStation.endLongitude.getConverter().setText(formatSaisi);
                } 
                if (!EditStation.endLongitude.isManual()) {
                    // position dans fichier
                    resultat1 = EditStation.endLongitude.checkAutoInputValues(Messages.getString(NemoSection.S_ELON));
                    if (resultat1){
                        String lign = EditStation.endLongitude.getLineText(0);
                        ligneOK = EditStation.endLongitude.checkInDataOrHeader(lign, Messages.getString(NemoSection.S_ELON));
                        ok = ok && ligneOK;
                    }else{
                        checkValue = false;
                    }
                    ok = ok && resultat1;
                }
                // check value (creer une nouvelle longitude)
                if (checkValue){                    
                    try {
                        LongitudeFormat longitudeFormat = new LongitudeFormat(formatSaisi);
                        longitudeFormat.setVariableLength(EditStation.endLongitude.isVariablePos());
                        longitudeFormat.parseObject(value);
                        
                    } catch (ParseException e) {
                        Utils.messageErreurAppend(Messages.Etape_67);
                        ok = ok && false;
                        e.printStackTrace();
                    }
                }
            }

            // PROFONDEUR DE FIN
            checkValue = true;
            if (!EditStation.endDepth.isManual()){
                resultat1 = EditStation.endDepth.checkAutoInputValues(Messages.getString(NemoSection.S_EDEPTH)); 
                if (resultat1){
                    String lign = EditStation.endDepth.getLineText(0);
                    ligneOK = EditStation.endDepth.checkInDataOrHeader(lign, Messages.getString(NemoSection.S_EDEPTH));
                    ok = ok && ligneOK;
                }else{
                    checkValue = false;
                }
                ok = ok && resultat1;
            }
            if (checkValue){   
                value = EditStation.endDepth.getValue();
                if (!value.trim().equals("") && !value.equals("NaN")){                                  //$NON-NLS-1$ //$NON-NLS-2$
                    if (!Pattern.matches("^[0-9]*[0-9][.]?[0-9]*$", value.trim())) {                           //$NON-NLS-1$
                        ok = ok && false;
                        Utils.messageErreurAppend(Messages.Etape_70);
                    }
                }
            }


            // DECLINAISON MAGNETIQUE
            value = EditStation.magneticDeclin.getValue();
            checkValue = true;
            if (!EditStation.magneticDeclin.isManual()){
                resultat1 = EditStation.magneticDeclin.checkAutoInputValues(Messages.getString(NemoSection.S_MAGNETIC));
                if (resultat1){
                    String lign = EditStation.magneticDeclin.getLineText(0);
                    ligneOK = EditStation.magneticDeclin.checkInDataOrHeader(lign, Messages.getString(NemoSection.S_MAGNETIC));
                    ok = ok && ligneOK;
                }else{
                    checkValue = false;
                }
                ok = ok && resultat1;
            }
            if (checkValue){                
                if (!value.trim().equals("")) {                                                                 //$NON-NLS-1$
                    if (!Pattern.matches("^[+-]{0,1}[0-9]*[0-9][.]?[0-9]*[0-9]*$", value.trim())) {                    //$NON-NLS-1$
                        ok = ok && false;
                        Utils.messageErreurAppend(Messages.Etape_102);
                    }
                }
            }

        } // fin specifique serie tempo

		// Section Flag QC
		boolean flagOK = false;
		for (i = 0; i < size; i++) {
            Shell shell = new Shell();
            String nomSection = Messages.getString(NemoSection.S_QCFLAG) + " - "                            //$NON-NLS-1$
                + EditStation.qcFlags.getTabFlagQC().getItems()[i].getText(1);
            AutoLine line = new AutoLine(shell, 1, true, MetaInData.FACULTATIF, false);
            line.setValue(EditStation.qcFlags.getTabFlagQC().getItems()[i].getText(2),
                    EditStation.qcFlags.getTabFlagQC().getItems()[i].getText(3),
                    EditStation.qcFlags.getTabFlagQC().getItems()[i].getText(4));
            flagOK = line.checkValues(nomSection);
			if (flagOK) {
                String zone = Messages.getString(NemoSection.S_QCFLAG).trim() + " : "                        //$NON-NLS-1$
                    + EditStation.qcFlags.getTabFlagQC().getItems()[i].getText(1);
                ligneOK = line.checkInDataOrHeader(line.getNumberLine().getText(), zone);
				ok = ok && ligneOK;
			} else {
				ok = ok && false;
			}
		}

		if (ok) {
			EditStation.okApresReset = true;
			okStation();
		} else {
			ViewFile.tabItem2.setText(Messages.onglet_station_error);
			ViewFile.etape = 2;
		}
	}

	public static void validationData() {
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
        
        String fmOut= new String();
        //Formatter formatter = new Formatter();


		if (nbreParam == 0) {
			ok = false;
			Utils.messageErreurAppend(Messages.Etape_117);
		} else {

			debuts = new int[nbreParam];
			fins = new int[nbreParam];
			debutsFlag = new String[nbreParam];
			finsFlag = new String[nbreParam];

			// remplissage des listes
			for (int i = 0; i < nbreParam; i++) {
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
                    if (indexSign == -1){
                        index = formatString.indexOf('%') + 1;
                    }else{
                        index = indexSign + 1;
                    }
                    int totLength = Integer.parseInt(formatString.substring(index, index + 1));
                    
                    
                    // Float ou Decimal ?
                    String type = formatString.substring(formatString.length() - 1);
                    if (type.equals("f") || type.equals("F")){       //$NON-NLS-1$ //$NON-NLS-2$
                        fmOut = String.format(java.util.Locale.US, formatString, Float.parseFloat(typeCourant.getSortie()));
                    }else if (type.equals("d") || type.equals("D")){ //$NON-NLS-1$ //$NON-NLS-2$
                        fmOut = String.format(java.util.Locale.US, formatString, Integer.parseInt(typeCourant.getSortie()));
                    }
                    
                    if((fmOut.equals(typeCourant.getSortie())) 
                            && (typeCourant.getSortie().length() == totLength))
                    {
    					// ok
    					ok = ok && true;
    
    				} else {
    					// erreur
    					ok = ok && false;
    					Utils.messageErreurAppend(Messages.Etape_106+ " " + typeCourant.getCode() + " : " + Messages.Etape_106b); //$NON-NLS-1$ //$NON-NLS-2$
    				}
                } catch (Exception e)
                {
                    // erreur
                    ok = ok && false;
                    Utils.messageErreurAppend(Messages.Etape_106+ " " + typeCourant.getCode() + " : " + Messages.Etape_106b); //$NON-NLS-1$ //$NON-NLS-2$
                    System.out.print(e.getMessage());
                }

				// check des valeurs debut<fin
				if (typeCourant.getDebut() > typeCourant.getFin()) {
					Utils
							.messageErreurAppend(Messages.Etape_98 + Global.SPACE + typeCourant.getCode()); 
					ok = false;
				}
				
//				 check des valeurs debut<fin flag
				if (!typeCourant.getDebutFlag().equals(Global.EMPTY_STRING) && !typeCourant.getFinFlag().equals(Global.EMPTY_STRING)) {
					if (Integer.parseInt(typeCourant.getDebutFlag()) > Integer.parseInt(typeCourant.getFinFlag())) {
						Utils
								.messageErreurAppend(Messages.Etape_125 + Global.SPACE + typeCourant.getCode());
						ok = false;
					}
				}
			}

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

			// TEST pour savoir si PRES est bien en premier mais pas pour
			// profile ou trajectoire
			TypeData premierType = TableData.types.get(0);

			// split sur " - "
			Pattern p = Pattern.compile(Global.SEP);
			String[] mots = p.split(premierType.getCode());
			String code = mots[0];
            
            EditConversion.setWarningData(Global.EMPTY_STRING);
			if (ViewFile.typeFichierEntree().equals(Global.PROFILE)) {
				if (!(code.equals(Intern.paramPRES) || code.equals(Intern.paramDEPH))) {
					// PRES ou DEPH n'est pas en premier
					Utils.messageErreurAppend(Messages.error_data_firstPRES);
					ok = ok && false;
				} else {
					ok = ok && true;
				}

				// Si PRES n'est pas premier => Medatlas impossible
				if (!ViewFile.isCollection() && !code.equals(Intern.paramPRES)) { 
                    EditConversion.setWarningData(Messages.ViewLog_7);
                }else{
                    EditConversion.setWarningData(Global.EMPTY_STRING);
                }
            }else if (ViewFile.typeFichierEntree().equals(Global.TRAJECTORY)){
                if (!code.equals(Intern.paramDEPH) && existDEPH) { 
                    // DEPH existe et n'est pas en premier
                    Utils.messageErreurAppend(Messages.error_data_firstDEPH_TRAJ);
                    ok = ok && false;
                }
            }
		}
		if (ok) {
			EditData.okApresReset = true;
			okData();
		}else{
            ViewFile.tabItem3.setText(Messages.onglet_data_error);
            ViewFile.etape = 3;
        }
	}

	private static void okDesc() {
        // FAE1386 : desactiver "One File per station" si Trajectory
        if (ViewFile.radTrajectoire.getSelection()){
            ViewFile.radMonoStation.setSelection(false);
            ViewFile.radMonoStation.setEnabled(false);
            ViewFile.radMonoFichier.setEnabled(true);
            ViewFile.radMonoFichier.setSelection(true);
        }
        
		// envoi des valeurs dans le modele
		// attention aux boutons radio

		// puis remplissage du modele avec les valeurs de l'IHM
		DataModel.initModele(ViewFile.dossierCampagne, ViewFile
				.typeFichierEntree(), ViewFile.nomFichier);

		Utils.messageOK(Messages.success_etape);

		// on rajoute "ok" dans l'onglet description
		ViewFile.tabItem.setText(Messages.onglet_file_valid);

		// on valide l'etape 1
		EditDesc.validee = true;

		try {
			// FAE 022
			if (ViewFile.isCollection()) {
				EditSurvey.okApresReset = true;
				EditSurvey.validee = true;

				DataModel.initSurvey();
				ViewFile.etape = 2;
			} else {
				ViewFile.etape = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void okSurvey() {

		DataModel.initSurvey();
		if (!ViewFile.tabItem1.isDisposed()) {
			ViewFile.tabItem1.setText(Messages.onglet_cruise_valid);
		}
		Utils.messageOK(Messages.success_etape);
        if (!EditConversion.getWarningCampagne().equals(Global.EMPTY_STRING)){
            Utils.messageOKAppend(Messages.ViewLog_8);
            Utils.messageOKAppend(EditConversion.getWarningCampagne());
        }

		// validation etape 2
		ViewFile.etape = 2;
		// envoi des valeurs dans le modele

	}

	private static void okStation(){

		// envoi des valeurs dans le modele
		Utils.messageOK(Messages.success_etape);
        if (!EditConversion.getWarningStation().equals(Global.EMPTY_STRING)){
            Utils.messageOKAppend(Messages.ViewLog_8);
            Utils.messageOKAppend(EditConversion.getWarningStation());
        }
		DataModel.initStation();
		ViewFile.tabItem2.setText(Messages.onglet_station_valid); 
		ViewFile.etape = 3;
	}

	private static void okData() {
		Utils.messageOK(Messages.success_etape);
		DataModel.initData();
		ViewFile.tabItem3.setText(Messages.onglet_data_valid);
		ViewFile.etape = 4;
		// envoi des valeurs dans le modele

	}

}
