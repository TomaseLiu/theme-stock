package com.datayes.bdb.theme.stock.test;


import com.datayes.bdb.theme.stock.util.*;
import com.datayes.bdb.theme.stock.entity.*;
import com.datayes.bdb.theme.stock.business.*;
import com.datayes.bdb.theme.stock.constant.ConfigConst;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.huaban.analysis.jieba.WordDictionary;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;


@Component
public class Main {
	
	private static final String SPRING_CONTEXT_FILE = "spring-process.xml";
	
	private static ApplicationContext context;
	
	TextAnalizer textAnalizer;
	
	@Autowired
	private DBTest dbTest;
	
	@Autowired
	private BusinessTest businessTest;
	
	@Autowired
	private FileIOTest fileIOTest;
	
	@Autowired
	private LocalUpdateTest localUpdateTest;
	
	
	public static void init() {
		context = new ClassPathXmlApplicationContext(SPRING_CONTEXT_FILE);
	}
	
	private void dbTest(String[] args){
		try{
			List<ThemeText> newsList = dbTest.getNewsFromDB();
			//MyFileWriter.writeThemeListFile("./Data/Themes.dat", bigdataThemeList);
			System.out.println("newsList size: " + newsList.size());
			String body = newsList.get(1).getBody();
			System.out.println("news: \n" + body +"\n\n");
			
			String title = newsList.get(1).getTitle();
			String source = newsList.get(1).getSource();
			String publishTime = newsList.get(1).getPublishTime();
			String type = newsList.get(1).getType();
			System.out.println(title + "\t" + source + "\t" + publishTime + "\t" + type);
			
			//List<DatayesdbpSecurity> securtyList = dbTest.getSecurtyFromDB();
			//System.out.println("securityList size: " + securtyList.size());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void mongoTest(String[] args){
		try{
			dbTest.getThemeStockInfoFromMongo("一带一路");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void searcherTest(String[] args){
		try{
			dbTest.getNewsWithKeyword("一带一路概念");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void business_test(String[] args){
		//businessTest.textAnalizerTest();
		businessTest.mongoAnalizerTest();
	}
	
	private void fileIO_test(String[] args){
		fileIOTest.createFolderIfNotExist("data/qa/2015-10-11");
	}
	
	
	private void localUpdate_test(String[] args){
		localUpdateTest.updateLocalData();
	}
	public static void main(String args[]){
		ApplicationContext conxt = new ClassPathXmlApplicationContext("spring.xml");
		Main p = conxt.getBean(Main.class);
		//p.start(args);
		//p.dbTest(args);
		//p.mongoTest(args);
		p.searcherTest(args);
		//p.business_test(args);
		//p.fileIO_test(args);
		//p.localUpdate_test(args);
		
		//String filePath = "./data/test/IP_16480837_112";
		//String jiebaDictPath = "./data/test/dict";
		
	}
}
