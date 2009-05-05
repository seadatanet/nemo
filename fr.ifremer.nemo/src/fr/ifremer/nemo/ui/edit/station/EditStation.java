package fr.ifremer.nemo.ui.edit.station;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.edit.Etape;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.ui.edit.station.AutoLine.MetaInData;
import fr.ifremer.nemo.ui.edit.survey.TableDataSurvey;
import fr.ifremer.nemo.ui.view.ViewEdit;
import fr.ifremer.nemo.ui.view.ViewFile;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditStation {
	// fonctions de remplissage de la zone d'édition pour les campagnes
	// /////////////////////////////////////////

	private static Shell shell = new Shell();

	public static Button buttonValidate, buttonRAZ, buttonExpand;
	public static Boolean okApresReset;
	public static boolean expand, validee, loaded, filled;
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

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING); 
        
		okApresReset = true;

		// affichage du texte dans la zone de gauche
		ViewFile.styledTextStation.setText(Utils.texteBrut);

        // affichage courant
        ViewFile.affiche = 2;
		loaded = true;
        filled = false;

		// de base les sections sont refermees
		expand = false;

		/*
		 * recreer un formEdition sur le toolkit principal pour pouvoir faire un
		 * dispose() dessus mais garder toutes les sections afin de les
		 * réafficher par la suite dans DrawingBack.
		 */

		ViewEdit.formEdition.getBody().setLayout(new GridLayout());
		ViewEdit.formEdition.setText(Messages.edit_station_title);
        
        shell.close();
        shell = new Shell();
		disposeElements();        
        
		// init des objets
		listeSectionsStation = new ArrayList<NemoSection>();
        initBoutons();
        initStationSections();     

        // remplissage de l'IHM par le modèle chargé
        if (DataModel.stationModel != null 
                && DataModel.stationModel.getModel().getDataType() != null){
            DataModel.fillBackStation();
            filled = true;
        }else if (DataModel.ouverture) {
            DataModel.fillStation();
            filled = true;
        } 

		formEdition.redraw();
		formEdition.reflow(true);

		// bouton validate
		buttonValidate.setParent(ViewEdit.formEdition.getBody());
		buttonValidate.setText(Messages.button_validateStep);
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
		buttonRAZ.setText(Messages.button_reset);
        buttonRAZ
        .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent e) {
                razText();
            }
        });

        // bouton collapse all / expand all
		buttonExpand.setParent(ViewEdit.formEdition.getBody());
		buttonExpand.setText(Global.PLUS);
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
                    buttonExpand.setText(Global.MOINS); 
                    expand = true;
                } else {
                    expand = false;
                    // expand passe false
                    buttonExpand.setText(Global.PLUS); 
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

		// refresh du formulaire
		formEdition.reflow(true);

	}

    private static void initBoutons() {
        buttonRAZ = new Button(shell, SWT.NONE);
        buttonExpand = new Button(shell, SWT.NONE);
        buttonValidate = new Button(shell, SWT.NONE);
    }
    
    private static void initStationSections(){
        stationNumber = new StationSection(shell, NemoSection.S_STATIONNUMBER, 
                                           Messages.getString(NemoSection.S_STATIONNUMBER), 
                                           Messages.edit_station_sectionReference_lib, true, true, 1);
        stationNumber.addManualInput(shell,null, ComboType.NONE, "1");                                                                  //$NON-NLS-1$ 
        if (!ViewFile.radTrajectoire.getSelection()){
            stationNumber.getManualInput().setNbColumns(2);
            stationNumber.getManualInput().setLabelButton(Messages.edit_station_refIncrement);
        }                                                                  
        stationNumber.addAutoInput(shell,Messages.label_auto_02, false, MetaInData.FACULTATIF, false);
        listeSectionsStation.add(stationNumber);
        
        if (!ViewFile.isCollection()) {
            dataType = new StationSection(shell, NemoSection.S_DATATYPE, 
                                          Messages.getString(NemoSection.S_DATATYPE), 
                                          Messages.edit_station_sectionDataType_lib, true, false, 1); 
            dataType.addCouple(shell, ComboType.DATATYPE, Global.EMPTY_STRING);
            listeSectionsStation.add(dataType);
        }
        
        historique = new StationSection(shell, NemoSection.S_DCHIST, 
                                        Messages.getString(NemoSection.S_DCHIST), 
                                        Messages.edit_station_sectionHistory_lib, true, false, 2);
        historique.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
        historique.addAutoInput(shell, null, false, MetaInData.INTERDIT, true);
        listeSectionsStation.add(historique);
        
        comments = new StationSection(shell, NemoSection.S_COMMENT, 
                                      Messages.getString(NemoSection.S_COMMENT), 
                                      Messages.edit_station_sectionComment_lib, true, false, 1);
        comments.addManualInput(shell, null, ComboType.BLOCK, Global.EMPTY_STRING);
        comments.addAutoInput(shell, null, true, MetaInData.INTERDIT, true);
        listeSectionsStation.add(comments);
        
        surfaceSample = new StationSection(shell, NemoSection.S_SURFACE, 
                                           Messages.getString(NemoSection.S_SURFACE), 
                                           Messages.edit_station_sectionSurface_lib, true, false, 2);
        surfaceSample.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
        surfaceSample.addAutoInput(shell, null, false, MetaInData.INTERDIT, true);
        listeSectionsStation.add(surfaceSample);
        
        utConvertion = new StationSection(shell, NemoSection.S_OFFSETTU, 
                                          Messages.getString(NemoSection.S_OFFSETTU), 
                                          Global.EMPTY_STRING, true, false, 1);  
        utConvertion.addManualInput(shell, null, ComboType.NONE, "0.0");                                                                  //$NON-NLS-1$
        utConvertion.addAutoInput(shell, null, false, MetaInData.INTERDIT, false);
        listeSectionsStation.add(utConvertion);
        
        time = new StationSection(shell, NemoSection.S_TIME, 
                                  Messages.getString(NemoSection.S_TIME), 
                                  Messages.edit_station_sectionTime_lib,  false, true, 1);  
        time.addConverter(shell, ComboType.TIME, Global.EMPTY_STRING);
        time.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
        time.addAutoInput(shell, null, false, MetaInData.FACULTATIF, false);
        listeSectionsStation.add(time);
        
        date = new StationSection(shell, NemoSection.S_DATE, 
                                  Messages.getString(NemoSection.S_DATE), 
                                  Messages.edit_station_sectionDate_lib, false, true, 1);   
        date.addConverter(shell, ComboType.DATE, Global.EMPTY_STRING);
        date.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
        date.addAutoInput(shell, null, false, MetaInData.FACULTATIF, false);
        listeSectionsStation.add(date);
        
        latitude = new StationSection(shell, NemoSection.S_LAT, 
                                      Messages.getString(NemoSection.S_LAT), 
                                      Messages.edit_station_sectionLat_lib, false, true, 1);
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
            endLatitude = new StationSection(shell, NemoSection.S_ELAT, 
                                             Messages.getString(NemoSection.S_ELAT), 
                                             Global.EMPTY_STRING, false, true, 1); 
            endLatitude.addConverter(shell, ComboType.LAT, Global.EMPTY_STRING);
            endLatitude.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
            endLatitude.addAutoInput(shell, null, true, MetaInData.INTERDIT, false);
            endLatitude.setTimeSeriesInfo(true);
            listeSectionsStation.add(endLatitude);
        }
            
        
        longitude = new StationSection(shell, NemoSection.S_LON, 
                                       Messages.getString(NemoSection.S_LON), 
                                       Messages.edit_station_sectionLon_lib, false, true, 1); 
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
            endLongitude = new StationSection(shell, NemoSection.S_ELON, 
                                              Messages.getString(NemoSection.S_ELON), 
                                              Global.EMPTY_STRING, false, true, 1);     
            endLongitude.addConverter(shell, ComboType.LON, Global.EMPTY_STRING);
            endLongitude.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
            endLongitude.addAutoInput(shell, null, true, MetaInData.INTERDIT, false);
            endLongitude.setTimeSeriesInfo(true);
            listeSectionsStation.add(endLongitude);
        }
        
        // FAE 1129 : ajout TEST / position variable à DEPTH
        depth = new StationSection(shell, NemoSection.S_DEPTH, 
                                   Messages.getString(NemoSection.S_DEPTH), 
                                   Messages.edit_station_sectionDepth_lib, true, true, 1); 
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
                endDepth = new StationSection(shell, NemoSection.S_EDEPTH, 
                                              Messages.getString(NemoSection.S_EDEPTH), 
                                              Global.EMPTY_STRING,  true, true, 1);
                endDepth.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
                endDepth.addAutoInput(shell, null, true, MetaInData.INTERDIT, false);
                endDepth.setTimeSeriesInfo(true);
                listeSectionsStation.add(endDepth);
            }
            
            sensorDepth = new StationSection(shell, NemoSection.S_SENSORDEPTH, 
                                             Messages.getString(NemoSection.S_SENSORDEPTH), 
                                             Messages.edit_station_sectionSensorDepth_lib, true, false, 1); 
            sensorDepth.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
            sensorDepth.addAutoInput(shell, null, false, MetaInData.FACULTATIF, false);
            sensorDepth.setTimeSeriesInfo(true);
            listeSectionsStation.add(sensorDepth);
            
            if (ViewFile.radSerieTempo.getSelection()){
                magneticDeclin = new StationSection(shell, NemoSection.S_MAGNETIC, 
                                                    Messages.getString(NemoSection.S_MAGNETIC), 
                                                    Messages.edit_station_sectionMagn_lib, true, false, 1);
                magneticDeclin.addManualInput(shell, null, ComboType.NONE, Global.EMPTY_STRING);
                magneticDeclin.addAutoInput(shell, null, false, MetaInData.INTERDIT, false);
                magneticDeclin.setTimeSeriesInfo(true);
                listeSectionsStation.add(magneticDeclin);
            }
            
            samplingRate = new StationSection(shell, NemoSection.S_SAMPLINGRATE, 
                                              Messages.getString(NemoSection.S_SAMPLINGRATE), 
                                              Messages.edit_station_sectionSampling_lib, true, true, 1);
            samplingRate.addConverter(shell, ComboType.FRQ, "*1");                                                                                //$NON-NLS-1$
            samplingRate.addManualInput(shell, null, ComboType.NONE, "0");                                                                     //$NON-NLS-1$
            samplingRate.addAutoInput(shell, null, false, MetaInData.INTERDIT, false);
            samplingRate.setTimeSeriesInfo(true);
            listeSectionsStation.add(samplingRate);
        }
        qcFlags = new QCflagsSection(shell, NemoSection.S_QCFLAG, 
                                     Messages.getString(NemoSection.S_QCFLAG), 
                                     Messages.edit_station_sectionQCflags_lib);
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
        
        for (NemoSection section : listeSectionsStation){
            if (section instanceof StationSection)
            {
                ((StationSection) section).diseableLineInData();
                
            }
        }
        
        
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

		ViewEdit.formEdition.setText(Messages.edit_station_title);

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
    
    protected static void razText(){

        for (NemoSection stationSection : listeSectionsStation){
            stationSection.razValues();
        }
        
        Utils.invalidEtapes();
    }

	private static void disposeElements() {

        if (listeSectionsStation != null){            
            for (NemoSection stationSection : listeSectionsStation){
                stationSection.dispose();
            }
        }
        if (buttonRAZ != null) buttonRAZ.dispose();
        if (buttonExpand != null) buttonExpand.dispose();
        if (buttonValidate != null) buttonValidate.dispose();
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

    public static void invalidEtape(){
        okApresReset = false;
        validee = false;
        if (ViewFile.affiche != 2 && loaded){
            loaded = false;
            disposeElements();
        }
        ViewFile.tabItem2.setText(Messages.onglet_station);  
    }
    
}
