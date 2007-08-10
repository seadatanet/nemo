package fr.ifremer.nemo.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

public class UpdateAction extends Action implements ISelectionListener,
		ActionFactory.IWorkbenchAction {
	private final IWorkbenchWindow window;

	private Composite top = null;

	private Combo combo = null;

	private ComboViewer comboViewer = null;

	private Group group = null;

	private ComboViewer comboViewer1 = null;

	private Group group1 = null;

	private Text textArea = null;

	private Button button = null;

	private Group group2 = null;

	private Button button1 = null;

	private Button button2 = null;

	private Text textArea1, textArea2 = null;

	public final static String ID = "fr.ifremer.nemo.ui.UpdateAction"; //$NON-NLS-1$

	// vars nécessaires à la fenêtre d'options

	public static Shell shelloptions;

	public static int options;

	public UpdateAction(IWorkbenchWindow window) {

		this.window = window;
		setId(ID);
		setText(Messages.getString("UpdateAction.1")); //$NON-NLS-1$
		window.getSelectionService().addSelectionListener(this);

	}

	public void dispose() {
		window.getSelectionService().addSelectionListener(this);

	}

	public void run() {
		// System.out.println("Menu update WebServices"); //$NON-NLS-1$
		EditSurvey.webServiceUpdate();

	}

	/**
	 * This method initializes combo
	 * 
	 */
	private void createCombo() {
		combo = new Combo(group, SWT.FILL);
		combo.setText("Langue"); //$NON-NLS-1$
		combo.setBounds(new Rectangle(4, 28, 76, 21));

		combo.add("Français"); //$NON-NLS-1$
		combo.add("English"); //$NON-NLS-1$
	}

	private void createGroup() {
		group = new Group(top, SWT.NONE);
		group.setLayout(null);
		group.setText("Langue"); //$NON-NLS-1$
		createCombo();
		group.setBounds(new Rectangle(4, 4, 91, 69));
	}

	private void createGroup1() {
		group1 = new Group(top, SWT.NONE);
		group1.setLayout(null);
		group1.setText("Dossier des modèles"); //$NON-NLS-1$
		group1.setBounds(new Rectangle(105, 4, 178, 69));
		textArea = new Text(group1, SWT.MULTI | SWT.WRAP);
		textArea.setBounds(new Rectangle(6, 15, 163, 18));
		button = new Button(group1, SWT.NONE);
		button.setBounds(new Rectangle(110, 36, 59, 23));
		button.setText("Parcourir"); //$NON-NLS-1$
	}

	/**
	 * This method initializes group2
	 * 
	 */
	private void createGroup2() {
		group2 = new Group(top, SWT.NONE);
		group2.setLayout(null);
		group2.setText("Web Service"); //$NON-NLS-1$
		group2.setBounds(new Rectangle(4, 78, 278, 61));
		textArea1 = new Text(group2, SWT.MULTI | SWT.V_SCROLL);

		textArea1.setBounds(new Rectangle(5, 14, 267, 40));
		textArea2 = new Text(group2, SWT.MULTI | SWT.V_SCROLL);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
	}

	public void createPartControl(Composite parent) {

		top = new Composite(parent, SWT.NONE);
		top.setLayout(null);
		createGroup();
		createGroup1();
		createGroup2();
		button1 = new Button(top, SWT.NONE);
		button1.setBounds(new Rectangle(180, 143, 56, 23));
		button1.setText("Annuler"); //$NON-NLS-1$
		button2 = new Button(top, SWT.NONE);
		button2.setBounds(new Rectangle(62, 143, 56, 23));
		button2.setText("Valider"); //$NON-NLS-1$

		// listener valider
		button1
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
//						System.out.println("options valider");
						options = 0;
						shelloptions.close();

					}
				});

		// listener annuler
		button2
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
//						System.out.println("options annuler");
						options = 0;

						shelloptions.close();

					}
				});

	}

}
