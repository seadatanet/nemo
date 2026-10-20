/* ============================================================================
 * Nom du fichier	: FileSection.java
 * Auteur			: dboulang
 * Date				: 19 mars 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.file;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: objet graphique représentant une section dans l'onglet File.
 * Cette section permet de choisir l'emplacement des titres de colonnes de
 * données pour les fichiers csv.
 * 
 * @author sbregent
 */
public class FileDataColumnTitlesSection extends FileSection {
	protected Composite composite;
	protected Button useTableForCsvButton;
	protected Button useColTitlesInInputFileButton;
	protected SetText columnTitleSetter;
	protected Button useP09CodeInInputFileButton;
	protected SetText p09CodeSetter;
	protected Button useP01CodeInInputFileButton;
	protected SetText p01CodeSetter;

	public FileDataColumnTitlesSection(String name, String title, String description, EditFileForm form) {
		super(name, title, description, form);

	}

	@Override
	public void addSetter(String stringLabel, String defaultValue) {
//        setter = new SetTextLineNb(stringLabel, defaultValue, false);
		columnTitleSetter = new SetTextMultipleLineNb(stringLabel, defaultValue, false);
		p09CodeSetter = new SetTextLineNb(stringLabel, defaultValue, false);
		p01CodeSetter = new SetTextLineNb(stringLabel, defaultValue, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void drawSection(Composite parent, boolean filled) {
		composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(composite);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(composite);

		useTableForCsvButton = new Button(composite, SWT.CHECK);
		useTableForCsvButton.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		useTableForCsvButton.setText(Messages.edit_file_columnTitlesSectionOnOff);
		useTableForCsvButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent g) {
				if (!useTableForCsvButton.getSelection()) {
					useColTitlesInInputFileButton.setSelection(false);
				}
				Nemo.getInstance().getViewMain().getEditDataForm().setMustReinitInputData(true);
				Utils.invalidEtapes();
			}
		});

		// Column Title

