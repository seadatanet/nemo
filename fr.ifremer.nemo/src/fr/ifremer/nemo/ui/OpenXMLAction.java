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

public class OpenXMLAction extends Action implements ISelectionListener, ActionFactory.IWorkbenchAction { //implements ISelectionListener, ActionFactory.IWorkbenchAction 
private final IWorkbenchWindow window;

public final static String ID = "fr.ifremer.nemo.ui.OpenXMLAction";

//vars nécessaires à la fenêtre d'options
//public static 
public static Shell shelloptions;
public static String nomXML, nomFichierXML;

public OpenXMLAction(IWorkbenchWindow window){
	
	this.window = window;
	setId(ID);
	setText("Ouvrir Description XML");
	window.getSelectionService().addSelectionListener(this);


	}


public void dispose(){
	window.getSelectionService().addSelectionListener(this);
	
	
}


public void run(){
	
EditSurvey.ouvertureDescriptionXML();
	
}

public void selectionChanged(IWorkbenchPart part, ISelection incoming){}

}
