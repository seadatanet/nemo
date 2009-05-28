/* ============================================================================
 * Nom du fichier	: CruiseDatatypeSection.java
 * Auteur			: dboulang
 * Date				: 28 mai 2009
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.survey;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: TODO
 * 
 * @author dboulang
 */
public class CruiseDatatypeSection extends NemoSection
{

    /**
     * @param shell
     * @param name
     * @param title
     * @param description
     */
    public CruiseDatatypeSection(Shell shell, String name, String title,
            String description)
    {
        super(shell, name, title, description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose()
    {
        super.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawSection(Composite parent, boolean filled)
    {
        TableDataSurvey.go(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillFromModel(StationModel stationModel)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not Implemented !");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initModel(StationModel stationModel)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not Implemented !");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void razValues()
    {
        // effacement de la liste des types de données dans la table
        TableDataSurvey.types = new ArrayList<TypeSurvey>();
        TableDataSurvey.majIHM();
    }

    public boolean chekMandatory()
    {
        boolean ok = true;

        if (TableDataSurvey.types.size() == 0)
        {
            Utils.messageErreurAppend(Messages.Etape_28);
            ok = false;
        }
        else
        {
            // vérif si pas de "nombre=0"
            int taille = TableDataSurvey.types.size();
            for (int i = 0; i < taille; i++)
            {
                int nbre = TableDataSurvey.types.get(i).getNombre();
                if (nbre == 0)
                {
                    Utils.messageErreurAppend(Messages.Etape_99);
                    ok = false;
                    break;
                }
            }

            // vérification au moins 1 "sensor"
            boolean atLeastOne = false;
            for (int i = 0; i < taille; i++)
            {
                if (TableDataSurvey.types.get(i).getSensor())
                {
                    atLeastOne = true;
                }
            }
            if (!atLeastOne)
            {
                Utils.messageErreurAppend(Messages.Etape_130);
                ok = false;
            }
        }
        return ok;
    }

}
