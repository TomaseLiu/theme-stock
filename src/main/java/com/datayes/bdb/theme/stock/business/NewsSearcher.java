package com.datayes.bdb.theme.stock.business;

import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;





import com.datayes.bdb.theme.stock.constant.ConfigConst;
import com.datayes.bdb.theme.stock.entity.NewsBodyInnerData;
import com.datayes.bdb.theme.stock.entity.NewsBodyInnerData.NewsBodyInnerDataResult;
import com.datayes.bdb.theme.stock.entity.DatayesdbpSecurity;
import com.datayes.bdb.theme.stock.entity.NewsElasticSearch;
import com.datayes.bdb.theme.stock.entity.NewsSearchResult;
import com.datayes.bdb.theme.stock.entity.ThemeSecurityObjPair;
import com.datayes.bdb.theme.stock.entity.ThemeText;
import com.datayes.bdb.theme.stock.util.DateUtil;
import com.datayes.search.client.es.EsSearchRequest;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.WordDictionary;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.huaban.analysis.jieba.SegToken;

@Repository
public class NewsSearcher extends BaseSearcher {
	
	private static final Logger LOG = LoggerFactory.getLogger(NewsSearcher.class);
	private static final String FIELD_TITLE = "title";
	private static final String FIELD_INDEXTITLE = "titleSeg";
	private static final String FIELD_BODY = "body";
	private static final String FIELD_NEWSID = "id";
	
	private static final String FIELD_INDEXBODY = "bodySeg";
	private static final String FIELD_SITENAME = "siteName";
	private static final String FIELD_URL = "url";
	private static final String FIELD_PUBLISH_TIME = "publishTime";
	private static final String FIELD_GROUP_ID = "groupId";
	private static final String MAIN_GROUP_ID = "mainGroupId";
	private static final String FIELD_SOURCE_TYPE = "sourceType";
	private static final String FIELD_USEFULL = "usefull";
	private static final String FIELD_IS_ACTIVE = "isActive";
	private static final int GROUP_ID_FILTER_NUM=-1;
	private static final String SOURCE_TYPE_FILTER_STR = "report";
	private static final String SOURCE_TYPE_NEWS = "news";
	private static final int IS_ACTIVE_MUST_NUM=1;
	private static final int USEFULL_MUST_NUM=1;
	private static final int LIMIT_RESULT_SIZE = 10;
	private static final int NOT_MAIN_GROUP_ID = -1;
	
	private static final RestTemplate restTemplate = new RestTemplate();
	static String host = ConfigConst.HTTP_MARKET_BASE_URL;
	public static String news_detail_uri = "/api/subject/getNewsContentAll.json?field=&newsID=%s";
	
	public SearchResponse advancedSearchNews(String keyWord, Date startDate, Date endDate, int pageIndex, int size, boolean sortByTime){
		
		LOG.info("Advance search news : keyword={},from {} to {}, page from {} to {} ", 
				keyWord, DateUtil.dateToStr(startDate, DateUtil.DatePattern.day), DateUtil.dateToStr(endDate, DateUtil.DatePattern.day), 
				pageIndex, size);
		
		String [] fieldNames = {FIELD_INDEXTITLE,FIELD_INDEXBODY};
		
		MultiMatchQueryBuilder queryBuilder = (keyWord == null || keyWord.equals("")) ? null : QueryBuilders.multiMatchQuery(keyWord, fieldNames).type(MatchQueryBuilder.Type.PHRASE);
		
		long start = startDate.getTime();
		long end = endDate.getTime();
		LOG.info("start time = {} ,end time = {}", start, end);
		FilterBuilder filterBuilder1 = FilterBuilders.rangeFilter(FIELD_PUBLISH_TIME).from(start).to(end);
		FilterBuilder filterBuilder2 = FilterBuilders.boolFilter().mustNot(FilterBuilders.termFilter(FIELD_GROUP_ID, GROUP_ID_FILTER_NUM));
		FilterBuilder filterBuilder3 = FilterBuilders.boolFilter().mustNot(FilterBuilders.termFilter(FIELD_SOURCE_TYPE, SOURCE_TYPE_FILTER_STR));
		FilterBuilder filterBuilder4 = FilterBuilders.boolFilter().must(FilterBuilders.termFilter(FIELD_IS_ACTIVE, IS_ACTIVE_MUST_NUM));
		FilterBuilder filterBuilder5 = FilterBuilders.boolFilter().must(FilterBuilders.termFilter(FIELD_USEFULL, USEFULL_MUST_NUM));
		/*去重*/
		FilterBuilder filterBuilder7 = FilterBuilders.boolFilter().mustNot(FilterBuilders.termFilter(MAIN_GROUP_ID, NOT_MAIN_GROUP_ID));
		FilterBuilder filterBuilder6 = FilterBuilders.boolFilter().must(FilterBuilders.termFilter(FIELD_SOURCE_TYPE, SOURCE_TYPE_FILTER_STR));
		
		
		FilterBuilder filterBuilders = FilterBuilders.andFilter(filterBuilder1,filterBuilder2,filterBuilder3,filterBuilder4,filterBuilder5);

		SearchResponse response = null;
		if(sortByTime){
			SortBuilder sortBuilder = SortBuilders.fieldSort(FIELD_PUBLISH_TIME).order(SortOrder.ASC);
			EsSearchRequest request = new EsSearchRequest(pageIndex*size, size, queryBuilder, filterBuilders, null, null, ConfigConst.SEARCH_INDEX, false, SearchType.DFS_QUERY_THEN_FETCH, null, ConfigConst.SEARCH_TYPE);
			response = client.search(request);
		}
		else{
			EsSearchRequest request = new EsSearchRequest(pageIndex*size, size, queryBuilder, filterBuilders, null, null, ConfigConst.SEARCH_INDEX, false, SearchType.DFS_QUERY_THEN_FETCH, null, ConfigConst.SEARCH_TYPE);
			response = client.search(request);
		}
		
		LOG.info("Advanced search result size = {}",response.getHits().totalHits());
		return response;
	}
	
