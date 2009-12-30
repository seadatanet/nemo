package fr.ifremer.nemo.ui.edit.data;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.Etape;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditDataForm extends EditForm
{

    @Override
    public void createEditionForm(Composite parent)
    {
        super.createEditionForm(parent);
        
        formEdition.getBody().setLayout(new GridLayout());
        formEdition.setText(Messages.edit_data_title);

        // bouton validate
        createButtonValidate();
        buttonValidate
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    Etape.validationData();
                }
            });

        // reset 
        createButtonRaz();
    }
    
    @Override
    protected void initSections(){
        // pas de section dans cet onglet
    }

    @Override
    public void afficheEdition()
    {
        super.afficheEdition();

        ViewLog.aideData();
        
        // affichage du texte dans la zone de gauche
        ViewMain.textAreaData.setText(Utils.texteBrut);

        loaded = true;
        ViewMain.affiche = 3;

        // remplissage de l'IHM par le modele charge
        // nom du model
        String modelName = Nemo.getInstance().getNomModele();
        
        if (TableData.types != null && !TableData.types.isEmpty()){
            TableData.razTableData();
            TableData.creationTable();
        }
        else if (!Global.EMPTY_STRING.equals(modelName)){
            DataModel.fillData();
        }
        else{
            TableData.fillTypes();
        }
    }

    @Override
    public void drawingBack()
    {
        super.drawingBack();

        ViewLog.aideData();
        ViewMain.affiche = 3;
    }
    
    @Override
    public void razText(){
        TableData.fillTypes();
    }
    
    @Override
    public void actualiseEdition(){
        // pas d'actualisation nécessaire pour cet onglet
    }

    public void invalidEtape()
    {
        super.invalidEtape(3);
        ViewMain.tabData.setText(Messages.onglet_data);
    }
}
