/*
 * MappingDatabaseEditApp.java
 */

package fr.ifremer.nemo.mapping;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.View;

/**
 * The main class of the application.
 */
public class MappingDatabaseEditApp extends SingleFrameApplication {

    View vue=null;
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        if(this.vue==null)
        {
            this.vue=new MappingDatabaseEditView(this);
        }
        show(vue);

    }

    protected void show() {
        show(this.vue);
    }

    protected void hide() {
        hide(this.vue);
        //this.vue.getApplication().exit();
        
    }

    @Override protected void shutdown() {
        hide(this.getMainView());
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of MappingDatabaseEditApp
     */
    public static MappingDatabaseEditApp getApplication() {
        return Application.getInstance(MappingDatabaseEditApp.class);
    }

    public View getView() {
        return this.vue;
    }
    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(MappingDatabaseEditApp.class, args);
    }
}
