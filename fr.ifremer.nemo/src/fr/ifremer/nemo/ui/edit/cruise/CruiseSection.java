/*
 * ============================================================================
 * Nom du fichier : SurveySection.java
 * Auteur : dboulang
 * Date : 28 mai 2009
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.cruise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.ui.edit.ComboType;
import fr.ifremer.nemo.ui.edit.Couple;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.util.Global;

/**
 * Description: objet graphique représentant une section dans l'onglet Campagne
 * 
 * @author dboulang
 */
public class CruiseSection extends NemoSection
{
	protected Composite compo;
	protected List<Couple> items;
	protected EditCruiseForm myForm;
	private final Map<itemID, Couple> itemsMap;
	/**
	 * allowed items IDs
	 * 
	 * @author altran
	 * 
	 */
	public enum itemID {
		REFERENCE, LOCAL_CSR_ID, BSH_CSR, NAME, SHIP, START_DATE, END_DATE, REGION, COUNTRY, LAB, CHIEF, DATA_CENTER, CONFIDENTIALITY, COMMENT
	}

	/**
	 * @param shell
	 * @param name
	 * @param title
	 * @param description
	 */
	public CruiseSection(String name, String title, String description, EditCruiseForm form)
	{
		super(name, title, description, form);
		myForm = form;
		items = new ArrayList<Couple>();
		itemsMap = new HashMap<CruiseSection.itemID, Couple>();
	}

	public void addItem(itemID id, String label, ComboType type, String defaultValue)
	{
		Couple item = new Couple(null, label, type, defaultValue);
		if (type == ComboType.BLOCK)
			item.setTextLimit(50000);
		items.add(item);
		itemsMap.put(id, item);
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


	public Couple getItem(itemID id) {
		return itemsMap.get(id);
	}
	public List<Couple> getItems()
	{
		return items;
	}
	public boolean checkMandatory(itemID id) {

		boolean ok = true;
		if (this.isEnabled()) {
			Couple item = itemsMap.get(id);
			if (item != null && item.isMandatory()) {
				ok = !item.isEmpty();
			}
		}
		return ok;
	}


}
