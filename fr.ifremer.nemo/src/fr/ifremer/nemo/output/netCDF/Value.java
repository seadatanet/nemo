package fr.ifremer.nemo.output.netCDF;

public class Value {
	Object value;
	char qualityValue;
	
	
	
	public Value(){
		
	}
	
	/**
	 * @param value
	 * @param qualityValue
	 */
	public Value(Object value) {
		this.value = value;
	}
	/**
	 * @param value
	 * @param qualityValue
	 */
	public Value(Object value, char qualityValue) {
		this.value = value;
		this.qualityValue = qualityValue;
	}
	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	/**
	 * @return the qualityValue
	 */
	public char getQualityValue() {
		return qualityValue;
	}
	/**
	 * @param qualityValue the qualityValue to set
	 */
	public void setQualityValue(char qualityValue) {
		this.qualityValue = qualityValue;
	}
	
	
	public String toString()
	{
		return "Value= "+this.value + " QC= "+this.qualityValue;
	}
}
