package com.datayes.bdb.theme.stock.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextUtil {
	public static List<String> getSentenceList(String text){
		List<String> sentenceList = new ArrayList<String>();
		try{
			text = text.replaceAll("\\s*\n", "。");
			//logger.info("get sentenceList: {}", text);
			String[] sentenceArray = text.trim().split("。|！|？|；");
			sentenceList = Arrays.asList(sentenceArray);

		}catch (Exception e){
			e.printStackTrace();
			//logger.info("error occurs when split text into sentences, {}", e.toString());
		}
		return sentenceList;
	}
}
