package fr.ifremer.nemo.output;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.domain.InputDomain;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.output.medatlas.MedatlasWriter;
import fr.ifremer.nemo.output.odv.OdvWriter;
import fr.ifremer.nemo.util.Conversion;

/**
 * Factory of NemoWriter
 * @author IFREMER
 */
public final class NemoWriterFactory {


	/**
	 * select NemoWriter according to conversion type
	 * @param Conversion
	 * @return NemoWriter selected
	 */
	public static NemoWriter createWriter(final Conversion conversion){
	// FAE 6143
        InputDomain id = Nemo.getInstance().getDomain();
        if (id != null)
        {
            CruiseDomain cd = id.getCruise();
            if (cd != null)
            {
                StationDomain sd = id.getStation();
                if (sd != null)
                {
                    sd.resetlocalCDIList();
                }
            }
        }
        
        switch (conversion){
            case MEDATLAS : 
                return new MedatlasWriter(); 
            case ODV : 
                return new OdvWriter(); 
        }
        return null;
	}
}
