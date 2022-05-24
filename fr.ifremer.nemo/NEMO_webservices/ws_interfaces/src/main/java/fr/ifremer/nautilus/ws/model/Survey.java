package fr.ifremer.nautilus.ws.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Survey {

    private String chiefScientist;

    private String comment;

    private String countryCode;

    private String dataCentre;

    private List<CollectedData> dataTypes = new ArrayList<>();

    private long endDate;

    private String laboratories;

    private String name;

    private String projectName;

    private String reference;

    private String crno;
    
    private String csr;

    private String regionName;

    private String shipCode;

    private String shipName;

    private long startDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Survey survey = (Survey) o;
        return endDate == survey.endDate &&
                startDate == survey.startDate &&
                Objects.equals(chiefScientist, survey.chiefScientist) &&
                Objects.equals(comment, survey.comment) &&
                Objects.equals(countryCode, survey.countryCode) &&
                Objects.equals(dataCentre, survey.dataCentre) &&
                Objects.equals(dataTypes, survey.dataTypes) &&
                Objects.equals(laboratories, survey.laboratories) &&
                Objects.equals(name, survey.name) &&
                Objects.equals(projectName, survey.projectName) &&
                Objects.equals(reference, survey.reference) &&
                Objects.equals(crno, survey.crno) &&
                Objects.equals(regionName, survey.regionName) &&
                Objects.equals(shipCode, survey.shipCode) &&
                Objects.equals(shipName, survey.shipName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chiefScientist, comment, countryCode, dataCentre, dataTypes, endDate, laboratories, name, projectName, reference, crno, regionName, shipCode, shipName, startDate);
    }

    public String getChiefScientist() {
        return chiefScientist;
    }

    public void setChiefScientist(String chiefScientist) {
        this.chiefScientist = chiefScientist;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDataCentre() {
        return dataCentre;
    }

    public void setDataCentre(String dataCentre) {
        this.dataCentre = dataCentre;
    }

    public List<CollectedData> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(List<CollectedData> dataTypes) {
        this.dataTypes = dataTypes;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getLaboratories() {
        return laboratories;
    }

    public void setLaboratories(String laboratories) {
        this.laboratories = laboratories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCrno() {
        return crno;
    }

    public void setCrno(String crno) {
        this.crno = crno;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getShipCode() {
        return shipCode;
    }

    public void setShipCode(String shipCode) {
        this.shipCode = shipCode;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

	public String getCsr() {
		return csr;
	}

	public void setCsr(String csr) {
		this.csr = csr;
	}
}
