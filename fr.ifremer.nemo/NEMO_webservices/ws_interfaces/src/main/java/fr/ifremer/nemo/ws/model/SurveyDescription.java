package fr.ifremer.nemo.ws.model;

import java.util.Objects;

public class SurveyDescription {

    private String name;

    private String reference;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SurveyDescription that = (SurveyDescription) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(reference, that.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, reference);
    }
}
