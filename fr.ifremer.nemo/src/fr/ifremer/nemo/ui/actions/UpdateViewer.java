package fr.ifremer.nemo.ui.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		shell.setLayout(gridLayout);
		
		shell.setText("WebServices Info");
		
		Text message = new Text(shell,  SWT.V_SCROLL|SWT.H_SCROLL);
		message.setEditable(false);
		message.setText(updateMessage);
		
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.verticalSpan = 2;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		message.setLayoutData(gridData);

		Button ok = new Button(shell, SWT.NONE|SWT.RIGHT);
		ok.setText("OK");
		ok.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});


		shell.open();
	}
}
