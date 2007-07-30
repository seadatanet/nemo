package fr.ifremer.nemo.ui;


public class EditDescBack {
	
	public static void affichageDescBack(){ 
	
	int i;
	
	//affichage bouton expand all
	EditDesc.buttonExpandDesc.setParent(ViewEdit.formEdition.getBody());
	
	//affichage bouton valider
	EditDesc.buttonValidateDesc.setParent(ViewEdit.formEdition.getBody());
	
	// affichage des sections
	for (i=0;i<=EditDesc.listeSectionsDesc.size()-1;i++){

		EditDesc.listeSectionsDesc.get(i).setParent(ViewEdit.formEdition.getBody());
		
		}

	//remise du titre
	ViewEdit.formEdition.setText("Description de fichier");
	
//	affichage bouton expand all
	EditDesc.buttonExpandDesc2.setParent(ViewEdit.formEdition.getBody());
	
	//refresh global
	ViewEdit.formEdition.reflow(true);
}
	
	
	
	
}