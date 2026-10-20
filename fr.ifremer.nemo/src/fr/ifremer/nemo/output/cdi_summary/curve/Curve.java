package fr.ifremer.nemo.output.cdi_summary.curve;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import fr.ifremer.nemo.output.cdi_summary.CDIResumeWriter;
import fr.ifremer.nemo.util.Global;

/*
 * Curve object for trajectories
 */
public class Curve {
	

	List<LatLon> latLonList;

	
	
    public Curve(List<LatLon> latLonList) {
        this.latLonList = latLonList;
    }
    
    public Curve() {
        this.latLonList = new ArrayList<LatLon>();
    }

    
    public List<LatLon> getLatLonList() {
    	return latLonList;
    }
    
    public void setLatLonList(List<LatLon> latLonList) {
    	this.latLonList = latLonList;
    }
    
    /**
     * Return Lon1 Lat1 Lon2 Lat2 …. Lonn Latn (Mikado format)
     * @return
     */
    public String getLatLonString() {
    	String latLonString = new String();
    	DecimalFormat df = CDIResumeWriter.getInstance().latLonDecimalFormat;
    	for (int i = 0; i<latLonList.size(); i++) {
    		LatLon latLon = latLonList.get(i);
    		if (i==0) {
    			latLonString =  df.format(latLon.getLon()) + Global.SPACE + df.format(latLon.getLat());
    		} else {
    			latLonString = latLonString + Global.SPACE + df.format(latLon.getLon()) + Global.SPACE + df.format(latLon.getLat());   
    		}
    	}
	   return latLonString; 
   }
   
	String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	String description;
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
