package fr.ifremer.nemo.ui;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

public class ViewEdit extends ViewPart {

	public static final String ID = "fr.ifremer.nemo.ui.ViewEdit";

	public static Composite top;

	// toolkit du formulaire global d'edition
	public static FormToolkit toolkitEdition;

	// formulaire d'edition
	public static ScrolledForm formEdition;

	@Override
	public void createPartControl(Composite parent) {
		top = parent;

		parent.setLayout(new FillLayout());

		toolkitEdition = new FormToolkit(parent.getDisplay());

		formEdition = toolkitEdition.createScrolledForm(parent);

	}

	@Override
	public void setFocus() {

	}

}
