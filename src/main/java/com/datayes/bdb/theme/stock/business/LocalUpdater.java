package com.datayes.bdb.theme.stock.business;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datayes.bdb.theme.stock.batch.Bootstrap;
import com.datayes.bdb.theme.stock.constant.ConfigConst;
import com.datayes.bdb.theme.stock.dao.bigdata.BigdataMapper;
import com.datayes.bdb.theme.stock.dao.datayesdbp.DatayesdbpMapper;
import com.datayes.bdb.theme.stock.dao.mongo.MongoThemeStockDao;
import com.datayes.bdb.theme.stock.entity.BigdataTheme;
import com.datayes.bdb.theme.stock.entity.BigdataThemeSecRel;
import com.datayes.bdb.theme.stock.entity.DatayesdbpSecurity;
import com.datayes.bdb.theme.stock.entity.ThemeSecurityObjPair;
import com.datayes.bdb.theme.stock.util.FileIOUtil;

@Service
public class LocalUpdater {

	private static final Logger logger = LoggerFactory.getLogger(LocalUpdater.class);
	
	@Autowired
	BigdataMapper bigdataMapper;
	
	@Autowired
	NewsSearcher newsSearcher;
	
	@Autowired
	TextAnalizer textAnalizer;
	
	@Autowired
	DatayesdbpMapper datayesdbpMapper;
	
	@Autowired
	MongoDataAnalizer mongoDataAnalizer;
	
	@Autowired
	MongoThemeStockDao mongoThemeStockDao;
	
	String HANDLED_THEMES_FILE_PATH = ConfigConst.HANDLED_THEMES_FILE_PATH;
	
	static String JiebaUserDictPath = ConfigConst.JIEBA_USER_DICT_FILE_PATH;
	
	
	/**
	 * @author dengxiang.liu
	 * @description update jieba user dictionary with securities and themes just came up recently; should be called before getThemeSecPair methods;
	 * @see 
	 */
	public void updateLocalJiebaDictWithSecAndTheme(){
		String jiebaUserDictPath = JiebaUserDictPath;
		
		logger.info("jieba user dictionary file path: {}", jiebaUserDictPath);
		try{
			List<DatayesdbpSecurity> securityList = datayesdbpMapper.getSecurityList();
			Map<String, String> tokenMap = FileIOUtil.readJiebaDict(jiebaUserDictPath);
			logger.info("original jieba dictionary size: {}", tokenMap==null ? 0:tokenMap.size());
			Integer addedNewsWordsCount = 0;
			for(DatayesdbpSecurity datayesdbpSecurity: securityList){
				if(!tokenMap.containsKey(datayesdbpSecurity.getSecShortName())){
					tokenMap.put(datayesdbpSecurity.getSecShortName(), "n");
					addedNewsWordsCount ++;
				}
			}
			
			List<BigdataTheme> bigdataThemeList = bigdataMapper.getThemeList();
			for(BigdataTheme bigdataTheme: bigdataThemeList){
				if(!tokenMap.containsKey(bigdataTheme.getThemeName())){
					tokenMap.put(bigdataTheme.getThemeName(), "n");
				}
			}
		
			FileIOUtil.writeJiebaDict(jiebaUserDictPath, tokenMap);
			logger.info("Add {} new words into jieba dictionary.", addedNewsWordsCount);
		} catch (Exception e){
			logger.error("error occurs when update jieba dictionary, {}", e.toString());
		}
	}
	
	
	
	/**
	 * @Author: dengxiang.liu
	 * @Desc: get unhandled themes from the different between themes list in database and themes list in local file;
	 * @See: updateHandledThemeFile()
	 */
	public List<BigdataTheme> getUnhandledThemeList(List<BigdataTheme> bigdataThemeList){

		/** get themes have been handled from the file */
		Map<Long, BigdataTheme> handledThemeMap = FileIOUtil.readThemeMap(HANDLED_THEMES_FILE_PATH);
		/** themes to be handled */
		List<BigdataTheme> unHandledThemeList = new ArrayList<BigdataTheme>();
		
		if(null == bigdataThemeList)
			return unHandledThemeList;
		for(BigdataTheme bigdataTheme: bigdataThemeList){
			try{
				Long themeID = bigdataTheme.getThemeID();
				if(!handledThemeMap.containsKey(themeID)){
					unHandledThemeList.add(bigdataTheme);
				}
			}catch (Exception e){
				logger.error(e.toString());
			}
		}
		logger.info("get unhandled theme list size: {}",  unHandledThemeList.size());
		return unHandledThemeList;
	}
	
	
	
	/**
	 * @Author: dengxiang.liu
	 * @Desc: write handled themes into file; call this after insert new theme-security pair data into databases;
	 * TODO: 
	 */
	public void updateHandledThemeFile(){
		
	}
	
