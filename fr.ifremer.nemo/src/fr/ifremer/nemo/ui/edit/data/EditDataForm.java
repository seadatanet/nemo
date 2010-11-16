package fr.ifremer.nemo.ui.edit.data;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.LineFragment;
import fr.ifremer.nemo.input.Parameter;
import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.Etape;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditDataForm extends EditForm
{
    
    
    @Override
    public void createEditionForm(Composite parent)
    {
        super.createEditionForm(parent);

        // init composants IHM
        initSections();

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

        // bouton collapse all / expand all
        createButtonExpand();

        drawSections();
    }

    @Override
    protected void initSections()
    {
        
    }

    @Override
    public void afficheEdition()
    {
        super.afficheEdition();

        ViewLog.aideData();

        // affichage du texte dans la zone de gauche
        Nemo.getInstance().getViewMain().textAreaData.setText(Utils.texteBrut);

        setLoaded(true);
        Nemo.getInstance().getViewMain().setAffiche(3);

        // remplissage de l'IHM par le modele charge
        // nom du model
        String modelName = Nemo.getInstance().getNomModele();
        
        if (TableData.types != null && !TableData.types.isEmpty()){
            TableData.razTableData();
            TableData.creationTable();
        }
        else if (!Global.EMPTY_STRING.equals(modelName)){
            fillFromModel();
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
        Nemo.getInstance().getViewMain().setAffiche(3);
    }

    @Override
    public void razText()
    {
        TableData.fillTypes();
    }

    @Override
    public void actualiseEdition()
    {
        // pas d'actualisation nécessaire pour cet onglet
    }

    public void invalidEtape()
    {
        super.invalidEtape(3);
        Nemo.getInstance().getViewMain().tabData.setText(Messages.onglet_data);
    }
    
    /**
     * fill with current model values 
     */
    @Override
    public void fillFromModel()
    {
        // /////////////////////////////////
        TableData.types = null;
        TableData.types = new ArrayList<TypeData>();

        ParameterModel[] parameterModels =
            Nemo.getInstance().getDomain().getCruise().getStation().getData().getModel().getParameterModels();

        // remplissage des parametres mesures
        for (ParameterModel paraCourant : parameterModels)
        {
            TypeData typeData = new TypeData();
            Parameter modeleCourant = paraCourant.getParameter();
            LineFragment frag = paraCourant.getFragment();
            LineFragment flag = paraCourant.getFlag();

            // code / fullName / sortie / format
            typeData.setCode(modeleCourant.getCode() + Global.SEP
                    + modeleCourant.getFullName());
            typeData.setNom(modeleCourant.getFullName());

            if (paraCourant.getConversionFormula().equals(Global.EMPTY_STRING)){
                typeData.setConversion(TypeData.DEFAULT_CONVERT);
            }
            else{
                typeData.setConversion(paraCourant.getConversionFormula());
            }
            typeData.setDebut(frag.getFirstCharOffset());
            typeData.setFin(frag.getLastCharOffset());
            if (flag != null)
            {
                typeData
                    .setDebutFlag(String.valueOf(flag.getFirstCharOffset()));
                typeData.setFinFlag(String.valueOf(flag.getLastCharOffset()));
            }

            String inputDefault = paraCourant.getInputDefault();
            if (inputDefault == null || inputDefault.equals("null"))inputDefault = Global.EMPTY_STRING; //$NON-NLS-1$
            typeData.setEntree(inputDefault);

            typeData.setUnite(modeleCourant.getUnit());
            typeData.setFormat(modeleCourant.getFormat());
            typeData.setSortie(modeleCourant.getDefault());
            typeData.setCodeP011(modeleCourant.getCodeP011());
            typeData.setNomP011(modeleCourant.getNameP011());
            typeData.setIsP011(modeleCourant.isP011());
            
            
            TableData.types.add(typeData);

        }

        // refresh de la table de l'onglet Data
        TableData.razTableData();

        // refresh de la table
        TableData.creationTable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initFromNewModel()
    {
        // recuperation du model correspondant
        fr.ifremer.nemo.input.DataModel dataModel = new fr.ifremer.nemo.input.DataModel();
        
        //int indexPRES = -1;

        // mise e jour des types avant validation
        TableData.majTypeData();

        // remplissage des parametres mesures
        //int nbreParam = TableData.types.size();

        // reset systematique des parametres stockes dans le dataModel
        Nemo.getInstance().getDomain().getCruise().getStation().getData().getModel().releaseParameterModels();

        for (TypeData typeCourant : TableData.types)
        {
            //TypeData typeCourant = TableData.types.get(i);

            // code / fullName / sortie / format
            //Parameter para = new Parameter();
            ParameterModel parameterModel = new ParameterModel();
            //parameterModel.setModel(para);

            // parameterModel.setModel(para);

            // split sur " - "
            Pattern p = Pattern.compile(Global.SEP);
            String mots[] = p.split(typeCourant.getCode());
            String code = mots[0];
            String nom = mots[1];

            Parameter para = new Parameter();
            // code, nom, unite
            para.setCode(code);
            para.setFullName(nom);
            para.setUnit(typeCourant.getUnite());

            // specific P011
            para.setCodeP011(typeCourant.getCodeP011());
            para.setNameP011(typeCourant.getNomP011());
            para.setP011(typeCourant.isP011());
            
            
            para.setDefault(typeCourant.getSortie());
            para.setFormat(typeCourant.getFormat());
            parameterModel.setParameter(para);

            // conversion / entree
            parameterModel.setConversionFormula(typeCourant.getConversion());
            parameterModel.setInputDefault(typeCourant.getEntree());

            // fragment du parametre
            LineFragment frag = new LineFragment();
            frag.setFirstCharOffset(typeCourant.getDebut());
            frag.setLastCharOffset(typeCourant.getFin());
            parameterModel.setFragment(frag);

            if (!typeCourant.getDebutFlag().equals(Global.EMPTY_STRING)
                    && !typeCourant.getFinFlag().equals(Global.EMPTY_STRING))
            {
                LineFragment flag = new LineFragment();
                flag.setFirstCharOffset(typeCourant.getDebutFlag());
                flag.setLastCharOffset(typeCourant.getFinFlag());
                parameterModel.setFlag(flag);
            }

            //if (code.trim().equals(Intern.paramPRES)){
            //    indexPRES = i;
            //}

            dataModel.addParameterModel(parameterModel);

        }
        // enregistrement de l'indexPRES
        //dModel.setPresIndex(indexPRES);
        Nemo.getInstance().getDomain().getCruise().getStation().getData().updateData(dataModel);
        

        Nemo.getInstance().getViewMain().getEditDataForm().validee = true;

    }
}
