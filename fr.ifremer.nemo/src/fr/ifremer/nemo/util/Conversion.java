package fr.ifremer.nemo.util;

/**
 * Description: Types de conversion
 * @author dboulang
 *
 */
public enum Conversion {
	MEDATLAS, ODV, NETCDF;

	public String getName() {
		String name = Global.EMPTY_STRING;
		switch (this) {
		case MEDATLAS:
			name = "Medatlas";//$NON-NLS-1$
			break;
		case NETCDF:
			name = "NetCDF";//$NON-NLS-1$
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
			name = "SDN NetCDF";//$NON-NLS-1$
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
			debut = "//"; //$NON-NLS-1$
			break;
		}
		return debut;
	}

	public String getExt() {
		String ext = Global.EMPTY_STRING;
		switch (this) {
		case ODV:
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
}
