package fr.ifremer.nemo.config;

import fr.ifremer.nemo.util.Global;

public class EdmerpEntity {
	Integer code;
	String name;
	
	public EdmerpEntity(int code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public EdmerpEntity() {
		this.code = 0;
		this.name = Global.EMPTY_STRING;
	}

	public Integer getCode() {
		return code;
	}
	
	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}


	

}
