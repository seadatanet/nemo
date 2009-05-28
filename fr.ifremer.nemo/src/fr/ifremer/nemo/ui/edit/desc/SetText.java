/* ============================================================================
 * Nom du fichier	: SetText.java
 * Auteur			: dboulang
 * Date				: 16 avr. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.desc;

import java.util.regex.Pattern;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.UIobject;
import fr.ifremer.nemo.ui.view.ViewEdit;
import fr.ifremer.nemo.ui.view.ViewFile;
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
    private final String defaultValue;
    private final boolean inText;
    private int textSize;

    public SetText(Shell shell, String stringLabel, String defaultValue,
            final boolean inText)
    {
        this.textSize = 293;
        this.defaultValue = defaultValue;
        this.inText = inText;

        if (stringLabel != null)
        {
            label = new Label(shell, SWT.NONE);
            label.setText(stringLabel);
        }

        text = new Text(shell, SWT.BORDER);
        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event)
            {
                Utils.invalidEtapes();
            }
        });

        buttonSet = new Button(shell, SWT.NONE);
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

    protected void injectText()
    {
        text.setText(ViewFile.textAreaDescription.getText().substring(
            ViewFile.textAreaDescription.getSelection().x,
            ViewFile.textAreaDescription.getSelection().y));
        text.pack();
    }

    protected void injectNumberLines()
    {
        int deb, fi, lignDepart, lignFin, i;

        // on recupere les coordonnees de la selection
        deb = ViewFile.textAreaDescription.getSelection().x;
        fi = ViewFile.textAreaDescription.getSelection().y;

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
        if (label != null)
        {
            label.setParent(compo);
            label.setBackground(ViewEdit.background);
        }

        text.setParent(compo);
        if (defaultValue != null && !filled)
            text.setText(defaultValue);
        GridDataFactory.fillDefaults().grab(true, false).hint(textSize, 12)
            .applyTo(text);

        buttonSet.setParent(compo);
        buttonSet.setBackground(ViewEdit.background);
        buttonSet.setText(Messages.button_set);
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
