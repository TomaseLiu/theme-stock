package com.datayes.bdb.theme.stock.batch.task;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.datayes.bdb.theme.stock.batch.BaseTask;
import com.datayes.bdb.theme.stock.business.LocalUpdater;
import com.datayes.bdb.theme.stock.entity.ThemeSecurityObjPair;
import com.datayes.bdb.theme.stock.util.DateUtil;


@Component
public class UpdateBeforeQATask extends BaseTask{
	
	private static final Logger LOG = LoggerFactory.getLogger(UpdateBeforeQATask.class);
	
	@Autowired
	LocalUpdater localUpdater;
	
	private void updateLocalDataOnce(){
		LOG.info("start updateLocalDataOnce");

		/**update jieba dictionary locally */
		localUpdater.updateLocalJiebaDictWithSecAndTheme();
		
		/** get new theme-security pairs */
		Map<String, List<ThemeSecurityObjPair>> themeSecPairMap = localUpdater.getThemeSecPairToBeQA();
		LOG.info("themeSecPairMap size: {}", themeSecPairMap == null ? 0:themeSecPairMap.size());
		
		if(null == themeSecPairMap || themeSecPairMap.isEmpty())
			return;
		
		String currentDateStr = DateUtil.dateToStr(new Date(), DateUtil.DatePattern.day2);
		
		/** write new theme-security pairs into local file */
		localUpdater.writeThemeSecPairToBeQAFile(themeSecPairMap, currentDateStr);
		LOG.info("finished updataLocalDataOnce");
	}
	
	
	@Scheduled(cron = "${batch.task.update.theme.stock.local.update.cron.expression}")
	@Override
	public void execute() {
		Date curDay = new Date();
		
		LOG.info("start to do schedule task: {}", curDay);
		try{
			updateLocalDataOnce();
		}catch (Exception e){
			e.printStackTrace();
		}
		LOG.info("finished schedule task: {}", curDay);
	}

	@Override
	public void dump(){
		Date curDay = new Date();
		LOG.info("start to do dump task: {}", curDay);
		System.out.println("start to do dump task: " + curDay);
		try {
			updateLocalDataOnce();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOG.info("finished dump task: {}", curDay);
	}
	
}