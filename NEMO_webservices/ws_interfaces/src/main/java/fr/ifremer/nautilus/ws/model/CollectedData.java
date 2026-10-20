package fr.ifremer.nautilus.ws.model;

import java.util.Objects;

public class CollectedData {

    private java.lang.String code;

    private int observationNumber;

    private boolean sensor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectedData that = (CollectedData) o;
        return observationNumber == that.observationNumber &&
                sensor == that.sensor &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, observationNumber, sensor);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getObservationNumber() {
        return observationNumber;
    }

    public void setObservationNumber(int observationNumber) {
        this.observationNumber = observationNumber;
    }

    public boolean isSensor() {
        return sensor;
    }

    public void setSensor(boolean sensor) {
        this.sensor = sensor;
    }
}
