package org.manwhore.displayer.filter;

/**
 * Helper class for the RelativeDate class.
 * 
 * @author siglerv
 *
 */
class Token {

	private String value;
	private Type type;
	
	public Token(Type type, String value)
	{
		this.type = type;
		this.value = value;		
	}	
		
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public static enum Type
	{
		MODIFIER,
		PERIOD,
		UNIT,
		NUMBER
	}
}
