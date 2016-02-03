package com.datayes.bdb.theme.stock.entity;


public class NewsElasticSearch {
	private String title;
	private Long newsID;
	private String siteName;
	private String publishTimeStr;
	private String publishTime;//
	private String url;
	private String score;

	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getNewsID() {
		return newsID;
	}
	public void setNewsID(Long newsID) {
		this.newsID = newsID;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTimestamp) {
		this.publishTime = publishTimestamp;
	}
	
	public String getPublishTimeStr(){
		return publishTimeStr;
	}
	
	public void setPublishTimeStr(String publishTimeStr){
		this.publishTimeStr = publishTimeStr;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "NewsElasticSearch [title=" + title + ", newsID=" + newsID
				+ ", siteName=" + siteName + ", publishTime=" + publishTime
				+ ", url=" + url + "]";
	}

}
