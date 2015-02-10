package fr.ifremer.nemo.ui.edit.cruise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import fr.ifremer.nemo.ui.edit.ComboType;
import fr.ifremer.nemo.ui.edit.Couple;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.util.Global;

/**
 * Description: objet graphique représentant une section dans l'onglet Campagne
 * 
 * 
 * @author dboulang
 */
public class CruiseSection extends NemoSection
{
	protected Composite compo;
	protected List<Couple> items;
	protected List<Button> checkButtons;
	protected EditCruiseForm myForm;
	private final List<Boolean> checks;
	private final List<Boolean> checkDefaults;
	private final List<String> checksLabels;
	private final Map<itemID, Couple> itemsMap;


	/**
	 * allowed items IDs
	 * @author altran
	 *
	 */
	public enum itemID {REFERENCE, LOCAL_CSR_ID, NAME, SHIP,
		START_DATE, END_DATE, REGION,
		COUNTRY, LAB, CHIEF, PROJECT,
		DATA_CENTER, CONFIDENTIALITY,
		COMMENT}
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
		checkButtons = new ArrayList<Button>();
		checks = new ArrayList<Boolean>();
		checkDefaults = new ArrayList<Boolean>();
		checksLabels = new ArrayList<String>();
		itemsMap = new HashMap<CruiseSection.itemID, Couple>();
	}

	public void addItem(itemID id, String label, ComboType type, String defaultValue)
	{
		addItem(id, label, type, defaultValue, false, false, null);
	}
	public void addItem(itemID id, String label, ComboType type, String defaultValue, boolean check, boolean checkDefaultValue, String checkLabel)
	{
		Couple item = new Couple(null, label, type, defaultValue);
		itemsMap.put(id, item);
		if (type == ComboType.BLOCK)
			item.setTextLimit(50000);


		items.add(item);
		checks.add(check);
		checkDefaults.add(checkDefaultValue);
		checksLabels.add(checkLabel);

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
		Couple item;
		Group group ;
		for ( int cpt=0; cpt<items.size(); cpt++){
			//		for (Couple item : items)

			item = items.get(cpt);
			if (checks.get(cpt)){
				group = new Group(compo, SWT.NONE);
				GridLayoutFactory.fillDefaults().numColumns(1).applyTo(group);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
				item.draw(group, filled);
				Button check = new Button(group, SWT.CHECK);
				checkButtons.add(check);
				check.setSelection(checkDefaults.get(cpt));
				check.setText(checksLabels.get(cpt));
				check.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
				{
					@Override
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent g)
					{
						for ( int cpt=0; cpt<items.size(); cpt++){
							//		for (Couple item : items)

							Couple item = items.get(cpt);
							if (checks.get(cpt)){
								if (((Button)g.getSource()).getSelection()){
									items.get(cpt).setEnabled(false);
									//									items.get(cpt).setText(Nemo.getInstance().getDomain().getCruise().getModel().getCorrespondingURLDefault().toString());
								}else{
									items.get(cpt).setEnabled(true);
								}
							}
						}
					}
				});
			} else{
				item.draw(compo, filled);
			}


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

	public Button getCheckBox(int index){
		return checkButtons.get(index);
	}

	public Couple getItem(itemID id){
		return itemsMap.get(id);
	}


	public List<Couple> getItems()
	{
		return items;
	}
	public boolean checkMandatory(itemID id){

		boolean ok = true;
		if (this.isEnabled()){
			Couple item = itemsMap.get(id);
			if (item!=null && item.isMandatory())
			{
				ok = !item.isEmpty();
			}
		}
		return ok;
	}

}
