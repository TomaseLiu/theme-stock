package com.datayes.bdb.theme.stock.entity;

public class DatayesdbpSecurity {
	private Long SecurityIdInt;
	private String TickerSymbol;
	private String SecShortName;
	private String SecFullName;
	
	
	public DatayesdbpSecurity(){
	}
	
	public DatayesdbpSecurity(Long securityIdInt, String tickerSymbol,
			String secShortName, String secFullName) {
		super();
		SecurityIdInt = securityIdInt;
		TickerSymbol = tickerSymbol;
		SecShortName = secShortName;
		SecFullName = secFullName;
	}
	public Long getSecurityIdInt() {
		return SecurityIdInt;
	}
	public void setSecurityIdInt(Long securityIdInt) {
		SecurityIdInt = securityIdInt;
	}
	public String getTickerSymbol() {
		return TickerSymbol;
	}
	public void setTickerSymbol(String tickerSymbol) {
		TickerSymbol = tickerSymbol;
	}
	public String getSecShortName() {
		return SecShortName;
	}
	public void setSecShortName(String secShortName) {
		SecShortName = secShortName;
	}
	public String getSecFullName() {
		return SecFullName;
	}
	public void setSecFullName(String secFullName) {
		SecFullName = secFullName;
	}
	
}
