package com.datayes.bdb.theme.stock.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datayes.bdb.theme.stock.business.LocalUpdater;
import com.datayes.bdb.theme.stock.entity.BigdataTheme;
import com.datayes.bdb.theme.stock.entity.ThemeSecurityObjPair;

public class FileIOUtil {
	
	
	private static final Logger logger = LoggerFactory.getLogger(FileIOUtil.class);

	public static String readTextFromFile(String filePath){
		String text = null;
		try {
			text = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error(e.toString());
		}
		return text;
	}
	
	public static void writeJiebaDict(String jiebaFilePath, Map<String, String> tokenMap){
		
		if(tokenMap == null)
			return;
		try {
			File writeFile = new File(jiebaFilePath);
			writeFile.createNewFile();
			BufferedWriter outBf = new BufferedWriter(new FileWriter(writeFile));
			for(String token: tokenMap.keySet()){
				String property = tokenMap.get(token);
				outBf.write(token + " " + "3" + " " + property);
				outBf.newLine();
			}
			outBf.close();
		} catch (IOException e) {
			logger.error(e.toString());
		}
	}
	
	public static Map<String, String> readJiebaDict(String jiebaDictFilePath){
		Map<String, String> tokenMap = new HashMap<String, String>();
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(jiebaDictFilePath), "UTF-8"));
			String line = null;
			while((line = br.readLine()) != null){
				String[] items = line.trim().split(" ");
				if(3 != items.length)
					continue;
				String token = items[0];
				String property = items[2];
				tokenMap.put(token, property);
			}
			br.close();
		}catch(Exception e){
			logger.error(e.toString());
		}
		return tokenMap;
	}
	
	public static void writeThemeList(String themeListFilePath, List<BigdataTheme> bigdataThemeList){
		if(bigdataThemeList == null)
			return;
		try {
			File writeFile = new File(themeListFilePath);
			writeFile.createNewFile();
			BufferedWriter outBf = new BufferedWriter(new FileWriter(writeFile));
			for(BigdataTheme bigdataTheme: bigdataThemeList){
				Long themeID = bigdataTheme.getThemeID();
				String themeName = bigdataTheme.getThemeName();
				Date themeInsertTime = bigdataTheme.getUpdateTime();
				outBf.write(themeID + " " + themeName + " " + themeInsertTime);
				outBf.newLine();
			}
			outBf.close();
		} catch (IOException e) {
			logger.error(e.toString());
		}
	}
		
	public static Map<Long, BigdataTheme> readThemeMap(String themeListFilePath){
		Map<Long, BigdataTheme> bigdataThemeMap = new HashMap<Long, BigdataTheme>();
		try{
			if(!(new File(themeListFilePath)).exists()){
				logger.info(" file not exists: {}.", themeListFilePath);
				return bigdataThemeMap;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(themeListFilePath), "UTF-8"));
			String line = null;
			while((line = br.readLine()) != null){
				String[] items = line.trim().split(" ");
				if(3 != items.length)
					continue;
				Long themeID = Long.parseLong(items[0]);
				String themeName = items[1];
				Date updateTime = DateUtil.strToDate(items[2], DateUtil.DatePattern.day);
				String themeDesc = "";
				BigdataTheme bigdataTheme = new BigdataTheme(themeID, themeName, themeDesc, updateTime);
				bigdataThemeMap.put(themeID, bigdataTheme);
			}
			br.close();
		}catch(Exception e){
			logger.error(e.toString());
		}
		logger.info("handled theme list size: {}", bigdataThemeMap == null ? 0 : bigdataThemeMap.size());
		return bigdataThemeMap;
	}

	public static void writeThemeSecPair(List<ThemeSecurityObjPair> themeSecPairMapToBeQA, String themeName, String folderPath){
		if(themeSecPairMapToBeQA == null)
			return;
		try {
			File writeFile = new File(folderPath + "/" + themeName + ".ThmSec");
			writeFile.createNewFile();
			BufferedWriter outBf = new BufferedWriter(new FileWriter(writeFile));
			for(ThemeSecurityObjPair themeSecurityObjPair: themeSecPairMapToBeQA){
				Long themeID = themeSecurityObjPair.getThemeID();
				String securityShortName = themeSecurityObjPair.getDatayesdbpSecurity().getSecShortName();
				String securityTickerSymbol = themeSecurityObjPair.getDatayesdbpSecurity().getTickerSymbol();
				Date findTime = themeSecurityObjPair.getFindTime();
				String type = themeSecurityObjPair.getType();
				String desc = themeSecurityObjPair.getPairDesc();
				outBf.write(themeID + " " + themeName + " " + securityShortName + " " + securityTickerSymbol 
						+ " " + DateUtil.dateToStr(findTime, DateUtil.DatePattern.day2) + " " + type + " " + desc);
				outBf.newLine();
			}
			logger.info("write theme sec pair into file success");
			outBf.close();
		} catch (IOException e) {
			logger.error(e.toString());
		}
	}
}
