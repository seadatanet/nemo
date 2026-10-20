package fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


public class ValidationComboBoxViewerCellEditor extends
		CustomComboBoxViewerCellEditor {

	private final ControlDecoration firstNameDecoration;

	/**
	 * Default constructor.
	 */
	public ValidationComboBoxViewerCellEditor(Composite parent) {
		super(parent);
		Combo comboControl = (Combo) getControl();
		Color mandatoryColor = new Color(Display.getCurrent(), 202, 232, 213);
		comboControl.setBackground(mandatoryColor);
		firstNameDecoration = new ControlDecoration(comboControl, SWT.LEFT
				| SWT.TOP);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		firstNameDecoration.setImage(fieldDecoration.getImage());
		firstNameDecoration.hide();
		addListener(new ICellEditorListener() {
			@Override
			public void editorValueChanged(boolean oldValidState,
					boolean newValidState) {
				if (!newValidState) {
					firstNameDecoration.setDescriptionText(getErrorMessage());
					firstNameDecoration.show();
				} else {
					firstNameDecoration.hide();
				}
			}

			@Override
			public void cancelEditor() {
			}

			@Override
			public void applyEditorValue() {
				firstNameDecoration.hide();
			}
		});
	}
}