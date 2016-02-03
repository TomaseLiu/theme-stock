package com.datayes.bdb.theme.stock.test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datayes.bdb.theme.stock.business.MongoDataAnalizer;
import com.datayes.bdb.theme.stock.business.TextAnalizer;
import com.datayes.bdb.theme.stock.dao.bigdata.BigdataMapper;
import com.datayes.bdb.theme.stock.dao.datayesdbp.DatayesdbpMapper;
import com.datayes.bdb.theme.stock.entity.BigdataTheme;
import com.datayes.bdb.theme.stock.entity.DatayesdbpSecurity;
import com.datayes.bdb.theme.stock.entity.ThemeSecurityObjPair;


@Service
public class BusinessTest {
	
	@Autowired
	private TextAnalizer textAnalizer;
	@Autowired
	private DatayesdbpMapper datayesdbpMapper;
	@Autowired
	private BigdataMapper bigdataMapper;
	@Autowired
	private MongoDataAnalizer mongoDataAnalizer;
	
	public void textAnalizerTest(){
		System.out.println("start");
		Set<BigdataTheme> themeSet = new HashSet<BigdataTheme>(bigdataMapper.getThemeList());
		List<DatayesdbpSecurity> securityList = datayesdbpMapper.getSecurityList();
		Boolean isSchedule = true;
		Map<String, List<ThemeSecurityObjPair>> themeSecPairMap = textAnalizer.findThemesSecPair(themeSet, securityList, isSchedule);
		System.out.println("themeSecPairMap size: " + themeSecPairMap.size());
		for(String themeName: themeSecPairMap.keySet()){
			System.out.println(themeName);
			for(ThemeSecurityObjPair themeSecurityObjPair: themeSecPairMap.get(themeName)){
				System.out.println(themeSecurityObjPair.getDatayesdbpSecurity().getSecShortName());
			}
			System.out.println(themeName + " size: " + themeSecPairMap.get(themeName).size());
		}
	}
	
	public void mongoAnalizerTest(){
		System.out.println("start mongo test");
		Set<BigdataTheme> themeSet = new HashSet<BigdataTheme>(bigdataMapper.getThemeList());
		List<DatayesdbpSecurity> securityList = datayesdbpMapper.getSecurityList();
		Boolean isSchedule = true;
		Map<String, List<ThemeSecurityObjPair>> themeSecPairMap = mongoDataAnalizer.findThemeSecPair(themeSet, securityList, isSchedule);
		System.out.println("themeSecPairMap size: " + themeSecPairMap.size());
		for(String themeName: themeSecPairMap.keySet()){
			System.out.println(themeName);
			for(ThemeSecurityObjPair themeSecurityObjPair: themeSecPairMap.get(themeName)){
				System.out.println(themeSecurityObjPair.getDatayesdbpSecurity().getSecShortName());
			}
			System.out.println(themeName + " size: " + themeSecPairMap.get(themeName).size());
		}
	}
}
