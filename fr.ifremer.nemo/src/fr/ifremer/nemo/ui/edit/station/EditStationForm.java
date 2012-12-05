package fr.ifremer.nemo.ui.edit.station;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.input.MediaModel;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.ComboType;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.ui.edit.cruise.TableDataSurvey;
import fr.ifremer.nemo.ui.edit.station.AutoLine.MetaInData;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

public class EditStationForm extends EditForm
{

	public StationSection stationNumber;
	public StationSection dataType, historique, comments,
			surfaceSample, utConvertion, time, date, latitude, endLatitude,
			longitude, endLongitude, depth, sensorDepth,
			magneticDeclin, samplingRate;

	public StationSection endDepth;

	public QCflagsSection qcFlags;

	private StyledText textAreaStation;

	public EditStationForm()
	{
		super();
		myValidator = new StationFormValidator();
	}

	@Override
	public void createEditionForm(Composite parent)
	{
		try
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

					validate();
				}
					});

			// bouton reset
			createButtonRaz();

			// bouton collapse all / expand all
			createButtonExpand();

			// TODO BGT tjs utile 6536?
			if (Nemo.getInstance().getViewMain().getMyRulesManager().isProcessCollection())
			{
				TableDataSurvey.initTypes();
			}

			// Draw Station Sections
			drawSections();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void razText()
	{
		super.razText();
		if (!samplingRate.getSection().isDisposed())
		{
			samplingRate.setEnabled(true);
			date.setEnabledManual(true);
			time.setEnabledManual(true);
		}
	}

	@Override
	public void afficheEdition()
	{
		super.afficheEdition();

		ViewLog.aideStation();

		// affichage du texte dans la zone de gauche
		textAreaStation.setText(Utils.texteBrut);

		// affichage courant
		setLoaded(true);
		Nemo.getInstance().getViewMain().setAffiche(2);

		// refreshEdition
		for (NemoSection stationSection : listeSections)
		{
			stationSection.dispose();
		}
		listeSections.clear();
		initSections();
		drawSections();

		// FAE 3749
		if(!Nemo.getInstance().getViewMain().getMyRulesManager().isConversionToNetCDF())
		{
    		Text textCommentaire = comments.getManualInput().getLines().get(0).getValueText();
    		GridDataFactory.fillDefaults().grab(true, true).hint(Global.SIZE_SECTION, SWT.DEFAULT).minSize(Global.SIZE_SECTION, 200)
    				.applyTo(textCommentaire);
		}
		// remplissage de l'IHM par le modèle chargé
		// if (!Nemo.getInstance().getNomModele().isEmpty())
		// FAE 10632 fill necessaire car appelle le remplissabe du dataType
		fillFromModel();
		filled = true;

		actualiseEdition();

		// refresh du formulaire
		formEdition.redraw();
		formEdition.reflow(true);
	}

	@Override
	protected void initSections()
	{
		RulesManager rulesManager = Nemo.getInstance().getViewMain().getMyRulesManager();
		/**
         * ==============================================================================================
		 * station number
         * ==============================================================================================
		 */
		stationNumber =
				new StationSection(Intern.S_STATIONNUMBER, Messages
						.getString(Intern.S_STATIONNUMBER),
						Messages.edit_station_sectionReference_lib, true, true, 1, this);
		stationNumber.addManualInput(null, ComboType.NONE, "1"); //$NON-NLS-1$ 
		if (!rulesManager.isTrajectory())
		{
			stationNumber.getManualInput().setNbColumns(2);
			stationNumber.getManualInput().setLabelButton(
					Messages.edit_station_refIncrement);
		}
		stationNumber.addAutoInput(Messages.label_auto_02, false,
				MetaInData.FACULTATIF, false, false);
		listeSections.add(stationNumber);
		/**
         * ==============================================================================================
		 * data type
         * ==============================================================================================
		 */

		dataType = new StationSection(Intern.S_DATATYPE, Messages.getString(Intern.S_DATATYPE), Messages.edit_station_sectionDataType_lib, true, false, 1, this);
		dataType.addCouple(ComboType.DATATYPE, Global.EMPTY_STRING);
		listeSections.add(dataType);
		/**
         * ==============================================================================================
		 * acquisition history
         * ==============================================================================================
		 */
		if(!rulesManager.isConversionToNetCDF())
		{
    		historique =
    				new StationSection(Intern.S_DCHIST, Messages
    						.getString(Intern.S_DCHIST),
    						Messages.edit_station_sectionHistory_lib, true, false, 2, this);
    		historique.addManualInput(null, ComboType.NONE, Global.EMPTY_STRING);
    		historique.addAutoInput(null, false, MetaInData.INTERDIT, true, false);
    		listeSections.add(historique);
		}
		/**
         * ==============================================================================================
		 * comments
         * ==============================================================================================
		 */
		if(!rulesManager.isConversionToNetCDF())
        {
    		comments =
    				new StationSection(Intern.S_COMMENT, Messages
    						.getString(Intern.S_COMMENT),
    						Messages.edit_station_sectionComment_lib, true, false, 1, this);
    		comments.addManualInput(null, ComboType.BLOCK, Global.EMPTY_STRING);
    		comments.addAutoInput(null, true, MetaInData.INTERDIT, true, false);
    		listeSections.add(comments);
    		comments.getAutoInput().setVariableLabel(
    				Messages.label_variable_comment);
        }
		/**
         * ==============================================================================================
		 * surface sample
         * ==============================================================================================
		 */
		if(!rulesManager.isConversionToNetCDF())
        {
    		surfaceSample =
    				new StationSection(Intern.S_SURFACE, Messages
    						.getString(Intern.S_SURFACE),
    						Messages.edit_station_sectionSurface_lib, true, false, 2, this);
    		surfaceSample.addManualInput(null, ComboType.NONE, Global.EMPTY_STRING);
    		surfaceSample.addAutoInput(null, false, MetaInData.INTERDIT, true,
    				false);
    		listeSections.add(surfaceSample);
        }
		/**
         * ==============================================================================================
		 * UT conversion
         * ==============================================================================================
		 */
		if(!rulesManager.isConversionToNetCDF())
        {
    		utConvertion =
    				new StationSection(Intern.S_OFFSETTU, Messages
    						.getString(Intern.S_OFFSETTU), Global.EMPTY_STRING, true,
    						false, 1, this);
    		utConvertion.addManualInput(null, ComboType.NONE, "0.0"); //$NON-NLS-1$
    		utConvertion.addAutoInput(null, false, MetaInData.INTERDIT, false,
    				false);
    		listeSections.add(utConvertion);
        }
		/**
         * ==============================================================================================
		 * time
         * ==============================================================================================
		 */
		time =
				new StationSection(Intern.S_TIME,
						Messages.getString(Intern.S_TIME),
						Messages.edit_station_sectionTime_lib, false, true, 1, this);

		time.addConverter(ComboType.TIME, Global.EMPTY_STRING);
		time.addManualInput(null, ComboType.NONE, Global.EMPTY_STRING);

		boolean defaultValueEnable = rulesManager.isProfile();
		time.addAutoInput(null, false, MetaInData.FACULTATIF, false, defaultValueEnable);
		listeSections.add(time);
		/**
         * ==============================================================================================
		 * date
         * ==============================================================================================
		 */
		date =
				new StationSection(Intern.S_DATE,
						Messages.getString(Intern.S_DATE),
						Messages.edit_station_sectionDate_lib, false, true, 1, this);
		date.addConverter(ComboType.DATE, Global.EMPTY_STRING);
		date.addManualInput(null, ComboType.NONE, Global.EMPTY_STRING);
		date.addAutoInput(null, false, MetaInData.FACULTATIF, false, false);
		listeSections.add(date);
		/**
         * ==============================================================================================
		 * latitude
         * ==============================================================================================
		 */
		latitude =
				new StationSection(Intern.S_LAT, Messages.getString(Intern.S_LAT),
						Messages.edit_station_sectionLat_lib, false, true, 1, this);
		latitude.addConverter(ComboType.LAT, Global.EMPTY_STRING);
		latitude.addManualInput(null, ComboType.NONE, Global.EMPTY_STRING);
		latitude.addAutoInput(null, true, MetaInData.FACULTATIF, false, false);
		listeSections.add(latitude);

		if (rulesManager.isTimeSeries() && !rulesManager.isConversionToNetCDF())
		{
			endLatitude =
					new StationSection(Intern.S_ELAT, Messages
							.getString(Intern.S_ELAT), Global.EMPTY_STRING, false,
							true, 1, this);
			endLatitude.addConverter(ComboType.LAT, Global.EMPTY_STRING);
			endLatitude.addManualInput(null, ComboType.NONE,
					Global.EMPTY_STRING);
			endLatitude.addAutoInput(null, true, MetaInData.INTERDIT, false,
					false);
			endLatitude.setTimeSeriesInfo(true);
			listeSections.add(endLatitude);
		}
		/**
         * ==============================================================================================
		 * longitude
         * ==============================================================================================
		 */
		longitude =
				new StationSection(Intern.S_LON, Messages.getString(Intern.S_LON),
						Messages.edit_station_sectionLon_lib, false, true, 1, this);
		longitude.addConverter(ComboType.LON, Global.EMPTY_STRING);
		longitude.addManualInput(null, ComboType.NONE, Global.EMPTY_STRING);
		longitude.addAutoInput(null, true, MetaInData.FACULTATIF, false, false);
		listeSections.add(longitude);

		if (rulesManager.isTimeSeries() && !rulesManager.isConversionToNetCDF())
		{
			endLongitude =
					new StationSection(Intern.S_ELON, Messages
							.getString(Intern.S_ELON), Global.EMPTY_STRING, false,
							true, 1, this);
			endLongitude.addConverter(ComboType.LON, Global.EMPTY_STRING);
			endLongitude.addManualInput(null, ComboType.NONE,
					Global.EMPTY_STRING);
			endLongitude.addAutoInput(null, true, MetaInData.INTERDIT, false,
					false);
			endLongitude.setTimeSeriesInfo(true);
			listeSections.add(endLongitude);
		}
		/**
         * ==============================================================================================
		 * bottom depth
         * ==============================================================================================
		 */
		// FAE 1129 : ajout TEST / position variable à DEPTH
		depth =
				new StationSection(Intern.S_DEPTH, Messages
						.getString(Intern.S_DEPTH),
						Messages.edit_station_sectionDepth_lib, true, true, 1, this);
		// FAE 4471 (2e version: on remet le champ manuel)
		depth.addManualInput(null, ComboType.NONE, Global.EMPTY_STRING);
		// depth.addNoValueInput(null, ComboType.NONE, Global.EMPTY_STRING);
		depth.addAutoInput(null, true, MetaInData.FACULTATIF, false, true);
		listeSections.add(depth);

		if (!rulesManager.isProfile())
		{
			if (rulesManager.isTimeSeries() && !rulesManager.isConversionToNetCDF())
			{
				endDepth =
						new StationSection(Intern.S_EDEPTH, Messages
								.getString(Intern.S_EDEPTH), Global.EMPTY_STRING, true,
								true, 1, this);
				endDepth.addManualInput(null, ComboType.NONE,
						Global.EMPTY_STRING);
				endDepth.addAutoInput(null, true, MetaInData.INTERDIT, false,
						false);
				endDepth.setTimeSeriesInfo(true);
				listeSections.add(endDepth);
			}
			/**
             * ==============================================================================================
			 * sensor depth
             * ==============================================================================================
			 */
			sensorDepth =
					new StationSection(Intern.S_SENSORDEPTH, Messages
							.getString(Intern.S_SENSORDEPTH),
							Messages.edit_station_sectionSensorDepth_lib, true, false,
							1, this);
			sensorDepth.addManualInput(null, ComboType.NONE,
					Global.EMPTY_STRING);
			sensorDepth.addAutoInput(null, false, MetaInData.FACULTATIF, false,
					false);
			sensorDepth.setTimeSeriesInfo(true);
			listeSections.add(sensorDepth);

			if (rulesManager.isTimeSeries() && !rulesManager.isConversionToNetCDF())
			{
				magneticDeclin =
						new StationSection(Intern.S_MAGNETIC, Messages
								.getString(Intern.S_MAGNETIC),
								Messages.edit_station_sectionMagn_lib, true, false, 1, this);
				magneticDeclin.addManualInput(null, ComboType.NONE,
						Global.EMPTY_STRING);
				magneticDeclin.addAutoInput(null, false, MetaInData.INTERDIT,
						false, false);
				magneticDeclin.setTimeSeriesInfo(true);
				listeSections.add(magneticDeclin);
			}
			/**
             * ==============================================================================================
			 * sampling rate
             * ==============================================================================================
			 */
			if(!rulesManager.isConversionToNetCDF())
	        {
    			samplingRate =
    					new StationSection(Intern.S_SAMPLINGRATE, Messages
    							.getString(Intern.S_SAMPLINGRATE),
    							Messages.edit_station_sectionSampling_lib, true, true, 1, this);
    			samplingRate.addConverter(ComboType.FRQ, "*1"); //$NON-NLS-1$
    			samplingRate.addManualInput(null, ComboType.NONE, "0"); //$NON-NLS-1$
    			samplingRate.addAutoInput(null, false, MetaInData.INTERDIT, false,
    					false);
    			samplingRate.setTimeSeriesInfo(true);
    			listeSections.add(samplingRate);
	        }
		}
		/**
         * ==============================================================================================
		 * QC flags
         * ==============================================================================================
		 */
		
    		qcFlags =
    				new QCflagsSection(Intern.S_QCFLAG, Messages
    						.getString(Intern.S_QCFLAG),
    						Messages.edit_station_sectionQCflags_lib, this);
    		listeSections.add(qcFlags);
    		
        
	}

	@Override
	public void actualiseEdition()
	{
		RulesManager rulesManager = Nemo.getInstance().getViewMain().getMyRulesManager();
		// pour trajectoires et time-series
		if (rulesManager.isTrajectory() || rulesManager.isTimeSeries())
		{
			// si Date est saisie dans les données
			boolean dateInData =
					date.getAutoInput().getLines().get(0).isInData();
			// si Time est saisie dans les données
			boolean timeInData =
					time.getAutoInput().getLines().get(0).isInData();
			// => Sampling Rate : disable (car pas nécessaire pour le calcul)
			if(!samplingRate.getSection().isDisposed())
			    samplingRate.setEnabled(!(dateInData || timeInData));
			date.setEnabledManual(!(dateInData || timeInData));
			time.setEnabledManual(!(dateInData || timeInData));
		}

		if (rulesManager.isTrajectory())
		{
			// in trajectory, latitude must be in data
			latitude.setEnableInData(MetaInData.OBLIGATOIRE);
			latitude.setEnabledManual(false);

			// in trajectory, longitude must be in data
			longitude.setEnableInData(MetaInData.OBLIGATOIRE);
			longitude.setEnabledManual(false);

			// in trajectory, depth must be in data
			depth.setEnableInData(MetaInData.OBLIGATOIRE);

		}

		for (NemoSection section : listeSections)
		{
			if (section instanceof StationSection)
			{
				((StationSection) section).diseableLineInData();
			}
		}

	}

	@Override
	public void drawingBack()
	{
		super.drawingBack();

		ViewLog.aideStation();
		Nemo.getInstance().getViewMain().setAffiche(2);
	}

	public boolean someInData()
	{
		boolean inData = false;
		for (NemoSection stationSection : listeSections)
		{
			if (stationSection instanceof StationSection
					&& ((StationSection) stationSection).isInData())
			{
				inData = true;
			}
		}
		return inData;
	}

	public void invalidEtape()
	{
		super.invalidEtape(2);
		Nemo.getInstance().getViewMain().tabStation.setText(Messages.onglet_station);
	}

	@Override
	public void fillFromModel()
	{

		MediaModel mModel = Nemo.getInstance().getDomain().getMedia().getModel();
		StationModel sModel = Nemo.getInstance().getDomain().getStation().getModel();

        for (NemoSection stationSection : listeSections){
            if(!stationSection.getSection().isDisposed())
                stationSection.fillFromModel(sModel);
		}

		// ENDDATETIME flag !!
        if (mModel.getInputType() != E_InputType.PROFILE){
			PropertyModel propertyModel = sModel.propertiesAt(Intern.S_EDATE);
			qcFlags.setFlagFragment(propertyModel
					.getFlag(), Intern.ENDDATETIME, Messages.getString(
					Intern.ENDDATETIME).trim());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initFromNewModel()
	{
		// recuperation du model correspondant
		StationDomain stationDomain = Nemo.getInstance().getDomain().getStation();
		StationModel stationModel = new StationModel();
		E_InputType inputType = Nemo.getInstance().getDomain().getMedia().getModel().getInputType();

		PropertyModel propertyModel;

		// parcours des sections de la station pour initialisation
		for (NemoSection stationSection : listeSections)
		{
		    if(!stationSection.getSection().isDisposed())
		        stationSection.initModel(stationModel);
		}

		// Completer les données manquantes

		// DM HISTORY 1&2
		// station.setDmHistory1(Global.EMPTY_STRING);
		// station.setDmHistory2(Global.EMPTY_STRING);

        if (inputType != E_InputType.PROFILE){
			// GESTION Time/Date in Data
            if(!samplingRate.getSection().isDisposed())
            {
                if (samplingRate.isEnabled()){
                    // calcul e partir frequence echantillonnage
                    stationDomain.setComputed(true);
                }
                else{
                    // calcul position date/heure dans les donnees
                    stationDomain.setComputed(false);
                    // remplissage avec les données par défaut
                    propertyModel = stationModel.propertiesAt(Intern.S_SAMPLINGRATE);
                    propertyModel.setUserProvided(true);
                    propertyModel.setConversionFormula("*1"); //$NON-NLS-1$
                    //sModel.getTimeSeriesModel().setSampleRateConversion("*1"); //$NON-NLS-1$
                    propertyModel.setUserValue(Global.ZERO);
                    // sModel.getTimeSeriesInformation().setSamplingRate(Global.ZERO);
                }
            }
            

			// DATE / HEURE DE FIN
			propertyModel = new PropertyModel();
			// stationModel.propertiesAt(Intern.S_EDATE);
			propertyModel.setProperty(Intern.S_EDATE);
			propertyModel.setUserProvided(true);
            propertyModel.setFlag(qcFlags.getFlagFragment(Intern.ENDDATETIME)); // Flag qualite
			stationModel.addProperty(propertyModel);

			propertyModel = new PropertyModel();
			// propertyModel = stationModel.propertiesAt(Intern.S_ETIME);
			propertyModel.setProperty(Intern.S_ETIME);
			propertyModel.setUserProvided(true);
			propertyModel.setFlag(qcFlags
					.getFlagFragment(Intern.ENDDATETIME)); // Flag qualite
			stationModel.addProperty(propertyModel);

            if (inputType == E_InputType.TRAJECTORY){
				initDiseabledField(stationModel, Intern.S_ELAT);
				initDiseabledField(stationModel, Intern.S_ELON);
				initDiseabledField(stationModel, Intern.S_EDEPTH);

				// DECLINAISON MAGNETIQUE : FAE 1386 - valeur par défaut pour
				// les Trajectoires
				propertyModel = new PropertyModel();
				propertyModel.setProperty(Intern.S_MAGNETIC);
				propertyModel.setUserProvided(true);
				propertyModel.setUserValue("-999"); //$NON-NLS-1$
				stationModel.addProperty(propertyModel);

			}
		} // fin specifique SERIE TEMPO ou TRAJECTOIRE

		// Mise à jour du model
		stationDomain.updateStation(stationModel);

		// flag validation à 1
		setValidated(true);
	}

	private static void initDiseabledField(StationModel sModel, String property)
	{
		PropertyModel propertyModel = new PropertyModel();
		propertyModel.setProperty(property);
        propertyModel.setFlag(Nemo.getInstance().getViewMain().getEditStationForm().qcFlags.getFlagFragment(property)); // Flag qualite
		propertyModel.setToCompute(true);
		sModel.addProperty(propertyModel);
	}

	public StyledText getTextAreaStation()
	{
		return textAreaStation;
	}

	public void setTextAreaStation(StyledText textAreaStation)
	{
		this.textAreaStation = textAreaStation;
	}
}
