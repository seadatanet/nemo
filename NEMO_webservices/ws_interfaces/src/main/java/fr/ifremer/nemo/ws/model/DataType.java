package fr.ifremer.nemo.ws.model;

import java.util.Objects;

public class DataType {

    private String code;

    private String libelle;

    private boolean sensor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataType dataType = (DataType) o;
        return sensor == dataType.sensor &&
                Objects.equals(code, dataType.code) &&
                Objects.equals(libelle, dataType.libelle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, libelle, sensor);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public boolean isSensor() {
        return sensor;
    }

    public void setSensor(boolean sensor) {
        this.sensor = sensor;
    }
}