	public SearchResponse baseSearchNews(String keyWord,Date startDate,Date endDate,int pageIndex,int size, boolean sortByTime){
		LOG.info("Base search news : keyword={},from {} to {}, page from {} to {} ",keyWord,startDate,endDate,pageIndex,size);
		String [] fieldNames = { FIELD_INDEXTITLE , FIELD_INDEXBODY };
		QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyWord, fieldNames);
		long start = startDate.getTime();
		long end = endDate.getTime();
		FilterBuilder filterBuilder1 = FilterBuilders.rangeFilter(FIELD_PUBLISH_TIME).from(start).to(end);
		FilterBuilder filterBuilder2 = FilterBuilders.rangeFilter(FIELD_GROUP_ID).from(GROUP_ID_FILTER_NUM+0.01);
		FilterBuilder filterBuilders = FilterBuilders.andFilter(filterBuilder2);

		SearchResponse response = null;
		if(sortByTime){
			LOG.info("case search sort");
			SortBuilder sortBuilder = SortBuilders.fieldSort(FIELD_PUBLISH_TIME).order(SortOrder.DESC);
			EsSearchRequest request = new EsSearchRequest(pageIndex*size, size, queryBuilder, filterBuilders, null, sortBuilder, ConfigConst.SEARCH_INDEX, false, SearchType.DFS_QUERY_THEN_FETCH, null, ConfigConst.SEARCH_TYPE);
			response = client.search(request);
		}
		else{
			LOG.info("case search non sort");
			EsSearchRequest request = new EsSearchRequest(pageIndex*size, size, queryBuilder, filterBuilders, null, null, ConfigConst.SEARCH_INDEX, false, SearchType.DFS_QUERY_THEN_FETCH, null, ConfigConst.SEARCH_TYPE);
			response = client.search(request);
		}
		
