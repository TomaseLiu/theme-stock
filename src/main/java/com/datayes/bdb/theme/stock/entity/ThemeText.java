package com.datayes.bdb.theme.stock.entity;


public class ThemeText {
	private String textID;
	private String title;
	private String publishTime;
	/*about: site name*/
	private String source;
	private String body;
	/*values: news/report*/
	private String type;
	
	
	public ThemeText(){
	}
	public ThemeText(String textID, String title, String publish_time, String site_name,
			String body, String type) {
		super();
		this.textID = textID;
		this.title = title;
		this.publishTime = publish_time;
		this.source = site_name;
		this.body = body;
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTextID() {
		return textID;
	}

	public void setTextID(String textID) {
		this.textID = textID;
	}
	
	
	
}
