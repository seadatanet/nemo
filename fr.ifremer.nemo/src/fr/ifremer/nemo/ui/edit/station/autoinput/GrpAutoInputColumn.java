package fr.ifremer.nemo.ui.edit.station.autoinput;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import fr.ifremer.nemo.ui.edit.station.StationSection;
import fr.ifremer.nemo.ui.edit.station.autoinput.autoline.AutoLine.MetaInData;
import fr.ifremer.nemo.ui.edit.station.autoinput.autoline.AutoLineColumn;

/**
 * Widget for automatic input ( column mode)
 *
 */
public class GrpAutoInputColumn extends GrpAutoInput {

	public GrpAutoInputColumn(StationSection parentSection, int nbLine, String labelButtonPos,  boolean variablePos, MetaInData inData, boolean withCursor,
			boolean defaultValueEnabled, SelectionListener listenerAuto) {
		super(parentSection, labelButtonPos,variablePos, defaultValueEnabled, listenerAuto, withCursor);
		for (int i = 0; i < nbLine; i++) {
			lines.add(new AutoLineColumn(this, i, (nbLine == 1), inData, withCursor));
		}
	}

	public void setValue(String line, String column, int i, boolean hasDefaultValue, boolean isDefaultValueSeceted,
			String defaultValue) {
		setEnabled(true);
		AutoLineColumn autoLine = (AutoLineColumn) lines.get(i);
		autoLine.setValue(line, column);

		super.setValue(hasDefaultValue, isDefaultValueSeceted, defaultValue);
	}
	
	@Override
	protected void drawDefaultValueComposite(Composite compo2) {
		// Ajout d'un filler pour l'alignement
			new Label(compo2, SWT.NONE);

		super.drawDefaultValueComposite(compo2);
	}

}
