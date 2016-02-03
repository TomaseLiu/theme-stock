package com.datayes.bdb.theme.stock.test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;














import com.datayes.bdb.theme.stock.business.NewsSearcher;
import com.datayes.bdb.theme.stock.business.TextAnalizer;
import com.datayes.bdb.theme.stock.constant.ConfigConst;
import com.datayes.bdb.theme.stock.dao.datayesdbp.DatayesdbpMapper;
import com.datayes.bdb.theme.stock.dao.mongo.MongoThemeStockDao;
import com.datayes.bdb.theme.stock.dao.news.NewsMapper;
import com.datayes.bdb.theme.stock.entity.DatayesdbpSecurity;
import com.datayes.bdb.theme.stock.entity.MongoThemeStock;
import com.datayes.bdb.theme.stock.entity.NewsElasticSearch;
import com.datayes.bdb.theme.stock.entity.ThemeSecurityObjPair;
//import com.datayes.bdb.theme.stock.entity.News;
import com.datayes.bdb.theme.stock.entity.ThemeText;
import com.datayes.bdb.theme.stock.util.DateUtil;




//import com.jcg.examples.repo.StockRepo;








import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.WordDictionary;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;


@Service
public class DBTest {
	
	@Autowired
	NewsMapper newsMapper;
	
	@Autowired
	DatayesdbpMapper datayesdbpMapper;

	@Autowired(required=false)
	MongoThemeStockDao mongoThemeStockDao;
	
	//@Autowired
	NewsSearcher newsSearcher = new NewsSearcher();
	
	@Autowired
	TextAnalizer textAnalizer;
	
	public List<ThemeText> getNewsFromDB(){
		Date endDate = new Date();
		Date startDate = endDate;
		startDate = DateUtil.addDay(startDate, -2);
		List<ThemeText> newsList = newsMapper.getNews(startDate, endDate);
		return newsList;
	}
	
	public List<DatayesdbpSecurity> getSecurtyFromDB(){
		List<DatayesdbpSecurity> securityList = datayesdbpMapper.getSecurityList();
		return securityList;
	}
	
	public List<MongoThemeStock> getThemeStockInfoFromMongo(String themeName){

		Date startTime = new Date();
		Date endTime = new Date();
		startTime = DateUtil.addDay(startTime, -50);
		System.out.println("startTime:" + DateUtil.dateToStr(startTime, DateUtil.DatePattern.day));
		
		String startTimeStr = DateUtil.dateToStr(startTime, DateUtil.DatePattern.day);
		String endTimeStr = DateUtil.dateToStr(endTime, DateUtil.DatePattern.day);
		List<MongoThemeStock> mongoThemeStockList = mongoThemeStockDao.searchByThemeName(themeName, startTimeStr, endTimeStr);
		
		System.out.println("MongoThemeStock size: " + mongoThemeStockList.size());
		for(MongoThemeStock mongoThemeStock: mongoThemeStockList){
			System.out.println("themeName: " + mongoThemeStock.getThemeName());
			System.out.println("webSite: " + mongoThemeStock.getWebSite());
			System.out.println(mongoThemeStock.getRelateStock());
		}
		return null;
	}
	
	public void getNewsWithKeyword(String keyword){
		Boolean sortByTime = true;
		Integer defaultPageSize = 1000;
		Date startDate = new Date();
		Date endDate = new Date();
		startDate = DateUtil.addDay(startDate, -200);
		
		
		String jiebaSearchDictPath = ConfigConst.JIEBA_SEARCH_DICT_PATH;
		WordDictionary.getInstance().init(Paths.get(jiebaSearchDictPath));
		JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();
		
		List<NewsElasticSearch> newsElasticSearchList = newsSearcher.searchNews(keyword, startDate, endDate, sortByTime, defaultPageSize, jiebaSegmenter);
		List<String> newsBodyList = new ArrayList<String>();
		System.out.println(newsElasticSearchList.size());
		
		System.out.println("start at: " + new Date());
		List<DatayesdbpSecurity> securityList = getSecurtyFromDB();
		
		String jiebaUserDictPath = ConfigConst.JIEBA_USER_DICT_PATH;
		WordDictionary.getInstance().init(Paths.get(jiebaUserDictPath));
		JiebaSegmenter segmenter = new JiebaSegmenter();
		List<ThemeSecurityObjPair> themeSecurityPairList = new ArrayList<ThemeSecurityObjPair>();
		for(NewsElasticSearch newsElasticSearch: newsElasticSearchList){
			//System.out.println(newsElasticSearch.getTitle());
			String body;
			try {
				body = newsSearcher.searchNewsBodyByNewsId(newsElasticSearch.getNewsID().toString());
				String textID = "news_" + newsElasticSearch.getNewsID().toString();
				String textTitle = newsElasticSearch.getTitle();
				String publishTimeStr = newsElasticSearch.getPublishTimeStr();
				String siteName = newsElasticSearch.getSiteName();
				String type = "news";
				ThemeText themeText = new ThemeText(textID, textTitle, publishTimeStr, siteName, body, type);
				List<ThemeSecurityObjPair> tmpThemeSecPairList = textAnalizer.getThemeSecPairFromOneThemeText(themeText, keyword, 1L, securityList, segmenter);
				if(tmpThemeSecPairList != null && !tmpThemeSecPairList.isEmpty())
					themeSecurityPairList.addAll(tmpThemeSecPairList);
				//newsBodyList.add(body);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(ThemeSecurityObjPair themeSecurityPair: themeSecurityPairList){
			System.out.println(themeSecurityPair.getThemeName() + " <----> " + themeSecurityPair.getDatayesdbpSecurity().getSecShortName() + 
					": " + themeSecurityPair.getPairDesc());
		}
		System.out.println("finished at: " + new Date());
		
	}
	
}
