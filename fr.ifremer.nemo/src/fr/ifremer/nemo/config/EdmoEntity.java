package fr.ifremer.nemo.config;

public class EdmoEntity {
	Integer code;
	String name;
	String country;

	public Integer getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getCountry() {
		return country;
	}

	public EdmoEntity(int code, String name, String country) {
		this.code = code;
		this.name = name;
		this.country = country;
	}
}
