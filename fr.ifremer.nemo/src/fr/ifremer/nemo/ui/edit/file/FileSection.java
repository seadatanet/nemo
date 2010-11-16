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
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: objet graphique représentant une section dans l'onglet File
 * 
 * @author dboulang
 */
public class FileSection extends NemoSection
{
    protected Composite compo;
    protected SetText setter;

    public FileSection(String name, String title, String description, EditFileForm form)
    {
        super(name, title, description, form);

    }

    public void addSetter(String stringLabel, String defaultValue)
    {
        setter = new SetText(stringLabel, defaultValue, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawSection(Composite parent, boolean filled)
    {
        compo = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compo);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(compo);

        setter.draw(compo, filled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void razValues()
    {
        if (setter != null)
            setter.razValues();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose()
    {
        super.dispose();
        if (compo != null)
            compo.dispose();
        if (setter != null)
            setter.dispose();
    }

    public boolean checkMandatory()
    {
        boolean ok = true;
        String log =
            Global.RN + "Section : " + title.trim() + Global.RN + "==>"; //$NON-NLS-1$  //$NON-NLS-2$

        if (setter != null)
        {
            ok = setter.checkMandatoryValue();
            log = log + Messages.error_notValid;
        }

        if (!ok)
            Utils.messageErreurAppend(log);

        return ok;
    }

    public String getValue()
    {
        return setter.getValue();
    }

    public void setValue(String value)
    {
        setter.setValue(value);
    }

}
