/*
 * ============================================================================
 * Nom du fichier : UIsection.java Auteur : dboulang Date : 28 janv. 09
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.util.Global;

/**
 * Description: classe abstraite définissant une Section NEMO
 * 
 * @author dboulang
 */
public abstract class NemoSection
{
	// constantes des noms possibles
	public static final String F_FILEHEADER = "enteteFichier"; //$NON-NLS-1$
	public static final String F_STATIONHEADER = "enteteStation"; //$NON-NLS-1$
	public static final String F_ENDSTATION = "finStation"; //$NON-NLS-1$
	public static final String F_ENDDATA = "indicFinDonnees"; //$NON-NLS-1$
	public static final String C_REFERENCE = "cuiseReference"; //$NON-NLS-1$
	public static final String C_LOCATION = "cruiseLocation"; //$NON-NLS-1$
	public static final String C_DATASOURCE = "cruiseDatasource"; //$NON-NLS-1$
	public static final String C_ARCHIVING = "cruiseArchiving"; //$NON-NLS-1$
	public static final String C_DATATYPE = "cruiseDatatype"; //$NON-NLS-1$
	public static final String C_COMMENTS = "cruiseComments"; //$NON-NLS-1$
	public static final String D_DATATYPE = "dataType"; //$NON-NLS-1$

	protected Section section;
	protected String name; // nom correspondant au tag xml (PropertyModel)
	protected String title;
	private final String description;
	// reference sur le formulaire d'edition auquel appartient la section
	protected EditForm myForm;

	public NemoSection(String name, String title, String description, EditForm form)
	{
		this(name, title, description, 40);
		myForm = form;
	}

	public NemoSection(String name, String title, String description, int padSize)
	{
		this.name = name;
		this.title = /* Formatter.getSingleton().padString( */title/* , padSize) */;
		this.description = description;
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose()
	{
		section.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	public void draw(FormToolkit toolkitEdition, final ScrolledForm formEdition, boolean filled)
	{
		section = toolkitEdition.createSection(formEdition.getBody(), Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);

		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(ExpansionEvent e)
			{
				formEdition.reflow(true);
			}
		});

		section.setText(title);
		section.setDescription(description);

		Composite sectionClient = toolkitEdition.createComposite(section);

		sectionClient.setLayout(new GridLayout());
		GridDataFactory.fillDefaults().grab(false, false).hint(Global.SIZE_SECTION, SWT.DEFAULT).applyTo(sectionClient);
		GridDataFactory.fillDefaults().grab(false, false).hint(Global.SIZE_SECTION, SWT.DEFAULT).applyTo(section);
		drawSection(sectionClient, filled);

		section.setClient(sectionClient);

		section.getParent().layout(true);
	}

	protected abstract void drawSection(Composite parent, boolean filled);

	/**
	 * {@inheritDoc}
	 */
	public abstract void razValues();

	/**
	 * {@inheritDoc}
	 */
	public void setEnabled(boolean enabled)
	{
		section.setEnabled(enabled);
		if (!enabled)
		{
			section.setExpanded(enabled);
			section.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		} else
		{
			section.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		}
	}

	public void setExpanded(boolean expanded)
	{
		section.setExpanded(expanded);
	}

	public boolean isEnabled()
	{
		return section.isEnabled();
	}

	public void initModel(StationModel stationModel)
	{
		// a implémenter dans les classes filles, mais pas obligatoire
	}

	public void fillFromModel(StationModel stationModel)
	{
		// a implémenter dans les classes filles, mais pas obligatoire
	}

	/**
	 * Getter sur name.
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	public Section getSection()
	{
		return section;
	}

	public String getTitle()
	{
		return title;
	}

}
