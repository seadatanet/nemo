package fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ValidationCellLabelProvider extends CellLabelProvider {

	Color mandatoryColor = new Color(Display.getCurrent(), 202, 232, 213);
	boolean mandatory;

	/**
	 * Default constructor.
	 */
	public ValidationCellLabelProvider(boolean mandatory) {
		super();
		this.mandatory = mandatory;
	}

	@Override
	public void update(ViewerCell cell) {
		if (mandatory) {
		cell.setBackground(mandatoryColor); // Color for ValidationCellLabelProvider
		}
	}

}
