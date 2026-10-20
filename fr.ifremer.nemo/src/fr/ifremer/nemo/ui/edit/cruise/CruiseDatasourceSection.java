/* ============================================================================
 * Nom du fichier	: CruiseDatatypeSection.java
 * Auteur			: pmahoudo
 * Date				: novembre 2022
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.cruise;

import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.config.EdmerpEntity;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.input.CruiseModel;

/**
 * Description: objet graphique représentant la section "Source de données" dans
 * l'onglet Campagne
 * 
 * @author pmahoudo
 */
public class CruiseDatasourceSection extends CruiseSection
{
    
	protected TableEdmerp tableEdmerp;
    
    
    /**
     * @param shell
     * @param name
     * @param title
     * @param description
     */
    public CruiseDatasourceSection(String name, String title, String description, EditCruiseForm form)
    {
        super(name, title, description, form);
        tableEdmerp = new TableEdmerp();
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
    	super.drawSection(parent, filled);
    	
    	tableEdmerp.go(parent);

    }

    public void fillFromModel(CruiseModel cruise)
    {
    	TableEdmerp.edmerpList.clear();

    	String edmerpCodes = cruise.getEdmerpCodes();
    	if (!edmerpCodes.isEmpty()) {
    		String[] edmerpCodesArray = edmerpCodes.split(";");
    		int taille = edmerpCodesArray.length;


    		for (int i = 0; i < taille; i++)
    		{
    			String edmerpCode = edmerpCodesArray[i];

    			for(EdmerpEntity eeWS : NemoLists.getEdmerpList()) {
    				if (eeWS.getCode().equals(Integer.valueOf(edmerpCode))) {
    					TableEdmerp.edmerpList.add(eeWS);
    					break;
    				}
    			}
    		}


    	}
    	// refresh de la liste des edmerp
    	tableEdmerp.getViewer().refresh();
    	tableEdmerp.updateCruiseModelEdmerpCodes();
    }

    public void initModel(CruiseModel cruise)
    {
        // remplissage de la liste des codes Edmerp de la campagne
        // parcours de la liste des codes Edmerp de l'IHMd
        int taille = TableEdmerp.edmerpList.size();

        for (int i = 0; i < taille; i++)
        { 	
        	EdmerpEntity ee = TableEdmerp.edmerpList.get(i);
        	
        	if (cruise.getEdmerpCodes().isEmpty()) {
        		cruise.setEdmerpCodes(String.valueOf(ee.getCode()));
        	} else {
        		cruise.addEdmerpCodes(String.valueOf(ee.getCode()));
        	}
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void razValues()
    {
    	super.razValues();
    	
        // effacement de la liste des codes Edmerp dans la table
    	TableEdmerp.edmerpList.clear();
    	tableEdmerp.getViewer().refresh();
    }

}
