package fr.ifremer.nemo.ui;


import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import fr.ifremer.nemo.mapping.MappingDatabaseEditView;
import fr.ifremer.nemo.util.Messages;

import fr.ifremer.nemo.mapping.*;


/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	public static IWorkbenchAction newAction, updateAction, exitAction,
			exitActionQuit, aboutAction, openAction, openXMLAction,
			saveXMLAction, optionsAction, helpAction, saveAction, saveAsAction, mappingDatabaseImportAction, mappingDatabaseExportAction, mappingDatabaseEditAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.

		exitActionQuit = ActionFactory.QUIT.create(window);

		exitAction = new ExitAction(window);
		// newAction = new SaveAction(window);
		openAction = new OpenAction(window);
		aboutAction = ActionFactory.ABOUT.create(window);

		// openXMLAction = new OpenXMLAction(window);
		// saveXMLAction = new SaveXMLAction(window);

		helpAction = ActionFactory.HELP_CONTENTS.create(window);

		optionsAction = new OptionsAction(window);
		// register(optionsAction);
	
		updateAction = new UpdateAction(window);
		saveAction = new SaveAction(window);

		// saveAsAction = ActionFactory.SAVE_AS.create(window);

        mappingDatabaseImportAction = new OpenMappingAction(window);
        mappingDatabaseExportAction = new ExportMappingAction(window);
        mappingDatabaseEditAction = new EditMappingAction(window);
        

		register(exitAction);
		// register(openXMLAction);
		register(aboutAction);
		register(saveAction);
		register(exitActionQuit);
        
        register(mappingDatabaseImportAction);
        register(mappingDatabaseExportAction);
        register(mappingDatabaseEditAction);
		// register(helpAction);

	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager(Messages
				.getString("ApplicationActionBarAdvisor.0"), Messages
				.getString("ApplicationActionBarAdvisor.1"));
		// fileMenu.add(newAction);
		fileMenu.add(openAction);
		fileMenu.add(saveAction);

		// fileMenu.add(saveAsAction);
		// fileMenu.add(new Separator());

		// fileMenu.add(openXMLAction);
		// fileMenu.add(saveXMLAction);

		fileMenu.add(new Separator());
		fileMenu.add(exitAction);

		MenuManager optionsMenu = new MenuManager(
				Messages.getString("ApplicationActionBarAdvisor.2"), Messages.getString("ApplicationActionBarAdvisor.3")); //$NON-NLS-1$ //$NON-NLS-2$
		optionsMenu.add(optionsAction);
		optionsMenu.add(updateAction);

        MenuManager mappingMenu = new MenuManager(
            Messages.getString("ApplicationActionBarAdvisor.4"), Messages.getString("ApplicationActionBarAdvisor.4")); //$NON-NLS-1$ //$NON-NLS-2$
        mappingMenu.add(mappingDatabaseImportAction);
        mappingMenu.add(mappingDatabaseExportAction);  
        fileMenu.add(new Separator());      
        mappingMenu.add(mappingDatabaseEditAction);          
        
        
		MenuManager helpMenu = new MenuManager("&?", "?"); //$NON-NLS-1$ //$NON-NLS-2$
		// helpMenu.add(helpAction);
		helpMenu.add(aboutAction);

		// MenuManager aboutMenu = new MenuManager("?", "?");
		// aboutMenu.add(aboutAction);

		menuBar.add(fileMenu);
		menuBar.add(optionsMenu);
        menuBar.add(mappingMenu);        
		menuBar.add(helpMenu);
		// menuBar.add(aboutMenu);

		saveAction.setEnabled(true);

	}

}