	/**
	 * @Author: Dengxiang.Liu
	 * @Sed: getUnhandledThemeList();
	 * @Desc: schedule_themes = all_themes - unhandled_themes
	 */
	public List<BigdataTheme> getSchedualThemeList(List<BigdataTheme> allThemeList, List<BigdataTheme> unhandledThemeList){
		if(null == allThemeList || allThemeList.isEmpty())
			return new ArrayList<BigdataTheme>();
		if(null == unhandledThemeList || unhandledThemeList.isEmpty())
			return allThemeList;
		allThemeList.removeAll(unhandledThemeList);
		return allThemeList;
	}
	
	/**
	 * @author dengxiang.liu
	 * @return current theme security pairs in database;
	 * @Desc get QAed and active theme-security relationships from database;
	 */
	public Map<String, Set<String>> getCurrentThemeSecurityPair(){
		List<BigdataThemeSecRel> themeSecPairList = bigdataMapper.getThemeSecurityPairList();
		Map<String, Set<String>> themeSecSetMap = new HashMap<String, Set<String>>();
		for(BigdataThemeSecRel bigdataThemeSecRel: themeSecPairList){
			Long themeID = bigdataThemeSecRel.getThemeID();
			String themeName = bigdataThemeSecRel.getThemeName();
			String tickerSymbol = bigdataThemeSecRel.getTickerSymbol();
			if(!themeSecSetMap.containsKey(themeID))
				themeSecSetMap.put(themeName, new HashSet<String>());
			themeSecSetMap.get(themeID).add(tickerSymbol);
		}
		return themeSecSetMap;
	}
	
	
	/**
	 * @Author: Dengxiang.Liu
	 * @Param: Map<Long, Set<String>> theme-security, pairs has been active;
	 * @Param: Map<Long, List<ThemeSecurityObjPair>>, theme-security pairs new found;
	 * @Return: Map<Long, List<ThemeSecurityObjPair>>, theme-security in found set but not in active set;
	 */
	public Map<String, List<ThemeSecurityObjPair>> getDiffThemeSecPairBetween(Map<String, Set<String>> curThemeSecSetMap, 
			Map<String, List<ThemeSecurityObjPair>> foundThemeSecPairMap){
		
		if(curThemeSecSetMap == null || curThemeSecSetMap == null)
			return foundThemeSecPairMap;
		
		Map<String, List<ThemeSecurityObjPair>> themeSecPairToBeAddMap = new HashMap<String, List<ThemeSecurityObjPair>>();
		for(String themeName: foundThemeSecPairMap.keySet()){
			List<ThemeSecurityObjPair> themeSecurityObjPairList = foundThemeSecPairMap.get(themeName);
			List<ThemeSecurityObjPair> themeSecPairToBeAddedList = new ArrayList<ThemeSecurityObjPair>();
			
			for(ThemeSecurityObjPair themeSecurityObjPair: themeSecurityObjPairList){
				try{
					String tickerSymbol = themeSecurityObjPair.getDatayesdbpSecurity().getTickerSymbol();
					if(curThemeSecSetMap.containsKey(themeName) && curThemeSecSetMap.get(themeName).contains(tickerSymbol)) 
						continue;
					
					themeSecPairToBeAddedList.add(themeSecurityObjPair);
					
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			if(!themeSecPairToBeAddedList.isEmpty())
				themeSecPairToBeAddMap.put(themeName, themeSecPairToBeAddedList);
		}
		return themeSecPairToBeAddMap;
	}
	
	
	/**
	 * @author dengxiang.liu
	 * @param MapA
	 * @param MapB
	 * @Desc Integrate B to A;
	 */
	public void IntegrateMaps(Map<String, List<ThemeSecurityObjPair>> MapA, Map<String, List<ThemeSecurityObjPair>> MapB){
		if(MapB != null && MapA != null){
			for(String themeID: MapB.keySet()){
				if(MapA.containsKey(themeID)){
					MapA.get(themeID).addAll(MapB.get(themeID));
				} else {
					MapA.put(themeID, MapB.get(themeID));
				}
			}
		}
	}
	
	
	/**
	 * @Author: Dengxiang.Liu
	 * @Return: Map<Long, List<ThemeSecurityPair>>; a map from themeID to it's ThemeSecurityPair List;
	 * @Desc: the result will be QA before insert into Database;
	 **/
	public Map<String, List<ThemeSecurityObjPair>> getThemeSecPairToBeQA(){
		logger.info("start getThemeSecPairToBeQA();");
		Map<String, Set<String>> curThemeSecPairMap = getCurrentThemeSecurityPair();
		List<DatayesdbpSecurity> securityList = datayesdbpMapper.getSecurityList();
		
		/* handle new themes */
		List<BigdataTheme> bigdataThemeList = bigdataMapper.getThemeList(); // all themes list;
		
		logger.info("all themes list size: {};", bigdataThemeList == null ? 0 : bigdataThemeList.size());
		
		List<BigdataTheme> unhandledThemeList = getUnhandledThemeList(bigdataThemeList);
		logger.info("unhandled themes list size: {};", unhandledThemeList == null ? 0 : unhandledThemeList.size());
		Set<BigdataTheme> unhandledThemeSet = (unhandledThemeList == null || unhandledThemeList.isEmpty()) ? 
				null : new HashSet<BigdataTheme>(unhandledThemeList);
		
		Map<String, List<ThemeSecurityObjPair>> newsThemeSecPairMapUnhandled = textAnalizer.findThemesSecPair(unhandledThemeSet, securityList, false);
		Map<String, List<ThemeSecurityObjPair>> crawlThemeSecPairMapUnhandled = mongoDataAnalizer.findThemeSecPair(unhandledThemeSet, securityList, false);
		
		Map<String, List<ThemeSecurityObjPair>> newsThemeSecPairMapToBeQAUnhandled = getDiffThemeSecPairBetween(curThemeSecPairMap, newsThemeSecPairMapUnhandled);
		Map<String, List<ThemeSecurityObjPair>> crawlThemeSecPairMapToBeQAUnhandled = getDiffThemeSecPairBetween(curThemeSecPairMap, crawlThemeSecPairMapUnhandled);
		
		
		/* handle schedule themes: schedule-themes = all-themes - unhandled-themes*/
		List<BigdataTheme> scheduleThemeList = getSchedualThemeList(bigdataThemeList, unhandledThemeList);
		Set<BigdataTheme> scheduleThemeSet = new HashSet<BigdataTheme>(scheduleThemeList == null ? new ArrayList<BigdataTheme>() : scheduleThemeList);
		logger.info("schedule themes Set size: {}", scheduleThemeSet == null ? 0 : scheduleThemeSet.size());
		
		Map<String, List<ThemeSecurityObjPair>> newsThemeSecPairMapSchedule = textAnalizer.findThemesSecPair(scheduleThemeSet, securityList, true);
		Map<String, List<ThemeSecurityObjPair>> crawlThemeSecPairMapSchedule = mongoDataAnalizer.findThemeSecPair(scheduleThemeSet, securityList, true);
		
		Map<String, List<ThemeSecurityObjPair>> newsThemeSecPairMapToBeQASchedule = getDiffThemeSecPairBetween(curThemeSecPairMap, newsThemeSecPairMapSchedule);
		Map<String, List<ThemeSecurityObjPair>> crawlThemeSecPairMapToBeQASchedule = getDiffThemeSecPairBetween(curThemeSecPairMap, crawlThemeSecPairMapSchedule);
		
		
		/* integrate theme-security pair maps */
		Map<String, List<ThemeSecurityObjPair>> globalThemeSecPairMapToBeQA = new HashMap<String, List<ThemeSecurityObjPair>>();
		
		
		IntegrateMaps(globalThemeSecPairMapToBeQA, newsThemeSecPairMapToBeQAUnhandled);
		IntegrateMaps(globalThemeSecPairMapToBeQA, newsThemeSecPairMapToBeQASchedule);
		IntegrateMaps(globalThemeSecPairMapToBeQA, crawlThemeSecPairMapToBeQAUnhandled);
		IntegrateMaps(globalThemeSecPairMapToBeQA, crawlThemeSecPairMapToBeQASchedule);
		
		
		logger.info("finished getThemeSecPairToBeQA();");
		return globalThemeSecPairMapToBeQA;
	}
	
	
	/**
	 * @author dengxiang.liu
	 * @description 将等待QA的(theme-security-type...)对写入本地文件；
	 */
	public void writeThemeSecPairToBeQAFile(Map<String, List<ThemeSecurityObjPair>> globalThemeSecPairMapToBeQA, String currentDateStr){
		if(globalThemeSecPairMapToBeQA == null){
			logger.info("No theme security pair to be qa today");
			return;
		}
		String globalQAFolderPath = "data/qa/";
		logger.info("{} themes have theme-security pair information need to be QA today", globalThemeSecPairMapToBeQA.size());
		try{
			globalQAFolderPath = ConfigConst.QA_FOLDER_PATH;
		} catch (Exception e){
			logger.error(e.toString());
		}
		for(String themeName: globalThemeSecPairMapToBeQA.keySet()){
			String qaFolderPath = globalQAFolderPath + currentDateStr;
			File qaFolderToBeCreate = new File(qaFolderPath);
			if(!qaFolderToBeCreate.exists()){
				Boolean success = qaFolderToBeCreate.mkdirs();
				if(!success){
					logger.info("create folder failed.");
					return;
				}
			}
			List<ThemeSecurityObjPair> themeSecPairMapToBeQA = globalThemeSecPairMapToBeQA.get(themeName);
			if(null != themeSecPairMapToBeQA && !themeSecPairMapToBeQA.isEmpty())
				FileIOUtil.writeThemeSecPair(themeSecPairMapToBeQA, themeName, qaFolderPath);
		}
	}
	
}
