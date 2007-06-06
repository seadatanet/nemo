package fr.ifremer.nemo.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

import fr.ifremer.nemo.input.InputModel;

public class OpenAction extends Action implements ISelectionListener, ActionFactory.IWorkbenchAction { //implements ISelectionListener, ActionFactory.IWorkbenchAction 
private final IWorkbenchWindow window;

public final static String ID = "fr.ifremer.nemo.ui.OpenAction";

//vars nécessaires à la fenêtre d'options
//public static 
public static Shell shelloptions;
public static String nomModele, nomFichierModele;

//private IStructuredSelection selection;

public OpenAction(IWorkbenchWindow window){
	
this.window = window;
setId(ID);
setText("Ouvrir un modèle");
window.getSelectionService().addSelectionListener(this);


}

public void dispose(){
	window.getSelectionService().addSelectionListener(this);
	
	
}



public void run(){
	FileInputStream stream = null;
	System.out.println("Menu open");
	
	//vérifier que un fichier est chargé avant d'ouvrir un modèle
	if (EditDesc.loaded ==0){
		Utils.messageErreur("Veuillez charger un fichier avant de sélectionner un modèle !");
	}
	else{
	
	
	InputModel inputModel = new InputModel();
	
	Shell shell = new Shell();
	FileDialog dialog = new FileDialog(shell, SWT.OPEN);
	nomModele = dialog.open();
	if (nomModele == null) {}else{
		
		//ouverture du fichier XML de modèle
		
		com.ago.xml.XMLBootstrapParser parser = new com.ago.xml.XMLBootstrapParser(inputModel);
    	
		try {
			stream = new java.io.FileInputStream(nomModele);
		} catch (FileNotFoundException e) {
			// TODO Bloc catch auto-généré
			e.printStackTrace();
		}
		System.out.println("Ouverture du modèle : "+nomModele);
		parser.parse(stream, false);

		File fich = new File(nomModele);
		nomFichierModele = fich.getName();
		
		DataModel.ouvertureModele(inputModel);
		DataModel.ouverture = true;
		
		
	}
	
	}
	
}

public void selectionChanged(IWorkbenchPart part, ISelection incoming){}






}
