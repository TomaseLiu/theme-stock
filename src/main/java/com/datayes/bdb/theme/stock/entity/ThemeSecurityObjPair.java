package com.datayes.bdb.theme.stock.entity;

import java.util.Date;

/*
 * @Author dengxiang.liu@datayes.com
 * @Description 
 * @Create Date 2016-01-18
 */
public class ThemeSecurityObjPair {
	/* id of theme */
	private Long themeID;
	/*name of the theme*/
	private String ThemeName;
	/*short name of the security*/
	private DatayesdbpSecurity datayesdbpSecurity;
	
	private String PairDesc;
	/*time when the pair established*/
	private Date FindTime;
	
	private String type;
	
	public ThemeSecurityObjPair(){
	}
	
	public ThemeSecurityObjPair(Long themeID, String themeName, DatayesdbpSecurity datayesdbpSecurity, String pairDesc, Date findTime, String type) {
		super();
		this.themeID = themeID;
		this.ThemeName = themeName;
		this.datayesdbpSecurity = datayesdbpSecurity;
		this.PairDesc = pairDesc;
		this.FindTime = findTime;
		this.type = type;
	}
	
	
	
	
	public Long getThemeID() {
		return themeID;
	}

	public void setThemeID(Long themeID) {
		this.themeID = themeID;
	}

	public String getThemeName() {
		return ThemeName;
	}
	public void setThemeName(String themeName) {
		this.ThemeName = themeName;
	}
	
	public DatayesdbpSecurity getDatayesdbpSecurity() {
		return datayesdbpSecurity;
	}

	public void setDatayesdbpSecurity(DatayesdbpSecurity datayesdbpSecurity) {
		this.datayesdbpSecurity = datayesdbpSecurity;
	}

	public String getPairDesc() {
		return PairDesc;
	}
	public void setPairDesc(String pairDesc) {
		PairDesc = pairDesc;
	}
	public Date getFindTime() {
		return FindTime;
	}
	public void setFindTime(Date findTime) {
		FindTime = findTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
