package fr.ifremer.nemo.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

public class OptionsAction extends Action implements ISelectionListener, ActionFactory.IWorkbenchAction { //implements ISelectionListener, ActionFactory.IWorkbenchAction 

private final IWorkbenchWindow window;

public static Composite top = null;
public static Group groupLangue, groupWS, groupDossier = null;
public static Combo comboLangue = null;
public static Button buttonCancel,buttonValider, buttonParcourir  = null;
public static Label labelNemo, labelNautilus = null;
public static Text textAreaNemo, textAreaNautilus, textDossier = null;

public final static String ID = "fr.ifremer.nemo.ui.OptionsAction";

//vars nécessaires à la fenêtre d'options
//public static 
public static Shell shelloptions;
public static int options;

//private IStructuredSelection selection;

public OptionsAction(IWorkbenchWindow window){
	
this.window = window;
setId(ID);
setText("Configurer");
window.getSelectionService().addSelectionListener(this);


}

public void dispose(){
	window.getSelectionService().addSelectionListener(this);

	
	
}



public void run(){
	//lancement IHM options
	System.out.println("Menu options");
	
	shelloptions = new Shell(Application.display, SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);

	//shelloptions = new Shell();
	shelloptions.setSize(400, 400);
	shelloptions.setText("Configuration de Nemo");
	shelloptions.setLayout(new GridLayout());
	//top = new Composite(shelloptions, SWT.NONE);

	createPartControl(shelloptions);
	//shelloptions.redraw();
	shelloptions.pack();
	shelloptions.open();
	options =1;
	
//	chargement des options
	Utils.initConfig();
	
	remplissageOptions();
	
}


public void remplissageOptions(){
//	 remplissage IHM avec les valeurs du fichier de config
	
	//DOSSIER
	textDossier.setText(Utils.configuration.getModelsDirectory());
		
	//LANGUE
	if (Utils.configuration.getLocale().toString().matches("fr_FR")){
		//on met la combo langue à FR
		comboLangue.select(0);
	} else{
		//on met la combo langue à GB
		comboLangue.select(1);
	}
	
	//WS
	textAreaNemo.setText(Utils.configuration.getNemoWebServiceURL());
	textAreaNautilus.setText(Utils.configuration.getNautilusWebServiceURL());
	
}


public void selectionChanged(IWorkbenchPart part, ISelection incoming){}

/**
 * This method initializes groupDossier	
 *
 */
private void createGroupDossier() {
	groupDossier = new Group(top, SWT.NONE);
	groupDossier.setLayout(null);
	groupDossier.setText("Dossier des modèles");
	groupDossier.setBounds(new Rectangle(176, 10, 242, 65));
	buttonParcourir = new Button(groupDossier, SWT.NONE);
	buttonParcourir.setBounds(new Rectangle(170, 38, 64, 23)); //64
	buttonParcourir.setText("Parcourir");
	buttonParcourir.pack();
	textDossier = new Text(groupDossier, SWT.BORDER);
	textDossier.setBounds(new Rectangle(5, 16, 229, 19));
	
	//listener sur le bouton
	buttonParcourir
	.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			System.out.println("parcourir"); 
			
			
		String nomRepertoire;
	    Shell shell = new Shell();
		DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
		nomRepertoire = dialog.open();
		if (nomRepertoire != null){
			textDossier.setText(nomRepertoire);

			}
		else { 
			
			System.out.println("pas de dossier sélectionné");
			ViewLog.styledTextLogErreurs.setText("\nVeuillez sélectionner un dossier pour continuer.");
			
		}

		}
	});
	
	
}




/**
 * This method initializes groupLangue	
 *
 */
private void createGroupLangue() {
	groupLangue = new Group(top, SWT.NONE);
	groupLangue.setLayout(null);
	groupLangue.setText("Choix de langue");
	createComboLangue();
	groupLangue.setBounds(new Rectangle(4, 10, 168, 65)); //166
	
}

/**
 * This method initializes comboLangue	
 *
 */
private void createComboLangue() {
	comboLangue = new Combo(groupLangue, SWT.NONE);
	comboLangue.setBounds(new Rectangle(2, 16, 160, 21));
	
	comboLangue.add("Français");
	comboLangue.add("English");
	comboLangue.select(0);
}

/**
 * This method initializes groupWS	
 *
 */
private void createGroupWS() {
	groupWS = new Group(top, SWT.NONE);
	groupWS.setLayout(null);
	groupWS.setText("Web Services");
	groupWS.setBounds(new Rectangle(4, 79, 414, 146));
	labelNemo = new Label(groupWS, SWT.NONE);
	labelNemo.setBounds(new Rectangle(13, 40, 62, 13));
	labelNemo.setText("NEMO");
	labelNemo.pack();
	labelNautilus = new Label(groupWS, SWT.NONE);
	labelNautilus.setBounds(new Rectangle(13, 100, 62, 13));
	labelNautilus.setText("NAUTILUS");
	labelNautilus.pack();
	textAreaNemo = new Text(groupWS, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
	textAreaNemo.setBounds(new Rectangle(85, 18, 316, 55));
	textAreaNautilus = new Text(groupWS, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
	textAreaNautilus.setBounds(new Rectangle(85, 82, 316, 55));
}

public void createPartControl(Composite parent) {


	top = new Composite(parent, SWT.NONE);
	top.setLayout(null);
	createGroupDossier();
	createGroupLangue();
	createGroupWS();
	buttonCancel = new Button(top, SWT.NONE);
	buttonCancel.setBounds(new Rectangle(224, 233, 69, 23)); //66
	buttonCancel.setText("Annuler");
	buttonCancel.pack();
	
	buttonValider = new Button(top, SWT.NONE);
	buttonValider.setBounds(new Rectangle(139, 233, 69, 23));
	buttonValider.setText("Valider");
	buttonValider.pack();
	
	
	
	
	
	//listener cancel
	buttonCancel.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			System.out.println("options cancel"); // TODO Auto-generated Event stub widgetSelected()
			
			shelloptions.close();
			
		}
	});

	//listener valider
	buttonValider.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			System.out.println("options valider"); // TODO Auto-generated Event stub widgetSelected()
			
			//sauvegarde de la config
			Utils.saveConfig();
			
			shelloptions.close();
			
		}
	});
	
	

}


}
