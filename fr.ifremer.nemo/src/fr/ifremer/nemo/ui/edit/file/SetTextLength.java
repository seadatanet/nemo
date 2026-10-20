/*
 * ============================================================================
 * Nom du fichier : SetText.java
 * Auteur : dboulang
 * Date : 16 avr. 09
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.file;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.UIobject;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: objet graphique contenant l'injection d'un texte ou d'un nombre
 * de lignes
 * 
 * @author dboulang
 */
public class SetTextLength implements UIobject
{
	protected Label label;
	protected Text text;
	protected Button buttonSet;

	private final String stringLabel;
	private final String defaultValue;

	private int textSize;

	public SetTextLength(String stringLabel, String defaultValue)
	{
		this.textSize = Global.SIZE_SECTION;
		this.stringLabel = stringLabel;
		this.defaultValue = defaultValue;
	}

	protected void injectTextSize()
	{
		EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
		text.setText(String.valueOf(editFileForm.getSelectedText().length()));
		text.pack();
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose()
	{
		if (label != null)
			label.dispose();
		text.dispose();
		buttonSet.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	public void draw(Composite compo, boolean filled)
	{
		if (stringLabel != null)
		{
			label = new Label(compo, SWT.WRAP);
			label.setText(stringLabel);
			label.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
			GridDataFactory.fillDefaults().grab(false, false).hint(textSize, SWT.DEFAULT)
					.applyTo(label);
		}

		text = new Text(compo, SWT.BORDER);
		if (defaultValue != null && !filled)
			text.setText(defaultValue);
		text.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent event)
			{
				Utils.invalidEtapes();
			}
		});

		GridDataFactory.fillDefaults().grab(false, false).hint(textSize, 12)
				.applyTo(text);

		buttonSet = new Button(compo, SWT.NONE);
		buttonSet.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		buttonSet.setText(Messages.button_set);
		buttonSet
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
				{
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g)
				{
					injectTextSize();

				}
				});
	}

	/**
	 * {@inheritDoc}
	 */
	public void razValues()
	{
		text.setText(defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setEnabled(boolean enabled)
	{
		if (label != null)
			label.setEnabled(enabled);
		text.setEnabled(enabled);
		buttonSet.setEnabled(enabled);
	}

	public boolean checkMandatoryValue()
	{
		boolean ok = true;
		if (text.getText().trim().equals(Global.EMPTY_STRING))
		{
			ok = false;
		}
		return ok;
	}

	public String getValue()
	{
		String value = text.getText();
		value = value.trim();
		return value;
	}

	public void setValue(String value)
	{
		text.setText(value);
	}

	public void setTextSize(int textSize)
	{
		this.textSize = textSize;
	}

}
