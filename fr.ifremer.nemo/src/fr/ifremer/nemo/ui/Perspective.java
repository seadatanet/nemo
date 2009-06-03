package fr.ifremer.nemo.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.ui.view.ViewMain;

public class Perspective implements IPerspectiveFactory
{

    public void createInitialLayout(IPageLayout layout)
    {

        layout.setEditorAreaVisible(false);

        layout.addStandaloneView(ViewLog.ID, false, IPageLayout.BOTTOM, 0.80f,
            layout.getEditorArea());
        layout.addStandaloneView(ViewMain.ID, false, IPageLayout.TOP, 0.20f,
            layout.getEditorArea());
        // layout.addStandaloneView(ViewMain.ID, false, IPageLayout.LEFT, 0.50f,
        // layout.getEditorArea());
        // layout.addStandaloneView(ViewEdit.ID, false, IPageLayout.RIGHT,
        // 0.50f,
        // layout.getEditorArea());
        layout.setFixed(true);

    }
}
