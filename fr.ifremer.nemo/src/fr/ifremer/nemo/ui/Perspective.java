package fr.ifremer.nemo.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.ui.view.ViewMain;

public class Perspective implements IPerspectiveFactory
{

    public void createInitialLayout(IPageLayout layout)
    {
        String editorArea = layout.getEditorArea();  
        layout.setEditorAreaVisible(false);
        layout.setFixed(true);

        layout.addStandaloneView(ViewLog.ID, 
                                 false, 
                                 IPageLayout.BOTTOM, 
                                 0.80f,
                                 editorArea);
        
        layout.addStandaloneView(ViewMain.ID, 
                                 false, 
                                 IPageLayout.TOP, 
                                 0.20f,
                                 editorArea);
    }
}
