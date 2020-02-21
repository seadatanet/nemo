package fr.ifremer.nemo.ui.edit.file.listeners.variants;

import org.eclipse.swt.events.SelectionEvent;

import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.edit.file.listeners.VariantSelectionListener;
import fr.ifremer.sismer_tools.resources.ODVConstants.E_Variant;

public class VariantBiologicalListener extends VariantSelectionListener{

	public VariantBiologicalListener(EditFileForm editFileForm) {
		super(editFileForm);
		
	}
	  @Override
		public void widgetDefaultSelected( SelectionEvent g)
	    {
	        widgetSelected(g);
	    }
	    
	    @Override
		public void widgetSelected( SelectionEvent e)
	    {
	    	if (myEditFileForm.getRadBiological().getSelection()) {
	    		  RulesManager.getInstance().updateFromIHM( E_Variant.BIOLOGICAL);
	          // invalidation de l'etape quand modif
	          Utils.invalidEtapes();
			}
	    }
}
