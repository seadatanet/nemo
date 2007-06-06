package fr.ifremer.nemo.ui;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FillLayout;

public class ViewEdit extends ViewPart {

	public static final String ID = "fr.ifremer.nemo.ui.ViewEdit"; // TODO Needs to be whatever is mentioned in plugin.xml

	public static Composite top;
	// toolkit du formulaire global d'édition
	public static FormToolkit toolkitEdition;
	// formulaire d'édition
	public static ScrolledForm formEdition;

	
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Raccord de méthode auto-généré
		top = parent;
		//top = new Composite(parent, SWT.NONE);
	
		parent.setLayout(new FillLayout());
	
		toolkitEdition = new FormToolkit(parent.getDisplay());
		
			formEdition = toolkitEdition.createScrolledForm(parent);
		//formEdition.setText("init debut");
	
	
		
	}

	@Override
	public void setFocus() {
		// TODO Raccord de méthode auto-généré

	}

	
	
	
}
