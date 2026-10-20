/*
 * MappingDatabaseEditApp.java
 */

package fr.ifremer.nemo.mapping;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.View;

import fr.ifremer.nemo.util.Messages;

/**
 * The main class of the application.
 */
public class MappingDatabaseEditApp extends SingleFrameApplication {

    MappingDatabaseEditView vue=null;
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
    	
    	/**
    	 * 
    	 * 
    	 * BGT 15/07/2015:
    	 * always create new view because of a bug on linux:
    	 * [xcb] Unknown request in queue while dequeuing
		 * [xcb] Most likely this is a multi-threaded client and XInitThreads has not been called
		 * [xcb] Aborting, sorry about that.
		 * java: ../../src/xcb_io.c:179: dequeue_pending_request: Assertion `!xcb_xlib_unknown_req_in_deq' failed.

    	 */
//        if(this.vue==null)
//        {
            fr.ifremer.nemo.ui.Utils.messageOK(Messages.CouplingTable_launch);
            this.vue=new MappingDatabaseEditView(this);
            show(vue);
//        } else
//        {
//        show();
//        }

    }

    protected void show() {
        show(this.vue);
        this.vue.reload();


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
