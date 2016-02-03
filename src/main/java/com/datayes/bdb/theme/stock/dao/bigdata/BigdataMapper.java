package com.datayes.bdb.theme.stock.dao.bigdata;


import java.util.List;

import com.datayes.bdb.theme.stock.entity.BigdataTheme;
import com.datayes.bdb.theme.stock.entity.BigdataThemeSecRel;
import com.datayes.bdb.theme.stock.entity.ThemeSecurityObjPair;
public interface BigdataMapper {
	public List<BigdataTheme> getThemeList();
	public List<BigdataThemeSecRel> getThemeSecurityPairList();
}
