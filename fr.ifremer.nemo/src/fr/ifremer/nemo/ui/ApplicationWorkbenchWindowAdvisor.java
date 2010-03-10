package fr.ifremer.nemo.ui;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import fr.ifremer.nemo.util.Intern;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public static IWorkbenchWindowConfigurer configurer_titre;

	public static IWorkbenchWindowConfigurer configurer;

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
    public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer config) {
		return new ApplicationActionBarAdvisor(config);
	}

	@Override
    public void preWindowOpen() {
		configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1024, 768));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.setShowProgressIndicator(false);

		configurer_titre = configurer;

        configurer.setTitle(Intern.application_title);

	}
    
 
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void postWindowCreate()
    {
        super.postWindowCreate();
        configurer.getWindow().getShell().setMinimized(false);
    }
}
