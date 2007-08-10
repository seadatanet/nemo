package fr.ifremer.nemo.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class Progress implements Runnable {

	public static Shell shell;

	/**
	 * @param args
	 */

	public void run() {

//		System.out.println("passage dans run");
		// ouverture de la fenetre de progression
		shell = new Shell(Application.display, SWT.BORDER);
//		System.out.println("passage dans run2");
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				ProgressBar progressbar = new ProgressBar(shell, SWT.HORIZONTAL
						| SWT.SMOOTH | SWT.INDETERMINATE);
				progressbar.setSize(200, 20);
				shell.pack();
			}
		});

	}
}