package fr.ifremer.nemo.output.cdi_summary.curve;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CurveCreator {


	private static CurveCreator instance;

	public static CurveCreator getInstance() {
		if (null == instance) {
			instance = new CurveCreator();
		}
		return instance;
	}

	public static List<Curve> compuiteCurvesFromLatLonList(List<LatLon> latLonList) {

		List<Curve> curveList = computeCurveList(latLonList);
		curveList = splitAntimerdianCurveList(curveList);
		curveList = cleanCurveListFromDuplicate(curveList);
		giveNameToCurveList(curveList);

		return curveList;
	}

	public static List<Curve> computeCurveList(List<LatLon> latLonList) {
		//		-- comme on ne peut pas mettre tous les points, on doit optimiser la trajectoire :
		//		--  - reperer les changements de directions (en comparant 3 points) : enregistrements de points
		//		--  - reperer les grands ecarts entre 2 points : enregistrement de segments (routes), a  condition qu'il y ai plus de 2 points

		int nb_segment = 0; // nombre de segments crees
		int nb_point =0 ; // nombre de points totals dans la trajectoire du navire
		int nb_point_final = 0; // nombre de points totals enregistres dans les curves
		int nb_point_segment = 0; // nombre de points dans le segment courant
		int interval_point = 0;  // interval maximum entre deux points enregistres

		LatLon lonlat_0 = null;  // lonlat du point de depart en objet Lonlat
		double lat_0 = 0; // lat point de depart (enregistre dans la curve) pour la comparaison des changements de directions
		double lon_0 = 0; // lon du point de depart

		LatLon lonlat_m = null; // lonlat du second point en objet Lonlat
		double lat_m = 0; // lat du second point (sera enregistre dans la curve s'il y a changement de direction)
		double lon_m = 9999; // lon du second point

		double ecart_lat_0=0; // lat : calcul de l'ecart absolu entre le point initial et le point courant
		double ecart_lon_0=0; // lon : calcul de l'ecart absolu entre le point initial et le point courant
		double ecart_lat_m=0; // lat : calcul de l'ecart absolu entre le second point initial et le point courant
		double ecart_lon_m=0; // lon : calcul de l'ecart absolu entre le second point initial et le point courant

		double distance_threshold_07 = 0.7;
		double distance_threshold_05 = 0.5;
		double test_ecart_m =0.025; // valeur pour le test (ecart_lat_m + ecart_lon_m > test_ecart_m)
		double test_ecart_0 =0.1; // valeur pous le test ecart_lat_0 + ecart_lon_0 > test_ecart_0

		List<LatLon> modifiedLatonList = new ArrayList<LatLon>();
		List<Curve> computedCurveList = new ArrayList<Curve>();
		
		// init variables
	    lon_0=9999;
        lon_m=9999;
        interval_point = latLonList.size()/300;

		// On parcourt la liste de couples latitude-longitude.
		for (int i = 0; i < latLonList.size(); i++) {
			nb_point = nb_point+1;

			//	LatLon currentLatLon = latLonList.get(i);
			// round 4 digits	
			double roundedLat = Math.round(latLonList.get(i).getLat() * 10000.0) /10000.0;
			double roundedLon = Math.round(latLonList.get(i).getLon() * 10000.0) /10000.0;
			LatLon currentLatLon = new LatLon(roundedLat, roundedLon);

			if (lon_0 == 9999) {  // test 1er point
				// 1er point initialise avec le point courant
				lat_0 = currentLatLon.getLat();
				lon_0 = currentLatLon.getLon();
				lonlat_0 = currentLatLon;
				// segment initialise avec le point courant
				modifiedLatonList = new ArrayList<LatLon>();
				modifiedLatonList.add(currentLatLon);
				// initialisation des compteurs
				nb_segment=1;
				nb_point_segment=1;
			} else {
				// calcul de l'ecart entre le point initial et le point courant
				ecart_lat_0 = Math.abs(currentLatLon.getLat() - lat_0);
				ecart_lon_0 = Math.abs(currentLatLon.getLon() - lon_0);
				if (ecart_lon_0 > 180) { // antimerdian
					ecart_lon_0 = 360 - ecart_lon_0;
				}
				if (lon_m != 9999) { // test existence second point : il faut 3 points pour commencer le test
					ecart_lat_m = Math.abs(currentLatLon.getLat()-lat_m);  // calcul de l'ecart entre le 2d point et le point courant
					ecart_lon_m = Math.abs(currentLatLon.getLon()-lon_m);
					if (ecart_lon_m > 180) { // antimerdian
						ecart_lon_m = 360 - ecart_lon_m;
					}
					if ((ecart_lat_m + ecart_lon_m) > distance_threshold_07) { //test nouveau segment (ecart trop important)
						// insertion d'un segment uniquement s'il a plus de 2 points
						if (nb_point_segment >= 2) {
							modifiedLatonList.add(lonlat_m);
							Curve addedCurve = new Curve(modifiedLatonList);
							computedCurveList.add(addedCurve);
							// reinitialisation du segment suivant
							nb_segment++;
						}
						// on reinitilise meme si on a pas créé de segments
						modifiedLatonList = new ArrayList<LatLon>();
						modifiedLatonList.add(currentLatLon);
						lat_0 = currentLatLon.getLat();
						lon_0 = currentLatLon.getLon();
						lonlat_0 = currentLatLon;
						lon_m = 9999;
						nb_point_segment=1;
					} else {
						if (lonlat_m != null && !lonlat_m.equals(lonlat_0) && ecart_lon_0 != 0 &&  ecart_lon_m != 0) { // test pour controler que les longitudes sont diff et eviter la divisions par 0
							if (( Math.abs((ecart_lat_0/ecart_lon_0)-(ecart_lat_m/ecart_lon_m))>0.5 ) || (nb_point > interval_point) || (ecart_lat_0 + ecart_lon_0 > distance_threshold_07)) { // test des changements de directions + controle du nombre de points enregistrés
								if ((ecart_lat_m + ecart_lon_m > test_ecart_m) || (ecart_lat_0 + ecart_lon_0 > test_ecart_0) || (nb_point > interval_point)) {  // test des distances entre les points + controle du nombre de points enregistrés
									modifiedLatonList.add(lonlat_m);
									nb_point_segment++;

									lat_0 = lat_m;
									lon_0 = lon_m;
									lonlat_0 = lonlat_m;
									nb_point = 0;
								}
							}
						}

						// reinitialisation du second point au point courant
						lonlat_m = currentLatLon;
						lat_m = currentLatLon.getLat();
						lon_m = currentLatLon.getLon();

					}

				} else { // existence second point : il faut 3 points pour commencer le test
					// test si enregistrement du second point
					if (!currentLatLon.equals(lonlat_0)) {  // test pour a eviter que le second point ne soit pas identique au premier
						if ((ecart_lat_0 + ecart_lon_0) > distance_threshold_07) { // test si le second point est trop distant du premier , on le prend comme premier point
							// on reinitialise le premier point avec le point courant (sans creer de segment)

							modifiedLatonList = new ArrayList<LatLon>();
							modifiedLatonList.add(currentLatLon);
							lat_0 = currentLatLon.getLat();
							lon_0 = currentLatLon.getLon();
							lonlat_0 = currentLatLon;
							lon_m = 9999;
							nb_point_segment = 1;
						} else if ((ecart_lat_0 + ecart_lon_0) > distance_threshold_05) { // test si le  point teste est trop distant du premier , on l'ajoute
							// on l'ajoute au  premier point avec le point courant (sans creer de segment)
							modifiedLatonList.add(currentLatLon);
							nb_point_segment++;
							lat_0 = lat_m;
							lon_0 = lon_m;
							lonlat_0 = lonlat_m;
							nb_point=0;

							lat_m = currentLatLon.getLat();
							lon_m = currentLatLon.getLon();
							lonlat_m = currentLatLon;
						} else {
							// enregistrement du second point
							lonlat_m = currentLatLon;
							lat_m = currentLatLon.getLat();
							lon_m = currentLatLon.getLon();
						}
					}
				}
			}
		}

		// insertion du dernier segment
		if (lonlat_m != null && !lonlat_m.equals(lonlat_0)) {  // test pour eviter que le dernier point ne soit pas identique au preceden
			if ((ecart_lat_0 + ecart_lon_0) < 0.7) { // test si le dernier point est trop distant du precedent on ne le prend pas
				modifiedLatonList.add(lonlat_m);
				nb_point_segment++;
			}
		}
		// insertion d'un segment uniquement s'il a plus de 2 points
		if (nb_point_segment>1) {  // test segment plus de 2 points	
			Curve addedCurve = new Curve(modifiedLatonList);
			computedCurveList.add(addedCurve);

		}  // fin test pour eviter que le dernier point ne soit pas identique au precedent

		return computedCurveList;
	}

	public static List<Curve> splitAntimerdianCurveList(List<Curve> curveList) {
		/* procedure de gestion de l'antimeridien : il faut decouper les curves si c'est le cas et ajouter des points a  180 ou -180 */
		List<Curve> computedCurveList = new ArrayList<Curve>();

		for (Curve curve : curveList) { 
			List<LatLon> latLonList = curve.getLatLonList();
			List<LatLon> modifiedLatLonList = new ArrayList<LatLon>(latLonList);
			//			List<Integer> crossingIndices = new ArrayList<>();
			List<Integer> modifiedCrossingIndices = new ArrayList<>();
			int offset = 0;

			// On parcourt la liste de couples latitude-longitude.
			for (int i = 0; i < latLonList.size()-1; i++) {
				LatLon currentLatLon = latLonList.get(i);
				LatLon nextLatLon = latLonList.get(i + 1);

				// ajoute le point courrant
				modifiedLatLonList.add(currentLatLon);

				// Si le passage de l'antim�ridien a lieu entre les deux couples de coordonn�es consid�r�s,
				if (isAntemeridianBetween(currentLatLon, nextLatLon)) {

					// ajoute un point � chaque au debut et a la fin des futures curves
					double beginCruve = 0;
					double endCruve = 0;
					if (currentLatLon.getLat() < nextLatLon.getLat()) {
						beginCruve = -180;
						endCruve = 180;
					} else {
						beginCruve = 180;
						endCruve = -180;
					}
					modifiedLatLonList.add(new LatLon(currentLatLon.getLat(), beginCruve));
					modifiedLatLonList.add(new LatLon(nextLatLon.getLat(), endCruve));
					offset = offset+2;

					// on ajoute l'indice du couple courant � la liste des indices de passage.
					//		        	crossingIndices.add(i);
					modifiedCrossingIndices.add(i+1+offset); 

				}
			}

			// ajoute le dernier point
			modifiedLatLonList.add(latLonList.get(latLonList.size()-1));



			if (!modifiedCrossingIndices.isEmpty()) {
				List<Curve> spilttedCurveList = splitCurve(modifiedLatLonList, modifiedCrossingIndices);
				computedCurveList.addAll(spilttedCurveList);
			} else {
				computedCurveList.add(curve);
			}

		}

		return computedCurveList;
	}

	/*
	 * Assuming a minimum travel distance between two provided longitude coordinates,
	 * hecks if the 180th meridian (antimeridian) is crossed.
	 */
	public static boolean isAntemeridianBetween(LatLon latLon1, LatLon latLon2) {
		double antimerdianLon = 180;
		return Math.abs(latLon2.getLon() - latLon1.getLon()) > antimerdianLon;   
	}    		

	public static List<Curve> splitCurve(List<LatLon> latLonList, List<Integer> crossingIndices) {
		List<Curve> spilttedCurveList = new ArrayList<Curve>();

		List<LatLon> currentList = new ArrayList<>();
		for (int i = 0; i < latLonList.size(); i++) {

			currentList.add(latLonList.get(i));
			if (crossingIndices.contains(i)) {
				Curve splittedCurve = new Curve(currentList);
				spilttedCurveList.add(splittedCurve);
				currentList = new ArrayList<>();
			}
		}

		return spilttedCurveList;

	}


	public static List<Curve> cleanCurveList_bis(List<Curve> curveList) {
		List<Curve> curveToRemoveList = new ArrayList<Curve>();
		for (Curve curve : curveList) {
			/* procedure qui supprime les doublons de points dans une curve */
			List<LatLon> latLonList = curve.getLatLonList();

			// Create a new LinkedHashSet (no duplicated object)
			Set<LatLon> latLonSet = new LinkedHashSet<>();
			// Add the elements to set
			latLonSet.addAll(latLonList);
			// Clear the list
			latLonList.clear();
			// add the elements of set
			// with no duplicates to the list
			latLonList.addAll(latLonSet);

			//        --  fin du segment : si la curve finale moins de 2 points il faut la supprimer,
			// If modified curve size is < 2, add to remove list
			if (latLonList.size() < 2) {
				curveToRemoveList.add(curve);
			}

		}
		// Remove the curves from the initial list
		for (Curve curveToRemove : curveToRemoveList) {
			curveList.remove(curveToRemove);
		}
		return curveList;
	}

	public static List<Curve> cleanCurveListFromDuplicate(List<Curve> curveList) {
		List<Curve> curveToRemoveList = new ArrayList<Curve>();
		for (Curve curve : curveList) {
			/* procedure qui supprime les doublons de points dans une curve */
			List<LatLon> latLonList = curve.getLatLonList();
			List<LatLon> filteredLatLonList = new ArrayList<LatLon>();

			// On parcourt la liste de couples latitude-longitude.
			for (LatLon latLon : latLonList) {
				// Si les coordonn�es courantes ne sont pas d�j� pr�sentes dans la liste r�sultat, on les
				// ajoute � cette liste.
				if (!filteredLatLonList.contains(latLon)) {
					filteredLatLonList.add(latLon);
				}
			}

			//        --  fin du segment : si la curve finale moins de 2 points il faut la supprimer,
			// If modified curve size is < 2, add to remove list
			if (filteredLatLonList.size() < 2) {
				curveToRemoveList.add(curve);
			}

		}
		// Remove the curves from the initial list
		for (Curve curveToRemove : curveToRemoveList) {
			curveList.remove(curveToRemove);
		}
		return curveList;
	}

	
	public static void giveNameToCurveList (List<Curve> curveList) {
		for (int i = 0; i< curveList.size(); i++) {
			Curve curve=  curveList.get(i);
			int curveNameName = i+1;
			curve.setName("CURVE " + curveNameName);
			curve.setDescription("CURVE " + curveNameName);
		}
	}

}



