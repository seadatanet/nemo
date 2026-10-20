package fr.ifremer.nemo.util.format;

public enum ParseElementType {
	HEMISPHERE(null), 
    SIGN(null), 
    DEGREES(null), 
    DEGREES_DECIMAL(null), 
    DEGREES_DECIMAL_SANS_POINT(null), 
    MINUTES(5), 
    MINUTES_DECIMAL(null), 
    MINUTES_DECIMAL_SANS_POINT(null), 
    SECONDS(6), SECONDSD(null), 
    MILLISECONDS(7), 
    CHARS(null), 
    YEAR(1), 
    YEAR19(1), 
    MONTH(2), 
    DAY(3), 
    HOURS12(4), 
    HOURS24(4), 
    HOURSDEC(7), 
    DAY_DECIMAL(null), 
    AMPM(null);
	
	public final Integer precision;
	
	private ParseElementType(Integer precision) {
		this.precision = precision;
	}
}
