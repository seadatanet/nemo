/*
 * ============================================================================
 * Nom du fichier : FileEndData.java Auteur : dboulang Date : 30 avr. 09
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.file;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_EndData;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: "Indicator of end of Data" Section in File Description
 * 
 * @author dboulang
 */
public class FileEndData extends FileSection
{
	protected Composite subCompo, col01, col02;
	protected Button radEOF, radEmptyLine, radBeginLineChanges, radNextStationBeginsWith, radManuel;
	protected Button radNextStationIsRegexpCheckbox;
	protected SetText setterStartStation;
	protected SetTextLength setterBeginLineChange;

	/**
	 * @param shell
	 * @param name
	 * @param title
	 * @param description
	 */
	public FileEndData(String name, String title, String description, EditFileForm form)
	{
		super(name, title, description, form);
		setterStartStation = new SetText(Messages.edit_file_termination_start, Global.EMPTY_STRING, true); //$NON-NLS-1$
		setterBeginLineChange = new SetTextLength(Messages.edit_file_beginLineChange, Global.EMPTY_STRING); //$NON-NLS-1$
	}

	@Override
	public void addSetter(String stringLabel, String defaultValue)
	{
		setter = new SetText(stringLabel, defaultValue, true);
	}

	private void addRadListener(final Button radio)
	{

		radio.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent g)
			{
				selectEndData(radio);
				Utils.invalidEtapes();

			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void drawSection(Composite parent, boolean filled)
	{
		compo = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compo);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compo);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(compo);

