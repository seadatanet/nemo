package fr.ifremer.nemo.ui.edit.station;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.ComboType;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.Etape;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.ui.edit.cruise.TableDataSurvey;
import fr.ifremer.nemo.ui.edit.station.AutoLine.MetaInData;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditStationForm extends EditForm
{
    //public ArrayList<NemoSection> listeSectionsStation;

    public StationSection stationNumber, dataType, historique, comments, 
            surfaceSample, utConvertion, time, date, latitude, endLatitude, 
            longitude, endLongitude, depth, endDepth, sensorDepth, 
            magneticDeclin, samplingRate;

    public QCflagsSection qcFlags;

    @Override
    public void createEditionForm(Composite parent)
    {
        super.createEditionForm(parent);

        formEdition.getBody().setLayout(new GridLayout());
        formEdition.setText(Messages.edit_station_title);

        // init des objets
        initSections();

        // bouton validate
        createButtonValidate();
        buttonValidate
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    Etape.validationStation();
                }
            });

        // bouton reset
        createButtonRaz();

        // bouton collapse all / expand all
        createButtonExpand();

        if (ViewMain.isCollection()){
            TableDataSurvey.initTypes();
        }

        // Draw Station Sections
        drawSections();
    }

    @Override
    public void afficheEdition()
    {
        super.afficheEdition();

        ViewLog.aideStation();
        
        // affichage du texte dans la zone de gauche
        ViewMain.textAreaStation.setText(Utils.texteBrut);

        // affichage courant
        loaded = true;
        ViewMain.affiche = 2;

        // refreshEdition
        for (NemoSection stationSection : listeSections){
            stationSection.dispose();
        }
        listeSections.clear();
        initSections();
        drawSections();

        // remplissage de l'IHM par le modèle chargé
        if (DataModel.stationModel != null
                && DataModel.stationModel.getModel().getDataType() != null){
            DataModel.fillBackStation();
            filled = true;
        }
        else if (DataModel.ouverture){
            DataModel.fillStation();
            filled = true;
        }
        actualiseEdition();

        // refresh du formulaire
        formEdition.redraw();
        formEdition.reflow(true);
    }

    @Override
    protected void initSections()
    {
        stationNumber =
            new StationSection(NemoSection.S_STATIONNUMBER, Messages
                .getString(NemoSection.S_STATIONNUMBER),
                Messages.edit_station_sectionReference_lib, true, true, 1);
        stationNumber.addManualInput(null, ComboType.NONE, "1"); //$NON-NLS-1$ 
        if (!ViewMain.radTrajectoire.getSelection())
        {
            stationNumber.getManualInput().setNbColumns(2);
            stationNumber.getManualInput().setLabelButton(
                Messages.edit_station_refIncrement);
        }
        stationNumber.addAutoInput(Messages.label_auto_02, false,
            MetaInData.FACULTATIF, false);
        listeSections.add(stationNumber);

        if (!ViewMain.isCollection())
        {
            dataType =
                new StationSection(NemoSection.S_DATATYPE, Messages
                    .getString(NemoSection.S_DATATYPE),
                    Messages.edit_station_sectionDataType_lib, true, false, 1);
            dataType.addCouple(ComboType.DATATYPE, Global.EMPTY_STRING);
            listeSections.add(dataType);
        }

        historique =
            new StationSection(NemoSection.S_DCHIST, Messages
                .getString(NemoSection.S_DCHIST),
                Messages.edit_station_sectionHistory_lib, true, false, 2);
        historique.addManualInput(null, ComboType.NONE, Global.EMPTY_STRING);
        historique.addAutoInput(null, false, MetaInData.INTERDIT, true);
        listeSections.add(historique);

        comments =
            new StationSection(NemoSection.S_COMMENT, Messages
                .getString(NemoSection.S_COMMENT),
                Messages.edit_station_sectionComment_lib, true, false, 1);
        comments.addManualInput(null, ComboType.BLOCK, Global.EMPTY_STRING);
        comments.addAutoInput(null, true, MetaInData.INTERDIT, true);
        listeSections.add(comments);

        surfaceSample =
            new StationSection(NemoSection.S_SURFACE, Messages
                .getString(NemoSection.S_SURFACE),
                Messages.edit_station_sectionSurface_lib, true, false, 2);
        surfaceSample.addManualInput(null, ComboType.NONE, Global.EMPTY_STRING);
        surfaceSample.addAutoInput(null, false, MetaInData.INTERDIT, true);
        listeSections.add(surfaceSample);

        utConvertion =
            new StationSection(NemoSection.S_OFFSETTU, Messages
                .getString(NemoSection.S_OFFSETTU), Global.EMPTY_STRING, true,
                false, 1);
        utConvertion.addManualInput(null, ComboType.NONE, "0.0"); //$NON-NLS-1$
        utConvertion.addAutoInput(null, false, MetaInData.INTERDIT, false);
        listeSections.add(utConvertion);

        time =
            new StationSection(NemoSection.S_TIME, Messages
                .getString(NemoSection.S_TIME),
                Messages.edit_station_sectionTime_lib, false, true, 1);
        time.addConverter(ComboType.TIME, Global.EMPTY_STRING);
        time.addManualInput(null, ComboType.NONE, Global.EMPTY_STRING);
        time.addAutoInput(null, false, MetaInData.FACULTATIF, false);
        listeSections.add(time);

        date =
            new StationSection(NemoSection.S_DATE, Messages
                .getString(NemoSection.S_DATE),
                Messages.edit_station_sectionDate_lib, false, true, 1);
        date.addConverter(ComboType.DATE, Global.EMPTY_STRING);
        date.addManualInput(null, ComboType.NONE, Global.EMPTY_STRING);
        date.addAutoInput(null, false, MetaInData.FACULTATIF, false);
        listeSections.add(date);

        latitude =
            new StationSection(NemoSection.S_LAT, Messages
                .getString(NemoSection.S_LAT),
                Messages.edit_station_sectionLat_lib, false, true, 1);
        latitude.addConverter(ComboType.LAT, Global.EMPTY_STRING);
        latitude.addManualInput(null, ComboType.NONE, Global.EMPTY_STRING);
        latitude.addAutoInput(null, true, MetaInData.FACULTATIF, false);
        listeSections.add(latitude);

        if (ViewMain.radSerieTempo.getSelection())
        {
            endLatitude =
                new StationSection(NemoSection.S_ELAT, Messages
                    .getString(NemoSection.S_ELAT), Global.EMPTY_STRING, false,
                    true, 1);
            endLatitude.addConverter(ComboType.LAT, Global.EMPTY_STRING);
            endLatitude.addManualInput(null, ComboType.NONE,
                Global.EMPTY_STRING);
            endLatitude.addAutoInput(null, true, MetaInData.INTERDIT, false);
            endLatitude.setTimeSeriesInfo(true);
            listeSections.add(endLatitude);
        }

        longitude =
            new StationSection(NemoSection.S_LON, Messages
                .getString(NemoSection.S_LON),
                Messages.edit_station_sectionLon_lib, false, true, 1);
        longitude.addConverter(ComboType.LON, Global.EMPTY_STRING);
        longitude.addManualInput(null, ComboType.NONE, Global.EMPTY_STRING);
        longitude.addAutoInput(null, true, MetaInData.FACULTATIF, false);
        listeSections.add(longitude);

        if (ViewMain.radSerieTempo.getSelection())
        {
            endLongitude =
                new StationSection(NemoSection.S_ELON, Messages
                    .getString(NemoSection.S_ELON), Global.EMPTY_STRING, false,
                    true, 1);
            endLongitude.addConverter(ComboType.LON, Global.EMPTY_STRING);
            endLongitude.addManualInput(null, ComboType.NONE,
                Global.EMPTY_STRING);
            endLongitude.addAutoInput(null, true, MetaInData.INTERDIT, false);
            endLongitude.setTimeSeriesInfo(true);
            listeSections.add(endLongitude);
        }

        // FAE 1129 : ajout TEST / position variable à DEPTH
        depth =
            new StationSection(NemoSection.S_DEPTH, Messages
                .getString(NemoSection.S_DEPTH),
                Messages.edit_station_sectionDepth_lib, true, true, 1);
        depth.addManualInput(null, ComboType.NONE, Global.EMPTY_STRING);
        depth.addAutoInput(null, true, MetaInData.FACULTATIF, false);
        listeSections.add(depth);

        if (!ViewMain.radProfilVertical.getSelection())
        {
            if (ViewMain.radSerieTempo.getSelection())
            {
                endDepth =
                    new StationSection(NemoSection.S_EDEPTH, Messages
                        .getString(NemoSection.S_EDEPTH), Global.EMPTY_STRING,
                        true, true, 1);
                endDepth.addManualInput(null, ComboType.NONE,
                    Global.EMPTY_STRING);
                endDepth.addAutoInput(null, true, MetaInData.INTERDIT, false);
                endDepth.setTimeSeriesInfo(true);
                listeSections.add(endDepth);
            }

            sensorDepth =
                new StationSection(NemoSection.S_SENSORDEPTH, Messages
                    .getString(NemoSection.S_SENSORDEPTH),
                    Messages.edit_station_sectionSensorDepth_lib, true, false,
                    1);
            sensorDepth.addManualInput(null, ComboType.NONE,
                Global.EMPTY_STRING);
            sensorDepth.addAutoInput(null, false, MetaInData.FACULTATIF, false);
            sensorDepth.setTimeSeriesInfo(true);
            listeSections.add(sensorDepth);

            if (ViewMain.radSerieTempo.getSelection())
            {
                magneticDeclin =
                    new StationSection(NemoSection.S_MAGNETIC, Messages
                        .getString(NemoSection.S_MAGNETIC),
                        Messages.edit_station_sectionMagn_lib, true, false, 1);
                magneticDeclin.addManualInput(null, ComboType.NONE,
                    Global.EMPTY_STRING);
                magneticDeclin.addAutoInput(null, false, MetaInData.INTERDIT,
                    false);
                magneticDeclin.setTimeSeriesInfo(true);
                listeSections.add(magneticDeclin);
            }

            samplingRate =
                new StationSection(NemoSection.S_SAMPLINGRATE, Messages
                    .getString(NemoSection.S_SAMPLINGRATE),
                    Messages.edit_station_sectionSampling_lib, true, true, 1);
            samplingRate.addConverter(ComboType.FRQ, "*1"); //$NON-NLS-1$
            samplingRate.addManualInput(null, ComboType.NONE, "0"); //$NON-NLS-1$
            samplingRate.addAutoInput(null, false, MetaInData.INTERDIT, false);
            samplingRate.setTimeSeriesInfo(true);
            listeSections.add(samplingRate);
        }
        qcFlags =
            new QCflagsSection(NemoSection.S_QCFLAG, Messages
                .getString(NemoSection.S_QCFLAG),
                Messages.edit_station_sectionQCflags_lib);
        listeSections.add(qcFlags);
    }

    @Override
    public void actualiseEdition()
    {
        // pour trajectoires et time-series
        if (ViewMain.radTrajectoire.getSelection()
                || ViewMain.radSerieTempo.getSelection()){
            // si Date est saisie dans les données
            boolean dateInData =
                date.getAutoInput().getLines().get(0).isInData();
            // si Time est saisie dans les données
            boolean timeInData =
                time.getAutoInput().getLines().get(0).isInData();
            // => Sampling Rate : disable (car pas nécessaire pour le calcul)
            samplingRate.setEnabled(!(dateInData || timeInData));
            date.setEnabledManual(!(dateInData || timeInData));
            time.setEnabledManual(!(dateInData || timeInData));
        }

        if (ViewMain.radTrajectoire.getSelection())
        {
            // in trajectory, latitude must be in data
            latitude.setEnableInData(MetaInData.OBLIGATOIRE);
            latitude.setEnabledManual(false);
            
            // in trajectory, longitude must be in data
            longitude.setEnableInData(MetaInData.OBLIGATOIRE);
            longitude.getManualInput().setGrpEnabled(false);
            
            // in trajectory, depth must be in data
            depth.setEnableInData(MetaInData.OBLIGATOIRE);
            depth.getManualInput().setGrpEnabled(false);
            
        }
        

        for (NemoSection section : listeSections){
            if (section instanceof StationSection){
                ((StationSection) section).diseableLineInData();
            }
        }
    }

    @Override
    public void drawingBack()
    {
        super.drawingBack();
        
        ViewLog.aideStation();
        ViewMain.affiche = 2;
    }

    public boolean someInData()
    {
        boolean inData = false;
        for (NemoSection stationSection : listeSections){
            if (stationSection instanceof StationSection
                    && ((StationSection) stationSection).isInData()){
                inData = true;
            }
        }
        return inData;
    }

    public void invalidEtape()
    {
        super.invalidEtape(2);
        ViewMain.tabStation.setText(Messages.onglet_station);
    }

}
