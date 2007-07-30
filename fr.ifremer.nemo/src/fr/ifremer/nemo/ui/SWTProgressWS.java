package fr.ifremer.nemo.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Shell;

public class SWTProgressWS extends ApplicationWindow{

	  public SWTProgressWS() {
		    super(null);
		  }
	
	public void run() {
	   
		 final Shell shell = new Shell();
	     
		  try {
	          new ProgressMonitorDialog(shell).run(true, false, new SWTProcessWS());
	        } catch (InvocationTargetException e) {
	         
	        } catch (InterruptedException e) {
	       
	        }
	    
	  }
	
	
}
