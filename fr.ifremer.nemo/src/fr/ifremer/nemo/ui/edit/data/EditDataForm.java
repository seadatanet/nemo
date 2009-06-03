package fr.ifremer.nemo.ui.edit.data;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.edit.Etape;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditDataForm
{
    public ScrolledForm formEdition;
    public FormToolkit toolkitEdition;

    public Button buttonRAZ, buttonValidate;

    public boolean validee, loaded;

    public Boolean okApresReset;

    // //////////////////////////////////////////

    public void createEditionForm(Composite parent)
    {
        // initialisation zone edition pour etape description de fichier

        validee = false;
        loaded = false;

        toolkitEdition = new FormToolkit(parent.getDisplay());
        formEdition = toolkitEdition.createScrolledForm(parent);
        formEdition.getBody().setLayout(new GridLayout());
        formEdition.setText(Messages.edit_data_title);

        // bouton validate
        buttonValidate = new Button(formEdition.getBody(), SWT.NONE);
        buttonValidate.setBackground(ViewMain.backgroungEdit);
        buttonValidate.setText(Messages.button_validateStep);
        buttonValidate
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {

                    Etape.validationData();

                    formEdition.redraw();
                    formEdition.reflow(true);

                }
            });

        // reset
        buttonRAZ = new Button(formEdition.getBody(), SWT.NONE);
        buttonRAZ.setBackground(ViewMain.backgroungEdit);
        buttonRAZ.setText(Messages.button_reset);
        buttonRAZ
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    TableData.fillTypes();
                    Utils.invalidEtapes();
                }
            });

// // remplissage de l'IHM par le modele charge
// if (TableData.types != null && !TableData.types.isEmpty())
// {
// TableData.razTableData();
// TableData.creationTable();
// }
// else if (DataModel.ouverture)
// {
// DataModel.fillData();
// }
// else
// {
// TableData.fillTypes();
// }

        formEdition.setVisible(false);
    }

    public void afficheEdition()
    {

        okApresReset = true;
        loaded = true;
        ViewMain.affiche = 3;

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING);

        // affichage du texte dans la zone de gauche
        ViewMain.textAreaData.setText(Utils.texteBrut);

        // remplissage de l'IHM par le modele charge
        if (TableData.types != null && !TableData.types.isEmpty())
        {
            TableData.razTableData();
            TableData.creationTable();
        }
        else if (DataModel.ouverture)
        {
            DataModel.fillData();
        }
        else
        {
            TableData.fillTypes();
        }

        formEdition.setVisible(true);
    }

    public void razEdition()
    {
        // reset de toute la zone d'edition apres etape descriptioon de fichier
        // on retire toutes les sections et les boutons expand et validation

        // effacement des boutons valider/expand/init serveur
        // buttonValidate.setParent(shell);
        // buttonRAZ.setParent(shell);

        // effacement titre formulaire
        // ViewEdit.formEdition.setText(Global.EMPTY_STRING);

        // refresh global
        // ViewEdit.formEdition.reflow(true);
        formEdition.setVisible(false);
    }

    public void ajoutModifyListenerText(ArrayList<Text> liste)
    {
        int taille = liste.size();
        for (int i = 0; i < taille; i++)
        {
            liste.get(i).addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event)
                {
                    // on modifie la valeur du champ texte

                    // des qu'on modifie une valeur, on invalide l'etape
                    Utils.invalidEtapes();

                }
            });

        }
    }

    public void DrawingBack()
    {
        // reset de toute la zone d'edition apres etape descriptioon de fichier
        // on retire toutes les sections et les boutons expand et validation

        ViewLog.aideData();

        // ScrolledForm s;
        // s = ViewEdit.formEdition;

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING);

        // buttonValidate.setParent(s.getBody());
        // buttonRAZ.setParent(s.getBody());

        // ViewEdit.formEdition.setText(Messages.edit_data_title);

        // refresh global
        // ViewEdit.formEdition.reflow(true);

        // affichage courant
        ViewMain.affiche = 3;

        formEdition.setVisible(true);

    }

    private void disposeElements()
    {
        if (buttonRAZ != null)
            buttonRAZ.dispose();
        if (buttonValidate != null)
            buttonValidate.dispose();
    }

    public void invalidEtape()
    {
        okApresReset = false;
        validee = false;
        if (ViewMain.affiche != 3 && loaded)
        {
            loaded = false;
            disposeElements();
        }
        ViewMain.tabData.setText(Messages.onglet_data);
    }
}
