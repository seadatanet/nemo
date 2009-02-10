package fr.ifremer.nemo.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.ui.object.ComboType;
import fr.ifremer.nemo.ui.object.QCflagsSection;
import fr.ifremer.nemo.ui.object.StationSection;
import fr.ifremer.nemo.ui.object.NemoSection;
import fr.ifremer.nemo.ui.object.AutoLine.MetaInData;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditStation {
	// fonctions de remplissage de la zone d'édition pour les campagnes
	// /////////////////////////////////////////

	private static Shell shell = new Shell();

	public static Button buttonValidate, buttonRAZ, buttonExpand;
	public static Boolean okApresReset;
	public static boolean expand, validee, loaded;
	public static ScrolledForm formEdition;
    public static ArrayList<NemoSection> listeSectionsStation;

    public static StationSection stationNumber, dataType, historique, 
                                 comments, surfaceSample, utConvertion, 
                                 time, date, latitude, endLatitude, 
                                 longitude, endLongitude, depth, endDepth, 
                                 sensorDepth, magneticDeclin, samplingRate;   

    public static QCflagsSection qcFlags;  

	public static void affichageEdition() {
		// initialisation zone édition pour étape description de fichier
		formEdition = ViewEdit.formEdition;
		ViewLog.aideStation();
		// raz message erreur
		Utils.messageErreur(""); //$NON-NLS-1$
		okApresReset = true;

		// affichage du texte dans la zone de gauche
		ViewFile.styledTextStation.setText(Utils.texteBrut);
		loaded = true;

		// de base les sections sont refermees
		expand = false;

		/*
		 * recreer un formEdition sur le toolkit principal pour pouvoir faire un
		 * dispose() dessus mais garder toutes les sections afin de les
		 * réafficher par la suite dans DrawingBack.
		 */

		ViewEdit.formEdition.getBody().setLayout(new GridLayout());
		ViewEdit.formEdition.setText(Messages.getString("EditStation.1")); //$NON-NLS-1$

		disposeElements();        
        
		// init des objets
		listeSectionsStation = new ArrayList<NemoSection>();
        initBoutons();
        initStationSections();     

        // remplissage de l'IHM par le modèle chargé
        if (DataModel.ouverture) {
            DataModel.fillStation();
        } 

		formEdition.redraw();
		formEdition.reflow(true);

		// bouton validate
		buttonValidate.setParent(ViewEdit.formEdition.getBody());
		buttonValidate.setText(Messages.getString("EditStation.4")); //$NON-NLS-1$
        buttonValidate
        .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {

                // on passe en ppar l'étape à laquelle on se trouve
                // Etape.etape(2);

                Etape.validationStation();

                formEdition.redraw();
                formEdition.reflow(true);

            }
        });

		// bouton reset
		buttonRAZ.setParent(ViewEdit.formEdition.getBody());
		buttonRAZ.setText("Reset"); //$NON-NLS-1$
        buttonRAZ
        .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent e) {

                for (NemoSection stationSection : listeSectionsStation){
                    stationSection.razValues();
                }
                
                okApresReset = false;
                ViewFile.tabItem2.setText(Messages.getString("EditStation.1")); //$NON-NLS-1$
                EditData.okApresReset = false;
                ViewFile.tabItem3.setText(Messages.getString("EditData.1")); //$NON-NLS-1$
                Utils.messageErreur(Global.EMPTY_STRING);

            }
        });

        // bouton collapse all / expand all
		buttonExpand.setParent(ViewEdit.formEdition.getBody());
		buttonExpand.setText("+"); //$NON-NLS-1$
        buttonExpand
        .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent e) {
                
                for (NemoSection stationSection : listeSectionsStation){
                    // seule les section accessibles s'ouvrent
                    if (!expand && stationSection.isEnabled()){
                        stationSection.setExpanded(true);
                    }else{
                        stationSection.setExpanded(false);
                    }
                }
                
                if (!expand) {
                    // expand passe true
                    buttonExpand.setText("-"); //$NON-NLS-1$
                    expand = true;
                } else {
                    expand = false;
                    // expand passe false
                    buttonExpand.setText("+"); //$NON-NLS-1$
                }

                // refresh global
                formEdition.reflow(true);

            }
        });

		if (ViewFile.isCollection()) {
			TableDataSurvey.initTypes(); 
		}

		
        // Draw Station Sections          
        for (NemoSection stationSection : listeSectionsStation){
            stationSection.initSection(EditStation.formEdition);
            stationSection.draw(EditStation.formEdition.getBody());
        }
        
        actualiseEdition();

		ViewEdit.formEdition.reflow(true);

		// raz du message d'erreurs
		Utils.messageErreur(Global.EMPTY_STRING); 

		// refresh du formulaire
		formEdition.reflow(true);

		// affichage courant
		ViewFile.affiche = 2;
	}

    private static void initBoutons() {
        buttonRAZ = new Button(shell, SWT.NONE);
        buttonExpand = new Button(shell, SWT.NONE);
        buttonValidate = new Button(shell, SWT.NONE);
    }
    
    private static void initStationSections(){
        stationNumber = new StationSection(shell, NemoSection.S_STATIONNUMBER, Messages.getString(NemoSection.S_STATIONNUMBER), Messages.getString("EditStation.6"), true, true, 1);     //$NON-NLS-1$ 
        stationNumber.addManualInput(shell,null, ComboType.NONE, "1");                                                                  //$NON-NLS-1$ 
        if (!ViewFile.radTrajectoire.getSelection()){
            stationNumber.getManualInput().setNbColumns(2);
            stationNumber.getManualInput().setLabelButton(Messages.getString("Drawing.6"));                                             //$NON-NLS-1$ 
        }                                                                  
        stationNumber.addAutoInput(shell,Messages.getString("Drawing.8"), false, MetaInData.FACULTATIF, false);                                //$NON-NLS-1$
        listeSectionsStation.add(stationNumber);
        
        if (!ViewFile.isCollection()) {
            dataType = new StationSection(shell, NemoSection.S_DATATYPE, Messages.getString(NemoSection.S_DATATYPE), Messages.getString("Drawing.13"), true, false, 1);   //$NON-NLS-1$
            dataType.addCouple(shell, ComboType.DATATYPE, Global.EMPTY_STRING);
            listeSectionsStation.add(dataType);
        }
        
        historique = new StationSection(shell, NemoSection.S_DCHIST, Messages.getString(NemoSection.S_DCHIST), Messages.getString("EditStation.8"), true, false, 2);       //$NON-NLS-1$ 
        historique.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
        historique.addAutoInput(shell, null, false, MetaInData.INTERDIT, true);
        listeSectionsStation.add(historique);
        
        comments = new StationSection(shell, NemoSection.S_COMMENT, Messages.getString(NemoSection.S_COMMENT), Messages.getString("EditStation.10"), true, false, 1);        //$NON-NLS-1$
        comments.addManualInput(shell, null, ComboType.BLOCK, Global.EMPTY_STRING);
        comments.addAutoInput(shell, null, true, MetaInData.INTERDIT, true);
        listeSectionsStation.add(comments);
        
        surfaceSample = new StationSection(shell, NemoSection.S_SURFACE, Messages.getString(NemoSection.S_SURFACE), Messages.getString("EditStation.12"), true, false, 2);  //$NON-NLS-1$
        surfaceSample.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
        surfaceSample.addAutoInput(shell, null, false, MetaInData.INTERDIT, true);
        listeSectionsStation.add(surfaceSample);
        
        utConvertion = new StationSection(shell, NemoSection.S_OFFSETTU, Messages.getString(NemoSection.S_OFFSETTU), Global.EMPTY_STRING, true, false, 1);  
        utConvertion.addManualInput(shell, null, ComboType.NONE, "0.0");                                                                  //$NON-NLS-1$
        utConvertion.addAutoInput(shell, null, false, MetaInData.INTERDIT, false);
        listeSectionsStation.add(utConvertion);
        
        time = new StationSection(shell, NemoSection.S_TIME, Messages.getString(NemoSection.S_TIME), Messages.getString("EditStation.14"),  false, true, 1);          //$NON-NLS-1$ 
        time.addConverter(shell, ComboType.TIME, Global.EMPTY_STRING);
        time.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
        time.addAutoInput(shell, null, false, MetaInData.FACULTATIF, false);
        listeSectionsStation.add(time);
        
        date = new StationSection(shell, NemoSection.S_DATE, Messages.getString(NemoSection.S_DATE), Messages.getString("EditStation.16"), false, true, 1);           //$NON-NLS-1$  
        date.addConverter(shell, ComboType.DATE, Global.EMPTY_STRING);
        date.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
        date.addAutoInput(shell, null, false, MetaInData.FACULTATIF, false);
        listeSectionsStation.add(date);
        
        latitude = new StationSection(shell, NemoSection.S_LAT, Messages.getString(NemoSection.S_LAT), Messages.getString("EditStation.20"), false, true, 1);       //$NON-NLS-1$  
        latitude.addConverter(shell, ComboType.LAT, Global.EMPTY_STRING);
        latitude.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
        latitude.addAutoInput(shell, null, true, MetaInData.FACULTATIF, false);
        // in trajectory, latitude must be in data
        if (ViewFile.radTrajectoire.getSelection()){
            latitude.setEnableInData(MetaInData.OBLIGATOIRE);
            latitude.setEnabledManual(false);
        }
        listeSectionsStation.add(latitude);
        
        if (ViewFile.radSerieTempo.getSelection()){
            endLatitude = new StationSection(shell, NemoSection.S_ELAT, Messages.getString(NemoSection.S_ELAT), Global.EMPTY_STRING, false, true, 1); 
            endLatitude.addConverter(shell, ComboType.LAT, Global.EMPTY_STRING);
            endLatitude.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
            endLatitude.addAutoInput(shell, null, true, MetaInData.INTERDIT, false);
            endLatitude.setTimeSeriesInfo(true);
            listeSectionsStation.add(endLatitude);
        }
            
        
        longitude = new StationSection(shell, NemoSection.S_LON, Messages.getString(NemoSection.S_LON), Messages.getString("EditStation.24"), false, true, 1);      //$NON-NLS-1$
        longitude.addConverter(shell, ComboType.LON, Global.EMPTY_STRING);
        longitude.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
        longitude.addAutoInput(shell, null, true, MetaInData.FACULTATIF, false);
        // in trajectory, longitude must be in data
        if (ViewFile.radTrajectoire.getSelection()){
            longitude.setEnableInData(MetaInData.OBLIGATOIRE);
            longitude.getManualInput().setGrpEnabled(false);
        }
        listeSectionsStation.add(longitude);
        
        if (ViewFile.radSerieTempo.getSelection()){
            endLongitude = new StationSection(shell, NemoSection.S_ELON, Messages.getString(NemoSection.S_ELON), Global.EMPTY_STRING, false, true, 1);     
            endLongitude.addConverter(shell, ComboType.LON, Global.EMPTY_STRING);
            endLongitude.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
            endLongitude.addAutoInput(shell, null, true, MetaInData.INTERDIT, false);
            endLongitude.setTimeSeriesInfo(true);
            listeSectionsStation.add(endLongitude);
        }
        
        // FAE 1129 : ajout TEST / position variable à DEPTH
        depth = new StationSection(shell, NemoSection.S_DEPTH, Messages.getString(NemoSection.S_DEPTH), Messages.getString("EditStation.28"), true, true, 1);           //$NON-NLS-1$
        depth.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
        depth.addAutoInput(shell, null, true, MetaInData.FACULTATIF, false);
        // in trajectory, depth must be in data
        if (ViewFile.radTrajectoire.getSelection()){
            depth.setEnableInData(MetaInData.OBLIGATOIRE);
            depth.setEnabledManual(false);
        }
        listeSectionsStation.add(depth);
        
        if (!ViewFile.radProfilVertical.getSelection()){
            if (ViewFile.radSerieTempo.getSelection()){
                endDepth = new StationSection(shell, NemoSection.S_EDEPTH, Messages.getString(NemoSection.S_EDEPTH), Global.EMPTY_STRING,  true, false, 1);
                endDepth.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
                endDepth.addAutoInput(shell, null, false, MetaInData.INTERDIT, false);
                endDepth.setTimeSeriesInfo(true);
                listeSectionsStation.add(endDepth);
            }
            
            sensorDepth = new StationSection(shell, NemoSection.S_SENSORDEPTH, Messages.getString(NemoSection.S_SENSORDEPTH), Messages.getString("EditStation.32"), true, false, 1);     //$NON-NLS-1$
            sensorDepth.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
            sensorDepth.addAutoInput(shell, null, false, MetaInData.FACULTATIF, false);
            sensorDepth.setTimeSeriesInfo(true);
            listeSectionsStation.add(sensorDepth);
            
            if (ViewFile.radSerieTempo.getSelection()){
                magneticDeclin = new StationSection(shell, NemoSection.S_MAGNETIC, Messages.getString(NemoSection.S_MAGNETIC), Messages.getString("EditStation.34"), true, false, 1);   //$NON-NLS-1$
                magneticDeclin.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
                magneticDeclin.addAutoInput(shell, null, false, MetaInData.INTERDIT, false);
                magneticDeclin.setTimeSeriesInfo(true);
                listeSectionsStation.add(magneticDeclin);
            }
            
            samplingRate = new StationSection(shell, NemoSection.S_SAMPLINGRATE, Messages.getString(NemoSection.S_SAMPLINGRATE), Messages.getString("EditStation.36"), true, true, 1);     //$NON-NLS-1$ 
            samplingRate.addConverter(shell, ComboType.FRQ, "*1");                                                                                //$NON-NLS-1$
            samplingRate.addManualInput(shell, null, ComboType.NONE, "0");                                                                     //$NON-NLS-1$
            samplingRate.addAutoInput(shell, null, false, MetaInData.INTERDIT, false);
            samplingRate.setTimeSeriesInfo(true);
            listeSectionsStation.add(samplingRate);
        }
        qcFlags = new QCflagsSection(shell, NemoSection.S_QCFLAG, Messages.getString(NemoSection.S_QCFLAG), Messages.getString("EditStation.18"));         //$NON-NLS-1$
        listeSectionsStation.add(qcFlags);
    }
    
    public static void actualiseEdition(){
        // pour trajectoires et time-series
        if (ViewFile.radTrajectoire.getSelection() || ViewFile.radSerieTempo.getSelection()){
            // si Date est saisie dans les données
            boolean dateInData = date.getAutoInput().getLines().get(0).isInData();
            // si Time est saisie dans les données
            boolean timeInData = time.getAutoInput().getLines().get(0).isInData();
            // => Sampling Rate : disable (car pas nécessaire pour le calcul)
            samplingRate.setEnabled(!(dateInData || timeInData));
            date.setEnabledManual(!(dateInData || timeInData));
            time.setEnabledManual(!(dateInData || timeInData));            
        }

        // pour trajectoires
        if (ViewFile.radTrajectoire.getSelection()){
            // si Depth est saisie dans les données,
            boolean depthInData = depth.isInData();
            // => EndDepth : disable (car renseigné automatiquement par NEMO)
            endDepth.setEnabled(!depthInData);
            depth.setEnabledManual(!depthInData);
        }
        
        // TODO griser line si vide pour toutes les stationSection
        
        
        
    }

	public static void DrawingBack() {
		// reset de toute la zone d'édition après étape descriptioon de fichier
		// on retire toutes les sections et les boutons expand et validation

		ViewLog.aideStation();

		ScrolledForm s;
		s = ViewEdit.formEdition;
		// raz message erreur
		Utils.messageErreur(Global.EMPTY_STRING);

		buttonValidate.setParent(s.getBody());
		buttonRAZ.setParent(s.getBody());
		buttonExpand.setParent(s.getBody());

        for (NemoSection stationSection : listeSectionsStation){
            stationSection.setExpanded(false);
            stationSection.initSection(s.getBody());
        }

		if (DataModel.station_modele != null) {
			if (DataModel.station_modele.getDataType() != null) {
				Utils.selectComboCode(dataType.getDatatype().getValueCombo(),
						DataModel.station_modele.getDataType().getCode());
			}
		} else if (DataModel.station != null) {
			if (DataModel.station.getDataType() != null) {
				Utils.selectComboCode(dataType.getDatatype().getValueCombo(),
						DataModel.station.getDataType().getCode());
			}
		}


		ViewEdit.formEdition.setText(Messages.getString("EditStation.51")); //$NON-NLS-1$

		// refresh global
		ViewEdit.formEdition.reflow(true);

		// affichage courant
		ViewFile.affiche = 2;
	}




    public static void razEdition() {
        // reset de toute la zone d'édition après étape descriptioon de fichier
        // on retire toutes les sections et les boutons expand et validation

       // effacement des boutons valider/expand/init serveur
        buttonExpand.setParent(shell);
        buttonValidate.setParent(shell);
        buttonRAZ.setParent(shell);

        for (NemoSection stationSection : listeSectionsStation){
            stationSection.setExpanded(true);
            stationSection.initSection(shell);
        }

        // titre à vide
        ViewEdit.formEdition.setText(Global.EMPTY_STRING);

        // refresh global
        ViewEdit.formEdition.reflow(true);

    }

	private static void disposeElements() {

		shell.close();

		shell = new Shell();
        if (listeSectionsStation != null){            
            for (NemoSection stationSection : listeSectionsStation){
                stationSection.dispose();
            }
        }
	}

    public static boolean someInData() {
        boolean inData = false;
        for (NemoSection stationSection : listeSectionsStation)
        {
            if (stationSection instanceof StationSection 
                    && ((StationSection) stationSection).isInData())
            {
                inData = true;
            }
        }
        return inData;
    }

    
    
//	public static void test() {
//		int i;
//
//		for (i = 0; i <= EditData.listeSectionsData.size() - 1; i++) {
//
//			EditData.listeSectionsData.get(i).setEnabled(false); // Parent(s);
//
//		}
//
//		// refresh global
//		ViewEdit.formEdition.reflow(true);
//	}
//
//	public static void test2() {
//		int i;
//
//		for (i = 0; i <= EditData.listeSectionsData.size() - 1; i++) {
//
//			EditData.listeSectionsData.get(i).setEnabled(true); // Parent(s);
//
//		}
//
//		// refresh global
//		ViewEdit.formEdition.reflow(true);
//	}


//    public static void initTypes() {
//
//        comboRefStationType.removeAll();
//
//        // remplissage avec les dataTypes de la campagne
//
//        int taille = TableDataSurvey.types.size();
//        for (int i = 0; i < taille; i++) {
//            // on n'ajoute que les sensors
//            if (TableDataSurvey.types.get(i).getSensor()) {
//                comboRefStationType.add(TableDataSurvey.types.get(i).getCode());
//            }
//        }
//    }
    
}
