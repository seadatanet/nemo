package fr.ifremer.nemo.ui.actions;

import java.awt.Desktop;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

public class UserManualAction extends Action implements ISelectionListener, ActionFactory.IWorkbenchAction {

	private final IWorkbenchWindow window;

	private final static String ID = "fr.ifremer.nemo.ui.UserManualAction"; //$NON-NLS-1$


	public UserManualAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText(Messages.usermanual_action);
        String urlString = "platform:/plugin/org.eclipse.ui/icons/full/etool16/help_contents.png"; //$NON-NLS-1$
        try {
			setImageDescriptor(ImageDescriptor.createFromURL(new URL(urlString)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		window.getSelectionService().addSelectionListener(this);
	}

	@Override
	public void dispose() {
		window.getSelectionService().addSelectionListener(this);
	}

	@Override
	public void run() {	
		String pdfFileName = Intern.dir_resources + File.separator + Intern.usermanual_file;
        try {

    		File pdfFile = new File(pdfFileName);
    		if (pdfFile.exists()) {

    			if (Desktop.isDesktopSupported()) {
    				Desktop.getDesktop().open(pdfFile);
    			} else {
    				System.out.println("Awt Desktop is not supported!");
    			}

    		} else {
    			System.out.println("File is not exists!");
    		}

    		System.out.println("Done");

    	  } catch (Exception ex) {
    		ex.printStackTrace();
    	  }

	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
	
}
