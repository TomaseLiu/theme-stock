package com.datayes.bdb.theme.stock.entity;

import java.util.Date;

public class BigdataTheme {

	private Long themeID;
	private String themeName;
	private String themeDesc;
	
	private Date updateTime;
	
	public BigdataTheme(){
	}
	
	public BigdataTheme(Long themeID, String themeName, String themeDesc, Date updateTime) {
		super();
		this.themeID = themeID;
		this.themeName = themeName;
		this.themeDesc = themeDesc;
		this.updateTime = updateTime;
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
	public String getThemeDesc() {
		return themeDesc;
	}
	public void setThemeDesc(String themeDesc) {
		this.themeDesc = themeDesc;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	
	
	
}
