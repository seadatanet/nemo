package fr.ifremer.nemo.ws.model;

import java.util.Objects;

public class Ship {

    private String code;

    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ship ship = (Ship) o;
        return Objects.equals(code, ship.code) &&
                Objects.equals(name, ship.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
