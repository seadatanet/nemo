/* ============================================================================
 * Nom du fichier	: AutoInput.java
 * Auteur			: dboulang
 * Date				: 21 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.station.autoinput;

import java.util.ArrayList;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.UIobject;
import fr.ifremer.nemo.ui.edit.station.StationSection;
import fr.ifremer.nemo.ui.edit.station.autoinput.autoline.AutoLine;
import fr.ifremer.nemo.ui.edit.station.autoinput.autoline.AutoLine.MetaInData;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: objet graphique "Automatic Input"
 * 
 * @author dboulang
 */
public abstract class GrpAutoInput implements UIobject {

	private final StationSection parentSection;
	private Button buttonPos;
	private String labelButtonPos;
	private Group groupe;
	protected ArrayList<AutoLine> lines;
	
	// use in GrpAutoInputStartEnd but necessary if converted from Column
	protected final boolean variablePos;
	public boolean defaultValueEnable;

	public Button checkBoxHasDefaultValue;
	public Text textDefaultValue;
	private final SelectionListener listenerAuto;
	private final boolean withCursor;



	public GrpAutoInput(StationSection parentSection , String labelButtonPos, boolean variablePos, boolean defaultValueEnabled, SelectionListener listenerAuto, boolean withCursor) {
		this.parentSection = parentSection;
		this.variablePos = variablePos;
		this.defaultValueEnable = defaultValueEnabled;
		this.listenerAuto = listenerAuto;

		this.labelButtonPos = labelButtonPos;
		if (this.labelButtonPos == null)
			this.labelButtonPos = Messages.label_auto_01;

		lines = new ArrayList<AutoLine>();
		
		this.withCursor = withCursor;
	}

	@Override
	public void draw(Composite autoInputComposite, boolean filled) {
		Composite compo2 = drawLinesComposite(autoInputComposite, filled);

		if (defaultValueEnable) {
			drawDefaultValueComposite(compo2);
		}
	}

	protected Composite drawLinesComposite(Composite autoInputComposite, boolean filled) {
		Color backgroundEditColor = Nemo.getInstance().getViewMain().getBackgroundEdit();
		buttonPos = new Button(autoInputComposite, SWT.RADIO);
		buttonPos.setBackground(backgroundEditColor);
		buttonPos.setText(labelButtonPos);

		if (listenerAuto != null) {
			buttonPos.addSelectionListener(listenerAuto);
		}

		groupe = new Group(autoInputComposite, SWT.NONE);
		groupe.setBackground(backgroundEditColor);
		groupe.setText(Messages.label_position);
		groupe.setLayout(new GridLayout());

		GridDataFactory.fillDefaults().grab(true, false).applyTo(groupe);

		Composite compo1 = AutoLine.drawLineCompo(groupe);
		compo1.setBackground(backgroundEditColor);
		for (AutoLine line : lines) {
			line.drawLine(compo1, filled);
		}

		Composite compo2 = AutoLine.drawSetCompo(groupe, lines.size());
		compo2.setBackground(backgroundEditColor);
		for (int i = 0; i < lines.size(); i++) {
			lines.get(i).drawSet(compo2, i);
		}
		return compo2;
	}

	protected void drawDefaultValueComposite(Composite compo2) {
		checkBoxHasDefaultValue = new Button(compo2, SWT.CHECK);
		checkBoxHasDefaultValue.setText(Messages.label_defaultValue);
		checkBoxHasDefaultValue.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (checkBoxHasDefaultValue.getSelection()) {
					textDefaultValue.setEnabled(true);
				} else {
					textDefaultValue.setEnabled(false);
					textDefaultValue.setText("");
				}
				Utils.invalidEtapes();
			}

		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(checkBoxHasDefaultValue);

