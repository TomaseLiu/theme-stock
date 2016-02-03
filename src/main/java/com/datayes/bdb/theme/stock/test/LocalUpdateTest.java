package com.datayes.bdb.theme.stock.test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datayes.bdb.theme.stock.business.LocalUpdater;
import com.datayes.bdb.theme.stock.entity.ThemeSecurityObjPair;
import com.datayes.bdb.theme.stock.util.DateUtil;

@Service
public class LocalUpdateTest {
	
	
	@Autowired
	private LocalUpdater localUpdater;
	
	public void updateLocalData(){
		Map<String, List<ThemeSecurityObjPair>> themeSecPairMap = localUpdater.getThemeSecPairToBeQA();
		String currentDateStr = DateUtil.dateToStr(new Date(), DateUtil.DatePattern.day);
		localUpdater.writeThemeSecPairToBeQAFile(themeSecPairMap, currentDateStr);
	}

}
