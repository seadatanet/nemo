package fr.ifremer.nemo.ui.edit.cruise;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import fr.ifremer.nemo.Nemo;
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
	protected List<Button> checkButtons;
	protected EditCruiseForm myForm;
	private final List<Boolean> checks;
	private final List<Boolean> checkDefaults;
	private final List<String> checksLabels;


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
	}

	public void addItem(String label, ComboType type, String defaultValue)
	{
		addItem(label, type, defaultValue, false, false, null);
	}
	public void addItem(String label, ComboType type, String defaultValue, boolean check, boolean checkDefaultValue, String checkLabel)
	{
		Couple item = new Couple(null, label, type, defaultValue);
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
									items.get(cpt).setText(Nemo.getInstance().getDomain().getCruise().getModel().getCorrespondingURLDefault());
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


	public int getItemIndex(String label){
		for ( int cpt=0; cpt<items.size(); cpt++){

			Couple item = items.get(cpt);
			// cas particulier des commentaires
			if (item.getType() == ComboType.BLOCK)
			{
				return cpt;
			} else if (item.getLabelString() != null
					&& item.getLabelString().equals(label))
			{
				return cpt;
			}
		}
		return -1;
	}

	public Couple getItem(int index){
		return items.get(index);
	}
	public Button getCheckBox(int index){
		return checkButtons.get(index);
	}
	public Couple getItem(String label)
	{
		//		for (Couple item : items)
		for ( int cpt=0; cpt<items.size(); cpt++){

			Couple item = items.get(cpt);
			// cas particulier des commentaires
			if (item.getType() == ComboType.BLOCK)
			{
				return item;
			} else if (item.getLabelString() != null
					&& item.getLabelString().equals(label))
			{
				return item;
			}
		}

		return null;
	}

	public List<Couple> getItems()
	{
		return items;
	}

	public boolean checkMandatory(String label)
	{
		boolean ok = true;
		Couple item = getItem(label);

		if (item.isMandatory())
		{
			ok = !item.isEmpty();
		}
		return ok;
	}

}