		// groups
		Group compoEndCurrent = new Group(compo, SWT.NONE);
		compoEndCurrent.setText(Messages.edit_file_endstation_group1);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compoEndCurrent);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(compoEndCurrent);

		Group compoNext = new Group(compo, SWT.NONE);
		compoNext.setText(Messages.edit_file_endstation_group2);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compoNext);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(compoNext);

		// fill groups
		// group1
		radEOF = new Button(compoEndCurrent, SWT.RADIO);
		radEOF.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		radEOF.setText(Messages.edit_file_idcEOF);

		radEmptyLine = new Button(compoEndCurrent, SWT.RADIO);
		radEmptyLine.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		radEmptyLine.setText(Messages.edit_file_idcEmptyLine);

		radManuel = new Button(compoEndCurrent, SWT.RADIO);
		radManuel.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		radManuel.setText(Messages.edit_file_idcManual);
		setter.draw(compoEndCurrent, filled);

		// group2

		radNextStationBeginsWith = new Button(compoNext, SWT.RADIO);
		radNextStationBeginsWith.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		radNextStationBeginsWith.setText(Messages.edit_file_idcNextStation);

		radNextStationIsRegexpCheckbox = new Button(compoNext, SWT.CHECK);
		radNextStationIsRegexpCheckbox.setText(Messages.edit_file_idcNextStationRegexp);
		radNextStationIsRegexpCheckbox.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		radNextStationIsRegexpCheckbox.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent g)
			{
				Utils.invalidEtapes();
			}
		});

		setterStartStation.draw(compoNext, filled);

		radBeginLineChanges = new Button(compoNext, SWT.RADIO);
		radBeginLineChanges.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		radBeginLineChanges.setText(Messages.edit_file_idcBeginLineChange);
		setterBeginLineChange.draw(compoNext, filled);

		selectEndData(radEOF);
		addRadListener(radEOF);
		addRadListener(radEmptyLine);
		addRadListener(radBeginLineChanges);
		addRadListener(radNextStationBeginsWith);
		addRadListener(radManuel);
	}

	@Override
	public boolean checkMandatory()
	{
		boolean ok = true;
		String log = Global.RN + "Section : " + title.trim() + Global.RN + "==>"; //$NON-NLS-1$  //$NON-NLS-2$

		if (setter != null && radManuel.getSelection())
		{
			ok = setter.checkMandatoryValue();
			log = log + Messages.error_file_idcEmpty;
		} else if (radNextStationBeginsWith.getSelection())
		{
			ok = setterStartStation.checkMandatoryValue();
			log = log + Messages.error_file_idcStartStationEmpty;
		} else if (radBeginLineChanges.getSelection())
		{
			ok = setterBeginLineChange.checkMandatoryValue();
			log = log + Messages.error_file_idcBeginLineChangeEmpty;
		}

		if (!ok)
			Utils.messageErreurAppend(log);

		return ok;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void razValues()
	{
		super.razValues();
		setterStartStation.razValues();
		setterBeginLineChange.razValues();
		selectEndData(radEOF);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose()
	{
		super.dispose();
		radEOF.dispose();
		radEmptyLine.dispose();
		radNextStationBeginsWith.dispose();
		radManuel.dispose();
		if (subCompo != null)
			subCompo.dispose();
		if (col01 != null)
			col01.dispose();
		if (col02 != null)
			col02.dispose();
	}

	public void selectEndData(Button radio)
	{
		radEOF.setSelection(false);
		radEmptyLine.setSelection(false);
		radBeginLineChanges.setSelection(false);
		radNextStationBeginsWith.setSelection(false);
		radManuel.setSelection(false);

		radNextStationIsRegexpCheckbox.setEnabled(false);
		setterStartStation.setEnabled(false);
		setterBeginLineChange.setEnabled(false);
		setter.setEnabled(false);

		if (radio != null)
		{
			if (radio == radEOF)
			{
				radEOF.setSelection(true);

			} else if (radio == radEmptyLine)
			{
				radEmptyLine.setSelection(true);

			} else if (radio == radBeginLineChanges)
			{
				radBeginLineChanges.setSelection(true);
				setterBeginLineChange.setEnabled(true);

			} else if (radio == radNextStationBeginsWith)
			{
				radNextStationBeginsWith.setSelection(true);
				radNextStationIsRegexpCheckbox.setEnabled(true);
				setterStartStation.setEnabled(true);

			} else if (radio == radManuel)
			{
				radManuel.setSelection(true);
				setter.setEnabled(true);

			} else
			{
				radEOF.setSelection(true);
			}

		}
	}

	/**
	 * Getter sur radEOF.
	 * 
	 * @return the radEOF
	 */
	public Button getRadEOF()
	{
		return radEOF;
	}

	/**
	 * Getter sur radLigne.
	 * 
	 * @return the radLigne
	 */
	public Button getRadLigne()
	{
		return radEmptyLine;
	}

	/**
	 * Getter sur radLigne.
	 * 
	 * @return the radLigne
	 */
	public Button getRadBeginLineChanges()
	{
		return radBeginLineChanges;
	}

	/**
	 * Getter sur radNextStation.
	 * 
	 * @return the radNextStation
	 */
	public Button getRadNextStation()
	{
		return radNextStationBeginsWith;
	}

	/**
	 * Getter sur radManuel.
	 * 
	 * @return the radManuel
	 */
	public Button getRadManuel()
	{
		return radManuel;
	}

	public boolean radEOFselected()
	{
		return radEOF.getSelection();
	}

	public boolean radLigneSelected()
	{
		return radEmptyLine.getSelection();
	}

	public boolean radNextStationSelected()
	{
		return radNextStationBeginsWith.getSelection();
	}

	public boolean radManuelSelected()
	{
		return radManuel.getSelection();
	}

	public String getStartStationIndicator()
	{
		return setterStartStation.getValue();
	}

	public void setStartStationIndicator(String value)
	{
		setterStartStation.setValue(value);
	}

	public String getBeginLineChangeIndicator()
	{
		return setterBeginLineChange.getValue();
	}

	public void setBeginLineChangeIndicator(String value)
	{
		setterBeginLineChange.setValue(value);
	}

	public void setStartStationIndicatorRegexpCheckbox(boolean isRegexp)
	{
		radNextStationIsRegexpCheckbox.setSelection(isRegexp);
	}

	public boolean getStartStationIndicatorRegexpCheckbox()
	{
		return radNextStationIsRegexpCheckbox.getSelection();
	}

	// TRADUCTION DES BOUTONS RADIO
	public E_EndData getEndData()
	{
		if (radNextStationBeginsWith.getSelection())
		{
			return E_EndData.NEXT_START_WITH;
		} else if (radManuel.getSelection())
		{
			return E_EndData.END_WITH;
		} else if (radEmptyLine.getSelection())
		{
			return E_EndData.EMPTY_LINE;
		} else if (radBeginLineChanges.getSelection())
		{
			return E_EndData.BEGIN_LINE_CHANGES;
		} else if (radEOF.getSelection())
		{
			return E_EndData.EOF;
		} else
		{
			// should not happend
			return E_EndData.EOF;
		}
	}

	public void setEndData(E_EndData end, String pattern, boolean isPatternRegexp)
	{
		switch (end)
		{
		case NEXT_START_WITH:
			selectEndData(getRadNextStation());
			setStartStationIndicator(pattern);
			setStartStationIndicatorRegexpCheckbox(isPatternRegexp);
			break;
		case END_WITH:
			selectEndData(getRadManuel());
			setValue(pattern);
			break;
		case EMPTY_LINE:
			selectEndData(getRadLigne());
			break;
		case BEGIN_LINE_CHANGES:
			selectEndData(getRadBeginLineChanges());
			setBeginLineChangeIndicator(String.valueOf(pattern.length()));
			break;
		default:
			selectEndData(getRadEOF());
			break;
		}
	}

	public void addSpaceBeforeDataTerminator()
	{
		setStartStationIndicator(Global.SPACE + getStartStationIndicator());
	}

	public String getIndicatorValue()
	{
		String indic = null;
		if (radManuelSelected())
		{
			indic = getValue();
		}
		return indic;
	}
}
