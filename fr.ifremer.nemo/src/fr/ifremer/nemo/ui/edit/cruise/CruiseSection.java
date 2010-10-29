/* ============================================================================
 * Nom du fichier	: SurveySection.java
 * Auteur			: dboulang
 * Date				: 28 mai 2009
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.cruise;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.ComboType;
import fr.ifremer.nemo.ui.edit.Couple;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: objet graphique représentant une section dans l'onglet Campagne
 * 
 * @author dboulang
 */
public class CruiseSection extends NemoSection
{
    protected Composite compo;
    protected List<Couple> items;

    /**
     * @param shell
     * @param name
     * @param title
     * @param description
     */
    public CruiseSection(String name, String title, String description)
    {
        super(name, title, description);
        items = new ArrayList<Couple>();
    }

    public void addItem(String label, ComboType type, String defaultValue)
    {
        Couple item = new Couple(null, label, type, defaultValue);
        if (type == ComboType.BLOCK)
            item.setTextLimit(50000);
        items.add(item);
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
        for (Couple item : items)
        {
            item.dispose();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawSection(Composite parent, boolean filled)
    {
        compo = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compo);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo);

        for (Couple item : items)
        {
            item.draw(compo, filled);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void razValues()
    {
        for (Couple item : items)
        {
            item.razValues();
            // FAE 3749 cas particulier des commentaires
            if (item.getType() == ComboType.BLOCK)
            {
                GridDataFactory.fillDefaults().grab(true, true).hint(Global.SIZE_SECTION, SWT.DEFAULT).minSize(Global.SIZE_SECTION, 200)
                .applyTo(item.getValueText());
            }
        }
    }

    public Couple getItem(String label)
    {
        for (Couple item : items)
        {
            // cas particulier des commentaires
            if (item.getType() == ComboType.BLOCK)
            {
                return item;
            }
            else if (item.getLabelString() != null
                    && item.getLabelString().equals(label))
            {
                return item;
            }
        }

        return null;
    }

    public boolean checkMandatory(String label)
    {
        boolean ok = true;
        Couple item = getItem(label);

        if (item == null || item.getText().equals(Global.EMPTY_STRING))
        {
            Utils.messageErreurAppend(Messages.Regexp_4 + getTitle() + " / " //$NON-NLS-1$
                    + label + Messages.Regexp_5);
            ok = false;
        }

        return ok;
    }

}
