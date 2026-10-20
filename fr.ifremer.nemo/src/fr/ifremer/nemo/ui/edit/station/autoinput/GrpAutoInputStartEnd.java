package fr.ifremer.nemo.ui.edit.station.autoinput;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.station.StationSection;
import fr.ifremer.nemo.ui.edit.station.autoinput.autoline.AutoLine;
import fr.ifremer.nemo.ui.edit.station.autoinput.autoline.AutoLine.MetaInData;
import fr.ifremer.nemo.ui.edit.station.autoinput.autoline.AutoLineStartEnd;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Widget for automatic input (char mode)
 *
 */
public class GrpAutoInputStartEnd extends GrpAutoInput {

	private String variableLabel;
	protected Button buttonVari;

	public GrpAutoInputStartEnd(StationSection parentSection, int nbLine, String labelButtonPos, boolean variablePos, MetaInData inData,
			boolean withCursor, boolean defaultValueEnabled, SelectionListener listenerAuto) {
		super(parentSection, labelButtonPos, variablePos, defaultValueEnabled, listenerAuto, withCursor);
		for (int i = 0; i < nbLine; i++) {
			lines.add(new AutoLineStartEnd(this, i, (nbLine == 1), inData, withCursor));
		}

		this.variableLabel = Messages.label_variable;
	}

	public void setVariableLabel(String value) {
		this.variableLabel = value;
	}

	@Override
	public void draw(Composite autoInputComposite, boolean filled) {
		Composite compo2 = drawLinesComposite(autoInputComposite, filled);

		if (variablePos) {
			drawVariableComposite(compo2);
		}

		if (defaultValueEnable) {
			drawDefaultValueComposite(compo2);
		}
	}

	private void drawVariableComposite(Composite compo2) {
		Color backgroundEditColor = Nemo.getInstance().getViewMain().getBackgroundEdit();
		buttonVari = new Button(compo2, SWT.CHECK);
		buttonVari.setParent(compo2);
		buttonVari.setBackground(backgroundEditColor);
		buttonVari.setText(this.variableLabel);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.END, SWT.CENTER).applyTo(buttonVari);

		buttonVari.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent g) {
				disableEndVari();
				Utils.invalidEtapes();
			}
		});
	}

	@Override
	protected void drawDefaultValueComposite(Composite compo2) {
		// Ajout d'un filler pour l'alignement
		if (buttonVari == null)
			new Label(compo2, SWT.NONE);

		super.drawDefaultValueComposite(compo2);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (buttonVari != null)
			buttonVari.dispose();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		if (buttonVari != null)
			buttonVari.setEnabled(enabled);

	}

	@Override
	public void razValues() {
		super.razValues();

		if (buttonVari != null)
			buttonVari.setSelection(false);
	}

	public boolean isVariablePos() {
		boolean variable = false;
		if (buttonVari != null) {
			variable = buttonVari.getSelection();
		}
		return variable;
	}

	/**
	 * Getter sur buttonVari.
	 * 
	 * @return the buttonVari
	 */
	public Button getButtonVari() {
		return buttonVari;
	}

	@Override
	public String getValue() {
		String result = Global.EMPTY_STRING;
		try {
			boolean posVariable = isVariablePos();
			for (AutoLine line : lines) {
				AutoLineStartEnd lineStartEnd = (AutoLineStartEnd) line;
				if ((lineStartEnd.getValue(posVariable) != null)
						&& (!lineStartEnd.getValue(posVariable).trim().equals(Global.EMPTY_STRING))) {
					result += lineStartEnd.getValue(posVariable);
				}
			}
		} catch (StringIndexOutOfBoundsException e) {
			return null;
		}
		return result;
	}

	@Override
	public String getValue(int i) {
		boolean posVariable = isVariablePos();
		String result = Global.EMPTY_STRING;
		AutoLineStartEnd lineStartEnd = (AutoLineStartEnd) lines.get(i);
		if ((lineStartEnd.getValue(posVariable) != null)
				&& (!lineStartEnd.getValue(posVariable).trim().equals(Global.EMPTY_STRING))) {
			result = lineStartEnd.getValue(posVariable);
		}
		return result;
	}

	public void setValue(String line, String start, String end, boolean posVariable, boolean valueVariable, int i,
			boolean hasDefaultValue, boolean isDefaultValueSeceted, String defaultValue) {
		setEnabled(true);
		AutoLineStartEnd autoLine = (AutoLineStartEnd) lines.get(i);
		autoLine.setValue(line, start, end);

		if (posVariable)
			buttonVari.setSelection(valueVariable);

		super.setValue(hasDefaultValue, isDefaultValueSeceted, defaultValue);
	}

	public void disableEndVari() {
		if (buttonVari != null) {
			for (AutoLine line : lines) {
				((AutoLineStartEnd) line).getEnd().setEnabled(!buttonVari.getSelection());
			}
		}
	}

}
