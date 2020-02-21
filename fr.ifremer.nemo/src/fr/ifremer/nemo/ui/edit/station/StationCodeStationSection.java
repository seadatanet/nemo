package fr.ifremer.nemo.ui.edit.station;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.edit.ComboType;
import fr.ifremer.nemo.ui.edit.station.AutoLine.MetaInData;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

public class StationCodeStationSection extends StationSection {
	Button useStationCodeInLocalCDIIdCheckBox;
	public StationCodeStationSection( EditStationForm form) {
		super(Intern.S_STATIONNUMBER, Messages.getString(Intern.S_STATIONNUMBER), Messages.edit_station_sectionReference_lib, true, true, 1, form);
		init();
	}

	private void init() {

		//		if (rulesManager.isTrajectory()) {
		//			stationNumber = new StationSection(Intern.S_STATIONNUMBER, Messages.getString(Intern.S_STATIONNUMBER), Messages.edit_station_sectionReference_lib_traj, true, true, 1, this);
		//		}else 	if (rulesManager.getMyInputVariant()==E_Variant.PHYSICOCHEMICAL ) {
		//			stationNumber = new StationSection(Intern.S_STATIONNUMBER, Messages.getString(Intern.S_STATIONNUMBER), Messages.edit_station_sectionReference_lib, true, true, 1, this);
		//		}else{
		//			stationNumber = new StationSection(Intern.S_STATIONNUMBER, Messages.getString(Intern.S_STATIONNUMBER), Messages.edit_station_sectionReference_lib_variants, true, true, 1, this);
		//		}

		addManualInput(null, ComboType.NONE, "1"); //$NON-NLS-1$
		getManualInput().setNbColumns(2);

		//		if (RulesManager.getInstance().getMyInputVariant()==E_Variant.PHYSICOCHEMICAL) {
		getManualInput().setLabelButton(Messages.edit_station_refIncrement);
		//		}else {
		//			getManualInput().setLabelButton(Messages.edit_station_refIncrement_variants);
		//		}

		// auto (read in data)
		if (!RulesManager.getInstance().isTrajectory()) {
			addAutoInput(Messages.label_auto_02, false, MetaInData.FACULTATIF, false, false);
		}

	}
	@Override
	protected void drawSection(Composite sectionClient, boolean filled) {
		super.drawSection(sectionClient, filled);
		useStationCodeInLocalCDIIdCheckBox = new Button(sectionClient, SWT.CHECK);
		useStationCodeInLocalCDIIdCheckBox.setText(Messages.edit_station_useInLocalCdiId);
		useStationCodeInLocalCDIIdCheckBox.setSelection(true);
		
	}
	@Override
	public void razValues() {
		super.razValues();
		initDescriptions();
		initUseStationCodeInLocalCDIIdCheckBox();
	}
	public void initUseStationCodeInLocalCDIIdCheckBox() {
		if (RulesManager.getInstance().isTrajectory()) {
			useStationCodeInLocalCDIIdCheckBox.setSelection(false);
		}else {
			useStationCodeInLocalCDIIdCheckBox.setSelection(true);
		}
	}
	public void initDescriptions() {
		if (RulesManager.getInstance().isTrajectory()) {
			getSection().setDescription( Messages.edit_station_sectionReference_lib_traj);
		}else
			//			if (RulesManager.getInstance().getMyInputVariant()==E_Variant.PHYSICOCHEMICAL ) 
		{
			getSection().setDescription( Messages.edit_station_sectionReference_lib);
		}
		//		else{
		//			getSection().setDescription( Messages.edit_station_sectionReference_lib_variants);
		//		}

	}

	public void setUseStationCodeInLocalCdiId(Boolean use) {
		useStationCodeInLocalCDIIdCheckBox.setSelection(use);
		
	}

}