		textDefaultValue = new Text(compo2, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textDefaultValue);
		textDefaultValue.setEnabled(checkBoxHasDefaultValue.getSelection());
		textDefaultValue.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Utils.invalidEtapes();
			}

		});
	}

	@Override
	public void dispose() {
		buttonPos.dispose();
		groupe.dispose();
		for (AutoLine line : lines) {
			line.dispose();
		}
		if (checkBoxHasDefaultValue != null) {
			checkBoxHasDefaultValue.dispose();
		}
		if (textDefaultValue != null) {
		textDefaultValue.dispose();	
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		buttonPos.setSelection(enabled);
		for (AutoLine line : lines) {
			line.setEnabled(enabled);
		}

		if (defaultValueEnable) {
			checkBoxHasDefaultValue.setEnabled(enabled);
			if (checkBoxHasDefaultValue.getSelection() || !enabled) {
				textDefaultValue.setEnabled(enabled);
			}
		}
	}

	public boolean isSelected() {
		return buttonPos.getSelection();
	}

	@Override
	public void razValues() {
		for (AutoLine line : lines) {
			line.razValues();
		}

		if (textDefaultValue != null)
			textDefaultValue.setText("");

		if (checkBoxHasDefaultValue != null)
			checkBoxHasDefaultValue.setSelection(false);

	}

	public int nbLineFilled() {
		int cpt = 0;
		for (AutoLine line : lines) {
			if (!line.isEmpty())
				cpt++;
		}
		return cpt;
	}

	public void setEnableInData(MetaInData enableInData) {
		for (AutoLine line : lines) {
			line.setEnableInData(enableInData);
		}
	}

	/**
	 * Getter sur buttonPos.
	 * 
	 * @return the buttonPos
	 */
	public Button getButtonPos() {
		return buttonPos;
	}

	// pas utile pour l'instant
	public String getValue() {
		String result = Global.EMPTY_STRING;
		try {
			for (AutoLine line : lines) {
				if ((line.getValue() != null) && (!line.getValue().trim().equals(Global.EMPTY_STRING))) {
					result += line.getValue();
				}
			}
		} catch (StringIndexOutOfBoundsException e) {
			return null;
		}
		return result;
	}

	public String getValue(int i) {
		String result = Global.EMPTY_STRING;
		AutoLine line = lines.get(i);
		if ((line.getValue() != null) && (!line.getValue().trim().equals(Global.EMPTY_STRING))) {
			result = line.getValue();
		}
		return result;
	}

	/**
	 * Return the entire raw line at index i.
	 * 
	 * @param i the line index (cf. autoLine)
	 * 
	 * @return the raw line found
	 */
	public String getRawLine(int i) {

		AutoLine line = lines.get(i);
		return line.getRawLine();
	}

	public void setValue(boolean hasDefaultValue, boolean isDefaultValueSeceted, String defaultValue) {
		setEnabled(true);

		if (defaultValueEnable && hasDefaultValue) {
			textDefaultValue.setEnabled(isDefaultValueSeceted);

			if (checkBoxHasDefaultValue != null)
				checkBoxHasDefaultValue.setSelection(isDefaultValueSeceted);

			if (textDefaultValue != null)
				textDefaultValue.setText(defaultValue);
		}
	}

	/**
	 * Getter sur lines.
	 * 
	 * @return the lines
	 */
	public ArrayList<AutoLine> getLines() {
		return lines;
	}

	public boolean isInData() {
		boolean result = false;
		for (AutoLine line : lines) {
			result = (result || line.isInData());
		}
		return result;
	}

	public boolean checkAutoLine(int i, String nomSection) {
		AutoLine line = lines.get(i);
		return line.checkValues(nomSection);
	}

	public int calculNbreLignes(int i) {
		AutoLine line = lines.get(i);
		return line.calculNbreLignes();
	}

	public boolean checkPosition(int i, int nbreLignes) {
		AutoLine line = lines.get(i);
		return line.checkPosition(nbreLignes);
	}

	public void selectInViewFile() {
		AutoLine line = lines.get(0);
		line.selectInViewFile();
	}

	public boolean checkInDataOrHeader(int i, String lign, String sectionName) {
		AutoLine line = lines.get(i);
		return line.checkInDataOrHeader(lign, sectionName);
	}

	public boolean isDefaultValueEnable() {
		return defaultValueEnable;
	}

	public boolean isDefaultValueSelected() {
		if (checkBoxHasDefaultValue != null)
			return checkBoxHasDefaultValue.getSelection();

		return false;
	}

	public String getDefaultValue() {
		if (textDefaultValue != null)
			return textDefaultValue.getText();
		else
			return "";
	}
	
	public String getLabelButtonPos() {
		return labelButtonPos;
	}

	public SelectionListener getListenerAuto() {
		return listenerAuto;
	}
	
	public boolean isWithCursor() {
		return withCursor;
	}
	
	public StationSection getParentSection() {
		return parentSection;
	}
	
	public boolean hasVariablePos() {
		return variablePos;
	}

}
