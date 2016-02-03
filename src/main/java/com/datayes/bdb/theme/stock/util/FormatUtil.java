package com.datayes.bdb.theme.stock.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.datayes.bdb.theme.stock.entity.ThemeSecurityObjPair;

public class FormatUtil {

	public static DecimalFormat decimalFormat = new DecimalFormat("#.####");
	
	public static void setDecimalFromat(Integer integerCount, Integer decimalCount){
		String strFormat = "";
		for(Integer i = 0; i < integerCount; i++){
			strFormat += "#";
		}
		strFormat += ".";
		for(Integer j = 0; j < decimalCount; j++){
			strFormat += "#";
		}
		decimalFormat.applyPattern(strFormat);
	}
	
	public static Double scale(Double d, int size) {
		BigDecimal b = new BigDecimal(d);
		d = b.setScale(size, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d;
	}
	
}
