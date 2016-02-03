package com.datayes.bdb.theme.stock.constant;

import com.datayes.bdb.theme.stock.util.Config;

public class ConfigConst {
	public static final String S3_REPORT_BUCKET_NAME = Config.DATASOURCE.get("datasource.s3.bucket.report");
	
	public static final String MONGO_GREATER_THAN_OPERATOR = "$gte";
	public static final String MONGO_LESS_THAN_EQUAL_OPERATOR = "$lte";

	public static final String HTTP_MARKET_BASE_URL = Config.DATASOURCE.get("datasource.http.market.base_url");

	
	public static final String[] SEARCH_URL_LIST = Config.DATASOURCE.get("datasource.search.url").split(",");
	public static final String CLUSTER_NAME = Config.DATASOURCE.get("datasource.search.cluster.name");
	public static final String SEARCH_INDEX = Config.DATASOURCE.get("datasource.search.index");
	public static final String SEARCH_TYPE = Config.DATASOURCE.get("datasource.search.index.type");

	
	/*用于搜索的jieba词典*/
	public static final String JIEBA_SEARCH_DICT_PATH = Config.BUSINESS.get("business.jieba.search.dict.path");
	/*jieba 词典路径文件夹*/
	public static final String JIEBA_USER_DICT_PATH = Config.BUSINESS.get("business.jieba.user.dict.path");
	/*jieba 词典文件路径*/
	public static final String JIEBA_USER_DICT_FILE_PATH = Config.BUSINESS.get("business.jieba.user.dict.file.path");
	
	/*已经处理过的主题的列表，用于对比当前数据库，以找到新添加的主题；程序开始时，需要更新一次*/
	public static final String HANDLED_THEMES_FILE_PATH = Config.BUSINESS.get("business.theme.handled.file.path");
	
	/*已经经过QA，数据库中需要添加或删除的theme-security对数据*/
	public static final String THEME_SEC_PAIR_TO_BE_UPDATE_FILE_PATH = Config.BUSINESS.get("business.theme.security.pair.to.be.update.file.path");
	
	public static final String NEWS_TIME_INTERVAL_UNSCHEDULE = Config.BUSINESS.get("business.theme.news.time.interval.unschedule");
	
	public static final String NEWS_TIME_INTERVAL_SCHEDULE = Config.BUSINESS.get("business.theme.news.time.interval.schedule");
	
	public static final String THEME_KEYWORD_SUFFIX = Config.BUSINESS.get("business.theme.keyword.suffix");
	
	public static final String MONGO_TIME_INTERVAL_UNSCHEDULE = Config.BUSINESS.get("business.mongo.time.interval.unschedule");
	
	public static final String MONGO_TIME_INTERVAL_SCHEDULE = Config.BUSINESS.get("business.mongo.time.interval.schedule");
	
	public static final String QA_FOLDER_PATH = Config.BUSINESS.get("business.qa.folder.path");
	
}
