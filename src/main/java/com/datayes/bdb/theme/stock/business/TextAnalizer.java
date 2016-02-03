package com.datayes.bdb.theme.stock.business;

import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datayes.bdb.theme.stock.constant.ConfigConst;
import com.datayes.bdb.theme.stock.entity.BigdataTheme;
import com.datayes.bdb.theme.stock.entity.DatayesdbpSecurity;
import com.datayes.bdb.theme.stock.entity.ThemeSecurityObjPair;
import com.datayes.bdb.theme.stock.entity.ThemeText;
import com.datayes.bdb.theme.stock.util.DateUtil;
import com.datayes.bdb.theme.stock.util.TextUtil;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.huaban.analysis.jieba.WordDictionary;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;


@Service
public class TextAnalizer {
	
	
	static Logger logger = LoggerFactory.getLogger(TextAnalizer.class);
	
	static String jiebaUserDictPath = ConfigConst.JIEBA_USER_DICT_PATH;
	
	
	/*
	static{
		WordDictionary.getInstance().init(Paths.get(jiebaUserDictPath));
	}
	*/
	
	@Autowired
	NewsSearcher newsSearcher;
	
	/**
	 * @deprecated
	 * @author dengxiang.liu
	 * @description get theme and security relationship from one piece of text, 
	 * @important THE TOPIC OF THESE TEXTS ARE UNKNOWN;
	 * @see getThemeSecPairListFromTextList;
	 * @param themeNameList: theme name should contain a suffix '概念股';
	 */
	public List<ThemeSecurityObjPair> getThemeSecPairListFromOneText(ThemeText textObj, List<String> themeNameList, 
			List<DatayesdbpSecurity> securityList,
			JiebaSegmenter segmenter){
		/* set of theme names */
		Set<String> themeNameSet = new HashSet<String>(themeNameList);
		/* map from security name (short and full) to datayesdbpSecurity object */
		Map<String, DatayesdbpSecurity> securityName2Obj = new HashMap<String, DatayesdbpSecurity>();
		for(DatayesdbpSecurity datayesdbpSecurity: securityList){
			String fullName = datayesdbpSecurity.getSecFullName();
			String shortName = datayesdbpSecurity.getSecShortName();
			securityName2Obj.put(fullName, datayesdbpSecurity);
			securityName2Obj.put(shortName, datayesdbpSecurity);
		}
		/* result */
		List<ThemeSecurityObjPair> themeSecPairList = new ArrayList<ThemeSecurityObjPair>();
		
		/* unpackage news' or report's info */
		String newsBody =  textObj.getBody();
		String publishTimeStr = textObj.getPublishTime();
		Date publishTime = new Date();
		String pairType = "text";
		try {
			//DateFormat dfOri = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			publishTime = formatter.parse(publishTimeStr);
			//DateUtil.strToDate(publishTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		/* List of sentences */
		List<String> sentenceList = TextUtil.getSentenceList(newsBody);
		
		for(String sentence: sentenceList){
			try{
				if(sentence != null && !sentence.isEmpty()){
					List<SegToken> tokens = segmenter.process(sentence, SegMode.SEARCH);
					/* set of hitten themes and securities */
					Set<String> hitThemeSet = new HashSet<String>();
					Set<String> hitSecSet = new HashSet<String>();
			
					for(SegToken token: tokens){
						String word = token.word;
						if(themeNameSet.contains(word)) hitThemeSet.add(word);
						if(securityName2Obj.containsKey(word)) hitSecSet.add(word);
					}
					if(!hitThemeSet.isEmpty() && !hitSecSet.isEmpty()){
						for(String hitTheme: hitThemeSet){
							for(String hitSec: hitSecSet){
								/* TODO: fixed */
								Long tmpThemeID = -1L;
								ThemeSecurityObjPair themeStockPair = new ThemeSecurityObjPair(tmpThemeID, hitTheme, securityName2Obj.get(hitSec), sentence, publishTime, pairType);
								themeSecPairList.add(themeStockPair);
							}
						}
					}
				}
			}catch (Exception e){
				logger.error(e.toString());
			}
		}
		return themeSecPairList;
	}
	
	/**
	 * @deprecated
	 * @Author dengxiang.liu
	 * @param textObjList
	 * @param themeNameList
	 * @param securityList
	 * @return theme security pair list
	 * @Desc The input texts (textObjList) have no topics, which means we don't know the topic of these theme texts;
	 */
	/* get theme-security pairs with theme list; */
	public List<ThemeSecurityObjPair> getThemeSecPairListFromTextList(List<ThemeText> textObjList, List<String> themeNameList, 
			List<DatayesdbpSecurity> securityList){
		WordDictionary.getInstance().init(Paths.get(jiebaUserDictPath));
		JiebaSegmenter segmenter = new JiebaSegmenter();
		List<ThemeSecurityObjPair> themeSecPairList = new ArrayList<ThemeSecurityObjPair>();
		for(ThemeText themeText: textObjList){
			try{
				List<ThemeSecurityObjPair> themeSecPairListCur = getThemeSecPairListFromOneText(themeText, themeNameList, securityList, segmenter);
				if(themeSecPairListCur != null && !themeSecPairListCur.isEmpty())
					themeSecPairList.addAll(themeSecPairListCur);
			}catch(Exception e){
				logger.error(e.toString());
			}
		}
		return themeSecPairList;
	}
	
	
	
	
	/**
	 * @Author:	dengxiang.liu
	 * @Param:	textObj, target text with ThemeText format;
	 * @Param:	themeName, target theme name; eg. "一带一路概念股"
	 * @Param:	securityList, list of all securities;
	 * @Param:	segmenter, jieba segmenter;
	 * @Desc:	get theme-security pairs from a ThemeText which comes from the searching (by keyword themeName) results;
	 * @Important: The differense between getThemeSecPairFromOneThemeText and getThemeSecPairFromOneText is that, in this function, we know the topic 
	 * of the textObj (the text comes from the searching (by themeName) results);
	 * @See getThemeSecPairFromOneText()
	 */
	public List<ThemeSecurityObjPair> getThemeSecPairFromOneThemeText(ThemeText textObj, String themeName, Long themeID,
			List<DatayesdbpSecurity> securityList, JiebaSegmenter segmenter){
		Map<String, DatayesdbpSecurity> securityName2Obj = new HashMap<String, DatayesdbpSecurity>();
		for(DatayesdbpSecurity datayesdbpSecurity: securityList){
			String fullName = datayesdbpSecurity.getSecFullName();
			String shortName = datayesdbpSecurity.getSecShortName();
			if(null != fullName)
				securityName2Obj.put(fullName, datayesdbpSecurity);
			if(null != shortName)
				securityName2Obj.put(shortName, datayesdbpSecurity);
		}
		
		List<ThemeSecurityObjPair> themeSecPairList = new ArrayList<ThemeSecurityObjPair>();
		/* unpackage news' or report's info */
		String newsBody =  textObj.getBody();
		String publishTimeStr = textObj.getPublishTime();
		Date publishTime = new Date();
		String pairType = "news";
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			publishTime = formatter.parse(publishTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		List<String> sentenceList = TextUtil.getSentenceList(newsBody);
		
		for(String sentence: sentenceList){
			try{
				if(!sentence.contains(themeName))
				continue;
				if(sentence != null && !sentence.isEmpty()){
					List<SegToken> tokens = segmenter.process(sentence, SegMode.SEARCH);

					Set<String> hitSecSet = new HashSet<String>();
			
					for(SegToken token: tokens){
						String word = token.word;
						if(securityName2Obj.containsKey(word)) hitSecSet.add(word);
					}
					if(!hitSecSet.isEmpty()){
						for(String hitSec: hitSecSet){
							ThemeSecurityObjPair themeStockPair = new ThemeSecurityObjPair(themeID, themeName, securityName2Obj.get(hitSec), sentence, publishTime, pairType);
								themeSecPairList.add(themeStockPair);
						}
					}
				}
			}catch (Exception e){
				logger.error(e.toString());
			}
		}
		return themeSecPairList;
	}
	
	/**
	 * @Author dengxiang.liu
	 * @Desc get theme-security pairs with one target theme; news/report comes from searching
	 */
	/* get theme-security pairs with one target theme; news/report comes from searching */
	public List<ThemeSecurityObjPair> getThemeSecPairListFromThemeTextList(List<ThemeText> textObjList, String themeName, Long themeID,
			List<DatayesdbpSecurity> securityList){
		
		logger.info("getThemeSecPairListFromTextList, @Param: textObjList, @Param: themeName, @Param: themeID, @Param: securityList");
		logger.info("textObjList size: {}", textObjList == null ? 0:textObjList.size());
		logger.info("themeName: {}", themeName);
		logger.info("securityList size: {}", securityList == null ? 0:securityList.size());
		
		/** because the Jieba dictionary will be update with security short names and theme names every time, we should load it every time */
		WordDictionary.getInstance().init(Paths.get(jiebaUserDictPath));
		JiebaSegmenter segmenter = new JiebaSegmenter();
		List<ThemeSecurityObjPair> themeSecPairList = new ArrayList<ThemeSecurityObjPair>();
		
		/** to make sure the uniqueness of theme-security pair */
		Set<String> globalThemeSecPairSet = new HashSet<String>();
		for(ThemeText themeText: textObjList){
			try{
				List<ThemeSecurityObjPair> themeSecPairListCur = getThemeSecPairFromOneThemeText(themeText, themeName, themeID, securityList, segmenter);
				if(themeSecPairListCur != null && !themeSecPairListCur.isEmpty()){
					for(ThemeSecurityObjPair themeSecurityObjPair: themeSecPairListCur){
						String tickerSymbol = themeSecurityObjPair.getDatayesdbpSecurity().getTickerSymbol();
						
						/* if the set has already have the pair, continue */
						if(globalThemeSecPairSet.contains(tickerSymbol)){
							continue;
						}
						globalThemeSecPairSet.add(tickerSymbol);
						
						themeSecPairList.add(themeSecurityObjPair);
					}
				}
			}catch(Exception e){
				logger.error(e.toString());
			}
		}
		return themeSecPairList;
	}
	
	/**
	 * @Author: Dengxiang.Liu
	 * @Param: themeList; could be themes have never been handled(getUnhandledThemeList()) or themes have been handled, depends on isScheduale; 
	 * @Param: isSchedule; true if the themes arn't new to system;
	 * @Return: Map<Long, List<ThemeSecurityPair>>; a map from themeID to it's ThemeSecurityPair List;
	 * @Desc: Find theme-security pairs with themes' news. The results will be QA before insert into Database;
	 */
	public Map<String, List<ThemeSecurityObjPair>> findThemesSecPair(Set<BigdataTheme> themeList, List<DatayesdbpSecurity> securityList,
			Boolean isSchedule){
		
		Map<String, List<ThemeSecurityObjPair>> themeSecurityPairMap = new HashMap<String, List<ThemeSecurityObjPair>>();

		if(null == themeList)
			return themeSecurityPairMap;
		
		Date endDate = new Date();

		/** configure data scale */
		Integer newsTimeInterval = isSchedule ? 10 : 300;
		String themeKeywordSuffix = "概念";
		try{
			newsTimeInterval = isSchedule ? Integer.parseInt(ConfigConst.NEWS_TIME_INTERVAL_SCHEDULE):
				Integer.parseInt(ConfigConst.NEWS_TIME_INTERVAL_UNSCHEDULE);
			themeKeywordSuffix = ConfigConst.THEME_KEYWORD_SUFFIX;
		}catch (Exception e) {
			logger.error(e.toString());
			newsTimeInterval = isSchedule ? 10 : 300;
			themeKeywordSuffix = "概念";
		}
		Date newsStartDate = DateUtil.addDay(endDate, -1*newsTimeInterval);
		
		for(BigdataTheme bigdataTheme: themeList){
			
			try{
				List<ThemeSecurityObjPair> themeSecurityPairList = new ArrayList<ThemeSecurityObjPair>();
			
				Long themeID = bigdataTheme.getThemeID();
			
				String themeName = bigdataTheme.getThemeName();
				String keyWords = themeName + themeKeywordSuffix;
				/** search theme news with themeName+suffix */
				List<ThemeText> themeTextList = newsSearcher.searchThemeTextWithKeyword(keyWords, newsStartDate, endDate, true, 500);
				logger.info("search new with keyword: {}, result size: {}", keyWords, themeTextList==null?0:themeTextList.size());
				/** get theme-security pairs from text list*/
				List<ThemeSecurityObjPair> themeSecurityPairListNews = getThemeSecPairListFromThemeTextList(themeTextList, themeName, themeID, securityList);
				logger.info("getThemeSecpairListFromTextList, themeName: {}, themeSecurityPairList size: {}",
						themeName, themeSecurityPairListNews==null?0:themeSecurityPairListNews.size());
				if(null != themeSecurityPairListNews) 
					themeSecurityPairList.addAll(themeSecurityPairListNews);
			
				/** @Map: themeID ---> ThemeSecurityPair List */
				themeSecurityPairMap.put(themeName, themeSecurityPairList);
			} catch (Exception e){
				logger.error(e.toString());
			}
		}
		return themeSecurityPairMap;
	}
	
}
