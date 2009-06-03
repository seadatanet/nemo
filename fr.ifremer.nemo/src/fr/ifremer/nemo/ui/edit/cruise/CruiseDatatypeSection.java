/* ============================================================================
 * Nom du fichier	: CruiseDatatypeSection.java
 * Auteur			: dboulang
 * Date				: 28 mai 2009
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.cruise;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.domain.DataType;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: objet graphique représentant la section "Type de données" dans
 * l'onglet Campagne
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
    public CruiseDatatypeSection(String name, String title, String description)
    {
        super(name, title, description);
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
        // TableDataSurvey.setSectionClientType(parent);
        // TableDataSurvey.tableTypeSurvey.setParent(parent);
    }

    public void fillFromModel(Survey survey)
    {
        DataType[] dataTypes = survey.getDataTypes();
        int taille = dataTypes.length;

        for (int i = 0; i < taille; i++)
        {
            TypeSurvey typeSurvey = new TypeSurvey();
            typeSurvey.setCode(dataTypes[i].getCode());
            typeSurvey.setNombre(dataTypes[i].getNumberOfProfiles());

            // QC
            if (dataTypes[i].isQc())
            {
                typeSurvey.setQc(Global.YES);
            }
            else
            {
                typeSurvey.setQc(Global.NO);
            }

            typeSurvey.setSensor(dataTypes[i].isSensor());

            TableDataSurvey.types.add(typeSurvey);
        }

        // refresh de la liste des dataTypes
        TableDataSurvey.majIHM();
    }

    public void initModel(Survey survey)
    {
        // remplissage de la liste des dataTypes de la campagne
        // parcours de la liste des dataTypes de l'IHM et creation d'un DataType
        // e chaque fois
        int taille = TableDataSurvey.types.size();

        // reset préventif des dataTypes
        survey.releaseDataTypes();

        for (int i = 0; i < taille; i++)
        {
            TypeSurvey typeSurvey = TableDataSurvey.types.get(i);
            DataType dataType = new DataType();
            dataType.setCode(typeSurvey.getCode());
            dataType.setNumberOfProfiles(typeSurvey.getNombre());

            if (typeSurvey.getQc().equals(Global.YES))
            {
                dataType.setQc(true);
            }
            else
            {
                dataType.setQc(false);
            }
            dataType.setSensor(typeSurvey.getSensor());

            // test par rapport aux types deja presents dans le DataModel
            // si l'etape a deje ete validee
            survey.addDataType(dataType);
        }
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
