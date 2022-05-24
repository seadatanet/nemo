/*
 * ============================================================================
 * Nom du fichier : GroupVerticalParameters.java
 * Auteur : sbregent
 * Date : 21 janv. 11
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.ParameterManager;
import fr.ifremer.nemo.input.parameters.VerticalParametersList;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: classe dédiée aux combos permettant de choisir les paramètres de
 * référence verticale
 * contient les méthodes pour les mises à jour du contenu des combo, appellées
 * par la tableData
 * 
 * @author sbregent
 * 
 */
public class GroupVerticalParameters {
	// combos pour choisir les paramètres verticaux de référence
	private Combo comboDBSSurf;
	private Combo comboDBSBed;
	private Combo comboP;
	private Combo comboHASLvl;

	public GroupVerticalParameters() {

	}

	public void createGroup(ScrolledForm formEdition) {

		Group group = new Group(formEdition.getBody(), SWT.NONE);
		group.setFont(Utils.font08);
		group.setText(Messages.edit_data_verticalParameters_label);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true)
		.margins(4, 4).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
		GridData comboGridData = new GridData(GridData.FILL_HORIZONTAL);
		comboGridData.minimumWidth=50;
		// Depth below sea surface
		Label labelDBSSurf = new Label(group, SWT.NO_BACKGROUND);
		labelDBSSurf.setText(Messages.edit_data_verticalParameters_depthBSSurf);
		comboDBSSurf = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
		comboDBSSurf.setLayoutData(comboGridData );
		
		// Depth below sea bed
		Label labelDBSBed = new Label(group, SWT.NO_BACKGROUND);
		labelDBSBed.setText(Messages.edit_data_verticalParameters_depthBSBed);
		comboDBSBed = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
		comboDBSBed.setLayoutData(comboGridData );

		// Pressure
		Label labelP = new Label(group, SWT.NO_BACKGROUND);
		labelP.setText(Messages.edit_data_verticalParameters_pressure);
		comboP = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
		comboP.setLayoutData(comboGridData );

		// Pressure
		Label labelHASLvl = new Label(group, SWT.NO_BACKGROUND);
		labelHASLvl.setText(Messages.edit_data_verticalParameters_heightASLvl);
		comboHASLvl = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
		comboHASLvl.setLayoutData(comboGridData );
		
		SelectionListener selectionListener = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				controlVerticalReferenceSelection((Combo) e.widget);
				Nemo.getInstance().getViewMain().getEditDataForm().invalidEtape();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				controlVerticalReferenceSelection((Combo) e.widget);
			}
		};

		comboDBSSurf.addSelectionListener(selectionListener);
		comboDBSBed.addSelectionListener(selectionListener);
		comboP.addSelectionListener(selectionListener);
		comboHASLvl.addSelectionListener(selectionListener);



		init();
	}

	private void init() {
		comboDBSSurf.add(Global.EMPTY_STRING);
		comboDBSBed.add(Global.EMPTY_STRING);
		comboP.add(Global.EMPTY_STRING);
		comboHASLvl.add(Global.EMPTY_STRING);
	}

	public void addTypeData(TypeData typeData) {
		ParameterManager pm = Nemo.getInstance().getDomain().getData().getMyParameterManager();
		if (pm.isPossible_PressureParameter(typeData)) {
			comboP.add(typeData.getCodeToUse());
		}
		if (pm.isPossible_DepthBSSurfParameter(typeData)) {
			comboDBSSurf.add(typeData.getCodeToUse());
		}
		if (pm.isPossible_DepthBSBedParameter(typeData)) {
			comboDBSBed.add(typeData.getCodeToUse());
		}
		if (pm.isPossible_HeightASlvlParameter(typeData)) {
			comboHASLvl.add(typeData.getCodeToUse());
		}

	}
/**
 * 
 * @param typeData
 * @return true if datatype was found and removed
 */
	public boolean removeToAllCombo(TypeData typeData) {
		String code = typeData.getCodeToUse();
		try {
			comboDBSSurf.remove(code);
			return true;
		} catch (Exception e) {
		}
		try {
			comboDBSBed.remove(code);
			return true;
		} catch (Exception e) {
		}
		try {
			comboP.remove(code);
			return true;
		} catch (Exception e) {
		}
		try {
			comboHASLvl.remove(code);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public void removeAllToAllCombos() {
		try {
			comboDBSSurf.removeAll();
		} catch (Exception e) {
		}
		try {
			comboDBSBed.removeAll();
		} catch (Exception e) {
		}
		try {
			comboP.removeAll();
		} catch (Exception e) {
		}
		try {
			comboHASLvl.removeAll();
		} catch (Exception e) {
		}
		init();
	}

	public void enable_pDepthBSBed(boolean enabled) {
		comboDBSBed.setEnabled(enabled);
	}

	public void enable_pDepthBSSurf(boolean enabled) {
		comboDBSSurf.setEnabled(enabled);
	}

	public void enable_pPressure(boolean enabled) {
		comboP.setEnabled(enabled);
	}

	public void enable_pHeightASLvl(boolean enabled) {
		comboHASLvl.setEnabled(enabled);
	}

	public Combo getComboDBSBed() {
		return comboDBSBed;
	}

	public Combo getComboDBSSurf() {
		return comboDBSSurf;
	}

	public Combo getComboP() {
		return comboP;
	}

	public Combo getComboHASLvl() {
		return comboHASLvl;
	}

	public void select(VerticalParametersList list) {

		for (int i = 0; i < comboDBSSurf.getItemCount(); i++) {
			if (comboDBSSurf.getItem(i).equals(list.getP_depthBSSurf())) {
				comboDBSSurf.select(i);
			}
		}
		for (int i = 0; i < comboDBSBed.getItemCount(); i++) {
			if (comboDBSBed.getItem(i).equals(list.getP_depthBSBed())) {
				comboDBSBed.select(i);
			}
		}
		for (int i = 0; i < comboP.getItemCount(); i++) {
			if (comboP.getItem(i).equals(list.getP_pressure())) {
				comboP.select(i);
			}
		}

		for (int i = 0; i < comboHASLvl.getItemCount(); i++) {
			if (comboHASLvl.getItem(i).equals(list.getP_heightASLvl())) {
				comboHASLvl.select(i);
			}
		}
	}

	public void controlVerticalReferenceSelection(Combo current) {

		if (RulesManager.getInstance().isConversionToNetCDF()) {
			if (!current.equals(comboDBSSurf))
				comboDBSSurf.select(0);

			if (!current.equals(comboDBSBed))
				comboDBSBed.select(0);

			if (!current.equals(comboP))
				comboP.select(0);

			if (!current.equals(comboHASLvl))
				comboHASLvl.select(0);
		}




	}

	public List<String> getSelection(){
		List<String> list = new ArrayList<String>();
		int index;
		String item;

		index = comboDBSSurf.getSelectionIndex();
		if (index != -1){
			item = comboDBSSurf.getItem(index);
			list.add(item);
		}
		index = comboDBSBed.getSelectionIndex();
		if (index != -1){
			item = comboDBSBed.getItem(index);
			list.add(item);
		}
		index = comboP.getSelectionIndex();
		if (index != -1){
			item = comboP.getItem(index);
			list.add(item);
		}
		index = comboHASLvl.getSelectionIndex();
		if (index != -1){
			item = comboHASLvl.getItem(index);
			list.add(item);
		}
		return list;
	}
}
