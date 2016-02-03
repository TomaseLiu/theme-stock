package com.datayes.bdb.theme.stock.business;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datayes.bdb.theme.stock.constant.ConfigConst;
import com.datayes.bdb.theme.stock.dao.mongo.MongoThemeStockDao;
import com.datayes.bdb.theme.stock.entity.BigdataTheme;
import com.datayes.bdb.theme.stock.entity.DatayesdbpSecurity;
import com.datayes.bdb.theme.stock.entity.MongoThemeStock;
import com.datayes.bdb.theme.stock.entity.ThemeSecurityObjPair;
import com.datayes.bdb.theme.stock.util.DateUtil;

@Service
public class MongoDataAnalizer {
	
	static Logger logger = LoggerFactory.getLogger(MongoDataAnalizer.class);

	@Autowired
	MongoThemeStockDao mongoThemeStockDao;
	
	
	/**
	 * @author dengxiang.liu
	 * @param mongoThemeStockList
	 * @param securityList
	 * @param bigdataThemeSet
	 * @return theme security pairs list
	 * @Desc get theme security pairs from mongo data about target theme;
	 */
	public List<ThemeSecurityObjPair> getThemeSecurityPairList(List<MongoThemeStock> mongoThemeStockList, List<DatayesdbpSecurity> securityList,
			Set<BigdataTheme> bigdataThemeSet){
		
		List<ThemeSecurityObjPair> themeSecurityPairList = new ArrayList<ThemeSecurityObjPair>();
		
		/** used to make sure the uniqueness of theme-security pair: themeID ---> tickerSymbol set*/
		Map<Long, Set<String>> globalThemeSecPairMap = new HashMap<Long, Set<String>>();
		if(mongoThemeStockList == null || securityList == null) return themeSecurityPairList;
		
		/** @Map: security ticer symbol ---> DatayesdbpSecurity Object */
		Map<String, DatayesdbpSecurity> tickerSymbol2Obj = new HashMap<String, DatayesdbpSecurity>();
		for(DatayesdbpSecurity security: securityList){
			String tickerSymbol = security.getTickerSymbol();
			tickerSymbol2Obj.put(tickerSymbol, security);
		}
		/** @Map: theme name ---> BigdataTheme themeID */
		Map<String, Long> themeName2ID = new HashMap<String, Long>();
		for(BigdataTheme bigdataTheme: bigdataThemeSet){
			Long themeID = bigdataTheme.getThemeID();
			String themeName = bigdataTheme.getThemeName();
			themeName2ID.put(themeName, themeID);
		}
		
		for(MongoThemeStock mongoThemeStock: mongoThemeStockList){
			String themeName = mongoThemeStock.getThemeName();
			String dateStr = mongoThemeStock.getDate();
			Date findTime = null;
			try {
				findTime = DateUtil.strToDate(dateStr, DateUtil.DatePattern.day);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			/** theme-security pair description */
			String webSite = mongoThemeStock.getWebSite();
			String pairType = "crawl";
			//List<String> tickerSymbolList = new ArrayList<String>();
			try{
				List<String> tickerSymbolList = mongoThemeStock.getRelateStock();
				if(tickerSymbolList == null || tickerSymbolList.isEmpty())
					continue;
				for(String tickerSymbol: tickerSymbolList){
				
					if(!tickerSymbol2Obj.containsKey(tickerSymbol)) continue;
				
					DatayesdbpSecurity curSecurity = tickerSymbol2Obj.get(tickerSymbol);
				
					/** if themes came from database do not contains the current theme (comes from mongo db), set mongo theme's id to -1 */
					Long themeID = themeName2ID.containsKey(themeName) ? themeName2ID.get(themeName) : -1L;
					if(themeID == -1L || 
							(globalThemeSecPairMap.containsKey(themeID) && globalThemeSecPairMap.get(themeID).contains(curSecurity.getTickerSymbol()))) 
						continue;
					ThemeSecurityObjPair themeSecurityPair = new ThemeSecurityObjPair(themeID, themeName, curSecurity, webSite, findTime, pairType);
					themeSecurityPairList.add(themeSecurityPair);
					if(!globalThemeSecPairMap.containsKey(themeID))
						globalThemeSecPairMap.put(themeID, new HashSet<String>());
					globalThemeSecPairMap.get(themeID).add(curSecurity.getTickerSymbol());
				}
			}catch (Exception e){
				logger.error(e.toString());
			}
		}
		
		return themeSecurityPairList;
	}
	
	
	/**
	 * @Author: Dengxiang.Liu
	 * @Param: themeList; could be themes have never been handled(getUnhandledThemeList()) or themes have been handled, depends on @Param isScheduale; 
	 * @Param: isSchedule; true if the themes arn't new to system;
	 * @Return: Map<Long, List<ThemeSecurityPair>>; a map from themeID to it's ThemeSecurityPair List;
	 * @Desc: the result will be QA before insert into Database;
	 */
	public Map<String, List<ThemeSecurityObjPair>> findThemeSecPair(Set<BigdataTheme> themeSet, List<DatayesdbpSecurity> securityList, Boolean isSchedule){
		
		Map<String, List<ThemeSecurityObjPair>> themeSecurityPairMap = new HashMap<String, List<ThemeSecurityObjPair>>();
		
		
		logger.info("findThemeSecPair with mongo data");
		logger.info("themeSet size: {}; securityList size: {}, isSchedule: {}",
				themeSet == null ? 0:themeSet.size(), securityList == null? 0:securityList.size(), isSchedule);
		
		if(null == themeSet)
			return themeSecurityPairMap;
		
		Date endDate = new Date();
		
		/** configure data scale */
		Integer mongoTimeInterval = isSchedule ? 5 : 50;
		try{
			mongoTimeInterval = isSchedule ? Integer.parseInt(ConfigConst.MONGO_TIME_INTERVAL_SCHEDULE):
				Integer.parseInt(ConfigConst.MONGO_TIME_INTERVAL_UNSCHEDULE);
		}catch (Exception e) {
			e.printStackTrace();
			mongoTimeInterval = isSchedule ? 5 : 50;
		}
		Date mongoStartDate = DateUtil.addDay(endDate, -1*mongoTimeInterval);
		
		logger.info("mongo data from: {}, to {}", 
				DateUtil.dateToStr(mongoStartDate, DateUtil.DatePattern.day), DateUtil.dateToStr(endDate, DateUtil.DatePattern.day2));
		
		for(BigdataTheme bigdataTheme: themeSet){
			
			try{
				List<ThemeSecurityObjPair> themeSecurityPairList = new ArrayList<ThemeSecurityObjPair>();
				Long themeID = bigdataTheme.getThemeID();
				String themeName = bigdataTheme.getThemeName();
			
				/** get mongo data ; with theme_name*/
				List<MongoThemeStock> mongoThemeStockList = mongoThemeStockDao.searchByThemeName(themeName, DateUtil.dateToStr(mongoStartDate, DateUtil.DatePattern.day),
						DateUtil.dateToStr(endDate, DateUtil.DatePattern.day));
			
				logger.info("themeName: {}, mongoThemeStockList size: {}", themeName, mongoThemeStockList==null?0:mongoThemeStockList.size());
			
				/** get theme-security pair from mongo data */
				List<ThemeSecurityObjPair> themeSecurityPairListMongo = getThemeSecurityPairList(mongoThemeStockList, securityList, themeSet);
			
				if(null != themeSecurityPairListMongo) 
					themeSecurityPairList.addAll(themeSecurityPairListMongo);
			
				/** @Map themeName ---> ThemeSecurityPair List */
				themeSecurityPairMap.put(themeName, themeSecurityPairList);
				
			}catch (Exception e){
				logger.error(e.toString());
			}
		}
		return themeSecurityPairMap;
	}
	
	
	
	
}
