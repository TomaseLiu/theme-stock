package com.datayes.bdb.theme.stock.dao.news;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.datayes.bdb.theme.stock.entity.ThemeText;
public interface NewsMapper {
	public List<ThemeText> getNews(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
