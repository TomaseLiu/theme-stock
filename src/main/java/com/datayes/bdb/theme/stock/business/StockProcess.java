package com.datayes.bdb.theme.stock.business;

import com.datayes.bdb.theme.stock.dao.datayesdbp.DatayesdbpMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class StockProcess {
	
	@Autowired
	DatayesdbpMapper datayesdbpMapper;
	
	/*
	public List<DatayesdbpSecurity> getSecurityListFromDB(){
		List<DatayesdbpSecurity> securityList = datayesdbpMapper.getSecurityList();
		return securityList;
	}
	*/
	
	public static void updateSegDict(){
		
	}
	
}
