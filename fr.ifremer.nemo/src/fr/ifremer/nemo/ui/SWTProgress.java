package fr.ifremer.nemo.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.output.ProgressNotification;

public class SWTProgress extends ApplicationWindow{
	public static SWTProcess swtProcess;
	public static ProgressNotification notifier;
	
	
	  public SWTProgress() {
		    super(null);
		  }
	
	public void run() {
	   
		 final Shell shell = new Shell();
	    
		 if (ViewFile.odv.getSelection()){SWTProcess.odv = true;}
		 else{SWTProcess.odv = false;}
		 
		  try {
			  
			  swtProcess = new SWTProcess();
	          new ProgressMonitorDialog(Application.display.getActiveShell()).run(true, true, swtProcess);
	        } catch (InvocationTargetException e) {
	         
	        } catch (InterruptedException e) {
	        	
	        }
	    
	  }
	
	
}
