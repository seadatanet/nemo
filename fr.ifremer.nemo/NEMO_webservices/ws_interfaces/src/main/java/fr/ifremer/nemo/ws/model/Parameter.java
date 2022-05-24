package fr.ifremer.nemo.ws.model;

import java.util.Objects;

public class Parameter {

    private String code;

    private String defaultValue;

    private String format;

    private String name;

    private String unit;

    private String unitP06;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter parameter = (Parameter) o;
        return Objects.equals(code, parameter.code) &&
                Objects.equals(defaultValue, parameter.defaultValue) &&
                Objects.equals(format, parameter.format) &&
                Objects.equals(name, parameter.name) &&
                Objects.equals(unit, parameter.unit) &&
                Objects.equals(unitP06, parameter.unitP06);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, defaultValue, format, name, unit, unitP06);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnitP06() {
        return unitP06;
    }

    public void setUnitP06(String unitP06) {
        this.unitP06 = unitP06;
    }
}