		useColTitlesInInputFileButton = new Button(composite, SWT.CHECK);
		useColTitlesInInputFileButton.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		useColTitlesInInputFileButton.setText(Messages.edit_file_columnTitlesSection);
		useColTitlesInInputFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent g) {
				Utils.invalidEtapes();
				columnTitleSetter.setEnabled(useColTitlesInInputFileButton.getSelection());
				// will redraw the input data without/without title columns
				Nemo.getInstance().getViewMain().getEditDataForm().setMustReinitInputData(true);
			}
		});

		columnTitleSetter.draw(composite, filled);
		columnTitleSetter.getText().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				Utils.invalidEtapes();
				// will redraw the input data without/without title columns
				Nemo.getInstance().getViewMain().getEditDataForm().setMustReinitInputData(true);
			}
		});

		columnTitleSetter.setEnabled(false);

		// P09 Code Param
		useP09CodeInInputFileButton = new Button(composite, SWT.CHECK);
		useP09CodeInInputFileButton.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		useP09CodeInInputFileButton.setText(Messages.edit_file_p09CodeSection);
		useP09CodeInInputFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent g) {
				Utils.invalidEtapes();
				p09CodeSetter.setEnabled(useP09CodeInInputFileButton.getSelection());
				// will redraw the input data without/without title columns
				Nemo.getInstance().getViewMain().getEditDataForm().setMustReinitInputData(true);
			}
		});

		p09CodeSetter.draw(composite, filled);
		p09CodeSetter.getText().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				Utils.invalidEtapes();
				// will redraw the input data without/without title columns
				Nemo.getInstance().getViewMain().getEditDataForm().setMustReinitInputData(true);
			}
		});

		p09CodeSetter.setEnabled(false);

		// P01 Code Param
		useP01CodeInInputFileButton = new Button(composite, SWT.CHECK);
		useP01CodeInInputFileButton.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		useP01CodeInInputFileButton.setText(Messages.edit_file_p01CodeSection);
		useP01CodeInInputFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent g) {
				Utils.invalidEtapes();
				p01CodeSetter.setEnabled(useP01CodeInInputFileButton.getSelection());
				// will redraw the input data without/without title columns
				Nemo.getInstance().getViewMain().getEditDataForm().setMustReinitInputData(true);
			}
		});

		p01CodeSetter.draw(composite, filled);
		p01CodeSetter.getText().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				Utils.invalidEtapes();
				// will redraw the input data without/without title columns
				Nemo.getInstance().getViewMain().getEditDataForm().setMustReinitInputData(true);
			}
		});

		p01CodeSetter.setEnabled(false);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void razValues() {
		useTableForCsvButton.setSelection(true);

		useColTitlesInInputFileButton.setSelection(false);
		columnTitleSetter.razValues();
		columnTitleSetter.setEnabled(useColTitlesInInputFileButton.getSelection());	

		useP09CodeInInputFileButton.setSelection(false);
		p09CodeSetter.razValues();
		p09CodeSetter.setEnabled(useP09CodeInInputFileButton.getSelection());
		p09CodeSetter.setEnabled(useP09CodeInInputFileButton.getSelection());
		useP01CodeInInputFileButton.setSelection(false);
		p01CodeSetter.razValues();
		p01CodeSetter.setEnabled(useP01CodeInInputFileButton.getSelection());
		p01CodeSetter.setEnabled(useP01CodeInInputFileButton.getSelection());
	}
	
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (composite != null)
			composite.dispose();
		if (columnTitleSetter != null)
			columnTitleSetter.dispose();
		if (p09CodeSetter != null)
			p09CodeSetter.dispose();
		if (p01CodeSetter != null)
			p01CodeSetter.dispose();
	}

	@Override
	public boolean checkMandatory() {
		boolean ok = true;
		String log = Global.RN + "Section : " + title.trim() + Global.RN + "==>"; //$NON-NLS-1$ //$NON-NLS-2$

		if (columnTitleSetter != null && useColTitlesInInputFileButton.getSelection()) {
			ok = ok & columnTitleSetter.checkMandatoryValue();
			log = log + Messages.error_notValid;
		}

		if (p09CodeSetter != null && useP09CodeInInputFileButton.getSelection()) {
			ok = ok & p09CodeSetter.checkMandatoryValue();
			log = log + Messages.error_notValid;
		}
		if (p01CodeSetter != null && useP01CodeInInputFileButton.getSelection()) {
			ok = ok & p01CodeSetter.checkMandatoryValue();
			log = log + Messages.error_notValid;
		}

		if (!ok)
			Utils.messageErreurAppend(log);

		return ok;
	}

	@Override
	public String getValue() {
		return columnTitleSetter.getValue();
	}

	public String getP09CodeValue() {
		return p09CodeSetter.getValue();
	}

	public String getP01CodeValue() {
		return p01CodeSetter.getValue();
	}

	@Override
	public void setValue(String value) {
		columnTitleSetter.setValue(value);
	}

	public void setP09CodeValue(String value) {
		p09CodeSetter.setValue(value);
	}

	public void setP01CodeValue(String value) {
		p01CodeSetter.setValue(value);
	}

	public boolean isColTitlesInStationHeader() {
		return useColTitlesInInputFileButton.getSelection();
	}

	public boolean isP09CodeInStationHeader() {
		return useP09CodeInInputFileButton.getSelection();
	}

	public boolean isP01CodeInStationHeader() {
		return useP01CodeInInputFileButton.getSelection();
	}

	public void setColTitlesInInputFile(boolean select) {
		useColTitlesInInputFileButton.setSelection(select);
	}

	public void setP09CodeInInputFile(boolean select) {
		useP09CodeInInputFileButton.setSelection(select);
	}

	public void setP01CodeInInputFile(boolean select) {
		useP01CodeInInputFileButton.setSelection(select);
	}

	public boolean isTableEnabledForCsv() {
		return useTableForCsvButton.getSelection();
	}

	public void setTableEnabledForCsv(boolean select) {
		useTableForCsvButton.setSelection(select);
	}

	public void enableColumnTitlesSetter(boolean enabled) {
		columnTitleSetter.setEnabled(enabled);
	}

	public void enableP09CodeSetter(boolean enabled) {
		p09CodeSetter.setEnabled(enabled);
	}

	public void enableP01CodeSetter(boolean enabled) {
		p01CodeSetter.setEnabled(enabled);
	}
}
