package com.datayes.bdb.theme.stock.entity;

import java.util.Date;

public class BigdataThemeSecRel {
	
	private Long themeID;
	
	private String themeName;
	
	private Long securityID;
	
	private String tickerSymbol;
	
	private String secShortName;
	
	private String relDesc;
	
	private String type;
	
	private Date beginTime;
	
	private Date endTime;
	
	private Integer isActive;

	
	public BigdataThemeSecRel(){
	}
	
	public BigdataThemeSecRel(Long themeID, String themeName, Long securityID,
			String secShortName, String type) {
		super();
		this.themeID = themeID;
		this.themeName = themeName;
		this.securityID = securityID;
		this.secShortName = secShortName;
		this.type = type;
	}

	public Long getThemeID() {
		return themeID;
	}

	public void setThemeID(Long themeID) {
		this.themeID = themeID;
	}

	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public Long getSecurityID() {
		return securityID;
	}

	public void setSecurityID(Long securityID) {
		this.securityID = securityID;
	}

	public String getTickerSymbol() {
		return tickerSymbol;
	}

	public void setTickerSymbol(String tickerSymbol) {
		this.tickerSymbol = tickerSymbol;
	}

	public String getSecShortName() {
		return secShortName;
	}

	public void setSecShortName(String secShortName) {
		this.secShortName = secShortName;
	}

	public String getRelDesc() {
		return relDesc;
	}

	public void setRelDesc(String relDesc) {
		this.relDesc = relDesc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
	
	
	
	

}
