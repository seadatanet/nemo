package fr.ifremer.nemo.ui.view;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

public class NemoVersionDialog extends MessageDialog {

	public NemoVersionDialog(Shell parentShell, String dialogTitle,
			Image dialogTitleImage, String dialogMessage, int dialogImageType,
			String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
				dialogImageType, dialogButtonLabels, defaultIndex);
		
	}
	
	@Override
	protected Control createCustomArea(Composite parent) {
//		 Link link = new Link( parent, SWT.WRAP );
//		    link.setText( "<a href=\"https://www.seadatanet.org/Software/NEMO\">https://www.seadatanet.org/Software/NEMO</a>" );
		    final String linkURL="https://www.seadatanet.org/Software/NEMO";
		    Link link = new Link(parent, SWT.NONE);
		    link.setText( "<a href=\""+linkURL+"\">"+linkURL+"</a>" );
		     
		    // Event handling when users click on links.
		    link.addSelectionListener(new SelectionAdapter()  {
		     
		        @Override
		        public void widgetSelected(SelectionEvent e) {
		            Program.launch(linkURL);
		        }
		         
		    });
		    
		    
		    return link;
	}

}
