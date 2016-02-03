package com.datayes.bdb.theme.stock.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

public abstract class BaseTask {
	private static final Logger logger = LoggerFactory.getLogger(BaseTask.class);
	
	protected boolean work = false;
	
	public void start() {
		work = true;
	}
	
	public void stop() {
		work = false;
	}
	
	public boolean isWork() {
		return work;
	}
	
	public abstract void execute();
	
	public abstract void dump();
	
	@Async
	public void asyncDump() {
		try {
			logger.info("Start to do " + this.getClass().getSimpleName() + " dump job.");
			dump();
			logger.info("Finish to do " + this.getClass().getSimpleName() + " dump job.");
		} catch (Exception e) {
			logger.error("Execute " + this.getClass().getSimpleName() + " dump job error.", e);
		}
		logger.info("Finish to do " + this.getClass().getSimpleName() +" dump job, start to do schedule job.");
		start();
	}
	
}
