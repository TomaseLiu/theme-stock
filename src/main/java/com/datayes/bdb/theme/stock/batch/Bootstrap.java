package com.datayes.bdb.theme.stock.batch;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.datayes.bdb.theme.stock.util.Config;
import com.datayes.bdb.theme.stock.batch.BaseTask;


public class Bootstrap {
	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
	private static final String SPRING_CONTEXT_FILE = "spring-batch.xml";
	private static final String DUMP_ARG = "dump";
	
	private ApplicationContext context;
	
	public void init() {
		logger.info("Init context.");
		context = new ClassPathXmlApplicationContext(SPRING_CONTEXT_FILE);
		logger.info("Init context success.");
	}
	
	public void startTask(boolean needDump) {
		Map<String, BaseTask> taskMap = context.getBeansOfType(BaseTask.class);
		if (needDump) {
			startDumpTask(taskMap);
		}
		startScheduledTask(taskMap);
	}
	
	private void startDumpTask(Map<String, BaseTask> taskMap) {
		String dumpTaskStr = Config.BATCH.get("batch.task.dump");
		if (!StringUtils.isBlank(dumpTaskStr)) {
			String[] dumpTasks = dumpTaskStr.split(",");
			for (String taskKey : dumpTasks) {
				BaseTask task = taskMap.get(taskKey);
				if (task != null) {
					logger.info("Start to init " + taskKey +" dump job.");
					task.asyncDump();
					taskMap.remove(taskKey);
				}
			}
		}
	}
	
	private void startScheduledTask(Map<String, BaseTask> taskMap) {
		String allTaskStr = Config.BATCH.get("batch.task.schedule");
		String[] taskStrs = allTaskStr.split(",");
		for (String taskKey : taskStrs) {
			BaseTask task = taskMap.get(taskKey);
			if (task != null) {
				logger.info("Start to init " + taskKey + " schedule task.");
				task.start();
			}
		}
	}
	
	public static void main(String[] args) {
		logger.info("Init batch.");
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.init();
	
		if (args != null && args.length > 0 && args[0].equalsIgnoreCase(DUMP_ARG)) {
			bootstrap.startTask(true);
		} else {
			bootstrap.startTask(false);
		}
	
		logger.info("Batch is working.");
	}
}
