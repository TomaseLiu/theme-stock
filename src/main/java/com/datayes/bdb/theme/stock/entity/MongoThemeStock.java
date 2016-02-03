package com.datayes.bdb.theme.stock.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection="theme_stock")
public class MongoThemeStock {
	
	@Id
	private String id;
	@Field("date")
	private String date;
	@Field("theme_name")
	private String themeName;
	@Field("insert_time")
	private String insertTime;
	@Field("web_site")
	private String webSite;
	@Field("relate_all")
	private List<String> relateStock;
	
	
	public MongoThemeStock(){
	}
	
	
	public MongoThemeStock(String id, String date, String themeName,
			String insertTime, String webSite, List<String> relateStock) {
		super();
		this.id = id;
		this.date = date;
		this.themeName = themeName;
		this.insertTime = insertTime;
		this.webSite = webSite;
		this.relateStock = relateStock;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getThemeName() {
		return themeName;
	}
	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}
	public String getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}
	public String getWebSite() {
		return webSite;
	}
	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}
	public List<String> getRelateStock() {
		return relateStock;
	}
	public void setRelateStock(List<String> relateStock) {
		this.relateStock = relateStock;
	}
	
	@Override
	public String toString(){
		return "theme_name: " + themeName + ", insert_time: " + insertTime + ", web_site: " + webSite;
	}
	
	
	
}
