package com.datayes.bdb.theme.stock.dao.mongo;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
//import org.springframework.stereotype.Repository;

import com.datayes.bdb.theme.stock.entity.MongoThemeStock;


public interface MongoThemeStockDao extends CrudRepository<MongoThemeStock, Long>
{
	//@Query("{'theme_name' : '舟山自贸区'}")
	@Query("{'theme_name' : ?0, 'date':{'$gte':?1, '$lt':?2}}")
	public List<MongoThemeStock> searchByThemeName(String themeName, String startTime, String endTime);
}
