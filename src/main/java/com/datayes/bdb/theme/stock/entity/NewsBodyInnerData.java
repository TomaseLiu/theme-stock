package com.datayes.bdb.theme.stock.entity;


import com.datayes.bdb.theme.stock.entity.NewsBodyInnerData.NewsBodyInnerDataResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class NewsBodyInnerData extends NewsBodyData<NewsBodyInnerDataResult> {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class NewsBodyInnerDataResult {
		private Long newsID;
		private String newsTitle;
		private String newsSummary;
		private String newsBody;
		private String newsURL;
		private String newsOriginSource;
		private String newsAuthor;
		private String newsPublishSite;
		private String newsPublishTime;
		private String newsInsertTime;
		public Long getNewsID() {
			return newsID;
		}
		public void setNewsID(Long newsID) {
			this.newsID = newsID;
		}
		public String getNewsTitle() {
			return newsTitle;
		}
		public void setNewsTitle(String newsTitle) {
			this.newsTitle = newsTitle;
		}
		public String getNewsSummary() {
			return newsSummary;
		}
		public void setNewsSummary(String newsSummary) {
			this.newsSummary = newsSummary;
		}
		public String getNewsBody() {
			return newsBody;
		}
		public void setNewsBody(String newsBody) {
			this.newsBody = newsBody;
		}
		public String getNewsURL() {
			return newsURL;
		}
		public void setNewsURL(String newsURL) {
			this.newsURL = newsURL;
		}
		public String getNewsOriginSource() {
			return newsOriginSource;
		}
		public void setNewsOriginSource(String newsOriginSource) {
			this.newsOriginSource = newsOriginSource;
		}
		public String getNewsAuthor() {
			return newsAuthor;
		}
		public void setNewsAuthor(String newsAuthor) {
			this.newsAuthor = newsAuthor;
		}
		public String getNewsPublishSite() {
			return newsPublishSite;
		}
		public void setNewsPublishSite(String newsPublishSite) {
			this.newsPublishSite = newsPublishSite;
		}
		public String getNewsPublishTime() {
			return newsPublishTime;
		}
		public void setNewsPublishTime(String newsPublishTime) {
			this.newsPublishTime = newsPublishTime;
		}
		public String getNewsInsertTime() {
			return newsInsertTime;
		}
		public void setNewsInsertTime(String newsInsertTime) {
			this.newsInsertTime = newsInsertTime;
		}
		
		
	}
	
}
	