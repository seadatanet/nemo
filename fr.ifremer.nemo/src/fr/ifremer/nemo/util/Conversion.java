package fr.ifremer.nemo.util;

import fr.ifremer.sismer_tools.resources.ODVConstants.E_Variant;

/**
 * Description: Types de conversion
 * @author dboulang
 *
 */
public enum Conversion {
	MEDATLAS, ODV, BODV, MODV, NETCDF;

	public String getName() {
		String name = Global.EMPTY_STRING;
		switch (this) {
		case MEDATLAS:
			name = "Medatlas";//$NON-NLS-1$
			break;
		case NETCDF:
			name = "NetCDF";//$NON-NLS-1$
			break;
		case BODV:
			name = "ODV Biological";//$NON-NLS-1$
			break;
		case MODV:
			name = "ODV microlitter";//$NON-NLS-1$
			break;
		default:
			name = this.toString();
			break;
		}
		return name;
	}

	public String getFormatName() {
		String name = Global.EMPTY_STRING;
		switch (this) {
		case NETCDF:
			name = "CFPOINT";//$NON-NLS-1$
			break;
		default:
			name = this.toString();
			break;
		}
		return name;
	}

	public String getDebut() {
		String debut = Global.EMPTY_STRING;
		switch (this) {
		case MEDATLAS:
			debut = "*"; //$NON-NLS-1$
			break;
		case ODV:
		case BODV:
		case MODV:
			debut = "//"; //$NON-NLS-1$
			break;
		}
		return debut;
	}

	public String getExt() {
		String ext = Global.EMPTY_STRING;
		switch (this) {
		case ODV:
		case BODV:
		case MODV:
			ext = Global.EXT_TXT;
			break;
		case NETCDF:
			ext = Global.EXT_NC;
			break;
		default:
			ext = ".*";//$NON-NLS-1$
			break;
		}
		return ext;
	}

	/*   
	   public String getTemplateName(){
	       String name = Global.EMPTY_STRING;
	       switch (this){
	           case MEDATLAS :
	               name = "medatlas.vtl";//$NON-NLS-1$
	               break;
	           default :
	               name = "odv.vtl";//$NON-NLS-1$
	               break;
	       }
	       return name;
	   }
	   */
	
	/**
	 * Get the format name from the ODV variant.
	 * @param variant
	 * @return
	 */
	public static String getFormatFromVariant(Conversion conv, E_Variant variant) {
		
		return getConversionFromVariant(conv, variant).getFormatName();
	}
	
	/**
	 * Get the new Conversion value from the ODV variant.
	 * @param variant
	 * @return
	 */
	public static Conversion getConversionFromVariant(Conversion conv, E_Variant variant) {
		
		Conversion returnedConv = conv;
		if (returnedConv == Conversion.ODV && variant != null) {
			switch (variant) {
			case BIOLOGICAL:
				returnedConv = Conversion.BODV;
				break;

			case MICROLITTER:
				returnedConv = Conversion.MODV;
				break;

			default:
				break;
			}
		}
		return returnedConv;

	}

}