		LOG.info("Base search result size = {}",response.getHits().totalHits());
		return response;
	}
	
	public NewsSearchResult searchNews(String keyWord, Date startDate, Date endDate, boolean sortByTime, Integer pageIndex, Integer pageSize) {
		if(StringUtils.isEmpty(keyWord))return null;
		List<NewsElasticSearch> list = new ArrayList<NewsElasticSearch>();
		SearchResponse response = null;
		String splittedWord = "";
		JiebaSegmenter segmenter = new JiebaSegmenter();
		List<SegToken> tokens = segmenter.process(keyWord, SegMode.INDEX);
		for (SegToken thisToken : tokens) {
			splittedWord+=(thisToken.word+" ");
		}
		try{
			LOG.info("before. timestamp"+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
			response = advancedSearchNews(splittedWord, startDate, endDate, pageIndex, pageSize,sortByTime);
			if(response != null && response.getHits().totalHits() < LIMIT_RESULT_SIZE){
				response = baseSearchNews(splittedWord, startDate, endDate, pageIndex, pageSize,sortByTime);
			}
			LOG.info("after. timestamp"+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
		}catch(Exception e){
			LOG.error("News search Error,key word = {}",splittedWord,e);
		}
		list = responseToNews(response);
		long total = response.getHits().getTotalHits();
		return new NewsSearchResult(list, pageIndex, pageSize, total);
	}
	
	public List<NewsElasticSearch> searchNews(String keyWord, Date startDate, Date endDate, Boolean sortByTime, 
			Integer defaultPageSize, JiebaSegmenter jiebaSegmenter){
		if(StringUtils.isEmpty(keyWord))
			return null;
		List<NewsElasticSearch> newsElasticSearchList = new ArrayList<NewsElasticSearch>();
		SearchResponse searchResponse = null;
		String splittedWord = "";
		//JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();
		List<SegToken> segTokenList = jiebaSegmenter.process(keyWord, SegMode.INDEX);
		for(SegToken token: segTokenList){
			splittedWord += (token.word + " ");
		}
		Integer pageIndex = 0;
		Boolean searchContinue = true;
		LOG.info("splittedWord: {}", splittedWord);
		try{
			while(searchContinue){
				searchResponse = advancedSearchNews(splittedWord, startDate, endDate, pageIndex, defaultPageSize, sortByTime);
				if(searchResponse == null) break;
				List<NewsElasticSearch> curNewsElasticSearchList = responseToNews(searchResponse);
				newsElasticSearchList.addAll(curNewsElasticSearchList);
				if(curNewsElasticSearchList.size() < defaultPageSize)
					searchContinue = false;
			}
		}catch (Exception e){
			LOG.error("News saerch error, keyword = {}", splittedWord, e);
		}
		return newsElasticSearchList;
	}
	
	private List<NewsElasticSearch> responseToNews( SearchResponse response) {
		List<NewsElasticSearch> list = new ArrayList<NewsElasticSearch>();
		if (response.getHits() != null) {
			for (SearchHit hit : response.getHits()) {
				NewsElasticSearch news = new NewsElasticSearch();
				news.setTitle(hit.getSource().get(FIELD_TITLE) != null ? hit.getSource().get(FIELD_TITLE).toString() : null);
				news.setNewsID((hit.getSource().get(FIELD_NEWSID) != null ? Long.valueOf(hit.getSource().get(FIELD_NEWSID).toString()) : null));
				
				DateFormat dfOri = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				String publishTimeStr = hit.getSource().get(FIELD_PUBLISH_TIME) != null ? hit.getSource().get(FIELD_PUBLISH_TIME).toString():null;
				Date publishTimeNorm = new Date();
				String publishTimeLongStr = null;
				
				try {
					publishTimeNorm = dfOri.parse(publishTimeStr);
					Long publishTimeLong = Long.valueOf(dfOri.parse(publishTimeStr).getTime());
					publishTimeLongStr = publishTimeLong.toString();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				news.setPublishTime(publishTimeLongStr);
				news.setPublishTimeStr(publishTimeStr);
				
				news.setSiteName(hit.getSource().get(FIELD_SITENAME) != null ? hit.getSource().get(FIELD_SITENAME).toString() : null);
				news.setUrl(hit.getSource().get(FIELD_URL) != null ? hit.getSource().get(FIELD_URL).toString() : null);
				news.setScore(String.valueOf(hit.getScore()));
				list.add(news);
			}
		}
		Collections.sort(list, new Comparator<NewsElasticSearch>(){
			@Override
			public int compare(NewsElasticSearch current, NewsElasticSearch other) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dengxiang
				Long currentPublishTime=0l;
				Long otherPublishTime=0l;
				try {
					currentPublishTime = Long.valueOf(current.getPublishTime());
					otherPublishTime = Long.valueOf(other.getPublishTime());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return currentPublishTime.compareTo(otherPublishTime);
			}
		});
		
		return list;
	}
	
	public String searchNewsBodyByNewsId(String newsId) throws Exception{
		String url = String.format(host + news_detail_uri, newsId);
		String news_body="";
		try{
			NewsBodyInnerData newsBodyInnerData = restTemplate.getForObject(url, NewsBodyInnerData.class);
			if (newsBodyInnerData != null &&newsBodyInnerData.getData() != null
					&& !newsBodyInnerData.getData().isEmpty()) {
				NewsBodyInnerDataResult res = newsBodyInnerData.getData().get(0);
				news_body = (res.getNewsBody());
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return news_body;
	}
	
	public List<ThemeText> searchThemeTextWithKeyword(String keyword, Date startDate, Date endDate, Boolean sortByTime, Integer defaultPageSize){
		List<ThemeText> themeTextList = new ArrayList<ThemeText>();

		
		/* load jieba dictionary for search */
		String jiebaSearchDictPath = ConfigConst.JIEBA_SEARCH_DICT_PATH;
		WordDictionary.getInstance().init(Paths.get(jiebaSearchDictPath));
		JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();
		
		List<NewsElasticSearch> newsElasticSearchList = searchNews(keyword, startDate, endDate, sortByTime, defaultPageSize, jiebaSegmenter);
		
		for(NewsElasticSearch newsElasticSearch: newsElasticSearchList){
			String body;
			try {
				body = searchNewsBodyByNewsId(newsElasticSearch.getNewsID().toString());
				String textID = "news_" + newsElasticSearch.getNewsID().toString();
				String textTitle = newsElasticSearch.getTitle();
				String publishTimeStr = newsElasticSearch.getPublishTimeStr();
				String siteName = newsElasticSearch.getSiteName();
				//String type = "news";
				ThemeText themeText = new ThemeText(textID, textTitle, publishTimeStr, siteName, body, SOURCE_TYPE_NEWS);
				themeTextList.add(themeText);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return themeTextList;
	}
	
	
}
