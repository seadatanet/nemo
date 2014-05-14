package fr.ifremer.nemo.ui.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.ui.Application;

public class UpdateViewer {
	Shell shell ;
	Composite compo;
	Text message;
	
	
	public UpdateViewer(){

	}

	public void display(String updateMessage) {
		shell = new Shell(Application.display);
		shell.setLayout(new FillLayout());
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		shell.setLayout(gridLayout);
		shell.setText("WebServices Info");
		compo = new Composite(shell, 0);
	
		message = new Text(compo, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		message.setLayoutData(new GridData(GridData.FILL_BOTH));

		message.setEditable(false);
		message.setText(updateMessage);
		message.setSize(600, 400);

		shell.setSize(600, 500);


		Button ok = new Button(shell, SWT.NONE);
		ok.setText("OK");
		ok.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});


		shell.open();
	}
	//	public static void main(String[] args) {
	//	    Shell shell = new Shell(Application.display);
	//	    shell.setLayout(new FillLayout());
	//
	//	    // this button has a minimum size of 400 x 400. If the window is resized to be big
	//	    // enough to show more than 400 x 400, the button will grow in size. If the window
	//	    // is made too small to show 400 x 400, scrollbars will appear.
	//	    ScrolledComposite c2 = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
	//	    Button b2 = new Button(c2, SWT.PUSH);
	//	    b2.setText("expanding button");
	//	    c2.setContent(b2);
	//	    c2.setExpandHorizontal(true);
	//	    c2.setExpandVertical(true);
	//	    c2.setMinWidth(400);
	//	    c2.setMinHeight(400);
	//
	//	    shell.setSize(600, 300);
	//	    shell.open();
	//	    while (!shell.isDisposed()) {
	//	      if (!Application.display.readAndDispatch())
	//	    	  Application.display.sleep();
	//	    }
	//	    Application.display.dispose();
	//	  }
}
