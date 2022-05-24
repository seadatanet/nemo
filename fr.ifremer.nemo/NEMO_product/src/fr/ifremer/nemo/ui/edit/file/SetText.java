/* ============================================================================
 * Nom du fichier	: SetText.java
 * Auteur			: dboulang
 * Date				: 16 avr. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.file;

import java.util.regex.Pattern;

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
public class SetText implements UIobject
{
    protected Label label;
    protected Text text;
    protected Button buttonSet;

    protected final String stringLabel;
    protected final String defaultValue;

    protected final boolean inText;
    protected int textSize;

    public SetText(String stringLabel, String defaultValue, final boolean inText)
    {
        this.textSize = Global.SIZE_SECTION;
        this.stringLabel = stringLabel;
        this.defaultValue = defaultValue;
        this.inText = inText;
    }

    protected void injectText()
    {
        EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
        text.setText(editFileForm.getSelectedText());
        text.pack();
    }

    protected void injectNumberLines()
    {
        int deb, fi, lignDepart, lignFin, i;
        EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
        // on recupere les coordonnees de la selection
        deb = editFileForm.getSelectedTextStartPosition();
        fi = editFileForm.getSelectedTextEndPosition();
        
        lignDepart = 1;
        lignFin = 1;
        // calcul de la ligne en fonction de la valeur de x
        // parcours du tableau des tailles de lignes cumulees

        if (deb == fi)
        {
            Utils.messageErreurAppend(Messages.Utils_26);

        }
        else
        {

            // calcul ligne depart
            for (i = 0; i < Utils.lignesBrutes.size(); i++)
            {

                if (deb == Utils.taillesCumLignes[i])
                {
                    lignDepart = i + 1;
                    break;
                }
                if (i == (Utils.lignesBrutes.size() - 1))
                {
                    lignDepart = i + 1;
                    break;
                }
                if ((deb >= Utils.taillesCumLignes[i])
                        && (deb < Utils.taillesCumLignes[i + 1]))
                {
                    lignDepart = i + 1;
                    break;
                }
            }

            // calcul ligne fin
            for (i = 0; i < Utils.lignesBrutes.size(); i++)
            {

                if (fi == Utils.taillesCumLignes[i])
                {
                    lignFin = i + 1;
                    break;
                }
                if (i == (Utils.lignesBrutes.size() - 1))
                {
                    lignFin = i + 1;
                    break;
                }
                if ((fi >= Utils.taillesCumLignes[i])
                        && (fi < Utils.taillesCumLignes[i + 1]))
                {
                    lignFin = i + 1;
                    break;
                }
            }
            // remplissage nbreLignes dans interface
            text.setText(String.valueOf((lignFin - lignDepart + 1)));
            text.pack();
        }
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
        text.addModifyListener(new ModifyListener() {
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
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent g)
                {
                    if (inText)
                    {
                        injectText();
                    }
                    else
                    {
                        injectNumberLines();
                    }

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
        if (inText)
        {
            if (text.getText().trim().equals(Global.EMPTY_STRING))
            {
                ok = false;
            }
        }
        else
        {
            if (!Pattern.matches("^[0-9][0-9]*$", text.getText())) { //$NON-NLS-1$
                ok = false;
            }
        }
        return ok;
    }

    public String getValue()
    {
        String value = text.getText();
        if (!inText)
        {
            value = value.trim();
        }
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
